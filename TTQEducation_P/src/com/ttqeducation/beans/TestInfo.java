package com.ttqeducation.beans;

/*
 * 测试信息类：包括试卷使用记录ID和试卷名称；
 * 
 * 注意：  这个类在付费支付模块用到，因为PickerView的原因；
 */
public class TestInfo {

	private String useID;			// 试卷使用记录ID；
	private String testName;		// 试卷名称；
	private int validDays;			// 支付天数；
	
	public TestInfo(){}
	
	// 用于 显示第几周用；
	public TestInfo(String testName){
		this.testName = testName;
	}
	
	public TestInfo(String useID, String testName){
		this.useID = useID;
		this.testName = testName;
	}
	
	// 用户支付使用到的构造函数；
	public TestInfo(String useID, String testName, int validDays){
		this.useID = useID;
		this.testName = testName;
		this.validDays = validDays;
	}
	
	public String getUseID() {
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
	public int getValidDays() {
		return validDays;
	}
	public void setValidDays(int validDays) {
		this.validDays = validDays;
	}
	
	
	
}
