package com.push.server.push;

import java.util.ArrayList;
import java.util.List;

import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;
import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;

import com.TangTangQing.Service.push.OfflineMsgUserRemoveCache;
import com.push.server.cache.IOSUserCache;
import com.push.server.cache.MessageCache;
import com.push.server.cache.MessageCount;
import com.push.server.cache.OfflineUserCache;
import com.push.server.database.DBOperationImpl;

public class PushToIOS{
	
	private static Logger log = Logger.getLogger(PushToIOS.class.getName());
	

	public static void sendMessage(String userID, String message){
		
		IOSUserCache iosUserCache = new IOSUserCache();
		
		
		String token = iosUserCache.getDeviceID(userID);
		List<String> tokens = new ArrayList<String>();
		tokens.add(token);
		String figure = iosUserCache.getFigure(userID);
		
		String count = iosUserCache.getCount(userID);
		
		int countMessage = 0;
		if(count == null){
			countMessage = 0;
		}else{
			countMessage = Integer.parseInt(count);
		}
		//System.out.println(appMessage(message));
		apns(figure, tokens,appleMessage( message), 0, "1");
		
		log.info("ios user------push server sends message:"+message+" to the user:"+userID);
	}
	
	public static String appleMessage(String message)
	{
		String tem[]=message.split("\\$");
		
		return tem[2]+":"+tem[3];
		
	}
	
	public static void apns(String figure, List<String> tokens, String message, int badge, String sound){
		
		System.out.println("准备向APNS发送");		
		//֤��·��
		String certificatePath = null;
//				System.getProperty("user.dir")+"/config/push.p12";
		//֤������
		//certificatePath = System.getProperty("user.dir")+"/config/ttq_parents.p12";
		String certificatePassword = "";
		
		if(isTeacher(figure)){
			certificatePath = System.getProperty("user.dir")+"/config/ttq_teacher.p12";
			certificatePassword = "Admin310";
			System.out.println("发送给教师用户"+message);
		}else{
			certificatePath = System.getProperty("user.dir")+"/config/ttq_parents.p12";
			certificatePassword = "Admin310";
			System.out.println("发送给家长用户"+message);
		}
		
		//һ�η�����Ϣ������Ĭ��Ϊһ��
		boolean single = true;
		//��Ϣ���͵�����
		PushNotificationPayload payLoad = new PushNotificationPayload();
		try {
			
			if(StringUtils.isNotEmpty(message)){  
					payLoad.addAlert(message);// ��Ϣ����  
	        } 
			
			payLoad.addBadge(badge);// iphoneӦ��ͼ����С��Ȧ�ϵ���ֵ  
			if (!StringUtils.isBlank(sound)) {
				payLoad.addSound(sound);// ����  
			} 
			//��ios������Ϣ�Լ�������ƻ��SSLServerSocket������
			PushNotificationManager pushManager = new PushNotificationManager(); 
			try {
				//��ʼ�����Ӳ�����һ��SSLSocket
				pushManager.initializeConnection(new AppleNotificationServerBasicImpl(  
				        certificatePath, certificatePassword, true));
			} catch (CommunicationException e) {
				e.printStackTrace();
			} catch (KeystoreException e) {
				e.printStackTrace();
			} 
			//���ڲ��������г����쳣����Ϣ
			List<PushedNotification> notifications = new ArrayList<PushedNotification>();
			
			// ����push��Ϣ  
            if (single) {  
                Device device = new BasicDevice();  
                device.setToken(tokens.get(0));  
                PushedNotification notification;
				try {
					notification = pushManager.sendNotification(device, payLoad, true);
					notifications.add(notification); 
				} catch (CommunicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
                 
            } else {  
            	try {
                List<Device> device = new ArrayList<Device>();  
	                for (String token : tokens) {  
						device.add(new BasicDevice(token));
	                }  
	                try {
						notifications = pushManager.sendNotifications(payLoad, device);
					} catch (CommunicationException e) {
						e.printStackTrace();
					} catch (KeystoreException e) {
						e.printStackTrace();
					}
            	} catch (InvalidDeviceTokenFormatException e) {
					e.printStackTrace();
				} 
            }  
            //���ڼ�ⷢ��ʧ����Ϣ�ľ�����Ϣ
            List<PushedNotification> failedNotifications = PushedNotification  
                    .findFailedNotifications(notifications);  
            // List<PushedNotification> successfulNotifications =  
            // PushedNotification  
            // .findSuccessfulNotifications(notifications);  
            if (failedNotifications != null) {  
                int failed = failedNotifications.size();  
                String errorLog = "失败条数" + failed + "; ";  
                for (PushedNotification failedNotification : failedNotifications) {  
                    Device d = failedNotification.getDevice();  
                    errorLog += "deviceId=" + d.getDeviceId() + "; token="  
                            + d.getToken();  
                }  
                  
                log.info("消息推送失败记录" + errorLog);  
            } 
            
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static boolean isTeacher(String figure){
		if(figure.equals("1")){
			return true;
		}else return false;
	}
	
	public static void sendMessage(String userID){
		
		OfflineUserCache offlineUserCache = new OfflineUserCache();
		DBOperationImpl dbOperation = new DBOperationImpl();
		MessageCache messageCache = new MessageCache();
		MessageCount messageCount = new MessageCount();
		OfflineMsgUserRemoveCache  offlineMsgUserRemoveCache=new OfflineMsgUserRemoveCache();	
		
		ArrayList<Integer> arrayList = offlineUserCache.getList(userID);
		//内存中移除该用户的未发送 消息记录
		offlineUserCache.remove(userID);
		//dbOperation.deleteOfflineUser(userID);
		
		for(int i=0; i<arrayList.size(); i++){
			int t = (int)arrayList.get(i);
			String message = messageCache.getMessage(t);
			sendMessage(userID, message);
			//标明此条消息已发送
			offlineMsgUserRemoveCache.put(userID, t);
			log.info("ios user------push server sends message:"+message+" to the user:"+userID);
			messageCount.minusOne(t);
		}
	}

}
