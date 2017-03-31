package com.ttqeducation.activitys.study;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ttqeducation.R;
import com.ttqeducation.activitys.study.TaskResultDetailActivity.ViewChild;
import com.ttqeducation.beans.TestScoreRanking;
import com.ttqeducation.myViews.MyListView;
import com.ttqeducation.tools.DateUtil;
import com.ttqeducation.tools.GeneralTools;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TestScoresRankingActivity extends Activity {

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
	private TestScoresRankingFragment chineseFragment = null;
	private TestScoresRankingFragment englishFragment = null;
	private TestScoresRankingFragment mathFragment = null;

	private String subjectNameStr = ""; // 记录当前是什么科目：语文，数学，外语；

	private TextView seeKnowledgePointDetails = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_scores_ranking);

		this.fragmentManager = getFragmentManager();
		this.subjectNameStr = "语文";
		initView();
		showSelectionFragment(R.id.testscore_chinese_layout);
	}

	public void initView() {
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("学生成绩排名详情");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				TestScoresRankingActivity.this.finish();
			}
		});

		// 界面顶部初始化；
		this.chineseLayout = (LinearLayout) super
				.findViewById(R.id.testscore_chinese_layout);
		this.mathLayout = (LinearLayout) super
				.findViewById(R.id.testscore_math_layout);
		this.englishLayout = (LinearLayout) super
				.findViewById(R.id.testscore_english_layout);

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

		// 查看知识点排名详情界面点击按钮；
		this.seeKnowledgePointDetails = (TextView) super
				.findViewById(R.id.see_knowledge_point_ranking_details);
		this.seeKnowledgePointDetails.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { // 进入知识点排名详细界面；
				// TODO Auto-generated method stub
				
				// 没有联网，不可以进入到下一个界面；
				if(!GeneralTools.getInstance().isOpenNetWork1(TestScoresRankingActivity.this)){
					showToast("未连接到互联网，请检查网络配置!");
					return;
				}
				
				Intent knowledgePointRankingIntent = new Intent(
						TestScoresRankingActivity.this,
						KnowledgePointRankingActivity1.class);
				knowledgePointRankingIntent.putExtra("subjectNameStr", TestScoresRankingActivity.this.subjectNameStr);
				startActivity(knowledgePointRankingIntent);
			}
		});

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
		// 背景色的恢复；
//		this.chineseLayout.setBackgroundColor(getResources().getColor(
//				R.color.main_center_bg));
//		this.mathLayout.setBackgroundColor(getResources().getColor(
//				R.color.main_center_bg));
//		this.englishLayout.setBackgroundColor(getResources().getColor(
//				R.color.main_center_bg));

		// 图片的恢复；
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
		case R.id.testscore_chinese_layout: // 点击语文选项；
			clearSelections();
			this.chineseTextView.setTextColor(getResources().getColor(
					R.color.textWhite));
			this.chineseImageView.setBackgroundResource(R.drawable.btn_circle_red_round);

			this.subjectNameStr = "语文";
			break;

		case R.id.testscore_math_layout: // 点击数学选项；
			clearSelections();
			this.mathTextView.setTextColor(getResources().getColor(
					R.color.textWhite));
			this.mathImageView.setBackgroundResource(R.drawable.btn_circle_red_round);

			this.subjectNameStr = "数学";
			break;

		case R.id.testscore_english_layout: // 点击英语选项；
			clearSelections();
			this.englishTextView.setTextColor(getResources().getColor(
					R.color.textWhite));
			this.englishImageView.setBackgroundResource(R.drawable.btn_circle_red_round);

			this.subjectNameStr = "英语";
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
		case R.id.testscore_chinese_layout:
			if (chineseFragment == null) {
				// 如果ContactsFragment为空，则创建一个并添加到界面上
				chineseFragment = new TestScoresRankingFragment(
						this.subjectNameStr);
				transaction.add(R.id.layout_central_content, chineseFragment);

			} else {
				// 如果ContactsFragment不为空，则直接将它显示出来
				transaction.show(chineseFragment);
			}

			break;

		case R.id.testscore_math_layout:
			if (mathFragment == null) {
				// 如果ContactsFragment为空，则创建一个并添加到界面上
				mathFragment = new TestScoresRankingFragment(
						this.subjectNameStr);
				transaction.add(R.id.layout_central_content, mathFragment);
			} else {
				// 如果ContactsFragment不为空，则直接将它显示出来
				transaction.show(mathFragment);
			}
			break;
		case R.id.testscore_english_layout:
			if (englishFragment == null) {
				// 如果ContactsFragment为空，则创建一个并添加到界面上
				englishFragment = new TestScoresRankingFragment(
						this.subjectNameStr);
				transaction.add(R.id.layout_central_content, englishFragment);
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
	
	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(TestScoresRankingActivity.this, toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}

}
