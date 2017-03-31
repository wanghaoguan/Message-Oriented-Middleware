package com.ttqeducation.activitys.study;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ttqeducation.R;
import com.ttqeducation.beans.DataRow;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.TestInfo;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DateUtil;
import com.ttqeducation.tools.DensityUtils;
import com.ttqeducation.tools.DesUtil;
import com.ttqeducation.tools.GeneralTools;
import com.ttqeducation.tools.HomeArc;
import com.ttqeducation.tools.ScreenUtils;

/**
 * 这个是学生作业结果展示，用于展示学生的各种作业完成情况。
 * 需要做的事情： 
 * 1.接受传过来的参数；
 * 2.根据传过来的参数获取数据；
 * 3.展示数据 ；
 * 4.点击“查看详细”跳转到TaskResultsDetailActivity；
 * （1）各类型作业答题详细列表（questionCode, textPageNum, pointName, isAnswer, isRight）；
 * （2）如果是考试,显示考试排名表。
 * 
 * @author 王勤为
 * 
 */
public class TaskResultsActivity extends Activity {

	private String subjectID; // 科目ID,全科：0， 语文：1， 数学：2， 英语：3
	private String workTypeID;
	private String studentID;
	private String classID;
	private String termID;
	
	public String day_choosed; // 选择的天
	public String week_choosed;// 选择的周
	public DataTable origin_student_knowledgeInfo_datatable;// 原始学生知识点数据表
	public DataTable good_knowledgeInfo_datatable;// 优知识点数据表
	public DataTable standard_knowledgeInfo_datatable;// 良知识点数据表
	public DataTable bad_knowledgeInfo_datatable;// 差知识点数据表

	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；
	// 科目选择
	private LinearLayout llSubject;
	private TextView tvSubject;
	private ImageView ivSubject;
	private LinearLayout llChooseSubject;
	private String[] subjects;
	private ListView lvChooseSubject;
	// 时间选择
	private LinearLayout llDate;
	private TextView tvDate;
	private ImageView ivDate;
	// 日历界面
	private LinearLayout llChooseDate;
	private LinearLayout[] daylayoutArray;
	private TextView yearAndMonth;
	private Date currentDate;
	private int weeknum;
	// 测试选择界面
	private LinearLayout llChooseTest;
	private ListView lvChooseTest;
	private String testName = ""; // 测试名称；
	//用Map存放useID,testName
	private List<TestInfo> listTest = new ArrayList<TestInfo>();
	private List<String> test = new ArrayList<String>();
	private ArrayAdapter<String> testAdapter;
	
	// 界面部分；
	private ListView lvTaskResult = null;
	private RelativeLayout rlNotice = null;
	
	// 条件变量；
	private String timeType = ""; // 从前一个界面获取时间类型：day,week,unit,midterm,finalterm
	private String timeValueStr = ""; // 从前一个界面获取数据，如 具体的日期，周，单元测试useID，等等；
	private String taskName = ""; // 测试名称；如：家庭作业完成情况;
	private String subjectName = ""; // 科目名称；如：数学;

	private RefreshView refreshView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_results);

		getDataFromIntent();
		
		getData();
				
		initView();
		
		// 展示家庭作业或者课堂作业的数据， workTypeID：1 家庭作业 2 课堂练习模式二 3 课堂练习模式一 4 单元测试 5 期中模拟测试 6 期末模拟考试
		if(workTypeID.equals("1")|workTypeID.equals("2")){
			if(timeValueStr != null && timeValueStr.length() > 0) {				
				get_studentDailyTaskOverView(studentID, subjectID, workTypeID, timeValueStr, timeType);
			}
		}				
	}
	
	// 从上一个界面获取的数据；
	public void getDataFromIntent() {
		
		this.timeType = getIntent().getStringExtra("timeType");
		this.timeValueStr = getIntent().getStringExtra("valueStr");
		this.taskName = getIntent().getStringExtra("taskName");
		this.subjectName = getIntent().getStringExtra("subjectName");
	}	
	
	// 获取参数
	public void getData() {
		
		subjectID = GeneralTools.getInstance().getSubjectIDByName(subjectName);
		workTypeID = GeneralTools.getInstance().getWorkTypeIDByName(taskName);
		studentID = UserInfo.getInstance().studentID;
		classID = UserInfo.getInstance().classID;
		termID = UserInfo.getInstance().termID;
	}
	
	public void initView() {
		// 设置标题栏中字体的显示；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText(taskName);
		
		// 标题栏部分 实例化；
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				TaskResultsActivity.this.finish();
			}
		});
		
		// 初始化左上角科目选择按钮（界面）
		initChooseSubject();

		// 时间选择
		llDate = (LinearLayout)findViewById(R.id.llDate);
		llDate.setOnClickListener(new MyOnClickLinstener());
		tvDate = (TextView)findViewById(R.id.tvDate);
		ivDate = (ImageView)findViewById(R.id.ivDate);		
		
		// 历史回顾
		llChooseDate = (LinearLayout)findViewById(R.id.llChooseDate);
		llChooseTest = (LinearLayout)findViewById(R.id.llChooseTest);
		
		if(taskName != null && taskName.length() > 0 
				&& (taskName.equals("家庭作业完成情况") || taskName.equals("课堂作业完成情况"))) {
											
			// 日历界面			
			daylayoutArray = new LinearLayout[42];
			daylayoutArray[0] = (LinearLayout) findViewById(R.id.daylayout1);
			daylayoutArray[1] = (LinearLayout) findViewById(R.id.daylayout2);
			daylayoutArray[2] = (LinearLayout) findViewById(R.id.daylayout3);
			daylayoutArray[3] = (LinearLayout) findViewById(R.id.daylayout4);
			daylayoutArray[4] = (LinearLayout) findViewById(R.id.daylayout5);
			daylayoutArray[5] = (LinearLayout) findViewById(R.id.daylayout6);
			daylayoutArray[6] = (LinearLayout) findViewById(R.id.daylayout7);
			daylayoutArray[7] = (LinearLayout) findViewById(R.id.daylayout8);
			daylayoutArray[8] = (LinearLayout) findViewById(R.id.daylayout9);
			daylayoutArray[9] = (LinearLayout) findViewById(R.id.daylayout10);
			daylayoutArray[10] = (LinearLayout) findViewById(R.id.daylayout11);
			daylayoutArray[11] = (LinearLayout) findViewById(R.id.daylayout12);
			daylayoutArray[12] = (LinearLayout) findViewById(R.id.daylayout13);
			daylayoutArray[13] = (LinearLayout) findViewById(R.id.daylayout14);
			daylayoutArray[14] = (LinearLayout) findViewById(R.id.daylayout15);
			daylayoutArray[15] = (LinearLayout) findViewById(R.id.daylayout16);
			daylayoutArray[16] = (LinearLayout) findViewById(R.id.daylayout17);
			daylayoutArray[17] = (LinearLayout) findViewById(R.id.daylayout18);
			daylayoutArray[18] = (LinearLayout) findViewById(R.id.daylayout19);
			daylayoutArray[19] = (LinearLayout) findViewById(R.id.daylayout20);
			daylayoutArray[20] = (LinearLayout) findViewById(R.id.daylayout21);
			daylayoutArray[21] = (LinearLayout) findViewById(R.id.daylayout22);
			daylayoutArray[22] = (LinearLayout) findViewById(R.id.daylayout23);
			daylayoutArray[23] = (LinearLayout) findViewById(R.id.daylayout24);
			daylayoutArray[24] = (LinearLayout) findViewById(R.id.daylayout25);
			daylayoutArray[25] = (LinearLayout) findViewById(R.id.daylayout26);
			daylayoutArray[26] = (LinearLayout) findViewById(R.id.daylayout27);
			daylayoutArray[27] = (LinearLayout) findViewById(R.id.daylayout28);
			daylayoutArray[28] = (LinearLayout) findViewById(R.id.daylayout29);
			daylayoutArray[29] = (LinearLayout) findViewById(R.id.daylayout30);
			daylayoutArray[30] = (LinearLayout) findViewById(R.id.daylayout31);
			daylayoutArray[31] = (LinearLayout) findViewById(R.id.daylayout32);
			daylayoutArray[32] = (LinearLayout) findViewById(R.id.daylayout33);
			daylayoutArray[33] = (LinearLayout) findViewById(R.id.daylayout34);
			daylayoutArray[34] = (LinearLayout) findViewById(R.id.daylayout35);
			daylayoutArray[35] = (LinearLayout) findViewById(R.id.daylayout36);
			daylayoutArray[36] = (LinearLayout) findViewById(R.id.daylayout37);
			daylayoutArray[37] = (LinearLayout) findViewById(R.id.daylayout38);
			daylayoutArray[38] = (LinearLayout) findViewById(R.id.daylayout39);
			daylayoutArray[39] = (LinearLayout) findViewById(R.id.daylayout40);
			daylayoutArray[40] = (LinearLayout) findViewById(R.id.daylayout41);
			daylayoutArray[41] = (LinearLayout) findViewById(R.id.daylayout42);
			
			yearAndMonth = (TextView) findViewById(R.id.yearAndMonth);
			
			initCalendar(new Date());
			
			// 显示选择的科目和时间
			tvSubject.setText(subjectName);
			tvDate.setText(timeValueStr);
		} else { // 单元、期中和期末测试			
			lvChooseTest = (ListView)findViewById(R.id.lvChooseTest);
			
			initChooseTest();
			
			// 获取测试情况
			if (taskName.equals("单元测试完成情况")) {
				// 获取单元测试信息
				get_unitTestList(subjectID, classID, termID);
			} else if (taskName.equals("期中测试完成情况")) {
				// 获取期中测试信息
				get_MidTermTestList(subjectID, classID, termID);
			} else if (taskName.equals("期末测试完成情况")) {
				// 获取期末测试信息
				get_FinalTermTestList(subjectID, classID, termID);
			}							
			
			// 显示选择的科目和时间
			tvSubject.setText(subjectName);
			tvDate.setText("历史回顾");
		}
		
		lvTaskResult = (ListView)findViewById(R.id.lvTaskResult);
		rlNotice = (RelativeLayout)findViewById(R.id.rlNotice); // 如果当天只有只有一次作业，为了不让界面显得太空白，添加图片提示
	}
	
	// 科目选择
	public void initChooseSubject() {
		
		llSubject = (LinearLayout)findViewById(R.id.llSubject);
		llSubject.setOnClickListener(new MyOnClickLinstener());
		tvSubject = (TextView)findViewById(R.id.tvSubject);
		ivSubject = (ImageView)findViewById(R.id.ivSubject);
		
		llChooseSubject = (LinearLayout)findViewById(R.id.llChooseSubject);
		lvChooseSubject = (ListView)findViewById(R.id.lvChooseSubject);
		
		subjects = new String[]{"语文", "数学", "英语"};
		ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_subject, subjects);
		lvChooseSubject.setAdapter(subjectAdapter);
		lvChooseSubject.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String subject = subjects[arg2];
				subjectID = GeneralTools.getInstance().getSubjectIDByName(subject);				
				
				if(workTypeID.equals("4")|workTypeID.equals("5")|workTypeID.equals("6")){
					// 获取测试情况
					if (taskName.equals("单元测试完成情况")) {
						// 获取单元测试信息
						get_unitTestList(subjectID, classID, termID);
					} else if (taskName.equals("期中测试完成情况")) {
						// 获取期中测试信息
						get_MidTermTestList(subjectID, classID, termID);
					} else if (taskName.equals("期末测试完成情况")) {
						// 获取期末测试信息
						get_FinalTermTestList(subjectID, classID, termID);
					}
				} else {
					get_studentDailyTaskOverView(studentID, subjectID, workTypeID,
							timeValueStr, timeType);
				}
				
				subjectName = subject;
				tvSubject.setText(subjectName);				
				llChooseSubject.setVisibility(View.GONE);
			}
		});
	}
				
	public void initCalendar(Date date) {
		for(int i = 0; i < 42; i++) {
			daylayoutArray[i].setBackgroundResource(0);
			((TextView)daylayoutArray[i].getChildAt(0)).setText("");
			((ImageView)daylayoutArray[i].getChildAt(1)).setImageResource(0);
		}
		currentDate = date;
	    String selectdate = DateUtil.convertDateToString("yyyy年MM月", date);
	    yearAndMonth.setText(selectdate);
	    
	    String firstDate =  DateUtil.getFirstDayOfMonth(date, "yyyy-MM-dd");
	    String lastDate = DateUtil.getlastDayOfMonth(date, "yyyy-MM-dd");
	    // 绑定数据到月历上
	    Date dateFirstDate = DateUtil.getFirstDayOfMonthDate(date);
	    // 星期日的weeknum为0，星期一的weeknum为1，......，星期六的weeknum为6
	    weeknum = DateUtil.getWeekNumOfDate(dateFirstDate);

	    int maxdaynum = Integer.parseInt(lastDate.substring(8,10));
	    int len = weeknum+maxdaynum;
	    TextView txtDay = null;
	    Date nowDate = new Date();
	    if(date.getYear() == nowDate.getYear() && date.getMonth() == nowDate.getMonth()) {
	    	// 获取当前日期的日
		    int day = 0;
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(date);
		    day = cal.get(Calendar.DATE);
		    day = weeknum + day;
	    	for(int i = weeknum; i < len; i++) {
		    	txtDay = (TextView)daylayoutArray[i].getChildAt(0);
		    	txtDay.setText((i-weeknum+1)+"");
		    	if(i < day) {
		    		// daylayoutArray[i].setBackgroundResource(R.drawable.menu_calendar_c);
		    		daylayoutArray[i].setBackgroundResource(R.drawable.datebox_past);
		    	} else {
		    		daylayoutArray[i].setBackgroundResource(R.drawable.datebox);
		    	}
	    	}	   	
	    } else if (date.getYear() < nowDate.getYear() || date.getYear() == nowDate.getYear() && date.getMonth() < nowDate.getMonth()){
	    	for(int i = weeknum; i < len; i++) {
		    	txtDay = (TextView)daylayoutArray[i].getChildAt(0);
		    	txtDay.setText((i-weeknum+1)+"");
		    	// daylayoutArray[i].setBackgroundResource(R.drawable.menu_calendar_c);
		    	daylayoutArray[i].setBackgroundResource(R.drawable.datebox_past);
	    	}
	    }else {
	    	for(int i = weeknum; i < len; i++) {
		    	txtDay = (TextView)daylayoutArray[i].getChildAt(0);
		    	txtDay.setText((i-weeknum+1)+"");
		    	daylayoutArray[i].setBackgroundResource(R.drawable.datebox);
	    	}
	    }		
	}
	
	/**
	 * 下一个月
	 * 
	 * @param v
	 */
	public void nextmonth(View v) {
		currentDate.setDate(1);
		currentDate.setMonth(currentDate.getMonth() + 1);
		Date nowDate = new Date();
		if(currentDate.getYear() == nowDate.getYear() && currentDate.getMonth() == nowDate.getMonth()) {
			currentDate = new Date();
		}
		initCalendar(currentDate);
	}

	/**
	 * 上一个月
	 * 
	 * @param v
	 */
	public void upmonth(View v) {
		currentDate.setDate(1);
		currentDate.setMonth(currentDate.getMonth() - 1);
		Date nowDate = new Date();
		if(currentDate.getYear() == nowDate.getYear() && currentDate.getMonth() == nowDate.getMonth()) {
			currentDate = new Date();
		}
		initCalendar(currentDate);
	}

	/**
	 * 某天的作业/测试结果
	 * 
	 * @param v
	 */
	public void task_info(View v) {
		LinearLayout line = (LinearLayout)v;
		
		if(!((TextView)line.getChildAt(0)).getText().equals("")) {
			TextView tvDay = (TextView)line.getChildAt(0);
			int day = Integer.parseInt(tvDay.getText().toString());									
			Date date = currentDate;			
			date.setDate(day);
			timeValueStr = DateUtil.convertDateToString("yyyy-MM-dd", date);
			tvDate.setText(timeValueStr);
			get_studentDailyTaskOverView(studentID, subjectID, workTypeID,
					timeValueStr,timeType);
			llChooseDate.setVisibility(View.GONE);
		}
	}

	public void initChooseTest() {
		for(int i = listTest.size() - 1; i >= 0; i--) {
			test.add(listTest.get(i).getTestName());
		}
		
		testAdapter = new ArrayAdapter<String>(getApplicationContext(), 
				R.layout.item_test, test);
		lvChooseTest.setAdapter(testAdapter);
		lvChooseTest.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				testName = test.get(arg2);
				timeValueStr = listTest.get(listTest.size() - arg2 - 1).getUseID();
				
				get_studentTestOverView(studentID, timeValueStr);
								
				llChooseTest.setVisibility(View.GONE);
			}
		});
	}
	
	class MyOnClickLinstener implements OnClickListener {

		public MyOnClickLinstener() {
			
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()) {
			case R.id.llSubject:
				// 当点击科目选择时，如果时间选择在前台则隐藏
				if(llChooseDate.getVisibility() == View.VISIBLE) {
					llChooseDate.setVisibility(View.GONE);
				}
				
				// 当点击科目选择时，如果测试选择在前台则隐藏
				if(llChooseTest.getVisibility() == View.VISIBLE) {
					llChooseTest.setVisibility(View.GONE);
				}
				
				if(llChooseSubject.getVisibility() == View.VISIBLE) {
					llChooseSubject.setVisibility(View.GONE);
				} else {
					llChooseSubject.setVisibility(View.VISIBLE);
				}
				
				break;
				
			case R.id.llDate :
				// 当点击事件选择时，如果科目选择在前台则隐藏
				if(llChooseSubject.getVisibility() == View.VISIBLE) {
					llChooseSubject.setVisibility(View.GONE);
				}
				
				if(workTypeID.equals("1")|workTypeID.equals("2")) {
					if(llChooseDate.getVisibility() == View.VISIBLE) {
						llChooseDate.setVisibility(View.GONE);
					} else {
						llChooseDate.setVisibility(View.VISIBLE);
					}
				} else {
					if(llChooseTest.getVisibility() == View.VISIBLE) {
						llChooseTest.setVisibility(View.GONE);
					} else {
						llChooseTest.setVisibility(View.VISIBLE);
					}
				}
				
				break;
				
			default:
				// 其它情况
				break;
			}
		}		
	}
	
	/**
	 * 获取发生过的单元测试
	 * 
	 */
	public void get_unitTestList(String subjectID, String classID, String termID) {
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, DataTable>() {

			// public String subjectID = null;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// 根据时间类型有不同的方法名和参数
				DataTable dt = new DataTable();
				// 方法名
				String methodName = "report_getAll_UnitTestList";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				// this.subjectID = params[0].toString();
				paramsMap.put("subjectID", params[0].toString());
				paramsMap.put("classID", params[1].toString());
				paramsMap.put("termID", params[2].toString());
				paramsMap.put("TokenID", tokenID);
				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 从本地获取学校URL
				SharedPreferences pre = getSharedPreferences("TTQAndroid",
						MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				if (schoolURL == null) {// 如果没有值
					return null;
				}
				getdatatool.setURL(schoolURL);
				try {
					dt = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "get_unitTestList()...出错了。。。");
					e.printStackTrace();
				}
				return dt;
			}

			protected void onPostExecute(DataTable result) {
				
				listTest.clear();
				
				if (result != null) {
					System.out.println(result.toString());// 测试代码，之后需要删除
					int count = result.getRowCount();
					try {
						for (int i = 0; i < count; i++) {
							String unitTestName = result.getCell(i, "unitInfo");
							String subjectID = result.getCell(i, "subjectID");
							String useID = result.getCell(i, "useID");
							
							TestInfo testInfo = new TestInfo(useID, unitTestName);
							listTest.add(testInfo);
						}						
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// 关闭刷新；
				refreshView.dismiss();

				initViewAfterGetData(); // 初始化界面		
			};
		}.execute(subjectID, classID, termID);
	}

	/**
	 * 获取发生过的期中测试
	 * 
	 */
	public void get_MidTermTestList(String subjectID, String classID,
			String termID) {
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, DataTable>() {
			// public String subjectID = null;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// 根据时间类型有不同的方法名和参数
				DataTable dt = new DataTable();
				// 方法名
				String methodName = "report_getAll_MidTermTestList";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				// this.subjectID = params[0].toString();
				paramsMap.put("subjectID", params[0].toString());
				paramsMap.put("classID", params[1].toString());
				paramsMap.put("termID", params[2].toString());
				paramsMap.put("TokenID", tokenID);
				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 从本地获取学校URL
				SharedPreferences pre = getSharedPreferences("TTQAndroid",
						MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				if (schoolURL == null) {// 如果没有值
					return null;
				}
				getdatatool.setURL(schoolURL);
				try {
					dt = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "get_MidTermTestList()...出错了。。。");
					e.printStackTrace();
				}
				return dt;
			}

			protected void onPostExecute(DataTable result) {
				
				listTest.clear();
				
				if (result != null) {
					System.out.println(result.toString());// 测试代码，之后需要删除
					int count = result.getRowCount();
					try {						
						for (int i = 0; i < count; i++) {
							String testName = result.getCell(i, "testName");
							String subjectID = result.getCell(i, "subjectID");
							String useID = result.getCell(i, "useID");
							
							TestInfo testInfo = new TestInfo(useID, testName);
							listTest.add(testInfo);
						}
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// 关闭刷新；
				refreshView.dismiss();
				
				initViewAfterGetData(); // 初始化界面
			};
		}.execute(subjectID, classID, termID);
	}

	/**
	 * 获取发生过的期末测试
	 * 
	 */
	public void get_FinalTermTestList(String subjectID, String classID,
			String termID) {
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, DataTable>() {
			// public String subjectID = null;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// 根据时间类型有不同的方法名和参数
				DataTable dt = new DataTable();
				// 方法名
				String methodName = "report_getAll_FinalTermTestList";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				// this.subjectID = params[0].toString();
				paramsMap.put("subjectID", params[0].toString());
				paramsMap.put("classID", params[1].toString());
				paramsMap.put("termID", params[2].toString());
				paramsMap.put("TokenID", tokenID);
				
				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 从本地获取学校URL
				SharedPreferences pre = getSharedPreferences("TTQAndroid",
						MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				if (schoolURL == null) {// 如果没有值
					return null;
				}
				getdatatool.setURL(schoolURL);
				try {
					dt = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "get_FinalTermTestList()...出错了。。。");
					e.printStackTrace();
				} 
				return dt;
			}

			protected void onPostExecute(DataTable result) {
				
				listTest.clear();
				
				if (result != null) {
					System.out.println(result.toString());// 测试代码，之后需要删除
					int count = result.getRowCount();
					try {
						for (int i = 0; i < count; i++) {
							String testName = result.getCell(i, "testName");
							String subjectID = result.getCell(i, "subjectID");
							String useID = result.getCell(i, "useID");
							
							TestInfo testInfo = new TestInfo(useID, testName);							
							listTest.add(testInfo);							
						}
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// 关闭刷新；
				refreshView.dismiss();
				// 初始化界面
				initViewAfterGetData();
			};
		}.execute(subjectID, classID, termID);
	}	
	
	private void initViewAfterGetData() {
		// 获取完数据后，初始化界面				
		if(listTest.size() > 0) { // 如果该科目存在测试
			timeValueStr = listTest.get(listTest.size() - 1).getUseID();
			testName = listTest.get(listTest.size() - 1).getTestName();
			get_studentTestOverView(studentID, timeValueStr);
		} else { // 如果该科目存在测试
			timeValueStr = "无";
			testName = "无";
			DataTable result = emptyResult();
			MyAdapter myAdapter = new MyAdapter(getApplicationContext(), result);
			lvTaskResult.setAdapter(myAdapter);
		}
		
		// 动态刷新测试选择列表
		test.clear();
		for(int i = listTest.size() - 1; i >= 0; i--) {
			test.add(listTest.get(i).getTestName());
		}
		testAdapter.notifyDataSetChanged();
	}
	
	// 当没有单元、期中和期末测试时，构造空数据
	private DataTable emptyResult() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("totalNum", "0");
		map.put("rightNum", "0");
		map.put("wrongNum", "0");
		map.put("unAnswerNum", "0");

		List<DataRow> listDataRow = new LinkedList<DataRow>();
		
		try {
			DataRow dr = new DataRow(map);
			listDataRow.add(dr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return new DataTable(listDataRow); 
	}
	
	/**
	 * 
	 * @author 進擊のotaku
	 *
	 * @describe 作业结果查看展示列表
	 *
	 */
	class MyAdapter extends BaseAdapter {

		private Context context;
		private DataTable dt;
		private LayoutInflater inflater;
		
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
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if(convertView == null) {
				convertView = inflater.inflate(R.layout.item_task_result, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.tvOverview = (TextView)convertView.findViewById(R.id.tvOverview);
				viewHolder.tvSubjectAndDate = (TextView)convertView.findViewById(R.id.tvSubjectAndDate);
				viewHolder.llArc1 = (LinearLayout)convertView.findViewById(R.id.llArc1);
				viewHolder.llArc2 = (LinearLayout)convertView.findViewById(R.id.llArc2);
				viewHolder.llArc3 = (LinearLayout)convertView.findViewById(R.id.llArc3);
				viewHolder.llArc4 = (LinearLayout)convertView.findViewById(R.id.llArc4);
				viewHolder.tvQuestionCount = (TextView)convertView.findViewById(R.id.tvQuestionCount);
				viewHolder.tvRightCount = (TextView)convertView.findViewById(R.id.tvRightCount);
				viewHolder.tvWrongCount = (TextView)convertView.findViewById(R.id.tvWrongCount);
				viewHolder.tvNoAnswerCount = (TextView)convertView.findViewById(R.id.tvNoAnswerCount);
				viewHolder.ivSeeDetail = (ImageView)convertView.findViewById(R.id.ivSeeDetail);
				
				int screenWidthPX = ScreenUtils.getScreenWidth(context);
				float screenWidthDP = DensityUtils.px2dp(context, screenWidthPX);
				float arcDiaDP = (screenWidthDP - 56) / 4;
				int arcDiaPX = DensityUtils.dp2px(getApplicationContext(), arcDiaDP);
				
				LayoutParams llArc1Params = (LayoutParams)viewHolder.llArc1.getLayoutParams();
				// llArc1Params.width = arcDiaPX;
				llArc1Params.height = arcDiaPX;
				viewHolder.llArc1.setLayoutParams(llArc1Params);
				
				LayoutParams llArc2Params = (LayoutParams)viewHolder.llArc2.getLayoutParams();
				// llArc2Params.width = arcDiaPX;
				llArc2Params.height = arcDiaPX;
				viewHolder.llArc2.setLayoutParams(llArc2Params);
				
				LayoutParams llArc3Params = (LayoutParams)viewHolder.llArc3.getLayoutParams();
				// llArc3Params.width = arcDiaPX;
				llArc3Params.height = arcDiaPX;
				viewHolder.llArc3.setLayoutParams(llArc3Params);
				
				LayoutParams llArc4Params = (LayoutParams)viewHolder.llArc4.getLayoutParams();
				// llArc4Params.width = arcDiaPX;
				llArc4Params.height = arcDiaPX;
				viewHolder.llArc4.setLayoutParams(llArc4Params);
				
				convertView.setTag(viewHolder);
			}else {
				viewHolder = (ViewHolder)convertView.getTag();
			}
			int totalNum = 0;
			int rightNum = 0;
			int wrongNum = 0;
			int unAnswerNum = 0;
			
			int totalPrecent = 0;
			int rightPrecent = 0;
			int wrongPrecent = 0;
			int unAnswerPrecent = 0;
			
			int useID = 0;
			
			try {
				totalNum = Integer.parseInt(dt.getCell(position, "totalNum"));
				rightNum = Integer.parseInt(dt.getCell(position, "rightNum"));
				wrongNum = Integer.parseInt(dt.getCell(position, "wrongNum"));
				unAnswerNum = Integer.parseInt(dt.getCell(position, "unAnswerNum"));
				
				if(totalNum > 0) {
					totalPrecent = 100;
					rightPrecent = Math.round((float)100 * rightNum / totalNum);
					wrongPrecent = Math.round((float)100 * wrongNum / totalNum);
					unAnswerPrecent = Math.round((float)100 * unAnswerNum / totalNum);
				}
				
				useID = Integer.parseInt(dt.getCell(position, "useID"));;
				
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (dataTableWrongException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// 往界面填充数据
			if(workTypeID.equals("4")|workTypeID.equals("5")|workTypeID.equals("6")) {
				viewHolder.tvSubjectAndDate.setText(subjectName + "  " + testName);
			} else {
				viewHolder.tvSubjectAndDate.setText(subjectName + "  " + timeValueStr);
			}
			viewHolder.llArc1.addView(new HomeArc(getApplicationContext(), 0xFF38B5E2, totalPrecent));
			viewHolder.llArc2.addView(new HomeArc(getApplicationContext(), 0xFF00C54F, rightPrecent));
			viewHolder.llArc3.addView(new HomeArc(getApplicationContext(), 0xFFFF4F4D, wrongPrecent));
			viewHolder.llArc4.addView(new HomeArc(getApplicationContext(), 0xFF656565, unAnswerPrecent));
			viewHolder.tvQuestionCount.setText(totalNum + "");
			viewHolder.tvRightCount.setText(rightNum + "");
			viewHolder.tvWrongCount.setText(wrongNum + "");
			viewHolder.tvNoAnswerCount.setText(unAnswerNum + "");
			
			// 作业查看详情，测试查看排名
			if(workTypeID.equals("4")|workTypeID.equals("5")|workTypeID.equals("6")) {
				viewHolder.ivSeeDetail.setImageResource(R.drawable.item_task_result_l);
				viewHolder.ivSeeDetail.setOnClickListener(new SeeDetailButtonOnClickListener());
			} else {
				viewHolder.ivSeeDetail.setImageResource(R.drawable.item_task_result_i);
				viewHolder.ivSeeDetail.setOnClickListener(new SeeDetailButtonOnClickListener(useID));
			}								
			
			return convertView;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 1;
		}
		
	}
	
	public static class ViewHolder {
		TextView tvOverview;
		TextView tvSubjectAndDate;
		LinearLayout llArc1;
		LinearLayout llArc2;
		LinearLayout llArc3;
		LinearLayout llArc4;
		TextView tvQuestionCount;
		TextView tvRightCount;
		TextView tvWrongCount;
		TextView tvNoAnswerCount;
		ImageView ivSeeDetail;
	}
	
	public class SeeDetailButtonOnClickListener implements OnClickListener {

		private int useID = 0;
		
		public SeeDetailButtonOnClickListener() {
			
		}
		
		public SeeDetailButtonOnClickListener(int useID) {
			this.useID = useID;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			// 没有联网，不可以进入到下一个界面；
			if(!GeneralTools.getInstance().isOpenNetWork1(TaskResultsActivity.this)){
				showToast("未连接到互联网，请检查网络配置!");
				return;
			}
			
			if(taskName.equals("家庭作业完成情况") || taskName.equals("课堂作业完成情况")){
				
				Intent intent = new Intent(TaskResultsActivity.this,
						TaskResultDetailForHTMLActivity.class);
				// 传递到下一个界面；
				//intent.putExtra("taskName", TaskResultsActivity.this.taskName);
				intent.putExtra("useID", useID);
				
				startActivity(intent);
				
			}else {	// 单元、期中、期末；
				Intent intent = new Intent(TaskResultsActivity.this, UnitTestResultDetailActivity.class);
				intent.putExtra("useID", timeValueStr);
				startActivity(intent);
			}
		}
		
	}
	
	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(TaskResultsActivity.this, toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}

	/**
	 * 获取学生的答题情况概览
	 * 
	 * @param timeType
	 *            ,时间条件类型 day week until_week
	 */
	public void get_studentDailyTaskOverView(String studentID,
			String subjectID, String WorkTypeID, String time, String timeType) {
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
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// 根据时间类型有不同的方法名和参数
				DataTable dt_student = new DataTable();
				// 方法名
				String methodName = null;
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();

				paramsMap.put("studentID", params[0].toString());
				paramsMap.put("subjectID", params[1].toString());
				paramsMap.put("workType", params[2].toString());
				paramsMap.put("TokenID", tokenID);
				
				String timeType = params[4].toString();
				if (timeType.equals("day")) {
					// 根据时间类型改变方法名和参数
					methodName = "APP_studentDaily_brief_byDay";
					paramsMap.put("date", params[3].toString());
					// paramsMap.put("date", "2015-01-27");
				} else if (timeType.equals("week")) {
					methodName = "APP_studentDaily_brief_byWeek";
					// 获取termID
					String termID = UserInfo.getInstance().termID;
					paramsMap.put("termID", termID);
					paramsMap.put("weekNum", params[3].toString());
					// paramsMap.put("weekNum", "2015-01-27");
				}

				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 从本地获取学校URL
				SharedPreferences pre = getSharedPreferences("TTQAndroid",
						MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				if (schoolURL == null) {// 如果没有值
					return null;
				}
				getdatatool.setURL(schoolURL);
				try {
					dt_student = getdatatool.getDataAsTable(methodName,
							paramsMap);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "get_studentDailyTaskOverView()...出错了。。。");
					e.printStackTrace();
				}
				return dt_student;
			}

			protected void onPostExecute(DataTable result) {				
				// 把获取到的数据写入界面
				if (result != null) {
					MyAdapter myAdapter = new MyAdapter(getApplicationContext(), result);
					lvTaskResult.setAdapter(myAdapter);
				}
				
				if(result != null && result.getRowCount() == 1) {
					rlNotice.setVisibility(View.VISIBLE);
					try {
						if(Integer.parseInt(result.getCell(0, "totalNum")) <= 0) {
							((TextView)rlNotice.getChildAt(1)).setText("今天没有作业！");
						} else {
							((TextView)rlNotice.getChildAt(1)).setText("今天只有一次作业！");
						}
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					rlNotice.setVisibility(View.GONE);
				}
				
				// 关闭刷新；
				refreshView.dismiss();
								
			};
		}.execute(studentID, subjectID, WorkTypeID, time, timeType);
	}


	/**获取学生的某次考试答题情况概览
	 * @param studentID
	 * @param useID
	 */
	public void get_studentTestOverView(String studentID, String useID) {
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
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				DataTable dt_student = new DataTable();
				// 方法名
				String methodName = null;
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("studentID", params[0].toString());
				paramsMap.put("useID", params[1].toString());
				paramsMap.put("TokenID", tokenID);
				
				// 根据时间类型改变方法名和参数
				methodName = "APP_getBriefData_byTest";
				
				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 从本地获取学校URL
				SharedPreferences pre = getSharedPreferences("TTQAndroid",
						MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				if (schoolURL == null) {// 如果没有值
					return null;
				}
				getdatatool.setURL(schoolURL);
				try {
					dt_student = getdatatool.getDataAsTable(methodName,
							paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "get_studentTestOverView()...出错了。。。");
					e.printStackTrace();
				}
				return dt_student;
			}

			protected void onPostExecute(DataTable result) {
				// 把获取到的数据写入界面
				if (result != null) {
					MyAdapter myAdapter = new MyAdapter(getApplicationContext(), result);
					lvTaskResult.setAdapter(myAdapter);
					
				}
				// 关闭刷新；
				refreshView.dismiss();							
			};
		}.execute(studentID, useID);
	}

}