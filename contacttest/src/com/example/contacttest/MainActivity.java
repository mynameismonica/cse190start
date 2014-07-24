package com.example.contacttest;

import java.util.ArrayList;
import java.util.Random;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;


public class MainActivity extends ActionBarActivity {
	public void chooseContact(View view) {
		Intent intent = new Intent(this, ChooseContactActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the Send button */
	public void sendMessage(View view) {
	    // Do something in response to button
	    TestSyncTask task = new TestSyncTask();
	    task.execute();
	}
	
	  private class TestSyncTask extends AsyncTask<String, Void, String> {
		  
		  @Override  
		  protected String doInBackground(String... seen) {
			  Random rnd = new Random();           // To randomly pick a contact
			  String name = null;                  // The contact's name
			  String contactId = null;             // The contact's row id in the contact table
			  
			  // From the ContactsContract table, select fields ID and DISPLAY NAME
			  String[] projection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
			  // Select only the contacts with at least one phone number associated with them
			  String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";
			  String[] selectionArgs = null;
			  
			  // Return selection in ascending order
			  String orderBy = ContactsContract.Contacts.DISPLAY_NAME + " ASC";

			  // Make the query, and return a cursor pointing to result of the query
			  Cursor contactsCursor = getContentResolver()
			          .query(ContactsContract.Contacts.CONTENT_URI, projection, selection, selectionArgs, orderBy);
			  
			  // Get the number of contacts with at least one phone number associated
			  int numContacts = contactsCursor.getCount();
			  
			  // Randomly select a contact from the ones in the query result
			  contactsCursor.moveToPosition(rnd.nextInt(numContacts));
			  
			  // Get the name and id of the random contact selected
			  name = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			  contactId = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));
			  
			  // Get the list of phone numbers associated with the contact
			  String[] phoneProjection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
			  String phoneSelection = ContactsContract.Data.CONTACT_ID + "=" + contactId;
			  Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			  
			  Cursor phoneCursor = getContentResolver()
				        .query(phoneUri, phoneProjection, phoneSelection, null, null);
			  phoneCursor.moveToNext();
			  String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			  
			  phoneCursor.close();
			  contactsCursor.close();
			  
			  return name + " and " + contactId + " and the phone number is " + phoneNumber;
		    }

		    @Override
		    protected void onPostExecute(String result) {
		    	TextView textElement = (TextView) findViewById(R.id.testText);
				textElement.setText(result); //leave this line to assign a specific text

		    }

		  }

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			return rootView;
		}
	}
}
