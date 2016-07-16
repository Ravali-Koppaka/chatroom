package com.clientapp;

import com.clientapp.client.Client;
import com.clientapp.client.stream.Stream;
import com.clientapp.config.CommandLineConfiguration;
import com.clientapp.config.Configuration;
import com.clientapp.config.ConfigurationProvider;

public class ClientApp {
	
	public static void main(String[] args) {
		ConfigurationProvider configurationProvider = new CommandLineConfiguration(args);
		Configuration configuration = configurationProvider.getConfiguration();
		Stream stream = new Stream(System.in, System.out);
		final Client client = new Client(configuration, stream);
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new Thread () {
			public void run() {
				client.shutDownClient();
			}
		});
		client.startChat();
//		MulticlientGenerator multiClientGenerator = new MulticlientGenerator();
//		multiClientGenerator.generateMultiClients(3, args);
	}
}
