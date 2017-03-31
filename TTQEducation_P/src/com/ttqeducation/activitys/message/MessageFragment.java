package com.ttqeducation.activitys.message;

import com.ttqeducation.R;
import com.ttqeducation.activitys.study.HomeworkArrangementFragment;
import com.ttqeducation.beans.UserCurrentState;
import com.ttqeducation.beans.UserInfo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageFragment extends Fragment {

	private LinearLayout homeAndSchoolInteraLayout = null;
	private LinearLayout noticeLayout = null;
	private TextView noReadNoticeNumTextView = null;
	private ImageView chatNewImageView = null;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View messageLayout = inflater.inflate(R.layout.message_layout1,
				container, false);
		
		initView(messageLayout);
		return messageLayout;
	}
	
	public void initView(View view){
		this.homeAndSchoolInteraLayout = (LinearLayout)view.findViewById(R.id.layout_homeAndSchoolIntera);
		this.noticeLayout = (LinearLayout)view.findViewById(R.id.layout_notice);
		this.noReadNoticeNumTextView = (TextView) view.findViewById(R.id.noReadNoticeNum_textView);
		this.chatNewImageView = (ImageView) view.findViewById(R.id.chatNew_img);
		
		// 设置家校互动 new的显示；
		if(UserCurrentState.getInstance().homeSchoolNew > 0){
			this.chatNewImageView.setVisibility(View.VISIBLE);
		}else {
			this.chatNewImageView.setVisibility(View.INVISIBLE);
		}
		
		if(UserInfo.getInstance().noReadNoticeNum <= 0){		// 未读通知数量少于0，则不显示new；
			this.noReadNoticeNumTextView.setVisibility(View.INVISIBLE);
		}else {
			this.noReadNoticeNumTextView.setVisibility(View.VISIBLE);
			this.noReadNoticeNumTextView.setText(""+UserInfo.getInstance().noReadNoticeNum);
		}
		
		this.homeAndSchoolInteraLayout.setOnClickListener(myClickListener);
		this.noticeLayout.setOnClickListener(myClickListener);
	}
	
	// 设置 new 的显示；
	public void setNewViewVisible(int noReadNoticeNum){
		if(noReadNoticeNum <= 0){		// 未读通知数量少于0，则不显示new；
			this.noReadNoticeNumTextView.setVisibility(View.INVISIBLE);
		}else {
			this.noReadNoticeNumTextView.setVisibility(View.VISIBLE);
			this.noReadNoticeNumTextView.setText(""+noReadNoticeNum);
		}
		
		// 设置家校互动 new的显示；
		if(UserCurrentState.getInstance().homeSchoolNew > 0){
			this.chatNewImageView.setVisibility(View.VISIBLE);
		}else {
			this.chatNewImageView.setVisibility(View.INVISIBLE);
		}
		
	}
	
	
	private OnClickListener myClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.layout_homeAndSchoolIntera:		// 点击的是 家校互动；
					Intent homeSchoolIntent = new Intent(getActivity(), HomeAndSchoolInteractionActivity.class);
					startActivity(homeSchoolIntent);
				break;

			case R.id.layout_notice:			// 点击的是通知公告；
					Intent noticeListIntent = new Intent(getActivity(), NoticeListActivity.class);
					startActivity(noticeListIntent);
				break;

			default:
				break;
			}
		}
	};
	
}
