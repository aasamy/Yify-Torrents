package com.yify.manager;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yify.mobile.CommentActivity;
import com.yify.mobile.R;
import com.yify.mobile.RatingActivity;
import com.yify.object.RequestObject;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class RatingAdapter extends BaseAdapter {
	
	private ArrayList<RequestObject> items;
	private LayoutInflater inflater;
	private Activity activity;
	private boolean showOverflow = true;
	
	public RatingAdapter() {}
	
	public RatingAdapter(Activity activity, ArrayList<RequestObject> items) {
		this.activity = activity;
		this.items = items;
		this.inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setItems(ArrayList<RequestObject> items) {
		this.items = items;
	}
	
	public void setUp(Activity a, ArrayList<RequestObject> items) {
		this.activity = a;
		this.items = items;
		this.inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void addItem(RequestObject object) {
		this.items.add(object);
	}

	@Override
	public int getCount() {
		return this.items.size();
	}

	@Override
	public Object getItem(int position) {
		if(position < this.items.size()) {
			return this.items.get(position);
		}
		return null;
	}
	
	public void dontShowIcon() {
		this.showOverflow = false;
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}
	
	public static class ViewHolder {
		
		public TextView title;
		public TextView votes;
		public ImageView overflow;
		public ImageView cover;
		
	}
	
	@Override
	public boolean isEnabled(int position) {
		return false; /* nothing can be pressed */
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		
		ViewHolder holder;
		
		if(convertView == null) {
			
			v = inflater.inflate(R.layout.rating_row, null);
			holder = new ViewHolder();
			
			holder.title = (TextView) v.findViewById(R.id.rating_title);
			holder.votes = (TextView) v.findViewById(R.id.rating_count);
			holder.cover = (ImageView) v.findViewById(R.id.rating_cover_image);
			holder.overflow = (ImageView) v.findViewById(R.id.rating_overflow);
			
			v.setTag(holder);
			
		} else {
			
			holder = (ViewHolder) v.getTag();
			
		}
		
		final RequestObject r = this.items.get(position);
		
		String title = (r.getMovieTitle().length() > 100) ? r.getMovieTitle().substring(0, 100) + "..." : r.getMovieTitle();
		
		holder.title.setText(title);
		holder.votes.setText(""+r.getVotes());
		holder.votes.setTextColor(Color.GREEN);
		if(r.getType() == RequestObject.CONFIRMED) {
			holder.votes.setVisibility(View.GONE);
		}
		ImageLoader.getInstance().displayImage(r.getCoverImage(), holder.cover);
		
		holder.overflow.setContentDescription(""+r.getRequestID());
		
		if(!this.showOverflow) {
			holder.overflow.setVisibility(View.GONE);
		}
		
		holder.overflow.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				PopupMenu menu = new PopupMenu(activity, arg0);
				menu.setOnMenuItemClickListener((RatingActivity)activity);
				menu.getMenu().add(r.getRequestID(), R.id.refresh_twitter, Menu.NONE, "Vote");
				menu.getMenu().add(r.getRequestID(), R.id.show_twitter, Menu.NONE, "More Info...");
				
				/* get the reply count from some where and disable first entry
				 * if the count is zero, also show the result count in the menu. */
				if(r.getType() == RequestObject.CONFIRMED) {
					menu.getMenu().findItem(R.id.refresh_twitter).setVisible(false);
				}
				Uri uri = Uri.parse("http://www.imdb.com/title/"+r.getImdbCode()+"/");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				Intent intentChooser = Intent.createChooser(intent, "More Info...");
				menu.getMenu().findItem(R.id.show_twitter).setIntent(intentChooser);
				
				menu.show();
			}
			
		});
		
		return v;
	}

}
