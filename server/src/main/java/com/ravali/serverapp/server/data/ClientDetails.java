package com.ravali.serverapp.server.data;

import java.net.Socket;

public class ClientDetails {
	
	private String userName;
	private Socket clientSocket;

	public ClientDetails() {
		this.userName = null;
		this.clientSocket = null;
	}

	public ClientDetails(String userName, Socket clientSocket) {
		this.userName = userName;
		this.clientSocket = clientSocket;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public String getUserName() {
		return userName;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

}
