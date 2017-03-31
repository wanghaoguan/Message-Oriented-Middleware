package com.ttqeducation.activitys.study;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.R;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.TaskCompletion;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.MyListView;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DesUtil;
import com.ttqeducation.tools.GeneralTools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 错题详情界面；
 * @author lvjie
 *
 */
public class ErrorQuestionDetailsActivity extends Activity {

	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；
	
	private TextView subjectNameTextView = null;
	private TextView errorQuestionsDateTextView = null;

	private String dateString = ""; // 存放错题时间，用于界面显示；
	private MyListView myListView = null;
	private MyAdapter mAdapter = null; // 得到一个MyAdapter对象,用于与listview绑定；
	private List<TaskCompletion> listErrorQuestionDetails = new ArrayList<TaskCompletion>(); // 存放错题汇总的内容；
	private RefreshView refreshView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_error_question_details);

		// generateData();
		getDataFromIntent();
		// initView();
		// mAdapter = new MyAdapter(this);
		// this.myListView.setAdapter(mAdapter);
		String subjectID = GeneralTools.getInstance().getSubjectIDByName(
				HomeworkArrangementFragment.subjectName);
		String studentID = UserInfo.getInstance().studentID;
		// System.out.println("科目ID"+subjectID);
		// System.out.println("学生ID"+studentID);
		this.getWrongQuestionList(studentID, this.dateString, subjectID);
	}

	public void initView() {
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("错题详情");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ErrorQuestionDetailsActivity.this.finish();
			}
		});

		this.subjectNameTextView = (TextView) super
				.findViewById(R.id.subjectName_textView);
		this.errorQuestionsDateTextView = (TextView) super
				.findViewById(R.id.errorQuestionDate_textView);
		// 注意 前一个界面就是
		this.subjectNameTextView.setText("科目: "
				+ HomeworkArrangementFragment.subjectName);
		this.errorQuestionsDateTextView.setText("答题时间: " + this.dateString);

		this.myListView = (MyListView) super
				.findViewById(R.id.listView_errorQuestion_details);
		this.myListView.setPullRefreshEnable(false); // 不提供下拉刷新功能；
		this.myListView.setPullLoadEnable(false); // 不提供上拉加载功能；

		mAdapter = new MyAdapter(this);
		this.myListView.setAdapter(mAdapter);
		
		showNoHomeworkInfoInView();
	}

	// 从前一个界面获取数据；
	public void getDataFromIntent() {
		this.dateString = getIntent().getStringExtra("dateString");
	}
	
	// 当没有家庭作业就显示无作业；
	public void showNoHomeworkInfoInView(){
		TextView noErrorQustionTextView = (TextView) super.findViewById(R.id.noErrorQustion_textView);
		if(this.listErrorQuestionDetails.size() <= 0){
			noErrorQustionTextView.setVisibility(View.VISIBLE);
		}		
	}

	// 模拟数据；正式不需要；从服务器端获取；
	public void generateData() {
		TaskCompletion taskCompletion;
		for (int i = 1; i < 12; i++) {
			taskCompletion = new TaskCompletion("练习一十一." + i, "页码: " + i,
					"正确答案: " + i);
			listErrorQuestionDetails.add(taskCompletion);
		}
	}

	/**
	 * 获取学生的错题汇总数据
	 * 
	 * @param timeType
	 *            ,时间条件类型 day week until_week
	 */
	public void getWrongQuestionList(String studentID, String date,
			String subjectID) {
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
				DataTable dt_student = new DataTable();
				// 方法名
				String methodName = "APP_getWrongWork_byDay";
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
				paramsMap.put("date", params[1].toString());
				paramsMap.put("subjectID", params[2].toString());
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
					Log.i("error", "getWrongQuestionList()...出错了。。。");
					e.printStackTrace();
				}
				return dt_student;
			}

			protected void onPostExecute(DataTable result) {
				if (result != null) {
					System.out.println("返回数据："+result.toString());
					TaskCompletion taskCompletion;
					int count = result.getRowCount();
					for (int i = 0; i < count; i++) {
						try {
							String pageNum = result.getCell(i, "textPageNum");
							String question = result.getCell(i, "questionCode");
							String rightAnswer = result.getCell(i, "answer");
							System.out
									.println(pageNum + question + rightAnswer);
							taskCompletion = new TaskCompletion(i + 1 + "."
									+ question, "第 " + pageNum + " 页", "正确答案:"
									+ rightAnswer);
							listErrorQuestionDetails.add(taskCompletion);
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				// 关闭刷新；
				refreshView.dismiss();
				// 获取完数据后，初始化界面
				
				ErrorQuestionDetailsActivity.this.initView();
			};
		}.execute(studentID, date, subjectID);
	}

	// 新建一个类继承BaseAdapter，实现listview与数据的绑定
	private class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater; // 得到一个LayoutInfalter对象用来导入布局

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return listErrorQuestionDetails.size(); // 返回数组的长度
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewChild viewChild;
			if (view == null) {
				view = mInflater.inflate(
						R.layout.error_question_detail_item_one1, null);
				viewChild = new ViewChild();
				/* 得到各个控件的对象 */
				viewChild.errorQuestionName = (TextView) view
						.findViewById(R.id.error_questionName_textview);
				viewChild.errorQuestionAnswer = (TextView) view
						.findViewById(R.id.error_questionAnswer_textview);
				viewChild.errorQuestionPage = (TextView) view
						.findViewById(R.id.error_questionPage_textview);
				viewChild.leftImageView = (ImageView) view.findViewById(R.id.leftTime_pic);
				viewChild.rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);

				view.setTag(viewChild); // 绑定ViewHolder对象
			} else {
				viewChild = (ViewChild) view.getTag(); // 取出ViewHolder对象
			}

			/* 设置TextView显示的内容，即我们存放在listHomeworkArrageConditions中的数据 */
			viewChild.errorQuestionName.setText(listErrorQuestionDetails.get(
					position).getTaskName());
			viewChild.errorQuestionAnswer.setText(listErrorQuestionDetails.get(
					position).getRightAnswer());
			viewChild.errorQuestionPage.setText(listErrorQuestionDetails.get(
					position).getTaskPage());
			
			// 控制左边时钟图；
			if(position == 0){
				viewChild.leftImageView.setBackgroundResource(R.drawable.time_pic1);
			}else if(position == listErrorQuestionDetails.size()-1){
				viewChild.leftImageView.setBackgroundResource(R.drawable.time_pic3);
			}else {
				viewChild.leftImageView.setBackgroundResource(R.drawable.time_pic2);
			}
			
			// 控制右边背景色；
			int value = position % 9;
			if(value <= 2){
				viewChild.rightLayout.setBackgroundResource(R.drawable.linearlayout_frame_circle_right_red);
			}else if(value <=5){
				viewChild.rightLayout.setBackgroundResource(R.drawable.linearlayout_frame_circle_right_orange);
			}else{
				viewChild.rightLayout.setBackgroundResource(R.drawable.linearlayout_frame_circle_right_blue);
			}

			return view;
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
	}

	// 存放列表子项的控件，用于在MyAdapter中设置;
	public final class ViewChild {
		public TextView errorQuestionName; // 错误题目名称;
		public TextView errorQuestionPage; // 错误题目页码；
		public TextView errorQuestionAnswer; // 错误题目正确答案；
		
		public ImageView leftImageView;		// 左边的时钟图；
		public LinearLayout rightLayout;	// 右边的背景；
	}

}
