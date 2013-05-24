package com.yify.mobile;

import java.util.ArrayList;
import java.util.HashMap;

import com.yify.object.Filter;
import com.yify.manager.FilterAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FilterActivity extends FragmentActivity {
	
	private Menu mainMenu = null;
	private ActionBar actionBar;
	private Filter filter;
	public static final String CUSTOM_FILTER_INTENT = "ACTION_SEARCH_FILTER_CUSTOM";
	private FilterAdapter<String> fa;
	private FilterAdapter<String> sa;
	private AlertDialog.Builder builder;
	LayoutInflater inflater;
	NumberPicker picker;
	Dialog dialog;
	View v;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filter); /* change to filter view after intent testing finished. */
		Intent intent = getIntent();
		filter = (Filter)intent.getParcelableExtra("filter");
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		inflater = (LayoutInflater) FilterActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setTitle("Filter Results - " + filter.getQuery());
		v = inflater.inflate(R.layout.rating_picker, null);
		picker = (NumberPicker) v.findViewById(R.id.rating_pick);
		picker.setMaxValue(9); picker.setMinValue(0);
		getActionBar().setDisplayShowTitleEnabled(true);
		Button button = (Button) findViewById(R.id.okaybutton);
		/* set up the two listviews. */
		
		ArrayList<HashMap<String, String>> fi = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> fe = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> entry = new HashMap<String, String>();
		entry.put("main", "Genre"); entry.put("sub", "Filter results by genre."); entry.put("value", filter.getGenre());
		fi.add(entry);
		entry = new HashMap<String, String>();
		entry.put("main", "Rating"); entry.put("sub", "Filter results based on IMDB Rating."); entry.put("value", ""+filter.getRating());
		fi.add(entry);
		entry = new HashMap<String, String>();
		entry.put("main", "Quality"); entry.put("sub", "Filter by video quality."); entry.put("value", filter.getQuality());
		fi.add(entry);
		fa = new FilterAdapter<String>(this, fi, true);
		
		ListView f = (ListView) findViewById(R.id.filter_listview);
		f.setAdapter(fa);
		
		entry = new HashMap<String, String>();
		entry.put("main", "Order"); entry.put("sub", "Order ASC or DESC"); entry.put("value", filter.getOrder());
		fe.add(entry);
		entry = new HashMap<String, String>();
		entry.put("main", "Sort by"); entry.put("sub", "Sort by date, downloaded etc..."); entry.put("value", filter.getSort());
		fe.add(entry);
		
		sa = new FilterAdapter<String>(this, fe, true);
		
		ListView s = (ListView) findViewById(R.id.sort_listview);
		s.setAdapter(sa);
		
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
		
		f.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				
				final TextView t1 = (TextView) view.findViewById(R.id.value_text);
				HashMap<String, String> item = (HashMap<String, String>) fa.getItem(position);
				
				String main = item.get("main"); String value = (String) t1.getText();
				
				final AlertDialog alert = builder.create();
				
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
							alert.dismiss();
						}
					});
					
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
							filter.setGenre(quality[which]);
							alert.dismiss();
							
						}
					});
					
					break;
				case 1:
					
					v = inflater.inflate(R.layout.rating_picker, null);
					picker = (NumberPicker) v.findViewById(R.id.rating_pick);
					picker.setMinValue(0); picker.setMaxValue(9);
					picker.setValue(Integer.parseInt(value));
					
					builder.setTitle("Set Rating").setView(v).setPositiveButton("OK", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							t1.setText("" + picker.getValue());
							filter.setRating(picker.getValue());
							dialog.dismiss();
							
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							dialog.cancel();
							alert.dismiss();
						}
					});
					break;
					
				}
				
				alert.show();
				
				
			}
			
		});
		
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Log.d("Filter Contents", "Quality=" + filter.getQuality() + ", Genre=" + filter.getGenre() + "Rating=" + filter.getRating() + ", Sort=" + filter.getSort() + ", Order=" + filter.getOrder());
				
			}
			
		});
		
		Log.d("Filter Contents", "Quality=" + filter.getQuality() + ", Genre=" + filter.getGenre() + "Rating=" + filter.getRating() + ", Sort=" + filter.getSort() + ", Order=" + filter.getOrder());
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean end = super.onCreateOptionsMenu(menu);
		
		mainMenu = menu;
		
		return end;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
		}
		
		return super.onOptionsItemSelected(item);
		
	}
	
}
