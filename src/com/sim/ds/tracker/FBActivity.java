package com.sim.ds.tracker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class FBActivity extends Activity {

	public static final String APP_ID = "225136784163155";
	private Facebook facebook = new Facebook(APP_ID);

	public static final String PREFS_NAME = "trackerpref";
	public Bundle vals;
	public boolean auth = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.fblayout);

		facebook.authorize(this, new String[] { "publish_stream" },
				new AuthorizeListener());

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		facebook.authorizeCallback(requestCode, resultCode, data);
		this.finish();
		Toast.makeText(getBaseContext(), "Location posted on facebook",
				Toast.LENGTH_LONG).show();
	}

	public void postLocToWall(Bundle values)

	{
		String accessToken = values.getString(Facebook.TOKEN);
		// post to wall
		Bundle bundle = new Bundle();
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);

		String myAddress = prefs.getString("myAddress", "");
		String lat = Float.toString((prefs.getFloat("lat", 0)));
		String lng = Float.toString((prefs.getFloat("lng", 0)));

		bundle.putString("message", " My current location (via Tracker):  "
				+ myAddress);
		bundle.putString("link", "http://maps.google.com/maps?q=" + lat + ","
				+ lng);

		bundle.putString(Facebook.TOKEN, accessToken);
		try {
			String response = facebook.request("me/feed", bundle, "POST");
			Toast.makeText(getBaseContext(), "Location posted on facebook",
					Toast.LENGTH_LONG).show();
			// this.finish();
		} catch (FileNotFoundException err) {
			// TODO Auto-generated catch block
			err.printStackTrace();
		} catch (MalformedURLException err) {
			// TODO Auto-generated catch block
			err.printStackTrace();
		} catch (IOException err) {
			// TODO Auto-generated catch block
			err.printStackTrace();
		}

	}

	class AuthorizeListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {

			// Handle a successful login
			vals = values;
			auth = true;
			postLocToWall(values);

			finish();
		}

		@Override
		public void onCancel() {

		}

		@Override
		public void onError(DialogError e) {

		}

		@Override
		public void onFacebookError(FacebookError e) {

		}
	}

}
