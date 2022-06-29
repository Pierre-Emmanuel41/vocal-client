package fr.pederobien.vocal.client.interfaces;

public interface ISecondaryVocalPlayer extends IVocalPlayer {

	/**
	 * @return True if this player is mute by the server main player.
	 */
	boolean isMuteByMainPlayer();
}
