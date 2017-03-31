package com.push.server.database;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

public class DBOperationImpl implements DBOperation{
	
	private static Logger log = Logger.getLogger(DBOperationImpl.class.getName());

	@Override
	public int insertOfflineUser(OfflineUser offlineUser) {
		SqlSession session = DBOperationUtil.getSqlSessionFactory()
				.openSession();
		int n = 0;
		try {
			n = session.insert("com.push.server.database.DBOperation.insertOfflineUser",offlineUser);
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("failed to insert offline user");
		}finally{
			session.close();
		}
		return n;
	}

	@Override
	public int insertMessage(Message message) {
		SqlSession session = DBOperationUtil.getSqlSessionFactory()
				.openSession();
		int n = -1;
		try {
			n = session.insert("com.push.server.database.DBOperation.insertMessage",message);
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("failed to insert message");
		}finally{
			session.close();
		}
		return n;
	}

	@Override
	public int deleteOfflineUser(String userID) {
		SqlSession session = DBOperationUtil.getSqlSessionFactory()
				.openSession();
		int n = -1;
		try {
			n = session.delete("com.push.server.database.DBOperation.deleteOfflineUser",userID);
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("failed to delete offline user");
		}finally{
			session.close();
		}
		return n;
	}

	@Override
	public int deleteMessage(Integer messageID) {
		SqlSession session = DBOperationUtil.getSqlSessionFactory()
				.openSession();
		int n = -1;
		try {
			n = session.delete("com.push.server.database.DBOperation.deleteMessage",messageID);
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("failed to delete message");
		}finally{
			session.close();
		}
		return n;
	}

	@Override
	public int selectMessageMaxID() {
		SqlSession session = DBOperationUtil.getSqlSessionFactory()
				.openSession();
		int n = -1;
		try {
			n = session.selectOne("com.push.server.database.DBOperation.selectMessageMaxID");
//			session.commit();
		} catch (Exception e) {
			n = 0;
			log.info("failed to select message max ID");
		}finally{
			session.close();
		}
		return n;
	}

	@Override
	public List<OfflineUser> selectAllOfflineUser() {
		SqlSession session = DBOperationUtil.getSqlSessionFactory()
				.openSession();
		List<OfflineUser> offlineUsers = new ArrayList<OfflineUser>();
		try {
			offlineUsers = session.selectList("com.push.server.database.DBOperation.selectAllOfflineUser");
//			session.commit();
		} catch (Exception e) {
//			e.printStackTrace();
			log.info("failed to select offline users or the table is null");
		}finally{
			session.close();
		}
		return offlineUsers;
	}

	@Override
	public List<Message> selectAllMessage() {
		SqlSession session = DBOperationUtil.getSqlSessionFactory()
				.openSession();
		List<Message> messages = new ArrayList<Message>();
		try {
			messages = session.selectList("com.push.server.database.DBOperation.selectAllMessage");
//			session.commit();
		} catch (Exception e) {
//			e.printStackTrace();
			log.info("failed to select messages or the table is null");
		}finally{
			session.close();
		}
		return messages;
	}

	@Override
	public int deleteOfflineuser(String userID, int messageID) {
		SqlSession session = DBOperationUtil.getSqlSessionFactory()
				.openSession();
		OfflineUser offlineUser = new OfflineUser();
		offlineUser.setUserID(userID);
		offlineUser.setMessageID(messageID);
		int n = -1;
		try {
			n = session.delete("com.push.server.database.DBOperation.deleteOfflineUser1",offlineUser);
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("failed to delete offline user");
		}finally{
			session.close();
		}
		return n;
	}
	
	public static void main(String[] args){
		Message m = new Message();
		m.setMessageID(2);
		m.setMessage("ddd");
		new DBOperationImpl().insertMessage(m);
	}

}
