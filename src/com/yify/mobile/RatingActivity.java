package com.yify.mobile;

import java.util.ArrayList;

import com.yify.object.LoginDialog;
import com.yify.object.RequestObject;

import android.app.ActionBar.Tab;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.app.*;

public class RatingActivity extends ActionBarActivity implements OnMenuItemClickListener, LoginDialog.LoginDialogListener, ActionBar.TabListener {
	
	ViewPager pager;
	
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.rating_base);
	}
	
	public static class ConfirmedFragment extends Fragment {
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View root = inflater.inflate(R.layout.rating_frag_base, container, false);
			/* get the list and add it to the adapter. */
			
			return root;
		}
		
	}
	
	private class GetRequests extends AsyncTask<Integer, Integer, ArrayList<RequestObject>> {
		
		private int type;
		private View root;
		
		public GetRequests(View root, int type) {
			this.root = root;
			this.type = type;
		}

		@Override
		protected ArrayList<RequestObject> doInBackground(Integer... params) {
			
			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(ArrayList<RequestObject> isValid) {
			
		}
	}

	@Override
	public void onSignInPressed(DialogFragment fragment, View v,
			String userinput, String passinput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

}
