package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.client.interfaces.IVocalServer;

public class VocalServerNameChangePostEvent extends VocalServerEvent {
	private String oldName;

	/**
	 * Creates an event thrown when a server has been renamed.
	 * 
	 * @param server  The server whose the name has changed.
	 * @param oldName The old server name.
	 */
	public VocalServerNameChangePostEvent(IVocalServer server, String oldName) {
		super(server);
		this.oldName = oldName;
	}

	/**
	 * @return The old server name.
	 */
	public String getOldName() {
		return oldName;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("server=" + getServer());
		joiner.add("oldName=" + getOldName());
		return String.format("%s_%s", getName(), joiner);
	}
}
