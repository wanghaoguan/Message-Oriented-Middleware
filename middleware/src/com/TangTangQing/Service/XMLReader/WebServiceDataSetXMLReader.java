package com.TangTangQing.Service.XMLReader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.push.server.cache.MessageCache;
import com.push.server.cache.OfflineUserCache;
import com.push.server.database.Message;
import com.push.server.database.OfflineUser;
import com.push.server.decode.DecodePushInfo;
import com.push.server.decode.DecodePushInfoTTQ;

public class WebServiceDataSetXMLReader {

	//解析存储过程"将通知公告中的有效数据载入到推送中间件"
	public static List<Message> readPushNotification_Message(String notice){
		Document doc = null;
		List<Message> messages= new ArrayList<Message>();
		try{
			 // 将字符串转为XML
			doc=DocumentHelper.parseText(notice);
			Element rootElt=doc.getRootElement();//获取根节点
			Iterator iter=rootElt.elementIterator("Table");
			while(iter.hasNext()){
				Message msg= new Message();
				Element recordElt =(Element) iter.next();
				String messageID = recordElt.elementTextTrim("messageID");				
				String messageContent=recordElt.elementTextTrim("messageContent");				
				String publishTime=recordElt.elementTextTrim("publishTime");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
				Date publishDate =sdf.parse(publishTime);	
//				String publishTime_Alter=DateFormat.getDateInstance(DateFormat.MEDIUM).format(publishDate);
				long a = publishDate.getTime();
				msg.setMessageID(Integer.parseInt(messageID));
				msg.setMessage(messageContent);
				msg.setTime(a);
				
				messages.add(msg);
			}
			return messages;
		}
		catch(DocumentException e){
			e.printStackTrace();
			return messages;
		}
		catch (Exception e){
			e.printStackTrace();
			return messages;
		}
	}
	
	//解析存储过程，将公告-用户推送关系表中所有未推送的数据载入推送中间件
	public static List<OfflineUser> readPushNotification_Receiver(String notice){
		Document doc = null;
		List<OfflineUser> offlineUsers = new ArrayList<OfflineUser>();
		try{
			 // 将字符串转为XML
			doc=DocumentHelper.parseText(notice);
			Element rootElt= doc.getRootElement();//获取根节点
			Iterator iter =rootElt.elementIterator("Table");
			while(iter.hasNext()){
				OfflineUser olu= new OfflineUser();
				Element recordElt=(Element) iter.next();
				String UserID =recordElt.elementTextTrim("userID");				 
				String messageID = recordElt.elementTextTrim("messageID");
				
				olu.setUserID(UserID);
				olu.setMessageID(Integer.parseInt(messageID));
				offlineUsers.add(olu);
			}
			return offlineUsers;
		}
		catch(DocumentException e){
			e.printStackTrace();
			return null;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
			
		}		
	}
	
	//解析客户端新发布的通知公告，包含两张表，通知表和通知-用户关系表
	public static DecodePushInfoTTQ readnewNotice(String newNotic){
		Document doc =null;
		MessageCache msgCache = new MessageCache();
		OfflineUserCache offCache = new OfflineUserCache();
		String[] userIDs;
		DecodePushInfoTTQ pushInfo = new DecodePushInfoTTQ();
		int listSize=0;
		int i=0;
		try{
			doc=DocumentHelper.parseText(newNotic);
			Element rootElt=doc.getRootElement();
			Iterator iter=rootElt.elementIterator("Table");//解析通知表
			
				
			Element recordElt= (Element)iter.next();
			String messageID=recordElt.elementTextTrim("messageID");
			//System.out.println(messageID);
			String messageContent=recordElt.elementTextTrim("messageContent");
			//System.out.println(messageContent);
			String publishTime=recordElt.elementTextTrim("publishTime");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
			Date publishDate =sdf.parse(publishTime);	
		    
			//String publishTime_Alter=DateFormat.getDateInstance(DateFormat.MEDIUM).format(publishDate);
			
			msgCache.put(Integer.parseInt(messageID), messageContent, publishDate.getTime());
			pushInfo.setMessageID(Integer.parseInt(messageID));	
			pushInfo.setMessage(messageContent);
			iter=rootElt.elementIterator("Table1");//解析通知-用户信息表
						
			while(iter.hasNext()){
				listSize++;
				Element recordElt2= (Element)iter.next();
				
			}
			userIDs= new String[listSize];
			Iterator iter2=rootElt.elementIterator("Table1");
			while(iter2.hasNext()){
				Element recordElt2= (Element)iter2.next();
				String UserID =recordElt2.elementTextTrim("userID");				
				//String messageID = recordElt.elementTextTrim("messageID");				
				//offCache.put(UserID, Integer.parseInt(messageID));
				userIDs[i]=UserID;
				i++;
			}
			pushInfo.setUserIDs(userIDs);
			return pushInfo;
		}
		catch(DocumentException e){
			e.printStackTrace();
			return null;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
}
