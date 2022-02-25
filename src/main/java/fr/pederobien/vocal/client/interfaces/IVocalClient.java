package fr.pederobien.vocal.client.interfaces;

import fr.pederobien.sound.interfaces.ISoundResourcesProvider;

public interface IVocalClient {

	/**
	 * Attempt to connect to the server.
	 */
	void connect();

	/**
	 * Abort the connection with the server.
	 */
	void disconnect();

	/**
	 * Dispose the connection with the server. After this, it is impossible to send data to the remote using this client.
	 */
	void dispose();

	/**
	 * Stops the soundProvider.getMicrophone() and the soundProvider.getSpeakers(). But does not disconnected the internal connection
	 * from the remote.
	 */
	public void pause();

	/**
	 * Pause the soundProvider.getMicrophone(), no data are sent to the remote.
	 */
	public void pauseMicrophone();

	/**
	 * Pause the soundProvider.getSpeakers(), data are received from the remote but no played by the soundProvider.getSpeakers().
	 */
	public void pauseSpeakers();

	/**
	 * Resumes the soundProvider.getMicrophone() and the soundProvider.getSpeakers() in order to send again data to the remote and
	 * receive data from the remote.
	 */
	public void resume();

	/**
	 * Resume the soundProvider.getMicrophone(), data are sent to the remote.
	 */
	public void resumeMicrophone();

	/**
	 * Resume the soundProvider.getSpeakers(), data are received from the remote and played by the soundProvider.getSpeakers().
	 */
	public void resumeSpeakers();

	/**
	 * @return The name of this client.
	 */
	String getName();

	/**
	 * @return The port number for the UDP communication.
	 */
	int getPort();

	/**
	 * @return The provider to get access to the client microphone , and speakers.
	 */
	ISoundResourcesProvider getSoundResourceProvider();
}
