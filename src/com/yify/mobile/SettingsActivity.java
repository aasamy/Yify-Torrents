package com.yify.mobile;

import com.yify.object.SettingsFragment;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {
	
	private ActionBar bar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* display settings fragment as main content. */
		this.getFragmentManager().beginTransaction()
		.replace(android.R.id.content, new SettingsFragment())
		.commit();
		
		bar = getActionBar();
		bar.setTitle("Settings");
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowTitleEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean end = super.onOptionsItemSelected(item);
		
		switch(item.getItemId()) {
		case android.R.id.home :
			finish();
			break;
		default :
			break;
		}
		
		return end;
	}
	
	
	
}
