package com.ttqeducation.teacher.activitys.teach;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.tools.DesUtil;

import android.R.integer;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 用于教师填写共性错误
 * @author hxy
 *
 */
public class SummaryActivity extends Activity {

	private String useID="";
	private String questionID="";
	private LinearLayout titleBackLayout;
	private TextView titleTextView;	
	private EditText etSummary;
	private LinearLayout llOK;
	private RefreshView refreshView ;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_summary);
		getContentFromIntent();
		initView();
	}

	

	private void getContentFromIntent() {
		// TODO Auto-generated method stub
		this.useID = getIntent().getStringExtra("useID").toString().trim();
		
		this.questionID = getIntent().getStringExtra("questionID").toString().trim();
		
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		this.titleBackLayout =(LinearLayout)super.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout);
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SummaryActivity.this.finish();
			}
		});
		this.titleTextView = (TextView)super.findViewById(R.id.action_bar).findViewById(R.id.title_text);
		this.titleTextView.setText("共性问题总结");
		this.etSummary = (EditText)findViewById(R.id.etSummary);
		this.llOK = (LinearLayout)findViewById(R.id.llOK);		
//		this.llOK.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				teacherCommitSummary();
//				//打包成apk之后，运行到下一句话出错--------------------------------------------------------------------
//				//问题出在对这个函数的调用上，如果函数中使用异步方式访问网络asyncTask，就会有异常
//				//APPCommitSummaryByQuestion(questionID, useID, etSummary.getText().toString().trim());
//			}
//		});	
		this.llOK.setOnClickListener(new MyOnClickListener());
		
		APPGetSummaryByQuestion(questionID, useID);
	}
	class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.llOK:
				//teacherCommitSummary();
				APPCommitSummaryByQuestion(questionID, useID, etSummary.getText().toString().trim());
			}
		}
		
	}
	/**
	 * 教师查看每一道题目写总结
	 * @param questionID
	 * @param useID
	 */
	public void APPGetSummaryByQuestion(String questionID ,	String useID){
		
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
				String methodName = "APP_getSummaryByQuestion";
				//参数Map
				Map<String,String> paramsMap = new HashMap<String,String>();
				String tokenID ="";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("questionID", params[0].toString());
				paramsMap.put("useID", params[1].toString());
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
					Log.i("error", "APP_getSummaryByQuestion()...出错了。。。");
					e.printStackTrace();
				}		
				
				return dt_result;
				
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				refreshView.dismiss();
				super.onPostExecute(result);
				if(result != null && result.getRowCount()>0){
					try {
						etSummary.setText(result.getCell(0, "summary"));
						if(result.getCell(0, "summary").trim().equals("anyType{}")){
							etSummary.setText("");
						}
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
			
		}.execute(questionID,useID);
	}
	
	/**
	 * 教师为每一道题目写总结
	 * @param questionID
	 * @param useID
	 * @param summary
	 */
	public void APPCommitSummaryByQuestion(String questionID, String useID, String summary){
		this.refreshView= new RefreshView(this,R.style.full_screen_dialog);
		new AsyncTask<Object, Object, String>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
				//Toast.makeText(getApplicationContext(), "onPreExecute", Toast.LENGTH_SHORT).show();
				
			}

			@Override
			protected String doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DesUtil.addTokenIDToSchoolWS(); // 向服务端添加tokenID;
				
				// 用模拟的数据库表存储返回结果
				String result = "";
				// 方法名
				String methodName = "APP_commitSummaryByQuestion";
				//参数Map
				Map<String,String> paramsMap = new HashMap<String,String>();
				String tokenID ="";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("questionID", params[0].toString());
				paramsMap.put("useID", params[1].toString());
				paramsMap.put("summary", params[2].toString());
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
					Log.i("error", "--APP_commitSummaryByQuestion()...出错了。。。" + e.toString());
					e.printStackTrace();
				}		
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				refreshView.dismiss();
				//之前打包成apk会有异常是因为下面的string转化为int。
				//打包apk之前提交总结成功返回的结果是1，打包apk之后提交总结成功返回的结果是"1;}"？？？
//				if(Integer.valueOf(result)>=0){//插入成功
//					Toast.makeText(getApplicationContext(), "总结已提交", Toast.LENGTH_SHORT).show();
//					SummaryActivity.this.finish();
//				}else{
//					Toast.makeText(getApplicationContext(), "总结提交失败", Toast.LENGTH_SHORT).show();
//				}
				if(result != null){
					if("1".equals(result.substring(0, 1))){
						Toast.makeText(getApplicationContext(), "总结已提交", Toast.LENGTH_SHORT).show();
						SummaryActivity.this.finish();
					}else{
						Toast.makeText(getApplicationContext(), "总结提交失败", Toast.LENGTH_SHORT).show();
					}
					//Toast.makeText(getApplicationContext(), "提交结果： " + result.substring(0, 1), Toast.LENGTH_SHORT).show();
				}	
			}
			
		}.execute(questionID,useID,summary);
	}
	
	public void teacherCommitSummary(){
		new AsyncTask<Object, Object, String>(){

			@Override
			protected String doInBackground(Object... params) {
				// TODO Auto-generated method stub
				String result = null;
				String methodName = "APP_commitSummaryByQuestion";
				
				String questionID = getIntent().getStringExtra("questionID");
				String useID = getIntent().getStringExtra("useID");
				String summary = etSummary.getText().toString();
				DesUtil.addTokenIDToSchoolWS();
				String TokenID = "";
				try {
					TokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("questionID", questionID);
				paramsMap.put("useID", useID);
				paramsMap.put("summary", summary);
				paramsMap.put("TokenID", TokenID);
				
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				SharedPreferences pre = getSharedPreferences("TTQAndroid", Activity.MODE_PRIVATE);
				getDataTool.setURL(pre.getString("school_service_url", null));
				try {
					result = getDataTool.getDataAsString(methodName, paramsMap);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result != null){
					if(Integer.valueOf(result) > 0){
						showToast("提交成功");
					}else{
						showToast("提交失败");
					}
				}
			}
			
		}.execute();
	}
	// 窗口提示信息；
	public void showToast(String toastMessage){
		Toast toast =  Toast.makeText(SummaryActivity.this, toastMessage, Toast.LENGTH_SHORT);
		toast.show();
	}

}
