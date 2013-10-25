package com.yify.security;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.yify.mobile.R;

/**
 * This class is a complete authentication model for this application.
 * It completely controls the authentication state, and provides callbacks
 * when the authentication state changes or fails, it also completely controls
 * its own state control. This class does not give access to the current state
 * that is done using State.getAuthorisedState(context);
 * @author Jack Timblin <jacktimblin@gmail.com>
 * @version 1.1 - removed all state controls to do with setting up the menu and
 * implemented a new system where an activity being handled by AuthHelper can
 * requestAuthentication() with a string action which is then returned in the State
 * and after authentication has been completed the relevent event will be triggered.
 * @version 1.2 - added state controls for the focus on the two edittexts and applied 
 * a listener to the DONE button on the keyboard to also trigger the authentication process.
 *
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class AuthHelper {
	
	private AuthCore core;
	private OnAuthenticatedStateChangeListener listener;
	private OnStateChangeListener slistener;
	private Activity parent;
	private State previousState;
	private LoginDialog login;
	private boolean loginShowing = false;
	
	/**
	 * this interface is used to notify an activity when the 
	 * authenticated state has changed or failed.
	 * @author Jack Timblin <jacktimblin@gmail.com>
	 *
	 */
	public interface OnAuthenticatedStateChangeListener {
		/**
		 * this method is fired at three different authentication states:
		 * 1. a authorisation request is requested and a user is already
		 * authenticated.
		 * 2. a authorisation request is requested and after a user has
		 * logged in successfully if there was no authenticated user previously.
		 * 3. a user has destroyedAuthentication i.e they have logged out.
		 * @param state the current state object.
		 */
		public void onAuthenticatedStateChange(State state);
		/**
		 * this method is fired when a login attempt has failed.
		 */
		public void onAuthenticationFailed();
	}
	
	/**
	 * this function requests authentication then notifies
	 * the onAuthenticatedStateChange(State) with the current state after
	 * authentication has been successful.
	 * @param action the action to attach to the request to be either
	 * handled by the AuthHelper (Authenticated Actions) or to be returned in the
	 * State object to be handled by the Activity that requested authentication.
	 */
	public void requestAuthorisation(String action) {
		State state = core.getAuthenticatedState(action);
		if(state.isOpen()) {
			listener.onAuthenticatedStateChange(state);
		} else {
			displayLoginDialog(action);
		}
	}
	
	/**
	 * This interface is used so that another class can
	 * mimic the activties lifecycle, for example a Dialog can
	 * implement this interface to receive callbacks when either
	 * onSaveInstanceState() or onCreate() has been called in AuthHelper.
	 * @author Jack Timblin
	 *
	 */
	protected interface OnStateChangeListener {
		/**
		 * called when the state is about to be saved.
		 * @param outState the current state to save.
		 */
		public void onSaveInstanceState(Bundle outState);
		/**
		 * called when the state is restored, i.e onCreate()
		 * has been called with a savedInstanceState.
		 * @param inState the saved state.
		 */
		public void onRestoreInstanceState(Bundle inState);
	}
	
	public AuthHelper(Activity parent, OnAuthenticatedStateChangeListener listener) {
		this.parent = parent;
		this.listener = listener;
		login = new LoginDialog(parent, listener);
		this.core = new AuthCore(parent, login);
		slistener = (OnStateChangeListener) login;
	}

	/**
	 * displays the login dialog.
	 */
	@SuppressWarnings("unused")
	private void displayLoginDialog() {
		login.show();
	}
	
	/**
	 * displays the login dialog and also assigns
	 * an action to this auth attempt.
	 * @param action the action to assign to this
	 * auth attempt.
	 */
	private void displayLoginDialog(String action) {
		login.setAction(action);
		login.show();
	}
	
	/**
	 * this method displays the logout dialog and
	 * fires the onAuthenticationStateChanged(State) on
	 * completion.
	 */
	public void destroyAuthentication() {
		displayLogoutDialog();
	}
	
	/**
	 * displays logout dialog.
	 */
	private void displayLogoutDialog() {
		if(listener == null) { /* there is nothing to listen for a state change */
			return;
		}
		State currentState = core.getAuthenticatedState();
		if(currentState.isOpen()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(parent)
			.setTitle("Logout")
			.setMessage("Are you sure you like to logout?")
			.setNegativeButton("No", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					/* just close the dialog. */
					loginShowing = false;
					dialog.dismiss();
				}
				
			})
			.setPositiveButton("Yes", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					/* perform a logout in core. */
					core.removeAuthenticatedUser();
					listener.onAuthenticatedStateChange(core.getAuthenticatedState());
					
					/* close the dialog. */
					loginShowing = false;
					dialog.dismiss();
				}
				
			});
			Dialog dialog = builder.create();
			dialog.show();
			loginShowing = true;
		}
	}
	
	/* set up methods to correspond with
	 * the lifecycle of an Android Activity.  */
	
	/**
	 * called on Activity.onCreate() keeps the state up to date.
	 * also calls onRestoreInstanceState of any open dialogs.
	 * @param savedInstanceState the savedState bundle.
	 */
	public void onCreate(Bundle savedInstanceState) {
		if(savedInstanceState != null) {
			StateModel sm = savedInstanceState.getParcelable("state");
			State state = core.getAuthenticatedState();
			if(sm.getPreviousState().isOpen() != state.isOpen()) {
				/* the state changed */
				if(listener != null) {
					listener.onAuthenticatedStateChange(state);
				}
			}
			previousState = sm.getPreviousState();
			if(slistener != null) {
				slistener.onRestoreInstanceState(savedInstanceState);
			}
			loginShowing = sm.dialogShowing();
			if(loginShowing) {
				displayLogoutDialog();
			}
		}
	}
	
	/**
	 * called on Activity.onResume(), updates the authenticated state.
	 */
	public void onResume() {
		if(previousState != null) { /* has a previous state been set? */
			State state = core.getAuthenticatedState();
			if(state.isOpen() != previousState.isOpen()) {
				if(listener != null) {
					listener.onAuthenticatedStateChange(state);
				}
			}
		}
	}
	
	/**
	 * saves the state for the AuthHelper and should be called
	 * on Activity.onSaveInstanceState();
	 * @param outState the Bundle saved during state transition.
	 */
	public void onSaveInstanceState(Bundle outState) {
		StateModel sm = this.getStateModel();
		outState.putParcelable("state",sm);
		if(slistener != null) {
			slistener.onSaveInstanceState(outState);
		}
	}
	
	/**
	 * builder class to return a snapshot of the current
	 * state of the AuthHelper.
	 * @return StateModel the current state.
	 */
	private StateModel getStateModel() {
		return new StateModel(core.getAuthenticatedState(), loginShowing);
	}
	
	/**
	 * A state class that holds all of the data needed in a state transition to
	 * keep state, currently maintains if a dialog is showing and the previous 
	 * authenticated state before the state changed.
	 * @author Jack Timblin
	 *
	 */
	private static class StateModel implements Parcelable {
		/**
		 * the previous state i.e the state added to this class is compared
		 * to the state after the state transition has took place.
		 */
		private State previousState;
		/**
		 * if the logout dialog was showing before the state transition.
		 */
		private boolean loginShowing;
		
		public static final int STATEMODEL_MODEL = 0X6757489;
		
		public StateModel(State previousState, boolean loginShowing) {
			this.previousState = previousState;
			this.loginShowing = loginShowing;
		}
		
		public StateModel(Parcel in) {
			this.previousState = in.readParcelable(State.class.getClassLoader());
			this.loginShowing = in.readInt() == 1;
		}

		@Override
		public int describeContents() {
			return STATEMODEL_MODEL;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeParcelable(previousState, flags);
			dest.writeInt(loginShowing ? 1 : 0);
		}
		
		/**
		 * get the previous state
		 * @return the previous state.
		 */
		public State getPreviousState() {
			return this.previousState;
		}
		
		/**
		 * check if the dialog was showing before the state transition.
		 * @return true if the dialog was showing, false if not.
		 */
		public boolean dialogShowing() {
			return this.loginShowing;
		}
		
		@SuppressWarnings("unused")
		public static final Parcelable.Creator<StateModel> CREATOR = new Parcelable.Creator<AuthHelper.StateModel>() {

			@Override
			public StateModel createFromParcel(Parcel source) {
				return new StateModel(source);
			}

			@Override
			public StateModel[] newArray(int size) {
				return new StateModel[size];
			}
		};
		
	}
	
	/**
	 * generates a custom login dialog to use for authentication purposes.
	 * state is handled by mimicing the AuthHelper state change controls.
	 * All keyboard actions are controlled, imcluding the next and done buttons.
	 * and focus is stored on the state and if a incorrect entry is detected the users
	 * password is destroyed and the username is highlighted.
	 * @author Jack
	 *
	 */
	private class LoginDialog extends Dialog
		implements View.OnClickListener, AuthCore.OnAsyncTaskCompletedListener, 
		AuthHelper.OnStateChangeListener {

		/**
		 * the parent, i.e the activity utilising the AuthHelper
		 */
		private Activity parent;
		/**
		 * the listener attached to the parent to notify of changes.
		 */
		private AuthHelper.OnAuthenticatedStateChangeListener listener;
		/**
		 * the two edittexts on the login form.
		 */
		private EditText user, pass;
		/**
		 * the signin button.
		 */
		private Button signin;
		
		/**
		 * if the text in the username edit text was highlighted
		 * before a state transition occured.
		 */
		private boolean isHighlighted = false;
		
		/*
		 * an action to return for this authentication attempt.
		 */
		private String action;
		
		/**
		 * this makes sure that the soft touch keyboard stays popped up when one of the
		 * edit texts has focus.
		 */
		private View.OnFocusChangeListener focus = new View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus) {
		            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		        }
		    }
		};
		
		/* state variables. */
		private String userText = "";
		private String passText = "";
		private boolean isVisible = false;
		
		public LoginDialog(Activity parent, AuthHelper.OnAuthenticatedStateChangeListener listener) {
			super(parent);
			this.parent = parent;
			this.listener = listener;
		}
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.login_dialog);
			setTitle("Please Sign in");
			user = (EditText) findViewById(R.id.username);
			pass = (EditText) findViewById(R.id.password);
			signin = (Button) findViewById(R.id.signinbutton);
			TextWatcher watcher = new TextWatcher() {

				@Override
				public void afterTextChanged(Editable s) {
					if(user.getText().toString().length() != 0) {
						if(pass.getText().toString().length() != 0) {
							signin.setEnabled(true);	
						} else {
							signin.setEnabled(false);
						}	
					} else {
						signin.setEnabled(false);
					}	
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {}

				@Override
				public void onTextChanged(CharSequence s, int start, int before,
						int count) {}
				
			};
			pass.addTextChangedListener(watcher);
			user.addTextChangedListener(watcher);
			signin.setEnabled(false);
			signin.setOnClickListener(this);
			
			user.setText(userText); pass.setText(passText);
			user.setOnFocusChangeListener(focus);
			pass.setOnFocusChangeListener(focus);
			pass.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if ((event.getAction() == KeyEvent.ACTION_DOWN)
							&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
						if(signin.isEnabled()) {
							signin.callOnClick();
						}
						return true;
					}
					return false;
				}
			});
			if(userText != null && userText.length() != 0) {
				if(passText != null && passText.length() != 0) {
					pass.requestFocus();
				} else {
					user.requestFocus();
				}
			}
			if(isHighlighted && userText != null && userText.length() != 0) {
				user.setSelection(0, userText.length());
			}
		}
		
		@Override
		public void show() {
			super.show();
			isVisible = true;
		}
		
		@Override
		public void dismiss() {
			this.resetDialog();
			super.dismiss();
			isVisible = false;
			action = null;
		}
		
		/**
		 * set an action for this authentication attempt.
		 * the action is automatically destroyed when the login dialog
		 * is dismissed. 
		 * @param action the action to assign to this auth attempt.
		 */
		public void setAction(String action) {
			this.action = action;
		}
		
		@Override
		public void onClick(View view) {
			switch(view.getId()) {
				case R.id.signinbutton:
					core.addAuthenticatedUser(user.getText().toString(), pass.getText().toString());
					break;
			}
		}

		@Override
		public void onPreExecute() {
			ViewFlipper state = (ViewFlipper)findViewById(R.id.loginstate);
			state.setDisplayedChild(1);
		}

		@Override
		public void onPostExecute(boolean success, String[] response) {
			if(!success) {
				pass.setText("");
				user.setSelection(0,user.getText().toString().length());
				isHighlighted = true;
				ViewFlipper flipper = (ViewFlipper) findViewById(R.id.loginstate);
				flipper.setDisplayedChild(0);
				Toast.makeText(parent, response[0], Toast.LENGTH_LONG).show();
				listener.onAuthenticationFailed();
			} else {
				State state = core.getAuthenticatedState(action);
				listener.onAuthenticatedStateChange(state);
				dismiss();
			}
		}
		
		/* methods to retain state, i.e to keep entered variables
		 * and display the login if it was open before state changed. */

		@Override
		public void onSaveInstanceState(Bundle outState) {
			if(isVisible) {
				userText = user.getText().toString();
				passText = pass.getText().toString();
				outState.putStringArray("data", new String[] {userText, passText});
				outState.putString("action", action);
				outState.putBoolean("isHigh", isHighlighted);
			}
			outState.putBoolean("visible", isVisible);
		}
		
		@Override
		public void onRestoreInstanceState(Bundle inState) {
			boolean is = inState.getBoolean("visible");
			if(is) {
				isVisible = is;
				String[] data = inState.getStringArray("data");
				userText = data[0]; passText = data[1];
				action = inState.getString("action");
				isHighlighted = inState.getBoolean("isHigh");
				if(isVisible) {
					show();
				}
			}
		}
		
		/**
		 * resets the dialog for future use, 
		 */
		private void resetDialog() {
			user.setText(""); pass.setText("");
			ViewFlipper state = (ViewFlipper) findViewById(R.id.loginstate);
			state.setDisplayedChild(0);
		}
		
	}
}
