package com.ttqeducation.teacher.beans;

public class TaskCompletion {

	private String questionID;
	

	private String questionCode; //题目名称
	private String rightPercent; //正确率
	
	public TaskCompletion(String questionID,String questionCode,String rightPercent){
		
		this.questionID=questionID;
		this.questionCode=questionCode;
		this.rightPercent=rightPercent;		
	}
	
	public String getQuestionCode(){
		return questionCode;
	}
	
	public void setQuestionCode(String questionCode){
		this.questionCode=questionCode;
	}
	
	public String getRightPercent(){
		return rightPercent;
	}
	
	public void setRightPercent(String rightPercent){
		this.rightPercent=rightPercent;
	}
	
	public String getQuestionID() {
		return questionID;
	}

	public void setQuestionID(String questionID) {
		this.questionID = questionID;
	}
}
