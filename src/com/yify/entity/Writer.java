package com.yify.entity;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class Writer extends Person {
	
	private String job;

	public Writer(JSONObject object) {
		super(TYPE_WRITER, object);
		this.job = object.optString("job");
	}
	
	public Writer(Parcel in) {
		super(in);
		this.job = in.readString();
	}
	
	public String getJob() {
		return this.job;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(job);
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
	
	public static final Parcelable.Creator<Writer> CREATOR = new Parcelable.Creator<Writer>() {

		@Override
		public Writer createFromParcel(Parcel source) {
			return new Writer(source);
		}

		@Override
		public Writer[] newArray(int size) {
			return new Writer[size];
		}
	};

}
