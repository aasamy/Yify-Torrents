package com.yify.manager;

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

	public DatabaseManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createAuthTable = "CREATE TABLE " + TABLE_AUTH + "("
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_HASH + 
				" TEXT, " + KEY_USERID + " INTEGER, " + KEY_USER + " TEXT" + ")";
		db.execSQL(createAuthTable);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTH);
		
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
		
		String query = "SELECT "+ KEY_ID +", " + KEY_HASH + ", "+ KEY_USERID +" FROM " + TABLE_AUTH;
		
		Cursor cursor = db.rawQuery(query, null);
		
		if(cursor == null) {
			return null;
		}
		
		if(cursor.getCount() == 0) {
			return null;
		}
		
		cursor.moveToFirst();
		
		return new String[] {cursor.getString(0), cursor.getString(1), cursor.getString(2)};
		
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
	
	

}
