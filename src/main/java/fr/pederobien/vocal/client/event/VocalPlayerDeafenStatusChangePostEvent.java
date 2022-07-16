package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.client.interfaces.IVocalPlayer;

public class VocalPlayerDeafenStatusChangePostEvent extends VocalPlayerEvent {
	private boolean oldDeafen;

	/**
	 * Creates an event thrown when the deafen status of a player has changed.
	 * 
	 * @param player    The player whose the deafen status has changed.
	 * @param oldDeafen The old player's deafen status.
	 */
	public VocalPlayerDeafenStatusChangePostEvent(IVocalPlayer player, boolean oldDeafen) {
		super(player);
		this.oldDeafen = oldDeafen;
	}

	/**
	 * @return The old player's deafen status.
	 */
	public boolean getOldDeafen() {
		return oldDeafen;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("currentDeafen=" + getPlayer().isDeafen());
		joiner.add("oldDeafen=" + getOldDeafen());
		return String.format("%s_%s", getName(), joiner);
	}
}
