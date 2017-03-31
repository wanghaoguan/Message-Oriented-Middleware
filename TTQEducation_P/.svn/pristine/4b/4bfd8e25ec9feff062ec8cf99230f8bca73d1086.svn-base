package com.ttqeducation.beans;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * 记录当前用户存在的一些状态；
 * 单例模式
 * @author lvjie
 *
 */
public class UserCurrentState {
	
	// 以下两个参数是监听到中间件发送的聊天消息所需；(用来判断是直接更新界面，还是在状态栏显示)
	public int currentView = 0;			// 记录教师当前是在哪个界面(1:表示在一对一聊天界面, 2:表示班级群聊界面);
	public String chatingID = "";	// 保存当前聊天的那个家长的编号或者是当前聊天的那个班级编号；
	
	public int homeSchoolNew = 0;		// 解决消息界面 家校互动new的问题，为0表示没有new了；
	
	
	// 解决tokenID 需要；
	public String userID = "";		// 用户的可执行编号；
	public String userPwd = "";		// 用户密码；
	public String schoolWebServiceUrl = "";	// 学校服务地址；
	
	
	private static UserCurrentState instance = null;
	private UserCurrentState(){
		SharedPreferences pre = ContextApplication.getAppContext().getSharedPreferences("TTQAndroid", Activity.MODE_PRIVATE);
		this.schoolWebServiceUrl = pre.getString("school_service_url", "");
		this.userID = pre.getString("user_account", "");
		this.userPwd = pre.getString("user_pwd", "");
	}
	
	public static UserCurrentState getInstance(){
		if(instance == null){
			instance = new UserCurrentState();
		}
		return instance;
	}
	
}
