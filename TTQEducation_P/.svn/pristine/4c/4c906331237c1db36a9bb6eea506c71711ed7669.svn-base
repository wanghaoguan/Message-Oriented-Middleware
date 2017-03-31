package com.ttqeducation.activitys.payment;


import com.ttqeducation.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ExternalFragment extends Fragment {
	
	private TextView produceInfoTextView = null;		// 产品信息； 如：三个月
	private TextView producePriceTextView = null;		// 产品价格；如：10.0元
	
	
//	public ExternalFragment(){}
//	public ExternalFragment(String businessTypeID, String typeName, float fee){
//		this.businessTypeID = businessTypeID;
//		this.typeName = typeName;
//		this.fee = fee;
//	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.pay_external, container, false);
		Log.i("lvjie", "1-->onCreateView()...");
		initView(view);
		Log.i("lvjie", "2-->onCreateView()...");
		return view;
	}
	
	public void initView(View view){
		this.produceInfoTextView = (TextView) view.findViewById(R.id.produceInfo_textView);
		this.producePriceTextView = (TextView) view.findViewById(R.id.product_price);
		Log.i("lvjie", "initView(View view)-->"+PayDemoActivity.typeName+"   "+PayDemoActivity.fee+"元");
		this.produceInfoTextView.setText("提供服务"+PayDemoActivity.typeName);
		this.producePriceTextView.setText(PayDemoActivity.fee+"元");
	}
	
}
