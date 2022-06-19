package fr.pederobien.vocal.client.impl.request;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import fr.pederobien.vocal.client.impl.RequestReceivedHolder;
import fr.pederobien.vocal.client.impl.VocalClientMessageFactory;
import fr.pederobien.vocal.client.interfaces.IRequestManager;
import fr.pederobien.vocal.client.interfaces.IVocalServer;
import fr.pederobien.vocal.common.impl.VocalIdentifier;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;

public abstract class RequestManager implements IRequestManager {
	private float version;
	private IVocalServer server;
	private Map<VocalIdentifier, Consumer<RequestReceivedHolder>> requests;

	/**
	 * Creates a request manager in order to modify the given server and answer to remote requests.
	 * 
	 * @param server  The server to update.
	 * @param version The version of the communication protocol associated to this requests manager.
	 */
	public RequestManager(IVocalServer server, float version) {
		this.server = server;
		this.version = version;
		requests = new HashMap<VocalIdentifier, Consumer<RequestReceivedHolder>>();
	}

	@Override
	public float getVersion() {
		return version;
	}

	@Override
	public void apply(RequestReceivedHolder holder) {
		Consumer<RequestReceivedHolder> answer = requests.get(holder.getRequest().getHeader().getIdentifier());

		if (answer == null)
			return;

		answer.accept(holder);
	}

	/**
	 * @return The map that stores requests.
	 */
	public Map<VocalIdentifier, Consumer<RequestReceivedHolder>> getRequests() {
		return requests;
	}

	/**
	 * @return The server to update.
	 */
	protected IVocalServer getServer() {
		return server;
	}

	/**
	 * Send a message based on the given parameter to the remote.
	 * 
	 * @param identifier The identifier of the request to create.
	 * @param properties The message properties.
	 */
	protected IVocalMessage create(float version, VocalIdentifier identifier, Object... properties) {
		return VocalClientMessageFactory.create(version, identifier, properties);
	}

	/**
	 * Send a message based on the given parameter to the remote.
	 * 
	 * @param version    The version of the communication protocol to use.
	 * @param request    The request received by the remote.
	 * @param identifier The identifier of the answer request.
	 * @param properties The message properties.
	 */
	protected IVocalMessage answer(float version, IVocalMessage request, VocalIdentifier identifier, Object... properties) {
		return VocalClientMessageFactory.answer(version, request, identifier, properties);
	}
}
