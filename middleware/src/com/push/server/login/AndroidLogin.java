package com.push.server.login;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.TangTangQing.Service.push.OfflineMsgUserRemoveCache;
import com.push.server.cache.AndroidUserCache;
import com.push.server.cache.IOSUserCache;
import com.push.server.cache.MessageCache;
import com.push.server.cache.MessageCount;
import com.push.server.cache.OfflineUserCache;
import com.push.server.database.DBOperationImpl;
import com.push.server.decode.DecodeInfo;
import com.push.server.decode.DecodeLoginInfo;
import com.push.server.push.PushToAndroid;
import com.push.server.tool.MessageType;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class AndroidLogin implements Login{
	
	private AndroidUserCache androidUserCache = null;
	private IOSUserCache iosUserCache = null;
	private OfflineUserCache offlineUserCache = null;
	private DBOperationImpl dbOperation = null;
	private MessageCache messageCache = null;
	private MessageCount messageCount = null;
	private OfflineMsgUserRemoveCache  offlineMsgUserRemoveCache=null;
	
	private Logger log = Logger.getLogger(AndroidLogin.class.getName());
	
	private ChannelHandlerContext ctx = null;
	private String info = null;
	
	public AndroidLogin(ChannelHandlerContext ctx, String info){
		this.ctx = ctx;
		this.info = info;
		
		androidUserCache = new AndroidUserCache();
		iosUserCache = new IOSUserCache();
		offlineUserCache = new OfflineUserCache();
		dbOperation = new DBOperationImpl();
		messageCache = new MessageCache();
		messageCount = new MessageCount();
		offlineMsgUserRemoveCache= new OfflineMsgUserRemoveCache();
	}

	@Override
	public String login() {
		
		DecodeInfo decodeInfo = new DecodeInfo();
		DecodeLoginInfo loginInfo = decodeInfo.decodeLoginInfo(info);
		System.out.println(info);
		String userID = loginInfo.getUserID();
		
		if(androidUserCache.isOnline(userID)){
			
			androidOnlineUserLogin(loginInfo);
			
		}
//		else if(iosUserCache.isOnline(userID)){
//			
//			iosOnlineUserLogin(loginInfo);
//			
//		}
		else if(offlineUserCache.isOffline(userID)){
			
			offlineUserCacheLogin(loginInfo);
			
		}else{
			
			offlineUserLogin(loginInfo);
			
		}
		
		return userID;
		
	}

	@Override
	public void androidOnlineUserLogin(DecodeLoginInfo loginInfo) {
		
		String userID = loginInfo.getUserID();
		String deviceID = loginInfo.getDeviceID();
		
		System.out.println(userID+":用户重连");
		log.info(userID+" has reconnected the push server");
		
		Channel channel = androidUserCache.getChannel(userID);
		
		if((deviceID != null) && androidUserCache.getDeviceID(userID).equals(deviceID)){
			
			sameDeviceIDUserLogin(channel, loginInfo);
			
		}else if((deviceID != null) && (!androidUserCache.getDeviceID(userID).equals(deviceID))){
			
			differentDeviceIDUserLogin(channel, loginInfo);
			
		}else{
			
			System.out.println("登录信息中设备号字段信息错误");
			log.error("the device number from login message is error");
			return ;
			
		}
	}

	@Override
	public void iosOnlineUserLogin(DecodeLoginInfo loginInfo) {
		
		log.info(loginInfo.getUserID()+":user switchs to the android device");
		
		iosUserCache.remove(loginInfo.getUserID());
		androidUserCache.put(loginInfo.getUserID(), ctx.channel());
		androidUserCache.put(loginInfo.getUserID(), loginInfo.getDeviceID());
		
		int type = MessageType.ANDROIDLOGIN_RESP.value();
		Help.respMsg(type, ctx);
	}

	@Override
	public void offlineUserCacheLogin(DecodeLoginInfo loginInfo) {
		
		String userID = loginInfo.getUserID();
		
		System.out.println(userID+"::离线用户登录");
		log.info(userID+":offline user has successfully logined push server");
		
		int type = MessageType.ANDROIDLOGIN_RESP.value();
		Help.respMsg(type, ctx);
		
		ArrayList<Integer> arrayList = offlineUserCache.getList(userID);
		offlineUserCache.remove(userID);
		//dbOperation.deleteOfflineUser(userID);
		
		
		for(int i=0; i<arrayList.size(); i++){
			
			int t = (int)arrayList.get(i);
			String message = messageCache.getMessage(t);
			
			PushToAndroid.sendMessage(ctx, message);
			offlineMsgUserRemoveCache.put(userID, t);
			messageCount.minusOne(t);
			
		}
//		offlineMsgUserRemoveCache.updateToDB();
		androidUserCache.put(userID, ctx.channel());
		androidUserCache.put(userID, loginInfo.getDeviceID());
	}

	@Override
	public void offlineUserLogin(DecodeLoginInfo loginInfo) {
		
		System.out.println(loginInfo.getUserID()+":登录成功");
		log.info(loginInfo.getUserID()+": has logined sucessfully push server");
		
		androidUserCache.put(loginInfo.getUserID(), ctx.channel());
		androidUserCache.put(loginInfo.getUserID(), loginInfo.getDeviceID());
		
		int type = MessageType.ANDROIDLOGIN_RESP.value();
		Help.respMsg(type, ctx);
		
	}

	public void sameDeviceIDUserLogin(Channel channel, DecodeLoginInfo loginInfo){
		
		channel.close();
		
		androidUserCache.remove(loginInfo.getUserID());
		androidUserCache.put(loginInfo.getUserID(), ctx.channel());
		androidUserCache.put(loginInfo.getUserID(), loginInfo.getDeviceID());
		
		int type = MessageType.ANDROIDLOGIN_RESP.value();
		Help.respMsg(type, ctx);
		
	}
	
	public void differentDeviceIDUserLogin(Channel channel, DecodeLoginInfo loginInfo){
		
		System.out.println("使用其他设备登录");
		
		if(loginInfo.getIsFirstTime().equals("1")){
			
			firstTimeLogin(channel, loginInfo);
			
		}else if(loginInfo.getIsFirstTime().equals("0")){
			
			notFirstTimeLogin();
			
		}else{
			
			System.out.println("登录信息中是否第一次登录字段错误");
			log.error("the frist time login field from login message is error");
			return ;
			
		}
	}
	
	public void firstTimeLogin(Channel channel, DecodeLoginInfo loginInfo){
		
		System.out.println("第一次使用该设备登录");
		
		androidUserCache.remove(loginInfo.getUserID());
		androidUserCache.put(loginInfo.getUserID(), ctx.channel());
		androidUserCache.put(loginInfo.getUserID(), loginInfo.getDeviceID());
				
		
		int type = MessageType.OTHERDEVLOGIN_RESP.value();
		Help.respMsg(type, channel);
		
	}
	
	public void notFirstTimeLogin(){
		
		System.out.println("曾经在这个设备登录过");
		
		int type = MessageType.OTHERDEVLOGIN_RESP.value();
		Help.respMsg(type, ctx);
		
	}
	
}
