package fr.pederobien.vocal.client.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.client.impl.VocalTcpConnection;
import fr.pederobien.vocal.client.interfaces.IVocalServer;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;

public class VocalCommunicationProtocolVersionSetPostEvent extends VocalServerEvent {
	private IVocalMessage request;
	private float version;
	private VocalTcpConnection connection;

	/**
	 * Creates an event throw when a request has been received from the remote in order to set a specific version of the communication
	 * protocol to use between the client and the server.
	 * 
	 * @param server     The server that received the request.
	 * @param request    The request sent by the remote.
	 * @param version    The version to use.
	 * @param connection The connection that has received the request.
	 */
	public VocalCommunicationProtocolVersionSetPostEvent(IVocalServer server, IVocalMessage request, float version, VocalTcpConnection connection) {
		super(server);
		this.request = request;
		this.version = version;
		this.connection = connection;
	}

	/**
	 * @return The request sent by the remote.
	 */
	public IVocalMessage getRequest() {
		return request;
	}

	/**
	 * @return The version of the communication protocol to use between the client and the server.
	 */
	public float getVersion() {
		return version;
	}

	/**
	 * @return The connection that received the request.
	 */
	public VocalTcpConnection getConnection() {
		return connection;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("server=" + getServer().getName());
		joiner.add("version=" + getVersion());
		return String.format("%s_%s", getName(), joiner);
	}
}
