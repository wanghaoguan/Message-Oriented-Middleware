package com.ttqeducation.activitys.message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.R;
import com.ttqeducation.activitys.study.KnowledgePointActivity;
import com.ttqeducation.activitys.study.KnowledgePointDetailActivity;
import com.ttqeducation.activitys.system.LaunchActivity;
import com.ttqeducation.activitys.system.MainActivity;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.NoticeListItem;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DesUtil;
import com.ttqeducation.tools.GeneralTools;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 消息详情界面
 * 
 * @author 高雨寒
 * 
 */
public class NoticeDetailActivity extends Activity {
	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；
	
	//消息部分
	public TextView noticeContentTitle_TextView=null; //消息的标题
	public TextView noticePublishTime_TextView=null;//消息发布事件
	public TextView noticeContent_TextView=null;//消息详细内容
	public TextView noticePublisher_TextView=null;//消息发布者
	//调用服务端查看具体信息需要参数
	private int noticeID= -1; // 从前一个界面传递过来的通知公告ID；
	private RefreshView refreshView = null;
	private NoticeListItem noticeInfo = null;
	
	private int viewInitFrom = 0;		// 界面初始化来自哪，1：表示从列表界面而来，2：表示从通知栏点击而来；
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice_content_detail);
		
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		getDataFromIntent();
		
		getNoticeContentByWS(this.noticeID, UserInfo.getInstance().studentID);
//		initView();
//		generateData();
	}
	
	//按返回按钮则释放页面信息
	public void initView() {

		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("通知详情");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				clickBack();
			}
		});
		
		this.noticeContentTitle_TextView=(TextView)super.findViewById(R.id.notice_content_title_textview);
		this.noticePublishTime_TextView=(TextView)super.findViewById(R.id.notice_content_publishTime_textview);
		this.noticeContent_TextView=(TextView)super.findViewById(R.id.notice_content_textview);
		this.noticePublisher_TextView=(TextView)super.findViewById(R.id.notice_publisher_textview);
		
		if(this.noticeInfo != null){
			NoticeDetailActivity.this.noticeContentTitle_TextView.setText(this.noticeInfo.getNoticeTitle());
			NoticeDetailActivity.this.noticePublishTime_TextView.setText(this.noticeInfo.getPublishTime());
			NoticeDetailActivity.this.noticeContent_TextView.setText(this.noticeInfo.getNoticeContent());
			NoticeDetailActivity.this.noticePublisher_TextView.setText("来自："+this.noticeInfo.getPublisherName().trim());
		}else {
			NoticeDetailActivity.this.noticeContentTitle_TextView.setText("默认");
			NoticeDetailActivity.this.noticePublishTime_TextView.setText("2015年3月13日");
			NoticeDetailActivity.this.noticeContent_TextView.setText("默认");
			NoticeDetailActivity.this.noticePublisher_TextView.setText("默认");
		}
		
	}
	
	public void getDataFromIntent(){
		this.noticeID = getIntent().getIntExtra("noticeID", -1);
		this.viewInitFrom = getIntent().getIntExtra("viewInitFrom", 0);
		Log.i("lvjie", "NoticeDetailActivity-->noticeID="+this.noticeID);
	}
	
	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(NoticeDetailActivity.this, toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}
	
	//构造的假数据
	public void generateData(){
		NoticeDetailActivity.this.noticeContentTitle_TextView.setText("学校通知");
		NoticeDetailActivity.this.noticePublishTime_TextView.setText("2015年3月13日");
		NoticeDetailActivity.this.noticeContent_TextView.setText("        "+"尊敬的家长朋友： 您好！感谢您长期以来对学校工作的大力支持！目前学校工作已近期末，学生寒假即将来临。为了使学生拥有一个充实而愉快的假期，为了给高二下学期的学习生活奠定一个良好的基础，假期的许多工作还需要您的督促与配合。");
		NoticeDetailActivity.this.noticePublisher_TextView.setText("张大大");		
	}
	
	// 从服务端获取某一条通知详情；
	public void getNoticeContentByWS(int noticeID, String studentID){
		
		new AsyncTask<Object, Object, DataTable>() {

			protected void onPreExecute() {
				super.onPreExecute();
				refreshView.show();
			};
			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				DataTable dt_notice = new DataTable();
			
				// 方法名
				String methodName = "APP_getPushNotification_detail";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("messageID", params[0].toString());
				paramsMap.put("studentID", params[1].toString());
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
					Log.i("lvjie", "1-->dt_notice = getDataTool.getDataAsTable(methodName,paramsMap)...");
					dt_notice = getDataTool.getDataAsTable(methodName,paramsMap);
					Log.i("lvjie", "2-->dt_notice = getDataTool.getDataAsTable(methodName,paramsMap)...");
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
				return dt_notice;
			}
			protected void onPostExecute(DataTable result) {
				if (result != null) {
					int count = result.getRowCount();
					for (int i = 0; i < count; i++) {
						try {
							Log.i("lvjie", "count-->"+count);
							String noticeTitle = result.getCell(i, "title");
							String publishTime = result.getCell(i, "publishTime");
							String noticeContent = result.getCell(i, "content");
							String publisherName = result.getCell(i, "publisherName");
							int noReadNum = Integer.parseInt(result.getCell(i, "unReadNotificationNum"));
							UserInfo.getInstance().noReadNoticeNum = noReadNum;
							
							Log.i("lvjie", "noticeTitle="+noticeTitle+"  publishTime="+publishTime+"  publisherName="+publisherName);

							NoticeDetailActivity.this.noticeInfo = new NoticeListItem(noticeTitle, noticeContent, publisherName, publishTime);
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				// 关闭刷新；
				refreshView.dismiss();
				
				initView();				
				// 没有联网，不可以进入到下一个界面；
				if(!GeneralTools.getInstance().isOpenNetWork1(NoticeDetailActivity.this)){
					showToast("未连接到互联网，请检查网络配置!");
				}
			};
			
		}.execute(noticeID, studentID);
		
	}

	// 当用户点击返回时，对不同的状态执行不同的代码；
	private void clickBack(){
		if(this.viewInitFrom == 1){		// 表示是从通知列表界面而来；
			NoticeDetailActivity.this.finish();
		}else if(this.viewInitFrom == 2){	// 表示是从通知栏点击而来；点击返回启动界面进入到主界面；
			Intent mainIntent = new Intent(NoticeDetailActivity.this, LaunchActivity.class);
			startActivity(mainIntent);
			NoticeDetailActivity.this.finish();
		}else {
			NoticeDetailActivity.this.finish();			// 默认关闭该界面；
		}
	}
	
	@Override
	// 当ActivityA的LaunchMode为SingleInstance,SingleTask时,
	// 如果已经ActivityA已经在堆栈中，那么此时会调用onNewIntent()方法
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		
		this.noticeID = intent.getIntExtra("noticeID", -1);
		this.viewInitFrom = getIntent().getIntExtra("viewInitFrom", 0);
		getNoticeContentByWS(this.noticeID, UserInfo.getInstance().studentID);
	}
	
	@Override
	// 手机自带的返回键；
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		clickBack();
	}
	
}


