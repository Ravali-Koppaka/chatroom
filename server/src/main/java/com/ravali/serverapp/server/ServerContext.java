package com.ravali.serverapp.server;

import com.ravali.serverapp.server.registry.ClientRegistry;

public class ServerContext {
	
	private boolean terminate;
	private ClientRegistry clientRegistry;

	public ServerContext(ClientRegistry ClientRegistry) {
		this.clientRegistry = ClientRegistry;
		terminate = false;
	}

	public void setTermination(boolean terminate) {
		this.terminate = terminate;
	}

	public boolean isTerminate() {
		return terminate;
	}

	public ClientRegistry getClientRegistry() {
		return clientRegistry;
	}

}
