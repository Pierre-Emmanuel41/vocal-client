package fr.pederobien.vocal.client.impl.request;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import fr.pederobien.utils.event.EventManager;
import fr.pederobien.vocal.client.event.VocalCommunicationProtocolVersionGetPostEvent;
import fr.pederobien.vocal.client.event.VocalCommunicationProtocolVersionSetPostEvent;
import fr.pederobien.vocal.client.event.VocalPlayerSpeakPostEvent;
import fr.pederobien.vocal.client.exceptions.PlayerAlreadyRegisteredException;
import fr.pederobien.vocal.client.impl.AbstractPlayer;
import fr.pederobien.vocal.client.impl.RequestReceivedHolder;
import fr.pederobien.vocal.client.impl.VocalSecondaryPlayer;
import fr.pederobien.vocal.client.impl.VocalServerPlayerList;
import fr.pederobien.vocal.client.impl.VocalTcpConnection;
import fr.pederobien.vocal.client.interfaces.IVocalMainPlayer;
import fr.pederobien.vocal.client.interfaces.IVocalPlayer;
import fr.pederobien.vocal.client.interfaces.IVocalServer;
import fr.pederobien.vocal.common.impl.VocalIdentifier;
import fr.pederobien.vocal.common.impl.messages.v10.GetCommunicationProtocolVersionsV10;
import fr.pederobien.vocal.common.impl.messages.v10.GetServerConfigurationV10;
import fr.pederobien.vocal.common.impl.messages.v10.PlayerSpeakSetMessageV10;
import fr.pederobien.vocal.common.impl.messages.v10.RegisterPlayerOnServerV10;
import fr.pederobien.vocal.common.impl.messages.v10.SetCommunicationProtocolVersionV10;
import fr.pederobien.vocal.common.impl.messages.v10.SetPlayerDeafenStatusV10;
import fr.pederobien.vocal.common.impl.messages.v10.SetPlayerMuteByStatusV10;
import fr.pederobien.vocal.common.impl.messages.v10.SetPlayerMuteStatusV10;
import fr.pederobien.vocal.common.impl.messages.v10.SetPlayerNameV10;
import fr.pederobien.vocal.common.impl.messages.v10.UnregisterPlayerFromServerV10;
import fr.pederobien.vocal.common.impl.messages.v10.model.PlayerInfo;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;

public class RequestManagerV10 extends RequestManager {

	/**
	 * Creates a request manager associated to version 1.0 in order to modify the given server and answer to remote requests.
	 * 
	 * @param server The server to update.
	 */
	public RequestManagerV10(IVocalServer server) {
		super(server, 1.0f);

		// Server messages
		getRequests().put(VocalIdentifier.GET_CP_VERSIONS, holder -> onGetCommunicationProtocolVersions((GetCommunicationProtocolVersionsV10) holder.getRequest()));
		getRequests().put(VocalIdentifier.SET_CP_VERSION, holder -> onSetCommunicationProtocolVersion(holder));

		// Player messages
		getRequests().put(VocalIdentifier.REGISTER_PLAYER_ON_SERVER, holder -> registerPlayerOnServer((RegisterPlayerOnServerV10) holder.getRequest()));
		getRequests().put(VocalIdentifier.UNREGISTER_PLAYER_FROM_SERVER, holder -> unregisterPlayerFromServer((UnregisterPlayerFromServerV10) holder.getRequest()));
		getRequests().put(VocalIdentifier.SET_PLAYER_NAME, holder -> setPlayerName((SetPlayerNameV10) holder.getRequest()));
		getRequests().put(VocalIdentifier.SET_PLAYER_MUTE, holder -> setPlayerMute((SetPlayerMuteStatusV10) holder.getRequest()));
		getRequests().put(VocalIdentifier.SET_PLAYER_MUTE_BY, holder -> setPlayerMuteBy((SetPlayerMuteByStatusV10) holder.getRequest()));
		getRequests().put(VocalIdentifier.SET_PLAYER_DEAFEN, holder -> setPlayerDeafen((SetPlayerDeafenStatusV10) holder.getRequest()));

		// Audio messages
		getRequests().put(VocalIdentifier.PLAYER_SPEAK_SET, holder -> setPlayerSpeak((PlayerSpeakSetMessageV10) holder.getRequest()));
	}

	@Override
	public IVocalMessage onGetCommunicationProtocolVersions(IVocalMessage request, List<Float> versions) {
		return answer(getVersion(), request, VocalIdentifier.GET_CP_VERSIONS, versions.toArray());
	}

	@Override
	public IVocalMessage onSetCommunicationProtocolVersion(IVocalMessage request, float version) {
		return answer(getVersion(), request, VocalIdentifier.SET_CP_VERSION, version);
	}

	@Override
	public IVocalMessage onServerJoin(String name, boolean isMute, boolean isDeafen) {
		return create(getVersion(), VocalIdentifier.SET_SERVER_JOIN, name, isMute, isDeafen);
	}

	@Override
	public IVocalMessage getServerConfiguration() {
		return create(getVersion(), VocalIdentifier.GET_SERVER_CONFIGURATION);
	}

	@Override
	public void onGetServerConfiguration(IVocalMessage request) {
		GetServerConfigurationV10 serverInfoMessage = (GetServerConfigurationV10) request;

		for (PlayerInfo playerInfo : serverInfoMessage.getServerInfo().values()) {
			try {
				((VocalServerPlayerList) getServer().getPlayers()).add(createPlayer(playerInfo));
			} catch (PlayerAlreadyRegisteredException e) {
				// Do nothing
			}
		}
	}

	@Override
	public IVocalMessage onServerLeave() {
		return create(getVersion(), VocalIdentifier.SET_SERVER_LEAVE);
	}

	@Override
	public IVocalMessage onPlayerNameChange(IVocalMainPlayer player, String newName) {
		return create(getVersion(), VocalIdentifier.SET_PLAYER_NAME, player.getName(), newName);
	}

	@Override
	public IVocalMessage onPlayerMuteChange(IVocalPlayer player, boolean newMute) {
		return create(getVersion(), VocalIdentifier.SET_PLAYER_MUTE, player.getName(), newMute);
	}

	@Override
	public IVocalMessage onPlayerMuteByChange(IVocalPlayer target, IVocalPlayer source, boolean newMute) {
		return create(getVersion(), VocalIdentifier.SET_PLAYER_MUTE_BY, target.getName(), source.getName(), newMute);
	}

	@Override
	public IVocalMessage onPlayerDeafenChange(IVocalPlayer player, boolean newDeafen) {
		return create(getVersion(), VocalIdentifier.SET_PLAYER_DEAFEN, player.getName(), newDeafen);
	}

	@Override
	public IVocalMessage onPlayerSpeak(IVocalPlayer player, byte[] data, boolean isMono, boolean isEncoded) {
		return create(getVersion(), VocalIdentifier.PLAYER_SPEAK_INFO, player.getName(), data, isMono, isEncoded);
	}

	/**
	 * Throw a {@link CommunicationProtocolVersionGetEvent} in order to fill the supported versions of the communication protocol.
	 * 
	 * @param request The request sent by the remote in order to get the supported versions.
	 */
	private void onGetCommunicationProtocolVersions(GetCommunicationProtocolVersionsV10 request) {
		EventManager.callEvent(new VocalCommunicationProtocolVersionGetPostEvent(getServer(), request));
	}

	/**
	 * Throw a {@link VocalCommunicationProtocolVersionSetPostEvent} in order to set the version of the communication protocol to use
	 * between the client and the server.
	 * 
	 * @param holder The holder that gather the request received by the remote and the connection that has received the request.
	 */
	private void onSetCommunicationProtocolVersion(RequestReceivedHolder holder) {
		if (!(holder.getConnection() instanceof VocalTcpConnection))
			return;

		VocalTcpConnection connection = (VocalTcpConnection) holder.getConnection();
		SetCommunicationProtocolVersionV10 request = (SetCommunicationProtocolVersionV10) holder.getRequest();
		EventManager.callEvent(new VocalCommunicationProtocolVersionSetPostEvent(getServer(), request, request.getVersion(), connection));
	}

	/**
	 * Adds a player on the server.
	 * 
	 * @param request The request sent by the remote in order to add a player.
	 */
	private void registerPlayerOnServer(RegisterPlayerOnServerV10 request) {
		try {
			((VocalServerPlayerList) getServer().getPlayers()).add(createPlayer(request.getPlayerInfo()));
		} catch (PlayerAlreadyRegisteredException e) {
			// Do nothing
		}
	}

	/**
	 * Removes a player from the server.
	 * 
	 * @param request The request sent by the remote in order to remove a player.
	 */
	private void unregisterPlayerFromServer(UnregisterPlayerFromServerV10 request) {
		((VocalServerPlayerList) getServer().getPlayers()).remove(request.getPlayerName());
	}

	/**
	 * Update the name of a player.
	 * 
	 * @param request The request sent by the remote in order to rename a player.
	 */
	private void setPlayerName(SetPlayerNameV10 request) {
		findPlayerAndUpdate(request.getOldName(), AbstractPlayer.class, player -> player.setName(request.getNewName()));
	}

	/**
	 * Update the mute status of a player.
	 * 
	 * @param request The request sent by the remote in order to set the mute status of a player.
	 */
	private void setPlayerMute(SetPlayerMuteStatusV10 request) {
		findPlayerAndUpdate(request.getPlayerName(), AbstractPlayer.class, player -> player.setMute(request.isMute()));
	}

	/**
	 * Update the mute status of a target player for a source player.
	 * 
	 * @param request The request sent by the remote in order to mute or unmute a target player for a source player.
	 */
	private void setPlayerMuteBy(SetPlayerMuteByStatusV10 request) {
		findPlayerAndUpdate(request.getTarget(), VocalSecondaryPlayer.class, player -> player.setMuteByMainPlayer(request.isMute()));
	}

	/**
	 * Update the deafen status of a player.
	 * 
	 * @param request The request sent by the remote in order to set the deafen status of a player.
	 */
	private void setPlayerDeafen(SetPlayerDeafenStatusV10 request) {
		findPlayerAndUpdate(request.getPlayerName(), AbstractPlayer.class, player -> player.setDeafen(request.isDeafen()));
	}

	/**
	 * Throw a VocalPlayerSpeakEvent.
	 * 
	 * @param request The request sent by the remote in order to player an audio sample.
	 */
	private void setPlayerSpeak(PlayerSpeakSetMessageV10 request) {
		IVocalPlayer transmitter = getPlayer(request.getPlayerName()).get();
		EventManager.callEvent(new VocalPlayerSpeakPostEvent(transmitter, request.getData(), request.isMono(), request.isEncoded(), request.getVolume()));
	}

	/**
	 * Try to find the player associated to the given name. First check if the name correspond to the main player of the server, then
	 * check if the name refers to a player that is registered in a channel.
	 * 
	 * @param name The name of the player to get.
	 * 
	 * @return An optional that contains a player, if registered, null otherwise.
	 */
	private Optional<IVocalPlayer> getPlayer(String name) {
		if (getServer().getMainPlayer() != null && getServer().getMainPlayer().getName().equals(name))
			return Optional.of(getServer().getMainPlayer());

		for (IVocalPlayer player : getServer().getPlayers())
			if (player.getName().equals(name))
				return Optional.of(player);

		return Optional.empty();
	}

	/**
	 * Apply the consumer if and only if the player is an instance of the given class.
	 * 
	 * @param player   The player to cast.
	 * @param clazz    The class used to cast the player.
	 * @param consumer The code to run.
	 */
	private <T extends IVocalPlayer> void updatePlayer(IVocalPlayer player, Class<T> clazz, Consumer<T> consumer) {
		try {
			consumer.accept(clazz.cast(player));
		} catch (Exception e) {
			// do nothing
		}
	}

	/**
	 * Apply the consumer if and only if there is a player associated to the given name and the player is an instance of the given
	 * class.
	 * 
	 * @param name     The name of the player to update.
	 * @param clazz    The class used to cast the player.
	 * @param consumer The code to run.
	 */
	private <T extends IVocalPlayer> void findPlayerAndUpdate(String name, Class<T> clazz, Consumer<T> consumer) {
		Optional<IVocalPlayer> optPlayer = getPlayer(name);
		if (!optPlayer.isPresent())
			return;

		updatePlayer(optPlayer.get(), clazz, consumer);
	}

	/**
	 * Creates a player.
	 * 
	 * @param info A description of the player to create.
	 * 
	 * @return The created player.
	 */
	private IVocalPlayer createPlayer(PlayerInfo info) {
		if (info.getName().equals(getServer().getMainPlayer().getName()))
			return getServer().getMainPlayer();

		// Player's name
		String name = info.getName();

		// Player's mute status
		boolean isMute = info.isMute();

		// Player's deafen status
		boolean isDeafen = info.isDeafen();

		// Player's muteBy status
		boolean isMuteByMainPlayer = info.isMuteByMainPlayer();

		return new VocalSecondaryPlayer(getServer(), name, isMute, isDeafen, isMuteByMainPlayer);
	}
}
