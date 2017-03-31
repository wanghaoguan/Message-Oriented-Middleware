package com.ttqeducation.teacher.activitys.teach;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestListener;


import com.ttqeducation.beans.TestInfo;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.tools.DensityUtils;
import com.ttqeducation.teacher.tools.DesUtil;
import com.ttqeducation.teacher.tools.GeneralTools;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.TeacherInfo;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.tools.DateUtil;

import com.ttqeducation.teacher.tools.ScreenUtils;
import com.ttqeducation.teacher.tools.HomeArc;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
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

/**
 * 
 * 从教室端作业结果查看中选择一个项目，在这里展示各种作业完成情况。 * 
 * 需要做的事情： 
 * 1.接受传过来的参数；
 * 2.根据传过来的参数获取数据；
 * 3.展示数据 ；
 * 
 * @author 侯翔宇
 */
public class TaskResultActivity extends Activity {
	
	private static final String HXY="hxy";
	
	private String subjectID; // 科目ID,全科：0， 语文：1， 数学：2， 英语：3
	private String workTypeID;
	private String studentID;
	private String classID;
	private String termID;
	
	public String day_choosed; // 选择的天
	public String week_choosed;// 选择的周
	public DataTable origin_student_knowledgeInfo_datatable;// 原始学生知识点数据表

	
	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；
	//科目选择
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
	
	//界面部分
	private ListView lvTaskResult = null;
	private RelativeLayout rlNotice= null;
	
	//条件变量
	private String timeType="";  // 从前一个界面获取时间类型：day,week,unit,midterm,finalterm
	private String timeValueStr = ""; // 从前一个界面获取数据，如 具体的日期，周，单元测试useID，等等；
	private String taskName = ""; // 测试名称；如：家庭作业完成情况;
	private String subjectName = ""; // 科目名称；如：数学;
	
	private RefreshView refreshView = null;    //加载等待圆圈
	
	//用Map存放useID,testName
	private List<TestInfo> listTest = new ArrayList<TestInfo>(); 
	private List<String> test = new ArrayList<String>();
	private ArrayAdapter<String> testAdapter;
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
				get_ClassDailyBriefByDay(classID, timeValueStr,workTypeID,subjectID );
			}
		}	
	}
	
	//从上一个界面获取数据
	public void getDataFromIntent(){
		
		this.timeType=getIntent().getStringExtra("timeType");
		this.timeValueStr=getIntent().getStringExtra("valueStr");
		this.taskName = getIntent().getStringExtra("taskName");
		//this.subjectName = getIntent().getStringExtra("subjectName");
		
		
		Log.i(HXY, TeacherInfo.getInstance().getTermID());
	}
	
	//获得参数
	public void getData(){
		
		//subjectID = GeneralTools.getInstance().getSubjectIDByName(subjectName);
		workTypeID= GeneralTools.getInstance().getWorkTypeIDByName(taskName);
		classID=TeacherInfo.getInstance().getClassID();
		termID = TeacherInfo.getInstance().getTermID();
		
	}
	
	//界面初始化
	public void initView(){
		// 标题栏字体显示
		this.titleTextView=(TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText(taskName);
		
		//标题栏实例化
		this.titleBackLayout = (LinearLayout)(super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				TaskResultActivity.this.finish();
			}
		});
		
		//初始化左上角科目选择
		initChooseSubject();
		
		// 时间选择
		llDate = (LinearLayout)findViewById(R.id.llDate);
		llDate.setOnClickListener(new MyOnClickLinstener());
		tvDate = (TextView)findViewById(R.id.tvDate);
		ivDate = (ImageView)findViewById(R.id.ivDate);		
		
		// 历史回顾
		llChooseDate = (LinearLayout)findViewById(R.id.llChooseDate);
		llChooseTest = (LinearLayout)findViewById(R.id.llChooseTest);
		
		//家庭作业和课堂作业右上为日期选择，其他三种测试右上为历史回顾，用来选择一次确定的测试
		if(taskName != null && taskName.length() > 0 
				&& (taskName.equals("家庭作业完成情况") || taskName.equals("课堂作业完成情况"))){
			
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
		}else {//单元、期中期末
			
			lvChooseTest = (ListView)findViewById(R.id.lvChooseTest);
			initChooseTest();
			
			// 显示选择的科目和时间
			tvSubject.setText(subjectName);
			tvDate.setText("历史回顾"); 
			
			//获取测试情况
			if(taskName.equals("单元测试完成情况")){
				//获取单元测试信息
				get_unitTestList(subjectID, classID, termID);
			}else if (taskName.equals("期中测试完成情况")){
				//获取期中测试信息
				get_MidTermTestList(subjectID, classID, termID);
			}else if (taskName.equals("期末测试完成情况")){
				//获取期末测试信息
				get_FinalTermTestList(subjectID, classID, termID);
			}
		}
		
		lvTaskResult = (ListView)findViewById(R.id.lvTaskResult);
		rlNotice = (RelativeLayout)findViewById(R.id.rlNotice); // 如果当天只有只有一次作业，为了不让界面显得太空白，添加图片提示
	}
	
	
	

	//科目选择
	public void initChooseSubject(){
		
		llSubject = (LinearLayout) findViewById(R.id.llSubject);
		llSubject.setOnClickListener(new MyOnClickLinstener());
		tvSubject = (TextView)findViewById(R.id.tvSubject);
		ivSubject = (ImageView)findViewById(R.id.ivSubject);
		
		llChooseSubject = (LinearLayout)findViewById(R.id.llChooseSubject);
		lvChooseSubject = (ListView)findViewById(R.id.lvChooseSubject);
		
		//subjects = new String[]{"语文", "数学", "英语"};
		subjects = TeacherInfo.getInstance().getSubjects();
		subjectName = subjects[0];
		subjectID = GeneralTools.getInstance().getSubjectIDByName(subjectName);
		if(subjects != null && subjects.length > 0){
			
			ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_subject,subjects);
			lvChooseSubject.setAdapter(subjectAdapter);
			lvChooseSubject.setOnItemClickListener(new OnItemClickListener() {
				//科目选择下拉框中具体科目点击事件
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					String subject = subjects[arg2];
					subjectID = GeneralTools.getInstance().getSubjectIDByName(subject);	
					subjectName=subject;
					
					tvSubject.setText(subjectName);
					llChooseSubject.setVisibility(View.GONE);
					
					if(workTypeID.equals("4")|workTypeID.equals("5")|workTypeID.equals("6")){
						//获取测试情况
						if(taskName.equals("单元测试完成情况")){
							//刷新该科目的单元测试列表
							get_unitTestList(subjectID, classID, termID);
						}else if(taskName.equals("期中测试完成情况")){
							get_MidTermTestList(subjectID, classID, termID);
						}else if(taskName.equals("期末测试完成情况")){
							get_FinalTermTestList(subjectID, classID, termID);
						}
					}else{
						Log.i("hxy", subjectID+";"+timeValueStr+";"+workTypeID);
						get_ClassDailyBriefByDay(classID, timeValueStr, workTypeID,subjectID);
					}
				}
				
			});
		}
		
		
		
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
	
	class MyOnClickLinstener implements OnClickListener{

		public MyOnClickLinstener() {
					
				}
		
		@Override
		public void onClick(View v) {			
			switch(v.getId()){
			case R.id.llSubject:
				
				// 当点击科目选择时，如果时间选择在前台则隐藏
				if(llChooseDate.getVisibility()==View.VISIBLE){
					llChooseDate.setVisibility(View.GONE);
				}
				// 当点击科目选择时，如果测试选择在前台则隐藏
				if(llChooseTest.getVisibility()==View.VISIBLE){
					llChooseTest.setVisibility(View.GONE);
				}
				
				if(llChooseSubject.getVisibility() == View.VISIBLE) {
					llChooseSubject.setVisibility(View.GONE);
				} else {
					llChooseSubject.setVisibility(View.VISIBLE);
				}
				
				break;
			case R.id.llDate:
				if(workTypeID.equals("1")|workTypeID.equals("2")) {
					if(llChooseDate.getVisibility() == View.VISIBLE) {
						llChooseDate.setVisibility(View.GONE);
					} else {
						llChooseDate.setVisibility(View.VISIBLE);
					}
				} else {
					//点击历史回顾时如果科目正在显示则隐藏
					if(llChooseSubject.getVisibility()==View.VISIBLE){
						llChooseSubject.setVisibility(View.GONE);
					}
					
					if(llChooseTest.getVisibility() == View.VISIBLE) {
						llChooseTest.setVisibility(View.GONE);
					} else {
						llChooseTest.setVisibility(View.VISIBLE);
					}
				}
				
				break;
			default:
				break;
				
			}
		}
		
	}
	
	public void initChooseTest(){
		
		//最近的显示在最上面
		for(int i=listTest.size()-1;i>=0;i--){
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
				//此处存放UseID
				timeValueStr = listTest.get(listTest.size() - arg2 - 1).getUseID();
				//Log.i("hxy", testName+";"+timeValueStr);
				llChooseTest.setVisibility(View.GONE);
				get_ClassDalilyBriefByUseTest(timeValueStr);
			}
		});
		
	}
	
	/**
	 * 获取发生过的单元测试
	 * @param subjectID  科目
	 * @param classID    班级ID
	 * @param termID     学期
	 */
	private void get_unitTestList(String subjectID, String classID,
			String termID) {
		
		listTest.clear();//切换科目时清空列表
		test.clear();
		//旋转等待dialog
		this.refreshView= new RefreshView(this, R.style.full_screen_dialog);
		//用异步调用来访问网络
		new AsyncTask<Object, Object, DataTable>(){			
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				
				DesUtil.addTokenIDToSchoolWS();  //添加tokenID
				String tokenID="";
				try{
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1){
					e1.printStackTrace();
				}
				
				//根据时间类型有不同而方法名和参数
				DataTable dt = new DataTable();
				//方法名
				String methodName ="report_getAll_UnitTestList";
				//存放参数的Map
				Map<String,String> paramsMap = new HashMap<String,String>();
				paramsMap.put("subjectID", params[0].toString());
				paramsMap.put("classID", params[1].toString());
				paramsMap.put("termID", params[2].toString());
				paramsMap.put("TokenID", tokenID);
				Log.i("hxy",  params[0].toString()+";"+params[1].toString()+";"+params[2].toString());
				//开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				//从本地富哦去学校URL
				SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				if(schoolURL==null){
					return null;	
				}
				getdatatool.setURL(schoolURL);
				try{
					dt= getdatatool.getDataAsTable(methodName,paramsMap);
				}catch (Exception e){
					Log.i("hxy", "get_unitTestList()...出错了。。。");
					e.printStackTrace();
				}
				return dt;				
			}			

			@Override
			protected void onPostExecute(DataTable result) {
				listTest.clear();
				
				if(result!=null){
					int count = result.getRowCount();
					try{
						for(int i=0;i<count;i++){
							String unitTestName = result.getCell(i, "unitInfo");
							String subjectID = result.getCell(i, "subjectID");
							String useID = result.getCell(i, "useID");
							
							TestInfo testInfo = new TestInfo(useID, unitTestName);
							listTest.add(testInfo);
						}
					}catch(dataTableWrongException e){
						
						e.printStackTrace();
					}
				}
				//关闭刷新
				refreshView.dismiss();
				initViewAfterGetData(); // 初始化界面		
			}			
			
		}.execute(subjectID,classID,termID);
	}
	
	/**
	 * 获取期中测试列表
	 * @param subjectID
	 * @param classID
	 * @param termID
	 */
	private void get_MidTermTestList(String subjectID, String classID,
			String termID) {
		// TODO Auto-generated method stub
		
		listTest.clear();//切换科目时清空列表
		test.clear();
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
	 * 获取期末考试列表
	 * @param subjectID
	 * @param classID
	 * @param termID
	 */
	private void get_FinalTermTestList(String subjectID, String classID,
			String termID) {
		
		listTest.clear();//切换科目时清空列表
		test.clear();
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
	
	private void initViewAfterGetData(){
		// 获取完数据后，初始化界面				
		if(listTest.size() > 0) { // 如果该科目存在测试
			timeValueStr = listTest.get(listTest.size() - 1).getUseID();
			testName = listTest.get(listTest.size() - 1).getTestName();
			Log.i("hxy", timeValueStr);
			Log.i("hxy", testName);
			get_ClassDalilyBriefByUseTest(timeValueStr);
			initChooseTest();
		}
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
			Log.i("hxy", subjectID+";"+timeValueStr+";"+workTypeID);
			get_ClassDailyBriefByDay(classID, timeValueStr, workTypeID,subjectID);
			llChooseDate.setVisibility(View.GONE);
		}
		Log.i("hxy",timeValueStr);
	}
	
	/**
	 * 查看家庭作业、课堂作业每次的题数、班级正确率，按UseID 分组
	 * @param classID
	 * @param answerDate
	 * @param testType
	 * @param subjectID
	 */
	public void get_ClassDailyBriefByDay(String classID ,String answerDate,String testType,String subjectID ){
		
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		// 用异步任务来访问访问网络
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
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				DataTable dt_result= new DataTable();
				//方法名
				String methodName = null;
				//存放参数
				Map<String,String> paramsMap = new HashMap<String, String>();
				
				paramsMap.put("classID",params[0].toString());
				paramsMap.put("answerDate",params[1].toString());
				paramsMap.put("testType",params[2].toString());
				paramsMap.put("subjectID", params[3].toString());
				paramsMap.put("TokenID",tokenID);
				
				methodName="APP_getClassDaily_brief_byDay";
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
					dt_result = getdatatool.getDataAsTable(methodName,
							paramsMap);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "APP_getClassDailyBriefByDay...出错了。。。");
					e.printStackTrace();
				}
				
				if(dt_result==null){
					Log.i("hxy","没有数据");
				}else{
					Log.i("hxy",String.valueOf( dt_result.Rows.size()));
				}
					
				return dt_result;		
				
				
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				//把获取到的数据写入界面
				if(result !=null){
					MyAdapter myAdapter = new MyAdapter(getApplicationContext(), result);
					lvTaskResult.setAdapter(myAdapter);
				}
				if(result !=null && result.getRowCount()==1){
					rlNotice.setVisibility(View.VISIBLE);
					try{
						if(Integer.parseInt(result.getCell(0, "questionCount"))<=0){
							((TextView)rlNotice.getChildAt(1)).setText("今天没有作业！");
						}else{
							((TextView)rlNotice.getChildAt(1)).setText("今天只有一次作业！");
						}
					}catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					rlNotice.setVisibility(View.GONE);
				}
				// 关闭刷新；
				refreshView.dismiss();
			}
			
			
			
		}.execute(classID,answerDate,testType,subjectID);
	}
	
	/**
	 * 作业结果查看中，根据试卷选择查看单元、期中期末的概述信息
	 * @param useID
	 */
	public void get_ClassDalilyBriefByUseTest(String useID){
		
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		//异步任务来访问网络
		new AsyncTask<Object,Object,DataTable>(){			
			
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
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// 根据时间类型有不同的方法名和参数
				DataTable dt_result = new DataTable();
				// 方法名
				String methodName = null;
				//存放MAP
				Map<String,String> paramsMap = new HashMap<String, String>();
				paramsMap.put("useID",params[0].toString());
				paramsMap.put("TokenID",tokenID);
				
				methodName ="APP_getClassDaily_brief_byTest";
				GetDataByWS getdatatool= GetDataByWS.getInstance();
				SharedPreferences pre=getSharedPreferences("TTQAndroid", MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				if(schoolURL==null){
					return null;
				}
				getdatatool.setURL(schoolURL);
				try{
					dt_result=getdatatool.getDataAsTable(methodName,paramsMap);
				}catch(Exception e){
					Log.i("error", "APP_getClassDaily_brief_byTest()...出错了。。。");
					e.printStackTrace();
				}
				return dt_result;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				//写入界面
				if(result !=null){
					MyAdapter myAdapter = new MyAdapter(getApplicationContext(),result);
					lvTaskResult.setAdapter(myAdapter);
				}
				refreshView.dismiss();
				super.onPostExecute(result);
			}
			 
			
		}.execute(useID);
		
		
	}
	
	
	/**
	 * 作业结果查看展示列表
	 * @author 侯翔宇
	 *
	 */
	class MyAdapter extends BaseAdapter{

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
			if(convertView==null){
				convertView=inflater.inflate(R.layout.item_task_result, parent,false);
				viewHolder = new ViewHolder();
				
				viewHolder.tvOverview = (TextView)convertView.findViewById(R.id.tvOverview);
				viewHolder.tvSubjectAndDate = (TextView)convertView.findViewById(R.id.tvSubjectAndDate);
				viewHolder.llArc1 = (LinearLayout)convertView.findViewById(R.id.llArc1);
				viewHolder.llArc2 = (LinearLayout)convertView.findViewById(R.id.llArc2);				
				viewHolder.tvQuestionCount = (TextView)convertView.findViewById(R.id.tvQuestionCount);				
				viewHolder.ivSeeDetail = (ImageView)convertView.findViewById(R.id.ivSeeDetail);
				
				int screenWidthPX = ScreenUtils.getScreenWidth(context);
				float screenWidthDP = DensityUtils.px2dp(context, screenWidthPX);
				//float arcDiaDP = (screenWidthDP - 56) / 4;
				float arcDiaDP = (screenWidthDP - 48) / 3;
				int arcDiaPX = DensityUtils.dp2px(getApplicationContext(), arcDiaDP);
				
				LayoutParams llArc1Params = (LayoutParams)viewHolder.llArc1.getLayoutParams();
				// llArc1Params.width = arcDiaPX;
				llArc1Params.height = arcDiaPX;
				viewHolder.llArc1.setLayoutParams(llArc1Params);
				
				LayoutParams llArc2Params = (LayoutParams)viewHolder.llArc2.getLayoutParams();
				// llArc2Params.width = arcDiaPX;
				llArc2Params.height = arcDiaPX;
				viewHolder.llArc2.setLayoutParams(llArc2Params);
				
				convertView.setTag(viewHolder);
			}else{
				viewHolder= (ViewHolder)convertView.getTag();
			}
			
			int totalPrecent = 0;			
			int totalNum=0;
			int rightPrecent = 0;
			int useID =0;
			String testNameDaily=null;
			
			try{
				totalNum=Integer.parseInt(dt.getCell(position, "questionCount"));
				rightPrecent=(int)(Float.parseFloat(dt.getCell(position, "classRightPercent"))*100) ;
				
				
				if(totalNum>0){
					totalPrecent=100;
				}
				useID = Integer.parseInt(dt.getCell(position, "useID"));
				
			}catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (dataTableWrongException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// 往界面填充数据
			if(workTypeID.equals("4")|workTypeID.equals("5")|workTypeID.equals("6")) {
				viewHolder.tvSubjectAndDate.setText(subjectName + "  " + testName);
			} else {//家庭和课堂作业
				try {
					testNameDaily= dt.getCell(position, "testName");
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				viewHolder.tvSubjectAndDate.setText(subjectName + "  " + testNameDaily);
			}
			
			viewHolder.llArc1.addView(new HomeArc(getApplicationContext(), 0xFF38B5E2, totalPrecent));
			viewHolder.llArc2.addView(new HomeArc(getApplicationContext(), 0xFF00C54F, rightPrecent));
			viewHolder.tvQuestionCount.setText(totalNum + "");
			
			// 作业查看详情，测试查看排名,4期暂时改为同样查看正确率
			if(workTypeID.equals("4")|workTypeID.equals("5")|workTypeID.equals("6")) {
				viewHolder.ivSeeDetail.setImageResource(R.drawable.item_task_result_i);
				viewHolder.ivSeeDetail.setOnClickListener(new SeeDetailButtonOnClickListener(Integer.parseInt(timeValueStr) ));
			} else {
				viewHolder.ivSeeDetail.setImageResource(R.drawable.item_task_result_i);
				viewHolder.ivSeeDetail.setOnClickListener(new SeeDetailButtonOnClickListener(useID));
			}								
			
			return convertView;
		}
		
	}
	
	public static class ViewHolder {
		TextView tvOverview;
		TextView tvSubjectAndDate;
		LinearLayout llArc1;
		LinearLayout llArc2;
		
		TextView tvQuestionCount;
		
		ImageView ivSeeDetail;
	}
	
	public class SeeDetailButtonOnClickListener implements OnClickListener{

		private int useID=0;
		
		public SeeDetailButtonOnClickListener(int useID){
			this.useID=useID;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(!GeneralTools.getInstance().isOpenNetWork1(TaskResultActivity.this)){
				showToast("未连接到互联网，请检查网络配置!");
				return;
			}
			Intent intent = new Intent(TaskResultActivity.this, TaskResultDetailForHTMLActivity.class);
			intent.putExtra("useID", this.useID);			
			intent.putExtra("taskName",TaskResultActivity.this.taskName);
			startActivity(intent);
		}
		
	}
	
	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(TaskResultActivity.this, toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}
}
