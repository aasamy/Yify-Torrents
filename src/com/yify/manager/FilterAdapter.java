package com.yify.manager;

import java.util.ArrayList;
import java.util.HashMap;

import com.yify.mobile.R;
import com.yify.object.*;
import com.yify.view.ViewFlinger;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
	
	public static class ViewHolder {
		
		public TextView main;
		public TextView sub;
		public TextView value;
		
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
			v.setTag(holder);
			
		} else {
			
			holder = (ViewHolder) v.getTag();
			
		}
		
		HashMap<String, T> entry = list.get(position);
		
		holder.main.setText((String) entry.get("main"));
		holder.sub.setText((String) entry.get("sub"));
		holder.value.setText("" + entry.get("value"));
		
		return v;
		
	}

}
