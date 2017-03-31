package com.ttqeducation.beans;

// 学生成绩排名界面展示需要；
public class TestScoreRanking {
	private String stuName;	 // 学生姓名;
	private String testName; // 测试名称;
	private String testTime; // 测试时间；
	private int ranking; // 成绩排名;
	private float score; // 获得分数;

	// 单元测试，期中，期末测试完成情况详情界面需要；
	public TestScoreRanking(int ranking,String stuName, float score) {
		// TODO Auto-generated constructor stub
		this.ranking = ranking;
		this.stuName = stuName;
		this.score = score;
	}
	
	// 学生成绩排名列表界面需要;
	public TestScoreRanking(String testName, String testTime, int ranking,
			float score) {
		this.testName = testName;
		this.testTime = testTime;
		this.ranking = ranking;
		this.score = score;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getTestTime() {
		return testTime;
	}

	public void setTestTime(String testTime) {
		this.testTime = testTime;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public String getStuName() {
		return stuName;
	}

	public void setStuName(String stuName) {
		this.stuName = stuName;
	}
	
	
}
