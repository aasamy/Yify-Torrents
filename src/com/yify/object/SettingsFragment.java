package com.yify.object;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yify.manager.DatabaseManager;
import com.yify.mobile.R;
import com.yify.mobile.SuggestionProvider;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.provider.SearchRecentSuggestions;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	
	private final String KEY_ALLOW_RECENT = "pref_search_enable";
	SearchRecentSuggestions suggestions;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.addPreferencesFromResource(R.xml.settings);
		
		suggestions = new SearchRecentSuggestions(getActivity(), SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
		final DatabaseManager man = new DatabaseManager(getActivity());
		
		/* update the state */
		
		Preference clearSearch = (Preference) this.findPreference("pref_search_clear");
		clearSearch.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference preference) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				
				builder.setMessage("Are you sure you want to clear your search data?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						suggestions.clearHistory();
						dialog.dismiss();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						dialog.dismiss();
						
					}
				}).show();
				
				return false;
			}
			
		});
		
		CheckBoxPreference sea = (CheckBoxPreference) this.findPreference(KEY_ALLOW_RECENT);
		clearSearch.setEnabled(sea.isChecked());
		CheckBoxPreference not = (CheckBoxPreference) this.findPreference("pref_gen_notif");
		//TimePreference tp = (TimePreference) this.findPreference("pref_gen_notif_refresh");
		//tp.setEnabled(not.isChecked());
		
		/* set the clear account dialog */
		Preference account = this.findPreference("pref_account_clear");
		
		account.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				
				builder.setMessage("Are you sure you would like to clear your account data?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						
						man.logout();
						dialog.dismiss();
						
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						dialog.dismiss();
						
					}
				}).show();
				
				return false;
			}
			
		});
		
		Preference cache = this.findPreference("pref_gen_imgcache");
		
		cache.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				
				builder.setMessage("This will remove all cached images from this device. Proceed?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						ImageLoader.getInstance().clearDiscCache();
						ImageLoader.getInstance().clearMemoryCache();
						dialog.dismiss();
						
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						dialog.dismiss();
						
					}
				}).show();
				
				return false;
			}
			
		});
		
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if(key.equals(KEY_ALLOW_RECENT)) {
			CheckBoxPreference p = (CheckBoxPreference) findPreference(key);
			Preference cle = findPreference("pref_search_clear");
			cle.setEnabled(p.isChecked());
			if(!p.isChecked()) {
				/* clear the entries */
				suggestions.clearHistory();
			}
		} else if(key.equals("pref_gen_notif")) {
			CheckBoxPreference not = (CheckBoxPreference) this.findPreference("pref_gen_notif");
			//TimePreference tp = (TimePreference) this.findPreference("pref_gen_notif_refresh");
			//tp.setEnabled(not.isChecked());
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
		.registerOnSharedPreferenceChangeListener(this);
		CheckBoxPreference sea = (CheckBoxPreference) this.findPreference(KEY_ALLOW_RECENT);
		Preference cle = findPreference("pref_search_clear");
		cle.setEnabled(sea.isChecked());
		CheckBoxPreference not = (CheckBoxPreference) this.findPreference("pref_gen_notif");
		//TimePreference tp = (TimePreference) this.findPreference("pref_gen_notif_refresh");
		//tp.setEnabled(not.isChecked());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
		.unregisterOnSharedPreferenceChangeListener(this);
	}
	
	

}
