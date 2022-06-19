package fr.pederobien.vocal.client.impl;

import fr.pederobien.vocal.common.impl.VocalErrorCode;
import fr.pederobien.vocal.common.impl.VocalIdentifier;
import fr.pederobien.vocal.common.impl.VocalMessageFactory;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;

public class VocalClientMessageFactory {
	private static final VocalMessageFactory FACTORY;

	static {
		FACTORY = VocalMessageFactory.getInstance(0);
	}

	/**
	 * Creates a message based on the given parameters associated to a specific version of the communication protocol.
	 * 
	 * @param version    The protocol version to use for the returned message.
	 * @param identifier The identifier of the request to create.
	 * @param properties The message properties.
	 * 
	 * @return The created message.
	 */
	public static IVocalMessage create(float version, VocalIdentifier identifier, Object... properties) {
		return FACTORY.create(version, identifier, VocalErrorCode.NONE, properties);
	}

	/**
	 * Parse the given buffer in order to create the associated header and the payload.
	 * 
	 * @param buffer The bytes array received from the remote.
	 * 
	 * @return A new message.
	 */
	public static IVocalMessage parse(byte[] buffer) {
		return FACTORY.parse(buffer);
	}

	/**
	 * Creates a new message corresponding to the answer of the <code>message</code>. The identifier is not incremented. A specific
	 * version of the communication protocol is used to create the answer.
	 * 
	 * @param version    The protocol version to use for the returned message.
	 * @param message    The message to answer.
	 * @param identifier The identifier of the answer request.
	 * @param properties The response properties.
	 * 
	 * @return The message associated to the answer.
	 */
	public static IVocalMessage answer(float version, IVocalMessage message, VocalIdentifier identifier, Object... properties) {
		return FACTORY.answer(version, message, identifier, VocalErrorCode.NONE, properties);
	}

}
