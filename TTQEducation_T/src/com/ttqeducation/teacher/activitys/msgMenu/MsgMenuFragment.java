package com.ttqeducation.teacher.activitys.msgMenu;


import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.activitys.message.ClassListActivity;
import com.ttqeducation.teacher.activitys.message.TeacherAndParentChatListActivity;
import com.ttqeducation.teacher.activitys.notice.NoticeFragmentActivity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MsgMenuFragment extends Fragment {

	private LinearLayout layout_notice;                   //通知公告
	private LinearLayout layout_homeAndSchoolIntera;	  //家校互动
	private LinearLayout layout_teacherGroupChat;		  //教师交流群
	private final String HXY="HXY";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View messageMuneLayout = inflater.inflate(R.layout.message_layout1, container, false);
		initView(messageMuneLayout);
		return messageMuneLayout;
	}
	
	public void initView(View view){
		
		this.layout_notice =(LinearLayout) view.findViewById(R.id.layout_notice);
		this.layout_homeAndSchoolIntera=(LinearLayout) view.findViewById(R.id.layout_homeAndSchoolIntera);
		this.layout_teacherGroupChat=(LinearLayout) view.findViewById(R.id.layout_teacherGroupChat);
		
		//为家校互动、教师交流群设置点击事件
		this.layout_homeAndSchoolIntera.setOnClickListener(new MyClickListener());
		this.layout_teacherGroupChat.setOnClickListener(new MyClickListener());
		this.layout_notice.setOnClickListener(new MyClickListener());
	}

	private class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()){
			case R.id.layout_homeAndSchoolIntera:
				Intent homeAndSchoolIntent= new Intent(getActivity(), TeacherAndParentChatListActivity.class);
				startActivity(homeAndSchoolIntent);
				break;
			case R.id.layout_teacherGroupChat:
				Intent teacherGroupChatIntent = new Intent(getActivity(),  ClassListActivity.class);
				startActivity(teacherGroupChatIntent);
				Log.i(HXY, "点击 layout_teacherGroupChat");
				break;
			case R.id.layout_notice:
				Intent noticeIntent = new Intent(getActivity(), NoticeFragmentActivity.class);
				startActivity(noticeIntent);
				Log.i(HXY, "点击 layout_notice");
				break;
			}
		}
		
	}
	
	
	
}
