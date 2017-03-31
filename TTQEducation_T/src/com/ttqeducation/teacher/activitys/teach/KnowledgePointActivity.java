package com.ttqeducation.teacher.activitys.teach;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



import com.ttqeducation.teacher.beans.DataRow;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.TeacherInfo;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.tools.DateUtil;
import com.ttqeducation.teacher.tools.DesUtil;
import com.ttqeducation.teacher.tools.GeneralTools;
import com.ttqeducation.teacher.tools.DensityUtils;
import com.ttqeducation.teacher.tools.KnowledgePointArc;
import com.ttqeducation.teacher.tools.KnowledgePointCountArc;
import com.ttqeducation.teacher.tools.ScreenUtils;

import android.R.integer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 知识点查看概况界面
 * @author 侯翔宇
 *
 */
public class KnowledgePointActivity extends Activity {

	//标题栏部分
	private LinearLayout titleBackLayout = null; // 返回按钮
	private TextView titleTextView = null ;// 标题栏文字
			
	//科目
	private String[] subjects;
	private String subjectName ="语文";
	private String subjectID="";
	
	//科目选择按钮
	private LinearLayout llSubject;
	private TextView tvSubject;
	private ImageView ivSubject;
	//科目选择的Listview
	private LinearLayout llChooseSubject;
	private ListView lvChooseSubject;
	
	
	//时间节点
	private String[] timeTypes;
	private String timeType= "";//包括 天 周 月 期中 期末
	//时间节点选择按钮
	private LinearLayout llTimeType;
	private TextView tvTimeType;
	private ImageView ivTimeType;
	//时间节点选择ListView
	private LinearLayout llChooseTimeType;
	private ListView lvChooseTimeType;
	//期中期末截止时间 
	private String deadline ="";
	//具体时间
	private String date=""; //日期  
	private String weekNum =""; //周次
	private List<String> listWeek= new ArrayList<String>();
	private String month="";
	private List<String> listMonth = new ArrayList<String>();
	private int termType = 0; //期中期末
	private int IsSetted = 0;//是否设置过期中期末测试时间
	//时间选择按钮
	private LinearLayout llDate;
	private TextView tvDate;
	private ImageView ivDate;
	// 日历界面
	private LinearLayout llChooseDate;
	private TextView yearAndMonth;
	private LinearLayout[] daylayoutArray;
	private Date currentDate;
	private int weeknum;//只给日历使用
	// 周、月选择ListView
	private LinearLayout llChooseWeekOrMonth;
	private ListView lvChooseWeekOrMonth;
	private ArrayAdapter<String> weekAdapter;
	private ArrayAdapter<String> monthAdapter;
	// 概述界面
	private ListView lvKnowledgePoint;
	private DataTable dtKnowledgePoint;
	
	
	//知识点明细
	private LinearLayout llKnowledgePoint;
	//曲线图
	private LinearLayout llGraph;
	private TextView tvGraph;
	private ImageView ivGraph;
	//关注班级
	private String caredClassID="";
	private String caredClassName="";
	private LinearLayout llCaredClass;
	private TextView tvCaredClass;
	private LinearLayout llChooseCaredClass;
	private ListView lvChooseCaredClass;
	private String[] classes=null;
	private Map<String,String> classesByGrade;//建立班级名称和班级ID的键值对，用于在选择班级后获取班级ID
	//分割线
	private View vdivide1;
	//自定义adapter
	private MyAdapter myAdapter;
	
	private String classID = "";
	private String termID = "";
	private int grade = 0;
	
	private RefreshView refreshView = null;
	private Dialog reminderDialog = null;	// 提示用dialog；
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_knowledge_point);
		
		initView();
		getData();
	}

	//控件实例化
	public void initView(){
		
		//标题栏部分
		this.titleTextView=(TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("知识点查看");
		//返回
		this.titleBackLayout = (LinearLayout)(super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				KnowledgePointActivity.this.finish();
			}
		});
		
		//科目选择按钮
		llSubject=(LinearLayout)findViewById(R.id.llSubject);
		tvSubject=(TextView)findViewById(R.id.tvSubject);
		ivSubject = (ImageView)findViewById(R.id.ivSubject);
		//科目选择listview
		llChooseSubject = (LinearLayout)findViewById(R.id.llChooseSubject);
		lvChooseSubject=(ListView)findViewById(R.id.lvChooseSubject);
		//科目选择列表
		llSubject.setOnClickListener(new  MyOnClickListener());
		initChooseSubject();
		
		//时间节点选择按钮
		llTimeType = (LinearLayout)findViewById(R.id.llTimeType);
		tvTimeType = (TextView)findViewById(R.id.tvTimeType);
		ivTimeType = (ImageView)findViewById(R.id.ivTimeType);
		// 时间节点选择ListView
		llChooseTimeType = (LinearLayout)findViewById(R.id.llChooseTimeType);
		lvChooseTimeType = (ListView)findViewById(R.id.lvChooseTimeType);
		//注册事件
		llTimeType.setOnClickListener(new MyOnClickListener());
		date= DateUtil.convertDateToString("yyyy-MM-dd", new Date());
		initChooseTimeType();
		
		//时间选择按键
		llDate=(LinearLayout)findViewById(R.id.llDate);
		tvDate = (TextView)findViewById(R.id.tvDate);
		ivDate = (ImageView)findViewById(R.id.ivDate);
		tvDate.setText(date);
		// 日历界面
		llChooseDate = (LinearLayout)findViewById(R.id.llChooseDate);
		// 周、月选择ListView
		llChooseWeekOrMonth = (LinearLayout)findViewById(R.id.llChooseWeekOrMonth);
		lvChooseWeekOrMonth = (ListView)findViewById(R.id.lvChooseWeekOrMonth);
		// 日历界面及日期界面注册
		llDate.setOnClickListener(new MyOnClickListener());
		initCalendarLayout();
		initCalendar(new Date());
		//曲线图
		llGraph=(LinearLayout) findViewById(R.id.llGraph);
		llGraph.setVisibility(View.INVISIBLE);
		llGraph.setOnClickListener(new MyOnClickListener());
		
		
		//关注班级
		tvCaredClass =(TextView)findViewById(R.id.tvCaredClass);
		llCaredClass =(LinearLayout)findViewById(R.id.llCaredClass);
		llChooseCaredClass = (LinearLayout)findViewById(R.id.llChooseCaredClass);
		lvChooseCaredClass = (ListView)findViewById(R.id.lvChooseCaredClass);
		llCaredClass.setOnClickListener(new MyOnClickListener());
		initChooseCaredClass(TeacherInfo.getInstance().getGrade());
		//知识点明细
		llKnowledgePoint=(LinearLayout)findViewById(R.id.llKnowledgePoint);
		//分隔符
		vdivide1=(View)findViewById(R.id.vdivide1);
		
		// 概述界面 
		lvKnowledgePoint = (ListView)findViewById(R.id.lvKnowledgePoint);
		
		llCaredClass.setVisibility(View.GONE);
		vdivide1.setVisibility(View.GONE);
		
		llGraph.setGravity(Gravity.CENTER);
		getDataFromService();
		
		llKnowledgePoint.setOnClickListener(new MyOnClickListener());
		
	}
	
	/**
	 * 根据教师当前选择班级的年级获取可以选择的班级，并载入界面
	 */
	private void initChooseCaredClass(String gradeID) {
		// TODO Auto-generated method stub
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, DataTable>() {
			@Override
			
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				//refreshView.show();
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				DataTable dt_student = new DataTable();
				
				// 方法名
				String methodName = "pub_class_getAllClassByGrade";
				
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				paramsMap.put("grade", params[0].toString());											
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
					Log.i("error", "pub_class_getAllClassByGrade()...出错了。。。");
					e.printStackTrace();
				}
				
				return dt_student;
			}

			protected void onPostExecute(DataTable result) {
				
				// 把获取到的数据写入界面
				classesByGrade= new HashMap<String, String>();
				classes = new String[result.getRowCount()];
				for(int i=0;i<result.getRowCount();i++){
					try {
						classes[i] = result.getCell(i, "className");
						classesByGrade.put(result.getCell(i, "className"), result.getCell(i, "classID"));
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if(result!=null && result.getRowCount()>0){
					 ArrayAdapter<String> classesAdapter = new ArrayAdapter<String>(getApplicationContext(),
							 R.layout.item_subject, classes);
					 lvChooseCaredClass.setAdapter(classesAdapter);
					 lvChooseCaredClass.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View view,
								int position, long arg3) {
							// TODO Auto-generated method stub
							caredClassID= classesByGrade.get(classes[position]);
							caredClassName = classes[position];
							tvCaredClass.setText(classes[position].trim());
							llChooseCaredClass.setVisibility(view.GONE);
							getDataFromService();
						}
					});
				}
				
				// 关闭刷新；
				refreshView.dismiss();											
			};
		}.execute(gradeID);
	}

	//获得参数
	public void getData(){
		
		classID=TeacherInfo.getInstance().getClassID();
		termID=TeacherInfo.getInstance().getTermID();
		grade =Integer.parseInt(TeacherInfo.getInstance().getGrade()) ;
		
	}
	
	class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.llSubject:
				llChooseTimeType.setVisibility(View.GONE); // 隐藏时间节点选择
				llChooseDate.setVisibility(View.GONE); // 隐藏日历界面
				llChooseWeekOrMonth.setVisibility(View.GONE); // 隐藏周和月选择界面
				llChooseCaredClass.setVisibility(View.GONE);//隐藏关注班级选择
				
				if(llChooseSubject.getVisibility()==View.VISIBLE){
					llChooseSubject.setVisibility(View.GONE);
				}
					
				else{
					llChooseSubject.setVisibility(View.VISIBLE);					
				}					
				break;
			case R.id.llTimeType:
				llChooseSubject.setVisibility(View.GONE); // 隐藏科目选择
				llChooseDate.setVisibility(View.GONE); // 隐藏日历界面
				llChooseWeekOrMonth.setVisibility(View.GONE); // 隐藏周和月选择界面	
				llChooseCaredClass.setVisibility(View.GONE);//隐藏关注班级选择
				
				if(llChooseTimeType.getVisibility()==View.VISIBLE){
					llChooseTimeType.setVisibility(View.GONE);
				}else{
					llChooseTimeType.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.llDate:
				llChooseSubject.setVisibility(View.GONE); // 隐藏科目选择
				llChooseTimeType.setVisibility(View.GONE); // 隐藏时间节点选择	
				llChooseCaredClass.setVisibility(View.GONE);//隐藏关注班级选择
				
				//时间类型为天  打开日历界面
				if(timeType!=null&&timeType.equals("天")){
					if(llChooseDate.getVisibility()==View.VISIBLE){
						llChooseDate.setVisibility(View.GONE);
					}else{
						llChooseDate.setVisibility(View.VISIBLE);
					}					
				}else if(timeType != null && (timeType.equals("周") || timeType.equals("月"))){
					if(llChooseWeekOrMonth.getVisibility() == View.VISIBLE) {
						llChooseWeekOrMonth.setVisibility(View.GONE);
					} else {
						llChooseWeekOrMonth.setVisibility(View.VISIBLE);
					}
				}
				break;
			case R.id.llKnowledgePoint:
				Intent intent = new Intent();
				intent.setClass(KnowledgePointActivity.this, KnowledgePointDetailActivity.class);
				
				if(timeType!= null && timeType.length() > 0){
					intent.putExtra("timeType", timeType);
				}
				
				if(timeType != null && timeType.length() > 0 && timeType.equals("天")){
					intent.putExtra("classID", classID);
					intent.putExtra("date", date);
					intent.putExtra("subjectID", subjectID);
				}else if (timeType != null && timeType.length() > 0 && timeType.equals("周")){
					intent.putExtra("classID", classID);					
					intent.putExtra("subjectID", subjectID);
					String weekID="";
					if( 0<Integer.parseInt(weekNum) && Integer.parseInt(weekNum)<10){
						weekID=termID.trim()+"0"+weekNum;
					}else{
						weekID=termID.trim()+weekNum;
					}
					intent.putExtra("weekID", weekID);
				}else if (timeType != null && timeType.length() > 0 && timeType.equals("月")){
					intent.putExtra("classID", classID);
					intent.putExtra("termID", termID);
					intent.putExtra("subjectID", subjectID);
					intent.putExtra("month", month);					
				}else if (timeType != null && timeType.length() > 0 && (timeType.equals("期中") || timeType.equals("期末"))){
					intent.putExtra("classID", classID);
					intent.putExtra("termID", termID);
					intent.putExtra("subjectID", subjectID);
					intent.putExtra("termType", termType);
				}
				startActivity(intent);
				break;
			case R.id.llCaredClass:
				llChooseSubject.setVisibility(View.GONE); // 隐藏科目选择
				llChooseDate.setVisibility(View.GONE); // 隐藏日历界面
				llChooseWeekOrMonth.setVisibility(View.GONE); // 隐藏周和月选择界面	
				llChooseTimeType.setVisibility(View.GONE);
				
				if(llChooseCaredClass.getVisibility()==View.VISIBLE){
					llChooseCaredClass.setVisibility(View.GONE);
				}else{
					llChooseCaredClass.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.llGraph:
				Intent intentGraph = new Intent();
				intentGraph.setClass(KnowledgePointActivity.this, KnowledgePointGraph.class);
				if(timeType!=null && timeType.length()>0){
					intentGraph.putExtra("timeType", timeType);
					
					//周对比曲线图
					if(timeType.equals("周")){
						intentGraph.putExtra("classID", classID);
						intentGraph.putExtra("subjectID", subjectID);
						intentGraph.putExtra("termID", termID);
					}//月对比曲线图（包括关注班级）
					else if(timeType.equals("月")){
						intentGraph.putExtra("classID", classID);
						intentGraph.putExtra("subjectID", subjectID);
						intentGraph.putExtra("termID", termID);
						intentGraph.putExtra("caredClassID", caredClassID);
						intentGraph.putExtra("grade", grade);
					}else if(timeType.equals("期中")||timeType.equals("期末")){
						intentGraph.putExtra("classID", classID);
						intentGraph.putExtra("subjectID", subjectID);
						intentGraph.putExtra("termID", termID);
						intentGraph.putExtra("caredClassID", caredClassID);
						intentGraph.putExtra("grade", grade);
						intentGraph.putExtra("termType", termType);
					}
					
					startActivity(intentGraph);
				}
				break;
			default:
				break;
			}
		}
		
	}
	
	//选择科目界面
	private void initChooseSubject(){
		
		//subjects = new String[]{"语文", "数学", "英语"};
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
					getDataFromService();
				}
				 
			});
		}
	}
	
	// 根据科目名字获取科目ID
	private void getSubjectID() {
		subjectID = GeneralTools.getInstance().getSubjectIDByName(subjectName);
		if(subjectID==null){
			subjectID="-1";
		}
	}
	
	//选择时间节点
	private void initChooseTimeType(){
		timeTypes= new String[]{"天", "周", "月", "期中", "期末"	};
		timeType = "天";//进入界面时默认为天
		tvTimeType.setText(timeType);
		//设置listview适配器
		ArrayAdapter<String> timeTypeAdapter = new ArrayAdapter<String>(getApplicationContext(), 
				R.layout.item_subject, timeTypes);
		lvChooseTimeType.setAdapter(timeTypeAdapter);
		//添加列表点击事件
		lvChooseTimeType.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				timeType=timeTypes[position];
				tvTimeType.setText(timeType);
				llChooseTimeType.setVisibility(view.GONE);
				if(timeType!=null &&timeType.length()>0 &&timeType.equals("天")){
					tvDate.setText(date);
					llCaredClass.setVisibility(View.GONE);
					vdivide1.setVisibility(View.GONE);
					llGraph.setGravity(Gravity.CENTER); 
					llGraph.setVisibility(View.INVISIBLE);
					
					getDataFromService();
				}else if(timeType!=null && timeType.length()>0 && timeType.equals("周")){
					Teach_GetWeek(DateUtil.convertDateToString("yyyy-MM-dd", new Date()));
					llCaredClass.setVisibility(View.GONE);
					vdivide1.setVisibility(View.GONE);
					llGraph.setVisibility(View.VISIBLE);
					llGraph.setGravity(Gravity.CENTER);					
				}else if(timeType!=null && timeType.length()>0 && timeType.equals("月")){
					teach_getPassedMonthByTerm(termID);
					llCaredClass.setVisibility(View.VISIBLE);
					vdivide1.setVisibility(View.VISIBLE);
					llGraph.setVisibility(View.VISIBLE);
					llGraph.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
				}else if(timeType!=null && timeType.length()>0 && (timeType.equals("期中")||timeType.equals("期末"))){
					llCaredClass.setVisibility(View.VISIBLE);
					vdivide1.setVisibility(View.VISIBLE);
					llGraph.setVisibility(View.VISIBLE);
					llGraph.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
					if(timeType.equals("期中")){
						termType=1;						
					}else{
						termType=2;
					}
					teach_midAndFinalTermDate_select(termID, grade, termType);
				}
			}
		});
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
	
	/**
	 * 查询当前属于第几周
	 */
	public void Teach_GetWeek(String createTime){
		this.refreshView= new RefreshView(this, R.style.full_screen_dialog);
		//异步过程访问网络
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
				DesUtil.addTokenIDToSchoolWS();
				DataTable dt_week = new DataTable();
				//方法名
				String methodName = "Teach_GetWeek";
				//存放参数map
				Map<String,String> paramsMap = new HashMap<String,String>();
				String tokenID="";
				try{
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				}catch(Exception e1){
					e1.printStackTrace();
				}
				
				paramsMap.put("time",params[0].toString());
				paramsMap.put("TokenID",tokenID);
				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 从本地获取学校URL,数据由SharedPreferences存放;
				SharedPreferences pre= getSharedPreferences("TTQAndroid", Activity.MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				if(schoolURL ==null){
					return null;
				}
				getdatatool.setURL(schoolURL);
				try{
					dt_week = getdatatool.getDataAsTable(methodName,paramsMap);
				}catch(Exception e){
					Log.i("error","Teach_getweek()....出错了");
					e.printStackTrace();
				}
				
				return dt_week;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				//把数据写入界面
				if(result!=null){
					int currentWeekNum= 0;//获取当前是第几周
					try{
						currentWeekNum=Integer.parseInt(result.getCell(0, "weekNum"));
					}catch(NumberFormatException e1){
						e1.printStackTrace();
					}catch(dataTableWrongException e1){
						e1.printStackTrace();
					}
					
					for(int i= currentWeekNum;i>=1;i--){
						listWeek.add(i+"");
					}						
				}
				
				weekAdapter= new ArrayAdapter<String>(getApplicationContext(),
						R.layout.item_test, listWeek);
				lvChooseWeekOrMonth.setAdapter(weekAdapter);
				lvChooseWeekOrMonth.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						weekNum=listWeek.get(arg2);
						tvDate.setText(weekNum);
						llChooseWeekOrMonth.setVisibility(View.GONE);
						getDataFromService();
					}
				});
				
				refreshView.dismiss();
				if(listWeek!=null&& listWeek.size()>0){
					weekNum= listWeek.get(0);
					tvDate.setText(listWeek.get(0));	
					getDataFromService();
				}else {
					tvDate.setText("时间回看");
				}				
			}		
			
		}.execute(createTime);
	}
	
	
	/**
	 * 获得本学期所有已经发生的月份
	 */
	public void teach_getPassedMonthByTerm(String termID){
		
		this.refreshView= new RefreshView(this,R.style.full_screen_dialog);
		Log.i("hxy", termID);
		//异步调用访问数据
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
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				DataTable dt_month = new DataTable();				
				// 方法名
				String methodName = "teach_getPassedMonthByTerm";
				//参数map
				Map<String,String> paramsMap= new HashMap<String, String>();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				paramsMap.put("termID",params[0].toString());
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
					dt_month = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "teach_getPassedMonthByTerm()...出错了。。。");
					e.printStackTrace();
				}		
				
				return dt_month;
			}

			@Override
			protected void onPostExecute(DataTable result) {				
				//数据写入界面
				if(result!=null){
					for(int i=result.getRowCount()-1;i>=0;i--){
						try{
							listMonth.add(result.getCell(i, "monthes"));
						}catch (dataTableWrongException e){
							e.printStackTrace();
						}
					}
				}
				
				monthAdapter = new ArrayAdapter<String>(getApplicationContext(),
						R.layout.item_test, listMonth);
				lvChooseWeekOrMonth.setAdapter(monthAdapter);
				lvChooseWeekOrMonth.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						month= listMonth.get(arg2);
						tvDate.setText(month);
						llChooseWeekOrMonth.setVisibility(View.GONE);	
						getDataFromService();
					}
				});
				//关闭刷新
				refreshView.dismiss();
				if(listMonth.size()>0&&listMonth!=null){
					month=listMonth.get(0);
					tvDate.setText(month);
					getDataFromService();
				}else{
					tvDate.setText("时间回看");
				}
			}			
			
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
		
		this.refreshView= new RefreshView(this, R.style.full_screen_dialog);
	
		//异步任务访问网络
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
				DataTable dt_result= new DataTable();
				String methodName ="App_midAndFinalTerm_select";
				
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
					dt_result = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "getStudentKnowledgeByDay()...出错了。。。");
					e.printStackTrace();
				}								
				
				return dt_result;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				//写数据到界面
				if(result!=null){
					try{
						IsSetted=Integer.parseInt(result.getCell(0, "IsSetted")) ;
					}catch(dataTableWrongException e){
						e.printStackTrace();
					}
				}else{
					IsSetted=0;
				}
				
				//关闭刷新
				refreshView.dismiss();
				if(IsSetted==1){
					tvDate.setText("");		
					getDataFromService();
				}else{//如果还没有设置期中或期末考试时间，注意，这里的期中期末未学校中真实的、唯一的测试
					tvDate.setText("");
					getDataFromService();
					if(timeType.equals("期中"))
						initReminderDialog("提示", "当前尚未设置考试时间，如果学校已经进行了期中测试，请输入该日期，一旦输入无法修改", "设置", "取消");
					else if (timeType.equals("期末"))
						initReminderDialog("提示", "当前尚未设置考试时间，如果学校已经进行了期末测试，请输入该日期，一旦输入无法修改", "设置", "取消");
				}
			}
			
			
			
		}.execute(termID,grade,termType);		
	}
	
	/**
	 * 窗口提示信息
	 */
	public void showToast(String toastMessage){
		Toast toast = Toast.makeText(KnowledgePointActivity.this, toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}
	
	/**
	 *  查看某天的知识点,日历界面的点击事件
	 * @param v
	 */
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
			getDataFromService();
		}
	}
	
	/**
	 * 通过方法访问WebService获取数据
	 * 
	 */
	private void getDataFromService(){
		if(timeType != null && timeType.length() > 0 && timeType.equals("天")){
			getClassKnowledgeByDay(classID,date,subjectID);
		}else if(timeType != null && timeType.length() > 0 && timeType.equals("周")){
			String weekID="";
			if( 0<Integer.parseInt(weekNum) && Integer.parseInt(weekNum)<10){
				weekID=termID.trim()+"0"+weekNum;
			}else{
				weekID=termID.trim()+weekNum;
			}
			getClassKnowledgeByWeek(classID,weekID,subjectID);
		}else if(timeType != null && timeType.length() > 0 && timeType.equals("月")){
			getClassKnowledgeByMonth(classID,subjectID,termID,month,caredClassID,grade);
		}else if (timeType != null && timeType.length() > 0 
				&& (timeType.equals("期中")||timeType.equals("期末"))){
			teach_chartCurve_classByTerm(classID,subjectID,termID,termType,caredClassID,grade);
		}
	}

	


	/**
	 * 班级每天知识点掌握度总体情况说明
	 * @param classID2
	 * @param date2
	 * @param subjectID2
	 */
	private void getClassKnowledgeByDay(String classID, String date,
			String subjectID) {
		// TODO Auto-generated method stub
		this.refreshView = new RefreshView(this,  R.style.full_screen_dialog);
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
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				DataTable dt_result = new DataTable();
				
				// 方法名
				String methodName = "APP_getClassKnowledgeByDay";
				//参数Map
				Map<String,String> paramsMap = new HashMap<String, String>();
				String tokenID ="";
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
					Log.i("error", "getClassKnowledgeByDay()...出错了。。。");
					e.printStackTrace();
				}
				
				return dt_result;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				initViewAfterGetData(result);
				refreshView.dismiss();	
			}

			
			
		}.execute(classID,date,subjectID);
	}
	
	/**
	 * 班级每周知识点掌握度总体情况说明
	 * @param classID
	 * @param weekID
	 * @param subjectID
	 */
	private void getClassKnowledgeByWeek(String classID, String weekID,
			String subjectID) {
		
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		//异步访问网络
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
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				DataTable dt_result = new DataTable();
				// 方法名
				String methodName = "APP_getClassKnowledgeByWeek";
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
					Log.i("error", "APP_getClassKnowledgeByWeek()...出错了。。。");
					e.printStackTrace();
				}
				
				return dt_result;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				initViewAfterGetData(result);
				// 关闭刷新；
				refreshView.dismiss();		
			}
			
		}.execute(classID,weekID,subjectID);
	}
	
	/**
	 * 班级每月知识点掌握度总体情况说明
	 * @param classID
	 * @param subjectID
	 * @param termID
	 * @param month
	 * @param CaredClassID
	 * @param grade
	 */
	private void getClassKnowledgeByMonth(String classID, String subjectID,
			String termID, String month, String CaredClassID, int grade) {
		// TODO Auto-generated method stub
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
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				// 用模拟的数据库表存储返回结果
				DataTable dt_result1 = new DataTable();
				DataTable dt_result2 = new DataTable(); // 全科目知识点
				
				// 方法名
				String methodName = "APP_getClassKnowledgeByMonth";
				
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
				paramsMap.put("subjectID", params[1].toString());
				paramsMap.put("termID", params[2].toString());
				paramsMap.put("month", params[3].toString());
				paramsMap.put("caredClassID", params[4].toString());	
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
					dt_result1 = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "APP_getClassKnowledgeByMonth()...出错了。。。");
					e.printStackTrace();
				}
				
				//获取全科
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.remove("subjectID");
				paramsMap.remove("TokenID");
				paramsMap.put("subjectID", "0");							
				paramsMap.put("TokenID", tokenID);
				
				try {
					dt_result2 = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "getStudentKnowledgeByDay()...出错了。。。");
					e.printStackTrace();
				}
				
				if(dt_result2 != null && dt_result2.getRowCount() > 0){
					try {
						if(dt_result1 != null) {
							dt_result1.addRow(dt_result2.getRow(0));
						} else {
							dt_result1 = emptySingleSubjectResult();
							dt_result1.addRow(dt_result2.getRow(0));
						}
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
				
				
				return dt_result1;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				initViewAfterGetData(result);
				refreshView.dismiss();
			}
			
		}.execute( classID,  subjectID, termID,  month,  CaredClassID,  grade);
	}
	
	
		
		/**
		 * 知识点掌握度曲线图，显示截止到期中或者期末按照单元的班级知识点平均掌握度
		 * @param classID
		 * @param subjectID
		 * @param termID
		 * @param termType
		 * @param caredClassID
		 * @param grade
		 */
		private void teach_chartCurve_classByTerm(String classID,
				String subjectID, String termID, int termType, String caredClassID,
				int grade) {
			
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
					DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
					
					// 用模拟的数据库表存储返回结果
					DataTable dt_result = new DataTable();
					
					// 方法名
					String methodName = "teach_chartCurve_classByTerm";
					
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
					paramsMap.put("subjectID", params[1].toString());
					paramsMap.put("termID", params[2].toString());	
					paramsMap.put("termType", params[3].toString());
					paramsMap.put("caredClassID", params[4].toString());					
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
						dt_result = getdatatool.getDataAsTable(methodName, paramsMap);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.i("error", "teach_chartCurve_classByTerm()...出错了。。。");
						e.printStackTrace();
					}
					
					return dt_result;
				}

				@Override
				protected void onPostExecute(DataTable result) {
					// TODO Auto-generated method stub
					initViewAfterGetData(result);
					refreshView.dismiss();
				}
				
				
			}.execute( classID,	 subjectID,  termID,  termType,  caredClassID, grade);
		}
		
		/**
		 * 存储教师设置期中期末时间的记录
		 * @param termID
		 * @param grade
		 * @param teacherID
		 * @param endTime
		 * @param termType
		 */
		private void teachMidAndFinalTermDateInsert(String termID, int grade, String teacherID,String endTime,int termType){
			
			this.refreshView= new RefreshView(this,R.style.full_screen_dialog);
			new AsyncTask<Object, Object, String>() {

				@Override
				protected void onPreExecute() {
					// TODO Auto-generated method stub
					super.onPreExecute();
					refreshView.show();
				}

				@Override
				protected String doInBackground(Object... params) {
					// TODO Auto-generated method stub
					
					DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
					
					// 用模拟的数据库表存储返回结果
					String result ="";
					// 方法名
					String methodName = "teach_midAndFinalTermDate_insert";
					//参数Map
					Map<String,String> paramsMap = new HashMap<String,String>();
					String tokenID ="";
					try {
						tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					paramsMap.put("termID", params[0].toString());
					paramsMap.put("grade", params[1].toString());
					paramsMap.put("teacherID", params[2].toString());
					paramsMap.put("endTime", params[3].toString());
					paramsMap.put("termType", params[4].toString());
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
						result = getdatatool.getDataAsString(methodName, paramsMap);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.i("error", "teach_midAndFinalTermDate_insert()...出错了。。。");
						e.printStackTrace();
					}		
					
					return result;
				}

				@Override
				protected void onPostExecute(String result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					refreshView.dismiss();
					try{ 
						if(result.length()>0 && Integer.valueOf(result)>0){
							//设置成功则重新查询							
							teach_chartCurve_classByTerm(classID,subjectID,KnowledgePointActivity.this.termID,
									KnowledgePointActivity.this.termType,caredClassID,	KnowledgePointActivity.this.grade);
						}else{
							showToast("设置失败");
						}
					}catch (Exception ex){
						showToast(ex.getMessage().toString());
					}
					
				}
				
			}.execute(termID,  grade,  teacherID, endTime, termType);
		}
		
		// 当没有天、周、月、期中和期末单科知识点只有全科知识点时，构造空数据
		private DataTable emptySingleSubjectResult() {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			if(timeType != null && timeType.equals("天")) {
				map.put("pointCount", 0);
				map.put("rightPersent", 0);
				
			} else if(timeType != null && timeType.equals("周")) {
				map.put("rightPersent", 0);				
				map.put("pointCount", 0);					
				
			} else if(timeType != null && timeType.equals("月")) {
				map.put("pointCount", 0);	
				map.put("classRightPersent", 0);
				map.put("caredClassRightPersent", 0);
				map.put("gradeRightPersent", 0);
					
						
			} else if(timeType != null && (timeType.equals("期中") || timeType.equals("期末"))) {
				map.put("unitName", "无");						
				map.put("rightPersent", 0);
				map.put("caredClassRightPersent", 0);
				map.put("gradeRightPersent", 0);
				map.put("endTime", "未测试");
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
		
		// 当没有天、周、月、期中和期末单科知识点只有全科知识点时，构造空数据
		private DataTable emptyResult() {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			if(timeType != null && timeType.equals("天")) {
				map.put("pointCount", 0);
				map.put("rightPersent", 0);
				
			} else if(timeType != null && timeType.equals("周")) {
				map.put("rightPersent", 0);				
				map.put("pointCount", 0);					
				
			} else if(timeType != null && timeType.equals("月")) {
				map.put("pointCount", 0);	
				map.put("classRightPersent", 0);
				map.put("caredClassRightPersent", 0);
				map.put("gradeRightPersent", 0);
					
						
			} else if(timeType != null && (timeType.equals("期中") || timeType.equals("期末"))) {
				map.put("unitName", "无");				
				map.put("rightPersent", 0);
				map.put("caredClassRightPersent", 0);
				map.put("gradeRightPersent", 0);
				map.put("endTime", "未测试");
			}

			List<DataRow> listDataRow = new LinkedList<DataRow>();
			
			try {
				DataRow dr = new DataRow(map);
				listDataRow.add(dr);	
				if(timeType!=null && timeType.equals("月")){
					listDataRow.add(dr);//月知识点包括全科
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			return new DataTable(listDataRow); 
		}
		
		private void initViewAfterGetData(DataTable result) {
			// TODO Auto-generated method stub
			//清除之前数据
			if( dtKnowledgePoint!=null){
				for(int i=dtKnowledgePoint.getRowCount()-1;i>=0;i--){
					dtKnowledgePoint.deleteRow(i);
				}
			}
			
			if(result!=null){
				dtKnowledgePoint=result;
			}else{//结果为空
				dtKnowledgePoint=emptyResult();//构建空数据
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
		
		/**
		 * 自定义adapter
		 */
		private class MyAdapter extends BaseAdapter{

			private Context context;
			private LayoutInflater inflater;
			private DataTable dt;
			
			public MyAdapter(Context context,DataTable dt){
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
			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				DayAndWeekViewHolder dayAndWeekViewHolder = null;
				MonthAndTermViewHolder monthAndTermViewHolder = null;
				
				//converView为空，新建 viewHolder
				if(convertView == null){
					//天周查询
					if(timeType != null && (timeType.equals("天") || timeType.equals("周"))){
						convertView= inflater.inflate(R.layout.item_knowledge_point_day_and_week, parent, false);
						
						dayAndWeekViewHolder = new DayAndWeekViewHolder();
						dayAndWeekViewHolder.tvOverview = (TextView)convertView.findViewById(R.id.tvOverview);
						dayAndWeekViewHolder.tvRemark = (TextView)convertView.findViewById(R.id.tvRemark);
						
						dayAndWeekViewHolder.llArc1 = (LinearLayout)convertView.findViewById(R.id.llArc1);
						dayAndWeekViewHolder.llArc2 = (LinearLayout)convertView.findViewById(R.id.llArc2);
						dayAndWeekViewHolder.tvDescription=(TextView)convertView.findViewById(R.id.tvDescription);						
					}else if (timeType!= null && (timeType.equals("月") || timeType.equals("期中") || timeType.equals("期末"))){
						convertView = inflater.inflate(R.layout.item_knowledge_point_month_and_term, parent, false);
						
						monthAndTermViewHolder = new MonthAndTermViewHolder();
						monthAndTermViewHolder.tvOverview = (TextView)convertView.findViewById(R.id.tvOverview);
						monthAndTermViewHolder.tvRemark = (TextView)convertView.findViewById(R.id.tvRemark);						
						monthAndTermViewHolder.llArc1 = (LinearLayout)convertView.findViewById(R.id.llArc1);
						monthAndTermViewHolder.llArc2 = (LinearLayout)convertView.findViewById(R.id.llArc2);
						monthAndTermViewHolder.llArc3 = (LinearLayout)convertView.findViewById(R.id.llArc3);
						monthAndTermViewHolder.tvDescription = (TextView)convertView.findViewById(R.id.tvDescription);
						monthAndTermViewHolder.tv21 =(TextView)convertView.findViewById(R.id.tv21);
					}
					
					// 百分比圆屏幕适配
					int screenWidthPX = ScreenUtils.getScreenWidth(context);
					float screenWidthDP = DensityUtils.px2dp(context, screenWidthPX);
					float arcDiaDP = (screenWidthDP - 48) / 3; // 左右间距和圆之间的间隙共48dp
					int arcDiaPX = DensityUtils.dp2px(getApplicationContext(), arcDiaDP);
					
					if(timeType!=null && (timeType.equals("天")||timeType.equals("周"))){
						LayoutParams llArc1Params =(LayoutParams) dayAndWeekViewHolder.llArc1.getLayoutParams();
						llArc1Params.height=arcDiaPX;
						dayAndWeekViewHolder.llArc1.setLayoutParams(llArc1Params);
						
						LayoutParams llArc2Params =(LayoutParams) dayAndWeekViewHolder.llArc2.getLayoutParams();
						llArc2Params.height=arcDiaPX;
						dayAndWeekViewHolder.llArc2.setLayoutParams(llArc2Params);
						
						convertView.setTag(dayAndWeekViewHolder);
					}else if (timeType!=null && (timeType.equals("月") || timeType.equals("期中") || timeType.equals("期末"))){
						LayoutParams llArc1Params =monthAndTermViewHolder.llArc1.getLayoutParams();
						
						llArc1Params.height = arcDiaPX;
						monthAndTermViewHolder.llArc1.setLayoutParams(llArc1Params);
						
						LayoutParams llArc2Params = (LayoutParams)monthAndTermViewHolder.llArc2.getLayoutParams();
						
						llArc2Params.height = arcDiaPX;
						monthAndTermViewHolder.llArc2.setLayoutParams(llArc2Params);
						
						LayoutParams llArc3Params = (LayoutParams)monthAndTermViewHolder.llArc3.getLayoutParams();
						
						llArc3Params.height = arcDiaPX;
						monthAndTermViewHolder.llArc3.setLayoutParams(llArc3Params);
																
						convertView.setTag(monthAndTermViewHolder);	
					}
				}
				//convertView 不为空  从convertview中获取viewHolder
				else {
					if(timeType != null && (timeType.equals("天") || timeType.equals("周"))){
						dayAndWeekViewHolder=(DayAndWeekViewHolder) convertView.getTag();
					}else if (timeType!=null &&
							(timeType.equals("月") || timeType.equals("期中") || timeType.equals("期末"))){
						monthAndTermViewHolder =(MonthAndTermViewHolder) convertView.getTag();
					}						
				}
				
				//从dataTable 中获得数据并显示在屏幕上
				if(timeType!=null && (timeType.equals("天")||timeType.equals("周"))){
					int pointCount =0;
					int rightPersent =0;
					String description="";
					
					try {
						pointCount = Integer.parseInt(dt.getCell(position, "pointCount"));
						rightPersent=Math.round(Float.parseFloat(dt.getCell(position, "rightPersent"))*100);
						
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//往界面填充数据
					dayAndWeekViewHolder.tvRemark.setText(subjectName+"   "+date);
					dayAndWeekViewHolder.llArc1.addView(new KnowledgePointCountArc(getApplicationContext(), 0xFF38B5E2, pointCount));
					dayAndWeekViewHolder.llArc2.addView(new KnowledgePointArc(getApplicationContext(), 0xFF00C54F, rightPersent));
					
					//成绩简评
					if(pointCount==0 && rightPersent==0){
						description="单科成绩简评： 无";
					}else if(rightPersent <= 100 && rightPersent > 80) {
						description = "单科成绩简评：优";
					} else if (rightPersent <= 80 && rightPersent > 60) {
						description = "单科成绩简评：良";
					} else if(rightPersent == 60) {
						description = "单科成绩简评：中";
					} else if(rightPersent < 60) {
						description = "单科成绩简评：差";
					}
					dayAndWeekViewHolder.tvDescription.setText(description);
				}else if (timeType !=null && 
						(timeType.equals("月") || timeType.equals("期中") || timeType.equals("期末"))){
					String untiName ="";  //期中期末查询时的单元名称
					int pointCount=0;
					int classRightPersent=0;
					int caredClassRightPersent =0;
					int gradeRightPersent=0;
					String unitName="";
					String endTime ="";	
					int unitCount=0;
					String description="";
					
					try {
						if(timeType.equals("月")){
							pointCount = Integer.parseInt(dt.getCell(position, "pointCount"));
							classRightPersent=Math.round(Float.parseFloat(dt.getCell(position, "classRightPersent"))*100);
							
							
						}else if (timeType.equals("期中") || timeType.equals("期末")){
							unitName=dt.getCell(position, "unitName");
							classRightPersent=Math.round(Float.parseFloat(dt.getCell(position, "rightPersent"))*100);
							endTime=dt.getCell(position, "endTime");
							unitCount=dt.getRowCount();
						}
						caredClassRightPersent=Math.round(Float.parseFloat(dt.getCell(position, "caredClassRightPersent"))*100);
						gradeRightPersent=Math.round(Float.parseFloat(dt.getCell(position, "gradeRightPersent"))*100);
						
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					//界面展示
					if(timeType.equals("月")){
						if(caredClassName!=null && caredClassName.trim()!="")
						monthAndTermViewHolder.tv21.setText(caredClassName.trim());
						if(position ==0 ){//单科
							monthAndTermViewHolder.tvOverview.setText("本科知识点");
							monthAndTermViewHolder.tvRemark.setText("（知识点数量：  "+pointCount+"）");
							
							
						} else if(position==1){//全科
							monthAndTermViewHolder.tvOverview.setText("全科知识点");
							monthAndTermViewHolder.tvRemark.setText("（知识点数量：  "+pointCount+"）");
							
						}
						
						//成绩简评
						if(pointCount==0 && classRightPersent==0){
							description="成绩简评： 无";
						}else if(classRightPersent <= 100 && classRightPersent > 80) {
							description = "成绩简评：优";
						} else if (classRightPersent <= 80 && classRightPersent > 60) {
							description = "成绩简评：良";
						} else if(classRightPersent == 60) {
							description = "成绩简评：中";
						} else if(classRightPersent < 60) {
							description = "成绩简评：差";
						}
						monthAndTermViewHolder.tvDescription.setText(description);
					}else if (timeType.equals("期中") || timeType.equals("期末")){
						monthAndTermViewHolder.tvOverview.setText(unitName);
						monthAndTermViewHolder.tvDescription.setVisibility(View.GONE);
						if(unitName != null && unitName.length() > 0 && unitName.equals("无")){
							monthAndTermViewHolder.tvRemark.setText("单元数：无" + "   截至日期：" + endTime);							
						}else{
							monthAndTermViewHolder.tvRemark.setText("单元数：  " + unitCount+ "   截至日期：" + endTime);		
							if(caredClassName!=null && caredClassName.trim()!="")
							monthAndTermViewHolder.tv21.setText(caredClassName.trim());
						}
						
					}
					
					monthAndTermViewHolder.llArc1.addView(new KnowledgePointArc(getApplicationContext(), 0xFF38B5E2, classRightPersent));
					monthAndTermViewHolder.llArc2.addView(new KnowledgePointArc(getApplicationContext(), 0xFF00C54F, caredClassRightPersent));
					monthAndTermViewHolder.llArc3.addView(new KnowledgePointArc(getApplicationContext(), 0xFFFF4F4D, gradeRightPersent));																
				}
				
				return convertView;
				
			}
			
		}
		
		/**
		 * 天和周的view
		 */
		public static class DayAndWeekViewHolder{
			TextView tvOverview;
			TextView tvRemark;
			LinearLayout llArc1;
			LinearLayout llArc2;
			TextView tvDescription;//简评
		}
		
		/**
		 * 月 期中 期末 view
		 */
		public static class MonthAndTermViewHolder{
			TextView tvOverview;
			TextView tvRemark;
			LinearLayout llArc1;
			LinearLayout llArc2;
			LinearLayout llArc3;
			TextView tvDescription;//简评
			TextView tv21; //关注班级均值
		}
		
		//当未设置期中期末时间时弹出对话框，询问是否设置时间
		public void initReminderDialog(String title, String content, String ikonw,String cancel){
			if (this.reminderDialog == null) {
				View view = LayoutInflater.from(KnowledgePointActivity.this).inflate(
						R.layout.dialog_reminder_authority_use, null);
				this.reminderDialog = new Dialog(KnowledgePointActivity.this,
						R.style.alertdialog_style);
				this.reminderDialog.setContentView(view);
				this.reminderDialog.show();
				
				((TextView)view.findViewById(R.id.titleReminder_textView)).setText(title);
				((TextView)view.findViewById(R.id.reminderInfo_textView)).setText(content);
				((TextView)view.findViewById(R.id.IKnow_textView)).setText(ikonw);
				if(cancel.equals("")){
					((TextView)view.findViewById(R.id.tv_cancel)).setVisibility(View.GONE);
				}else{
					((TextView)view.findViewById(R.id.tv_cancel)).setText(cancel);
				}
					

				// 动态设置界面图片大小；
				DisplayMetrics metrics = getResources().getDisplayMetrics(); // 用来获取屏幕的分辨率；
				int screenWidthPX = metrics.widthPixels;
				int screenWidthDP = (int) DensityUtils.px2dp(KnowledgePointActivity.this,
						screenWidthPX);

				// 设置图片的大小；
				int layoutWidthDP = (screenWidthDP * 4) / 5;
				int layoutWidthPX = DensityUtils
						.dp2px(KnowledgePointActivity.this, layoutWidthDP);

				LayoutParams params = view.getLayoutParams();
				params.width = layoutWidthPX;
				view.setLayoutParams(params);

				// 添加点击事件
				view.findViewById(R.id.IKnow_textView).setOnClickListener(
					new OnClickListener(){

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							reminderDialog.dismiss();
							showatePicker();
						}						
					});		
				
				view.findViewById(R.id.tv_cancel).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						reminderDialog.dismiss();
					}
				});

				} else {
					this.reminderDialog.show();
			}
		}
		
		//设置考试时间
		public void showatePicker(){
			
			Calendar cal = Calendar.getInstance();
			MyDatePickerDialog dialog = new MyDatePickerDialog(KnowledgePointActivity.this, new MyDatePickerDialog.OnDateSetListener() {
				
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
					String dateStr = String.valueOf(year)+"-"+mm+"-"+dd;
					
					teachMidAndFinalTermDateInsert(termID, grade, TeacherInfo.getInstance().execTeacherID, dateStr,termType);
					//showToast(dateStr);
				}
			}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
			dialog.setTitle("请设置学校考试时间");
			dialog.show();
		}
		
		//重新 datePickerDialog 方法，使点击区域外面时不等同于选择时间
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
}
