package com.arogita.appclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.example.sampleappclient.R;

public class EcgWaveActivity extends Activity {
	private static final String TAG = "EcgWaveActivity";
	public static String ECG_WAVE_MSG = "ECG_WAVE_MSG";

	public String label = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ece_wave);
		Bundle bundle = getIntent().getExtras();
		label = bundle.getString("Label");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ece_wave, menu);
		return true;
	}
	
	private XYPlot plot = null;
	private SimpleXYSeries series = null;
	
	private static final int HISTORY_SIZE = 500;

	//ReaderThread readerThread = null;
	private void prepareGraphs(){
		plot =  (XYPlot) findViewById(R.id.XYPlot);
		series = new SimpleXYSeries(label);
		bindGraphs(plot, series);
	}
	public final static int ECG_MSG = 0x01;
	private Handler mIncomingHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			drawChart(msg.arg1);
			
		}
	};
	private final Messenger mMessenger = new Messenger(mIncomingHandler);
	
	class ReaderThread extends Thread{
		Messenger messenger = null;
		InputStream is = null;
		boolean cont = true;
		ReaderThread(Messenger messenger, InputStream inputStream){
			this.messenger = messenger;
			this.is = inputStream;
		}
		public void run(){
			while(cont){
				int i=0;
				try {
					i = is.read();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.w(TAG, "Got value from file "+ i);
				while(i>=0){
					//Log.w(TAG, "Got value from file "+ i);
					try {
						messenger.send(Message.obtain(null, ECG_MSG, i, 0, 0));
						
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						i = is.read();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			}
		}
		// Stops the thread
		public void cancel() {
			Log.d(TAG,"cont set to false");
			cont = false;
		}
	}
	
	
	
	
	
	BroadcastReceiver connectionUpdates = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
        	int i = intent.getIntExtra("message", 0);
            Log.d(TAG, "Got message: " + i + ", Tid = " + Thread.currentThread().getId());
            
            if (i > 0)
            	drawChart(i);
        }
    };
    
    public void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver(
	            connectionUpdates ,
	            new IntentFilter(EcgWaveActivity.ECG_WAVE_MSG));
		prepareGraphs();
	}

    public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(connectionUpdates);

	}
    
	private void bindGraphs(XYPlot applicationPlot, SimpleXYSeries series) {
		series.useImplicitXVals();
		applicationPlot.setRangeBoundaries(0, 250, BoundaryMode.FIXED);
		applicationPlot.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);		
		applicationPlot.addSeries(series, new LineAndPointFormatter(Color.BLACK, null, null, null));
		applicationPlot.setDomainStepValue(5);
		applicationPlot.setTicksPerRangeLabel(3);
		applicationPlot.setDomainLabel("");
		applicationPlot.getDomainLabelWidget().pack();
		applicationPlot.setRangeLabel("");
		applicationPlot.getRangeLabelWidget().pack();
		//applicationPlot.setMarkupEnabled(true);
		applicationPlot.getGraphWidget().setPadding(0, 10, 5, 5);
		applicationPlot.getLegendWidget().setMarginBottom(5);
	}
	
	public  void drawChart(int val){
		if (series.size() > HISTORY_SIZE) {
			series.removeFirst();
		}
		series.addLast(null, (val & 0xFF));	       
		plot.redraw();
	}

}
