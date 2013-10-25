package com.yify.mobile;

import com.nostra13.universalimageloader.core.ImageLoader;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;

public class FullScreenActivity extends ActionBarActivity {

	private ImageView image;
	private int type;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.full_image);
		
		getActionBar().hide();
		Intent intent = getIntent();
		int type = intent.getIntExtra("type", -1);
		image = (ImageView) findViewById(R.id.full_image);
		
		if(type == -1) {
			finish();
		}
		
		this.type = type;
		
	    String item = intent.getStringExtra("item");
	    //image.setLayoutParams( new ViewGroup.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
	    ImageLoader.getInstance().displayImage(item, image);
	    image.setScaleType(ImageView.ScaleType.FIT_XY);
	    if(type == MovieActivity.COVER) {
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        
        
    }
	
}
