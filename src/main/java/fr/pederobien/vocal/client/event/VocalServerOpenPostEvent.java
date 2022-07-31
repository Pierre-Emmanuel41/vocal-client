package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.client.interfaces.IVocalServer;

public class VocalServerOpenPostEvent extends VocalServerEvent {

	/**
	 * Creates an event thrown when a vocal server has been opened.
	 * 
	 * @param server The vocal server that has been opened.
	 */
	public VocalServerOpenPostEvent(IVocalServer server) {
		super(server);
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("server=" + getServer());
		return String.format("%s_%s", getName(), joiner);
	}
}
