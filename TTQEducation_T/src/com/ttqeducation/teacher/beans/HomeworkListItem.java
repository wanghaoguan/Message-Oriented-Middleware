package com.ttqeducation.teacher.beans;

public class HomeworkListItem {

	private String testID;
	private String testName;
	private String createTime;
	private String typeBelongs;
	
	public String getTestID() {
		return testID;		}

	public void setTestID(String testID) {
		this.testID = testID;
	}
	
	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}		
	
	public String getTypeBelongs() {
		return typeBelongs;
	}

	public void setTypeBelongs(String typeBelongs) {
		this.typeBelongs = typeBelongs;
	}

	//构造函数
	public HomeworkListItem(String testID,String testName,String createTime,String typeBelongs){
		this.testID=testID;
		this.testName=testName;
		this.createTime=createTime;
		this.typeBelongs=typeBelongs;
	}
}
