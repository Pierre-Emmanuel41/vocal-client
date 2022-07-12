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

	/**
	 * Creates a message in order to change the name of a player.
	 * 
	 * @param player  The player whose the name should be updated.
	 * @param newName The new player name.
	 * 
	 * @return The message to send to the remote in order to update the name of a player.
	 */
	IVocalMessage onPlayerNameChange(IVocalMainPlayer player, String newName);

	/**
	 * Creates a message in order to update the player mute status.
	 * 
	 * @param player  The player whose the mute status has changed.
	 * @param newMute The new player's mute status.
	 * 
	 * @return The message to send to the remote in order to update the mute status of a player.
	 */
	IVocalMessage onPlayerMuteChange(IVocalPlayer player, boolean newMute);

	/**
	 * Creates a message in order to mute or unmute a player for another player.
	 * 
	 * @param target  The player to mute or unmute for another player.
	 * @param source  The player for which a player is mute or unmute.
	 * @param newMute The mute status of the player.
	 * 
	 * @return The message to send to the remote in order to update the muteby status of a player.
	 */
	IVocalMessage onPlayerMuteByChange(IVocalPlayer target, IVocalPlayer source, boolean newMute);
}
