package com.yify.mobile;

import android.app.ActionBar;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;
import java.util.*;

import com.yify.manager.ApiManager;
import com.yify.object.*;

public class MovieActivity extends FragmentActivity {
	
	private MovieInfoPageAdapter adapter;
	private ActionBar actionBar;
	private ViewPager pager;
	private ViewFlipper flipper;
	private TextView err;
	private ConnectivityDetector detector;
	private int movieid;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.pager_layout);
		actionBar = getActionBar();
		adapter = new MovieInfoPageAdapter(this.getSupportFragmentManager());
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		movieid = intent.getIntExtra("id", -1);
		
		detector = new ConnectivityDetector(this);
		pager = (ViewPager) findViewById(R.id.pager);
		flipper = (ViewFlipper) findViewById(R.id.movie_state);
		flipper.setDisplayedChild(0); /* show the loading state */
		
		if(movieid != -1) {
			new GetMovieInfo().execute(movieid);
		} else {
			err.setText("There was an error getting the item you requested");
			flipper.setDisplayedChild(2);
		}
		
	}
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case android.R.id.home:
	            	finish();
	            	break;
	            	
	        }
	        
	        return super.onOptionsItemSelected(item);
	 }
	
	public static class MovieInfoPageAdapter extends FragmentStatePagerAdapter {

		public MovieInfoPageAdapter(FragmentManager fm) {
			super(fm);
			
		}

		@Override
		public Fragment getItem(int arg0) {
			Fragment frag = null;
			
			switch(arg0) {
			case 1:
				frag = new MainInfo();
				/* do more calculations here. */
				break;
			case 0 :
				frag = new VideoInfo();
				break;
			}
			return frag;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			switch(position) {
			case 1:
				return "Main Info";
			case 0:
				return "Video Info";
			}
			
			return null;
		}
		
	}
	
	public static class MainInfo extends Fragment {
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
			
			View v = inflater.inflate(R.layout.main_fragment, container, false);
			
			return v;
			
		}
		
	}
	
	public static class VideoInfo extends Fragment {
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
			
			View v = inflater.inflate(R.layout.main_fragment, container, false);
			
			TextView t = (TextView) v.findViewById(R.id.main_frag_text);
			
			t.setText("This is the video information section.");
			return v;
			
		}
		
	}
	
	private class GetMovieInfo extends AsyncTask<Integer, Integer, ItemObject> {

		@Override
		protected ItemObject doInBackground(Integer... arg0) {
			
			if(!detector.isConnectionAvailable()) {
				return null;
			}
			
			int movieid = arg0[0];
			
			ApiManager manager = new ApiManager();
			
			return manager.getMovieDetails(movieid);
		}
		
		@Override
		protected void onPostExecute(ItemObject response) {
			
			boolean valid = true;
			
			if(!detector.isConnectionAvailable()) {
				err.setText("You are currently not connected to a network.");
				valid = false;
			}
			
			if(valid) {
				if((response == null) || (!(response instanceof ItemObject))) {
					err.setText("An Error Occured finding the item you requested.");
					valid = false;
				}
			}
			
			if(!valid) {
				flipper.setDisplayedChild(2);
				return;
			}
			
			Log.d("Found Item : " + response.getMovieID(), "Movie Title: " + response.getMovieTitle());
			pager.setAdapter(adapter);
			
			/* set up all of the tabs.
			 * first main tab. */
			//View main = pager.getChildAt(1);
			
			//TextView t = (TextView) main.findViewById(R.id.main_frag_text);
			//t.setText("Movie Title: " + response.getMovieTitle());
			
			pager.setCurrentItem(1);
			flipper.setDisplayedChild(1);
			
		}
	}

}
