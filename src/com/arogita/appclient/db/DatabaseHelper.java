package com.arogita.appclient.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.arogita.appclient.patient.PatientProfile;
import com.arogita.appclient.report.ReportProfile;

public class DatabaseHelper extends SQLiteOpenHelper{


	static final String dbName="medicalDB";

	// Patient table
	static final String patientTable="PatientTable";
	public static final String firstName="FirstName";
	public static final String lastName="LastName";
	public static final String patientId="PatientId";// Auto
	public static final String dateOfBirth="DateOfBirth";
	public static final String gender="Gender";


	// Report table
	static final String reportTable="ReportTable";
	public static final String reportId="ReportId";// Auto
	public static final String dateStr="Date";
	public static final String patient="PatientId";
	public static final String saturation="Saturation";
	public static final String pulse="Pulse";
	public static final String temp="Temp";
	public static final String nibp="Nibp";
	public static final String heartRate="HeartRate";
	public static final String respRate="RespRate";
	public static final String stLevel="StLevel";
	public static final String arrCode="ArrCode";
	public static final String chartFileName="ChartFileName";
	public static final String notes="Notes";

	public DatabaseHelper(Context context) {
		super(context, dbName, null, 2);

	}



	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("Calling Database oncreate");
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE "+patientTable+" ("+patientId+ " INTEGER PRIMARY KEY AUTOINCREMENT , "+
				firstName+ " TEXT, " + lastName+ " TEXT, "+
				dateOfBirth+ " TEXT, " + gender+ " TEXT )");

		db.execSQL("CREATE TABLE "+reportTable+"("+
				reportId+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
				dateStr+" TEXT, "+saturation+" TEXT, "+
				pulse+" TEXT, "+ temp +" TEXT, "+
				nibp+" TEXT, "+ heartRate+" TEXT, "+
				respRate+" TEXT, " + stLevel+" TEXT, "+
				arrCode+" TEXT, " + chartFileName+" TEXT, "+
				notes+" TEXT, "+
				patient+" INTEGER NOT NULL ,FOREIGN KEY ("+patient+") REFERENCES "+
				patientTable+" ("+patientId+"));");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("Calling Database onUpgrade");
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+reportTable);
		db.execSQL("DROP TABLE IF EXISTS "+patientTable);
		onCreate(db);
	}

	public long  insertPatient(PatientProfile cf){
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put(firstName, cf.getFirstName());
		cv.put(lastName, cf.getLastName());
		cv.put(gender, cf.getGender());
		cv.put(dateOfBirth, cf.getDob());
		long ret = db.insert(patientTable, cf.getId(), cv);
		if (ret > 0){
			
		}
		
		db.close();
		return ret;
	}

	public Cursor getAllPatients()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT * from "+ patientTable,new String [] {});

		return cur;
	}



	public void updatePatient(PatientProfile cf) {
		// TODO Auto-generated method stub
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues cv=new ContentValues();
		System.out.println("DatabaseHelper, id is " + cf.getId());
		int id = Integer.valueOf(cf.getId());
		System.out.println("DatabaseHelper, id is " + id);
		cv.put(patientId, id);
		cv.put(firstName, cf.getFirstName());
		cv.put(lastName, cf.getLastName());
		cv.put(gender, cf.getGender());
		cv.put(dateOfBirth, cf.getDob());
		db.update(patientTable, cv, " patientId = ?",  new String[] {cf.getId()});
		db.close();
	}



	public Cursor getAllReportsByPatient(String patientId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT "+dateStr+ ", "+reportId+
				" from "+ reportTable +
				" where "+ patient +" = ?", new String [] {patientId});

		return cur;
	}



	public long insertReport(ReportProfile rf) {
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put(patient, rf.getPatientId());
		cv.put(dateStr, rf.getDateStr());
		//
		cv.put(saturation, rf.getSaturation());
		cv.put(pulse, rf.getPulse());
		cv.put(nibp, rf.getNibp());
		cv.put(temp, rf.getTemp());
		cv.put(heartRate, rf.getHeartRate());
		cv.put(respRate, rf.getRespRate());
		cv.put(stLevel, rf.getStLevel());
		cv.put(arrCode, rf.getArrCode());
		cv.put(notes, rf.getNotes());
		cv.put(chartFileName, rf.getChartFileName());

		long ret = db.insert(reportTable, null, cv);
		db.close();
		return ret;
	}



	public void updateReport(String reportId, String docNotes) {
		// TODO Auto-generated method stub
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put(notes, docNotes);
		
		db.update(reportTable, cv, " reportId = ?",  new String[] {reportId});
		db.close();
	}



	public Cursor getReport(String reportId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT * "+ 
				" from "+ reportTable +
				" where reportId = ?", new String [] {reportId});

		return cur;
	}



	public String getUid(PatientProfile pp) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT  "+ patientId + 
				" from "+ patientTable +
				" where "+ firstName +" = ? and " + lastName +" = ? and "+
				dateOfBirth +" = ? ", new String [] {pp.getFirstName(),
				pp.getLastName(), pp.getDob()});
		
		while(cur.moveToNext()){
			return cur.getString(0);
		}
		
		return null;
	}
}
