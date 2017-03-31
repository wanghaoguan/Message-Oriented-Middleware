package com.ttqeducation.activitys.study;

import com.ttqeducation.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
 *  该文件已没用；
 * @author lvjie
 *
 */
public class TestScoresRankingConditionActivity extends Activity {

	/********* 吕杰定义的变量 **********/
	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；

	/************* 界面顶部部分； **************/
	// 布局
	private LinearLayout chineseLayout = null;
	private LinearLayout mathLayout = null;
	private LinearLayout englishLayout = null;
	// 图片
	private ImageView chineseImageView = null;
	private ImageView mathImageView = null;
	private ImageView englishImageView = null;
	// 文字
	private TextView chineseTextView = null;
	private TextView mathTextView = null;
	private TextView englishTextView = null;
	// 下划线
	private ImageView chineseLine = null;
	private ImageView mathLine = null;
	private ImageView englishLine = null;

	// 中间的点击按钮；
	private ImageView selectImageView = null; // 中间的查询按钮；
	// 下面的点击按钮；；
	private TextView weekStopTextView = null; // 截止按钮；
	private DatePicker weekStopPicker = null; // 周选择；
	private LinearLayout weekStopLayout = null;
	private TextView testTextView = null; // 考试按钮；
	private LinearLayout testLayout = null; // 下面的考试布局；

	private String subjectNameStr = ""; // 记录当前是什么科目：语文，数学，外语；
	private String selectConditionStr = ""; // 记录当前的查询条件：until_week;
	private String selectConditionValue = ""; // 查询条件对应的值： 4；

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_scores_ranking_condition);

		initView();
	}

	public void initView() {
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("学生成绩排名查询");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				TestScoresRankingConditionActivity.this.finish();
			}
		});

		// 界面顶部初始化；
		this.chineseLayout = (LinearLayout) super
				.findViewById(R.id.testRanking_chinese_layout);
		this.mathLayout = (LinearLayout) super
				.findViewById(R.id.testRanking_math_layout);
		this.englishLayout = (LinearLayout) super
				.findViewById(R.id.testRanking_english_layout);

		this.chineseImageView = (ImageView) super
				.findViewById(R.id.chinese_image);
		this.mathImageView = (ImageView) super.findViewById(R.id.math_image);
		this.englishImageView = (ImageView) super
				.findViewById(R.id.english_image);

		this.chineseTextView = (TextView) super.findViewById(R.id.chinese_text);
		this.mathTextView = (TextView) super.findViewById(R.id.math_text);
		this.englishTextView = (TextView) super.findViewById(R.id.english_text);

		this.chineseLine = (ImageView) super.findViewById(R.id.chinese_line);
		this.mathLine = (ImageView) super.findViewById(R.id.math_line);
		this.englishLine = (ImageView) super.findViewById(R.id.english_line);

		// 增加点击事件；
		this.chineseLayout.setOnClickListener(myClickListener);
		this.mathLayout.setOnClickListener(myClickListener);
		this.englishLayout.setOnClickListener(myClickListener);

		// 中间查询按钮初始化
		this.selectImageView = (ImageView) super
				.findViewById(R.id.select_imageView);
		this.selectImageView.setOnClickListener(myClickListener);

		// 下面点击按钮初始化；
		this.weekStopTextView = (TextView) super
				.findViewById(R.id.testRanking_stopweek_textview);
		this.weekStopTextView.setOnClickListener(myClickListener);
		this.testTextView = (TextView) super
				.findViewById(R.id.testRanking_test_textview);
		this.testTextView.setOnClickListener(myClickListener);

		// 选择的控件；
		this.weekStopPicker = (DatePicker) super
				.findViewById(R.id.weekStopPicker);
		this.weekStopLayout = (LinearLayout) super
				.findViewById(R.id.weekStop_layout);
		this.testLayout = (LinearLayout) super.findViewById(R.id.test_layout);

		// 隐藏 月和日；
		((ViewGroup) ((ViewGroup) this.weekStopPicker.getChildAt(0))
				.getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
		((ViewGroup) ((ViewGroup) this.weekStopPicker.getChildAt(0))
				.getChildAt(0)).getChildAt(1).setVisibility(View.GONE);
		this.weekStopPicker.init(1, 1, 1, null);
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
		this.chineseLayout.setBackgroundColor(getResources().getColor(
				R.color.main_center_bg));
		this.mathLayout.setBackgroundColor(getResources().getColor(
				R.color.main_center_bg));
		this.englishLayout.setBackgroundColor(getResources().getColor(
				R.color.main_center_bg));

		// 图片的恢复；
		this.chineseImageView.setBackgroundResource(R.drawable.chinese);
		this.mathImageView.setBackgroundResource(R.drawable.math);
		this.englishImageView.setBackgroundResource(R.drawable.english);

		// 文字的恢复；
		this.chineseTextView.setTextColor(getResources().getColor(
				R.color.textGray));
		this.mathTextView.setTextColor(getResources()
				.getColor(R.color.textGray));
		this.englishTextView.setTextColor(getResources().getColor(
				R.color.textGray));

		// 下划线的恢复;
		this.chineseLine.setBackgroundColor(getResources().getColor(
				R.color.main_center_bg));
		this.mathLine.setBackgroundColor(getResources().getColor(
				R.color.main_center_bg));
		this.englishLine.setBackgroundColor(getResources().getColor(
				R.color.main_center_bg));
	}

	// 点击某个按钮；
	public void clickTopLayout(int id) {

		switch (id) {
		case R.id.testRanking_chinese_layout: // 点击语文选项；
			clearSelections();
			this.chineseLayout.setBackgroundColor(getResources().getColor(
					R.color.textWhite));
			this.chineseTextView.setTextColor(getResources().getColor(
					R.color.textGreen));
			this.chineseLine.setBackgroundColor(getResources().getColor(
					R.color.textGreen));
			this.chineseImageView.setBackgroundResource(R.drawable.chinese_pre);

			this.subjectNameStr = "语文";
			break;

		case R.id.testRanking_math_layout: // 点击数学选项；
			clearSelections();
			this.mathLayout.setBackgroundColor(getResources().getColor(
					R.color.textWhite));
			this.mathTextView.setTextColor(getResources().getColor(
					R.color.textGreen));
			this.mathLine.setBackgroundColor(getResources().getColor(
					R.color.textGreen));
			this.mathImageView.setBackgroundResource(R.drawable.math_pre);

			this.subjectNameStr = "数学";
			break;

		case R.id.testRanking_english_layout: // 点击英语选项；
			clearSelections();
			this.englishLayout.setBackgroundColor(getResources().getColor(
					R.color.textWhite));
			this.englishTextView.setTextColor(getResources().getColor(
					R.color.textGreen));
			this.englishLine.setBackgroundColor(getResources().getColor(
					R.color.textGreen));
			this.englishImageView.setBackgroundResource(R.drawable.english_pre);

			this.subjectNameStr = "英语";
			break;

		case R.id.testRanking_stopweek_textview: // 点击截止按钮；

			// 让截止按钮变绿 字体变为白色， 其他按钮变灰，字体深灰；
			this.weekStopTextView
					.setBackgroundResource(R.drawable.btn_circle_green);
			this.weekStopTextView.setTextColor(getResources().getColor(
					R.color.textWhite));
			this.testTextView.setBackgroundResource(R.drawable.btn_circle_gray);
			this.testTextView.setTextColor(getResources().getColor(
					R.color.textGray));

			this.weekStopLayout.setVisibility(View.VISIBLE);
			this.testLayout.setVisibility(View.GONE);

			this.selectConditionStr = "until_week";
			break;
		case R.id.testRanking_test_textview: // 点击考试按钮；

			// 让考试按钮变绿 字体变为白色， 其他按钮变灰，字体深灰；
			this.testTextView
					.setBackgroundResource(R.drawable.btn_circle_green);
			this.testTextView.setTextColor(getResources().getColor(
					R.color.textWhite));
			this.weekStopTextView
					.setBackgroundResource(R.drawable.btn_circle_gray);
			this.weekStopTextView.setTextColor(getResources().getColor(
					R.color.textGray));

			this.testLayout.setVisibility(View.VISIBLE);
			this.weekStopLayout.setVisibility(View.GONE);

			this.selectConditionStr = "test";
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
		Intent intent = new Intent(TestScoresRankingConditionActivity.this,
				TestScoresRankingActivity.class);
		startActivity(intent);
	}

	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(TestScoresRankingConditionActivity.this,
				toastMessage, Toast.LENGTH_SHORT);
		toast.show();
	}

}
