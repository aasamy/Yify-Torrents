package com.yify.mobile;

import java.util.ArrayList;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.yify.manager.ApiManager;
import com.yify.manager.CommentAdapter;
import com.yify.object.CommentObject;

public class CommentReplyActivity extends ActionBarActivity {
	
	private Menu menu;
	private ActionBar bar;
	private ViewFlipper flipper;
	private TextView err;
	private int movieID;
	private ListView list;
	private int commentID;
	private String user;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		bar = getActionBar();
		setContentView(R.layout.search);
		
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowTitleEnabled(true);
		
		
		flipper = (ViewFlipper) findViewById(R.id.search_state);
		err = (TextView) findViewById(R.id.search_no_results);
		list = (ListView) findViewById(R.id.search_listView);
		
		list.setFastScrollEnabled(true);
		list.setTextFilterEnabled(true);
		
		Intent intent = getIntent();
		movieID = intent.getIntExtra("id", -1);
		commentID = intent.getIntExtra("cID", -1);
		
		bar.setTitle("Comment Replies");
		
		flipper.setDisplayedChild(0);
		
		if(movieID == -1) {
			err.setText("An error occured processing your request");
			flipper.setDisplayedChild(2);
		} else {
			new GetComments().execute(new Integer[]{movieID, commentID});
		}
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		case android.R.id.home :
			finish();
			break;
		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private class GetComments extends AsyncTask<Integer, Integer, ArrayList<CommentObject>> {

		private ConnectivityDetector detector = new ConnectivityDetector(CommentReplyActivity.this);
		
		@Override
		protected ArrayList<CommentObject> doInBackground(Integer... arg0) {
			
			if(!detector.isConnectionAvailable()) {
				return null;
			}
			
			ApiManager manager = new ApiManager();
			
			Log.d("Intent Data", "movieid : " + arg0[0] + ", commentid : " + arg0[1]);
			
			return manager.getCommentReplies(arg0[0], arg0[1]);
		}
		
		@Override
		protected void onPostExecute(ArrayList<CommentObject> response ){
			
			if(response == null) {
				err.setText("You Currently dont have a network connection");
				flipper.setDisplayedChild(2);
				return;
			}
			
			if(response.isEmpty()) {
				
				err.setText("There are no replies to this comment.");
				flipper.setDisplayedChild(2);
				return;
				
			}
			
			CommentAdapter adapter = new CommentAdapter(CommentReplyActivity.this, response);
			adapter.dontShowIcon();
			list.setAdapter(adapter);
			
			flipper.setDisplayedChild(1);
			
		}
		
	}
	

}
