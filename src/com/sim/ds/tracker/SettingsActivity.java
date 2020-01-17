package com.sim.ds.tracker;

import com.sim.ds.tracker.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	public Spinner spinner;
	public int spinneritem;
	// TO DO: put these in string.xml
	String[] emailfreq = { "Never", "Every half hour", "Every hour",
			"Once a day" };
	public static final String PREFS_NAME = "trackerpref";
	SharedPreferences.Editor prefEditor;
	SharedPreferences pref;
	int spinnerselection;

	String emailaddress;
	String gmailaddress;
	String passwd;
	public EditText et1, et2, et3;
	AlarmManager am;
	long interval;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setlayout);
		// save
		final Button b1 = (Button) findViewById(R.id.button1);

		// have to do this everytime user comes
		readPreferences();

		prefEditor = pref.edit();

		// listeners
		// save
		b1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// get email addr from edittext
				String txt1 = et1.getText().toString();
				// gmail addr
				String txt2 = et2.getText().toString();
				// gmail passwd
				String txt3 = et3.getText().toString();
				// save email addr to pref file
				prefEditor.putString("emailaddress", txt1);
				prefEditor.putString("gmailaddress", txt2);
				prefEditor.putString("passwd", txt3);
				prefEditor.commit();
				Toast.makeText(getApplicationContext(), "Saved successfully.",
						Toast.LENGTH_LONG).show();
				startEmailService();
			}
		});

		// spinner selection
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				spinneritem = spinner.getSelectedItemPosition();

				// save spinner selection to pref file

				prefEditor.putInt("spinnerselection", spinneritem);
				prefEditor.commit();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	public void readPreferences() {
		// email frequency spinner
		spinner = (Spinner) findViewById(R.id.spinner1);
		// show spinner
		setFrequencySpinner();
		// read preference file
		pref = getSharedPreferences(PREFS_NAME, MODE_WORLD_READABLE);
		// spinner
		spinnerselection = pref.getInt("spinnerselection", 0);
		spinner.setSelection(spinnerselection);

		// set email addr
		et1 = (EditText) findViewById(R.id.editText1);
		emailaddress = pref.getString("emailaddress", "");
		et1.setText(emailaddress);
		// gmail
		et2 = (EditText) findViewById(R.id.gtext);
		gmailaddress = pref.getString("gmailaddress", "");
		et3 = (EditText) findViewById(R.id.ptext);
		passwd = pref.getString("passwd", "");

	}

	public void startEmailService() {

		int sel = pref.getInt("spinnerselection", 0);

		// Set alarm
		if (sel != 0) {
			if (sel == 1) // half hr
				interval = AlarmManager.INTERVAL_HALF_HOUR;
			else if (sel == 2) // 1 hr
				interval = AlarmManager.INTERVAL_HOUR;
			else if (sel == 3) // once a day
				interval = AlarmManager.INTERVAL_DAY;

			// for testing
			// interval = 1;
		}

		// set inexact less drain on battery
		AlarmManager am = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(this, EmailService.class);

		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		am.cancel(pi); // remove older pi

		if (sel > 0) {
			am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + interval, interval, pi);
		}

	}

	public void setFrequencySpinner() {

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, emailfreq);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		this.finish();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
		readPreferences();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub

		super.onStart();
		readPreferences();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
