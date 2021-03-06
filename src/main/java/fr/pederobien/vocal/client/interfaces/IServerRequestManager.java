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

	/**
	 * Creates a message in order to join a vocal server.
	 * 
	 * @param version  The protocol version to use to create a vocal message.
	 * @param name     The player's name.
	 * @param isMute   The player's mute status.
	 * @param isDeafen The player's deafen status.
	 * 
	 * @return The message to send to the remote in order to join a vocal server.
	 */
	IVocalMessage onServerJoin(float version, String name, boolean isMute, boolean isDeafen);

	/**
	 * Creates a message in order to retrieve the server configuration.
	 * 
	 * @param version The protocol version to use to create a vocal message.
	 * 
	 * @return The message to send to the remote in order to get the server configuration.
	 */
	IVocalMessage getServerConfiguration(float version);

	/**
	 * Update the configuration of the server associated to this request manager.
	 * 
	 * @param request The request that contains the server configuration.
	 */
	void onGetServerConfiguration(IVocalMessage request);

	/**
	 * Creates a message in order to leave a vocal server.
	 * 
	 * @param version The protocol version to use to create a vocal message.
	 * 
	 * @return The message to send to the remote in order to leave a vocal server.
	 */
	IVocalMessage onServerLeave(float version);

	/**
	 * Creates a message in order to change the name of a player.
	 * 
	 * @param version The protocol version to use to create a vocal message.
	 * @param player  The player whose the name should be updated.
	 * @param newName The new player name.
	 * 
	 * @return The message to send to the remote in order to update the name of a player.
	 */
	IVocalMessage onPlayerNameChange(float version, IVocalMainPlayer player, String newName);

	/**
	 * Creates a message in order to update the player mute status.
	 * 
	 * @param version The protocol version to use to create a vocal message.
	 * @param player  The player whose the mute status has changed.
	 * @param newMute The new player's mute status.
	 * 
	 * @return The message to send to the remote in order to update the mute status of a player.
	 */
	IVocalMessage onPlayerMuteChange(float version, IVocalPlayer player, boolean newMute);

	/**
	 * Creates a message in order to mute or unmute a player for another player.
	 * 
	 * @param version The protocol version to use to create a vocal message.
	 * @param target  The player to mute or unmute for another player.
	 * @param source  The player for which a player is mute or unmute.
	 * @param newMute The mute status of the player.
	 * 
	 * @return The message to send to the remote in order to update the muteby status of a player.
	 */
	IVocalMessage onPlayerMuteByChange(float version, IVocalPlayer target, IVocalPlayer source, boolean newMute);

	/**
	 * Creates a message in order to update the player deafen status.
	 * 
	 * @param version   The protocol version to use to create a vocal message.
	 * @param player    The player whose the deafen status has changed.
	 * @param newDeafen The new player's deafen status.
	 * 
	 * @return The message to send to the remote in order to update the deafen status of a player.
	 */
	IVocalMessage onPlayerDeafenChange(float version, IVocalPlayer player, boolean newDeafen);

	/**
	 * Creates a message in order to send an audio sample to the vocal server.
	 * 
	 * @param version   The protocol version to use to create a vocal message.
	 * @param player    The speaking player.
	 * @param data      The bytes array that represents an audio sample to send.
	 * @param isMono    True if the audio signal is a mono signal, false otherwise.
	 * @param isEncoded True if the audio sample has been encoded, false otherwise.
	 * 
	 * @return The message to send to send an audio sample to the vocal server.
	 */
	IVocalMessage onPlayerSpeak(float version, IVocalPlayer player, byte[] data, boolean isMono, boolean isEncoded);
}
