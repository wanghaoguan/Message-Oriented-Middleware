package com.ttqeducation.activitys.payment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.crypto.spec.DESKeySpec;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.ttqeducation.R;
import com.ttqeducation.activitys.study.StudyFragment;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.TestInfo;
import com.ttqeducation.beans.UserCurrentState;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DesUtil;

public class PayDemoActivity extends FragmentActivity {

	public static final String PARTNER = "2088811353352319";
	public static final String SELLER = "hnttq999@126.com";
	public static final String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMcNlMmE+ChissCuG3hTrTdwNBgVjGrx8zAu6E27dlSyK6e1VYntQ4mDhDneyLqZu0J7Z9BhR0/imBPqD9G8wv2UpX+GV1j7NofgTd/3iYZkPTBvMR8e9rzG0zq9z27brUQsB1X9nQj1zTQNINEt9B2O8hF4D1uaqlhJ1SkCw+PPAgMBAAECgYBRKJ9unvGTfdKGLzbyE6P/g5cp7rdG74mJLsb20qVHmTGlepD64Kt1yRLDiatsMbwhOjG0y8UItEnvbLKwgHc7tvBMiTN44fo9Jv9R8Hw42FfczZHVrBdj3zYj2dWou69y+pdNrZYlUmAUFF1ZkiA2T21tPfglG5nfTyxGbfYIWQJBAO0Dt42/3bdrJVpXZ/5Nek9A+qBbguZFVmPvcjp8wERTy2r708R/MOQ356fQM8VGynk885XWcjESHCTm5ZqKqV0CQQDW/2qMCQTa+Si9vF0XtWF8jZBW3wGhrN5lK3kv1XM6LXZR/D6RGvr3IyIQoQFbg/sIuAhJFY8eb51UBdNVGLMbAkEArsrq93LkRHyUU4fafcUNyp0VOGXEp6XDDHhuOYv/D2Tsw71sc6GJoQVJpoz5YSfP8pFs4rgABwhgESbndY2FUQJBAIJtpOebORTWfr7MHPWbFa8H+n6Y/1Zjlu9tNjGsniC3H8aQ7iv9YC65Y5sO8dHg+VPKwUwFBjQCpkEo35IKT+0CQQDDUiohLB0k+VwV+hOpxECTKfOTOEIcC+P1m0riOWTMWRgwG7cGqIsZqlZqFWfHvS1u8DNUuKGYlmKDSYkuMym4";
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
			 	Result resultObj = new Result((String) msg.obj);
				String resultStatus = resultObj.resultStatus;
				
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(PayDemoActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();
					updateOrderToDbByWS();
					// lvjie 添加;
					Intent intent = new Intent();
					setResult(10, intent);
					finish();
				} else if(TextUtils.equals(resultStatus, "8000")){
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000” 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
						Toast.makeText(PayDemoActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();
				}else if(TextUtils.equals(resultStatus, "4000")){
					//结果为4000表示手机上没有安装支付宝
					Toast.makeText(PayDemoActivity.this, "请确定手机上已安装了支付宝",
							Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(PayDemoActivity.this, "支付失败 ，失败代码 " + resultStatus,
							Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(PayDemoActivity.this, "检查结果为：" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		};
	};

	// 标题栏部分；
	private RelativeLayout titleBackLayout = null; // 标题栏返回按钮；
	
	// 因为ExternalFragment需要用到该参数，所以申明为static类型；
	public static String businessTypeID = "";		// 付费类型ID；
	public static String typeName = "";			// 付费类型名；
	public static float fee = 0.0f;				// 费用；
	private String urlHead = "";		// 保存付费链接头部；如：http://www.hnkkq.com/
	
	private String orderID = "";		// 保存从服务端获取的订单号，用于付费成功后修改订单；
	
	
	private RefreshView refreshView = null;
	
	//--tuimao 以下是插入数据库需要的数据，上面的没有用，不知道那些需要那些不需要。
	private String payInfo_title;
	private float payInfo_totalFee;
	private float payInfo_price;
	private String payInfo_schoolCode;
	private String payInfo_stuID;
	private String payInfo_name;
	private int payInfo_businessType;
	private int payInfo_cus_reportType;
	private String payInfo_payType;
	private String payInfo_subjectID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDataFromIntent();
		getSchoolWebSiteFromNativeXML();
		setContentView(R.layout.pay_main);
		
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		submitOrderToDbByWS(UserInfo.getInstance().studentID, UserInfo.getInstance().parentName);
		
	}
	
	public void initView(){
		// 标题栏部分 实例化；
		this.titleBackLayout = (RelativeLayout) super
				.findViewById(R.id.title_back_layout);
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				PayDemoActivity.this.finish();
			}
		});
		
	}
	
	// 从本地xml获取付费链接；
	public void getSchoolWebSiteFromNativeXML(){
		SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
		this.urlHead = pre.getString("schoolWebSite", "");
	}
	
	public void getDataFromIntent(){
		PayDemoActivity.this.businessTypeID = getIntent().getStringExtra("businessTypeID");
		String typeNameAndFee = getIntent().getStringExtra("typeNameAndFee");
		
		String [] tempString  = typeNameAndFee.split("            ￥");
		PayDemoActivity.typeName = tempString[0];
		PayDemoActivity.fee = Float.parseFloat(tempString[1]);
		
		//Log.i("shen", "--->typeNameAndFee"+typeNameAndFee);
		//Log.i("shen", "--->typeName="+PayDemoActivity.typeName+"   fee="+PayDemoActivity.fee);
		payInfo_title = getIntent().getStringExtra("payInfo_title");
		payInfo_totalFee = Float.parseFloat(tempString[1]);
		payInfo_price = Float.parseFloat(tempString[1]);
		SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
		payInfo_schoolCode = pre.getString("schoolCode", "");
		payInfo_stuID = UserInfo.getInstance().studentID;
		payInfo_name = UserInfo.getInstance().childName;
		payInfo_businessType = getIntent().getIntExtra("payInfo_businessType", -1);
		payInfo_cus_reportType = getIntent().getIntExtra("payInfo_cus_reportType", -1);
		payInfo_payType = getIntent().getStringExtra("payInfo_payType");
		payInfo_subjectID = getIntent().getStringExtra("payInfo_subjectID");
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 界面中的 付款 按钮点击事件，
	 * 
	 */
	public void pay(View v) {

		//String orderInfo = getOrderInfo("测试的商品", "该测试商品的详细描述", "0.01");
		//这句话显示在支付界面上，以及支付的金额设定
		String orderInfo = getOrderInfo("堂堂清教学互动服务", "提供服务事件"+PayDemoActivity.typeName, String.valueOf(payInfo_totalFee));
		String sign = sign(orderInfo);
		
		Log.i("lvjie", "---orderInfo="+orderInfo+"  sign="+sign);
		
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(PayDemoActivity.this);
				// 调用支付接口
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);		// 跳转到支付界面；
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

//	/**
//	 * check whether the device has authentication alipay account.
//	 * 查询终端设备是否存在支付宝认证账户  Button 按钮；
//	 * 
//	 */
//	public void check(View v) {
//		Runnable checkRunnable = new Runnable() {
//
//			@Override
//			public void run() {
//				PayTask payTask = new PayTask(PayDemoActivity.this);
//				boolean isExist = payTask.checkAccountIfExist();
//				
//				Message msg = new Message();
//				msg.what = SDK_CHECK_FLAG;
//				msg.obj = isExist;
//				mHandler.sendMessage(msg);
//			}
//		};
//
//		Thread checkThread = new Thread(checkRunnable);
//		checkThread.start();
//
//	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String subject, String body, String price) {
		
		// 合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + " 这里存放登录的账号" + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		//这个路径没有用到
		orderInfo += "&notify_url=" + "\"" + "http://www.hnkkq.com//Adroid_Server/NotifyUrl.aspx"
				+ "\"";
//		orderInfo += "&notify_url=" + "\"" + this.urlHead+"/Adroid_Server/NotifyUrl.aspx"
//				+ "\"";
		

		// 接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值
		// orderInfo += "&paymethod=\"expressGateway\"";
		Log.v("order:",orderInfo);
		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 获取外部订单号
	 * 
	 */
	public String getOutTradeNo() {
//		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
//				Locale.getDefault());
//		Date date = new Date();
//		String key = format.format(date);
//
//		Random r = new Random();
//		key = key + r.nextInt();
//		key = key.substring(0, 15);
//		return key;
		return PayDemoActivity.this.orderID;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

	// lvjie 添加   向数据库提交订单；
	public void submitOrderToDbByWS(String studentID, String parentName){
		new AsyncTask<Object, Object, DataTable>(){
			
			private String schoolCode = "";
			
			protected void onPreExecute() {
				SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
				schoolCode = pre.getString("schoolCode", "");
				//PayDemoActivity.this.refreshView.show();
			};
			
			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DataTable dt_paymentOrderInfo = new DataTable();

				// 方法名
				String methodName = "pub_order_Add";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("title", payInfo_title);
				paramsMap.put("body", "");			//--tuimao   不知道这个列是什么
				paramsMap.put("total_fee", ""+payInfo_totalFee);
				paramsMap.put("price", ""+payInfo_price);
				paramsMap.put("quantity", ""+1);
				paramsMap.put("schoolCode", payInfo_schoolCode);
				paramsMap.put("stuID", payInfo_stuID);
				paramsMap.put("name", payInfo_name);
				paramsMap.put("trade_no", "");					// 这个表示什么？  --tuimao应该是支付宝的订单号，没有用到，传入的是空白字符串
				paramsMap.put("trade_satus", "wait_for_pay");
				paramsMap.put("trade_time", "");//--tuimao   ,不用手动传入值，数据库自动设为系统时间
				paramsMap.put("businessType", ""+payInfo_businessType);
				paramsMap.put("cus_ReportType", ""+payInfo_cus_reportType);
				paramsMap.put("TokenID", DesUtil.getDesTokenID(UserCurrentState.getInstance().userID, "Admin203", 1));

				// 获取数据
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				Resources res = getResources();
				String companyURL = res.getString(R.string.company_service_url);
				if (companyURL == null) {// 如果没有值
					return null;
				}
				getDataTool.setURL(companyURL);
				try {
					dt_paymentOrderInfo = getDataTool.getDataAsTable(methodName,paramsMap);
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
				return dt_paymentOrderInfo;
			}
			
			protected void onPostExecute(DataTable result) {
				if(result != null){
					try {
						PayDemoActivity.this.orderID = result.getCell(0, "out_trade_no");
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// 关闭刷新；
				//refreshView.dismiss();
				// 获取完数据后，初始化界面
				initView();
				
			};
			
		}.execute(studentID, parentName);
	}
	
	
	// lvjie 添加  修改订单，表示付款；  没有返回值，暂时没做完；
	//-- tuimao 修改
	public void updateOrderToDbByWS(){
		new AsyncTask<Object, Object, DataTable>(){

			protected void onPreExecute() {
				//PayDemoActivity.this.refreshView.show();
			};
			
			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DataTable dt_paymentOrderInfo = new DataTable();
				SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
				String userDeadline = pre.getString("getUserDeadline", "");
				// 方法名
				String methodName = "pub_order_Update";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("out_trade_no", PayDemoActivity.this.orderID);
				paramsMap.put("trade_no", "");		// 这个参数 暂时不明确；???
				paramsMap.put("trade_status", "trade_success");
				int payTypee = Integer.parseInt(payInfo_payType);
				paramsMap.put("payType", ""+payTypee);
				paramsMap.put("extraDays", ""+0);
				paramsMap.put("oldDeadline", userDeadline);
				String tokenID = DesUtil.getDesTokenID(UserCurrentState.getInstance().userID, "Admin203", 1);
				paramsMap.put("TokenID", tokenID);

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
					String test = getDataTool.getDataAsString(methodName, paramsMap);
					//Log.i("shen", "--->test " + test);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return dt_paymentOrderInfo;
			}
			
			protected void onPostExecute(DataTable result) {
				getCurrentUserDeadline();
				//PayDemoActivity.this.refreshView.dismiss();
			};
			
		}.execute();
	}
	/**
	 * @author Tuimao
	 * 调用公司端存储过程pub_student_getBaseChargeDeadline
	 * 用来获取用户最新的deadline，获得之后将最新的deadline放入SharedPreference，key=getUserDeadline
	 * 获取最新的deadline之后，调用下面的addOrdinaryBusinessOrder/addCustomizedBusinessOrder方法，将普通/定制业务数据插入数据库
	 */
	public void getCurrentUserDeadline(){
		SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
		String userID = pre.getString("user_account", null);
		
		new AsyncTask<Object, Object, DataTable>(){

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				//PayDemoActivity.this.refreshView.show();
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DataTable dt = null;
				String methodName = "pub_student_getBaseChargeDeadline";				
				String tokenID = DesUtil.getDesTokenID(params[0].toString(), "Admin203", 1);
				Map< String, String>paramsMap = new HashMap<String, String>();
				paramsMap.put("studentID", params[0].toString());
				paramsMap.put("TokenID", tokenID);
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				getDataTool.setURL(getResources().getString(R.string.company_service_url));
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
					
					String newDeadline = "";
					try {
						String getDeadline = result.getCell(0, "endTime");
						newDeadline = getDeadline.replace('T', ' ').substring(0, 19);
						//更新deadline,
						SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
						SharedPreferences.Editor pre_editor = pre.edit();
						pre_editor.putString("getUserDeadline", newDeadline);
						pre_editor.commit();
						//更新过deadline之后，根据开通普通业务还是定制业务调用相应方法。
						if("普通业务".equals(payInfo_title)){
							addOrdinaryBusinessOrder();
						}
						if("定制业务".equals(payInfo_title)){
							addCustomizedBusinessOrder();
						}
						//PayDemoActivity.this.refreshView.dismiss();
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					Log.i("shen", "---get new deadline ,result is null ");
					//PayDemoActivity.this.refreshView.dismiss();
				}
			}
			
		}.execute(userID);
	}
	/**
	 * @author Tuimao
	 * 调用学校端存储过程pub_student_chargeModule_add
	 * 在获取最新的deadline之后，如果用户开通普通业务，调用这个方法向数据库中添加订单
	 */
	public void addOrdinaryBusinessOrder(){
		SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
		String userID = pre.getString("user_account", null);
		String deadline = pre.getString("getUserDeadline", null);
		String schoolURL = pre.getString("school_service_url", null);
		new AsyncTask<Object, Object, DataTable>(){

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				//PayDemoActivity.this.refreshView.show();
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				String result = "";
				String methodName = "pub_student_chargeModule_add";
				
				DesUtil.addTokenIDToSchoolWS();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Map<String, String>paramsMap = new HashMap<String, String>();
				paramsMap.put("studentID", params[0].toString());
				paramsMap.put("deadline", params[1].toString());
				paramsMap.put("TokenID", tokenID);
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				getDataTool.setURL(params[2].toString());
				try {
					result = getDataTool.getDataAsString(methodName, paramsMap);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				//PayDemoActivity.this.refreshView.dismiss();
			}
			
		}.execute(userID, deadline, schoolURL);
	}
	/**
	 * @author Tuimao
	 * 调用学校端存储过程pub_student_customizedBussiness_add
	 * 在获取最新的deadline之后，如果用户开通定制业务，调用这个方法向数据库中添加订单
	 */
	public void addCustomizedBusinessOrder(){
		SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
		String userID = pre.getString("user_account", null);
		String schoolURL = pre.getString("school_service_url", null);
		String typeFlag = "";
		if(PayDemoActivity.typeName.contains("学期")){
			typeFlag = "1";
		}else if(PayDemoActivity.typeName.contains("学年")){
			typeFlag = "2";
		}else{
			typeFlag = "3";
		}
		new AsyncTask<Object, Object, DataTable>(){

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				//PayDemoActivity.this.refreshView.show();
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				String result = "";
				String methodName = "pub_student_customizedBussiness_add";
				DesUtil.addTokenIDToSchoolWS();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Map<String, String>paramsMap = new HashMap<String, String>();
				paramsMap.put("studentID", params[0].toString());
				paramsMap.put("subjectID", params[1].toString());
				paramsMap.put("typeFlag", params[2].toString());
				paramsMap.put("TokenID", tokenID);
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				getDataTool.setURL(params[3].toString());
				try {
					result = getDataTool.getDataAsString(methodName, paramsMap);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RuntimeException e){
					Log.i("shen", "--->PayDemonActivity addCustomizedBusinessOrder RuntimeException "+e.toString());
				}
				return null;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				//PayDemoActivity.this.refreshView.dismiss();
			}
			
			
		}.execute(userID, payInfo_subjectID, typeFlag, schoolURL);
	}
}
