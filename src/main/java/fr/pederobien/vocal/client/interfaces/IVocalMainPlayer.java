package fr.pederobien.vocal.client.interfaces;

import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;

public interface IVocalMainPlayer extends IVocalPlayer {

	/**
	 * Set the name of this player.
	 * 
	 * @param name     The new player name.
	 * @param callback The action to execute when an answer has been received from the server.
	 */
	void setName(String name, Consumer<IResponse> callback);

	/**
	 * Set the deafen status of this player.
	 * 
	 * @param isDeafen The new player deafen status.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void setDeafen(boolean isDeafen, Consumer<IResponse> callback);
}
