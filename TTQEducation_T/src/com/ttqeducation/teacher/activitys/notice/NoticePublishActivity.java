package com.ttqeducation.teacher.activitys.notice;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.TeacherInfo;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.tools.DesUtil;
import com.ttqeducation.teacher.tools.Tools;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NoticePublishActivity extends Activity {

	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；
	private LinearLayout layoutPublishNoticeBtn = null;	// 发送通知按钮；
  
	private EditText noticeTitleEditText = null;
	private EditText publisherNameEditText = null;
	private EditText noticeContentEditText = null;
	
	private RefreshView refreshView = null;
	private String classID = "";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice_publish);
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		getDataFromIntent();
		
		initView();
	}
	
	public void getDataFromIntent(){
		this.classID = getIntent().getStringExtra("classID");
		Log.i("lvjie", "NoticePublishActivity-->getDataFromIntent()..."+this.classID);
	}
	
	public void initView(){
		// 标题栏初始化;
		this.titleBackLayout = (LinearLayout) super.findViewById(R.id.title_back_layout);
		this.titleTextView = (TextView) super.findViewById(R.id.title_text);
		this.layoutPublishNoticeBtn = (LinearLayout) super.findViewById(R.id.layout_publishNoticeBtn);
				
		this.noticeTitleEditText = (EditText) super.findViewById(R.id.noticeTitle);
		this.publisherNameEditText = (EditText) super.findViewById(R.id.noticePublisherName);
		this.noticeContentEditText = (EditText) super.findViewById(R.id.noticeContent);
		
		this.noticeTitleEditText.setText("班级通知");
		this.publisherNameEditText.setText(TeacherInfo.getInstance().teacherName+"老师");
		
		this.titleBackLayout.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		this.layoutPublishNoticeBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 发布通知公告；
				String noticeTitle = NoticePublishActivity.this.noticeTitleEditText.getText().toString().trim();
				String publisherName = NoticePublishActivity.this.publisherNameEditText.getText().toString().trim();
				String noticeContent = NoticePublishActivity.this.noticeContentEditText.getText().toString().trim();
				
				if(noticeTitle.equals("")){
					showToast("通知标题不能为空");
					return;
				}
				if(publisherName.equals("")){
					showToast("发布者姓名不能为空");
					return;
				}
				if(noticeContent.equals("")){
					showToast("发布内容不能为空");
					return;
				}
				
				publishNoticeToWS(noticeTitle, noticeContent);
				
			}
		});
		
	}
	
	public void showToast(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	// 发布通知公告；
	public void publishNoticeToWS(String noticeTitle, String noticeContent){
		new AsyncTask<Object, Object, String>(){

			@Override
			protected void onPreExecute() {
				refreshView.show();
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
				int len = params[1].toString().length();
				if(len>15){
					len = 15;
				}
				len--;
				Log.i("lvjie", "len="+len+"  title="+params[0].toString()+"  content_brief="+params[1].toString().substring(0, len));
				
				paramsMap.put("type", 1+"");					// 通知类型；1：表示班级通知;
				paramsMap.put("title", params[0].toString());	
				paramsMap.put("content", params[1].toString());
				paramsMap.put("content_brief", params[1].toString().substring(0, len));	// 通知内容简介；
				paramsMap.put("receiver", classID);						// 接收者，这里为班级ID；
				paramsMap.put("publisherID", TeacherInfo.getInstance().execTeacherID);
				paramsMap.put("publisherName", TeacherInfo.getInstance().teacherName);
				paramsMap.put("TokenID", tokenID);
				Log.i("lvjie", "title="+params[0].toString()+"  content_brief="+params[1].toString().substring(0, len));
				
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
//					dt_notice = getDataTool.getDataAsTable(methodName, paramsMap);
//					String noticeID = dt_notice.getCell(0, "messageID");
					result = getDataTool.getDataAsString(methodName, paramsMap);
					
					Log.i("lvjie", "2-->******"+result);
					String noticeID = result.split("\\$")[0];
					Log.i("lvjie", "3-->******noticeID="+noticeID);
					sendNoticeInfoToMiddleWare(noticeID);		// 启动与中间件通信， 以后不能注释；
					Log.i("lvjie", "成功向中间件发送通知公告.....");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return result;
			}
			
			@Override
			protected void onPostExecute(String result) {
				refreshView.dismiss();
				if(result != null){
					showToast("发布成功");
//					Intent intent = new Intent();
					setResult(1);
					finish();
				}else {
					showToast("发布失败");
				}
			}
			
		}.execute(noticeTitle, noticeContent);
	}
	
	// 向中间件发送消息；
	public void sendNoticeInfoToMiddleWare(final String noticeID){
		SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
		String pushingServiceIP = pre.getString("pushingServiceIP", "");
		String pushingServicePort = pre.getString("pushingServicePort", "0");
		int port = Integer.parseInt(pushingServicePort);
		if(port == 0){
			return;
		}
		Log.i("lvjie", "发送通知：pushingServiceIP="+pushingServiceIP+"   pushingServicePort="+pushingServicePort);
		SocketClient socketClient = new SocketClient(pushingServiceIP, port, noticeID);	// hxy
		socketClient.start();
		
	}
	
	/***** socket类开始 ******/
	/**
	 * socket连接解决办法；
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
		private String noticeID = "";
		
		public SocketClient(String ipAddress, int port, String noticeID){
			this.ipAddress = ipAddress;
			this.port = port;
			this.noticeID = noticeID;
		}
		
		// 启动socket连接；
		public void start(){
			try {
				
				Log.i("lvjie", "1-->连接成功......");
				socket = new Socket(ipAddress, port);
				this.isConnection = true;
				Log.i("lvjie", "连接成功......");
				
				writer = new DataOutputStream(socket.getOutputStream());
//				reader = new DataInputStream(socket.getInputStream());
				String sendInfoString = this.noticeID;
				sendInfo(sendInfoString, 12);		// 12表示向中间件发送通知消息；
								
//				startServerReplyListener(reader);
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
			
		// 关闭socket和心跳；
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

	
}
