package fr.pederobien.vocal.client.interfaces;

import java.util.List;

import fr.pederobien.vocal.client.impl.RequestReceivedHolder;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;

public interface IRequestManager {

	/**
	 * @return The version of the communication protocol associated to this requests manager.
	 */
	float getVersion();

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
	 * @param version The version of the communication protocol to use.
	 * 
	 * @return The message to send to the server in order to specify the supported versions.
	 */
	IVocalMessage onSetCommunicationProtocolVersion(IVocalMessage request, float version);

	/**
	 * Creates a message in order to join a vocal server.
	 * 
	 * @param name     The player's name.
	 * @param isMute   The player's mute status.
	 * @param isDeafen The player's deafen status.
	 * 
	 * @return The message to send to the remote in order to join a vocal server.
	 */
	IVocalMessage onServerJoin(String name, boolean isMute, boolean isDeafen);

	/**
	 * Creates a message in order to retrieve the server configuration.
	 * 
	 * @return The message to send to the remote in order to get the server configuration.
	 */
	IVocalMessage getServerConfiguration();

	/**
	 * Update the configuration of the server associated to this manager.
	 * 
	 * @param request The request that contains the server configuration.
	 */
	void onGetServerConfiguration(IVocalMessage request);

	/**
	 * Creates a message in order to leave a vocal server.
	 * 
	 * @return The message to send to the remote in order to leave a vocal server.
	 */
	IVocalMessage onServerLeave();
}
