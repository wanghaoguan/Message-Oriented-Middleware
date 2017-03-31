package com.push.server.push;

import com.push.server.cache.IOSUserCache;
import com.push.server.decode.DecodeInfo;
import com.push.server.decode.DecodeLoginInfo;

public class UnreadMessage {
	
	String info = null;
	
	public UnreadMessage(String info){
		this.info = info;
	}
	
	public String setCount(){
		DecodeInfo decodeInfo = new DecodeInfo();
		DecodeLoginInfo loginInfo = decodeInfo.decodeLoginInfo(info);
		
		IOSUserCache iosUserCache = new IOSUserCache();
		
		String userID = loginInfo.getUserID();
		int count = Integer.parseInt(iosUserCache.getCount(userID));
		count = count - 1;
		
		iosUserCache.put(userID, count+"");
		
		return userID;
	}

}
