package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.client.interfaces.IVocalPlayer;

public class VocalPlayerSpeakPreEvent extends VocalPlayerEvent {
	private byte[] data;

	/**
	 * Creates an event thrown when a player is about to send an audio sample to the vocal server.
	 * 
	 * @param player The player that is about to send an audio sample.
	 * @param data   The audio sample.
	 */
	public VocalPlayerSpeakPreEvent(IVocalPlayer player, byte[] data) {
		super(player);
		this.data = data;
	}

	/**
	 * @return The byte array that represents the audio sample.
	 */
	public byte[] getData() {
		return data;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("length=" + getData().length);
		return String.format("%s_%s", getName(), joiner);
	}
}
