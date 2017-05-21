package com.arogita.appclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.arogita.appclient.db.DatabaseHelper;
import com.arogita.appclient.patient.PatientProfile;
import com.example.sampleappclient.R;

public class PatientSearchActivity extends Activity {
		private static final String TAG = "PatientSearchActivity";
		
		public static int PATIENT_ADD = 0;
		public static int PATIENT_VIEW = 1;
		// List view
	   private ListView lv;	    
	   // Listview Adapter
	   ArrayAdapter<PatientProfile> adapter;
	   // SearchView
	   SearchView searchView;

	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.activity_patient_search);
	       
	       getActionBar().setTitle("Patient Search");
	        
	       lv = (ListView) findViewById(R.id.list_view);
	       lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					Log.d(TAG, "Count in the view "+ arg0.getCount()+", arg2 = "+ arg2+", arg3 = "+arg3);
					PatientProfile pp = (PatientProfile)arg0.getItemAtPosition(arg2); 
					Intent intent = new Intent(PatientSearchActivity.this, PatientActivity.class);
					intent.putExtra(PatientActivity.FirstName, pp.getFirstName());
					intent.putExtra(PatientActivity.LastName, pp.getLastName());
					intent.putExtra(PatientActivity.UID, pp.getId());
					intent.putExtra(PatientActivity.DOB, pp.getDob());
					intent.putExtra(PatientActivity.GENDER, pp.getGender());
			    	startActivityForResult(intent, PATIENT_VIEW);
				}
	       });	       
	       //getActionBar().setDisplayShowTitleEnabled(false);
	       //this.deleteDatabase("medicalDB");
	       initializeAdapter();
	   }
	   
	   public void initializeAdapter(){
		   DatabaseHelper dbHelper = new DatabaseHelper(this);
	       Log.d(TAG,"Init Adapter, Database name "+ dbHelper.getDatabaseName());
	       
	       Cursor cur = dbHelper.getAllPatients();
	       
	       List<PatientProfile> currentPatients = new ArrayList<PatientProfile>();
	       
	       int colCount = cur.getColumnCount();
	       String fName = null;
	       String lName = null;
	       String patientId = null;
	       String dateOfBirth = null;
	       String gender = null;
	       while(cur.moveToNext()){
	    	   for (int i =0;i < colCount;i++){
			       Log.d (TAG, cur.getColumnName(i) +" : " +cur.getString(i));
			       if(cur.getColumnName(i).equalsIgnoreCase(DatabaseHelper.firstName)){
			    	   fName = cur.getString(i);
			       }else if(cur.getColumnName(i).equalsIgnoreCase(DatabaseHelper.lastName)){
			    	   lName = cur.getString(i);
			       }else if(cur.getColumnName(i).equalsIgnoreCase(DatabaseHelper.patientId)){
			    	   patientId = cur.getString(i);
			       }else if(cur.getColumnName(i).equalsIgnoreCase(DatabaseHelper.dateOfBirth)){
			    	   dateOfBirth = cur.getString(i);
			       }else if(cur.getColumnName(i).equalsIgnoreCase(DatabaseHelper.gender)){
			    	   gender = cur.getString(i);
			       }
			       
		       }
	    	   
	    	   currentPatients.add(new PatientProfile(fName,lName, dateOfBirth, gender, patientId));
	    	   
	       }
	       // Adding items to listview
	       adapter = new ArrayAdapter<PatientProfile>(this, R.layout.list_items, R.id.product_name, currentPatients);
	       lv.setAdapter(adapter);
	       
	   }
	   
	   /*     
	   @Override
	   public boolean onOptionsItemSelected(MenuItem item) {
	       // Handle item selection	  
		   switch (item.getItemId()) {
	       case R.id.action_add:
	    	   //callAddAction();
	           return true;
	       
	       default:
	           return super.onOptionsItemSelected(item);
	  
	   }*/
	     @Override
	   public boolean onCreateOptionsMenu(Menu menu) {
	       // Inflate the menu items for use in the action bar
	       MenuInflater inflater = getMenuInflater();
	       inflater.inflate(R.menu.list_view_search, menu);
	       
	       MenuItem searchItem = menu.findItem(R.id.action_search);
	       searchView = (SearchView)searchItem.getActionView();
	       setupSearchView();
	       
	       MenuItem addItem = menu.findItem(R.id.action_add);
	       addItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {	
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					// TODO Auto-generated method stub
					callPatientAddAction();
					return false;
					
				}
			});
	       
	       return true;
	          
	   }
	     
	     private void callPatientAddAction(){
	    	 Intent intent = new Intent(this, PatientEditActivity.class);
	    	 startActivityForResult(intent, PATIENT_ADD);
	     }
	      
	   private void setupSearchView(){
		   searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
				
				@Override
				public boolean onQueryTextSubmit(String query) {
					return false;
				}
				
				@Override
				public boolean onQueryTextChange(String newText) {
					// TODO Auto-generated method stub
					// When user changed the Text
		            PatientSearchActivity.this.adapter.getFilter().filter(newText);   
					return false;
				}
			});
	   }
	   
	   @Override
	   public void onActivityResult(int requestCode, int resultCode, Intent data) {
           //super.onActivityResult(requestCode, resultCode, data);
		  initializeAdapter();
	   }
	

}
