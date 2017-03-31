package com.push.server.cache;

import java.util.HashMap;
import java.util.Map;

public class MessageCount {

	private static Map<Integer, Integer> messageCount = new HashMap<Integer, Integer>();
	private MessageCache messageCache = new MessageCache();
	
	public synchronized void put(int messageID,int count){
		messageCount.put(messageID, count);
	}
	
	public synchronized void remove(int messageID){
		messageCount.remove(messageID);
	}
	
	
	public boolean isContains(int messageID){
		return messageCount.containsKey(messageID);
	}
	
	public int getCount(int messageID){
		int count = -1;
		try{
			count = messageCount.get(messageID);
		}catch(NullPointerException e){
			count = 0;
		}
		return count;
	}
	
	public synchronized void minusOne(int messageID){
		if(isContains(messageID)){
			int tmp = getCount(messageID);
			tmp = tmp - 1;
			remove(messageID);
			if(tmp == 0){
				messageCache.remove(messageID);
			}else{
				put(messageID, tmp);
				
			}
			
		}
	}
	
	public synchronized void addOne(int messageID){
		if(isContains(messageID)){
			int tmp = getCount(messageID);
			tmp = tmp + 1;
			remove(messageID);
			put(messageID, tmp);
		}else{
			put(messageID, 1);
		}
	}

}
