package com.push.server;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.push.server.cache.AndroidUserCache;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartBeat extends ChannelHandlerAdapter{

	private static Logger log = Logger.getLogger(HeartBeat.class.getName());
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		super.channelRead(ctx, msg);
System.out.println("心跳消息");
		ctx.fireChannelRead(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		
		AndroidUserCache androidUserCache = new AndroidUserCache();
		IdleStateEvent e = (IdleStateEvent)evt;
		
		if(e.state() == IdleState.ALL_IDLE){

			Map<String, Channel> onlineUser = androidUserCache.getOnlineUser();
			Iterator iter = onlineUser.entrySet().iterator();
			boolean flag = true;
			
			while (iter.hasNext()) {
				
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				String id = key.toString();
				Object val = entry.getValue();
				SocketChannel  ctxValue = (SocketChannel)val;
				
				if(ctx.channel() == ctxValue){
					
					androidUserCache.remove(id);
					log.info(id+" heartbeat timeout��remove the user from the offline cache");
System.out.println("心跳信息超时了");
					ctx.channel().close();
					flag = false;
					break;
				}
			}
			
			if(flag){
				log.info("the user ID is null,so it is a short link");
System.out.println("该连接的用户为推送服务器--已经退出");
				ctx.close();
			}
			
		}
	}

}
