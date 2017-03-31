package com.ttqeducation.teacher.activitys.others;



import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.activitys.system.ChooseSchoolActivity;
import com.ttqeducation.teacher.beans.TeacherInfo;
import com.ttqeducation.teacher.network.PushService;
import com.ttqeducation.teacher.tools.DensityUtils;


import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class OthersFragment extends Fragment{
	
	private LinearLayout changePwdLayout = null; // 修改密码--修改为个人设置
	private LinearLayout exitLayout = null; // 退出系统
	
	private Dialog exitDialog = null; // 退出系统对话框
	private Dialog closeSystemDialog = null; // 关闭系统对话框
	
	private TextView reminderTextView = null;	// 界面提示部分；
	private boolean isRunBackgroundServer = true; // 退出系统是否允许后台服务，也即是否消息提示
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.others_fragment, container, false);
		initView(view);
		return view;
	}
	
	public void initView(View view){
		
		this.reminderTextView = (TextView) view.findViewById(R.id.reminderInfo_textView);
		this.reminderTextView.setText(TeacherInfo.getInstance().teacherName+"老师，您好！");
		
		this.changePwdLayout = (LinearLayout) view.findViewById(R.id.layout_changePwd);
		this.exitLayout = (LinearLayout) view.findViewById(R.id.layout_exit);
		
		this.changePwdLayout.setOnClickListener(new MyOnClickListener());
		this.exitLayout.setOnClickListener(new MyOnClickListener());
		
		
//		// 动态设置界面图片大小；
//		DisplayMetrics metrics = getResources().getDisplayMetrics(); // 用来获取屏幕的分辨率；
//		int screenWidthPX = metrics.widthPixels;
//		int screenWidthDP = (int) DensityUtils.px2dp(getActivity(),
//				screenWidthPX);
//
//		// 设置图片的大小；
//		int imageWidthDP = screenWidthDP / 2 - 26;
//		int imageWidthPX = DensityUtils.dp2px(getActivity(), imageWidthDP);
//		
//		int[] LinearLayout_other = new int[] {R.id.layout_changePwd, R.id.layout_exit};
//		int count_other=LinearLayout_other.length;
//
//		for (int j = 0; j < count_other; j++) {
//			LinearLayout linearLayout = (LinearLayout) view.findViewById(LinearLayout_other[j]);
//			LayoutParams params = (LayoutParams) linearLayout.getLayoutParams();
//			params.width = imageWidthPX;
//			params.height = imageWidthPX;
//			linearLayout.setLayoutParams(params);
//		}
	}
	
	
	private class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.layout_changePwd: // 密码修改--改为个人设置
				Intent intent = new Intent(getActivity(), UserOwnSetActivity.class);
				startActivity(intent);
				break;
			case R.id.layout_exit: // 退出系统
				initExitDialog();
				break;
			case R.id.exitCurrentID_layout: // 退出当前账号
				exitDialog.dismiss();
				Intent chooseSchoolIntent = new Intent(getActivity(), ChooseSchoolActivity.class);
				startActivity(chooseSchoolIntent);
				getActivity().finish();
				TeacherInfo.getInstance().clearInstance();		// 清除之前保存的公共信息；
				setRunBackgroundServer(false);				// 停止中间件服务；
				// 修改是第一次登陆；便于下次进入系统直接进入学校选择界面；
				SharedPreferences pre = getActivity().getSharedPreferences("TTQAndroid", getActivity().MODE_PRIVATE);
				SharedPreferences.Editor pre_editor = pre.edit();
				pre_editor.putBoolean("isLoginSucceed", false);
				pre_editor.commit();
				break;
			case R.id.closeSystem_layout: // 关闭系统
				exitDialog.dismiss();
				initCloseSystemDialog();
				break;
			case R.id.cancle_textView: // 取消关闭系统
				closeSystemDialog.dismiss();
				break;
			case R.id.closeSystem_textView: // 正真关闭系统
				closeSystemDialog.dismiss();
				setRunBackgroundServer(isRunBackgroundServer);
				TeacherInfo.getInstance().initSystem = 0;
				getActivity().finish();
				break;

			default:
				break;
			}
		}

	}
	  
	// 初始化退出系统对话框；
	public void initExitDialog() {
		if (this.exitDialog == null) { 
			View view = LayoutInflater.from(getActivity()).inflate(
					R.layout.dialog_exit, null);
			this.exitDialog = new Dialog(getActivity(),
					R.style.alertdialog_style);
			this.exitDialog.setContentView(view);
			this.exitDialog.setCanceledOnTouchOutside(true);
			this.exitDialog.show();

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

			// 增加点击事件
			view.findViewById(R.id.exitCurrentID_layout).setOnClickListener(
					new MyOnClickListener());
			view.findViewById(R.id.closeSystem_layout).setOnClickListener(
					new MyOnClickListener());

		} else {
			this.exitDialog.show();
		}
	}
	
	// 初始化关闭系统对话框；
	public void initCloseSystemDialog() {
		if (this.closeSystemDialog == null) {
			View view = LayoutInflater.from(getActivity()).inflate(
					R.layout.dialog_close_system, null);
			this.closeSystemDialog = new Dialog(getActivity(),
					R.style.alertdialog_style);
			this.closeSystemDialog.setContentView(view);
			this.closeSystemDialog.setCanceledOnTouchOutside(true);
			this.closeSystemDialog.show();

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
					new MyOnClickListener());
			view.findViewById(R.id.closeSystem_textView).setOnClickListener(
					new MyOnClickListener());
			((CheckBox) view.findViewById(R.id.isRunBackgroundServer_checkBox))
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							isRunBackgroundServer = isChecked;
							Log.i("lvjie", "" + isChecked);
						}
					});

		} else {
			this.closeSystemDialog.show();
		}
	}
	
	public void setRunBackgroundServer(boolean isRun) {
		if (isRun) {
			showToast("启动后台服务");
		} else {
			showToast("关闭后台服务");
			Intent intent = new Intent(getActivity(),PushService.class);
			getActivity().stopService(intent);
		}
	}
	
	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(getActivity(), toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}



	
}
