package com.serverapp;


import org.apache.log4j.PropertyConfigurator;

import com.serverapp.config.CommandLineConfigSetter;
import com.serverapp.config.ServerConfigSetter;
import com.serverapp.config.ServerConfiguration;
import com.serverapp.server.Server;
import com.serverapp.server.ServerContext;
import com.serverapp.server.registry.InMemoryClientRegistry;

public class ServerApp {
	
	public static void main(String args[]) {
		PropertyConfigurator.configure("log4j.properties");
		ServerConfigSetter configSetter = new CommandLineConfigSetter(args);
		ServerConfiguration serverConfiguration = configSetter.getConfiguration();
		ServerContext serverContext = new ServerContext(new InMemoryClientRegistry());
		final Server server = new Server(serverConfiguration, serverContext);
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new Thread() {
			public void run() {
				server.shutdownServer();
			}
		});
		server.runServer();
	}

}
