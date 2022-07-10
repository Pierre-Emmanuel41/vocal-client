package fr.pederobien.vocal.client.event;

import fr.pederobien.vocal.client.interfaces.IVocalServerPlayerList;

public class VocalServerListEvent extends ProjectVocalClientEvent {
	private IVocalServerPlayerList list;

	/**
	 * Creates a vocal server list event.
	 * 
	 * @param list The list source involved in this event.
	 */
	public VocalServerListEvent(IVocalServerPlayerList list) {
		this.list = list;
	}

	/**
	 * @return The list involved in this event.
	 */
	public IVocalServerPlayerList getList() {
		return list;
	}
}
