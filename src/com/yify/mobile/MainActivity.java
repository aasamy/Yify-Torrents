package com.yify.mobile;

import android.os.AsyncTask;
import android.os.Build;
import java.util.ArrayList;
import java.util.HashMap;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yify.manager.ApiManager;
import com.yify.manager.DatabaseManager;
import com.yify.manager.ProductAdapter;
import com.yify.object.AuthObject;
import com.yify.object.CommentObject;
import com.yify.object.ListObject;
import com.yify.object.UpcomingObject;
import com.yify.view.ViewFlinger;
import android.widget.ListView;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnSuggestionListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.view.MenuItem;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MainActivity extends ActionBarActivity {
	
	private Menu mainMenu = null;
	private ViewFlipper upcomingflipper;
	private ConnectivityDetector detector;
	private ViewFlipper popularFlipper;
	private ViewFlipper latestFlipper;
	private SearchView searchView; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		upcomingflipper = (ViewFlipper) findViewById(R.id.upcoming_state);
		latestFlipper = (ViewFlipper) findViewById(R.id.latest_state);
		popularFlipper = (ViewFlipper) findViewById(R.id.popular_state);
		detector = new ConnectivityDetector(this);
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("SAVED_INDEX", 1);
	}
	
	@Override
	public boolean onSearchRequested() {
		
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		mainMenu = menu;
		
		//set up the default search service.
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		//call superclass constructor.
		boolean end = super.onCreateOptionsMenu(menu);
		mainMenu.findItem(R.id.menu_home).setVisible(false); /* <-- already on home. */
		mainMenu.findItem(R.id.menu_filter).setVisible(false);
		
		//execute background task to grab upcoming movies.
		getActionBarHelper().setRefreshActionItemState(true);
		upcomingflipper.setDisplayedChild(0);
		latestFlipper.setDisplayedChild(0);
		popularFlipper.setDisplayedChild(0);
		/* get the upcoming films */
		new MainAsync<String, Integer, UpcomingObject>().execute(new String[] {"UP"});
		/* get the latest films. */
		new MainAsync<String, Integer, ListObject>().execute(new String[] {"LIST", "LAT"});
		/* get popular films */
		new MainAsync<String, Integer, ListObject>().execute(new String[] {"LIST", "POP"});
		
		return end;
	}
	
//	@Override
//	public void startActivity(Intent intent) {
//		if(!detector.isConnectionAvailable()) {
//			Toast.makeText(this, "Network not available, Please make sure you are connected to a network.", Toast.LENGTH_SHORT).show();
//			if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
//				searchView.setIconified(true);
//			}
//			//return;
//		}
//		super.startActivity(intent);
//	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			case R.id.menu_refresh:
				
				TextView[] t = {(TextView) findViewById(R.id.upcoming_err), (TextView) findViewById(R.id.popular_err), (TextView) findViewById(R.id.latest_err)};
				ProgressBar[] pb = {(ProgressBar) findViewById(R.id.loading_upcoming), (ProgressBar) findViewById(R.id.loading_popular), (ProgressBar) findViewById(R.id.loading_latest)};
				
				upcomingflipper.setDisplayedChild(0);
				latestFlipper.setDisplayedChild(0);
				popularFlipper.setDisplayedChild(0);
				if(!detector.isConnectionAvailable()) {
					Toast.makeText(this, "No Network Connection....", Toast.LENGTH_SHORT).show();
					
				} else  {
					for(TextView t1 : t) {
						t1.setVisibility(View.GONE);
					}
					for(ProgressBar p : pb) {
						p.setVisibility(View.VISIBLE);
					}
					Toast.makeText(this, "Refreshing Movie Feeds...", Toast.LENGTH_SHORT).show();
				}
				getActionBarHelper().setRefreshActionItemState(true);
				/* get the upcoming films */
				new MainAsync<String, Integer, UpcomingObject>().execute(new String[] {"UP"});
				/* get the latest films. */
				new MainAsync<String, Integer, ListObject>().execute(new String[] {"LIST", "LAT"});
				/* get popular films */
				new MainAsync<String, Integer, ListObject>().execute(new String[] {"LIST", "POP"});
				
				break;
			case R.id.menu_search:
				Toast.makeText(this, "Search Pressed", Toast.LENGTH_SHORT).show();
				if(!detector.isConnectionAvailable()) {
					return true;
				}
				return false;
			case R.id.menu_share:
            	//open share intent to share URL of App in playstore.
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "TesterURL");
                startActivity(Intent.createChooser(shareIntent, "Share..."));
                break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private class MainAsync<Params, Result, T extends UpcomingObject> extends AsyncTask<Params, Result, ArrayList<T>> {
		
		/* a layout inflater to use. */
		final LayoutInflater inflater = MainActivity.this.getLayoutInflater();
		
		/* set the views to update. */
		int listToUpdate = -1;
		
		/* the view variables. */
		ViewFlinger fln;
		ViewFlipper flp;
		TextView t;
		ProgressBar  pb;
		
		/* a connectivity detector to use. */
		ConnectivityDetector detector = new ConnectivityDetector(MainActivity.this);

		@SuppressWarnings("unchecked")
		@Override
		protected ArrayList<T> doInBackground(Params... params) {
			
			if(params[0].equals("LIST")) {
				this.listToUpdate = (params[1].equals("POP")) ? 0 : 1;
			} else {
				this.listToUpdate = 2;
			}
			
			int tResource; int pbResource; int flnResource; int flpResource;
			
			switch(this.listToUpdate) {
				case 0:
					tResource = R.id.popular_err;
					pbResource = R.id.loading_popular;
					flnResource = R.id.popular_flinger;
					flpResource = R.id.popular_state;
					break;
				case 1:
					tResource = R.id.latest_err;
					pbResource = R.id.loading_latest;
					flnResource = R.id.latest_flinger;
					flpResource = R.id.latest_state;
					break;
				case 2:
					tResource = R.id.upcoming_err;
					pbResource = R.id.loading_upcoming;
					flnResource = R.id.upcoming_flinger;
					flpResource = R.id.upcoming_state;
					break;
				default:
					return null; /* could not find what process this was for. */
			}
			
			/* inflate the resources. */
			fln = (ViewFlinger) MainActivity.this.findViewById(flnResource);
			flp = (ViewFlipper) MainActivity.this.findViewById(flpResource);
			t = (TextView) MainActivity.this.findViewById(tResource);
			pb = (ProgressBar) MainActivity.this.findViewById(pbResource);
			
			/* set to default */
			//pb.setVisibility(View.VISIBLE);
			//t.setVisibility(View.GONE);
			
			if(!this.detector.isConnectionAvailable()) {
				return null;
			}
			
			ApiManager manager = new ApiManager();
			
			Params function = params[0];
			
			ArrayList<T> data = null;
			
			if(function.equals("LIST")) {
				
				Params subaction = params[1];
				
				if(subaction.equals("POP")) {
					
					 data = (ArrayList<T>) manager.getList(null, null, null, 0, 10, 1, "downloaded", "desc");
					
				} else {
					
					 data = (ArrayList<T>) manager.getList(null, null, null, 0, 10, 1, "date", "desc");
					
				}
				
			} else {
				
				data = (ArrayList<T>) manager.getUpcoming();
				
			}
			
			return data;
		}
		
		@Override
		protected void onPostExecute(ArrayList<T> response) {
			
			boolean valid = true;
			
			/* check internet connectivity */
			if(!this.detector.isConnectionAvailable()) {
				this.t.setText("You are currently not connected to a network.");
				valid = false;
			}
			
			if(valid) {
				if((response == null) || response.isEmpty()) {
					this.t.setText("There are no movies to display.");
					valid = false;
				}
			}
			
			if(!valid) {
				this.pb.setVisibility(View.GONE);
				this.t.setVisibility(View.VISIBLE);
				if(this.listToUpdate == 0) { /* i know this is the last process to run. */
					getActionBarHelper().setRefreshActionItemState(false);
				}
				return;
			}
			
			/* destroy all old views, so it doesnt keep adding the same thing. */
			this.fln.removeAllViews();
			
			/* inflate the new views based on the response and change the state. */
			for(int i = 0; i < response.size(); i++) {
				
				View newView = this.inflater.inflate(R.layout.item, null);
				
				TextView t1 = (TextView) newView.findViewById(R.id.text);
				TextView t2 = (TextView) newView.findViewById(R.id.text2);
				ImageView img = (ImageView) newView.findViewById(R.id.image);
				
				final T item = response.get(i);
				String mt = (item instanceof ListObject) ? "Genre: " + ((ListObject) item).getGenre() + ", Downloaded " + ((ListObject) item).getDownloaded() + " times" : "Uploaded by: " + item.getUploader();
				OnClickListener click = (item instanceof ListObject) ? new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						Toast.makeText(getApplicationContext(), "Tapped: " + ((ListObject) item).getMovieID(), Toast.LENGTH_SHORT).show();
						
					}
					
				} : null;
				t1.setText(item.getMovieTitle()); t2.setText(mt);
				if(click != null ) {
					newView.setOnClickListener(click);
				}
				ImageLoader.getInstance().displayImage(item.getMovieCover(), img);
				
				this.fln.addView(newView);
				
			}
			
			/* change the state, and stop the refresh icon. */
			this.flp.setDisplayedChild(1);
			if(this.listToUpdate == 0) {
				getActionBarHelper().setRefreshActionItemState(false);
			}
			
			
		}
		
	}

}
