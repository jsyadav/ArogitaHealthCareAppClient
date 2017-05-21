package com.arogita.appclient;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.arogita.appclient.bluetooth.BluetoothService;
import com.arogita.appclient.db.DatabaseHelper;
import com.arogita.appclient.protocol.RequestCommand;
import com.arogita.appclient.protocol.Response;
import com.arogita.appclient.report.ReportProfile;
import com.example.sampleappclient.R;

public class ReportActivity extends Activity {

	private static final String TAG = "ReportActivity";

	
	private boolean inFahrenheit = false;
	private BluetoothService btService = null;
	private LinkedBlockingQueue<Integer> sendingQueue = null;
	boolean isTempOn = false;
	boolean isSpo2On = false;
	boolean isNibpOn = false;
	boolean isEcgOn = false;

	private String patientId = null;
	private String docNotes = "";
	
	private TextView statusView;
	private TextView heartRateView;
	private TextView respRateView;
	private TextView stLevelView;
	private TextView arrCodeView;
	private TextView nibpView;
	private TextView saturationView;
	private TextView pulseView;
	private TextView tempView;
	
	// button
	private Button save;
	private Button notes;
	private Button reset;
	private Button settings;
	
	public static int INVOKE_NOTES_ACTIVITY = 0;
	public static int INVOKE_SETTINGS_ACTIVITY = 1;
	public static int INVOKE_PLOT_ZOOM_ACTIVITY = 2;
	
	private int tempCount = 0;
	private int spo2Count = 0;
	private int ecgCount = 0;
	private int waveSaveDuration = 15;
	public  String filename = null;
	private FileOutputStream outputStream = null;
	private boolean ecgCaptureStart = false;
	
	

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
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
		reset = (Button)findViewById(R.id.reset);
		notes = (Button)findViewById(R.id.notes);
		settings = (Button)findViewById(R.id.settings);
		
		bindButtons();
		bindGraphs();
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null){
			patientId = bundle.getString(PatientActivity.UID);
		}
		
		btService = new BluetoothService(this);		
		btService.checkBTState();
		

		try {
			long time = System.currentTimeMillis();
			filename = String.valueOf(time);
			outputStream = openFileOutput(filename, Context.MODE_PRIVATE);		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startTest();

	}

	
	private static final int HISTORY_SIZE = 500;
	boolean pauseFlag = false;
	boolean zoomFlagI = false;
	boolean zoomFlagII = false;
	boolean zoomFlagIII = false;
	boolean zoomFlagAVF = false;
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
		plotLeadI.addSeries(seriesLeadI, new LineAndPointFormatter(Color.BLACK, null, null, null));
		
		plotLeadII = setPlot(plotLeadII,R.id.lead2,100, 150);
		seriesLeadII = new SimpleXYSeries("Lead II");
		seriesLeadII.useImplicitXVals();
		plotLeadII.addSeries(seriesLeadII, new LineAndPointFormatter(Color.GREEN, null, null, null));
		
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
				/*
				if (pauseFlag)
					pauseFlag = false;
				else
					pauseFlag = true;
				*/
				XYPlot plot = (XYPlot)v;
				Intent intent = new Intent (ReportActivity.this,EcgWaveActivity.class);
				Set<XYSeries> series = plot.getSeriesSet();
				String title = series.iterator().next().getTitle();
				if (title.equals("Lead I")){
					zoomFlagI = true;
				}else if (title.equals("Lead II")){
					zoomFlagII = true;
				}else if (title.equals("Lead III")){
					zoomFlagIII = true;
				}else {
					zoomFlagAVF = true;
				}
				Log.d(TAG, "Title is " + title);
				intent.putExtra("Label", title);
				startActivityForResult(intent, INVOKE_PLOT_ZOOM_ACTIVITY);
				
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

	// Messages that comes from the receiver thread	
	public final static int DEVICE_CONNECT = 0x00;
	public final static int DEVICE_ALERT = 0x10;
	
	public final static int ECG_WAVE_MSG = 0x01;
	public final static int ECG_PARAM_MSG = 0x02;
	public final static int NIBP_PARAM_MSG = 0x03;
	public final static int SPO2_PARAM_MSG = 0x04;
	public final static int TEMP_PARAM_MSG = 0x05;
	public final static int SOFTWARE_PARAM_MSG = 0xFC;
	public final static int HARDWARE_PARAM_MSG = 0xFD;
	public final static int SPO2_WAVE_MSG = 0xFE;
	public final static int RESP_WAVE_MSG = 0xFF;
	

	// Handling incoming messages from the receiver thread
	private Handler mIncomingHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == DEVICE_CONNECT){
				statusView.setText((String)msg.obj);
				Toast.makeText(ReportActivity.this, (String)msg.obj, Toast.LENGTH_LONG).show();
				return;
			}else if (msg.what == DEVICE_ALERT){
				String [] str = (String[])msg.obj;
				AlertMesg(str[0], str[1]);
				return;
			}
			
			Response resp = (Response)msg.obj;
			String log = "";
			
				
			switch (msg.what) {
			case ECG_PARAM_MSG:
				// ECG Status
				log += "ECG, Sig Intensity: " + resp.getEcgSigIntensity() +
				", lead status: "+resp.getEcgLeadStatus()+
				", wave gain: " + resp.getEcgWaveGain()+
				", filter mode: "+ resp.getEcgFilterMode()+
				", lead mode: "+ resp.getEcgLeadMode();
				if (!resp.getEcgLeadStatus().equals("Normal")){
					statusView.setTextColor(Color.RED);
				}else{
					statusView.setTextColor(Color.BLACK);
				}
				statusView.setText(log);
				//Param
				heartRateView.setText(resp.getEcgHeartRate());				
				respRateView.setText(resp.getEcgRespRate());				
				stLevelView.setText(resp.getEcgStLevel());				
				arrCodeView.setText(resp.getEcgArrCode());
				/* Auto turned off feature*/
				if (resp.getEcgLeadStatus().equals("Normal")){
					ecgCaptureStart = true;
					if (ecgCount++ > waveSaveDuration){
						endEcgTest();
						ecgCount = 0;
					}	
				}/**/
				break;
			case SPO2_PARAM_MSG:
				// SPO2 status
				log += "SPO2, status: "+ resp.getSpo2Status();
				if (!resp.getSpo2Status().equals("Normal")){
					statusView.setTextColor(Color.RED);
				}else{
					statusView.setTextColor(Color.BLACK);
				}
				statusView.setText(log);
				// Param
				saturationView.setText(resp.getSaturationLevel());
				pulseView.setText(resp.getPulse());
				/* Auto turned off feature*/
				if (resp.getSpo2Status().equals("Normal")){
					if (spo2Count++ > 5){
						endSpo2Test();
						spo2Count = 0;
					}	
				}/**/
			
				
				break;
			case TEMP_PARAM_MSG:
				log += "Temp, status: "+ resp.getTempStatus();
				if (!resp.getTempStatus().equals("Normal")){
					statusView.setTextColor(Color.RED);
				}else{
					statusView.setTextColor(Color.BLACK);
				}
				statusView.setText(log);
				// Param					
				String temp = resp.getTemp1();
				if (temp.equalsIgnoreCase("--.--"))
					break;
				if (inFahrenheit == false){
					tempView.setText(resp.getTemp1() +" C");
				}else{
					tempView.setText(centigradeToFahrenite(resp.getTemp1()) +" F");					
				}
				/* Auto turned off feature*/
				if (resp.getTempStatus().equals("Normal")){
					if (tempCount++ > 5){
						endTempTest();
						tempCount = 0;
					}	
				}/**/

				break;
			case NIBP_PARAM_MSG:
				// NIBP Status
				log += "BP, Patient mode: "+ resp.getNibpPatientMode()+
				", test result: "+ resp.getNibpTestResult()+
				", cuff pressure: " + resp.getNibpCuffPressure();
				if (resp.getNibpTestResult().equals("Finished")||
						resp.getNibpTestResult().equals("Running")){
					statusView.setTextColor(Color.BLACK);
				}else{
					statusView.setTextColor(Color.RED);
				}
				statusView.setText(log);
				//Param
				String [] msgStr = resp.getNibpValue();
				nibpView.setText(msgStr[0]+"/"+msgStr[1]);
				
				/* Auto turned off feature */
				if (resp.getNibpTestResult().equals("Finished") &&
						!msgStr[0].equals(msgStr[1]) &&
						!msgStr[0].equals("0") &&
						!msgStr[1].equals("0")){
					// Test end
					endNIBPTest();
					nibpView.setText(msgStr[0]+"/"+msgStr[1]);
					Log.d (TAG,resp.getNibpTestResult()+"--" + msgStr[0]+"/"+msgStr[1]);
				}/**/

				break;
			case HARDWARE_PARAM_MSG:
				AlertMesg("Hardware Version ", resp.getVersion());
				break;
			case SOFTWARE_PARAM_MSG:		
				AlertMesg("Software Version ", resp.getVersion());
				break;
			case ECG_WAVE_MSG:
				byte[] ecgWave = resp.getCommand();
				if (ecgCaptureStart){
					persistEcgWave(ecgWave);
				}
				
				int i= 0;
				if (zoomFlagI || zoomFlagII ||
						zoomFlagIII || zoomFlagAVF){
					//Log.w(TAG, "Getting the ECG Wave message, thread id " +Thread.currentThread().getId() + ", "+ msgStr);
					Intent intent = new Intent(EcgWaveActivity.ECG_WAVE_MSG);
					if (zoomFlagI)
						intent.putExtra("message", resp.getEcgIWaveAplitude());
					else if (zoomFlagII)
						intent.putExtra("message", resp.getEcgIIWaveAplitude());
					else if (zoomFlagIII)
						intent.putExtra("message", resp.getEcgIIIWaveAplitude());
					else if (zoomFlagAVF && isLeadV == false)
						intent.putExtra("message", resp.getEcgAVFWaveAplitude());
					else if (zoomFlagAVF && isLeadV == true)
						intent.putExtra("message", resp.getEcgVWaveAplitude());
					LocalBroadcastManager.getInstance(ReportActivity.this).sendBroadcast(intent);
					
				}
				
				if(drawFrequency++ > 125){
					if (!pauseFlag){
						plotLeadI.redraw();
						plotLeadII.redraw();
						plotLeadIII.redraw();
						plotAVF.redraw();
					}
					drawFrequency = 0;
				}
				
				i = resp.getEcgIWaveAplitude();
				if (i> 250){
					Log.e(TAG, "Got ecg value > 250");
				}
				if (i > 0){
					if (seriesLeadI.size() > HISTORY_SIZE) {
						seriesLeadI.removeFirst();
					}
					seriesLeadI.addLast(null, i);
					// Don't call draw for each record available.
					// It will be called when RESP_WAVE_MSG comes
					// with ECG.
					//plotLeadI.redraw();
			
				}
				i = resp.getEcgIIWaveAplitude();
				if (i > 0){
						if (seriesLeadII.size() > HISTORY_SIZE) {
							seriesLeadII.removeFirst();
						}
						seriesLeadII.addLast(null, i);
						// Don't call draw for each record available.
						// It will be called when RESP_WAVE_MSG comes
						// with ECG.
						//plotLeadII.redraw();				
				}
				i = resp.getEcgIIIWaveAplitude();
				if (i> 250){
					Log.e(TAG, "Got ecg value > 250");
				}
				if (i > 0){
					if (seriesLeadIII.size() > HISTORY_SIZE) {
						seriesLeadIII.removeFirst();
					}
					seriesLeadIII.addLast(null, i);
					// Don't call draw for each record available.
					// It will be called when RESP_WAVE_MSG comes
					// with ECG.
					//plotLeadIII.redraw();
			
				}
				if (isLeadV){
					i = resp.getEcgVWaveAplitude();
				}else{
					i = resp.getEcgAVFWaveAplitude();
				}
				if (i > 0){
						if (seriesAVF.size() > HISTORY_SIZE) {
							seriesAVF.removeFirst();
						}
						seriesAVF.addLast(null, i);
						// Don't call draw for each record available.
						// It will be called when RESP_WAVE_MSG comes
						// with ECG.
						//plotAVF.redraw();				
				}
				break;
			case RESP_WAVE_MSG:
				/*
				i =  resp.getWaveAplitude();
				if (i> 250){
					Log.e(TAG, "Got resp value > 250");
				}
				if (i > 0 ){
					if (respHistorySeries.size() > HISTORY_SIZE) {
						respHistorySeries.removeFirst();
					}
					//respHistorySeries.addLast(null, i); Don't plot for now
					if (!pauseFlag1)
						applicationPlot1.redraw();
					if (!pauseFlag2)
						applicationPlot2.redraw();
				}*/
				break;
			case SPO2_WAVE_MSG:	
				/*
				i =  resp.getWaveAplitude();
				if (i> 100){
					Log.e(TAG, "Got sp2 value > 100");
				}
				if (i > 0){
					if (spo2HistorySeries.size() > HISTORY_SIZE) {
						spo2HistorySeries.removeFirst();
					}
					spo2HistorySeries.addLast(null,i);	       
					applicationPlot1.redraw();
				}		*/		
				break;

			default:
				super.handleMessage(msg);
			}

		}
	};

	private final Messenger mMessenger = new Messenger(mIncomingHandler);

	public void bindButtons(){
		// Save		
		save.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				DatabaseHelper dbHelper = new DatabaseHelper(ReportActivity.this);
				ReportProfile rf = createReportProfie();
				dbHelper.insertReport(rf);
				AlertMesgWithFinish("Report saving status", "Saving in cloud isn't available" );				
			}
		});

		// Reset
		reset.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				String dash = "------";
				//ECG				
				heartRateView.setText(dash);				
				respRateView.setText(dash);				
				stLevelView.setText(dash);				
				arrCodeView.setText(dash);
				// SPO2				
				saturationView.setText(dash);				
				pulseView.setText(dash);
				//Temp				
				tempView.setText(dash);
				//NIBP				
				nibpView.setText(dash);
				
				//ecgHistorySeries;
				//spo2HistorySeries;
				//respHistorySeries
				if (tempLayoutView != null){
					tempLayoutView.setBackgroundResource(R.drawable.normalborder);
					isTempOn = false; 
					tempCount = 0;
				}
				if (nibpLayoutView != null){
					nibpLayoutView.setBackgroundResource(R.drawable.normalborder);
					isNibpOn = false;
				}
				if (spo2LayoutView != null){
					spo2LayoutView.setBackgroundResource(R.drawable.normalborder);
					isSpo2On = false;
				}
				if (ecgLayoutView != null){
					ecgLayoutView.setBackgroundResource(R.drawable.normalborder);
					isEcgOn = false;
				}
			}
		});

		// Notes
		notes.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReportActivity.this, NotesActivity.class);
				intent.putExtra("Notes", docNotes);
				startActivityForResult(intent, INVOKE_NOTES_ACTIVITY);	
			}
		});


		// Settings
		settings.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {

				final String [] options = {"Settings", "Hardware version", "Software version"};
				new AlertDialog.Builder(ReportActivity.this)
				.setTitle("Options " )
				.setItems(options, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Log.w(TAG,"your choice of setting mode is " + arg1);
						switch(arg1){
						case 0:
							Intent intent = new Intent(ReportActivity.this,SettingsActivity.class);
							startActivityForResult(intent, INVOKE_SETTINGS_ACTIVITY);
							break;
						case 1:
							sendMessageToSenderThread(RequestCommand.HardwareVersionInquiry);
							break;
						case 2:
							sendMessageToSenderThread(RequestCommand.SoftwareVersionInquiry);
							break;		
						}
					}
				}).show();
			}});
	}
	
	private ReportProfile createReportProfie() {
		// TODO Auto-generated method stub
		String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
		Log.d(TAG,"Inserting report for patient "+ patientId+", date " + currentDateTimeString);
		ReportProfile rf = new ReportProfile(currentDateTimeString,patientId,null);
		// HR				
		rf.setHeartRate(heartRateView.getText().toString());								
		rf.setRespRate(respRateView.getText().toString());				
		rf.setStLevel(stLevelView.getText().toString());								
		rf.setArrCode(arrCodeView.getText().toString());

		//SPO2
		rf.setSaturation(saturationView.getText().toString());
		rf.setPulse(pulseView.getText().toString());
		
		// NIBP
		rf.setNibp(nibpView.getText().toString());
		
		// TEMP
		rf.setTemp(tempView.getText().toString());
		
		// Notes
		rf.setNotes(docNotes);
		
		// Chartfile
		rf.setChartFileName(filename);
		return rf;
	}

	View tempLayoutView = null;
	View nibpLayoutView = null;
	View spo2LayoutView = null;
	View ecgLayoutView = null;
	
	
	public void tempLayoutClicked(View v){
		tempLayoutView = v;
		if(isTempOn == false){
			v.setBackgroundResource(R.drawable.clickedborder);
			sendMessageToSenderThread(RequestCommand.TEMP_Test_Enable);
			isTempOn = true;
		}else if(isTempOn = true){
			endTempTest();
		}

	}

	// nibp button handler
	public void nibpLayoutClicked(View v){
		nibpLayoutView = v;
		if (isNibpOn == false){
			v.setBackgroundResource(R.drawable.clickedborder);
			sendMessageToSenderThread(RequestCommand.NIBP_Test_Enable);	
			isNibpOn = true;
		}else if( isNibpOn == true){
			endNIBPTest();
			
		}
	}

	// spo2 button handler
	public void spo2LayoutClicked(View v){
		spo2LayoutView = v;
		if (isSpo2On == false){
			v.setBackgroundResource(R.drawable.clickedborder);
			sendMessageToSenderThread(RequestCommand.SPO2_Test_Enable);
			// Disable this data 
			//sendMessageToSenderThread(RequestCommand.SPO2_WaveOutputEnable);
			isSpo2On = true;
		}else if( isSpo2On == true){
			sendMessageToSenderThread(RequestCommand.SPO2_Test_Disable);
			sendMessageToSenderThread(RequestCommand.SPO2_WaveOutputDisable);
			v.setBackgroundResource(R.drawable.completedborder);
			isSpo2On = false;
		}
	}

	// nibp button handler
	public void ecgLayoutClicked(View v){
		ecgLayoutView = v;
		if(isEcgOn == false){
			v.setBackgroundResource(R.drawable.clickedborder);
			sendMessageToSenderThread(RequestCommand.ECG_Test_Enable);
			sendMessageToSenderThread(RequestCommand.ECG_WaveOutputEnable);
			// Disable this data 
			//sendMessageToSenderThread(RequestCommand.RESP_WaveOutputEnable);
			isEcgOn = true;
		}else if (isEcgOn == true){			
			sendMessageToSenderThread(RequestCommand.ECG_Test_Disable);
			sendMessageToSenderThread(RequestCommand.ECG_WaveOutputDisable);
			sendMessageToSenderThread(RequestCommand.RESP_WaveOutputDisable);
			v.setBackgroundResource(R.drawable.completedborder);
			isEcgOn = false;
		}
	}
	
	
	public void endTempTest(){
		sendMessageToSenderThread(RequestCommand.TEMP_Test_Disable);
		isTempOn = false;
		tempLayoutView.setBackgroundResource(R.drawable.completedborder);
	}
	
	public void endNIBPTest(){
		// Don't send the disable command
		// The test automatically stop.
		//sendMessageToSenderThread(RequestCommand.NIBP_Test_Disable);
		isNibpOn = false;
		nibpLayoutView.setBackgroundResource(R.drawable.completedborder);
		
	}
	
	public void endSpo2Test(){
		sendMessageToSenderThread(RequestCommand.SPO2_Test_Disable);
		sendMessageToSenderThread(RequestCommand.SPO2_WaveOutputDisable);
		isSpo2On = false;
		spo2LayoutView.setBackgroundResource(R.drawable.completedborder);
	}
	
	public void endEcgTest(){
		sendMessageToSenderThread(RequestCommand.ECG_Test_Disable);
		sendMessageToSenderThread(RequestCommand.ECG_WaveOutputDisable);
		sendMessageToSenderThread(RequestCommand.RESP_WaveOutputDisable);
		isEcgOn = false;
		ecgLayoutView.setBackgroundResource(R.drawable.completedborder);
	}


	// Method to handle the response from the return of started activities.
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == INVOKE_SETTINGS_ACTIVITY){
			loadPref();
		}else if(( requestCode == INVOKE_NOTES_ACTIVITY) && (resultCode == RESULT_OK)){
			Bundle bundle = data.getExtras();
			if (bundle != null){
				docNotes = bundle.getString("Notes");	
			} 
		}else if (requestCode == INVOKE_PLOT_ZOOM_ACTIVITY){
			zoomFlagI= false;
			zoomFlagII = false;
			zoomFlagIII = false;
			zoomFlagAVF = false;
		}
	}

	private void loadPref() {
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		// Set the temperature unit
		String tempPref = mySharedPreferences.getString("tempPref", "Fahrenheit");
		Log.i(TAG, "Temperature unit = "+ tempPref);
		if (tempPref.equals("Fahrenheit")){
			inFahrenheit = true;
		}else if (tempPref.equals("Centigrade")){
			inFahrenheit = false;
		}

		if (btService.isConnected() == false){
			return;
		}
		// Set the BP patient mode
		String bpPatientModePref = mySharedPreferences.getString("bpPatientModePref","Adult");
		Log.i(TAG, "NIBP patient mode = "+ bpPatientModePref );
		if (bpPatientModePref.equals("Adult")){
			sendMessageToSenderThread(RequestCommand.NIBP_Patient_ModeAdult);
		}else if (bpPatientModePref.equals("Child")){
			sendMessageToSenderThread(RequestCommand.NIBP_Patient_ModeChild);
		}else if (bpPatientModePref.equals("Neonate")){
			sendMessageToSenderThread(RequestCommand.NIBP_Patient_ModeNeonate);
		}

		// Set the ECG filter mode
		String ecgFilterModePref = mySharedPreferences.getString("ecgFilterModePref","Monitor");
		Log.i(TAG, "ECG Filter mode = "+ ecgFilterModePref );
		if (ecgFilterModePref.equals("Operation")){
			sendMessageToSenderThread(RequestCommand.ECG_FilterModeOperation);
		}else if (ecgFilterModePref.equals("Monitor")){
			sendMessageToSenderThread(RequestCommand.ECG_FilterModeMonitor);
		}else if (ecgFilterModePref.equals("Diagnose")){
			sendMessageToSenderThread(RequestCommand.ECG_FilterModeDiagnose);
		}


		// Set the ECG wave gain
		String ecgWaveGain = mySharedPreferences.getString("ecgWaveGainPref", "x1");
		Log.i(TAG, "ECG wave gain = "+ ecgWaveGain );
		if (ecgWaveGain.equals("x.25")){
			sendMessageToSenderThread(RequestCommand.ECG_WaveGainxQuarter);
		}else if (ecgWaveGain.equals("x.5")){
			sendMessageToSenderThread(RequestCommand.ECG_WaveGainxHalf);
		}else if (ecgWaveGain.equals("x1")){
			sendMessageToSenderThread(RequestCommand.ECG_WaveGainxOnce);
		}else if (ecgWaveGain.equals("x2")){
			sendMessageToSenderThread(RequestCommand.ECG_WaveGainxTwice);
		}

		// Set the ECG wave gain
		isLeadV = false;
		seriesAVF.setTitle("aVF");
		String ecgLead3Lead5SwitchPref = mySharedPreferences.getString("ecgLead3Lead5SwitchPref", "Lead5_V");
		Log.i(TAG, "NIBP patient mode = "+ ecgLead3Lead5SwitchPref );
		if (ecgLead3Lead5SwitchPref.equals("Lead3_I")){
			sendMessageToSenderThread(RequestCommand.ECG_Lead3_5_Switch1);
		}else if (ecgLead3Lead5SwitchPref.equals("Lead3_II")){
			sendMessageToSenderThread(RequestCommand.ECG_Lead3_5_Switch2);
		}else if (ecgLead3Lead5SwitchPref.equals("Lead3_III")){
			sendMessageToSenderThread(RequestCommand.ECG_Lead3_5_Switch3);
		}else if (ecgLead3Lead5SwitchPref.equals("Lead5_V")){
			isLeadV = true;
			seriesAVF.setTitle("LeadV");
			sendMessageToSenderThread(RequestCommand.ECG_Lead3_5_Switch4);
		}

		// Set the Resp wave gain
		String respWaveGain = mySharedPreferences.getString("respWaveGainPref", "x1");
		Log.i(TAG, "RESP wave gain = "+ respWaveGain );
		if (ecgWaveGain.equals("x.25")){
			sendMessageToSenderThread(RequestCommand.RESP_WaveGainQuater);
		}else if (ecgWaveGain.equals("x.5")){
			sendMessageToSenderThread(RequestCommand.RESP_WaveGainHalf);
		}else if (ecgWaveGain.equals("x1")){
			sendMessageToSenderThread(RequestCommand.RESP_WaveGainOnce);
		}else if (ecgWaveGain.equals("x2")){
			sendMessageToSenderThread(RequestCommand.RESP_WaveGainTwice);
		}
		
		// How long the wave data to be stored.
		String waveSaveWindow = mySharedPreferences.getString("waveSaveWindowPref", "15");
		Log.i(TAG, "Wave Save Window  = "+ waveSaveWindow );
		waveSaveDuration = Integer.valueOf(waveSaveWindow);
	}

	// Convenience method to convert the temperature unit.
	public String centigradeToFahrenite(String tempCentStr){
		Double tempCent = Double.valueOf(tempCentStr.split(" ")[0]);
		Double tempFahre = (1.8*tempCent) + 32;
		DecimalFormat df2 = new DecimalFormat("###.##");		
		String temp = String.valueOf(df2.format(tempFahre));
		return temp;
	}


	// Disable all the reading when start
	public void sendDisableCommandstoServer(){
		sendMessageToSenderThread(RequestCommand.NIBP_Test_Disable);
		sendMessageToSenderThread(RequestCommand.ECG_Test_Disable);
		sendMessageToSenderThread(RequestCommand.SPO2_Test_Disable);
		sendMessageToSenderThread(RequestCommand.TEMP_Test_Disable);

		// Disable the wave 
		sendMessageToSenderThread(RequestCommand.ECG_WaveOutputDisable);
		sendMessageToSenderThread(RequestCommand.SPO2_WaveOutputDisable);
		sendMessageToSenderThread(RequestCommand.RESP_WaveOutputDisable);

		isTempOn = false; isNibpOn = false;
		isNibpOn = false; isSpo2On = false;
	}

	// Called by Sender Thread to give its queue
	public void setSenderQueue(LinkedBlockingQueue<Integer> queue){
		this.sendingQueue = queue;
	}

	// Enqueue the message in the sender's queue
	private void sendMessageToSenderThread(int what) {
		// Below this line we want connection is established 
		// with the server
		if (btService.isConnected() == false){
			AlertMesg("Connection Error", "Wait for device to connect");
			return;
		}
		if (sendingQueue == null) {
			Log.d(TAG, "No clients registered.");
			AlertMesg("Sender Error" , "Sender queue/thread not active");
			return;
		}
		try {
			sendingQueue.put(what);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// Disconnect from the box
	public void onDestroy() {
		stopTest();
		super.onDestroy();
	}
	
	public void startTest(){
		Toast.makeText(ReportActivity.this, "Connecting to box, please wait..", Toast.LENGTH_LONG).show();
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.w(TAG, "Starting the test, thread id = " + Thread.currentThread().getId());
				if(btService.connectToServer(mMessenger)){
					Log.i(TAG,"Connected to the BT device, thread id = " + Thread.currentThread().getId());
					sendDisableCommandstoServer();
					loadPref();
					//statusView.setText("Connected");
				}else{
					Log.w(TAG,"Unable to connected to the BT device, thread id =  " + Thread.currentThread().getId());
				}
			}}.start();
			
	}

	public void stopTest(){
		Log.w(TAG, "Stopping the test");
		if (btService != null && btService.isConnected()){
			sendDisableCommandstoServer();
			btService.close();	
		}
		if (outputStream != null){
			try {
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}			
		statusView.setText("Disconnected");
	}

	

	public void AlertMesg( String title, String message ){

		new AlertDialog.Builder(ReportActivity.this)
		.setTitle( title)
		.setMessage( message )
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				//finish();
			}
		}).show();

	}
	public void AlertMesgWithFinish( String title, String message ){

		new AlertDialog.Builder(ReportActivity.this)
		.setTitle( title)
		.setMessage( message )
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		}).show();

	}

	public void persistEcgWave(byte[] i){
		try {
			if (outputStream!= null){
				outputStream.write(i);
				outputStream.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	


}