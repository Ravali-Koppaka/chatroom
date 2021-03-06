package com.ravali.serverapp.server.tracker;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ravali.serverapp.server.ServerContext;
import com.ravali.serverapp.server.registry.ClientRegistry;

public class ActiveClientTracker {
	
	private static final long SLEEP_TIME = 1000 * 10;
	private static final Logger LOGGER = LoggerFactory.getLogger(ActiveClientTracker.class.getName());

	private ServerContext serverContext;

	public ActiveClientTracker(ServerContext serverContext) {
		this.serverContext = serverContext;
	}

	public void track() {
		ClientRegistry clientRegistry = serverContext.getClientRegistry();
		try {
			while (!serverContext.isTerminate()) {
				Thread.sleep(SLEEP_TIME);
				System.out.println(clientRegistry.getActiveClients());
				LOGGER.info("Active Clients : {}", clientRegistry.getActiveClients());
			}
		} catch (InterruptedException e) {
			LOGGER.error("Interrupt on finding active Clients", e);
		}
	}

}
