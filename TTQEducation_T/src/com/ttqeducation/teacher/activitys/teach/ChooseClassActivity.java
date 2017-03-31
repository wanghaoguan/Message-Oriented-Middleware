package com.ttqeducation.teacher.activitys.teach;

import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.beans.TeacherInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ChooseClassActivity extends Activity {

	//标题栏部分
	private LinearLayout titleBackLayout = null;
	private TextView titleTextView = null;
	String[] classes;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_class);
		//界面初始化
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		this.titleBackLayout =(LinearLayout) super.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout);
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ChooseClassActivity.this.finish();
			}
		});
		//实例化标题栏
		this.titleTextView=(TextView) super.findViewById(R.id.action_bar).findViewById(R.id.title_text);
		this.titleTextView.setText("班级选择");
		//载入数据
		classes = TeacherInfo.getInstance().getClasses();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChooseClassActivity.this, android.R.layout.simple_list_item_1, classes);
		ListView listView = (ListView)findViewById(R.id.lvChooseClass);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				if(classes.length>0 && classes!=null){
				    
					Intent intent = new Intent();
					intent.putExtra("choosedClassName", classes[position]);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
	}
	

}
