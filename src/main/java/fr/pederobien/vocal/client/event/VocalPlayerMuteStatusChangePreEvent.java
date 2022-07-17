package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.utils.ICancellable;
import fr.pederobien.vocal.client.interfaces.IVocalPlayer;

public class VocalPlayerMuteStatusChangePreEvent extends VocalPlayerEvent implements ICancellable {
	private boolean isCancelled;
	private boolean newMute;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the mute status of a player is about to change.
	 * 
	 * @param player   The player whose the mute status is about to change.
	 * @param newMute  The new player's mute status.
	 * @param callback The action to execute when an answer has been received from the server.
	 */
	public VocalPlayerMuteStatusChangePreEvent(IVocalPlayer player, boolean newMute, Consumer<IResponse> callback) {
		super(player);
		this.newMute = newMute;
		this.callback = callback;
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
	 * @return The new player's mute status.
	 */
	public boolean getNewMute() {
		return newMute;
	}

	/**
	 * @return The action to execute when an answer has been received from the server.
	 */
	public Consumer<IResponse> getCallback() {
		return callback;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("currentMute=" + getPlayer().isMute());
		joiner.add("newMute=" + getNewMute());
		return String.format("%s_%s", getName(), joiner);
	}
}
