package com.ttqeducation.activitys.system;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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

import com.ttqeducation.R;
import com.ttqeducation.activitys.study.StudyFragment;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.UserCurrentState;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DateUtil;
import com.ttqeducation.tools.DesUtil;
import com.ttqeducation.tools.GeneralTools;
import com.ttqeducation.tools.MD5;

/**
 * 登录 需要做的事情： 1.用户填写帐号密码，连接对应学校Web服务，验证帐号密码是否正确 2.如果通过学校Web服务地址获取不到数据：
 * （1）重新获取对应学校的Web服务地址，如果还不行，证明系统有误，堂堂清数据库的地址出错（如果是空值则说明该学校已经被屏蔽，无服务）
 * 
 * @author 王勤为
 * 
 */
public class LoginActivity extends Activity {

	// 堂堂清Web服务的地址 "http://192.168.137.18:8089/ManageService.asmx";
	public String COMPANY_SERVICE_URL = null;

	// 学校Web服务的地址 "http://192.168.137.1:8888/ManageService.asmx"
	public String SCHOOL_SERVICE_URL = null;

	// 用户的帐号
	private String account = null;

	private AutoCompleteTextView userIDEditText = null; // 用户账号；
	private EditText pwdEditText = null; // 用户密码；
	private Button loginButton = null; // 登陆按钮；
	private Button backInputButton = null; // 返回输入按钮；
	private ImageView loginTopImageView = null;		// 登录界面顶部的图片；

	private String[] userIDs = null; // 保存家长正确输入的用户名,用来提示；
	
	private int[] moduleExpenseInfos = {2,2,2,2,2,2};		// 保存哪些模块需要收费及免费(一共为六个模块)；
	private int[] moduleUse = {1,1,1,1,1,1};				// 存放用户哪些模块可以使用；(默认都可以使用);
	
	private RefreshView refreshView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		this.initView();
	}

	public void initView() {

		// 获取堂堂清公司的服务地址
		Resources res = getResources();
		this.COMPANY_SERVICE_URL = (String) res
				.getText(R.string.company_service_url);
		// 从本地获取学校URL
		SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
		String schoolURL = pre.getString("school_service_url", null);

		if (schoolURL == null) {
			// 如果从本地没有获取到数据，则说明出错了，应该重新进入选择代码界面走一遍流程

		}
		this.SCHOOL_SERVICE_URL = schoolURL;
		System.out.println("那边传过来的学校WS地址" + schoolURL);

		readSchoolCodeFromNative(); // 从本地读取用户账户；
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
		this.loginTopImageView = (ImageView) super.findViewById(R.id.ivLoginTop);
		// 动态设置界面图片大小；
		DisplayMetrics metrics = getResources().getDisplayMetrics(); // 用来获取屏幕的分辨率；
		int screenWidthPX = metrics.widthPixels;
		LayoutParams layoutParams = this.loginTopImageView.getLayoutParams();		
		layoutParams.width = (int)((screenWidthPX / (float)1080) * 552);
		layoutParams.height = (int)((screenWidthPX / (float)1080) * 291);
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
	//			String methodName = "pub_parent_login";
				String methodName = "APP_userLogin";
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
				UserCurrentState.getInstance().userPwd = params[1].toString();		// 保存密码到全局；
				// 获取数据
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				getDataTool.setURL(LoginActivity.this.SCHOOL_SERVICE_URL);
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
					String login_flag = "";
					String user_id = "";
					try {
						login_flag = result.getCell(0, "loginResult").toString();
						user_id = result.getCell(0, "userID").toString();
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.i("lvjie", "login_flag="+login_flag+"  user_id="+user_id);
					if (login_flag.equals("true") == true) {// 帐号密码正确
						
						// 保存家长的帐号											
						SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
						SharedPreferences.Editor pre_edit = pre.edit();
						pre_edit.putString("user_account",user_id);		// 以后调用服务端方法都是该用户编号；
						pre_edit.putString("user_view_show_id", LoginActivity.this.account);	// lj添加；
						pre_edit.putString("user_pwd", UserCurrentState.getInstance().userPwd);	// lj添加；用户的密码保存到本地；
						// 存入第一次使用标识和最后登录日期
						pre_edit.putBoolean("ifFirstUse", true);
						Date currentDate = new Date();
						pre_edit.putString("lastLoginDate", DateUtil.convertDateToString("yyyy-MM-dd", currentDate));
						pre_edit.commit();
		
						saveSchoolCodeInNative();
						
	//					getModuleExpenseInfoByWS();		// 获取模块信息；
						UserCurrentState.getInstance().userID = user_id;
						String userID = UserCurrentState.getInstance().userID+"|"+UserCurrentState.getInstance().userPwd;
						String tokenID = DesUtil.getTokenIDStr(UserCurrentState.getInstance().userID, 2);
						try {
							Log.i("lvjie", "加密前:  userID="+userID+"  tokenID="+tokenID);
							userID = DesUtil.EncryptAsDoNet(userID, "Admin310");
							tokenID = DesUtil.EncryptAsDoNet(tokenID, "Admin407");
							Log.i("lvjie", "加密后:  userID="+userID+"  tokenID="+tokenID);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						addTokenIDToWS(userID, tokenID);
		
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
						Log.i("error", "登陆界面-->用户登陆出错...");
						// 没有数据,可能是学校服务器地址需要更新了（重新改变了？），网络没有连接
						Toast toast = Toast.makeText(LoginActivity.this,
								"帐号密码不正确，请重新输入！", Toast.LENGTH_SHORT);
						toast.show();
	
				}
				
			}
		
		}.execute(account, password);
	}
	
 
	/**
	 * 向公司端数据库中添加tokenID;
	 * @param userID = 用户编号+"|"+用户密码   进行 key="Admin310" 加密
	 * @param tokenID = getTokenIDStr()   进行 key="Admin407"  加密 
	 */
	public void addTokenIDToWS(String userID, String tokenID){
		new AsyncTask<Object, Object, DataTable>(){
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}
			
			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DataTable dt_token = null;
				
				return dt_token;
			}
				
			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
				String schoolCode = pre.getString("schoolCode", "");				
				getModuleExpenseInfoByWS(schoolCode);
			}
			
		}.execute(userID, tokenID);
	}
	
	// 从服务端获取哪些模块需要收费；
	//Tuimao onPostExecute里面获得的东西被注释，数据库修改了
	public void getModuleExpenseInfoByWS(String schoolCode){
		
		new AsyncTask<Object, Object, DataTable>(){
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
//				refreshView.show();
			}
			
			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DataTable dt_moduleExpenseInfo = new DataTable();
				
				// 方法名
				String methodName = "APP_getModuleFlag";
	
				// 获取数据
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String schoolCode= params[0].toString();
				String tokenID = DesUtil.getDesTokenID(UserCurrentState.getInstance().userID, "Admin203", 1);
				Log.i("lvjie", "传递参数：schoolCode="+schoolCode+"   tokenID="+tokenID);
				paramsMap.put("schoolCode", schoolCode);
				paramsMap.put("TokenID", tokenID);
				// 获取堂堂清公司的服务地址
				Resources res = getResources();
				String companyURL = (String) res.getText(R.string.company_service_url);
				getDataTool.setURL(companyURL);
				try {
					dt_moduleExpenseInfo = getDataTool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "1-->getModuleExpenseInfoByWS(String schoolCode)..出错了。。。");
					e.printStackTrace();
				}
				return dt_moduleExpenseInfo;
			}
				
			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				if (result != null) {
//					int count = result.getRowCount();
//					for (int i = 0; i < count; i++) {
//						try {
//							LoginActivity.this.moduleExpenseInfos[0] = Integer.parseInt(result.getCell(i, "module_1"));
//							LoginActivity.this.moduleExpenseInfos[1] = Integer.parseInt(result.getCell(i, "module_2"));
//							LoginActivity.this.moduleExpenseInfos[2] = Integer.parseInt(result.getCell(i, "module_3"));
//							LoginActivity.this.moduleExpenseInfos[3] = Integer.parseInt(result.getCell(i, "module_4"));
//							LoginActivity.this.moduleExpenseInfos[4] = Integer.parseInt(result.getCell(i, "module_5"));
//							LoginActivity.this.moduleExpenseInfos[5] = Integer.parseInt(result.getCell(i, "module_6"));
//							
//							Log.i("lvjie", "moduleExpenseInfos="+LoginActivity.this.moduleExpenseInfos[0]+" "+
//							" "+LoginActivity.this.moduleExpenseInfos[1]+" "+LoginActivity.this.moduleExpenseInfos[2]+
//							" "+LoginActivity.this.moduleExpenseInfos[3]+" "+LoginActivity.this.moduleExpenseInfos[4]+
//							" "+LoginActivity.this.moduleExpenseInfos[5]);
//	
//						} catch (dataTableWrongException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
				}else {
					Log.i("Tuimao", "----->LoginActivity获得result为null");
				}
				setModuleShowByExpenseInfos();
				// 关闭刷新；
				refreshView.dismiss();
								
				// 进入主界面
				Intent toMainActivity = new Intent(LoginActivity.this,
						MainActivity.class);
				toMainActivity.putExtra("moduleUse", LoginActivity.this.moduleUse);
				LoginActivity.this.startActivity(toMainActivity);
				LoginActivity.this.finish();
			}
			
		}.execute(schoolCode);
	}
	
	// 设置单例中的截止天数；用来判断模块的可用性；
	private void setDeadLineDays(){
		Date date1 = new Date();
		Date date2;
		try {
			Log.i("lvjie", "登陆界面-->setDeadLineDays()...deadline="+UserInfo.getInstance().deadline);
			
			if(UserInfo.getInstance().deadline == null){		// 没有截止日期；
				UserInfo.getInstance().deadLineDays = -100;
				return;
			}
			date2 = DateUtil.convertStringToDate("yyyy-MM-dd", UserInfo.getInstance().deadline);
			
			UserInfo.getInstance().deadLineDays = DateUtil.daysBetween(date1, date2);			// 获取两个日期的相隔天数；
			Log.i("lvjie", "登陆界面-->setDeadLineDays():  date1="+date1+"   date2="+date2+"   k="+UserInfo.getInstance().deadLineDays);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	
	// 根据模块收费情况以及用户缴费情况，判断模块是否可用；
	public void setModuleShowByExpenseInfos(){
		for(int i=0; i<this.moduleExpenseInfos.length; i++){
			if(this.moduleExpenseInfos[i] == 0){
				UserInfo.getInstance().flag = 0;		// 当有一个模块收费，则设置表示为0；表示APP是需要收费的；
			}
		}
		
		setDeadLineDays();
		if(UserInfo.getInstance().deadLineDays >= 0){	// 没有到期，表示各个模块都可以用；
			
		}else {			// 到了截止时间，收费模块不可用；0表示收费，1表示免费；
			if(this.moduleExpenseInfos[0] == 0){
				this.moduleUse[0] = 0;
			}
			if(this.moduleExpenseInfos[1] == 0){
				this.moduleUse[1] = 0;
			}
			if(this.moduleExpenseInfos[2] == 0){
				this.moduleUse[2] = 0;
			}
			if(this.moduleExpenseInfos[3] == 0){
				this.moduleUse[3] = 0;
			}
			if(this.moduleExpenseInfos[4] == 0){
				this.moduleUse[4] = 0;
			}
			if(this.moduleExpenseInfos[5] == 0){
				this.moduleUse[5] = 0;
			}
			
			UserInfo.getInstance().moduleUse = this.moduleUse;
		}
		Log.i("lvjie", "登陆界面：deadline="+UserInfo.getInstance().deadline+"   moduleUse="+Arrays.toString(UserInfo.getInstance().moduleUse));
	
	}
		
	
	// 把家长输入过的正确的用户名保存在本地；方便下次输入自动提示；
	private void saveSchoolCodeInNative() {
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
	private void readSchoolCodeFromNative() {
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
}
