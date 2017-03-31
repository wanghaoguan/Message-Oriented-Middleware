package com.ttqeducation.teacher.beans;

import java.text.ParseException;
import java.util.Date;

import com.ttqeducation.teacher.tools.DateUtil;


public class ChartInfo {
	private int chatID;	
	private String chartContent;
	private String chartTime;
	private boolean isMeSend;
	
	private String sendName;		// 发送信息人的姓名；  群聊需要该信息；
	private String senderID;			// 发送者的编号；
	
	public ChartInfo() {
		super();
	}
	
	/**
	 * 自己发送数据更新界面调用的构造函数；一对一聊天使用
	 * @param chartContent
	 * @param chartTime
	 * @param isMeSend
	 */
	public ChartInfo(String chartContent, String chartTime, boolean isMeSend){
		this.chartContent = chartContent;
		this.chartTime = chartTime;
		this.isMeSend = isMeSend;
	}

	/**
	 * 从服务端接收过来的数据，时间需要格式化，调用该构造函数；  一对一聊天使用
	 * @param chatID
	 * @param chartContent
	 * @param chartTime
	 * @param isMeSend
	 */
	public ChartInfo(int chatID, String chartContent, String chartTime, boolean isMeSend) {
		super();
		this.chatID = chatID;
		this.chartContent = chartContent;
		this.chartTime = chartTime;
		this.isMeSend = isMeSend;
		
		this.chartTime = chartTime.replace("T", " ");
		this.chartTime = this.chartTime.substring(0, this.chartTime.length()-6);
		try {
			Date date = DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", this.chartTime);
			this.chartTime = DateUtil.convertDateToString("yyyy年MM月dd日 HH:mm", date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 从服务端接收过来的数据，时间需要格式化，调用该构造函数；群聊 详情界面调用；
	 * @param chartContent
	 * @param chartTime
	 * @param isMeSend
	 * @param sender
	 */
	public ChartInfo(String chartContent, String chartTime, boolean isMeSend, String sender, String senderID){

		this.chartContent = chartContent;
		this.isMeSend = isMeSend;
		this.sendName = sender;
		this.senderID = senderID;
		
		this.chartTime = chartTime.replace("T", " ");
		this.chartTime = this.chartTime.substring(0, this.chartTime.length()-6);
		try {
			Date date = DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", this.chartTime);
			this.chartTime = DateUtil.convertDateToString("yyyy年MM月dd日 HH:mm", date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public String getSenderID() {
		return senderID;
	}

	public void setSenderID(String senderID) {
		this.senderID = senderID;
	}

	public String getSendName() {
		return sendName;
	}

	public void setSendName(String sendName) {
		this.sendName = sendName;
	}

	public int getChatID() {
		return chatID;
	}

	public void setChatID(int chatID) {
		this.chatID = chatID;
	}

	public String getChartContent() {
		return chartContent;
	}
	public void setChartContent(String chartContent) {
		this.chartContent = chartContent;
	}
	public String getChartTime() {
		return chartTime;
	}
	public void setChartTime(String chartTime) {
		this.chartTime = chartTime;
	}
	public boolean isMeSend() {
		return isMeSend;
	}
	public void setMeSend(boolean isMeSend) {
		this.isMeSend = isMeSend;
	}
	
	
}
