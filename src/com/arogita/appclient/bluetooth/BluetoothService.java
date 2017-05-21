package com.arogita.appclient.bluetooth;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import com.arogita.appclient.ReportActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class BluetoothService {
	private static final String TAG = "BluetoothService";
	
	private static final int REQUEST_ENABLE_BT = 1;
	// Well known SPP UUID
	private static final UUID MY_UUID =
			UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// Insert your server's MAC address
	//private static String address = "7C:D1:C3:7D:CA:95"; // mac book pro
	//private static String address = "20:13:10:09:00:D8";
	//private static String address = null;
	private static BluetoothDevice btDevice = null;
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private ReportActivity activity = null;
	private boolean connected;
	private Messenger messenger = null;
	
	private BluetoothReceiverThread receiverThread = null;
	private BluetoothSenderThread senderThread = null;
	
	public BluetoothService(ReportActivity mainActivity){
		activity = mainActivity;
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		connected = false;
	}
	
	public boolean isConnected(){
		return connected;
	}
	
	
	public void close(){
		if(receiverThread!=null){
			receiverThread.cancel();
			//receiveThread.interrupt();
			try {
				Log.w(TAG, "Stopping the test, called cancel");
				receiverThread.join();
				Log.w(TAG, "Stopping the test, waiting to join receiver");
			} catch (InterruptedException e) {
				// Not sure if this kills thread
				receiverThread.interrupt();
			}
			receiverThread = null;
		}
		if(senderThread!=null){
			senderThread.cancel();
			
			try {
				Log.w(TAG, "Stopping the test, called cancel");
				senderThread.join();
				Log.w(TAG, "Stopping the test, waiting for join sender");
			} catch (InterruptedException e) {
				// Not sure if this kills thread
				senderThread.interrupt();
			}
			senderThread = null;
		}
		if (btSocket != null){
			if (btSocket.isConnected()){
				try {
					btSocket.close();
					connected = false;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			btSocket = null;
			String msg = "Disconnected from "+ btDevice.getName();
			try {
				messenger.send(Message.obtain(null, ReportActivity.DEVICE_CONNECT, 0, 0, msg));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	
	// Connect to the server
		public boolean connectToServer(Messenger messenger){ 
			this.messenger = messenger;
			if (btDevice == null){
				String [] str = {"Bluetooth Error", "Not paired with any BT device."};
				try {
					messenger.send(Message.obtain(null, ReportActivity.DEVICE_ALERT, 0, 0, str));
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
				return false;
			}
			// Set up a pointer to the remote node using it's address.
			BluetoothDevice device = btAdapter.getRemoteDevice(btDevice.getAddress());

			// Two things are needed to make a connection:
			//   A MAC address, which we got above.
			//   A Service ID or UUID.  In this case we are using the
			//     UUID for SPP.
			try {

				//btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);	    	
				btSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);

			} catch (IOException e) {
				String [] str = {"Fatal Error", "In connect, failed to close socket." + e.getMessage() + "."};
				try {
					messenger.send(Message.obtain(null, ReportActivity.DEVICE_ALERT, 0, 0, str));
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}

			// Discovery is resource intensive.  Make sure it isn't going on
			// when you attempt to connect and pass your message.
			btAdapter.cancelDiscovery();

			// Establish the connection.  This will block until it connects.
			try {
				btSocket.connect();				
				senderThread = new BluetoothSenderThread(activity, btSocket);
				senderThread.start();
				receiverThread = new BluetoothReceiverThread(messenger,btSocket);
				receiverThread.start();
				String msg = "Connected to " + btDevice.getName();
				try {
					messenger.send(Message.obtain(null, ReportActivity.DEVICE_CONNECT, 0, 0, msg));
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				connected = true;
				return true;

			} catch (IOException e) {
				e.printStackTrace();
				String [] str = {"Connection Error", "Check if the box is switched on"};
				try {
					messenger.send(Message.obtain(null, ReportActivity.DEVICE_ALERT, 0, 0, str));
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				close();
			}
			return false;
		}
	
	public String getConnectedDeviceName(){
		return btDevice.getName();
	}
	
	public void checkBTState() {
		// Check for Bluetooth support and then check to make sure it is turned on
		// Emulator doesn't support Bluetooth and will return null
		if(btAdapter==null) { 
			activity.AlertMesg("Fatal Error", "Bluetooth Not supported. Aborting.");
		} else {
			if (btAdapter.isEnabled()) {
				Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

				if (pairedDevices.size() > 1){
					activity.AlertMesg("Config Error", "More than one Bluetooth found.");
				}
				for (BluetoothDevice bdev: pairedDevices){
					btDevice = bdev;
				}
			} else {
				//Prompt user to turn on Bluetooth
				Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
				activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	}
}

