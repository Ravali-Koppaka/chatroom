package com.clientapp.client;

public class ClientContext {
	private boolean terminate;

	public ClientContext() {
		terminate = false;
	}

	public void setTermination(boolean terminate) {
		this.terminate = terminate;
	}

	public boolean isTerminate() {
		return terminate;
	}

}
