package com.ttqeducation.activitys.study;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ttqeducation.R;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.TaskCompletion;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;

import com.ttqeducation.tools.DesUtil;
import com.ttqeducation.tools.GeneralTools;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class TaskResultDetailForHTMLActivity extends Activity implements OnGestureListener {

	private String useID="";
	private String studentID="";
	
	private LinearLayout titleBackLayout = null;
	private TextView titleTextView = null;
	private WebView wvQuestionFile = null;//用于展示题目HTML
	private WebView wvAnswerFile = null; //用于展示答案HTML
	private ScrollView scrollvew = null;//滑动模块
	private RefreshView refreshView ;
	
	private int currentNum = 0 ; //当前是第几道题
	private TextView tvCurrentQues = null;
	private TextView tvTotalQues = null;//一共有多少道题	
	private TextView tvQuestionCode = null;
	private TextView tvKeyValue = null;
	private TextView tvIsRight = null;
	
	private List<AnswerDetail> answerDetailList = new ArrayList<AnswerDetail>();
	
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
		setContentView(R.layout.activity_task_result_detail_for_html);
		getDateFromIntent();
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		this.titleTextView=(TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("作业结果详情");
		this.titleBackLayout = (LinearLayout)(super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TaskResultDetailForHTMLActivity.this.finish();
			}
		});
		
		this.tvKeyValue = (TextView)findViewById(R.id.tvKeyValue);
		this.tvIsRight =(TextView)findViewById(R.id.tvIsRight);
		this.scrollvew = (ScrollView)findViewById(R.id.scrollView2);
		this.wvQuestionFile =(WebView)findViewById(R.id.wvQuestionFile);
		this.wvQuestionFile.setWebViewClient(new WebViewClient());
		
		this.wvQuestionFile.setOnTouchListener(new OnTouchListener() {//防止webview的滑动和scrollview滑动冲突
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_UP) 
					scrollvew.requestDisallowInterceptTouchEvent(false); 
			    else  
			    	scrollvew.requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		
		this.wvAnswerFile =(WebView)findViewById(R.id.wvAnswerFile);
		this.wvAnswerFile.setWebViewClient(new WebViewClient());
		this.wvAnswerFile.setOnTouchListener(new OnTouchListener() {//防止webview的滑动和scrollview滑动冲突
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_UP) 
					scrollvew.requestDisallowInterceptTouchEvent(false); 
			    else  
			    	scrollvew.requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		
		this.tvCurrentQues = (TextView)findViewById(R.id.tvCurrentQues);
		this.tvTotalQues = (TextView)findViewById(R.id.tvTotalQues);
		this.tvQuestionCode = (TextView)findViewById(R.id.tvQuestionCode);
		
//		wvQuestionFile.loadUrl("http://115.29.161.214:8088/html/112108151_199.html");
//		wvAnswerFile.loadUrl("http://115.29.161.214:8088/html/151653142_50.html");
		
		// 创建手势检测器 
		this.detector = new GestureDetector(getApplicationContext(), this);
		
		//初始化控件上显示的数据
		this.tvCurrentQues.setText("0");
		this.tvTotalQues.setText("0");		
		this.tvQuestionCode.setText("");
		this.tvKeyValue.setText("");
		this.tvIsRight.setText("");
		
		get_studentDailyTaskDetail(this.studentID, this.useID);
	}

	private void getDateFromIntent() {
		// TODO Auto-generated method stub
		this.useID =String.valueOf( getIntent().getIntExtra("useID", -1));
		this.studentID = UserInfo.getInstance().studentID;

	}
	
	/**
	 * 获取学生的的详细答题情况
	 * 
	 * 
	 *           
	 */
	public void get_studentDailyTaskDetail(String studentID, String useID) {
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
				String methodName = "APP_student_AnswerDetail_byUseID";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				// paramsMap.put("studentID", "S20140001");
				// paramsMap.put("date", "2015-01-27");
				// paramsMap.put("subjectID", "2");
				paramsMap.put("studentID", params[0].toString());
				paramsMap.put("useID", params[1].toString());
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
					dt_student = getdatatool.getDataAsTable(methodName,
							paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "get_studentDailyTaskDetail()...出错了。。。");
					e.printStackTrace();
				}
				return dt_student;
			}

			protected void onPostExecute(DataTable result) {
				if (result != null) {
					int count=result.getRowCount();
					for(int i=0;i<count;i++){
						try {
							answerDetailList.add(new AnswerDetail(result.getCell(i, "questionCode"),
									result.getCell(i, "isRight"), 
									result.getCell(i, "keyValue"),
									result.getCell(i, "answerFilePath"),
									result.getCell(i,"questionFilePath"))
									);
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				// 关闭刷新；
				refreshView.dismiss();
				initViewAfterGetData();
				// 获取完数据后，初始化界面
				
			}
		}.execute(studentID, useID);
	}

	
	protected void initViewAfterGetData() {
		// TODO Auto-generated method stub
		if(answerDetailList.size()>0){
			currentNum = 0;
			tvCurrentQues.setText((currentNum+1)+"");
			tvTotalQues.setText(answerDetailList.size()+"");
			tvQuestionCode.setText(answerDetailList.get(0).getQuestionCode().trim());
			if(answerDetailList.get(0).getQuestionFilePath().equals("")){
				//wvQuestionFile.loadUrl("about:blank");
				wvQuestionFile.setVisibility(View.GONE);
			}else{
				wvQuestionFile.loadUrl(answerDetailList.get(0).getQuestionFilePath());
				wvQuestionFile.setVisibility(View.VISIBLE);
			}
			
			if(answerDetailList.get(0).getAnswerFilePath().equals("")){
				wvAnswerFile.loadUrl("about:blank");
			}else{
				wvAnswerFile.loadUrl(answerDetailList.get(0).getAnswerFilePath());
			}
			tvKeyValue.setText(answerDetailList.get(0).getKeyValue());
			String isRight = answerDetailList.get(0).getIsRight();
			if(isRight.trim().equals("0")){
				tvIsRight.setText("错误");
				tvIsRight.setTextColor(this.getResources().getColor(R.color.textRed));
			}else if (isRight.trim().equals("1")){
				tvIsRight.setText("正确");
				tvIsRight.setTextColor(this.getResources().getColor(R.color.textGreen2));
			}
		}
	}

	private void getNextQuestion(){//跳转到下一道题目
		if(currentNum+1 == answerDetailList.size() || answerDetailList.size()==0){
			showToast("当前为最后一道题");
		}else{
			currentNum++;
			tvCurrentQues.setText((currentNum+1)+"");
			tvQuestionCode.setText(answerDetailList.get(currentNum).getQuestionCode().trim());
			if(answerDetailList.get(currentNum).getQuestionFilePath().equals("")){//如果没有题目HTML
				//wvQuestionFile.loadUrl("about:blank");
				wvQuestionFile.setVisibility(View.GONE);
			}else{
				wvQuestionFile.loadUrl(answerDetailList.get(currentNum).getQuestionFilePath());
				wvQuestionFile.setVisibility(View.VISIBLE);
			}
			if(answerDetailList.get(currentNum).getAnswerFilePath().equals("")){
				wvAnswerFile.loadUrl("about:blank");
			}else{
				wvAnswerFile.loadUrl(answerDetailList.get(currentNum).getAnswerFilePath());
			}
			tvKeyValue.setText(answerDetailList.get(currentNum).getKeyValue());
			String isRight = answerDetailList.get(currentNum).getIsRight();
			if(isRight.trim().equals("0")){
				tvIsRight.setText("错误");
				tvIsRight.setTextColor(this.getResources().getColor(R.color.textRed));
			}else if (isRight.trim().equals("1")){
				tvIsRight.setText("正确");
				tvIsRight.setTextColor(this.getResources().getColor(R.color.textGreen2));
			}
			
		}
	}

	private void getLastQuestion(){//跳转到上一道题目
		if(currentNum == 0){
			showToast("当前为第一道题");
		}else{
			currentNum --;
			tvCurrentQues.setText((currentNum+1)+"");	
			tvQuestionCode.setText(answerDetailList.get(currentNum).getQuestionCode().trim());
			if(answerDetailList.get(currentNum).getQuestionFilePath().equals("")){
				//wvQuestionFile.loadUrl("about:blank");
				wvQuestionFile.setVisibility(View.GONE);
			}else{
				wvQuestionFile.loadUrl(answerDetailList.get(currentNum).getQuestionFilePath());
				wvQuestionFile.setVisibility(View.VISIBLE);
			}
			
			if(answerDetailList.get(currentNum).getAnswerFilePath().equals("")){
				wvAnswerFile.loadUrl("about:blank");
			}else{
				wvAnswerFile.loadUrl(answerDetailList.get(currentNum).getAnswerFilePath());
			}
			tvKeyValue.setText(answerDetailList.get(currentNum).getKeyValue());
			String isRight = answerDetailList.get(currentNum).getIsRight();
			if(isRight.trim().equals("0")){
				tvIsRight.setText("错误");
				tvIsRight.setTextColor(this.getResources().getColor(R.color.textRed));
			}else if (isRight.trim().equals("1")){
				tvIsRight.setText("正确");
				tvIsRight.setTextColor(this.getResources().getColor(R.color.textGreen2));
			}
		}
	}
	
	public class AnswerDetail{
		private String questionCode;
		private String isRight;
		private String keyValue;
		private String answerFilePath;
		private String questionFilePath;
		
		

		public AnswerDetail(String questionCode,String isRight,String keyValue,String answerFilePath,String questionFilePath){
			this.questionCode= questionCode;
			this.isRight = isRight;
			this.keyValue = keyValue;
			this.answerFilePath= answerFilePath;
			if(answerFilePath == null||answerFilePath.equals("anyType{}")  ){
				this.answerFilePath ="";				
			}	
			this.questionFilePath = questionFilePath;
			if(questionFilePath == null||questionFilePath.equals("anyType{}")  ){
				this.questionFilePath ="";				
			}	
		}

		public String getQuestionCode() {
			return questionCode;
		}

		public void setQuestionCode(String questionCode) {
			this.questionCode = questionCode;
		}

		public String getIsRight() {
			return isRight;
		}

		public void setIsRight(String isRight) {
			this.isRight = isRight;
		}

		public String getKeyValue() {
			return keyValue;
		}

		public void setKeyValue(String keyValue) {
			this.keyValue = keyValue;
		}

		public String getAnswerFilePath() {
			return answerFilePath;
		}

		public void setAnswerFilePath(String answerFilePath) {
			this.answerFilePath = answerFilePath;
		}
		public String getQuestionFilePath() {
			return questionFilePath;
		}

		public void setQuestionFilePath(String questionFilePath) {
			this.questionFilePath = questionFilePath;
		}
	}

	//窗口提示信息
	public void showToast(String toastMessage){
		Toast toast = Toast.makeText(TaskResultDetailForHTMLActivity.this, toastMessage, Toast.LENGTH_SHORT);
		toast.show();
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
}
