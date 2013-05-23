package com.yify.object;

import java.util.HashMap;

public class ItemObject extends ListObject {

	private String uploaderNotes;
	private String resolution;
	private String frameRate;
	private String language;
	private HashMap<String, String> screenshots;
	private String runtime;
	private String subtitles;
	private String youtubeID;
	private String youtubeURL;
	private String ageRating;
	private String subGenre;
	private String shortDescription;
	private String longDescription;
	
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
	public HashMap<String, String> getScreenshots() {
		return screenshots;
	}
	public void setScreenshots(HashMap<String, String> screenshots) {
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
	
	
}
