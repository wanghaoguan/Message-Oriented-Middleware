package com.push.server.push;

import org.apache.log4j.Logger;

import com.TangTangQing.Service.GetService.GetTTQService;
import com.push.server.cache.AndroidUserCache;
import com.push.server.cache.IOSUserCache;
import com.push.server.cache.MessageCache;
import com.push.server.cache.MessageCount;
import com.push.server.cache.OfflineUserCache;
import com.push.server.database.DBOperationImpl;
import com.push.server.database.OfflineUser;
import com.push.server.decode.DecodeInfo;
import com.push.server.decode.DecodePushInfo;
import com.push.server.decode.DecodePushInfoTTQ;
import com.push.server.login.Help;
import com.push.server.tool.MessageType;

import io.netty.channel.ChannelHandlerContext;

public class PushMessage {
	
	private IOSUserCache iosUserCache = null;
	private AndroidUserCache androidUserCache = null;
	private MessageCache messageCache = null;
	private MessageCount messageCount = null;
	private OfflineUserCache offlineUserCache = null;
	
	private ChannelHandlerContext ctx = null;
	private String info = null;
	
	private Logger log = Logger.getLogger(PushMessage.class.getName());
			
	public PushMessage(ChannelHandlerContext ctx, String info){
		this.ctx = ctx;
		this.info = info;
		
		iosUserCache = new IOSUserCache();
		messageCache = new MessageCache();
		androidUserCache = new AndroidUserCache();
		messageCount = new MessageCount();
		offlineUserCache = new OfflineUserCache();
	}
	
	public void pushMessage(){
		
		DecodeInfo decodeInfo = new DecodeInfo();
		DecodePushInfo pushInfo = decodeInfo.decodePushInfo(info);
		
		int type = MessageType.PUSH_RESP.value();
		Help.respMsg(type, ctx);
		
		int key = sendToClient(pushInfo);
		
		if(key != 0){
			messageCache.remove(key);
		}
	}
	
	public void pushMessage(DecodePushInfoTTQ pushInfo){
		
//		DecodeInfo decodeInfo = new DecodeInfo();
//		DecodePushInfo pushInfo = decodeInfo.decodePushInfo(info);
		
		int type = MessageType.PUSH_RESP.value();
		Help.respMsg(type, ctx);
		
		int key = sendToClient(pushInfo);
		
		if(key != 0){
			messageCache.remove(key);
		}
	}
	
	public int sendToClient(DecodePushInfo pushInfo){
		
		//�ж����û�û�н��յ�������Ϣ
		int messageNum = 0;
		
		//�ж���Ϣ�Ƿ�ȫ���������
		boolean isFinish = true;
		
		//ϵͳ��ǰʱ��
		long currentTime = System.currentTimeMillis();
		
	 	//�Զ������Ϣ���
		int messagekey = messageCache.put(pushInfo.getMessage(),currentTime);
		
		String[] userIDs = pushInfo.getUserIDs();
		
		for(int i=0; i<pushInfo.getUserIDs().length; i++){
			
			if(androidUserCache.isOnline(userIDs[i])){
				PushToAndroid.sendMessage(userIDs[i], pushInfo.getMessage());
			}else if(iosUserCache.isOnline(userIDs[i])){
				PushToIOS.sendMessage(userIDs[i], pushInfo.getMessage());
			}else{
				putOfflineUser(userIDs[i], messagekey);
				messageNum++;
				isFinish = false;
			}
			
		}
		
		log.info(messagekey+" :message has pushed");
		
		if(isFinish){
			
			return messagekey;
			
		}else{
			
			messageCount.put(messagekey, messageNum);
			return 0;
		}
	}
	
public int sendToClient(DecodePushInfoTTQ pushInfo){
		
		//�ж����û�û�н��յ�������Ϣ
		int messageNum = 0;
		
		//�ж���Ϣ�Ƿ�ȫ���������
		boolean isFinish = true;
		
		//ϵͳ��ǰʱ��
		long currentTime = System.currentTimeMillis();
		
	 	//�Զ������Ϣ���
		//int messagekey = messageCache.put(pushInfo.getMessage(),currentTime);
		int messagekey=pushInfo.getMessageID();
		
		
		String[] userIDs = pushInfo.getUserIDs();
		
		for(int i=0; i<pushInfo.getUserIDs().length; i++){
			
			if(androidUserCache.isOnline(userIDs[i])){
				PushToAndroid.sendMessage(userIDs[i], pushInfo.getMessage());
			}else if(iosUserCache.isOnline(userIDs[i])){
				PushToIOS.sendMessage(userIDs[i], pushInfo.getMessage());
			}else{
				putOfflineUser(userIDs[i], messagekey);
				messageNum++;
				isFinish = false;
			}
			
		}
		
		log.info(messagekey+" :message has pushed");
		
		if(isFinish){
			
			return messagekey;
			
		}else{
			
			messageCount.put(messagekey, messageNum);
			return 0;
		}
	}

	public void putOfflineUser(String userID, int messageKey){
		
		//DBOperationImpl dbOperation = new DBOperationImpl();
		
		offlineUserCache.put(userID, messageKey);
//		OfflineUser offlineUser = new OfflineUser();
//		offlineUser.setUserID(userID);
//		offlineUser.setMessageID(messageKey);
		//dbOperation.insertOfflineUser(offlineUser);		
		//GetTTQService.getService().getTangTangQingServiceSoap().appPushNotificationReceiverUpdatePushState(userID, messageKey);
	}
}
