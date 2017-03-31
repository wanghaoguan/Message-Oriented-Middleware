package com.ttqeducation.beans;

import java.text.ParseException;
import java.util.Date;

import com.ttqeducation.tools.DateUtil;

public class TeacherInfo {
	private String teacherID;		// 教师编号；
	private String teacherName;		// 老师姓名;
	private String chatInfo;		// 聊天概览;
	private String chatTime;		// 聊天时间；
	private int noReadCount;		//未读数；
	
	public TeacherInfo(){}
	
	public TeacherInfo(String teacherID, String teacherName, String chatInfo, String chatTime, int noReadCount){
		this.teacherID = teacherID;
		this.teacherName = teacherName;
		this.chatInfo = chatInfo.trim();
		this.chatTime = chatTime;
		this.noReadCount = noReadCount;
		
		if("".equals(this.chatInfo)){
			this.chatTime = "";
		}else {
			this.chatTime = chatTime.replace("T", " ");
			this.chatTime = this.chatTime.substring(0, this.chatTime.length()-6);
			try {
				Date date = DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", this.chatTime);
				this.chatTime = DateUtil.convertDateToString("yyyy年MM月dd日 HH:mm", date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
		
	public String getTeacherID() {
		return teacherID;
	}

	public void setTeacherID(String teacherID) {
		this.teacherID = teacherID;
	}

	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public String getChatInfo() {
		return chatInfo;
	}
	public void setChatInfo(String chatInfo) {
		this.chatInfo = chatInfo;
	}
	public String getChatTime() {
		return chatTime;
	}
	public void setChatTime(String chatTime) {
		this.chatTime = chatTime;
	}
	public int getNoReadCount() {
		return noReadCount;
	}
	public void setNoReadCount(int noReadCount) {
		this.noReadCount = noReadCount;
	}
	
}
