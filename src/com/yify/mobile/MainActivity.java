package com.yify.mobile;

import android.os.AsyncTask;
import android.os.Build;
import java.util.ArrayList;
import com.yify.manager.ApiManager;
import com.yify.manager.DatabaseManager;
import com.yify.manager.ProductAdapter;
import com.yify.object.AuthObject;
import com.yify.object.CommentObject;
import com.yify.object.UpcomingObject;
import android.widget.ListView;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.view.MenuItem;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MainActivity extends ActionBarActivity {
	
	private Menu mainMenu = null;
	private ViewFlipper flipper;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		flipper = (ViewFlipper) findViewById(R.id.main_view_state);
		
		if(savedInstanceState != null) {
			int savedIndex = savedInstanceState.getInt("SAVED_INDEX");
			flipper.setDisplayedChild(savedIndex);
		} else {
			flipper.setDisplayedChild(0);
		}
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("SAVED_INDEX", 1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		mainMenu = menu;
		
		//set up the default search service.
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		
		//call superclass constructor.
		boolean end = super.onCreateOptionsMenu(menu);
		mainMenu.findItem(R.id.menu_home).setVisible(false); /* <-- already on home. */
		
		//execute background task to grab upcoming movies.
		getActionBarHelper().setRefreshActionItemState(true);
		flipper = (ViewFlipper) findViewById(R.id.main_view_state);
		flipper.setDisplayedChild(0);
		
		listView = (ListView) findViewById(R.id.upcoming_list);
		
		/* get the upcoming films. */
		new getUpcoming().execute("");
		
		return end;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			case R.id.menu_refresh:
				TextView view = (TextView) MainActivity.this.findViewById(R.id.err_message);
				ProgressBar loading = (ProgressBar) MainActivity.this.findViewById(R.id.main_loading_bar);
				view.setVisibility(View.GONE);
				loading.setVisibility(View.VISIBLE);
				flipper.setDisplayedChild(0);
				Toast.makeText(this, "Refreshing Upcoming Movie Feed...", Toast.LENGTH_SHORT).show();
				getActionBarHelper().setRefreshActionItemState(true);
				
				new getUpcoming().execute("");
				break;
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
		public void onPostExecute(ArrayList<UpcomingObject> response) {
			
			final LayoutInflater inflater = MainActivity.this.getLayoutInflater();
			TextView view = (TextView) MainActivity.this.findViewById(R.id.err_message);
			ProgressBar loading = (ProgressBar) MainActivity.this.findViewById(R.id.main_loading_bar);
			
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
			
			
			MainActivity.this.listView.setFastScrollEnabled(true);
			final ProductAdapter adapter = new ProductAdapter(MainActivity.this, response, false);
			MainActivity.this.listView.setAdapter(adapter);
			MainActivity.this.listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					UpcomingObject o = (UpcomingObject) adapter.getItem(position);
					Toast.makeText(getApplicationContext(), o.getImdbCode(), Toast.LENGTH_SHORT).show();
					
				}
				
			});
			MainActivity.this.flipper.setDisplayedChild(1);
			getActionBarHelper().setRefreshActionItemState(false);
		}
		
	}

}
