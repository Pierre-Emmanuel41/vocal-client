package fr.pederobien.vocal.client.impl.request;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;

import fr.pederobien.vocal.client.impl.RequestReceivedHolder;
import fr.pederobien.vocal.client.interfaces.IRequestManager;
import fr.pederobien.vocal.client.interfaces.IServerRequestManager;
import fr.pederobien.vocal.client.interfaces.IVocalMainPlayer;
import fr.pederobien.vocal.client.interfaces.IVocalPlayer;
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
		register(new RequestManagerV10(server));
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

	@Override
	public IVocalMessage onServerJoin(float version, String name, boolean isMute, boolean isDeafen) {
		return findManagerAndReturn(version, manager -> manager.onServerJoin(name, isMute, isDeafen));
	}

	@Override
	public IVocalMessage getServerConfiguration(float version) {
		return findManagerAndReturn(version, manager -> manager.getServerConfiguration());
	}

	@Override
	public void onGetServerConfiguration(IVocalMessage request) {
		findManagerAndAccept(request.getHeader().getVersion(), manager -> manager.onGetServerConfiguration(request));
	}

	@Override
	public IVocalMessage onServerLeave(float version) {
		return findManagerAndReturn(version, manager -> manager.onServerLeave());
	}

	@Override
	public IVocalMessage onPlayerNameChange(float version, IVocalMainPlayer player, String newName) {
		return findManagerAndReturn(version, manager -> manager.onPlayerNameChange(player, newName));
	}

	@Override
	public IVocalMessage onPlayerMuteChange(float version, IVocalPlayer player, boolean newMute) {
		return findManagerAndReturn(version, manager -> manager.onPlayerMuteChange(player, newMute));
	}

	@Override
	public IVocalMessage onPlayerMuteByChange(float version, IVocalPlayer target, IVocalPlayer source, boolean newMute) {
		return findManagerAndReturn(version, manager -> manager.onPlayerMuteByChange(target, source, newMute));
	}

	@Override
	public IVocalMessage onPlayerDeafenChange(float version, IVocalPlayer player, boolean newDeafen) {
		return findManagerAndReturn(version, manager -> manager.onPlayerDeafenChange(player, newDeafen));
	}

	@Override
	public IVocalMessage onPlayerSpeak(float version, IVocalPlayer player, byte[] data) {
		return findManagerAndReturn(version, manager -> manager.onPlayerSpeak(player, data));
	}

	/**
	 * Register the given request manager in this global request manager.
	 * 
	 * @param manager The manager to request.
	 */
	protected void register(IRequestManager manager) {
		managers.put(manager.getVersion(), manager);
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

	/**
	 * Apply the function of the manager associated to the given version if registered.
	 * 
	 * @param version  The version of the manager.
	 * @param function The function to apply.
	 */
	protected void findManagerAndAccept(float version, Consumer<IRequestManager> consumer) {
		IRequestManager manager = managers.get(version);
		if (manager == null)
			return;

		consumer.accept(manager);
	}
}
