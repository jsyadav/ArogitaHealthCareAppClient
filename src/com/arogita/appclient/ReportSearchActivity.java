package com.arogita.appclient;

import java.util.ArrayList;
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
import com.arogita.appclient.report.ReportProfile;
import com.example.sampleappclient.R;

public class ReportSearchActivity extends Activity {
	private static final String TAG = "ResultSearchActivity";
	   // List view
	   private ListView lv;
	   // Listview Adapter
	   ArrayAdapter<ReportProfile> adapter;
	   // Search view
	   SearchView searchView;
	   String patientId = null;

	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.activity_report_search);
	       Bundle bundle = getIntent().getExtras();
	       if (bundle != null){
	    	   patientId = bundle.getString(PatientActivity.UID);
	    	   Log.d(TAG, "Patient Id " + patientId);
	       }
	       
	       String profileName = "Patient id, " + patientId;
	       getActionBar().setTitle("Reports for "+ profileName);
	        
	       lv = (ListView) findViewById(R.id.list_view);
	       lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.v(TAG, "Count in the view "+ arg0.getCount()+", arg2 = "+ arg2+", arg3 = "+arg3);
				ReportProfile rp = (ReportProfile)arg0.getItemAtPosition(arg2); 
				Intent intent = new Intent(ReportSearchActivity.this, ReportReviewActivity.class);
				intent.putExtra(PatientActivity.UID,rp.getPatientId() );
				intent.putExtra("ReportId",rp.getReportId() );
				intent.putExtra("Date",rp.getDateStr());
		    	startActivityForResult(intent, PatientActivity.REPORT_VIEW);
			}
		});
	       initializeAdapter();
	   }
	        
	   public void initializeAdapter(){
		   DatabaseHelper dbHelper = new DatabaseHelper(this);
	       Log.d(TAG,"Searching reports for patient "+ patientId);
	       Cursor cur = dbHelper.getAllReportsByPatient(patientId);
	      
	       List<ReportProfile> currentReports = new ArrayList<ReportProfile>();
	       
	       int colCount = cur.getColumnCount();
	       String date = null;
	       String reportId = null;
	       
	       while(cur.moveToNext()){
	    	   for (int i =0;i < colCount;i++){
			       Log.d (TAG, cur.getColumnName(i) +" : " +cur.getString(i));
			       if(cur.getColumnName(i).equalsIgnoreCase("Date")){
			    	   date = cur.getString(i);
			       }
			       if(cur.getColumnName(i).equalsIgnoreCase("ReportId")){
			    	   reportId = cur.getString(i);
			       }
		       }
	    	   
	    	   currentReports.add(new ReportProfile(date, patientId, reportId));
	    	   
	       }
	       // Adding items to listview
	       adapter = new ArrayAdapter<ReportProfile>(this, R.layout.list_items,
	    		   R.id.product_name, currentReports);
	       lv.setAdapter(adapter);
	       
	   }
	   /*
	   @Override
	   public boolean onOptionsItemSelected(MenuItem item) {
	       // Handle item selection	  
		   switch (item.getItemId()) {
	       case R.id.action_add:
	    	   newTest();
	           return true;
	       
	       default:
	           return super.onOptionsItemSelected(item);
	   	}
	   
	  
	   }
	   */
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
					newTest();
					return false;
					
				}
			});
	       
	       return true;
	          
	   }
	     
	     private void newTest(){
	    	 Intent intent = new Intent(ReportSearchActivity.this, ReportActivity.class);
	    	 intent.putExtra(PatientActivity.UID, patientId);
			 startActivityForResult(intent, PatientActivity.REPORT_NEW);
	     }
	      
	   private void setupSearchView(){
		   searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
				
				@Override
				public boolean onQueryTextSubmit(String query) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean onQueryTextChange(String newText) {
					// TODO Auto-generated method stub
					// When user changed the Text
		            ReportSearchActivity.this.adapter.getFilter().filter(newText);   
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
