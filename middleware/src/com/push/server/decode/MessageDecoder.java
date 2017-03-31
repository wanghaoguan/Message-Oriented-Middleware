package com.push.server.decode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.util.ReferenceCountUtil;

import org.apache.log4j.Logger;

import com.push.server.tool.ConfigMap;
import com.push.server.tool.DataConvert;
import com.push.server.tool.XMLParser;

public class MessageDecoder extends DelimiterBasedFrameDecoder{
	
	private static Logger log = Logger.getLogger(MessageDecoder.class.getName());

	public MessageDecoder(int maxFrameLength, ByteBuf delimiter) {
		super(maxFrameLength, delimiter);
	}

	@Override
	protected Object decode(ChannelHandlerContext arg0, ByteBuf arg1)
			throws Exception {
		log.info("push server has received the message");
		ByteBuf buf = (ByteBuf)super.decode(arg0, arg1);
		byte[] req = null;
		try{
			req = new byte[buf.readableBytes()];
		}catch(NullPointerException e){
			log.error("the data from bytebuf is null");
		}
		buf.readBytes(req);
		
		//使用内存池后要明确的释放缓冲区bytebuf的内存，防止内存泄露
		ReferenceCountUtil.release(buf);
		
		return decodeHelper(req);
	}
	
	public MessageInfo decodeHelper(byte[] req){
		
		MessageHeader header = new MessageHeader();
		MessageInfo message = new MessageInfo();
		
		int length = -1;
		int type = -1;
		String body = null;
		
		try{
			int messageLength = Integer.parseInt(ConfigMap.getValue(XMLParser.PUSH_SERVER_MESSAGE_LENGTH));
			int typeLength = Integer.parseInt(ConfigMap.getValue(XMLParser.PUSH_SERVER_MESSAGE_TYPE));
			int headerLength = messageLength+typeLength;
			
			type = DataConvert.byteArraayToInt(DataConvert.cutOutByte(req,typeLength,headerLength));
			length = DataConvert.byteArraayToInt(DataConvert.cutOutByte(req, 0,typeLength));
			length = length-ConfigMap.getValue(XMLParser.PUSH_SERVER_DELIMITER).length();
			
			body = new String(DataConvert.cutOutByte(req, headerLength, headerLength+length));
		}catch(Exception e){
			log.error("message decode error", e);
		}
		header.setLength(length);
		header.setType(type);
		
		message.setHeader(header);
		message.setBody(body);
		System.out.println(body);
		return message;
	}
	
	
}
