package com.yify.mobile;

import java.util.ArrayList;
import java.util.HashMap;

import com.yify.object.CustomDialog;
import com.yify.object.Filter;
import com.yify.manager.FilterAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FilterActivity extends FragmentActivity implements CustomDialog.CustomDialogListener {
	
	private Menu mainMenu = null;
	private ActionBar actionBar;
	private Filter filter;
	public static final String CUSTOM_FILTER_INTENT = "ACTION_SEARCH_FILTER_CUSTOM";
	private FilterAdapter<String> fa;
	private FilterAdapter<String> sa;
	private AlertDialog.Builder builder;
	private TextView ratingTv;
	private EditText text;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filter); /* change to filter view after intent testing finished. */
		Intent intent = getIntent();
		
		/* set the keyboard not to come up auto. */
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		/* grab intent filter object. */
		filter = (Filter)intent.getParcelableExtra("filter"); 
		
		/* sort out the action bar settings */
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		setTitle("Filter Results - " + filter.getQuery());
		getActionBar().setDisplayShowTitleEnabled(true);
		
		/* set up the edit text to contain the used query */
		text = (EditText) findViewById(R.id.editquery);
		text.setText(filter.getQuery());
		text.clearFocus();
		
		/* set up the two listviews. */
		ArrayList<HashMap<String, String>> fi = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> fe = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> entry = new HashMap<String, String>();
		entry.put("main", "Genre"); entry.put("sub", "Filter results by genre."); entry.put("value", filter.getGenre()); entry.put("icon", "no"); entry.put("pressable", "yes"); entry.put("loading", "no");
		fi.add(entry);
		entry = new HashMap<String, String>();
		entry.put("main", "Rating"); entry.put("sub", "Filter results based on IMDB Rating."); entry.put("value", ""+filter.getRating()); entry.put("icon", "no"); entry.put("pressable", "yes"); entry.put("loading", "no");
		fi.add(entry);
		entry = new HashMap<String, String>();
		entry.put("main", "Quality"); entry.put("sub", "Filter by video quality."); entry.put("value", filter.getQuality()); entry.put("icon", "no"); entry.put("pressable", "yes"); entry.put("loading", "no");
		fi.add(entry);
		fa = new FilterAdapter<String>(this, fi, true);
		
		ListView f = (ListView) findViewById(R.id.filter_listview);
		f.setAdapter(fa);
		
		entry = new HashMap<String, String>();
		entry.put("main", "Order"); entry.put("sub", "Order ASC or DESC"); entry.put("value", filter.getOrder()); entry.put("icon", "no"); entry.put("pressable", "yes"); entry.put("loading", "no");
		fe.add(entry);
		entry = new HashMap<String, String>();
		entry.put("main", "Sort by"); entry.put("sub", "Sort by date, downloaded etc..."); entry.put("value", filter.getSort()); entry.put("icon", "no"); entry.put("pressable", "yes"); entry.put("loading", "no");
		fe.add(entry);
		
		sa = new FilterAdapter<String>(this, fe, true);
		
		ListView s = (ListView) findViewById(R.id.sort_listview);
		s.setAdapter(sa);
		
		/* set up the dialogs and clicklisteners. */
		
		builder = new AlertDialog.Builder(this);
		
		s.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				final TextView t = (TextView) view.findViewById(R.id.value_text);
				HashMap<String, String> item = (HashMap<String, String>) sa.getItem(position);
				
				String main = item.get("main"); String value = (String) t.getText();
				
				switch(position) {
				case 0:
					
					final String[] items = new String[] {"asc", "desc"};
					
					int checkedItem = (value.equalsIgnoreCase("desc")) ? 1 : 0;
					
					builder.setTitle("Set Order").setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							t.setText(items[which]);
							filter.setOrder(items[which]);
							dialog.dismiss();
							
						}
					}).show();
					break;
				case 1:
					
					final String[] sortby = new String[] {"ALL", "Date", "Seeds", "Peers", "Size", "Alphabetic", "Rating", "Downloaded"};
					
					int sortchecked = 0;
					
					for(int i = 0; i < sortby.length; i++) {
						
						if(sortby[i].equalsIgnoreCase(value)) {
							
							sortchecked = i;
							
						}
						
					}
					
					builder.setTitle("Set Sort").setSingleChoiceItems(sortby, sortchecked, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							t.setText(sortby[which]);
							filter.setSort(sortby[which]);
							dialog.dismiss();
							
						}
					}).show();
					
					break;
				}
				
			}
			
		});
		
		/* disable scrolling on both listviews */
		
		OnTouchListener listener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if(event.getAction() == MotionEvent.ACTION_MOVE) {
					return true;
				}
				
				return false;
			}
			
		};
		
		s.setOnTouchListener(listener);
		f.setOnTouchListener(listener);
		
		f.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				
				final TextView t1 = (TextView) view.findViewById(R.id.value_text);
				HashMap<String, String> item = (HashMap<String, String>) fa.getItem(position);
				
				String main = item.get("main"); String value = (String) t1.getText();
				
				switch(position) {
				case 0:
					
					final String[] genres = new String[] {"ALL", "Action", "Adventure", "Animation", "Biography", "Comedy", "Crime", 
							"Documentary", "Drama", "Family", "Film-Noir", "Game-Show", "History", "Horror", "Music", "Musical", "Mystery", 
							"News", "Reality-TV", "Romance", "Sci-Fi", "Sport", "Talk-Show", "Thriller", "War", "Western"};
					
					int sortchecked = 0;
					
					for(int i = 0; i < genres.length; i++) {
						
						if(genres[i].equalsIgnoreCase(value)) {
							
							sortchecked = i;
							
						}
						
					}
					
					builder.setTitle("Set Genre").setSingleChoiceItems(genres, sortchecked, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							t1.setText(genres[which]);
							filter.setGenre(genres[which]);
							dialog.dismiss();
						}
					}).show();
					
					break;
				case 2:
					
					final String[] quality = new String[] {"ALL", "720p", "1080p", "3D"};
					
					int qualchecked = 0;
					
					for(int i = 0; i < quality.length; i++) {
						
						if(quality[i].equalsIgnoreCase(value)) {
							
							qualchecked = i;
							
						}
						
					}
					
					builder.setTitle("Set Quality").setSingleChoiceItems(quality, qualchecked, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							t1.setText(quality[which]);
							filter.setQuality(quality[which]);
							dialog.dismiss();
							
						}
					}).show();
					
					break;
				case 1:
					
					DialogFragment dialogFragment = new CustomDialog();
					Bundle val = new Bundle();
					val.putInt("current", Integer.parseInt(value));
					ratingTv = t1;
					dialogFragment.setArguments(val);
					dialogFragment.show(getFragmentManager(), "numberpicker");
					
					break;
					
				}
				
				
			}
			
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean end = super.onCreateOptionsMenu(menu);
		
		getMenuInflater().inflate(R.menu.main, menu);
		
		/* hide not required menu items. */
		menu.findItem(R.id.menu_filter).setVisible(false);
		menu.findItem(R.id.menu_login).setVisible(false);
		menu.findItem(R.id.menu_share).setVisible(false);
		menu.findItem(R.id.menu_refresh).setVisible(false);
		menu.findItem(R.id.menu_search).setVisible(false);
		menu.findItem(R.id.menu_settings).setVisible(false);
		menu.findItem(R.id.menu_home).setVisible(false);
		menu.findItem(R.id.menu_download).setVisible(false);
		menu.findItem(R.id.menu_request).setVisible(false);
		mainMenu = menu;
		
		return end;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			case R.id.menu_accept :
				
				filter.setQuery(text.getText().toString());
				
				Intent intent = new Intent(this, SearchActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("filter", filter);
				intent.setAction(CUSTOM_FILTER_INTENT);
				startActivity(intent);
				break;
		}
		
		return super.onOptionsItemSelected(item);
		
	}

	@Override
	public void onPositiveAction(DialogFragment dialog, View v) {
		
		NumberPicker picker = (NumberPicker) v.findViewById(R.id.rating_pick);
		
		this.ratingTv.setText(""+picker.getValue());
		filter.setRating(picker.getValue());
		
		dialog.dismiss();
		
	}

	@Override
	public void onNegativeAction(DialogFragment dialog) {
		// TODO Auto-generated method stub
		dialog.dismiss();
	}
	
}
