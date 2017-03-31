package com.ttqeducation.activitys.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.R;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.TeacherInfo;
import com.ttqeducation.beans.UserCurrentState;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.MyListView;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DesUtil;
import com.ttqeducation.tools.GeneralTools;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 聊天人员列表界面；
 * @author lvjie
 *
 */
public class HomeAndSchoolInteractionActivity extends Activity {
	
	private int[] personImgID = {R.drawable.parent1, R.drawable.parent2,R.drawable.parent3,R.drawable.parent4,
			R.drawable.parent5,R.drawable.parent6,R.drawable.parent7,R.drawable.parent8};

	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；
	
	private MyListView  personsListView = null;
	private List<TeacherInfo> listPersons = new ArrayList<TeacherInfo>();
	private MyAdapter mAdapter = null;
	private RefreshView refreshView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_school);

		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
//		initView();
		getParentUnreadMesgByWS(UserInfo.getInstance().studentID, UserInfo.getInstance().classID);
	}
	
	public void initView(){
		
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("家校互动");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				HomeAndSchoolInteractionActivity.this.finish();
			}
		});
		
		this.personsListView = (MyListView) super.findViewById(R.id.listView_persons);
		this.personsListView.setPullLoadEnable(false);
		this.personsListView.setPullRefreshEnable(false);
		
//		generateData();
		mAdapter = new MyAdapter(this);
		this.personsListView.setAdapter(mAdapter);
		
		// 列表子项的点击事件；
		this.personsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HomeAndSchoolInteractionActivity.this, CommunicationActivity.class);
				intent.putExtra("teacherID", listPersons.get(position-1).getTeacherID());
				intent.putExtra("teacherName", listPersons.get(position-1).getTeacherName());
				int k = (position-1)%8;
				intent.putExtra("teacherImg", personImgID[k]);
				startActivity(intent);
				
				// 设置new的问题；
				if(listPersons.get(position-1).getNoReadCount()>0){
					listPersons.get(position-1).setNoReadCount(0);
					UserCurrentState.getInstance().homeSchoolNew--;
				}
				HomeAndSchoolInteractionActivity.this.mAdapter.notifyDataSetChanged();
			}
		});
	}
	
	public void generateData(){
		TeacherInfo teacherInfo;
		for(int i=0;i<10;i++){
			teacherInfo = new TeacherInfo(i+"", "教师 "+i, "国庆节放假通知...", "15:20", i*10);
			this.listPersons.add(teacherInfo);
		}
	}
	
	// 窗口提示信息；
	public void showToast(String toastMessage){
		Toast toast =  Toast.makeText(HomeAndSchoolInteractionActivity.this, toastMessage, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	// 从服务端获取家长孩子所在班级老师信息，用来界面展示；
	public void getParentUnreadMesgByWS(String studentID, String classID){
		new AsyncTask<Object, Object, DataTable>() {
			@Override
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
				
				DataTable dt_parentUnreadMeg = new DataTable();

				// 方法名
				String methodName = "APP_ParentUnreadMesg";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String studentID = params[0].toString();
				String classID = params[1].toString();
				Log.i("lvjie", "传递的参数： studentID="+studentID+"  classID="+classID);
				paramsMap.put("studentID", studentID);
				paramsMap.put("classID", classID);
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
					dt_parentUnreadMeg = getDataTool.getDataAsTable(methodName,
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
				return dt_parentUnreadMeg;
			}
			@Override
			protected void onPostExecute(DataTable result) {
				if (result != null) {
					TeacherInfo teacherInfo;
					int count = result.getRowCount();
					for (int i = 0; i < count; i++) {
						try {
							String teacherID = result.getCell(i, "teacherID");
							String teacherName = result.getCell(i, "teacherName");
							int unread = Integer.parseInt(result.getCell(i, "unread"));
							String content_brief = result.getCell(i, "content_brief");
							String publishTime = result.getCell(i, "publishTime");
							
							Log.i("lvjie", "teacherName="+teacherName+"  content_brief="+content_brief);
							if("anyType{}".equals(content_brief)){
								content_brief = "  ";
							}
							teacherInfo = new TeacherInfo(teacherID, teacherName, content_brief, publishTime, unread);
							listPersons.add(teacherInfo);
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				// 关闭刷新；
				refreshView.dismiss();
				
				// 没有联网，不可以进入到下一个界面；
				if(!GeneralTools.getInstance().isOpenNetWork1(HomeAndSchoolInteractionActivity.this)){
					showToast("未连接到互联网，请检查网络配置!");
				}
				
				initView();
				
			};
		
		}.execute(studentID, classID);
	}
	
	
	private class MyAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater; //得到一个LayoutInfalter对象用来导入布局
	        
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listPersons.size();
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
			if(view == null){
				view = mInflater.inflate(R.layout.person_item_one, null);
				viewChild = new ViewChild();
				/* 得到各个控件的对象 */
				viewChild.chatInfoTextView = (TextView) view.findViewById(R.id.chatInfo_textView);
				viewChild.chatTimeTextView = (TextView) view.findViewById(R.id.chatTime_textView);
				viewChild.personImageView = (ImageView) view.findViewById(R.id.person_img);
				viewChild.noReadCounTextView = (TextView) view.findViewById(R.id.noReadCount_textView);
				viewChild.teacherNameTextView = (TextView) view.findViewById(R.id.teacherName_textView);
				view.setTag(viewChild);
			}else {
				viewChild = (ViewChild) view.getTag(); // 取出ViewHolder对象
			}
			
			if(listPersons.get(position).getNoReadCount()<=0){
				viewChild.noReadCounTextView.setVisibility(View.INVISIBLE);
			}else {
				viewChild.noReadCounTextView.setVisibility(View.VISIBLE);
				viewChild.noReadCounTextView.setText(listPersons.get(position).getNoReadCount()+"");
			}
			
			viewChild.chatInfoTextView.setText(listPersons.get(position).getChatInfo());
			viewChild.chatTimeTextView.setText(listPersons.get(position).getChatTime());		
			viewChild.teacherNameTextView.setText(listPersons.get(position).getTeacherName());
			
			int k = position % 8;
			viewChild.personImageView.setBackgroundResource(personImgID[k]);
			
			return view;
		}
		
		// 存放列表子项的控件，用于在MyAdapter中设置;
		public final class ViewChild {
			public TextView teacherNameTextView; // 老师姓名;
			public TextView chatInfoTextView; // 聊天概览;
			public TextView chatTimeTextView; // 聊天时间；
			public TextView noReadCounTextView;  //未读数；
			public ImageView personImageView;	// 教师图片；
		}
		
	}
	
}
