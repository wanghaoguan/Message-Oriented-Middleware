package com.ttqeducation.tools;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.R;
import com.ttqeducation.network.GetDataByWS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

/**
 * 通用工具类，单例模式
 * 
 * @author 王勤为
 * 
 */
public class GeneralTools extends Activity {

	public Boolean if_success = false;
	public static GeneralTools instance = null;

	private GeneralTools() {
	}

	public static GeneralTools getInstance() {
		synchronized (GeneralTools.class) {
			if (GeneralTools.instance == null) {
				instance = new GeneralTools();
			}
		}
		return GeneralTools.instance;
	}

	/**
	 * 计算两个日期之间相隔的天数
	 * 
	 * @param old_date
	 * @param new_date
	 * @return
	 * @throws ParseException
	 */
	@SuppressLint("SimpleDateFormat")
	public int daysBetween(Date old_date, Date new_date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		old_date = sdf.parse(sdf.format(old_date));
		new_date = sdf.parse(sdf.format(new_date));
		Calendar cal = Calendar.getInstance();
		cal.setTime(old_date);
		long time1 = cal.getTimeInMillis();
		cal.setTime(new_date);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 检查是否有网络连接
	 * 
	 * @return
	 */
	public boolean isOpenNetwork() {
		Log.i("lvjie", "1  isOpenNetwork()...");
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		Log.i("lvjie", "2  isOpenNetwork()...");
		if (connManager.getActiveNetworkInfo() != null) {
			Log.i("lvjie", "3  isOpenNetwork()...");
			return connManager.getActiveNetworkInfo().isAvailable();
		}
		Log.i("lvjie", "4  isOpenNetwork()...");
		return false;
	}
	
	/**
	 * 网络打开，返回true,否则为false;
	 * @param context
	 * @return
	 */
	public boolean isOpenNetWork1(Context context){
		ConnectivityManager connectivity = (ConnectivityManager) context  
                .getSystemService(Context.CONNECTIVITY_SERVICE);  
 
        if (null != connectivity)  
        {  
            NetworkInfo info = connectivity.getActiveNetworkInfo();  
            if (null != info && info.isConnected())  
            {  
                if (info.getState() == NetworkInfo.State.CONNECTED)  
                {  
                    return true;  
                }  
            }  
        }  
        return false; 
	}

	/**
	 * 转化题目状态1 表示正确 2 表示错误 3 表示未答；
	 * 
	 * @param isAnswer
	 * @param isRight
	 * @return
	 */
	public String getTaskState(String isAnswer, String isRight) {
		String taskState = null;
		if (isAnswer.equals("1")) {// 如果答了题
			if (isRight.equals("1")) {
				// 正确
				taskState = "1";
			} else {// 错误
				taskState = "2";
			}

		} else {// 如果没答题
			taskState = "3";
		}
		// System.out.println("答题状态："+taskState);
		return taskState;
	}

	/**
	 * 通过科目名获得其ID
	 * 
	 * @param subjectName
	 * @return
	 */
	public String getSubjectIDByName(String subjectName) {
		String SubjectID = null;

		if (subjectName.equals("语文")) {
			SubjectID = "1";

		} else if (subjectName.equals("数学")) {
			SubjectID = "2";
		} else if (subjectName.equals("英语")) {
			SubjectID = "3";
		} else if (subjectName.equals("全科")) {
			SubjectID = "0";
		}
		return SubjectID;
	}

	/**
	 * 通过汉字名得到作业类型ID
	 * 
	 * @param WorkTypeName
	 *            1 家庭作业 2 课堂练习模式二 3 课堂练习模式一 4 单元测试 5 期中模拟测试 6 期末模拟考试
	 * @return
	 */
	public String getWorkTypeIDByName(String WorkTypeName) {
		String WorkTypeID = null;

		if (WorkTypeName.equals("家庭作业完成情况")) {
			WorkTypeID = "1";

		} else if (WorkTypeName.equals("课堂作业完成情况")) {
			WorkTypeID = "2";
		}
		// } else if (WorkTypeName.equals("家庭作业完成情况")) {
		// WorkTypeID = "3";
		// }
		else if (WorkTypeName.equals("单元测试完成情况")) {
			WorkTypeID = "4";
		} else if (WorkTypeName.equals("期中测试完成情况")) {
			WorkTypeID = "5";
		} else if (WorkTypeName.equals("期末测试完成情况")) {
			WorkTypeID = "6";
		}
		return WorkTypeID;
	}

	/**
	 * 更新本地对应学校Web服务的方法
	 * 
	 * @param schoolCode
	 * @return
	 */
	public Boolean UpdateSchoolWSURL(String schoolCode) {
		Boolean if_success_flag = false;
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, String>() {
			@Override
			protected String doInBackground(Object... params) {
				String school_url = null;
				// 方法名
				String methodName = "pub_getSchoolWSURL";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("schoolCode", params[0].toString());
				// 获取数据
				GetDataByWS getDataTool = GetDataByWS.getInstance();
				// 获取堂堂清公司的服务地址
				Resources res = getResources();
				String company_service_url = (String) res
						.getText(R.string.company_service_url);
				getDataTool.setURL(company_service_url);
				try {
					school_url = getDataTool.getDataAsString(methodName,
							paramsMap);
				} catch (IOException e) {
					System.out.println("访问WS失败，可能是地址或参数错误,或网络没有连接"
							+ e.getMessage());
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return school_url;
			}

			protected void onPostExecute(String result) {
				if (result != null) {
					// 更新保存学校的WebService地址
					SharedPreferences pre = getSharedPreferences("TTQAndroid",
							MODE_PRIVATE);
					SharedPreferences.Editor pre_edit = pre.edit();
					pre_edit.putString("school_service_url", result);
					pre_edit.commit();
					GeneralTools.this.if_success = true;

				} else {
					GeneralTools.this.if_success = false;
				}
			}
		}.execute(schoolCode);
		;
		// 执行
		if_success_flag = GeneralTools.this.if_success;
		return if_success_flag;
	}

	/**
	 * 获取保存在本地的学校URL，如果本地不存在则返回空值
	 * 
	 * @return
	 */
	public String getSchoolWSURL() {
		Looper.prepare();
		// 从本地获取学校URL
		SharedPreferences pre = getSharedPreferences("TTQAndroid", MODE_PRIVATE);
		String schoolURL = pre.getString("school_service_url", null);

		if (schoolURL == null) {
			// 如果从本地没有获取到数据，则说明出错了，应该重新进入选择代码界面走一遍流程（暂时没有做）

		}
		Looper.loop();
		return schoolURL;

	}
}
