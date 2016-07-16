package com.serverapp.config;

public class CommandLineConfigSetter implements ServerConfigSetter {
	
	private String[] args;

	public CommandLineConfigSetter(String[] args) {
		this.args = args;
	}

	public ServerConfiguration getConfiguration() {
		return new ServerConfiguration(Integer.parseInt(args[0]));
	}

}
