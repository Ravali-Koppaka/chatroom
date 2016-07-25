package com.ravali.clientapp.config;

public class Configuration {
	private String userName;
	private String host;
	private int port;

	public Configuration(String userName, String host, int port) {
		this.userName = userName;
		this.host = host;
		this.port = port;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

}
