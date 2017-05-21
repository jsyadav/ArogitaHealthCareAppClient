package com.arogita.appclient.bluetooth;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.arogita.appclient.ReportActivity;
import com.arogita.appclient.protocol.EncodeDecode;
import com.arogita.appclient.protocol.Request;
import com.arogita.appclient.protocol.RequestCommand;

public class BluetoothSenderThread extends Thread{
	private static final String TAG = "BluetoothSenderThread";
	
	private BluetoothSocket btSocket = null;
	private boolean connected = false;
	private boolean continueLoop = false;
	private OutputStream outStream = null;
	private ReportActivity activity = null;
	public  LinkedBlockingQueue<Integer> bluetoothQueueForSending = null;;

	
	public BluetoothSenderThread(ReportActivity activity, BluetoothSocket socket) {
		this.activity = activity;
		this.btSocket = socket;
		this.bluetoothQueueForSending = new LinkedBlockingQueue<Integer>();
		connected = false;
		continueLoop = false;
	}
	
		
	// run method for send thread
	public void run(){
		continueLoop = true;
		activity.setSenderQueue(bluetoothQueueForSending);
		while(continueLoop){
			Integer i = this.bluetoothQueueForSending.poll();
			if( i != null ){
				short msg = (short)(i & 0xFFFF);
				sendToServer(msg);
			}else{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}
			
	}
	// Stops the thread
	public void cancel() {
		Log.d(TAG,"cont set to false");
		continueLoop = false;
	}
	
	public boolean isConnected(){
		return this.connected;
	}

	
	public void sendToServer(short command){
		if (!btSocket.isConnected()){
			activity.runOnUiThread(new Runnable(){
				public void run() {
					activity.AlertMesg("Socket Error ", "Not connected to server ");
				}});
			 
			return;
		}		  		
		// Create a data stream so we can talk to server.
		try {
			outStream = btSocket.getOutputStream();
			byte[] reqMessage = EncodeDecode.encode(new Request(RequestCommand.getByteArray(command)));

			String msg = "\nCommand: "+ EncodeDecode.byteArrayToHexString(reqMessage);
			Log.w(TAG, msg);
			outStream.write(reqMessage);
			outStream.flush();		    	 			      

		}    catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				btSocket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}


}
	