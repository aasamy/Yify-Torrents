package com.yify.entity;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class Producer extends Person {
	
	public boolean isExecutive;
	
	public Producer(JSONObject object) {
		super(TYPE_PRODUCER, object);
		this.isExecutive = object.optBoolean("executive");
	}
	
	public Producer(Parcel in) {
		super(in);
		this.isExecutive = in.readInt() == 1;
	}
	
	public boolean isExecutive() {
		return this.isExecutive;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(this.isExecutive ? 1 : 0);
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
	
	public static final Parcelable.Creator<Producer> CREATOR = new Parcelable.Creator<Producer>() {

		@Override
		public Producer createFromParcel(Parcel source) {
			return new Producer(source);
		}

		@Override
		public Producer[] newArray(int size) {
			return new Producer[size];
		}
	};

}
