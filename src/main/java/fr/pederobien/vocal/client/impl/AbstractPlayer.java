package fr.pederobien.vocal.client.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import fr.pederobien.vocal.client.interfaces.IPlayer;
import fr.pederobien.vocal.client.interfaces.IResponse;
import fr.pederobien.vocal.client.interfaces.IVocalServer;

public abstract class AbstractPlayer implements IPlayer {
	private IVocalServer server;
	private String name;
	private AtomicBoolean isMute, isDeafen;
	private Lock lock;

	/**
	 * Creates a player associated to a name.
	 * 
	 * @param name The player name.
	 */
	protected AbstractPlayer(IVocalServer server, String name) {
		this.server = server;
		this.name = name;

		isMute = new AtomicBoolean(false);
		isDeafen = new AtomicBoolean(false);
		lock = new ReentrantLock(true);
	}

	@Override
	public IVocalServer getServer() {
		return server;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isMute() {
		return isMute.get();
	}

	@Override
	public void setMute(boolean isMute, Consumer<IResponse> callback) {
		if (isMute() == isMute)
			return;

	}

	@Override
	public boolean isDeafen() {
		return isDeafen.get();
	}

	/**
	 * Set the mute status of this player. For internal use only.
	 * 
	 * @param isMute The new player mute status.
	 */
	public void setMute(boolean isMute) {
		setMute0(isMute);
		// if (setMute0(isMute))
		// EventManager.callEvent(new PlayerMuteStatusChangePostEvent(this, !isMute));
	}

	/**
	 * Set the deafen status of this player. For internal use only.
	 * 
	 * @param isDeafen The new player deafen status.
	 */
	public void setDeafen(boolean isDeafen) {
		setDeafen0(isDeafen);
		// if (setDeafen0(isDeafen))
		// EventManager.callEvent(new PlayerDeafenStatusChangePostEvent(this, !isDeafen));
	}

	/**
	 * @return The lock associated to this player.
	 */
	protected Lock getLock() {
		return lock;
	}

	/**
	 * Set the name of this player.
	 * 
	 * @param name The new name of this player.
	 */
	protected void setName0(String name) {
		this.name = name;
	}

	/**
	 * Set the mute status of this player.
	 * 
	 * @param isMute The new mute status of this player.
	 * 
	 * @return True if the mute status has changed, false otherwise.
	 */
	protected boolean setMute0(boolean isMute) {
		return this.isMute.compareAndSet(!isMute, isMute);
	}

	/**
	 * Set the deafen status of this player.
	 * 
	 * @param isDeafen The new deafen status of this player.
	 * 
	 * @return True if the deafen status has changed, false otherwise.
	 */
	protected boolean setDeafen0(boolean isDeafen) {
		return this.isDeafen.compareAndSet(!isDeafen, isDeafen);
	}
}