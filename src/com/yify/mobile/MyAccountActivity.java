package com.yify.mobile;

import android.os.Bundle;
import android.view.Menu;

import com.yify.object.AuthObject;


public class MyAccountActivity extends ActionBarActivity {

	private AuthObject user;
	private Menu mainMenu;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
	}
	
	
}
