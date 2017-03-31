package com.push.server.decode;

public class DecodeLoginInfo {
	
	private String userID = null;
	private String deviceID = null;
	private String isFirstTime = null;
	private String figure = null;
	
	public String getFigure() {
		return figure;
	}
	public void setFigure(String figure) {
		this.figure = figure;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public String getIsFirstTime() {
		return isFirstTime;
	}
	public void setIsFirstTime(String isFirstTime) {
		this.isFirstTime = isFirstTime;
	}
	
}
