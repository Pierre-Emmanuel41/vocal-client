package fr.pederobien.vocal.client.event;

import fr.pederobien.vocal.client.interfaces.IVocalMainPlayer;

public class VocalMainPlayerEvent extends VocalPlayerEvent {

	/**
	 * Creates a vocal main player event.
	 * 
	 * @param player The server main player source involved in this event.
	 */
	public VocalMainPlayerEvent(IVocalMainPlayer player) {
		super(player);
	}

	@Override
	public IVocalMainPlayer getPlayer() {
		return (IVocalMainPlayer) super.getPlayer();
	}
}
