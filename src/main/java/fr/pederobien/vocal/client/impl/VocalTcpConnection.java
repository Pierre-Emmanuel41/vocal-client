package fr.pederobien.vocal.client.impl;

import java.util.function.Consumer;

import fr.pederobien.communication.ResponseCallbackArgs;
import fr.pederobien.communication.event.ConnectionDisposedEvent;
import fr.pederobien.communication.event.ConnectionLostEvent;
import fr.pederobien.communication.event.UnexpectedDataReceivedEvent;
import fr.pederobien.communication.impl.TcpClientImpl;
import fr.pederobien.communication.interfaces.ITcpConnection;
import fr.pederobien.messenger.impl.Response;
import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.utils.event.LogEvent;
import fr.pederobien.vocal.client.event.VocalCommunicationProtocolVersionGetPostEvent;
import fr.pederobien.vocal.client.event.VocalCommunicationProtocolVersionSetPostEvent;
import fr.pederobien.vocal.client.event.VocalMainPlayerDeafenStatusChangePreEvent;
import fr.pederobien.vocal.client.event.VocalMainPlayerNameChangePreEvent;
import fr.pederobien.vocal.client.event.VocalPlayerMuteStatusChangePreEvent;
import fr.pederobien.vocal.client.event.VocalServerJoinPreEvent;
import fr.pederobien.vocal.client.event.VocalServerLeavePreEvent;
import fr.pederobien.vocal.client.interfaces.IVocalServer;
import fr.pederobien.vocal.common.impl.VocalCallbackMessage;
import fr.pederobien.vocal.common.impl.VocalErrorCode;
import fr.pederobien.vocal.common.impl.VocalMessageExtractor;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;

public class VocalTcpConnection extends VocalConnection implements IEventListener {
	private ITcpConnection connection;

	/**
	 * Creates a TCP connection associated to the given server.
	 * 
	 * @param server The server that contains the IP address and the TCP port number.
	 */
	public VocalTcpConnection(IVocalServer server) {
		super(server);
		connection = new TcpClientImpl(server.getAddress().getAddress().getHostAddress(), server.getAddress().getPort(), new VocalMessageExtractor(), true);
		EventManager.registerListener(this);
	}

	/**
	 * @return The connection with the remote.
	 */
	public ITcpConnection getTcpConnection() {
		return connection;
	}

	/**
	 * Send a message to the remote in order to retrieve the server configuration.
	 * 
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public void getServerConfiguration(Consumer<IResponse> callback) {
		IVocalMessage request = getRequestManager().getServerConfiguration(getVersion());
		send(request, args -> parse(args, callback, message -> getRequestManager().onGetServerConfiguration(message)));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onCommunicationProtocolVersionGet(VocalCommunicationProtocolVersionGetPostEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		send(getRequestManager().onGetCommunicationProtocolVersions(event.getRequest(), event.getVersions()), null);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onCommunicationProtocolVersionSet(VocalCommunicationProtocolVersionSetPostEvent event) {
		if (!event.getConnection().equals(this) || getVersion() != -1)
			return;

		setVersion(getServer().getRequestManager().getVersions().contains(event.getVersion()) ? event.getVersion() : 1.0f);
		send(getRequestManager().onSetCommunicationProtocolVersion(event.getRequest(), event.getVersion()), null);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onServerJoin(VocalServerJoinPreEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		boolean isMute = getServer().getMainPlayer() == null ? false : getServer().getMainPlayer().isMute();
		boolean isDeafen = getServer().getMainPlayer() == null ? false : getServer().getMainPlayer().isDeafen();
		send(getRequestManager().onServerJoin(getVersion(), event.getPlayerName(), isMute, isDeafen), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onServerLeave(VocalServerLeavePreEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		if (!getServer().isReachable())
			event.getCallback().accept(new Response(VocalErrorCode.NONE));
		else
			send(getRequestManager().onServerLeave(getVersion()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerNameChange(VocalMainPlayerNameChangePreEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		send(getRequestManager().onPlayerNameChange(getVersion(), event.getPlayer(), event.getNewName()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerMuteStatusChange(VocalPlayerMuteStatusChangePreEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		if (event.getPlayer().equals(getServer().getMainPlayer()))
			send(getRequestManager().onPlayerMuteChange(getVersion(), event.getPlayer(), event.getNewMute()), args -> parse(args, event.getCallback(), null));
		else
			send(getRequestManager().onPlayerMuteByChange(getVersion(), event.getPlayer(), getServer().getMainPlayer(), event.getNewMute()),
					args -> parse(args, event.getCallback(), null));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerDeafenStatusChange(VocalMainPlayerDeafenStatusChangePreEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		send(getRequestManager().onPlayerDeafenChange(getVersion(), event.getPlayer(), event.getNewDeafen()), args -> parse(args, event.getCallback(), null));
	}

	@EventHandler
	private void onUnexpectedDataReceive(UnexpectedDataReceivedEvent event) {
		if (!event.getConnection().equals(getTcpConnection()))
			return;

		IVocalMessage request = VocalClientMessageFactory.parse(event.getBuffer());
		if (getVersion() != -1 && getVersion() != request.getHeader().getVersion()) {
			String format = "Receiving message with unexpected getVersion() of the communication protocol, expected=v%s, actual=v%s";
			EventManager.callEvent(new LogEvent(format, getVersion(), request.getHeader().getVersion()));
		} else
			getServer().getRequestManager().apply(new RequestReceivedHolder(VocalClientMessageFactory.parse(event.getBuffer()), this));
	}

	@EventHandler
	private void onConnectionDispose(ConnectionDisposedEvent event) {
		if (!event.getConnection().equals(getTcpConnection()))
			return;

		EventManager.unregisterListener(this);
	}

	@EventHandler
	private void onConnectionLost(ConnectionLostEvent event) {
		if (!event.getConnection().equals(getTcpConnection()))
			return;

		setVersion(-1);
	}

	/**
	 * Send the given message to the remote.
	 * 
	 * @param message  The message to send to the remote.
	 * @param callback The callback to run when a response has been received before the timeout.
	 */
	private void send(IVocalMessage message, Consumer<ResponseCallbackArgs> callback) {
		if (getTcpConnection() == null || getTcpConnection().isDisposed())
			return;

		getTcpConnection().send(new VocalCallbackMessage(message, callback));
	}

	/**
	 * First check if there is a timeout for the answer, then parse the bytes array associated to the response.
	 * 
	 * @param args     The argument for the callback that contains the response and an indication if there is a timeout.
	 * @param callback The callback to run when a response has been received.
	 * @param consumer The consumer to run in order to update the getServer().
	 */
	private void parse(ResponseCallbackArgs args, Consumer<IResponse> callback, Consumer<IVocalMessage> consumer) {
		if (args.isTimeout())
			callback.accept(new Response(VocalErrorCode.TIMEOUT));
		else {
			IVocalMessage response = VocalClientMessageFactory.parse(args.getResponse().getBytes());
			if (response.getHeader().isError() || consumer == null)
				callback.accept(new Response(response.getHeader().getErrorCode()));
			else {
				consumer.accept(response);
				callback.accept(new Response(VocalErrorCode.NONE));
			}
		}
	}
}
