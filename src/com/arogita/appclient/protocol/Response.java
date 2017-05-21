package com.arogita.appclient.protocol;

import com.arogita.appclient.ReportActivity;

import android.widget.TextView;

public class Response extends Message{

public byte[] command;

	
	public Response (byte[] comm){
		this.command = comm;
	}

	public byte[] getCommand() {
		return command;
	}

	public void setCommand(byte[] command) {
		this.command = command;
	}
	
	public static void messageType(byte[] b){
		int len = b[2];
		if (b[3] == MessageCommand.ECG_Wave){
			
		}else if (b[3] == MessageCommand.ECG_Param){
			
		}else if (b[3] == MessageCommand.NIBP_Param){
			
		}else if (b[3] == MessageCommand.SPO2_Param){
			
		}else if (b[3] == MessageCommand.TEMP_Param){
			
		}else if (b[3] == MessageCommand.SoftwareVersion){
			
		}else if (b[3] == MessageCommand.HardwareVersion){
			
		}else if (b[3] == MessageCommand.SPO2_Wave){
			
		}else if (b[3] == MessageCommand.RESP_Wave){
			
		}else{
			
		}
	}

	public String getVersion(){
		int msgLen = command[2];
		String verStr ="";
		for (int i = 0; i< (msgLen-3) ;i++){
			verStr += (char)(command[4+i]);
		}
		return verStr;
	}
	
	// SPO2 and RESP Wave
	public int getWaveAplitude(){
		int ret = (command[4]&0x00FF);
		return ret;
	}
	
	public byte[] getECGWave(){
		return command;
	}
	
	public int getEcgIWaveAplitude(){
		int ret = (command[4]&0x00FF);
		return ret;
	}
	public int getEcgIIWaveAplitude(){
		int ret = (command[5]&0x00FF);
		return ret;
	}
	public int getEcgIIIWaveAplitude(){
		int ret = (command[6]&0x00FF);
		return ret;
	}
	public int getEcgAVRWaveAplitude() {
		int ret = (command[7]&0x00FF);
		return ret;
	}
	public int getEcgAVLWaveAplitude() {
		int ret = (command[8]&0x00FF);
		return ret;
	}
	public int getEcgAVFWaveAplitude() {
		int ret = (command[9]&0x00FF);
		return ret;
	}
	public int getEcgVWaveAplitude(){
		int ret = (command[10]&0x00FF);
		return ret;
	}
	
	
	
	// Temperature Status
	public String getTempStatus(){
		if (command[4]==0x00){
			return "Normal";
		}else if (command[4]==0x01){
			return "Temp1 off";
		}else if (command[4]==0x02){
			return "Normal";
			//return "Temp2 off";
		}else if (command[4]==0x03){
			return "Temp1 & 2 off";
		}
		return "";
			
	}
	
	// Temp1 value
	public String getTemp1(){
		if ((command[4] == 0x00)||(command[4] == 0x02)){
			int integral1 = command[5];
			int decimal1 = command[6];
			String temp1 = integral1+"."+decimal1;
			return temp1;
		}else {
			return "--.--";
		}
	}
	
	// Get SPO2 status
	public String getSpo2Status(){
		String ret = "";		
		if (command[4]==0x00){
			ret = "Normal";
		}else if (command[4]==0x01){
			ret = "Sensor off";
		}else if (command[4]==0x02){
			ret = "No finger";
		}else if (command[4]==0x03){
			ret = "Searching pulse ";
		}
		return ret;
	}
	
	public String getSaturationLevel(){
		if(command[4] == 0x00){
			return String.valueOf((command[5]));
		}
		return "0";
	}

	public String getPulse(){
		if(command[4] == 0x00){
			return String.valueOf((command[6]&0x00FF));
		}
		return "0";	
	}
	
	public String getNibpPatientMode(){
		String ret = "";
		switch(command[4] & 0x03){
		case (0x00):
			ret = "Adult";
		break;
		case (0x01):
			ret = "Child";
		break;
		case (0x02):
			ret = "Neonate";
		break;
		}
		return ret;
	}
	public String getNibpTestResult(){
		String ret = "";
		switch(command[4] & 0x3c){		 
		case(0x00):
			ret = "Finished";
			break;
		case(0x04):
			ret = "Running";
			break;
		case(0x08):
			ret = "Test stopped.";
			break;
		case(0x0C):
			ret = "Over pressure protected";
			break;
		case(0x10):
			ret = "Loose";
			break;
		case(0x14):
			ret = "Timeout";
			break;
		case(0x18):
			ret = "Error";
			break;
		case(0x1C):
			ret = "Disturb";
			break;
		case(0x20):
			ret = "Off Range";
		break;
		case(0x21):
			ret = "Initializing";
		break;
		case(0x22):
			ret = "Initialized";
		break;
		
		}
		return ret;
	}
	
	public String[] getNibpValue(){
		if ((command[4] & 0x3c) == 0x00){
			int sysPressure = (command[6] & 0x00FF);
			int diaPressure = (command[8] & 0x00FF);
			String [] msgStr = new String[2];
			msgStr[0] = String.valueOf(sysPressure);
			msgStr[1] = String.valueOf(diaPressure);
			return msgStr;
		}else{
			String[] msgStr = {"00", "00"};
			return msgStr;
		}
	}
	public String getNibpCuffPressure(){
		int cuffPressure = (command[5] & 0x00FF);
		return String.valueOf(cuffPressure);
	}
	
	public String getEcgSigIntensity(){
		String ret ="";
		switch(command[4] & 0x01) {
		case(0x00):
			ret = "Normal";
			break;
		case(0x01):
			ret = "Weak";
			break;
		}
		return ret;
		
	}
	
	public String getEcgLeadStatus(){
		String ret ="";
		switch(command[4] & 0x02) {
		case(0x00):
			ret = "Normal";
			break;
		case(0x02):
			ret = "Lead Off";
			break;
		}
		return ret;
	}
	
	
	public String getEcgWaveGain(){
		String ret ="";
		switch(command[4] & 0x0C) {
		case(0x00):
			ret = "x.25";
			break;
		case(0x04):
			ret = "x.5";
			break;
		case(0x08):
			ret = "x1";
			break;
		case(0x0C):
			ret = "x2";
			break;
		}
		return ret;

	}
	public String getEcgFilterMode(){
		String ret ="";
		switch(command[4] & 0x20) {
		case(0x00):
			ret = "Opertion";
			break;
		case(0x10):
			ret = "Monitor";
			break;
		case(0x20):
			ret = "Diagnose";
			break;
		
		}
		return ret;

	}
	public String getEcgLeadMode(){
		String ret ="";
		switch(command[4] & 0xC0) {
		case(0x00):
			ret = "LeadI";
			break;
		case(0x40):
			ret = "LeadII";
			break;
		case(0x80):
			ret = "LeadIII";
			break;
		case(0xC0):
			ret = "LeadV";
			break;
		}
		return ret;

	}
	
	public String getEcgHeartRate(){
		int hr = (command[5] & 0x00FF);
		return String.valueOf(hr);
	}
	public String getEcgRespRate(){
		int rr = (command[6] & 0x00FF);
		return String.valueOf(rr);
	}
	public String getEcgStLevel(){
		int st = (command[7] & 0x00FF);
		return String.valueOf(st);
	}
	public String getEcgArrCode(){
		String arrCode = "";
		switch(command[8] & 0x0F){
		case (0x00):
			arrCode = " Analysis ";
			break;
		case (0x01):
			arrCode = " Normal ";	
			break;
		case (0x02):
			arrCode = " Asystole ";
			break;
		case (0x03):
			arrCode = " VFIb/VTAC ";
			break;
		case (0x04):
			arrCode = " R on T ";
			break;
		case (0x05):
			arrCode = " Multi PVCS ";
			break;
		case (0x06):
			arrCode = " Couple PVCS ";
			break;
		case (0x07):
			arrCode = " PVC ";
			break;
		case (0x08):
			arrCode = " Bigerminy ";
			break;
		case (0x09):
			arrCode = " Trigermny ";
			break;
		case (0x0A):
			arrCode = " Tachycardia ";
			break;
		case (0x0B):
			arrCode = " Bradycardia ";
			break;
		case (0x0C):
			arrCode = " Missed Beats ";
			break;	
		}
		return arrCode;
	}


	

	
}
