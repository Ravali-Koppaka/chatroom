package com.ravali.serverapp.server.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.commons.exceptions.EndpointException;
import com.commons.exceptions.MessageFormatException;
import com.commons.util.MessageUtil;
import com.ravali.serverapp.server.Server;
import com.ravali.serverapp.server.ServerContext;
import com.ravali.serverapp.server.buffer.Queue;
import com.ravali.serverapp.server.data.ClientDetails;
import com.ravali.serverapp.server.data.Message;

public class ClientMessageReader {
	
	public static final int WAIT_TIME_IN_MILLISEC = 1000;
	private final static Logger LOGGER = LoggerFactory.getLogger(ClientMessageReader.class);

	private Queue queue;
	private ClientDetails clientDetails;
	private ServerContext serverContext;
	
	public ClientMessageReader(Queue queue, ClientDetails clientDetails, ServerContext serverContext) {
		this.queue = queue;
		this.clientDetails = clientDetails;
		this.serverContext = serverContext;
	}

	private boolean isMessageReady(BufferedReader bufferedReader) {
		try {
			boolean ready = true;
			while (!bufferedReader.ready()) {
				Thread.sleep(Server.SLEEP_TIME_IN_MILLISEC);
				if (serverContext.isTerminate()) {
					ready = false;
					break;
				}
			}
			return ready;
		} catch (IOException e) {
			throw new EndpointException("I/O Error on creating socket streams", e);
		} catch (InterruptedException e) {
			LOGGER.error("Server interrupted", e);
			throw new EndpointException("Server interrupted on reading", e);
		}
	}

	private boolean serverNotTerminated() {
		return (!serverContext.isTerminate());
	}

	private Message parseMessage(String messageData) {
		try {
			String receiver = MessageUtil.getReceiver(messageData);
			String message = MessageUtil.getMessage(messageData);
			return new Message(clientDetails.getUserName(), receiver, message);
		} catch (MessageFormatException e) {
			throw new MessageFormatException(e);
		}
	}

	private void putMessage(Message message) {
		synchronized (queue) {
			while ((serverNotTerminated()) && (queue.isFull())) {
				try {
					queue.wait(WAIT_TIME_IN_MILLISEC);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			queue.enqueue(message);
			queue.notifyAll();
		}
	}

	private void closeClientSocket() {
		Socket clientSocket = clientDetails.getClientSocket();
		try {
			if (clientSocket != null) {
				clientSocket.close();
			}
		} catch (IOException e) {
			LOGGER.error("I/O Error on closing socket", e);
		}
	}

	public void read(BufferedReader bufferedReader) {
		try {
			while (serverNotTerminated()) {
				LOGGER.debug("Waiting for message from {}" ,clientDetails.getUserName());
				if (isMessageReady(bufferedReader)) {
					String messageData = bufferedReader.readLine();
					LOGGER.debug("{} read", messageData);
					if (MessageUtil.isQuitMessage(messageData)) {
						break;
					}
					Message message = parseMessage(messageData);
					LOGGER.debug("{} parsed", message.getMessage());
					putMessage(message);
					LOGGER.debug("{} enqueued", message.getMessage());
				}
			}
		} catch (IOException e) {
			throw new EndpointException("I/O Error on creating socket streams", e);
		} finally {
			closeClientSocket();
		}
	}

}
