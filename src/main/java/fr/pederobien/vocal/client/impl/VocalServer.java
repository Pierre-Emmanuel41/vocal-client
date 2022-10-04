package fr.pederobien.vocal.client.impl;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import fr.pederobien.communication.event.ConnectionCompleteEvent;
import fr.pederobien.communication.event.ConnectionDisposedEvent;
import fr.pederobien.communication.event.ConnectionLostEvent;
import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.sound.event.MicrophoneDataEncodedEvent;
import fr.pederobien.sound.impl.AudioPacket;
import fr.pederobien.sound.impl.SoundResourcesProvider;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.utils.event.LogEvent;
import fr.pederobien.vocal.client.event.VocalCommunicationProtocolVersionSetPostEvent;
import fr.pederobien.vocal.client.event.VocalPlayerNameChangePostEvent;
import fr.pederobien.vocal.client.event.VocalPlayerSpeakPostEvent;
import fr.pederobien.vocal.client.event.VocalPlayerSpeakPreEvent;
import fr.pederobien.vocal.client.event.VocalServerAddressChangePostEvent;
import fr.pederobien.vocal.client.event.VocalServerAddressChangePreEvent;
import fr.pederobien.vocal.client.event.VocalServerClosePostEvent;
import fr.pederobien.vocal.client.event.VocalServerClosePreEvent;
import fr.pederobien.vocal.client.event.VocalServerJoinPostEvent;
import fr.pederobien.vocal.client.event.VocalServerJoinPreEvent;
import fr.pederobien.vocal.client.event.VocalServerLeavePostEvent;
import fr.pederobien.vocal.client.event.VocalServerLeavePreEvent;
import fr.pederobien.vocal.client.event.VocalServerNameChangePostEvent;
import fr.pederobien.vocal.client.event.VocalServerNameChangePreEvent;
import fr.pederobien.vocal.client.event.VocalServerOpenPostEvent;
import fr.pederobien.vocal.client.event.VocalServerOpenPreEvent;
import fr.pederobien.vocal.client.event.VocalServerReachableStatusChangeEvent;
import fr.pederobien.vocal.client.impl.request.ServerRequestManager;
import fr.pederobien.vocal.client.interfaces.IServerRequestManager;
import fr.pederobien.vocal.client.interfaces.IVocalMainPlayer;
import fr.pederobien.vocal.client.interfaces.IVocalServer;
import fr.pederobien.vocal.client.interfaces.IVocalServerPlayerList;

public class VocalServer implements IVocalServer, IEventListener {
	private String name;
	private InetSocketAddress address;
	private AtomicBoolean isReachable;
	private AtomicBoolean tryOpening;
	private AtomicBoolean isJoined;
	private IServerRequestManager serverRequestManager;
	private IVocalServerPlayerList players;
	private IVocalMainPlayer mainPlayer;
	private VocalTcpConnection tcpConnection;
	private VocalUdpConnection udpConnection;
	private Lock lock;
	private Condition serverConfiguration, communicationProtocolVersion;
	private boolean connectionLost;

	/**
	 * Creates a vocal server associated to a name and an address;
	 * 
	 * @param name    The vocal server's name.
	 * @param address The vocalserver's address.
	 */
	public VocalServer(String name, InetSocketAddress address) {
		this.name = name;
		this.address = address;

		isReachable = new AtomicBoolean(false);
		tryOpening = new AtomicBoolean(false);
		isJoined = new AtomicBoolean(false);
		serverRequestManager = new ServerRequestManager(this);
		players = new VocalServerPlayerList(this);
		mainPlayer = new VocalMainPlayer(this, "Unknown");
		lock = new ReentrantLock(true);
		serverConfiguration = lock.newCondition();
		communicationProtocolVersion = lock.newCondition();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		lock.lock();
		try {
			if (this.name.equals(name))
				return;

			String oldName = this.name;
			EventManager.callEvent(new VocalServerNameChangePreEvent(this, name), () -> this.name = name, new VocalServerNameChangePostEvent(this, oldName));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public InetSocketAddress getAddress() {
		return address;
	}

	@Override
	public void setAddress(InetSocketAddress address) {
		lock.lock();
		try {
			InetSocketAddress oldAddress = getAddress();
			if (oldAddress.equals(address))
				return;

			Runnable update = () -> this.address = address;
			EventManager.callEvent(new VocalServerAddressChangePreEvent(this, address), update, new VocalServerAddressChangePostEvent(this, oldAddress));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isReachable() {
		return isReachable.get();
	}

	@Override
	public void open() {
		if (!tryOpening.compareAndSet(false, true))
			return;

		if (isReachable())
			return;

		Runnable update = () -> {
			EventManager.registerListener(this);
			openConnection();
		};

		EventManager.callEvent(new VocalServerOpenPreEvent(this), update, new VocalServerOpenPostEvent(this));
	}

	@Override
	public void join(String name, Consumer<IResponse> callback) {
		Consumer<IResponse> update = response -> {
			if (!response.hasFailed()) {
				((VocalMainPlayer) mainPlayer).setName(name);
				EventManager.callEvent(new VocalServerJoinPostEvent(this));
			}
			callback.accept(response);
		};
		EventManager.callEvent(new VocalServerJoinPreEvent(this, name, update));

		lock.lock();
		try {
			if (!serverConfiguration.await(5000, TimeUnit.MILLISECONDS)) {
				isJoined.set(false);
				throw new IllegalStateException("Time out on server configuration request.");
			}

			isJoined.set(true);
			udpConnection.getUdpConnection().connect();
		} catch (InterruptedException e) {
			// Do nothing
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isJoined() {
		return isJoined.get();
	}

	@Override
	public void leave(Consumer<IResponse> callback) {
		if (!isJoined.compareAndSet(true, false))
			return;

		Consumer<IResponse> update = response -> {
			udpConnection.getUdpConnection().disconnect();
			EventManager.callEvent(new VocalServerLeavePostEvent(this));
			callback.accept(response);
		};

		EventManager.callEvent(new VocalServerLeavePreEvent(this, update));
	}

	@Override
	public void close() {
		Runnable update = () -> {
			tryOpening.set(false);
			closeConnection();
			EventManager.unregisterListener(this);
		};
		EventManager.callEvent(new VocalServerClosePreEvent(this), update, new VocalServerClosePostEvent(this));
	}

	@Override
	public IServerRequestManager getRequestManager() {
		return serverRequestManager;
	}

	@Override
	public IVocalServerPlayerList getPlayers() {
		return players;
	}

	@Override
	public IVocalMainPlayer getMainPlayer() {
		return mainPlayer;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof IVocalServer))
			return false;

		IVocalServer other = (IVocalServer) obj;
		return name.equals(other.getName()) && address.equals(other.getAddress());
	}

	@Override
	public String toString() {
		return String.format("%s_Vocal_%s:%s", name, getAddress().getAddress().getHostAddress(), getAddress().getPort());
	}

	@EventHandler
	private void onConnectionComplete(ConnectionCompleteEvent event) {
		if (tcpConnection == null || !event.getConnection().equals(tcpConnection.getTcpConnection()))
			return;

		setReachable(true);

		Thread communicationProtocolVersionThread = new Thread(() -> {
			lock.lock();
			try {
				if (!communicationProtocolVersion.await(5000, TimeUnit.MILLISECONDS)) {
					tcpConnection.getTcpConnection().dispose();
					udpConnection.getUdpConnection().dispose();
					throw new IllegalStateException("Time out on establishing the version of the communication protocol.");
				}
			} catch (InterruptedException e) {
				// Do nothing
			} finally {
				tryOpening.set(false);
				lock.unlock();
			}
		}, "CommunicationProtocolVersion");
		communicationProtocolVersionThread.setDaemon(true);
		communicationProtocolVersionThread.start();
	}

	@EventHandler
	private void onSetCommunicationProtocolVersion(VocalCommunicationProtocolVersionSetPostEvent event) {
		if (tcpConnection == null || !event.getConnection().equals(tcpConnection))
			return;

		lock.lock();
		try {
			communicationProtocolVersion.signal();
		} finally {
			lock.unlock();
		}

		if (connectionLost && isJoined())
			join(mainPlayer.getName(), response -> connectionLost = false);
	}

	@EventHandler
	private void onServerJoin(VocalServerJoinPostEvent event) {
		if (!event.getServer().equals(this))
			return;

		Consumer<IResponse> callback = response -> {
			if (response.hasFailed())
				EventManager.callEvent(new LogEvent("Error while retrieving server configuration, reason: %s", response.getErrorCode().getMessage()));
			else {
				lock.lock();
				try {
					serverConfiguration.signal();
				} finally {
					lock.unlock();
				}
			}
		};

		tcpConnection.getServerConfiguration(callback);

		SoundResourcesProvider.getMixer().clear(false);

		if (!getMainPlayer().isDeafen())
			SoundResourcesProvider.getSpeakers().start();
		if (!getMainPlayer().isMute())
			SoundResourcesProvider.getMicrophone().start();
	}

	@EventHandler
	private void onMicrophoneDataEncoded(MicrophoneDataEncodedEvent event) {
		EventManager.callEvent(new VocalPlayerSpeakPreEvent(getMainPlayer(), event.getEncoded(), true, true));
	}

	@EventHandler
	private void onPlayerSpeak(VocalPlayerSpeakPostEvent event) {
		if (!event.getPlayer().getServer().equals(this))
			return;

		// Player's name
		String name = event.getPlayer().getName();

		// Audio sample
		byte[] data = event.getData();

		// Data mono status;
		boolean isMono = event.isMono();

		// Data encoded status
		boolean isEncoded = event.isEncoded();

		// Global volume
		double global = event.getVolume().getGlobal();

		// Left volume
		double left = event.getVolume().getLeft();

		// Right volume
		double right = event.getVolume().getRight();

		SoundResourcesProvider.getMixer().put(new AudioPacket(name, data, isMono, isEncoded, global, right, left));
	}

	@EventHandler
	private void onServerLeave(VocalServerLeavePostEvent event) {
		if (!event.getServer().equals(this))
			return;

		isJoined.set(false);
		SoundResourcesProvider.getSpeakers().stop();
		SoundResourcesProvider.getMicrophone().stop();
		((VocalServerPlayerList) getPlayers()).clear();
	}

	@EventHandler
	private void onConnectionDisposed(ConnectionDisposedEvent event) {
		if (tcpConnection == null || !event.getConnection().equals(tcpConnection.getTcpConnection()))
			return;

		setReachable(false);
		SoundResourcesProvider.getSpeakers().stop();
		SoundResourcesProvider.getMicrophone().stop();
		EventManager.unregisterListener(this);
	}

	@EventHandler
	private void onConnectionLost(ConnectionLostEvent event) {
		if (tcpConnection == null || !event.getConnection().equals(tcpConnection.getTcpConnection()))
			return;

		setReachable(false);
		connectionLost = true;
		SoundResourcesProvider.getSpeakers().stop();
		SoundResourcesProvider.getMicrophone().stop();
		((VocalServerPlayerList) getPlayers()).clear();
	}

	@EventHandler
	private void onPlayerNameChanged(VocalPlayerNameChangePostEvent event) {
		if (!event.getPlayer().getServer().equals(this))
			return;

		SoundResourcesProvider.getMixer().renameStream(event.getOldName(), event.getPlayer().getName());
	}

	/**
	 * Set the the new reachable status of the remote.
	 * 
	 * @param isReachable True if the remote is reachable, false otherwise.
	 */
	private void setReachable(boolean isReachable) {
		if (this.isReachable.compareAndSet(!isReachable, isReachable))
			EventManager.callEvent(new VocalServerReachableStatusChangeEvent(this, !isReachable));
	}

	private void openConnection() {
		if (isReachable())
			return;

		tcpConnection = new VocalTcpConnection(this);
		udpConnection = new VocalUdpConnection(this);
		tcpConnection.getTcpConnection().connect();
	}

	private void closeConnection() {
		if (tcpConnection == null || tcpConnection.getTcpConnection().isDisposed())
			return;

		tcpConnection.getTcpConnection().dispose();
		udpConnection.getUdpConnection().dispose();
	}
}
