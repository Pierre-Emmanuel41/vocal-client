package fr.pederobien.vocal.client.impl;

import java.time.LocalTime;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TimeSynchroThread extends Thread {
	private LocalTime time;
	private Lock lock;

	/**
	 * Creates a thread in order to keep time-synchronized this client with the remote.
	 * 
	 * @param server The server associated to this thread.
	 */
	public TimeSynchroThread(LocalTime time) {
		super("TimeSynchronizer");
		this.time = time;

		setDaemon(true);
		setPriority(MAX_PRIORITY);

		lock = new ReentrantLock(true);
	}

	/**
	 * @return The actual time synchronized with the remote.
	 */
	public LocalTime getTime() {
		return time;
	}

	/**
	 * Set the new server time to synchronize with the remote.
	 * 
	 * @param time The new time.
	 */
	public void setTime(LocalTime time) {
		lock.lock();
		try {
			this.time = time;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				Thread.sleep(10);

				lock.lock();
				time = time.plusNanos(10000000);
				lock.unlock();
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}
