package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;

import fr.pederobien.utils.ICancellable;
import fr.pederobien.vocal.client.interfaces.IVocalServer;

public class VocalServerClosePreEvent extends VocalServerEvent implements ICancellable {
	private boolean isCancelled;

	/**
	 * Creates an event thrown when a server is about to be closed.
	 * 
	 * @param server The server that is about to be closed.
	 */
	public VocalServerClosePreEvent(IVocalServer server) {
		super(server);
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("server=" + getServer());
		return String.format("%s_%s", getName(), joiner);
	}
}
