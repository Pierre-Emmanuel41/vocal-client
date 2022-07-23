package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.client.interfaces.IVocalPlayer;
import fr.pederobien.vocal.common.impl.VolumeResult;

public class VocalPlayerSpeakPostEvent extends VocalPlayerEvent {
	private byte[] data;
	private boolean isMono, isEncoded;
	private VolumeResult volume;

	/**
	 * Creates an event thrown when an audio sample should be played.
	 * 
	 * @param player    The speaking player.
	 * @param data      The bytes array that represents the audio sample.
	 * @param volume    The different sound volume of the audio sample.
	 * @param isMono    True if the audio signal is a mono signal, false otherwise.
	 * @param isEncoded True if the audio sample has been encoded, false otherwise.
	 */
	public VocalPlayerSpeakPostEvent(IVocalPlayer player, byte[] data, boolean isMono, boolean isEncoded, VolumeResult volume) {
		super(player);
		this.data = data;
		this.isMono = isMono;
		this.isEncoded = isEncoded;
		this.volume = volume;
	}

	/**
	 * @return The audio sample.
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

	/**
	 * @return The different sound volume of the audio sample.
	 */
	public VolumeResult getVolume() {
		return volume;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("isMono=" + isMono());
		joiner.add("isEncoded=" + isEncoded());
		String format = "volume={global=%s, left=%s, right=%s}";
		joiner.add(String.format(format, getVolume().getGlobal(), getVolume().getLeft(), getVolume().getRight()));
		return String.format("%s_%s", getName(), joiner);
	}
}
