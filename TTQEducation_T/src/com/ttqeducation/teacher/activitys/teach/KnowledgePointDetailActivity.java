package com.ttqeducation.teacher.activitys.teach;

import java.util.HashMap;
import java.util.Map;

import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.tools.DesUtil;

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
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 界面功能：展示包含的每一个知识点的名称、班级掌握度、
 * 班级最高掌握度
 * @author Dell
 *
 */
public class KnowledgePointDetailActivity extends Activity {

	//标题栏部分
	private LinearLayout titleBackLayout = null;
	private TextView titleTextView = null;
	
	//明细
	private ListView lvKnowledgePointDetail=null;
	
	private RefreshView refreshview=null;
	
	MyAdapter myAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_knowledge_point_detail);
		
		initView();
		
		getDateFromIntent();
	}

	

	private void initView() {
		// TODO Auto-generated method stub
		//实例化
		this.titleTextView =(TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("知识点明细");
		this.titleBackLayout =(LinearLayout) super.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout);
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				KnowledgePointDetailActivity.this.finish();
			}
		});
		
		this.lvKnowledgePointDetail =(ListView) findViewById(R.id.lvKnowledgePointDetail);
		
		
	}

	private void getDateFromIntent() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		String timeType = intent.getStringExtra("timeType");
		if(timeType != null && timeType.length() > 0 && timeType.equals("天")){
			String classID= intent.getStringExtra("classID");
			String date= intent.getStringExtra("date");
			String subjectID= intent.getStringExtra("subjectID");
			//showToast(classID+"  "+ date+"  "+subjectID);
			getClassKnowledgeDetailByDay(classID,date,subjectID);
		}else if (timeType != null && timeType.length() > 0 && timeType.equals("周")){
			String classID= intent.getStringExtra("classID");			
			String subjectID= intent.getStringExtra("subjectID");			
			String weekID= intent.getStringExtra("weekID");
			//showToast(classID+"  "+ subjectID+"  "+weekID);
			getClassKnowledgeDetailByWeek(classID,weekID,subjectID);
		}else if (timeType != null && timeType.length() > 0 && timeType.equals("月")){
			String classID= intent.getStringExtra("classID");			
			String subjectID= intent.getStringExtra("subjectID");			
			String termID= intent.getStringExtra("termID");
			String month= intent.getStringExtra("month");
			//showToast(classID+"  "+ subjectID+"  "+termID+"  "+month);
			getClassKnowledgeDetailByMonth(classID,termID,subjectID,month);
		}else if (timeType != null && timeType.length() > 0 && (timeType.equals("期中") || timeType.equals("期末"))){
			String classID= intent.getStringExtra("classID");			
			String subjectID= intent.getStringExtra("subjectID");			
			String termID= intent.getStringExtra("termID");
			int termType= intent.getIntExtra("termType", 0);
			//showToast(classID+"  "+ subjectID+"  "+termID+"  "+termType);
			getClassKnowledgeDetailByTerm(classID,termID,subjectID,termType);
		}
	}
	
	

	//窗口提示信息
	public void showToast(String toastMessage){
		Toast toast = Toast.makeText(KnowledgePointDetailActivity.this, 
				toastMessage, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	/**
	 * 班级每天知识点掌握度详细情况说明
	 * @param classID
	 * @param date
	 * @param subjectID
	 */
	private void getClassKnowledgeDetailByDay(String classID, String date,
			String subjectID) {
		// TODO Auto-generated method stub
		this.refreshview= new RefreshView(this, R.style.full_screen_dialog);
		
		//异步任务访问网络数据
		new AsyncTask<Object, Object, DataTable>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshview.show();
			
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				DataTable dt_result = new DataTable();
				
				// 方法名
				String methodName = "APP_getClassKnowledgeDetailByDay";
				
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
				paramsMap.put("date", params[1].toString());
				paramsMap.put("subjectID", params[2].toString());											
				paramsMap.put("TokenID", tokenID);
				
				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 从本地获取学校URL,数据由SharedPreferences存放;
				SharedPreferences pre = getSharedPreferences("TTQAndroid", Activity.MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);				
				if (schoolURL == null) {// 如果没有值
					return null;
				}
				getdatatool.setURL(schoolURL);
				try {
					dt_result = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "getClassKnowledgeDetailByDay()...出错了。。。");
					e.printStackTrace();
				}
				
				return dt_result;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				initViewAfterGetData(result);
				refreshview.dismiss();
			}

			
			
		}.execute(classID,date,subjectID);
	}
	
	/**
	 * 	班级每周知识点掌握度详细情况说明
	 * @param classID
	 * @param weekID
	 * @param subjectID
	 */
	private void getClassKnowledgeDetailByWeek(String classID, String weekID,
			String subjectID) {
		// TODO Auto-generated method stub
		this.refreshview= new RefreshView(this, R.style.full_screen_dialog);
		
		//异步任务访问网络数据
		new AsyncTask<Object, Object, DataTable>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshview.show();
			
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				DataTable dt_result = new DataTable();
				
				// 方法名
				String methodName = "APP_getClassKnowledgeDetailByWeek";
				
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
				paramsMap.put("weekID", params[1].toString());
				paramsMap.put("subjectID", params[2].toString());											
				paramsMap.put("TokenID", tokenID);
				
				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 从本地获取学校URL,数据由SharedPreferences存放;
				SharedPreferences pre = getSharedPreferences("TTQAndroid", Activity.MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);				
				if (schoolURL == null) {// 如果没有值
					return null;
				}
				getdatatool.setURL(schoolURL);
				try {
					dt_result = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "getClassKnowledgeDetailByWeek()...出错了。。。");
					e.printStackTrace();
				}
				
				return dt_result;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				initViewAfterGetData(result);
				refreshview.dismiss();
			}
			
		}.execute(classID,weekID,subjectID);		
		
	}
	
	/**
	 * 班级每月知识点掌握度详细情况说明
	 * @param classID
	 * @param termID
	 * @param subjectID
	 * @param month
	 */
	private void getClassKnowledgeDetailByMonth(String classID, String termID,
			String subjectID, String month) {
		// TODO Auto-generated method stub
		this.refreshview= new RefreshView(this, R.style.full_screen_dialog);
		
		//异步任务访问网络数据
		new AsyncTask<Object, Object, DataTable>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshview.show();
			
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				DataTable dt_result = new DataTable();
				
				// 方法名
				String methodName = "APP_getClassKnowledgeDetailByMonth";
				
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
				paramsMap.put("termID", params[1].toString());
				paramsMap.put("subjectID", params[2].toString());		
				paramsMap.put("month", params[3].toString());		
				paramsMap.put("TokenID", tokenID);
				
				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 从本地获取学校URL,数据由SharedPreferences存放;
				SharedPreferences pre = getSharedPreferences("TTQAndroid", Activity.MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);				
				if (schoolURL == null) {// 如果没有值
					return null;
				}
				getdatatool.setURL(schoolURL);
				try {
					dt_result = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "getClassKnowledgeDetailByMonth()...出错了。。。");
					e.printStackTrace();
				}
				
				return dt_result;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				initViewAfterGetData(result);
				refreshview.dismiss();				
			}
			
		}.execute(classID,termID,subjectID,month);
	}
	
	/**
	 * 班级每学期知识点掌握度详细情况说明
	 * @param classID
	 * @param termID
	 * @param subjectID
	 * @param termType
	 */
	private void getClassKnowledgeDetailByTerm(String classID, String termID,
			String subjectID, int termType) {
		// TODO Auto-generated method stub
		this.refreshview= new RefreshView(this, R.style.full_screen_dialog);
		
		//异步任务访问网络数据
		new AsyncTask<Object, Object, DataTable>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshview.show();
			
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				DataTable dt_result = new DataTable();
				
				// 方法名
				String methodName = "APP_getClassKnowledgeDetailByTerm";
				
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
				paramsMap.put("termID", params[1].toString());
				paramsMap.put("subjectID", params[2].toString());		
				paramsMap.put("termType", params[3].toString());		
				paramsMap.put("TokenID", tokenID);
				
				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 从本地获取学校URL,数据由SharedPreferences存放;
				SharedPreferences pre = getSharedPreferences("TTQAndroid", Activity.MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);				
				if (schoolURL == null) {// 如果没有值
					return null;
				}
				getdatatool.setURL(schoolURL);
				try {
					dt_result = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "APP_getClassKnowledgeDetailByTerm()...出错了。。。");
					e.printStackTrace();
				}
				
				return dt_result;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				initViewAfterGetData(result);
				refreshview.dismiss();
			}
			
		}.execute(classID,termID,subjectID,termType);
	}
	
	//将数据载入界面
	private void initViewAfterGetData(DataTable result) {
		// TODO Auto-generated method stub
		if(result!=null){
			myAdapter = new MyAdapter(getApplicationContext(),result);
			lvKnowledgePointDetail.setAdapter(myAdapter);
		}
	}
	
	private class MyAdapter extends BaseAdapter{

		private Context context;		
		private LayoutInflater inflater;
		private DataTable dt;
		
		public MyAdapter (Context context,DataTable dt){
			this.context=context;
			inflater=LayoutInflater.from(context);
			this.dt=dt;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dt.getRowCount();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub			
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}		
		
		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			int type =0;
			//偶数从0开始
			if(position%2==0){
				type=0;
			}else{
				type=1;
			}
			return type;
		}

				
		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return super.getViewTypeCount();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null; //单数
			
			
			int type = getItemViewType(position);
			//convertView 为null  新建viewHolder
			if(convertView == null){
				//if(type==0){
					convertView = inflater.inflate(R.layout.item_knowledge_point_detail, parent,false);
					viewHolder = new ViewHolder();
					viewHolder.tvOrderNum =(TextView) convertView.findViewById(R.id.tvOrderNum);
					viewHolder.tvKnowledgePointName =(TextView) convertView.findViewById(R.id.tvKnowledgeName);
					viewHolder.tvRightPersent =(TextView) convertView.findViewById(R.id.tvRightPersent);
					viewHolder.tvMaxRightPersent =(TextView) convertView.findViewById(R.id.tvMaxRightPersent);
					
					//viewHolderDual.tvOrderNum.setBackgroundColor(getResources().getColor(R.color.knowledgeDetailNumDual) );
					convertView.setTag(viewHolder);
				
			}else{
				
				viewHolder =(ViewHolder) convertView.getTag();
						
			}
			
			String knowledgePointName ="";
			int rightPersent=0;
			int maxRightPersent=0;
			//从DataTable中获取数据
			try { 
				knowledgePointName=dt.getCell(position, "pointName");
				rightPersent=Math.round(Float.parseFloat( dt.getCell(position, "rightPersent"))*100);
				maxRightPersent=Math.round(Float.parseFloat(dt.getCell(position, "maxRightPersent"))*100);
			} catch (dataTableWrongException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//填充数据
			
			viewHolder.tvOrderNum.setText((position+1)+"");
			viewHolder.tvKnowledgePointName.setText(knowledgePointName);
			viewHolder.tvRightPersent.setText(rightPersent+"%");
			viewHolder.tvMaxRightPersent.setText(maxRightPersent+"%");
			
			/*if(type==0){
				viewHolder.tvOrderNum.setBackgroundColor(getResources().getColor(R.color.knowledgeDetailNumDual));
			}else if(type==1){
				viewHolder.tvOrderNum.setBackgroundColor(getResources().getColor(R.color.knowledgeDetailNumOdd));
			}*/
			
			return convertView;
		}
		
	}
	
	// 单数
	private class ViewHolder {
		TextView tvOrderNum;
		TextView tvKnowledgePointName;
		TextView tvRightPersent;
		TextView tvMaxRightPersent;
	}
	
	
	
}
