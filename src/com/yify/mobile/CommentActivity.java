package com.yify.mobile;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import java.util.*;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yify.manager.ApiManager;
import com.yify.manager.CommentAdapter;
import com.yify.manager.FilterAdapter;
import com.yify.object.*;

public class CommentActivity extends ActionBarActivity implements OnMenuItemClickListener {
	
	
	private Menu menu;
	private ActionBar bar;
	private ViewFlipper flipper;
	private TextView err;
	private int movieID;
	private ListView list;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		bar = getActionBar();
		setContentView(R.layout.search);
		
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowTitleEnabled(true);
		bar.setTitle("Comments");
		
		flipper = (ViewFlipper) findViewById(R.id.search_state);
		err = (TextView) findViewById(R.id.search_no_results);
		list = (ListView) findViewById(R.id.search_listView);
		
		list.setFastScrollEnabled(true);
		list.setTextFilterEnabled(true);
		
		Intent intent = getIntent();
		movieID = intent.getIntExtra("id", -1);
		
		flipper.setDisplayedChild(0);
		
		if(movieID == -1) {
			err.setText("An error occured processing your request");
			flipper.setDisplayedChild(2);
		} else {
			new GetComments().execute(movieID);
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

		private ConnectivityDetector detector = new ConnectivityDetector(CommentActivity.this);
		
		@Override
		protected ArrayList<CommentObject> doInBackground(Integer... arg0) {
			
			if(!detector.isConnectionAvailable()) {
				return null;
			}
			
			ApiManager manager = new ApiManager();
			
			return manager.getMovieComments(arg0[0], false);
		}
		
		@Override
		protected void onPostExecute(ArrayList<CommentObject> response ){
			
			if(response == null) {
				err.setText("You Currently dont have a network connection");
				flipper.setDisplayedChild(2);
				return;
			}
			
			if(response.isEmpty()) {
				
				err.setText("There are no comments for this movie.");
				flipper.setDisplayedChild(2);
				return;
				
			}
			
			CommentAdapter adapter = new CommentAdapter(CommentActivity.this, response);
			list.setAdapter(adapter);
			
			flipper.setDisplayedChild(1);
			
		}
		
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		
		switch(item.getItemId()) {
		case R.id.refresh_twitter:
			/* show replies. */
			Intent intent = new Intent(CommentActivity.this, CommentReplyActivity.class);
			intent.putExtra("id", movieID); intent.putExtra("cID", item.getGroupId());
			startActivity(intent);
			
			break;
		case R.id.show_twitter:
			/* reply to this comment.
			 * check is logged in, if not start log in activity, else start commentpost activity. */
			Toast.makeText(this, ""+item.getGroupId() + ", " + movieID, Toast.LENGTH_LONG).show();
			break;
		}
		
		return false;
	}
	
	

}
