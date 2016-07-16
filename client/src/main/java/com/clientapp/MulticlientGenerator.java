package com.clientapp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.clientapp.client.Client;
import com.clientapp.client.stream.Stream;
import com.clientapp.config.Configuration;

public class MulticlientGenerator {
	
	public void generateMultiClients(int multiClients, String args[]) {
		for (int i = 1; i <= multiClients; i++) {
			try {
				Configuration configuration = new Configuration("c" + i, args[0], Integer.parseInt(args[1]));
				Stream stream = new Stream(new FileInputStream("chat" + i), new FileOutputStream("chatFile" + i));
				Client client = new Client(configuration, stream);
				client.startChat();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
