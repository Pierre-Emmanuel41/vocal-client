package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.utils.ICancellable;
import fr.pederobien.vocal.client.interfaces.IResponse;
import fr.pederobien.vocal.client.interfaces.IVocalMainPlayer;

public class VocalMainPlayerDeafenStatusChangePreEvent extends VocalMainPlayerEvent implements ICancellable {
	private boolean isCancelled;
	private boolean newDeafen;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the deafen status of a server main player is about to change.
	 * 
	 * @param player    The player whose the deafen status is about to change.
	 * @param newDeafen The new player deafen status.
	 * @param callback  The action to execute when an answer has been received from the server.
	 */
	public VocalMainPlayerDeafenStatusChangePreEvent(IVocalMainPlayer player, boolean newDeafen, Consumer<IResponse> callback) {
		super(player);
		this.newDeafen = newDeafen;
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
	 * @return The new player deafen status.
	 */
	public boolean getNewDeafen() {
		return newDeafen;
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
		joiner.add("currentDeafen=" + getPlayer().isDeafen());
		joiner.add("newDeafen=" + getNewDeafen());
		return String.format("%s_%s", getName(), joiner);
	}
}
