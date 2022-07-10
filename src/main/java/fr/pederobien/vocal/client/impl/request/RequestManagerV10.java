package fr.pederobien.vocal.client.impl.request;

import java.util.List;

import fr.pederobien.utils.event.EventManager;
import fr.pederobien.vocal.client.event.VocalCommunicationProtocolVersionGetPostEvent;
import fr.pederobien.vocal.client.event.VocalCommunicationProtocolVersionSetPostEvent;
import fr.pederobien.vocal.client.exceptions.PlayerAlreadyRegisteredException;
import fr.pederobien.vocal.client.impl.RequestReceivedHolder;
import fr.pederobien.vocal.client.impl.VocalSecondaryPlayer;
import fr.pederobien.vocal.client.impl.VocalServerPlayerList;
import fr.pederobien.vocal.client.interfaces.IVocalPlayer;
import fr.pederobien.vocal.client.interfaces.IVocalServer;
import fr.pederobien.vocal.common.impl.VocalIdentifier;
import fr.pederobien.vocal.common.impl.messages.v10.GetCommunicationProtocolVersionsV10;
import fr.pederobien.vocal.common.impl.messages.v10.GetServerConfigurationV10;
import fr.pederobien.vocal.common.impl.messages.v10.RegisterPlayerOnServerV10;
import fr.pederobien.vocal.common.impl.messages.v10.SetCommunicationProtocolVersionV10;
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
		SetCommunicationProtocolVersionV10 request = (SetCommunicationProtocolVersionV10) holder.getRequest();
		EventManager.callEvent(new VocalCommunicationProtocolVersionSetPostEvent(getServer(), request, request.getVersion(), holder.getConnection()));
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
	 * Creates a player.
	 * 
	 * @param info A description of the player to create.
	 * 
	 * @return The created player.
	 */
	private IVocalPlayer createPlayer(PlayerInfo info) {
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
