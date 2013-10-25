package com.yify.entity;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class Movie extends Entity {
	
	/* trakt - normal call. */
	private String title;
	private int releaseYear;
	private String imdbId;
	private String posterURL;
	private String bannerURL;
	private ArrayList<String> genres;
	
	/* trakt - full call */
	private Date releaseDate;
	private String youtubeURL;
	private String overview;
	private String certification;
	private ArrayList<Person> people;
	private int runtime;
	
	/* yify variables. */
	private String magnetURL;
	
	/* other variables */
	private int type;
	
	public static final int TYPE_PARTIAL = 0;
	public static final int TYPE_FULL = 1;
	
	public Movie(JSONObject in, String magnetURL, int type) {
		this.title = in.optString("title");
		this.releaseYear = in.optInt("year");
		this.imdbId = in.optString("imdb_id");
		JSONObject images = in.optJSONObject("images");
		if(images != null) {
			this.bannerURL = images.optString("fanart");
			this.posterURL = images.optString("poster");
		}
		JSONArray g = in.optJSONArray("genres");
		this.genres = new ArrayList<String>();
		for(int i = 0; i < g.length(); i++) {
			this.genres.add(g.optString(i));
		}
		this.magnetURL = magnetURL;
		if(type == TYPE_FULL) {
			this.releaseDate = new Date(in.optLong("released")*1000);
			this.youtubeURL = in.optString("trailer");
			this.runtime = in.optInt("runtime");
			this.overview = in.optString("overview");
			this.certification = in.optString("certification");
			this.people = new ArrayList<Person>();
			JSONObject p = in.optJSONObject("people");
			JSONArray d = p.optJSONArray("directors");
			for(int i = 0; i < d.length(); i++) {
				this.people.add(new Director(d.optJSONObject(i)));
			}
			JSONArray w = p.optJSONArray("writers");
			for(int i = 0; i < w.length(); i++) {
				this.people.add(new Writer(w.optJSONObject(i)));
			}
			JSONArray pd = p.optJSONArray("producers");
			for(int i = 0; i < pd.length(); i++) {
				this.people.add(new Producer(pd.optJSONObject(i)));
			}
			JSONArray a = p.optJSONArray("actors");
			for(int i = 0; i < a.length(); i++) {
				this.people.add(new Actor(a.optJSONObject(i)));
			}
		}
		this.type = type;
	}
	
	public Movie(Parcel in) {

		this.genres = new ArrayList<String>();
		this.title = in.readString();
		this.bannerURL = in.readString();
		in.readStringList(genres);
		this.imdbId = in.readString();
		this.posterURL = in.readString();
		this.releaseYear = in.readInt();
		this.type = in.readInt();
		this.magnetURL = in.readString();
		if (type == TYPE_FULL) {
			this.certification = in.readString();
			this.overview = in.readString();
			this.releaseDate = new Date(in.readLong());
			this.runtime = in.readInt();
			this.youtubeURL = in.readString();
			this.people = new ArrayList<Person>();
			ArrayList<Director> d = new ArrayList<Director>();
			in.readTypedList(d, Director.CREATOR);
			for (Director di : d) {
				people.add(di);
			}
			ArrayList<Writer> w = new ArrayList<Writer>();
			in.readTypedList(w, Writer.CREATOR);
			for (Writer wr : w) {
				people.add(wr);
			}
			ArrayList<Producer> p = new ArrayList<Producer>();
			in.readTypedList(p, Producer.CREATOR);
			for (Producer pd : p) {
				people.add(pd);
			}
			ArrayList<Actor> a = new ArrayList<Actor>();
			in.readTypedList(a, Actor.CREATOR);
			for (Actor ac : a) {
				people.add(ac);
			}
		}
		
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(bannerURL);
		dest.writeStringList(genres);
		dest.writeString(imdbId);
		dest.writeString(posterURL);
		dest.writeInt(releaseYear);
		dest.writeInt(type);
		dest.writeString(magnetURL);
		if (type == TYPE_FULL) {
			dest.writeString(certification);
			dest.writeString(overview);
			dest.writeLong(releaseDate.getTime());
			dest.writeInt(runtime);
			dest.writeString(youtubeURL);
			dest.writeTypedList(getDirectors());
			dest.writeTypedList(getWriters());
			dest.writeTypedList(getProducers());
			dest.writeTypedList(getActors());
		}
	}

	@Override
	public View onCreateView(Activity parent) {
		return null;
	}
	
	public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

		@Override
		public Movie createFromParcel(Parcel source) {
			return new Movie(source);
		}

		@Override
		public Movie[] newArray(int size) {
			return new Movie[size];
		}
	};
	
	public String getTitle() {
		return title;
	}

	public int getReleaseYear() {
		return releaseYear;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public String getYoutubeURL() {
		return youtubeURL;
	}

	public String getOverview() {
		return overview;
	}

	public String getCertification() {
		return certification;
	}

	public String getImdbId() {
		return imdbId;
	}

	public String getPosterURL() {
		return posterURL;
	}

	public String getBannerURL() {
		return bannerURL;
	}

	public ArrayList<String> getGenres() {
		return genres;
	}

	public ArrayList<Person> getPeople() {
		return people;
	}
	
	public ArrayList<Director> getDirectors() {
		ArrayList<Director> d = new ArrayList<Director>();
		for(Person p : getPeople()) {
			if(p.getType() == Person.TYPE_DIRECTOR) {
				d.add((Director)p);
			}
		}
		return d;
	}
	
	public ArrayList<Actor> getActors() {
		ArrayList<Actor> a = new ArrayList<Actor>();
		for(Person p : getPeople()) {
			if(p.getType() == Person.TYPE_ACTOR) {
				a.add((Actor)p);
			}
		}
		return a;
	}
	
	public ArrayList<Writer> getWriters() {
		ArrayList<Writer> w = new ArrayList<Writer>();
		for(Person p : getPeople()) {
			if(p.getType() == Person.TYPE_WRITER) {
				w.add((Writer)p);
			}
		}
		return w;
	}
	
	public ArrayList<Producer> getProducers() {
		ArrayList<Producer> pd = new ArrayList<Producer>();
		for(Person p : getPeople()) {
			if(p.getType() == Person.TYPE_WRITER) {
				pd.add((Producer)p);
			}
		}
		return pd;
	}

	public int getRuntime() {
		return runtime;
	}

	public String getMagnetURL() {
		return magnetURL;
	}
	
	public int getType() {
		return this.type;
	}
	
	public void completeMovie(JSONObject in) {
		this.releaseDate = new Date(in.optLong("released")*1000);
		this.youtubeURL = in.optString("trailer");
		this.runtime = in.optInt("runtime");
		this.overview = in.optString("overview");
		this.certification = in.optString("certification");
		this.people = new ArrayList<Person>();
		JSONObject p = in.optJSONObject("people");
		JSONArray d = p.optJSONArray("directors");
		for(int i = 0; i < d.length(); i++) {
			this.people.add(new Director(d.optJSONObject(i)));
		}
		JSONArray w = p.optJSONArray("writers");
		for(int i = 0; i < w.length(); i++) {
			this.people.add(new Writer(w.optJSONObject(i)));
		}
		JSONArray pd = p.optJSONArray("producers");
		for(int i = 0; i < pd.length(); i++) {
			this.people.add(new Producer(pd.optJSONObject(i)));
		}
		JSONArray a = p.optJSONArray("actors");
		for(int i = 0; i < a.length(); i++) {
			this.people.add(new Actor(a.optJSONObject(i)));
		}
		this.type = TYPE_FULL;
	}

	@Override
	public View onCreateSubView(Activity parent) {
		// TODO Auto-generated method stub
		return null;
	}}
