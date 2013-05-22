package com.yify.mobile;

import com.yify.object.Filter;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FilterActivity extends ActionBarActivity {
	
	private Menu mainMenu = null;
	private ActionBar actionBar;
	private Filter filter;
	public static final String CUSTOM_FILTER_INTENT = "ACTION_SEARCH_FILTER_CUSTOM";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main); /* change to filter view after intent testing finished. */
		Intent intent = getIntent();
		filter = (Filter)intent.getParcelableExtra("filter");
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		setTitle("Filter Results - " + filter.getQuery());
		getActionBar().setDisplayShowTitleEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean end = super.onCreateOptionsMenu(menu);
		
		mainMenu = menu;
		
		return end;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
		}
		
		return super.onOptionsItemSelected(item);
		
	}
	
}
