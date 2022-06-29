package fr.pederobien.vocal.client.impl;

import java.util.function.Consumer;

import fr.pederobien.vocal.client.interfaces.IVocalMainPlayer;
import fr.pederobien.vocal.client.interfaces.IResponse;
import fr.pederobien.vocal.client.interfaces.IVocalServer;

public class VocalMainPlayer extends AbstractPlayer implements IVocalMainPlayer {

	/**
	 * Creates a player associated to a name and a server.
	 * 
	 * @param server The server on which this player is registered.
	 * @param name   The player name.
	 */
	protected VocalMainPlayer(IVocalServer server, String name) {
		super(server, name);
	}

	@Override
	public void setDeafen(boolean isDeafen, Consumer<IResponse> callback) {

	}
}
