package com.ttqeducation.teacher.activitys.notice;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.myViews.RefreshView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NoticeFragmentActivity extends Activity {

	private RefreshView refreshView = null;
	private TextView titleTextView = null;
	private LinearLayout titleBackLayout = null;  //返回按钮
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notice_fragment_panel);
		initView();
	}
	
	private void initView(){
		
		this.titleTextView = (TextView) (findViewById(R.id.action_bar).findViewById(R.id.title_text));
		titleTextView.setText("通知公告");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				clickBack();
			}
		});
	}

	
	public void clickBack(){
		this.finish();
	}
}
