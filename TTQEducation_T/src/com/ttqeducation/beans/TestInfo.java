package com.ttqeducation.beans;

/**
 * 测试信息类：包括试卷使用记录ID和试卷名称；
 * @author 侯翔宇
 *
 */
public class TestInfo {

	private String useID ;			//试卷使用记录
	private String testName;			//试卷名称
	
	public TestInfo(){}
	
	public TestInfo(String testName){
		this.testName=testName;
	}
	
	public TestInfo(String useID,String testName){
		this.useID=useID;
		this.testName=testName;
	}
	
	public String getUseID(){
		return useID;
	}
	
	public void setUseID(String useID) {
		this.useID = useID;
	}
	
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	
}
