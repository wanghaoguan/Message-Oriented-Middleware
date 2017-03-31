package com.push.server.database;

import java.util.List;

public interface DBOperation {
	
	public int insertOfflineUser(OfflineUser offlineUser);
	public int insertMessage(Message message);
	
	public int deleteOfflineUser(String userID);
	public int deleteOfflineuser(String userID, int messageID);
	public int deleteMessage(Integer messageID);
	public int selectMessageMaxID();
	
	public List<OfflineUser> selectAllOfflineUser();
	public List<Message> selectAllMessage();
	
}
