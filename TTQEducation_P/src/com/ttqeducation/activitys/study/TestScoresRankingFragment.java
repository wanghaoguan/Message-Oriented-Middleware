package com.ttqeducation.activitys.study;

/**
 *  成绩排名内容部分；
 *  
 *  王勤为在此文件中写 调用服务端的代码，获取 成绩排名情况信息；只需向listTestScoreRankings添加数据即可；
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.R;
import com.ttqeducation.activitys.study.TaskTypeChooseActivity.ViewChild;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.HomeworkArrageCondition;
import com.ttqeducation.beans.KnowledgePoint;
import com.ttqeducation.beans.TestScoreRanking;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.MyListView;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DateUtil;
import com.ttqeducation.tools.DesUtil;
import com.ttqeducation.tools.GeneralTools;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TestScoresRankingFragment extends Fragment {

	List<TestScoreRanking> listTestScoreRankings = new ArrayList<TestScoreRanking>();
	private MyListView myListView = null;
	private MyAdapter mAdapter = null; // 得到一个MyAdapter对象,用于与listview绑定；

	private String subjectNameString = "";
	private RefreshView refreshView = null;
	public TestScoresRankingFragment() {

	}

	public TestScoresRankingFragment(String subjectName) {
		this.subjectNameString = subjectName;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View layout = inflater
				.inflate(R.layout.fragment_test_scores_ranking_content,
						container, false);

		//获取需要的参数  
		String subjectID = GeneralTools.getInstance().getSubjectIDByName(
				this.subjectNameString);
		String studentID = UserInfo.getInstance().studentID;
//		String termID = "201420151";
		String termID = UserInfo.getInstance().termID;
		this.getStudentTestScoreAndRank(subjectID, studentID, termID,layout);
		
		this.initView(layout);
//		generateData();
//		this.mAdapter = new MyAdapter(getActivity());
//		this.myListView.setAdapter(this.mAdapter);
//
//		showToast("TestScoresRankingFragment-->onCreateView()...."
//				+ this.subjectNameString);
//		Log.i("lvjie", "TestScoresRankingFragment-->onCreateView()...");
		return layout;
	}

	public void initView(View view) {
		this.myListView = (MyListView) view
				.findViewById(R.id.listView_testScoresRanking_content);
		this.myListView.setPullRefreshEnable(false); // 不提供下拉刷新功能；
		this.myListView.setPullLoadEnable(false); // 不提供上拉加载功能；
		this.myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			// 列表的点击事件；
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

			}
		});
		this.mAdapter = new MyAdapter(getActivity());
		this.myListView.setAdapter(this.mAdapter);

		Log.i("lvjie", "TestScoresRankingFragment-->onCreateView()...");
	}

	// 模拟数据；
	public void generateData() {
		TestScoreRanking testScoreRanking;
		Date date = new Date();
		for (int i = 0; i < 10; i++) {
			String dateString = DateUtil.convertDateToString("yyyy年MM月dd日",
					date);
			testScoreRanking = new TestScoreRanking("单元测试" + i, dateString,
					i + 1, 95 - i);

			listTestScoreRankings.add(testScoreRanking);
			date = DateUtil.getNextDay(date);
		}
	}

	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(getActivity(), toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}

	// 新建一个类继承BaseAdapter，实现listview与数据的绑定
	private class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater; // 得到一个LayoutInfalter对象用来导入布局

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return listTestScoreRankings.size(); // 返回数组的长度
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewChild viewChild;
			if (view == null) {
				view = mInflater.inflate(R.layout.test_ranking_detail_item_one1,
						null);
				viewChild = new ViewChild();
				/* 得到各个控件的对象 */
				viewChild.testNameTextView = (TextView) view
						.findViewById(R.id.testName_textview);
				viewChild.testTimeTextView = (TextView) view
						.findViewById(R.id.testTime_textview);
				viewChild.rankingTextView = (TextView) view
						.findViewById(R.id.testRanking_textview);
				viewChild.scoreTextView = (TextView) view
						.findViewById(R.id.testScore_textview);
				
				viewChild.leftLineImageView = (ImageView) view.findViewById(R.id.leftLine_img);
				viewChild.rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);
				
				view.setTag(viewChild); // 绑定ViewHolder对象
			} else {
				viewChild = (ViewChild) view.getTag(); // 取出ViewHolder对象
			}

			/* 设置TextView显示的内容，即我们存放在动态数组中的数据 */
			viewChild.testNameTextView.setText(listTestScoreRankings.get(
					position).getTestName());
			viewChild.testTimeTextView.setText("测试时间："+listTestScoreRankings.get(
					position).getTestTime());
			viewChild.rankingTextView.setText("成绩排名："
					+ listTestScoreRankings.get(position).getRanking());
			viewChild.scoreTextView.setText("得分：\n"
					+ listTestScoreRankings.get(position).getScore());
			
			if(position == listTestScoreRankings.size()-1){
				viewChild.leftLineImageView.setVisibility(View.GONE);
			}else {
				viewChild.leftLineImageView.setVisibility(View.VISIBLE);
			}
			
			int value = position%4;
			if(value == 0){
				viewChild.rightLayout.setBackgroundResource(R.drawable.linearlayout_frame_circle_right_red);
			}else if(value == 1){
				viewChild.rightLayout.setBackgroundResource(R.drawable.linearlayout_frame_circle_right_orange);
			}else if(value == 2){
				viewChild.rightLayout.setBackgroundResource(R.drawable.linearlayout_frame_circle_right_blue);
			}else {
				viewChild.rightLayout.setBackgroundResource(R.drawable.linearlayout_frame_circle_right_green);
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
				return R.drawable.small_right;
			} else if (state == 2) {
				return R.drawable.small_wrong;
			} else {
				return R.drawable.small_noanswer;
			}
		}
	}

	// 存放列表子项的控件，用于在MyAdapter中设置;
	public final class ViewChild {
		public TextView testNameTextView; // 测试名称;
		public TextView testTimeTextView; // 测试时间；
		public TextView rankingTextView; // 班级排名；
		public TextView scoreTextView; // 分数；
		
		public ImageView leftLineImageView;	// 左边的绿线；
		public LinearLayout rightLayout;	// 右边的背景色；
	}

	/**
	 * 获取学生的某科目的所有发生过的考试信息
	 * 
	 * @param timeType
	 *            ,时间条件类型 day week until_week
	 */
	public void getStudentTestScoreAndRank(String subjectID, String studentID,
			String termID,View layout) {
		this.refreshView = new RefreshView(this.getActivity(), R.style.full_screen_dialog);
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, DataTable>() {
			View layout = null;
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
				this.layout = (View) params[3];
				// 方法名
				String methodName = "report_stuRankOneTerm_eachTest_oneSubject";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("subjectID", params[0].toString().trim());
				paramsMap.put("studentID", params[1].toString().trim());
				paramsMap.put("termID", params[2].toString().trim());
				paramsMap.put("TokenID", tokenID);
				
				Log.i("lvjie", ""+params[0].toString()+"   "+params[1].toString()+"   "+params[2].toString());
				// 获取数据
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				// 从本地获取学校URL
				SharedPreferences pre = getActivity().getSharedPreferences("TTQAndroid", getActivity().MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				if (schoolURL == null) {// 如果没有值
					return null;
				}
				getDataTool.setURL(schoolURL);

				try {
					dt_student = GetDataByWS.getInstance().getDataAsTable(
							methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "getStudentTestScoreAndRank()...出错了。。。");
					e.printStackTrace();
				}
				return dt_student;
			}

			protected void onPostExecute(DataTable result) {

				if (result != null && result.getRowCount() != 0) {
					// 解析表格数据
					// 遍历
					int count = result.getRowCount();
					for (int i = 0; i < count; i++) {
						try {
							String testName = result.getCell(i, "testName");
							String testTime = result.getCell(i, "times");
							String times[] = testTime.split("T");			// 这行是吕杰添加的，用来切割时间；
							int ranking = Integer.valueOf(result.getCell(i,
									"ranks"));
							float score = Float.valueOf(result.getCell(i,
									"totalScore"));
							Log.i("lvjie", ""+testName+"  "+times[0]+"  "+ranking+"  "+score);
							TestScoreRanking testScoreRanking = new TestScoreRanking(
									testName, times[0], ranking, score);

							TestScoresRankingFragment.this.listTestScoreRankings.add(testScoreRanking);

						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} else {// 原始表没有数据
					System.out.println("原始表中没有数据！");
				}
				// 关闭刷新；
				refreshView.dismiss();

				TestScoresRankingFragment.this.initView(layout);

			};
		}.execute(subjectID, studentID, termID, layout);
	}
}
