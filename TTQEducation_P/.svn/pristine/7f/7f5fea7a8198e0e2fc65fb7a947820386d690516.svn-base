package com.ttqeducation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KnowledgePointGraphActivity extends Activity {

	// 标题栏部分
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null; // 标题栏文字；
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_knowledge_point_graph);
		
		getDataFromIntent();
		
		initView();
	}
	
	private void getDataFromIntent() {
		
	}
	
	private void initView() {
		// 标题栏部分实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("曲线对比图");
		
		// 返回
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				KnowledgePointGraphActivity.this.finish();
			}
		});
	}
}
