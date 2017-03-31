package com.push.server.push;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import com.push.server.cache.AndroidUserCache;
import com.push.server.tool.ConfigMap;
import com.push.server.tool.DataConvert;
import com.push.server.tool.MessageType;
import com.push.server.tool.XMLParser;

public class PushToAndroid{
	
	private static Logger log = Logger.getLogger(PushToAndroid.class.getName());
	
	public static void sendMessage(String userID, String message){
		
		AndroidUserCache androidUserCache = new AndroidUserCache();
		
		Channel channel = androidUserCache.getChannel(userID);
		
		int type = MessageType.SENDMESSAGE.value();
		byte[] typeByte = DataConvert.intToByteArray(type);
		byte[] body = (message+ConfigMap.getValue(XMLParser.PUSH_SERVER_DELIMITER)).getBytes();
		int lengthBody = body.length;
		byte[] lengthByte = DataConvert.intToByteArray(lengthBody);
		byte[] header = DataConvert.mergeByteArray(lengthByte, typeByte);
		
		byte[] sendMessage = DataConvert.mergeByteArray(header, body);
		int length = sendMessage.length;
		
		ByteBuf buf = Unpooled.buffer(length);
		buf.writeBytes(sendMessage);
		channel.writeAndFlush(buf);
		log.info("android user------push server sends message:"+message+" to the user:"+userID);
	}
	
	public static void sendMessage(ChannelHandlerContext ctx, String message){
		byte[] body = (message+ConfigMap.getValue(XMLParser.PUSH_SERVER_DELIMITER)).getBytes();
		
		int bodyLength = body.length;
		byte[] lengthByte = DataConvert.intToByteArray(bodyLength);
		int type1 = MessageType.SENDMESSAGE.value();
		byte[] typeByte = DataConvert.intToByteArray(type1);
		
		byte[] header = DataConvert.mergeByteArray(lengthByte, typeByte);
		
		byte[] sendMessage = DataConvert.mergeByteArray(header, body);
		int length1 = sendMessage.length;
		
		ByteBuf bodyBuf = Unpooled.buffer(length1);
		bodyBuf.writeBytes(sendMessage);
		ctx.writeAndFlush(bodyBuf);
	}

}
