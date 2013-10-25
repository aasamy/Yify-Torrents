package com.yify.security;

import android.os.Parcel;
import android.os.Parcelable;

public class State implements Parcelable {
	
	private Auth auth;
	private boolean isOpen;
	private String action;
	private boolean hasAction;
	
	public static final int STATE_MODEL = 0X9212040;
	
	public State(Auth auth) {
		this.auth = auth;
		this.isOpen = (auth.getUsername() != null && auth.getHash() != null);
		this.hasAction = false;
	}
	
	public State(Auth auth, String action) {
		this(auth);
		if(action != null && action.length() != 0) {
			this.action = action;
			this.hasAction = true;
		}
	}
	
	public State(Parcel in) {
		this.auth = in.readParcelable(Auth.class.getClassLoader());
		this.isOpen = (auth.getUsername() != null && auth.getHash() != null);
		this.hasAction = in.readInt() == 1;
		if(hasAction)
			this.action = in.readString();
	}
	
	/**
	 * determines if the state is currently open, i.e
	 * if there is an authenticated user.
	 * @return true is the state is open, false if not.
	 */
	public boolean isOpen() {
		return this.isOpen;
	}
	
	public boolean hasAction() {
		return this.hasAction;
	}
	
	public String getAction() {
		return this.action;
	}
	
	/**
	 * returns the ID of the auth object, 
	 * only used to remove the auth on 'logout'
	 * @return the ID of theAuth object
	 */
	protected int getAuthenticatedStateId() {
		return auth.getId();
	}
	
	/**
	 * returns the hash for api calls, this is the
	 * only variable exposed from the Auth.
	 * @return the hash.
	 */
	protected String getAuthenticatedHash() {
		return auth.getHash();
	}

	@Override
	public int describeContents() {
		return STATE_MODEL;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(auth, flags);
		dest.writeInt(this.hasAction ? 1 : 0);
		if(hasAction)
			dest.writeString(action);
	}
	
	public static final Parcelable.Creator<State> CREATOR = new Parcelable.Creator<State>() {

		@Override
		public State createFromParcel(Parcel source) {
			return new State(source);
		}

		@Override
		public State[] newArray(int size) {
			return new State[size];
		}
	};

}
