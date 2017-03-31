package com.ttqeducation.activitys.study;

/**
 * 吕杰
 * 此文件对应的界面为主界面中 动态学习 的部分，已经完成界面的初始化，及动态适应不同分辨率的手机，给对应按钮增加点击事件；
 * 此界面的任务基本完成，增加新的需求需要再添加，例如new的问题；
 */

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ttqeducation.R;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.UserCurrentState;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DensityUtils;
import com.ttqeducation.tools.DesUtil;

public class StudyFragment extends Fragment {

	private ImageView ivWelcomePicture = null; // 上方欢迎图片
	// 五个格子，用于点击事件；
	private FrameLayout homeworkResultLayout = null; // 作业结果查看
	private FrameLayout homeworkArrangementLayout = null; // 家庭作业布置情况
	private FrameLayout knowledgeSituationLayout = null; // 知识点掌握情况
	private FrameLayout phychologicalTestResultLayout = null; // 心理测试结果查看
	private FrameLayout studySituationLayout = null; // 学习情况分析定制情况

	// 五个格子中每个格子对应的图片；
	private ImageView homeworkResultImg = null; // 作业结果查看
	private ImageView homeworkArrangementImg = null; // 家庭作业布置情况
	private ImageView knowledgeSituationImg = null; // 知识点掌握情况
	private ImageView phychologicalTestResultImg = null; // 心理测试结果查看
	private ImageView studySituationImg = null; // 学习情况分析定制情况
	
	private Dialog reminderUseDialog = null;	// 提醒用户缴费使用功能模块对话框；
	private Dialog reminderDialog = null;	// 陈总提出的，以后不需要；
	private Dialog reminderDialog1 = null;	// 陈总提出的，以后不需要；
	private Dialog updateAppDialog = null;	// 版本更新对话框；
	
	private TextView welcomeTextView = null;	// 欢迎家长部分文字；
	
	private int[] moduleUse = {1,1,1,1,1}; // 存放用户哪些模块可以使用；(默认都可以使用);
	
	private String downLoadAppUrl = "";			// 下载app的url;
	
	//付费模块
	public static boolean schoolFlag = false;
	public static boolean userFlag = false;
	public static String chargeModuleString = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.study_layout1, container, false);
		
		getAppVersionByWS();		// 有需要就进行版本更新；
		
		Log.i("lvjie", "StudyFragment-->onCreateView()...");
		initView(layout);
		
		return layout;
	}

	
	
	public void initView(View view) {
		// 欢迎家长
		this.welcomeTextView = (TextView) view.findViewById(R.id.welcome_textView);
		this.welcomeTextView.setText(UserInfo.getInstance().childName.trim()+"家长，欢迎您！");
		
		// 上方欢迎图片
		this.ivWelcomePicture = (ImageView) view.findViewById(R.id.ivWelcomePicture);
		
		// 初始化五个格子;
		this.homeworkResultLayout = (FrameLayout) view
				.findViewById(R.id.homework_result_layout);
		this.homeworkArrangementLayout = (FrameLayout) view
				.findViewById(R.id.homework_arrangement_layout);
		this.knowledgeSituationLayout = (FrameLayout) view
				.findViewById(R.id.knowledge_situation_layout);
		this.phychologicalTestResultLayout = (FrameLayout) view
				.findViewById(R.id.phychological_test_result_layout);
		this.studySituationLayout = (FrameLayout) view
				.findViewById(R.id.study_situation_layout);

		// 对每个格子添加点击事件；
		this.homeworkResultLayout.setOnClickListener(myClickListener);
		this.homeworkArrangementLayout.setOnClickListener(myClickListener);
		this.knowledgeSituationLayout.setOnClickListener(myClickListener);
		this.phychologicalTestResultLayout.setOnClickListener(myClickListener);
		this.studySituationLayout.setOnClickListener(myClickListener);

		// 初始化五个格子中的图片；
		this.homeworkResultImg = (ImageView) view
				.findViewById(R.id.homework_result_img);
		this.homeworkArrangementImg = (ImageView) view
				.findViewById(R.id.homework_arrangement_img);
		this.knowledgeSituationImg = (ImageView) view
				.findViewById(R.id.knowledge_situation_img);
		this.phychologicalTestResultImg = (ImageView) view
				.findViewById(R.id.phychological_test_result_img);
		this.studySituationImg = (ImageView) view
				.findViewById(R.id.study_situation_img);
		
		// 获取屏幕宽度
		DisplayMetrics metrics = getResources().getDisplayMetrics(); // 用来获取屏幕的分辨率；
		int screenWidthPX = metrics.widthPixels;
		float screenWidthDP = DensityUtils.px2dp(getActivity(), screenWidthPX);
		// 缩放比例
		float widthWithDP = screenWidthDP - ((37 * 2 + 24) / 3);
		float widthWithPX = DensityUtils.dp2px(getActivity(), widthWithDP);
		float widthScale = widthWithPX / 982; // 缩放比例
		
		// 上方欢迎图片
		LayoutParams ivWelcomePictureParams = (LayoutParams) ivWelcomePicture.getLayoutParams();
		ivWelcomePictureParams.width = screenWidthPX;
		ivWelcomePictureParams.height = (int)((screenWidthPX / (float)1080) * 563);
		ivWelcomePicture.setLayoutParams(ivWelcomePictureParams);
		
		// 作业结果查看
		LayoutParams homeworkResultParams = (LayoutParams) homeworkResultLayout.getLayoutParams();
		homeworkResultParams.width = (int)(widthScale * 649);
		homeworkResultParams.height = (int)(widthScale * 484);
		homeworkResultLayout.setLayoutParams(homeworkResultParams);
		
		// 家庭作业布置情况
		LayoutParams homeworkArrangementParams = (LayoutParams) homeworkArrangementLayout.getLayoutParams();
		homeworkArrangementParams.width = (int)(widthScale * 491);
		homeworkArrangementParams.height = (int)(widthScale * 272);
		homeworkArrangementLayout.setLayoutParams(homeworkArrangementParams);
		
		// 知识点查看
		LayoutParams knowledgeSituationParams = (LayoutParams) knowledgeSituationLayout.getLayoutParams();
		knowledgeSituationParams.width = (int)(widthScale * 333);
		knowledgeSituationParams.height = (int)(widthScale * 230);
		knowledgeSituationLayout.setLayoutParams(knowledgeSituationParams);
		
		// 心理测试结果查看
		LayoutParams phychologicalTestResultParams = (LayoutParams) phychologicalTestResultLayout.getLayoutParams();
		phychologicalTestResultParams.width = (int)(widthScale * 491);
		phychologicalTestResultParams.height = (int)(widthScale * 272);
		phychologicalTestResultLayout.setLayoutParams(phychologicalTestResultParams);
		
		// 分析报告订购
		LayoutParams studySituationParams = (LayoutParams) studySituationLayout.getLayoutParams();
		studySituationParams.width = (int)(widthScale * 333);
		studySituationParams.height = (int)(widthScale * 230);
		studySituationLayout.setLayoutParams(studySituationParams);
		
		// 设置收费模块与是否可用的界面显示；		
		setModuleShowByModuleUse();
	
	}
	
	/**
	 * @author Tuimao
	 * 检测学校是否能用权限模块
	 */
	public void checkSchoolJurisdiction(){
		SharedPreferences pre = getActivity().getSharedPreferences("TTQAndroid", getActivity().getApplicationContext().MODE_PRIVATE);
		String userID = pre.getString("user_account", null);
		String schoolCode = pre.getString("schoolCode", null);
		new AsyncTask<Object, Object, DataTable>(){

			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DataTable dt = null;
				String methodName = "APP_getModuleFlag";
				
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = DesUtil.getDesTokenID(params[0].toString(), "Admin203", 1);
				paramsMap.put("schoolCode", params[1].toString());
				paramsMap.put("TokenID", tokenID);
				
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				getDataTool.setURL((String) getResources().getText(R.string.company_service_url));
				try {
					dt = getDataTool.getDataAsTable(methodName, paramsMap);
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return dt;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				if(result != null){
					try {
						if("true".equals(result.getCell(0, "freeUse"))){
							StudyFragment.schoolFlag = true;
						}else{
							StudyFragment.schoolFlag = false;
						}
						StudyFragment.chargeModuleString = result.getCell(0, "chargeModules");
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				super.onPostExecute(result);
			}
			
		}.execute(userID, schoolCode);
	}
	/**
	 * @author Tuimao
	 * 检测用户个人能否使用权限模块
	 */
	public void checkUserJurisdiction(){
		SharedPreferences pre = getActivity().getSharedPreferences("TTQAndroid", getActivity().getApplicationContext().MODE_PRIVATE);
		String userID = pre.getString("user_account", null);
		String schoolURL = pre.getString("school_service_url", null);
		//Log.i("shen", "--->userID " + userID);
		//String tokenID = DesUtil.getDesTokenID(userID, "Admin407", 2);
		new AsyncTask<Object, Object, DataTable>(){

			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DataTable dt = null;
				//获取tokenID必须要到AsyncTask里面？？？？？？
				DesUtil.addTokenIDToSchoolWS();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//Log.i("shen", "---tokenID " + tokenID);
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("studentID", params[0].toString());
				paramsMap.put("TokenID", tokenID);
				String methodName = "pub_student_getChargeModuleState";
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				getDataTool.setURL(params[1].toString());
				try {
					dt = getDataTool.getDataAsTable(methodName, paramsMap);
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return dt;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				String isApplyed = null;
				String isFree = null;
				String deadline = null;
				String startTime = null;
				String endTime = null;
				if(result != null){
					try {
						isFree = result.getCell(0, "isFree");
						deadline = result.getCell(0, "deadline");
						startTime = result.getCell(0, "startTime");
						endTime = result.getCell(0, "endTime");
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else	System.out.println("---" + "result is null");
				String getDeadline = deadline.replace('T', ' ').substring(0, 19);
				SharedPreferences pre = getActivity().getSharedPreferences("TTQAndroid", getActivity().getApplicationContext().MODE_PRIVATE);
				SharedPreferences.Editor pre_editor = pre.edit();
				pre_editor.putString("getUserDeadline", getDeadline);
				pre_editor.commit();
				String getStartTime = startTime.replace('T', ' ').substring(0, 19);
				String getEndTime = startTime.replace('T', ' ').substring(0, 19);
				Date currentDate = new Date();
				SimpleDateFormat fomatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String currentDateStr = fomatter.format(currentDate);
				if("true".equals(isFree)){
					if("true".equals(isFree)){
						//时间为null或者时间为""，都能判断成功
						if(currentDateStr.compareTo(getStartTime)>0&&getEndTime.compareTo(currentDateStr)>0){
							StudyFragment.userFlag = true;
						}else{
							StudyFragment.userFlag = false;
						}
					}
					else{
						if(getDeadline.compareTo(currentDateStr)>0){
							StudyFragment.userFlag = true;
						}else{
							StudyFragment.userFlag = false;
						}
					}
				}
				super.onPostExecute(result);
			}
			
		}.execute(userID, schoolURL);
	}
	
	private View.OnClickListener myClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			SharedPreferences pre = getActivity().getSharedPreferences("TTQAndroid", getActivity().getApplicationContext().MODE_PRIVATE);
			String userID = pre.getString("user_account", null);
			
			int viewID = view.getId();
			
			switch (viewID) {
			case R.id.homework_result_layout:			// 作业结果查看；
				if(schoolFlag||userFlag){
					Intent taskTypeIntent = new Intent(getActivity(),
							TaskTypeChooseActivity.class);
					startActivity(taskTypeIntent);
				}else{
					new AsyncTask<Object, Object, DataTable>(){

						@Override
						protected DataTable doInBackground(Object... params) {
							// TODO Auto-generated method stub
							DataTable dt = null;
							
							String tokenID = DesUtil.getDesTokenID(params[0].toString(), "Admin203", 1);
							Map<String, String> paramsMap = new HashMap<String, String>();
							paramsMap.put("moduleName", params[1].toString());
							paramsMap.put("TokenID", tokenID);
							
							String methodName = "Base_chargeModule_getModuleID";
							GetDataByWS getDataTool = GetDataByWS.getInstance();
							getDataTool.setURL((String) getResources().getText(R.string.company_service_url));
							try {
								dt = getDataTool.getDataAsTable(methodName, paramsMap);
							} catch (dataTableWrongException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (XmlPullParserException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return dt;
						}

						@Override
						protected void onPostExecute(DataTable result) {
							// TODO Auto-generated method stub
							String moduleID = null;
							if(result != null){
								try {
									moduleID = result.getCell(0, "ModuleID");
								} catch (dataTableWrongException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if(chargeModuleString.contains(moduleID)){
									initReminderUseDialog();
								}else{
									Intent taskTypeIntent = new Intent(getActivity(),
											TaskTypeChooseActivity.class);
									startActivity(taskTypeIntent);
								}
							}else{
								Log.i("Tuimao", "查询结果为空");
							}
							super.onPostExecute(result);
						}
						
					}.execute(userID, "作业结果查看");
				}
//				if(moduleUse[0] == 0){
//					initReminderUseDialog();
//				}else {
//					Intent taskTypeIntent = new Intent(getActivity(),
//							TaskTypeChooseActivity.class);
//					startActivity(taskTypeIntent);
//				}
				break;
				
			case R.id.homework_arrangement_layout:		// 家庭作业布置情况；
				if(schoolFlag||userFlag){
					Intent homeworkIntent = new Intent(getActivity(),
							HomeworkArrangementActivity.class);
					homeworkIntent.putExtra("titleStr", "家庭作业布置情况");
					startActivity(homeworkIntent);
				}else{
					new AsyncTask<Object, Object, DataTable>(){

						@Override
						protected DataTable doInBackground(Object... params) {
							// TODO Auto-generated method stub
							DataTable dt = null;
							
							String tokenID = DesUtil.getDesTokenID(params[0].toString(), "Admin203", 1);
							Map<String, String> paramsMap = new HashMap<String, String>();
							paramsMap.put("moduleName", params[1].toString());
							paramsMap.put("TokenID", tokenID);
							
							String methodName = "Base_chargeModule_getModuleID";
							GetDataByWS getDataTool = GetDataByWS.getInstance();
							getDataTool.setURL((String) getResources().getText(R.string.company_service_url));
							try {
								dt = getDataTool.getDataAsTable(methodName, paramsMap);
							} catch (dataTableWrongException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (XmlPullParserException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return dt;
						}

						@Override
						protected void onPostExecute(DataTable result) {
							// TODO Auto-generated method stub
							String moduleID = null;
							if(result != null){
								try {
									moduleID = result.getCell(0, "ModuleID");
								} catch (dataTableWrongException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if(chargeModuleString.contains(moduleID)){
									initReminderUseDialog();
								}else{
									Intent homeworkIntent = new Intent(getActivity(),
											HomeworkArrangementActivity.class);
									homeworkIntent.putExtra("titleStr", "家庭作业布置情况");
									startActivity(homeworkIntent);
								}
							}else{
								Log.i("Tuimao", "查询结果为空");
							}
							super.onPostExecute(result);
						}
						
					}.execute(userID, "家庭作业布置情况");
				}
//				if(moduleUse[1] == 0){
//					initReminderUseDialog();
//				}else {
//					Intent homeworkIntent = new Intent(getActivity(),
//							HomeworkArrangementActivity.class);
//					homeworkIntent.putExtra("titleStr", "家庭作业布置情况");
//					startActivity(homeworkIntent);
//				}				
				break;
					
			case R.id.knowledge_situation_layout:		// 知识点掌握情况；
				if(schoolFlag||userFlag){
					Intent knowledgePointConditionIntent = new Intent(
							getActivity(), KnowledgePointActivity.class);
					startActivity(knowledgePointConditionIntent);
				}else{
					new AsyncTask<Object, Object, DataTable>(){

						@Override
						protected DataTable doInBackground(Object... params) {
							// TODO Auto-generated method stub
							DataTable dt = null;
							
							String tokenID = DesUtil.getDesTokenID(params[0].toString(), "Admin203", 1);
							Map<String, String> paramsMap = new HashMap<String, String>();
							paramsMap.put("moduleName", params[1].toString());
							paramsMap.put("TokenID", tokenID);
							
							String methodName = "Base_chargeModule_getModuleID";
							GetDataByWS getDataTool = GetDataByWS.getInstance();
							getDataTool.setURL((String) getResources().getText(R.string.company_service_url));
							try {
								dt = getDataTool.getDataAsTable(methodName, paramsMap);
							} catch (dataTableWrongException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (XmlPullParserException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return dt;
						}

						@Override
						protected void onPostExecute(DataTable result) {
							// TODO Auto-generated method stub
							String moduleID = null;
							if(result != null){
								try {
									moduleID = result.getCell(0, "ModuleID");
								} catch (dataTableWrongException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if(chargeModuleString.contains(moduleID)){
									initReminderUseDialog();
								}else{
									Intent knowledgePointConditionIntent = new Intent(
											getActivity(), KnowledgePointActivity.class);
									startActivity(knowledgePointConditionIntent);
								}
							}else{
								Log.i("Tuimao", "查询结果为空");
							}
							super.onPostExecute(result);
						}
						
					}.execute(userID, "作业结果查看");
				}
//				if(moduleUse[2] == 0){
//					initReminderUseDialog();
//				}else {
//					Intent knowledgePointConditionIntent = new Intent(
//							getActivity(), KnowledgePointActivity.class);
//					startActivity(knowledgePointConditionIntent);
//				}			
				break;
			
			case R.id.phychological_test_result_layout:
				initReminderDialog1("提示","        该项目正在研发，请期待！","我知道了");
				break;

			case R.id.study_situation_layout:
				initReminderDialog("提示","        该项目需数据积累，请期待！","我知道了");
//				initUpdateAppDialog("当前版本：v1.0.0", "更新版本：v1.2.0");
//				if(moduleUse[5] == 0){
//					initReminderUseDialog();
//				}else {
//					showToast("学习情况分析报告定制");
//				}
				break;

			default:
				break;
			}
		}

	};
	
	// 初始化提示对话框，用户没有进行缴费，则无法进入对应模块；
	public void initReminderUseDialog(){
		if (this.reminderUseDialog == null) {
			View view = LayoutInflater.from(getActivity()).inflate(
					R.layout.dialog_reminder_authority_use, null);
			this.reminderUseDialog = new Dialog(getActivity(),
					R.style.alertdialog_style);
			this.reminderUseDialog.setContentView(view);
			this.reminderUseDialog.show();

			// 动态设置界面图片大小；
			DisplayMetrics metrics = getResources().getDisplayMetrics(); // 用来获取屏幕的分辨率；
			int screenWidthPX = metrics.widthPixels;
			int screenWidthDP = (int) DensityUtils.px2dp(getActivity(),
					screenWidthPX);

			// 设置图片的大小；
			int layoutWidthDP = (screenWidthDP * 4) / 5;
			int layoutWidthPX = DensityUtils
					.dp2px(getActivity(), layoutWidthDP);

			LayoutParams params = view.getLayoutParams();
			params.width = layoutWidthPX;
			view.setLayoutParams(params);

			// 添加点击事件
			view.findViewById(R.id.IKnow_textView).setOnClickListener(
				new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						reminderUseDialog.dismiss();
					}						
				});		

			} else {
				this.reminderUseDialog.show();
		}
	}

	// 陈总提出的新需求，以后不需要；
	public void initReminderDialog(String title, String content, String ikonw){
		if (this.reminderDialog == null) {
			View view = LayoutInflater.from(getActivity()).inflate(
					R.layout.dialog_reminder_authority_use, null);
			this.reminderDialog = new Dialog(getActivity(),
					R.style.alertdialog_style);
			this.reminderDialog.setContentView(view);
			this.reminderDialog.show();
			
			((TextView)view.findViewById(R.id.titleReminder_textView)).setText(title);
			((TextView)view.findViewById(R.id.reminderInfo_textView)).setText(content);
			((TextView)view.findViewById(R.id.IKnow_textView)).setText(ikonw);

			// 动态设置界面图片大小；
			DisplayMetrics metrics = getResources().getDisplayMetrics(); // 用来获取屏幕的分辨率；
			int screenWidthPX = metrics.widthPixels;
			int screenWidthDP = (int) DensityUtils.px2dp(getActivity(),
					screenWidthPX);

			// 设置图片的大小；
			int layoutWidthDP = (screenWidthDP * 4) / 5;
			int layoutWidthPX = DensityUtils
					.dp2px(getActivity(), layoutWidthDP);

			LayoutParams params = view.getLayoutParams();
			params.width = layoutWidthPX;
			view.setLayoutParams(params);

			// 添加点击事件
			view.findViewById(R.id.IKnow_textView).setOnClickListener(
				new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						reminderDialog.dismiss();
					}						
				});		

			} else {
				this.reminderDialog.show();
		}
	}

	// 陈总提出的新需求，以后不需要；
	public void initReminderDialog1(String title, String content, String ikonw){
		if (this.reminderDialog1 == null) {
			View view = LayoutInflater.from(getActivity()).inflate(
					R.layout.dialog_reminder_authority_use, null);
			this.reminderDialog1 = new Dialog(getActivity(),
					R.style.alertdialog_style);
			this.reminderDialog1.setContentView(view);
			this.reminderDialog1.show();
			
			((TextView)view.findViewById(R.id.titleReminder_textView)).setText(title);
			((TextView)view.findViewById(R.id.reminderInfo_textView)).setText(content);
			((TextView)view.findViewById(R.id.IKnow_textView)).setText(ikonw);

			// 动态设置界面图片大小；
			DisplayMetrics metrics = getResources().getDisplayMetrics(); // 用来获取屏幕的分辨率；
			int screenWidthPX = metrics.widthPixels;
			int screenWidthDP = (int) DensityUtils.px2dp(getActivity(),
					screenWidthPX);

			// 设置图片的大小；
			int layoutWidthDP = (screenWidthDP * 4) / 5;
			int layoutWidthPX = DensityUtils
					.dp2px(getActivity(), layoutWidthDP);

			LayoutParams params = view.getLayoutParams();
			params.width = layoutWidthPX;
			view.setLayoutParams(params);

			// 添加点击事件
			view.findViewById(R.id.IKnow_textView).setOnClickListener(
				new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						reminderDialog1.dismiss();
					}						
				});		

			} else {
				this.reminderDialog1.show();
		}
	}

	// 更新app 对话框；
	public void initUpdateAppDialog(String currentVersion, String updateVersion){
		if (this.updateAppDialog == null) {
			View view = LayoutInflater.from(getActivity()).inflate(
					R.layout.dialog_update_app, null);
			this.updateAppDialog = new Dialog(getActivity(),
					R.style.alertdialog_style);
			this.updateAppDialog.setContentView(view);
			this.updateAppDialog.show();
			
			((TextView)view.findViewById(R.id.currentVersion_textView)).setText(currentVersion);
			((TextView)view.findViewById(R.id.updateVersion_textView)).setText(updateVersion);

			// 动态设置界面图片大小；
			DisplayMetrics metrics = getResources().getDisplayMetrics(); // 用来获取屏幕的分辨率；
			int screenWidthPX = metrics.widthPixels;
			int screenWidthDP = (int) DensityUtils.px2dp(getActivity(),
					screenWidthPX);

			// 设置图片的大小；
			int layoutWidthDP = (screenWidthDP * 4) / 5;
			int layoutWidthPX = DensityUtils
					.dp2px(getActivity(), layoutWidthDP);

			LayoutParams params = view.getLayoutParams();
			params.width = layoutWidthPX;
			view.setLayoutParams(params);

			// 添加点击事件
			view.findViewById(R.id.cancle_textView).setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						updateAppDialog.dismiss();
					}						
				});
			
			view.findViewById(R.id.ok_textView).setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// "http://192.168.137.1:8055/TTQEducation_1.1.0.0.apk"
					Uri uri = Uri.parse(StudyFragment.this.downLoadAppUrl);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
					updateAppDialog.dismiss();
				}
			});
			
		 } else {
				this.updateAppDialog.show();
		}
	}

	
	// 根据模块收费情况以及用户缴费情况，判断模块是否可用；
	public void setModuleShowByModuleUse(){
		this.moduleUse = UserInfo.getInstance().moduleUse;
		Log.i("lvjie", "主界面最终各个模块的使用权限：moduleUse="+Arrays.toString(this.moduleUse));
		// 到了截止时间，收费模块不可用；0表示不可使用，1表示可以使用；
		// 作业结果查看
		if(this.moduleUse[0] == 0){
			this.homeworkResultImg.setColorFilter(Color.GRAY,Mode.MULTIPLY);
			this.homeworkResultLayout.setAlpha(0.8f);
		}else {
			this.homeworkResultImg.clearColorFilter();
			this.homeworkResultLayout.setAlpha(1.0f);
		}
		
		// 家庭作业布置情况
		if(this.moduleUse[1] == 0){
			this.homeworkArrangementImg.setColorFilter(Color.GRAY,Mode.MULTIPLY);
			this.homeworkArrangementLayout.setAlpha(0.8f);
		}else {
			this.homeworkArrangementImg.clearColorFilter();
			this.homeworkArrangementLayout.setAlpha(1.0f);
		}

		// 知识点掌握情况	
		if(this.moduleUse[2] == 0){
			this.knowledgeSituationImg.setColorFilter(Color.GRAY,Mode.MULTIPLY);
			this.knowledgeSituationLayout.setAlpha(0.8f);
		}else {
			this.knowledgeSituationImg.clearColorFilter();
			this.knowledgeSituationLayout.setAlpha(1.0f);
		}
		
		// 心理测试结果查看
		if(this.moduleUse[3] == 0){
			this.phychologicalTestResultImg.setColorFilter(Color.GRAY,Mode.MULTIPLY);
			this.phychologicalTestResultLayout.setAlpha(0.8f);
		}else {
			this.phychologicalTestResultImg.clearColorFilter();
			this.phychologicalTestResultLayout.setAlpha(1.0f);
		}
		
		// 分析报告订购
		if(this.moduleUse[4] == 0){
			this.studySituationImg.setColorFilter(Color.GRAY,Mode.MULTIPLY);
			this.studySituationLayout.setAlpha(0.8f);
		}else {
			this.studySituationImg.clearColorFilter();
			this.studySituationLayout.setAlpha(1.0f);
		}
	}
	
	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(getActivity(), toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub	
		super.onResume();
		
		//检测用户能否使用付费模块
		checkSchoolJurisdiction();
		checkUserJurisdiction();
		
		Log.i("lvjie", "StudyFragment-->onResume()...");
		setModuleShowByModuleUse();		
	}

	// 从公司端获取APP版本；
	public void getAppVersionByWS(){
		new AsyncTask<Object, Object, DataTable>() {

			protected void onPreExecute() {
				super.onPreExecute();
			};
			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DataTable dt_version = new DataTable();
				
				// 方法名
				String methodName = "pub_publishVersion_getUpdateNo";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("type", 4+"");
				paramsMap.put("TokenID", DesUtil.getDesTokenID(UserCurrentState.getInstance().userID, "Admin203", 1));

				// 获取数据
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				getDataTool.setURL((String) getResources().getText(R.string.company_service_url));
				try {
					dt_version = getDataTool.getDataAsTable(methodName, paramsMap);
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return dt_version;
			}
			protected void onPostExecute(DataTable result) {
				if (result != null) {
					int count = result.getRowCount();
					for (int i = 0; i < count; i++) {
						try {
							String updateVersion = result.getCell(i, "Version").trim();
							String downloadUrl = result.getCell(i, "downloadUrl").trim();
							//StudyFragment.this.downLoadAppUrl = downloadUrl.substring(0, len-4)+"_"+updateVersion+".apk";
							StudyFragment.this.downLoadAppUrl = downloadUrl;
							
							//获取AndroidManifest文件里面的app version
							PackageManager packageManager = getActivity().getApplicationContext().getPackageManager();
							try {
								PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getApplicationContext().getPackageName(), 0);
								String currentVersion = packageInfo.versionName;
								if(!currentVersion.equals(updateVersion)){
									initUpdateAppDialog("当前版本：v"+currentVersion, "更新版本：v"+updateVersion);
								}
							} catch (NameNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
//							String currentVersion = getResources().getString(R.string.app_version);
//							if(!currentVersion.equals(updateVersion)){
//								initUpdateAppDialog("当前版本：v"+currentVersion, "更新版本：v"+updateVersion);
//							}
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}

			};
			
		}.execute();
	}

}
