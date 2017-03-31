package com.ttqeducation.activitys.system;

import java.util.HashMap;
import java.util.Map;

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
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.ttqeducation.R;
import com.ttqeducation.activitys.message.MessageFragment;
import com.ttqeducation.activitys.others.OthersFragment;
import com.ttqeducation.activitys.study.StudyFragment;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.UserCurrentState;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.network.PushService;
import com.ttqeducation.tools.DensityUtils;
import com.ttqeducation.tools.DesUtil;
import com.ttqeducation.tools.GeneralTools;

/**
 * 项目的主Activity，所有的Fragment都嵌入在这里。 需要做的事情 1.显示界面 2.获取家长的孩子信息，如年级，班级信息（每次都要做）
 * 3.如果通过学校Web服务地址获取不到数据： （1）重新获取对应学校的Web服务地址，如果还不行，证明系统有误，堂堂清数据库的地址出错
 * （如果获取的Web地址是空值则说明该学校已经被屏蔽，无服务）
 * 
 * @author 王勤为
 * 
 *         需要做的事情
 */

public class MainActivity extends Activity implements OnClickListener, OnGestureListener {

	/* 用于展示消息的Fragment */
	private MessageFragment messageFragment = null;

	/* 用于展示学习动态的Fragment */
	private StudyFragment studyFragment;

	/* 用于展示其他的Fragment */
	private OthersFragment othersFragment;

	/* 消息界面布局 */
	private View messageLayout;

	/* 学习界面布局 */
	private View studyLayout;

	/* 其他界面布局 */
	private View othersLayout;

	/* 在Tab布局上显示消息图标的控件 */
	private ImageView messageImage;

	/* 在Tab布局上显示学习图标的控件 */
	private ImageView studyImage;

	/* 在Tab布局上显示其他图标的控件 */
	private ImageView othersImage;

	/* 在Tab布局上显示消息标题的控件 */
	private TextView messageText;

	/* 在Tab布局上显示学习标题的控件 */
	private TextView studyText;

	/* 在Tab布局上显示其他标题的控件 */
	private TextView othersText;

	/* 用于对Fragment进行管理 */
	private FragmentManager fragmentManager;

	private TextView titleTextView = null; // 标题栏的文字；
	
	private Intent serverIntent = null;		// 后台服务；
	
	// 下线对话框；
	private Dialog offLineDialog = null; // 下线对话框
	private int viewFrom = 0;			// 2：表示来自中间件；提示账号在另一手机登陆；
	
	// 定义手势检测器
	private GestureDetector detector;
	// 定义左右滑动动作触发距离限制
	final int flingMinXDistance = 100;
	final int flingMaxYDistance = 300;
	// 定义上下滑动动作触发距离限制
	final int flingMaxXDistance = 300;
	final int flingMinYDistance = 100;
	// 定义滑动动作在X轴方向的最小速度
	final int flingMinVelocityX = 0;
	// 定义down的位置,动作由down、move、up组成
	private float downX;
	private float downY;
	// ViewFlipper实现划动效果
	private ViewFlipper viewFlipper;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		this.serverIntent = new Intent(MainActivity.this,PushService.class);
		
		getDataFromIntent();
		
		// 创建手势检测器
		detector = new GestureDetector(getApplicationContext(), this);
		
		// 初始化布局元素
		this.initViews();
		fragmentManager = getFragmentManager();
		// 第一次启动时选中第0个tab
		setTabSelection(0);
		
		if(this.viewFrom == 2){		// 表示被挤下线；
			initOffLineDialog();
		}
		

		getNoReadNoticeNumByWS(UserInfo.getInstance().studentID);
	}
	
	private void getDataFromIntent(){
		this.viewFrom = getIntent().getIntExtra("viewfrom", 0);
		Log.i("lvjie", "MainActivity--> viewFrom="+this.viewFrom);
	}

	/**
	 * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
	 */
	private void initViews() {
		// 标题栏的文字；
		this.titleTextView = (TextView) super.findViewById(R.id.title_text);
		
		// 下面是界面底部的三个按钮部分；
		messageLayout = findViewById(R.id.message_layout);
		studyLayout = findViewById(R.id.study_layout);
		othersLayout = findViewById(R.id.others_layout);

		// 下面是界面底部三个按钮对应的图片部分;
		messageImage = (ImageView) findViewById(R.id.message_image);
		studyImage = (ImageView) findViewById(R.id.study_image);
		othersImage = (ImageView) findViewById(R.id.others_image);

		// 下面是界面底部文字描述部分；
		messageText = (TextView) findViewById(R.id.message_text);
		studyText = (TextView) findViewById(R.id.study_text);
		othersText = (TextView) findViewById(R.id.others_text);

		// 添加点击事件；
		messageLayout.setOnClickListener(this);
		studyLayout.setOnClickListener(this);
		othersLayout.setOnClickListener(this);
		
		viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
		
		// 启用后台服务；
		if(this.viewFrom != 2){
			MainActivity.this.startService(MainActivity.this.serverIntent);	
		}
	}
	
	// 启动后台服务，与中间件通信；
	public void startService(){
		new Thread(new Runnable() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				MainActivity.this.startService(MainActivity.this.serverIntent);
				Log.i("lvjie", "MainActivity-->启动后台服务，进行连接中间件...");
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.study_layout:
			// 当点击了学习tab时，选中第1个tab
			setTabSelection(0);
			break;
		case R.id.message_layout:
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
		default:
			break;
		}
	}

	/**
	 * 根据传入的index参数来设置选中的tab页。
	 * 
	 * @param index
	 *            每个tab页对应的下标。0表示学习，1表示消息，2表示其他。
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
			titleTextView.setText("欢迎"); // 修改标题栏的文字；
			// 当点击了学习tab时，改变控件的图片和文字颜色
			// studyImage.setImageResource(R.drawable.study_dynamic_pre);
			studyImage.setBackgroundResource(R.drawable.footer_11);
			studyText.setTextColor(getResources().getColor(R.color.main_text_blue));
			if (studyFragment == null) {
				// 如果ContactsFragment为空，则创建一个并添加到界面上
				studyFragment = new StudyFragment();
				transaction.add(R.id.content, studyFragment);
			} else {
				// 如果ContactsFragment不为空，则直接将它显示出来
				transaction.show(studyFragment);
			}
			break;
		case 1:
			titleTextView.setText("消息"); // 修改标题栏的文字；
			// 当点击了消息tab时，改变控件的图片和文字颜色
			// messageImage.setImageResource(R.drawable.messages_pre);
			messageImage.setBackgroundResource(R.drawable.footer_22);
			messageText.setTextColor(getResources().getColor(R.color.main_text_blue));
			if (messageFragment == null) {
				// 如果MessageFragment为空，则创建一个并添加到界面上
				messageFragment = new MessageFragment();
				transaction.add(R.id.content, messageFragment);
			} else {
				// 如果MessageFragment不为空，则直接将它显示出来
				transaction.show(messageFragment);
			}
			break;

		case 2:
			titleTextView.setText("其他"); // 修改标题栏的文字；
			// 当点击了其他tab时，改变控件的图片和文字颜色
			// othersImage.setImageResource(R.drawable.others_pre);
			othersImage.setBackgroundResource(R.drawable.footer_33);
			othersText.setTextColor(getResources().getColor(R.color.main_text_blue));
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
		
		// studyImage.setImageResource(R.drawable.study_dynamic);
		studyImage.setBackgroundResource(R.drawable.footer_1);
		studyText.setTextColor(getResources().getColor(R.color.textGray));
		// messageImage.setImageResource(R.drawable.messages);
		messageImage.setBackgroundResource(R.drawable.footer_2);
		messageText.setTextColor(getResources().getColor(R.color.textGray));
		// othersImage.setImageResource(R.drawable.others);
		othersImage.setBackgroundResource(R.drawable.footer_3);
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
		if (studyFragment != null) {
			transaction.hide(studyFragment);
		}
		if (othersFragment != null) {
			transaction.hide(othersFragment);
		}

	}

	// 从服务器端获取未读的通知公告数量，用来解决new的问题；
	public void getNoReadNoticeNumByWS(String studentID){
		
		new AsyncTask<Object, Object, DataTable>() {
			
			protected void onPreExecute() {};
			
			protected DataTable doInBackground(Object... params) {
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				
				DataTable dt_noReadNoticeNum = new DataTable();
				
				// 方法名
				String methodName = "APP_getPushNotification_unReadNum";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("studentID", params[0].toString());
				paramsMap.put("TokenID", tokenID);
				// 获取数据
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				// 从本地获取学校URL
				SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				if (schoolURL == null) {// 如果没有值
					return null;
				}
				getDataTool.setURL(schoolURL);
				try {
					dt_noReadNoticeNum = getDataTool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "MainActivity-->getNoReadNoticeNumByWS()...出错了...");
					e.printStackTrace();
				}
				return dt_noReadNoticeNum;

			};
			
			protected void onPostExecute(DataTable result) {
				if (result != null) {
					int count = result.getRowCount();
					for (int i = 0; i < count; i++) {
						try {
							
							Log.i("lvjie", "unReadNum="+result.getCell(i, "unReadNotificationNum")+"  count="+count);
							int unReadNum = Integer.parseInt(result.getCell(i, "unReadNotificationNum"));						
							
							UserInfo.getInstance().noReadNoticeNum = unReadNum;
							if(unReadNum > 0){			// 显示new标识；
								showNewInView(true);
							}else {
								showNewInView(false);
							}
							break;
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}else {
					Log.i("error", "MainActivity-->getNoReadNoticeNumByWS()...出错了...");
				}
			
				// 没有联网，不可以进入到下一个界面；
				if(!GeneralTools.getInstance().isOpenNetWork1(MainActivity.this)){
					showToast("未连接到互联网，请检查网络配置!");
				}

			};
			
		}.execute(studentID);
	}
	
	// 在主界面底部是否显示new标识；
	public void showNewInView(boolean isShow){
		ImageView newView = (ImageView) super.findViewById(R.id.showNew_view);
		if(isShow){
			newView.setVisibility(View.VISIBLE);
			if(messageFragment != null){
				messageFragment.setNewViewVisible(UserInfo.getInstance().noReadNoticeNum);
			}
		}else {
			newView.setVisibility(View.INVISIBLE);
		}	
	}
	
	// 窗口提示信息；
	public void showToast(String toastMessage){
		Toast toast =  Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	@Override
	protected void onResume() {			// 解决new数量的需要；
		// TODO Auto-generated method stub
		super.onResume();
		
		Log.i("lvjie", "mainactivity--->onResume()...noReadNoticeNum"+UserInfo.getInstance().noReadNoticeNum);
		// 设置消息界面通知公告是否显示new;
		if(messageFragment != null){
			messageFragment.setNewViewVisible(UserInfo.getInstance().noReadNoticeNum);
		}
		
		// 设置主界面底部是否有红点；
		if(UserInfo.getInstance().noReadNoticeNum <=0 && UserCurrentState.getInstance().homeSchoolNew<=0){
			showNewInView(false);
		}else {
			showNewInView(true);
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
//			unregisterReceiver(myBroadcast);
			super.onBackPressed();
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
	
	public void setRunBackgroundServer(boolean isRun) {
		if (isRun) {
			Intent intent = new Intent(this,PushService.class);
			this.startService(intent);
			//showToast("启动后台服务");
		} else {
			//showToast("关闭后台服务");
			Intent intent = new Intent(this,PushService.class);
			this.stopService(intent);
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		unregisterReceiver(this.myBroadcast);
		super.onDestroy();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("lvjie", "MainActivity--->onNewIntent()...");
		this.serverIntent = new Intent(MainActivity.this,PushService.class);
		
		this.viewFrom = intent.getIntExtra("viewfrom", 0);
		Log.i("lvjie", "MainActivity-->onNewIntent() viewFrom="+this.viewFrom);
		// 初始化布局元素
		this.initViews();
		fragmentManager = getFragmentManager();
		// 第一次启动时选中第0个tab
		setTabSelection(0);
		
		if(this.viewFrom == 2){		// 表示被挤下线；
			initOffLineDialog();
		}
		getNoReadNoticeNumByWS(UserInfo.getInstance().studentID);
		super.onNewIntent(intent);
	}

	// 将触碰事件交给GestureDetector处理
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return detector.onTouchEvent(event);
	}
	
	// 接受事件并且区分点击事件和滑动事件
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// 处理左右滑动事件
		detector.onTouchEvent(ev);
		
		if(ev.getAction() == MotionEvent.ACTION_DOWN) {
			downX = ev.getX();
			downY = ev.getY();
			super.dispatchTouchEvent(ev);
		} else if(ev.getAction() == MotionEvent.ACTION_MOVE) {
			super.dispatchTouchEvent(ev);
		} else if(ev.getAction() == MotionEvent.ACTION_UP) {
			if(Math.abs(ev.getX() - downX) == 0 && Math.abs(ev.getY() - downY) == 0) { // 处理点击事件
				super.dispatchTouchEvent(ev);
			} else if(Math.abs(ev.getX() - downX) > flingMinXDistance 
					&& Math.abs(ev.getY() - downY) < flingMaxYDistance
					&& Math.abs(ev.getX() - downX) - Math.abs(ev.getY() - downY) > 0) { // 不出你左右滑动事件
				
			} else if(Math.abs(ev.getX() - downX) < flingMaxXDistance 
					&& Math.abs(ev.getY() - downY) > 0
					&& Math.abs(ev.getX() - downX) - Math.abs(ev.getY() - downY) < 0) { // 处理上下滑动事件
				super.dispatchTouchEvent(ev);
			}			
		} 
		
		return true;
	}
	
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		// 从右到左滑动
		if(e1.getX() - e2.getX() > flingMinXDistance 
				&& Math.abs(e1.getY() - e2.getY()) < flingMaxYDistance
				&& Math.abs(velocityX) > flingMinVelocityX) {
			if(titleTextView.getText().equals("服务项目")) {
				setTabSelection(1);
				
				Animation lInAnim = AnimationUtils.loadAnimation(getApplicationContext(), 
						R.animator.push_left_in); // 向左滑动左侧进入的渐变效果（alpha 0.1  -> 1.0）  
	            viewFlipper.setInAnimation(lInAnim);
	            viewFlipper.showNext();
			} else if(titleTextView.getText().equals("消息")) {
				setTabSelection(2);
				
				Animation lInAnim = AnimationUtils.loadAnimation(getApplicationContext(), 
						R.animator.push_left_in); // 向左滑动左侧进入的渐变效果（alpha 0.1  -> 1.0）  
				viewFlipper.setInAnimation(lInAnim);
	            viewFlipper.showNext();
			}
		}
		
		// 从左到右滑动
		else if(e2.getX() - e1.getX() > flingMinXDistance 
				&& Math.abs(e1.getY() - e2.getY()) < flingMaxYDistance
				&& Math.abs(velocityX) > flingMinVelocityX) {
			if(titleTextView.getText().equals("其他")) {
				setTabSelection(1);
				
				Animation rInAnim = AnimationUtils.loadAnimation(getApplicationContext(), 
						R.animator.push_right_in); // 向右滑动左侧进入的渐变效果（alpha  0.1 -> 1.0）  
				viewFlipper.setInAnimation(rInAnim);
	            viewFlipper.showNext();
			} else if(titleTextView.getText().equals("消息")) {
				setTabSelection(0);
				
				Animation rInAnim = AnimationUtils.loadAnimation(getApplicationContext(), 
						R.animator.push_right_in); // 向右滑动左侧进入的渐变效果（alpha  0.1 -> 1.0）  
				viewFlipper.setInAnimation(rInAnim);
	            viewFlipper.showNext();
			}
		}		
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}	
}
