package com.ttqeducation.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.R;
import com.ttqeducation.activitys.message.CommunicationActivity;
import com.ttqeducation.activitys.message.NoticeDetailActivity;
import com.ttqeducation.activitys.system.MainActivity;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.UserCurrentState;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.tools.DateUtil;
import com.ttqeducation.tools.DesUtil;
import com.ttqeducation.tools.Tools;

import android.R.bool;
import android.R.integer;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.Notification.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PushService extends Service {

	// 设置静态变量，确定是属于哪种消息；
	public static final int NOTICEID = 1111;
	public static final int LOGIN_REQUEST = 0;			// 发送登陆请求;
	public static final int LOGIN_RESPONSE = 1;			// 登陆请求反馈;
	public static final int HEARTBEAT_REQUEST = 2;		// 发送心跳请求;
	public static final int PUSH_REQUEST = 3;		// 用户发送推送消息请求；
	public static final int PUSH_RESPONSE = 4;		// 推送消息请求反馈；
	public static final int SENDMESSAGE = 5;		// 中间件向用户推送消息；
	
//	private String deviceID = "";
	private String androidId = "";
	
	// 用来产生随机数；
	private static final Random random = new Random(System.currentTimeMillis());
	private String receiveMsg = "";			// 保存接收到的消息；
	private SocketClient socketClient = null;	// 连接服务端的socket;
	private boolean isCloseSocketNormal = false;	// 是否正常关闭socket连接；
	private boolean isFirstConnection = true;		// 是否为第一次连接socket;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("lvjie", "PushService()-->onBind()...");
		return null;
	}

	@Override
	/**
	 * onCreate()--->onStartCommand()
	 * 当系统不存在这个服务的时,启动服务时会执行该方法；
	 */
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.i("lvjie", "PushService()-->onCreate()...");
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		// 获取设备编号，平板暂时获取不了；
//		deviceID = telephonyManager.getDeviceId();
		androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
		Log.i("lvjie", "androidId="+androidId);
		new Thread(new Runnable() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
				String pushingServiceIP = pre.getString("pushingServiceIP", "");
				String pushingServicePort = pre.getString("pushingServicePort", "0");
				int port = Integer.parseInt(pushingServicePort);
				Log.i("lvjie", "中间件服务：pushingServiceIP="+pushingServiceIP+"  pushingServicePort="+pushingServicePort);
				socketClient = new SocketClient(pushingServiceIP, port);
				socketClient.start();

			}
		}).start();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("lvjie", "PushService()-->onDestroy()...");
		this.isCloseSocketNormal = true;
		if(this.socketClient != null){
			this.socketClient.closeSocketAndHeart();
		}		
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i("lvjie", "PushService()-->onStartCommand()...flags="+flags+"  startId="+startId);
		return START_STICKY;
	}
	
	/***** socket类开始 ******/
	/**
	 * socket连接解决办法；
	 * 1、程序首先启动心跳，然后连接socket，
	 * 2、当手机没有连接网络时启动软件，socket连接失败，心跳会在一次启动socket连接
	 * 同时关闭该心跳，启动一条新的心跳，检查是否连接成功，不成功则重复以上；
	 * 3、当socket连接成功，则心跳的作用是每隔固定时间向中间件发送信息；
	 * @author lvjie
	 *
	 */
	private class SocketClient{
			
		private Timer heartTimer = null;
		
		// 用来读取服务端的信息
		private DataOutputStream writer = null;
		private DataInputStream reader = null;
		private Socket socket = null;
		private boolean isConnection = false;		// 是否正在连接;
		
		private String ipAddress = "";		// IP地址
		private int port=0;					// 端口号
		
		public SocketClient(String ipAddress, int port){
			this.ipAddress = ipAddress;
			this.port = port;
			
		}
		
		// 启动socket连接；
		public void start(){
			try {
				
				if(isFirstConnection){			// 第一次启动连接，启动心跳，以后出现异常再次启动，不会再启动心跳；
					startHeartTimer();			// 启动心跳;
					Log.i("lvjie", "第一次连接，开启心跳.....");
					isFirstConnection = false;
				}else {
					startHeartTimer();
				}
				
				socket = new Socket(ipAddress, port);
				isConnection = true;
				Log.i("lvjie", "连接成功......isFirstConnection="+isFirstConnection);
				
				// 从本地获取学生ID；
				SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
				String studentID = pre.getString("user_account", "");
				writer = new DataOutputStream(socket.getOutputStream());
				reader = new DataInputStream(socket.getInputStream());
				String sendInfoString = studentID.trim()+","+androidId+","+1;
				sendInfo(sendInfoString,PushService.LOGIN_REQUEST);		// 0表示登陆
								
				startServerReplyListener(reader);

				Log.i("lvjie", "能运行到这吗?..应该不会吧...可以的，线程正常结束...");
			} catch (Exception e) {
				
				if(isCloseSocketNormal){		// 是正常关闭socket的；
					// 什么也不需做；
					Log.i("lvjie", "线程结束...");
				}else {
					Log.i("lvjie", "连接失败，线程结束......isConnection="+isConnection);
					closeSocket();		// 当出现异常，需要关闭socket；
					isCloseSocketNormal = false;
				}				
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
				receiveMsg = rec.split("\\$_")[0];
				Log.i("lvjie", "从服务端接收的消息：receiveMsg="+receiveMsg);
				
				if(type == 5){		// type==5 表示是中间件推送过来的消息；
					analysisReceiveMsg();
				}else if(type == 8){		// 表示在另一个手机上登陆了，当前手机需要关闭退出；
					
					showOffLineView();
				}			
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
					
					convertBytesToString(sendBytes);
					
					this.writer.write(sendBytes);
					this.writer.flush();
					Log.i("lvjie", "客户端成功发送的消息为："+sendStr+"  isConnection="+isConnection);
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
		
		// 启动一个心跳，保持与服务端的连接；
		public void startHeartTimer(){
			
			this.heartTimer = new Timer();
			heartTimer.schedule(new TimerTask() {			
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						sendInfo("h",PushService.HEARTBEAT_REQUEST);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						// 出现异常，说明socket断开连接了,就需要重新连接；
						Log.i("lvjie", "会运行到心跳异常么？");
						closeSocketAndHeart();
						start();		// 再一次启动socket连接；
						Log.i("lvjie", "start()-->再一次启动socket连接；");
						
						e.printStackTrace();
					}
				}
			}, 1000*10, 1000*20);		// 程序运行到这，延迟10秒发送消息，以后每隔20秒发送一次消息；
		}
		
		// 关闭socket和心跳；
		public void closeSocketAndHeart(){
			
			try {
				isConnection = false;
					
				// 关闭心跳；
				if(heartTimer != null){							
					heartTimer.cancel();	// 关闭心跳；
					Log.i("lvjie", "心跳正常关闭...");
				}else {
					Log.i("lvjie", "心跳出现异常...");
				}
				
				// 关闭输入输出流；
				if(!socket.isInputShutdown()){
					socket.shutdownInput();
				}
				if(!socket.isOutputShutdown()){
					socket.shutdownOutput();
				}
				if(reader!=null){
					reader.close();
				}
				if(writer!=null){
					writer.close();
				}
				if(!socket.isClosed()){
					socket.close();							
				}
				Log.i("lvjie", "正常关闭连接...");
								
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.i("lvjie", "关闭连接异常...");						
				e.printStackTrace();
			}

		}
	
		// 关闭socket;
		public void closeSocket(){
			if(reader!=null||writer!=null||socket!=null){
				try {
					socket.shutdownInput();
					socket.shutdownOutput();
					reader.close();
					writer.close();
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.i("lvjie", "正常关闭连接...");
			}else {
				Log.i("lvjie", "socket出现异常...");
			}
		}
		
	}
	/*****   socket类结束    ******/
	
	// 解析接收到的消息的字符串，前面已经把二进制数据转化成了字符串；
	private void  analysisReceiveMsg(){
		Log.i("lvjie", "analysisReceiveMsg()...");
		if(PushService.this.receiveMsg != null){
			if(!PushService.this.receiveMsg.equals("")){
				Log.i("lvjie", "receiveMsg="+PushService.this.receiveMsg);
				String tempStr[] = PushService.this.receiveMsg.split("\\$");
				Log.i("lvjie", "analysisReceiveMsg()...len="+tempStr.length);
				showReceiveMsgInView(tempStr);			
			}
		}
	}
	
	// 把中间件推送过来的消息显示到通知栏上；
	private void showReceiveMsgInView(String[] revieve){
		int messageID = 0;
		int messageType = 0;
		String messageTitle = "";
		String messageContent = "";
		if(revieve.length>=4){
			messageID = Integer.parseInt(revieve[0]);
			messageType = Integer.parseInt(revieve[1]);
			messageTitle = revieve[2];
			messageContent = revieve[3];
		}else {
			return;
		}	
		switch (messageType) {
		case 1:		// 表示班级通知公告；
			Log.i("lvjie", "showReceiveMsgInView()...");
			Intent intentClassNotice = new Intent(PushService.this,NoticeDetailActivity.class);
			intentClassNotice.putExtra("noticeID", messageID); // 把参数传递到消息内容显示页面；
			intentClassNotice.putExtra("viewInitFrom", 2);
			showNotify(intentClassNotice, messageContent, messageTitle, messageContent);
			break;
		case 2:		// 表示学校通知公告；
			Intent intentSchoolNotice = new Intent(PushService.this,NoticeDetailActivity.class);
			intentSchoolNotice.putExtra("noticeID", messageID); // 把参数传递到消息内容显示页面；
			intentSchoolNotice.putExtra("viewInitFrom", 2);
			showNotify(intentSchoolNotice, messageContent, messageTitle, messageContent);
			break;
		case 3:		// 表示系统通知公告；
			Intent intentSystemNotice = new Intent(PushService.this,NoticeDetailActivity.class);
			intentSystemNotice.putExtra("noticeID", messageID); // 把参数传递到消息内容显示页面；
			intentSystemNotice.putExtra("viewInitFrom", 2);
			showNotify(intentSystemNotice, messageContent, messageTitle, messageContent);
			break;
		case 4:		// 表示一对一聊天；
			exeOneChatInfo(messageID, messageContent, messageTitle, revieve[4]);
			break;	

		default:
			break;
		}
	}
	
	// 在状态栏能够显示消息，当点击 跳转到界面上；
	private void showNotify(Intent intent,String ticker,String title,String content) {
		
		// 弹出通知栏
		int i = random.nextInt();
		PendingIntent pendingIntent = PendingIntent.getActivity(
				PushService.this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification.Builder builder = new Builder(PushService.this)
				.setTicker(ticker).setSmallIcon(com.ttqeducation.R.drawable.ic_launcher)
				.setWhen(System.currentTimeMillis()).setContentTitle(title)
				.setContentText(content)
				.setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true)
				.setContentIntent(pendingIntent);

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = builder.getNotification();
		int j = random.nextInt();
		notificationManager.notify(j, notification);

	}

	// 当接收到下线消息，则进入主界面，并提示下线对话框；
	private void showOffLineView(){
		Log.i("lvjie", "PushService-->成功被挤下线了...");

		this.isCloseSocketNormal = true;
		this.socketClient.closeSocketAndHeart();

		myHandler.sendEmptyMessage(1);
		
		Log.i("lvjie", "showOffLineView().....end");
		
	}
	
	private Handler myHandler = new Handler(){	
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Log.i("lvjie", "PushService-->myHandler = new Handler()...");
				Intent intent = new Intent(PushService.this, MainActivity.class);
				intent.putExtra("viewfrom", 2);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				break;

			default:
				break;
			}
		};
		
	};
	
	// 处理一对一聊天发送过来的消息；
	private void exeOneChatInfo(int chatID, String chatInfo, String chatTilte, String chatingID){
		// 表示当前用户正处在一对一聊天界面且表示发送过来的消息者正好是当前聊天的消息人；
		if(UserCurrentState.getInstance().currentView == 1 && UserCurrentState.getInstance().chatingID.equals(chatingID)){		
			
			getNoticeContentByWS(1, chatID, UserInfo.getInstance().studentID);
		}else {
			String teacherName = chatTilte.split("来自：")[1];
			Intent chatIntent = new Intent(PushService.this, CommunicationActivity.class);
			chatIntent.putExtra("teacherID", chatingID);
			chatIntent.putExtra("teacherName", teacherName);
			chatIntent.putExtra("teacherImg", R.drawable.parent11);
			chatIntent.putExtra("viewInitFrom", 2);
			Log.i("lvjie", "从中间件启动的界面-->teacherName="+teacherName+"  chatingID="+chatingID);
			showNotify(chatIntent, chatInfo, chatTilte, chatInfo);
		}
	}
	
	private void getNoticeContentByWS(final int type, int noticeID, String studentID){
		new AsyncTask<Object, Object, DataTable>() {
			protected void onPreExecute() {
				super.onPreExecute();
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
					dt_notice = getDataTool.getDataAsTable(methodName,paramsMap);
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
							String time = result.getCell(i, "publishTime");
							String noticeContent = result.getCell(i, "content");
//							String publisherName = result.getCell(i, "publisherName");
//							int noReadNum = Integer.parseInt(result.getCell(i, "unReadNotificationNum"));
							String publishTime = time.replace("T", " ");
							Log.i("lvjie", "noticeContent="+noticeContent+"  publishTime="+publishTime);
							try {
								Date date = DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", publishTime);
								publishTime = DateUtil.convertDateToString("yyyy年MM月dd日 HH:mm", date);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Log.i("lvjie", "PushService-->发送一条广播...");
							if(type == 1){		// 表示的是一对一聊天；
								Intent intent = new Intent("mobile.onechat.parent");		// 设置广播名称；
								intent.addCategory("mobile.action.onechat.parent");		// 设置广播种类；
								intent.putExtra("chatInfo", noticeContent);				// 广播传递的值；
								intent.putExtra("chatTime", publishTime);				// 广播传递的值；
								sendBroadcast(intent);
							}						
							Log.i("lvjie", "PushService-->发送一条广播成功...");
						
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			};
			
		}.execute(noticeID, studentID);
	}
	
}
