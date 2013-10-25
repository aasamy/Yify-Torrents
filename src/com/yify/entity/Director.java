package com.yify.entity;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class Director extends Person {
	
	public Director(JSONObject object) {
		super(TYPE_DIRECTOR, object);
	}
	
	public Director(Parcel in) {
		super(in);
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
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
	
	public static final Parcelable.Creator<Director> CREATOR = new Parcelable.Creator<Director>() {

		@Override
		public Director createFromParcel(Parcel source) {
			return new Director(source);
		}

		@Override
		public Director[] newArray(int size) {
			return new Director[size];
		}
	};

}
