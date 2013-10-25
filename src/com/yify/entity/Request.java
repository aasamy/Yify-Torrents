package com.yify.entity;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Request implements Parcelable {
	
	public static final int REQUEST_MODEL = 0X684868;
	
	private ArrayList<Movie> response;
	private int responseCount;
	private boolean isValidResponse;
	
	public Request(Parcel in) {
		response = new ArrayList<Movie>();
		in.readTypedList(response, Movie.CREATOR);
		this.responseCount = in.readInt();
		this.isValidResponse = in.readInt() == 1;
	}
	
	public Request(ArrayList<Movie> response, int responseCount) {
		this.response = response;
		this.responseCount = responseCount;
		this.isValidResponse = (response.size() > 0);
	}
	
	public Request() {
		response = new ArrayList<Movie>();
		responseCount = 0;
		this.isValidResponse = false;
	}
	
	public int getResponseCount() {
		return this.responseCount;
	}
	
	public ArrayList<Movie> getResponse() {
		return response;
	}
	
	public boolean isValidResponse() {
		return this.isValidResponse;
	}

	@Override
	public int describeContents() {
		return REQUEST_MODEL;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(response);
		dest.writeInt(responseCount);
		dest.writeInt(this.isValidResponse ? 1 : 0);
	}
	
	public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() {

		@Override
		public Request createFromParcel(Parcel source) {
			return new Request(source);
		}

		@Override
		public Request[] newArray(int size) {
			return new Request[size];
		}
	};


}
