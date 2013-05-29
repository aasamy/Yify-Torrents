package com.yify.mobile;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Gallery;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import java.util.*;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yify.manager.ApiManager;
import com.yify.manager.FilterAdapter;
import com.yify.object.*;

public class MovieActivity extends ActionBarActivity {
	
	public static final int COVER = 1;
	public static final int SCREENSHOT = 2;
	
	private LinearLayout imageScroll;
	private ActionBar actionBar;
	private ViewFlipper state;
	private TextView err;
	private ConnectivityDetector detector;
	private LayoutInflater inflater;
	private ListView list;
	private FilterAdapter<String> adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.pager_layout);
		
		actionBar = getActionBar();
		imageScroll = (LinearLayout) findViewById(R.id.imageViewInner);
		state = (ViewFlipper) findViewById(R.id.movie_state);
		err = (TextView) findViewById(R.id.movie_err);
		detector = new ConnectivityDetector(this);
		inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		list = (ListView) findViewById(R.id.movie_info_listview);
		
		state.setDisplayedChild(0);
		
		Intent intent = getIntent();
		int movieid = intent.getIntExtra("id", -1);
		
		if(movieid != -1) {
			new GetMovie().execute(movieid);
		} else {
			err.setText("There was an error retrieving movie information");
			state.setDisplayedChild(2);
		}
		
	}
	
	private class GetMovie extends AsyncTask<Integer, Integer, ItemObject> {

		@Override
		protected ItemObject doInBackground(Integer... arg0) {
			
			if(!detector.isConnectionAvailable()) {
				return null;
			}
			
			ApiManager manager = new ApiManager();
			return manager.getMovieDetails(arg0[0]);
		}
		
		@Override
		protected void onPostExecute(final ItemObject response) {
			
			if((response == null) || (!(response instanceof ItemObject))) {
				err.setText("You currently do not have a network connection");
				state.setDisplayedChild(2);
				return;
			}
			
			View c = inflater.inflate(R.layout.image_cover, null);
			
			ImageView iv = (ImageView) c.findViewById(R.id.imagebutton_movie);
			
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			
			ImageLoader.getInstance().displayImage(response.getLargeCover(), iv);
			
			iv.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					
					Intent intent = new Intent(MovieActivity.this, FullScreenActivity.class);
					intent.putExtra("type", MovieActivity.COVER);
					intent.putExtra("item", response.getLargeCover());
					startActivity(intent);
					
				}
				
			});
			
			imageScroll.addView(c);
			
			MovieActivity.this.sortOutScreenshots(response.getScreenshots());
			
			ArrayList<HashMap<String, String>> fi = new ArrayList<HashMap<String, String>>();
			
			HashMap<String, String> entry = new HashMap<String, String>();
			entry.put("main", "General Information"); entry.put("sub", response.getShortDescription().substring(0, 70) + "...");
			entry.put("value", ""); entry.put("icon", "yes"); entry.put("pressable", "yes"); entry.put("loading", "no");
			fi.add(entry);
			
			entry = new HashMap<String, String>();
			entry.put("main", "Torrent Information"); entry.put("sub", "Seeds/Peers: " + response.getTorrentSeeds() + "/" + response.getTorrentPeers());
			entry.put("value", ""); entry.put("icon", "yes"); entry.put("pressable", "yes"); entry.put("loading", "no");
			fi.add(entry);
			
			entry = new HashMap<String, String>();
			entry.put("main", "Comments"); entry.put("sub", ""); entry.put("value", ""); entry.put("icon", "no"); entry.put("pressable", "no");
			entry.put("loading", "yes");
			fi.add(entry);
			
			entry = new HashMap<String, String>();
			entry.put("main", "Trailer"); entry.put("sub", ""); entry.put("value", ""); entry.put("icon", "yes"); entry.put("pressable", "yes");
			entry.put("loading", "no");
			fi.add(entry);
			
			adapter = new FilterAdapter<String>(MovieActivity.this, fi, true);
			list.setAdapter(adapter);
			
			/* get the movie comment count. */
			new GetCommentCount().execute(response.getMovieID());
			
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(response.getMovieTitle());
			actionBar.setDisplayShowTitleEnabled(true);
			state.setDisplayedChild(1);
			
			
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		case android.R.id.home : 
			finish();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void sortOutScreenshots(final String[] screenshots) {
		
		/* med 1 */
		View m1 = inflater.inflate(R.layout.image_layout, null);
		ImageView im1 = (ImageView) m1.findViewById(R.id.imagebutton_movie);
		ImageLoader.getInstance().displayImage(screenshots[0], im1);
		im1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				Intent intent = new Intent(MovieActivity.this, FullScreenActivity.class);
				intent.putExtra("type", MovieActivity.SCREENSHOT);
				intent.putExtra("item", screenshots[0]);
				startActivity(intent);
				
			}
			
		});
		
		imageScroll.addView(m1);
		
		/* med2 */
		View m2 = inflater.inflate(R.layout.image_layout, null);
		ImageView im2 = (ImageView) m2.findViewById(R.id.imagebutton_movie);
		ImageLoader.getInstance().displayImage(screenshots[1], im2);
		im2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				Intent intent = new Intent(MovieActivity.this, FullScreenActivity.class);
				intent.putExtra("type", MovieActivity.SCREENSHOT);
				intent.putExtra("item", screenshots[1]);
				startActivity(intent);
				
			}
			
		});
		
		imageScroll.addView(m2);
		
		/* med3 */
		View m3 = inflater.inflate(R.layout.image_layout, null);
		ImageView im3 = (ImageView) m3.findViewById(R.id.imagebutton_movie);
		ImageLoader.getInstance().displayImage(screenshots[2], im3);
		im3.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				Intent intent = new Intent(MovieActivity.this, FullScreenActivity.class);
				intent.putExtra("type", MovieActivity.SCREENSHOT);
				intent.putExtra("item", screenshots[2]);
				startActivity(intent);
				
			}
			
		});
		
		imageScroll.addView(m3);
		
	}
	
	private class GetCommentCount extends AsyncTask<Integer, Integer, ArrayList<CommentObject>> {

		@Override
		protected ArrayList<CommentObject> doInBackground(Integer... params) {
			
			/* dont need to check the internet, as already checked it with movie grab. */
			
			ApiManager manager = new ApiManager();
			return manager.getMovieComments(params[0]);
			
		}
		
		@Override
		protected void onPostExecute(ArrayList<CommentObject> response) {
			
			@SuppressWarnings("unchecked")
			HashMap<String, String> entry = (HashMap<String, String>) adapter.getItem(2);	/* the comments entry */
			
			if((response == null) || (response.size() == 0)) {
				entry.put("loading", "no");
				entry.put("icon", "no");
				entry.put("value", "0");
				entry.put("pressable", "no");
			} else {
				entry.put("loading", "no");
				entry.put("icon", "yes");
				entry.put("value", "" + response.size());
				entry.put("pressable", "yes");
			}
			
			
			adapter.notifyDataSetChanged();
			
		}
		
	}
	
	
	
	
}
