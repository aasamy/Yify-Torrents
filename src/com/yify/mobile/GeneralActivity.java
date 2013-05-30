package com.yify.mobile;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.yify.object.ItemObject;

public class GeneralActivity extends ActionBarActivity {

	private ItemObject item;
	private TextView desc;
	private ActionBar bar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
	    this.item = intent.getParcelableExtra("item");
	    setContentView(R.layout.general_information);
	    this.desc = (TextView) findViewById(R.id.desc);
	    this.bar = getActionBar();
	    
	    bar.setTitle("Movie Details");
	    bar.setDisplayHomeAsUpEnabled(true);
	    bar.setDisplayShowTitleEnabled(true);
	    
	    desc.setText(item.getLongDescription());
		
	}
	
	
}
