package fr.pederobien.vocal.client.impl;

import fr.pederobien.communication.event.DataReceivedEvent;
import fr.pederobien.communication.impl.UdpClientImpl;
import fr.pederobien.communication.interfaces.IUdpConnection;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.utils.event.LogEvent;
import fr.pederobien.vocal.client.event.VocalPlayerSpeakPreEvent;
import fr.pederobien.vocal.client.interfaces.IVocalServer;
import fr.pederobien.vocal.common.impl.VocalAddressMessage;
import fr.pederobien.vocal.common.impl.VocalMessageExtractor;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;

public class VocalUdpConnection extends VocalConnection implements IEventListener {
	private IUdpConnection connection;

	/**
	 * Creates a UDP connection associated to the given server.
	 * 
	 * @param server The server that contains the IP address and the UDP port number.
	 */
	public VocalUdpConnection(IVocalServer server) {
		super(server);
		connection = new UdpClientImpl(server.getAddress().getAddress().getHostAddress(), server.getAddress().getPort(), new VocalMessageExtractor());
		EventManager.registerListener(this);
	}

	/**
	 * @return The connection with the remote.
	 */
	public IUdpConnection getUdpConnection() {
		return connection;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerSpeak(VocalPlayerSpeakPreEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		send(getRequestManager().onPlayerSpeak(getVersion(), event.getPlayer(), event.getData()));
	}

	@EventHandler
	private void onDataReceived(DataReceivedEvent event) {
		if (!event.getConnection().equals(getUdpConnection()))
			return;

		IVocalMessage request = VocalClientMessageFactory.parse(event.getBuffer());
		if (getVersion() != -1 && getVersion() != request.getHeader().getVersion()) {
			String format = "Receiving message with unexpected getVersion() of the communication protocol, expected=v%s, actual=v%s";
			EventManager.callEvent(new LogEvent(format, getVersion(), request.getHeader().getVersion()));
		} else
			getServer().getRequestManager().apply(new RequestReceivedHolder(request, this));
	}

	/**
	 * Send the given message to the remote.
	 * 
	 * @param message  The message to send to the remote.
	 * @param callback The callback to run when a response has been received before the timeout.
	 */
	private void send(IVocalMessage message) {
		if (getUdpConnection() == null || getUdpConnection().isDisposed())
			return;

		getUdpConnection().send(new VocalAddressMessage(message));
	}
}
