package com.ttqeducation.activitys.others;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.R;
import com.ttqeducation.activitys.system.ChooseSchoolActivity;
import com.ttqeducation.beans.ContextApplication;
import com.ttqeducation.beans.UserCurrentState;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.network.PushService;
import com.ttqeducation.tools.DateUtil;
import com.ttqeducation.tools.DensityUtils;
import com.ttqeducation.tools.DesUtil;
import com.ttqeducation.tools.GeneralTools;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;

public class OthersFragment extends Fragment {

	private TextView parentNameTextView = null; // 家长名字
	private TextView reminderInfoTextView = null; // 下面的提示信息

	private LinearLayout paymentLayout = null; // 服务缴费
	private LinearLayout tryoutLayout = null; // 申请试用
	private LinearLayout changePwdLayout = null; // 修改密码 -->变为跳转的个人设置界面了；
	private LinearLayout exitLayout = null; // 退出系统

	private Dialog tryoutDialog = null; // 申请试用对话框
	private Dialog exitDialog = null; // 退出系统对话框
	private Dialog closeSystemDialog = null; // 关闭系统对话框
	private Dialog tryoutFailureDialog = null; // 申请失败提示对话框；
	private String deadline = null;

	private boolean isRunBackgroundServer = true; // 退出系统是否允许后台服务，也即是否消息提示

	private RefreshView refreshView = null;// 等待圆圈界面

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View newsLayout = inflater.inflate(R.layout.others_layout, container,
				false);

		 initView(newsLayout);
		return newsLayout;
	}
	

	public void initView(View view) {
		this.deadline=UserInfo.getInstance().deadline;
		this.parentNameTextView = (TextView) view
				.findViewById(R.id.parentName_textView);
		this.parentNameTextView.setText(UserInfo.getInstance().childName.trim()+"家长，您好！");
		this.reminderInfoTextView = (TextView) view
				.findViewById(R.id.reminderInfo_textView);
		setReminderTextView();

		this.paymentLayout = (LinearLayout) view
				.findViewById(R.id.layout_payment);
		this.tryoutLayout = (LinearLayout) view
				.findViewById(R.id.layout_tryout);
		this.changePwdLayout = (LinearLayout) view
				.findViewById(R.id.layout_changePwd);
		this.exitLayout = (LinearLayout) view.findViewById(R.id.layout_exit);

		this.paymentLayout.setOnClickListener(new MyOnClickListener());
		this.tryoutLayout.setOnClickListener(new MyOnClickListener());
		this.changePwdLayout.setOnClickListener(new MyOnClickListener());
		this.exitLayout.setOnClickListener(new MyOnClickListener());
		
	}
	
	// 让提示信息控件显示内容；
	public void setReminderTextView(){
		
		if(UserInfo.getInstance().flag == 1){
			// 表示模块都是免费使用的；
			this.reminderInfoTextView.setText("       感谢您的使用，请在消息栏中留下宝贵意见，以便我们能给您和您的孩子提供更贴心的服务！");
			return;
		}
		
		this.deadline = UserInfo.getInstance().deadline;
		Log.i("lvjie", "deadline="+this.deadline);
		String reminderInfo = "";
		int k = -1;
		
		if(this.deadline == null){
			return;
		}
		
		if(!this.deadline.trim().equals("null")){		// 表示有一个截止时间；	
			Date date1 = new Date();
			Date date2;			
			try {
//				this.deadline = "2015-03-10";		// 自己测试，临时使用；
				date2 = DateUtil.convertStringToDate("yyyy-MM-dd", this.deadline);
				k = DateUtil.daysBetween(date1, date2);			// 获取两个日期的相隔天数；
				Log.i("lvjie", "date1="+date1+"   date2="+date2+"   k="+k);
				String date2String = DateUtil.convertDateToString("yyyy年MM月dd日", date2);
				
				if(k<0){		// 已到期；			
					reminderInfo = "感谢您的使用，您当前没有\n开通付费功能，如有需要请付费开通!";
					// 用来设置 突出字体的颜色;   字体全为红色；
					SpannableStringBuilder builder = new SpannableStringBuilder(reminderInfo);
					// ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
					ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
					builder.setSpan(redSpan, 0, reminderInfo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					this.reminderInfoTextView.setText(builder);
					
				}else if(k<=30){		// 具体到期时间少于30天；
					reminderInfo = "感谢您的使用，您的服务\n将在 "+k+"天 后到期，请及时缴费！";
					// 用来设置 突出字体的颜色;
					SpannableStringBuilder builder = new SpannableStringBuilder(reminderInfo);
					// ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
					ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
					builder.setSpan(redSpan, 14, 14+(k+"").length()+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					this.reminderInfoTextView.setText(builder);
				}else {			// 距离到期时间大于30天；
					reminderInfo = "感谢您的使用，您的\n服务有效期至"+date2String+"。";
					this.reminderInfoTextView.setText(reminderInfo);
					Log.i("lvjie", "reminderInfo="+reminderInfo);
				}			
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {			// 为空；表示没有截止日期；
			
		}
		
		
	}

	// 当申请试用成功，马上改变提示信息控件显示内容；
	public void setReminderTextViewByApply(){
		setReminderTextView();
	}
	
	private class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.layout_payment: // 服务缴费    暂时注释，用来修改界面测试；
//				if(UserInfo.getInstance().flag == 1){
//					// 模块免费使用，用户点击没效果；
//					return;
//				}
				
				// 没有联网，不可以进入到下一个界面；
				if(!GeneralTools.getInstance().isOpenNetWork1(getActivity())){
					showToast("未连接到互联网，请检查网络配置!");
					return;
				}
				
				Intent paymentIntent = new Intent(getActivity(),
						ServePaymentConditionActivity.class);
				startActivityForResult(paymentIntent, 10);
				break;
			case R.id.layout_tryout: // 申请试用  暂时注释，用来修改界面测试；
				
//				if(UserInfo.getInstance().flag == 1){
//					// 模块免费使用，用户点击没效果；
//					return;
//				}
				
				// 没有联网，不可以进入到下一个界面；
				if(!GeneralTools.getInstance().isOpenNetWork1(getActivity())){
					showToast("未连接到互联网，请检查网络配置!");
					return;
				}
				Intent intent = new Intent(getActivity(), ApplyTryoutActivity.class);
				startActivity(intent);
//				initTryoutDialog();
				break;
			case R.id.giveUp_textView: // 放弃申请试用
				tryoutDialog.dismiss();
				break;
			case R.id.tryout_textView: // 申请试用成功 ；
				//点击试用，调用申请的方法
				// 获取用户ID
				String user_id = UserInfo.getInstance().studentID;
				OthersFragment.this.Check_and_ApplyToTryOut(user_id);

				break;
			case R.id.layout_changePwd: // 密码修改
				Intent changePwdIntent = new Intent(getActivity(),
						UserOwnSetActivity.class);
				startActivity(changePwdIntent);
				break;
			case R.id.layout_exit: // 退出系统
				initExitDialog();
				break;
			case R.id.exitCurrentID_layout: // 退出当前账号
				Intent chooseSchoolIntent = new Intent(getActivity(),
						ChooseSchoolActivity.class);
				startActivity(chooseSchoolIntent);
				getActivity().finish();
				UserInfo.getInstance().clearInstance();		// 清除之前保存的公共信息；
				setRunBackgroundServer(false);				// 停止中间件服务；
				
				// 修改是第一次登陆；便于下次进入系统直接进入学校选择界面；
				SharedPreferences pre = getActivity().getSharedPreferences("TTQAndroid", getActivity().MODE_PRIVATE);
				SharedPreferences.Editor pre_editor = pre.edit();
				pre_editor.putBoolean("ifFirstUse", false);
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
				getActivity().finish();
				break;

			default:
				break;
			}
		}

	}

	public void setRunBackgroundServer(boolean isRun) {
		if (isRun) {
			//showToast("启动后台服务");
		} else {
			//showToast("关闭后台服务");
			Intent intent = new Intent(getActivity(),PushService.class);
			getActivity().stopService(intent);
		}
	}
	

	// 初始化申请试用对话框；
	public void initTryoutDialog() {
		if (this.tryoutDialog == null) {
			View view = LayoutInflater.from(getActivity()).inflate(
					R.layout.dialog_apply_tryout1, null);
			this.tryoutDialog = new Dialog(getActivity(),
					R.style.alertdialog_style);
			this.tryoutDialog.setContentView(view);
			this.tryoutDialog.show();

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
			view.findViewById(R.id.giveUp_textView).setOnClickListener(
					new MyOnClickListener());
			view.findViewById(R.id.tryout_textView).setOnClickListener(
					new MyOnClickListener());

		} else {
			this.tryoutDialog.show();
		}
	}

	// 初始化申请试用失败对话框；
	public void initTryOutFailureDialog(){
		if (this.tryoutFailureDialog == null) {
			View view = LayoutInflater.from(getActivity()).inflate(
					R.layout.dialog_reminder_authority_use, null);
			((TextView)view.findViewById(R.id.titleReminder_textView)).setText("申请失败!");
			((TextView)view.findViewById(R.id.reminderInfo_textView)).setText("        尊敬的用户，十分抱歉您的申请试用机会已用完，感谢您的使用!");
			this.tryoutFailureDialog = new Dialog(getActivity(),
					R.style.alertdialog_style);
			this.tryoutFailureDialog.setContentView(view);
			this.tryoutFailureDialog.show();

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
			((TextView)view.findViewById(R.id.IKnow_textView)).setText("好");
			view.findViewById(R.id.IKnow_textView).setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					tryoutFailureDialog.dismiss();
				}
			});
			
		} else {
			this.tryoutFailureDialog.show();
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

	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(getActivity(), toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}

	/**用户申请试用，如成供返回1 否则0
	 * @param user_id
	 */
	public void Check_and_ApplyToTryOut(String user_id) {
		this.refreshView = new RefreshView(this.getActivity(),
				R.style.full_screen_dialog);
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, DataTable>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected DataTable doInBackground(Object... params) {

				DataTable dt = null;
				// 方法名
				String methodName = "APP_apply_try";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("studentID", params[0].toString());
				paramsMap.put("TokenID", DesUtil.getDesTokenID(UserCurrentState.getInstance().userID, "Admin203", 1));
				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 获取堂堂清公司的服务地址
				Resources res = getResources();
				String companyURL = (String) res
						.getText(R.string.company_service_url);
				if (companyURL == null) {// 如果没有值
					return null;
				}
				getdatatool.setURL(companyURL);
				try {
					try {
						dt = getdatatool.getDataAsTable(methodName, paramsMap);
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return dt;
			}

			protected void onPostExecute(DataTable result) {
				
				// 关闭刷新；
				refreshView.dismiss();
				
				if (result != null) {
					try {
						Boolean flag = Boolean.parseBoolean(result.getCell(0, "flag"));
						String deadLine = result.getCell(0, "NewDeadline").split("T")[0];
						Log.i("lvjie", "申请试用：flag="+flag+"   deadLine="+deadLine);
						UserInfo.getInstance().deadline = deadLine;			// 不管申请试用是否成功，都修改当前保存的到期时间；

						if(flag==true){//如成功
							tryoutDialog.dismiss();
							showToast("申请试用成功!");
							setReminderTextViewByApply();
							
						}else{//如果失败
							tryoutDialog.dismiss();
//							showToast("申请试用失败！您已经使用过1次");
							initTryOutFailureDialog();
						}
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		}.execute(user_id);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i("lvjie", "OthersFragment--->onActivityResult()...resultCode="+resultCode);
		if(resultCode == 10){	// 表示支付成功了，需要再次从数据库中获取截止时间；
			getDeadLineFromWS();		// 再一次获取截止时间，并设置界面显示；
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void getDeadLineFromWS(){
		if(this.refreshView == null){
			this.refreshView = new RefreshView(this.getActivity(),
					R.style.full_screen_dialog);
		}
		new AsyncTask<Object, Object, DataTable>(){
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				Log.i("lvjie", "getDeadLineFromWS()-->AsyncTask...doInBackground()");
				DataTable dt = null;
				// 方法名
				String methodName = "APP_getUserDeadline";
				// 存放参数的map
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();

				paramsMap.put("studentID", UserInfo.getInstance().studentID);
				paramsMap.put("TokenID", DesUtil.getDesTokenID(UserCurrentState.getInstance().userID, "Admin203", 1));
				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 获取堂堂清公司的服务地址
				Resources res = ContextApplication.getAppContext()
						.getResources();
				String companyURL = (String) res
						.getText(R.string.company_service_url);
				if (companyURL == null) {// 如果没有值
					return null;
				}
				getdatatool.setURL(companyURL);
				try {
					try {
						dt = getdatatool.getDataAsTable(methodName, paramsMap);
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return dt;
			}

			protected void onPostExecute(DataTable result) {
				
				// 关闭刷新；
				refreshView.dismiss();
				if (result != null) {
					try {
						UserInfo.getInstance().deadline = result.getCell(0, "deadline").toString().split("T")[0];
						Log.i("lvjie", "getDeadLineFromWS()-->AsyncTask...onPostExecute()  "+UserInfo.getInstance().deadline);
						setReminderTextView();
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		}.execute();
	}
	
}
