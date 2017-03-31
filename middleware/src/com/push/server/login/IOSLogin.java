package com.push.server.login;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;

import com.push.server.cache.AndroidUserCache;
import com.push.server.cache.IOSUserCache;
import com.push.server.cache.OfflineUserCache;
import com.push.server.decode.DecodeInfo;
import com.push.server.decode.DecodeLoginInfo;
import com.push.server.push.PushToIOS;
import com.push.server.tool.MessageType;

public class IOSLogin implements Login{
	
	private AndroidUserCache androidUserCache = null;
	private IOSUserCache iosUserCache = null;
	private OfflineUserCache offlineUserCache = null;
	
	private ChannelHandlerContext ctx = null;
	private String info = null;
	
	private Logger log = Logger.getLogger(IOSLogin.class.getName());
	
	public IOSLogin(ChannelHandlerContext ctx, String info){
		this.ctx = ctx;
		this.info = info;
		
		androidUserCache = new AndroidUserCache();
		iosUserCache = new IOSUserCache();
		offlineUserCache = new OfflineUserCache();
	}

	@Override
	public String login() {
		
		DecodeInfo decodeInfo = new DecodeInfo();
		DecodeLoginInfo loginInfo = decodeInfo.decodeLoginInfo(info);
		
		String userID = loginInfo.getUserID();
		
//		if(androidUserCache.isOnline(userID)){
//			androidOnlineUserLogin(loginInfo);
//		}else 
		if(iosUserCache.isOnline(userID)){
			iosOnlineUserLogin(loginInfo);
		}else if(offlineUserCache.isOffline(userID)){
			offlineUserCacheLogin(loginInfo);
		}else{
			offlineUserLogin(loginInfo);
		}
		
		return userID;
		
	}

	@Override
	public void androidOnlineUserLogin(DecodeLoginInfo loginInfo) {
		
		log.info(loginInfo.getUserID()+":user switchs to the ios device");
		
		androidUserCache.remove(loginInfo.getUserID());
		iosUserCache.put(loginInfo.getUserID(), loginInfo.getDeviceID(),loginInfo.getFigure());
		
		int type = MessageType.IOSLOGIN_RESP.value();
		Help.respMsg(type, ctx);
		
	}

	@Override
	public void iosOnlineUserLogin(DecodeLoginInfo loginInfo) {
		
		log.info(loginInfo.getUserID()+":user relogins push server with ios device");
		
//		String saveDeviceID = iosUserCache.getDeviceID(loginInfo.getDeviceID());
		
//		if(!saveDeviceID.equals(loginInfo.getDeviceID())){
			iosUserCache.remove(loginInfo.getUserID());
			iosUserCache.put(loginInfo.getUserID(), loginInfo.getDeviceID(), loginInfo.getFigure());
//		}
		
	}

	@Override
	public void offlineUserCacheLogin(DecodeLoginInfo loginInfo) {
		
		log.info(loginInfo.getUserID()+":offline user logins push server with ios device");
		
		iosUserCache.put(loginInfo.getUserID(), loginInfo.getDeviceID(), loginInfo.getFigure());
		
		PushToIOS.sendMessage(loginInfo.getUserID());
		
		int type = MessageType.IOSLOGIN_RESP.value();
		Help.respMsg(type, ctx);
	}

	@Override
	public void offlineUserLogin(DecodeLoginInfo loginInfo) {
		
		System.out.println(loginInfo.getUserID()+":登录成功");
		log.info(loginInfo.getUserID()+":user logins push server with ios device");
		
		iosUserCache.put(loginInfo.getUserID(), loginInfo.getDeviceID(), loginInfo.getFigure());
		
		int type = MessageType.IOSLOGIN_RESP.value();
		Help.respMsg(type, ctx);
	}

}
