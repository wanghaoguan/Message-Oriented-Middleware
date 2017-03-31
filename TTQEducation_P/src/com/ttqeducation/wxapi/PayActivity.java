package com.ttqeducation.wxapi;


import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelmsg.GetMessageFromWX;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.ttqeducation.R;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.UserCurrentState;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DesUtil;

import android.R.integer;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PayActivity extends Activity {
	
	private IWXAPI api;
	public static RefreshView refreshView = null;
	public static PayActivity instance = null;
	
	private String payInfo_title;
	private String payInfo_totalFee;
	private String payInfo_price;
	private String payInfo_businessType;
	private String payInfo_cus_reportType;
	private String payInfo_payType;
	private String payInfo_schoolCode;
	private String payInfo_userID;
	private String payInfo_userName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		refreshView = new RefreshView(this, R.style.full_screen_dialog);
		
		setContentView(R.layout.pay_wxpay_confirm);
		TextView titleTextView = (TextView) (super.findViewById(R.id.action_bar)).findViewById(R.id.title_text);
		titleTextView.setText("生成微信订单");
		LinearLayout backLinearLayout = (LinearLayout) (super.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		backLinearLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PayActivity.this.finish();
			}
		});
		//向数据库添加一条新订单,返回一个out_trade_no，
		//用这个out_trade_no作为参数传给微信，才能获得支付所需要的参数prepay_id
		addOrderBeforePay();

				
	}
	/**
	 * 获取到out_trade_no之后调用这个方法
	 * 首先使用httpPost获取prepay_id，然后调用startPay调起支付。
	 */
	public void getPrepayIDThenStartPayAfterGetOutTradeNo(){
		api = WXAPIFactory.createWXAPI(getApplicationContext(), null);
		
		String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		String entity = beforeGetPrepayID();
		refreshView.show();
		new AsyncTask<Object, Object, byte[]>() {

			@Override
			protected byte[] doInBackground(Object... params) {
				// TODO Auto-generated method stub
				
				String getUrl = params[0].toString();
				String getEntity = params[1].toString();
				byte[] buf = Util.httpPost(getUrl, getEntity);
				return buf;
			}

			@Override
			protected void onPostExecute(byte[] result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result != null){
					String content = new String(result);
					//获取prepayID
					String getPrepayID = content.substring(355, 391);
					//Map中已经获取到了需要的数据
					//Map<String, String> XMLMap = new HashMap<String, String>();
					//XMLMap = decodeXml(content);
					
					//return_code为"SUCCESS"时有以下返回结果
//					Log.i("shen", "---PayActivity---test map appid: " + XMLMap.get("appid"));
//					Log.i("shen", "---PayActivity---test map mch_id: " + XMLMap.get("mch_id"));
//					Log.i("shen", "---PayActivity---test map nonce_str: " + XMLMap.get("nonce_str"));
//					Log.i("shen", "---PayActivity---test map sign: " + XMLMap.get("sign"));
//					Log.i("shen", "---PayActivity---test map result_code: " + XMLMap.get("result_code"));
//					//return_code为"SUCCESS"，result_code为"OK"时有以下结果
//					Log.i("shen", "---PayActivity---test map trade_type: " + XMLMap.get("trade_type"));
//					Log.i("shen", "---PayActivity---test map prepay_id: " + XMLMap.get("prepay_id"));
					
					//调用微信支付api
//					--------------------尝试调起支付--------------------
					PayReq req = new PayReq();
					req.appId 			= "wxcc588a0e7b481be8";
					req.partnerId 		= "1421286902";
					req.prepayId 		= getPrepayID;
					//req.prepayId 		= XMLMap.get("prepay_id");
					req.packageValue 	= "Sign=WXPay";
					req.nonceStr	 	= get_nonce_str();
					req.timeStamp 		= String.valueOf(get_timestamp());
					
					List<NameValuePair> signParams = new LinkedList<NameValuePair>();
					signParams.add(new BasicNameValuePair("appid", req.appId));
					signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
					signParams.add(new BasicNameValuePair("package", req.packageValue));
					signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
					signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
					signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
					req.sign = getSignBeforeGetPrepayID(signParams);
					//req.sign
					startPay(req);
				}
			}
			
		}.execute(url, entity);
	}
	/**
	 * 为了获取预支付订单，准备调用微信接口需要参入的10个参数
	 * @return
	 */
	public String beforeGetPrepayID(){
		String nonce_str = get_nonce_str();//得到随机字符串
		String IOS_XMLStr = null;
		List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
		packageParams.add(new BasicNameValuePair("appid", "wxcc588a0e7b481be8"));		//appID
		packageParams.add(new BasicNameValuePair("body", payInfo_title + ":" + WXPayEntryActivity.payInfo_typeName));
		packageParams.add(new BasicNameValuePair("mch_id", "1421286902"));				//商户ID
		packageParams.add(new BasicNameValuePair("nonce_str", nonce_str));				//随机字符串
		packageParams.add(new BasicNameValuePair("notify_url", "http://www.hnkkq.com"));//瞎填的，估计没用
		packageParams.add(new BasicNameValuePair("out_trade_no", WXPayEntryActivity.payInfo_out_trade_no));		//订单号
		packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));		//瞎填的，估计没用
		//钱按分来算
//		int int_totalFee = Integer.parseInt(payInfo_totalFee.substring(0, 3));
//		String string_totalFee = String.valueOf(int_totalFee*100);
		float float_price = Float.parseFloat(payInfo_totalFee);
		int int_price = (int)float_price;
		String string_price = String.valueOf(int_price*100);

		//付费总金额string_price
		packageParams.add(new BasicNameValuePair("total_fee", "1"));					
		packageParams.add(new BasicNameValuePair("trade_type", "APP"));
		String sign = getSignBeforeGetPrepayID(packageParams);
		packageParams.add(new BasicNameValuePair("sign", sign));

		String xmlStr = stringToXML(packageParams);
		//Log.i("shen", "--->PayActivity---xml String: " + xmlStr);
		try {
			//这里必须要将提交的XML编码为“ISO8859-1”，否则签名失败！！！
			IOS_XMLStr = new String(xmlStr.getBytes(), "ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return IOS_XMLStr;
	}
	/**
	 * 获取要传的参数：nonce_str
	 * @return
	 */
	public String get_nonce_str(){
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(1000)).getBytes());
	}
	/**
	 * 获取要传的参数：timestamp
	 * @return
	 */
	public long get_timestamp(){
		return System.currentTimeMillis()/1000;
	}
	/**
	 * 获取prepay_id之前要做的事
	 * 获取要传的参数：sign
	 * @param params
	 * @return
	 */
	public String getSignBeforeGetPrepayID(List<NameValuePair> params){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<params.size();i++){
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append("HunanTangTangQing855587771969126");//key值
		
		String sign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		return sign;
	}
	/**
	 * 获取prepay_id之前要做的事
	 * 传入一个List<NameValuePair>，返回一个xml格式的字符串
	 * @param params
	 * @return
	 */
	public String stringToXML(List<NameValuePair> params){
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for(int i=0;i<params.size();i++){
			sb.append("<" + params.get(i).getName() + ">");
			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");
		return sb.toString();
	}
	/**
	 * 传入一个xml格式的字符串，返回一个map
	 * @param content
	 * @return
	 */
	public Map<String,String> decodeXml(String content) {  
		  
        try {  
            Map<String, String> xml = new HashMap<String, String>();  
            XmlPullParser parser = Xml.newPullParser();  
            parser.setInput(new StringReader(content));  
            int event = parser.getEventType();  
            while (event != XmlPullParser.END_DOCUMENT) {  
  
                String nodeName=parser.getName();  
                switch (event) {  
                    case XmlPullParser.START_DOCUMENT:  
  
                        break;  
                    case XmlPullParser.START_TAG:  
  
                        if("xml".equals(nodeName)==false){  
                            //实例化student对象  
                            xml.put(nodeName,parser.nextText());  
                        }  
                        break;  
                    case XmlPullParser.END_TAG:  
                        break;  
                }  
                event = parser.next();  
            }  
  
            return xml;  
        } catch (Exception e) {  
            Log.e("orion",e.toString());
        }  
        return null;  
  
    }
	public void startPay(PayReq pre){
		api.registerApp(Constants.APP_ID);
		
		//判断本机是否装有微信
		boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
		if(isPaySupported){
			api.sendReq(pre);
			//refreshView.dismiss();
		}else{
			Toast.makeText(getApplicationContext(), "请确认本机安装有微信！", Toast.LENGTH_SHORT).show();
			refreshView.dismiss();
			finish();
		}
		
	}
/**
 * 在支付之前，先向公司端的数据库添加一条记录
 */
	public void addOrderBeforePay(){
		new AsyncTask<Object, Object, DataTable>(){

			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DataTable dt = null;
				String methodName = "pub_order_Add";
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("title", payInfo_title);
				paramsMap.put("body", "");
				paramsMap.put("total_fee", payInfo_totalFee);
				paramsMap.put("price", payInfo_price);
				paramsMap.put("quantity", ""+1);
				paramsMap.put("schoolCode", payInfo_schoolCode);
				paramsMap.put("stuID", payInfo_userID);
				paramsMap.put("name", payInfo_userName);
				paramsMap.put("trade_no", "");
				paramsMap.put("trade_satus", "wait_for_pay");
				paramsMap.put("trade_time", "");
				paramsMap.put("businessType", payInfo_businessType);
				paramsMap.put("cus_ReportType", payInfo_cus_reportType);
				paramsMap.put("TokenID", DesUtil.getDesTokenID(UserCurrentState.getInstance().userID, "Admin203", 1));
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
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				payInfo_title = getIntent().getStringExtra("payInfo_title");
				WXPayEntryActivity.payInfo_title = payInfo_title;
				WXPayEntryActivity.payInfo_subjectID = getIntent().getStringExtra("payInfo_subjectID");
				String[] feeAndName = getIntent().getStringExtra("typeNameAndFee").split("            ￥");
				WXPayEntryActivity.payInfo_typeName = feeAndName[0];
				payInfo_totalFee = feeAndName[1];
				payInfo_price = feeAndName[1];
				payInfo_businessType = getIntent().getStringExtra("payInfo_businessType");
				payInfo_cus_reportType = getIntent().getStringExtra("payInfo_cus_reportType");
				payInfo_payType = getIntent().getStringExtra("payInfo_payType");
				SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
				payInfo_schoolCode = pre.getString("schoolCode", "");
				payInfo_userID = UserInfo.getInstance().studentID;
				payInfo_userName = UserInfo.getInstance().childName;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result != null){
					try {
						//已经获得了out_trade_no，然后就要准备获取prepay_id
						WXPayEntryActivity.payInfo_out_trade_no = result.getCell(0, "out_trade_no");
						//获取到out_trade_no之后，调用下面方法，获取prepay_id并且开始支付
						getPrepayIDThenStartPayAfterGetOutTradeNo();
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}.execute();
	}
}
