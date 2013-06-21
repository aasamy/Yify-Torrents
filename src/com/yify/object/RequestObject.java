package com.yify.object;

import org.apache.commons.lang3.StringEscapeUtils;

public class RequestObject {
	
	private int requestID;
	private String movieTitle;
	private String ImdbLink;
	private String ImdbCode;
	private String coverImage;
	private String shortDescription;
	private String genre;
	private String movieRating;
	private String dateAdded;
	private int votes;
	private int userID;
	private String userName;
	private int type;
	
	public static final int CONFIRMED = 0;
	public static final int ONGOING = 1;
	
	public int getRequestID() {
		return requestID;
	}
	public void setRequestID(int requestID) {
		this.requestID = requestID;
	}
	public String getMovieTitle() {
		return movieTitle;
	}
	public void setMovieTitle(String movieTitle) {
		this.movieTitle = StringEscapeUtils.unescapeHtml4(movieTitle);
	}
	public String getImdbLink() {
		return ImdbLink;
	}
	public void setImdbLink(String imdbLink) {
		ImdbLink = imdbLink;
	}
	public String getImdbCode() {
		return ImdbCode;
	}
	public void setImdbCode(String imdbCode) {
		ImdbCode = imdbCode;
	}
	public String getCoverImage() {
		return coverImage;
	}
	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getMovieRating() {
		return movieRating;
	}
	public void setMovieRating(String movieRating) {
		this.movieRating = movieRating;
	}
	public String getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(String dateAdded) {
		this.dateAdded = dateAdded;
	}
	public int getVotes() {
		return votes;
	}
	public void setVotes(int votes) {
		this.votes = votes;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

}
