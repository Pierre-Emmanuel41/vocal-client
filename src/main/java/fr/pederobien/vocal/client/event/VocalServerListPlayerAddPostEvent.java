package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.client.interfaces.IVocalPlayer;
import fr.pederobien.vocal.client.interfaces.IVocalServerPlayerList;

public class VocalServerListPlayerAddPostEvent extends VocalServerListEvent {
	private IVocalPlayer player;

	/**
	 * Creates an event thrown when a player has been added to the list of players of a vocal server;
	 * 
	 * @param list   The list to which a player has been added.
	 * @param player The added player.
	 */
	public VocalServerListPlayerAddPostEvent(IVocalServerPlayerList list, IVocalPlayer player) {
		super(list);
		this.player = player;
	}

	/**
	 * @return The added player.
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
