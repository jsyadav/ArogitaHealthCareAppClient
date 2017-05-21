package com.arogita.appclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.arogita.appclient.db.DatabaseHelper;
import com.arogita.appclient.patient.PatientProfile;
import com.example.sampleappclient.R;

public class PatientEditActivity extends Activity {

	public static String TAG = "PatientEditActivity";

	EditText fName=null;
	EditText lName=null;
	Button dob = null;
	RadioGroup genderGroup = null;
	String genderString = null;
	boolean newPatient = true;
	String uId = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_edit);
		
		fName = (EditText)findViewById(R.id.fname);
		lName = (EditText)findViewById(R.id.lname);
		dob = (Button)findViewById(R.id.dob);
		genderGroup = (RadioGroup)findViewById(R.id.genderGroup);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null){
			newPatient = false;
			uId = bundle.getString(PatientActivity.UID);			
			fName.setText(bundle.getString(PatientActivity.FirstName));
			lName.setText(bundle.getString(PatientActivity.LastName));
			dob.setText(bundle.getString(PatientActivity.DOB));
			genderString = bundle.getString(PatientActivity.GENDER);
			Log.w(TAG, uId +"-"+fName.getText() + "-" + genderString +"-"+ dob);
			RadioButton maleButton = (RadioButton)findViewById(R.id.male);
			if (genderString.equalsIgnoreCase(maleButton.getText().toString())){
				genderGroup.check(R.id.male);
			}else {
				genderGroup.check(R.id.female);
			}
			
		}
		dob = (Button)findViewById(R.id.dob);
		dob.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DatePickerFragment((Button)v);
				newFragment.show(getFragmentManager(), "datePicker");
			}
		});


		Button save = (Button)findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatabaseHelper dbHelper = new DatabaseHelper(PatientEditActivity.this);
				int gen = genderGroup.getCheckedRadioButtonId();
				PatientProfile pp = new PatientProfile(fName.getText().toString(), 
						lName.getText().toString(), dob.getText().toString(), getGenderStringFromId(gen) , uId );
				if (newPatient){
					long ret = dbHelper.insertPatient(pp);
					if (ret < 0){
						new AlertDialog.Builder(PatientEditActivity.this)
						.setTitle( "DB Error")
						.setMessage( "Insert failed while adding patient" )
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {

							}
						}).show();
					}
					uId = dbHelper.getUid(pp);

				}else {
					dbHelper.updatePatient(pp);
				}
				Intent intent = new Intent();
				intent.putExtra(PatientActivity.FirstName, fName.getText().toString());
				intent.putExtra(PatientActivity.LastName, lName.getText().toString());
				intent.putExtra(PatientActivity.UID, uId);
				intent.putExtra(PatientActivity.DOB, dob.getText().toString());
				intent.putExtra(PatientActivity.GENDER, getGenderStringFromId(gen));
				Log.d(TAG, "Sending the edited data " + fName.getText());
				setResult(RESULT_OK, intent);
				finish();

			}
		});

	}
	
	public String getGenderStringFromId(int i){
		RadioButton button = (RadioButton)findViewById(i);
		return button.getText().toString();
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.
				INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		return true;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_patient, menu);
		return true;
	}

	
}
