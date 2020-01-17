package com.sim.ds.tracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;

public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		final String PREFS_NAME = "trackerpref";

		intent.getAction();

		if (intent.getAction().equalsIgnoreCase(
				"android.intent.action.BOOT_COMPLETED")) {

			// Toast.makeText(context, "Received BOOT broadcast",
			// Toast.LENGTH_LONG).show();
			// call EmailService

			long interval = 1;

			SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,
					0);

			int sel = prefs.getInt("spinnerselection", 0);

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
			AlarmManager am = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent i = new Intent(context, EmailService.class);

			PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
			am.cancel(pi);

			if (sel > 0) {
				am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
						SystemClock.elapsedRealtime() + interval, interval, pi);
			}

		}

	}

}
