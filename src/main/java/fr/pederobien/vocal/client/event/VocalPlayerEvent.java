package fr.pederobien.vocal.client.event;

import fr.pederobien.vocal.client.interfaces.IVocalPlayer;

public class VocalPlayerEvent extends ProjectVocalClientEvent {
	private IVocalPlayer player;

	/**
	 * Creates a vocal player event.
	 * 
	 * @param player The player source involved in this event.
	 */
	public VocalPlayerEvent(IVocalPlayer player) {
		this.player = player;
	}

	/**
	 * @return The player involved in this event.
	 */
	public IVocalPlayer getPlayer() {
		return player;
	}
}
