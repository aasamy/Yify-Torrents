package com.yify.mobile;

public class UserObject {

	private int userID;
	private String username;
	private String joinDate;
	private String lastSeenDate;
	private int torrentsDownloaded;
	private int moviesRequested;
	private int commentCount;
	private int chatTimeSeconds;
	private String avatar;
	private String userRole;
	private String about;
	
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getJoinDate() {
		return joinDate;
	}
	public void setJoinDate(String joinDate) {
		this.joinDate = joinDate;
	}
	public String getLastSeenDate() {
		return lastSeenDate;
	}
	public void setLastSeenDate(String lastSeenDate) {
		this.lastSeenDate = lastSeenDate;
	}
	public int getTorrentsDownloaded() {
		return torrentsDownloaded;
	}
	public void setTorrentsDownloaded(int torrentsDownloaded) {
		this.torrentsDownloaded = torrentsDownloaded;
	}
	public int getMoviesRequested() {
		return moviesRequested;
	}
	public void setMoviesRequested(int moviesRequested) {
		this.moviesRequested = moviesRequested;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public int getChatTimeSeconds() {
		return chatTimeSeconds;
	}
	public void setChatTimeSeconds(int chatTimeSeconds) {
		this.chatTimeSeconds = chatTimeSeconds;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	
	
	
}
