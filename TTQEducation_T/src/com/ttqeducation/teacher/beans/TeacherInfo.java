package com.ttqeducation.teacher.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.ttqeducation.teacher.beans.ContextApplication;
import com.ttqeducation.teacher.beans.DataTable;

import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.tools.DesUtil;


/**
 * 这个是用户类，保存教师的一些信息，采用单例模式
 * 
 * @author Administrator
 * 
 */
public class TeacherInfo {
	
	public String teacherID = "";		// 教师编号;
	public String execTeacherID = "";	// 开发人员  用来查询数据库使用的教师编号；
	public String teacherName = "";		// 教师姓名；
//	public String[] classIDs;			// 所教班级；	
	// 解决token需要；
	public String execTeacherPwd = "";	// 教师密码；
	
	//测试时期，先把班级写死
	public String currentClassID="";
	public String currentClassName="";	
	public String termID;
	public String termType;
	public String termName;
	public String[] classes=null;			//教师所教所有班级列表
	public String[] subjects=null;			//教师所教班级对应科目


	//暂时把年级写死
	public String grade="0";
	Map<String, TeacherAthority> teacherAthority = null; //存储教师权限，可以查看哪些科目

	public Map<String, TeacherAthority> getTeacherAthority() {
		return teacherAthority;
	}

	public void setTeacherAthority(Map<String, TeacherAthority> teacherAthority) {
		this.teacherAthority = new HashMap<String, TeacherAthority>();
		this.teacherAthority = teacherAthority;
	}



	public int noReadNoticeNum = 0;		// 未读通知公告数；
	
	private int[] personImgID = {R.drawable.teacher1, R.drawable.teacher2, 
			R.drawable.teacher3, R.drawable.teacher4, R.drawable.teacher5, R.drawable.teacher6};
	
	// 以下两个参数是监听到中间件发送的聊天消息所需；(用来判断是直接更新界面，还是在状态栏显示)
	/**
	 * 记录教师当前是在哪个界面
	 * 1:表示在一对一聊天界面,  2:表示班级群聊界面; 
	 */
	public int currentView = 0;
	/**
	 * 保存当前聊天的那个家长的编号或者是当前聊天的那个班级编号；
	 */
	public String chatingID = "";
	
	// 班级群聊所需，key为教师编号， value为对应的头像ID；
	public HashMap<String, Integer> hsTeacherPic = new HashMap<String, Integer>();
	
	/**
	 * 1:表示系统处于打开状态；  2:表示系统是通过通知栏进入到某一个界面的(此状态下当按返回按钮，需要进入到主界面)；
	 */
	public int initSystem = 0;
	
	
	// 存放教师所教班级未读通知数量相关信息；
	public Map<String, ClassUnReadMsgInfo> classUnReadMsgInfos = new HashMap<String, ClassUnReadMsgInfo>();
	
	private static TeacherInfo instance = null;
	
	private TeacherInfo(){
		
		this.getTermInfo();
	}
	
	public static TeacherInfo getInstance(){
		if(TeacherInfo.instance != null){
			return TeacherInfo.instance;
		}else {
			TeacherInfo.instance = new TeacherInfo();
			return TeacherInfo.instance;
		}
	}
	
	public void clearInstance(){
		TeacherInfo.instance = null;
	}
	
	/**
	 * 通过用户编号，获取对应的头像；
	 * @param personID
	 * @return
	 */
	public int getPersonPicID(String personID){
		if(hsTeacherPic.containsKey(personID)){
			return hsTeacherPic.get(personID);
		}else {
			Random random = new Random();
			int k = random.nextInt(6);
			hsTeacherPic.put(personID, personImgID[k]);
			return personImgID[k];
		}
	}
	
	
	
	// 定义匿名内部线程类用于访问数据
	public Thread getTermInfoThread;
	/**
	 * 获得教师信息的方法
	 * 
	 * @param account
	 */
	public void getTermInfo() {
		getTermInfoThread = new Thread() {

			public void run() {
				
				//DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				// 方法名
				String methodName = "Teach_GetCurrentTerm";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("currentTime", "2015-02-03"); // 时间随便传（假参数）
				/*paramsMap.put("TokenID", tokenID);*/
				// 获取数据
				GetDataByWS getDataTool = GetDataByWS.getInstance();

				// 从本地获取学校URL
				SharedPreferences pre = ContextApplication.getAppContext()
						.getSharedPreferences("TTQAndroid",
								Activity.MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				// 设置URL
				getDataTool.setURL(schoolURL);
				try {
					DataTable result = getDataTool.getDataAsTable(methodName, paramsMap);
					if(result != null){
						TeacherInfo.this.termID = result.getCell(0, "termID");
						TeacherInfo.this.termType = result.getCell(0, "term");
						TeacherInfo.this.termName = result.getCell(0, "termName");
						Log.i("hxy", TeacherInfo.this.termID);
					}else {
						Log.i("error", "UserInfo--->getTermInfo()...出错了。。。");
					}
					
				} catch (Exception e) {
					System.out.println("访问WS失败，可能是地址或参数错误,或网络没有连接"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		};

		getTermInfoThread.start();
		try {
			getTermInfoThread.join(); // 阻塞当前线程，等待thread执行完毕，可以带参数，最多等待多长时间
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public String getTermID(){
		
		return this.termID;
	}
	
	public String getClassID(){
		
		return this.currentClassID;
	}
	
	public void setClassID(String classID){
		
		this.currentClassID=classID;
	}
	
	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}
	
	public String getCurrentClassName() {
		return currentClassName;
	}

	public void setCurrentClassName(String currentClassName) {
		this.currentClassName = currentClassName;
	}
	
	public String[] getClasses() {
		return classes;
	}

	public void setClasses(String[] classes) {
		this.classes = classes;
	}
	
	public String[] getSubjects() {
		return subjects;
	}

	public void setSubjects(String[] subjects) {
		this.subjects = subjects;
	}
	
	/**
	 * 载入选择班级的数据,设置班级名称  ID  年级  对应的科目
	 * @param className
	 */
	public void setChoosedClass(String className){
		setCurrentClassName(className);
		setClassID(getTeacherAthority().get(className).classID);
		setGrade(getTeacherAthority().get(className).grade);
		setSubjects(getTeacherAthority().get(className).subjectName);
	}
	
}
