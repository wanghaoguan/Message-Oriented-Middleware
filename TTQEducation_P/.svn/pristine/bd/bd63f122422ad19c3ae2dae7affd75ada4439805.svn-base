package com.ttqeducation.activitys.message;

/**
 * 吕杰
 * 该类主要用于测试需要，不属于项目的一部分;
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.R;
import com.ttqeducation.activitys.study.ErrorQuestionDetailsActivity;
import com.ttqeducation.beans.ChartInfo;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.UserCurrentState;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.MyListView;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.myViews.MyListView.IMyListViewListener;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DateUtil;
import com.ttqeducation.tools.DesUtil;
import com.ttqeducation.tools.GeneralTools;
import com.ttqeducation.tools.Tools;

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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 一对一聊天详情界面；
 * @author lvjie
 *
 */
public class CommunicationActivity extends Activity implements IMyListViewListener  {

	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；
	
	private EditText sendInfoEditText = null;
	private Button sendInfoButton = null;
	private RefreshView refreshView = null;
	
	private MyListView recieveInfoListView = null;
	private MyAdapter mAdapter = null; // 得到一个MyAdapter对象 
	private List<ChartInfo> listRecieveInfos = new ArrayList<ChartInfo>();			// 存放总数据，及界面存在的数据；	
	private List<ChartInfo> listRecieveInfosLastTen = new ArrayList<ChartInfo>();	// 存放下拉刷新的数据；
	
	private String teacherID = "";		// 教师编号；
	private int teacherImgID = 0;		// 教师图片；
	private String teacherName = "";	// 教师姓名；
	
	private int chatID = 0;				// 保存当前最旧一条聊天ID，用来下拉刷新；
	
	private OneChatBroadcast myBroadcast = null;		// 广播；
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_communication);

		getDataFromIntent();
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		
		getLatestChatInfoByWS(UserInfo.getInstance().studentID, this.teacherID);
//		generateData();
//		initView();		
	}
	
	public void getDataFromIntent(){
		this.teacherID = this.getIntent().getStringExtra("teacherID").trim();
		this.teacherName = this.getIntent().getStringExtra("teacherName").trim();
		this.teacherImgID = this.getIntent().getIntExtra("teacherImg", R.drawable.parent10);
		Log.i("lvjie", "teacherID="+teacherID+"  teacherName="+teacherName);
		
		UserCurrentState.getInstance().currentView = 1;
		UserCurrentState.getInstance().chatingID = this.teacherID;
	}

	public void initView() {
		
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText(this.teacherName);
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				UserCurrentState.getInstance().chatingID = "";
				UserCurrentState.getInstance().currentView = 0;	
				CommunicationActivity.this.finish();
			}
		});
		
		this.recieveInfoListView = (MyListView)super.findViewById(R.id.listView_communication);
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
				publishChatInfoToWS(sendInfo);			// 向数据库插入记录；
				
				Date date = new Date();
				String time = DateUtil.convertDateToString("HH:mm", date);
				ChartInfo chartInfo = new ChartInfo(sendInfo, time, true);
				listRecieveInfos.add(chartInfo);
				mAdapter.notifyDataSetChanged();
				sendInfoEditText.setText("");
			}
			
		});
		
		
		// 该广播注册监听器；
		this.myBroadcast = new OneChatBroadcast();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("mobile.onechat.parent");				// 监听一条广播；
		intentFilter.addCategory("mobile.action.onechat.parent");	// 添加广播的类型；		
		registerReceiver(this.myBroadcast, intentFilter);
	}
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		Log.i("lvjie", "MainActivity-->onRefresh()...");
		getLastTenChatInfoByWS(UserInfo.getInstance().studentID, this.teacherID, this.chatID);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}
	
	// 模拟数据  最初的5条；
	public void generateData(){
		for(int i=0;i<5;i++){
			
			Date date = new Date();
			String time = DateUtil.convertDateToString("HH:mm", date);
			
			if(i%2 == 0){
				ChartInfo chartInfo = new ChartInfo(i,"您好，"+i, time, true);
				this.listRecieveInfos.add(chartInfo);
			}else {
				ChartInfo chartInfo = new ChartInfo(i,"大家好，"+i, time, false);
				this.listRecieveInfos.add(chartInfo);
			}
			
		}
	}
	
	// 下拉刷新，产生数据；
	public void generateLastTenData() {
		this.listRecieveInfosLastTen.clear();
		for (int i = 0; i < 5; i++) {

			Date date = new Date();
			String time = DateUtil.convertDateToString("HH:mm", date);

			if (i % 2 == 0) {
				ChartInfo chartInfo = new ChartInfo(i, "今天上午通知好，" + i, time, true);
				this.listRecieveInfosLastTen.add(chartInfo);
			} else {
				ChartInfo chartInfo = new ChartInfo(i, "确定有通知么？" + i, time, false);
				this.listRecieveInfosLastTen.add(chartInfo);
			}

		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		UserCurrentState.getInstance().chatingID = "";
		UserCurrentState.getInstance().currentView = 0;	
		unregisterReceiver(myBroadcast);
	}
	
	private class MyAdapter extends BaseAdapter{

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
						R.layout.communication_listitem, null);
				viewChild = new ViewChild();
				/* 得到各个控件的对象 */
				viewChild.tvTime = (TextView) view.findViewById(R.id.time_textView);
				viewChild.tvReceiveInfo = (TextView) view.findViewById(R.id.recieveInfo_textView);
				viewChild.tvSendInfo = (TextView) view.findViewById(R.id.sendInfo_textView);
				viewChild.layoutMe = (RelativeLayout) view.findViewById(R.id.layout_me);
				viewChild.layoutOther = (RelativeLayout) view.findViewById(R.id.layout_other);
				viewChild.meImageView = (ImageView) view.findViewById(R.id.image_me);
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
				viewChild.meImageView.setBackgroundResource(R.drawable.parent12);
			}else {
				viewChild.layoutOther.setVisibility(View.VISIBLE);
				viewChild.layoutMe.setVisibility(View.GONE);
				viewChild.tvTime.setText(listRecieveInfos.get(position).getChartTime());
				viewChild.tvReceiveInfo.setText(listRecieveInfos.get(position).getChartContent());
				viewChild.otherImageView.setBackgroundResource(CommunicationActivity.this.teacherImgID);
			}

			return view;
		}
		
	}
	
	// 存放列表子项的控件，用于在MyAdapter中设置;
    public final class ViewChild {
        public TextView tvTime;
        public TextView tvSendInfo;
        public TextView tvReceiveInfo;
        
        public RelativeLayout layoutMe;
        public RelativeLayout layoutOther;
        
        public ImageView meImageView;
        public ImageView otherImageView;
    }

    /**
     * 获取最初的聊天信息
     * @param studentID
     * @param teacherID
     * @param chatID
     */
 	public void getLatestChatInfoByWS(String studentID, String teacherID){
 		new AsyncTask<Object, Object, DataTable>(){

 			protected void onPreExecute() {
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
 				
 				DataTable dt_chatInfoList = new DataTable();
				
				// 方法名
				String methodName = "APP_parent_getChatInfo";
				String studentID =  params[0].toString();
				String teacherID = params[1].toString();
				Log.i("lvjie", "studentID="+studentID+"    teacherID="+teacherID);
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("studentID", studentID);
				paramsMap.put("teacherID", teacherID);
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
								
							Log.i("lvjie", "chatID="+chatID+"  user="+user+"  chartContent="+chartContent);
							
							if(i==0){			// 获取最后一个消息编号；
								CommunicationActivity.this.chatID = chatID;
							}
							boolean isMeSend = (user==1)?true:false;
							

							chartInfo = new ChartInfo(chatID, chartContent, publishTime, isMeSend);
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
				if(!GeneralTools.getInstance().isOpenNetWork1(CommunicationActivity.this)){
					showToast("未连接到互联网，请检查网络配置!");
				}
 				initView();
 			};
 			
 		}.execute(studentID, teacherID);
 	}
 	
 
    /**
     * 获取接下来10条聊天信息,chatID设为最后的一条消息ID；
     * @param studentID
     * @param teacherID
     * @param chatID
     */
 	public void getLastTenChatInfoByWS(String studentID, String teacherID, int chatID){
 		new AsyncTask<Object, Object, DataTable>(){

 			protected void onPreExecute() {
 				
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
 				
 				DataTable dt_chatInfoList = new DataTable();
				
				// 方法名
				String methodName = "APP_parent_getChatInfo";
				// 存放参数的map
				Map<String, Object> paramsMap = new HashMap<String, Object>();
				paramsMap.put("studentID", params[0].toString());
				paramsMap.put("teacherID", params[1].toString());
				paramsMap.put("messageID", CommunicationActivity.this.chatID+"");
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
					dt_chatInfoList = getDataTool.getDataAsTable(methodName,
							paramsMap);
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
							int chatID = Integer.parseInt(result.getCell(i, "messageID"));
							int user = Integer.parseInt(result.getCell(i, "user"));
							String chartContent = result.getCell(i, "content");
							String publishTime = result.getCell(i, "publishTime");
							
							Log.i("lvjie", "chatID="+chatID+"  user="+user+"  chartContent="+chartContent);
							if(i==1){			// 获取最后一个消息编号；
								CommunicationActivity.this.chatID = chatID;
							}
							boolean isMeSend = (user==1)?true:false;
							

							chartInfo = new ChartInfo(chatID, chartContent, publishTime, isMeSend);
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
				if(!GeneralTools.getInstance().isOpenNetWork1(CommunicationActivity.this)){
					showToast("未连接到互联网，请检查网络配置!");
				}
				
				onLoad();
				listRecieveInfos.addAll(0, listRecieveInfosLastTen);
 				mAdapter.notifyDataSetChanged();
 				recieveInfoListView.setAdapter(mAdapter);
 				recieveInfoListView.setSelection(listRecieveInfosLastTen.size());	
 				
 				if(listRecieveInfosLastTen.size() == 0){
 					showToast("没有消息!");
 				}
 							
 			};
 			
 		}.execute(studentID, teacherID, chatID);
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
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				String result = " ";
				
				// 方法名
				String methodName = "APP_PushNotification_Message_insert";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				int len = params[0].toString().length();
				if(len>15){
					len = 15;
				}
				Log.i("lvjie", "len="+len+"  content_brief="+params[0].toString().substring(0, len));
				
				paramsMap.put("type", 4+"");					// 通知类型；4：表示一对一聊天;
				paramsMap.put("title", "来自："+UserInfo.getInstance().childName.trim()+"家长");	// 必须用这种格式，后台服务需要对接收的消息处理；
				paramsMap.put("content", params[0].toString());
				paramsMap.put("content_brief", params[0].toString().substring(0, len));	// 通知内容简介；
				paramsMap.put("receiver", CommunicationActivity.this.teacherID);						// 接收者，这里为老师ID；
				paramsMap.put("publisherID", UserInfo.getInstance().studentID);
				paramsMap.put("publisherName", UserInfo.getInstance().childName.trim());
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
						String chatID = result.split("\\$")[0];
						Log.i("lvjie", "2-->******chatID="+chatID);
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
 		Toast toast =  Toast.makeText(CommunicationActivity.this, toastMessage, Toast.LENGTH_SHORT);
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
	
	private class OneChatBroadcast extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String chatInfo = intent.getStringExtra("chatInfo");
			String time = intent.getStringExtra("chatTime");
			Log.i("lvjie", "中间件发送过来的聊天信息：  chatInfo="+chatInfo+"  time="+time);
			ChartInfo chartInfo = new ChartInfo(chatInfo, time, false);
			listRecieveInfos.add(chartInfo);
			mAdapter.notifyDataSetChanged();
		}
		
	}
 	
}
