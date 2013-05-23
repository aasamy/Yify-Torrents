package com.yify.object;

public class AuthUserObject extends UserObject {
	
	private String ipAddress;
	private int numVotesLeft;
	private int numRequestsLeft;
	private boolean profileActive;
	
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public int getNumVotesLeft() {
		return numVotesLeft;
	}
	public void setNumVotesLeft(int numVotesLeft) {
		this.numVotesLeft = numVotesLeft;
	}
	public int getNumRequestsLeft() {
		return numRequestsLeft;
	}
	public void setNumRequestsLeft(int numRequestsLeft) {
		this.numRequestsLeft = numRequestsLeft;
	}
	public boolean isProfileActive() {
		return profileActive;
	}
	public void setProfileActive(boolean profileActive) {
		this.profileActive = profileActive;
	}
	

}
