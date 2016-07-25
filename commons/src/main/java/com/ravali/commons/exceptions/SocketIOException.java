package com.ravali.commons.exceptions;

public class SocketIOException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SocketIOException() {
		super();
	}

	public SocketIOException(String message) {
		super(message);
	}

	public SocketIOException(String message, Throwable cause) {
		super(message, cause);
	}

	public SocketIOException(Throwable cause) {
		super(cause);
	}

}
