package com.yify.mobile;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.yify.object.ItemObject;

public class TorrentActivity extends ActionBarActivity {

	private ItemObject item;
	private ActionBar bar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
	    this.item = intent.getParcelableExtra("item");
	    setContentView(R.layout.torrent_information);
	    this.bar = getActionBar();
	    
	    /* get the nine textviews for each of the columns in the general info table. */
	    ((TextView) findViewById(R.id.ps_text)).setText(item.getTorrentPeers() + "/" + item.getTorrentSeeds());
	    ((TextView) findViewById(R.id.hash_text)).setText(item.getTorrentHash());
	    
	    bar.setTitle("Torrent Details");
	    bar.setDisplayHomeAsUpEnabled(true);
	    bar.setDisplayShowTitleEnabled(true);
	    
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		case android.R.id.home :
			finish();
			break;
		}
		
		return super.onOptionsItemSelected(item);
		
	}
	
}
