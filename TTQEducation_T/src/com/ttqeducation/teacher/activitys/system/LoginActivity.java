package com.ttqeducation.teacher.activitys.system;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.beans.ClassUnReadMsgInfo;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.TeacherInfo;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.tools.DesUtil;
import com.ttqeducation.teacher.tools.GeneralTools;
import com.ttqeducation.teacher.tools.MD5;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 登录 需要做的事情： 1.用户填写帐号密码，连接对应学校Web服务，验证帐号密码是否正确 2.如果通过学校Web服务地址获取不到数据：
 * （1）重新获取对应学校的Web服务地址，如果还不行，证明系统有误，堂堂清数据库的地址出错（如果是空值则说明该学校已经被屏蔽，无服务）
 * 
 * @author 王勤为
 * 
 */
public class LoginActivity extends Activity {

	// 堂堂清Web服务的地址 "http://192.168.137.18:8089/ManageService.asmx";
//	public String COMPANY_SERVICE_URL = null;

	// 学校Web服务的地址 "http://192.168.137.1:8888/ManageService.asmx"
	public String SCHOOL_SERVICE_URL = null;

	// 用户的帐号
	private String account = null;

	private AutoCompleteTextView userIDEditText = null; // 用户账号；
	private EditText pwdEditText = null; // 用户密码；
	private Button loginButton = null; // 登陆按钮；
	private Button backInputButton = null; // 返回输入按钮；
	private ImageView loginTopImageView = null;		// 登录界面顶部的图片；

	private String[] userIDs = null; // 保存教师正确输入的用户名,用来提示；
		
	private RefreshView refreshView = null;
	
	private String teacherPwd = null;		// 临时保存一下用户密码；

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		this.initView();
	}

	public void initView() {
		
		SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
		// 从本地获取学校URL
		String schoolURL = pre.getString("school_service_url", null);
		
		SharedPreferences.Editor pre_edit = pre.edit();
		// 默认为false，即没有登陆成功，下次就直接进入登陆界面；
		pre_edit.putBoolean("isLoginSucceed", false);
		pre_edit.commit();

		

		if (schoolURL == null) {
			// 如果从本地没有获取到数据，则说明出错了，应该重新进入选择代码界面走一遍流程

		}
		this.SCHOOL_SERVICE_URL = schoolURL;
		System.out.println("那边传过来的学校WS地址" + schoolURL);

		readUserIDFromNative(); // 从本地读取用户账户；
		this.userIDEditText = (AutoCompleteTextView) super
				.findViewById(R.id.edit_account);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, this.userIDs);
		this.userIDEditText.setAdapter(adapter);

		this.pwdEditText = (EditText) super.findViewById(R.id.edit_password);
		this.loginButton = (Button) super.findViewById(R.id.button_login);
		this.backInputButton = (Button) super
				.findViewById(R.id.button_backTochooseSchool);

		// 登陆；
		this.loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 获取输入的帐号,密码
				String account = userIDEditText.getText().toString().trim();
				String password = pwdEditText.getText().toString().trim();

				if (account.equals("")) {
					showToast("请输入账号!");
				} else if (password.equals("")) {
					showToast("请输入密码!");
				} else {
					
					if(!GeneralTools.getInstance().isOpenNetWork1(LoginActivity.this)){
						showToast("未连接到互联网，请检查网络配置!");
					}else {
						// 调用获取对应学校Web服务地址的方法
						LoginActivity.this.teacherPwd = password;
						LoginActivity.this.validateUser(account, password);
					}
					
				}
			}
		});

		// 返回上一个界面；
		this.backInputButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this,
						ChooseSchoolActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
			}
		});
		
		// 动态设置登录界面顶部的图片；
		this.loginTopImageView = (ImageView) super.findViewById(R.id.loginTop_img);
		// 动态设置界面图片大小；
		DisplayMetrics metrics = getResources().getDisplayMetrics(); // 用来获取屏幕的分辨率；
		int screenWidthPX = metrics.widthPixels; 
		int screenHeightPX = screenWidthPX*823/1081;
		LayoutParams layoutParams = this.loginTopImageView.getLayoutParams();		
		layoutParams.width = screenWidthPX;
		layoutParams.height = screenHeightPX;
		this.loginTopImageView.setLayoutParams(layoutParams);
		
		// 增加点击事件，隐藏键盘；
		this.loginTopImageView.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
						LoginActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// 点击其他地方，让键盘消失；
		((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
				LoginActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		return super.onTouchEvent(event);
	}
	
	/**
	 * 验证用户登录的方法
	 * 
	 * @param account
	 * @param password
	 */
	public void validateUser(String account, String password) {
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

			// 保存帐号进入成员变量
			LoginActivity.this.account = params[0].toString();

			// 方法名
			String methodName = "APP_teacherLogin";
			// 存放参数的map
			Map<String, String> paramsMap = new HashMap<String, String>();		
			String teacherID = params[0].toString().trim()+"#mobilekey";
			String password = params[1].toString().trim()+"mobilekey";
			try {
				Log.i("lvjie", "加密前：teacherID="+teacherID+"   password="+password);
				teacherID = DesUtil.EncryptAsDoNet(teacherID, "Admin310");
				password = MD5.getMD5(password);
				password = password.toUpperCase();
				Log.i("lvjie", "加密后：teacherID="+teacherID+"   password="+password);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			paramsMap.put("teacherID", teacherID);
			paramsMap.put("password", password);
			
			// 获取数据
			GetDataByWS getDataTool = GetDataByWS.getInstance();
			getDataTool.setURL(LoginActivity.this.SCHOOL_SERVICE_URL);
			Log.i("lvjie", "SCHOOL_SERVICE_URL="+LoginActivity.this.SCHOOL_SERVICE_URL);
			try {
				dt = getDataTool.getDataAsTable(methodName, paramsMap);
			} catch (IOException e) {
				System.out.println("访问WS失败，可能是地址或参数错误,或网络没有连接"
						+ e.getMessage());

				// 更新学校URL地址// 从本地获取学校代码
				SharedPreferences pre = getSharedPreferences("TTQAndroid",
						MODE_PRIVATE);
				String schoolCode = pre.getString("schoolCode", null);
				GeneralTools.getInstance().UpdateSchoolWSURL(schoolCode);
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (dataTableWrongException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return dt;
		}

		protected void onPostExecute(DataTable result) {
					
			if (result != null) {
				String loginFlag = "";
				String teacherName= "";
				String execTeacherID = "";
				String classIDs = "";
				try {
					loginFlag = result.getCell(0, "loginResult").toString();
					if(loginFlag.equals("true")){
						teacherName = result.getCell(0, "teacherName").toString().trim();
//						classIDs = result.getCell(0, "classIDs").toString().trim();
						execTeacherID = result.getCell(0, "teacherID").toString().trim();
						Log.i("lvjie", "loginFlag="+loginFlag+"  teacherName="+teacherName+"  classIDs="+classIDs);
					}
					
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (loginFlag.equals("true") == true) {// 帐号密码正确
					
					// 保存教师的帐号											
					SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
					SharedPreferences.Editor pre_edit = pre.edit();
					pre_edit.putBoolean("isLoginSucceed", true);
					pre_edit.putString("teacherID", LoginActivity.this.account);
					pre_edit.putString("execTeacherID", execTeacherID);
					pre_edit.putString("teacherName", teacherName);
					pre_edit.putString("classIDs", classIDs);
					pre_edit.putString("teacherPwd", LoginActivity.this.teacherPwd);
					pre_edit.commit();					
					
					// 保存信息到单例中，方便之后的界面需要；
					TeacherInfo.getInstance().teacherID = LoginActivity.this.account;
					TeacherInfo.getInstance().execTeacherID = execTeacherID;
					TeacherInfo.getInstance().teacherName = teacherName;
//					TeacherInfo.getInstance().classIDs = classIDs.split(",");
					TeacherInfo.getInstance().execTeacherPwd = LoginActivity.this.teacherPwd;
	
					saveUserIDInNative();
					
					getTeacherUnreadMesgByWS(TeacherInfo.getInstance().execTeacherID);
	
				} else {// 帐号密码不正确
						// 清空
					
					// 关闭刷新；
					refreshView.dismiss();
					
					EditText m = (EditText) findViewById(R.id.edit_password);
					m.setText("");
					Toast toast = Toast.makeText(LoginActivity.this,
							"帐号密码不正确，请重新输入！", Toast.LENGTH_LONG);
					toast.show();
				}
			} else {
				
					// 关闭刷新；
					refreshView.dismiss();
					
					// 没有数据,可能是学校服务器地址需要更新了（重新改变了？），网络没有连接
					EditText m = (EditText) findViewById(R.id.edit_password);
					m.setText("");
					Toast toast = Toast.makeText(LoginActivity.this,
							"帐号密码不正确，请重新输入！", Toast.LENGTH_SHORT);
					toast.show();

					}
				
			}
	
		}.execute(account, password);
	}
		
	// 把老师输入过的正确的用户名保存在本地；方便下次输入自动提示；
	private void saveUserIDInNative() {
		SharedPreferences pre = getSharedPreferences("TTQAndroid_Reminder",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor pre_edit = pre.edit();
		String saveUserID = "";
		// 判断给用户名是否已经保存在本地；
		int count = this.userIDs.length;
		boolean flag = true; // 是否把界面输入的用户名保存在本地；
		Log.i("lvjie", "准备保存在本地的用户ID数量为：count=" + count);
		if (count >= 1) {
			for (int i = 0; i < count; i++) {
				if (this.userIDs[i].equals(this.account)) { // 表示存在；
					flag = false;
				}
				saveUserID += this.userIDs[i] + ",";
			}
		}

		if (flag) {
			saveUserID += this.account;
		}
		Log.i("lvjie", "准备保存在本地的用户ID为：saveUserID=" + saveUserID);
		pre_edit.putString("userID", saveUserID);
		pre_edit.commit();
	}

	// 把保存在本地的学校编码读取出来，保存在schoolCodes中,用于提示；
	private void readUserIDFromNative() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				"TTQAndroid_Reminder", Activity.MODE_PRIVATE);
		String userIDs = sharedPreferences.getString("userID", "");

		this.userIDs = userIDs.split(",");
		Log.i("lvjie", "保存在本地的用户ID有：userIDs=" + userIDs
				+ "  this.userIDs.length=" + this.userIDs.length);
	}

	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(LoginActivity.this, toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
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
				Log.i("lvjie", "明文参数：teacherID="+teacherID+"  tokenID="+DesUtil.tokenID);
				paramsMap.put("teacherID", teacherID);
				paramsMap.put("TokenID", tokenID);
				Log.i("lvjie", "密文参数：teacherID="+teacherID+"  tokenID="+tokenID);
				// 获取数据
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				getDataTool.setURL(LoginActivity.this.SCHOOL_SERVICE_URL);
				try {
					dt = getDataTool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					Log.i("error", "参数：teacherID="+teacherID+"  tokenID="+tokenID);
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
							Log.i("lvjie", "LoginActivity-->getTeacherUnreadMesgByWS()..."+classUnReadMsgInfo);
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}else {
					Log.i("error", "LoginActivity-->getTeacherUnreadMesgByWS()...从服务端获取班级未读消息失败...");
				}
				
				// 关闭刷新；
				refreshView.dismiss();
				// 进入主界面
				Intent mainIntent = new Intent(LoginActivity.this,
						MainActivity.class);
				LoginActivity.this.startActivity(mainIntent);
				LoginActivity.this.finish();
				
			};
			
		}.execute(teacherID);
	}
	
}
