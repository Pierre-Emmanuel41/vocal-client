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
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.utils.event.LogEvent;
import fr.pederobien.vocal.client.event.ServerReachableStatusChangeEvent;
import fr.pederobien.vocal.client.event.VocalCommunicationProtocolVersionSetPostEvent;
import fr.pederobien.vocal.client.event.VocalServerAddressChangePostEvent;
import fr.pederobien.vocal.client.event.VocalServerAddressChangePreEvent;
import fr.pederobien.vocal.client.event.VocalServerJoinPostEvent;
import fr.pederobien.vocal.client.event.VocalServerJoinPreEvent;
import fr.pederobien.vocal.client.event.VocalServerLeavePostEvent;
import fr.pederobien.vocal.client.event.VocalServerLeavePreEvent;
import fr.pederobien.vocal.client.event.VocalServerNameChangePostEvent;
import fr.pederobien.vocal.client.event.VocalServerNameChangePreEvent;
import fr.pederobien.vocal.client.impl.request.ServerRequestManager;
import fr.pederobien.vocal.client.interfaces.IResponse;
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
	private VocalTcpConnection connection;
	private Lock lock;
	private Condition serverConfiguration, communicationProtocolVersion;
	private boolean connectionLost;

	public VocalServer(String name, InetSocketAddress address) {
		this.name = name;
		this.address = address;

		isReachable = new AtomicBoolean(false);
		tryOpening = new AtomicBoolean(false);
		isJoined = new AtomicBoolean(false);
		serverRequestManager = new ServerRequestManager(this);
		players = new VocalServerPlayerList(this);
		lock = new ReentrantLock(true);
		serverConfiguration = lock.newCondition();
		communicationProtocolVersion = lock.newCondition();

		EventManager.registerListener(this);
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

			Runnable update = () -> {
				this.address = address;
				if (connection != null && !connection.getTcpConnection().isDisposed())
					closeConnection();
				openConnection();
			};
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

		lock.lock();
		try {
			openConnection();

			if (!communicationProtocolVersion.await(5000, TimeUnit.MILLISECONDS)) {
				connection.getTcpConnection().dispose();
				throw new IllegalStateException("Time out on establishing the version of the communication protocol.");
			}
		} catch (InterruptedException e) {
			// Do nothing
		} finally {
			tryOpening.set(false);
			lock.unlock();
		}
	}

	@Override
	public void join(String name, Consumer<IResponse> callback) {
		if (!isJoined.compareAndSet(false, true))
			return;

		Consumer<IResponse> update = response -> {
			if (!response.hasFailed()) {
				mainPlayer = new VocalMainPlayer(this, name);
				EventManager.callEvent(new VocalServerJoinPostEvent(this));
			}
			callback.accept(response);
		};
		EventManager.callEvent(new VocalServerJoinPreEvent(this, name, update));

		lock.lock();
		try {
			if (!serverConfiguration.await(5000, TimeUnit.MILLISECONDS)) {
				isJoined.set(false);
				connection.getTcpConnection().dispose();
				throw new IllegalStateException("Time out on server configuration request.");
			}

			isJoined.set(true);
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
			if (!response.hasFailed())
				EventManager.callEvent(new VocalServerLeavePostEvent(this));
			callback.accept(response);
		};
		EventManager.callEvent(new VocalServerLeavePreEvent(this, update));
	}

	@Override
	public void close() {
		closeConnection();
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

	@EventHandler
	private void onConnectionComplete(ConnectionCompleteEvent event) {
		if (connection == null || !event.getConnection().equals(connection.getTcpConnection()))
			return;

		setReachable(true);
	}

	@EventHandler
	private void onSetCommunicationProtocolVersion(VocalCommunicationProtocolVersionSetPostEvent event) {
		if (connection == null || !event.getConnection().equals(connection))
			return;

		lock.lock();
		try {
			communicationProtocolVersion.signal();
		} finally {
			lock.unlock();
		}

		if (connectionLost)
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

		connection.getServerConfiguration(callback);
	}

	@EventHandler
	private void onServerLeave(VocalServerLeavePostEvent event) {
		if (!event.getServer().equals(this))
			return;

		isJoined.set(false);
		((VocalServerPlayerList) getPlayers()).clear();
	}

	@EventHandler
	private void onConnectionDisposed(ConnectionDisposedEvent event) {
		if (connection == null || !event.getConnection().equals(connection.getTcpConnection()))
			return;

		setReachable(false);
		EventManager.unregisterListener(this);
	}

	@EventHandler
	private void onConnectionLost(ConnectionLostEvent event) {
		if (connection == null || !event.getConnection().equals(connection.getTcpConnection()))
			return;

		connectionLost = true;
		isJoined.set(false);
		setReachable(false);
		((VocalServerPlayerList) getPlayers()).clear();
	}

	/**
	 * Set the the new reachable status of the remote.
	 * 
	 * @param isReachable True if the remote is reachable, false otherwise.
	 * 
	 * @return True if the reachable status has changed, false otherwise.
	 */
	private boolean setReachable(boolean isReachable) {
		boolean changed = this.isReachable.compareAndSet(!isReachable, isReachable);
		if (changed)
			EventManager.callEvent(new ServerReachableStatusChangeEvent(this, isReachable));

		return changed;
	}

	private void openConnection() {
		if (isReachable())
			return;

		connection = new VocalTcpConnection(this);
		connection.getTcpConnection().connect();
	}

	private void closeConnection() {
		if (!setReachable(false))
			return;

		connection.getTcpConnection().dispose();
	}
}
