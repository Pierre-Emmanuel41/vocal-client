package fr.pederobien.vocal.client.exceptions;

import fr.pederobien.vocal.client.interfaces.IVocalServerPlayerList;
import fr.pederobien.vocal.client.interfaces.IVocalPlayer;

public class PlayerAlreadyRegisteredException extends ServerPlayerListException {
	private static final long serialVersionUID = 1L;
	private IVocalPlayer player;

	public PlayerAlreadyRegisteredException(IVocalServerPlayerList list, IVocalPlayer player) {
		super(String.format("The player %s is already registered in list %s", player.getName(), list.getName()), list);
		this.player = player;
	}

	/**
	 * @return The already registered player.
	 */
	public IVocalPlayer getPlayer() {
		return player;
	}
}
