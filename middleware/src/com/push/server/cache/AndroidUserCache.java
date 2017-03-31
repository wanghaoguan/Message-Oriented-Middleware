package com.push.server.cache;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public class AndroidUserCache {
	
	//存储在线用户，用户ID，对应的Channel
	private static Map<String, Channel> onlineUser = new HashMap<String, Channel>();
	private static Map<String, String> deviceNum = new HashMap<String, String>();
	
	/*
	 * 同步添加在线用户
	 */

	public synchronized void put(String id,Channel sc){
		onlineUser.put(id, sc);
	}
	
	/*
	 * 同步移除在线用户
	 */
	public synchronized void remove(String id){
		onlineUser.remove(id);
		deviceNum.remove(id);
	}
	
	/*
	 * 在线用户登录的设备
	 */
	public synchronized void put(String userID, String deviceID){
		deviceNum.put(userID, deviceID);
	}
	
	/*
	 * 获取登录的设备ID
	 */
	public String getDeviceID(String userID){
		return deviceNum.get(userID);
	}
	/*
	 * 获取在线用户的人数
	 */
	public int size(){
		return onlineUser.size();
	}
	
	/*
	 * 判断该用户是否在线
	 */
	public boolean isOnline(String id){
		return onlineUser.containsKey(id);
	}
	
	/*
	 * 得到该用户的Channel
	 */
	public Channel getChannel(String id){
		return onlineUser.get(id);
	}
	
	/*
	 * 获取所有在线用户的Map
	 */
	public Map<String, Channel> getOnlineUser(){
		return onlineUser;
	}
	
}
