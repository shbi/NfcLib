package com.mobiquityinc.nfclib;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

/**
 * Supports NfcReading in the activity</br>
 * Extending this Activity class will enable foreground dispatching for that Activity.</br>
 * getTechFilters should return an array of string arrays that contain the different NFC </br>
 * technologies you want to capture
 * getIntentFilters should return an array of IntentFilters that emumerate the different intents you want to capture
 * NDEF_DISCOVERED</br>
 * TECH_DISCOVERED</br>
 * TAG_DISCOVERED</br>
 * 
 * newIntent() is called when a new NFC intent arrives. You should handle it in that call.
 * @author jschneider
 *
 */
public abstract class NfcActivity extends Activity {
		
	PendingIntent pendingIntent = null;
	private boolean debugging = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if(null == nfcAdapter) {
			return;
		}
		IntentFilter[] intentFilters = getIntentFilters();
		String[][] techFilters = getTechFilters();
		nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, techFilters);
	}
	/**
	 * Returns list of TechFilters that the activity wants.</br>
	 * Returning null is the same as all Technologies
	 * @return list of TechFilters, can be null to catch all Technologies
	 */
	protected abstract String[][] getTechFilters();
	/**
	 * Returns list of IntentFilters than the activity will catch, </br>
	 * if null then TAG_DISCOVERED will be the only intent captured and</br>
	 * TechFilters is ignored
	 * @return list of IntentFilters, can be null to catch TAG_DISCOVERED
	 */
	protected abstract IntentFilter[] getIntentFilters();
	
	@Override
	protected void onPause() {
		super.onPause();
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if(null == nfcAdapter) {
			return;
		}
		nfcAdapter.disableForegroundDispatch(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		newIntent();
	}
	
	/**
	 * This method gets called when a new tag is scanned.
	 */
	protected abstract void newIntent();
	
	public void showTechList(Tag tag){
		if(debugging){
			String[] techlist = null;
			if(tag != null) {
				techlist = tag.getTechList();
			}
			if(techlist != null){
				for(int i = 0; i < techlist.length; i++){
					Log.i("TechList", techlist[i]);
				}
			}
		}
	}
	
	public void setDebugging(boolean debugging){
		this.debugging = debugging;
	}
	
	public boolean isDebuggin(){
		return debugging;
	}
	
	/**
	 * turns the tag associated with the intent, returns null if not present
	 * @return the tag associated with the intent, can be null
	 */
	protected Tag getTag(){
		return getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
	}
}
