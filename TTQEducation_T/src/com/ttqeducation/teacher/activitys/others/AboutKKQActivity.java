package com.ttqeducation.teacher.activitys.others;

/**
 * 吕杰
 * 
 */

import com.ttqeducation.teacher.R;



import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AboutKKQActivity extends Activity {

	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；
	private TextView appVersionTextView = null;
	private LinearLayout aboutKKQLayout = null;		// 关于；

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_kkq);

		initView();
	}

	public void initView() {
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("关于");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AboutKKQActivity.this.finish();
			}
		});
		
		this.aboutKKQLayout = (LinearLayout) super.findViewById(R.id.layout_aboutKKQ);
		this.aboutKKQLayout.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AboutKKQActivity.this, UserProtocolActivity.class);
				startActivity(intent);
				
			}
		});
		PackageManager packageManager = getApplicationContext().getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getApplicationContext().getPackageName(), 0);
			String currentAppVersion = packageInfo.versionName;
			appVersionTextView = (TextView)findViewById(R.id.teacherAppVersionTextView);
			appVersionTextView.setText(getResources().getString(R.string.app_name) + " " + currentAppVersion);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	
	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(AboutKKQActivity.this, toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}

}
