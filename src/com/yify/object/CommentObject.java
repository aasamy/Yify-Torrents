package com.yify.object;

import org.apache.commons.lang3.StringEscapeUtils;

public class CommentObject {

	private int commentID;
	private String text;
	private int userID;
	private String parentCommentID;
	private String username;
	private String userGroup;
	private String dateAdded;
	
	
	public int getCommentID() {
		return commentID;
	}
	public void setCommentID(int commentID) {
		this.commentID = commentID;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = StringEscapeUtils.unescapeHtml4(text).replace("<br />", "");
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getParentCommentID() {
		return parentCommentID;
	}
	public void setParentCommentID(String parentCommentID) {
		this.parentCommentID = parentCommentID;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserGroup() {
		return userGroup;
	}
	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}
	public String getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(String dateAdded) {
		this.dateAdded = dateAdded;
	}
	
}
