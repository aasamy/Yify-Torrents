package com.yify.manager;

import java.util.ArrayList;

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

public class ProductAdapter<T extends UpcomingObject> extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<T> list;
    private static LayoutInflater inflater=null;
    private boolean isEnabled = false;
    
    public ProductAdapter(Activity a, ArrayList<T> l, boolean isEnabled) {
        activity = a;
        list = l;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isEnabled = isEnabled;
    }

    public int getCount() {
    	return list.size();
    }

    public Object getItem(int position) {
    	
    	if(position < list.size()) {
    		return list.get(position);
    	}
    	
        return null;
    }
    
    public void addItem(T item) {
    	this.list.add(item);
    }
    
    @Override
    public boolean isEnabled(int position) {
    	if(this.isEnabled) {
    		if(position == this.getCount()) {
    			return false;
    		}
    		return super.isEnabled(position);
    	}
    	return false;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public static class ViewHolder{
        public TextView text;
        public ImageView image;
        public TextView subTitle;
        public ViewFlinger flinger;
        public Button button;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        ViewHolder holder;
        if(convertView==null){
            vi = inflater.inflate(R.layout.item, null);
            holder=new ViewHolder();
            holder.text=(TextView)vi.findViewById(R.id.text);
            holder.image=(ImageView)vi.findViewById(R.id.image);
            holder.subTitle=(TextView)vi.findViewById(R.id.text2);
            vi.setTag(holder);
        }
        else
            holder=(ViewHolder)vi.getTag();
        
        String description = list.get(position).getMovieTitle();
        if(description.length() > 100) {
        	description = description.substring(0, 100) + "...";
        }
        holder.text.setText(description);
        holder.image.setTag(list.get(position).getMovieCover());
        T item = list.get(position);
        String subtitle = (item instanceof ListObject) ? "Genre : " + ((ListObject) item).getGenre() + ", Downloaded " + ((ListObject) item).getDownloaded() + " times" : "Uploaded by: " + item.getUploader();
        holder.subTitle.setText(subtitle);
        //imageLoader.DisplayImage(list.get(position).getMovieCover(), activity, holder.image, R.drawable.default_product);
        
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(list.get(position).getMovieCover(),holder.image);
        return vi;
    }
}