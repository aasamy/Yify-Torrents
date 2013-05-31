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

public class CommentActivity extends ActionBarActivity {
	
	
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
		
		Intent intent = getIntent();
		movieID = intent.getIntExtra("id", -1);
		
		flipper.setDisplayedChild(0);
		
		if(movieID == -1) {
			err.setText("An error occured processing your request");
			flipper.setDisplayedChild(2);
		} else {
			/* search for the comments. */
		}
		
	}
	
	

}
