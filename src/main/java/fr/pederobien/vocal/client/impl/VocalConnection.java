package fr.pederobien.vocal.client.impl;

import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.vocal.client.interfaces.IServerRequestManager;
import fr.pederobien.vocal.client.interfaces.IVocalServer;

public class VocalConnection implements IEventListener {
	private IVocalServer server;
	private static float version;

	protected VocalConnection(IVocalServer server) {
		this.server = server;
		version = -1.0f;
	}

	/**
	 * @return The server associated to this vocal connection.
	 */
	protected IVocalServer getServer() {
		return server;
	}

	/**
	 * @return The request manager associated to the server.
	 */
	protected IServerRequestManager getRequestManager() {
		return getServer().getRequestManager();
	}

	/**
	 * @return The version of the communication protocol.
	 */
	protected float getVersion() {
		return version;
	}

	/**
	 * Set the version of the communication protocol.
	 * 
	 * @param version The new version to use.
	 */
	protected void setVersion(float version) {
		VocalConnection.version = version;
	}
}
