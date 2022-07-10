package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.utils.ICancellable;
import fr.pederobien.vocal.client.interfaces.IResponse;
import fr.pederobien.vocal.client.interfaces.IVocalMainPlayer;

public class VocalMainPlayerNameChangePreEvent extends VocalMainPlayerEvent implements ICancellable {
	private boolean isCancelled;
	private String newName;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when the name of the server main player is about to change.
	 * 
	 * @param player   The player whose the name is about to change.
	 * @param newName  The new player name.
	 * @param callback The action to execute when an answer has been received from the server.
	 */
	public VocalMainPlayerNameChangePreEvent(IVocalMainPlayer player, String newName, Consumer<IResponse> callback) {
		super(player);
		this.newName = newName;
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
	 * @return The new player name.
	 */
	public String getNewName() {
		return newName;
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
		joiner.add("currentName=" + getPlayer().getName());
		joiner.add("newName=" + getNewName());
		return String.format("%s_%s", getName(), joiner);
	}
}
