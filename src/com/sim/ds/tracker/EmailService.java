package com.sim.ds.tracker;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class EmailService extends Service {
	@Override
	public IBinder onBind(Intent intent) {

		return null;

	}

	@Override
	public void onCreate() {
		super.onCreate();

		// Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

	}

	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);
		final String PREFS_NAME = "trackerpref";

		SharedPreferences pref;
		pref = getSharedPreferences(PREFS_NAME, MODE_WORLD_READABLE);
		// Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

		String emailaddress = pref.getString("emailaddress", "");
		String myAddress = pref.getString("myAddress", "");
		String gmailaddress = pref.getString("gmailaddress", "");
		String passwd = pref.getString("passwd", "");
		float lat = pref.getFloat("lat", 0);
		float lng = pref.getFloat("lng", 0);
		Mail m = new Mail(gmailaddress, passwd);

		String[] toArr = { emailaddress };
		m.setTo(toArr);
		m.setFrom(gmailaddress);
		m.setSubject("My current location (via TRACKER )");
		m.setBody("   " + myAddress + "\n" + "http://maps.google.com/maps?q="
				+ lat + "," + lng);

		try {
			// m.addAttachment("/sdcard/filelocation");

			if (m.send()) {
				// Toast.makeText(this, "Email was sent successfully.",
				// Toast.LENGTH_LONG).show();
			} else {
				// Toast.makeText(this, "Email was not sent.",
				// Toast.LENGTH_LONG)
				// .show();
			}
		} catch (Exception e) {

		}
	}

}