package com.sim.ds.tracker;



import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class SMSActivity extends Activity {
	
	protected static final int  CON_CODE = 50;
	public String phonenum = "";
	public String msg = "";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.fblayout);
		Bundle bundle = getIntent().getExtras();
        msg = bundle.getString("body");
		Intent contactintent = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI );
		startActivityForResult(contactintent, CON_CODE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK){
		if (requestCode == CON_CODE) {
			
			//Get contact info and send sms
			sendSMStoContact(data);
			this.finish();
		}
		}

	}

	
	private void sendSMStoContact(Intent data) {
		// TODO Auto-generated method stub
		//show contact picker, choose, get ph#.
		Cursor cursor = null;
       // String phonenum = "";
        try {
            Uri result = data.getData();
            
            // get the contact id from the Uri
            String id = result.getLastPathSegment();

            // query for phone
            cursor = getContentResolver().query(Phone.CONTENT_URI,
                    null, Phone.CONTACT_ID + "=?", new String[] { id },
                    null);

            int phoneID = cursor.getColumnIndex(Phone.DATA);

            
            if (cursor.moveToFirst()) {
                phonenum = cursor.getString(phoneID);
               // Toast.makeText(this, " phone found for contact." + phonenum,
                       // Toast.LENGTH_LONG).show();
              
            } else {
               // Log.w(DEBUG_TAG, "No results");
            }
        } catch (Exception e) {
          
        } finally {
            if (cursor != null) {
                cursor.close();
            }
           
            //send
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
    		sendIntent.putExtra("address",phonenum); 
    		sendIntent.putExtra("subject", "My current location: ");
            sendIntent.putExtra("sms_body", msg); 
            sendIntent.setType("vnd.android-dir/mms-sms");
            startActivity(sendIntent);
            

        }
		
		
       
        
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
		
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

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		
		super.onStop();
	}



}
