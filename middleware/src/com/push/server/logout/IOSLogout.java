package com.push.server.logout;

import org.apache.log4j.Logger;

import com.push.server.cache.IOSUserCache;
import com.push.server.decode.DecodeInfo;
import com.push.server.decode.DecodeLoginInfo;

public class IOSLogout implements Logout{

	private Logger log = Logger.getLogger(IOSLogout.class.getName());
	
	private String info = null;
	
	public IOSLogout(String info){
		this.info = info;
	}
	
	@Override
	public String logout() {
		
		DecodeInfo decodeInfo = new DecodeInfo();
		DecodeLoginInfo loginInfo = decodeInfo.decodeLoginInfo(info);
		IOSUserCache iosUserCache = new IOSUserCache();
		
		String userID = loginInfo.getUserID();
	System.out.println("userID:"+userID);	
		if(iosUserCache.isOnline(userID)){
			
			log.info(userID+":user logout push server");
			iosUserCache.remove(userID);
		}
		
		return userID;
	}

}
