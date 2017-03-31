package com.ttqeducation.activitys.study;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.ttqeducation.R;
import com.ttqeducation.R.color;
import com.ttqeducation.R.drawable;
import com.ttqeducation.R.id;
import com.ttqeducation.R.layout;
import com.ttqeducation.R.style;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DensityUtils;
import com.ttqeducation.tools.DesUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class KnowledgePointGraphActivity extends Activity {

	// 标题栏部分
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null; // 标题栏文字；
	
	private LinearLayout llChartCurveTypeByMonth = null;
	private LinearLayout llSingleSubject = null;
	private LinearLayout llSingleSubjectImage = null;
	private TextView tvSingleSubjectText = null;
	private LinearLayout llAllSubject = null;
	private LinearLayout llAllSubjectImage = null;
	private TextView tvAllSubjectText = null;
	
	private RefreshView refreshView = null;
	
	private String timeType = "";
	private String chartCurveTypeByMonth = "单科";
	
	private String studentID = "";
	private String subjectID = "";
	private String termID = "";
	private String classID = "";
	private int grade = 0;
	private int termType = 0;
	
	private List<DataTable> dtListChartCurve;
	
	// achartengine曲线图
	private RelativeLayout static_chart_line_layout;

    // 用于存放每条折线的点数据
    private XYSeries line1, line2, line3;
    // 用于存放所有需要绘制的XYSeries
    private XYMultipleSeriesDataset mDataset;
    // 用于存放每条折线的风格
    private XYSeriesRenderer renderer1, renderer2, renderer3;
    // 用于存放所有需要绘制的折线的风格
    private XYMultipleSeriesRenderer mXYMultipleSeriesRenderer;
    private GraphicalView chart;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_knowledge_point_graph);
		
		initView();
		
		getDataFromIntent();						
	}
	
	private void initView() {
		// 标题栏部分实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("曲线对比图");
		
		// 返回
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				KnowledgePointGraphActivity.this.finish();
			}
		});
		
		llChartCurveTypeByMonth = (LinearLayout)findViewById(R.id.llChartCurveTypeByMonth);
		llSingleSubject = (LinearLayout)findViewById(R.id.llSingleSubject);
		llSingleSubjectImage = (LinearLayout)findViewById(R.id.llSingleSubjectImage);
		tvSingleSubjectText = (TextView)findViewById(R.id.tvSingleSubjectText);
		llAllSubject = (LinearLayout)findViewById(R.id.llAllSubject);
		llAllSubjectImage = (LinearLayout)findViewById(R.id.llAllSubjectImage);
		tvAllSubjectText = (TextView)findViewById(R.id.tvAllSubjectText);
		
		llSingleSubject.setOnClickListener(BtnOnClickedListener);
		llAllSubject.setOnClickListener(BtnOnClickedListener);
		
		static_chart_line_layout = (RelativeLayout) findViewById(R.id.static_chart_line_layout);
	}
	
	private OnClickListener BtnOnClickedListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch(id) {
			case R.id.llSingleSubject:
				if(chartCurveTypeByMonth != null && chartCurveTypeByMonth.length() > 0
					&& chartCurveTypeByMonth.equals("全科")) {
					clearSelections();
					chartCurveTypeByMonth = "单科";
					tvSingleSubjectText.setTextColor(getResources().getColor(R.color.textWhite));
					llSingleSubjectImage.setBackgroundResource(R.drawable.btn_circle_red_round);
					
					chartCurveByMonth(studentID, subjectID, termID, classID);
				}
				break;
				
			case R.id.llAllSubject:
				if(chartCurveTypeByMonth != null && chartCurveTypeByMonth.length() > 0
				&& chartCurveTypeByMonth.equals("单科")) {
					clearSelections();
					chartCurveTypeByMonth = "全科";
					tvAllSubjectText.setTextColor(getResources().getColor(R.color.textWhite));
					llAllSubjectImage.setBackgroundResource(R.drawable.btn_circle_red_round);
					
				allSubjectChartCurveByMonth(studentID, termID, classID, grade);
			}
				break;
				
			default:
				break;
			}
		}			
	};
	
	// 恢复所有为灰色；
	public void clearSelections() {
		// 文字背景圆角恢复；
		llSingleSubjectImage.setBackgroundResource(R.drawable.linearlayout_frame_circle_gray);
		llAllSubjectImage.setBackgroundResource(R.drawable.linearlayout_frame_circle_gray);

		// 文字的恢复；
		tvSingleSubjectText.setTextColor(getResources().getColor(
				R.color.textGray));
		tvAllSubjectText.setTextColor(getResources()
				.getColor(R.color.textGray));
	}	
	
	private void getDataFromIntent() {
		Intent intent = getIntent();
		timeType = intent.getStringExtra("timeType");
		
		if(timeType != null && timeType.length() > 0 && timeType.equals("周")) {
			studentID = intent.getStringExtra("studentID");
			subjectID = intent.getStringExtra("subjectID");
			termID = intent.getStringExtra("termID");
			classID = intent.getStringExtra("classID");
			
			chartCurveByWeek(studentID, subjectID , termID, classID);
						
		} else if(timeType != null && timeType.length() > 0 && timeType.equals("月")) {
			studentID = intent.getStringExtra("studentID");
			subjectID = intent.getStringExtra("subjectID");
			termID = intent.getStringExtra("termID");
			classID = intent.getStringExtra("classID");
			grade = intent.getIntExtra("grade", 0);
			
			llChartCurveTypeByMonth.setVisibility(View.VISIBLE);
			chartCurveByMonth(studentID, subjectID, termID, classID);
			
		} else if(timeType != null && timeType.length() > 0 && (timeType.equals("期中") || timeType.equals("期末"))) {
			studentID = intent.getStringExtra("studentID");
			classID = intent.getStringExtra("classID");
			subjectID = intent.getStringExtra("subjectID");
			termID = intent.getStringExtra("termID");
			termType = intent.getIntExtra("termType", 0);
			grade = intent.getIntExtra("grade", 0);
			
			chartCurveByTerm(studentID, classID, subjectID , termID, termType, grade);		
		}
	}	
	
	/**
	 * 周曲线比对图3条曲线：1、学生个人单科评均； 2、班级单科平均； 3、个人全科。
	 *
	 * @param studentID
	 * @param subjectID
	 * @param termID
	 * @param classID
	 */
	private void chartCurveByWeek(String studentID, String subjectID , String termID, String classID) {
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, List<DataTable>>() {
			@Override
			
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected List<DataTable> doInBackground(Object... params) {
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				List<DataTable> dtList = new ArrayList<DataTable>();
				DataTable dtPerson = new DataTable(); // 截止到某一周个人单科掌握度
				DataTable dtClass = new DataTable(); // 截止到某一周某个班级单科掌握度
				DataTable dtAll = new DataTable(); // 截止到某一周个人全科掌握度
				
				// 方法名
				String methodName = "teach_chartCurve_studentByWeek";
				
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
					dtPerson = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "teach_chartCurve_studentByWeek...出错了。。。");
					e.printStackTrace();
				}
				
				if(dtPerson != null && dtPerson.getRowCount() > 0) {
					dtList.add(dtPerson);
				}
				
				// 获取截止到某一周某个班级单科掌握度
				methodName = "teach_chartCurve_classByWeek";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				paramsMap.remove("studentID");
				paramsMap.remove("TokenID");
				paramsMap.put("classID", params[3].toString());							
				paramsMap.put("TokenID", tokenID);
				
				try {
					dtClass = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "teach_chartCurve_classByWeek...出错了。。。");
					e.printStackTrace();
				}
								
				if(dtClass != null && dtClass.getRowCount() > 0) {
					dtList.add(dtClass);
				}
				
				// 获取截止到某一周个人全科掌握度
				methodName = "teach_chartCurve_studentByWeek";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				paramsMap.remove("classID");
				paramsMap.remove("subjectID");
				paramsMap.remove("TokenID");
				paramsMap.put("studentID", params[0].toString());
				paramsMap.put("subjectID", "0");
				paramsMap.put("TokenID", tokenID);
				
				try {
					dtAll = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "teach_chartCurve_studentByWeek...出错了。。。");
					e.printStackTrace();
				}
							
				if(dtAll != null && dtAll.getRowCount() > 0) {
					dtList.add(dtAll);
				}
									
				return dtList;
			}

			protected void onPostExecute(List<DataTable> result) {
				
				// 把获取到的数据写入界面
				initViewAfterGetData(result);
				
				// 关闭刷新；
				refreshView.dismiss();											
			};
		}.execute(studentID, subjectID , termID, classID);		
	}
	
	/**
	 * 月曲线比对，单科图3条曲线：1、学生个人单科平均； 2、学生个人全科； 3、班级单科平均。
	 * 
	 * @param studentID
	 * @param subjectID
	 * @param termID
	 * @param classID
	 * @param grade
	 */
	private void chartCurveByMonth(String studentID, String subjectID, String termID, String classID) {
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, List<DataTable>>() {
			@Override
			
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected List<DataTable> doInBackground(Object... params) {
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				List<DataTable> dtList = new ArrayList<DataTable>();
				DataTable dtPerson = new DataTable(); // 截止到某一月个人单科掌握度
				DataTable dtPersonAll = new DataTable(); // 截止到某一月个人全科掌握度
				DataTable dtClass = new DataTable(); // 截止到某一月班级单科掌握度
								
				// 方法名，截止到某一月个人单科掌握度
				String methodName = "teach_chartCurve_studentByMonth";
				
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
					dtPerson = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "teach_chartCurve_studentByMonth...出错了。。。");
					e.printStackTrace();
				}
				
				if(dtPerson != null && dtPerson.getRowCount() > 0 ) {
					dtList.add(dtPerson);
				}
								
				// 获取截止到某一月个人全科掌握度
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// 把subjectID改成0（全科），更新TokenID
				paramsMap.remove("subjectID");
				paramsMap.remove("TokenID");
				paramsMap.put("subjectID", "0");
				paramsMap.put("TokenID", tokenID);
				
				try {
					dtPersonAll = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "teach_chartCurve_studentByMonth...出错了。。。");
					e.printStackTrace();
				}				
				
				if(dtPersonAll != null && dtPersonAll.getRowCount() > 0) {
					dtList.add(dtPersonAll);
				}
					
				// 获取截止到某一月某个班级单科掌握度
				methodName = "teach_chartCurve_classByMonth";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// 把studentID变成classID，subjectID改成单科，更新TikenID
				paramsMap.remove("studentID");
				paramsMap.remove("subjectID");
				paramsMap.remove("TokenID");
				paramsMap.put("classID", params[3].toString());
				paramsMap.put("subjectID", params[1].toString());
				paramsMap.put("TokenID", tokenID);
				
				try {
					dtClass = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "teach_chartCurve_classByMonth...出错了。。。");
					e.printStackTrace();
				}
								
				if(dtClass != null && dtClass.getRowCount() > 0) {
					dtList.add(dtClass);
				}																					
				
				return dtList;
			}

			protected void onPostExecute(List<DataTable> result) {
				
				// 把获取到的数据写入界面
				initViewAfterGetData(result);
				
				// 关闭刷新；
				refreshView.dismiss();											
			};
		}.execute(studentID, subjectID , termID, classID);		
	}
	
	/**
	 * 月曲线比对，综合科目图3条曲线：1、学生综合科目平均； 2、班级综合科目平均； 3、年级综合科目平均。
	 * 
	 * @param studentID
	 * @param subjectID
	 * @param termID
	 * @param classID
	 * @param grade
	 */
	private void allSubjectChartCurveByMonth(String studentID, String termID, String classID, int grade) {
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, List<DataTable>>() {
			@Override
			
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected List<DataTable> doInBackground(Object... params) {
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				List<DataTable> dtList = new ArrayList<DataTable>();
				DataTable dtPersonAll = new DataTable(); // 截止到某一月个人全科掌握度
				DataTable dtClassAll = new DataTable(); // 截止到某一月某个班级全科掌握度
				DataTable dtGradeAll = new DataTable(); // 截止到某一月年级全科科掌握度
								
				// 方法名，截止到某一月个人单科掌握度
				String methodName = "teach_chartCurve_studentByMonth";
				
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
				paramsMap.put("subjectID", "0");
				paramsMap.put("termID", params[1].toString());							
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
					dtPersonAll = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "teach_chartCurve_studentByMonth...出错了。。。");
					e.printStackTrace();
				}
				
				if(dtPersonAll != null && dtPersonAll.getRowCount() > 0 ) {
					dtList.add(dtPersonAll);
				}												
																	
				// 获取截止到某一周某个班级全科掌握度
				methodName = "teach_chartCurve_classByMonth";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// 把studentID变成classID，更新TikenID
				paramsMap.remove("studentID");
				paramsMap.remove("TokenID");
				paramsMap.put("classID", params[2].toString());
				paramsMap.put("TokenID", tokenID);
				
				try {
					dtClassAll = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "teach_chartCurve_classByMonth...出错了。。。");
					e.printStackTrace();
				}
								
				if(dtClassAll != null && dtClassAll.getRowCount() > 0) {
					dtList.add(dtClassAll);
				}					
				
				// 获取截止到某一月年级全科掌握度
				methodName = "teach_chartCurve_gradeByMonth";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				paramsMap.remove("classID");
				paramsMap.remove("TokenID");
				paramsMap.put("grade", params[3].toString());
				paramsMap.put("TokenID", tokenID);
				
				try {
					dtGradeAll = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "teach_chartCurve_gradeByMonth...出错了。。。");
					e.printStackTrace();
				}
				
				if(dtGradeAll != null && dtGradeAll.getRowCount() > 0) {
					dtList.add(dtGradeAll);
				}
				
				return dtList;
			}

			protected void onPostExecute(List<DataTable> result) {
				
				// 把获取到的数据写入界面
				initViewAfterGetData(result);
				
				// 关闭刷新；
				refreshView.dismiss();											
			};
		}.execute(studentID, termID, classID, grade);		
	}	
	
	/**
	 * 截止期中/期末曲线比对图3条曲线：1、学生各单元平均值对比曲线； 2、各单元班级平均比对曲线； 3、各单元年级平均值比对曲线。
	 * 
	 * @param studentID
	 * @param classID
	 * @param subjectID
	 * @param termID
	 * @param termType
	 * @param grade
	 */
	private void chartCurveByTerm(String studentID, String classID, String subjectID , String termID, int termType, int grade) {
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, List<DataTable>>() {
			@Override
			
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected List<DataTable> doInBackground(Object... params) {
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				List<DataTable> dtList = new ArrayList<DataTable>();
				// 用模拟的数据库表存储返回结果
				DataTable dt = new DataTable();
				
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
					dt = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "chartCurveByTerm()...出错了。。。");
					e.printStackTrace();
				}								
				
				if(dt != null && dt.getRowCount() > 0) {
					dtList.add(dt);
				}
				
				return dtList;
			}

			protected void onPostExecute(List<DataTable> result) {
				
				// 把获取到的数据写入界面
				initViewAfterGetData(result);
				
				// 关闭刷新；
				refreshView.dismiss();											
			};
		}.execute(studentID, classID, subjectID , termID, termType, grade);		
	}
	
	// 获取数据后展示到界面上
	private void initViewAfterGetData(List<DataTable> result) {
		// 清空数据
		if(dtListChartCurve != null) {
			dtListChartCurve.clear();
		}
		
		// 获取完数据后，初始化界面				
		if(result != null && result.size() > 0) { // 结果不为空
			dtListChartCurve = result;
			
			// 生成曲线图
			if(timeType != null && timeType.length() > 0 && timeType.equals("周")
					&& dtListChartCurve.size() == 3) {
				// 周曲线对比图
				initChart("学生单科", "班级单科", "学生全科");
			} else if(timeType != null && timeType.length() > 0 && timeType.equals("月")
					&& dtListChartCurve.size() == 3) {
				if(chartCurveTypeByMonth != null && chartCurveTypeByMonth.length() > 0 
						&& chartCurveTypeByMonth.equals("单科")) {
					// 月曲线对比图（单科目）
					initChart("学生单科", "学生全科", "班级单科");
				} else if(chartCurveTypeByMonth != null && chartCurveTypeByMonth.length() > 0 
						&& chartCurveTypeByMonth.equals("全科")) {
					// 月曲线对比图（全科目）
					initChart("学生全科", "班级全科", "年级全科");
				}
			} else if (timeType != null && timeType.length() > 0 
					&& (timeType.equals("期中") || timeType.equals("期末"))
					&& dtListChartCurve.size() == 1) {
				// 期中、期末曲线对比图
				initChart("学生单科", "班级单科", "年级单科");
			}
			
		} else { 		
			// 结果为空
		}								
	}
	
	private void initChart(String line1Name, String line2Name, String line3Name) {
        // 初始化，必须保证XYMultipleSeriesDataset对象中的XYSeries数量和
        // XYMultipleSeriesRenderer对象中的XYSeriesRenderer数量一样多
        line1 = new XYSeries(line1Name);
        line2 = new XYSeries(line2Name);
        line3 = new XYSeries(line3Name);
        renderer1 = new XYSeriesRenderer();
        renderer2 = new XYSeriesRenderer();
        renderer3 = new XYSeriesRenderer();
        mDataset = new XYMultipleSeriesDataset();
        mXYMultipleSeriesRenderer = new XYMultipleSeriesRenderer();

        //对XYSeries和XYSeriesRenderer的对象的参数赋值
        initLine(line1);
        initLine(line2);
        initLine(line3);
        initRenderer(renderer1, Color.BLUE, PointStyle.CIRCLE, true);
        initRenderer(renderer2, Color.RED, PointStyle.TRIANGLE, true);
        initRenderer(renderer3, Color.GREEN, PointStyle.DIAMOND, true);

        //将XYSeries对象和XYSeriesRenderer对象分别添加到XYMultipleSeriesDataset对象和XYMultipleSeriesRenderer对象中。
        mDataset.addSeries(line1);
        mDataset.addSeries(line2);
        mDataset.addSeries(line3);
        mXYMultipleSeriesRenderer.addSeriesRenderer(renderer1);
        mXYMultipleSeriesRenderer.addSeriesRenderer(renderer2);
        mXYMultipleSeriesRenderer.addSeriesRenderer(renderer3);

        //配置chart参数
        int i = dtListChartCurve.get(0).getRowCount();
        
        setChartSettings(mXYMultipleSeriesRenderer, 0, i, 0, 100, Color.BLACK,
                Color.BLACK);

        //通过该函数获取到一个View 对象
        chart = ChartFactory.getLineChartView(this, mDataset, mXYMultipleSeriesRenderer);

        //将该View 对象添加到layout中
        static_chart_line_layout.removeAllViews();
        static_chart_line_layout.addView(chart, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
    }

    private void initLine(XYSeries series) {
    	
    	if(timeType != null && timeType.length() > 0 
    			&& (timeType.equals("期中") || timeType.equals("期末"))) {
    		String type = "";
    		if(series.equals(line1)) {
	        	type = "rightPersent";
	        } else if (series.equals(line2)) {
	        	type = "classRightPersent";
	        } else if (series.equals(line3)) {
	        	type = "gradeRightPersent";
	        }
    		
    		DataTable dt = dtListChartCurve.get(0);
	    	for(int i = 0; i < dt.getRowCount(); i++) {
	    		try {
					series.add(i + 1, (Double.parseDouble(dt.getCell(i, type))) * 100);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
    	} else if(timeType != null && timeType.length() > 0 
    			&& (timeType.equals("周") || timeType.equals("月"))){
    		
	    	int i = 0;
	    	
	        if(series.equals(line1)) {
	        	i = 0;
	        } else if (series.equals(line2)) {
	        	i = 1;
	        } else if (series.equals(line3)) {
	        	i = 2;
	        } 
	        
	        DataTable dt = dtListChartCurve.get(i);
	    	for(int j = 0; j < dt.getRowCount(); j++) {
	    		try {
					series.add(j + 1, (Double.parseDouble(dt.getCell(j, "rightPercent"))) * 100);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
    	}
    }

    private XYSeriesRenderer initRenderer(XYSeriesRenderer renderer, int color,
            PointStyle style, boolean fill) {
        // 设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
        renderer.setColor(color);
        renderer.setPointStyle(style);
        renderer.setFillPoints(fill);
        renderer.setLineWidth(2);
        return renderer;
    }

    protected void setChartSettings(XYMultipleSeriesRenderer mXYMultipleSeriesRenderer,
            double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
        
    	mXYMultipleSeriesRenderer.setApplyBackgroundColor(true);
    	mXYMultipleSeriesRenderer.setBackgroundColor(Color.argb(255, 239, 239, 239));
        mXYMultipleSeriesRenderer.setXAxisMin(xMin);
        // mXYMultipleSeriesRenderer.setAxisTitleTextSize(25); // 设置坐标轴标题文本大小        
        int textSizeWithPX = 0;		        
        // mXYMultipleSeriesRenderer.setXAxisMax(xMax);
        if(timeType != null && timeType.length() > 0
        		&& (timeType.equals("期中") || timeType.equals("期末"))){
        	textSizeWithPX = DensityUtils.dp2px(getApplicationContext(), 9);
        	if(xMax <= 3) {
        		mXYMultipleSeriesRenderer.setXAxisMax(xMax);
        	} else {
        		mXYMultipleSeriesRenderer.setXAxisMax(3);
        	}
        } else {
        	textSizeWithPX = DensityUtils.dp2px(getApplicationContext(), 12);
        	if(xMax <= 10) {
        		mXYMultipleSeriesRenderer.setXAxisMax(xMax);
        	} else {
        		mXYMultipleSeriesRenderer.setXAxisMax(10);
        	}
        }
        mXYMultipleSeriesRenderer.setLabelsTextSize(textSizeWithPX); // 设置数轴的文字大小
        mXYMultipleSeriesRenderer.setYAxisMin(yMin);
        mXYMultipleSeriesRenderer.setYAxisMax(yMax + 10);
        mXYMultipleSeriesRenderer.setAxesColor(axesColor);
        mXYMultipleSeriesRenderer.setLabelsColor(labelsColor);
        mXYMultipleSeriesRenderer.setXLabelsColor(labelsColor);
        mXYMultipleSeriesRenderer.setYLabelsColor(0, labelsColor);
        mXYMultipleSeriesRenderer.setShowGrid(true);
        mXYMultipleSeriesRenderer.setGridColor(Color.argb(255, 174, 174, 174));
        mXYMultipleSeriesRenderer.setShowCustomTextGrid(false);
        
        for(int x = 1; x <= xMax; x++) {
        	try {
        		if (timeType != null && timeType.length() > 0 && timeType.equals("周")) {
        			mXYMultipleSeriesRenderer.addXTextLabel(x, dtListChartCurve.get(0).getCell(x - 1, "weekNum"));
        		} else if(timeType != null && timeType.length() > 0 
        				&& timeType.equals("月")) {
        			mXYMultipleSeriesRenderer.addXTextLabel(x, dtListChartCurve.get(0).getCell(x - 1, "monthes") + "月");
        		 } else if(timeType != null && timeType.length() > 0 
         				&& (timeType.equals("期中") || timeType.equals("期末"))) {
         			mXYMultipleSeriesRenderer.addXTextLabel(x, dtListChartCurve.get(0).getCell(x - 1, "unitName"));
         		}			
			} catch (dataTableWrongException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        mXYMultipleSeriesRenderer.setXLabels(0);
        // mXYMultipleSeriesRenderer.setXLabelsAngle(45);
        
        for(int y = 0; y <= 10; y++) {
        	mXYMultipleSeriesRenderer.addYTextLabel(y * 10, y * 10 + "%");
        }       
        mXYMultipleSeriesRenderer.setYLabels(0);
        
        mXYMultipleSeriesRenderer.setYLabelsAlign(Align.RIGHT);
        mXYMultipleSeriesRenderer.setPointSize((float) 5);
        mXYMultipleSeriesRenderer.setShowLegend(true);
        mXYMultipleSeriesRenderer.setLegendTextSize(textSizeWithPX);
        // mXYMultipleSeriesRenderer.setFitLegend(true);
        mXYMultipleSeriesRenderer.setMarginsColor(Color.argb(255, 239, 239, 239));
        mXYMultipleSeriesRenderer.setMargins(new int[]{textSizeWithPX, (int)(textSizeWithPX * 2.5), (int)(textSizeWithPX * 1.5), 0});
        mXYMultipleSeriesRenderer.setZoomEnabled(false, false);
        mXYMultipleSeriesRenderer.setPanEnabled(true, true);
        mXYMultipleSeriesRenderer.setPanLimits(new double[]{0, xMax + 1, 0, yMax + 1});
    }
}