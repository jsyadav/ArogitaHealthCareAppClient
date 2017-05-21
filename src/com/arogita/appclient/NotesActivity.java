package com.arogita.appclient;

import com.example.sampleappclient.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;



public class NotesActivity extends Activity {
	
	private TextView oldNotes ;
	private TextView newNote ;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notes);
		
		oldNotes = (TextView)findViewById(R.id.old_notes);
		newNote = (TextView)findViewById(R.id.new_note);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null){
			String old = bundle.getString("Notes");	
			oldNotes.setText(old);
		}
		
		Button done = (Button)findViewById(R.id.done);
		done.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent output = new Intent();
				String combinedNotes = oldNotes.getText() + "\n" + newNote.getText();
				output.putExtra("Notes", combinedNotes);
				setResult(RESULT_OK, output);
				finish();
			}
		});
		
			
    }
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                                                        INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
}