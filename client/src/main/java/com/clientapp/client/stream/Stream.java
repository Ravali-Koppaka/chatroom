package com.clientapp.client.stream;

import java.io.InputStream;
import java.io.OutputStream;

public class Stream {
	private InputStream ipStream;
	private OutputStream opStream;

	public Stream(InputStream ipStream, OutputStream opStream) {
		this.ipStream = ipStream;
		this.opStream = opStream;
	}

	public InputStream getInputStream() {
		return ipStream;
	}

	public OutputStream getOutputStream() {
		return opStream;
	}

	public void setInputStream(InputStream ipStream) {
		this.ipStream = ipStream;
	}

	public void setOutputStream(OutputStream opStream) {
		this.opStream = opStream;
	}

}
