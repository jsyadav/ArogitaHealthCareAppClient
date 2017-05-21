package com.arogita.appclient.bluetooth;

import java.io.IOException;
import java.io.InputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.arogita.appclient.ReportActivity;
import com.arogita.appclient.protocol.EncodeDecode;
import com.arogita.appclient.protocol.Response;


public class BluetoothReceiverThread extends Thread{

	private static final String TAG = "BluetoothReceiveThread";

	private BluetoothSocket btSocket= null;
	private Messenger mainThradMessanger = null;
	boolean continueLoop = false;


	public BluetoothReceiverThread(Messenger mMessenger, BluetoothSocket socket) {
		// TODO Auto-generated constructor stub
		this.mainThradMessanger = mMessenger;
		this.btSocket = socket;
		continueLoop = false;
		
	}
	private void sendMessage(int what, int value, Object obj) {
		if (mainThradMessanger == null) {
			Log.d(TAG, "No clients registered.");
			return;
		}

		try {
			mainThradMessanger.send(Message.obtain(null, what, value, 0, obj));
		} catch (RemoteException e) {
			// Unable to reach client.
			e.printStackTrace();
		}
	}

	// run method for receive thread
	public void run(){
		continueLoop = true;

		while(continueLoop){
			try {		
				byte[] msg;
				msg = getMessage(btSocket.getInputStream());
				if ( msg != null){
					sendMessageToUI(msg);
				}				
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	// Stops the thread
	public void cancel() {
		Log.d(TAG,"cont set to false");
		continueLoop = false;
	}

	// Process the message
	private void sendMessageToUI(byte[] printMsg){
		int message = 0x00;
		switch(printMsg[3]){
		case(byte) (0xFC):
			// Software version
			message = ReportActivity.SOFTWARE_PARAM_MSG;
		break;
		case(byte) (0xFD):
			// Hardware version
			message = ReportActivity.HARDWARE_PARAM_MSG;
		break;
		case(0x02):
			// ECG readings
			message = ReportActivity.ECG_PARAM_MSG;
		break;
		case(0x03):
			// NIBP readings
			message = ReportActivity.NIBP_PARAM_MSG;
		break;
		case(0x04):
			// SPO2 readings
			message = ReportActivity.SPO2_PARAM_MSG;
		break;
		case(0x05):
			// Temperature readings
			message = ReportActivity.TEMP_PARAM_MSG;
		break;
		case(0x01):
			// ECG Wave
			message = ReportActivity.ECG_WAVE_MSG;
		break;
		case(byte) (0xFE):
			// SPO2 Wave
			message = ReportActivity.SPO2_WAVE_MSG;
		break;
		case(byte) (0xFF):
			// Resp Wave
			message = ReportActivity.RESP_WAVE_MSG;
		break;
		}
		this.sendMessage(message, 0, new Response(printMsg));
	}

	// Read message from Bluetoother Server
	public byte[] getMessage(InputStream in){
		try {
			if (in.available() > 0){
				if (in.read() == 85){
					if (in.read() == 170){
						int len = (in.read() & 0x00FF);
						byte[] msg = new byte [2+ len];
						msg[0] = 0x55; msg[1] = (byte)0xAA;msg[2]=(byte)len;
						in.read(msg,3,len-1);		
						Log.i(TAG,"getMessage: " +EncodeDecode.byteArrayToHexString(msg));
						return msg;
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	
}
