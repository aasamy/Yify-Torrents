package com.yify.mobile;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.SearchRecentSuggestions;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.yify.object.*;

import java.util.*;

import com.yify.manager.ApiManager;
import com.yify.manager.ProductAdapter;
import com.yify.mobile.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SearchActivity extends ActionBarActivity {
	
	private String quality = "ALL";
	private String genre = "ALL";
	private int rating = 0;
	private String order = "desc";
	private String sort = "date";
	private Menu mainMenu = null;
	private ActionBar actionBar;
	private String query;
	private ConnectivityDetector detector;
	protected boolean loadMore;
	private ArrayList<ListObject> items;
	private int set = 1;
	ProductAdapter<ListObject> adapter;
	private ListView listView;
	private View footerView;
	private ViewFlipper state;
	private TextView err;
	private Runnable loadMoreItems = new Runnable() {

		@Override
		public void run() {
			
			loadMore = true;
			
			/* get 20 more items based on the old query. */
			ApiManager manager = new ApiManager();
			items = manager.getList(query, quality, genre, rating, 20, set, sort, order);
			
			runOnUiThread(setNewItems);
			
		}
		
	};
	
	protected Runnable setNewItems = new Runnable() {
		
		@Override
		public void run() {
			
			if(detector.isConnectionAvailable()) {
			
			if((items != null) && (items.size() != 0)) {
				
				/* add the new items to the adapter. */
				for(int i = 0; i < items.size(); i++) {
					adapter.addItem(items.get(i));
				}
				
				adapter.notifyDataSetChanged();
				
				loadMore = false;
				
				set++;
				
				if(items.size() < 20) {
					listView.removeFooterView(footerView);
					loadMore = true;
				}
				
			} else {
				/* stop searching for items and hide footerView */
			}
			} else {
				listView.removeFooterView(footerView);
				loadMore = true;
			}
			
		}
		
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.search);
	    this.detector = new ConnectivityDetector(this);
	    setTitle("Search Results");
	    getActionBar().setDisplayShowTitleEnabled(true);
	    state = (ViewFlipper) findViewById(R.id.search_state);
	    err = (TextView) findViewById(R.id.search_no_results);
	    state.setDisplayedChild(0); /* show loading state */
	    //handle the intent
	    Intent intent = getIntent();
	    this.handleIntent(intent);
	    
	    
	}
	
	
	
	private void handleIntent(Intent intent) {
		
		if(Intent.ACTION_SEARCH.equals(intent.getAction())) {

			String query = intent.getStringExtra(SearchManager.QUERY);
			
			//search yify torrents for the query.
			
			String[] params = {query};
			this.query = query;
			
			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
			suggestions.saveRecentQuery(this.query, null);
			
			//search yify.
			new SearchMovies().execute(query);
			
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.main, menu);
		mainMenu = menu;
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		mainMenu.findItem(R.id.menu_search).setVisible(false);
		mainMenu.findItem(R.id.menu_refresh).setVisible(false);
		boolean bool = super.onCreateOptionsMenu(menu);
		return bool;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			case android.R.id.home:
				Intent home = new Intent(this, MainActivity.class);
				home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(home);
				break;
			case R.id.menu_filter:
				Intent filter = new Intent(this, FilterActivity.class);
				filter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Filter data = new Filter(query, quality, genre, rating, sort, order);
				filter.putExtra("filter", data);
				startActivity(filter);
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private class SearchMovies extends AsyncTask<String, Integer, ArrayList<ListObject>> {

		
		
		@Override
		protected ArrayList<ListObject> doInBackground(String... params) {
			listView = (ListView) findViewById(R.id.search_listView);
			if(!detector.isConnectionAvailable()) {
				return null;
			}
			
			ApiManager manager = new ApiManager();
			return manager.getList(params[0], quality, genre, rating, 20, set, sort, order);
			
		}
		
		@Override
		public void onPostExecute(ArrayList<ListObject> response) {
			
			if(response == null) {
				err.setText("You currently have no network connection.");
				state.setDisplayedChild(2);
				return;
			}
			
			if(response.size() == 0) {
				/* show no results found. */
				err.setText("No results where found.");
				state.setDisplayedChild(2);
				return;
			}
			
			footerView = ((LayoutInflater) SearchActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer, null, false);
			
			if(response.size() == 20) {
				listView.addFooterView(footerView);
			}
			
			listView.setFastScrollEnabled(true);
			adapter = new ProductAdapter<ListObject>(SearchActivity.this, response, true);
			listView.setAdapter(adapter);
			
			SearchActivity.this.set = 2; /*<-- finished loading initial first set, move to next*/
			
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					if(position == adapter.getCount()) {
						return;
					}
					
					ListObject o = (ListObject) adapter.getItem(position);
					Toast.makeText(getApplicationContext(), o.getFilesize(), Toast.LENGTH_SHORT).show();
					
				}
				
			});
			
			listView.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
						int totalItemCount) {
					
					int lastInScreen = firstVisibleItem + visibleItemCount;
					/* if the last entry is in view attempt to load more items */
					if((lastInScreen == totalItemCount) && (!loadMore)) {
						Thread thread = new Thread(null, loadMoreItems);
						thread.start();
					}
					
				}

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					// TODO Auto-generated method stub
					
				}
				
			});
			
			state.setDisplayedChild(1);
		}
		
	}
	
	
	
}
