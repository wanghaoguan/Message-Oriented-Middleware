package com.ttqeducation.beans;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.R;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DesUtil;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;


/**
 * 这个是用户类，保存家长的一些信息，采用单例模式
 * 
 * @author Administrator
 * 
 */
public class UserInfo {

	// 信息是否为空标志
	public Boolean if_has_info = false;

	// 信息成员变量
	public String parentName;
	public String studentID;
	public String childName;
	public String grade;
	public String classID;
	public String className;
	public String termID;
	public String termName;
	public String termType; // 学期类型，上学期还是下学期 0-上学期 1-下学期
	public int currentWeek;// 当前周
	public String deadline;
	
	public int deadLineDays;	// 用来保存离到期时间的天数；用于其他界面中显示；
	public int[] moduleUse = {1,1,1,1,1};		// 存放用户哪些模块可以使用；(默认都可以使用);
	public int noReadNoticeNum = 0;				// 实时保存未读消息的数量，用来解决new的问题；
	
	
	/**
	 * 2015.06.23
	 * 当模块都免费使用时，标识为1，默认都免费；其他界面提供 特别的 文字提示；
	 */
	public int flag = 1;						
	

	private static UserInfo instance = null;

	private UserInfo() {
		this.getUserInfo();
		this.getTermInfo();
		this.getCurrentWeek();
		this.getUserDeadline();
		
	}

	public static UserInfo getInstance() {
		if (UserInfo.instance != null) {
			return UserInfo.instance;
		} else {
			UserInfo.instance = new UserInfo();
			return UserInfo.instance;
		}

	}

	// 关闭单例，清除对象；
	public static void clearInstance() {
		if (UserInfo.instance != null) {
			UserInfo.instance = null;
		}
	}

	// 定义匿名内部线程类用于访问数据
	public Thread getCurrentWeekThread;

	/**
	 * 获取当前所属周
	 * 
	 */
	public void getCurrentWeek() {
		// 实例化
		getCurrentWeekThread = new Thread() {

			public void run() {
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				// 方法名
				String methodName = "Teach_GetWeek";
				
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("time", "2014-05-16");
				paramsMap.put("TokenID", tokenID);
				// 从本地获取帐号
				SharedPreferences pre = ContextApplication.getAppContext()
						.getSharedPreferences("TTQAndroid",
								Activity.MODE_PRIVATE);
				// 获取数据
				GetDataByWS getDataTool = GetDataByWS.getInstance();

				// 从本地获取学校URL
				String schoolURL = pre.getString("school_service_url", null);
				// 设置URL
				getDataTool.setURL(schoolURL);
				try {

					DataTable result = getDataTool.getDataAsTable(methodName, paramsMap);
					if(result != null){
						UserInfo.this.currentWeek = Integer.parseInt(result.getCell(0, "weekNum"));
					}else {
						Log.i("error", "UserInfo--->getCurrentWeek()..出错了...");
					}
					

				} catch (Exception e) {
					System.out.println("访问WS失败，可能是地址或参数错误,或网络没有连接"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		};

		getCurrentWeekThread.start();
		try {
			getCurrentWeekThread.join(); // 阻塞当前线程，等待thread执行完毕，可以带参数，最多等待多长时间
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 用于返回结果
	// public DataTable userInfo_table = null;

	// 定义匿名内部线程类用于访问数据
	public Thread getUserDataThread;

	/**
	 * 获得家长信息的方法
	 * 
	 * @param account
	 */
	public void getUserInfo() {
		// 实例化
		getUserDataThread = new Thread() {

			public void run() {
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				// 方法名
				String methodName = "pub_getParentNameAndItsChildInfoById";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// 从本地获取帐号
				SharedPreferences pre = ContextApplication.getAppContext().getSharedPreferences("TTQAndroid",
						Activity.MODE_PRIVATE);
				String account = pre.getString("user_account", "");

				if (account == "") {
					System.out.println("获取本地帐号失败！");
					return;
				}

				paramsMap.put("userID", account);
				paramsMap.put("TokenID", tokenID);
				// 获取数据
				GetDataByWS getDataTool = GetDataByWS.getInstance();

				// 从本地获取学校URL
				String schoolURL = pre.getString("school_service_url", null);
				// 设置URL
				getDataTool.setURL(schoolURL);
				try {
					DataTable result = getDataTool.getDataAsTable(methodName, paramsMap);
					if(result != null){
						UserInfo.this.parentName = result.getCell(0, "parentsName");
						UserInfo.this.studentID = result.getCell(0, "studentID");
						UserInfo.this.childName = result.getCell(0, "studentName");
						UserInfo.this.grade = result.getCell(0, "classGrade");
						UserInfo.this.classID = result.getCell(0, "classID");
						UserInfo.this.className = result.getCell(0, "className");
					}else {
						Log.i("error", "UserInfo--->getUserInfo()...出错了。。。");
					}
					
				} catch (Exception e) {
					System.out.println("访问WS失败，可能是地址或参数错误,或网络没有连接"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		};

		getUserDataThread.start();
		try {
			getUserDataThread.join(); // 阻塞当前线程，等待thread执行完毕，可以带参数，最多等待多长时间
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 定义匿名内部线程类用于访问数据
	public Thread getTermInfoThread;

	/**
	 * 获得家长信息的方法
	 * 
	 * @param account
	 */
	public void getTermInfo() {
		getTermInfoThread = new Thread() {

			public void run() {
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
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
				paramsMap.put("TokenID", tokenID);
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
						UserInfo.this.termID = result.getCell(0, "termID");
						UserInfo.this.termType = result.getCell(0, "term");
						UserInfo.this.termName = result.getCell(0, "termName");
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

	// 定义匿名内部线程类用于访问数据
	public Thread getUserDeadlineThread;

	/**
	 * 获得家长信息的方法
	 * 加密修改成功；
	 * @param account
	 */
	public void getUserDeadline() {
		getUserDeadlineThread = new Thread() {

			public void run() {
				DataTable dt = null;
				// 方法名
				String methodName = "APP_getUserDeadline";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String TokenID = DesUtil.getDesTokenID(UserCurrentState.getInstance().userID, "Admin203", 1);
				paramsMap.put("studentID", UserInfo.this.studentID);
				paramsMap.put("TokenID", TokenID);
				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 获取堂堂清公司的服务地址
				Resources res = ContextApplication.getAppContext()
						.getResources();
				String companyURL = (String) res
						.getText(R.string.company_service_url);
				if (companyURL == null) {// 如果没有值
					return;
				}
				getdatatool.setURL(companyURL);
				try {
					dt = getdatatool.getDataAsTable(methodName, paramsMap);
					Log.i("lvjie", "UserInfo-->getUserDeadline()-->执行成功...");
					if(dt != null){
						UserInfo.this.deadline = dt.getCell(0, "deadline").toString().split("T")[0];
					}else {
						Log.i("error", "UserInfo-->getUserDeadline()...出错了。。。");
					}
					
				}catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("lvjie", "UserInfo-->getUserDeadline()-->执行失败...");
					e.printStackTrace();
				}

			}
		};

		getUserDeadlineThread.start();
		try {
			getUserDeadlineThread.join(); // 阻塞当前线程，等待thread执行完毕，可以带参数，最多等待多长时间
			
			System.out.println("获取的用户过期时间："+UserInfo.this.deadline);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
