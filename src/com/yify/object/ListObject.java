package com.yify.object;

public class ListObject extends UpcomingObject {

	private int movieID;
	private String movieURL;
	private String quality;
	private String filesize;
	private String movieRating;
	private String genre;
	private String torrentSeeds;
	private int downloaded;
	private int torrentPeers;
	private String torrentURL;
	private String torrentHash;
	private String torrentMagnetURL;
	private int resultCount; /* store in here for easy access */
	
	public int getResultCount() {
		return resultCount;
	}
	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}
	public int getMovieID() {
		return movieID;
	}
	public void setMovieID(int movieID) {
		this.movieID = movieID;
	}
	public String getMovieURL() {
		return movieURL;
	}
	public void setMovieURL(String movieURL) {
		this.movieURL = movieURL;
	}
	public String getQuality() {
		return quality;
	}
	public void setQuality(String quality) {
		this.quality = quality;
	}
	public String getFilesize() {
		return filesize;
	}
	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}
	public String getMovieRating() {
		return movieRating;
	}
	public void setMovieRating(String movieRating) {
		this.movieRating = movieRating;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getTorrentSeeds() {
		return torrentSeeds;
	}
	public void setTorrentSeeds(String torrentSeeds) {
		this.torrentSeeds = torrentSeeds;
	}
	public int getDownloaded() {
		return downloaded;
	}
	public void setDownloaded(int downloaded) {
		this.downloaded = downloaded;
	}
	public int getTorrentPeers() {
		return torrentPeers;
	}
	public void setTorrentPeers(int torrentPeers) {
		this.torrentPeers = torrentPeers;
	}
	public String getTorrentURL() {
		return torrentURL;
	}
	public void setTorrentURL(String torrentURL) {
		this.torrentURL = torrentURL;
	}
	public String getTorrentHash() {
		return torrentHash;
	}
	public void setTorrentHash(String torrentHash) {
		this.torrentHash = torrentHash;
	}
	public String getTorrentMagnetURL() {
		return torrentMagnetURL;
	}
	public void setTorrentMagnetURL(String torrentMagnetURL) {
		this.torrentMagnetURL = torrentMagnetURL;
	}
	
	
	
	
}
