package com.ttqeducation.network;

import com.ttqeducation.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TestServerActivity extends Activity {

	private TextView showInfoTextView = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_server);
	
		initView();
	}
	
	public void initView(){

		this.showInfoTextView = (TextView)super.findViewById(R.id.showInfo_textView);
		this.showInfoTextView.setText(getIntent().getStringExtra("noticeCotent"));
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		Log.i("lvjie", "onNewIntent()...");
		this.showInfoTextView = (TextView)super.findViewById(R.id.showInfo_textView);
		this.showInfoTextView.setText(intent.getStringExtra("noticeCotent"));
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Log.i("lvjie", "onBackPressed()...");
	}
	
}
