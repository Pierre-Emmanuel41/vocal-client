package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.client.interfaces.IVocalPlayer;

public class VocalPlayerVolumeChangePostEvent extends VocalPlayerEvent {
	private float oldVolume;

	/**
	 * Creates an event thrown when the sound volume of a player has changed.
	 * 
	 * @param player    The player whose the sound volume has changed.
	 * @param oldVolume The old player's sound volume.
	 */
	public VocalPlayerVolumeChangePostEvent(IVocalPlayer player, float oldVolume) {
		super(player);
		this.oldVolume = oldVolume;
	}

	/**
	 * @return The old player's sound volume.
	 */
	public float getNewVolume() {
		return oldVolume;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("currentVolume=" + getPlayer().getVolume());
		joiner.add(String.format("oldVolume=%.2f", getNewVolume()));
		return String.format("%s_%s", getName(), joiner);
	}
}
