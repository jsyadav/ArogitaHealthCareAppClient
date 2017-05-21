package com.arogita.appclient.protocol;


public class PortSettings {
	
	final int PARITY_NONE = 0;
	final int PARITY_ODD = 1;
	final int PARITY_EVEN = 2;
	
	int baudRate = 115200;
	int dataBits = 8;
	int parityBit = PARITY_NONE;
	float stopBit = 1;
	
	PortSettings(int bRate, int dBits, int pBit, float sBit){
		this.baudRate = bRate;
		this.dataBits = dBits;
		this.parityBit = pBit;
		this.stopBit = sBit;
	}
}
