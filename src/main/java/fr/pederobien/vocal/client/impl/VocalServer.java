package fr.pederobien.vocal.client.impl;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fr.pederobien.communication.event.ConnectionCompleteEvent;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.vocal.client.event.ServerReachableStatusChangeEvent;
import fr.pederobien.vocal.client.interfaces.IServerRequestManager;
import fr.pederobien.vocal.client.interfaces.IVocalServer;

public class VocalServer implements IVocalServer, IEventListener {
	private String name;
	private InetSocketAddress address;
	private AtomicBoolean isReachable;
	private AtomicBoolean tryOpening;
	private IServerRequestManager serverRequestManager;
	private VocalTcpConnection connection;
	private Lock lock;
	private Condition serverConfiguration, communicationProtocolVersion;

	public VocalServer(String name, InetSocketAddress address) {
		this.name = name;
		this.address = address;

		isReachable = new AtomicBoolean(false);
		tryOpening = new AtomicBoolean(false);
		connection = new VocalTcpConnection(this);

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
			this.name = name;
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
			if (this.address.equals(address))
				return;

			InetSocketAddress oldAddress = this.address;
			this.address = address;
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
	public void close() {
		closeConnection();
	}

	@Override
	public IServerRequestManager getRequestManager() {
		return serverRequestManager;
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
