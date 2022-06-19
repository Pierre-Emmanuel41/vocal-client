package fr.pederobien.vocal.client.event;

import fr.pederobien.vocal.client.interfaces.IVocalServer;

public class VocalServerEvent extends ProjectVocalClientEvent {
	private IVocalServer server;

	/**
	 * Creates a vocal server event.
	 * 
	 * @param server The server source involved in this event.
	 */
	public VocalServerEvent(IVocalServer server) {
		this.server = server;
	}

	/**
	 * @return The server involved in this event.
	 */
	public IVocalServer getServer() {
		return server;
	}
}
