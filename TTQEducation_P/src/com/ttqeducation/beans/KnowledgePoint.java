package com.ttqeducation.beans;

public class KnowledgePoint {
	private int untilWeek; // 截止周；
	private float rightPercent; // 正确率；
	private int ranking; // 排名；

	public KnowledgePoint(int untilWeek, float rightPercent, int ranking) {
		this.untilWeek = untilWeek;
		this.rightPercent = rightPercent;
		this.ranking = ranking;
	}

	public int getUntilWeek() {
		return untilWeek;
	}

	public void setUntilWeek(int untilWeek) {
		this.untilWeek = untilWeek;
	}

	public float getRightPercent() {
		return rightPercent;
	}

	public void setRightPercent(float rightPercent) {
		this.rightPercent = rightPercent;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

}
