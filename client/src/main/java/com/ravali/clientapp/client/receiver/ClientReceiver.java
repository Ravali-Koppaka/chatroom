package com.ravali.clientapp.client.receiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.commons.exceptions.EndpointException;
import com.ravali.clientapp.client.ClientContext;
import com.ravali.clientapp.client.receiver.ClientReceiver;

public class ClientReceiver {
	
	private static final String ANSI_RESET = "\u001B[0m";
	private static final String ANSI_GREEN = "\u001B[32m";
	private static final int SLEEP_TIME = 5;
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientReceiver.class);

	private ClientContext clientContext;
	private Socket clientSocket;
	private OutputStream opStream;

	public ClientReceiver(Socket clientSocket, OutputStream opStream, ClientContext clientContext) {
		this.clientSocket = clientSocket;
		this.opStream = opStream;
		this.clientContext = clientContext;
	}

	private void closeClientSocket() {
		if (clientSocket != null) {
			try {
				clientSocket.close();
			} catch (IOException e1) {
				LOGGER.error("I/O Error on closing streams", e1);
			}
		}
	}

	private boolean clientNotTerminated() {
		return !clientContext.isTerminate();
	}

	private boolean isMessageReceived(BufferedReader bufferedReader) {
		try {
			boolean ready = true;
			while (!bufferedReader.ready()) {
				Thread.sleep(SLEEP_TIME);
				if (clientContext.isTerminate()) {
					ready = false;
					break;
				}
			}
			return ready;
		} catch (IOException e) {
			closeClientSocket();
			throw new EndpointException("I/O Error", e);
		} catch (InterruptedException e) {
			LOGGER.error("Interrupt on receiving message", e);
			throw new EndpointException("Failure on receiving message", e);
		}
	}
	
	public void receive() {
		PrintStream printStream = new PrintStream(opStream);
		try {
			InputStream ipStream = clientSocket.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ipStream));
			while (clientNotTerminated()) {
				if (isMessageReceived(bufferedReader)) {
					String message = bufferedReader.readLine();
					LOGGER.debug("{} received", message);
					printStream.println(ANSI_GREEN + message + ANSI_RESET);
				}
			}
		} catch (IOException e) {
			closeClientSocket();
			throw new EndpointException("I/O Error", e);
		} finally {
			clientContext.setTermination(true);
			printStream.close();
		}
	}

}
