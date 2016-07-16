package com.commons.exceptions;

public class EndpointException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EndpointException() {
		super();
	}

	public EndpointException(String message) {
		super(message);
	}

	public EndpointException(String message, Throwable cause) {
		super(message, cause);
	}

	public EndpointException(Throwable cause) {
		super(cause);
	}

}
