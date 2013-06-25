package com.yify.mobile;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yify.manager.DatabaseManager;
import com.yify.object.AuthObject;
import com.yify.object.AuthUserObject;


public class MyAccountActivity extends ActionBarActivity {

	private AuthObject user;
	private Menu mainMenu;
	private ActionBar bar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.profile_layout);
		
		/* set up the actionbar title */
		bar = getActionBar();
		bar.setTitle("My Account");
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowTitleEnabled(true);
		final DatabaseManager manager = new DatabaseManager(this);
		
		Button logout = (Button) findViewById(R.id.logout);
		
		Button edit = (Button) findViewById(R.id.edit);
		edit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				Toast.makeText(getApplicationContext(), "Editing is currently unavailable.", Toast.LENGTH_SHORT).show();
				
			}
			
		});
		
		logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Toast.makeText(getApplicationContext(), "You have successfully logged out.", Toast.LENGTH_SHORT).show();
				manager.logout();
				finish();
				
			}
			
		});

		String hash = manager.getHash();
		
		if(hash == null) {
			Toast.makeText(getApplicationContext(), "No user is currently logged in.", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		new getUser(this, R.layout.profile_layout).execute();
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		case android.R.id.home : 
			finish();
			break;
		default :
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private class getUser extends AsyncTask<Integer, Integer, Boolean> {
		
		private DatabaseManager man;
		private AuthObject user;
		private Activity activity;
		private int usedView;
		
		public getUser(Activity activity, int usedView) {
			this.man = new DatabaseManager(activity);
			this.activity = activity;
			this.usedView = usedView;
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			
			ConnectivityDetector detect = new ConnectivityDetector(activity);
			
			if(!detect.isConnectionAvailable()) {
				return false; /* no connection available to get profile details. */
			}
			
			this.user = man.getLoggedInUser();
			
			if(user != null) {
				return true;
			}
			
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean isValid) {
			
			if(isValid) {
				
				AuthUserObject data = user.getUser();
				
				switch(this.usedView) {
				case R.layout.profile_layout :
					/* set the data to the view from the user */
					((TextView) activity.findViewById(R.id.user)).setText(data.getUsername());
					((TextView) activity.findViewById(R.id.joined)).setText(data.getJoinDate());
					((TextView) activity.findViewById(R.id.userole)).setText(data.getUserRole());
					
					String active = (data.isProfileActive()) ? "Active" : "Disabled";
					int color = (data.isProfileActive()) ? Color.GREEN : Color.RED;
					
					((TextView) activity.findViewById(R.id.active)).setText(active);
					((TextView) activity.findViewById(R.id.active)).setTextColor(color);
					
					((TextView) activity.findViewById(R.id.torrentsdownloaded)).setText("Torrents Downloaded: " + data.getTorrentsDownloaded());
					((TextView) activity.findViewById(R.id.commentcount)).setText("Comment Count: " + data.getCommentCount());
					((TextView) activity.findViewById(R.id.moviesrequested)).setText("Movies Requested: " + data.getMoviesRequested());
					((TextView) activity.findViewById(R.id.requestsleft)).setText("Remaining Requests: " + data.getNumRequestsLeft());
					((TextView) activity.findViewById(R.id.votesleft)).setText("Remaining Votes: " + data.getNumVotesLeft());
					
					/* set the profile picture */
					ImageView img = (ImageView) activity.findViewById(R.id.profilepicture);
					
					DisplayImageOptions options = new DisplayImageOptions.Builder()
					.cacheOnDisc()
					.cacheInMemory()
					.showImageForEmptyUri(R.drawable.defaultprofileimage)
					.showImageOnFail(R.drawable.defaultprofileimage)
					.showStubImage(R.drawable.defaultprofileimage)
					.build();
					
					ImageLoader.getInstance().displayImage(data.getAvatar(), img, options);
					
					ViewFlipper flipper = (ViewFlipper) activity.findViewById(R.id.profilestate);
					flipper.setDisplayedChild(1);
					
					break;
				default :
					break;
				}
				
			} else {
				
				Toast.makeText(getApplicationContext(), "Could not find logged in user.", Toast.LENGTH_SHORT).show();
				activity.finish();
				
			}
			
		}
		
	}
	
	
}
