package com.yify.mobile;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
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

public class MovieActivity extends FragmentActivity implements ActionBar.TabListener {
	
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
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		
		Intent intent = getIntent();
		movieid = intent.getIntExtra("id", -1);
		err = (TextView) findViewById(R.id.movie_err);
		detector = new ConnectivityDetector(this);
		pager = (ViewPager) findViewById(R.id.pager);
		flipper = (ViewFlipper) findViewById(R.id.movie_state);
		flipper.setDisplayedChild(0); /* show the loading state */
		
		/* add the tabs to the actionbar, but dont define the navigation mode to tabs until data is loaded. */
		actionBar.addTab(actionBar.newTab().setText("Video Info").setTabListener(MovieActivity.this));
		actionBar.addTab(actionBar.newTab().setText("Movie Info").setTabListener(MovieActivity.this), true);
		
		/* add the pagechange listener. */
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			 @Override
	            public void onPageSelected(int position) {
	                actionBar.setSelectedNavigationItem(position);
	            }
		});
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
		
		private ItemObject item;
		
		public MovieInfoPageAdapter(FragmentManager fm, ItemObject item) {
			super(fm);
			
			this.item = item;
			
		}

		@Override
		public Fragment getItem(int arg0) {
			Fragment frag = null;
			
			switch(arg0) {
			case 1:
				frag = new MainInfo();
				break;
			case 0 :
				frag = new VideoInfo();
				break;
			}
			/* do more calculations here. */
			Bundle main = new Bundle();
			main.putParcelable("item", this.item);
			frag.setArguments(main);
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
			
			TextView t = (TextView) v.findViewById(R.id.main_frag_text);
			
			Bundle main = this.getArguments();
			
			ItemObject object = main.getParcelable("item");
			
			t.setText("The Movie Title is: " + object.getMovieTitle() + "And the URL for the first meduim screenshot is: " + object.getScreenshots().get("med1"));
			
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
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			adapter = new MovieInfoPageAdapter(MovieActivity.this.getSupportFragmentManager(), response);
			pager.setAdapter(adapter);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setTitle(response.getMovieTitle());
			pager.setCurrentItem(1);
			
			flipper.setDisplayedChild(1);
			
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		pager.setCurrentItem(tab.getPosition());
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

}
