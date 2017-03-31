package com.push.server;

import org.apache.log4j.Logger;

import com.push.server.cache.AndroidUserCache;
import com.push.server.cache.IOSUserCache;
import com.push.server.decode.MessageHeader;
import com.push.server.decode.MessageInfo;
import com.push.server.login.AndroidLogin;
import com.push.server.login.IOSLogin;
import com.push.server.logout.IOSLogout;
import com.push.server.push.PushMessage;
import com.push.server.push.UnreadMessage;
import com.push.server.tool.MessageType;
import com.TangTangQing.Service.push.getNewNoticFromDB;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerHandler extends ChannelHandlerAdapter{

	private static Logger log = Logger.getLogger(ServerHandler.class.getName());
	private String userID = null;
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		
		MessageInfo message = (MessageInfo)msg;
		MessageHeader header = message.getHeader();
		
		String info = message.getBody();
		int type = header.getType();
		
		if(type == MessageType.ANDROIDLOGIN_REQ.value()){
			userID = new AndroidLogin(ctx, info).login();
		}else if(type == MessageType.PUSH_REQ.value()){
			new PushMessage(ctx, info).pushMessage();
		}else if(type == MessageType.IOSLOGIN_REQ.value()){
			userID = new IOSLogin(ctx, info).login();
		}else if(type == MessageType.IOSLOGOUT_REQ.value()){
			userID = new IOSLogout(info).logout();
		}else if(type == MessageType.UNREADMESSAGENUM_RESP.value()){
			userID = new UnreadMessage(info).setCount();
		}else if(type == MessageType.TTQPUSH_REQ.value()){
			//using TTQ client to send a new notice
			new getNewNoticFromDB(ctx, info).getNoticeByID();
		}
		else{
			ctx.fireChannelRead(msg);
		}
	}
/*
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		
		AndroidUserCache androidUserCache = new AndroidUserCache();
		IOSUserCache iosUserCache = new IOSUserCache();
		
		if(userID != null && androidUserCache.isOnline(userID)){
			
			androidUserCache.remove(userID);
			log.error(userID+" user is exception��and is closed connection by server");

System.out.println(userID+"用户出现异常，关闭连接");
	
		}else if(userID != null && iosUserCache.isOnline(userID)){
//			iosUserCache.remove(userID);
			log.error(userID+" user is A short connection��and is closed connection by server");
		}else{
			
System.out.println("推送服务器退出，已关闭连接");
 			log.error("push complete, and close the connection");
		}
		ctx.close();
	}
*/
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		
		AndroidUserCache androidUserCache = new AndroidUserCache();
		IOSUserCache iosUserCache = new IOSUserCache();
		
		if(userID != null && androidUserCache.isOnline(userID)){
			Channel channel = androidUserCache.getChannel(userID);
			if( channel != null && channel.equals(ctx.channel())){
			
				androidUserCache.remove(userID);
				log.error(userID+" user is exception  and is closed connection by server");

System.out.println(userID+"用户出现异常，关闭连接");
			}
	
		}else if(userID != null && iosUserCache.isOnline(userID)){
//			iosUserCache.remove(userID);
			log.error(userID+" user is A short connection,and is closed connection by server");
		}else{
			
System.out.println("推送服务器退出，已关闭连接");
 			log.error("push complete, and close the connection");
		}
		ctx.close();
	}

}
