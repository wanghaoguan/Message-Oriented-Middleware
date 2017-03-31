package com.push.server.decode;

import org.apache.log4j.Logger;

import com.push.server.tool.ConfigMap;
import com.push.server.tool.XMLParser;

public class DecodeInfo {

	private Logger log = Logger.getLogger(DecodeInfo.class.getName());
	
	public DecodeLoginInfo decodeLoginInfo(String info){
		
		DecodeLoginInfo loginInfo = new DecodeLoginInfo();
		
		String delimiter = ConfigMap.getValue(XMLParser.PUSH_SERVER_IDDELIMITER);
		
		String userInfo[] = info.split(delimiter);
		
		if(userInfo.length ==3){
			
			loginInfo.setUserID(userInfo[0]);
			loginInfo.setDeviceID(userInfo[1]);
			loginInfo.setIsFirstTime(userInfo[2]);
			loginInfo.setFigure("");
			
		}else if(userInfo.length ==4){
			
			loginInfo.setUserID(userInfo[0]);
			loginInfo.setDeviceID(userInfo[1]);
			loginInfo.setIsFirstTime(userInfo[2]);
			loginInfo.setFigure(userInfo[3]);
			
		}else if(userInfo.length == 1){
			loginInfo.setUserID(userInfo[0]);
			loginInfo.setDeviceID("");
			loginInfo.setIsFirstTime("");
			loginInfo.setFigure("");
		}else {
			
System.out.println("登录消息格式错误");
			log.error("the login message format is error");
			return null;
		}
		
		return loginInfo;
	}
	
	public DecodePushInfo decodePushInfo(String info){
		
		DecodePushInfo pushInfo = new DecodePushInfo();
		
		String delimiterMsg = ConfigMap.getValue(XMLParser.PUSH_SERVER_MESSAGEDELIMITER);
		String delimiterID = ConfigMap.getValue(XMLParser.PUSH_SERVER_IDDELIMITER);
		String[] messageInfo = info.split(delimiterMsg);
		
		if(messageInfo.length == 2){
			
			String userIDs[] = messageInfo[0].split(delimiterID);
			pushInfo.setUserIDs(userIDs);
			pushInfo.setMessage(messageInfo[1]);
			
		}else if(messageInfo.length == 1){
			
			pushInfo.setMessage(messageInfo[0]);
			
		}else{
			
System.out.println("推送消息格式错误");
			log.error("push message format is error");
			return null;
		}
		
		return pushInfo;
	}
	
}
