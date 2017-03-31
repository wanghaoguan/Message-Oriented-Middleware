package com.ttqeducation.activitys.study;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.R;
import com.ttqeducation.R.layout;
import com.ttqeducation.activitys.study.TaskTypeChooseActivity.ViewChild;
import com.ttqeducation.activitys.system.LoginActivity;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.KnowledgePointRanking;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author 王勤为
 * 
 *         这个界面是 单元测试、期中和期末作业完成详情界面（也即是显示班级的排名情况）
 */

public class UnitTestResultDetailActivity extends Activity {

	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；
		
	private MyListView myListView = null;
	private MyAdapter mAdapter = null; // 得到一个MyAdapter对象,用于与listview绑定；;

	// 存放单元、期中、期末测试完成情况详情；(王勤为只需向这个列表中添加数据即可)
	private List<KnowledgePointRanking> listKnowledgePointRankings = new ArrayList<KnowledgePointRanking>(); 
	private RefreshView refreshView = null;

	// 条件变量，从上一个界面获取
	private String useID = "";		// 王勤为需要；

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_unit_test_result_detail);		
		getDataFromIntent();
		
		
//		this.initView();
//		this.mAdapter = new MyAdapter(this);
//		generateData();
//		this.myListView.setAdapter(this.mAdapter);
		
		String studentID = UserInfo.getInstance().studentID;
		this.get_studentTestDetail(studentID, this.useID);
	}

	public void getDataFromIntent() {
		this.useID = getIntent().getStringExtra("useID");
		Log.i("lvjie", "useID="+this.useID);
	}

	public void initView() {
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("班级排名");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				UnitTestResultDetailActivity.this.finish();
			}
		});

		this.myListView = (MyListView) super
				.findViewById(R.id.listView_unit_test_details);
		this.myListView.setPullRefreshEnable(false); // 不提供下拉刷新功能；
		this.myListView.setPullLoadEnable(false); // 不提供上拉加载功能；
		this.mAdapter = new MyAdapter(this);
		this.myListView.setAdapter(this.mAdapter);
		
		this.mAdapter = new MyAdapter(this);
//		generateData();
		this.myListView.setAdapter(this.mAdapter);
		
		Log.i("lvjie", "UnitTestResultDetailActivity-->onCreate()");
		
		if(this.listKnowledgePointRankings.size() == 0){
			super.findViewById(R.id.noInfo_textView).setVisibility(View.VISIBLE);
		}
	}

	// 新建一个类继承BaseAdapter，实现listview与数据的绑定
	private class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater; // 得到一个LayoutInfalter对象用来导入布局

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return listKnowledgePointRankings.size(); // 返回数组的长度
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewChild viewChild;
			Log.i("lvjie", "View getView");
			if (view == null) {
				view = mInflater.inflate(R.layout.unit_test_detail_item_one, null);
				viewChild = new ViewChild();
				/* 得到各个控件的对象 */
				viewChild.rankingTextView = (TextView) view
						.findViewById(R.id.unitTestRank_textView);
				viewChild.stuNameTextview = (TextView) view
						.findViewById(R.id.stuName_textView);
				viewChild.testScoreTextview = (TextView) view
						.findViewById(R.id.testScore_textview);
				viewChild.itemBgLayout = (LinearLayout) view.findViewById(R.id.item_bg_layout);

				view.setTag(viewChild); // 绑定ViewHolder对象
			} else {
				viewChild = (ViewChild) view.getTag(); // 取出ViewHolder对象
			}

			/* 设置TextView显示的内容，即我们存放在动态数组中的数据 */			
			if(listKnowledgePointRankings.get(position).getRanking() == 1){
				
				viewChild.itemBgLayout.setBackgroundResource(R.drawable.linearlayout_frame_circle_red);
				viewChild.rankingTextView.setBackgroundResource(R.drawable.btn_circle_white);
				viewChild.rankingTextView.setTextColor(getResources().getColor(R.color.textRed));
				
				viewChild.stuNameTextview.setTextColor(getResources().getColor(R.color.textWhite));
				viewChild.testScoreTextview.setTextColor(getResources().getColor(R.color.textWhite));
				
			}else if(listKnowledgePointRankings.get(position).getRanking() == 2){
				
				viewChild.itemBgLayout.setBackgroundResource(R.drawable.linearlayout_frame_circle_orange);
				viewChild.rankingTextView.setBackgroundResource(R.drawable.btn_circle_white);
				viewChild.rankingTextView.setTextColor(getResources().getColor(R.color.textOrange));
				
				viewChild.stuNameTextview.setTextColor(getResources().getColor(R.color.textWhite));
				viewChild.testScoreTextview.setTextColor(getResources().getColor(R.color.textWhite));
				
			}else if(listKnowledgePointRankings.get(position).getRanking() == 3){
				
				viewChild.itemBgLayout.setBackgroundResource(R.drawable.linearlayout_frame_circle_blue);
				viewChild.rankingTextView.setBackgroundResource(R.drawable.btn_circle_white);
				viewChild.rankingTextView.setTextColor(getResources().getColor(R.color.textBlue1));
				
				viewChild.stuNameTextview.setTextColor(getResources().getColor(R.color.textWhite));
				viewChild.testScoreTextview.setTextColor(getResources().getColor(R.color.textWhite));
				
			}else{
				
				viewChild.itemBgLayout.setBackgroundResource(R.drawable.linearlayout_frame_circle_white);
				viewChild.rankingTextView.setBackgroundResource(R.drawable.btn_circle_green);
				viewChild.rankingTextView.setTextColor(getResources().getColor(R.color.textWhite));
				
				viewChild.stuNameTextview.setTextColor(getResources().getColor(R.color.textGray));
				viewChild.testScoreTextview.setTextColor(getResources().getColor(R.color.textGray));
				
			}
			
			
			viewChild.rankingTextView.setText(""+listKnowledgePointRankings.get(position).getRanking());

			viewChild.stuNameTextview.setText(listKnowledgePointRankings.get(position).getStuName());
			viewChild.testScoreTextview.setText("掌握度："+listKnowledgePointRankings.get(position).getScore());

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
		public TextView rankingTextView; // 排名;
		public TextView stuNameTextview; // 姓名；
		public TextView testScoreTextview; // 分数；
		
		public LinearLayout itemBgLayout;	// 条目得背景颜色；		
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
					KnowledgePointRanking KnowledgePointRanking;
					int count = result.getRowCount();
					for (int i = 0; i < count; i++) {
						try {
							int rank = Integer.parseInt(result.getCell(i, "classRank"));
							String stuName = result.getCell(i, "studentName");
							String score = result.getCell(i, "totalScore");

							KnowledgePointRanking = new KnowledgePointRanking(rank, stuName, score);
							UnitTestResultDetailActivity.this.listKnowledgePointRankings.add(KnowledgePointRanking);
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				// 关闭刷新；
				refreshView.dismiss();
				// 获取完数据后，初始化界面
				UnitTestResultDetailActivity.this.initView();
			};
		}.execute(studentID, useID);
	}
}
