package com.push.server.other;

import java.util.List;

import org.apache.log4j.Logger;

import com.TangTangQing.Service.GetService.GetTTQService;
import com.TangTangQing.Service.XMLReader.WebServiceDataSetXMLReader;
import com.push.server.cache.MessageCache;
import com.push.server.cache.MessageCount;
import com.push.server.cache.OfflineUserCache;
import com.push.server.database.DBOperationImpl;
import com.push.server.database.Message;
import com.push.server.database.OfflineUser;


public class InitCache implements Runnable{
	
	//private DBOperationImpl dbOperation = new DBOperationImpl();
	private MessageCache messageCache = new MessageCache();
	private OfflineUserCache offlineUserCache = new OfflineUserCache();
	private MessageCount messageCount = new MessageCount();
	private List<OfflineUser> offlineUser = null;
	private List<Message> messages = null;
	private Logger log = Logger.getLogger(InitCache.class.getName());
	
	public InitCache(){
		
	}
	
	public void initOfflineUserCache(){
		//offlineUser = dbOperation.selectAllOfflineUser();
		String PushNotification_receiver= GetTTQService.getService().getMidServiceSoap().appPushNotificationReceiverToMid();
		
		offlineUser = WebServiceDataSetXMLReader.readPushNotification_Receiver(PushNotification_receiver);
		for(int i=0; i<offlineUser.size(); i++){
			OfflineUser user = offlineUser.get(i);
			String userID = user.getUserID();
			int messageID = user.getMessageID();
			offlineUserCache.put(userID, messageID);
			messageCount.addOne(messageID);
		}
		
		//offlineUserCache.printOfflineUserCache();
		log.info("init offline user cache successfully");
		
	}
	
	public void initMessageCache(){
		//messages = dbOperation.selectAllMessage();
		String PushNotification_Message= GetTTQService.getService().getMidServiceSoap().appPushNotificationMessageToMid();
		messages=WebServiceDataSetXMLReader.readPushNotification_Message(PushNotification_Message);
		for(int i=0; i<messages.size(); i++){
			Message message = messages.get(i);
			int messageID = message.getMessageID();
			String messageBody = message.getMessage();
			long time = message.getTime();
			messageCache.put(messageID, messageBody, time);
		}
		
		//messageCache.printMessageCache();
		log.info("init message cache successfully");
	}

	@Override
	public void run() {
		initMessageCache();
		initOfflineUserCache();
	}

}
