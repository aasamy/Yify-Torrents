package com.yify.entity;

import org.json.JSONObject;
import android.os.Parcel;

public abstract class Person extends Entity {
	
	/* type constants*/
	public static final int TYPE_DIRECTOR = 0;
	public static final int TYPE_ACTOR = 1;
	public static final int TYPE_PRODUCER = 2;
	public static final int TYPE_WRITER = 0;
	
	private int type;
	private String headshot;
	private String name;
	
	public Person(int type, JSONObject object) {
		this.type = type;
		this.name = object.optString("name");
		JSONObject images = object.optJSONObject("images");
		if(images != null) {
			this.headshot = images.optString("headshot");
		}
	}
	
	public Person(Parcel in) {
		String[] data = new String[2];
		in.readStringArray(data);
		this.name = data[0]; this.headshot = data[1];
		this.type = in.readInt();
	}
	
	public int getType() {
		return type;
	}
	
	public String getHeadshot() {
		return this.headshot;
	}
	
	public String getName() {
		return this.name;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] {this.name, this.headshot});
		dest.writeInt(this.type);
	}

}
