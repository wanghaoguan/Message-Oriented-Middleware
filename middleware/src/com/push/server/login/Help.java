package com.push.server.login;

import com.push.server.tool.ConfigMap;
import com.push.server.tool.DataConvert;
import com.push.server.tool.XMLParser;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class Help {
	
	public static void respMsg(int type, ChannelHandlerContext ctx){
		byte[] byteresp = buildResp("success", type);
		ByteBuf resp = Unpooled.copiedBuffer(byteresp);
		ctx.writeAndFlush(resp);
	}
	
	public static void respMsg(int type, Channel ctx){
		byte[] byteresp = buildResp("Offline Notice", type);
		ByteBuf resp = Unpooled.copiedBuffer(byteresp);
		ctx.writeAndFlush(resp);
	}
	
	public static byte[] buildResp(String str, int type){
		
		String body = str+ConfigMap.getValue(XMLParser.PUSH_SERVER_DELIMITER);
		int length = body.length();
		byte[] header = DataConvert.mergeByteArray(DataConvert.intToByteArray(length),DataConvert.intToByteArray(type));
		
		byte[] resp = DataConvert.mergeByteArray(header, body.getBytes());
		return resp;
	}

}
