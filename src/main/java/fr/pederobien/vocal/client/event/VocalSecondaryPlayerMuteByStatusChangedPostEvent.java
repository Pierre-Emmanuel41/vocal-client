package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.client.interfaces.IVocalMainPlayer;
import fr.pederobien.vocal.client.interfaces.IVocalSecondaryPlayer;

public class VocalSecondaryPlayerMuteByStatusChangedPostEvent extends VocalSecondaryPlayerEvent {
	private boolean oldMute;

	public VocalSecondaryPlayerMuteByStatusChangedPostEvent(IVocalSecondaryPlayer player, boolean oldMute) {
		super(player);
		this.oldMute = oldMute;
	}

	/**
	 * The player that is muted or unmuted for the muting player.
	 */
	@Override
	public IVocalSecondaryPlayer getPlayer() {
		return super.getPlayer();
	}

	/**
	 * @return The player that mutes or unmutes another player.
	 */
	public IVocalMainPlayer getMutingPlayer() {
		return getPlayer().getServer().getMainPlayer();
	}

	/**
	 * @return The old mute status of the muted player for the muting player.
	 */
	public boolean getOldMute() {
		return oldMute;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("target=" + getPlayer().getName());
		joiner.add("source=" + getMutingPlayer().getName());
		joiner.add("currentMute=" + getPlayer().isMuteByMainPlayer());
		joiner.add("oldMute=" + getOldMute());
		return String.format("%s_%s", getName(), joiner);
	}
}
