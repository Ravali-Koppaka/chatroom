package com.ravali.serverapp.server.registry;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ravali.serverapp.server.data.ClientDetails;

public class InMemoryClientRegistry implements ClientRegistry{
	
	private Map<String, Socket> mapper = new ConcurrentHashMap<String, Socket>();

	public void register(ClientDetails clientDetails) {
		mapper.put(clientDetails.getUserName(), clientDetails.getClientSocket());
	}

	public void deregister(ClientDetails clientDetails) {
		mapper.remove(clientDetails.getUserName(), clientDetails.getClientSocket());
	}

	public ClientDetails getClientDetails(String userName) {
		Socket clientSocket = mapper.get(userName);
		if (clientSocket == null) {
			return null;
		} else {
			ClientDetails clientDetails = new ClientDetails(userName, clientSocket);
			return clientDetails;
		}
	}

	public int getActiveClients() {
		return mapper.size();
	}

}
