package com.ttqeducation.activitys.others;

/**
 * 吕杰
 * 
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.R;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.UserCurrentState;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DesUtil;
import com.ttqeducation.tools.GeneralTools;
import com.ttqeducation.tools.MD5;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class ChangePwdActivity extends Activity {

	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；

	private TextView currentIdTextView = null;	// 当前账号
	private EditText oldPwdEditText = null; // 当前密码
	private EditText firstPwdEditText = null; // 第一次输入的新密码
	private EditText secondPwdEditText = null; // 第二次输入的新密码
	private CheckBox isShowPwdCheckBox = null; // 是否显示密码；
	private Button okButton = null; // 确认按钮；

	private RefreshView refreshView = null;// 等待圆圈界面
	private String parentID = null;			// 家长账号；
	
	private String newPwd = null;			// 保存当前用户修改成功的密码；

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_pwd);

		initView();
	}

	public void initView() {
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("修改密码");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ChangePwdActivity.this.finish();
			}
		});

		setParentID();
		// 设置账号显示；
		this.currentIdTextView = (TextView)super.findViewById(R.id.currentId_textView);		
		this.currentIdTextView.setText(this.parentID);
		
		this.oldPwdEditText = (EditText) super
				.findViewById(R.id.oldPwd_editText);
		this.firstPwdEditText = (EditText) super
				.findViewById(R.id.firstPwd_editText);
		this.secondPwdEditText = (EditText) super
				.findViewById(R.id.secondPwd_editText);

		this.isShowPwdCheckBox = (CheckBox) super
				.findViewById(R.id.isShowPwd_checkBox);
		this.isShowPwdCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							// 设置明码显示密码
							oldPwdEditText
									.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
							firstPwdEditText
									.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
							secondPwdEditText
									.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
						} else {
							// 设置密文显示密码
							oldPwdEditText
									.setInputType(InputType.TYPE_CLASS_TEXT
											| InputType.TYPE_TEXT_VARIATION_PASSWORD);
							firstPwdEditText
									.setInputType(InputType.TYPE_CLASS_TEXT
											| InputType.TYPE_TEXT_VARIATION_PASSWORD);
							secondPwdEditText
									.setInputType(InputType.TYPE_CLASS_TEXT
											| InputType.TYPE_TEXT_VARIATION_PASSWORD);
						}
					}
				});

		this.okButton = (Button) super.findViewById(R.id.ok_button);
		this.okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				// 没有联网，不可以进入到下一个界面；
				if(!GeneralTools.getInstance().isOpenNetWork1(ChangePwdActivity.this)){
					showToast("未连接到互联网，请检查网络配置!");
					return;
				}
				
				// 从界面获取密码；
				String oldPwdString = oldPwdEditText.getText().toString();
				String firstPwdString = firstPwdEditText.getText().toString();
				String secondPwdString = secondPwdEditText.getText().toString();

				// 如果两次输入的新密码相同，则调用方法验证原密码是否正确，如果正确则调用修改密码的方法
				if (firstPwdString.equals(secondPwdString)
						&& !firstPwdString.equals("")) {

					String studentID = ChangePwdActivity.this.parentID;
					Log.i("lvjie", "studentID="+studentID+"  oldPwdString="+oldPwdString+"  firstPwdString="+firstPwdString);
					ChangePwdActivity.this.validateAndChangeUserPass(studentID, oldPwdString, firstPwdString);
				} else {
					showToast("两次新密码不相同!");
				}
			}
		});
		
		
		ImageView loginTopImageView = (ImageView) super.findViewById(R.id.loginTop_img);
		// 动态设置界面图片大小；
		DisplayMetrics metrics = getResources().getDisplayMetrics(); // 用来获取屏幕的分辨率；
		int screenWidthPX = metrics.widthPixels; 
		int screenHeightPX = screenWidthPX*823/1081;
		LayoutParams layoutParams = loginTopImageView.getLayoutParams();		
		layoutParams.width = screenWidthPX;
		layoutParams.height = screenHeightPX;
		loginTopImageView.setLayoutParams(layoutParams);
		
		// 增加点击事件，隐藏键盘；
		loginTopImageView.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
						ChangePwdActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
	}
	
	private void setParentID(){
		SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
		this.parentID = pre.getString("user_view_show_id", "");
	}
	
	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(ChangePwdActivity.this, toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}

	/**
	 * 验证用户帐号密码是否正确，如果正确则修改成新密码的方法
	 * 
	 * @param account
	 * @param old_password
	 */
	public void validateAndChangeUserPass(String account, String old_password,
			String new_password) {
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, DataTable>() {

			private String account = null;
			private String old_password = null;
			private String new_password = null;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected DataTable doInBackground(Object... params) {		// 相当于登陆;			
				DataTable dt = null;
				// 方法名
				String methodName = "APP_userLogin";

				// 为类成员变量赋值，方便其他地方调用
				this.account = params[0].toString();
				this.old_password = params[1].toString();
				this.new_password = params[2].toString();

				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();		
				String enrolNum = params[0].toString().trim()+"#mobilekey";
				String password = params[1].toString().trim()+"mobilekey";
				
				try {
					Log.i("lvjie", "加密前：enrolNum="+enrolNum+"   password="+password);
					enrolNum = DesUtil.EncryptAsDoNet(enrolNum, "Admin310");
					password = MD5.getMD5(password);
					password = password.toUpperCase();
					Log.i("lvjie", "加密后：enrolNum="+enrolNum+"   password="+password);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				paramsMap.put("enrolNum", enrolNum);
				paramsMap.put("password", password);
				
				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 从本地获取学校URL,并设置好
				SharedPreferences pre = getSharedPreferences("TTQAndroid",
						MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				if (schoolURL == null) {// 如果没有值，直接返回空值
					return null;
				}
				getdatatool.setURL(schoolURL);

				try {
					dt = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					Log.i("lvjie", "用户验证出错了....");
					e.printStackTrace();
				}
				return dt;
			}

			protected void onPostExecute(DataTable result) {
				if (result != null) {
					String login_flag = null;
					try {
						login_flag = result.getCell(0, "loginResult").toString();
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// 关闭刷新；
					refreshView.dismiss();
					if (login_flag.equals("true") == true) {// 帐号密码正确
						ChangePwdActivity.this.changeParentPassWord(
								UserInfo.getInstance().studentID, this.new_password);

					} else {// 帐号密码不正确
						showToast("原密码不正确!");                   
					}
				} else {
					// 关闭刷新；
					refreshView.dismiss();
					// 没有数据,可能是学校服务器地址需要更新了（重新改变了？），网络没有连接
					showToast("无法获取数据，请检查输入和网络连接！");					
				}
				
			}
		}.execute(account, old_password, new_password);
	}

	/**
	 * 修改家长的密码的方法
	 * 
	 */
	public void changeParentPassWord(String user_id, String new_pass) {
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, String>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected String doInBackground(Object... params) {
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				String change_flag = "false";
				// 方法名
				String methodName = "pub_changeParentPass_APP";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String studentID = params[0].toString().trim()+"#mobilekey";
				String newPass = params[1].toString().trim()+"#mobilekey";
							
				try {
					Log.i("lvjie", "加密前：studentID="+studentID+"  newPass="+newPass);
					studentID = DesUtil.EncryptAsDoNet(studentID, "Admin310");
					newPass = DesUtil.EncryptAsDoNet(newPass, "Admin310");
					Log.i("lvjie", "加密后：studentID="+studentID+"  newPass="+newPass);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				paramsMap.put("studentID", studentID);
				paramsMap.put("newPass", newPass);
				ChangePwdActivity.this.newPwd = params[1].toString();
				paramsMap.put("TokenID", tokenID);
				Log.i("lvjie", "newPwd="+ChangePwdActivity.this.newPwd);
				
				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 从本地获取学校URL,并设置好
				SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				if (schoolURL == null) {// 如果没有值，直接返回空值
					return null;
				}
				getdatatool.setURL(schoolURL);
				try {
					change_flag = getdatatool.getDataAsString(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("lvjie", "getdatatool.getDataAsString(methodName, paramsMap)...出错了。。。");
					e.printStackTrace();
				}
				return change_flag;
			}

			protected void onPostExecute(String change_flag) {
				if (change_flag != null) {
					if (change_flag.equals("true")) {// 修改密码成功
						showToast("修改密码成功!");
						initNativeData();
						ChangePwdActivity.this.finish();
					} else {
						Log.i("lvjie", "1-->修改密码失败，请检查网络！");
						showToast("修改密码失败，请检查网络！");
					}
				}else {
					Log.i("lvjie", "2-->修改密码失败，请检查网络！");
					showToast("修改密码失败，请检查网络！");
				}
				// 关闭刷新；
				refreshView.dismiss();

			};
		}.execute(user_id, new_pass);
	}
	
	// 密码修改成功， 需要修改保存在本地的密码和当前内存中的密码；
	public void initNativeData(){
		UserCurrentState.getInstance().userPwd = this.newPwd;
		Log.i("lvjie", "修改密码保存在本地："+this.newPwd);
		SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
		SharedPreferences.Editor pre_edit = pre.edit();
		pre_edit.putString("user_pwd", this.newPwd);	// lj添加；用户的密码保存到本地；
		pre_edit.commit();
	}
}
