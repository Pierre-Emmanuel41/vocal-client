package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.client.interfaces.IVocalPlayer;
import fr.pederobien.vocal.client.interfaces.IVocalServerPlayerList;

public class VocalServerListPlayerRemovePostEvent extends VocalServerListEvent {
	private IVocalPlayer player;

	/**
	 * Creates an event thrown when a player has been removed from the list of players of a vocal server;
	 * 
	 * @param list   The list to which a player has been added.
	 * @param player The removed player.
	 */
	public VocalServerListPlayerRemovePostEvent(IVocalServerPlayerList list, IVocalPlayer player) {
		super(list);
		this.player = player;
	}

	/**
	 * @return The removed player.
	 */
	public IVocalPlayer getPlayer() {
		return player;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("list=" + getList().getName());
		joiner.add("player=" + getPlayer().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
