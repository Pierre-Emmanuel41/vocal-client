package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;
import java.util.function.Consumer;

import fr.pederobien.utils.ICancellable;
import fr.pederobien.vocal.client.interfaces.IResponse;
import fr.pederobien.vocal.client.interfaces.IVocalServer;

public class VocalServerJoinPreEvent extends VocalServerEvent implements ICancellable {
	private boolean isCancelled;
	private String playerName;
	private Consumer<IResponse> callback;

	/**
	 * Creates an event thrown when a player is about to join a vocal server.
	 * 
	 * @param server     The server the player is about to join.
	 * @param playerName The player's name.
	 * @param callback   The action to execute when an answer has been received from the server.
	 */
	public VocalServerJoinPreEvent(IVocalServer server, String playerName, Consumer<IResponse> callback) {
		super(server);
		this.playerName = playerName;
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
	 * @return The player's name.
	 */
	public String getPlayerName() {
		return playerName;
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
		joiner.add("server=" + getServer().getName());
		joiner.add("player=" + getPlayerName());
		return String.format("%s_%s", getName(), joiner);
	}
}
