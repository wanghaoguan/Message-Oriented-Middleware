package com.ttqeducation.teacher.activitys.system;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.tools.GeneralTools;
import com.ttqeducation.teacher.tools.ScreenUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 选择学校代码 需要做的事情：
 * 
 * 1.用户填写学校代码，连接堂堂清公司web服务，获取该学校的web服务地址 2.进入登陆界面，保存学校地址在本地
 * 
 * @author 王勤为
 * 
 *         吕杰 已经做好了 界面的初始化，学校代码输入为空的判断 和 此界面到下一个界面间的界面刷新过程； 增加了让子线程睡眠2秒，以后需要去掉；
 */
public class ChooseSchoolActivity extends Activity {

	// 堂堂清Web服务的地址 "http://192.168.137.18:8089/ManageService.asmx";
	public String COMPANY_SERVICE_URL = null;
	// 学校Web服务的地址 "http://192.168.137.1:8888/ManageService.asmx"
	public String SCHOOL_SERVICE_URL = null;
	private String schoolCode = null;

	private AutoCompleteTextView schoolCodeEditText = null; // 学校编码；
	private TextView yesTextView = null; // 确定按钮；
	private RefreshView refreshView = null;
	private String[] schoolCodes = null;		// 保存家长正确输入的学校编号,用来提示；

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_school);
		initView();
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
	}

	public void initView() {
		
		readSchoolCodeFromNative();		// 从本地读取学校编码；
		// 提示输入框；
		this.schoolCodeEditText = (AutoCompleteTextView) super.findViewById(R.id.edit_schoolCode);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, this.schoolCodes);
		this.schoolCodeEditText.setAdapter(adapter);
		
		this.yesTextView = (TextView) super.findViewById(R.id.yes);

		// 获取堂堂清公司的服务地址
		Resources res = getResources();
		this.COMPANY_SERVICE_URL = (String) res
				.getText(R.string.company_service_url);

		this.yesTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ChooseSchoolActivity.this.schoolCode = schoolCodeEditText
						.getText().toString().trim();
				if (ChooseSchoolActivity.this.schoolCode.equals("")) {
					showToast("请输入学校代码！");
				} else {
					
					// 调用获取对应学校Web服务地址的方法
					ChooseSchoolActivity.this.getSchoolWSURL(schoolCode);
				}

			}
		});
	}

	/**
	 * 获得对应学校Web服务的方法
	 * 
	 */
	public void getSchoolWSURL(String schoolCode) {
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
				DataTable dt_school = null;
				String school_url = null;

				try {
					Thread.sleep(2000); // 测试代码，之后需删掉
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// 方法名
				String methodName = "pub_getSchoolWSURL";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("schoolCode", params[0].toString());
				// 获取数据
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				getDataTool.setURL(ChooseSchoolActivity.this.COMPANY_SERVICE_URL);
				Log.i("lvjie", "COMPANY_SERVICE_URL="+COMPANY_SERVICE_URL);
				try {
					dt_school = getDataTool.getDataAsTable(methodName,
							paramsMap);
				} catch (Exception e) {
					// 网络访问有异常
					System.out.println("访问WS失败，可能是地址或参数错误" + e.getMessage());
					e.printStackTrace();
				}
				return dt_school;
			}

			protected void onPostExecute(DataTable result) {
				if (result != null) {
					
					String school_url = "";
					String schoolWebSite = "";
					String pushingServiceIP = "";
					String pushingServicePort = "";
					try {
						school_url = result.getCell(0, "schoolServiceURL");
						schoolWebSite = result.getCell(0, "schoolWebSite");
						pushingServiceIP = result.getCell(0, "pushingServiceIP").trim();
						pushingServicePort = result.getCell(0, "pushingServicePort").trim();
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// 保存学校的WebService地址 以及学校代码以及付费链接；
					SharedPreferences pre = getSharedPreferences("TTQAndroid",
							MODE_PRIVATE);
					SharedPreferences.Editor pre_edit = pre.edit();
					pre_edit.putString("school_service_url", school_url);
					pre_edit.putString("schoolCode", ChooseSchoolActivity.this.schoolCode);
					pre_edit.putString("schoolWebSite", schoolWebSite);
					pre_edit.putString("pushingServiceIP", pushingServiceIP);
					pre_edit.putString("pushingServicePort", pushingServicePort);
					pre_edit.commit();
					
					Log.i("lvjie", "school_service_url="+school_url+"  schoolWebSite"+schoolWebSite);
					
					// 吕杰添加;2015-03-25
					saveSchoolCodeInNative(ChooseSchoolActivity.this.schoolCode);

					// 跳转到登陆界面,并传递参数
					Intent toLoginIntent = new Intent(
							ChooseSchoolActivity.this, LoginActivity.class);

					toLoginIntent.putExtra("SCHOOL_SERVICE_URL", school_url);
					ChooseSchoolActivity.this.startActivity(toLoginIntent);
					ChooseSchoolActivity.this.finish();
					// 关闭刷新；
					refreshView.dismiss();

				} else {

					// 关闭刷新；
					refreshView.dismiss();
					
					if(!GeneralTools.getInstance().isOpenNetWork1(ChooseSchoolActivity.this)){
						showToast("未连接到互联网，请检查网络配置!");
					}else {
						// 学校地址为空
						Toast toast = Toast.makeText(ChooseSchoolActivity.this,
								"无法获取对应学校数据，请检查学校代码是否正确！", Toast.LENGTH_SHORT);
						toast.show();
					}

				}
			}
		}.execute(schoolCode);
	}
	
	// 把家长输入过的正确的学校编码保存在本地；方便下次输入自动提示；
	private void saveSchoolCodeInNative(String schoolCode){
		SharedPreferences pre = getSharedPreferences("TTQAndroid_Reminder",Activity.MODE_PRIVATE);
		SharedPreferences.Editor pre_edit = pre.edit();
		String saveCode = "";
		// 判断给学校编码是否已经保存在本地；
		int count = this.schoolCodes.length;
		boolean flag = true;		// 是否把界面输入的学校编号保存在本地；
		Log.i("lvjie", "准备保存在本地的学校编码数量为：count="+count);
		if(count >= 1){
			for(int i=0;i<count;i++){
				if(this.schoolCodes[i].equals(this.schoolCode)){	// 表示存在；
					flag = false;
				}
				saveCode +=this.schoolCodes[i]+",";
			}
		}
				
		if(flag){
			saveCode +=this.schoolCode;
		}
		Log.i("lvjie", "准备保存在本地的学校编码为：saveCode="+saveCode);
		pre_edit.putString("schoolCode",saveCode);
		pre_edit.commit();
	}
	
	// 把保存在本地的学校编码读取出来，保存在schoolCodes中,用于提示；
	private void readSchoolCodeFromNative(){
		SharedPreferences sharedPreferences = getSharedPreferences("TTQAndroid_Reminder",Activity.MODE_PRIVATE);
		String schoolCodes = sharedPreferences.getString("schoolCode", "");
		
		this.schoolCodes = schoolCodes.split(",");
		Log.i("lvjie", "保存在本地的学校编码有：schoolCodes="+schoolCodes+"  this.schoolCodes.length="+this.schoolCodes.length);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// 点击其他地方，让键盘消失；
		((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
				ChooseSchoolActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		return super.onTouchEvent(event);
	}
	
	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(ChooseSchoolActivity.this, toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}

}
