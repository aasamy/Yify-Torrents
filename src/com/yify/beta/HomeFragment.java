package com.yify.beta;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.yify.mobile.R;

public class HomeFragment extends Fragment {
	
	private ViewFlipper state;
	private Activity parent;
	
	public final static int LOADING = 0;
	public final static int MAIN = 1;
	public final static int ERROR = 2;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.b_home_fragment, container, false);
		state = (ViewFlipper) v.findViewById(R.id.state);
		/* set the initial state */
		setState(LOADING);
		
//		/* view generation. */
//		TextView t = new TextView(parent);
//		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//		rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
//		t.setLayoutParams(rlp);
//		t.setGravity(Gravity.CENTER);
//		t.setText("Hello World");
//		RelativeLayout c = (RelativeLayout) v.findViewById(R.id.content_frame);
//		c.addView(t);
		
		setState(MAIN);
		
		return v;
	}
	
	/**
	 * sets the current state of this fragment, either loading, main or error states.
	 * @param state the state to set this fragment to.
	 */
	public void setState(int state) {
		this.state.setDisplayedChild(state);
	}
	
	@Override
	public void onAttach(Activity parent) {
		super.onAttach(parent);
		this.parent = parent;
	}

}
