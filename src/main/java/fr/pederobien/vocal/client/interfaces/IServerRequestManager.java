package fr.pederobien.vocal.client.interfaces;

import java.util.List;

import fr.pederobien.vocal.client.impl.RequestReceivedHolder;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;

public interface IServerRequestManager {

	/**
	 * @return The latest version of the communication protocol associated to this requests manager.
	 */
	float getVersion();

	/**
	 * @return An array that contains all supported versions of the communication protocol.
	 */
	List<Float> getVersions();

	/**
	 * Performs server configuration update according to the given request.
	 * 
	 * @param request The request sent by the remote.
	 */
	void apply(RequestReceivedHolder holder);

	/**
	 * Creates a message in order to specify the supported versions of the communication protocol.
	 * 
	 * @param request  The request sent by the remote in order to get the supported versions.
	 * @param versions A list that contains the supported versions.
	 * 
	 * @return The message to send to the server in order to specify the supported versions.
	 */
	IVocalMessage onGetCommunicationProtocolVersions(IVocalMessage request, List<Float> versions);

	/**
	 * Creates a message in order to set the version of the communication protocol to use between the client and the server.
	 * 
	 * @param request The request sent by the remote in order to get the supported versions.
	 * @param version The version of the communication protocol.
	 * 
	 * @return The message to send to the server in order to specify the supported versions.
	 */
	IVocalMessage onSetCommunicationProtocolVersion(IVocalMessage request, float version);
}
