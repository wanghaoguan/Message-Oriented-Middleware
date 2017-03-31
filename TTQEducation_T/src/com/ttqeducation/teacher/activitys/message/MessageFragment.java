package com.ttqeducation.teacher.activitys.message;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.TeacherInfo;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.tools.DensityUtils;
import com.ttqeducation.teacher.tools.DesUtil;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MessageFragment extends Fragment{
	
	// 九宫格的每个格子； 用于点击事件；
	private LinearLayout homeSchoolLayout = null; // 家校互动
	private LinearLayout teacherCommunicationLayout = null; // 教师交流群
	
	private TextView welcomTextView = null;			// 欢迎文字提示；
	
	private Dialog updateAppDialog = null;	// 版本更新对话框；
	private String downLoadAppUrl = "";			// 下载app的url;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.message_fragment, container, false);
		
		getAppVersionByWS();		// 有需要就进行版本更新；
		initView(view);
		
		return view;
	}
	
	public void initView(View view){
		
		this.welcomTextView = (TextView) view.findViewById(R.id.welcome_textView);
		this.welcomTextView.setText(TeacherInfo.getInstance().teacherName+"老师,  您好!");
		
		this.homeSchoolLayout = (LinearLayout) view.findViewById(R.id.home_school_layout);
		this.teacherCommunicationLayout = (LinearLayout) view.findViewById(R.id.teacher_communication_layout);
		
		this.homeSchoolLayout.setOnClickListener(new MyClickListener());
		this.teacherCommunicationLayout.setOnClickListener(new MyClickListener());
		
		// 动态设置界面图片大小；
		DisplayMetrics metrics = getResources().getDisplayMetrics(); // 用来获取屏幕的分辨率；
		int screenWidthPX = metrics.widthPixels;
		int screenWidthDP = (int) DensityUtils.px2dp(getActivity(),
				screenWidthPX);

		// 设置图片的大小；
		int imageWidthDP = screenWidthDP / 2 - 26;
		int imageWidthPX = DensityUtils.dp2px(getActivity(), imageWidthDP);
		int[] layout = new int[] { R.id.home_school_layout,
				R.id.teacher_communication_layout};
		int count = layout.length;
		for (int i = 0; i < count; i++) {
			LinearLayout linearLayout = (LinearLayout) view.findViewById(layout[i]);
			LayoutParams params = (LayoutParams) linearLayout.getLayoutParams();
			params.width = imageWidthPX;
			params.height = imageWidthPX;
			linearLayout.setLayoutParams(params);
		}
		
	}
	
	private class MyClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.home_school_layout:
//				showToast("家校互动");
				Intent personListIntent = new Intent(getActivity(), TeacherAndParentChatListActivity.class);
				startActivity(personListIntent);
				break;
			case R.id.teacher_communication_layout:
//				showToast("班级交流");
				Intent classListIntent = new Intent(getActivity(), ClassListActivity.class);
				startActivity(classListIntent);
				break;

			default:
				break;
			}
		}
		
	}
	
	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(getActivity(), toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
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
							int len = downloadUrl.length();
							MessageFragment.this.downLoadAppUrl = downloadUrl.substring(0, len-4)+"_"+updateVersion+".apk";
							Log.i("lvjie", "updateVersion="+updateVersion+"  downLoadAppUrl="+downLoadAppUrl);
							String currentVersion = getResources().getString(R.string.app_version);
							if(!currentVersion.equals(updateVersion)){
								initUpdateAppDialog("当前版本：v"+currentVersion, "更新版本：v"+updateVersion);
							}
							
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
						// TODO Auto-generated method stub
						updateAppDialog.dismiss();
					}						
				});
			
			view.findViewById(R.id.ok_textView).setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// "http://192.168.137.1:8055/TTQEducation_1.1.0.0.apk"
					Uri uri = Uri.parse(MessageFragment.this.downLoadAppUrl);
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
