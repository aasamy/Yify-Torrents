package com.yify.mobile;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Gallery;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import java.util.*;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yify.manager.ApiManager;
import com.yify.object.*;

public class MovieActivity extends ActionBarActivity {
	
	public static final int COVER = 1;
	public static final int SCREENSHOT = 2;
	
	private LinearLayout imageScroll;
	private ActionBar actionBar;
	private Menu menu;
	private ViewFlipper state;
	private TextView err;
	private ConnectivityDetector detector;
	private LayoutInflater inflater;
	private int i;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.pager_layout);
		
		actionBar = getActionBar();
		imageScroll = (LinearLayout) findViewById(R.id.imageViewInner);
		state = (ViewFlipper) findViewById(R.id.movie_state);
		err = (TextView) findViewById(R.id.movie_err);
		detector = new ConnectivityDetector(this);
		inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		state.setDisplayedChild(0);
		
		Intent intent = getIntent();
		int movieid = intent.getIntExtra("id", -1);
		
		if(movieid != -1) {
			new GetMovie().execute(movieid);
		} else {
			err.setText("There was an error retrieving movie information");
			state.setDisplayedChild(2);
		}
		
	}
	
	private class GetMovie extends AsyncTask<Integer, Integer, ItemObject> {

		@Override
		protected ItemObject doInBackground(Integer... arg0) {
			
			if(!detector.isConnectionAvailable()) {
				return null;
			}
			
			ApiManager manager = new ApiManager();
			return manager.getMovieDetails(arg0[0]);
		}
		
		@Override
		protected void onPostExecute(final ItemObject response) {
			
			if((response == null) || (!(response instanceof ItemObject))) {
				err.setText("You currently do not have a network connection");
				state.setDisplayedChild(2);
				return;
			}
			
			View c = inflater.inflate(R.layout.image_cover, null);
			
			ImageView iv = (ImageView) c.findViewById(R.id.imagebutton_movie);
			
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			
			ImageLoader.getInstance().displayImage(response.getLargeCover(), iv);
			
			iv.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					
					Intent intent = new Intent(MovieActivity.this, FullScreenActivity.class);
					intent.putExtra("type", MovieActivity.COVER);
					intent.putExtra("item", response.getLargeCover());
					startActivity(intent);
					
				}
				
			});
			
			imageScroll.addView(c);
			
			final String[] screenshots = response.getScreenshots();
			
			final String[] large = new String[] {screenshots[3], screenshots[4], screenshots[5]};
			
			for(i = 0; i < 3; i++) {
				
				View v = inflater.inflate(R.layout.image_layout, null);
				
				ImageView image = (ImageView) v.findViewById(R.id.imagebutton_movie);
				
				ImageLoader.getInstance().displayImage(screenshots[i], image);
				
				image.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						
						Intent intent = new Intent(MovieActivity.this, FullScreenActivity.class);
						intent.putExtra("type", MovieActivity.SCREENSHOT);
						intent.putExtra("item", large[i]);
						startActivity(intent);
						
					}
					
				});
				
				imageScroll.addView(v);
				
			}
			
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(response.getMovieTitle());
			actionBar.setDisplayShowTitleEnabled(true);
			state.setDisplayedChild(1);
			
			
		}
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
