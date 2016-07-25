package com.ravali.serverapp;


import org.apache.log4j.PropertyConfigurator;

import com.ravali.serverapp.config.CommandLineConfigSetter;
import com.ravali.serverapp.config.ServerConfigSetter;
import com.ravali.serverapp.config.ServerConfiguration;
import com.ravali.serverapp.server.Server;
import com.ravali.serverapp.server.ServerContext;
import com.ravali.serverapp.server.registry.InMemoryClientRegistry;

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
