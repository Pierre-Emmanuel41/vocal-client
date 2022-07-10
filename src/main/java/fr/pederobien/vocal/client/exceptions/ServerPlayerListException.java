package fr.pederobien.vocal.client.exceptions;

import fr.pederobien.vocal.client.interfaces.IVocalServerPlayerList;

public class ServerPlayerListException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private IVocalServerPlayerList list;

	public ServerPlayerListException(String message, IVocalServerPlayerList list) {
		super(message);
		this.list = list;
	}

	/**
	 * @return The list involved in this exception.
	 */
	public IVocalServerPlayerList getList() {
		return list;
	}
}
