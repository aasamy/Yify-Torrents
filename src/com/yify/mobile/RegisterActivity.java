package com.yify.mobile;

import android.app.ActionBar;
import android.os.Bundle;

public class RegisterActivity extends ActionBarActivity {
	
	private ActionBar bar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);
		bar = getActionBar();
		bar.setTitle("Register");
		bar.setDisplayShowTitleEnabled(true);
		
	}

}
