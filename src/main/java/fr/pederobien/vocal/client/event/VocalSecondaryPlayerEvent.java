package fr.pederobien.vocal.client.event;

import fr.pederobien.vocal.client.interfaces.IVocalSecondaryPlayer;

public class VocalSecondaryPlayerEvent extends VocalPlayerEvent {

	/**
	 * Creates a vocal secondary player event.
	 * 
	 * @param player The player source involved in this event.
	 */
	public VocalSecondaryPlayerEvent(IVocalSecondaryPlayer player) {
		super(player);
	}

	@Override
	public IVocalSecondaryPlayer getPlayer() {
		return (IVocalSecondaryPlayer) super.getPlayer();
	}
}
