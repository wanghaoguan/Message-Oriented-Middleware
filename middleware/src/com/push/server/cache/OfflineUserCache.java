package com.push.server.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OfflineUserCache {
	//�洢android�������û����û�ID����Ϣ�б�
	private static Map<String, ArrayList<Integer>> offlineUser = new HashMap<String, ArrayList<Integer>>();
	
	/*
	 * ͬ����������û�
	 */
	public  synchronized void put(String userID,int messageID){
//			offlineUser.put(id, value);
		if(isOffline(userID)){
			ArrayList<Integer> arrayList = getList(userID);
			arrayList.add(messageID);
		}else{
			ArrayList<Integer> arrayList = new ArrayList<Integer>();
			arrayList.add(messageID);
			put(userID, arrayList);
		}
		
	}
	
	public  synchronized void put(String id,ArrayList<Integer> value){
		offlineUser.put(id, value);
	}
	
	/*
	 * ͬ���Ƴ������û�
	 */
	public synchronized void remove(String id){
		offlineUser.remove(id);
		
	}
	
	/*
	 * ��ȡ�����û�����Ϣ�б�
	 */
	public  ArrayList<Integer> getList(String key){
		return offlineUser.get(key);
	}
	
	public Map<String, ArrayList<Integer>> getOfflineUser(){
		return offlineUser;
	}
	
	public int getArraySize(String userID){
		ArrayList<Integer> a = getList(userID);
		return a.size();
	}
	
	/*
	 * �ж��û��Ƿ�����
	 */
	public  boolean isOffline(String key){
		return offlineUser.containsKey(key);
	}
	
	/*
	 * ��ȡ�����û�������
	 */
	public int size(){
		return offlineUser.size();
	}
	
	/*
	 * ����ʱ��ӡofflineUser�б?�鿴�Ƿ����ݿ�����
	 */
	public void printOfflineUserCache(){
		Iterator iter = offlineUser.entrySet().iterator();
		int count =0;
		while(iter.hasNext()){
			count++;
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			System.out.print("User:"+key+"\t");
			System.out.println("messageID:"+val);
		}
		System.out.println("一共有"+count+"行记录");
	}
}
