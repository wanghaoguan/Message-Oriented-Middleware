package com.ttqeducation.teacher.activitys.teach;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ttqeducation.teacher.R;

import com.ttqeducation.teacher.activitys.teach.KnowledgePointActivity.DayAndWeekViewHolder;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.tools.DesUtil;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 家庭作业布置页面，用于显示题干题库的题目信息
 * @author 侯翔宇
 *
 */
public class HomeWorkArrangeDetailContentActivity extends Activity implements OnGestureListener {

	//从上一个界面载入数据
	private String testID;
	private String typeBelong;
	private String subjectID;
	private String testName;
	private String classID;
	private String teacherID;
	//标题栏
	private LinearLayout titleBackLayout;
	private TextView titleTextView;
	//布置按钮
	private LinearLayout llSubmit=null;
	//题目进度编号
	private TextView tvCurrentNum;
	private TextView tvTotalNum;
	private int currentNum = 0;
	private int totalNum = 0;
	private RefreshView refreshView;	
	//保存题目的URL列表
	private List<Question> questionList = new ArrayList<Question>();
	//用于展示题干的webView
	private WebView webView;
	//用于配套教辅题目页码和题目名称的显示
	private LinearLayout llQuestionName;
	private TextView tvQuestionName;
	
	//提交时间
	public String mm="";
	public String dd="";
	
	// 定义手势检测器
	private GestureDetector detector;
	// 定义左右滑动动作触发距离限制
	final int flingMinXDistance = 100;
	final int flingMaxYDistance = 300;
	// 定义上下滑动动作触发距离限制
	final int flingMaxXDistance = 300;
	final int flingMinYDistance = 100;
	// 定义滑动动作在X轴方向的最小速度
	final int flingMinVelocityX = 0;
	// 定义down的位置,动作由down、move、up组成
	private float downX;
	private float downY;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homework_arrange_detaile_content);
		getDataFromIntent();
		initView();
		APPgetQuestionPreviewByTest(testID, typeBelong);
	}

	

	private void getDataFromIntent() {
		// TODO Auto-generated method stub
		this.testID = getIntent().getStringExtra("testID");
		this.typeBelong = getIntent().getStringExtra("typeBelong");
		this.subjectID = getIntent().getStringExtra("subject");
		this.testName = getIntent().getStringExtra("testName");
		this.classID = getIntent().getStringExtra("classID");
		this.teacherID = getIntent().getStringExtra("teacherID");
		
	}

	private void initView() {
		// TODO Auto-generated method stub
		this.titleBackLayout = (LinearLayout)findViewById(R.id.title_back_layout);
		this.titleTextView=(TextView)findViewById(R.id.title_text); 
		this.titleTextView.setText("家庭作业内容预览");
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HomeWorkArrangeDetailContentActivity.this.finish();
			}
		});
		this.tvCurrentNum = (TextView)findViewById(R.id.tvCurrentNum);
		this.tvTotalNum = (TextView)findViewById(R.id.tvTotalNum);
		this.webView = (WebView)findViewById(R.id.web_view);
		this.tvCurrentNum.setText("0");
		this.tvTotalNum.setText("0");		
		this.webView.setWebViewClient(new WebViewClient());
		this.llSubmit = (LinearLayout)findViewById(R.id.llSubmit);
		this.llQuestionName = (LinearLayout)findViewById(R.id.llQuestionName);
		this.tvQuestionName = (TextView)findViewById(R.id.tvQuestionName);
		//如果是从题库公司获取的题目，则没有页码和题目名称等数据，隐藏llQuestionName
		if(this.typeBelong.equals("2")){
			this.llQuestionName.setVisibility(View.GONE);
		}else if (this.typeBelong.equals("1")){
			this.llQuestionName.setVisibility(View.VISIBLE);
		}
		
		this.llSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				 Calendar cal = Calendar.getInstance();
				
				 MyDatePickerDialog dialog= new MyDatePickerDialog(HomeWorkArrangeDetailContentActivity.this, new MyDatePickerDialog.OnDateSetListener() {
					
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
				
				//默认最早为第二天提交，防止布置即刻过期情况
				cal.add(Calendar.DAY_OF_MONTH, 1);
				dialog.getDatePicker().setMinDate(cal.getTime().getTime());
				dialog.setTitle("选择提交时间");
				dialog.show();
			}
		});		
		
		// 创建手势检测器 
		detector = new GestureDetector(getApplicationContext(), this); 
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
	
	 /*
	  * 定义question类型
	  */
	class Question{
		private String questionCode;//题目名称
		private String questionFilePath;

		 
		public String getQuestionFilePath() {
			return questionFilePath;
		}

		public void setQuestionFilePath(String questionFilePath) {
			this.questionFilePath = questionFilePath;
		}

		public String getQuestionCode() {
			return questionCode;
		}

		public void setQuestionCode(String questionCode) {
			this.questionCode = questionCode;
		}
		
		public Question(String questionCode, String questionFilePath){
			this.questionCode=questionCode;
			this.questionFilePath = questionFilePath;
		}
		
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
					questionList.add(new Question(result.getCell(i, "questionCode"),result.getCell(i, "questionFilePath")));
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			Log.i("hxy", questionList.get(0).toString());
			this.webView.loadUrl(questionList.get(0).getQuestionFilePath().toString());
			this.tvQuestionName.setText(questionList.get(0).getQuestionCode().toString());
			this.currentNum=1;
			this.totalNum=questionList.size();
			this.tvCurrentNum.setText(String.valueOf(currentNum));
			this.tvTotalNum.setText(String.valueOf(totalNum)); 
			
		}
	}


	// 将触碰事件交给Gesture Detector处理
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return detector.onTouchEvent(event);
	}
	
	// 接受事件并且区分点击事件和滑动事件
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// 处理左右滑动事件
		detector.onTouchEvent(ev);
		
		if(ev.getAction() == MotionEvent.ACTION_DOWN) {
			downX = ev.getX();
			downY = ev.getY();
			super.dispatchTouchEvent(ev);
		} else if(ev.getAction() == MotionEvent.ACTION_MOVE) {
			super.dispatchTouchEvent(ev);
		} else if(ev.getAction() == MotionEvent.ACTION_UP) {
			if(Math.abs(ev.getX() - downX) == 0 && Math.abs(ev.getY() - downY) == 0) { // 处理点击事件
				super.dispatchTouchEvent(ev);
			} else if(Math.abs(ev.getX() - downX) > flingMinXDistance 
					&& Math.abs(ev.getY() - downY) < flingMaxYDistance
					&& Math.abs(ev.getX() - downX) - Math.abs(ev.getY() - downY) > 0) { // 不出你左右滑动事件
				
			} else if(Math.abs(ev.getX() - downX) < flingMaxXDistance 
					&& Math.abs(ev.getY() - downY) > 0
					&& Math.abs(ev.getX() - downX) - Math.abs(ev.getY() - downY) < 0) { // 处理上下滑动事件
				super.dispatchTouchEvent(ev);
			}			
		} 
		
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		// 从右到左滑动
		if(e1.getX() - e2.getX() > flingMinXDistance 
				&& Math.abs(e1.getY() - e2.getY()) < flingMaxYDistance
				&& Math.abs(velocityX) > flingMinVelocityX){
			//showToast("下一道");
			getNextQuestion();
		}
		// 从左到右滑动
		else if(e2.getX() - e1.getX() > flingMinXDistance 
				&& Math.abs(e1.getY() - e2.getY()) < flingMaxYDistance
				&& Math.abs(velocityX) > flingMinVelocityX){
			//showToast("上一道");
			getLastQuestion();
		}
		return false;
	}



	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void getNextQuestion(){
		//list中无数据
		if(totalNum == 0){
			showToast("没有题目");
		}else if(currentNum == totalNum ){//当前是最后一道题
			showToast("当前为最后一道题");
		}else{
			currentNum++;
			this.tvCurrentNum.setText(String.valueOf(currentNum));
			this.webView.loadUrl(questionList.get(currentNum-1).getQuestionFilePath().toString());
			this.tvQuestionName.setText(questionList.get(currentNum-1).getQuestionCode().toString());
		}
	}
	
	public void getLastQuestion(){
		//list中无数据
		if(totalNum == 0){
			showToast("没有题目");
		}else if(currentNum == 1){//当前为第一道题
			showToast("当前为第一道题");
		}else{ 
			currentNum--;
			this.tvCurrentNum.setText(String.valueOf(currentNum));
			this.webView.loadUrl(questionList.get(currentNum-1).getQuestionFilePath().toString());
			this.tvQuestionName.setText(questionList.get(currentNum-1).getQuestionCode().toString());
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
					if(result != null && Integer.valueOf( result.getCell(0, "useID").toString())>0){
						Toast toast =  Toast.makeText(HomeWorkArrangeDetailContentActivity.this, 
								"布置成功,作业收取时间为"+getSubmitMonth()+"月"+getSubmitDay()+"日", Toast.LENGTH_LONG);
						toast.show();
					}else{
						Toast toast =  Toast.makeText(HomeWorkArrangeDetailContentActivity.this, 
								"布置失败", Toast.LENGTH_LONG);
						toast.show();
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
    
	//窗口提示信息
	public void showToast(String toastMessage){
		Toast toast = Toast.makeText(HomeWorkArrangeDetailContentActivity.this, toastMessage, Toast.LENGTH_SHORT);
		toast.show();
	}
}
