package com.yify.manager;

import java.util.ArrayList;

import com.yify.mobile.R;
import com.yify.object.*;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<UpcomingObject> list;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    private boolean isEnabled = false;
    
    public ProductAdapter(Activity a, ArrayList<UpcomingObject> l, boolean isEnabled) {
        activity = a;
        list = l;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
        this.isEnabled = isEnabled;
    }

    public int getCount() {
    	return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }
    
    @Override
    public boolean isEnabled(int position) {
    	if(this.isEnabled) {
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
    }

    public View getView(int position, View convertView, ViewGroup parent) {
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
        holder.subTitle.setText("Uploaded by: " + list.get(position).getUploader());
        imageLoader.DisplayImage(list.get(position).getMovieCover(), activity, holder.image, R.drawable.default_product);
        return vi;
    }
}