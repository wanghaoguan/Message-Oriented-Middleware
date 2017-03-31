package com.ttqeducation.activitys.study;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.R;
import com.ttqeducation.R.layout;
import com.ttqeducation.activitys.study.HomeworkArrangementFragment.ViewChild;
import com.ttqeducation.activitys.system.ChooseSchoolActivity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 家庭作业布置情况详情界面
 * 
 * @author 吕杰
 * 
 */
public class HomeworkArrangementDetailActivity extends Activity {

	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；
	
	private TextView subjectNameTextView = null;
	private TextView homeworkArrageDateTextView = null;

	private MyListView myListView = null;
	private MyAdapter mAdapter = null; // 得到一个MyAdapter对象,用于与listview绑定；
	private List<TaskCompletion> listHomeworkArrageDetails = new ArrayList<TaskCompletion>(); // 存放家庭作业布置的内容；
	private String dateString = ""; // 存放作业布置时间，用于界面显示；

	public DataTable origin_student_HomeWorkInfo_datatable = null;// 原始学生家庭作业数据表
	private RefreshView refreshView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homework_arrange_detail);

		getDataFromIntent();
		// initViews(); //这里不再调用，在获取数据方法里面调用，因为只有数据获取完才初始化界面

		// HomeworkArrangementFragment.subjectName
		String subjectID = GeneralTools.getInstance().getSubjectIDByName(
				HomeworkArrangementFragment.subjectName);
		String studentID = UserInfo.getInstance().studentID;
		// System.out.println("科目ID"+subjectID);
		// System.out.println("学生ID"+studentID);

		this.getHomeWorkArrangementInfo(studentID, this.dateString, subjectID);
	}

	/**
	 * 初始化界面
	 * 
	 */
	private void initViews() {
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("家庭作业布置详情");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				HomeworkArrangementDetailActivity.this.finish();
			}
		});

		this.subjectNameTextView = (TextView) super
				.findViewById(R.id.subjectName_textView);
		this.homeworkArrageDateTextView = (TextView) super
				.findViewById(R.id.homeworkArrageDate_textView);
		this.subjectNameTextView.setText("科目: "
				+ HomeworkArrangementFragment.subjectName);
		this.homeworkArrageDateTextView.setText("布置时间: " + this.dateString);

		this.myListView = (MyListView) super
				.findViewById(R.id.listView_homeworkArrage_detail);
		this.myListView.setPullRefreshEnable(false); // 不提供下拉刷新功能；
		this.myListView.setPullLoadEnable(false); // 不提供上拉加载功能；

		this.mAdapter = new MyAdapter(this);
		this.myListView.setAdapter(this.mAdapter);
		
		showNoHomeworkInfoInView();
	}

	// 从前一个界面获取数据；
	public void getDataFromIntent() {
		this.dateString = getIntent().getStringExtra("dateString");
	}
	
	// 当没有家庭作业就显示无作业；
	public void showNoHomeworkInfoInView(){
		TextView noHomeworkTextView = (TextView) super.findViewById(R.id.noHomework_textView);
		if(this.listHomeworkArrageDetails.size() <= 0){
			noHomeworkTextView.setVisibility(View.VISIBLE);
		}		
	}

	// 暂时用于产生数据， 以后就不需要了，从服务端获取；
	public void generateData() {
		TaskCompletion taskCompletion;

//		this.getHomeWorkArrangementInfo("C2014401", "2015-01-27", "2");
//		System.out.println("xxx"
//				+ origin_student_HomeWorkInfo_datatable.toString());
//		// 如果获取到了数据
//		if (origin_student_HomeWorkInfo_datatable != null
//				&& origin_student_HomeWorkInfo_datatable.getRowCount() != 0) {
//			System.out.println("获取到了数据"
//					+ origin_student_HomeWorkInfo_datatable.toString());
//
//			int count = origin_student_HomeWorkInfo_datatable.getRowCount();
//			for (int i = 0; i < count; i++) {
//				try {
//					String question = origin_student_HomeWorkInfo_datatable
//							.getCell(i, 0);
//					String pageNum = origin_student_HomeWorkInfo_datatable
//							.getCell(i, 0);
//					taskCompletion = new TaskCompletion("题目" + i + question,
//							"第 " + pageNum + " 页");
//					listHomeworkArrageDetails.add(taskCompletion);
//				} catch (dataTableWrongException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			}
//		} else {// 如果没有数据
//			Toast toast = Toast.makeText(
//					HomeworkArrangementDetailActivity.this, "该日期没有家庭作业！",
//					Toast.LENGTH_SHORT);
//			toast.show();
//
//		}

		// 
		 for(int i=1;i<10;i++){
		 taskCompletion = new TaskCompletion("家庭作业  做一做 1."+i, "第 "+i+" 页");
		 listHomeworkArrageDetails.add(taskCompletion);
		 }
	}

	/**
	 * 获取学生的家庭作业布置情况 ，把获取的数据存入origin_student_HomeWorkInfo_datatable中
	 * 
	 * @param timeType
	 *            ,时间条件类型 day week until_week
	 */
	public void getHomeWorkArrangementInfo(String studentID, String date,
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
				String methodName = "APP_getHomework_byDay";
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
					Log.i("error", "getHomeWorkArrangementInfo()...出错了。。。");
					e.printStackTrace();
				}
				return dt_student;
			}

			protected void onPostExecute(DataTable result) {
				if (result != null) {
					System.out.println("表格数据2" + result.toString());
					HomeworkArrangementDetailActivity.this.origin_student_HomeWorkInfo_datatable = result;
					TaskCompletion taskCompletion;
					int count = result.getRowCount();
					for (int i = 0; i < count; i++) {
						try {
//							String question = result.getCell(i, "questionCode").substring(2);
							String question = result.getCell(i, "questionCode");
							String pageNum = result.getCell(i, "textPageNum");
							Log.i("lvjie", i + 1 + "."+ question+"   第 " + pageNum + " 页");
							taskCompletion = new TaskCompletion(i + 1 + "."
									+ question, "第 " + pageNum + " 页");
							listHomeworkArrageDetails.add(taskCompletion);
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				// 关闭刷新；
				refreshView.dismiss();
				// 获取完数据后，初始化界面
				HomeworkArrangementDetailActivity.this.initViews();
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
			return listHomeworkArrageDetails.size(); // 返回数组的长度
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewChild viewChild;
			if (view == null) {
				view = mInflater.inflate(
						R.layout.homeworkarrange_detail_item_one1, null);
				viewChild = new ViewChild();
				/* 得到各个控件的对象 */
				viewChild.homeworkQuestionName = (TextView) view
						.findViewById(R.id.homework_questionName_textview);
				viewChild.homeworkQuestionPage = (TextView) view
						.findViewById(R.id.homework_questionPage_textview);
				viewChild.leftTimeImageView = (ImageView) view.findViewById(R.id.leftTime_pic);
				viewChild.rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);

				view.setTag(viewChild); // 绑定ViewHolder对象
			} else {
				viewChild = (ViewChild) view.getTag(); // 取出ViewHolder对象
			}

			/* 设置TextView显示的内容，即我们存放在listHomeworkArrageConditions中的数据 */
			viewChild.homeworkQuestionName.setText(listHomeworkArrageDetails
					.get(position).getTaskName());
			viewChild.homeworkQuestionPage.setText(listHomeworkArrageDetails
					.get(position).getTaskPage());
			
			// 控制左边时钟图；
			if(position == 0){
				viewChild.leftTimeImageView.setBackgroundResource(R.drawable.time_pic1);
			}else if(position == listHomeworkArrageDetails.size()-1){
				viewChild.leftTimeImageView.setBackgroundResource(R.drawable.time_pic3);
			}else {
				viewChild.leftTimeImageView.setBackgroundResource(R.drawable.time_pic2);
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
		public TextView homeworkQuestionName; // 家庭作业作业名称;
		public TextView homeworkQuestionPage; // 作业页码；
		
		public ImageView leftTimeImageView;	  // 左边的时钟图；
		public LinearLayout rightLayout;	  // 右边的背景图；
	}

}
