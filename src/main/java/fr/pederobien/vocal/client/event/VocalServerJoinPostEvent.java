package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.client.interfaces.IVocalServer;

public class VocalServerJoinPostEvent extends VocalServerEvent {

	/**
	 * Creates an event thrown when a server has been joined by a player.
	 * 
	 * @param server The server the player has joined.
	 */
	public VocalServerJoinPostEvent(IVocalServer server) {
		super(server);
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("server=" + getServer().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
