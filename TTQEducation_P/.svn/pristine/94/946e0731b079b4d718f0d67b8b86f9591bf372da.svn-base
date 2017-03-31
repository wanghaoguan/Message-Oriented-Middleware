package com.ttqeducation.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.beans.DataRow;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.tools.GeneralTools;

import android.R.integer;
import android.util.Log;

/**
 * 本类用于访问WebService使用单例模式
 * 
 * @author 王勤为
 * 
 */
public class GetDataByWS {

	private static GetDataByWS instance = null;

	// 服务器的WebService的命名空间
	public String SERVICE_NS = "http://tangtangqing.org/";
	// 服务器的地址 "http://192.168.137.1:8888/ManageService.asmx";
	public String SERVICE_URL = null;
	// 传输对象
	public HttpTransportSE ht;
	// 创建“信封对象”，使用SOAP1.1协议
	public SoapSerializationEnvelope envelope;
	// = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	// 创建“信”对象
	public SoapObject soapObject;

	/**
	 * 隐藏构造器方法
	 * 
	 */
	private GetDataByWS() {

	}

	/**
	 * 获取单例实例的方法
	 *  synchronized 关键字，代表这个方法加锁,相当于不管哪一个线程A每次运行到这个方法时,
	 *  都要检查有没有其它正在用这个方法的线程B（或者C D等）,有的话要等正在使用这个方法的
	 *  线程B（或者C D）运行完这个方法后再运行此线程A,没有的话,直接运行 它包括两种用法
	 * @return
	 */
	public static GetDataByWS getInstance() {
		synchronized (GetDataByWS.class) {
			if (GetDataByWS.instance == null) {
				GetDataByWS.instance = new GetDataByWS();
			}
		}
		return GetDataByWS.instance;
	}

	// 定义设置命名空间和服务网址的方法
	public void setNamespaceAndURL(String namespace, String URL) {

		this.SERVICE_NS = namespace;
		this.SERVICE_URL = URL;
	}

	public void setURL(String URL) {

		this.SERVICE_URL = URL;
	}

	public void setNamespace(String namespace) {

		this.SERVICE_NS = namespace;
	}

	/**
	 * 调用WebService 获取String结果
	 * 
	 * @param methodName
	 * @param params
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public String callWebService_getString(String methodName,
			Map<String, String> params) throws IOException,
			XmlPullParserException {
		soapObject = new SoapObject(SERVICE_NS, methodName);
		// 2、设置调用方法的参数值，如果没有参数，可以省略，
		if (params != null) {
			Iterator iter = params.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				soapObject.addProperty((String) entry.getKey(),
						(String) entry.getValue());
			}
		}
		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = soapObject;
		envelope.dotNet = true;
		String dataResult = null;
		// try {
		System.out.println("开始执行连接！" + SERVICE_URL);
		ht = new HttpTransportSE(SERVICE_URL);
		ht.call(SERVICE_NS + methodName, envelope);
		if (envelope.getResponse() != null) {
			// 解析得到的数据
			SoapObject result = (SoapObject) envelope.bodyIn;

			if (result.getProperty(0).getClass().getName()
					.equals("org.ksoap2.serialization.SoapPrimitive") == true) {
				SoapPrimitive childObject = (SoapPrimitive) result
						.getProperty(0);
				dataResult = childObject.toString();
			}else {
				System.out.println("1-->result="+result.toString());
				String[] tempStrings = result.toString().split("=");
				dataResult = tempStrings[tempStrings.length-1];
			}
		} else {
			System.out.println("2-->服务器没有返回数据！");
			return null;
		}

		return dataResult;

	}

	/**
	 * 调用WebService 获取Datatable结果
	 * 
	 * @param methodName
	 * @param params
	 * @return
	 * @throws dataTableWrongException
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public DataTable callWebService_getTable(String methodName,Map<String, String> params) 
			throws dataTableWrongException,IOException, XmlPullParserException {
		
		// 指定WebService的命名空间和调用的方法名
		soapObject = new SoapObject(SERVICE_NS, methodName);

		// 设置调用方法的参数值，如果没有参数，可以省略，
		if (params != null) {
			Iterator iter = params.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				//  设置调用方法的参数值，这一步是可选的，如果方法没有参数，可以省略这一步
				//  要注意的是，addProperty方法的第1个参数虽然表示调用方法的参数名，
				// 但该参数值并不一定与服务端的WebService类中的方法参数名一致，只要设置参数的顺序一致即可
				soapObject.addProperty((String) entry.getKey(),
						(String) entry.getValue());
			}
		}
		// 生成调用WebService方法的SOAP请求信息,设置SOAP协 议的版本号
		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = soapObject;
		envelope.dotNet = true;
		DataTable resultTable = null;
		
		// try {
		System.out.println("开始执行连接！"+SERVICE_URL);
		ht = new HttpTransportSE(SERVICE_URL);
		
		// 使用call方法调用WebService方法
		ht.call(SERVICE_NS + methodName, envelope, null);
		// 使用getResponse方法获得WebService方法的返回结果
		if (envelope.getResponse() != null) {
			// 解析得到的数据,封装成DataTable
			SoapObject result = (SoapObject) envelope.bodyIn;
			Log.i("调试信息", "返回详细内容："+result.toString());
			resultTable = this.parseToDataTable(result);

		} else {
			Log.i("调试信息", "服务器没有返回数据！");
			return null;
		}
		return resultTable;
	}

	/**
	 * 将SoapObject数据解析成DataTable
	 * 
	 * @param soapobject
	 * @return
	 * @throws dataTableWrongException
	 */
	public DataTable parseToDataTable(SoapObject soapobject)
			throws dataTableWrongException {
				
		SoapObject childObject1 = (SoapObject) soapobject.getProperty(0);
		SoapObject childObject2 = (SoapObject) childObject1.getProperty("diffgram");
		// 获得表节点
		SoapObject tableObject = null;
		if (childObject2.getPropertyCount() != 0) {
			tableObject = (SoapObject) childObject2.getProperty("NewDataSet");
		} else {
			return null;
		}
		
		// 初始化结果表
		DataTable resultTable = new DataTable();

		System.out.println("解析开始！");

		// 如果结果不为空
		int count = tableObject.getPropertyCount();// 获得结果数，即有多少行

		System.out.println("结果数：" + count);
		if (count != 0) {
			// 遍历
			for (int i = 0; i < count; i++) {
				Map<String, Object> iMap = new HashMap<String, Object>();
				// 获得行节点
				SoapObject row = (SoapObject) tableObject.getProperty(i);
				int num = row.getPropertyCount();
				// 遍历列
				for (int j = 0; j < num; j++) {
					PropertyInfo n = new PropertyInfo();
					row.getPropertyInfo(j, n);
					// System.out.println("列名："+n.name+"值："+row.getProperty(j));
					iMap.put(n.name, row.getProperty(j));
				}
				// 新建一行并加入结果表
				DataRow dr = new DataRow(iMap);
				// System.out.println(dr.getCell(0)+dr.getCell(1)+dr.getCell(2));
				resultTable.addRow(dr);
			}
		}
		return resultTable;
	}

	/**
	 * 真正给外部调用的获取数据方法，返回String类型
	 * 
	 * @param params
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public String getDataAsString(Object... params) throws IOException,
			XmlPullParserException {
		// System.out.println(this.SERVICE_URL);
		String value = null;
		if (params != null && params.length == 2) {// 如果有2个参数，即方法名+参数map
			value = this.callWebService_getString((String) params[0],
					(Map<String, String>) params[1]);
		} else if (params != null && params.length == 1) {// 如果只有1个参数，即方法名
			value = this.callWebService_getString((String) params[0], null);
		}
		return value;

	}

	/**
	 * 真正给外部调用的获取数据方法,返回DataTable类型
	 * 
	 * @param params
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws dataTableWrongException
	 */
	@SuppressWarnings("unchecked")
	public DataTable getDataAsTable(Object... params)
			throws dataTableWrongException, IOException, XmlPullParserException {
		DataTable value = null;
		if (params != null && params.length == 2) {// 如果有2个参数，即方法名+参数map
			value = this.callWebService_getTable((String) params[0],
					(Map<String, String>) params[1]);
		} else if (params != null && params.length == 1) {// 如果只有1个参数，即方法名
			value = this.callWebService_getTable((String) params[0], null);
		}
		// System.out.println("表格数据"+value.toString());
		return value;

	}

	/*
	 * 包含异步任务代码的方法，暂时认为不可取，注释
	 * 
	 * @SuppressWarnings("unchecked") public String getDataAsString(Object...
	 * params) { // 用异步任务来访问访问网络 new AsyncTask<Object, Object, String>() {
	 * 
	 * @Override protected String doInBackground(Object... params) { String
	 * value = null; if (params != null && params.length == 2) {//
	 * 如果有2个参数，即方法名+参数map value = this .callWebService_getString((String)
	 * params[0], (Map<String, String>) params[1]); } else if (params != null &&
	 * params.length == 1) {// 如果只有1个参数，即方法名 value =
	 * this.callWebService_getString( (String) params[0], null); } return value;
	 * }
	 * 
	 * protected void onPostExecute(String result) { if (result != null) {
	 * this.resultStr = result; } };
	 * 
	 * }.execute(params);
	 * 
	 * String resultString = this.resultStr; return resultString; }
	 */
}