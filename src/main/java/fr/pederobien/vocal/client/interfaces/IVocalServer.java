package fr.pederobien.vocal.client.interfaces;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

public interface IVocalServer {

	/**
	 * @return The name of this server.
	 */
	String getName();

	/**
	 * Set the name of this server.
	 * 
	 * @param name The new server name.
	 */
	void setName(String name);

	/**
	 * @return The server address.
	 */
	InetSocketAddress getAddress();

	/**
	 * Set the address of this server.
	 * 
	 * @param address The new server address.
	 */
	void setAddress(InetSocketAddress address);

	/**
	 * @return True if the server is reachable and requests can be sent to the remote, false otherwise.
	 */
	boolean isReachable();

	/**
	 * Attempt a connection to the remote.
	 */
	void open();

	/**
	 * Join the server associated to this client. Once this client has successfully joined the remote, then it can send requests to
	 * the remote.
	 * 
	 * @param name     The name of the main player for this server.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void join(String name, Consumer<IResponse> callback);

	/**
	 * @return True if the server has been joined by the player.
	 */
	boolean isJoined();

	/**
	 * Leave the server associated to this client. Once this client has successfully leaved the remote, it cannot performs requests on
	 * the server until the method call is called.
	 * 
	 * @param callback The callback to run when an answer is received from the server.
	 */
	void leave(Consumer<IResponse> callback);

	/**
	 * Abort the connection to the remote.
	 */
	void close();

	/**
	 * @return The manager responsible to create messages to send to the remote.
	 */
	IServerRequestManager getRequestManager();

	/**
	 * @return The player associated to this vocal server.
	 */
	IVocalMainPlayer getMainPlayer();
}
