package com.clientapp.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandLineConfiguration implements ConfigurationProvider{
	private String[] args;

	public CommandLineConfiguration(String[] args) {
		this.args = args;
	}

	public Configuration getConfiguration() {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		// String userName = null;
		System.out.print("Enter userName : ");
		String userName = null;
		try {
			userName = bufferedReader.readLine();
		} catch (IOException e) {
			throw new ConfigurationException("I/O Error on reading user name", e);
		}
		return new Configuration(userName, args[0], Integer.parseInt(args[1]));
	}
}
