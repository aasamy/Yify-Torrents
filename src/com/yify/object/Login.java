package com.yify.object;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.yify.manager.ApiManager;
import com.yify.manager.DatabaseManager;
import com.yify.mobile.ConnectivityDetector;
import com.yify.mobile.R;

public class Login extends AsyncTask<String, Integer, String> {
	
	boolean loggedIn = false;
	DatabaseManager manager;
	ConnectivityDetector detector;
	View loginDialogView;
	DialogFragment frag;
	Activity activity;
		
		public Login(boolean LoggedIn, DatabaseManager manager, ConnectivityDetector detector, View loginDialogView, 
				DialogFragment frag, Activity activity) {
			
			this.activity = activity;
			this.loggedIn = LoggedIn;
			this.manager = manager;
			this.detector = detector;
			this.loginDialogView = loginDialogView;
			this.frag = frag;
			
		}

		@Override
		protected String doInBackground(String... params) {
			
			if(!detector.isConnectionAvailable()) {
				return null;
			}
			
			ApiManager man = new ApiManager();
			String[] response = man.login(params[0], params[1]);
			
			if(response.length > 1) {
				
				manager.addAuth(response[0], Integer.parseInt(response[1]), response[2]);
				
				String username = manager.getLoggedInUserName();
				
				loggedIn = (username == null) ? false : true;
				
				return username;
				
			}
			
			return "";
			
		}
		
		@Override
		protected void onPostExecute(String response) {
			
			String message = "";
			
			
			if(response == null || response.equals("")) {
				message = "Your username or password was incorrect, please try again.";
			}
			
			ViewFlipper flipper = (ViewFlipper) this.loginDialogView.findViewById(R.id.loginstate);
			EditText pass = (EditText) loginDialogView.findViewById(R.id.password);
			
			if(loggedIn) {
				Toast.makeText(activity, "You have successfully signed in " + response, Toast.LENGTH_SHORT).show();
				frag.dismiss();
			} else {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				pass.setText("");
				flipper.setDisplayedChild(0);
				builder.setMessage(message)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
					}
				}).show();
			}
			
		}
		
	}
