package com.yify.mobile;

import java.util.ArrayList;

import com.yify.manager.ApiManager;
import com.yify.manager.DatabaseManager;
import com.yify.manager.RatingAdapter;
import com.yify.object.Login;
import com.yify.object.LoginDialog;
import com.yify.object.ReplyDialog;
import com.yify.object.RequestDialog;
import com.yify.object.RequestObject;

import android.app.ActionBar.Tab;
import android.app.AlertDialog.Builder;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class RatingActivity extends ActionBarActivity implements OnMenuItemClickListener, LoginDialog.LoginDialogListener,
	RequestDialog.RequestListener{
	
	ViewPager pager;
	ActionBar bar;
	private final String KEY_SAVED_INDEX = "key_saved_index";
	private boolean isLoggedIn = false;
	private DatabaseManager man;
	private ConnectivityDetector detect;
	private Menu menu;
	private SearchView searchView;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rating_base);
		pager = (ViewPager) findViewById(R.id.pager);
		ViewFlipper flipper = (ViewFlipper) findViewById(R.id.ratingstate);
		bar = getActionBar();
		detect = new ConnectivityDetector(this);
		
		/* first check the connection... */
		bar.setTitle("Requests");
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		
		man = new DatabaseManager(this);
		
		String hash = man.getHash();
		isLoggedIn = (hash == null) ? false : true;
		
		if (detect.isConnectionAvailable()) {

			/* then add the tabs. */
			TabsAdapter adapter = new TabsAdapter(this, pager, menu);
			adapter.addTab(bar.newTab().setText("Ongoing"),
					OngoingFragment.class, null);
			adapter.addTab(bar.newTab().setText("Confirmed"),
					ConfirmedFragment.class, null);

			/* then display the tabs. */
			bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			
			bar.setSelectedNavigationItem(
					savedInstanceState != null ? savedInstanceState.getInt(KEY_SAVED_INDEX) : 0
					);
			
			
			
			flipper.setDisplayedChild(1);
			
		} else {
			
			flipper.setDisplayedChild(2);
			
		}
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_SAVED_INDEX, bar.getSelectedNavigationIndex());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean end = super.onOptionsItemSelected(item);
		
		switch(item.getItemId()) {
		case android.R.id.home :
			finish();
			break;
		case R.id.menu_home :
			Intent ho = new Intent(this, MainActivity.class);
			ho.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(ho);
			break;
		case R.id.menu_login :
			this.isLoggedIn = (man.getLoggedInUserName() == null) ? false : true;
			if(!this.isLoggedIn) {
				DialogFragment login = new LoginDialog();
				login.show(getFragmentManager(), "login");
			} else {
				Intent my = new Intent(this, MyAccountActivity.class);
				startActivity(my);
			}
			break;
		case R.id.menu_settings :
			Intent set = new Intent(this, SettingsActivity.class);
			startActivity(set);
			break;
		case R.id.menu_share:
			//open share intent to share URL of App in playstore.
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "TesterURL");
            startActivity(Intent.createChooser(shareIntent, "Share..."));
        	break;
		case R.id.menu_accept :
			this.isLoggedIn = (man.getHash() == null) ? false : true;
			if(!this.isLoggedIn) {
				DialogFragment login = new LoginDialog();
				login.show(getFragmentManager(), "login");
			} else {
				DialogFragment req = new RequestDialog();
				req.show(getFragmentManager(), "req");
			}
			break;
		default :
			break;
		}
		
		return end;
	}
	
	public static class ConfirmedFragment extends Fragment {
		
		public boolean loadMore = false;
		private View root;
		private RatingAdapter adapter;
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			root = inflater.inflate(R.layout.rating_frag_base, container, false);
			/* get the list and add it to the adapter. */
			Activity a = this.getActivity();
			adapter = new RatingAdapter();
			new GetRequests(root, RequestObject.CONFIRMED, a, adapter).execute();
			/* done initial load. */
			
			return root;
		}
		
	}
	
	public static class OngoingFragment extends Fragment  {
		private View root;
		private RatingAdapter adapter;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			root = inflater.inflate(R.layout.rating_frag_base, container, false);
			/* get the list and add it to the adapter. */
			Activity a = this.getActivity();
			adapter = new RatingAdapter();
			new GetRequests(root, RequestObject.ONGOING, a, adapter).execute();
			/* done initial load. */
			
			return root;
		}
	}
	
	public static class GetRequests extends AsyncTask<Integer, Integer, ArrayList<RequestObject>> {
		
		private int type;
		private View root;
		private int set = 1;
		private Activity activity;
		private View footerView;
		private boolean loadMore = false;
		private RatingAdapter adapter;
		private ListView list;
		private Runnable loadMoreItems = new Runnable() {

			@Override
			public void run() {
				
				loadMore = true;
				
				/* get 20 more items based on the old query. */
				ApiManager manager = new ApiManager();
				items = manager.getRequests(type, 20, set);
				
				activity.runOnUiThread(setNewItems);
				
			}
			
		};
		protected ConnectivityDetector detector;
		private ArrayList<RequestObject> items;
		protected Runnable setNewItems = new Runnable() {
			
			@Override
			public void run() {
				
				if(detector.isConnectionAvailable()) {
				
				if((items != null) && (items.size() != 0)) {
					
					/* add the new items to the adapter. */
					for(int i = 0; i < items.size(); i++) {
						adapter.addItem(items.get(i));
					}
					
					adapter.notifyDataSetChanged();
					
					loadMore = false;
					
					set++;
					
					if(items.size() < 20) {
						list.removeFooterView(footerView);
						loadMore = true;
					}
					
				} else {
					/* stop searching for items and hide footerView */
				}
				} else {
					list.removeFooterView(footerView);
					loadMore = true;
				}
				
			}
			
		};
		
		public GetRequests(View root, int type, Activity a, RatingAdapter ra) {
			this.root = root;
			this.type = type;
			this.activity = a;
			this.adapter = new RatingAdapter();
			this.detector = new ConnectivityDetector(activity);
		}

		@Override
		protected ArrayList<RequestObject> doInBackground(Integer... params) {
			
			/* get the requests for this tab.
			 * the connection will have already been checked. */
		   ApiManager man = new ApiManager();
		   return man.getRequests(this.type, 20, 1);
		}
		
		@Override
		protected void onPostExecute(ArrayList<RequestObject> isValid) {
			
			if(isValid != null) {
				list = (ListView) this.root.findViewById(R.id.fragmentlist);
				footerView = ((LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer, null, false);
				
				if(isValid.size() == 20) {
					list.addFooterView(footerView);
				}
				
				adapter.setUp(activity, isValid);
				set = 2;
				list.setAdapter(adapter);
				
				list.setOnScrollListener(new OnScrollListener() {

					@Override
					public void onScroll(AbsListView view,
							int firstVisibleItem, int visibleItemCount,
							int totalItemCount) {
						int lastInScreen = firstVisibleItem + visibleItemCount;
						
						if((lastInScreen == totalItemCount) && (!loadMore)) {
							Thread thread = new Thread(null, loadMoreItems);
							thread.start();
						}
						
						
					}

					@Override
					public void onScrollStateChanged(AbsListView view,
							int scrollState) {
						// TODO Auto-generated method stub
						
					}
					
				});
				
			}
			
		}
	}

	@Override
	public void onSignInPressed(DialogFragment fragment, View v,
			String userinput, String passinput) {
		
		ViewFlipper flipper = (ViewFlipper) v.findViewById(R.id.loginstate);
		flipper.setDisplayedChild(1);
		
		new Login(isLoggedIn, man, detect, v, fragment, this).execute(new String[] {userinput, passinput});
		this.isLoggedIn = (man.getHash() == null) ? false : true;
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		/* keep the log in state up to date. */
		this.isLoggedIn = (man.getHash() == null) ? false : true;
	}

	@Override
	public boolean onMenuItemClick(final MenuItem item) {
		
		switch(item.getItemId()) {
		case R.id.refresh_twitter:
			/* check if logged in, have to be logged in to vote for a film. */
			this.isLoggedIn = (man.getHash() == null) ? false : true;
			if(!this.isLoggedIn) {
				/* show login dialog. */
				DialogFragment login = new LoginDialog();
				login.show(getFragmentManager(), "login");
			} else {
				/* show confirm dialog. */
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				
				builder.setTitle("Vote on this request?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						/* vote on this request */
						new Vote().execute(item.getGroupId());
						dialog.dismiss();
						
						
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						dialog.dismiss();
						
					}
				}).show();
			}
			break;
		}
		
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.main, menu);
		this.menu = menu;
		
		this.menu.findItem(R.id.menu_accept).setIcon(R.drawable.content_new);
		this.menu.findItem(R.id.menu_download).setVisible(false);
		this.menu.findItem(R.id.menu_filter).setVisible(false);
		this.menu.findItem(R.id.menu_refresh).setVisible(false);
		
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		
		if(bar.getSelectedNavigationIndex() == 1)
			this.menu.findItem(R.id.menu_accept).setVisible(false);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	private class Vote extends AsyncTask<Integer, Integer, String> {
		
		@Override
		protected String doInBackground(Integer... params) {
			
			if(!detect.isConnectionAvailable()) {
				return "You currently have no network connection.";
			}
			
			String hash = man.getHash();
			
			if(hash == null) {
				return "You are currently not logged in.";
			}
			
			ApiManager mana = new ApiManager();
			return mana.voteRequest(hash, params[0]);
			
		}
		
		@Override
		protected void onPostExecute(String response) {
			
			String message;
			
			if(response == null) {
				message = "You have successfully voted on this request.";
			} else {
				message = response;
			}
			
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
			
		}
	}
	
	public static class TabsAdapter extends FragmentPagerAdapter
	implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
		private Menu menu;
 
		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;
 
			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}
 
		public TabsAdapter(FragmentActivity activity, ViewPager pager, Menu menu) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mActionBar = activity.getActionBar();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
			this.menu = menu;
		}
 
		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			mActionBar.addTab(tab);
			notifyDataSetChanged();
		}
 
 
		public int getCount() {
			return mTabs.size();
		}
 
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(), info.args);
		}
 
 
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}
 
 
		public void onPageSelected(int position) {
			mActionBar.setSelectedNavigationItem(position);
			if(menu != null)
				menu.findItem(R.id.menu_accept).setVisible(position == 1 ? false : true);
		}
 
 
		public void onPageScrollStateChanged(int state) {
		}
 
		public void onTabReselected(Tab tab, FragmentTransaction ft) {}
 
		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {	
			Object tag = tab.getTag();
			for (int i=0; i<mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
					if(menu != null)
						menu.findItem(R.id.menu_accept).setVisible(i == 1 ? false : true);
				}
			}
		}
 
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {}
	}

	@Override
	public void onRequestPressed(DialogFragment fragment, View view) {
		
		EditText message = (EditText) view.findViewById(R.id.query);
		Spinner type = (Spinner) view.findViewById(R.id.requestType);
		ViewFlipper flipper = (ViewFlipper) view.findViewById(R.id.requeststate);
		flipper.setDisplayedChild(1);
		
		new SendRequest(message.getText().toString(), type.getSelectedItemPosition(), 
				this, view, fragment).execute();
		
	}
	
	private class SendRequest extends AsyncTask<Integer, Integer, String> {

		private int itemID;
		private final int IDMB = 1;
		private final int TITLE = 0;
		private String code;
		private Activity a;
		private View v;
		private DialogFragment f;
		
		public SendRequest(String code, int itemID, Activity a, View v, DialogFragment f) {
			this.code = code;
			this.itemID = itemID;
			this.a = a;
			this.v = v;
			this.f = f;
		}
		
		@Override
		protected String doInBackground(Integer... arg0) {
			
			ConnectivityDetector d = new ConnectivityDetector(a);
			DatabaseManager dm = new DatabaseManager(a);
			ApiManager man = new ApiManager();
			
			if(!d.isConnectionAvailable()) {
				return "You are not currently connected to a network.";
			}
			
			if(dm.getHash() == null) {
				return "You are not currently logged in.";
			}
			
			boolean isvalid;
			
			if(this.itemID == IDMB) {
				if(!man.isValidIMDBCode(code)) {
					return "The IMDB code you supplied is not valid.";
				}
			}
			
			return man.makeRequest(dm.getHash(), code);
		}
		
		@Override
		protected void onPostExecute(String response) {
			
			ViewFlipper flipper = (ViewFlipper) v.findViewById(R.id.requeststate);
			flipper.setDisplayedChild(1);
			
		    String message = (response == null) ?
		    		"You movie request is successful" : response;
		    
		    Toast.makeText(a, message, Toast.LENGTH_SHORT).show();
		    
		    if(response != null) {
		    	flipper.setDisplayedChild(0);
		    } else {
		    	f.dismiss();
		    }
			
		}
	}

	@Override
	public void onCancelPressed(DialogFragment fragment) {
		
		fragment.dismiss();
		
	}

}
