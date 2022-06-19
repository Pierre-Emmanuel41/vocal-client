package fr.pederobien.vocal.client.event;

import java.util.List;
import java.util.StringJoiner;

import fr.pederobien.vocal.client.interfaces.IVocalServer;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;

public class CommunicationProtocolVersionGetPostEvent extends VocalServerEvent {
	private IVocalMessage request;

	/**
	 * Creates an event throw when a request has been received from the remote in order to get the supported versions of the
	 * communication protocol.
	 * 
	 * @param server  The server that received the request.
	 * @param request The request sent by the remote in order to get the supported versions.
	 */
	public CommunicationProtocolVersionGetPostEvent(IVocalServer server, IVocalMessage request) {
		super(server);
		this.request = request;
	}

	/**
	 * @return The request sent by the remote.
	 */
	public IVocalMessage getRequest() {
		return request;
	}

	/**
	 * @return An array that contains the supported versions of the communication protocol.
	 */
	public List<Float> getVersions() {
		return getServer().getRequestManager().getVersions();
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("server=" + getServer().getName());

		StringJoiner versionsJoiner = new StringJoiner(", ", "{", "}");
		for (float version : getVersions())
			versionsJoiner.add("" + version);

		joiner.add("versions=" + versionsJoiner);
		return String.format("%s_%s", getName(), joiner);
	}
}
