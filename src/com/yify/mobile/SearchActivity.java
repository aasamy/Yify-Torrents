package com.yify.mobile;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yify.object.*;

import java.util.*;

import com.yify.manager.ApiManager;
import com.yify.manager.ProductAdapter;
import com.yify.mobile.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SearchActivity extends ActionBarActivity {

	private Menu mainMenu = null;
	private ActionBar actionBar;
	private String query;
	private TextView resultsText;
	private ConnectivityDetector detector;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.search);
	    this.detector = new ConnectivityDetector(this);
	    
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
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private class SearchMovies extends AsyncTask<String, Integer, ArrayList<ListObject>> {

		private ListView listView = (ListView) findViewById(R.id.search_listView);
		
		@Override
		protected ArrayList<ListObject> doInBackground(String... params) {
			
			if(!detector.isConnectionAvailable()) {
				return null;
			}
			
			ApiManager manager = new ApiManager();
			return manager.getList(params[0], null, null, 0, 20, 1, null, null);
			
		}
		
		@Override
		public void onPostExecute(ArrayList<ListObject> response) {
			listView.setFastScrollEnabled(true);
			final ProductAdapter<ListObject> adapter = new ProductAdapter<ListObject>(SearchActivity.this, response, true);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					ListObject o = (ListObject) adapter.getItem(position);
					Toast.makeText(getApplicationContext(), o.getFilesize(), Toast.LENGTH_SHORT).show();
					
				}
				
			});
		}
		
	}
	
}
