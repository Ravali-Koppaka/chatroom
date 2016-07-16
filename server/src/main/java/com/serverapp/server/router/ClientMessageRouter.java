package com.serverapp.server.router;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.commons.exceptions.EndpointException;
import com.commons.util.IOUtil;
import com.commons.util.MessageUtil;
import com.serverapp.server.ServerContext;
import com.serverapp.server.buffer.Queue;
import com.serverapp.server.data.ClientDetails;
import com.serverapp.server.data.Message;
import com.serverapp.server.registry.ClientRegistry;

public class ClientMessageRouter {
	
	public static final int WAIT_TIME_IN_MILISEC = 1000;
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientMessageRouter.class);

	private Queue queue;
	private ServerContext serverContext;

	public ClientMessageRouter(Queue messageBuffer, ServerContext serverContext) {
		this.queue = messageBuffer;
		this.serverContext = serverContext;
	}

	private Message takeMessage() {
		Message message;
		synchronized (queue) {
			LOGGER.debug("Waiting for message to route");
			while (serverNotTerminated() && queue.isEmpty()) {
				try {
					queue.wait(WAIT_TIME_IN_MILISEC);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			message = queue.dequeue();
			queue.notifyAll();
		}
		return message;
	}

	private boolean serverNotTerminated() {
		return (!serverContext.isTerminate());
	}

	public void route() {
		ClientRegistry clientRegistry = serverContext.getClientRegistry();
		while (serverNotTerminated()) {
			Message message = takeMessage();
			LOGGER.debug("{} dequeued", message.getMessage());
			if (serverNotTerminated()) {
				ClientDetails receiverDetails = clientRegistry.getClientDetails(message.getReceiver());
				String messageData;
				if(receiverDetails == null) {
					receiverDetails = clientRegistry.getClientDetails(message.getSender());
					//TODO handle when sender quits
					messageData = MessageUtil.createMessage("Server", MessageUtil.INVALID_USER);
				} else {
					messageData = MessageUtil.createMessage(message.getSender(), message.getMessage());
				}
				try {
					OutputStream opStream = receiverDetails.getClientSocket().getOutputStream();
					LOGGER.debug("{} routing..", messageData);
					IOUtil.write(opStream, messageData);
					LOGGER.debug ("{} routed", messageData);
				} catch (IOException e) {
					throw new EndpointException("I/O Error on creating streams", e);
				}
			}
		}
	}

}
