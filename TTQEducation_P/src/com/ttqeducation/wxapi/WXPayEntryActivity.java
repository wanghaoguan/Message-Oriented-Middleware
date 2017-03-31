package com.ttqeducation.wxapi;








import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.ttqeducation.R;
import com.ttqeducation.activitys.payment.PayDemoActivity;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DesUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;
	
    public static String payInfo_out_trade_no;
    public static String payInfo_title;
    public static String payInfo_subjectID;
    public static String payInfo_typeName;//定制业务添加数据库使用
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_wxpay_result);
        
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
        
        Button confirmButton = (Button) findViewById(R.id.pay_wxpay_confirm_comfirmButton);
		confirmButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PayActivity.instance.finish();
				finish();
			}
		});
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		//支付完成后，吐司支付结果，关闭refreshView，关闭本页面以及PayActivity页面
		if(resp.errCode == 0){
			updateOrderAfterPay();
			Toast.makeText(getApplicationContext(), "支付成功！", Toast.LENGTH_SHORT).show();
			PayActivity.refreshView.dismiss();
			PayActivity.instance.finish();
			finish();
		}else if(resp.errCode == -1){
			Toast.makeText(getApplicationContext(), "支付失败！支付时发生错误！", Toast.LENGTH_SHORT).show();
			PayActivity.refreshView.dismiss();
			PayActivity.instance.finish();
			finish();
		}else if(resp.errCode == -2){
			Toast.makeText(getApplicationContext(), "支付失败！用户取消支付！", Toast.LENGTH_SHORT).show();
			PayActivity.refreshView.dismiss();
			PayActivity.instance.finish();
			finish();
		}else{
			Toast.makeText(getApplicationContext(), "支付失败！支付时产生未知错误！", Toast.LENGTH_SHORT).show();
			PayActivity.refreshView.dismiss();
			PayActivity.instance.finish();
			finish();
		}
	}
	/**
	 * 调动公司端存储过程pub_order_Update
	 * 用户缴费之后，更新数据库，最后获取最新的deadline
	 */
	public void updateOrderAfterPay(){
		new AsyncTask<Object, Object, String>() {

			@Override
			protected String doInBackground(Object... params) {
				// TODO Auto-generated method stub
				String result = null;
				String methodName = "pub_order_Update";
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("out_trade_no", WXPayEntryActivity.payInfo_out_trade_no);
				paramsMap.put("trade_no", "");
				paramsMap.put("trade_status", "trade_success");
				paramsMap.put("payType", ""+6);
				paramsMap.put("extraDays", ""+0);
				SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
				paramsMap.put("oldDeadline", pre.getString("getUserDeadline", ""));
				
				String tokenID = DesUtil.getDesTokenID(pre.getString("user_account", ""), "Admin203", 1);
				paramsMap.put("TokenID", tokenID);
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				getDataTool.setURL(getResources().getString(R.string.company_service_url));
				try {
					result = getDataTool.getDataAsString(methodName, paramsMap);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				getCurrentUserDeadline();
				super.onPostExecute(result);
			}
			
		}.execute();
	}
	/**
	 * 调用公司端存储过程pub_student_getBaseChargeDeadline
	 * 缴费完成之后，查询用户最新的deadline并保存到SharedPreference
	 */
	public void getCurrentUserDeadline(){
		new AsyncTask<Object, Object, DataTable>(){

			@Override
			protected DataTable doInBackground(Object... params) {
				// TODO Auto-generated method stub
				DataTable dt = null;
				String methodName = "pub_student_getBaseChargeDeadline";
				Map<String, String> paramsMap = new HashMap<String, String>();
				SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
				paramsMap.put("studentID", pre.getString("user_account", ""));
				String tokenID = DesUtil.getDesTokenID(pre.getString("user_account", ""), "Admin203", 1);
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
					try {
						//获取最新的userDeadline
						String currentUserDeadline = result.getCell(0, "endTime").replace('T', ' ').substring(0, 19);
						SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
						SharedPreferences.Editor pre_editor = pre.edit();
						pre_editor.putString("getUserDeadline", currentUserDeadline);
						pre_editor.commit();
						if("普通业务".equals(payInfo_title)){
							addOrdinaryBusinessOrder();
						}
						if("定制业务".equals(payInfo_title)){
							addCustomizedBusinessOrder();
						}
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//根据payInfo_title是普通业务还是定制业务修改学校端数据库
			}
			
		}.execute();
	}
	/**
	 * 调用学校端存储过程pub_student_chargeModule_add
	 * 缴费完成后，如果开通的是普通业务，向学校数据库中添加记录
	 */
	public void addOrdinaryBusinessOrder(){
		new AsyncTask<Object, Object, String>(){

			@Override
			protected String doInBackground(Object... params) {
				// TODO Auto-generated method stub
				String result = "";
				String methodName = "pub_student_chargeModule_add";
				
				SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
				String userID = pre.getString("user_account", "");
				String userDeadline = pre.getString("getUserDeadline", "");
				String schoolURL = pre.getString("school_service_url", "");
				
				DesUtil.addTokenIDToSchoolWS();
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("studentID", userID);
				paramsMap.put("deadline", userDeadline);
				paramsMap.put("TokenID", tokenID);
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				getDataTool.setURL(schoolURL);
				try {
					result = getDataTool.getDataAsString(methodName, paramsMap);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return result;
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
			}
			
		}.execute();
	}
	/**
	 * 调用学校端存储过程pub_student_customizedBussiness_add
	 * 缴费完成后，如果开通的是定制业务，向学校数据库中添加记录
	 */
	public void addCustomizedBusinessOrder(){
		SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
		String userID = pre.getString("user_account", "");
		String schoolURL = pre.getString("school_service_url", "");
		String typeFlag = "";
		if(WXPayEntryActivity.payInfo_typeName.contains("学期")){
			typeFlag = "1";
		}else if(WXPayEntryActivity.payInfo_typeName.contains("学年")){
			typeFlag = "2";
		}else{
			typeFlag = "3";
		}
		new AsyncTask<Object, Object, String>(){

			@Override
			protected String doInBackground(Object... params) {
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
				
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("studentID", params[0].toString());
				paramsMap.put("subjectID", WXPayEntryActivity.payInfo_subjectID);
				paramsMap.put("typeFlag", params[1].toString());
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
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
			}
			
		}.execute(userID, typeFlag, schoolURL);
	}
}