package com.ttqeducation.teacher.activitys.message;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.beans.ClassInfo;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.ParentInfo;
import com.ttqeducation.teacher.beans.TeacherInfo;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.tools.DesUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.FlagToString;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;

/**
 * 家校互动 人员列表显示界面；
 * 
 * @author 
 * 
 */
public class TeacherAndParentChatListActivity extends Activity {
	// 标题栏部分；
	private TextView titleTextView = null;
	private LinearLayout titleBackLayout = null;
	
	private RefreshView refreshView = null;
	
	private List<ClassInfo> listClassInfos = new ArrayList<ClassInfo>();
	private List<List<ParentInfo>> listGroupParentInfos = new ArrayList<List<ParentInfo>>();
	
	private ExpandableListView myExpandableListView = null;		// 树形菜单；
	private MyAdapter mAdapter = null;
	
	int[] parentImg = {R.drawable.parent1,R.drawable.parent2,R.drawable.parent3,R.drawable.parent4,R.drawable.parent5
			,R.drawable.parent6,R.drawable.parent7,R.drawable.parent8,R.drawable.parent9,R.drawable.parent10};
	
	private int[] goupNews = null;		// 解决界面上父节点new的问题；
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_class_parent_list);	
		
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		getClassInfosByWS(TeacherInfo.getInstance().execTeacherID);
//		generateData();
//		initView();
	}
	
	//按返回按钮则释放页面信息
	public void initView() {

		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("家校互动");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				clickBack();
			}
		});
		
		
		this.myExpandableListView = (ExpandableListView) super.findViewById(R.id.expandableListView_classAndPersonList);  
        this.myExpandableListView.setGroupIndicator(null);  		// 去掉默认的箭头；       
        this.mAdapter = new MyAdapter(this);           
        this.myExpandableListView.setAdapter(this.mAdapter);  
                  
        this.myExpandableListView.setOnChildClickListener(new OnChildClickListener() {			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				// 从0开始的；
				Log.i("lvjie", "groupPosition="+groupPosition+"  childPosition="+childPosition);
				Intent chatIntent = new Intent(TeacherAndParentChatListActivity.this, OneChatActivity.class);
				chatIntent.putExtra("parentID", listGroupParentInfos.get(groupPosition).get(childPosition).getParentID());
				chatIntent.putExtra("parentName", listGroupParentInfos.get(groupPosition).get(childPosition).getParentName());
				chatIntent.putExtra("parentImgID", listGroupParentInfos.get(groupPosition).get(childPosition).getParentImgID());
				startActivity(chatIntent);
				
				// 以下是解决new的问题；
				if(listGroupParentInfos.get(groupPosition).get(childPosition).getNoReadCount()>0){
					listGroupParentInfos.get(groupPosition).get(childPosition).setNoReadCount(0);
					TeacherAndParentChatListActivity.this.goupNews[groupPosition]--;
				}			
				if(TeacherAndParentChatListActivity.this.goupNews[groupPosition]<=0){
					listClassInfos.get(groupPosition).setShowNew(false);
				}				
				TeacherAndParentChatListActivity.this.mAdapter.notifyDataSetChanged();
				
				return false;
			}
		});
				
	}
	
	public void generateData(){
		ClassInfo classInfo;
		List<ParentInfo> listParentInfos ;
		for(int i=0; i<4; i++){
			classInfo = new ClassInfo(i+"", "班级 "+i, 20);
			listParentInfos = new ArrayList<ParentInfo>();
			for(int j=0; j<8; j++){
				ParentInfo parentInfo ;
				if(j<3){
					parentInfo = new ParentInfo(""+j, "家长  "+j, 20);
				}else {
					parentInfo = new ParentInfo(""+j, "家长  "+j, 1);
				}
				
				listParentInfos.add(parentInfo);
			}
			
			this.listClassInfos.add(classInfo);
			this.listGroupParentInfos.add(listParentInfos);
		}
	}
	
	// 通过服务获取 班级列表； 一对一聊天班级信息列表；
	public void getClassInfosByWS(String execTeacherID){
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
				DataTable dt_classInfo = null;
				
				// 方法名
				String methodName = "APP_Teacher_Parent_UnreadChat_byClass";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("teacherID", params[0].toString());
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
					dt_classInfo = getDataTool.getDataAsTable(methodName, paramsMap);
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
				return dt_classInfo;
			}
			@Override
			protected void onPostExecute(DataTable result) {
				if (result != null) {
					ClassInfo classInfo;
					int count = result.getRowCount();
					TeacherAndParentChatListActivity.this.goupNews = new int[count];	// 值用来存放子节点中有未读的数量；
					for (int i = 0; i < count; i++) {
						try {
							
							String classID = result.getCell(i, "classID").trim();
							String className = result.getCell(i, "className").trim();
							int studentNum = Integer.parseInt(result.getCell(i, "studentNum"));
							
							Log.i("lvjie", "classID="+classID+"  className="+className+"  studentNum="+studentNum);

							classInfo = new ClassInfo(classID, className, studentNum);
							listClassInfos.add(classInfo);
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					Log.i("lvjie", "listClassInfos.size()="+listClassInfos.size());
					getParentInfoByWS(listClassInfos);		// 获取对应的家长信息；
				}else {
					refreshView.dismiss();
				}
			};
			
		}.execute(execTeacherID);
	}
	
	// 通过服务获取  学生家长信息；下拉列表信息；
	public void getParentInfoByWS(final List<ClassInfo> listClassInfoss){
		new AsyncTask<Object, Object, DataTable>(){

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
//				refreshView.show();
			}
			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				DataTable dt_parentInfo = null;
				
				// 方法名
				String methodName = "APP_Teacher_Parent_UnreadChat_byParentID";
				
				// 获取数据
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				// 从本地获取学校URL
				SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				if (schoolURL == null) {// 如果没有值
					return null;
				}
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int count = listClassInfoss.size();
				
				for(int i=0; i<count; i++){			// 循环获取班级对应的家长信息；
					Log.i("lvjie", "execTeacherID="+TeacherInfo.getInstance().execTeacherID+"  classID="+listClassInfoss.get(i).getClassID());
					paramsMap.put("teacherID", TeacherInfo.getInstance().execTeacherID);
					paramsMap.put("classID", listClassInfoss.get(i).getClassID());
					paramsMap.put("TokenID", tokenID);
					getDataTool.setURL(schoolURL);
					try {
						dt_parentInfo = getDataTool.getDataAsTable(methodName, paramsMap);
						analysisDT(dt_parentInfo, i);
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
				}
				
				return dt_parentInfo;
			}
			@Override
			protected void onPostExecute(DataTable result) {
				refreshView.dismiss();
				initView();
			};
			
		}.execute("");
	}
	
	// 解析DataTable
	public void analysisDT(DataTable dt_parentInfo, int position){
		List<ParentInfo> listParentInfos = new ArrayList<ParentInfo>();
		if (dt_parentInfo != null) {
			ParentInfo parentInfo;
			int count = dt_parentInfo.getRowCount();
			
			for (int i = 0; i < count; i++) {
				try {
					String studentID = dt_parentInfo.getCell(i, "studentID").trim();
					String studentName = dt_parentInfo.getCell(i, "studentName").trim();
					int noReadCount = Integer.parseInt(dt_parentInfo.getCell(i, "unread"));					
					Log.i("lvjie", "studentID="+studentID+"  studentName="+studentName+"  noReadCount="+noReadCount);
					if(noReadCount>0){
						TeacherAndParentChatListActivity.this.goupNews[position]++;
					}
					parentInfo = new ParentInfo(studentID, studentName, noReadCount);
					listParentInfos.add(parentInfo);
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if(TeacherAndParentChatListActivity.this.goupNews[position] > 0){
			this.listClassInfos.get(position).setShowNew(true);
		}
		
		this.listGroupParentInfos.add(listParentInfos);
	}
	
	

	
	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(TeacherAndParentChatListActivity.this, toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public void clickBack(){
		this.finish();
	}
	
	
	/***************  数据绑定类  ******************/
	private class MyAdapter extends BaseExpandableListAdapter{

		private Context context;
		
		public MyAdapter(Context context){
			this.context = context;
		}
		
		@Override  
	    public Object getChild(int groupPosition, int childPosition) {  
	        // TODO Auto-generated method stub  
//	    	Log.i("lvjie", "ExpandableAdapter-->getChild()...");
	        return listGroupParentInfos.get(groupPosition).get(childPosition);  
	    }  
	    @Override  
	    public long getChildId(int groupPosition, int childPosition) {  
	        // TODO Auto-generated method stub 
//	    	Log.i("lvjie", "ExpandableAdapter-->getChildId()...");
	        return childPosition;  
	    }  
	    @Override  
	    public View getChildView(int groupPosition, int childPosition,  
	            boolean isLastChild, View view, ViewGroup parent) {  
	        // TODO Auto-generated method stub      	
	    	ViewChild viewChild;
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.tree_menu_child, null);
				viewChild = new ViewChild();
				/* 得到各个控件的对象 */
				viewChild.tvName = (TextView) view.findViewById(R.id.name_textView);
				viewChild.tvNew = (TextView) view.findViewById(R.id.new_textView);
				viewChild.parentImageView = (ImageView) view.findViewById(R.id.parent_img);
				view.setTag(viewChild); // 绑定ViewHolder对象
			} else {
				viewChild = (ViewChild) view.getTag(); // 取出ViewHolder对象
			}
			
			viewChild.tvName.setText(listGroupParentInfos.get(groupPosition).get(childPosition).getParentName());
			int noReadCount = listGroupParentInfos.get(groupPosition).get(childPosition).getNoReadCount();
			if(noReadCount>0){
				viewChild.tvNew.setVisibility(View.VISIBLE);
				viewChild.tvNew.setText(noReadCount+"");
			}else {
				viewChild.tvNew.setVisibility(View.INVISIBLE);
			}
			
			int k = childPosition%10;
			listGroupParentInfos.get(groupPosition).get(childPosition).setParentImgID(parentImg[k]);
			viewChild.parentImageView.setBackgroundResource(parentImg[k]);
			
			return view;
			
	    }  
	    
	    @Override  
	    public int getChildrenCount(int groupPosition) {  
	        // TODO Auto-generated method stub  
//	    	Log.i("lvjie", "ExpandableAdapter-->getChildrenCount()...");
	        return listGroupParentInfos.get(groupPosition).size();  
	    }  
	    @Override  
	    public Object getGroup(int groupPosition) {  
	        // TODO Auto-generated method stub 
//	    	Log.i("lvjie", "ExpandableAdapter-->getGroup()...");
	        return listClassInfos.get(groupPosition);  
	    }  
	    @Override  
	    public int getGroupCount() {  
	        // TODO Auto-generated method stub 
//	    	Log.i("lvjie", "ExpandableAdapter-->getGroupCount()...");
	        return listClassInfos.size();  
	    }  
	    @Override  
	    public long getGroupId(int groupPosition) {  
	        // TODO Auto-generated method stub
//	    	Log.i("lvjie", "ExpandableAdapter-->getGroupId()...");
	        return groupPosition;  
	    }  
	    @Override  
	    public View getGroupView(int groupPosition, boolean isExpanded,  
	            View view, ViewGroup parent) {  
	        // TODO Auto-generated method stub  
//	    	Log.i("lvjie", "ExpandableAdapter-->getGroupView()...");
	         
	    	ViewParent viewParent = null;
	    	if(view == null){
	    		view = LayoutInflater.from(context).inflate(R.layout.tree_menu_parent, null);
	    		viewParent = new ViewParent();
	    		viewParent.imageView = (ImageView) view.findViewById(R.id.imageView_parent);
	    		viewParent.tvName = (TextView) view.findViewById(R.id.textView_parent);
	    		viewParent.newImageView = (ImageView) view.findViewById(R.id.new_img);
	    		view.setTag(viewParent);
	    	}else {
				viewParent = (ViewParent) view.getTag();
			}
	    	
	    	viewParent.tvName.setText(listClassInfos.get(groupPosition).getClassName());
	    	if(isExpanded){
	    		viewParent.imageView.setBackgroundResource(R.drawable.tree_ex);
	    	}else {
	    		viewParent.imageView.setBackgroundResource(R.drawable.tree_ec);
			}
	    	if(listClassInfos.get(groupPosition).isShowNew()){
	    		viewParent.newImageView.setVisibility(View.VISIBLE);
	    	}else {
	    		viewParent.newImageView.setVisibility(View.INVISIBLE);
			}
	    	
	        return view;  
	    }  
	    @Override  
	    public boolean hasStableIds() {  
	        // TODO Auto-generated method stub  
	        return false;  
	    }  
	    @Override  
	    public boolean isChildSelectable(int groupPosition, int childPosition) {  
	        // TODO Auto-generated method stub  
//	    	Log.i("lvjie", "ExpandableAdapter-->isChildSelectable()..."+groupPosition+"   "+childPosition);
	        return true;  
	    }  
	         	    
	    // 存放列表子项的控件，用于在MyAdapter中设置;
	 	class ViewChild {
	 		public TextView tvName; // 姓名;
	 		public TextView tvNew;	// new 显示数据；

	 		public ImageView parentImageView;	// 家长头像；
	 	}
	 	
	 	// 存放列表子项的控件，用于在MyAdapter中设置;
	  	class ViewParent {
	  		public ImageView imageView;	// 箭头图片；
	  		public TextView tvName; // 班级名;
	  		public ImageView newImageView;	// new 图片；
	  	}
		
	}
	
}


