package com.yify.mobile;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import java.util.*;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yify.manager.ApiManager;
import com.yify.manager.FilterAdapter;
import com.yify.object.*;

public class MovieActivity extends ActionBarActivity {
	
	public static final int COVER = 1;
	public static final int SCREENSHOT = 2;
	
	private static final int REQ_START_STANDALONE_PLAYER = 1;
	private static final int REQ_RESOLVE_SERVICE_MISSING = 2;
	
	private final String developerKey = "AIzaSyCO25LlTSVOpP8R5ZA80iFyu3C2bI7m-rM";
	
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
		
		OnTouchListener listener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if(event.getAction() == MotionEvent.ACTION_MOVE) {
					return true;
				}
				
				return false;
			}
			
		};
		
		list.setOnTouchListener(listener);
		
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
			
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					switch(position) {
					case 0:
						Intent intent = new Intent(MovieActivity.this, GeneralActivity.class);
						intent.putExtra("item", response);
						startActivity(intent);
						break;
					case 1:
						Intent i = new Intent(MovieActivity.this, TorrentActivity.class);
						i.putExtra("item", response);
						startActivity(i);
						break;
					case 3:
						Intent m = YouTubeStandalonePlayer.createVideoIntent(MovieActivity.this, MovieActivity.this.developerKey, response.getYoutubeID());
						
						if(m != null) {
							
							if(canResolveIntent(m)) {
								
								startActivityForResult(m, MovieActivity.REQ_START_STANDALONE_PLAYER);
								
							} else {
								
								YouTubeInitializationResult.SERVICE_MISSING
									.getErrorDialog(MovieActivity.this, MovieActivity.REQ_RESOLVE_SERVICE_MISSING).show();
								
							}
							
						}
						
						break;
					case 2:
						Intent c = new Intent(MovieActivity.this, CommentActivity.class);
						c.putExtra("id", response.getMovieID());
						startActivity(c);
						break;
					default:
						break;
					}
					
				}
				
			});
			
			/* get the movie comment count. */
			new GetCommentCount().execute(response.getMovieID());
			
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(response.getMovieTitle());
			actionBar.setDisplayShowTitleEnabled(true);
			state.setDisplayedChild(1);
			
			
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQ_START_STANDALONE_PLAYER && resultCode != RESULT_OK) {
			YouTubeInitializationResult errorReason = 
					YouTubeStandalonePlayer.getReturnedInitializationResult(data);
			
			if(errorReason.isUserRecoverableError()) {
				errorReason.getErrorDialog(this, 0).show();
			} else {
				Toast.makeText(this, "An error occured processing your request: " + errorReason.toString(), Toast.LENGTH_SHORT).show();
			}
			
		}
	}
	
	private boolean canResolveIntent(Intent intent) {
		List<ResolveInfo> resolveInfo = this.getPackageManager().queryIntentActivities(intent, 0);
		return resolveInfo != null && !resolveInfo.isEmpty();
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
	
	private class GetCommentCount extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			
			/* dont need to check the internet, as already checked it with movie grab. */
			
			ApiManager manager = new ApiManager();
			return manager.getCommentCount(params[0]);
			
		}
		
		@Override
		protected void onPostExecute(Integer response) {
			
			@SuppressWarnings("unchecked")
			HashMap<String, String> entry = (HashMap<String, String>) adapter.getItem(2);	/* the comments entry */
			
			if((response == null) || (response == 0)) {
				entry.put("loading", "no");
				entry.put("icon", "no");
				entry.put("value", "0");
				entry.put("pressable", "no");
			} else {
				entry.put("loading", "no");
				entry.put("icon", "yes");
				entry.put("value", "" + response);
				entry.put("pressable", "yes");
			}
			
			
			adapter.notifyDataSetChanged();
			
		}
		
	}
	
	
	
	
}
