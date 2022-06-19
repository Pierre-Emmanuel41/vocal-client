package fr.pederobien.vocal.client.interfaces;

import fr.pederobien.vocal.common.impl.VocalErrorCode;

public interface IResponse {

	/**
	 * @return If an exception or an error occurs.
	 */
	boolean hasFailed();

	/**
	 * @return The error code returned by the server if an error occurs when sending data to the remote or receiving data from the
	 *         remote.
	 */
	VocalErrorCode getErrorCode();
}
