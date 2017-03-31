package com.ttqeducation.teacher.beans;

import java.text.ParseException;
import java.util.Date;

import com.ttqeducation.teacher.tools.DateUtil;

public class NoticeListItem {
		
//	private String noticeSource; // 消息发布源； 学校通知
//	private String noticeSummary;//消息发布概要：下面显示的一行文字
	
	private int noticeID;
	private String noticeTitle; 	// 消息的标题
	private String noticeContent;	// 消息的详细内容
	private String publisherName;	// 消息发布人
	private String publishTime; // 消息发布时间； 2015-01-28 15：30
	private boolean isRead;		// 是否已读；
	
	private int attachID;		// 附件编号；
	private String attachName;	// 附件名称；
	
	public NoticeListItem(){}

	//构造显示消息列表的函数
	public NoticeListItem(int noticeID, String noticeTitle, String noticeContent, String publishTime, boolean isRead) {
		this.noticeID = noticeID;
		this.noticeTitle = noticeTitle;
		this.noticeContent = noticeContent;
		this.isRead = isRead;
		
		this.publishTime = publishTime.replace("T", " ");
		this.publishTime = this.publishTime.substring(0, this.publishTime.length()-6);
		try {
			Date date = DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", this.publishTime);
			this.publishTime = DateUtil.convertDateToString("yyyy年MM月dd日 HH:mm", date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// 构造显示消息详细内容的函数(目前调用该构造函数)
	public NoticeListItem(String noticeTitle, String noticeContent, String publisherName,String publishTime){
		this.noticeTitle = noticeTitle;
		this.noticeContent = noticeContent;
		this.publisherName = publisherName;
		
		this.publishTime = publishTime;
		
	}
	
	// 构造显示消息详细内容的函数
	public NoticeListItem(String noticeTitle, String noticeContent, String publisherName,
			String publishTime, int attachID, String attachName) {
		this.noticeTitle = noticeTitle;
		this.noticeContent = noticeContent;
		this.publisherName = publisherName;
		this.publishTime = (publishTime.replace("T", " ")).split("+")[0];;
		this.attachID = attachID;
		this.attachName = attachName;
	}
	

	public boolean isRead() {
		return isRead;
	}
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	public int getNoticeID() {
		return noticeID;
	}
	public void setNoticeID(int noticeID) {
		this.noticeID = noticeID;
	}
	public String getNoticeTitle() {
		return noticeTitle;
	}
	public void setNoticeTitle(String noticeTitle) {
		this.noticeTitle = noticeTitle;
	}
	public String getNoticeContent() {
		return noticeContent;
	}
	public void setNoticeContent(String noticeContent) {
		this.noticeContent = noticeContent;
	}
	public String getPublisherName() {
		return publisherName;
	}
	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	public int getAttachID() {
		return attachID;
	}
	public void setAttachID(int attachID) {
		this.attachID = attachID;
	}
	public String getAttachName() {
		return attachName;
	}
	public void setAttachName(String attachName) {
		this.attachName = attachName;
	}
	
}
