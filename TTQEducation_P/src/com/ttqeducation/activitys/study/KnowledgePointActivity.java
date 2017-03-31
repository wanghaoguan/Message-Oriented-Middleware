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
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
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
import android.widget.TextView;
import android.widget.Toast;

import com.ttqeducation.R;
import com.ttqeducation.beans.DataRow;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DateUtil;
import com.ttqeducation.tools.DensityUtils;
import com.ttqeducation.tools.DesUtil;
import com.ttqeducation.tools.GeneralTools;
import com.ttqeducation.tools.KnowledgePointArc;
import com.ttqeducation.tools.KnowledgePointCountArc;
import com.ttqeducation.tools.ScreenUtils;

/**
 * 知识点查看概况界面
 * 
 * @author 吕杰、王勤为
 *
 */
public class KnowledgePointActivity extends Activity {

	// 标题栏部分
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null; // 标题栏文字；
		
	// 科目
	private String[] subjects;
	private String subjectName = "语文"; // 科目名称
	private String subjectID = "";
	// 科目选择按钮
	private LinearLayout llSubject;
	private TextView tvSubject;
	private ImageView ivSubject;
	// 科目选择的ListView
	private LinearLayout llChooseSubject;
	private ListView lvChooseSubject;
		
	// 时间节点
	private String[] timeTypes;
	private String timeType = ""; // 时间类型：天、周、月、期中和期末
	// 时间节点选择按钮
	private LinearLayout llTimeType;
	private TextView tvTimeType;
	private ImageView ivTimeType;
	// 时间节点选择ListView
	private LinearLayout llChooseTimeType;
	private ListView lvChooseTimeType;
	// 期中期末截至时间
	private String deadline = "";
	
	// 具体时间
	private String date = ""; // 具体的日期
	private String weekNum = ""; // 周的ID
	private List<String> listWeek = new ArrayList<String>();
	private List<String> listWeekDisplay = new ArrayList<String>();
	private String month = "";
	private List<String> listMonth = new ArrayList<String>();
	private List<String> listMonthDisplay = new ArrayList<String>();
	private int termType = 0;
	private int IsSetted = 0;
	// 时间选择按钮
	private LinearLayout llDate;
	private TextView tvDate;
	private ImageView ivDate;
	// 日历界面
	private LinearLayout llChooseDate;
	private TextView yearAndMonth;
	private LinearLayout[] daylayoutArray;
	private Date currentDate;
	private int weeknum;
	// 周、月选择ListView
	private LinearLayout llChooseWeekOrMonth;
	private ListView lvChooseWeekOrMonth;
	private ArrayAdapter<String> weekAdapter;
	private ArrayAdapter<String> monthAdapter;
	
	// 曲线图
	private LinearLayout llGraph;
	
	// 知识点明细
	private LinearLayout llKnowledgePointDetail;
		
	// 概述界面
	private ListView lvKnowledgePoint;
	private DataTable dtKnowledgePoint;
	private MyAdapter myAdapter;
	
	private String studentID = "";
	private String classID = "";
	private String termID = "";
	private int grade = 0;
	
	private RefreshView refreshView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_knowledge_point);
		
		getDataFromIntent();
		
		initView();

		getData();
		
		
	}

	// 获取从上一个界面传来的数据
	private void getDataFromIntent() {
		
	}

	public void initView() {
		// 标题栏部分实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("知识点查看");
		
		// 返回
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				KnowledgePointActivity.this.finish();
			}
		});
		
		// 科目选择按钮
		llSubject = (LinearLayout)findViewById(R.id.llSubject);
		tvSubject = (TextView)findViewById(R.id.tvSubject);
		ivSubject = (ImageView)findViewById(R.id.ivSubject);
		// 科目选择的ListView
		llChooseSubject = (LinearLayout)findViewById(R.id.llChooseSubject);
		lvChooseSubject = (ListView)findViewById(R.id.lvChooseSubject);
		// 科目选择列表
		llSubject.setOnClickListener(new MyOnClickLinstener());
		initChooseSubject();
		
		// 时间节点选择按钮
		llTimeType = (LinearLayout)findViewById(R.id.llTimeType);
		tvTimeType = (TextView)findViewById(R.id.tvTimeType);
		ivTimeType = (ImageView)findViewById(R.id.ivTimeType);
		// 时间节点选择ListView
		llChooseTimeType = (LinearLayout)findViewById(R.id.llChooseTimeType);
		lvChooseTimeType = (ListView)findViewById(R.id.lvChooseTimeType);
		// 时间节点选择列表
		llTimeType.setOnClickListener(new MyOnClickLinstener());
		initChooseTimeType();
		
		// 时间选择按钮
		llDate = (LinearLayout)findViewById(R.id.llDate);
		tvDate = (TextView)findViewById(R.id.tvDate);
		ivDate = (ImageView)findViewById(R.id.ivDate);
		// 日历界面
		llChooseDate = (LinearLayout)findViewById(R.id.llChooseDate);
		// 周、月选择ListView
		llChooseWeekOrMonth = (LinearLayout)findViewById(R.id.llChooseWeekOrMonth);
		lvChooseWeekOrMonth = (ListView)findViewById(R.id.lvChooseWeekOrMonth);
		// 日历界面
		llDate.setOnClickListener(new MyOnClickLinstener());
		initCalendarLayout();
		initCalendar(new Date());		
		
		// 曲线图
		llGraph = (LinearLayout)findViewById(R.id.llGraph);
		llGraph.setOnClickListener(new MyOnClickLinstener());
		setLLGraphClickable(false);
		
		// 知识点明细
		llKnowledgePointDetail = (LinearLayout)findViewById(R.id.llKnowledgePointDetail);
		llKnowledgePointDetail.setOnClickListener(new MyOnClickLinstener());
		
		// 概述界面 
		lvKnowledgePoint = (ListView)findViewById(R.id.lvKnowledgePoint);
		
		date = DateUtil.convertDateToString("yyyy-MM-dd", new Date());
		tvDate.setText(date);
		getDateFromService();				
	}
	
	/**
	 * 
	 * @author 進擊のotaku
	 *
	 * @describe 处理点击事件
	 *
	 */
	class MyOnClickLinstener implements OnClickListener {

		public MyOnClickLinstener() {
			
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()) {
			case R.id.llSubject:
				
				llChooseTimeType.setVisibility(View.GONE); // 隐藏时间节点选择
				llChooseDate.setVisibility(View.GONE); // 隐藏日历界面
				llChooseWeekOrMonth.setVisibility(View.GONE); // 隐藏周和月选择界面							
				
				if(llChooseSubject.getVisibility() == View.VISIBLE) {
					llChooseSubject.setVisibility(View.GONE);
				} else {
					llChooseSubject.setVisibility(View.VISIBLE);
				}
				
				break;
			
			// 时间节点选择
			case R.id.llTimeType :
				llChooseSubject.setVisibility(View.GONE); // 隐藏科目选择
				llChooseDate.setVisibility(View.GONE); // 隐藏日历界面
				llChooseWeekOrMonth.setVisibility(View.GONE); // 隐藏周和月选择界面									
				
				if(llChooseTimeType.getVisibility() == View.VISIBLE) {
					llChooseTimeType.setVisibility(View.GONE);
				} else {
					llChooseTimeType.setVisibility(View.VISIBLE);
				}
				
				break;
				
			case R.id.llDate :
				llChooseSubject.setVisibility(View.GONE); // 隐藏科目选择
				llChooseTimeType.setVisibility(View.GONE); // 隐藏时间节点选择				
																				
				// 时间类型为“天”，通过日历界面选择具体某一天
				if(timeType != null && timeType.equals("天")) {
					if(llChooseDate.getVisibility() == View.VISIBLE) {
						llChooseDate.setVisibility(View.GONE);
					} else {
						llChooseDate.setVisibility(View.VISIBLE);
					}					
				} 
				// 时间类型为“周”或者“月”，通过列表选择某一周或者某一月
				else if(timeType != null && (timeType.equals("周") || timeType.equals("月"))){
					if(llChooseWeekOrMonth.getVisibility() == View.VISIBLE) {
						llChooseWeekOrMonth.setVisibility(View.GONE);
					} else {
						llChooseWeekOrMonth.setVisibility(View.VISIBLE);
					}
				}
				
				break;
			
			// 查看曲线图
			case R.id.llGraph:
				Intent intentGraph = new Intent();
				intentGraph.setClass(KnowledgePointActivity.this, KnowledgePointGraphActivity.class);
				
				if(timeType != null && timeType.length() > 0) {
					intentGraph.putExtra("timeType", timeType);
					
					// 周曲线比对图需要传入的参数
					if(timeType.equals("周")) {
						intentGraph.putExtra("studentID", studentID);
						intentGraph.putExtra("subjectID", subjectID);
						intentGraph.putExtra("termID", termID);
						intentGraph.putExtra("classID", classID);
					} 
					// 月曲线比对图需要传入的参数
					else if(timeType.equals("月")) {
						intentGraph.putExtra("studentID", studentID);
						intentGraph.putExtra("subjectID", subjectID);
						intentGraph.putExtra("termID", termID);
						intentGraph.putExtra("classID", classID);
						intentGraph.putExtra("grade", grade);
					} 
					// 期中期末曲线比对图需要传入的参数
					else if(timeType.equals("期中") || timeType.equals("期末")) {
						intentGraph.putExtra("studentID", studentID);
						intentGraph.putExtra("classID", classID);
						intentGraph.putExtra("subjectID", subjectID);					
						intentGraph.putExtra("termID", termID);
						intentGraph.putExtra("termType", termType);
						intentGraph.putExtra("grade", grade);
					}
				}
										
				startActivity(intentGraph);
				break;
			
			// 查看知识点明细
			case R.id.llKnowledgePointDetail:
				Intent intent = new Intent();
				intent.setClass(KnowledgePointActivity.this, KnowledgePointDetailActivity.class);
				
				if(timeType != null && timeType.length() > 0) {
					intent.putExtra("timeType", timeType);
				}
				
				if(timeType != null && timeType.length() > 0 && timeType.equals("天")) {
					intent.putExtra("studentID", studentID);
					intent.putExtra("subjectID", subjectID);
					intent.putExtra("date", date);
					intent.putExtra("classID", classID);
				} else if(timeType != null && timeType.length() > 0 && timeType.equals("周")) {
					intent.putExtra("studentID", studentID);
					intent.putExtra("subjectID", subjectID);
					intent.putExtra("weekNum", weekNum);
					intent.putExtra("classID", classID);
				} else if(timeType != null && timeType.length() > 0 && timeType.equals("月")) {
					intent.putExtra("studentID", studentID);
					intent.putExtra("subjectID", subjectID);
					intent.putExtra("termID", termID);
					intent.putExtra("month", month);
					intent.putExtra("classID", classID);
				} else if(timeType != null && timeType.length() > 0 && (timeType.equals("期中") || timeType.equals("期末"))) {
					intent.putExtra("studentID", studentID);
					intent.putExtra("subjectID", subjectID);
					intent.putExtra("termID", termID);
					intent.putExtra("termType", termType);
					intent.putExtra("classID", classID);
				}
				
				startActivity(intent);
				break;
				
			default:
				// 其它情况
				break;
			}
		}		
	}
	
	// 选择科目界面
	private void initChooseSubject() {
		subjects = new String[]{"语文", "数学", "英语"};
		subjectName = subjects[0];
		getSubjectID();
		tvSubject.setText(subjectName);	
		
		ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(getApplicationContext(), 
				R.layout.item_subject, subjects);
		lvChooseSubject.setAdapter(subjectAdapter);
		lvChooseSubject.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				subjectName = subjects[arg2];
				getSubjectID();			
				tvSubject.setText(subjectName);				
				llChooseSubject.setVisibility(View.GONE);
				getDateFromService();
			}
		});
	}
	
	// 选择时间节点
	private void initChooseTimeType() {
		timeTypes = new String[]{"天", "周", "月", "期中", "期末"}; // 时间节点类型
		timeType = "天";	// 默认选择天			
		tvTimeType.setText(timeType);
		
		ArrayAdapter<String> timeTypeAdapter = new ArrayAdapter<String>(getApplicationContext(), 
				R.layout.item_subject, timeTypes);
		lvChooseTimeType.setAdapter(timeTypeAdapter);
		lvChooseTimeType.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				timeType = timeTypes[arg2];				
				tvTimeType.setText(timeType);				
				llChooseTimeType.setVisibility(View.GONE);
				if(timeType != null && timeType.length() > 0 && timeType.equals("天")) {				
					tvDate.setText(date);
					setLLGraphClickable(false);	
					getDateFromService();
				} else if(timeType != null && timeType.length() > 0 && timeType.equals("周")) {
					setLLGraphClickable(true);
					Teach_GetWeek(DateUtil.convertDateToString("yyyy-MM-dd", new Date()));
				} else if(timeType != null && timeType.length() > 0 && timeType.equals("月")) {					
					setLLGraphClickable(true);
					teach_getPassedMonthByTerm(termID);			
				} else if(timeType != null && timeType.length() > 0 && (timeType.equals("期中") || timeType.equals("期末"))) {
					setLLGraphClickable(true);
					if(timeType.equals("期中")) {
						termType = 1;
					} else {
						termType = 2;
					}
					teach_midAndFinalTermDate_select(termID, grade, termType);
				}							
			}
		});
	}
	
	/**
	 * 设置曲线图按钮是否可以点击
	 * 
	 * @param boo
	 */
	private void setLLGraphClickable(Boolean boo) {
		ImageView ivGraph = (ImageView)findViewById(R.id.ivGraph);
		
		if(boo == true) {
			llGraph.setClickable(true);
			ivGraph.clearColorFilter();
			llGraph.setAlpha(1.0f);
		} else if (boo == false) {
			llGraph.setClickable(false);
			ivGraph.setColorFilter(Color.GRAY,Mode.MULTIPLY);
			llGraph.setAlpha(0.5f);
		}
	}
	
	private void initCalendarLayout() {
		yearAndMonth = (TextView)findViewById(R.id.yearAndMonth); // 日历界面上的年月显示
		
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
	}
	
	// 日历界面
	public void initCalendar(Date date) {
		
		for(int i = 0; i < 42; i++) {
			daylayoutArray[i].setBackgroundResource(0);
			((TextView)daylayoutArray[i].getChildAt(0)).setText("");
			((ImageView)daylayoutArray[i].getChildAt(1)).setImageResource(0);	
		}
		
		currentDate = date;
	    String selectdate = DateUtil.convertDateToString("yyyy年MM月", date);
	    yearAndMonth.setText(selectdate); // 显示当前的年月
	    
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
	
	// 下一个月	 
	public void nextmonth(View v) {
		currentDate.setDate(1);
		currentDate.setMonth(currentDate.getMonth() + 1);
		Date nowDate = new Date();
		if(currentDate.getYear() == nowDate.getYear() && currentDate.getMonth() == nowDate.getMonth()) {
			currentDate = new Date();
		}
		initCalendar(currentDate);
	}

	// 上一个月
	public void upmonth(View v) {
		currentDate.setDate(1);
		currentDate.setMonth(currentDate.getMonth() - 1);
		Date nowDate = new Date();
		if(currentDate.getYear() == nowDate.getYear() && currentDate.getMonth() == nowDate.getMonth()) {
			currentDate = new Date();
		}
		initCalendar(currentDate);
	}
	
	// 查看某天的知识点	 
	public void task_info(View v) {
		LinearLayout line = (LinearLayout)v;
		
		if(!((TextView)line.getChildAt(0)).getText().equals("")) {
			TextView tvDay = (TextView)line.getChildAt(0);
			int day = Integer.parseInt(tvDay.getText().toString());
			
			Date chosenDate = currentDate;			
			chosenDate.setDate(day);
					
			date = DateUtil.convertDateToString("yyyy-MM-dd", chosenDate);
			tvDate.setText(date);
			llChooseDate.setVisibility(View.GONE);
									
			// 查询某天的知识点掌握情况
			getDateFromService();
		}
	}
	
	private void getData() {
		getSubjectID();
		studentID = UserInfo.getInstance().studentID;
		classID = UserInfo.getInstance().classID;
		termID = UserInfo.getInstance().termID;
		grade = Integer.parseInt(UserInfo.getInstance().grade);
	}
	
	// 根据科目名字获取科目ID
	private void getSubjectID() {
		subjectID = GeneralTools.getInstance().getSubjectIDByName(subjectName);
	}
	
	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(KnowledgePointActivity.this, toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}

	// 调用方法访问WebService获取数据
	private void getDateFromService() {
		if(timeType != null && timeType.length() > 0 && timeType.equals("天")) {
			// 这是天
			getStudentKnowledgeByDay(studentID, subjectID, date, classID);
		} else if(timeType != null && timeType.length() > 0 && timeType.equals("周")) {
			// 这是周
			APP_getStudentKnowledgeByWeek(studentID, subjectID, weekNum);
		} else if(timeType != null && timeType.length() > 0 && timeType.equals("月")) {
			// 这是月
			APP_getStudentKnowledgeByMonth(studentID, subjectID, Integer.parseInt(month),termID, 
					classID, grade);
		} else if(timeType != null && timeType.length() > 0 
				&& (timeType.equals("期中") || timeType.equals("期末"))) {
			// 这是期中、期末
			teach_chartCurve_studentByTerm(studentID, classID, subjectID, termID, termType, grade);
			// teach_chartCurve_studentByTerm("410006001S20150001","C2014501","2","201420152",1,5);
			
		}
	}
	
	/**
	 * 通过当前时间获取当前属于第几周
	 * 
	 * @param createtime_old
	 */
	public void Teach_GetWeek(String createtime_old) {
		
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
				String methodName = "Teach_GetWeek";
				
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				paramsMap.put("time", params[0].toString());	
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
					Log.i("error", "Teach_GetWeek()...出错了。。。");
					e.printStackTrace();
				}								
				
				return dt_student;
			}
	
			protected void onPostExecute(DataTable result) {
				
				// 把获取到的数据写入界面
				if(result != null) {
					int currentWeekNum = 0; // 通过当前时间获取的第几周信息
					try {
						currentWeekNum = Integer.parseInt(result.getCell(0, "weekNum"));
					} catch (NumberFormatException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (dataTableWrongException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
					for(int i = currentWeekNum; i >= 1; i--) {						
						listWeek.add(i + "");
						listWeekDisplay.add("第" + i + "周");
					}
				}
				
				weekAdapter = new ArrayAdapter<String>(getApplicationContext(), 
						R.layout.item_test, listWeekDisplay);
				lvChooseWeekOrMonth.setAdapter(weekAdapter);
				lvChooseWeekOrMonth.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						// TODO Auto-generated method stub
						weekNum = listWeek.get(arg2);
						tvDate.setText(listWeekDisplay.get(arg2));
						llChooseWeekOrMonth.setVisibility(View.GONE);
						getDateFromService();
					}
				});
				
				// 关闭刷新；
				refreshView.dismiss();	
				
				if(listWeek != null && listWeek.size() > 0) {
					weekNum = listWeek.get(0);
					tvDate.setText(listWeekDisplay.get(0));
					getDateFromService();
				} else {
					tvDate.setText("无");
				}
				
			};
		}.execute(createtime_old);
	}
	
	
	/**
	 * 获取本学期所有已发生的月份
	 * 
	 * @param termID
	 */
	public void teach_getPassedMonthByTerm(String termID) {
		
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
				String methodName = "teach_getPassedMonthByTerm";
				
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				paramsMap.put("termID", params[0].toString());	
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
				if(result != null) {
					for(int i = result.getRowCount() - 1; i >= 0; i--) {
						try {
							listMonth.add(result.getCell(i, "monthes"));
							listMonthDisplay.add(result.getCell(i, "monthes") + "月");
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
																	
				monthAdapter = new ArrayAdapter<String>(getApplicationContext(), 
						R.layout.item_test, listMonthDisplay);
				lvChooseWeekOrMonth.setAdapter(monthAdapter);
				lvChooseWeekOrMonth.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						// TODO Auto-generated method stub
						month = listMonth.get(arg2);
						tvDate.setText(listMonthDisplay.get(arg2));
						llChooseWeekOrMonth.setVisibility(View.GONE);
						getDateFromService();
					}
				});
				
				// 关闭刷新；
				refreshView.dismiss();	
				
				if(listMonth != null && listMonth.size() > 0) {
					month = listMonth.get(0);
					tvDate.setText(listMonthDisplay.get(0));
					getDateFromService();
				} else {
					tvDate.setText("无");
				}
				
			};
		}.execute(termID);
	}
	
	/**
	 * 查询是否有教师设置过期中期末时间
	 * 
	 * @param termID
	 * @param grade
	 * @param termType
	 */
	public void teach_midAndFinalTermDate_select(String termID, int grade, int termType) {
		
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
				String methodName = "App_midAndFinalTerm_select";
				
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				paramsMap.put("termID", params[0].toString());
				paramsMap.put("grade", params[1].toString());
				paramsMap.put("termType", params[2].toString());
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
				if(result != null) {				
					try {
						IsSetted = Integer.parseInt(result.getCell(0, "IsSetted"));
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}											
				} else {
					IsSetted = 0;
				}																						
				
				// 关闭刷新；
				refreshView.dismiss();	
				
				if(IsSetted == 1) {
					tvDate.setText("");
					getDateFromService();
				} else {
					tvDate.setText("");
					getDateFromService();
				}
				
			};
		}.execute(termID, grade, termType);
	}
	
	/**
	 * 学生每天知识点掌握度总体情况说明
	 * 
	 * @param studentID
	 * @param subjectID
	 * @param data
	 * @param classID
	 */
	public void getStudentKnowledgeByDay(String studentID, String subjectID, String data, String classID) {
		
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
				String methodName = "APP_getStudentKnowledgeByDay";
				
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
		}.execute(studentID, subjectID, data, classID);
	}
	
	/**
	 * 学生每周知识点掌握度总体情况说明
	 * 
	 * @param studentID
	 * @param subjectID
	 * @param weekID
	 */
	public void APP_getStudentKnowledgeByWeek(String studentID, String subjectID, String weekNum) {
		
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
				DataTable dt_student_all = new DataTable(); // 全科目知识点
				
				// 方法名
				String methodName = "APP_getStudentKnowledgeByWeek";
				
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
				
				// 获取全科知识点掌握情况
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// 把subjectID变成全科，更新TikenID
				paramsMap.remove("subjectID");
				paramsMap.remove("TokenID");
				paramsMap.put("subjectID", "0");							
				paramsMap.put("TokenID", tokenID);
				
				try {
					dt_student_all = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "getStudentKnowledgeByDay()...出错了。。。");
					e.printStackTrace();
				}
				
				if(dt_student_all != null && dt_student_all.getRowCount() > 0) {
					try {
						if(dt_student != null && dt_student.getRowCount() > 0) {
							dt_student.addRow(dt_student_all.getRow(0));
						} else {
							dt_student = emptySingleSubjectResult();
							dt_student.addRow(dt_student_all.getRow(0));
						}
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				return dt_student;
			}

			protected void onPostExecute(DataTable result) {
				
				// 把获取到的数据写入界面
				initViewAfterGetData(result);
				
				// 关闭刷新；
				refreshView.dismiss();											
			};
		}.execute(studentID, subjectID, weekNum);
	}

	/**
	 * 学生每月知识点掌握度总体情况说明，包括班级对比年级对比
	 * 
	 * @param studentID
	 * @param subjectID
	 * @param month
	 * @param termID
	 * @param classID
	 * @param grade
	 */
	public void APP_getStudentKnowledgeByMonth(String studentID, String subjectID, int month, 
			String termID, String classID, int grade) {
		
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
				DataTable dt_student_all = new DataTable(); // 全科目知识点
				
				// 方法名
				String methodName = "APP_getStudentKnowledgeByMonth";
				
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
				paramsMap.put("month", params[2].toString());
				paramsMap.put("termID", params[3].toString());
				paramsMap.put("classID", params[4].toString());	
				paramsMap.put("grade", params[5].toString());	
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
				
				// 获取全科知识点掌握情况
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// 把subjectID变成全科，更新TikenID
				paramsMap.remove("subjectID");
				paramsMap.remove("TokenID");
				paramsMap.put("subjectID", "0");							
				paramsMap.put("TokenID", tokenID);
				
				try {
					dt_student_all = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "getStudentKnowledgeByDay()...出错了。。。");
					e.printStackTrace();
				}
				
				if(dt_student_all != null && dt_student_all.getRowCount() > 0) {
					try {
						if(dt_student != null) {
							dt_student.addRow(dt_student_all.getRow(0));
						} else {
							dt_student = emptySingleSubjectResult();
							dt_student.addRow(dt_student_all.getRow(0));
						}
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				return dt_student;
			}
	
			protected void onPostExecute(DataTable result) {
				
				// 把获取到的数据写入界面
				initViewAfterGetData(result);
				
				// 关闭刷新；
				refreshView.dismiss();											
			};
		}.execute(studentID, subjectID, month, termID, classID, grade);
	}

	/**
	 * 显示截止到期中或者期末按照单元的班级知识点平均掌握度
	 * 
	 * @param studentID
	 * @param classID
	 * @param subject
	 * @param termID
	 * @param termType
	 * @param grade
	 */
	public void teach_chartCurve_studentByTerm(String studentID, String classID, String subject, 
			String termID, int termType, int grade) {
		
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
				String methodName = "teach_chartCurve_studentByTerm";
				
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
				paramsMap.put("classID", params[1].toString());	
				paramsMap.put("subjectID", params[2].toString());
				paramsMap.put("termID", params[3].toString());	
				paramsMap.put("termType", params[4].toString());
				paramsMap.put("grade", params[5].toString());
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
		}.execute(studentID, classID, subject, termID, termType, grade);
	}
	
	// 获取数据后展示到界面上
	private void initViewAfterGetData(DataTable result) {
		// 清空数据
		if(dtKnowledgePoint != null) {
			for(int i = dtKnowledgePoint.getRowCount() - 1; i >= 0; i--) {
				dtKnowledgePoint.deleteRow(i);
			}
		}
		
		// 获取完数据后，初始化界面				
		if(result != null) { // 结果不为空
			dtKnowledgePoint = result;
		} else { // 结果为空，构造“0”数据		
			dtKnowledgePoint = emptyResult();		
		}
		
		if(timeType != null && timeType.length() > 0 && (timeType.equals("期中") || timeType.equals("期末"))) {
			try {
				tvDate.setText(dtKnowledgePoint.getCell(0, "endTime"));
			} catch (dataTableWrongException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		myAdapter = new MyAdapter(getApplicationContext(), dtKnowledgePoint);
		lvKnowledgePoint.setAdapter(myAdapter);
		
	}
	
	// 当没有天、周、月、期中和期末知识点时，构造空数据
	private DataTable emptyResult() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		
		if(timeType != null && timeType.equals("天")) {
			map.put("pointCount", 0);
			map.put("rightPersent", 0);
			map.put("classPersent", 0);
			map.put("dayRank", 0);
		} else if(timeType != null && timeType.equals("周")) {
			map.put("pointCount", 0);
			map.put("rightPersent", 0);
			map.put("classPersent", 0);
			map.put("weekRank", 0);
		} else if(timeType != null && timeType.equals("月")) {
			map.put("pointCount", 0);
			map.put("classRank", 0);
			map.put("gradeRank", 0);
			map.put("rightPersent", 0);
			map.put("classRightPersent", 0);
			map.put("gradeRightPersent", 0);
		} else if(timeType != null && (timeType.equals("期中") || timeType.equals("期末"))) {
			map.put("unitName", "无");
			map.put("classRank", 0);
			map.put("gradeRank", 0);
			map.put("rightPersent", 0);
			map.put("classRightPersent", 0);
			map.put("gradeRightPersent", 0);
			map.put("endTime", "无");
		}

		List<DataRow> listDataRow = new LinkedList<DataRow>();
		
		try {
			DataRow dr = new DataRow(map);
			listDataRow.add(dr);
			if(timeType != null && timeType.equals("周")) {
				listDataRow.add(dr); // 添加全科知识点，之前是单科知识点
			} else if(timeType != null && timeType.equals("月")) {
				listDataRow.add(dr); // 添加全科知识点，之前是单科知识点
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return new DataTable(listDataRow); 
	}
	
	// 当没有天、周、月、期中和期末单科知识点只有全科知识点时，构造空数据
	private DataTable emptySingleSubjectResult() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		
		if(timeType != null && timeType.equals("天")) {
			map.put("pointCount", 0);
			map.put("rightPersent", 0);
			map.put("classPersent", 0);
			map.put("dayRank", 0);
		} else if(timeType != null && timeType.equals("周")) {
			map.put("rightPersent", 0);
			map.put("classPersent", 0);
			map.put("pointCount", 0);					
			map.put("weekRank", 0);
		} else if(timeType != null && timeType.equals("月")) {
			map.put("gradeRightPersent", 0);
			map.put("rightPersent", 0);
			map.put("monthClassRank", 0);
			map.put("pointCount", 0);		
			map.put("monthGradeRank", 0);			
			map.put("classRightPersent", 0);		
		} else if(timeType != null && (timeType.equals("期中") || timeType.equals("期末"))) {
			map.put("unitName", "无");
			map.put("classRank", 0);
			map.put("gradeRank", 0);
			map.put("rightPersent", 0);
			map.put("classRightPersent", 0);
			map.put("gradeRightPersent", 0);
		}

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
	 * @describe 自定义Adapter
	 *
	 */
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
			return 0;
		}	

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return super.getItemViewType(position);
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return super.getViewTypeCount();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			DayAndWeekViewHolder dayAndWeekViewHolder = null;
			MonthAndTermViewHolder monthAndTermViewHolder = null;
			
			// convertView为空，新建ViewHolder
			if(convertView == null) {
				if(timeType != null && (timeType.equals("天") || timeType.equals("周"))) {
					convertView = inflater.inflate(R.layout.item_knowledge_point_day_and_week, parent, false);
					
					dayAndWeekViewHolder = new DayAndWeekViewHolder();
					dayAndWeekViewHolder.tvOverview = (TextView)convertView.findViewById(R.id.tvOverview);
					dayAndWeekViewHolder.tvRank = (TextView)convertView.findViewById(R.id.tvRank);
					dayAndWeekViewHolder.llArc1 = (LinearLayout)convertView.findViewById(R.id.llArc1);
					dayAndWeekViewHolder.llArc2 = (LinearLayout)convertView.findViewById(R.id.llArc2);
					dayAndWeekViewHolder.llArc3 = (LinearLayout)convertView.findViewById(R.id.llArc3);
					dayAndWeekViewHolder.tvRemark = (TextView)convertView.findViewById(R.id.tvRemark);
				} else if (timeType != null && 
						(timeType.equals("月") || timeType.equals("期中") || timeType.equals("期末"))){
					convertView = inflater.inflate(R.layout.item_knowledge_point_month_and_term, parent, false);
					
					monthAndTermViewHolder = new MonthAndTermViewHolder();
					monthAndTermViewHolder.tvOverview = (TextView)convertView.findViewById(R.id.tvOverview);
					monthAndTermViewHolder.tvPointOrUnitCount = (TextView)convertView.findViewById(R.id.tvPointOrUnitCount);
					monthAndTermViewHolder.tvRank = (TextView)convertView.findViewById(R.id.tvRank);
					monthAndTermViewHolder.llArc1 = (LinearLayout)convertView.findViewById(R.id.llArc1);
					monthAndTermViewHolder.llArc2 = (LinearLayout)convertView.findViewById(R.id.llArc2);
					monthAndTermViewHolder.llArc3 = (LinearLayout)convertView.findViewById(R.id.llArc3);
				
				}
				
				// 百分比圆屏幕适配
				int screenWidthPX = ScreenUtils.getScreenWidth(context);
				float screenWidthDP = DensityUtils.px2dp(context, screenWidthPX);
				float arcDiaDP = (screenWidthDP - 48) / 3; // 左右间距和圆之间的间隙共48dp
				int arcDiaPX = DensityUtils.dp2px(getApplicationContext(), arcDiaDP);
				
				if(timeType != null && (timeType.equals("天") || timeType.equals("周"))) {
					LayoutParams llArc1Params = (LayoutParams)dayAndWeekViewHolder.llArc1.getLayoutParams();
					// llArc1Params.width = arcDiaPX;
					llArc1Params.height = arcDiaPX;
					dayAndWeekViewHolder.llArc1.setLayoutParams(llArc1Params);
					
					LayoutParams llArc2Params = (LayoutParams)dayAndWeekViewHolder.llArc2.getLayoutParams();
					// llArc2Params.width = arcDiaPX;
					llArc2Params.height = arcDiaPX;
					dayAndWeekViewHolder.llArc2.setLayoutParams(llArc2Params);
					
					LayoutParams llArc3Params = (LayoutParams)dayAndWeekViewHolder.llArc3.getLayoutParams();
					// llArc3Params.width = arcDiaPX;
					llArc3Params.height = arcDiaPX;
					dayAndWeekViewHolder.llArc3.setLayoutParams(llArc3Params);
															
					convertView.setTag(dayAndWeekViewHolder);
					
				} else if (timeType != null && 
						(timeType.equals("月") || timeType.equals("期中") || timeType.equals("期末"))){
					LayoutParams llArc1Params = (LayoutParams)monthAndTermViewHolder.llArc1.getLayoutParams();
					// llArc1Params.width = arcDiaPX;
					llArc1Params.height = arcDiaPX;
					monthAndTermViewHolder.llArc1.setLayoutParams(llArc1Params);
					
					LayoutParams llArc2Params = (LayoutParams)monthAndTermViewHolder.llArc2.getLayoutParams();
					// llArc2Params.width = arcDiaPX;
					llArc2Params.height = arcDiaPX;
					monthAndTermViewHolder.llArc2.setLayoutParams(llArc2Params);
					
					LayoutParams llArc3Params = (LayoutParams)monthAndTermViewHolder.llArc3.getLayoutParams();
					// llArc3Params.width = arcDiaPX;
					llArc3Params.height = arcDiaPX;
					monthAndTermViewHolder.llArc3.setLayoutParams(llArc3Params);
															
					convertView.setTag(monthAndTermViewHolder);	
				}
			
			// convertView不为空，从convertView中获取ViewHolder
			}else {
				if(timeType != null && (timeType.equals("天") || timeType.equals("周"))) {
					dayAndWeekViewHolder = (DayAndWeekViewHolder)convertView.getTag();			
				} else if (timeType != null && 
						(timeType.equals("月") || timeType.equals("期中") || timeType.equals("期末"))){
					monthAndTermViewHolder = (MonthAndTermViewHolder)convertView.getTag();
				}									
			}
			
			// 从DataTable中获取数据并显示到屏幕上
			// 天和周
			if(timeType != null && (timeType.equals("天") || timeType.equals("周"))) {
				int pointCount = 0;
				int rightPersent = 0;
				int classPersent = 0;
				int rank = 0;
				String strRemark = "";
				
				try {
					pointCount = Integer.parseInt(dt.getCell(position, "pointCount"));
					rightPersent = Math.round(Float.parseFloat(dt.getCell(position, "rightPersent")) * 100);
					classPersent = Math.round(Float.parseFloat(dt.getCell(position, "classPersent")) * 100);
					if(timeType.equals("天")) {
						rank = Integer.parseInt(dt.getCell(position, "dayRank"));
					} else if (timeType.equals("周")) {
						rank = Integer.parseInt(dt.getCell(position, "weekRank"));
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// 往界面填充数据
				if(position == 0) {
					dayAndWeekViewHolder.tvOverview.setText("本科目知识点");
				} else if (position == 1) {
					dayAndWeekViewHolder.tvOverview.setText("全科目知识点");
				}
				dayAndWeekViewHolder.tvRank.setText("年级排名：" + rank);
				dayAndWeekViewHolder.llArc1.addView(new KnowledgePointCountArc(getApplicationContext(), 0xFF38B5E2, pointCount));
				dayAndWeekViewHolder.llArc2.addView(new KnowledgePointArc(getApplicationContext(), 0xFF00C54F, rightPersent));
				dayAndWeekViewHolder.llArc3.addView(new KnowledgePointArc(getApplicationContext(), 0xFFFF4F4D, classPersent));				
			
				// 成绩简评
				if(pointCount == 0 && rightPersent == 0 && classPersent == 0) {
					strRemark = "单科成绩简评：无";
				} else if(rightPersent <= 100 && rightPersent > 80) {
					strRemark = "单科成绩简评：优";
				} else if (rightPersent <= 80 && rightPersent > 60) {
					strRemark = "单科成绩简评：良";
				} else if(rightPersent == 60) {
					strRemark = "单科成绩简评：中";
				} else if(rightPersent < 60) {
					strRemark = "单科成绩简评：差";
				}
				dayAndWeekViewHolder.tvRemark.setText(strRemark);
				
			// 月、期中和期末
			} else if (timeType != null && 
					(timeType.equals("月") || timeType.equals("期中") || timeType.equals("期末"))){
				String unitName = ""; // 单元名字（期中期末）
				int pointCount = 0; // 知识点数量（月）			
				int unitCount = 0; // 单元数（期中期末）
				int classRank = 0; // 班级排名
				int gradeRank = 0; // 年级排名
				int rightPersent = 0;
				int classRightPersent = 0;
				int gradeRightPersent = 0;
				String endTime = "";
								
				try {
					if(timeType.equals("月")) {
						pointCount = Integer.parseInt(dt.getCell(position, "pointCount"));
						classRank = Integer.parseInt(dt.getCell(position, "monthClassRank"));
						gradeRank = Integer.parseInt(dt.getCell(position, "monthGradeRank"));
					} else if (timeType.equals("期中") || timeType.equals("期末")) {
						unitName = dt.getCell(position, "unitName");						
						unitCount = dt.getRowCount();
						classRank = Integer.parseInt(dt.getCell(position, "classRank"));
						gradeRank = Integer.parseInt(dt.getCell(position, "gradeRank"));
						endTime = dt.getCell(position, "endTime");
					}
					rightPersent = Math.round(Float.parseFloat(dt.getCell(position, "rightPersent")) * 100);
					classRightPersent = Math.round(Float.parseFloat(dt.getCell(position, "classRightPersent")) * 100);
					gradeRightPersent = Math.round(Float.parseFloat(dt.getCell(position, "gradeRightPersent")) * 100);
					
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// 往界面填充数据	
				if(timeType.equals("月")) {
					if(position == 0) {
						monthAndTermViewHolder.tvOverview.setText("本科目知识点");
						monthAndTermViewHolder.tvPointOrUnitCount.setVisibility(View.VISIBLE);
						monthAndTermViewHolder.tvPointOrUnitCount.setText("（知识点数量：" + pointCount + "）");
					} else if (position == 1) {
						monthAndTermViewHolder.tvOverview.setText("全科目知识点");
						monthAndTermViewHolder.tvPointOrUnitCount.setVisibility(View.GONE);
					}					
								
				} else if (timeType.equals("期中") || timeType.equals("期末")) {
					monthAndTermViewHolder.tvOverview.setText(unitName);
					if(unitName != null && unitName.length() > 0 && unitName.equals("无")) {
						monthAndTermViewHolder.tvPointOrUnitCount.setText("单元数：无" + "   截至日期：" + endTime);
					} else {
						monthAndTermViewHolder.tvPointOrUnitCount.setText("单元数：" + unitCount + "   截至日期：" + endTime);
					}
					
				}
				
				monthAndTermViewHolder.tvRank.setText("班级排名：" + classRank + "   年级排名：" + gradeRank);
				// 画圆
				monthAndTermViewHolder.llArc1.addView(new KnowledgePointArc(getApplicationContext(), 0xFF38B5E2, rightPersent));
				monthAndTermViewHolder.llArc2.addView(new KnowledgePointArc(getApplicationContext(), 0xFF00C54F, classRightPersent));
				monthAndTermViewHolder.llArc3.addView(new KnowledgePointArc(getApplicationContext(), 0xFFFF4F4D, gradeRightPersent));																
			}
		
			return convertView;
		}
		
	}
	
	/**
	 * 
	 * @author 進擊のotaku
	 *
	 * @describe 天和周的view
	 *
	 */
	public static class DayAndWeekViewHolder {
		TextView tvOverview;
		TextView tvRank; // 排名
		LinearLayout llArc1;
		LinearLayout llArc2;
		LinearLayout llArc3;
		TextView tvRemark; // 简评
	}
	
	/**
	 * 
	 * @author 進擊のotaku
	 *
	 * @describe 月、期中和期末的view
	 *
	 */
	public static class MonthAndTermViewHolder {
		TextView tvOverview;
		TextView tvPointOrUnitCount; // 知识点数量或者单元总数
		TextView tvRank; // 排名
		LinearLayout llArc1;
		LinearLayout llArc2;
		LinearLayout llArc3;
	}
}
