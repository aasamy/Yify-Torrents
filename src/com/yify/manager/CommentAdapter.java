package com.yify.manager;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.yify.*;
import com.yify.mobile.CommentActivity;
import com.yify.mobile.R;
import com.yify.object.CommentObject;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CommentAdapter extends BaseAdapter {
	
	private ArrayList<CommentObject> items;
	private LayoutInflater inflater;
	private Activity activity;
	private boolean showOverflow = true;
	
	public CommentAdapter(Activity activity, ArrayList<CommentObject> items) {
		this.activity = activity;
		this.items = items;
		this.inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		
		public TextView user;
		public TextView date;
		public TextView message;
		public ImageView overflow;
		
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
			
			v = inflater.inflate(R.layout.comment_item, null);
			holder = new ViewHolder();
			
			holder.user = (TextView) v.findViewById(R.id.name);
			holder.date = (TextView) v.findViewById(R.id.date);
			holder.message = (TextView) v.findViewById(R.id.message);
			holder.overflow = (ImageView) v.findViewById(R.id.overflow);
			
			v.setTag(holder);
			
		} else {
			
			holder = (ViewHolder) v.getTag();
			
		}
		
		final CommentObject comment = this.items.get(position);
		
		holder.user.setText(comment.getUsername());
		holder.date.setText(comment.getDateAdded());
		holder.message.setText(comment.getText());
		
		holder.overflow.setContentDescription(""+comment.getCommentID());
		
		if(!this.showOverflow) {
			holder.overflow.setVisibility(View.GONE);
		}
		
		holder.overflow.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				PopupMenu menu = new PopupMenu(activity, arg0);
				menu.setOnMenuItemClickListener((CommentActivity)activity);
				menu.getMenu().add(comment.getCommentID(), R.id.refresh_twitter, Menu.NONE, "Show Replies (" + comment.getReplyCount() + ")");
				menu.getMenu().add(comment.getCommentID(), R.id.show_twitter, Menu.NONE, "Reply to this comment");
				
				/* get the reply count from some where and disable first entry
				 * if the count is zero, also show the result count in the menu. */
				if(comment.getReplyCount() == 0) {
					menu.getMenu().findItem(R.id.refresh_twitter).setVisible(false);
				}
				
				menu.show();
			}
			
		});
		
		return v;
	}

}
