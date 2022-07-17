package fr.pederobien.vocal.client.impl;

import fr.pederobien.vocal.common.interfaces.IVocalMessage;

public class RequestReceivedHolder {
	private IVocalMessage request;
	private VocalConnection connection;

	/**
	 * Creates a holder to gather the request received from the remote and the connection that received the request.
	 * 
	 * @param request    The request sent by the remote.
	 * @param connection The connection that has received the request.
	 */
	public RequestReceivedHolder(IVocalMessage request, VocalConnection connection) {
		this.request = request;
		this.connection = connection;
	}

	/**
	 * @return The request sent by the remote.
	 */
	public IVocalMessage getRequest() {
		return request;
	}

	/**
	 * @return The connection that has received the request.
	 */
	public VocalConnection getConnection() {
		return connection;
	}
}
