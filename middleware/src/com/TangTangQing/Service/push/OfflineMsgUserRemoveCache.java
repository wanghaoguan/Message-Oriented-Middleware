package com.TangTangQing.Service.push;

import java.util.ArrayList;
import java.util.TimerTask;

import com.TangTangQing.Service.GetService.GetTTQService;

public class OfflineMsgUserRemoveCache extends TimerTask {
	private static ArrayList offlineMsgUserRemove = new ArrayList<>() ;
	
	public synchronized void put(String userID,int messageID){
		String user_msg=userID.trim()+";"+Integer.toString(messageID);
		offlineMsgUserRemove.add(user_msg);
		System.out.println();
	}
	
	public synchronized void clearCache(){
		offlineMsgUserRemove.clear();
	}
	
	public synchronized void updateToDB(){
		String stringList="";
		for(int i=0;i<offlineMsgUserRemove.size();i++){
			stringList+=offlineMsgUserRemove.get(i).toString().trim()+"/";
		}
		
		int result= GetTTQService.getService().getMidServiceSoap().appPushNotificationReceiverUpdatePushState(stringList);
		System.out.println();
		
	}
	
	public void run(){
		String stringList="";
		for(int i=0;i<offlineMsgUserRemove.size();i++){
			stringList+=offlineMsgUserRemove.get(i).toString().trim()+"/";
		}
		if(offlineMsgUserRemove.size()>0){
			try{
				int result= GetTTQService.getService().getMidServiceSoap().appPushNotificationReceiverUpdatePushState(stringList);
				offlineMsgUserRemove.clear();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		
		System.out.println();
	}
}
