package com.ravali.clientapp;

import com.ravali.clientapp.client.Client;
import com.ravali.clientapp.client.stream.Stream;
import com.ravali.clientapp.config.CommandLineConfiguration;
import com.ravali.clientapp.config.Configuration;
import com.ravali.clientapp.config.ConfigurationProvider;

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
