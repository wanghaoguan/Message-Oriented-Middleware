package com.ttqeducation.teacher.activitys.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.activitys.system.LaunchActivity;
import com.ttqeducation.teacher.activitys.system.ResultLaunchActivity;
import com.ttqeducation.teacher.beans.ChartInfo;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.TeacherInfo;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.MyListView;
import com.ttqeducation.teacher.myViews.MyListView.IMyListViewListener;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.tools.DateUtil;
import com.ttqeducation.teacher.tools.DesUtil;
import com.ttqeducation.teacher.tools.GeneralTools;
import com.ttqeducation.teacher.tools.Tools;

import android.R.integer;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 教师端 班级群聊详情界面；
 * @author lvjie
 *
 */
public class GroupChatActivity extends Activity implements IMyListViewListener {
	
	// 标题栏部分；
	private TextView titleTextView = null;
	private LinearLayout titleBackLayout = null;
  
	private EditText sendInfoEditText = null;
	private Button sendInfoButton = null;
	
	private MyListView recieveInfoListView = null;
	private MyAdapter mAdapter = null; // 得到一个MyAdapter对象 
	private List<ChartInfo> listRecieveInfos = new ArrayList<ChartInfo>();			// 存放总数据，及界面存在的数据；	
	private List<ChartInfo> listRecieveInfosLastTen = new ArrayList<ChartInfo>();	// 存放下拉刷新的数据；
	private RefreshView refreshView = null;
	
	private String classID = "";
	private String className = "";
	private int chatID = 0;				// 保存当前最旧一条聊天ID，用来下拉刷新；
	
	private GroupChatBroadcast myBroadcast = null;		// 广播；
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 与一对一聊天 用的是同一个界面；
		setContentView(R.layout.activity_one_chat);
		
		getDataFromIntent();
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		
		getLatestChatInfoByWS(TeacherInfo.getInstance().execTeacherID, this.classID);
//		generateData();
//		initView();
		
		
	}
	
	public void getDataFromIntent(){
		this.classID = getIntent().getStringExtra("classID");
		this.className = getIntent().getStringExtra("className");
		
		Log.i("lvjie", "classID="+this.classID+"  className="+this.className);
		
		TeacherInfo.getInstance().currentView = 2;
		TeacherInfo.getInstance().chatingID = this.classID;
	}
	
	public void initView(){
		
		// 表明进入系统，是从通知栏进入的，当按返回按钮，需要进入到主界面；
		if(TeacherInfo.getInstance().initSystem != 1){
			TeacherInfo.getInstance().initSystem = 2;
		}
		
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText(this.className);
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				clickBack();
			}
		});
				
		
		this.recieveInfoListView = (MyListView)super.findViewById(R.id.listView_showInfo);
		this.recieveInfoListView.setMyListViewListener(this);
		this.recieveInfoListView.setPullLoadEnable(false);		// 设置可以进行上拉加载；
		this.recieveInfoListView.setPullRefreshEnable(true);	// 设置不可以进行下拉刷新；
		
		this.mAdapter = new MyAdapter(this);
		this.recieveInfoListView.setAdapter(this.mAdapter);
		
		this.sendInfoEditText = (EditText)super.findViewById(R.id.sendInfo_editText);
		this.sendInfoButton = (Button)super.findViewById(R.id.sendInfo_button);
		
		this.sendInfoButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String sendInfo = sendInfoEditText.getText().toString().trim();
				
				if("".equals(sendInfo)){
					showToast("发送消息不能为空!");
					return;
				}
				
				publishChatInfoToWS(sendInfo);
				Date date = new Date();
				String time = DateUtil.convertDateToString("hh:mm", date);
				ChartInfo chartInfo = new ChartInfo(sendInfo, time, true);
				listRecieveInfos.add(chartInfo);
				mAdapter.notifyDataSetChanged();
				sendInfoEditText.setText("");
			}
		});
		
		// 该广播注册监听器；
		this.myBroadcast = new GroupChatBroadcast();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("mobile.groupchat");				// 监听一条广播；
		intentFilter.addCategory("mobile.action.groupchat");	// 添加广播的类型；		
		registerReceiver(this.myBroadcast, intentFilter);
				
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		TeacherInfo.getInstance().currentView = 0;
		TeacherInfo.getInstance().chatingID = "";
		unregisterReceiver(myBroadcast);
		super.onDestroy();
	}
	
	public void generateData(){
		
		for(int i=0;i<5;i++){
			
			Date date = new Date();
			String time = DateUtil.convertDateToString("HH:mm", date);
			
			if(i%2 == 0){
				ChartInfo chartInfo = new ChartInfo("您好，"+i, time, true);
				this.listRecieveInfos.add(chartInfo);
			}else {
				ChartInfo chartInfo = new ChartInfo("大家好，"+i, time, false, "老师  "+i,"");
				this.listRecieveInfos.add(chartInfo);
			}
			
		}

	}
	
	public void generateLastTenData() {
		this.listRecieveInfosLastTen.clear();
		for (int i = 0; i < 5; i++) {

			Date date = new Date();
			String time = DateUtil.convertDateToString("HH:mm", date);

			if (i % 2 == 0) {
				ChartInfo chartInfo = new ChartInfo("今天上午通知好，" + i, time, true);
				this.listRecieveInfosLastTen.add(chartInfo);
			} else {
				ChartInfo chartInfo = new ChartInfo("确定有通知么？" + i, time, false, "老师   "+i,"");
				this.listRecieveInfosLastTen.add(chartInfo);
			}

		}
	}
	
	public void clickBack(){
		TeacherInfo.getInstance().currentView = 0;
		TeacherInfo.getInstance().chatingID = "";
		Log.i("lvjie", "initSystem="+TeacherInfo.getInstance().initSystem);
		if(TeacherInfo.getInstance().initSystem == 2){	// 需要进入到主界面；
			Intent intent = new Intent(GroupChatActivity.this, LaunchActivity.class);
			startActivity(intent);
		}
		this.finish();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		clickBack();
	}
	
	/********* 聊天界面的 listview 数据绑定 类 开始  ***********/
	private class MyAdapter extends BaseAdapter{
		
		private int[] personImgID = {R.drawable.teacher1, R.drawable.teacher2, 
				R.drawable.teacher3, R.drawable.teacher4, R.drawable.teacher5, R.drawable.teacher6};
		Random random = new Random();
		
		private LayoutInflater mInflater; //得到一个LayoutInfalter对象用来导入布局
        
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listRecieveInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewChild viewChild;
			if (view == null) {
				view = mInflater.inflate(
						R.layout.group_chat_listitem, null);
				viewChild = new ViewChild();
				/* 得到各个控件的对象 */
				viewChild.tvTime = (TextView) view.findViewById(R.id.time_textView);
				viewChild.tvReceiveInfo = (TextView) view.findViewById(R.id.recieveInfo_textView);
				viewChild.tvSendInfo = (TextView) view.findViewById(R.id.sendInfo_textView);
				viewChild.layoutMe = (RelativeLayout) view.findViewById(R.id.layout_me);
				viewChild.layoutOther = (RelativeLayout) view.findViewById(R.id.layout_other);
				viewChild.tvOtherName = (TextView) view.findViewById(R.id.otherName_textView);
				viewChild.otherImageView = (ImageView) view.findViewById(R.id.image_other);
				
				view.setTag(viewChild); // 绑定ViewChild对象
			} else {
				viewChild = (ViewChild) view.getTag(); // 取出ViewChild对象
			}

			if(listRecieveInfos.get(position).isMeSend()){
				viewChild.layoutOther.setVisibility(View.GONE);
				viewChild.layoutMe.setVisibility(View.VISIBLE);
				viewChild.tvTime.setText(listRecieveInfos.get(position).getChartTime());
				viewChild.tvSendInfo.setText(listRecieveInfos.get(position).getChartContent());
			}else {
				viewChild.layoutOther.setVisibility(View.VISIBLE);
				viewChild.layoutMe.setVisibility(View.GONE);
				viewChild.tvTime.setText(listRecieveInfos.get(position).getChartTime());
				viewChild.tvReceiveInfo.setText(listRecieveInfos.get(position).getChartContent());
				viewChild.tvOtherName.setText(listRecieveInfos.get(position).getSendName());
				
				// 每个老师都起初都分配一个头像，然后从hs中取头像ID；
				String id = listRecieveInfos.get(position).getSenderID();
				if(TeacherInfo.getInstance().hsTeacherPic.containsKey(id)){
					int img = TeacherInfo.getInstance().hsTeacherPic.get(id);
					viewChild.otherImageView.setBackgroundResource(img);
				}else {
					int k = random.nextInt(6);
					viewChild.otherImageView.setBackgroundResource(personImgID[k]);
					TeacherInfo.getInstance().hsTeacherPic.put(id, personImgID[k]);
				}
			}

			return view;
		}
		
	}
	
	// 存放列表子项的控件，用于在MyAdapter中设置;
    public final class ViewChild {
        public TextView tvTime;
        public TextView tvSendInfo;
        public TextView tvReceiveInfo;
        public TextView tvOtherName;
        
        public RelativeLayout layoutMe;
        public RelativeLayout layoutOther;
        
        public ImageView otherImageView;	// 对方老师的头像；
    }
    /********* 聊天界面的 listview 数据绑定 类 结束  ***********/
    
	@Override
	// 下拉刷新；
	public void onRefresh() {
		// TODO Auto-generated method stub
		Log.i("lvjie", "MainActivity-->onRefresh()...");
		getLastTenChatInfoByWS(TeacherInfo.getInstance().execTeacherID, this.classID);
	}

	@Override
	// 上拉加载；
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}	
		
	/**
     * 获取最初的聊天信息
     * @param studentID
     * @param teacherID
     * @param chatID
     */
 	public void getLatestChatInfoByWS(String execTeacherID, String classID){
 		new AsyncTask<Object, Object, DataTable>(){

 			protected void onPreExecute() {
 				refreshView.show();
 			};
 			@Override
 			protected DataTable doInBackground(Object... params) {
 				// TODO Auto-generated method stub
 				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
 				DataTable dt_chatInfoList = null;
				
				// 方法名
				String methodName = "APP_teacherClassChat_info";
				String teacherID =  params[0].toString();
				String classID = params[1].toString();
				Log.i("lvjie", "传递参数： teacherID="+teacherID+"    classID="+classID);
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("teacherID", teacherID);
				paramsMap.put("classID", classID);
				paramsMap.put("messageID", 0+"");
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
					dt_chatInfoList = getDataTool.getDataAsTable(methodName, paramsMap);
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
				return dt_chatInfoList;
 			}
 			protected void onPostExecute(DataTable result) {
 				if (result != null) {
					ChartInfo chartInfo;
					int count = result.getRowCount();
					for (int i = 0; i < count; i++) {
						try {
							String chartContent = result.getCell(i, "content");
							String publishTime = result.getCell(i, "publishTime");
							int user = Integer.parseInt(result.getCell(i, "user"));
							int chatID = Integer.parseInt(result.getCell(i, "messageID"));
							String userName = result.getCell(i, "userName").trim();
							String userID = result.getCell(i, "publisherID").trim();
								
							Log.i("lvjie", "content="+chartContent+"  user="+user+"  userName="+userName);
							
							if(i==0){			// 获取最后一个消息编号；
								GroupChatActivity.this.chatID = chatID;
							}
							boolean isMeSend = (user==1)?true:false;
							

							chartInfo = new ChartInfo(chartContent, publishTime, isMeSend, userName, userID);
							listRecieveInfos.add(chartInfo);
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				// 关闭刷新；
				refreshView.dismiss();
				
				// 没有联网，不可以进入到下一个界面；
				if(!GeneralTools.getInstance().isOpenNetWork1(GroupChatActivity.this)){
					showToast("未连接到互联网，请检查网络配置!");
				}
 				initView();
 			};
 			
 		}.execute(execTeacherID, classID);
 	}
 	
 
    /**
     * 获取接下来10条聊天信息,chatID设为最后的一条消息ID；
     * @param studentID
     * @param teacherID
     * @param chatID
     */
 	public void getLastTenChatInfoByWS(String execTeacherID, String classID){
 		new AsyncTask<Object, Object, DataTable>(){

 			protected void onPreExecute() {
 				
 			};
 			@Override
 			protected DataTable doInBackground(Object... params) {
 				// TODO Auto-generated method stub
 				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
 				DataTable dt_chatInfoList = new DataTable();
				
				// 方法名
				String methodName = "APP_teacherClassChat_info";
				String teacherID =  params[0].toString();
				String classID = params[1].toString();
				Log.i("lvjie", "传递参数： teacherID="+teacherID+"    classID="+classID);
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("teacherID", teacherID);
				paramsMap.put("classID", classID);
				paramsMap.put("messageID", GroupChatActivity.this.chatID+"");
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
					dt_chatInfoList = getDataTool.getDataAsTable(methodName, paramsMap);
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
				return dt_chatInfoList;
 			}
 			protected void onPostExecute(DataTable result) {
 				listRecieveInfosLastTen.clear();
 				if (result != null) {
					ChartInfo chartInfo;
					int count = result.getRowCount();
					for (int i = 0; i < count; i++) {
						try {
							String chartContent = result.getCell(i, "content");
							String publishTime = result.getCell(i, "publishTime");
							int user = Integer.parseInt(result.getCell(i, "user"));
							int chatID = Integer.parseInt(result.getCell(i, "messageID"));
							String userName = result.getCell(i, "userName");
							String userID = result.getCell(i, "publisherID");	
							Log.i("lvjie", "content="+chartContent+"  user="+user+"  userName="+userName);
							
							if(i==0){			// 获取最后一个消息编号；
								GroupChatActivity.this.chatID = chatID;
							}
							boolean isMeSend = (user==1)?true:false;
							

							chartInfo = new ChartInfo(chartContent, publishTime, isMeSend, userName, userID);
							listRecieveInfosLastTen.add(chartInfo);
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				// 关闭刷新；
				refreshView.dismiss();
				
				// 没有联网，不可以进入到下一个界面；
				if(!GeneralTools.getInstance().isOpenNetWork1(GroupChatActivity.this)){
					showToast("未连接到互联网，请检查网络配置!");
				}
				
				onLoad();
								
 				if(listRecieveInfosLastTen.size() == 0){
 					showToast("没有消息!");
 				}else {
 					listRecieveInfos.addAll(0, listRecieveInfosLastTen);
 	 				mAdapter.notifyDataSetChanged();
 	 				recieveInfoListView.setAdapter(mAdapter);
 	 				recieveInfoListView.setSelection(listRecieveInfosLastTen.size());	
				}
 							
 			};
 			
 		}.execute(execTeacherID, classID);
 	}

 	/**
 	 * 向数据库中插入聊天信息；
 	 * 家长给老师发送消息；
 	 */
 	public void publishChatInfoToWS(String chatInfo){
 		new AsyncTask<Object, Object, String>() {

 			@Override
			protected void onPreExecute() {
			};
			
			@Override
			protected String doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				String result = " ";
				
				// 方法名
				String methodName = "APP_PushNotification_Message_insert";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int len = params[0].toString().length();
				if(len>15){
					len = 15;
				}
				Log.i("lvjie", "len="+len+"  content_brief="+params[0].toString().substring(0, len));
				
				paramsMap.put("type", 5+"");					// 通知类型；5：表示班级群聊;
				paramsMap.put("title", "来自："+GroupChatActivity.this.className);	
				paramsMap.put("content", params[0].toString());
				paramsMap.put("content_brief", params[0].toString().substring(0, len));	// 通知内容简介；
				paramsMap.put("receiver", GroupChatActivity.this.classID);						// 接收者，这里为班级ID；
				paramsMap.put("publisherID", TeacherInfo.getInstance().execTeacherID);
				paramsMap.put("publisherName", TeacherInfo.getInstance().teacherName);
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
					result = getDataTool.getDataAsString(methodName, paramsMap);
					if(result != null){
						Log.i("lvjie", "2-->******"+result);
						String chatID = result.split("\\$")[0];
						sendNoticeInfoToMiddleWare(chatID);		// 启动与中间件通信
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return result;
			}
			
			@Override
			protected void onPostExecute(String result) {
				if(result != null){
					showToast("发送成功");
				}else {
					showToast("发送失败");
				}
			}
			
			
		}.execute(chatInfo);
 	}
	
	
	private void onLoad() {
		recieveInfoListView.stopRefresh();
		recieveInfoListView.stopLoadMore();
		String time = DateUtil.convertDateToString("HH:mm", new Date());
		recieveInfoListView.setRefreshTime(time);
	}
	
	// 窗口提示信息；
 	public void showToast(String toastMessage){
 		Toast toast =  Toast.makeText(GroupChatActivity.this, toastMessage, Toast.LENGTH_SHORT);
 		toast.show();
 	}

 	// 向中间件发送消息；
 	public void sendNoticeInfoToMiddleWare(final String noticeID){

 		SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
		String pushingServiceIP = pre.getString("pushingServiceIP", "");
		String pushingServicePort = pre.getString("pushingServicePort", "0");
		int port = Integer.parseInt(pushingServicePort);
		Log.i("lvjie", "中间件服务：pushingServiceIP="+pushingServiceIP+"  pushingServicePort="+pushingServicePort);	
		SocketClient socketClient = new SocketClient(pushingServiceIP, port, noticeID);	// hxy
		socketClient.start();
 		
 	}
  	
 	
 	/***** socket类开始 ******/
 	/**
 	 * socket连接解决办法；   这个还需修改，需要重新定义发送的内容；例如确保这条信息是自己发送的，就不需要从中间件接收；    
 	 * @author lvjie
 	 *
 	 */
 	private class SocketClient{
 					
 		// 用来读取服务端的信息
 		private DataOutputStream writer = null;
 		private DataInputStream reader = null;
 		private Socket socket = null;
 		private boolean isConnection = false;
 		
 		private String ipAddress = "";		// IP地址
 		private int port=0;					// 端口号
 		private String chatID = "";
 		
 		public SocketClient(String ipAddress, int port, String chatID){
 			this.ipAddress = ipAddress;
 			this.port = port;
 			this.chatID = chatID;
 		}
 		
 		// 启动socket连接；
 		public void start(){
 			try {
 				
 				Log.i("lvjie", "1-->连接成功......");
 				socket = new Socket(ipAddress, port);
 				this.isConnection = true;
 				Log.i("lvjie", "连接成功......");
 				
 				writer = new DataOutputStream(socket.getOutputStream());
 				reader = new DataInputStream(socket.getInputStream());
 				String sendInfoString = this.chatID;
 				sendInfo(sendInfoString, 12);		// 12表示向中间件发送通知消息；
 								
 				startServerReplyListener(reader);
 				closeSocket();
 				Log.i("lvjie", "能运行到这吗?..应该不会吧...可以的，线程正常结束...");
 			} catch (Exception e) {
 				Log.i("lvjie", "2..->.........");
 				closeSocket();		// 当出现异常，需要关闭socket；				
 				e.printStackTrace();
 			} 
 		}
 		
 		// 监听中间件发送的消息；
 		public void startServerReplyListener(DataInputStream reader) throws Exception{
 			while (isConnection) {
 				byte[] head_len = new byte[4];
 				byte[] head_type = new byte[4];
 				
 				if(reader.read(head_len) == -1){
 					Log.i("lvjie", "reader.read(head_len) == -1");
 					throw new IOException();
 				}
 				if(reader.read(head_type) == -1){
 					Log.i("lvjie", "reader.read(head_type) == -1");
 					throw new IOException();
 				}
 				
 				int len = Tools.getInt(head_len);
 				int type = Tools.getInt(head_type);
 				
 				byte[] rec_byte = new byte[len];
 				
 				if(reader.read(rec_byte) == -1){
 					Log.i("lvjie", "reader.read(rec_byte) == -1");
 					throw new IOException();
 				}
 				
 				String rec = new String(rec_byte);

 				Log.i("lvjie", "从服务端接收的消息：len="+len+"  type="+type+"  rec="+rec);
 				String receiveMsg = rec.split("\\$_")[0];
 				Log.i("lvjie", "从服务端接收的消息：receiveMsg="+receiveMsg);
 				this.isConnection = false;		// 当接收到了消息，则停止接收消息；		
 			}
 			Log.i("lvjie", "停止监听中间件发送消息...");
 		}
 		
 		// 向服务端发送消息；
 		public void sendInfo(String sendStr,int type) throws Exception{
 			if(this.socket.isConnected()){
 				if(this.writer != null){
 					sendStr = sendStr+"$_";//加上尾部
 					
 					byte[] b = sendStr.getBytes();		// 把字符串转化为字节数组；
 					int length = b.length;
 										
 					//头部type4字节，长度4字节。长度是后面的额
 					byte[] head_len = Tools.intToByteArray(length);
 					byte[] head_type = Tools.intToByteArray(type);												

 					byte[] head = Tools.megerBytes(head_len,head_type);
 					byte[] sendBytes = Tools.megerBytes(head, b);
 										
 					this.writer.write(sendBytes);
 					this.writer.flush();
 					Log.i("lvjie", "客户端成功发送的消息为："+sendStr);
 				}
 			}
 		}

 		// 把从服务端收到的字节数组转化为字符串；
 		public String convertBytesToString(byte[] recieveInfo){
 			String info = "";
 			int len = recieveInfo.length;
 			
 			byte[] head_len = new byte[4];
 			byte[] head_type = new byte[4];
 			byte[] content = new byte[len-8];

 			
 			for(int i=0;i<len;i++){
 				if(i<4){
 					head_len[i] = recieveInfo[i];
 				}else if(i<8){
 					head_type[i-4] = recieveInfo[i];
 				}else {
 					content[i-8] = recieveInfo[i];
 					info = new String(content);
 				}
 			}
 			
 			Log.i("lvjie", "解析信息：head_len="+Tools.getInt(head_len)+"  head_type="+Tools.getInt(head_type)+"  info="+info);
 			
 			return info;
 		}
 			
 		// 关闭socket；
 		public void closeSocket(){
 			
 			try {
 				isConnection = false;
 				
 				// 关闭输入输出流；
 				if(socket != null){
 					if(!socket.isInputShutdown()){
 						socket.shutdownInput();
 					}
 					if(!socket.isOutputShutdown()){
 						socket.shutdownOutput();
 					}
 				}
 				
 				if(reader!=null){
 					reader.close();
 				}
 				if(writer!=null){
 					writer.close();
 				}
 				if(socket != null){
 					if(!socket.isClosed()){
 						socket.close();							
 					}
 				}
 				
 				Log.i("lvjie", "正常关闭连接...");
 								
 			} catch (Exception e) {
 				// TODO Auto-generated catch block
 				Log.i("lvjie", "关闭连接异常...");						
 				e.printStackTrace();
 			}

 		}

 	}
 	/*****   socket类结束    ******/
 	
 	
 	/***********   创建一个广播类，中间件注册该广播，对该广播发送数据,用来更新界面     *************/
	private class GroupChatBroadcast extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String chatInfo = intent.getStringExtra("chatInfo");
			String time = intent.getStringExtra("chatTime");
			String publisherName = intent.getStringExtra("publisherName");
			String publisherID = intent.getStringExtra("publisherID");
			Log.i("lvjie", "中间件发送过来的聊天信息：  chatInfo="+chatInfo+"  time="+time);
			ChartInfo chartInfo = new ChartInfo();
			chartInfo.setChartContent(chatInfo);
			chartInfo.setChartTime(time);
			chartInfo.setMeSend(false);
			chartInfo.setSendName(publisherName);
			chartInfo.setSenderID(publisherID);
			listRecieveInfos.add(chartInfo);
			mAdapter.notifyDataSetChanged();
		}		
	}
	
	@Override
	// 该activity为单例模式，当处于其他用户聊天界面……
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		
		this.classID = intent.getStringExtra("classID");
		this.className = intent.getStringExtra("className");
		Log.i("lvjie", "classID="+this.classID+"  className="+this.className);
		
		TeacherInfo.getInstance().currentView = 1;
		TeacherInfo.getInstance().chatingID = this.classID;
		this.listRecieveInfos.clear();
		this.chatID = 0;
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		getLatestChatInfoByWS(TeacherInfo.getInstance().execTeacherID, this.classID);
	}
 	
}
