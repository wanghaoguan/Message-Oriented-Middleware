package com.ttqeducation.teacher.activitys.teach;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.TeacherInfo;
import com.ttqeducation.teacher.beans.dataTableWrongException;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author hxy
 * 从主界面的主观题批改按钮点击进入此界面。展示主观题作业列表
 *
 */
public class SubjectiveHomeworkActivity extends Activity {

	//标题栏部分
	private LinearLayout titleBackLayout = null;
	private TextView titleTextView = null;
	private RefreshView refreshView = null;
	private LinearLayout llSubject=null;  //科目列表
	private TextView tvSubject = null;  //科目名称
	private LinearLayout llChooseSubject=null; //科目下拉列表
	private ListView lvChooseSubject = null;
	
	private ListView lvSubjectiveHomework = null;
	//科目
	private String[] subjects;
	private String subjectName ="语文";
	private String subjectID="";
	
	private String teacherID="";
	private String termID="";
	private String classID="";
	
	private List<SubjectiveHomework> subjectveHomeworkList = new ArrayList<SubjectiveHomework>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subjective_homework_list);
		getDate();
		initView();
	}


	private void initView() {
		// TODO Auto-generated method stub
		this.titleBackLayout = (LinearLayout) (super.findViewById(R.id.action_bar)
				.findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SubjectiveHomeworkActivity.this.finish();
			}
		});
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar)
				.findViewById(R.id.title_text));
		this.titleTextView.setText("作业列表");
		//科目选择按钮
		llSubject=(LinearLayout)findViewById(R.id.llSubject);
		tvSubject = (TextView)findViewById(R.id.tvSubject);
		//科目选择list
		llChooseSubject=(LinearLayout)findViewById(R.id.llChooseSubject);
		lvChooseSubject =(ListView)findViewById(R.id.lvChooseSubject);
		llSubject.setOnClickListener(new  MyOnClickListener());
		initChooseSubject();
		//作业列表
		this.lvSubjectiveHomework =(ListView)findViewById(R.id.lvSubjectiveHomework);
		this.lvSubjectiveHomework.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				// TODO Auto-generated method stub
				
				int isOverdue =  subjectveHomeworkList.get(position).getIsoverdue();
				if(isOverdue == 0){
					showToast("该次作业未到提交时间，请稍后批阅");
				}else{
					String useID =  subjectveHomeworkList.get(position).getUseID();
					Intent intent = new Intent(SubjectiveHomeworkActivity.this, SubjectiveHomeworkDetailActivity.class);
					intent.putExtra("useID", useID);
					startActivity(intent);
				}
				
			}
		});
		
	}




	private void getDate() {
		// TODO Auto-generated method stub
		this.teacherID = TeacherInfo.getInstance().execTeacherID;
		this.classID = TeacherInfo.getInstance().getClassID();
		this.termID = TeacherInfo.getInstance().getTermID();
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
					llChooseSubject.setVisibility(view.GONE);
					APPgetSubjectiveTestList(teacherID, classID, subjectID, termID);
				}
				 
			});
			 APPgetSubjectiveTestList(teacherID, classID, subjectID, termID);
		}
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
	
	
	/**
	 * 获取主观题作业列表
	 * @param createrID
	 * @param classID
	 * @param subjectID
	 * @param termID
	 */
	private void APPgetSubjectiveTestList(String createrID, String classID,
			String subjectID,String termID){
		this.refreshView= new RefreshView(this,R.style.full_screen_dialog);
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
				String methodName = "APP_getSubjectiveTestList";
				//参数Map
				Map<String,String> paramsMap = new HashMap<String,String>();
				String tokenID ="";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("createrID",params[0].toString());
				paramsMap.put("classID",params[1].toString());
				paramsMap.put("subjectID",params[2].toString());
				paramsMap.put("termID",params[3].toString());
				paramsMap.put("TokenID",tokenID);
				
				//开始访问数据			
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
					Log.i("error", "APP_getSubjectiveTestList()...出错了。。。");
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
			
		}.execute( createrID,  classID, subjectID, termID);
	}
	 
	private void initViewAfterGetData(DataTable result){
		if(result !=null && result.getRowCount()>0){
			for(int i=0;i<result.getRowCount();i++ ){
				try {
					subjectveHomeworkList.add(new SubjectiveHomework(result.getCell(i, "useID"),
							result.getCell(i, "testName"),result.getCell(i, "endTime"),Integer.parseInt (result.getCell(i, "isoverdue"))));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			MyAdapter myAdapter = new MyAdapter(SubjectiveHomeworkActivity.this,
					R.layout.subjective_homework_list_item,subjectveHomeworkList );
			this.lvSubjectiveHomework.setAdapter(myAdapter);
			
		}
	}
	
	public class MyAdapter extends ArrayAdapter<SubjectiveHomework>{

		private int resourceID;
		public MyAdapter(Context context, int resource, 
				List<SubjectiveHomework> objects) {
			super(context, resource, objects);
			resourceID= resource;
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			SubjectiveHomework subjectiveHomework = getItem(position);
			View  view =LayoutInflater.from(getContext()).inflate(resourceID, null);
			TextView tvEndTime = (TextView)view.findViewById(R.id.tvEndTime);
			TextView tvTestName = (TextView) view.findViewById(R.id.tvTestName);
			TextView tvStatus = (TextView) view.findViewById(R.id.tvStatus);
			tvEndTime.setText(subjectiveHomework.getEndTime());
			tvTestName.setText(subjectiveHomework.getTestName());
			if(subjectiveHomework.getIsoverdue() == 0){
				tvStatus.setTextColor(getResources().getColor(R.color.textRed));
				tvStatus.setText("未截止");				
			}else{
				tvStatus.setTextColor(getResources().getColor(R.color.textGreen2));
				tvStatus.setText("已截止");
			}
			return view;
		}
		
		
		
	}
	
	class SubjectiveHomework{
		
		private String useID;
		private String testName; 
		private String endTime;
		private int isoverdue;		

		public SubjectiveHomework(String useID,String testName,String endTime,int isoverdue){
			this.useID=useID;
			this.testName = testName;
			this.endTime = endTime;
			this.isoverdue = isoverdue;
		}
		
		public String getUseID() {
			return useID;
		}

		public void setUseID(String useID) {
			this.useID = useID;
		}
		
		public String getTestName() {
			return testName;
		}

		public void setTestName(String testName) {
			this.testName = testName;
		}
		
		public String getEndTime() {
			return endTime;
		}

		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
		
		public int getIsoverdue() {
			return isoverdue;
		}

		public void setIsoverdue(int isoverdue) {
			this.isoverdue = isoverdue;
		}
	}
	
	// 窗口提示信息；
	public void showToast(String toastMessage){
		Toast toast =  Toast.makeText(SubjectiveHomeworkActivity.this, toastMessage, Toast.LENGTH_SHORT);
		toast.show();
	}
}
