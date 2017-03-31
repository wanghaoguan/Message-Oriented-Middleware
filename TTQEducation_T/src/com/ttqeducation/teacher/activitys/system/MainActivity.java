package com.ttqeducation.teacher.activitys.system;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.activitys.mainItem.MainItemFragment;
import com.ttqeducation.teacher.activitys.message.MessageFragment;
import com.ttqeducation.teacher.activitys.msgMenu.MsgMenuFragment;
import com.ttqeducation.teacher.activitys.notice.NoticeFragment;
import com.ttqeducation.teacher.activitys.others.OthersFragment;
import com.ttqeducation.teacher.activitys.teach.ChooseClassActivity;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.TeacherAthority;
import com.ttqeducation.teacher.beans.TeacherInfo;

import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.network.PushService;
import com.ttqeducation.teacher.tools.DateUtil;
import com.ttqeducation.teacher.tools.DensityUtils;
import com.ttqeducation.teacher.tools.DesUtil;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener{

	private MessageFragment messageFragment ;		    // 用于展示消息的Fragment，现在不用了
	private MsgMenuFragment noticeFragment;				// 用于展示通知公告的Fragment,现在为展示各种通知消息的Fragment
	private OthersFragment othersFragment;				// 用于展示其他的Fragment
	private MainItemFragment mainItemFragment;          // 用于展示主界面的Fragment
	private FragmentManager fragmentManager;  			// 用于对Fragment进行管理
	private LinearLayout llChangeClass;                 // 用于切换班级
	private static final int GET_CHOOSED_CLASSNAME=1;

	/**********     界面底部部分        ***********/
	private View messageLayout;		// 服务项目界面布局
	private View noticeLayout;		// 消息界面布局
	private View othersLayout;		// 其他界面布局

	private ImageView messageImage;		// 在Tab布局上显示消息图标的控件
	private ImageView noticeImage;   	// 在Tab布局上显示学习图标的控件
	private ImageView othersImage;		// 在Tab布局上显示其他图标的控件
	
	private TextView messageText;		// 在Tab布局上显示消息标题的控件
	private TextView noticeText;			// 在Tab布局上显示学习标题的控件
	private TextView othersText;		// 在Tab布局上显示其他标题的控件

	private TextView titleTextView = null; // 标题栏的文字；
	private int viewFrom = 0;			// 2：表示来自中间件；提示账号在另一手机登陆；
	
	private Intent serverIntent = null;		// 后台服务；
	
	// 下线对话框；
	private Dialog offLineDialog = null; // 下线对话框
	RefreshView refreshView=null;
	String teacherID =null;
	String termID=null;
	private Map<String, TeacherAthority> teacherAthority = null; //存储教师权限，可以查看哪些科目
	String[] classes=null ;
	//String currentClass=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		this.serverIntent = new Intent(MainActivity.this,PushService.class);	// 初始化中间件服务；
		// 初始化布局元素
		this.initView();
		setLoginTimeToNative();
		getDataFromIntent();
		getData();
		
		fragmentManager = getFragmentManager();
		// 第一次启动时选中第0个tab
		setTabSelection(0);
		
		

		if(this.viewFrom == 2){		// 表示被挤下线；
			initOffLineDialog();
		}
	}
	
	private void getData() {
		// TODO Auto-generated method stub
		
		this.teacherID=TeacherInfo.getInstance().execTeacherID;
		this.termID=TeacherInfo.getInstance().getTermID();
		getTeacherAuthority(teacherID, termID);
	}
	
	private void getTeacherAuthority(String teacherID, String termID) { 
		// TODO Auto-generated method stub
		refreshView = new RefreshView(this, R.style.full_screen_dialog);
		teacherAthority = new HashMap<String, TeacherAthority>();
		//异步调用网络数据
		new AsyncTask<Object, Object, DataTable>(){

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				DataTable dt_result = new DataTable();
				
				// 方法名
				String methodName = "teach_getTeacherAuthority";
				
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				paramsMap.put("teacherID", params[0].toString());
				paramsMap.put("termID", params[1].toString());											
				paramsMap.put("TokenID", tokenID);
				
				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 从本地获取学校URL,数据由SharedPreferences存放;
				SharedPreferences pre = getSharedPreferences("TTQAndroid", Activity.MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);				
				if (schoolURL == null) {// 如果没有值
					return null;
				}
				getdatatool.setURL(schoolURL);
				try {
					dt_result = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "teach_getTeacherAuthority()...出错了。。。");
					e.printStackTrace();
				}
				
				return dt_result;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result != null){
					classes= new String[result.getRowCount()];
					try{
						for(int i=0;i<result.getRowCount();i++){
							/*teacherAthority.put(result.getCell(i, "className"), 
									new TeacherAthority(result.getCell(i, "classID"), result.getCell(i, "subjectNames")));*/
							String className = result.getCell(i, "className").trim();
							String classID = result.getCell(i, "classID").trim();
							String subjectNames=  result.getCell(i, "subjectNames").trim();
							String grade = result.getCell(i, "grade").trim();
							TeacherAthority ta= new TeacherAthority(classID, subjectNames, grade);
							teacherAthority.put(className, ta);
							classes[i]=result.getCell(i, "className").trim();
						}
						
						TeacherInfo.getInstance().setTeacherAthority(teacherAthority);
						
					}catch(dataTableWrongException e){
						e.printStackTrace();
					}
				}
				if(result!=null){					
					
					try {
						TeacherInfo.getInstance().setChoosedClass(result.getCell(0, "className").trim());
						//设置班级列表
						TeacherInfo.getInstance().setClasses(classes);
					} catch (dataTableWrongException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
					titleTextView.setText(TeacherInfo.getInstance().getCurrentClassName());
				}
				refreshView.dismiss();
			}
			
		}.execute(teacherID,termID);
		
	}

	private void getDataFromIntent(){
		this.viewFrom = getIntent().getIntExtra("viewfrom", 0);
		Log.i("lvjie", "MainActivity--> viewFrom="+this.viewFrom);
	}
	
	// 把用户进入系统的时间保存在本地文件，方便判断是否直接进入到主界面；
	public void setLoginTimeToNative(){

		String loginTime = DateUtil.convertDateToString("yyyy-MM-dd", new Date());
		SharedPreferences pre = getSharedPreferences(
				"TTQAndroid", MODE_PRIVATE);
		SharedPreferences.Editor pre_edit = pre.edit();
		pre_edit.putString("loginTime", loginTime);
		pre_edit.commit();
		
		Log.i("lvjie", "MainActivity--->loginTime="+loginTime);
	}
	
	public void initView(){
		// 记录用户状态， 表示用户进入系统了；
		TeacherInfo.getInstance().initSystem = 1;
		//切换班级按钮
		this.llChangeClass = (LinearLayout) findViewById(R.id.llChangeClass);
		this.llChangeClass.setOnClickListener(this);
		// 标题栏的文字；
		this.titleTextView = (TextView) super.findViewById(R.id.title_text);

		// 下面是界面底部的三个按钮部分；
		messageLayout = findViewById(R.id.message_layout);
		noticeLayout = findViewById(R.id.notice_layout);
		othersLayout = findViewById(R.id.others_layout);

		// 下面是界面底部三个按钮对应的图片部分;
		messageImage = (ImageView) findViewById(R.id.message_image);
		noticeImage = (ImageView) findViewById(R.id.notice_image);
		othersImage = (ImageView) findViewById(R.id.others_image);

		// 下面是界面底部文字描述部分；
		messageText = (TextView) findViewById(R.id.message_text);
		noticeText = (TextView) findViewById(R.id.notice_text);
		othersText = (TextView) findViewById(R.id.others_text);

		// 添加点击事件；
		messageLayout.setOnClickListener(this);
		noticeLayout.setOnClickListener(this);
		othersLayout.setOnClickListener(this);
		
		// 启用后台服务；
		if(this.viewFrom != 2){
			MainActivity.this.startService(MainActivity.this.serverIntent);	
		}
	}

	// 初始化下线对话框；
	public void initOffLineDialog() {
		if (this.offLineDialog == null) {
			View view = LayoutInflater.from(this).inflate(R.layout.dialog_offline, null);
			this.offLineDialog = new Dialog(this, R.style.alertdialog_style);
			this.offLineDialog.setContentView(view);
			this.offLineDialog.show();

			// 动态设置界面图片大小；
			DisplayMetrics metrics = getResources().getDisplayMetrics(); // 用来获取屏幕的分辨率；
			int screenWidthPX = metrics.widthPixels;
			int screenWidthDP = (int) DensityUtils.px2dp(this,
					screenWidthPX);

			// 设置图片的大小；
			int layoutWidthDP = (screenWidthDP * 4) / 5;
			int layoutWidthPX = DensityUtils.dp2px(this, layoutWidthDP);

			LayoutParams params = view.getLayoutParams();
			params.width = layoutWidthPX;
			view.setLayoutParams(params);

			// 添加点击事件
			view.findViewById(R.id.againLogin_textView).setOnClickListener(this);
			view.findViewById(R.id.exit_textView).setOnClickListener(this);

		} else {
			this.offLineDialog.show();
		}
	}
		
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.message_layout:
			// 当点击了学习tab时，选中第1个tab
			setTabSelection(0);
			break;
		case R.id.notice_layout:
			// 当点击了消息tab时，选中第2个tab
			setTabSelection(1);
			break;
		case R.id.others_layout:
			// 当点击了其他tab时，选中第3个tab
			setTabSelection(2);
			break;
		case R.id.againLogin_textView:
			// 账号在另一个设备上登陆，重新登陆     先关闭服务,重新开始启动界面；
			MainActivity.this.offLineDialog.dismiss();
			MainActivity.this.setRunBackgroundServer(false);
			Intent intent = new Intent(MainActivity.this, LaunchActivity.class);
			MainActivity.this.startActivity(intent);
			MainActivity.this.finish();
			break;
		case R.id.exit_textView:
			// 账号在另一个设备上登陆，退出
			MainActivity.this.offLineDialog.dismiss();
			MainActivity.this.setRunBackgroundServer(false);
			MainActivity.this.finish();
			break;
		case R.id.llChangeClass:
			Intent intent2 = new Intent(MainActivity.this, ChooseClassActivity.class);
			MainActivity.this.startActivityForResult(intent2, GET_CHOOSED_CLASSNAME);
			
			break;
		default:
			break;
		}
	}
	
	/**
	 * 根据传入的index参数来设置选中的tab页。
	 * 
	 * @param index
	 *            每个tab页对应的下标。0表示服务项目，1表示各种消息，2表示其他。
	 */
	private void setTabSelection(int index) {
		// 每次选中之前先清楚掉上次的选中状态
		clearSelection();
		// 开启一个Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);
		switch (index) {
		case 0:
			this.llChangeClass.setVisibility(View.VISIBLE);
			if(TeacherInfo.getInstance().getClassID()!=null)
	//		titleTextView.setText(TeacherInfo.getInstance().getCurrentClassName());
			titleTextView.setText("欢迎");// 修改标题栏的文字；
			// 当点击了学习tab时，改变控件的图片和文字颜色			
			messageImage.setBackgroundResource(R.drawable.item_pre_4);
			messageText.setTextColor(getResources().getColor(R.color.title_bgNew));
			if (mainItemFragment == null) {
				// 如果ContactsFragment为空，则创建一个并添加到界面上
				mainItemFragment = new MainItemFragment();
				transaction.add(R.id.content, mainItemFragment);
			} else {
				// 如果ContactsFragment不为空，则直接将它显示出来
				transaction.show(mainItemFragment);
			}
			break;
		case 1:
			this.llChangeClass.setVisibility(View.GONE);
			titleTextView.setText("消息"); // 修改标题栏的文字；
			// 当点击了消息tab时，改变控件的图片和文字颜色
			// messageImage.setImageResource(R.drawable.messages_pre);
			noticeImage.setBackgroundResource(R.drawable.message_pre_4);
			noticeText.setTextColor(getResources().getColor(R.color.title_bgNew));
			if (noticeFragment == null) {
				// 如果MessageFragment为空，则创建一个并添加到界面上
				noticeFragment = new MsgMenuFragment();
				transaction.add(R.id.content, noticeFragment);
			} else {
				// 如果MessageFragment不为空，则直接将它显示出来
				transaction.show(noticeFragment);
			}
			break;

		case 2:
			this.llChangeClass.setVisibility(View.GONE);
			titleTextView.setText("其他"); // 修改标题栏的文字；
			// 当点击了其他tab时，改变控件的图片和文字颜色
			// othersImage.setImageResource(R.drawable.others_pre);
			othersImage.setBackgroundResource(R.drawable.other_pre_4);
			othersText.setTextColor(getResources().getColor(R.color.title_bgNew));
			if (othersFragment == null) {
				// 如果NewsFragment为空，则创建一个并添加到界面上
				othersFragment = new OthersFragment();
				transaction.add(R.id.content, othersFragment);
			} else {
				// 如果NewsFragment不为空，则直接将它显示出来
				transaction.show(othersFragment);
			}
			break;
		default:
			break;
		}
		transaction.commit();
	}

	/**
	 * 清除掉所有的选中状态。
	 */
	private void clearSelection() {
		// messageImage.setImageResource(R.drawable.messages);
		messageImage.setBackgroundResource(R.drawable.item_4);
		messageText.setTextColor(getResources().getColor(R.color.textGray));
		// studyImage.setImageResource(R.drawable.study_dynamic);
		noticeImage.setBackgroundResource(R.drawable.message_4);
		noticeText.setTextColor(getResources().getColor(R.color.textGray));
		// othersImage.setImageResource(R.drawable.others);
		othersImage.setBackgroundResource(R.drawable.other_4);
		othersText.setTextColor(getResources().getColor(R.color.textGray));
	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 * 
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (messageFragment != null) {
			transaction.hide(messageFragment);
		}
		if (noticeFragment != null) {
			transaction.hide(noticeFragment);
		}
		if (othersFragment != null) {
			transaction.hide(othersFragment);
		}
		if(mainItemFragment != null){
			transaction.hide(mainItemFragment);
		}

	}
	
	private long exittime = 0;
	@Override
	// 解决退出系统
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(System.currentTimeMillis()-exittime>3000){
			Toast.makeText(MainActivity.this, "再按一次 退出程序", Toast.LENGTH_SHORT).show();
			exittime = System.currentTimeMillis();
		}
		else{
			TeacherInfo.getInstance().initSystem = 0;
			super.onBackPressed();
		}
	}

	public void setRunBackgroundServer(boolean isRun) {
		if (isRun) {
			Intent intent = new Intent(this,PushService.class);
			this.startService(intent);
		} else {
			Intent intent = new Intent(this,PushService.class);
			this.stopService(intent);
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		this.serverIntent = new Intent(MainActivity.this,PushService.class);	// 初始化中间件服务；
		this.viewFrom = intent.getIntExtra("viewfrom", 0);
		// 初始化布局元素
		this.initView();
		fragmentManager = getFragmentManager();
		// 第一次启动时选中第0个tab
		setTabSelection(0);

		if(this.viewFrom == 2){		// 表示被挤下线；
			initOffLineDialog();
		}		
		super.onNewIntent(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch(requestCode){
		case GET_CHOOSED_CLASSNAME:
			if(resultCode == RESULT_OK){
				String choosedClassName = data.getStringExtra("choosedClassName");
				//选择一个班级，载入这个班级的数据
				TeacherInfo.getInstance().setChoosedClass(choosedClassName);
				titleTextView.setText(choosedClassName);
			}
			break;
		default:
			break;
		}
	}
	
	
}
