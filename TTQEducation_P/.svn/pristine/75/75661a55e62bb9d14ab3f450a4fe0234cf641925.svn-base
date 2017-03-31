package com.ttqeducation.activitys.study;

/**
 *  家庭作业布置情况 按时间条件 查看界面  提供时间点击 及 进入下一个详情界面；
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ttqeducation.R;
import com.ttqeducation.beans.HomeworkArrageCondition;
import com.ttqeducation.myViews.MyListView;
import com.ttqeducation.tools.DateUtil;
import com.ttqeducation.tools.GeneralTools;

public class HomeworkArrangementFragment extends Fragment {

	public static String subjectName = ""; // 科目名：语文；
	private MyListView myListView = null;
	private MyAdapter mAdapter = null; // 得到一个MyAdapter对象,用于与listview绑定；
	// 用来存放 时间及时间描述；
	private List<HomeworkArrageCondition> listHomeworkArrageConditions = new ArrayList<HomeworkArrageCondition>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View layout = inflater
				.inflate(R.layout.fragment_homeworkarrangement_content,
						container, false);

		this.initView(layout);
		generateData();
		this.mAdapter = new MyAdapter(getActivity());
		this.myListView.setAdapter(this.mAdapter);
		return layout;
	}

	public void initView(View view) {
		this.myListView = (MyListView) view
				.findViewById(R.id.listView_homeworkArrage_condition);
		this.myListView.setPullRefreshEnable(false); // 不提供下拉刷新功能；
		this.myListView.setPullLoadEnable(false); // 不提供上拉加载功能；
		this.myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			// 列表的点击事件；
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				// 没有联网，不可以进入到下一个界面；
				if(!GeneralTools.getInstance().isOpenNetWork1(getActivity())){
					showToast("未连接到互联网，请检查网络配置!");
					return;
				}
				
				if (HomeworkArrangementActivity.titleString.equals("家庭作业布置情况")) {

					String dateString = ((TextView) view
							.findViewById(R.id.homeworkArrage_date_textview))
							.getText().toString();
					Intent intent = new Intent(getActivity(),
							HomeworkArrangementDetailActivity.class);
					intent.putExtra("dateString", dateString); // 把参数传递到家庭作业布置情况详情界面；
					startActivity(intent);
				} else if (HomeworkArrangementActivity.titleString
						.equals("错题汇总")) {
					String dateString = ((TextView) view
							.findViewById(R.id.homeworkArrage_date_textview))
							.getText().toString();
					Intent intent = new Intent(getActivity(),
							ErrorQuestionDetailsActivity.class);
					intent.putExtra("dateString", dateString); // 把参数传递到错题况详情界面；
					startActivity(intent);
				}

			}
		});
	}

	// 该函数已经把日期产生成功了；
	public void generateData() {
		HomeworkArrageCondition homeworkArrageCondition;
		Date date = new Date();
		for (int i = 0; i < 14; i++) {
			
			String dateString = DateUtil.convertDateToString("yyyy-MM-dd",
					date);
			String weekNum = "("+DateUtil.getWeekOfDate(date)+")";
			
			if (i == 0) {				
				homeworkArrageCondition = new HomeworkArrageCondition(
						dateString, weekNum, "今天");
			} else if (i == 1) {
				homeworkArrageCondition = new HomeworkArrageCondition(
						dateString, weekNum, "昨天");
			} else if (i == 2) {
				homeworkArrageCondition = new HomeworkArrageCondition(
						dateString, weekNum, "前天");
			} else {
				homeworkArrageCondition = new HomeworkArrageCondition(
						dateString, weekNum, i + " 天前");
			}
			listHomeworkArrageConditions.add(homeworkArrageCondition);
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
			return listHomeworkArrageConditions.size(); // 返回数组的长度
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewChild viewChild;
			if (view == null) {
				view = mInflater.inflate(
						R.layout.homeworkarrage_condition_item_one1, null);
				viewChild = new ViewChild();
				/* 得到各个控件的对象 */
				viewChild.homeworkArrageDate = (TextView) view
						.findViewById(R.id.homeworkArrage_date_textview);
				viewChild.homewrokArrageWeekNum = (TextView) view.findViewById(R.id.homeworkArrage_week_textview);
				viewChild.homeworkArrageDateDescrip = (TextView) view
						.findViewById(R.id.homeworkArrage_dateDescrip_textview);
				viewChild.leftTimeImageView = (ImageView) view.findViewById(R.id.leftTime_pic);
				viewChild.rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);

				view.setTag(viewChild); // 绑定ViewHolder对象
			} else {
				viewChild = (ViewChild) view.getTag(); // 取出ViewHolder对象
			}

			/* 设置TextView显示的内容，即我们存放在listHomeworkArrageConditions中的数据 */
			viewChild.homeworkArrageDate.setText(listHomeworkArrageConditions.get(position).getHomeworkArrageDate());
			viewChild.homewrokArrageWeekNum.setText(listHomeworkArrageConditions.get(position).getHomeworkArrageWeekNum());
			viewChild.homeworkArrageDateDescrip.setText(listHomeworkArrageConditions.get(position).getHomeworkArrageDateDescrip());
			
			// 控制左边时钟图；
			if(position == 0){
				viewChild.leftTimeImageView.setBackgroundResource(R.drawable.time_pic1);
			}else if(position == 13){
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
		public TextView homeworkArrageDate; // 家庭作业布置时间;
		public TextView homewrokArrageWeekNum;	// 星期；
		public TextView homeworkArrageDateDescrip; // 时间描述；
		public ImageView leftTimeImageView;			// 左边的时钟图；
		
		public LinearLayout rightLayout;			// 右边的背景色；
	}

}
