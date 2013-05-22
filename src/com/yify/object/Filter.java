package com.yify.object;

import android.os.Parcel;
import android.os.Parcelable;

public class Filter implements Parcelable {
	
	private String query;
	private String quality;
	private String genre;
	private int rating;
	private String sort;
	private String order;

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeString(query);
		dest.writeString(quality);
		dest.writeString(genre);
		dest.writeInt(rating);
		dest.writeString(sort);
		dest.writeString(order);

	}
	
	public Filter(Parcel in) {
		
		this.query = in.readString();
		this.quality = in.readString();
		this.genre = in.readString();
		this.rating = in.readInt();
		this.sort = in.readString();
		this.order = in.readString();
		
	}
	
	public Filter(String query, String quality, String genre, int rating,
			String sort, String order) {
		
		this.query = query;
		this.quality = quality;
		this.genre = genre;
		this.rating = rating;
		this.order = order;
		this.sort = sort;
		
	}
	
	public String getQuery() {
		return this.query;
	}
	
	public String getQuality() {
		return this.quality;
	}
	
	public String getGenre() {
		return this.genre;
	}
	
	public int getRating() {
		return this.rating;
	}
	
	public String getOrder() {
		return this.order;
	}
	
	public String getSort() {
		return this.sort;
	}
	
	public static final Parcelable.Creator<Filter> CREATOR = new Parcelable.Creator<Filter>() {

		@Override
		public Filter createFromParcel(Parcel source) {
			
			return new Filter(source);
			
		}

		@Override
		public Filter[] newArray(int size) {

			return new Filter[size];
			
		}
	};

}
