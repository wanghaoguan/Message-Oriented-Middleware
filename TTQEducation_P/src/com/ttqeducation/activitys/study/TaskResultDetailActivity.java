package com.ttqeducation.activitys.study;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

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

/**
 * 这个是显示作业结果的详细信息的（一道题一道题）
 * 
 * @author 王勤为
 * 
 *         这个界面是 作业完成详情界面
 */

public class TaskResultDetailActivity extends Activity {

	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；
		
	private MyListView myListView = null;
	private MyAdapter mAdapter = null; // 得到一个MyAdapter对象,用于与listview绑定；;

	private List<TaskCompletion> listTaskCompletions = new ArrayList<TaskCompletion>(); // 存放作业完成情况；
	private RefreshView refreshView = null;

	// 条件变量，从上一个界面获取（吕杰待做）
	private String taskName = ""; // 测试名称；如：家庭作业完成情况;
	private int useID = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_result_detail);

		getDataFromIntent();
		// this.initView();
		// this.mAdapter = new MyAdapter(this);
		// generateData();
		// this.myListView.setAdapter(this.mAdapter);
		
		String studentID = UserInfo.getInstance().studentID;

		this.get_studentDailyTaskDetail(studentID, useID + "");
	}

	public void getDataFromIntent() {
		this.taskName = getIntent().getStringExtra("taskName");
		this.useID = getIntent().getIntExtra("useID", 0);
	}

	public void initView() {
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		// 修改标题栏标题，把 情况 改为 详情；
		this.titleTextView.setText(""
				+ this.taskName.substring(0, (this.taskName.length() - 2))
				+ "详情");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				TaskResultDetailActivity.this.finish();
			}
		});

		this.myListView = (MyListView) super
				.findViewById(R.id.listView_task_details);
		this.myListView.setPullRefreshEnable(false); // 不提供下拉刷新功能；
		this.myListView.setPullLoadEnable(false); // 不提供上拉加载功能；
		this.mAdapter = new MyAdapter(this);
		this.myListView.setAdapter(this.mAdapter);
		
		if(this.listTaskCompletions.size() == 0){
			super.findViewById(R.id.noInfo_textView).setVisibility(View.VISIBLE);
		}
	}

	// 产生数据； 用来测试， 以后不需要了；
	public void generateData() {
		TaskCompletion taskCompletion;
		for (int i = 1; i < 10; i++) {
			if (i % 3 == 0) {
				taskCompletion = new TaskCompletion("做一做  1." + i, "第  " + i
						+ " 页", 1);
			} else if (i % 3 == 1) {
				taskCompletion = new TaskCompletion("做一做  1." + i, "第  " + i
						+ " 页", 2);
			} else {
				taskCompletion = new TaskCompletion("做一做  1." + i, "第  " + i
						+ " 页", 3);
			}
			this.listTaskCompletions.add(taskCompletion);
		}
	}

	/**
	 * 获取学生的的详细答题情况
	 * 
	 * @param timeType
	 *            ,时间条件类型 day week until_week
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
					System.out.println(result.toString());
					TaskCompletion taskCompletion;
					int count = result.getRowCount();
					for (int i = 0; i < count; i++) {
						try {
							String pageNum = result.getCell(i, "textPageNum");
							String question = result.getCell(i, "questionCode");
							String isAnswer = result.getCell(i, "isAnswer");
							String isRight = result.getCell(i, "isRight");

							// 转化题目状态1 表示正确 2 表示错误 3 表示未答；
							int taskState = Integer.parseInt(GeneralTools
									.getInstance().getTaskState(isAnswer,
											isRight));

							// System.out.println(pageNum + question +
							// isAnswer+"答题状态："+taskState);

							taskCompletion = new TaskCompletion(question, "第 "
									+ pageNum + " 页", taskState);
							listTaskCompletions.add(taskCompletion);
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				// 关闭刷新；
				refreshView.dismiss();
				// 获取完数据后，初始化界面
				TaskResultDetailActivity.this.initView();
			};
		}.execute(studentID, useID);
	}

	/**
	 * 获得学生某次测试的考试问题
	 * 
	 * @param studentID
	 * @param useID
	 */
	public void get_studentTestDetail(String studentID, String useID) {
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
				String methodName = "APP_getClassRank_byTest";
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
					Log.i("error", "get_studentTestDetail()...出错了。。。");
					e.printStackTrace();
				}
				return dt_student;
			}

			protected void onPostExecute(DataTable result) {
				if (result != null) {
					System.out.println(result.toString());//测试代码，之后需要删除
					TaskCompletion taskCompletion;
					int count = result.getRowCount();
					for (int i = 0; i < count; i++) {
						try {
							String pageNum = result.getCell(i, "textPageNum");
							String question = result.getCell(i, "questionCode");
							String isAnswer = result.getCell(i, "isAnswer");
							String isRight = result.getCell(i, "isRight");

							// 转化题目状态1 表示正确 2 表示错误 3 表示未答；
							int taskState = Integer.parseInt(GeneralTools
									.getInstance().getTaskState(isAnswer,
											isRight));

							// System.out.println(pageNum + question +
							// isAnswer+"答题状态："+taskState);

							taskCompletion = new TaskCompletion(question, "第 "
									+ pageNum + " 页", taskState);
							listTaskCompletions.add(taskCompletion);
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				// 关闭刷新；
				refreshView.dismiss();
				// 获取完数据后，初始化界面
				TaskResultDetailActivity.this.initView();
			};
		}.execute(studentID, useID);
	}

	// 新建一个类继承BaseAdapter，实现listview与数据的绑定
	private class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater; // 得到一个LayoutInfalter对象用来导入布局

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return listTaskCompletions.size(); // 返回数组的长度
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewChild viewChild;
			if (view == null) {
				view = mInflater.inflate(R.layout.task_detail_item_one, null);
				viewChild = new ViewChild();
				/* 得到各个控件的对象 */
				viewChild.taskDetailPic = (View) view
						.findViewById(R.id.task_detail_pic);
				viewChild.taskQuestionNameTextview = (TextView) view
						.findViewById(R.id.task_questionName_textview);
				viewChild.taskQuestionPageTextview = (TextView) view
						.findViewById(R.id.task_questionPage_textview);
				viewChild.rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);

				view.setTag(viewChild); // 绑定ViewHolder对象
			} else {
				viewChild = (ViewChild) view.getTag(); // 取出ViewHolder对象
			}

			/* 设置TextView显示的内容，即我们存放在动态数组中的数据 */
			viewChild.taskDetailPic
					.setBackgroundResource(getPicIDByTaskState(listTaskCompletions
							.get(position).getTaskState()));
			viewChild.taskQuestionNameTextview.setText(listTaskCompletions.get(
					position).getTaskName());
			viewChild.taskQuestionPageTextview.setText(listTaskCompletions.get(
					position).getTaskPage());
			
			if(listTaskCompletions.get(position).getTaskState() == 1){
				viewChild.rightLayout.setBackgroundResource(R.drawable.linearlayout_frame_circle_right_blue);
			}else if(listTaskCompletions.get(position).getTaskState() == 2){
				viewChild.rightLayout.setBackgroundResource(R.drawable.linearlayout_frame_circle_right_red);
			}else {
				viewChild.rightLayout.setBackgroundResource(R.drawable.linearlayout_frame_circle_right_orange);
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

		// 通过作业完成的正确与否来获取对应的图片资源；
		public int getPicIDByTaskState(int state) {
			if (state == 1) {
				return R.drawable.right_count;
			} else if (state == 2) {
				return R.drawable.wrong_count;
			} else {
				return R.drawable.noanswer_count;
			}
		}
		
	}

	// 存放列表子项的控件，用于在MyAdapter中设置;
	public final class ViewChild {
		public View taskDetailPic; // 作业正确与错误对应的图片;
		public TextView taskQuestionNameTextview; // 作业名；
		public TextView taskQuestionPageTextview; // 作业页码；
		
		public LinearLayout rightLayout;		// 右边的背景；
	}

}
