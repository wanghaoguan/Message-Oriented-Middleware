package com.push.server.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.push.server.database.DBOperationImpl;
import com.push.server.database.Message;

public class MessageCache {

	private static Map<Integer, String> messageMap = new HashMap<Integer, String>();
	//��¼��Ϣ¼���ʱ��
	private static Map<Integer, Long> time = new HashMap<Integer, Long>();
	
	private static DBOperationImpl dbOperation = new DBOperationImpl();
	
	private static int key = dbOperation.selectMessageMaxID()+1;
	
	public synchronized int put(String value,long currentTime){
	
		messageMap.put(key, value);
		
		time.put(key, currentTime);
		
		Message message = new Message();
		message.setMessageID(key);
		message.setMessage(value);
		message.setTime(currentTime);
		
		dbOperation.insertMessage(message);
		
		return key++;
	}
	
	public  synchronized void put(int messageID, String message, long currentTime){
		messageMap.put(messageID, message);
		time.put(messageID, currentTime);
	}
	
	public synchronized void remove(int messageID){
		messageMap.remove(messageID);
		time.remove(messageID);
//		dbOperation.deleteMessage(messageID);
	}
	
	public synchronized void removeAll(List<Integer> list){
		
		for(int i=0; i<list.size(); i++){
			messageMap.remove((int)list.get(i));
			time.remove((int)list.get(i));
			dbOperation.deleteMessage((int)list.get(i));
		}
	}
	
	public Map<Integer, Long> getTimeMap(){
		return time;
	}
	
	public long getTime(int messageID){
		return time.get(messageID);
	}
	
	public String getMessage(int messageID){
		return messageMap.get(messageID);
	}
	
	public int size(){
		return messageMap.size();
	}
	
	public void printMessageCache(){
		Iterator iter = messageMap.entrySet().iterator();
		int count =0;
		while(iter.hasNext()){
			count++;
			Map.Entry entry=(Map.Entry) iter.next();
			Object key=entry.getKey();
			Object val=entry.getValue();
			System.out.print("messageID:"+key+"\t");
			System.out.println("content:"+val);
		}
		System.out.print("�ܹ���"+count+"����Ϣ");
		
		Iterator iterTime = time.entrySet().iterator();
		int countTime =0;
		while(iterTime.hasNext()){
			countTime++;
			Map.Entry entry=(Map.Entry) iterTime.next();
			Object key=entry.getKey();
			Object val=entry.getValue();
			System.out.print("messageID:"+key+"\t");
			System.out.println("publishTime:"+val);
		}
		System.out.print("�ܹ���"+countTime+"����Ϣ");
	}
}
