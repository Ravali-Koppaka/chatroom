package com.serverapp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.commons.exceptions.EndpointException;
import com.serverapp.config.ServerConfiguration;
import com.serverapp.server.buffer.Queue;
import com.serverapp.server.data.ClientDetails;
import com.serverapp.server.reader.ClientMessageReader;
import com.serverapp.server.registry.ClientRegistry;
import com.serverapp.server.router.ClientMessageRouter;
import com.serverapp.server.tracker.ActiveClientTracker;

public class Server {
	
	public static final long SLEEP_TIME_IN_MILLISEC = 5;
	private static final Logger LOGGER = LoggerFactory.getLogger(Server.class.getName());

	private ServerSocket serverSocket;
	private ServerContext serverContext;
	private List<Thread> threadList;
    
	public Server(ServerConfiguration serverConfiguration, ServerContext serverContext) {
		serverInit(serverConfiguration, serverContext);
	}

	private void serverInit(ServerConfiguration serverConfiguration, ServerContext serverContext) {
		try {
			serverSocket = new ServerSocket(serverConfiguration.getPort());
			this.serverContext = serverContext;
			this.threadList = new ArrayList<Thread>();
		} catch (IOException e) {
			throw new EndpointException("I/O Error on opening server socket", e);
		}
	}

	private void joinThreads(List<Thread> threadList) {
		for (Thread thread : threadList) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				LOGGER.error("Interrupt on server shutdown", e);
				throw new EndpointException("Interrupt on server shutdown", e);
			}
		}
	}

	private String readUserName(BufferedReader bufferedReader) {
		try {
			while (!bufferedReader.ready()) {
				Thread.sleep(SLEEP_TIME_IN_MILLISEC);
			}
			return bufferedReader.readLine();
		} catch (IOException e) {
			throw new EndpointException("I/O Error on creating stream", e);
		} catch (InterruptedException e) {
			LOGGER.error("Server interrupted", e);
			throw new EndpointException("Server interrupted on reading", e);
		}
	}

	private void startActiveClientTracker() {
		Thread activeClientThread = new Thread(new Runnable() {
			public void run() {
				new ActiveClientTracker(serverContext).track();
			}
		});
		activeClientThread.setName("Active Clients tracker");
		activeClientThread.start();
		threadList.add(activeClientThread);
	}

	private void startClientMessageRouter(final Queue queue) {
		Thread clientMessageRouterThread = new Thread(new Runnable() {
			public void run() {
				new ClientMessageRouter(queue, serverContext).route();
			}
		});
		clientMessageRouterThread.setName("Message Router");
		clientMessageRouterThread.start();
		threadList.add(clientMessageRouterThread);
	}

	private void startClientMessageReader(final Queue queue, Socket clientSocket) {
		try {
			InputStream ipStream = clientSocket.getInputStream();
			final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ipStream));
			final ClientDetails clientDetails = new ClientDetails(readUserName(bufferedReader), clientSocket);
			Thread clientReaderThread = new Thread(new Runnable() {
				public void run() {
					ClientRegistry clientRegistry = serverContext.getClientRegistry();
					clientRegistry.register(clientDetails);
					LOGGER.info("{} connected" , clientDetails.getUserName());
					try {
						new ClientMessageReader(queue, clientDetails, serverContext).read(bufferedReader);
					} finally {
						clientRegistry.deregister(clientDetails);
						LOGGER.info("{} disconnected" , clientDetails.getUserName());
					}
				}
			});
			clientReaderThread.setName("Client Reader - " + clientDetails.getUserName());
			clientReaderThread.start();
			threadList.add(clientReaderThread);
		} catch (IOException e) {
			throw new EndpointException("I/O Error while creating stream", e);
		}
 	}

	public void runServer() {
		startActiveClientTracker();
		Queue queue = new Queue();
		startClientMessageRouter(queue);
		try {
			while (!serverContext.isTerminate()) {
				Socket clientSocket = serverSocket.accept();
				startClientMessageReader(queue, clientSocket);
			}
		} catch (IOException e) {
			throw new EndpointException("I/O Error while connecting", e);
		}
	}

	public void shutdownServer() {
		serverContext.setTermination(true);
		joinThreads(threadList);
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			LOGGER.error("I/O Error on closing the socket", e);
			throw new EndpointException("I/O Error on closing the socket", e);
		}
	}
}
