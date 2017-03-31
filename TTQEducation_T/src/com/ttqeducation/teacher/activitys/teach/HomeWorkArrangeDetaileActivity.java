package com.ttqeducation.teacher.activitys.teach;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.tools.DesUtil;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 家庭作业布置页面，用于显示页码题库的题目信息
 * @author 侯翔宇
 * 
 *
 */
public class HomeWorkArrangeDetaileActivity extends Activity   {

	
	//从上一个界面传入数据
	private String testID;
	private String typeBelong;
	private String subjectID;
	private String testName;
	private String classID;
	private String teacherID;
	//标题栏
	private LinearLayout titleBackLayout = null;
	private TextView titleTextView = null;
	//布置按钮
	private LinearLayout llSubmit=null;//布置家庭作业按钮
	//题目列表
	private ListView lvHomeWorkArrangeItem =null;
	//提交时间
	public String mm="";
	public String dd="";
	
	
	private RefreshView refreshView;
	private List<Question> questionList = new ArrayList<Question>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homework_arrange_detaile);
		getDataFromIntent();
		initView();
		APPgetQuestionPreviewByTest(testID, typeBelong);
		
	}
	
	

	private void initView() {
		// TODO Auto-generated method stub
		this.titleBackLayout = (LinearLayout)findViewById(R.id.title_back_layout);
		this.titleTextView =(TextView)findViewById(R.id.title_text);
		titleTextView.setText("家庭作业内容预览");
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HomeWorkArrangeDetaileActivity.this.finish();
			}
		});
		
		this.llSubmit =(LinearLayout)findViewById(R.id.llSubmit);
		this.lvHomeWorkArrangeItem=(ListView)findViewById(R.id.listView_homeWorkArrangeItem);
		this.llSubmit = (LinearLayout)findViewById(R.id.llSubmit);
		this.llSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				 Calendar cal = Calendar.getInstance();
				
				 MyDatePickerDialog dialog= new MyDatePickerDialog(HomeWorkArrangeDetaileActivity.this, new MyDatePickerDialog.OnDateSetListener() {
					
					@Override 
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						// TODO Auto-generated method stub
						String mm="";
						String dd ="";
						if(monthOfYear<=9){
							mm="0"+(monthOfYear+1);
						}else{
							mm=String.valueOf(monthOfYear+1);
						}
						if(dayOfMonth<=9){
							dd= "0"+dayOfMonth;
						}else{
							dd=String.valueOf(dayOfMonth);
						}
						String dateStr = String.valueOf(year)+"-"+mm+"-"+dd+" 00:00:00";
						
						setSubmitTime(mm, dd);
						APPTeachInsertTestUse(testID, testName, classID, 1, subjectID, teacherID, dateStr, 0, 0);
						//showToast(dateStr);
					}
				}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
				
				cal.add(Calendar.DAY_OF_MONTH, 1);
				dialog.getDatePicker().setMinDate(cal.getTime().getTime());//设定只能选择当天之后的时间
				dialog.setTitle("选择提交时间");
				dialog.show();
			}
		});		
	}

	private void setSubmitTime(String mm,String dd){
		this.mm= mm;
		this.dd= dd;
	}
	
	private String getSubmitMonth(){
		return this.mm;
	}
	
	private String getSubmitDay(){
		return this.dd;
	}
	
	private void getDataFromIntent() {
		// TODO Auto-generated method stub
		this.testID = getIntent().getStringExtra("testID");
		this.typeBelong=getIntent().getStringExtra("typeBelong");
		this.subjectID = getIntent().getStringExtra("subject");
		this.testName = getIntent().getStringExtra("testName");
		this.classID = getIntent().getStringExtra("classID");
		this.teacherID = getIntent().getStringExtra("teacherID");
		
	}
	
	
	/**
	 * APP使用,教师备课时查看题目预览
	 * @param testID
	 * @param typeBelong
	 */
	private void APPgetQuestionPreviewByTest(String testID, String typeBelong){
		
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
				String methodName = "APP_getQuestionPreviewByTest";
				//参数Map
				Map<String,String> paramsMap = new HashMap<String,String>();
				String tokenID ="";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("testID", params[0].toString());
				paramsMap.put("typeBelong", params[1].toString());
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
					Log.i("error", "APP_getQuestionPreviewByTest()...出错了。。。");
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
			
		}.execute(testID,typeBelong);
	}

	protected void initViewAfterGetData(DataTable result) {
		// TODO Auto-generated method stub
		if(result!=null && result.getRowCount()>0){ 
			for(int i=0;i<result.getRowCount();i++ ){
				try {
					questionList.add(new Question(result.getCell(i, "questionCode")));
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			MyAdapter myAdapter = new MyAdapter(HomeWorkArrangeDetaileActivity.this, 
					R.layout.homework_arrange_detail_item, questionList);
			this.lvHomeWorkArrangeItem.setAdapter(myAdapter);
		}
	}
	
	class Question{
		private String questionCode;

		
		public String getQuestionCode() {
			return questionCode;
		}

		public void setQuestionCode(String questionCode) {
			this.questionCode = questionCode;
		}
		
		public Question(String questionCode){
			this.questionCode=questionCode;
		}
		
	}
	
	public class MyAdapter extends ArrayAdapter<Question>{

		private int resourceID;
		public MyAdapter(Context context, int resource, List<Question> objects) {
			super(context, resource, objects);
			resourceID = resource;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Question question=getItem(position);
			View view = LayoutInflater.from(getContext()).inflate(resourceID, null);
			TextView tvQuestionCode =(TextView)view.findViewById(R.id.tvQuestionName);
			tvQuestionCode.setText(question.getQuestionCode());
			return view;
		}
		
		
		
	}

    class MyDatePickerDialog extends DatePickerDialog {

        public MyDatePickerDialog(Context context, OnDateSetListener callBack,
                int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
        }
        @Override
        protected void onStop() {
            //super.onStop();
        }
    }
	
    /**
     * 供期教师APP使用，主要用来布置家庭作业，在teach_testUse中写入截止时间、是否判过并生成averageRate表数据（未判过），是否批改过主观题
     * @param testID
     * @param testName
     * @param classID
     * @param testTypeID
     * @param subjectID
     * @param creatorID
     * @param endTime
     * @param isChecked
     * @param isPictureChecked
     */
    public void APPTeachInsertTestUse(String testID, String testName, String classID, int testTypeID, String subjectID,
    		String creatorID,String endTime,int isChecked,int isPictureChecked){
    	
    	this.refreshView = new RefreshView(this,  R.style.full_screen_dialog);
    	new AsyncTask<Object, Object, DataTable >(){
   		    		
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
				DataTable result = new DataTable();
				// 方法名
				String methodName = "APP_teach_insertTestUse";
				//参数Map
				Map<String,String> paramsMap = new HashMap<String, String>();
				String tokenID ="";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("testID",params[0].toString());
				paramsMap.put("testName",params[1].toString());
				paramsMap.put("classID",params[2].toString());
				paramsMap.put("testTypeID",params[3].toString());
				paramsMap.put("subjectID",params[4].toString());
				paramsMap.put("creatorID",params[5].toString());
				paramsMap.put("endTime",params[6].toString());
				paramsMap.put("isChecked",params[7].toString());
				paramsMap.put("isPictureChecked",params[8].toString());
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
					result = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "APP_getTestList()...出错了。。。");
					e.printStackTrace();
				}	
				
				return result;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				try {
					teachStudentTestuseInsert(Integer.valueOf(result.getCell(0, "useID")));
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (dataTableWrongException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				refreshView.dismiss();
				try {
					if(result !=null && Integer.valueOf( result.getCell(0, "useID").toString())>0){
						Toast toast =  Toast.makeText(HomeWorkArrangeDetaileActivity.this, 
								"布置成功,作业收取时间为"+getSubmitMonth()+"月"+getSubmitDay()+"日", Toast.LENGTH_LONG);
						toast.show();
					}else{
						Toast toast =  Toast.makeText(HomeWorkArrangeDetaileActivity.this, 
								"布置失败", Toast.LENGTH_LONG);
						toast.show(); 
					}
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
    		
    	}.execute(testID,  testName,  classID,  testTypeID,  subjectID,
        		 creatorID, endTime, isChecked, isPictureChecked);
    }
    
    
    /**
     * 教师布置作业时需要调用,在teach_student_testuse中插入数据，默认学生为未答状态
     * @param useID
     */
    public void teachStudentTestuseInsert(int useID){
    	
    	new AsyncTask<Object, Object, Integer>() {    		
    		
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();				
			}

			@Override
			protected Integer doInBackground(Object... params) {
				// TODO Auto-generated method stub
				
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				int result = -1;
				// 方法名
				String methodName = "teach_student_testuse_insert";
				//参数Map
				Map<String,String> paramsMap = new HashMap<String, String>();
				String tokenID ="";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("useID",params[0].toString());				
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
					result =Integer.valueOf( getdatatool.getDataAsString(methodName, paramsMap));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "teach_student_testuse_insert()...出错了。。。");
					e.printStackTrace();
				}	
				
				return result;
			}

			@Override
			protected void onPostExecute(Integer result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
			}

			
		}.execute(useID);
    }
    
	// 窗口提示信息；
	public void showToast(String toastMessage){
		Toast toast =  Toast.makeText(HomeWorkArrangeDetaileActivity.this, toastMessage, Toast.LENGTH_SHORT);
		toast.show();
	}
}
