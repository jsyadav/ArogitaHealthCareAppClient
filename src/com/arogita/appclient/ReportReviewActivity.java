package com.arogita.appclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.TextView;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.arogita.appclient.bluetooth.BluetoothReceiverThread;
import com.arogita.appclient.db.DatabaseHelper;
import com.arogita.appclient.protocol.Response;
import com.example.sampleappclient.R;

public class ReportReviewActivity extends Activity {

	private static final String TAG = "ReportReviewActivity";

	String patientId = null;
	String reportId = null;

	private TextView statusView;
	private TextView heartRateView;
	private TextView respRateView;
	private TextView stLevelView;
	private TextView arrCodeView;
	private TextView nibpView;
	private TextView saturationView;
	private TextView pulseView;
	private TextView tempView;
	private String chartFileName;

	// button
	private Button save;
	private Button notes;

	private String docNotes="";



	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_review);
		ActionBar actionBar = getActionBar();
		actionBar.hide();

		statusView = (TextView) findViewById(R.id.status);
		heartRateView =  (TextView)findViewById(R.id.hr);
		respRateView= (TextView)findViewById(R.id.rr);
		stLevelView = (TextView)findViewById(R.id.st);		
		arrCodeView = (TextView)findViewById(R.id.arr);
		nibpView = (TextView)findViewById(R.id.nibp);
		saturationView = (TextView)findViewById(R.id.sat);
		pulseView = (TextView)findViewById(R.id.pulse);
		tempView = (TextView)findViewById(R.id.temp);

		save = (Button)findViewById(R.id.save);
		notes = (Button)findViewById(R.id.notes);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null){
			patientId = bundle.getString(PatientActivity.UID);
			Log.d(TAG, "Patient id from intent " + patientId);
			reportId = bundle.getString("ReportId");
			if (reportId != null){
				// View report mode
				// Make only notes as editable	
				Log.d(TAG, "In read only mode for report " + reportId);
				Log.d(TAG, "Date" + bundle.getString("Date"));

				statusView.setText("Reviewing Report (only Notes are updatable).....");
				readAndAssignReportData();
			}
			bindButtons();
			bindGraphs();
			startReaderThread();
		}
	}


	// Read data form DB and present in the view
	private void readAndAssignReportData() {
		// TODO Auto-generated method stub
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		Log.d(TAG,"Searching report "+ reportId);
		Cursor cur = dbHelper.getReport(reportId);
		int colCount = cur.getColumnCount();
		while(cur.moveToNext()){
			for (int i =0;i < colCount;i++){
				Log.d (TAG, cur.getColumnName(i) +" : " +cur.getString(i));
				if(cur.getColumnName(i).equalsIgnoreCase(DatabaseHelper.temp)){
					tempView.setText(cur.getString(i));
				}else if(cur.getColumnName(i).equalsIgnoreCase(DatabaseHelper.nibp)){
					nibpView.setText(cur.getString(i));
				}else if(cur.getColumnName(i).equalsIgnoreCase(DatabaseHelper.saturation)){
					saturationView.setText(cur.getString(i));
				}else if(cur.getColumnName(i).equalsIgnoreCase(DatabaseHelper.pulse)){
					pulseView.setText(cur.getString(i));
				}else if(cur.getColumnName(i).equalsIgnoreCase(DatabaseHelper.heartRate)){
					heartRateView.setText(cur.getString(i));
				}else if(cur.getColumnName(i).equalsIgnoreCase(DatabaseHelper.respRate)){
					respRateView.setText(cur.getString(i));
				}else if(cur.getColumnName(i).equalsIgnoreCase(DatabaseHelper.stLevel)){
					stLevelView.setText(cur.getString(i));
				}else if(cur.getColumnName(i).equalsIgnoreCase(DatabaseHelper.arrCode)){
					arrCodeView.setText(cur.getString(i));
				}else if(cur.getColumnName(i).equalsIgnoreCase(DatabaseHelper.notes)){
					docNotes = cur.getString(i);
				}else if(cur.getColumnName(i).equalsIgnoreCase(DatabaseHelper.chartFileName)){
					chartFileName = cur.getString(i);
				}
			}	    	   
		}
	}





	private static final int HISTORY_SIZE = 500;
	boolean pauseFlag = false;
	int drawFrequency = 0;
	boolean isLeadV = false;

	private XYPlot plotLeadI = null;
	private SimpleXYSeries seriesLeadI = null;
	private XYPlot plotLeadII = null;
	private SimpleXYSeries seriesLeadII = null;
	private XYPlot plotLeadIII = null;
	private SimpleXYSeries seriesLeadIII = null;
	private XYPlot plotAVF = null;
	private SimpleXYSeries seriesAVF = null;

	public void bindGraphs(){
		plotLeadI = setPlot(plotLeadI,R.id.lead1, 80,255);
		seriesLeadI = new SimpleXYSeries("Lead I");
		seriesLeadI.useImplicitXVals();
		//plotLeadI.addSeries(seriesLeadI, new LineAndPointFormatter(Color.rgb(100, 100, 300), Color.BLUE, null, null));
		plotLeadI.addSeries(seriesLeadI, new LineAndPointFormatter(Color.BLACK, null, null, null));

		plotLeadII = setPlot(plotLeadII,R.id.lead2,100, 150);
		seriesLeadII = new SimpleXYSeries("Lead II");
		seriesLeadII.useImplicitXVals();
		plotLeadII.addSeries(seriesLeadII, new LineAndPointFormatter(Color.GREEN,null, null, null));

		plotLeadIII = setPlot(plotLeadIII,R.id.lead3,0,180);
		seriesLeadIII = new SimpleXYSeries("Lead III");
		seriesLeadIII.useImplicitXVals();
		plotLeadIII.addSeries(seriesLeadIII, new LineAndPointFormatter(Color.BLUE, null, null, null));

		plotAVF = setPlot(plotAVF,R.id.avf,50,150);
		seriesAVF = new SimpleXYSeries("aVF");
		seriesAVF.useImplicitXVals();
		plotAVF.addSeries(seriesAVF, new LineAndPointFormatter(Color.WHITE, null, null, null));

	}

	public XYPlot setPlot(XYPlot plot, int plotId, int minY, int maxY){
		plot = (XYPlot) findViewById(plotId);
		plot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if (pauseFlag)
					pauseFlag = false;
				else
					pauseFlag = true;
				

			}
		});


		// minY = -5 and maxY = 255
		plot.setRangeBoundaries(minY, maxY, BoundaryMode.FIXED);
		plot.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);		

		plot.setDomainStepValue(5);
		plot.setTicksPerRangeLabel(3);
		plot.setDomainLabel("");
		plot.getDomainLabelWidget().pack();
		plot.setRangeLabel("");
		plot.getRangeLabelWidget().pack();
		//applicationPlot2.setMarkupEnabled(true);
		plot.getGraphWidget().setPadding(0, 10, 5, 5);
		plot.getLegendWidget().setMarginBottom(5);

		return plot;
	}


	private Handler mIncomingHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			drawChart(msg.obj);

		}
	};

	private final Messenger mMessenger = new Messenger(mIncomingHandler);
	ReaderThread readerThread = null;

	private void startReaderThread(){

		try {
			File file = new File(this.getFilesDir(), chartFileName);
			Log.w(TAG, "Path is "+file.getPath()+", size "+ file.length());

			FileInputStream inputStream = this.openFileInput(chartFileName);
			readerThread= new ReaderThread(mMessenger, inputStream);
			readerThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	class ReaderThread extends Thread{
		Messenger messenger = null;
		InputStream is = null;
		boolean cont = true;
		ReaderThread(Messenger messenger, InputStream inputStream){
			this.messenger = messenger;
			this.is = inputStream;
		}
		public void run(){
			BluetoothReceiverThread rt = new BluetoothReceiverThread(null, null);
			while(cont){
				byte[] msg;
				msg = rt.getMessage(this.is);
				if ( msg != null){
					try {
						if (!pauseFlag)
							messenger.send(Message.obtain(null, 0, new Response(msg)));
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
			/*
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
						if (!pauseFlag)
							messenger.send(Message.obtain(null, 0, i, 0, 0));

						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
			}*/
		}

		// Stops the thread
		public void cancel() {
			Log.d(TAG,"cont set to false");
			cont = false;
		}

	}

	// Disconnect from the box
	public void onDestroy() {
		readerThread.cancel();
		super.onDestroy();
	}

	public  void drawChart(Object obj){
		if(drawFrequency++ > 125){
			if (!pauseFlag){
				plotLeadI.redraw();
				plotLeadII.redraw();
				plotLeadIII.redraw();
				plotAVF.redraw();
			}
			drawFrequency = 0;
		}

		int i = 0;
		Response resp = (Response)obj;

		// Series LeadI
		i = resp.getEcgIWaveAplitude();
		if (i > 0){
			if (seriesLeadI.size() > HISTORY_SIZE) {
				seriesLeadI.removeFirst();
			}
			seriesLeadI.addLast(null, i);
		}

		// Series LeadII
		i = resp.getEcgIIWaveAplitude();
		if (i > 0){
			if (seriesLeadII.size() > HISTORY_SIZE) {
				seriesLeadII.removeFirst();
			}
			seriesLeadII.addLast(null, i);
		}

		// Series LeadIII
		i = resp.getEcgIIIWaveAplitude();
		if (i > 0){
			if (seriesLeadIII.size() > HISTORY_SIZE) {
				seriesLeadIII.removeFirst();
			}
			seriesLeadIII.addLast(null, i);
		}

		// Series avF
		i = resp.getEcgAVFWaveAplitude();
		if (i > 0){
			if (seriesAVF.size() > HISTORY_SIZE) {
				seriesAVF.removeFirst();
			}
			seriesAVF.addLast(null, i);
		}
	}

	public void bindButtons(){
		// Save
		save.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				DatabaseHelper dbHelper = new DatabaseHelper(ReportReviewActivity.this);				
				// report Id is auto generated, so send just a null				
				dbHelper.updateReport( reportId, docNotes);
				finish();
			}
		});

		// Notes
		notes.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReportReviewActivity.this, NotesActivity.class);
				intent.putExtra("Notes", docNotes);
				startActivityForResult(intent, ReportActivity.INVOKE_NOTES_ACTIVITY);	
			}
		});
	}

	// Method to handle the response from the return of started activities.
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(( requestCode == ReportActivity.INVOKE_NOTES_ACTIVITY) && (resultCode == RESULT_OK)){
			Bundle bundle = data.getExtras();
			if (bundle != null){
				docNotes = bundle.getString("Notes");	
			} 
		}
	}


}