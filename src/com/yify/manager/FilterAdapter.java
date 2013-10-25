package com.yify.manager;

import java.util.ArrayList;
import java.util.HashMap;

import com.yify.mobile.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class FilterAdapter<T extends Object> extends BaseAdapter {
	
	private Activity activity;
	private ArrayList<HashMap<String, T>> list;
	private static LayoutInflater inflator = null;
	private boolean isEnabled = false;
	
	
	public FilterAdapter(Activity a, ArrayList<HashMap<String, T>> i, boolean e) {
		activity = a; list = i; isEnabled = e;
		inflator = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		
		if(position < list.size()) {
			return list.get(position);
		}
		
		return null;
		
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void addItem(HashMap<String, T> entry, int position) {
		
			list.remove(position);
			list.add(position, entry);
	}
	
	public static class ViewHolder {
		
		public TextView main;
		public TextView sub;
		public TextView value;
		public ViewFlipper flipper;
		public ImageView icon;
		public ProgressBar bar;
		
	}
	
	@Override
	public boolean isEnabled(int position) {
		
		if(position < list.size()) {
			
			HashMap<String, T> entry = this.list.get(position);
			
			if(entry.get("pressable").equals("no")) {
				return false;
			} else {
				return true;
			}
			
		}
		
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		
		ViewHolder holder;
		
		if(convertView == null) {
			
			v = inflator.inflate(R.layout.filter_item, null);
			holder = new ViewHolder();
			holder.main = (TextView) v.findViewById(R.id.setting_main);
			holder.sub = (TextView) v.findViewById(R.id.settings_sub);
			holder.value = (TextView) v.findViewById(R.id.value_text);
			holder.flipper = (ViewFlipper) v.findViewById(R.id.comment_state);
			holder.icon = (ImageView) v.findViewById(R.id.next_icon);
			holder.bar = (ProgressBar) v.findViewById(R.id.comments_loading);
			v.setTag(holder);
			
		} else {
			
			holder = (ViewHolder) v.getTag();
			
		}
		
		HashMap<String, T> entry = list.get(position);
		
		holder.main.setText((String) entry.get("main"));
		holder.sub.setText((String) entry.get("sub"));
		holder.value.setText("" + entry.get("value"));
		
		if(entry.get("icon").equals("yes")) {
			holder.icon.setImageResource(R.drawable.navigation_next_item);
			FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) holder.value.getLayoutParams();
			params.setMargins(0, 0, 80, 0);
			holder.value.setLayoutParams(params);
		}
		
		if(entry.get("loading").equals("yes")) {
			holder.bar.setVisibility(View.VISIBLE);
			holder.flipper.setDisplayedChild(1);
		} else {
			holder.bar.setVisibility(View.GONE);
			holder.flipper.setDisplayedChild(0);
		}
		
		if(entry.get("sub").equals("")) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			holder.main.setLayoutParams(params);
		}
		
		return v;
		
	}

}
