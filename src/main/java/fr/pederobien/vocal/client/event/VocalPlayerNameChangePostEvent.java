package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.client.interfaces.IVocalPlayer;

public class VocalPlayerNameChangePostEvent extends VocalPlayerEvent {
	private String oldName;

	/**
	 * Creates an event thrown when the name of the server main player has changed.
	 * 
	 * @param player  The player whose the name has changed.
	 * @param oldName The old player name.
	 */
	public VocalPlayerNameChangePostEvent(IVocalPlayer player, String oldName) {
		super(player);
		this.oldName = oldName;
	}

	/**
	 * @return The old player name.
	 */
	public String getOldName() {
		return oldName;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("currentName=" + getPlayer().getName());
		joiner.add("oldName=" + getOldName());
		return String.format("%s_%s", getName(), joiner);
	}
}
