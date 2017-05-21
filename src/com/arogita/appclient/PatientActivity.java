package com.arogita.appclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts.Data;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.arogita.appclient.db.DatabaseHelper;
import com.arogita.appclient.patient.PatientProfile;
import com.example.sampleappclient.R;

public class PatientActivity extends Activity {
	public static String TAG = "PatientActivity";
	
	public static int REPORT_NEW = 0;
	public static int REPORT_VIEW = 1;
	public static int PATIENT_EDIT = 2;
	
	public static String FirstName = "FirstName";
	public static String LastName = "LastName";
	public static String UID = "ID";
	public static String DOB = "DateOfBirth";
	public static String GENDER = "Gender";
	
	
	TextView fName=null;
	TextView lName=null;
	String uId=null;
	TextView dateOfBirth = null;
	TextView gender = null;
	
	boolean newPatient = true;
	KeyListener keyListener = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient);

		fName = (TextView)findViewById(R.id.fname);
		lName = (TextView)findViewById(R.id.lname);
		dateOfBirth = (TextView)findViewById(R.id.dob);
		gender = (TextView)findViewById(R.id.gender);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null){
			Log.d(TAG, "Existing patient flow in Patient Activity");
			newPatient = false;
			fName.setText(bundle.getString(FirstName));
			lName.setText(bundle.getString(LastName));
			uId = bundle.getString(UID);
			dateOfBirth.setText(bundle.getString(DOB));
			gender.setText(bundle.getString(GENDER));
		}

		String profileName = fName.getText() + " " + lName.getText();
		getActionBar().setTitle(profileName +  "'s Profile");
		
		Button viewHistory = (Button)findViewById(R.id.view_history);
		viewHistory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(PatientActivity.this, ReportSearchActivity.class);
				intent.putExtra(UID, uId);
				startActivityForResult(intent, REPORT_VIEW);
			}
		});
		Button newTest = (Button)findViewById(R.id.new_test);
		newTest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(PatientActivity.this, ReportActivity.class);
				intent.putExtra(UID, uId);
				startActivityForResult(intent, REPORT_NEW);
				
			}
		});
		
	}
	
	  @Override
	   public boolean onCreateOptionsMenu(Menu menu) {
	       // Inflate the menu items for use in the action bar
	       MenuInflater inflater = getMenuInflater();
	       inflater.inflate(R.menu.menu_edit, menu);
	       
	       MenuItem addItem = menu.findItem(R.id.action_edit);
	       addItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {	
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					// TODO Auto-generated method stub
					callPatientEditAction();
					return false;
					
				}
			});
	       
	       return true;
	          
	   }
	  
	  public void callPatientEditAction(){
		  	Intent intent = new Intent(PatientActivity.this, PatientEditActivity.class);
			intent.putExtra(FirstName, fName.getText().toString());
			intent.putExtra(LastName, lName.getText().toString());
			intent.putExtra(UID, uId);
			intent.putExtra(DOB, dateOfBirth.getText().toString());
			intent.putExtra(GENDER, gender.getText().toString());
	    	startActivityForResult(intent, PATIENT_EDIT);
	  }
	
	
	
	 @Override
	   public void onActivityResult(int requestCode, int resultCode, Intent data) {
         //super.onActivityResult(requestCode, resultCode, data);
		  if (requestCode == PATIENT_EDIT){
			  if (data != null){				  					  	
				  	fName.setText(data.getStringExtra(FirstName));				  	
					lName.setText(data.getStringExtra(LastName));
					dateOfBirth.setText(data.getStringExtra(DOB));
					gender.setText(data.getStringExtra(GENDER));
			  }
		  }
	   }

}
