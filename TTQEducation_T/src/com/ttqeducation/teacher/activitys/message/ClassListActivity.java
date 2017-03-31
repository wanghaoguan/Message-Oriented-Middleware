package com.ttqeducation.teacher.activitys.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.beans.ClassUnReadMsgInfo;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.TeacherInfo;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.MyListView;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.network.GetDataByWS;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 群聊  班级列表界面；
 * 用的xml与通知公告中的班级列表界面相同；
 * @author lvjie
 *
 */
public class ClassListActivity extends Activity{
	
	// 标题栏部分；
	private TextView titleTextView = null;
	private LinearLayout titleBackLayout = null;
	
	private List<ClassUnReadMsgInfo> listClassUnReadMsgInfos = new ArrayList<ClassUnReadMsgInfo>();
	private MyListView myListView = null;
	private MyAdapter mAdapter = null; // 得到一个MyAdapter对象,用于与listview绑定；
	
	private RefreshView refreshView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notice_fragment);

		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		getClassInfoByWS(TeacherInfo.getInstance().execTeacherID);
		
//		generateData();
//		initView();
	}

	
	public void initView(){
		
		// 标题栏部分 实例化；
		super.findViewById(R.id.action_bar).setVisibility(View.VISIBLE);
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("教师交流群");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				clickBack();
			}
		});
				
		this.myListView = (MyListView) super.findViewById(R.id.listView_notice);
		this.myListView.setPullRefreshEnable(false); // 不提供下拉刷新功能；
		this.myListView.setPullLoadEnable(false); // 不提供上拉加载功能；
		this.myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			// 列表的点击事件；
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(position != 0){
					position--;
				}
				Log.i("lvjie","ClassListActivity-->initView()..."+listClassUnReadMsgInfos.size()+"    "+position);
				Intent groupChatIntent = new Intent(ClassListActivity.this, GroupChatActivity.class);
				groupChatIntent.putExtra("classID", listClassUnReadMsgInfos.get(position).getClassID());
				groupChatIntent.putExtra("className", listClassUnReadMsgInfos.get(position).getClassName());
				startActivity(groupChatIntent);
				
				listClassUnReadMsgInfos.get(position).setUnReadMsgNum(0+"");
				ClassListActivity.this.mAdapter.notifyDataSetChanged();
			}
		});
		this.mAdapter = new MyAdapter(this);
		this.myListView.setAdapter(this.mAdapter);
	}
	
	public void generateData(){
		ClassUnReadMsgInfo classUnReadMsgInfo;
		for(int i=0; i<5; i++){
			classUnReadMsgInfo = new ClassUnReadMsgInfo(""+i, "班级  "+i, ""+i);
			this.listClassUnReadMsgInfos.add(classUnReadMsgInfo);
		}
	}
	
	public void clickBack(){
		this.finish();
	}
	
	// 通过老师编号，获取对应的班级信息；
	public void getClassInfoByWS(String execTeacherID){
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
				
				DataTable dt_classInfoList = new DataTable();
			
				// 方法名
				String methodName = "APP_teacherClassChat";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String teacherID = params[0].toString();
				Log.i("lvjie", "传递参数： teacherID="+teacherID);
				paramsMap.put("teacherID", teacherID);
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
					dt_classInfoList = getDataTool.getDataAsTable(methodName,
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
				return dt_classInfoList;
			}
			
			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				if (result != null) {
					ClassUnReadMsgInfo classUnReadMsgInfo;
					int count = result.getRowCount();
					for (int i = 0; i < count; i++) {
						try {
							String classID = result.getCell(i, "classID");
							String className = result.getCell(i, "className");
							String unRead = result.getCell(i, "unRead");
							
							Log.i("lvjie", "classID="+classID+"  className="+className+"  unRead="+unRead);

							classUnReadMsgInfo = new ClassUnReadMsgInfo(classID, className, unRead);
							listClassUnReadMsgInfos.add(classUnReadMsgInfo);
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				// 关闭刷新；
				refreshView.dismiss();
				
				// 没有联网，不可以进入到下一个界面；
				if(!GeneralTools.getInstance().isOpenNetWork1(ClassListActivity.this)){
					showToast("未连接到互联网，请检查网络配置!");
				}
				
				initView();
			}
			
			
		}.execute(execTeacherID);
	}
	
	/********* 班级列表界面的 listview 数据绑定 类 开始  ***********/
	// 新建一个类继承BaseAdapter，实现listview与数据的绑定
	private class MyAdapter extends BaseAdapter {
		
		int[] classPicID = {R.drawable.class_pic1, R.drawable.class_pic2, R.drawable.class_pic3,
				R.drawable.class_pic4, R.drawable.class_pic5};
		private LayoutInflater mInflater; // 得到一个LayoutInfalter对象用来导入布局

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return listClassUnReadMsgInfos.size(); // 返回数组的长度
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewChild viewChild;
			if (view == null) {
				// 和通知公告的班级列表一样；
				view = mInflater.inflate(R.layout.notice_fragment_item_one, null);
				viewChild = new ViewChild();
				/* 得到各个控件的对象 */
				viewChild.classNameTextView= (TextView) view
						.findViewById(R.id.className_textView);
				viewChild.noticeNumTextView = (TextView) view
						.findViewById(R.id.noticeNum_textView);
				viewChild.classPicImageView = (ImageView) view.findViewById(R.id.classImg_imageView);

				view.setTag(viewChild); // 绑定ViewHolder对象
			} else {
				viewChild = (ViewChild) view.getTag(); // 取出ViewHolder对象
			}

			/* 设置TextView显示的内容，即我们存放在动态数组中的数据 */
			viewChild.classNameTextView.setText(listClassUnReadMsgInfos.get(position).getClassName());
			int count = Integer.parseInt(listClassUnReadMsgInfos.get(position).getUnReadMsgNum());
			if(count == 0){
				viewChild.noticeNumTextView.setVisibility(View.INVISIBLE);
			}else {
				viewChild.noticeNumTextView.setVisibility(View.VISIBLE);
				viewChild.noticeNumTextView.setText(count+"");
			}
			
			// 动态设置班级图像；
			int k = position%5;
			viewChild.classPicImageView.setBackgroundResource(classPicID[k]);
			

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
	}

	// 存放列表子项的控件，用于在MyAdapter中设置;
	public final class ViewChild {
		public TextView classNameTextView; // 班级名称;
		public TextView noticeNumTextView; // 通知数量；
		
		public ImageView classPicImageView;	// 班级图片；
	}
	
	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(this, toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}
	
}
