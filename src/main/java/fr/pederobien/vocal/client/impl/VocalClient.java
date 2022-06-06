package fr.pederobien.vocal.client.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import fr.pederobien.communication.event.ConnectionCompleteEvent;
import fr.pederobien.communication.event.DataReceivedEvent;
import fr.pederobien.communication.impl.UdpClientImpl;
import fr.pederobien.communication.interfaces.IUdpConnection;
import fr.pederobien.sound.event.MicrophoneDataEncodedEvent;
import fr.pederobien.sound.impl.AudioPacket;
import fr.pederobien.sound.impl.SoundResourcesProvider;
import fr.pederobien.sound.interfaces.ISoundResourcesProvider;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.vocal.client.interfaces.IVocalClient;
import fr.pederobien.vocal.common.impl.VocalAddressMessage;
import fr.pederobien.vocal.common.impl.VocalIdentifier;
import fr.pederobien.vocal.common.impl.VocalMessageExtractor;
import fr.pederobien.vocal.common.impl.VocalMessageFactory;
import fr.pederobien.vocal.common.impl.VolumeResult;
import fr.pederobien.vocal.common.impl.messages.v10.PlayerSpeakSetMessageV10;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;

public class VocalClient implements IVocalClient, IEventListener {
	private String name;
	private int port;
	private ISoundResourcesProvider soundProvider;
	private IUdpConnection udpClient;
	private VocalMessageFactory factory;
	private AtomicBoolean pauseMicrophone, pauseSpeakers, isDisposed;

	/**
	 * Creates a server for vocal communication between several players.
	 * 
	 * @param name    The server name.
	 * @param address The server address.
	 * @param port    The server port number for the UDP communication.
	 */
	public VocalClient(String name, String address, int port) {
		this.name = name;
		this.port = port;

		soundProvider = new SoundResourcesProvider();
		factory = VocalMessageFactory.getInstance(0);
		udpClient = new UdpClientImpl(address, port, new VocalMessageExtractor());

		pauseMicrophone = new AtomicBoolean(false);
		pauseSpeakers = new AtomicBoolean(false);
		isDisposed = new AtomicBoolean(false);

		EventManager.registerListener(this);
	}

	@Override
	public void connect() {
		udpClient.connect();
	}

	@Override
	public void disconnect() {
		soundProvider.getMicrophone().stop();
		soundProvider.getSpeakers().stop();
		udpClient.disconnect();
	}

	@Override
	public void dispose() {
		if (!isDisposed.compareAndSet(false, true))
			return;

		disconnect();
		udpClient.dispose();
	}

	@Override
	public boolean isDisposed() {
		return isDisposed.get();
	}

	@Override
	public void pause() {
		pauseMicrophone();
		pauseSpeakers();
	}

	@Override
	public void pauseMicrophone() {
		if (!pauseMicrophone.compareAndSet(false, true))
			return;

		soundProvider.getMicrophone().pause();
	}

	@Override
	public void pauseSpeakers() {
		if (!pauseSpeakers.compareAndSet(false, true))
			return;

		soundProvider.getSpeakers().pause();
	}

	@Override
	public void resume() {
		resumeMicrophone();
		resumeSpeakers();
	}

	@Override
	public void resumeMicrophone() {
		if (!pauseMicrophone.compareAndSet(true, false))
			return;

		soundProvider.getMicrophone().resume();
	}

	@Override
	public void resumeSpeakers() {
		if (!pauseSpeakers.compareAndSet(true, false))
			return;

		soundProvider.getSpeakers().resume();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public ISoundResourcesProvider getSoundResourceProvider() {
		return soundProvider;
	}

	@EventHandler
	private void onConnectionComplete(ConnectionCompleteEvent event) {
		if (!event.getConnection().equals(udpClient))
			return;

		if (!pauseMicrophone.get())
			soundProvider.getMicrophone().start();

		if (!pauseSpeakers.get())
			soundProvider.getSpeakers().start();
	}

	@EventHandler
	private void onMicroDataEncoded(MicrophoneDataEncodedEvent event) {
		if (udpClient.isDisposed() || !event.getMicrophone().equals(soundProvider.getMicrophone()))
			return;

		udpClient.send(new VocalAddressMessage(factory.create(VocalIdentifier.PLAYER_SPEAK_INFO, getName(), event.getEncoded())));
	}

	@EventHandler
	private void onDataReceived(DataReceivedEvent event) {
		if (!event.getConnection().equals(udpClient))
			return;

		IVocalMessage message = factory.parse(event.getBuffer());
		if (pauseSpeakers.get() || message.getHeader().getIdentifier() != VocalIdentifier.PLAYER_SPEAK_SET)
			return;

		PlayerSpeakSetMessageV10 playerSpeakMessage = (PlayerSpeakSetMessageV10) message;
		String playerName = playerSpeakMessage.getPlayerName();
		byte[] encodedData = playerSpeakMessage.getData();
		VolumeResult volume = playerSpeakMessage.getVolume();
		AudioPacket packet = new AudioPacket(playerName, encodedData, volume.getGlobal(), volume.getRight(), volume.getLeft(), true, true);
		soundProvider.getMixer().put(packet);
	}
}
