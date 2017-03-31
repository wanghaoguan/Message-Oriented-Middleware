package com.ttqeducation.teacher.beans;

// 班级信息；
public class ClassInfo {
	private String classID;		// 班级编号；
	private String className;	// 班级名称；
	private int studentCount;	// 学生人数；
	private boolean isShowNew;	// 是否显示New图片；
	
	public ClassInfo() {
		super();
	}

	public ClassInfo(String classID, String className, int studentCount) {
		super();
		this.classID = classID;
		this.className = className;
		this.studentCount = studentCount;
	}
	
	
	
	public boolean isShowNew() {
		return isShowNew;
	}

	public void setShowNew(boolean isShowNew) {
		this.isShowNew = isShowNew;
	}

	public int getStudentCount() {
		return studentCount;
	}

	public void setStudentCount(int studentCount) {
		this.studentCount = studentCount;
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
	
	
	
}
