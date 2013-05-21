package com.yify.mobile;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.yify.mobile.R;

public class SearchActivity extends ActionBarActivity {

	private Menu mainMenu = null;
	private ActionBar actionBar;
	private String query;
	private TextView resultsText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.search);
	}
	
}
