package com.ttqeducation.teacher.beans;

public class ParentInfo {
	
	private String parentID;
	private String parentName;
	private boolean hasNoReadMsg;
	private int noReadCount;	// 未读数量；
	private int parentImgID;	// 家长头像图片的标识；
	
	public ParentInfo() {
		super();
	}
	
//	public ParentInfo(String parentID, String parentName, boolean hasNoReadMsg) {
//		super();
//		this.parentID = parentID;
//		this.parentName = parentName;
//		this.hasNoReadMsg = hasNoReadMsg;
//	}
	
	public ParentInfo(String parentID, String parentName, int noReadCount) {
		super();
		this.parentID = parentID;
		this.parentName = parentName;
		this.noReadCount = noReadCount;
	}
	
		
	public int getParentImgID() {
		return parentImgID;
	}

	public void setParentImgID(int parentImgID) {
		this.parentImgID = parentImgID;
	}

	public int getNoReadCount() {
		return noReadCount;
	}

	public void setNoReadCount(int noReadCount) {
		this.noReadCount = noReadCount;
	}

	public String getParentID() {
		return parentID;
	}
	public void setParentID(String parentID) {
		this.parentID = parentID;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public boolean isHasNoReadMsg() {
		return hasNoReadMsg;
	}

	public void setHasNoReadMsg(boolean hasNoReadMsg) {
		this.hasNoReadMsg = hasNoReadMsg;
	}
	
	
	
}
