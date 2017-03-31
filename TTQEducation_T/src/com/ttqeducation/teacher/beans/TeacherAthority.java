package com.ttqeducation.teacher.beans;

/**
 * 用来存储教师的查看权限，可以查看各个班级的哪些科目
 * @author Dell
 *
 */
public class TeacherAthority {
	String classID;
	String[] subjectName;
	String grade;
	
	public TeacherAthority(String classID,String subjectNames,String grade){
		this.classID=classID;
		this.subjectName= subjectNames.split(";");
		this.grade=grade;
	}
}
