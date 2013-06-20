package com.yify.mobile;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
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
import com.yify.manager.DatabaseManager;
import com.yify.manager.FilterAdapter;
import com.yify.object.*;

public class CommentActivity extends ActionBarActivity implements OnMenuItemClickListener, LoginDialog.LoginDialogListener, 
	ReplyDialog.ReplyListener{
	
	
	private Menu menu;
	private ActionBar bar;
	private ViewFlipper flipper;
	private TextView err;
	private int movieID;
	private ListView list;
	private DatabaseManager manager;
	private boolean isLoggedIn = false;
	public CommentAdapter adapter;
	private int commentID = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		bar = getActionBar();
		setContentView(R.layout.search);
		manager = new DatabaseManager(this);
		
		this.isLoggedIn = (manager.getLoggedInUserName() != null) ? true : false; 
		
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
			new GetComments(false).execute(movieID);
		}
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		case android.R.id.home :
			finish();
			break;
		case R.id.menu_home :
			Intent h = new Intent(CommentActivity.this, MainActivity.class);
			h.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(h);
			break;
		case R.id.menu_share :
			//open share intent to share URL of App in playstore.
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "TesterURL");
            startActivity(Intent.createChooser(shareIntent, "Share..."));
			break;
		case R.id.menu_refresh : 
			this.isLoggedIn = (this.manager.getLoggedInUserName() == null) ? false : true;
			if(!this.isLoggedIn) {
				DialogFragment log = new LoginDialog();
				log.show(getFragmentManager(), "login");
			} else {
				commentID = -1;
				DialogFragment reply = new ReplyDialog();
				reply.show(getFragmentManager(), "reply");
			}
			break;
		case R.id.menu_login : 
			this.isLoggedIn = (this.manager.getLoggedInUserName() == null) ? false : true;
			if(!this.isLoggedIn) {
				DialogFragment log = new LoginDialog();
				log.show(getFragmentManager(), "login");
			} else {
				/* show myaccount. */
				Intent my = new Intent(CommentActivity.this, MyAccountActivity.class);
				startActivity(my);
			}
			break;
		case R.id.menu_settings :
			Intent set = new Intent(this, SettingsActivity.class);
			startActivity(set);
			break;
		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		this.menu = menu;
		
		boolean end = super.onCreateOptionsMenu(menu);
		this.menu.findItem(R.id.menu_filter).setVisible(false);
		this.menu.findItem(R.id.menu_accept).setVisible(false);
		this.menu.findItem(R.id.menu_refresh).setIcon(R.drawable.content_new);
		this.menu.findItem(R.id.menu_search).setVisible(false);
		this.menu.findItem(R.id.menu_download).setVisible(false);
		
		return end;
	}
	
	private class GetComments extends AsyncTask<Integer, Integer, ArrayList<CommentObject>> {

		private ConnectivityDetector detector = new ConnectivityDetector(CommentActivity.this);
		private boolean update = false;
		
		public GetComments(boolean update) {
			this.update = update;
		}
		
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
			
			if(!this.update) {
				adapter = new CommentAdapter(CommentActivity.this, response);
				list.setAdapter(adapter);
			} else {
				adapter.setItems(response);
				adapter.notifyDataSetChanged();
			}
			
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
			commentID = item.getGroupId();
			this.isLoggedIn = (this.manager.getLoggedInUserName() == null) ? false : true;
			if(!this.isLoggedIn) {
				/* start log in activity. */
				DialogFragment login = new LoginDialog();
				login.show(getFragmentManager(), "login");
			} else {
				/* user logged in. */
				DialogFragment reply = new ReplyDialog();
				reply.show(getFragmentManager(), "reply");
			}
			
			break;
		}
		
		return false;
	}
	@Override
	public void onSignInPressed(DialogFragment fragment, View v,
			String userinput, String passinput) {
		
		ViewFlipper flipper = (ViewFlipper) v.findViewById(R.id.loginstate);
		flipper.setDisplayedChild(1);
		
		ConnectivityDetector detect = new ConnectivityDetector(this);
		
		new Login(this.isLoggedIn, this.manager, detect, v, fragment, this).execute(new String[]{userinput, passinput});
		this.isLoggedIn = (this.manager.getLoggedInUserName() == null) ? false : true;
		
	}
	@Override
	public void onReplyPressed(DialogFragment fragment, View view) {
		
		/* try and reply to the comment */
		ViewFlipper flip = (ViewFlipper) view.findViewById(R.id.replystate);
		flip.setDisplayedChild(1);
		
		new Reply(CommentActivity.this, fragment, view, commentID, movieID).execute();
		
		
	}
	@Override
	public void onCancelPressed(DialogFragment fragment) {
		
		fragment.dismiss();
		
	}
	
	private class Reply extends AsyncTask<Integer, Integer, Boolean> {
		
		private ConnectivityDetector detect;
		private DatabaseManager manager;
		private DialogFragment fragment;
		private View view;
		private ApiManager api;
		private int commentID = -1;
		private int movieID = -1;
		private Activity activity;
		
		
		public Reply(Activity activity, DialogFragment fragment, View view, int commentID, 
				int movieID) {
			this.detect = new ConnectivityDetector(activity);
			this.manager = new DatabaseManager(activity);
			this.fragment = fragment;
			this.view = view;
			this.api = new ApiManager();
			this.movieID = movieID;
			this.commentID = commentID;
			this.activity = activity;
		}

		@Override
		protected Boolean doInBackground(Integer... arg0) {
			
			String hash = manager.getHash();
			if(hash == null) {
				return false;
			}
			
			EditText text = (EditText) this.view.findViewById(R.id.editText1);
			
			if(!detect.isConnectionAvailable()) {
				Toast.makeText(activity, "No Network Connection....", Toast.LENGTH_SHORT).show();
				return false;
			}
			
			String response = api.postComment(text.getText().toString(), movieID, commentID, hash);
			
			if(response == null) {
				return true;
			}
			
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean response) {
			
			if(response) {
				/* close the dialog. */
				CommentActivity.this.flipper.setDisplayedChild(0);
				new GetComments(true).execute(movieID);
				this.fragment.dismiss();
				
			} else {
				/* change the view back. */
				ViewFlipper flipper = (ViewFlipper) view.findViewById(R.id.replystate);
				flipper.setDisplayedChild(0);
			}
			
		}
	}
	
	

}
