package com.ttqeducation.beans;

// 作业完成情况表；
public class TaskCompletion {
	private String taskName; // 作业名称；
	private String taskPage; // 作业页码；
	private int taskState; // 1 表示正确 2 表示错误 3 表示未答；

	// 错题汇总界面需要增加的变量；
	private String rightAnswer; // 正确答案；

	// 该构造方法是 家庭作业布置情况查看需要；
	public TaskCompletion(String taskName, String taskPage) {
		this.taskName = taskName;
		this.taskPage = taskPage;
	}

	// 该构造方法是 作业完成情况查看界面 的需要;
	public TaskCompletion(String taskName, String taskPage, int taskState) {
		this.taskName = taskName;
		this.taskPage = taskPage;
		this.taskState = taskState;
	}

	// 该构造方法是 错题汇总详情界面 的需要;
	public TaskCompletion(String taskName, String taskPage, String rightAnswer) {
		this.taskName = taskName;
		this.taskPage = taskPage;
		this.rightAnswer = rightAnswer;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskPage() {
		return taskPage;
	}

	public void setTaskPage(String taskPage) {
		this.taskPage = taskPage;
	}

	public int getTaskState() {
		return taskState;
	}

	public void setTaskState(int taskState) {
		this.taskState = taskState;
	}

	public String getRightAnswer() {
		return rightAnswer;
	}

	public void setRightAnswer(String rightAnswer) {
		this.rightAnswer = rightAnswer;
	}

}
