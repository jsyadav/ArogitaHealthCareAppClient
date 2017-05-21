package com.arogita.appclient.protocol;

public class Request extends Message {

	
	public byte[] command;

	
	public Request (byte[] comm){
		this.command = comm;
	}

	public byte[] getCommand() {
		return command;
	}

	public void setCommand(byte[] command) {
		this.command = command;
	}

	

	
}
