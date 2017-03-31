package com.push.server.cache;

import java.util.HashMap;
import java.util.Map;

public class IOSUserCache {
	
	//ios在线用户的ID和唯一标识
	private static Map<String, String> iosUserMap = new HashMap<String, String>();
	private static Map<String, String> figureMap = new HashMap<String, String>();
	private static Map<String, String> countMap = new HashMap<String, String>();
	
	public synchronized void put(String userID,String deviceID,String figure){
		iosUserMap.put(userID, deviceID);
		figureMap.put(userID, figure);
	}
	
	public synchronized void remove(String userID){
		iosUserMap.remove(userID);
		figureMap.remove(userID);
	}
	
	public String getDeviceID(String userID){
		return iosUserMap.get(userID);
	}
	
	public String getFigure(String userID){
		return figureMap.get(userID);
	}
	
	public boolean isOnline(String userID){
		return iosUserMap.containsKey(userID);
	}
	
	public String getCount(String userID){
		return countMap.get(userID);
	}
	
	public synchronized void put(String userID, String count){
		countMap.put(userID, count);
	}
	
	public int size(){
		return iosUserMap.size();
	}
}
