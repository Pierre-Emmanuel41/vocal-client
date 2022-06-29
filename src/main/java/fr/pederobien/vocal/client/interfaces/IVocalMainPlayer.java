package fr.pederobien.vocal.client.interfaces;

import java.util.function.Consumer;

public interface IVocalMainPlayer extends IVocalPlayer {

	/**
	 * Set the deafen status of this player.
	 * 
	 * @param isDeafen The new player deafen status.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void setDeafen(boolean isDeafen, Consumer<IResponse> callback);
}
