package com.yify.security;

import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class is a central Auth instance and 
 * contains the details of the currently logged in user
 * only the State object should manipulate this class.
 * @author Jack Timblin <jacktimblin@gmail.com>
 *
 */
public class Auth implements Parcelable {
	/**
	 * the id of the auth object in the local db.
	 */
	private int ID;
	/**
	 * the hash of the currently logged in user.
	 */
	private String hash;
	/**
	 * username of the currently logged in user.
	 */
	public String username;
	/**
	 * the unix timestamp of when this user was logged in.
	 */
	public long authCreatedTimestamp;
	
	/* constants */
	/**
	 * used to describe contents of this class in describeContents();
	 */
	public static final int AUTH_MODEL = 0X9000001;
	
	/**
	 *  used to return a non-valid auth model.
	 */
	public Auth() {} 
	/**
	 * creates a valid Auth instance.
	 * @param username the username of the currently logged in user.
	 * @param password the password of the currently logged in user, stored in SHA1.
	 * @param authCreated the unix timestamp of when this user was added 'logged in'.
	 */
	public Auth(String username, 
			long authCreated, int Id, String hash) {
		this.username = username;
		this.authCreatedTimestamp = authCreated;
		this.ID = Id;
		this.hash = hash;
	}
	/**
	 * creates a Auth object from a Parcel, used for state transition.
	 * @param in the parcel instance to create this Auth instance.
	 */
	public Auth(Parcel in) {
		this.username = in.readString();
		this.authCreatedTimestamp = in.readLong();
		this.ID = in.readInt();
		this.hash = in.readString();
	}
	
	/**
	 * quick to bundle method so this instance can be returned inside a bundle.
	 * @return a Bundle to pass to a Intent or arguments to a fragment.
	 */
	public Bundle toBundle() {
		Bundle b = new Bundle();
		b.putParcelable("auth", this);
		return b;
	}
	
	/**
	 * get the username in this auth object.
	 * @return the username;
	 */
	public String getUsername() {
		return this.username; 
	}
	
	/**
	 * return the unix timestamp of when the authenticated user logged in.
	 * @return the authcreated timestamp.
	 */
	private long getAuthCreated() {
		return this.authCreatedTimestamp;
	}
	
	public int getId() {
		return this.ID;
	}
	
	public String getHash() {
		return this.hash;
	}
	
	/**
	 * test to see if the auth crendentials need updating from the
	 * server.
	 * @param field the field to test against, eg Calendar.MONTH
	 * @param value the value to test against eg -2 for minus 2 of the field set from now.
	 * @return true if the date set is less than the date specified.
	 */
	public boolean needsUpdating(int field, int value) {
		Calendar c = Calendar.getInstance();
		c.add(field, value);
		long d = c.getTimeInMillis();
		return (d < getAuthCreated());
	}

	@Override
	public int describeContents() {
		return AUTH_MODEL;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(this.username);
		out.writeLong(this.authCreatedTimestamp);
		out.writeInt(ID);
		out.writeString(hash);
	}
	
	/**
	 * creator instance so this class can be created from a parcel.
	 */
	public static final Parcelable.Creator<Auth> CREATOR = new Parcelable.Creator<Auth>() {

		@Override
		public Auth createFromParcel(Parcel source) {
			return new Auth(source);
		}

		@Override
		public Auth[] newArray(int size) {
			return new Auth[size];
		}
	};

}
