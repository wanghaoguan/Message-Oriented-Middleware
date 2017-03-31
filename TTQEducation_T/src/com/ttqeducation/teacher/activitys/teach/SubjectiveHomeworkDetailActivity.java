package com.ttqeducation.teacher.activitys.teach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.tools.DensityUtils;
import com.ttqeducation.teacher.tools.DesUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 主观题照片批阅界面
 * @author Dell
 *
 */
public class SubjectiveHomeworkDetailActivity extends Activity implements OnGestureListener {

	private String useID;
	private LinearLayout titleBackLayout = null; //标题栏返回键
	private TextView titleTextView = null; //标题栏
	private WebView webview ;  //用于显示题干题目的控件
	private ScrollView scrollview = null; //滑动模块
	private TextView tvQuestionCode = null; //显示题目名称，题干题目和页码题目都可能会有数据
	private TextView tvStudentName = null;//学生姓名
	private EditText etReply = null; //教师批语输入框
	private LinearLayout llQues = null;//题目选择按钮
	private LinearLayout llChooseQuestion = null; //题目选择下拉框
	private ListView lvChooseQuestion = null;//题目选择列表
	private String[] questionTags;
	private List<Question> questionList = new ArrayList<Question>(); //用来记录本次作业所有的主观题题目
	private int questionNum=0; //题目的总个数
	private String currentQuestionID =""; //当前题目ID
	private int currentQuestionNum = 0 ; //当前看到的是第几道题目
	private RefreshView refreshView = null;
	private ArrayAdapter<String> questionAdapter;
	private TextView tvQues= null; //用于显示题目下拉框上的文字
	private Dialog nextQuestionDialog = null;
	private ImageView ivQuestionPic = null;
	
	private List<StudentAnswer> studentAnswerList = new ArrayList<StudentAnswer>();//用来某道题目学生答案
	private int studentNum = 0 ; //当前题目有多少个学生作答
	private String currentStudentID =""; //当前是哪个学生的答案
	private int currentStudentNum =0; //当前是第几个学生作答
	
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
	
	// 答题照片URL
	private String answerURL = "http://192.168.137.204:8086/pic/JJ.jpg";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subjective_homework_detail);
		getDataFromIntent();
		initView(); //加载布局
	}
	private void initView() {
		// TODO Auto-generated method stub
		
		this.titleBackLayout = (LinearLayout)(super.findViewById(R.id.action_bar)
				.findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SubjectiveHomeworkDetailActivity.this.finish();
			}
		});
		this.titleTextView =(TextView)(super.findViewById(R.id.action_bar)
				.findViewById(R.id.title_text));
		this.titleTextView.setText("照片批阅");
		this.scrollview = (ScrollView)findViewById(R.id.scrollView1);
		this.tvQuestionCode =(TextView) findViewById(R.id.tvQuestionCode);
		this.tvStudentName =(TextView) findViewById(R.id.tvName);
		this.etReply = (EditText)findViewById(R.id.etReply);
		this.llQues = (LinearLayout)findViewById(R.id.llQues); 
		this.tvQues = (TextView)findViewById(R.id.tvQues);
		this.ivQuestionPic = (ImageView)findViewById(R.id.ivQuestionPic);
		this.llChooseQuestion =(LinearLayout)findViewById(R.id.llChooseQuestion);
		this.lvChooseQuestion =(ListView)findViewById(R.id.lvChooseQuestion);
		this.lvChooseQuestion.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				if(currentQuestionNum!= position){
					
					currentQuestionNum = position;
					changeQuestion(currentQuestionNum);						
				}				
				llChooseQuestion.setVisibility(View.GONE);
			}
			
		});
		this.llQues.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(llChooseQuestion.getVisibility() == View.VISIBLE){
					llChooseQuestion.setVisibility(View.GONE);
					
				}else {
					llChooseQuestion.setVisibility(View.VISIBLE);
					
				}
			}
		});
		this.webview = (WebView)findViewById(R.id.web_view1);
		this.webview.setWebViewClient(new WebViewClient());
		this.webview.setOnTouchListener(new OnTouchListener() {//防止webview的滑动和scrollview滑动冲突
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_UP) 
					scrollview.requestDisallowInterceptTouchEvent(false); 
			    else  
			    	scrollview.requestDisallowInterceptTouchEvent(true);			              
				return false;
			}
		});
		
		// 创建手势检测器 
		detector = new GestureDetector(getApplicationContext(), this);
		initQuestion(useID);
		//setImageView(ivQuestionPic, answerURL);
		
	}
	
	/**
	 * 获取本次作业包含的题目信息，并传入questionList
	 * @param useID
	 */
	public void initQuestion(String useID ) {
		// TODO Auto-generated method stub
		
		new AsyncTask<Object, Object, DataTable>() {
            
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				DataTable dt_result = new DataTable();
				// 方法名
				String methodName = "APP_getSubjectiveQuestionList";
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
					dt_result = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "APP_getSubjectiveQuestionList()...出错了。。。");
					e.printStackTrace();
				}		
				
				return dt_result;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result != null && result.getRowCount()>0){
					questionTags = new String[result.getRowCount()];
					for(int i=0 ; i<result.getRowCount();i++){ //将获取的题目存入list，同时根据题目个数生成下拉框第一题、第二题等....
						try {
							questionList.add(new Question(result.getCell(i, "questionID"), result.getCell(i, "questionCode"),
									result.getCell(i, "questionFilePath"), result.getCell(i, "typeBelong")));
							
							questionTags[i] = "第"+(i+1)+"题";
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}								
					}
					
					//载入题目下拉框数据
					 questionAdapter = new ArrayAdapter<String>(getApplicationContext(),
							 R.layout.item_subject, questionTags);
					 lvChooseQuestion.setAdapter(questionAdapter);
					 //题目下拉框默认显示 “第一题”
					 tvQues.setText(questionTags[0]);
					 tvQuestionCode.setText(questionList.get(0).getQuestionCode());
					 questionNum = result.getRowCount();
					 currentQuestionID = questionList.get(0).getQuestionID();
					 currentQuestionNum = 0; //默认当前是第一题，0表示第一题
					 //只要题目有HTML链接，不分题干题目和页码题目都显示webview
					 if(!questionList.get(0).getQuestionFilePath().equals("") ){
						 webview.loadUrl(questionList.get(0).getQuestionFilePath());
					 }else{
						 webview.setVisibility(View.GONE);
					 }
					 
					APPgetSubjectiveAnswerByQuestionID(currentQuestionID, SubjectiveHomeworkDetailActivity.this.useID);
				}
			}
			
		}.execute(useID);
		
	}
	
	// 图片下载
	private void setImageView(ImageView iv, String imageURL) {
		Picasso.with(SubjectiveHomeworkDetailActivity.this)
		.load(answerURL)
		.transform(new CropSquareTransformation())
		.into(iv);
	}
	
	public class CropSquareTransformation implements Transformation {
		
		  @Override public Bitmap transform(Bitmap source) {
		    // 原图片宽高
			int width = source.getWidth();
		    int height = source.getHeight();
		    
		    // 获取屏幕宽度
			DisplayMetrics metrics = getResources().getDisplayMetrics(); // 用来获取屏幕的分辨率；
			int screenWidthPX = metrics.widthPixels;
			float marginLeftRightPX = DensityUtils.dp2px(SubjectiveHomeworkDetailActivity.this, 32);
			// 缩放比例
			float widthWithPX = screenWidthPX - marginLeftRightPX;
			float scale = widthWithPX / width; // 缩放比例
			
			// 图片缩放后的宽高
			int scaleWidth = (int)widthWithPX;
			int scaleHeight = (int)(scale * height);
		    		  
		    Bitmap result = Bitmap.createScaledBitmap(source, scaleWidth, scaleHeight, false);
		    if (result != source) {
		      source.recycle();
		    }
		    return result;
		  }

		  @Override public String key() { return "square()"; }
		}	
	
	private void getDataFromIntent() {
		// TODO Auto-generated method stub
		this.useID = getIntent().getStringExtra("useID");
		
	}

	/**
	 * 切换题目时调用
	 * @param questionNum
	 */
	private void changeQuestion(int currentQuestionNum){
		
		 tvQuestionCode.setText(questionList.get(currentQuestionNum).getQuestionCode());
		 tvQues.setText(questionTags[currentQuestionNum]);
		  
		 currentQuestionID = questionList.get(currentQuestionNum).getQuestionID();
		 
		 //只要题目有HTML链接，不分题干题目和页码题目都显示webview
		 if(!questionList.get(currentQuestionNum).getQuestionFilePath().equals("") ){
			 webview.loadUrl(questionList.get(currentQuestionNum).getQuestionFilePath());
		 }else{
			 webview.setVisibility(View.GONE);
		 }
		 APPgetSubjectiveAnswerByQuestionID(currentQuestionID, useID);
	}
	
	/**
	 * 主观题批改时，根据题目获取学生答案
	 * @param questionID
	 * @param useID
	 */
	public void APPgetSubjectiveAnswerByQuestionID(String questionID,String useID){
		this.refreshView = new RefreshView(this,  R.style.full_screen_dialog);
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
				String methodName = "APP_getSubjectiveAnswerByQuestionID";
				//参数Map
				Map<String,String> paramsMap = new HashMap<String, String>();
				String tokenID ="";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("questionID",params[0].toString());
				paramsMap.put("useID",params[1].toString());				
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
					Log.i("error", "APP_getSubjectiveAnswerByQuestionID()...出错了。。。");
					e.printStackTrace();
				}		
				
				return dt_result;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				refreshView.dismiss();
				studentAnswerList.clear();
				if(result != null && result.getRowCount()>0){
					for(int i=0;i<result.getRowCount();i++){
						try {
							studentAnswerList.add(new StudentAnswer(result.getCell(i, "studentID"), 
									result.getCell(i, "studentName"), result.getCell(i, "photoPath"), 
									result.getCell(i, "teacherReply"), result.getCell(i, "hasChecked")));							
							
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
					studentNum = result.getRowCount();  //有多少个学生答题
					currentStudentID = studentAnswerList.get(0).getStudentID(); //当前是哪个学生的答案
					currentStudentNum = 0; //当前是第几个学生作答
					tvStudentName.setText(studentAnswerList.get(0).getStudentName().trim());
					etReply.setText(studentAnswerList.get(0).getTeacherReply().trim());
					answerURL = studentAnswerList.get(0).getPhotoPath().trim();
					if(answerURL.equals("")){
						ivQuestionPic.setVisibility(View.GONE);						
					}else{						
						setImageView(ivQuestionPic, answerURL);
						ivQuestionPic.setVisibility(View.VISIBLE);
					}
				}
			}
			
		}.execute(questionID,useID);
	}
	
	public void APPCheckSubjectiveQuestion(String questionID, String useID, String studentID,String teacherReply){
		
		this.refreshView = new RefreshView(this,  R.style.full_screen_dialog);
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
				String result = "";
				// 方法名
				String methodName = "APP_checkSubjectiveQuestion";
				//参数Map
				Map<String,String> paramsMap = new HashMap<String, String>();
				String tokenID ="";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("questionID",params[0].toString());
				paramsMap.put("useID",params[1].toString());
				paramsMap.put("studentID",params[2].toString());
				paramsMap.put("teacherReply",params[3].toString());				
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
					Log.i("error", "APP_checkSubjectiveQuestion()...出错了。。。");
					e.printStackTrace();
				}	
				
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				refreshView.dismiss();
				//showToast(result.trim());
				try{
					if(Integer.parseInt(result.trim()) <=0){
						showToast("判题失败");
					}
				}catch(Exception ex){
					ex.printStackTrace();
					//showToast(ex.getMessage());
				}
			}
			
		}.execute(questionID,useID,studentID,teacherReply);
	}
	
	
	//描述本次试卷包含的题目的类
	public class Question{
		
		private String questionID ;
		private String questionCode;
		private String questionFilePath;
		private String typeBelong;
		
		public Question(String questionID,String questionCode,String questionFilePath,String typeBelong){
			this.questionID = questionID;
			this.questionCode = questionCode;
			this.questionFilePath = questionFilePath;
			if(questionFilePath.equals("anyType{}")){
				this.questionFilePath ="";
			}
			this.typeBelong = typeBelong;
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
		public String getQuestionFilePath() {
			return questionFilePath;
		}
		public void setQuestionFilePath(String questionFilePath) {
			this.questionFilePath = questionFilePath;
		}
		public String getTypeBelong() {
			return typeBelong;
		}
		public void setTypeBelong(String typeBelong) {
			this.typeBelong = typeBelong;
		}		
	}
	
	//用来存放 某一道题目学生的答案 的类
	public class StudentAnswer{
		
		private String studentID;
		private String studentName;
		private String photoPath;
		private String teacherReply;
		private String hasChecked;
		
		public StudentAnswer( String studentID, String studentName, String photoPath, String teacherReply, String hasChecked){
			
			this.studentID = studentID;
			this.studentName = studentName;
			this.photoPath = photoPath;
			this.teacherReply = teacherReply;
			this.hasChecked = hasChecked;
			
			if(photoPath.equals("anyType{}")){
				this.photoPath = "";
			}
			if(teacherReply.equals("anyType{}")){
				this.teacherReply = "";
			}
		}
		
		public String getStudentID() {
			return studentID;
		}
		public void setStudentID(String studentID) {
			this.studentID = studentID;
		}
		public String getStudentName() {
			return studentName;
		}
		public void setStudentName(String studentName) {
			this.studentName = studentName;
		}
		public String getPhotoPath() {
			return photoPath;
		}
		public void setPhotoPath(String photoPath) {
			this.photoPath = photoPath;
		}
		public String getTeacherReply() {
			return teacherReply;
		}
		public void setTeacherReply(String teacherReply) {
			this.teacherReply = teacherReply;
		}
		public String getHasChecked() {
			return hasChecked;
		}
		public void setHasChecked(String hasChecked) {
			this.hasChecked = hasChecked;
		}
		
	}
	
	// 窗口提示信息；
	public void showToast(String toastMessage){
		Toast toast =  Toast.makeText(SubjectiveHomeworkDetailActivity.this, toastMessage, Toast.LENGTH_SHORT);
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
	
	//以下为手势监听方法
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
			getNextQuestion();
			
		}
		// 从左到右滑动
		else if(e2.getX() - e1.getX() > flingMinXDistance 
				&& Math.abs(e1.getY() - e2.getY()) < flingMaxYDistance
				&& Math.abs(velocityX) > flingMinVelocityX){
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
	  
	//获取上一道题目的答案
	public void getLastQuestion(){
		if(currentStudentNum == 0){
			showToast("当前为第一个学生答案");
		}else{
			studentAnswerList.get(currentStudentNum).setTeacherReply(etReply.getText().toString().trim());
			APPCheckSubjectiveQuestion(currentQuestionID, useID, currentStudentID, etReply.getText().toString().trim());
			currentStudentNum --;
			currentStudentID = studentAnswerList.get(currentStudentNum).getStudentID();
			tvStudentName.setText(studentAnswerList.get(currentStudentNum).getStudentName().trim());
			etReply.setText(studentAnswerList.get(currentStudentNum).getTeacherReply().trim());
			answerURL = studentAnswerList.get(currentStudentNum).getPhotoPath().trim();
			if(answerURL.equals("")){
				ivQuestionPic.setVisibility(View.GONE);						
			}else{						
				setImageView(ivQuestionPic, answerURL);
				ivQuestionPic.setVisibility(View.VISIBLE);
			}
		}
	}
	
	//获取下一道题目
	public void getNextQuestion(){
		if(questionNum == 0 || studentNum == 0) {// 没有数据
			showToast("没有学生作答");
		}else if(currentStudentNum +1 == studentNum){
			//showToast("当前为最后一个学生答案");
			
			if(this.nextQuestionDialog == null){
				View view = LayoutInflater.from(SubjectiveHomeworkDetailActivity.this).
						inflate(R.layout.question_next, null);
				this.nextQuestionDialog = new Dialog(SubjectiveHomeworkDetailActivity.this, R.style.alertdialog_style);
				this.nextQuestionDialog.setContentView(view);
				this.nextQuestionDialog.setCanceledOnTouchOutside(true);
				this.nextQuestionDialog.show();
				
				//添加点击事件
				view.findViewById(R.id.ivSummary).setOnClickListener(new MyOnclickListener());
				view.findViewById(R.id.ivNextQuestion).setOnClickListener(new MyOnclickListener());
				}else{
				nextQuestionDialog.show();
			}
			APPCheckSubjectiveQuestion(currentQuestionID, useID, currentStudentID, etReply.getText().toString().trim());
		}else{
			studentAnswerList.get(currentStudentNum).setTeacherReply(etReply.getText().toString().trim());
			APPCheckSubjectiveQuestion(currentQuestionID, useID, currentStudentID, etReply.getText().toString().trim());
			currentStudentNum ++;
			currentStudentID = studentAnswerList.get(currentStudentNum).getStudentID();
			tvStudentName.setText(studentAnswerList.get(currentStudentNum).getStudentName().trim());
			etReply.setText(studentAnswerList.get(currentStudentNum).getTeacherReply().trim());
			answerURL = studentAnswerList.get(currentStudentNum).getPhotoPath().trim();
			if(answerURL.equals("")){
				ivQuestionPic.setVisibility(View.GONE);						
			}else{						
				setImageView(ivQuestionPic, answerURL);
				ivQuestionPic.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private class MyOnclickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()){
			case R.id.ivSummary://总结共性错误
				Intent intent = new Intent(SubjectiveHomeworkDetailActivity.this, SummaryActivity.class);
				intent.putExtra("useID", useID);
				intent.putExtra("questionID", currentQuestionID);
				startActivity(intent);
				break;
			case R.id.ivNextQuestion://切换到下一道题目
				if(currentQuestionNum+1 ==questionNum){
					showToast("当前是最后一道题");
				}else{
					currentQuestionNum ++;
					changeQuestion(currentQuestionNum);
				}
				nextQuestionDialog.dismiss();
				break;
			}
		}
		
	}
}
