package com.ttqeducation.activitys.study;

import com.ttqeducation.R;
import com.ttqeducation.R.layout;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 家庭作业布置情况 按时间条件 查看界面；时间点击在对应的fragment中；
 * 
 * @author 吕杰
 * 
 */
public class HomeworkArrangementActivity extends Activity {

	// 存放标题栏字幕， 用于后面判断进入的是 家庭作业布置情况界面 还是 错题汇总界面；
	public static String titleString = "";
	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；

	/************* 界面顶部部分； **************/
	// 布局
	private LinearLayout chineseLayout = null;
	private LinearLayout mathLayout = null;
	private LinearLayout englishLayout = null;
	
	// 文字的圆角背景
	private LinearLayout chineseImageView = null;
	private LinearLayout mathImageView = null;
	private LinearLayout englishImageView = null;
	// 文字
	private TextView chineseTextView = null;
	private TextView mathTextView = null;
	private TextView englishTextView = null;

	// 各个子模块；
	private FragmentManager fragmentManager = null; // 用于对下面的fragment进行管理；
	private HomeworkArrangementFragment chineseFragment = null;
	private HomeworkArrangementFragment englishFragment = null;
	private HomeworkArrangementFragment mathFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homework_arrangement_situation1);

		this.fragmentManager = getFragmentManager();
		this.initViews();
		HomeworkArrangementFragment.subjectName = "语文"; // 初始化选择 语文；
		showSelectionFragment(R.id.homework_chinese_layout);
//		showSelectionFragment(R.id.homework_math_layout);
	}

	/**
	 * 初始化界面
	 * 
	 */
	private void initViews() {
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				HomeworkArrangementActivity.this.finish();
			}
		});

		HomeworkArrangementActivity.titleString = getIntent().getStringExtra(
				"titleStr");
		this.titleTextView.setText(HomeworkArrangementActivity.titleString); // 设置标题栏字幕；

		// 界面顶部初始化；
		this.chineseLayout = (LinearLayout) super
				.findViewById(R.id.homework_chinese_layout);
		this.mathLayout = (LinearLayout) super
				.findViewById(R.id.homework_math_layout);
		this.englishLayout = (LinearLayout) super
				.findViewById(R.id.homework_english_layout);

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
	}

	private OnClickListener myClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			clearSelections();
			clickTopLayout(view.getId());
		}
	};

	// 恢复所有为灰色；
	public void clearSelections() {
//		// 背景色的恢复；
//		this.chineseLayout.setBackgroundColor(getResources().getColor(
//				R.color.light_gray));
//		this.mathLayout.setBackgroundColor(getResources().getColor(
//				R.color.light_gray));
//		this.englishLayout.setBackgroundColor(getResources().getColor(
//				R.color.light_gray));

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
		case R.id.homework_chinese_layout:
			this.chineseTextView.setTextColor(getResources().getColor(
					R.color.textWhite));
			this.chineseImageView.setBackgroundResource(R.drawable.btn_circle_red_round);
			HomeworkArrangementFragment.subjectName = "语文";
			break;

		case R.id.homework_math_layout:
			this.mathTextView.setTextColor(getResources().getColor(
					R.color.textWhite));
			this.mathImageView.setBackgroundResource(R.drawable.btn_circle_red_round);
			HomeworkArrangementFragment.subjectName = "数学";
			break;

		case R.id.homework_english_layout:
			this.englishTextView.setTextColor(getResources().getColor(
					R.color.textWhite));
			this.englishImageView.setBackgroundResource(R.drawable.btn_circle_red_round);
			HomeworkArrangementFragment.subjectName = "英语";
			break;

		default:
			break;
		}
		showSelectionFragment(id);
	}

	// 显示 选中的fragment;
	public void showSelectionFragment(int id) {

		// 开启一个Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);
		switch (id) {
		case R.id.homework_chinese_layout:
			if (chineseFragment == null) {
				// 如果ContactsFragment为空，则创建一个并添加到界面上
				chineseFragment = new HomeworkArrangementFragment();
				transaction.add(R.id.homework_arrangement_content,
						chineseFragment);
			} else {
				// 如果ContactsFragment不为空，则直接将它显示出来
				transaction.show(chineseFragment);
			}

			break;

		case R.id.homework_math_layout:
			if (mathFragment == null) {
				// 如果ContactsFragment为空，则创建一个并添加到界面上
				mathFragment = new HomeworkArrangementFragment();
				transaction
						.add(R.id.homework_arrangement_content, mathFragment);
			} else {
				// 如果ContactsFragment不为空，则直接将它显示出来
				transaction.show(mathFragment);
			}
			break;
		case R.id.homework_english_layout:
			if (englishFragment == null) {
				// 如果ContactsFragment为空，则创建一个并添加到界面上
				englishFragment = new HomeworkArrangementFragment();
				transaction.add(R.id.homework_arrangement_content,
						englishFragment);
			} else {
				// 如果ContactsFragment不为空，则直接将它显示出来
				transaction.show(englishFragment);
			}
			break;
		default:
			break;
		}
		transaction.commit();
	}

	// 隐藏fragment;
	private void hideFragments(FragmentTransaction transaction) {
		if (chineseFragment != null) {
			transaction.hide(chineseFragment);
		}
		if (mathFragment != null) {
			transaction.hide(mathFragment);
		}
		if (englishFragment != null) {
			transaction.hide(englishFragment);
		}
	}

}
