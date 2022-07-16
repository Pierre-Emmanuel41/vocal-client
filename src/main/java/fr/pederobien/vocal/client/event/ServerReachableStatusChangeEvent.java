package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.client.interfaces.IVocalServer;

public class ServerReachableStatusChangeEvent extends VocalServerEvent {
	private boolean oldReachable;

	/**
	 * Creates an event thrown when the reachable status of a server has changed.
	 * 
	 * @param server       The server whose the reachable status has changed.
	 * @param oldReachable The old reachable status.
	 */
	public ServerReachableStatusChangeEvent(IVocalServer server, boolean oldReachable) {
		super(server);
		this.oldReachable = oldReachable;
	}

	/**
	 * @return The old reachable status of the server.
	 */
	public boolean getOldReachable() {
		return oldReachable;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("server=" + getServer().getName());
		joiner.add("currentReachable=" + getServer().isReachable());
		joiner.add("oldReachable=" + getOldReachable());
		return String.format("%s_%s", getName(), joiner);
	}
}
