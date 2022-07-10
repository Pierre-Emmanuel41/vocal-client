package fr.pederobien.vocal.client.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import fr.pederobien.vocal.client.interfaces.IVocalSecondaryPlayer;
import fr.pederobien.vocal.client.interfaces.IVocalServer;

public class VocalSecondaryPlayer extends AbstractPlayer implements IVocalSecondaryPlayer {
	private AtomicBoolean isMuteByMainPlayer;

	/**
	 * Creates a player associated to a name and a server.
	 * 
	 * @param server             The server on which this player is registered.
	 * @param name               The player name.
	 * @param isMute             The player's mute status.
	 * @param isDeafen           The player's deafen status.
	 * @param isMuteByMainPlayer The player's mute status for the server main player.
	 */
	public VocalSecondaryPlayer(IVocalServer server, String name, boolean isMute, boolean isDeafen, boolean isMuteByMainPlayer) {
		super(server, name);

		this.isMuteByMainPlayer = new AtomicBoolean(false);

		setMute0(isMute);
		setDeafen0(isDeafen);
		setMuteByMainPlayer0(isMuteByMainPlayer);
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
		if (!setMuteByMainPlayer0(isMuteByMainPlayer))
			return;

		// EventManager.callEvent(new PlayerMuteStatusChangePostEvent(this, oldMute));
	}

	/**
	 * Set the mute status of this player.
	 * 
	 * @param isMuteByMainPlayer The new mute status of this player for the server main player.
	 * 
	 * @return True if the mute status has changed, false otherwise.
	 */
	protected boolean setMuteByMainPlayer0(boolean isMuteByMainPlayer) {
		return this.isMuteByMainPlayer.compareAndSet(!isMuteByMainPlayer, isMuteByMainPlayer);
	}
}
