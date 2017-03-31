package com.ttqeducation.teacher.activitys.system;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.beans.ClassUnReadMsgInfo;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.TeacherInfo;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.tools.DateUtil;
import com.ttqeducation.teacher.tools.DensityUtils;
import com.ttqeducation.teacher.tools.DesUtil;
import com.ttqeducation.teacher.tools.GeneralTools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class LaunchActivity extends Activity {
	
	public static final int GOTO_SCHOOLCHOOSE_VIEW = 1;	// 第一次登陆，进入学校选择界面；
	public static final int GOTO_LOGIN_VIEW = 2;		// 很久没有进入系统了， 时间大于30天，直接进入登陆界面；
	public static final int GOTO_MAIN_VIEW = 3;			// 之前登陆过了， 再次直接进入主界面；
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		
		check_FirstUse_Expiration();
	}
	
	
	private Handler myHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LaunchActivity.GOTO_SCHOOLCHOOSE_VIEW:		// 第一次启动，进入到学校选择界面；
				Intent chooseSchoolIntent = new Intent(LaunchActivity.this, ChooseSchoolActivity.class);
				startActivity(chooseSchoolIntent);
				LaunchActivity.this.finish();
				break;
			case LaunchActivity.GOTO_LOGIN_VIEW:			// 很久没有进入系统了， 时间大于30天，直接进入登陆界面；
				Intent loginIntent = new Intent(LaunchActivity.this, LoginActivity.class);
				startActivity(loginIntent);
				LaunchActivity.this.finish();
				break;
			case LaunchActivity.GOTO_MAIN_VIEW:				// 之前登陆过了， 再次直接进入主界面；
				setTeachInfoToInstance();	// 向单例模式中加入教师相关信息；
				getTeacherUnreadMesgByWS(TeacherInfo.getInstance().execTeacherID);
				break;

			default:
				break;
			}
		};
	};
	
	public void check_FirstUse_Expiration(){
		SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
		boolean isLoginSucceed = pre.getBoolean("isLoginSucceed", false);
		if(isLoginSucceed){			// 上次是成功登陆的
			
			Date startDate;
			int differDays = 200;
			try {
				startDate = DateUtil.convertStringToDate("yyyy-MM-dd", pre.getString("loginTime", "2014-01-01"));
				differDays = DateUtil.daysBetween(startDate, new Date());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(differDays >= 30){		// 很久没有进入系统了，跳到登陆界面；
				myHandler.sendEmptyMessageDelayed(LaunchActivity.GOTO_LOGIN_VIEW, 1000);
			}else {		// 进入主界面；
				myHandler.sendEmptyMessageDelayed(LaunchActivity.GOTO_MAIN_VIEW, 1000);
			}
			
		}else {		// 上次没有登陆成功；
			// 延迟两秒，进入学校选择界面；
			myHandler.sendEmptyMessageDelayed(LaunchActivity.GOTO_SCHOOLCHOOSE_VIEW, 1000);
		}
				
	}
	
	// 读取本地xml文件数据，赋值给单例模式中的教师信息；
	public void setTeachInfoToInstance(){
		SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
//		TeacherInfo.getInstance().classIDs = pre.getString("classIDs", "").split(",");
		TeacherInfo.getInstance().teacherID = pre.getString("teacherID", "");
		TeacherInfo.getInstance().execTeacherID = pre.getString("execTeacherID", "");
		TeacherInfo.getInstance().teacherName = pre.getString("teacherName", "");
		TeacherInfo.getInstance().execTeacherPwd = pre.getString("teacherPwd", "");
	}
	
	// 从服务端获取教师所教班级未读等相关信息；
	public void getTeacherUnreadMesgByWS(String teacherID){
		new AsyncTask<Object, Object, DataTable>(){
			
			@Override
			protected void onPreExecute() {
				
			};
			
			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				
				DataTable dt = null;

				String teacherID = params[0].toString();
				Log.i("lvjie", "launch-->teacherID"+teacherID);
				// 方法名
				String methodName = "APP_teacherUnreadMesg_byClass";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("teacherID", teacherID);
				paramsMap.put("TokenID", tokenID);
				Log.i("lvjie", "参数为： teacherID="+teacherID+"  tokenID="+tokenID);
				// 获取数据
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				// 从本地获取学校URL
				SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);				
				String schoolURL = pre.getString("school_service_url", null);
				getDataTool.setURL(schoolURL);
				try {
					dt = getDataTool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					Log.i("error", "访问WS失败，可能是地址或参数错误,或网络没有连接"+e.getMessage());
					e.printStackTrace();
				}
				return dt;
			}
			
			@Override
			protected void onPostExecute(DataTable result) {
				if(result != null){
					int rowCount = result.getRowCount();
					for(int i=0; i<rowCount; i++){
						try {
							String classID = result.getCell(i, "classID").trim();
							String className = result.getCell(i, "className").trim();
							String unReadMsgNum = result.getCell(i, "unRead").trim();
							ClassUnReadMsgInfo classUnReadMsgInfo = new ClassUnReadMsgInfo(classID, className, unReadMsgNum);
							TeacherInfo.getInstance().classUnReadMsgInfos.put(classID, classUnReadMsgInfo);
							Log.i("lvjie", "LaunchActivity-->getTeacherUnreadMesgByWS()..."+classUnReadMsgInfo);
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}else {
					Log.i("lvjie", "LaunchActivity-->getTeacherUnreadMesgByWS()...从服务端获取班级未读消息失败...");
				}
				
				// 进入主界面
				Intent mainIntent = new Intent(LaunchActivity.this, MainActivity.class);
				startActivity(mainIntent);
				LaunchActivity.this.finish();
				
			};
			
		}.execute(teacherID);
	}

	
}
