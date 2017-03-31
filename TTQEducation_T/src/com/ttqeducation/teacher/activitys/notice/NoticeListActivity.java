package com.ttqeducation.teacher.activitys.notice;

/**
 *  通知公告：显示通知列表，按时间条件及通知名称查询进入下一个详情页面；
 *  @author 高雨寒
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.NoticeListItem;
import com.ttqeducation.teacher.beans.TeacherInfo;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.MyListView;
import com.ttqeducation.teacher.myViews.MyListView.IMyListViewListener;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.tools.DateUtil;
import com.ttqeducation.teacher.tools.DesUtil;
import com.ttqeducation.teacher.tools.GeneralTools;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class NoticeListActivity extends Activity implements IMyListViewListener{
	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；
	private LinearLayout layoutSendNoticeBtn = null;	// 发送通知按钮；
	
	private RefreshView refreshView = null;
	private MyListView myListView = null;
	private MyAdapter mAdapter = null; // 得到一个MyAdapter对象,用于与listview绑定；
	// 用来存放 时间及时间描述；
    private List<NoticeListItem> listNoticeListItems = new ArrayList<NoticeListItem>();
    private String endPublishTime = "";			// 保存界面上显示的最后一条通知公告的发布时间；
    private String classID = "";

    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice_list);
		getDataFromIntent();
				
		initView();
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		getNoticeListByWS(this.classID, TeacherInfo.getInstance().execTeacherID);
		
	}
    
    public void getDataFromIntent(){
    	this.classID = getIntent().getStringExtra("classID");
    	Log.i("lvjie", "NoticeListActivity-->getDataFromIntent()...classID="+this.classID);
    }
	
	public void initView() {
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("通知公告");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				NoticeListActivity.this.finish();
			}
		});
		this.layoutSendNoticeBtn = (LinearLayout) super.findViewById(R.id.action_bar).findViewById(R.id.layout_rightBtn);
		this.layoutSendNoticeBtn.setVisibility(View.VISIBLE);
		this.layoutSendNoticeBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 进入到发布界面；
				Intent sendInfoIntent = new Intent(NoticeListActivity.this, NoticePublishActivity.class);
				sendInfoIntent.putExtra("classID", classID);
				startActivityForResult(sendInfoIntent, 100);				
			}
		});
		
		
		this.myListView = (MyListView) super.findViewById(R.id.listView_noticelist_detail);
		this.myListView.setMyListViewListener(this);
		this.myListView.setPullRefreshEnable(false); // 不提供下拉刷新功能；
		this.myListView.setPullLoadEnable(true); // 提供上拉加载功能；
		this.myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			// 列表的点击事件；
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				position--;			// 从0开始；
				String dateString = ((TextView) view
						.findViewById(R.id.notice_source_textview))
						.getText().toString();  //获得要传给下一页的参数
				Intent intent = new Intent(NoticeListActivity.this,NoticeDetailActivity.class);
				Log.i("lvjie", "position="+position+"  noticeID="+listNoticeListItems.get(position).getNoticeID());
				intent.putExtra("noticeID", listNoticeListItems.get(position).getNoticeID()); // 把参数传递到消息内容显示页面；
				intent.putExtra("viewInitFrom", 1);
				startActivity(intent);
				
				listNoticeListItems.get(position).setRead(true);
				NoticeListActivity.this.mAdapter.notifyDataSetChanged();
			}
		});
	}
	
	// 该函数已经把日期产生成功了；
	public void generateData() {
		NoticeListItem listNoticeListItem;
		Date date = new Date();
		for (int i = 1; i < 15; i++) {
			if (i%3 == 0) {
				String dateString = "学校通知";
				String dataSummary="家长会通知请按时到达。。。";
				listNoticeListItem = new NoticeListItem(i,dateString, dataSummary, "2015年3月"+i+"日"+" 15：30", false);
				
			} else if (i%3 == 1) {
				String dateString = "班级通知";
				String dataSummary="请值日的同学打扫公共卫生。。。";
				listNoticeListItem = new NoticeListItem(1, dateString, dataSummary, "2015年3月"+i+"日"+" 7：30",false);
			} else if (i%3 == 2) {
				String dateString = "学校通知";
				String dataSummary= "家长会通知请按时到达。。。";
				listNoticeListItem = new NoticeListItem(1, dateString, dataSummary, "2015年3月"+i+"日"+" 15：30",true);
			} else {
				String dateString = "班级通知";
				String dataSummary= "请值日的同学打扫公共卫生。。。";
				listNoticeListItem = new NoticeListItem(1, dateString, dataSummary, "2015年3月"+i+"日"+" 7：30", true);
			}
			listNoticeListItems.add(listNoticeListItem);
			date = DateUtil.getNextDay(date);
		}
	}
	
	// 从服务器获取最新的通知公告列表；
	public void getNoticeListByWS(String classID, String teacherID){
		
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
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				DataTable dt_noticeList = new DataTable();
				
				// 方法名
				String methodName = "APP_getLatestPushNotification_teacher";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("classID", params[0].toString());
				paramsMap.put("teacherID", params[1].toString());
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
					dt_noticeList = getDataTool.getDataAsTable(methodName,
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
				return dt_noticeList;
			}
			
			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				if (result != null) {
					NoticeListItem noticeList;
					int count = result.getRowCount();
					for (int i = 0; i < count; i++) {
						try {
							int noticeID = Integer.parseInt(result.getCell(i, "messageID"));
							boolean isRead = Boolean.parseBoolean(result.getCell(i, "isRead"));
							String noticeTitle = result.getCell(i, "title");
							String noticeContent = result.getCell(i, "content_brief");
							String publishTime = result.getCell(i, "publishTime");
							
							Log.i("lvjie", "noticeID="+noticeID+"  isRead="+isRead+"  noticeTitle="+noticeTitle);
							if(i==count-1){			// 获取最后一个时间；
								endPublishTime = publishTime;
							}

							noticeList = new NoticeListItem(noticeID, noticeTitle, noticeContent, publishTime, isRead);
							listNoticeListItems.add(noticeList);
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				// 关闭刷新；
				refreshView.dismiss();
				
				// 没有联网，不可以进入到下一个界面；
				if(!GeneralTools.getInstance().isOpenNetWork1(NoticeListActivity.this)){
					showToast("未连接到互联网，请检查网络配置!");
				}
				
				// 获取完数据后，初始化界面
				mAdapter = new MyAdapter(NoticeListActivity.this);
				myListView.setAdapter(mAdapter);
			}
			
		}.execute(classID, teacherID);
	}
	
	@Override
	public void onRefresh() {		// 下拉刷新触发事件；
		// TODO Auto-generated method stub
		Log.i("lvjie", "onRefresh()...");
	}

	@Override
	public void onLoadMore() {		// 上拉加载触发事件；
		// TODO Auto-generated method stub
		Log.i("lvjie", "onLoadMore()..."+TeacherInfo.getInstance().execTeacherID+"    "+endPublishTime);
		getNoticeListNextTenByWS(this.classID, TeacherInfo.getInstance().execTeacherID, endPublishTime);
	}
	
	// 从服务器获取接下来十条通知公告，用于上拉加载；
	public void getNoticeListNextTenByWS(String classID, String teacherID, String publishTime){
		
		new AsyncTask<Object, Object, DataTable>(){

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}
			
			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				DataTable dt_noticeList = new DataTable();
				try {
					Thread.sleep(2000); // 测试代码，之后需删掉
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// 方法名
				String methodName = "APP_getPushNotification_nextFifteen_teacher";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("classID", params[0].toString());
				paramsMap.put("teacherID", params[1].toString());
				paramsMap.put("time", params[2].toString());
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
					dt_noticeList = getDataTool.getDataAsTable(methodName,paramsMap);
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
				return dt_noticeList;
			}
			
			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				int count = 0;
				if (result != null) {
					NoticeListItem noticeList;
					count = result.getRowCount();
					for (int i = 0; i < count; i++) {
						try {
							int noticeID = Integer.parseInt(result.getCell(i, "messageID"));
							boolean isRead = Boolean.parseBoolean(result.getCell(i, "isRead"));
							String noticeTitle = result.getCell(i, "title");
							String noticeContent = result.getCell(i, "content_brief");
							String publishTime = result.getCell(i, "publishTime");
							
							if(i==count-1){			// 获取最后一个时间；
								endPublishTime = publishTime;
							}

							noticeList = new NoticeListItem(noticeID, noticeTitle, noticeContent, publishTime, isRead);
							listNoticeListItems.add(noticeList);
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				// 获取完数据后，初始化界面
				mAdapter.notifyDataSetChanged();  // listItem数据有增加，只需调用该函数，界面会发生改变；
				onLoad();		// 停止上拉加载；
				if(count == 0){
					showToast("没有通知公告!");
				}
			}
			
		}.execute(classID, teacherID,  publishTime);
	}
	
	private void onLoad() {
		myListView.stopRefresh();
		myListView.stopLoadMore();
		myListView.setRefreshTime("刚刚");
	}
	
	// 窗口提示信息；
	public void showToast(String toastMessage){
		Toast toast =  Toast.makeText(NoticeListActivity.this, toastMessage, Toast.LENGTH_SHORT);
		toast.show();
	}
	
    //新建一个类继承BaseAdapter，实现listview与数据的绑定
	private class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater; // 得到一个LayoutInfalter对象用来导入布局

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return listNoticeListItems.size(); // 返回数组的长度
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewChild viewChild;
			if (view == null) {
				view = mInflater.inflate(
						R.layout.notice_list_item_one, null);
				viewChild = new ViewChild();
				/* 得到各个控件的对象 */
				viewChild.noticeSource = (TextView) view
						.findViewById(R.id.notice_source_textview);
				viewChild.noticePublishTime = (TextView) view
						.findViewById(R.id.notice_publishTime_textview);
				viewChild.noticeSummary = (TextView) view
						.findViewById(R.id.notice_summary_textview);
				viewChild.newImageView = (ImageView)view.findViewById(R.id.new_img);
				viewChild.leftBottomImageView = (ImageView) view.findViewById(R.id.leftButtom_line);
				viewChild.leftTopImageView = (ImageView) view.findViewById(R.id.leftTop_line);
				view.setTag(viewChild); // 绑定ViewHolder对象
			} else {
				viewChild = (ViewChild) view.getTag(); // 取出ViewHolder对象
			}

			/* 设置TextView显示的内容，即我们存放在listNoticeListItems中的数据 */
			viewChild.noticeSource.setText(listNoticeListItems.get(position).getNoticeTitle());
			viewChild.noticePublishTime.setText(listNoticeListItems.get(position).getPublishTime());
			viewChild.noticeSummary.setText(listNoticeListItems.get(position).getNoticeContent());
			
			// 竖线显示的设置；
			if(position == 0){
				viewChild.leftTopImageView.setVisibility(View.INVISIBLE);
				viewChild.leftBottomImageView.setVisibility(View.VISIBLE);
			}else if(position == listNoticeListItems.size()-1){
				viewChild.leftTopImageView.setVisibility(View.VISIBLE);
				viewChild.leftBottomImageView.setVisibility(View.INVISIBLE);
			}else {
				viewChild.leftTopImageView.setVisibility(View.VISIBLE);
				viewChild.leftBottomImageView.setVisibility(View.VISIBLE);
			}
			
			// 是否显示new
			if(listNoticeListItems.get(position).isRead()){		// 如果是已读；则不显示new
				viewChild.newImageView.setVisibility(View.INVISIBLE);	// 隐藏；
			}else {
				viewChild.newImageView.setVisibility(View.VISIBLE);	// 显示；
			}
			return view;
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
		
		// 存放列表子项的控件，用于在MyAdapter中设置;
		public final class ViewChild {
			public TextView noticeSource; // 消息来源;
			public TextView noticePublishTime; // 消息发布的时间；
			public TextView noticeSummary;  //消息的概要文字；
			public ImageView newImageView;	// 新消息图片；
			
			public ImageView leftTopImageView;	// 左边图片上部分竖线；
			public ImageView leftBottomImageView;	// 左边图片下部分竖线；
		}

	}
	
	@Override
	// 当通知公告发布成功，返回到这，执行该方法；
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == 100){
			if(resultCode == 1){		// 表示的发布成功，返回；
				Log.i("lvjie", "NoticeListActivity--->onActivityResult()...发布通知成功~");
				
				this.listNoticeListItems.clear();
				getNoticeListByWS(this.classID, TeacherInfo.getInstance().execTeacherID);
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

}
