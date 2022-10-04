package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;

import fr.pederobien.utils.ICancellable;
import fr.pederobien.vocal.client.interfaces.IVocalPlayer;

public class VocalPlayerVolumeChangePreEvent extends VocalPlayerEvent implements ICancellable {
	private boolean isCancelled;
	private float newVolume;

	/**
	 * Creates an event thrown when the sound volume of a player is about to change.
	 * 
	 * @param player    The player whose the sound volume is about to change.
	 * @param newVolume The new player's sound volume.
	 * @param callback  The action to execute when an answer has been received from the server.
	 */
	public VocalPlayerVolumeChangePreEvent(IVocalPlayer player, float newVolume) {
		super(player);
		this.newVolume = newVolume;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	/**
	 * @return The new player's sound volume.
	 */
	public float getNewVolume() {
		return newVolume;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("currentVolume=" + getPlayer().getVolume());
		joiner.add(String.format("newVolume=%.2f", getNewVolume()));
		return String.format("%s_%s", getName(), joiner);
	}
}
