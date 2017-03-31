package com.ttqeducation.teacher.beans;

/**
 * 该教师未读消息 相关信息；
 * @author lvjie
 *
 */
public class ClassUnReadMsgInfo {
	private String classID;				// 班级ID；
	private String className;			// 班级名称;
	private String unReadMsgNum;		// 未读消息数量;
	
	public ClassUnReadMsgInfo(){}
	
	public ClassUnReadMsgInfo(String classID, String className, String unReadMsgNum) {
		this.classID = classID;
		this.className = className;
		this.unReadMsgNum = unReadMsgNum;
	}
	
	public String getClassID() {
		return classID;
	}
	public void setClassID(String classID) {
		this.classID = classID;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getUnReadMsgNum() {
		return unReadMsgNum;
	}
	public void setUnReadMsgNum(String unReadMsgNum) {
		this.unReadMsgNum = unReadMsgNum;
	}

	@Override
	public String toString() {
		return "ClassUnReadMsgInfo [classID=" + classID + ", className="
				+ className + ", unReadMsgNum=" + unReadMsgNum + "]";
	}
	
	
}
