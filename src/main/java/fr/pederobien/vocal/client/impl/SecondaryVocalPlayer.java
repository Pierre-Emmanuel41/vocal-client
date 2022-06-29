package fr.pederobien.vocal.client.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import fr.pederobien.vocal.client.interfaces.ISecondaryVocalPlayer;
import fr.pederobien.vocal.client.interfaces.IVocalServer;

public class SecondaryVocalPlayer extends AbstractPlayer implements ISecondaryVocalPlayer {
	private AtomicBoolean isMuteByMainPlayer;

	/**
	 * Creates a player associated to a name and a server.
	 * 
	 * @param server The server on which this player is registered.
	 * @param name   The player name.
	 */
	public SecondaryVocalPlayer(IVocalServer server, String name) {
		super(server, name);

		isMuteByMainPlayer = new AtomicBoolean(false);
	}

	@Override
	public boolean isMuteByMainPlayer() {
		return isMuteByMainPlayer.get();
	}

	/**
	 * Set the mute status of this player for the main player. For internal use only.
	 * 
	 * @param isMuteByMainPlayer The new player mute status.
	 */
	public void setMuteByMainPlayer(boolean isMuteByMainPlayer) {
		if (!this.isMuteByMainPlayer.compareAndSet(!isMuteByMainPlayer, isMuteByMainPlayer))
			return;

		// EventManager.callEvent(new PlayerMuteStatusChangePostEvent(this, oldMute));
	}
}
