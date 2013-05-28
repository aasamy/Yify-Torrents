package com.yify.object;

import java.util.HashMap;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemObject extends ListObject implements Parcelable {

	private String uploaderNotes;
	private String resolution;
	private String frameRate;
	private String language;
	private String[] screenshots;
	private String runtime;
	private String subtitles;
	private String youtubeID;
	private String youtubeURL;
	private String ageRating;
	private String subGenre;
	private String shortDescription;
	private String longDescription;
	private String largeCover;
	
	public String getUploaderNotes() {
		return uploaderNotes;
	}
	public void setUploaderNotes(String uploaderNotes) {
		this.uploaderNotes = uploaderNotes;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public String getFrameRate() {
		return frameRate;
	}
	public void setFrameRate(String frameRate) {
		this.frameRate = frameRate;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String[] getScreenshots() {
		return screenshots;
	}
	public void setScreenshots(String ...screenshots) {
		this.screenshots = screenshots;
	}
	public String getRuntime() {
		return runtime;
	}
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}
	public String getYoutubeID() {
		return youtubeID;
	}
	public void setYoutubeID(String youtubeID) {
		this.youtubeID = youtubeID;
	}
	public String getYoutubeURL() {
		return youtubeURL;
	}
	public void setYoutubeURL(String youtubeURL) {
		this.youtubeURL = youtubeURL;
	}
	public String getAgeRating() {
		return ageRating;
	}
	public void setAgeRating(String ageRating) {
		this.ageRating = ageRating;
	}
	public String getSubGenre() {
		return subGenre;
	}
	public void setSubGenre(String subGenre) {
		this.subGenre = subGenre;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	public String getLongDescription() {
		return longDescription;
	}
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
	public String getSubtitles() {
		return subtitles;
	}
	public void setSubtitles(String subtitles) {
		this.subtitles = subtitles;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int flags) {
		
		out.writeInt(this.screenshots.length);
		
		for(int i = 0; i < this.screenshots.length; i++) {
			
			out.writeString(this.screenshots[i]);
			
		}
		
		out.writeInt(this.getMovieID());
		out.writeString(this.getMovieTitle());
		out.writeString(this.getMovieURL());
		out.writeString(this.getDateAdded());
		out.writeString(this.getQuality());
		out.writeString(this.getMovieCover());
		out.writeString(this.getImdbCode());
		out.writeString(this.getImdbLink());
		out.writeString(this.getFilesize());
		out.writeString(this.getMovieRating());
		out.writeString(this.getGenre());
		out.writeString(this.getTorrentSeeds());
		out.writeInt(this.getDownloaded());
		out.writeInt(this.getTorrentPeers());
		out.writeString(this.getTorrentURL());
		out.writeString(this.getTorrentHash());
		out.writeString(this.getTorrentMagnetURL());
		out.writeString(this.getUploader());
		out.writeString(this.getUploaderNotes());
		out.writeString(this.getResolution());
		out.writeString(this.getRuntime());
		out.writeString(this.getFrameRate());
		out.writeString(this.getLanguage());
		out.writeString(this.getSubtitles());
		out.writeString(this.getYoutubeID());
		out.writeString(this.getYoutubeURL());
		out.writeString(this.getAgeRating());
		out.writeString(this.getSubGenre());
		out.writeString(this.getShortDescription());
		out.writeString(this.getLongDescription());
		out.writeString(this.getLargeCover());
		
	}
	
	public ItemObject() {
		
	}
	
	public ItemObject(Parcel in) {
		
		int screenshotLength = in.readInt();
		
		String[] screenshots = new String[screenshotLength];
		
		for(int i = 0; i < screenshotLength; i++) {
			screenshots[i] = in.readString();
		}
		
		this.screenshots = screenshots;
		
		this.setMovieID(in.readInt());
		this.setMovieTitle(in.readString());
		this.setMovieURL(in.readString());
		this.setDateAdded(in.readString());
		this.setQuality(in.readString());
		this.setMovieCover(in.readString());
		this.setImdbCode(in.readString());
		this.setImdbLink(in.readString());
		this.setFilesize(in.readString());
		this.setMovieRating(in.readString());
		this.setGenre(in.readString());
		this.setTorrentSeeds(in.readString());
		this.setDownloaded(in.readInt());
		this.setTorrentPeers(in.readInt());
		this.setTorrentURL(in.readString());
		this.setTorrentHash(in.readString());
		this.setTorrentMagnetURL(in.readString());
		this.setUploader(in.readString());
		this.setUploaderNotes(in.readString());
		this.setResolution(in.readString());
		this.setRuntime(in.readString());
		this.setFrameRate(in.readString());
		this.setLanguage(in.readString());
		this.setSubtitles(in.readString());
		this.setYoutubeID(in.readString());
		this.setYoutubeURL(in.readString());
		this.setAgeRating(in.readString());
		this.setSubGenre(in.readString());
		this.setShortDescription(in.readString());
		this.setLongDescription(in.readString());
		this.setLargeCover(in.readString());
		
	}
	
	public String getLargeCover() {
		return largeCover;
	}
	public void setLargeCover(String largeCover) {
		this.largeCover = largeCover;
	}

	public static final Parcelable.Creator<ItemObject> CREATOR = new Parcelable.Creator<ItemObject>() {

		@Override
		public ItemObject createFromParcel(Parcel source) {
			
			return new ItemObject(source);
			
		}

		@Override
		public ItemObject[] newArray(int size) {

			return new ItemObject[size];
			
		}
	};
	
	
}
