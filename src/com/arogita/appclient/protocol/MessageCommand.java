package com.arogita.appclient.protocol;

public class MessageCommand {
	public static byte ECG_Wave = 0x01;
	public static byte ECG_Param = 0x02;
	public static byte NIBP_Param = 0x03;
	public static byte SPO2_Param = 0x04;
	public static byte TEMP_Param = 0x05;
	public static byte SoftwareVersion = (byte)0xFC;
	public static byte HardwareVersion = (byte)0xFD;
	public static byte SPO2_Wave = (byte)0x0FE;
	public static byte RESP_Wave = (byte)0x0FF;
	
	public static String getMessageName(byte b){

		switch(b){
		case(0x01):
			return "ECG_Wave";
		case(0x02):
			return "ECG_Param";
		case(0x03):
			return "NIBP_Param";
		case(0x04):
			return "SPO2_Param";
		case(0x05):
			return "TEMP_Param";
		case (byte)(0xFC):
			return "SoftwareVersion";
		case (byte)(0xFD):
			return "HardwareVersion";
		case (byte)(0xFE):
			return "SPO2_Wave";
		case (byte)(0xFF):
			return "RESP_Wave";
		
		default:
			return "Unknown message";
		}
	}
}
