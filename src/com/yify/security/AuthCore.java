package com.yify.security;

import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import com.yify.manager.ApiManager;
import com.yify.mobile.ConnectivityDetector;

public class AuthCore extends SQLiteOpenHelper {
	
	/* database constructor variables. */
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "YifyAuth";
	
	/* database variables. */
	private static final String TABLE_AUTH = "auth";
	private static final String KEY_HASH = "hash";
	private static final String KEY_USERID = "userID";
	private static final String KEY_ID = "id";
	private static final String KEY_USER = "userName";
	private static final String KEY_TIMESTAMP = "loggedInTimestamp";
	
	/**
	 * a reference to the parent.
	 */
	private Activity parent;
	/**
	 * a reference to the async listener
	 * to notify when steps in the Async task have been completed
	 * WARNING - only pre and post execution can be notified as
	 * they run on the UI thread.
	 */
	private OnAsyncTaskCompletedListener listener;
	
	/**
	 * called at certain stages of the AuthAsync tasks execution, 
	 * useful to update the view.
	 * @author Jack Timblin
	 *
	 */
	protected interface OnAsyncTaskCompletedListener {
		/**
		 * called just before the background task is initiated
		 */
		public void onPreExecute();
		/**
		 * called after the async task is completed.
		 * @param success if the authentication was successful.
		 * @param response the response from the server, could be an error message.
		 */
		public void onPostExecute(boolean success, String[] response);
	}

	public AuthCore(Activity parent, OnAsyncTaskCompletedListener listener) {
		super(parent, DATABASE_NAME, null, DATABASE_VERSION);
		this.parent = parent;
		this.listener = listener;
	}
	
	protected AuthCore(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createAuthTable = "CREATE TABLE " + TABLE_AUTH + "("
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_HASH + 
				" TEXT, " + KEY_USERID + " INTEGER, " + KEY_USER + " TEXT, " + KEY_TIMESTAMP + " BIGINT" + ")";
		db.execSQL(createAuthTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTH);
		onCreate(db);
	}
	
	/**
	 * gets the current Auth model from the database
	 * the password is not saved in the local db so it not obtainable.
	 * @return Auth the current auth object.
	 */
	private Auth getAuth() {
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT " + KEY_ID + ", " + KEY_USER + ", " + KEY_HASH + ", " + KEY_USERID +  ", " + KEY_TIMESTAMP + " FROM " + TABLE_AUTH;
		
		Cursor cursor = db.rawQuery(query, null);
		
		if(cursor == null || cursor.getCount() == 0) {
			return new Auth();
		}
		
		cursor.moveToFirst();
		Auth a = new Auth(cursor.getString(1), cursor.getLong(4), cursor.getInt(0), cursor.getString(2));
		db.close();
		return a;
	}
	
	/**
	 * returns the state object which determines whether
	 * we are in an authenticated state or not, only this class
	 * can manipulate an Auth object.
	 * @return the current auth state.
	 */
	protected State getAuthenticatedState() {
		return new State(getAuth());
	}
	
	/**
	 * returns the state object with a predefined action.
	 * @param action the action to attach to the state.
	 * @return the current state.
	 */
	protected State getAuthenticatedState(String action) {
		return new State(getAuth(), action);
	}
	
	/**
	 * removes an authenticated user from the local db.
	 * essentially a 'logout'
	 */
	protected void removeAuthenticatedUser() {
		State currentState = this.getAuthenticatedState();
		if(currentState.isOpen()) {
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(TABLE_AUTH, KEY_ID + " = ?", new String[] {String.valueOf(currentState.getAuthenticatedStateId())});
			db.close();
		}
	}
	
	/**
	 * adds an authenticated user to the db, if there is currently a
	 * logged in user, then that is removed. this method initialises the 
	 * background task.
	 * @param username the username entered in the dialog. 
	 * @param password the password entered in the dialog.
	 */
	protected void addAuthenticatedUser(String username, String password) {
		if(listener == null) {
			return; /* no listener to listen for the callback. */
		}
		this.removeAuthenticatedUser();
		new AuthenticateAsyncTask(this.getWritableDatabase(),
				username, password, parent).execute();
	}
	
	protected class AuthenticateAsyncTask extends AsyncTask<String, String, String[]> {
		
		private SQLiteDatabase db;
		private String userText, passText;
		private ConnectivityDetector detect;
		
		public AuthenticateAsyncTask(SQLiteDatabase db,
				String userText, String passText, Activity parent) {
			detect = new ConnectivityDetector(parent);
			this.userText = userText;
			this.passText = passText;
			this.db = db;
		}
		
		@Override
		public void onPreExecute() {
			/* set the view state to loading. */
			listener.onPreExecute();
		}

		@Override
		protected String[] doInBackground(String... params) {
			if(!detect.isConnectionAvailable()) {
				return new String[] {"You currently have no network connection."};
			}
			
			if((userText == null || userText.length() == 0) || (passText == null || passText.length() == 0)) {
				return new String[] {"Please ensure you provide all of the required information."};
			}
			
			/*  */
			Yify man = new Yify();
			return man.login(userText, passText);
		}
		
		@Override
		public void onPostExecute(String[] response) {
			if(response.length > 1) {
				Date d = new Date();
				ContentValues values = new ContentValues();
				values.put(KEY_HASH, response[0]);
				values.put(KEY_USERID, response[1]);
				values.put(KEY_USER, response[2]);
				values.put(KEY_TIMESTAMP, d.getTime());
				
				db.insert(TABLE_AUTH, null, values);
				db.close();
			}
			listener.onPostExecute((response.length > 1), response);
		}
		
	}

}
