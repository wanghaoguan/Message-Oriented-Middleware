package com.ttqeducation.teacher.activitys.teach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.HomeworkListItem;
import com.ttqeducation.teacher.beans.TeacherInfo;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.MyListView;
import com.ttqeducation.teacher.myViews.MyListView.IMyListViewListener;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author hxy
 * 家庭作业布置：备课文件列表界面，用于显示曾经备过课的文件。
 *          原先根据备课的类型将跳转的界面分为题干题目展示和页码题目展示；
 *          16.9.7日修改后将下一级界面统一使用为支持HTML展示的题干题目展示界面。
 */
public class HomeWorkArrangeActivity extends Activity implements IMyListViewListener {

	//标题栏部分
	private LinearLayout titleBackLayout = null;
	private TextView titleTextView = null;
			 
	private RefreshView refreshView = null;
	private MyListView myListView = null;
	private LinearLayout llSubject=null;  //科目列表
	private TextView tvSubject = null;  //科目名称
	private LinearLayout llChooseSubject=null; //科目下拉列表
	private ListView lvChooseSubject = null;	
	
	//科目
	private String[] subjects;
	private String subjectName ="语文";
	private String subjectID="";
	
	private DataTable dt_homeList=null;
	private String teacherID="";
	private String testType="1";
	private String grade="";
	private String termID="";
	
	private List<HomeworkListItem> listHomeworkListItem = new ArrayList<HomeworkListItem>();
	private MyAdapter myAdapter = null;
	private int currentListNum =-1; //记录当前载入到了哪条作业列表，用于上拉刷新
	private int endFlag=0;			//判断是否所有数据加载完毕
	private static final int ONE_PAGENUM = 15;//每页作业列表的数量
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.activity_homework_arrange_list);
		getData();
		initView();
		
	}

	private void getData() {
		// TODO Auto-generated method stub
		teacherID=TeacherInfo.getInstance().execTeacherID;
		testType="1";
		grade= TeacherInfo.getInstance().getGrade() ;		
		termID = TeacherInfo.getInstance().getTermID();
	}

	private void initView() {
		// TODO Auto-generated method stub
		this.titleBackLayout = (LinearLayout) (super.findViewById(R.id.action_bar)
				.findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HomeWorkArrangeActivity.this.finish();
			}
		});
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar)
				.findViewById(R.id.title_text));
		this.titleTextView.setText("备课文件列表");
		
		//科目选择按钮
		llSubject=(LinearLayout)findViewById(R.id.llSubject);
		tvSubject = (TextView)findViewById(R.id.tvSubject);
		//科目选择list
		llChooseSubject=(LinearLayout)findViewById(R.id.llChooseSubject);
		lvChooseSubject =(ListView)findViewById(R.id.lvChooseSubject);
		llSubject.setOnClickListener(new  MyOnClickListener());
		initChooseSubject();
		
		this.myListView =(MyListView) super.findViewById(R.id.listView_homework);
		this.myListView.setMyListViewListener(this);
		this.myListView.setPullRefreshEnable(false);//不提供下拉刷新
		this.myListView.setPullLoadEnable(true);//提供上拉加载
		this.myListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			//列表点击事件
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				position--;//索引从零开始
				String testID =listHomeworkListItem.get(position).getTestID();
				//typeBelong 原先用于区分题干题库和页码题库，分别进入两个不同的界面，现在统一进入题干题库界面
				String typeBelong = listHomeworkListItem.get(position).getTypeBelongs();
				String testName = listHomeworkListItem.get(position).getTestName();
				String classID = TeacherInfo.getInstance().getClassID();
				String teacherID = TeacherInfo.getInstance().execTeacherID;
//				if(typeBelong.equals("1")){
//					Intent intent = new Intent(HomeWorkArrangeActivity.this, HomeWorkArrangeDetaileActivity.class);
//					intent.putExtra("testID", testID);
//					intent.putExtra("typeBelong", typeBelong);
//					intent.putExtra("subject", subjectID);
//					intent.putExtra("testName", testName);
//					intent.putExtra("classID", classID);
//					intent.putExtra("teacherID", teacherID);
//					startActivity(intent); 
//				}else 
					if(typeBelong.equals("2")||typeBelong.equals("1")){
					Intent intent = new Intent(HomeWorkArrangeActivity.this, HomeWorkArrangeDetailContentActivity.class);
					intent.putExtra("testID", testID);
					intent.putExtra("typeBelong", typeBelong);
					intent.putExtra("subject", subjectID);
					intent.putExtra("testName", testName);
					intent.putExtra("classID", classID);
					intent.putExtra("teacherID", teacherID);
					startActivity(intent); 
				}
				
			}
			
		});
		
	}

	/**
	 * 初始化科目
	 */
	private void initChooseSubject() {
		// TODO Auto-generated method stub
		subjects= TeacherInfo.getInstance().getSubjects();
		if(subjects!=null && subjects.length>0)
		{
			subjectName = subjects[0];
			getSubjectID();
			tvSubject.setText(subjectName);
			 ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(getApplicationContext(),
					 R.layout.item_subject, subjects);
			 lvChooseSubject.setAdapter(subjectAdapter);
			 lvChooseSubject.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					// TODO Auto-generated method stub
					subjectName = subjects[position];
					getSubjectID();
					tvSubject.setText(subjectName);
					llChooseSubject.setVisibility(View.GONE);
					app_getTestList(teacherID, testType, grade, subjectID, termID);
				}
				 
			});
			 app_getTestList(teacherID, testType, grade, subjectID, termID);
		}
	}

	/**
	 * 获取该名教师在该年级、该科目、该学期的所有备课文件
	 * @param teacherID
	 * @param testType
	 * @param grade
	 * @param subjectID
	 * @param termID
	 */
	private void app_getTestList(String teacherID,String testType,String grade,String subjectID,String termID) {
		// TODO Auto-generated method stub
		this.refreshView = new RefreshView(this,  R.style.full_screen_dialog);
		new AsyncTask<Object, Object, DataTable>() {
			
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				DataTable dt_result = new DataTable();
				// 方法名
				String methodName = "APP_getTestList";
				//参数Map
				Map<String,String> paramsMap = new HashMap<String, String>();
				String tokenID ="";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("teacherID",params[0].toString());
				paramsMap.put("testType",params[1].toString());
				paramsMap.put("grade",params[2].toString());
				paramsMap.put("subjectID",params[3].toString());
				paramsMap.put("termID",params[4].toString());
				paramsMap.put("TokenID",tokenID);
				
				//开始访问数据			
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 从本地获取学校URL,数据由SharedPreferences存放;
				SharedPreferences pre = getSharedPreferences("TTQAndroid", Context.MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);				
				if (schoolURL == null) {// 如果没有值
					return null;
				}
				getdatatool.setURL(schoolURL);
				
				try {
					dt_result = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "APP_getTestList()...出错了。。。");
					e.printStackTrace();
				}		
				
				return dt_result;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				initViewAfterGetData(result);
				refreshView.dismiss();
				
			}
			
		}.execute(teacherID, testType, grade, subjectID, termID);
	}

	//将获取的数据载入界面
	protected void initViewAfterGetData(DataTable result) {
		// TODO Auto-generated method stub
		//2017-2-14  沈修改，点击科目之前清空之前的缓存。
		listHomeworkListItem.clear();
		//清除之前数据
		if(dt_homeList != null){
			for(int i=dt_homeList.getRowCount()-1;i>=0;i--){
				dt_homeList.deleteRow(i);
			}
		}
		//赋予本次数据
		if(result != null){
			dt_homeList = result;
		}else{
			endFlag=1;
			//2017-2-14  沈修改，点击科目之前清空之前的缓存。
			myAdapter = new MyAdapter(HomeWorkArrangeActivity.this);
			myListView.setAdapter(myAdapter);
			return;
		}
		
		HomeworkListItem homeworkItem ;
		//如果所有数目大于等于15，则展示15条记录，否则展示所有记录并将结束标志位置为1
		int homeworkListNum=0;
		if(dt_homeList.getRowCount()>=ONE_PAGENUM){
			homeworkListNum=ONE_PAGENUM;
			currentListNum=ONE_PAGENUM;
		}else{//不足15条
			homeworkListNum=dt_homeList.getRowCount();
			endFlag=1;
		}
		for(int i=0; i<homeworkListNum; i++){
			try{
				String testID = dt_homeList.getCell(i, "testID");
				String testName = dt_homeList.getCell(i, "testName");
				String createTime = dt_homeList.getCell(i, "createTime");
				String typeBelongs = dt_homeList.getCell(i, "typeBelongs");
				homeworkItem= new HomeworkListItem(testID,testName,createTime,typeBelongs);
				listHomeworkListItem.add(homeworkItem);
				
			}catch (dataTableWrongException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 关闭刷新；
		refreshView.dismiss();
		// 获取完数据后，初始化界面
		myAdapter = new MyAdapter(HomeWorkArrangeActivity.this);
		myListView.setAdapter(myAdapter);
		
	}
	
	/**
	 * 自定义adapter
	 */
	private class MyAdapter extends BaseAdapter{
		private Context context;
		private LayoutInflater inflater;
		
		public MyAdapter(Context context){
			this.context = context;
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listHomeworkListItem.size();//返回list长度
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder;
			if(convertView == null){
				convertView = inflater.inflate(R.layout.homework_arrange_list_item, null);
				viewHolder= new ViewHolder();
				//获得各个控件对象
				viewHolder.tvHomeworkName = (TextView)convertView
						.findViewById(R.id.tvHomeworkName);
				convertView.setTag(viewHolder);//绑定viewHolder对象				
			}else{
				viewHolder =(ViewHolder) convertView.getTag();
			}
			//设置作业名称TextView显示的内容
			viewHolder.tvHomeworkName.setText(listHomeworkListItem.get(position).getTestName());
			return convertView;
		}	
		
		
	}
	
	//存放列表子项的控件，用于在MyAdapter中设置
	public static class ViewHolder{
		TextView tvHomeworkName ;
	}

	// 根据科目名字获取科目ID
	private void getSubjectID() {
		subjectID = GeneralTools.getInstance().getSubjectIDByName(subjectName);
		if(subjectID==null){
			subjectID="-1";
		}
	}
	
	class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()){
			case R.id.llSubject:
				if(llChooseSubject.getVisibility()==View.VISIBLE){
					llChooseSubject.setVisibility(View.GONE);
				}					
				else{
					llChooseSubject.setVisibility(View.VISIBLE);					
				}	
				break;
			}
		}
		
	}

	@Override
	public void onRefresh() {  //下拉触发事件 
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadMore() { //上拉加载,如果剩余数量>15条则显示15条，否则显示剩余全部
		// TODO Auto-generated method stub
		if(endFlag==1){
			showToast("没有更多!");
		}else{
			int homeworkListNum=0;
			if(currentListNum+ONE_PAGENUM < dt_homeList.getRowCount()){//如果还没有达到总数目
				homeworkListNum=ONE_PAGENUM;
				
			}else if(currentListNum+ONE_PAGENUM == dt_homeList.getRowCount()){//如果和总数目相等
				homeworkListNum=ONE_PAGENUM;				
				endFlag=1;
			}else //如果超过总数
			{
				homeworkListNum=dt_homeList.getRowCount()-currentListNum;
				endFlag=1;
			}
			HomeworkListItem homeworkItem;
			for(int i=currentListNum; i<currentListNum+homeworkListNum; i++){
				try{
					String testID = dt_homeList.getCell(i, "testID");
					String testName = dt_homeList.getCell(i, "testName");
					String createTime = dt_homeList.getCell(i, "createTime");
					String typeBelongs = dt_homeList.getCell(i, "typeBelongs");
					homeworkItem= new HomeworkListItem(testID,testName,createTime,typeBelongs);
					listHomeworkListItem.add(homeworkItem);
					
					
				}catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			currentListNum+=ONE_PAGENUM;
			myAdapter.notifyDataSetChanged();
			
		}
		
		onLoad();
		
	}
	
	
	private void onLoad() {
		myListView.stopRefresh();
		myListView.stopLoadMore();
		myListView.setRefreshTime("刚刚");
	}
	
	// 窗口提示信息；
	public void showToast(String toastMessage){
		Toast toast =  Toast.makeText(HomeWorkArrangeActivity.this, toastMessage, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	
}
