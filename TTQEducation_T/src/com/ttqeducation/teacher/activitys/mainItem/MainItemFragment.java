package com.ttqeducation.teacher.activitys.mainItem;

/**
 *侯翔宇
 *此界面对应程序的主界面；主要完成界面的初始化，及动态适应不同分辨率的手机，给对应按钮增加点击事件； 
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.activitys.message.MessageFragment;
import com.ttqeducation.teacher.activitys.teach.HomeWorkArrangeActivity;
import com.ttqeducation.teacher.activitys.teach.KnowledgePointActivity;
import com.ttqeducation.teacher.activitys.teach.SubjectiveHomeworkActivity;
import com.ttqeducation.teacher.activitys.teach.TaskTypeChooseActivity;

import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.TeacherInfo;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.tools.DensityUtils;
import com.ttqeducation.teacher.tools.DesUtil;

import com.ttqeducation.teacher.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MainItemFragment extends Fragment {
	
	private ImageView ivWelcomePicture=null;		//上方欢迎图片
	//五个格子，用于点击事件
	private FrameLayout subjectiveHomeworkLayout=null;     //家庭作业主观题批改
	private FrameLayout homeworkResultLayout=null;         //作业结果查看
	private FrameLayout knowledgeSituationLayout=null;     //知识点掌握度查看
	private FrameLayout psychologicalTestResultLayout=null;      //心理健康查看
	private FrameLayout homeworkArrangementLayout=null;    //家庭作业布置
	
	private ImageView subjectiveHomeworkImg=null;     //家庭作业主观题
	private ImageView homeworkResultImg=null;         //作业结果查看
	private ImageView knowledgeSituationImg=null;     //知识点掌握度查看
	private ImageView psychologicalTestResultImg=null;      //心理健康查看
	private ImageView homeworkArrangementImg=null;    //家庭作业布置
	
	private TextView welcomeTextView = null;	// 欢迎家长部分文字；
	
	private Dialog reminderDialog = null;	// 提示用dialog；
	private String downLoadAppUrl = "";			// 下载app的url;
	private Dialog updateAppDialog = null;	// 版本更新对话框；
	private String teacherID="";
	private String termID="";
	
	private RefreshView refreshView=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View layout=inflater.inflate(R.layout.activity_item, container, false);
		 getAppVersionByWS();		// 有需要就进行版本更新；
		
		
		initView(layout);
		return layout;
	}
	
	

	

	public void initView(View view){
		// 欢迎家长
		this.welcomeTextView = (TextView) view.findViewById(R.id.welcome_textView);
		this.welcomeTextView.setText(TeacherInfo.getInstance().teacherName+"老师，欢迎您");
		
		//获取上方图片
		this.ivWelcomePicture=(ImageView) view.findViewById(R.id.welcomePicture);
		//获取五个按键模块
		this.subjectiveHomeworkLayout=(FrameLayout)view.findViewById(R.id.subjective_homework_layout);
		this.homeworkResultLayout= (FrameLayout) view.findViewById(R.id.homework_result_layout);
		this.knowledgeSituationLayout=(FrameLayout) view.findViewById(R.id.knowledge_situation_layout);
		this.psychologicalTestResultLayout=(FrameLayout) view.findViewById(R.id.psychological_test_result_layout);
		this.homeworkArrangementLayout=(FrameLayout)view.findViewById(R.id.homework_arrangement_layout);
		
		//设置点击事件
		this.subjectiveHomeworkLayout.setOnClickListener(new MyClickListener());
		this.homeworkResultLayout.setOnClickListener(new MyClickListener());
		this.knowledgeSituationLayout.setOnClickListener(new MyClickListener());
		this.psychologicalTestResultLayout.setOnClickListener(new MyClickListener());
		this.homeworkArrangementLayout.setOnClickListener(new MyClickListener());
		
		//初始化五个格子中的图片
		this.subjectiveHomeworkImg=(ImageView) view.findViewById(R.id.subjective_homework_img);
		this.homeworkResultImg=(ImageView) view.findViewById(R.id.homework_result_img);
		this.knowledgeSituationImg=(ImageView) view.findViewById(R.id.knowledge_situation_img);
		this.psychologicalTestResultImg=(ImageView) view.findViewById(R.id.psychological_test_result_img);
		this.homeworkArrangementImg=(ImageView) view.findViewById(R.id.homework_arrangement_img);
		
		// 获取屏幕宽度
		DisplayMetrics metrics = getResources().getDisplayMetrics(); // 用来获取屏幕的分辨率；
		int screenWidthPX = metrics.widthPixels;
		float screenWidthDP = DensityUtils.px2dp(getActivity(), screenWidthPX);
		// 作业结果查看、家庭作业布置情况、知识点查看
		float firstLineWithDP = screenWidthDP - ((89 * 2 + 19) / 3);
		float firstLineWithPX = DensityUtils.dp2px(getActivity(), firstLineWithDP);
		float firstScale = firstLineWithPX / 883; // 缩放比例
		// 心理测试结果查看、分析报告订购
		float secondLineWithDP = screenWidthDP - ((89 * 2 + 22) / 3);
		float secondLineWithPX = DensityUtils.dp2px(getActivity(), secondLineWithDP);
		float secondScale = secondLineWithPX / 880; // 缩放比例
		
		//欢迎图片
		LayoutParams ivWelcomePictureParams = (LayoutParams) ivWelcomePicture.getLayoutParams();
		ivWelcomePictureParams.width = screenWidthPX;
		ivWelcomePictureParams.height = (int)((screenWidthPX / (float)1080) * 563);
		ivWelcomePicture.setLayoutParams(ivWelcomePictureParams);
		
		// 主观题批阅
		LayoutParams subjectiveHomeworkParams = (LayoutParams) subjectiveHomeworkLayout.getLayoutParams();
		subjectiveHomeworkParams.width = (int)(firstScale * 649);
		subjectiveHomeworkParams.height = (int)(firstScale * 484);
		subjectiveHomeworkLayout.setLayoutParams(subjectiveHomeworkParams);
		
		// 家庭作业布置情况
		LayoutParams homeworkArrangementParams = (LayoutParams) homeworkArrangementLayout.getLayoutParams();
		homeworkArrangementParams.width = (int)(secondScale * 491);
		homeworkArrangementParams.height = (int)(secondScale * 272);
		homeworkArrangementLayout.setLayoutParams(homeworkArrangementParams);
		
		// 知识点查看
		LayoutParams knowledgeSituationParams = (LayoutParams) knowledgeSituationLayout.getLayoutParams();
		knowledgeSituationParams.width = (int)(firstScale * 333);
		knowledgeSituationParams.height = (int)(firstScale * 230);
		knowledgeSituationLayout.setLayoutParams(knowledgeSituationParams);
		
		// 心理测试结果查看
		LayoutParams psychologicalTestResultParams = (LayoutParams) psychologicalTestResultLayout.getLayoutParams();
		psychologicalTestResultParams.width = (int)(secondScale * 491);
		psychologicalTestResultParams.height = (int)(secondScale * 272);
		psychologicalTestResultLayout.setLayoutParams(psychologicalTestResultParams);
		
		// 作业结果查看
		LayoutParams homeworkResultParams = (LayoutParams) homeworkResultLayout.getLayoutParams();
		homeworkResultParams.width = (int)(firstScale * 333);
		homeworkResultParams.height = (int)(firstScale * 230);
		homeworkResultLayout.setLayoutParams(homeworkResultParams);
		
		
	}
	
	private class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.subjective_homework_layout:
				Intent subjectiveHomeworkIntent = new Intent(
						getActivity(), SubjectiveHomeworkActivity.class);
				startActivity(subjectiveHomeworkIntent);
				break;
			case R.id.psychological_test_result_layout:
				initReminderDialog("提示", "        该模块正在研发中，请期待！", "我知道了","");
				break;
			case R.id.homework_arrangement_layout:
				Intent homeworkArrangeIntent = new Intent(
						getActivity(), HomeWorkArrangeActivity.class);
				startActivity(homeworkArrangeIntent);
				break;
			case R.id.homework_result_layout:				//作业结果查看
				Intent homeworkResultIntent= new Intent(
						getActivity(), TaskTypeChooseActivity.class);
				startActivity(homeworkResultIntent);
				break;
			case R.id.knowledge_situation_layout:
				Intent knowledgePoint = new Intent(
						getActivity(), KnowledgePointActivity.class);
				startActivity(knowledgePoint);
				break;
			default:
				break;
			}
		}
		
	}
	
	
	// 陈总提出的新需求，以后不需要；
	public void initReminderDialog(String title, String content, String ikonw,String cancel){
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
			if(cancel.equals("")){
				((TextView)view.findViewById(R.id.tv_cancel)).setVisibility(View.GONE);
			}else{
				((TextView)view.findViewById(R.id.tv_cancel)).setText(cancel);
			}
				

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
	
	// 从公司端获取APP版本；
	public void getAppVersionByWS(){
		new AsyncTask<Object, Object, DataTable>() {

			protected void onPreExecute() {
				super.onPreExecute();
			};
			@Override
			protected DataTable doInBackground(Object... params) {
				DataTable dt_version = new DataTable();
				
				// 方法名
				String methodName = "pub_publishVersion_getUpdateNo";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("type", 5+"");
				paramsMap.put("TokenID", DesUtil.getDesTokenID(TeacherInfo.getInstance().execTeacherID, "Admin203", 1));
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
							MainItemFragment.this.downLoadAppUrl = downloadUrl;
							
							//从AndroidManifest获取app version
							PackageManager packageManager = getActivity().getApplicationContext().getPackageManager();
							try {
								PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getApplicationContext().getPackageName(), 0);
								String currentAppVersion = packageInfo.versionName;
								if(!currentAppVersion.equals(updateVersion)){
									initUpdateAppDialog("当前版本：v"+currentAppVersion, "更新版本：v"+updateVersion);
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
			}
			
		}.execute();
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
						updateAppDialog.dismiss();
					}						
				});
			
			view.findViewById(R.id.ok_textView).setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					// "http://192.168.137.1:8055/TTQEducation_1.1.0.0.apk"
					Uri uri = Uri.parse(MainItemFragment.this.downLoadAppUrl);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
					updateAppDialog.dismiss();
				}
			});
			
		 } else {
				this.updateAppDialog.show();
		}
	}

	
	
}
