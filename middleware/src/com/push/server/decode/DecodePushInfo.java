package com.push.server.decode;

public class DecodePushInfo {
	
	private String[] userIDs = null;
	private String message = null;
	
	public String[] getUserIDs() {
		return userIDs;
	}
	public void setUserIDs(String[] userIDs) {
		this.userIDs = userIDs;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
