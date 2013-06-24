package com.yify.manager;

import java.util.ArrayList;

import com.yify.object.AuthObject;
import com.yify.object.AuthUserObject;
import com.yify.object.UserObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManager extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "YifySQL";
	
	private static final String TABLE_AUTH = "auth";
	
	private static final String KEY_HASH = "hash";
	private static final String KEY_USERID = "userID";
	private static final String KEY_ID = "id";
	private static final String KEY_USER = "userName";
	
	/* constants used for movieid table to store latest films
	 * (to test if a new film is available.) */
	
	private static final String TABLE_LATEST = "latest";
	private static final String KEY_MOVIEID = "movieID";

	public DatabaseManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createAuthTable = "CREATE TABLE " + TABLE_AUTH + "("
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_HASH + 
				" TEXT, " + KEY_USERID + " INTEGER, " + KEY_USER + " TEXT" + ")";
		db.execSQL(createAuthTable);
		String createLatestTable = "CREATE TABLE " + TABLE_LATEST + "("
				+ KEY_MOVIEID + " INTEGER PRIMARY KEY)";
		db.execSQL(createLatestTable);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTH);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LATEST);
		onCreate(db);
		
	}
	
	public void addAuth(String hash, int userID, String username) {
		boolean valid = true;
		
		String[] id = this.checkIsAuth();
		
		if(id != null) {
			try {
				this.deleteAuth(Integer.parseInt(id[0]));
			} catch (NumberFormatException e) {valid = false;}
		}
		
		if(valid) {
			SQLiteDatabase db = this.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put(KEY_HASH, hash);
			values.put(KEY_USERID, userID);
			values.put(KEY_USER, username);
			
			db.insert(TABLE_AUTH, null, values);
			db.close();
		}
		
	}
	
	public void deleteAuth(int id) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_AUTH, KEY_ID + " = ?",
				new String[] {String.valueOf(id)});
		
		db.close();
		
	}
	
	public void logout() {
		
		String[] data = this.checkIsAuth();
		
		if(data != null) {
			
			this.deleteAuth(Integer.parseInt(data[0]));
			
		}
		
	}
	
	public String[] checkIsAuth() {
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		String query = "SELECT "+ KEY_ID +", " + KEY_HASH + ", "+ KEY_USERID + ", " + KEY_USER +" FROM " + TABLE_AUTH;
		
		Cursor cursor = db.rawQuery(query, null);
		
		if(cursor == null) {
			return null;
		}
		
		if(cursor.getCount() == 0) {
			return null;
		}
		
		cursor.moveToFirst();
		
		return new String[] {cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)};
		
	}
	
	public String getHash() {
		String[] data = this.checkIsAuth();
		
		if(data != null) {
			return data[1];
		}
		
		return null;
	}
	
	public String getLoggedInUserName() {
		String[] data = this.checkIsAuth();
		
		if(data != null) {
			return data[3];
		}
		
		return null;
		
	}
	
	public AuthObject getLoggedInUser() {
		
		AuthObject auth = null;
		
		String[] id = this.checkIsAuth();
		
		if(id != null) {
			
			ApiManager manager = new ApiManager();
			
			AuthUserObject user = manager.getCurrentUser(id[1]);
			
			if(user != null) {
				
				auth = new AuthObject();
				
				auth.setUser(user);
				auth.setHash(id[1]);
				
			}
			
		}
		
		return auth;
		
	}
	
	/* latest movie functions */
	
	/**
	 * updates the movieid table, truncate and refresh.
	 * @param integers the movieids to add.
	 */
	public void updateMovies(ArrayList<Integer> integers) {
		SQLiteDatabase db = this.getWritableDatabase();
		this.deleteMovies();
		for(Integer item : integers) {
			ContentValues values = new ContentValues();
			values.put(KEY_MOVIEID, item);
			db.insert(TABLE_LATEST, null, values);
		}
		db.close();
	}
	
	/**
	 * truncates the movie id table.
	 */
	public void deleteMovies() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_LATEST, "1", null);
		db.close();
	}
	
	/**
	 * gets if there is new movies available, runs at a custom time interval, defaulted
	 * at every 24 hours, the database is automatically updated when this method is called
	 * so then all that needs to be called in the background service is this function.
	 * 
	 * @param movieids
	 * @return
	 */
	public int getNewFilmCount(ArrayList<Integer> movieids, boolean initial) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		String query = "SELECT " + KEY_MOVIEID + " FROM " + TABLE_LATEST;
		
		Cursor cursor = db.rawQuery(query, null);
		
		if(cursor == null) {
			return 0;
		}
		
		if(cursor.getCount() == 0 && !initial) {
			return 0;
		}
		
		@SuppressWarnings("unchecked")
		ArrayList<Integer> copy = (ArrayList<Integer>) movieids.clone();
		
		if (cursor.getCount() != 0) {
			while (cursor.moveToNext()) {
				for (int i = 0; i < movieids.size(); i++) {
					if (movieids.get(i) == cursor.getInt(0)) {
						movieids.remove(i);
					}
				}
			}
		}
		
		this.updateMovies(copy);
		return movieids.size();
		
	}
	
	

}
