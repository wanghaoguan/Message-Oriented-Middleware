package com.ttqeducation.beans;

public class HomeworkArrageCondition {
	private String homeworkArrageDate; // 家庭作业布置时间； 2015-01-28
	private String homeworkArrageWeekNum;	// 日期对应的星期；星期一
	private String homeworkArrageDateDescrip; // 家庭作业布置时间的描述； 今天

	public HomeworkArrageCondition(String date, String week, String dateDescrip) {
		this.homeworkArrageDate = date;
		this.homeworkArrageWeekNum = week;
		this.homeworkArrageDateDescrip = dateDescrip;
	}

	public String getHomeworkArrageDate() {
		return homeworkArrageDate;
	}

	public void setHomeworkArrageDate(String homeworkArrageDate) {
		this.homeworkArrageDate = homeworkArrageDate;
	}

	public String getHomeworkArrageWeekNum() {
		return homeworkArrageWeekNum;
	}

	public void setHomeworkArrageWeekNum(String homeworkArrageWeekNum) {
		this.homeworkArrageWeekNum = homeworkArrageWeekNum;
	}

	public String getHomeworkArrageDateDescrip() {
		return homeworkArrageDateDescrip;
	}

	public void setHomeworkArrageDateDescrip(String homeworkArrageDateDescrip) {
		this.homeworkArrageDateDescrip = homeworkArrageDateDescrip;
	}

}
