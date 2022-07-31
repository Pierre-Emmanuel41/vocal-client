package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.client.interfaces.IVocalServer;

public class VocalServerClosePostEvent extends VocalServerEvent {

	/**
	 * Creates an event thrown when a server has been closed.
	 * 
	 * @param server The closed server.
	 */
	public VocalServerClosePostEvent(IVocalServer server) {
		super(server);
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("server=" + getServer());
		return String.format("%s_%s", getName(), joiner);
	}
}
