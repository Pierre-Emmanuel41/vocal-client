package fr.pederobien.vocal.client.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.vocal.client.event.VocalPlayerNameChangePostEvent;
import fr.pederobien.vocal.client.event.VocalServerListPlayerAddPostEvent;
import fr.pederobien.vocal.client.event.VocalServerListPlayerRemovePostEvent;
import fr.pederobien.vocal.client.exceptions.PlayerAlreadyRegisteredException;
import fr.pederobien.vocal.client.interfaces.IVocalPlayer;
import fr.pederobien.vocal.client.interfaces.IVocalServer;
import fr.pederobien.vocal.client.interfaces.IVocalServerPlayerList;

public class VocalServerPlayerList implements IVocalServerPlayerList, IEventListener {
	private IVocalServer server;
	private Map<String, IVocalPlayer> players;
	private Lock lock;

	public VocalServerPlayerList(IVocalServer server) {
		this.server = server;

		players = new LinkedHashMap<String, IVocalPlayer>();
		lock = new ReentrantLock(true);

		EventManager.registerListener(this);
	}

	@Override
	public Iterator<IVocalPlayer> iterator() {
		return toList().iterator();
	}

	@Override
	public IVocalServer getServer() {
		return server;
	}

	@Override
	public String getName() {
		return server.getName();
	}

	@Override
	public Optional<IVocalPlayer> get(String name) {
		return Optional.ofNullable(players.get(name));
	}

	@Override
	public Stream<IVocalPlayer> stream() {
		return toList().stream();
	}

	@Override
	public List<IVocalPlayer> toList() {
		return new ArrayList<IVocalPlayer>(players.values());
	}

	/**
	 * Appends the given player to this list. For internal use only.
	 * 
	 * @param player The player to add.
	 */
	public void add(IVocalPlayer player) {
		lock.lock();
		try {
			Optional<IVocalPlayer> optPlayer = get(player.getName());
			if (optPlayer.isPresent())
				throw new PlayerAlreadyRegisteredException(this, optPlayer.get());

			players.put(player.getName(), player);
			EventManager.callEvent(new VocalServerListPlayerAddPostEvent(this, player));
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Removes the player associated to the given name from this list. For internal use only.
	 * 
	 * @param name The name of the player to remove.
	 */
	public void remove(String name) {
		lock.lock();
		try {
			IVocalPlayer player = players.remove(name);
			if (player != null)
				EventManager.callEvent(new VocalServerListPlayerRemovePostEvent(this, player));
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Removes all registered players.
	 */
	public void clear() {
		Set<String> names = new HashSet<String>(players.keySet());
		for (String name : names)
			remove(name);
	}

	@EventHandler
	private void onPlayerNameChange(VocalPlayerNameChangePostEvent event) {
		Optional<IVocalPlayer> optOldPlayer = get(event.getOldName());
		if (!optOldPlayer.isPresent())
			return;

		Optional<IVocalPlayer> optNewPlayer = get(event.getPlayer().getName());
		if (optNewPlayer.isPresent())
			throw new PlayerAlreadyRegisteredException(server.getPlayers(), optNewPlayer.get());

		lock.lock();
		try {
			players.remove(event.getOldName());
			players.put(event.getPlayer().getName(), event.getPlayer());
		} finally {
			lock.unlock();
		}
	}
}
