package com.ttqeducation.teacher.activitys.notice;



import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.activitys.system.LoginActivity;
import com.ttqeducation.teacher.activitys.system.MainActivity;
import com.ttqeducation.teacher.beans.ClassUnReadMsgInfo;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.TeacherInfo;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.MyListView;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.tools.DesUtil;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class NoticeFragment extends Fragment{
	
	private List<ClassUnReadMsgInfo> listClassUnReadMsgInfos = new ArrayList<ClassUnReadMsgInfo>();
	private MyListView myListView = null;
	private MyAdapter mAdapter = null; // 得到一个MyAdapter对象,用于与listview绑定；
	
	private RefreshView refreshView = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.notice_fragment, container, false);
		this.refreshView = new RefreshView(getActivity(), R.style.full_screen_dialog);
		
		generateData();
		initView(view);
		
		return view;
	}
	
	public void generateData(){
		Iterator<String> iterator = TeacherInfo.getInstance().classUnReadMsgInfos.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			this.listClassUnReadMsgInfos.add(TeacherInfo.getInstance().classUnReadMsgInfos.get(key));
		}
	}
	
	public void initView(View view){
		this.myListView = (MyListView) view
				.findViewById(R.id.listView_notice);
		this.myListView.setPullRefreshEnable(false); // 不提供下拉刷新功能；
		this.myListView.setPullLoadEnable(false); // 不提供上拉加载功能；
		this.myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			// 列表的点击事件；
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent noticeListIntent = new Intent(getActivity(), NoticeListActivity.class);
				Log.i("lvjie", "NoticeFragment-->setOnItemClickListener()... position="+position);
				noticeListIntent.putExtra("classID", listClassUnReadMsgInfos.get(position-1).getClassID());
				startActivityForResult(noticeListIntent, 100);
			}
		});
		this.mAdapter = new MyAdapter(getActivity());
		this.myListView.setAdapter(this.mAdapter);
	}
	
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i("lvjie", "NoticeFragment-->onActivityResult()...");
		this.listClassUnReadMsgInfos.clear();
		getTeacherUnreadMesgByWS(TeacherInfo.getInstance().execTeacherID);
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	// 从服务端获取教师所教班级未读等相关信息；
	public void getTeacherUnreadMesgByWS(String teacherID){
		
		new AsyncTask<Object, Object, DataTable>(){			
			@Override
			protected void onPreExecute() {
				refreshView.show();
			};
			
			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				
				DataTable dt = null;

				String teacherID = params[0].toString();

				// 方法名
				String methodName = "APP_teacherUnreadMesg_byClass";
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
				paramsMap.put("TokenID", DesUtil.getDesTokenID(TeacherInfo.getInstance().execTeacherID, "Admin203", 2));
				paramsMap.put("TokenID", tokenID);
				// 获取数据
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				// 从本地获取学校URL
				SharedPreferences pre = getActivity().getSharedPreferences("TTQAndroid", Context.MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				getDataTool.setURL(schoolURL);
				try {
					dt = getDataTool.getDataAsTable(methodName, paramsMap);
				} catch (IOException e) {
					Log.i("lvjie", "访问WS失败，可能是地址或参数错误,或网络没有连接"+e.getMessage());
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return dt;
			}
			
			@Override
			protected void onPostExecute(DataTable result) {
				if(result != null){
					int rowCount = result.getRowCount();
					for(int i=0; i<rowCount; i++){
						try {
							String classID = result.getCell(i, "classID").trim();
							String className = result.getCell(i, "className").trim();
							String unReadMsgNum = result.getCell(i, "unRead").trim();
							ClassUnReadMsgInfo classUnReadMsgInfo = new ClassUnReadMsgInfo(classID, className, unReadMsgNum);
							listClassUnReadMsgInfos.add(classUnReadMsgInfo);
							Log.i("lvjie", "LoginActivity-->getTeacherUnreadMesgByWS()..."+classUnReadMsgInfo);
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					mAdapter.notifyDataSetChanged();
				}else {
					Log.i("lvjie", "NoticeFragment-->getTeacherUnreadMesgByWS()...从服务端获取班级未读消息失败...");
				}				
				// 关闭刷新；
				refreshView.dismiss();				
			};
			
		}.execute(teacherID);
	}
		
		
}
