package com.ttqeducation.activitys.study;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ttqeducation.R;
import com.ttqeducation.myViews.MyListView;
import com.ttqeducation.tools.DateUtil;
import com.ttqeducation.tools.GeneralTools;

/**
 * 这个是作业结果类型选择界面 需要做的事情： 1.呈现一个条目选择界面 2.当用户点击任意条目时，启动TaskResultsActivity，并传入参数
 * 
 * @author 王勤为
 * 
 */
public class TaskTypeChooseActivity extends Activity {

	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；
	
	private ImageView ivTaskTypeChoose = null; // 图片
	private MyListView myListView = null;

	private String[] taskTypeName = new String[] { "家庭作业完成情况查看", "课堂作业完成情况查看",
			"单元测试完成情况查看", "期中测试完成情况查看", "期末测试完成情况查看" }; // 存放作业类型名称；
	private Integer[] taskTypePic = new Integer[] { R.drawable.homework_result_icon_1,
			R.drawable.homework_result_icon_2, R.drawable.homework_result_icon_3,
			R.drawable.homework_result_icon_4, R.drawable.homework_result_icon_5}; // 存放图片ID；

	private MyAdapter mAdapter = null; // 得到一个MyAdapter对象,用于与listview绑定；

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_type_choose);

		this.initViews();
		this.mAdapter = new MyAdapter(this);
		this.myListView.setAdapter(this.mAdapter);
	}

	/**
	 * 初始化界面
	 * 
	 */
	private void initViews() {
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("作业结果查看");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				TaskTypeChooseActivity.this.finish();
			}
		});

		// 图片自适应
		ivTaskTypeChoose = (ImageView)findViewById(R.id.ivTaskTypeChoose);
		// 动态设置界面图片大小；
		DisplayMetrics metrics = getResources().getDisplayMetrics(); // 用来获取屏幕的分辨率；
		int screenWidthPX = metrics.widthPixels;
		LayoutParams layoutParams = this.ivTaskTypeChoose.getLayoutParams();		
		layoutParams.width = screenWidthPX;
		layoutParams.height = (int)((screenWidthPX / (float)1080) * 629);
		this.ivTaskTypeChoose.setLayoutParams(layoutParams);
		
		this.myListView = (MyListView) super
				.findViewById(R.id.listView_type_choose);
		this.myListView.setPullRefreshEnable(false); // 不提供下拉刷新功能；
		this.myListView.setPullLoadEnable(false); // 不提供上拉加载功能；
		this.myListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			// 列表的点击事件；
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				String titleString = ((TextView) view.findViewById(R.id.taskname_textview)).getText().toString();								
				int len = titleString.length();
				titleString = titleString.substring(0, len - 2);
				
				// 没有联网，不可以进入到下一个界面；
				if(!GeneralTools.getInstance().isOpenNetWork1(getApplicationContext())){
					showToast("未连接到互联网，请检查网络配置!");
					return;
				}
								
				// 家庭作业，课堂作业 如下进入；	
				if(titleString.equals("家庭作业完成情况") || titleString.equals("课堂作业完成情况")) { 
					
					Intent intent = new Intent(TaskTypeChooseActivity.this, TaskResultsActivity.class);
					intent.putExtra("timeType", "day");
					intent.putExtra("valueStr", DateUtil.convertDateToString("yyyy-MM-dd", new Date()));
					intent.putExtra("taskName", titleString);
					intent.putExtra("subjectName", "语文");
					startActivity(intent);
					
				} else if (titleString.equals("单元测试完成情况")) {
					
					Intent intent = new Intent(TaskTypeChooseActivity.this, TaskResultsActivity.class);
					intent.putExtra("timeType", "unit");
					intent.putExtra("taskName", titleString);
					intent.putExtra("subjectName", "语文");
					startActivity(intent);
					
				} else if (titleString.equals("期中测试完成情况")) {
					
					Intent intent = new Intent(TaskTypeChooseActivity.this, TaskResultsActivity.class);
					intent.putExtra("timeType", "midterm");
					intent.putExtra("taskName", titleString);
					intent.putExtra("subjectName", "语文");
					startActivity(intent);
									
				} else if (titleString.equals("期末测试完成情况")) {

						Intent intent = new Intent(TaskTypeChooseActivity.this, TaskResultsActivity.class);
						intent.putExtra("timeType", "finalterm");
						intent.putExtra("taskName", titleString);
						intent.putExtra("subjectName", "语文");
						startActivity(intent);					
				}
			}
		});
	}

	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(TaskTypeChooseActivity.this, toastMessage,
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
			return taskTypeName.length; // 返回数组的长度
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewChild viewChild;
			if (view == null) {
				view = mInflater.inflate(R.layout.task_type_item_one, null);
				viewChild = new ViewChild();
				/* 得到各个控件的对象 */
				viewChild.taskTypeName = (TextView) view
						.findViewById(R.id.taskname_textview);
				viewChild.taskTypePic = (View) view.findViewById(R.id.task_pic);

				view.setTag(viewChild); // 绑定ViewHolder对象
			} else {
				viewChild = (ViewChild) view.getTag(); // 取出ViewHolder对象
			}

			/* 设置TextView显示的内容，即我们存放在动态数组中的数据 */	
			// pannableStringBuilder builder = new SpannableStringBuilder(taskTypeName[position]);
			// builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_text_blue)), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			viewChild.taskTypeName.setText(taskTypeName[position]);
			
			viewChild.taskTypePic.setBackgroundResource(taskTypePic[position]);

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
		public View taskTypePic; // 作业对应的图片;
		public TextView taskTypeName; // 作业类型名；
	}

}
