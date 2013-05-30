package com.yify.mobile;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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
	    
	    int minutes = Integer.parseInt(item.getRuntime());
	    int remainder = minutes%60;
	    int hours = (minutes - remainder) / 60;
	    
	    /* get the nine textviews for each of the columns in the general info table. */
	    ((TextView) findViewById(R.id.genre_text)).setText(item.getGenre() + " | " + item.getSubGenre());
	    ((TextView) findViewById(R.id.size_text)).setText(item.getFilesize());
	    ((TextView) findViewById(R.id.quality_text)).setText(item.getQuality());
	    ((TextView) findViewById(R.id.reso_text)).setText(item.getResolution());
	    ((TextView) findViewById(R.id.fr_text)).setText(item.getFrameRate() + " fps");
	    ((TextView) findViewById(R.id.lang_text)).setText(item.getLanguage());
	    ((TextView) findViewById(R.id.st_text)).setText(item.getSubtitles());
	    ((TextView) findViewById(R.id.rt_text)).setText(hours + " hrs, " + remainder + " mins");
	    ((TextView) findViewById(R.id.rating_text)).setText(item.getMovieRating() + "/10");
	    
	    bar.setTitle("Movie Details");
	    bar.setDisplayHomeAsUpEnabled(true);
	    bar.setDisplayShowTitleEnabled(true);
	    
	    desc.setText(item.getLongDescription());
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
