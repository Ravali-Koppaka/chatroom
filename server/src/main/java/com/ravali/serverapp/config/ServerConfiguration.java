package com.ravali.serverapp.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.commons.exceptions.ConnectionException;

public class ServerConfiguration {
	
	public String host;
	public int port;

	public ServerConfiguration(int port) {
		try {
			this.host = InetAddress.getLocalHost().getHostName();
			this.port = port;
		} catch (UnknownHostException e) {
			throw new ConnectionException("Host unknown", e);
		}
	}

	public ServerConfiguration(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}


}
