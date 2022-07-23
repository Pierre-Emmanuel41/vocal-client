package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.client.interfaces.IVocalPlayer;

public class VocalPlayerSpeakPreEvent extends VocalPlayerEvent {
	private byte[] data;
	private boolean isMono, isEncoded;

	/**
	 * Creates an event thrown when a player is about to send an audio sample to the vocal server.
	 * 
	 * @param player    The player that is about to send an audio sample.
	 * @param data      The audio sample.
	 * @param isMono    True if the audio signal is a mono signal, false otherwise.
	 * @param isEncoded True if the audio sample has been encoded, false otherwise.
	 */
	public VocalPlayerSpeakPreEvent(IVocalPlayer player, byte[] data, boolean isMono, boolean isEncoded) {
		super(player);
		this.data = data;
		this.isMono = isMono;
		this.isEncoded = isEncoded;
	}

	/**
	 * @return The byte array that represents the audio sample.
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @return True if the audio signal is a mono signal, false otherwise.
	 */
	public boolean isMono() {
		return isMono;
	}

	/**
	 * @return True if the audio sample has been encoded, false otherwise.
	 */
	public boolean isEncoded() {
		return isEncoded;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("length=" + getData().length);
		joiner.add("isMono=" + isMono());
		joiner.add("isEncoded=" + isEncoded());
		return String.format("%s_%s", getName(), joiner);
	}
}
