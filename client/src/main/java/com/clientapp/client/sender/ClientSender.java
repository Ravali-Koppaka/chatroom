package com.clientapp.client.sender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clientapp.client.ClientContext;
import com.commons.exceptions.EndpointException;
import com.commons.util.IOUtil;
import com.commons.util.MessageUtil;

public class ClientSender {
	private static final int SLEEP_TIME = 5;
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientSender.class);

	private ClientContext clientContext;
	private Socket clientSocket;
	private InputStream ipStream;

	public ClientSender(Socket clientSocket, InputStream ipStream, ClientContext clientContext) {
		this.clientSocket = clientSocket;
		this.ipStream = ipStream;
		this.clientContext = clientContext;
	}

	private boolean isQuit(String message) {
		return (message == null) || (MessageUtil.isQuitMessage(message));
	}

	private boolean clientNotTerminated() {
		return (!clientContext.isTerminate());
	}

	private boolean isMessageReady(BufferedReader bufferedReader) {
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
		} catch (InterruptedException e) {
			LOGGER.error("Interrupt on sending message", e);
			throw new EndpointException("Failure on reading message", e);
		} catch (IOException e) {
			closeClientSocket();
			throw new EndpointException("I/O Error", e);
		}
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

	private void closeSender(BufferedReader bufferedReader) {
		clientContext.setTermination(true);
		try {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		} catch (IOException e) {
			LOGGER.error("I/O Error on closing streams", e);
		}
	}

	public void send() {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ipStream));
		OutputStream opStream = null;
		try {
			opStream = clientSocket.getOutputStream();
			while (clientNotTerminated()) {
				if (isMessageReady(bufferedReader)) {
					String message = bufferedReader.readLine();
					if (isQuit(message)) {
						clientContext.setTermination(true);
						break;
					}
					IOUtil.write(opStream, message);
					LOGGER.debug("{} sent", message);
				}
			}
		} catch (IOException e) {
			closeClientSocket();
			throw new EndpointException("I/O Error", e);
		} finally {
			MessageUtil.sendQuitMessage(opStream);
			closeSender(bufferedReader);
		}
	}
}
