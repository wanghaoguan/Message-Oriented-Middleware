package com.push.server.decode;

public class DecodePushInfoTTQ {

	private String[] userIDs = null;
	private String message = null;
	private int messageID = 0;
	
	public int getMessageID() {
		return messageID;
	}
	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}
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
