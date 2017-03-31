package com.ttqeducation.teacher.activitys.teach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.TaskCompletion;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.tools.DesUtil;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class TaskResultDetailForHTMLActivity extends Activity implements OnGestureListener{

	private String useID="";
	private String taskName;
	private LinearLayout titleBackLayout = null;
	private TextView titleTextView = null;
	private WebView wvQuestionFile = null;//用于展示题目HTML
	private WebView wvAnswerFile = null; //用于展示答案HTML
	private ScrollView scrollvew = null;//滑动模块
	private RefreshView refreshView ;
	private List<AnswerDetail> answerDetailList = new ArrayList<AnswerDetail>();
	private int currentNum = 0 ; //当前是第几道题
	private TextView tvCurrentQues = null;
	private TextView tvTotalQues = null;
	private TextView tvPercent = null;
	private TextView tvQuestionCode = null;
	private TextView tvSummary = null;
	//实现滑动效果
//	private ViewFlipper viewFlipper = null;
	
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
		getDataFromIntent();
		initView();
		
	}


	private void getDataFromIntent() {
		// TODO Auto-generated method stub
		this.useID = String.valueOf( getIntent().getIntExtra("useID", -1));
		this.taskName = getIntent().getStringExtra("taskName");		
		
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		this.titleTextView=(TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("家庭作业结果详情");
		this.titleBackLayout = (LinearLayout)(super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TaskResultDetailForHTMLActivity.this.finish();
			}
		});
		this.scrollvew = (ScrollView)findViewById(R.id.scrollView2);
		
		this.wvQuestionFile =(WebView)findViewById(R.id.wvQuestionFile);
		
//		//支持javascript
	
//		// 设置可以支持缩放 
//		this.wvQuestionFile.getSettings().setSupportZoom(true); 
//		// 设置出现缩放工具 
//		this.wvQuestionFile.getSettings().setBuiltInZoomControls(true);
//		//扩大比例的缩放
//		this.wvQuestionFile.getSettings().setUseWideViewPort(true);
//		//自适应屏幕
//		this.wvQuestionFile.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
//		this.wvQuestionFile.getSettings().setLoadWithOverviewMode(true);
		/************************************************************************/
//		this.wvQuestionFile.setSaveEnabled(true);		
		wvQuestionFile.getSettings().setJavaScriptEnabled(true);
		wvQuestionFile.addJavascriptInterface(new HeightGetter(), "jo");

		wvQuestionFile.setWebViewClient(new WebViewClient() {

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}

			public void onPageFinished(WebView view, String url) {
				wvQuestionFile.loadUrl("javascript:window.jo.run(document.documentElement.scrollHeight+'');");
			}
		});
		
		//this.wvQuestionFile.setWebViewClient(new WebViewClient());
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
		
//		this.wvAnswerFile.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
//		this.wvAnswerFile.getSettings().setLoadWithOverviewMode(true);
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
		this.tvPercent = (TextView)findViewById(R.id.tvPercent);
		this.tvQuestionCode = (TextView)findViewById(R.id.tvQuestionCode);
		this.tvSummary = (TextView)findViewById(R.id.tvSummary);
		// 创建手势检测器 
		this.detector = new GestureDetector(getApplicationContext(), this);
//		this.viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper_subjective_homework);
		
		//初始化控件上显示的数据
		this.tvCurrentQues.setText("0");
		this.tvTotalQues.setText("0");
		this.tvPercent.setText("0");
		this.tvQuestionCode.setText("");
		this.tvSummary.setText("");
		
		
//		wvQuestionFile.loadUrl("http://115.29.161.214:8088/html/112108151_199.html");
//		wvAnswerFile.loadUrl("http://115.29.161.214:8088/html/151653142_50.html");
		
		getClassAnswerDetail(useID);
		
	}
	
	/**
	 * 根据useID查找题目详细答题情况
	 * @param useID
	 */	
	private void getClassAnswerDetail(String useID) {
		// TODO Auto-generated method stub
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		//异步访问网络
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
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				DataTable dt_result = new DataTable();
				// 方法名
				String methodName = "APP_getClassAnswerDetail_byUseID";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("useID", params[0].toString());
				paramsMap.put("TokenID", tokenID);
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
					Log.i("error", "APP_getClassAnswerDetail_byUseID()...出错了。。。");
					e.printStackTrace();
				}
				return dt_result;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				if(result !=null){
					
					int count=result.getRowCount();
					for(int i=0;i<count;i++){
						try{
//							String questionID = result.getCell(i, "questionID");
//							String questionCode = result.getCell(i, "questionCode");
//							String rightPercent = result.getCell(i, "rightPercent");
							
							answerDetailList.add(new AnswerDetail(result.getCell(i, "questionID"),
									result.getCell(i, "questionCode"), result.getCell(i, "rightPercent"), 
									result.getCell(i, "answerFilePath"), result.getCell(i, "questionFilePath"), result.getCell(i, "summary")));
							
							
						}catch (dataTableWrongException e) {
							e.printStackTrace();
						}						
					}					
				}				
				refreshView.dismiss();
				// 获取完数据后，初始化界面
				initViewAfterGetData();
			}
					
			
		}.execute(useID);
	}

	private void initViewAfterGetData() {//加载第一道题目时界面初始化
		// TODO Auto-generated method stub
		if(answerDetailList.size()>0){
			currentNum = 0;
			tvCurrentQues.setText((currentNum+1)+"");
			
			tvTotalQues.setText(answerDetailList.size()+"");
			tvPercent.setText( (int)(Float.parseFloat( answerDetailList.get(0).getRightPercent())*100)+"") ;
			tvQuestionCode.setText(answerDetailList.get(0).getQuestionCode().trim());
			tvSummary.setText(answerDetailList.get(0).getSummary().trim());
			
			
			
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
			
			//showToast(String.valueOf( wvQuestionFile.getContentHeight()));
//			wvAnswerFile.loadUrl(answerDetailList.get(0).getAnswerFilePath());
			
			
//			wvQuestionFile.loadUrl("http://115.29.161.214:8088/html/112108151_199.html");
//			wvAnswerFile.loadUrl("http://115.29.161.214:8088/html/151653142_50.html");
			
			

			//wvQuestionFile.loadUrl("http://www.baidu.com");
		}
		
	}
	
	private class HeightGetter {
		@JavascriptInterface
		public void run(final String height) {
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(getApplicationContext(), height+wvQuestionFile.getWidth(), 0).show();
					
				}
			});
		}
	}
	
	private void getNextQuestion(){//跳转到下一道题目
		//showToast(String.valueOf( wvQuestionFile.getContentHeight()));
		
		if(currentNum+1 == answerDetailList.size() || answerDetailList.size()==0){
			showToast("当前为最后一道题");
		}else{
			currentNum ++;
			tvCurrentQues.setText((currentNum+1)+"");			
			tvPercent.setText( (int)(Float.parseFloat( answerDetailList.get(currentNum).getRightPercent())*100)+"") ;
			tvQuestionCode.setText(answerDetailList.get(currentNum).getQuestionCode().trim());
			tvSummary.setText(answerDetailList.get(currentNum).getSummary().trim());
			
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
			
			
		}
	}
	
	private void getLastQuestion(){//跳转到上一道题目
		//showToast(String.valueOf( wvQuestionFile.getContentHeight()));
		if(currentNum ==0 ){
			showToast("当前为第一道题");
		}else{
			currentNum --;
			tvCurrentQues.setText((currentNum+1)+"");			
			tvPercent.setText( (int)(Float.parseFloat( answerDetailList.get(currentNum).getRightPercent())*100)+"") ;
			tvQuestionCode.setText(answerDetailList.get(currentNum).getQuestionCode().trim());
			tvSummary.setText(answerDetailList.get(currentNum).getSummary().trim());
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
			
		}
	}

	public class AnswerDetail{
		
		private String questionID;
		private String questionCode;
		private String rightPercent;
		private String answerFilePath;
		private String questionFilePath;
		private String summary;
		
		public AnswerDetail(String questionID, String questionCode, String rightPercent, String answerFilePath,
				String questionFilePath, String summary){
			this.questionID=questionID;
			this.questionCode= questionCode;
			this.rightPercent = rightPercent;
			this.answerFilePath= answerFilePath;
			if(answerFilePath.equals("anyType{}") || answerFilePath == null){
				this.answerFilePath ="";
			}
			this.questionFilePath = questionFilePath;
			if(questionFilePath.equals("anyType{}") || questionFilePath == null){
				this.questionFilePath ="";
			}
			this.summary = summary;
			if(summary == null || summary.equals( "anyType{}")  ){
				this.summary ="";
			}
		}
		
		public String getQuestionID() {
			return questionID;
		}
		public void setQuestionID(String questionID) {
			this.questionID = questionID;
		}
		public String getQuestionCode() {
			return questionCode;
		}
		public void setQuestionCode(String questionCode) {
			this.questionCode = questionCode;
		}
		public String getRightPercent() {
			return rightPercent;
		}
		public void setRightPercent(String rightPercent) {
			this.rightPercent = rightPercent;
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
		public String getSummary() {
			return summary;
		}
		public void setSummary(String summary) {
			this.summary = summary;
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
		// 从右到左滑动
		if(e1.getX() - e2.getX() > flingMinXDistance 
				&& Math.abs(e1.getY() - e2.getY()) < flingMaxYDistance
				&& Math.abs(velocityX) > flingMinVelocityX){
			//showToast("下一道");			
			getNextQuestion();
//			Animation lInAnim = AnimationUtils.loadAnimation(this,
//					R.animator.push_left_in2);// 向左滑动左侧进入的渐变效果（alpha 0.1  -> 1.0） 
//			viewFlipper.setInAnimation(lInAnim);
//			viewFlipper.showNext();
		}
		// 从左到右滑动
		else if(e2.getX() - e1.getX() > flingMinXDistance 
				&& Math.abs(e1.getY() - e2.getY()) < flingMaxYDistance
				&& Math.abs(velocityX) > flingMinVelocityX){
			//showToast("上一道");
			getLastQuestion();
//			Animation rInAnim = AnimationUtils.loadAnimation(this,
//					R.animator.push_right_in);//  向右滑动左侧进入的渐变效果（alpha  0.1 -> 1.0）
//			viewFlipper.setInAnimation(rInAnim);
//			viewFlipper.showNext();
			
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
