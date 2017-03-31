package com.ttqeducation.activitys.study;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Text;

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

import com.ttqeducation.R;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DesUtil;

public class KnowledgePointDetailActivity extends Activity {

	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null; // 标题栏文字；
	
	// 明细部分
	private ListView lvKnowledgePointDetail = null;
	private MyAdapter myAdapter;
	
	private RefreshView refreshView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_knowledge_point_detail);
		
		initView();
		
		getDataFromIntent();		
	}

	public void initView() {
		// 标题栏部分 实例化；
		this.titleTextView = (TextView)
				(super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("知识点明细");
		
		this.titleBackLayout = (LinearLayout)
				(super.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				KnowledgePointDetailActivity.this.finish();
			}
		});
	
		lvKnowledgePointDetail = (ListView)findViewById(R.id.lvKnowledgePointDetail);
	}
	
	public void getDataFromIntent() {
		Intent intent = getIntent();
		String timeType = intent.getStringExtra("timeType");
		if(timeType != null && timeType.length() > 0 && timeType.equals("天")) {
			String studentID = intent.getStringExtra("studentID");
			String subjectID = intent.getStringExtra("subjectID");
			String date = intent.getStringExtra("date");
			String classID = intent.getStringExtra("classID");
			
			APP_getStudentKnowledgeDetailByDay(studentID, subjectID, date, classID);
		} else if(timeType != null && timeType.length() > 0 && timeType.equals("周")) {
			String studentID = intent.getStringExtra("studentID");
			String subjectID = intent.getStringExtra("subjectID");
			String weekNum = intent.getStringExtra("weekNum");
			String classID = intent.getStringExtra("classID");
			
			APP_getStudentKnowledgeDetailByWeek(studentID, subjectID, weekNum, classID);
		} else if(timeType != null && timeType.length() > 0 && timeType.equals("月")) {
			String studentID = intent.getStringExtra("studentID");
			String subjectID = intent.getStringExtra("subjectID");
			String termID = intent.getStringExtra("termID");
			int month = Integer.parseInt(intent.getStringExtra("month"));
			String classID = intent.getStringExtra("classID");
			
			APP_getStudentKnowledgeDetailByMonth(studentID, subjectID, termID, month, classID);
		} else if(timeType != null && timeType.length() > 0 && (timeType.equals("期中") || timeType.equals("期末"))) {
			String studentID = intent.getStringExtra("studentID");
			String subjectID = intent.getStringExtra("subjectID");
			String termID = intent.getStringExtra("termID");
			int termType = intent.getIntExtra("termType", 0);
			String classID = intent.getStringExtra("classID");
			
			APP_getStudentKnowledgeDetailByTerm(studentID, subjectID, termID, termType, classID);
		}
	}

	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(KnowledgePointDetailActivity.this,
				toastMessage, Toast.LENGTH_SHORT);
		toast.show();
	}

	/**
	 * 学生每天知识点掌握度详细情况说明
	 * 
	 * @param studentID
	 * @param subjectID
	 * @param data
	 * @param classID
	 */
	public void APP_getStudentKnowledgeDetailByDay(String studentID, String subjectID, 
			String date, String classID) {
		
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, DataTable>() {
			@Override
			
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				DataTable dt_student = new DataTable();
				
				// 方法名
				String methodName = "APP_getStudentKnowledgeDetailByDay";
				
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				paramsMap.put("studentID", params[0].toString());
				paramsMap.put("subjectID", params[1].toString());
				paramsMap.put("date", params[2].toString());	
				paramsMap.put("classID", params[3].toString());								
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
					dt_student = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "getStudentKnowledgeByDay()...出错了。。。");
					e.printStackTrace();
				}
				
				return dt_student;
			}

			protected void onPostExecute(DataTable result) {
				
				// 把获取到的数据写入界面
				initViewAfterGetData(result);
				
				// 关闭刷新；
				refreshView.dismiss();											
			};
		}.execute(studentID, subjectID, date, classID);
	}
	
	/**
	 * 学生每周知识点掌握度详细情况说明
	 * 
	 * @param studentID
	 * @param subjectID
	 * @param weekNum
	 * @param classID
	 */
	public void APP_getStudentKnowledgeDetailByWeek(String studentID, String subjectID, 
			String weekNum, String classID) {
		
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, DataTable>() {
			@Override
			
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				DataTable dt_student = new DataTable();
				
				// 方法名
				String methodName = "APP_getStudentKnowledgeDetailByWeek";
				
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				paramsMap.put("studentID", params[0].toString());
				paramsMap.put("subjectID", params[1].toString());
				paramsMap.put("weekNum", params[2].toString());	
				paramsMap.put("classID", params[3].toString());								
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
					dt_student = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "getStudentKnowledgeByDay()...出错了。。。");
					e.printStackTrace();
				}
				
				return dt_student;
			}

			protected void onPostExecute(DataTable result) {
				
				// 把获取到的数据写入界面
				initViewAfterGetData(result);
				
				// 关闭刷新；
				refreshView.dismiss();											
			};
		}.execute(studentID, subjectID, weekNum, classID);
	}

	/**
	 * 学生每月知识点掌握度详细情况说明
	 * 
	 * @param studentID
	 * @param subjectID
	 * @param termID
	 * @param month
	 * @param classID
	 */
	public void APP_getStudentKnowledgeDetailByMonth(String studentID, String subjectID, 
			String termID, int month, String classID) {
	
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, DataTable>() {
			@Override
			
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}
	
			@Override
			protected DataTable doInBackground(Object... params) {
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				DataTable dt_student = new DataTable();
				
				// 方法名
				String methodName = "APP_getStudentKnowledgeDetailByMonth";
				
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				paramsMap.put("studentID", params[0].toString());
				paramsMap.put("subjectID", params[1].toString());
				paramsMap.put("termID", params[2].toString());	
				paramsMap.put("month", params[3].toString());
				paramsMap.put("classID", params[4].toString());
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
					dt_student = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "getStudentKnowledgeByDay()...出错了。。。");
					e.printStackTrace();
				}
				
				return dt_student;
			}
	
			protected void onPostExecute(DataTable result) {
				
				// 把获取到的数据写入界面
				initViewAfterGetData(result);
				
				// 关闭刷新；
				refreshView.dismiss();											
			};
		}.execute(studentID, subjectID, termID, month, classID);
	}

	/**
	 * 学生学期节点知识点掌握度详细情况说明,1为期中,2为期末
	 * 
	 * @param studentID
	 * @param subjectID
	 * @param termID
	 * @param termType
	 * @param classID
	 */
	public void APP_getStudentKnowledgeDetailByTerm(String studentID, String subjectID, 
			String termID, int termType, String classID) {
	
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, DataTable>() {
			@Override
			
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}
	
			@Override
			protected DataTable doInBackground(Object... params) {
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				DataTable dt_student = new DataTable();
				
				// 方法名
				String methodName = "APP_getStudentKnowledgeDetailByTerm";
				
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				paramsMap.put("studentID", params[0].toString());
				paramsMap.put("subjectID", params[1].toString());
				paramsMap.put("termID", params[2].toString());	
				paramsMap.put("termType", params[3].toString());
				paramsMap.put("classID", params[4].toString());
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
					dt_student = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "getStudentKnowledgeByDay()...出错了。。。");
					e.printStackTrace();
				}
				
				return dt_student;
			}
	
			protected void onPostExecute(DataTable result) {
				
				// 把获取到的数据写入界面
				initViewAfterGetData(result);
				
				// 关闭刷新；
				refreshView.dismiss();											
			};
		}.execute(studentID, subjectID, termID, termType, classID);
	}
	
	// 获取数据后展示到界面上
	private void initViewAfterGetData(DataTable result) {					
		if(result != null) {
			myAdapter = new MyAdapter(getApplicationContext(), result);
			lvKnowledgePointDetail.setAdapter(myAdapter);
		}
	}
	
	private class MyAdapter extends BaseAdapter {
		
		private Context context;		
		private LayoutInflater inflater;
		private DataTable dt;
		
		public MyAdapter(Context context, DataTable dt) {
			this.context = context;
			inflater = LayoutInflater.from(context);
			this.dt = dt;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dt.getRowCount();
		}

		@Override
		public Object getItem(int position) {
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
			int type = 0;
			
			if((position % 2) == 0) {
				type = 0;
			} else if ((position % 2) == 1) {
				type = 1;
			}
			
			return type;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolderOdd viewHolderOdd = null; // 单数
			ViewHolderDual viewHolderDual = null; // 双数
			
			int type = getItemViewType(position);
			
			// convertView为空，新建ViewHolder
			if(convertView == null) {
				if(type == 0) {
					convertView = inflater.inflate(R.layout.item_knowledge_detail_dual, parent, false);
					
					viewHolderDual = new ViewHolderDual();
					
					viewHolderDual.tvOrderNum = (TextView)convertView.findViewById(R.id.tvOrderNum);
					viewHolderDual.tvKnowledgePointName = (TextView)convertView.findViewById(R.id.tvKnowledgePointName);
					viewHolderDual.tvRightPersent = (TextView)convertView.findViewById(R.id.tvRightPersent);
					viewHolderDual.tvMaxRightPersent = (TextView)convertView.findViewById(R.id.tvMaxRightPersent);
				
					convertView.setTag(viewHolderDual);
				} else if (type == 1) {
					convertView = inflater.inflate(R.layout.item_knowledge_detail_odd, parent, false);
					
					viewHolderOdd = new ViewHolderOdd();
					
					viewHolderOdd.tvOrderNum = (TextView)convertView.findViewById(R.id.tvOrderNum);
					viewHolderOdd.tvKnowledgePointName = (TextView)convertView.findViewById(R.id.tvKnowledgePointName);
					viewHolderOdd.tvRightPersent = (TextView)convertView.findViewById(R.id.tvRightPersent);
					viewHolderOdd.tvMaxRightPersent = (TextView)convertView.findViewById(R.id.tvMaxRightPersent);
				
					convertView.setTag(viewHolderOdd);
				}
											
			// convertView不为空，从convertView中获取ViewHolder
			}else {			
				if(type == 0) {
					viewHolderDual = (ViewHolderDual)convertView.getTag();
				} else if (type == 1) {
					viewHolderOdd = (ViewHolderOdd)convertView.getTag();
				}												
			}
			
			// 从DataTable中获取数据并显示到屏幕上
			String knowledgePointName = "";
			int rightPersent = 0;
			int maxRightPersent = 0;
			
			// 从DataTable中获取数据			
			try {
				knowledgePointName = dt.getCell(position, "pointName");
				rightPersent = Math.round(Float.parseFloat(dt.getCell(position, "rightPersent")) * 100);
				maxRightPersent = Math.round(Float.parseFloat(dt.getCell(position, "maxRightPersent")) * 100);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (dataTableWrongException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// 往界面填充数据
			if(type == 0) {				
				viewHolderDual.tvOrderNum.setText((position + 1) + "");
				viewHolderDual.tvKnowledgePointName.setText(knowledgePointName);
				viewHolderDual.tvRightPersent.setText(rightPersent + "%");
				viewHolderDual.tvMaxRightPersent.setText(maxRightPersent + "%");
			} else if (type == 1) {
				viewHolderOdd.tvOrderNum.setText((position + 1) + "");
				viewHolderOdd.tvKnowledgePointName.setText(knowledgePointName);
				viewHolderOdd.tvRightPersent.setText(rightPersent + "%");
				viewHolderOdd.tvMaxRightPersent.setText(maxRightPersent + "%");
			}
			
			return convertView;
		}					
	}
	
	// 单数
	private class ViewHolderOdd {
		TextView tvOrderNum;
		TextView tvKnowledgePointName;
		TextView tvRightPersent;
		TextView tvMaxRightPersent;
	}
	
	// 双数
	private class ViewHolderDual {
		TextView tvOrderNum;
		TextView tvKnowledgePointName;
		TextView tvRightPersent;
		TextView tvMaxRightPersent;
	}
	
}
