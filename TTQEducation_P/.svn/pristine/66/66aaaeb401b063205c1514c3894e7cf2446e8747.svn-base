package com.ttqeducation.activitys.system;

/**
 * 吕杰
 * 该类主要用于测试需要，不属于项目的一部分;
 */

import com.ttqeducation.R;
import com.ttqeducation.activitys.study.HomeworkArrangementActivity;
import com.ttqeducation.activitys.study.TaskTypeChooseActivity;
import com.ttqeducation.activitys.study.UnitTestResultDetailActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class BranchActivity extends Activity {

	private Button wqwButton = null;
	private Button ljButton = null;
	private Button overButton = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_branch);

		initView();
	}

	public void initView() {
		this.wqwButton = (Button) super.findViewById(R.id.wqwBtn);
		this.ljButton = (Button) super.findViewById(R.id.ljBtn);
		this.overButton = (Button) super.findViewById(R.id.overBtn);

		// 王勤为点击事件；
		this.wqwButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.i("lvjie", "界面启动");
				Intent intent = new Intent(BranchActivity.this,
						MainActivity.class);
				startActivity(intent);
			}
		});

		// 吕杰点击事件；
		this.ljButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BranchActivity.this,
						ChooseSchoolActivity.class);
				startActivity(intent);
			}
		});

		// 完整过程点击事件；
		this.overButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BranchActivity.this,
						LaunchActivity.class);
//				Intent intent = new Intent(BranchActivity.this, UnitTestResultDetailActivity.class);
				startActivity(intent);
			}
		});
	}

}
