package com.ttqeducation.activitys.study;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ttqeducation.R;
import com.ttqeducation.beans.TestInfo;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.myViews.PickerView;
import com.ttqeducation.tools.GeneralTools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 这个是学生知识点活动，用于展示学生的知识点掌握程度情况 时间条件:天，周，截止周
 * 需要做的事情：1.根据时间条件获取知识点掌握度详细列表（pointID,pointName,rightPercent）
 * 2.处理该结果,(1)计算总的平均掌握度(2)划分成三个等级,并计算其占比
 * 
 * @author 王勤为
 * 
 */
public class KnowledgePointConditionActivity extends Activity {

	/********* 吕杰定义的变量 **********/
	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；

	/************* 界面顶部部分； **************/
	// 布局
	private LinearLayout chineseLayout = null;
	private LinearLayout mathLayout = null;
	private LinearLayout englishLayout = null;
	// 文字背景
	private LinearLayout chineseImageView = null;
	private LinearLayout mathImageView = null;
	private LinearLayout englishImageView = null;
	// 文字
	private TextView chineseTextView = null;
	private TextView mathTextView = null;
	private TextView englishTextView = null;

	// 中间的点击按钮；
	private ImageView selectImageView = null; // 中间的查询按钮；
	// 下面的点击按钮；
	private TextView dateTextView = null; // 日期按钮；
	private TextView weekTextView = null; // 周按钮；
	private TextView weekStopTextView = null; // 截止按钮；

	private DatePicker datePicker = null; // 日期选择；
	private PickerView weekPicker = null; // 周选择；
	private LinearLayout weekLayout = null;
	
	List<TestInfo> listWeekNum = new ArrayList<TestInfo>();		// 用来存放周数；

	private String subjectNameStr = ""; // 记录当前是什么科目：语文，数学，外语；
	private String selectConditionStr = ""; // 记录当前的查询条件：day, week, until_week;
	private String selectConditionValue = ""; // 查询条件对应的值：2015-01-02 或 4；

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_knowledge_point_condition);

		initView();
	}

	public void initView() {
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("学生知识点掌握情况");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				KnowledgePointConditionActivity.this.finish();
			}
		});

		// 界面顶部初始化；
		this.chineseLayout = (LinearLayout) super
				.findViewById(R.id.knowledge_chinese_layout);
		this.mathLayout = (LinearLayout) super
				.findViewById(R.id.knowledge_math_layout);
		this.englishLayout = (LinearLayout) super
				.findViewById(R.id.knowledge_english_layout);

		this.chineseImageView = (LinearLayout) super
				.findViewById(R.id.chinese_image);
		this.mathImageView = (LinearLayout) super.findViewById(R.id.math_image);
		this.englishImageView = (LinearLayout) super
				.findViewById(R.id.english_image);

		this.chineseTextView = (TextView) super.findViewById(R.id.chinese_text);
		this.mathTextView = (TextView) super.findViewById(R.id.math_text);
		this.englishTextView = (TextView) super.findViewById(R.id.english_text);

		// 增加点击事件；
		this.chineseLayout.setOnClickListener(myClickListener);
		this.mathLayout.setOnClickListener(myClickListener);
		this.englishLayout.setOnClickListener(myClickListener);

		// 中间查询按钮初始化
		this.selectImageView = (ImageView) super
				.findViewById(R.id.select_imageView);
		this.selectImageView.setOnClickListener(myClickListener);

		// 下面点击按钮初始化；
		this.dateTextView = (TextView) super
				.findViewById(R.id.knowledge_date_textview);
		this.weekTextView = (TextView) super
				.findViewById(R.id.knowledge_week_textview);
		this.weekStopTextView = (TextView) super
				.findViewById(R.id.knowledge_weekstop_textview);

		this.dateTextView.setOnClickListener(myClickListener);
		this.weekTextView.setOnClickListener(myClickListener);
		this.weekStopTextView.setOnClickListener(myClickListener);

		// 选择的控件；
		this.datePicker = (DatePicker) super.findViewById(R.id.datePicker);
		this.weekPicker = (PickerView) super.findViewById(R.id.weekPicker);
		this.weekPicker.setSelectTestSize(50);
		this.weekPicker.setOthersTestSize(40);
		this.weekLayout = (LinearLayout) super.findViewById(R.id.week_layout);
				
		setMaxWeekPicker(UserInfo.getInstance().currentWeek);


		// 默认选择的是语文，日期查询；
		this.subjectNameStr = "语文";
		this.selectConditionStr = "day";
	}

	private OnClickListener myClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			clickTopLayout(view.getId());
		}
	};

	// 恢复所有为灰色；
	public void clearSelections() {
		// 背景色的恢复；
//		this.chineseLayout.setBackgroundColor(getResources().getColor(
//				R.color.main_center_bg));
//		this.mathLayout.setBackgroundColor(getResources().getColor(
//				R.color.main_center_bg));
//		this.englishLayout.setBackgroundColor(getResources().getColor(
//				R.color.main_center_bg));

		// 文字背景圆角恢复；
		this.chineseImageView.setBackgroundResource(R.drawable.linearlayout_frame_circle_gray);
		this.mathImageView.setBackgroundResource(R.drawable.linearlayout_frame_circle_gray);
		this.englishImageView.setBackgroundResource(R.drawable.linearlayout_frame_circle_gray);

		// 文字的恢复；
		this.chineseTextView.setTextColor(getResources().getColor(
				R.color.textGray));
		this.mathTextView.setTextColor(getResources()
				.getColor(R.color.textGray));
		this.englishTextView.setTextColor(getResources().getColor(
				R.color.textGray));
	}

	// 点击某个按钮；
	public void clickTopLayout(int id) {

		switch (id) {
		case R.id.knowledge_chinese_layout: // 点击语文选项；
			clearSelections();
			this.chineseTextView.setTextColor(getResources().getColor(
					R.color.textWhite));
			this.chineseImageView.setBackgroundResource(R.drawable.btn_circle_red_round);

			this.subjectNameStr = "语文";
			break;

		case R.id.knowledge_math_layout: // 点击数学选项；
			clearSelections();
			this.mathTextView.setTextColor(getResources().getColor(
					R.color.textWhite));
			this.mathImageView.setBackgroundResource(R.drawable.btn_circle_red_round);

			this.subjectNameStr = "数学";
			break;

		case R.id.knowledge_english_layout: // 点击英语选项；
			clearSelections();

			this.englishTextView.setTextColor(getResources().getColor(
					R.color.textWhite));
			this.englishImageView.setBackgroundResource(R.drawable.btn_circle_red_round);

			this.subjectNameStr = "英语";
			break;

		case R.id.knowledge_date_textview: // 点击日期按钮；
			// 让日期按钮变绿 字体变为白色， 其他两个按钮变灰，字体深灰；
			this.dateTextView
					.setBackgroundResource(R.drawable.btn_circle_green);
			this.dateTextView.setTextColor(getResources().getColor(
					R.color.textWhite));
			this.weekTextView.setBackgroundResource(R.drawable.btn_circle_gray);
			this.weekTextView.setTextColor(getResources().getColor(
					R.color.textGray));
			this.weekStopTextView
					.setBackgroundResource(R.drawable.btn_circle_gray);
			this.weekStopTextView.setTextColor(getResources().getColor(
					R.color.textGray));

			this.datePicker.setVisibility(View.VISIBLE);
			this.weekLayout.setVisibility(View.GONE);

			this.selectConditionStr = "day";
			break;

		case R.id.knowledge_week_textview: // 点击周按钮；

			// 让周按钮变绿 字体变为白色， 其他两个按钮变灰，字体深灰；
			this.weekTextView
					.setBackgroundResource(R.drawable.btn_circle_green);
			this.weekTextView.setTextColor(getResources().getColor(
					R.color.textWhite));
			this.dateTextView.setBackgroundResource(R.drawable.btn_circle_gray);
			this.dateTextView.setTextColor(getResources().getColor(
					R.color.textGray));
			this.weekStopTextView
					.setBackgroundResource(R.drawable.btn_circle_gray);
			this.weekStopTextView.setTextColor(getResources().getColor(
					R.color.textGray));

			this.weekLayout.setVisibility(View.VISIBLE);
			this.datePicker.setVisibility(View.GONE);

			this.selectConditionStr = "week";
			break;

		case R.id.knowledge_weekstop_textview: // 点击截止按钮；

			// 让截止按钮变绿 字体变为白色， 其他两个按钮变灰，字体深灰；
			this.weekStopTextView
					.setBackgroundResource(R.drawable.btn_circle_green);
			this.weekStopTextView.setTextColor(getResources().getColor(
					R.color.textWhite));
			this.weekTextView.setBackgroundResource(R.drawable.btn_circle_gray);
			this.weekTextView.setTextColor(getResources().getColor(
					R.color.textGray));
			this.dateTextView.setBackgroundResource(R.drawable.btn_circle_gray);
			this.dateTextView.setTextColor(getResources().getColor(
					R.color.textGray));

			this.weekLayout.setVisibility(View.VISIBLE);
			this.datePicker.setVisibility(View.GONE);

			this.selectConditionStr = "untill_week";
			break;

		case R.id.select_imageView: // 点击中间查询按钮；

			goNextActivity();
			break;

		default:
			break;
		}
	}

	// 进入下一个界面；
	public void goNextActivity() {
		
		// 没有联网，不可以进入到下一个界面；
		if(!GeneralTools.getInstance().isOpenNetWork1(KnowledgePointConditionActivity.this)){
			showToast("未连接到互联网，请检查网络配置!");
			return;
		}
		
		String valueString = "";
		if (this.selectConditionStr.equals("day")) {
			int year = this.datePicker.getYear();
			int month = this.datePicker.getMonth() + 1;
			int day = this.datePicker.getDayOfMonth();

			valueString += year + "-";
			if (month < 10) {
				valueString += "0" + month + "-";
			} else {
				valueString += month + "-";
			}

			if (day < 10) {
				valueString += "0" + day;
			} else {
				valueString += day;
			}
		} else {
			valueString = this.weekPicker.getTextValue() + "";
		}

		Intent intent = new Intent(KnowledgePointConditionActivity.this,
				KnowledgePointActivity.class);
		intent.putExtra("subjectName", this.subjectNameStr);
		intent.putExtra("timeType", this.selectConditionStr);
		intent.putExtra("timeValueStr", valueString);
		startActivity(intent);
	}

	// 设置周按钮的最大显示周
	private void setMaxWeekPicker(int max){
		
		if(this.weekPicker == null){
			return;
		}
		if(max<1){		// 默认为50周；
			max=50;
		}
		
		for(int i=1; i<=max; i++){
			TestInfo weekInfo = new TestInfo(i+"");
			listWeekNum.add(weekInfo);
		}
		this.weekPicker.setData(listWeekNum);
		
	}
			
	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(KnowledgePointConditionActivity.this,
				toastMessage, Toast.LENGTH_SHORT);
		toast.show();
	}

}
