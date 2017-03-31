package com.ttqeducation.activitys.study;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.R;
import com.ttqeducation.activitys.study.KnowledgePointRankingFragment.ViewChild;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.KnowledgePoint;
import com.ttqeducation.beans.TestScoreRanking;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.MyListView;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DesUtil;
import com.ttqeducation.tools.GeneralTools;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class KnowledgePointRankingActivity1 extends Activity {

	/********* 吕杰定义的变量 **********/
	// 标题栏部分；
	private RelativeLayout titleBackLayout = null; // 标题栏返回按钮；
	
	private MyListView myListView = null;
	private MyAdapter mAdapter = null; // 得到一个MyAdapter对象,用于与listview绑定；
	// 用来存放 知识点掌握情况 相关信息；
	private List<KnowledgePoint> listKnowledgePoints = new ArrayList<KnowledgePoint>();

	private String subjectNameStr = ""; // 记录当前是什么科目：语文，数学，外语；
	
	private RefreshView refreshView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_knowledge_point_ranking1);

		this.refreshView = new RefreshView(this,R.style.full_screen_dialog);
		getDataFromIntent();
		
		// 获取需要的参数 请吕杰传入科目名！！！！
		String subjectID = GeneralTools.getInstance().getSubjectIDByName(
				this.subjectNameStr);
		String studentID = UserInfo.getInstance().studentID;
		String termID = UserInfo.getInstance().termID;
		this.getStudentKnowledgeRankInfo(subjectID, studentID, termID);
//		initView();
	}

	public void initView() {
		// 标题栏部分 实例化；
		this.titleBackLayout = (RelativeLayout) super
				.findViewById(R.id.title_back_layout);
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				KnowledgePointRankingActivity1.this.finish();
			}
		});
		
		this.myListView = (MyListView) super.findViewById(R.id.listView_knowledgePointRanking_content);
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
		this.mAdapter = new MyAdapter(this);
		this.myListView.setAdapter(this.mAdapter);

	}
	
	public void getDataFromIntent(){
		this.subjectNameStr = getIntent().getStringExtra("subjectNameStr");
		Log.i("lvjie", "subjectNameStr="+this.subjectNameStr);
	}
	
	public void generateData(){
		for(int i=0; i<10; i++){
			KnowledgePoint knowledgePoint = new KnowledgePoint(i, 12.0f+i*6, i);
			listKnowledgePoints.add(knowledgePoint);
		}
	}
	

	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(this, toastMessage,
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
			return listKnowledgePoints.size(); // 返回数组的长度
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewChild viewChild;
			if (view == null) {
				view = mInflater.inflate(
						R.layout.knowledge_point_ranking_item_one, null);
				viewChild = new ViewChild();
				/* 得到各个控件的对象 */
				viewChild.untillWeekTextView = (TextView) view
						.findViewById(R.id.untillWeek_textview);
				viewChild.rightPercentTextView = (TextView) view
						.findViewById(R.id.rightPercent_textview);
				viewChild.rankingTextView = (TextView) view
						.findViewById(R.id.ranking_textview);
				viewChild.leftImageView = (ImageView) view.findViewById(R.id.leftTime_pic);

				view.setTag(viewChild); // 绑定ViewHolder对象
			} else {
				viewChild = (ViewChild) view.getTag(); // 取出ViewHolder对象
			}
			
			// 控制左边时钟图；
			if(position == 0){
				viewChild.leftImageView.setBackgroundResource(R.drawable.time_pic1);
			}else if(position == 13){
				viewChild.leftImageView.setBackgroundResource(R.drawable.time_pic3);
			}else {
				viewChild.leftImageView.setBackgroundResource(R.drawable.time_pic2);
			}

			/* 设置TextView显示的内容，即我们存放在listHomeworkArrageConditions中的数据 */
			viewChild.untillWeekTextView.setText("截止到第 "
					+ listKnowledgePoints.get(position).getUntilWeek() + " 周");
			viewChild.rightPercentTextView.setText("掌握度："
					+ listKnowledgePoints.get(position).getRightPercent());
			viewChild.rankingTextView.setText("班级排名为："
					+ listKnowledgePoints.get(position).getRanking());

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
		public TextView untillWeekTextView; // 截止周;
		public TextView rightPercentTextView; // 正确率；
		public TextView rankingTextView; // 排名；
		
		public ImageView leftImageView; // 左边的图片；
	}

	/**
	 * 获取学生的知识点掌握程度的排名情况
	 * 
	 * @param timeType
	 *            ,时间条件类型 day week until_week
	 */
	public void getStudentKnowledgeRankInfo(String subjectID, String studentID,
			String termID) {
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
				String methodName = "report_stuRankOneTerm_byKnowledgePoint_oneSubject_new";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("subjectID", params[0].toString());
				paramsMap.put("studentID", params[1].toString());
				paramsMap.put("termID", params[2].toString());
				paramsMap.put("TokenID", tokenID);
				Log.i("lvjie", "***"+params[0].toString()+"  "+params[1].toString()+"  "+params[2].toString());
				// 获取数据
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				// 从本地获取学校URL
				SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				if (schoolURL == null) {// 如果没有值
					return null;
				}
				getDataTool.setURL(schoolURL);

				try {
					dt_student = GetDataByWS.getInstance().getDataAsTable(
							methodName, paramsMap);
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return dt_student;
			}

			protected void onPostExecute(DataTable result) {
				if (result != null && result.getRowCount() != 0) {
					// 解析表格数据
					// 遍历
					int count = result.getRowCount();
					KnowledgePoint knowledgePoint;
					for (int i = 0; i < count; i++) {
						try {
							int untillWeek = Integer.valueOf(result.getCell(i,
									"endPointName").split("周")[0]);
							float rightPercent = Float.valueOf(result.getCell(
									i, "rates"));
							int ranking = Integer.valueOf(result.getCell(i,
									"ranks"));
							knowledgePoint = new KnowledgePoint(untillWeek,
									rightPercent, ranking);
							listKnowledgePoints.add(knowledgePoint);

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

				KnowledgePointRankingActivity1.this.initView();

			};
		}.execute(subjectID, studentID, termID);
	}
	
}
