package com.push.server.database;

public class OfflineUser {

	private String userID;
	private Integer messageID;
	
	public String getUserID() {
		return userID;
	}
	
	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	public Integer getMessageID() {
		return messageID;
	}
	
	public void setMessageID(Integer messageID) {
		this.messageID = messageID;
	}
	
}
