package com.yify.mobile;

import android.os.AsyncTask;
import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	
	DatabaseManager db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		db = new DatabaseManager(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		new GetUpcoming().execute("");
		return true;
	}
	
	private class GetUpcoming extends AsyncTask<String, Integer, ArrayList<CommentObject>> {

		@Override
		protected ArrayList<CommentObject> doInBackground(String... arg0) {
			ApiManager manager = new ApiManager();
			
			String[] results = manager.login("jacktimblin@gmail.com", "monster1");
			
			Log.d("response", results[0]);
			
			if(results.length > 1) {
				db.addAuth(results[0], Integer.parseInt(results[1]), results[2]);
			}
			
			AuthObject auth = db.getLoggedInUser();
			
			if(auth != null) {
				
				Log.d("User", "User is Logged in: " + auth.getUser().getUsername() + ", Join date is: " + auth.getUser().getJoinDate());
				
			}
			
			return manager.getMovieComments(34345454);
			
		}
		
		@Override
		public void onPostExecute(ArrayList<CommentObject> response) {
			Log.d("Async", "AsyncTask started");
			
			if((response != null) && (response.size() != 0)) {
				
				for(int i = 0; i < response.size(); i++) {
					
					Log.d("Comment", response.get(i).getText());
					
				}
				
			}
			
			Log.d("Comment", "No comments found.");
			
		}
		
	}

}
