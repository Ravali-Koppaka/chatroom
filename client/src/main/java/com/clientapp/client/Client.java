package com.clientapp.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clientapp.client.receiver.ClientReceiver;
import com.clientapp.client.sender.ClientSender;
import com.clientapp.client.stream.Stream;
import com.clientapp.config.Configuration;
import com.commons.exceptions.ConnectionException;
import com.commons.exceptions.EndpointException;
import com.commons.util.IOUtil;

public class Client {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Client.class.getName());

	private Configuration configuration;
	private Stream stream;
	private ClientContext clientContext = new ClientContext();
	private Socket clientSocket;
	private Thread senderThread;
	private Thread receiverThread;

	public Client(Configuration configuration, Stream stream) {
		this.configuration = configuration;
		this.stream = stream;
		connect();
	}

	private void connect() {
		String serverAddress = configuration.getHost();
		int serverPort = configuration.getPort();
		try {
			// TODO change IPAddress from configurations
			clientSocket = new Socket(serverAddress, serverPort);
			IOUtil.write(clientSocket.getOutputStream(), configuration.getUserName());
		} catch (UnknownHostException e) {
			throw new ConnectionException("Host unknown ", e);
		} catch (IOException e) {
			if (clientSocket != null) {
				try {
					clientSocket.close();
				} catch (IOException e1) {
					LOGGER.error("I/O Error on closing socket", e1);
				}
			}
			throw new EndpointException("I/O Error on creating socket", e);
		}
	}
	
	public void startChat() {
		// TODO singleton for sender and receiver handlers
		senderThread = new Thread(new Runnable() {
			public void run() {
				new ClientSender(clientSocket, stream.getInputStream(), clientContext).send();
			}
		});
		receiverThread = new Thread(new Runnable() {
			public void run() {
				new ClientReceiver(clientSocket, stream.getOutputStream(), clientContext).receive();
			}
		});
		senderThread.setName("Client sender - " + configuration.getUserName());
		senderThread.start();
		receiverThread.setName("Client receiver - " + configuration.getUserName());
		receiverThread.start();
	}

	public void shutDownClient() {
		clientContext.setTermination(true);
		try {
			senderThread.join();
			receiverThread.join();
			if (clientSocket != null) {
				clientSocket.close();
			}
		} catch (InterruptedException e) {
			LOGGER.error("Interrupt on closing Client", e);
		} catch (IOException e) {
			LOGGER.error("I/O Error on closing streams", e);
		}
	}

}
