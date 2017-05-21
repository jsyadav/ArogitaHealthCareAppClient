package com.arogita.appclient.protocol;

public class RequestCommand {
	public static short ECG_Test_Disable = 0x0100;
	public static short ECG_Test_Enable = 0x0101;
	
	public static short NIBP_Test_Disable = 0x0200;
	public static short NIBP_Test_Enable = 0x0201;
	
	public static short SPO2_Test_Disable = 0x0300;
	public static short SPO2_Test_Enable = 0x0301;
	
	public static short TEMP_Test_Disable = 0x0400;
	public static short TEMP_Test_Enable = 0x0401;
	
	public static short ECG_Lead3_5_Switch1 = 0x0501;
	public static short ECG_Lead3_5_Switch2 = 0x0502;	
	public static short ECG_Lead3_5_Switch3 = 0x0503;
	public static short ECG_Lead3_5_Switch4 = 0x0504;
	
	public static short ECG_WaveGainxQuarter = 0x0701;
	public static short ECG_WaveGainxHalf = 0x0702;
	public static short ECG_WaveGainxOnce = 0x0703;
	public static short ECG_WaveGainxTwice = 0x0704;
	
	public static short ECG_FilterModeOperation = 0x0801;
	public static short ECG_FilterModeMonitor = 0x0802;
	public static short ECG_FilterModeDiagnose = 0x0803;
	
	public static short NIBP_Patient_ModeAdult = 0x0901;
	public static short NIBP_Patient_ModeChild = 0x0902;
	public static short NIBP_Patient_ModeNeonate = 0x0903;
	
	public static short NIBP_PresentCuffPressure = 0x0A;
	
	public static short NIBP_StaticPressureCalibrateStop = 0x0B00;
	public static short NIBP_StaticPressureCalibrateStatic = 0x0B01;
	
	public static short NIBP_StaticPressureBiasSetup = 0x0C;
	
	public static short TEMP1_BiasSetup = 0x0D;
	public static short Temp2_BiasSetup = 0x0E;
	
	public static short RESP_WaveGainQuater = 0x0F01;
	public static short RESP_WaveGainHalf = 0x0F02;
	public static short RESP_WaveGainOnce = 0x0F03;
	public static short RESP_WaveGainTwice = 0x0F04;	
	
	public static short NIBP_LeakAgeTestStop = (short)0x1000;
	
	public static short ECG_WaveOutputDisable = (short)0xFB00;
	public static short ECG_WaveOutputEnable = (short)0xFB01;
	
	public static short SoftwareVersionInquiry = (short)0xFC00;
	public static short HardwareVersionInquiry = (short)0XFD00;
	
	public static short SPO2_WaveOutputDisable = (short)0xFE00;
	public static short SPO2_WaveOutputEnable = (short)0xFE01;
	
	public static short RESP_WaveOutputDisable = (short)0xFF00;
	public static short RESP_WaveOutputEnable = (short)0xFF01;
	
	public static short InvalidCommand = (short)0xFFFF;
	
	public static byte[] getByteArray(short command){
		byte[] ret = new byte[2];
		ret[0] = (byte)((command & 0xFF00)>>8);
		ret[1] = (byte)(command & 0x00FF);
		
		return ret;
	}
	
	public static short getCommandFromByteArray(byte[] bArray){
		short ret = 0;
		ret = bArray[0]; ret <<= 8;
		ret |= bArray[1];
		return ret;
	}
	
	public static String getCommandName(byte[] bArray){
		short command = getCommandFromByteArray(bArray);
		
		switch(command){
		case(0x0100):
			return "ECG_Test_Disable";
		case(0x0101):
			return "ECG_Test_Enable";
		
		case(0x0200):
			return "NIBP_Test_Disable";
		case(0x0201):
			return "NIBP_Test_Enable";
		
		case(0x0300):
			return "SPO2_Test_Disable";		 
		case(0x030):
			return "SPO2_Test_Enable";
		
		case(0x0400):
			return "TEMP_Test_Disable";
		 
		case(0x0401):
			return "TEMP_Test_Enable";
		 
		case(0x0501):
			return "ECG_Lead3_5_Switch1";
		
		case(0x0502):
			return "ECG_Lead3_5_Switch2";
		 
		case(0x0503):
			return "ECG_Lead3_5_Switch3";

		case(0x0504):
			return "ECG_Lead3_5_Switch4";
		 
		case(0x0701):
			return "ECG_WageGainxQuarter";
		
		 
		case(0x0702):
			return "ECG_WageGainxHalf";
		 
		case(0x0703):
			return "ECG_WageGainxOnce";
		 
		case(0x0704):
			return "ECG_WageGainxTwice";
		 
		case(0x0801):
			return "ECG_FilterModeOperation";
		
		 
		case(0x0802):
			return "ECG_FilterModeMonitor";
		 
		case(0x0803):
			return "ECG_FilterModeDiagnose";
		 
		case(0x0901):
			return "NIBP_Patient_ModeAdult";
		
		 
		case(0x0902):
			return "NIBP_Patient_ModeChild";
		 
		case(0x0903):
			return "NIBP_Patient_ModeNeonate";
		 
		case(0x0A):
			return "NIBP_PresentCuffPressure";
		
		 
		case(0x0B00):
			return "NIBP_StaticPressureCalibrateStop";
		
		
		
		 
		case(0x0B01):
			return "NIBP_StaticPressureCalibrateStatic";
		 
		case(0x0C):
			return "NIBP_StaticPressureBiasSetup";
		
		 
		case(0x0D):
			return "TEMP1_BiasSetup";
		
		 
		case(0x0E):
			return "Temp2_BiasSetup";
		 
		case(0x0F01):
			return "RESP_WaveGainQuater";
		
		 
		case(0x0F02):
			return "RESP_WaveGainHalf";
		 
		case(0x0F03):
			return "RESP_WaveGainOnce";
		 
		case(0x0F04):
			return "RESP_WaveGapublicTwice";
		 
		case(0x1000):
			return "NIBP_LeakAgeTestStop";
		 
		case(short) (0xFB00):
			return "ECG_WaveOutputDisable";
		
		 
		case(short) (0xFB01):
			return "ECG_WaveOutputEnable";
		 
		case(short) (0xFC00):
			return "softwareVersionInquiry";
		
		 
		case(short) (0XFD00):
			return "hardwareVersionInquiry";
		 
		case(short) (0xFE00):
			return "SPO2_WaveOutputDisable";
		
		 
		case(short) (0xFE01):
			return "SPO2_WaveOutputEnable";
		 
		case(short) (0xFF00):
			return "RESP_WaveOutputDisable";
		
		 
		case(short) (0xFF01):
			return "RESP_WaveOutputEnable";
		 
		}
		return "Unknown Comand";
	}
}
