package com.sim.ds.tracker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.sim.ds.tracker.R;

public class ShowMapActivity extends MapActivity implements OnClickListener {

	protected static final int FB_CODE = 20;
	protected static final int SND_CODE = 30;
	protected static final int SMS_CODE = 40;

	double lat;
	double lng;
	public static final String PREFS_NAME = "trackerpref";
	SharedPreferences.Editor prefEditor;
	SharedPreferences pref;
	GeoPoint point;
	public String myAddress;
	EditText tv;
	ProgressDialog ADialog;
	public String latstr, lngstr;
	private MapView mapView;
	private MapController mapController;
	private LocationManager locationManager;
	private LocationListener locationListener;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maplayout);
		ImageView b1 = (ImageView) findViewById(R.id.SettingsButton);
		b1.setOnClickListener(this);

		ImageView b2 = (ImageView) findViewById(R.id.FBButton);
		b2.setOnClickListener(this);

		ImageView b3 = (ImageView) findViewById(R.id.EmailButton);
		b3.setOnClickListener(this);

		ImageView b4 = (ImageView) findViewById(R.id.SMSButton);
		b4.setOnClickListener(this);

		pref = getSharedPreferences(PREFS_NAME, MODE_WORLD_READABLE);

		Criteria ct = new Criteria();
		ct.setAccuracy(Criteria.ACCURACY_FINE);

		ADialog = ProgressDialog.show(ShowMapActivity.this, "",
				"Getting current location " + "\n" + "Please wait.... ", false,
				false);
		timerDelayRemoveDialog(ADialog);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationListener = new GPSLocationListener();

		mapView = (MapView) findViewById(R.id.mapview);
		// enable to show Satellite view
		mapView.setSatellite(true);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(19);

		tv = (EditText) findViewById(R.id.address);
		tv.setFocusable(false);
		tv.setClickable(false);

	}

	public void timerDelayRemoveDialog(final ProgressDialog d) {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				if (d.isShowing() == true) {
					d.dismiss();
					Toast.makeText(getApplicationContext(),
							"GPS is not available", Toast.LENGTH_LONG).show();
				}

			}
		}, 60000);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	class GPSLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {

			if (location != null) {
				GeoPoint point = new GeoPoint(
						(int) (location.getLatitude() * 1E6),
						(int) (location.getLongitude() * 1E6));
				if ((ADialog).isShowing() == true)
					(ADialog).dismiss();

				lat = location.getLatitude();
				lng = location.getLongitude();
				mapController.animateTo(point);
				mapController.setCenter(point);
				mapController.setZoom(19);

				// add marker
				MapOverlay mapOverlay = new MapOverlay();
				mapOverlay.setPointToDraw(point);
				List<Overlay> listOfOverlays = mapView.getOverlays();
				listOfOverlays.clear();
				listOfOverlays.add(mapOverlay);

				String address = ConvertPointToLocation(point);
				// Toast.makeText(getBaseContext(), address, Toast.LENGTH_SHORT)
				// .show();
				myAddress = address;
				latstr = Float.toString((float) lat);
				lngstr = Float.toString((float) lng);
				mapView.invalidate();
				// update text
				tv.setText("");
				tv.setText(" " + myAddress + "\n");
				tv.append(" Latitude: " + latstr + ",Longitude: " + lngstr);
				setAddress();
			}
		}

		public String ConvertPointToLocation(GeoPoint point) {
			String address = "";
			Geocoder geoCoder = new Geocoder(getBaseContext(),
					Locale.getDefault());
			try {
				List<Address> addresses = geoCoder.getFromLocation(
						point.getLatitudeE6() / 1E6,
						point.getLongitudeE6() / 1E6, 1);

				if (addresses.size() > 0) {
					for (int index = 0; index < addresses.get(0)
							.getMaxAddressLineIndex(); index++)
						address += addresses.get(0).getAddressLine(index) + " ";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			return address;
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(), "GPS not available",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	class MapOverlay extends Overlay {
		private GeoPoint pointToDraw;

		public void setPointToDraw(GeoPoint point) {
			pointToDraw = point;
		}

		public GeoPoint getPointToDraw() {
			return pointToDraw;
		}

		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			super.draw(canvas, mapView, shadow);

			// convert point to pixels
			Point screenPts = new Point();
			mapView.getProjection().toPixels(pointToDraw, screenPts);

			// add marker
			Bitmap bmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.marker);
			canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 48, null);

			return true;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == FB_CODE) {
			// back from FBActivity
			// Toast.makeText(getBaseContext(), "Location posted on facebook",
			// Toast.LENGTH_LONG).show();
		} else if (requestCode == SND_CODE) {
			// back from SettingsActivity

		} else if (requestCode == SMS_CODE) {
			// back from ContactActivity

		}

	}

	public void setAddress() {

		prefEditor = pref.edit();
		prefEditor.putString("myAddress", myAddress);
		prefEditor.putFloat("lat", (float) lat);
		prefEditor.putFloat("lng", (float) lng);
		prefEditor.commit();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		setAddress();

		if (R.id.SettingsButton == v.getId()) {
			Intent setintent = new Intent(getApplicationContext(),
					SettingsActivity.class);

			startActivityForResult(setintent, SND_CODE);

		} else if (R.id.FBButton == v.getId()) {

			Intent fbintent = new Intent(getApplicationContext(),
					FBActivity.class);

			startActivityForResult(fbintent, FB_CODE);

		} else if (R.id.SMSButton == v.getId())

		{

			String msg = myAddress + "\n" + "http://maps.google.com/maps?q="
					+ latstr + "," + lngstr;

			Intent smsintent = new Intent(getApplicationContext(),
					SMSActivity.class);
			smsintent.putExtra("body", msg);
			startActivityForResult(smsintent, SMS_CODE);

		}

		else if (R.id.EmailButton == v.getId()) {

			String ebody = myAddress + "\n" + "http://maps.google.com/maps?q="
					+ latstr + "," + lngstr;
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"My current location (via Tracker)");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, ebody);
			startActivity(Intent.createChooser(emailIntent,
					"Send your email in:"));
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				2000, 300, locationListener);
		myAddress = pref.getString("myAddress", "");
		latstr = Float.toString((pref.getFloat("lat", 0)));
		lngstr = Float.toString((pref.getFloat("lng", 0)));
		tv.setText("");
		tv.setText("  " + myAddress + "\n");
		tv.append("  Latitude: " + latstr + ",Longitude: " + lngstr);
		// super.onResume();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		// locationManager.removeUpdates(locationListener);
		setAddress();

		super.onPause();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub

		super.onStart();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		setAddress();
		locationManager.removeUpdates(locationListener);
		super.onStop();
	}

}
