package fr.pederobien.vocal.client.impl.request;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Function;

import fr.pederobien.vocal.client.impl.RequestReceivedHolder;
import fr.pederobien.vocal.client.interfaces.IRequestManager;
import fr.pederobien.vocal.client.interfaces.IServerRequestManager;
import fr.pederobien.vocal.client.interfaces.IVocalServer;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;

public class ServerRequestManager implements IServerRequestManager {
	private NavigableMap<Float, IRequestManager> managers;

	/**
	 * Creates a request management in order to modify the given server and answer to remote requests.
	 * 
	 * @param server The server to update.
	 */
	public ServerRequestManager(IVocalServer server) {
		managers = new TreeMap<Float, IRequestManager>();
		// register(new RequestManagerV10(server));
	}

	@Override
	public float getVersion() {
		return managers.lastKey();
	}

	@Override
	public List<Float> getVersions() {
		return new ArrayList<Float>(managers.keySet());
	}

	@Override
	public void apply(RequestReceivedHolder holder) {
		IRequestManager manager = managers.get(holder.getRequest().getHeader().getVersion());

		if (manager == null)
			return;

		manager.apply(holder);
	}

	@Override
	public IVocalMessage onGetCommunicationProtocolVersions(IVocalMessage request, List<Float> versions) {
		return findManagerAndReturn(1.0f, manager -> manager.onGetCommunicationProtocolVersions(request, versions));
	}

	@Override
	public IVocalMessage onSetCommunicationProtocolVersion(IVocalMessage request, float version) {
		return findManagerAndReturn(1.0f, manager -> manager.onSetCommunicationProtocolVersion(request, version));
	}

	/**
	 * Apply the function of the manager associated to the given version if registered.
	 * 
	 * @param version  The version of the manager.
	 * @param function The function to apply.
	 * 
	 * @return The created message.
	 */
	protected IVocalMessage findManagerAndReturn(float version, Function<IRequestManager, IVocalMessage> function) {
		IRequestManager manager = managers.get(version);
		if (manager == null)
			return null;

		return function.apply(manager);
	}
}
