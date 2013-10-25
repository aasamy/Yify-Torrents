package com.yify.entity;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class Actor extends Person {
	
	private String role;
	
	public Actor(JSONObject object) {
		super(TYPE_ACTOR, object);
		this.role = object.optString("character");
	}
	
	public Actor(Parcel in) {
		super(in);
		this.role = in.readString();
	}
	
	public String getRole() {
		return this.role;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(role);
	}

	@Override
	public View onCreateView(Activity parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View onCreateSubView(Activity parent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static final Parcelable.Creator<Actor> CREATOR = new Parcelable.Creator<Actor>() {

		@Override
		public Actor createFromParcel(Parcel source) {
			return new Actor(source);
		}

		@Override
		public Actor[] newArray(int size) {
			return new Actor[size];
		}
	};

}
