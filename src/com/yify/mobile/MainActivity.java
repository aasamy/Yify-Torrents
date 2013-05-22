package com.yify.mobile;

import android.os.AsyncTask;
import android.os.Build;
import java.util.ArrayList;
import java.util.HashMap;
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
	private ActionBar actionBar;
	private ConnectivityDetector detector;
	private ViewFlinger upcomingFlinger;
	private SearchView searchView; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		actionBar = getActionBar();
		upcomingflipper = (ViewFlipper) findViewById(R.id.upcoming_state);
		upcomingFlinger = (ViewFlinger) findViewById(R.id.upcoming_flinger);
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
		
		/* get the upcoming films. */
		new getUpcoming().execute("");
		
		return end;
	}
	
	@Override
	public void startActivity(Intent intent) {
		if(!detector.isConnectionAvailable()) {
			Toast.makeText(this, "Network not available, Please make sure you are connected to a network.", Toast.LENGTH_SHORT).show();
			if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
				searchView.setIconified(true);
			}
			return;
		}
		super.startActivity(intent);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			case R.id.menu_refresh:
				TextView view = (TextView) MainActivity.this.findViewById(R.id.upcoming_err);
				ProgressBar loading = (ProgressBar) MainActivity.this.findViewById(R.id.loading_upcoming);
				view.setVisibility(View.GONE);
				loading.setVisibility(View.VISIBLE);
				upcomingflipper.setDisplayedChild(0);
				if(!detector.isConnectionAvailable()) {
					Toast.makeText(this, "No Network Connection....", Toast.LENGTH_SHORT).show();
					
				} else  {
					Toast.makeText(this, "Refreshing Upcoming Movie Feed...", Toast.LENGTH_SHORT).show();
				}
				getActionBarHelper().setRefreshActionItemState(true);
				
				new getUpcoming().execute("");
				break;
			case R.id.menu_search:
				Toast.makeText(this, "Search Pressed", Toast.LENGTH_SHORT).show();
				if(!detector.isConnectionAvailable()) {
					return true;
				}
				return false;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private class getUpcoming extends AsyncTask<String, Integer, ArrayList<UpcomingObject>> {
		
		private ConnectivityDetector detector = new ConnectivityDetector(
				MainActivity.this);
		
		@Override
		protected ArrayList<UpcomingObject> doInBackground(String... params) {
			if(!detector.isConnectionAvailable()) {
				return null;
			}
			
			ApiManager manager = new ApiManager();
			return manager.getUpcoming();
			
		}
		
		@Override
		public void onPostExecute(final ArrayList<UpcomingObject> response) {
			
			final LayoutInflater inflater = MainActivity.this.getLayoutInflater();
			TextView view = (TextView) MainActivity.this.findViewById(R.id.upcoming_err);
			ProgressBar loading = (ProgressBar) MainActivity.this.findViewById(R.id.loading_upcoming);
			
			if(!detector.isConnectionAvailable()) {
				view.setVisibility(View.VISIBLE);
				view.setText("You Currently Have No Network Connection");
				getActionBarHelper().setRefreshActionItemState(false);
				loading.setVisibility(View.GONE);
				return;
			}
			
			if((response == null) || (response.isEmpty())) {
				view.setVisibility(View.VISIBLE);
				view.setText("There is no upcoming movies to display.");
				getActionBarHelper().setRefreshActionItemState(false);
				loading.setVisibility(View.GONE);
				return;
			}
			
			MainActivity.this.upcomingFlinger.removeAllViews();
			
			/* add all of the upcoming films to the flinger. */
			for(int i = 0; i < response.size(); i++) {
				
				@SuppressWarnings("unchecked")
				View newView = inflater.inflate(R.layout.item, null);
				
				TextView text1 = (TextView) newView.findViewById(R.id.text);
				TextView text2 = (TextView) newView.findViewById(R.id.text2);
				ImageView img = (ImageView) newView.findViewById(R.id.image);
				
				text1.setText(response.get(i).getMovieTitle());
				text2.setText("Uploaded by: " + response.get(i).getUploader());
				com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(response.get(i).getMovieCover(), img);
				
				MainActivity.this.upcomingFlinger.addView(newView);
			}
			
			MainActivity.this.upcomingflipper.setDisplayedChild(1);
			getActionBarHelper().setRefreshActionItemState(false);
		}
		
	}
	
	private class getLatestAndPopular extends AsyncTask<String, Integer, HashMap<String, ArrayList<ListObject>>>  {

		@Override
		protected HashMap<String, ArrayList<ListObject>> doInBackground(String... arg0) {
			if(!MainActivity.this.detector.isConnectionAvailable()) {
				return null;
			}
			ApiManager manager = new ApiManager();
			HashMap<String, ArrayList<ListObject>> data = new HashMap<String, ArrayList<ListObject>>();
			
			data.put("popular", manager.getList(null, null, null, 0, 10, 1, "downloaded", "desc"));
			data.put("latest", manager.getList(null, null, null, 0, 10, 1, "date", "desc"));
			
			return data;
			
		}
		
		@Override
		public void onPostExecute(HashMap<String, ArrayList<ListObject>> response) {
			
			/* check connection, and check variables are set. */
			
			/* set each of the viewflingers with the data, and then set the flipper view to active from loading
			 * then stop the refresh icon, and done. */
			
		}
		
	}

}
