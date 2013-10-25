package com.yify.beta;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.yify.mobile.R;
import com.yify.security.AuthHelper;
import com.yify.security.State;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MainActivity extends FragmentActivity {
	
	private FragmentManager fm;
	
	public static final int FRAGMENT_HOME = 0;
	public static final int FRAGMENT_REQUESTS = 1;
	public static final int FRAGMENT_MYACCOUNT = 2;
	public static final int FRAGMENT_SETTINGS = 3;
	public static final int FRAGMENT_COUNT = FRAGMENT_SETTINGS + 1;
	public static final String ACTION_MYACCOUNT = "action_myaccount";
	
	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	private int activeScreen = FRAGMENT_HOME;
	private AuthHelper helper;
	
	private String[] menuTitles;
	
	private DrawerLayout drawer;
	private ListView drawerList;
	private CharSequence title;
	private final CharSequence drawerTitle = "Navigation";
	private ActionBarDrawerToggle drawerToggle;
	private boolean isResumed;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.b_activity_main);
		
		/* set up authentication helper. */
		helper = new AuthHelper(this, new AuthHelper.OnAuthenticatedStateChangeListener() {
			
			@Override
			public void onAuthenticationFailed() {}
			
			@Override
			public void onAuthenticatedStateChange(State state) {
				handleAction(state);
			}
		});
		helper.onCreate(savedInstanceState);
		
		/* hide all of the fragments from view. */
		fm = this.getSupportFragmentManager();
		fragments[FRAGMENT_HOME] = fm.findFragmentById(R.id.homeFragment);
		Log.d("Home", fragments[FRAGMENT_HOME].toString());
		FragmentTransaction ft = fm.beginTransaction();
		for(Fragment f: fragments) {
			ft.hide(f);
		}
		ft.commit();
		
		/* set up the navigation drawer. */
		menuTitles = getResources().getStringArray(R.array.menuTitles);
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, menuTitles));
		drawerToggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_drawer, R.string.drawerOpenDesc, R.string.drawerCloseDesc) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(title);
			}
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(drawerTitle);
			}
		};
		
		drawer.setDrawerListener(drawerToggle);
		drawerList.setOnItemClickListener(new DrawerItemClickListener());
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		/* retrieve state variables. */
		if(savedInstanceState != null) {
			activeScreen = savedInstanceState.getInt("active");
		}
	}
	
	@Override
	public void setTitle(CharSequence title) {
		this.title = title;
		getActionBar().setTitle(title);
	}
	
	@Override
	public void onResumeFragments() {
		super.onResumeFragments();
		Log.d("Error", "onResmueFragments");
		if(activeScreen < menuTitles.length) {
			selectItem(activeScreen);
		} else{
			showFragment(activeScreen,true);
		}
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.syncState();
	}
	
	private void showFragment(int fragmentID, boolean addToBackStack) {
		if (isResumed) {
			FragmentTransaction ft = fm.beginTransaction();
			for (int i = 0; i < fragments.length; i++) {
				if (i == fragmentID) {
					ft.show(fragments[i]);
				} else {
					ft.hide(fragments[i]);
				}
			}
			ft.commit();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		helper.onResume();
		isResumed = true;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		isResumed = false;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		helper.onSaveInstanceState(outState);
	}
	
	/**
	 * Handles an action from an authentication request.
	 * @param state the state object returned.
	 */
	private void handleAction(State state) {
		if(state.isOpen()) {
			if(state.hasAction() && state.getAction().equals(ACTION_MYACCOUNT)) {
				selectItem(FRAGMENT_MYACCOUNT);
			}
		}
	}
	
	/**
	 * A complete process to change to a different fragment, handles the
	 * action bar title, selecting the drawer item, showing the fragment and 
	 * closing the drawer.
	 * @param fragmentID which fragment to show.
	 * @param args any extra variables to pass to the fragment, can be null.
	 */
	public void selectItem(int fragmentID) {
		if(fragmentID != FRAGMENT_MYACCOUNT) {
			showFragment(fragmentID, true);
			drawerList.setItemChecked(fragmentID, true);
			setTitle((fragmentID < menuTitles.length) ? menuTitles[fragmentID] : "");
			drawer.closeDrawer(drawerList);
		} else {
			helper.requestAuthorisation(ACTION_MYACCOUNT);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * A listview OnItemClickListener to attach to the drawerlayout so the navigation
	 * panel registers for onClick events.
	 * @author Jack Timblin
	 *
	 */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if(position != activeScreen) {
				selectItem(position);
			} else {
				drawer.closeDrawer(drawerList);
			}
		}
		
	}

}
