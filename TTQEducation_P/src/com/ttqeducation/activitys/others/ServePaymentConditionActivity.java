package com.ttqeducation.activitys.others;

/**
 * 吕杰
 * 
 */


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.R;
import com.ttqeducation.activitys.payment.CaiFuTongActivity;
import com.ttqeducation.activitys.payment.PayDemoActivity;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.TestInfo;
import com.ttqeducation.beans.UserCurrentState;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.MyListView;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DensityUtils;
import com.ttqeducation.tools.DesUtil;
import com.ttqeducation.wxapi.PayActivity;

import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class ServePaymentConditionActivity extends Activity {	
	
	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；
	private RefreshView refreshView = null;
		
	private MyListView myListView = null;
	private MyAdapter mAdapter = null; // 得到一个MyAdapter对象,用于与listview绑定；
	
	private List<TestInfo> listPaymentConditions = new ArrayList<TestInfo>();
	private List<TestInfo> customizedBusinessItem = new ArrayList<TestInfo>();
	private List<TestInfo> ordinaryBusinessItem = new ArrayList<TestInfo>();
	
	private Dialog paymentPlatformChooseDialog = null; // 支付平台选择对话框；
	
	// 该变量的作用是在下一个界面当支付成功，销毁掉这个界面，方便下一个界面直接跳到主界面；
	public static ServePaymentConditionActivity instance = null;
	
	private int position = 0;		// 点击的条目是第几条；
	
	private LinearLayout businessChooseLinearLayout = null;//上方选择普通业务还是定制业务
	private LinearLayout subjectChooseLinearLayout = null;//上方选择科目
	private LinearLayout showAllBusinessLinearLayout = null;//控制所有的业务是显示还是隐藏
	private LinearLayout showAllSubjectLinearLayout = null;//控制所有科目是显示还是隐藏
	private ListView selectBusinessListView = null;//ListView通过Adapter将内容显示
	private ListView selectSubjectListView = null;
	private TextView businessTextView = null;//选择一个业务类型之后，将上方文本改为选择的业务
	private TextView subjectTextView = null;//选择一个科目之后，将上方文本改为选择的科目
	private String[] businessTypeArr = new String[] {"普通业务", "定制业务"};
	private String[] subjectNameArr;//存放所有的科目和ID，在函数onCreat--->getAllSubjectNameAndID中获取值
	private String[] subjectIDArr;
	
	private String payInfo_title;
	private int payInfo_businessType;
	private int payInfo_cus_reportType;
	private String payInfo_subjectID;
	private String payInfo_payType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_payment_condition);
		
		instance = this;
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
//		generateData();
//		initView();
		SharedPreferences pre = getSharedPreferences("TTQAndroid",MODE_PRIVATE);
		String schoolCode = pre.getString("schoolCode", "");
		Log.i("lvjie", "schoolCode = "+schoolCode);
		getCustomizedBusinessInfo(schoolCode);//获取学校定制业务信息
		getAllSubjectNameAndID();//获取科目信息
		//调用这个方法的时候获得了所有普通业务的信息，同时将普通业务信息显示出来，即付费界面默认显示的是普通业务
		getPaymentConditionInfoByWS(schoolCode);
		
	}

	public void initView(){
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("选择开通项");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ServePaymentConditionActivity.this.finish();
			}
		});
		showAllBusinessItems();
		initTwoListView();
		

		
	}
	
	//tuimao  --原来放在initView里面用来显示myListView里面的内容，购买的那些
	//中间的ListView要显示的购买选项。
	public void showAllBusinessItems(){
		this.myListView = (MyListView) super.findViewById(R.id.listView_payment_choose);
		this.myListView.setPullRefreshEnable(false); // 不提供下拉刷新功能；
		this.myListView.setPullLoadEnable(false); // 不提供上拉加载功能；

		this.mAdapter = new MyAdapter(this);
		this.myListView.setAdapter(this.mAdapter);
		this.myListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//--tuimao 这个position是从1开始的，不是从0开始的？
				ServePaymentConditionActivity.this.position = position-1;

				initPaymentPlatformChooseDialog();	// 初始化支付平台选择对话框；
			}
			
		});
	}
	/**
	 * @author Tuimao
	 * 初始化两个下拉列表，设置事件
	 */
	public void initTwoListView(){
		this.businessChooseLinearLayout = (LinearLayout) super.findViewById(R.id.businessChooseLinearLayout);
		this.subjectChooseLinearLayout = (LinearLayout) super.findViewById(R.id.subjectChooseLinearLayout);
		this.showAllBusinessLinearLayout = (LinearLayout) super.findViewById(R.id.showAllBusinessLinearLayout);
		this.showAllSubjectLinearLayout = (LinearLayout) super.findViewById(R.id.showAllSubjectLinearLayout);
		this.selectBusinessListView = (ListView) super.findViewById(R.id.selectBusinessListView);
		this.selectSubjectListView = (ListView) super.findViewById(R.id.selectSubjectListView);
		//点击ListView后，设置ListView的文本。
		this.businessTextView = (TextView) super.findViewById(R.id.businessChooseTextView);
		this.subjectTextView = (TextView) super.findViewById(R.id.subjectChooseTextView);
		//为两个ListView设置点击事件
		this.businessChooseLinearLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showAllSubjectLinearLayout.setVisibility(View.GONE);
				if(showAllBusinessLinearLayout.getVisibility() == View.VISIBLE){
					showAllBusinessLinearLayout.setVisibility(View.GONE);
				}else{
					showAllBusinessLinearLayout.setVisibility(View.VISIBLE);
				}
			}
		});
		this.subjectChooseLinearLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showAllBusinessLinearLayout.setVisibility(View.GONE);
				if(showAllSubjectLinearLayout.getVisibility() == View.VISIBLE){
					showAllSubjectLinearLayout.setVisibility(View.GONE);
				}else{
					showAllSubjectLinearLayout.setVisibility(View.VISIBLE);
				}
			}
		});
		//两个ListView
		//此时三个数组应已有要用的数据
//		for(int i=0;i<subjectNameArr.length;i++){
//			Log.i("shen", "--->subjectNameArr " + subjectNameArr[i]);
//			Log.i("shen", "--->subjectIDArr " + subjectIDArr[i]);
//		}
		ArrayAdapter<String> businessListViewAdatpter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_subject, businessTypeArr);
		selectBusinessListView.setAdapter(businessListViewAdatpter);
		ArrayAdapter<String> subjectListViewAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_subject, subjectNameArr);
		selectSubjectListView.setAdapter(subjectListViewAdapter);
		selectBusinessListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				showAllBusinessLinearLayout.setVisibility(View.GONE);
				businessTextView.setText(businessTypeArr[position]);
				if("普通业务".equals(businessTypeArr[position])){
					//点击了普通业务
					subjectChooseLinearLayout.setVisibility(View.GONE);
					listPaymentConditions.clear();
					listPaymentConditions.addAll(ordinaryBusinessItem);
					showAllBusinessItems();
					
					payInfo_title = "普通业务";
				}
				if("定制业务".equals(businessTypeArr[position])){
					//点击了定制业务
					subjectChooseLinearLayout.setVisibility(View.VISIBLE);
					listPaymentConditions.clear();
					listPaymentConditions.addAll(customizedBusinessItem);
					showAllBusinessItems();
					
					payInfo_title = "定制业务";
				}
			}
		});
		selectSubjectListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//所有科目列表
				showAllSubjectLinearLayout.setVisibility(View.GONE);
				subjectTextView.setText(subjectNameArr[position]);
				payInfo_subjectID = subjectIDArr[position];
			}
		});
	}
	// 模拟数据；
	public void generateData(){
		TestInfo testInfo;
		for(int i=0; i<5; i++){
			testInfo = new TestInfo(i+"", "开通付费项目 "+i);
			this.listPaymentConditions.add(testInfo);
		}
	}
	
	public void initPaymentPlatformChooseDialog(){
		if (this.paymentPlatformChooseDialog == null) {
			View view = LayoutInflater.from(ServePaymentConditionActivity.this).inflate(
					R.layout.dialog_payment_platform_choice, null);
			this.paymentPlatformChooseDialog = new Dialog(ServePaymentConditionActivity.this,
					R.style.alertdialog_style);
			this.paymentPlatformChooseDialog.setContentView(view);
			this.paymentPlatformChooseDialog.setCanceledOnTouchOutside(true);
			this.paymentPlatformChooseDialog.show();

			// 动态设置界面大小；
			DisplayMetrics metrics = getResources().getDisplayMetrics(); // 用来获取屏幕的分辨率；
			int screenWidthPX = metrics.widthPixels;
			int screenWidthDP = (int) DensityUtils.px2dp(ServePaymentConditionActivity.this,
					screenWidthPX);

			// 设置图片的大小；
			int layoutWidthDP = (screenWidthDP * 4) / 5;
			int layoutWidthPX = DensityUtils
					.dp2px(ServePaymentConditionActivity.this, layoutWidthDP);

			LayoutParams params = view.getLayoutParams();
			params.width = layoutWidthPX;
			view.setLayoutParams(params);

			// 添加点击事件
			view.findViewById(R.id.zhifubao_layout).setOnClickListener(
					new MyOnClickListener());
			view.findViewById(R.id.weixin_layout).setOnClickListener(
					new MyOnClickListener());
			

		} else {
			this.paymentPlatformChooseDialog.show();
		}
	}
	
	private class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.zhifubao_layout:		// 选择支付宝支付；
				if("定制业务".equals(businessTextView.getText().toString())){
					if("科目".equals(subjectTextView.getText().toString())){
						showToast("请科目类型！");
						return;
					}
				}
				// 进入到下一个界面；  服务缴费目前还存在问题，如：缴费成功，如何回到主界面，更新标识；
				Intent zhifubao = new Intent(ServePaymentConditionActivity.this, PayDemoActivity.class);
//				zhifubao.putExtra("businessTypeID", myPickerView.getTextKey());
//				zhifubao.putExtra("typeNameAndFee", myPickerView.getTextValue());
				zhifubao.putExtra("businessTypeID", listPaymentConditions.get(
						ServePaymentConditionActivity.this.position).getUseID());
				if("普通业务".equals(payInfo_title)){
					payInfo_cus_reportType = 0;
					payInfo_businessType = Integer.parseInt(listPaymentConditions.get(ServePaymentConditionActivity.this.position).getUseID());
				}
				if("定制业务".equals(payInfo_title)){
					payInfo_businessType = 0;
					payInfo_cus_reportType = Integer.parseInt(listPaymentConditions.get(ServePaymentConditionActivity.this.position).getUseID());
				}
				zhifubao.putExtra("payInfo_title", payInfo_title);
				payInfo_payType = "1";
				zhifubao.putExtra("payInfo_payType", payInfo_payType);
				zhifubao.putExtra("payInfo_subjectID", payInfo_subjectID);
				zhifubao.putExtra("payInfo_businessType", payInfo_businessType);
				zhifubao.putExtra("payInfo_cus_reportType", payInfo_cus_reportType);
				zhifubao.putExtra("typeNameAndFee", listPaymentConditions.get(
						ServePaymentConditionActivity.this.position).getTestName());
				
				startActivityForResult(zhifubao, 100);
				ServePaymentConditionActivity.this.paymentPlatformChooseDialog.dismiss();
				break;
				
			case R.id.weixin_layout:		// 选择财付通支付；
											//财付通不用了，换成微信支付
//				Intent caifutong = new Intent(ServePaymentConditionActivity.this, CaiFuTongActivity.class);
//
//				caifutong.putExtra("businessTypeID", listPaymentConditions.get(
//						ServePaymentConditionActivity.this.position).getUseID());
//				caifutong.putExtra("typeNameAndFee", listPaymentConditions.get(
//						ServePaymentConditionActivity.this.position).getTestName());
//				
//				startActivity(caifutong);
//				ServePaymentConditionActivity.this.paymentPlatformChooseDialog.dismiss();
				if("定制业务".equals(businessTextView.getText().toString())){
					if("科目".equals(subjectTextView.getText().toString())){
						showToast("请科目类型！");
						return;
					}
				}
				Intent weixinIntent = new Intent(ServePaymentConditionActivity.this, PayActivity.class);
				if("普通业务".equals(payInfo_title)){
					payInfo_cus_reportType = 0;
					payInfo_businessType = Integer.parseInt(listPaymentConditions.get(ServePaymentConditionActivity.this.position).getUseID());
				}
				if("定制业务".equals(payInfo_title)){
					payInfo_businessType = 0;
					payInfo_cus_reportType = Integer.parseInt(listPaymentConditions.get(ServePaymentConditionActivity.this.position).getUseID());
				}
				weixinIntent.putExtra("payInfo_title", payInfo_title);
				payInfo_payType = "6";
				weixinIntent.putExtra("payInfo_payType", payInfo_payType);
				weixinIntent.putExtra("payInfo_subjectID", payInfo_subjectID);
				weixinIntent.putExtra("payInfo_businessType", ""+payInfo_businessType);
				weixinIntent.putExtra("payInfo_cus_reportType", ""+payInfo_cus_reportType);
				weixinIntent.putExtra("typeNameAndFee", listPaymentConditions.get(
						ServePaymentConditionActivity.this.position).getTestName());
				startActivity(weixinIntent);
				ServePaymentConditionActivity.this.paymentPlatformChooseDialog.dismiss();
				break;
			default:
				break;
			}
		}
		
	}

	// 从服务端获取付费条件信息；
	public void getPaymentConditionInfoByWS(String schoolCode){
		new AsyncTask<Object, Object, DataTable>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}
			
			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DataTable dt_paymentConditionInfos = new DataTable();

				// 方法名
				String methodName = "APP_getPaymentOptionsInfoBySchoolCode";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("schoolCode", params[0].toString());
				paramsMap.put("TokenID", DesUtil.getDesTokenID(UserCurrentState.getInstance().userID, "Admin203", 1));
				// 获取数据
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				// 从本地获取学校URL
				Resources res = getResources();
				String companyURL = res.getString(R.string.company_service_url);
				if (companyURL == null) {// 如果没有值
					return null;
				}
				getDataTool.setURL(companyURL);
				try {
					dt_paymentConditionInfos = getDataTool.getDataAsTable(methodName,paramsMap);
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return dt_paymentConditionInfos;
			}
			
			protected void onPostExecute(DataTable result) {
				if (result != null) {
					TestInfo paymentCondition;
					int count = result.getRowCount();
					for (int i = 0; i < count; i++) {
						try {
							String businessTypeID = result.getCell(i, "businessTypeID");
							String typeName = result.getCell(i, "typeName");
							String fee = result.getCell(i, "fee");
							int validDays = Integer.parseInt(result.getCell(i, "validDays"));
							
							Log.i("lvjie", "businessTypeID="+businessTypeID+"  typeName="+typeName+"  fee="+fee);
							paymentCondition = new TestInfo(businessTypeID, typeName+"            ￥"+fee, validDays);
							//listPaymentConditions.add(paymentCondition);
							ordinaryBusinessItem.add(paymentCondition);
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					listPaymentConditions.clear();
					listPaymentConditions.addAll(customizedBusinessItem);
				}else {
				}
				Log.i("lvjie", "关闭刷新");
				// 关闭刷新；
				refreshView.dismiss();
				// 获取完数据后，初始化界面
				initView();
			};
			
		}.execute(schoolCode);
	}
	/**
	 * @author Tuimao
	 * @param SchoolCode
	 * 根据学校代码查询学校的定制业务，上面的函数查询的是普通业务
	 * 使用存储过程pub_school_customizedBusiness_queryBySchoolCode，传入参数schoolID，TokenID
	 */
	public void getCustomizedBusinessInfo(String schoolCode){
		new AsyncTask<Object, Object, DataTable>(){

			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DataTable dt = null;
				
				String methodName = "pub_school_customizedBusiness_queryBySchoolCode";
				
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("schoolID", params[0].toString());
				String tokenID = DesUtil.getDesTokenID(UserCurrentState.getInstance().userID, "Admin203", 1);
				paramsMap.put("TokenID", tokenID);
				String companyURL = getResources().getString(R.string.company_service_url);
				
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				getDataTool.setURL(companyURL);
				
				try {
					dt = getDataTool.getDataAsTable(methodName, paramsMap);
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return dt;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				if(result != null){
					try {
						TestInfo payment;
						for(int i=0;i<result.getRowCount();i++){
							String businessTypeID = result.getCell(i, "typeID");
							String typeName = result.getCell(i, "typeDescription");
							String fee = result.getCell(i, "defaultFee");
							payment = new TestInfo(businessTypeID, typeName + "            ￥" + fee);
							customizedBusinessItem.add(payment);
						}
						//Log.i("shen", "---->typeDescription" + result.getCell(0, "typeDescription"));
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				super.onPostExecute(result);
			}
			
		}.execute(schoolCode);
	}
	/**
	 * @author Tuimao
	 * 获得所有的科目名称和相应的ID
	 */
	public void getAllSubjectNameAndID(){
		new AsyncTask<Object, Object, DataTable>(){

			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DataTable dt = null;
				String methodName = "pub_subject_select";
				Map<String, String> paramsMap = new HashMap<String, String>();
				
				
				//String tokenID = DesUtil.getDesTokenID(UserCurrentState.getInstance().userID, "Admin203", 1);
				DesUtil.addTokenIDToSchoolWS();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				paramsMap.put("TokenID", tokenID);
				SharedPreferences pre = getSharedPreferences("TTQAndroid", getApplicationContext().MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				getDataTool.setURL(schoolURL);
				try {
					dt = getDataTool.getDataAsTable(methodName, paramsMap);
				} catch (dataTableWrongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return dt;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result != null){
					try {
						subjectNameArr = new String[result.getRowCount()];
						subjectIDArr = new String[result.getRowCount()];
						for(int i=0;i<result.getRowCount();i++){
							subjectIDArr[i] = result.getCell(i, "subjectID");
							subjectNameArr[i] = result.getCell(i, "subjectName");
						}
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					Log.i("shen", "--->ServePayment getAllSubjectNameAndID result is null");
				}
			}
			
		}.execute();
	}
	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(this, toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}

	
	@Override
	// 从下一个界面传递参数到此界面；
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i("lvjie", "ServePaymentConditionActivity--->onActivityResult()...");
		if(requestCode == 100){		// 表示是从支付宝支付传递返回过来；
			if(resultCode == 10){	// 表示支付成功
				//setModuleUseTrue();
				Intent intent = new Intent();
				setResult(10, intent);
				finish();
			}
		}else if(requestCode == 200){	// 表示从财付通支付传递返回过来；
			if(resultCode == 20){	// 表示支付成功
				//setModuleUseTrue();
				Intent intent = new Intent();
				setResult(10, intent);
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	// 设置模块都可以使用；
	public void setModuleUseTrue(){
		int len = UserInfo.getInstance().moduleUse.length;
		for(int i=0; i<len; i++){
			UserInfo.getInstance().moduleUse[i] = 1;
		}
	}
	
	// 新建一个类继承BaseAdapter，实现listview与数据的绑定
	private class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater; // 得到一个LayoutInfalter对象用来导入布局

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return listPaymentConditions.size(); // 返回数组的长度
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewChild viewChild;
			if (view == null) {
				view = mInflater.inflate(
						R.layout.payment_choose_item_one, null);
				viewChild = new ViewChild();
				/* 得到各个控件的对象 */
				viewChild.paymentInfoTextView= (TextView) view
						.findViewById(R.id.payment_info_textview);
				viewChild.paymentBuyTextView = (TextView) view
						.findViewById(R.id.payment_buy_textview);
				viewChild.rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);

				view.setTag(viewChild); // 绑定ViewHolder对象
			} else {
				viewChild = (ViewChild) view.getTag(); // 取出ViewHolder对象
			}

			/* 设置TextView显示的内容，即我们存放在listHomeworkArrageConditions中的数据 */
			viewChild.paymentInfoTextView.setText(listPaymentConditions.get(position).getTestName());
			viewChild.paymentBuyTextView.setText("购买");
		
			// 控制右边背景色；
			int value = position % 3;
			if(value == 0){
				viewChild.rightLayout.setBackgroundResource(R.drawable.linearlayout_frame_circle_right_blue);
			}else if(value == 1){
				viewChild.rightLayout.setBackgroundResource(R.drawable.linearlayout_frame_circle_right_red);
			}else{
				viewChild.rightLayout.setBackgroundResource(R.drawable.linearlayout_frame_circle_right_orange);
			}

			return view;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	// 存放列表子项的控件，用于在MyAdapter中设置;
	public final class ViewChild {
		public TextView paymentInfoTextView; // 付费信息;
		public TextView paymentBuyTextView; // 购买;
		
		public LinearLayout rightLayout;	  // 右边的背景图；
	}
	
	
}
