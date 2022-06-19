package fr.pederobien.vocal.client.impl;

import fr.pederobien.vocal.client.interfaces.IResponse;
import fr.pederobien.vocal.common.impl.VocalErrorCode;

public class Response implements IResponse {
	private VocalErrorCode errorCode;

	/**
	 * Constructs a response when an error occurs.
	 * 
	 * @param errorCode The error code returned by the server when an error occurs.
	 */
	public Response(VocalErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public boolean hasFailed() {
		return errorCode != VocalErrorCode.NONE;
	}

	@Override
	public VocalErrorCode getErrorCode() {
		return errorCode;
	}
}
