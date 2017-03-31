package com.ttqeducation.teacher.activitys.teach;

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

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.activitys.teach.KnowledgePointActivity.MyOnClickListener;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.tools.DesUtil;
import com.ttqeducation.teacher.tools.DensityUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class KnowledgePointGraph extends Activity {

	//标题栏部分
	private LinearLayout titleBackLayout = null;
	private TextView titleTextView = null;
	private LinearLayout llChartCurveTypeByMonth = null;
	private LinearLayout llSingleSubject = null;
	private LinearLayout llSingleSubjectImage = null;
	private TextView tvSingleSubjectText = null;
	private LinearLayout llAllSubject = null;
	private LinearLayout llAllSubjectImage = null;
	private TextView tvAllSubjectText= null;
	private LinearLayout llNoData = null;
	private RefreshView refreshView = null;
	
	
	private String timeType="";
	private String classID="";
	private String subjectID="";
	private String termID="";
	private int termType=0;
	private String caredClassID="";
	private int grade =0;
	private int chartType = 0;//标明具体是那种曲线界面
	//存放各个曲线图的原始数据
	private List<DataTable> dtListChartCurve;
	private String chartCurveTypeByMonth="单科";
	
	//曲线图
	private RelativeLayout chart_line_layout;
	// 用于存放每条折线的点数据
	private XYSeries line1,line2,line3;
	// 用于存放所有需要绘制的XYSeries
    private XYMultipleSeriesDataset mDataset;
    // 用于存放每条折线的风格
    private XYSeriesRenderer renderer1, renderer2, renderer3;
    // 用于存放所有需要绘制的折线的风格
    private XYMultipleSeriesRenderer mXYMultipleSeriesRenderer;
    private GraphicalView chart;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_knowledge_point_graph);
		initView();
		getDataFromIntent();
		
		
	}

	private void initView() {
		// TODO Auto-generated method stub
		//标题栏部分实例化；
		this.titleBackLayout=(LinearLayout) (super.findViewById(R.id.action_bar)
				.findViewById(R.id.title_back_layout));
		this.titleTextView = (TextView)(super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("曲线对比图");
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				KnowledgePointGraph.this.finish();
			}
		});
		
		llChartCurveTypeByMonth = (LinearLayout)findViewById(R.id.llChartCurveTypeByMonth);
		llSingleSubject = (LinearLayout)findViewById(R.id.llSingleSubject);
		llSingleSubjectImage = (LinearLayout)findViewById(R.id.llSingleSubjectImage);
		tvSingleSubjectText = (TextView)findViewById(R.id.tvSingleSubjectText);
		llAllSubject = (LinearLayout)findViewById(R.id.llAllSubject);
		llAllSubjectImage = (LinearLayout)findViewById(R.id.llAllSubjectImage);
		tvAllSubjectText = (TextView)findViewById(R.id.tvAllSubjectText);
		
		llSingleSubject.setOnClickListener(BtnOnclickListener);
		llAllSubject.setOnClickListener(BtnOnclickListener);
		
		chart_line_layout = (RelativeLayout) findViewById(R.id.chart_line_layout);
		llNoData = (LinearLayout)findViewById(R.id.llNoData);
	}

	private void getDataFromIntent() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		timeType = intent.getStringExtra("timeType");
		
		if(timeType!= null && timeType.length()>0 && timeType.equals("周")){
			classID=intent.getStringExtra("classID");
			subjectID=intent.getStringExtra("subjectID");
			termID= intent.getStringExtra("termID");
			this.llChartCurveTypeByMonth.setVisibility(View.GONE);
			chartCurveByWeek(classID,subjectID ,termID);
		} else if(timeType!= null && timeType.length()>0 && timeType.equals("月")){
			classID=intent.getStringExtra("classID");
			subjectID=intent.getStringExtra("subjectID");
			termID= intent.getStringExtra("termID");
			caredClassID= intent.getStringExtra("caredClassID");
			grade= intent.getIntExtra("grade", 0);
			this.llChartCurveTypeByMonth.setVisibility(View.VISIBLE);
			chartCurveByMonth(classID,subjectID,termID,caredClassID,String.valueOf(grade) );
		} else if(timeType!= null && timeType.length()>0 && (timeType.equals("期中")||timeType.equals("期末"))){
			classID=intent.getStringExtra("classID");
			subjectID=intent.getStringExtra("subjectID");
			termID= intent.getStringExtra("termID");
			caredClassID= intent.getStringExtra("caredClassID");
			grade= intent.getIntExtra("grade", 0);
			termType= intent.getIntExtra("termType", 0);
			this.llChartCurveTypeByMonth.setVisibility(View.GONE);
			chartCurveByTerm(classID,subjectID,termID,String.valueOf(termType) ,caredClassID,String.valueOf(grade));
			
		}
	}

	

	private OnClickListener BtnOnclickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch(id){
			case R.id.llSingleSubject:
				if(chartCurveTypeByMonth != null && chartCurveTypeByMonth.length()>0 && 
					chartCurveTypeByMonth.equals("全科")){
					clearSelections();
					chartCurveTypeByMonth="单科";
					tvSingleSubjectText.setTextColor(getResources().getColor(R.color.textWhite));
					llSingleSubjectImage.setBackgroundResource(R.drawable.btn_circle_red_round);
					chartCurveByMonth(classID, subjectID, termID, caredClassID,String.valueOf(grade));
				}
				break;
			case R.id.llAllSubject:
				if(chartCurveTypeByMonth != null && chartCurveTypeByMonth.length() > 0
				&& chartCurveTypeByMonth.equals("单科")){
					clearSelections();
					chartCurveTypeByMonth ="全科";
					tvAllSubjectText.setTextColor(getResources().getColor( R.color.textWhite));
					llAllSubjectImage.setBackgroundResource(R.drawable.btn_circle_red_round);
					chartCurveByMonth(classID, "0", termID, caredClassID,String.valueOf(grade) );
				}
				break;
			default:
				break;
			}
		}
	};
	
	//回复所有灰色
	public void clearSelections(){
		//文字背景圆角恢复；
		llSingleSubjectImage.setBackgroundResource(R.drawable.linearlayout_frame_circle_gray);
		llAllSubjectImage.setBackgroundResource(R.drawable.linearlayout_frame_circle_gray);
		//文字的恢复
		tvSingleSubjectText.setTextColor(getResources().getColor(R.color.textGray));
		tvAllSubjectText.setTextColor(getResources().getColor(R.color.textGray));
	}
	
	/**
	 * 周曲线图，只包括一个数据：班级单科平均值
	 * @param classID
	 * @param subjectID
	 * @param termID
	 */
	private void chartCurveByWeek(String classID, String subjectID, String termID) {
		// TODO Auto-generated method stub
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		
		//异步访问网络数据
		new AsyncTask<Object, Object,List< DataTable>>() {
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected List< DataTable> doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				//存储结果
				List< DataTable> dtList = new ArrayList<DataTable>();
				DataTable dtClass = new DataTable();//班级单科周掌握度
				
				//方法名
				String methodName = "teach_chartCurve_classByWeek";
				Map<String,String> paramsMap= new HashMap<String, String>();
				String tokenID ="";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("classID", params[0].toString());
				paramsMap.put("subjectID", params[1].toString());
				paramsMap.put("termID",params[2].toString());
				paramsMap.put("TokenID",tokenID);
				
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
					dtClass = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "teach_chartCurve_classByWeek()...出错了。。。");
					e.printStackTrace();
				}
				
				if(dtClass !=null && dtClass.getRowCount()>0){
					dtList.add(dtClass);
				}
				return dtList;
			}

			@Override
			protected void onPostExecute(List<DataTable>  result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				initViewAfterGetData(result);
				refreshView.dismiss();
			}
			
			
		}.execute(classID,subjectID,termID);
	}


	/**
	 * 月曲线图，单科三条曲线，班级单科、关注班级单科、年级单科;当subjectID为0时，查看的是上述全科的三条曲线，所用服务端方法完全一致
	 * @param classID
	 * @param subjectID
	 * @param termID
	 * @param caredClassID
	 */
	private void chartCurveByMonth(String classID, String subjectID,
			String termID, String caredClassID,String grade) {
		// TODO Auto-generated method stub
		
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		//异步访问数据
		new AsyncTask<Object, Object, List<DataTable>>(){			
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected List<DataTable> doInBackground(Object... params) {
				// TODO Auto-generated method stub
				
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				List<DataTable> dtList = new ArrayList<DataTable>();
				DataTable dtClass = new DataTable(); // 截止到某一月班级单科掌握度
				DataTable dtCaredClass = new DataTable(); // 截止到某一月关注班级全科掌握度
				DataTable dtGrade = new DataTable(); // 截止到某一月年级单科掌握度
				
				// 方法名，截止到某一月班级单科掌握度
				String methodName = "teach_chartCurve_classByMonth";
				
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
					dtClass = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "teach_chartCurve_classByMonth...出错了。。。");
					e.printStackTrace();
				}
				
				if(dtClass !=null && dtClass.getRowCount()>0){
					dtList.add(dtClass);
				}
				
				// 获取截止到某一月关注班级掌握度
				methodName="teach_chartCurve_classByMonth";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//替换参数
				paramsMap.remove("classID");
				paramsMap.remove("TokenID");
				paramsMap.put("classID",params[3].toString());
				paramsMap.put("TokenID",tokenID);
				
				try {
					dtCaredClass = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "teach_chartCurve_classByMonth(caredClass)...出错了。。。");
					e.printStackTrace();
				}
				
				if(dtCaredClass != null && dtCaredClass.getRowCount()>0){
					dtList.add(dtCaredClass);
				}
				
				//获取截止到某一月年级掌握度
				methodName = "teach_chartCurve_gradeByMonth";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				paramsMap.remove("classID");
				paramsMap.put("grade", params[4].toString());
				paramsMap.remove("TokenID");
				paramsMap.put("TokenID", tokenID);
				
				try {
					dtGrade= getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "teach_chartCurve_gradeByMonth...出错了。。。");
					e.printStackTrace();
				}
								
				if(dtGrade != null && dtGrade.getRowCount() > 0) {
					dtList.add(dtGrade);
				}			
				
				return dtList;
			}

			@Override
			protected void onPostExecute(List<DataTable> result) {
				// TODO Auto-generated method stub
				initViewAfterGetData(result);
				refreshView.dismiss();
			}						
			
		}.execute(classID,subjectID,termID,caredClassID,grade);
	}
	
	/**
	 * 截止期中/期末曲线比对图3条曲线：1、班级各单元平均值对比曲线； 2、关注班级各单元平均比对曲线； 3、年级各单元平均值比对曲线。
	 * @param classID
	 * @param subjectID
	 * @param termID
	 * @param termType
	 * @param caredClassID
	 * @param grade
	 */
	private void chartCurveByTerm(String classID, String subjectID,
			String termID, String termType, String caredClassID,
			String grade) {
		// TODO Auto-generated method stub
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		//异步调用数据
		new AsyncTask<Object, Object, List<DataTable>>(){
									
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected List<DataTable> doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				List<DataTable> dtList = new ArrayList<DataTable>();
				// 用模拟的数据库表存储返回结果
				DataTable dt = new DataTable();
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
					dt = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "teach_chartCurve_classByTerm()...出错了。。。");
					e.printStackTrace();
				}				
				
				if(dt!=null && dt.getRowCount()>0){
					dtList.add(dt);
				}
				
				return dtList;
			}

			@Override
			protected void onPostExecute(List<DataTable> result) {
				// TODO Auto-generated method stub
				initViewAfterGetData(result);
				refreshView.dismiss();
			}
			
		}.execute(classID,subjectID,termID,termType,caredClassID,grade);
	}	
	

	/**
	 * 获取数据后展示到界面上
	 * @param result
	 * 
	 */
	private void initViewAfterGetData(List<DataTable> result) {
		// TODO Auto-generated method stub
		//
		if(dtListChartCurve!= null){
			dtListChartCurve.clear();
		}
		//初始化界面
		if(result != null & result.size()>0){
			dtListChartCurve =result;
			
			List<String> chartCurveNames = null;
			//生成曲线图
			if(timeType != null && timeType.length()>0 && timeType.equals("周") 
					&& dtListChartCurve.size() ==1){
				chartCurveNames = new ArrayList<String>();
				chartCurveNames.add("班级单科");
				
				chartType=1;
				//周曲线对比图
				initChart(chartCurveNames);
			}else if (timeType != null && timeType.length() > 0 && timeType.equals("月")
					&& dtListChartCurve.size() == 3){
				if(chartCurveTypeByMonth != null && chartCurveTypeByMonth.length() > 0 
						&& chartCurveTypeByMonth.equals("单科")){
					chartCurveNames = new ArrayList<String>();
					chartCurveNames.add("班级单科");
					chartCurveNames.add("关注班级");
					chartCurveNames.add("年级单科");
					chartType=2;
					//月曲线单科
					initChart(chartCurveNames);
				}else if (chartCurveTypeByMonth != null && chartCurveTypeByMonth.length() > 0 
						&& chartCurveTypeByMonth.equals("全科")){
					chartCurveNames = new ArrayList<String>();
					chartCurveNames.add("班级全科");
					chartCurveNames.add("关注班级");
					chartCurveNames.add("年级全科");
					chartType=3;
					//月曲线全科
					initChart(chartCurveNames);
				}
			}else if (timeType != null && timeType.length() > 0 
					&& (timeType.equals("期中") || timeType.equals("期末"))
					&& dtListChartCurve.size() == 1){
				
				chartCurveNames = new ArrayList<String>();
				chartCurveNames.add("班级单科");
				chartCurveNames.add("关注班级");
				chartCurveNames.add("年级单科");
				chartType=4;
				//学期曲线
				initChart(chartCurveNames );
			}
		} else{
			//结果为空
		}
	}

	/**
	 * 
	 * @param chartCurveNames
	 * @param chartType 1:周曲线图，只有一条曲线  2：月曲线图，2或3条曲线，单科  3：月曲线图，2或3条曲线，全科 
	 * 					4：期中期末曲线图，2或3条曲线
	 */
	private void initChart(List<String> chartCurveNames) {
		// TODO Auto-generated method stub
		// 初始化，必须保证XYMultipleSeriesDataset对象中的XYSeries数量和
        // XYMultipleSeriesRenderer对象中的XYSeriesRenderer数量一样多
		//周曲线图 ，只有一条曲线
		if(chartType ==1){
			line1= new XYSeries(chartCurveNames.get(0));			
			line2= new XYSeries("");			
			line3= new XYSeries("");			
		}else if(chartType==2 || chartType==3 || chartType==4)
		{
			line1 = new XYSeries(chartCurveNames.get(0));
			line2 = new XYSeries(chartCurveNames.get(1));
			line3 = new XYSeries(chartCurveNames.get(2));
		}
		
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
       if(chartType==1){//周曲线只有一条线
    	   mDataset.addSeries(line1);
    	   mXYMultipleSeriesRenderer.addSeriesRenderer(renderer1);
       }else if (chartType== 2|| chartType== 3){//月曲线有2到3条
    	   if(caredClassID.equals("")){//无关注班级
    		   mDataset.addSeries(line1);
    		   mDataset.addSeries(line3);
    		   mXYMultipleSeriesRenderer.addSeriesRenderer(renderer1);
    		   mXYMultipleSeriesRenderer.addSeriesRenderer(renderer3);
    	   }else {
    		   mDataset.addSeries(line1);
    		   mDataset.addSeries(line2);
    		   mDataset.addSeries(line3);
    		   mXYMultipleSeriesRenderer.addSeriesRenderer(renderer1);
    		   mXYMultipleSeriesRenderer.addSeriesRenderer(renderer2);
    		   mXYMultipleSeriesRenderer.addSeriesRenderer(renderer3);
    	   }
       }else if (chartType==4){//学期曲线图，2到三条
    	   if(caredClassID.equals("")){//无关注班级
    		   mDataset.addSeries(line1);
    		   mDataset.addSeries(line3);
    		   mXYMultipleSeriesRenderer.addSeriesRenderer(renderer1);
    		   mXYMultipleSeriesRenderer.addSeriesRenderer(renderer3);
    	   }else {
    		   mDataset.addSeries(line1);
    		   mDataset.addSeries(line2);
    		   mDataset.addSeries(line3);
    		   mXYMultipleSeriesRenderer.addSeriesRenderer(renderer1);
    		   mXYMultipleSeriesRenderer.addSeriesRenderer(renderer2);
    		   mXYMultipleSeriesRenderer.addSeriesRenderer(renderer3);
    	   }
	   }
     //配置chart参数
       int i = dtListChartCurve.get(0).getRowCount();
       
       setChartSettings(mXYMultipleSeriesRenderer, 0, i, 0, 100, Color.BLACK,
               Color.BLACK);
       
     //通过该函数获取到一个View 对象
       chart = ChartFactory.getLineChartView(this, mDataset, mXYMultipleSeriesRenderer);
       
       chart_line_layout.removeAllViews();
       chart_line_layout.addView(chart, new LayoutParams(LayoutParams.MATCH_PARENT,
               LayoutParams.MATCH_PARENT));
       
	}

	private void initLine(XYSeries series){
		//期中期末
		if(chartType==4){
			String columnName="";
			if(series.equals(line1)){
				columnName="rightPersent";
			}else if (series.equals(line2)){
				columnName="caredClassRightPersent";
			}else if (series.equals(line3)){
				columnName="gradeRightPersent";
			}
			
			DataTable dt = dtListChartCurve.get(0);
			for(int i=0; i<dt.getRowCount(); i++){
				try {
					series.add(i+1, (Double.parseDouble(dt.getCell(i, columnName))) * 100);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}//周曲线图
		else if (chartType == 1){
			String columnName="";
			if(series.equals(line1)){
				DataTable dt = dtListChartCurve.get(0);
				columnName ="rightPercent";
				for(int i=0 ;i<dt.getRowCount() ; i++){
					try {
						series.add(i+1, (Double.parseDouble(dt.getCell(i, columnName))) * 100);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else{
				return;
			}
		}//月曲线图，需要区分是否有关注班级，如果caredClassID 为空字符串则不提取它的数据（即第二个结果集）
		else if (chartType==2 || chartType==3 ){
			
			if(series.equals(line2) && caredClassID.equals("")) {
				return;
			}
			
			int i =0;
			String columnName="";
			if(series.equals(line1)){
				i=0; //line1(本班级) 使用 0索引结果集
				columnName ="rightPercent";
			}else if(series.equals(line2)){
				i=1; //line2(关注班级)使用1索引结果集
				columnName ="rightPercent";
			}else if (series.equals(line3)){
				i=2; //line3(年级)使用2索引结果集
				columnName ="rightPercent";
			}
			
			DataTable dt = dtListChartCurve.get(i);
			
			for(int j=0; j< dt.getRowCount(); j++){
				
				try {
					series.add(j+1,  (Double.parseDouble(dt.getCell(j, columnName))) * 100);
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
        mXYMultipleSeriesRenderer.setShowCustomTextGrid(true);
        
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








