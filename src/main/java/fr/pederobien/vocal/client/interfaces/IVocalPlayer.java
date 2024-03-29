package fr.pederobien.vocal.client.interfaces;

import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;

public interface IVocalPlayer {

	/**
	 * @return The server on which this player is registered.
	 */
	IVocalServer getServer();

	/**
	 * @return The player name.
	 */
	String getName();

	/**
	 * @return True if this player is mute, false otherwise.
	 */
	boolean isMute();

	/**
	 * Set the mute status of this player.
	 * 
	 * @param isMute   The new player mute status.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void setMute(boolean isMute, Consumer<IResponse> callback);

	/**
	 * @return True is this player is deafen, false otherwise.
	 */
	boolean isDeafen();

	/**
	 * @return The audio volume of the player.
	 */
	float getVolume();

	/**
	 * Set the sound volume of the given player.
	 * 
	 * @param volume The new sound volume.
	 */
	void setVolume(float volume);
}
