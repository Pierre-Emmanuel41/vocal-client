package fr.pederobien.vocal.client.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface IVocalServerPlayerList {

	/**
	 * @return The server to which this list is attached.
	 */
	IVocalServer getServer();

	/**
	 * @return The name of this player list.
	 */
	String getName();

	/**
	 * Get the player associated to the given name.
	 * 
	 * @param name The player name.
	 * 
	 * @return An optional that contains the player if registered, an empty optional otherwise.
	 */
	Optional<IVocalPlayer> get(String name);

	/**
	 * @return a sequential {@code Stream} over the elements in this collection.
	 */
	Stream<IVocalPlayer> stream();

	/**
	 * @return A copy of the underlying list.
	 */
	List<IVocalPlayer> toList();
}
