package com.ravali.serverapp.server.registry;

import com.ravali.serverapp.server.data.ClientDetails;

public interface ClientRegistry {
	
	void register(ClientDetails clientDetails);

	void deregister(ClientDetails clientDetails);

	ClientDetails getClientDetails(String userName);

	int getActiveClients();

}
