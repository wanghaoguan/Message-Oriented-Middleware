package com.ttqeducation.activitys.others;

/**
 * 吕杰
 * 
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.R;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.GeneralTools;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class AboutKKQActivity extends Activity {

	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；

	private TextView tvAppVersion = null;
	
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
		
		Resources resources = getResources();
		String appName = resources.getString(R.string.app_name);
		
		//从AndroidManifest获取app version
		PackageManager packageManager = getApplicationContext().getPackageManager();
		PackageInfo packageInfo;
		try {
			packageInfo = packageManager.getPackageInfo(getApplicationContext().getPackageName(), 0);
			String currentAppVersion = packageInfo.versionName;
			tvAppVersion = (TextView)findViewById(R.id.tvAppVersion);
			tvAppVersion.setText(appName + " " + currentAppVersion);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//不再从R.String获取appVersion
		//String appVersion = resources.getString(R.string.app_version);	
		//tvAppVersion = (TextView)findViewById(R.id.tvAppVersion);
		//tvAppVersion.setText(appName + " " + appVersion);
		
		this.aboutKKQLayout = (LinearLayout) super.findViewById(R.id.layout_aboutKKQ);
		this.aboutKKQLayout.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AboutKKQActivity.this, UserProtocolActivity.class);
				startActivity(intent);
				
			}
		});
		
	}

	
	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(AboutKKQActivity.this, toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}

}
