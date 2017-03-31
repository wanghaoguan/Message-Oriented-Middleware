package com.ttqeducation.activitys.payment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.R;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.UserCurrentState;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DesUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CaiFuTongActivity extends Activity {

	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；
	
	private WebView webView = null;
	// 因为ExternalFragment需要用到该参数，所以申明为static类型；
	private String businessTypeID = "";		// 付费类型ID；
	private String typeName = "";			// 付费类型名；
	private int fee = 0;				// 费用；
	
	private RefreshView refreshView = null;
	private String orderID = "";		// 保存从服务端获取的订单号，用于付费成功后修改订单；
	private String urlHead = "";		// 保存付费链接头部；如：http://www.hnkkq.com/
	
	private boolean isPurchaseSuccess = false;		// 是否支付成功；
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDataFromItent();
		getSchoolWebSiteFromNativeXML();
		setContentView(R.layout.activity_caifutong);
		
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);		
		submitOrderToDbByWS(UserInfo.getInstance().studentID, UserInfo.getInstance().parentName);
	}
	
	public void getDataFromItent(){
		this.businessTypeID = getIntent().getStringExtra("businessTypeID");
		String typeNameAndFee = getIntent().getStringExtra("typeNameAndFee");

		String [] tempString  = typeNameAndFee.split("            ￥");
		this.typeName = tempString[0];
		float temp = Float.parseFloat(tempString[1]);
		this.fee = (int)(temp*100);
		
		Log.i("lvjie", "1-->typeName="+this.typeName+"   fee="+this.fee);
	}
	
	// 从本地xml获取付费链接；
	public void getSchoolWebSiteFromNativeXML(){
		SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
		this.urlHead = pre.getString("schoolWebSite", "");
	}
	
	public void initView(){
		
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("财付通");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(isPurchaseSuccess){
					Intent intent = new Intent();
					setResult(20, intent);
					CaiFuTongActivity.this.finish();
				}
				
			}
		});
		
		this.webView = (WebView) findViewById(R.id.webView);
		// http://www.hnkkq.com/Adroid_Server/TenpayRequest.aspx?money=1&product_name=test&sp_billno=U072015051500000013
		String product_name = "提供服务：使用堂堂清教育软件"+this.typeName;
		try {
			product_name = URLEncoder.encode(product_name, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String paymentURL = CaiFuTongActivity.this.urlHead+"Adroid_Server/TenpayRequest.aspx?money="+
		this.fee+"&product_name="+product_name+"&sp_billno="+this.orderID+"&extraDays=0";
		Log.i("lvjie", "paymentURL="+paymentURL);

		this.webView.loadUrl(paymentURL);
		
		//启用支持javascript
		WebSettings settings = this.webView.getSettings();
		settings.setJavaScriptEnabled(true);
		
		//覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		webView.setWebViewClient(new WebViewClient(){
           @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	            // TODO Auto-generated method stub
	            //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
	        	Log.i("lvjie", "CaiFuTongActivity-->shouldOverrideUrlLoading()...url="+url);
	        	
	        	if (urlHead.equals(url.subSequence(0, urlHead.length()))) {		// 当出现支付成功页面，表示支付成功；
					isPurchaseSuccess = true;
				}
	            view.loadUrl(url);
	            return true;
	        }
        });
	}
	
	// lvjie 添加   向数据库提交订单；
	public void submitOrderToDbByWS(String studentID, String parentName){
		new AsyncTask<Object, Object, DataTable>(){
			private String schoolCode = "";
			protected void onPreExecute() {
				SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
				schoolCode = pre.getString("schoolCode", "");
				Log.i("lvjie", "submitOrderToDbByWS-->schoolCode="+schoolCode);
				CaiFuTongActivity.this.refreshView.show();
			};
			
			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DataTable dt_paymentOrderInfo = new DataTable();

				// 方法名
				String methodName = "pub_order_Add";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("title", "常规业务付费");
				paramsMap.put("body", "测试订单");
				paramsMap.put("total_fee", ""+PayDemoActivity.fee);
				paramsMap.put("price", ""+PayDemoActivity.fee);
				paramsMap.put("quantity", ""+1);
				paramsMap.put("schoolCode", schoolCode);
				paramsMap.put("stuID", params[0].toString());
				paramsMap.put("name", params[1].toString());
				paramsMap.put("trade_no", "");					// 这个表示什么？
				paramsMap.put("trade_satus", "wait_for_pay");
				paramsMap.put("trade_time", "2015-05-27");
				paramsMap.put("businessType", CaiFuTongActivity.this.businessTypeID);
				paramsMap.put("cus_ReportType", ""+0);
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
				if (result != null) {
					int count = result.getRowCount();
					for (int i = 0; i < count; i++) {
						try {
							CaiFuTongActivity.this.orderID = result.getCell(i, "out_trade_no");
							
							Log.i("lvjie", "  orderID="+CaiFuTongActivity.this.orderID);
							break;
						} catch (dataTableWrongException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}else {
				}
				// 关闭刷新；
				refreshView.dismiss();
				// 获取完数据后，初始化界面
				initView();
			};
			
		}.execute(studentID, parentName);
	}

	
}
