package com.yify.mobile;

import android.os.AsyncTask;
import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		new GetUpcoming().execute("");
		return true;
	}
	
	private class GetUpcoming extends AsyncTask<String, Integer, ArrayList<ListObject>> {

		@Override
		protected ArrayList<ListObject> doInBackground(String... arg0) {
			ApiManager manager = new ApiManager();
			return manager.getList(null, null, null, 0, 20, 1,
					null, null);
			
		}
		
		@Override
		public void onPostExecute(ArrayList<ListObject> response) {
			Log.d("Async", "AsyncTask started");
			if(response.size() != 0) {
				
				for(int i = 0; i < response.size(); i++) {
					
					Log.d("Entry", response.get(i).getMovieTitle());
					
				}
				
			}
			
		}
		
	}

}
