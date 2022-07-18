package fr.pederobien.vocal.client.event;

import java.net.InetSocketAddress;
import java.util.StringJoiner;

import fr.pederobien.vocal.client.interfaces.IVocalServer;

public class VocalServerAddressChangePostEvent extends VocalServerEvent {
	private InetSocketAddress oldAddress;

	/**
	 * Creates an event thrown when the address of a server has changed.
	 * 
	 * @param server     The server whose address has changed.
	 * @param oldAddress The old server address.
	 */
	public VocalServerAddressChangePostEvent(IVocalServer server, InetSocketAddress oldAddress) {
		super(server);
		this.oldAddress = oldAddress;
	}

	/**
	 * @return The old server address.
	 */
	public InetSocketAddress getOldAddress() {
		return oldAddress;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("server=" + getServer());
		joiner.add("currentAddress=" + getServer().getAddress());
		joiner.add("oldAddress=" + getOldAddress());
		return String.format("%s_%s", getName(), joiner);
	}
}
