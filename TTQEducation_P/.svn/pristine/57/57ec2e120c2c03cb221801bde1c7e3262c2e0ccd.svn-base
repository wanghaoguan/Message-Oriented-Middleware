package com.ttqeducation.activitys.study;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.ttqeducation.R;
import com.ttqeducation.R.layout;
import com.ttqeducation.beans.DataTable;
import com.ttqeducation.beans.TaskCompletion;
import com.ttqeducation.beans.TestInfo;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.beans.dataTableWrongException;
import com.ttqeducation.myViews.RefreshView;
import com.ttqeducation.network.GetDataByWS;
import com.ttqeducation.tools.DesUtil;
import com.ttqeducation.tools.GeneralTools;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 这个是专门用来选择科目和时间条件的中间活动， 需要做的事情： （1）接收TaskType传过来的作业类型参数，动态改变标题
 * （2）呈现选择界面，时间类型+时间 （3）选好了后点击查询按钮，进入TaskResultsActivity，传作业类型参数
 * 
 * @author 王勤为
 * 
 *         这个界面是作业完成情况按时间等条件来查询 对应的界面；实体事件在对应的fragment中；
 */
public class TaskConditionChooseActivity extends Activity {

	private String titleString = ""; // 接收上一个界面传递过来的参数，用于标题的显示;

	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；

	/************* 界面顶部部分； **************/
	// 布局
	private LinearLayout chineseLayout = null;
	private LinearLayout mathLayout = null;
	private LinearLayout englishLayout = null;
	// 图片
	private LinearLayout chineseImageView = null;
	private LinearLayout mathImageView = null;
	private LinearLayout englishImageView = null;
	// 文字
	private TextView chineseTextView = null;
	private TextView mathTextView = null;
	private TextView englishTextView = null;


	// 储存各种变量：当前所属周，各科目单元测试，期中、期末测试
	private int currentWeek;
	//用Map存放useID,testName
	private List<TestInfo> unitTest_chinese = new ArrayList<TestInfo>();
	private List<TestInfo> unitTest_math = new ArrayList<TestInfo>();
	private List<TestInfo> unitTest_english = new ArrayList<TestInfo>();
	private List<TestInfo> midTermTest_chinese = new ArrayList<TestInfo>();
	private List<TestInfo> midTermTest_math = new ArrayList<TestInfo>();
	private List<TestInfo> midTermTest_english = new ArrayList<TestInfo>();
	private List<TestInfo> finalTermTest_chinese = new ArrayList<TestInfo>();
	private List<TestInfo> finalTermTest_math = new ArrayList<TestInfo>();
	private List<TestInfo> finalTermTest_english = new ArrayList<TestInfo>();
	

	private RefreshView refreshView = null;

	// 各个子模块；
	private FragmentManager fragmentManager = null; // 用于对下面的fragment进行管理；
	private TaskConditionChooseContentFragment chineseFragment = null;
	private TaskConditionChooseContentFragment englishFragment = null;
	private TaskConditionChooseContentFragment mathFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_condition_choose1);

		this.fragmentManager = getFragmentManager();
		getDataFromIntent();

		// initView();
		// showSelectionFragment(R.id.task_chinese_layout); // 初始化显示语文；
		this.getDataFromWeb();
	}

	// 获取从上一个界面传递过来的数据；
	public void getDataFromIntent() {
		this.titleString = getIntent().getStringExtra("title");
	}
	
	// 在初始化的时候获取所有需要的信息，用来传递给下一个fragment
	public void getDataFromWeb() {
		String classID = UserInfo.getInstance().classID;
		String termID = UserInfo.getInstance().termID;
		// 获取周
		this.currentWeek = UserInfo.getInstance().currentWeek;

		// 获取测试情况
		if (this.titleString.equals("单元测试完成情况")) {
			// this.get_unitTestList("1", classID, termID);
			// this.get_unitTestList("2", classID, termID);
			// this.get_unitTestList("3", classID, termID);
			// 获取所有科目的单元测试信息
			this.get_unitTestList("0", classID, termID);
		} else if (this.titleString.equals("期中测试完成情况")) {
			// this.get_MidTermTestList("1", classID, termID);
			// this.get_MidTermTestList("2", classID, termID);
			// this.get_MidTermTestList("3", classID, termID);
			this.get_MidTermTestList("0", classID, termID);
		} else if (this.titleString.equals("期末测试完成情况")) {
			// this.get_FinalTermTestList("1", classID, termID);
			// this.get_FinalTermTestList("2", classID, termID);
			// this.get_FinalTermTestList("3", classID, termID);
			this.get_FinalTermTestList("0", classID, termID);
		} else {
			// 直接初始化界面
			initView();
			showSelectionFragment(R.id.task_chinese_layout); // 初始化显示语文；
		}
	}

	// 初始化界面；
	public void initView() {

		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		// 设置标题栏中字体的显示；
		titleTextView.setText(this.titleString);
		TaskConditionChooseContentFragment.titleString = this.titleString;
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				TaskConditionChooseActivity.this.finish();
			}
		});

		// 界面顶部初始化；
		this.chineseLayout = (LinearLayout) super
				.findViewById(R.id.task_chinese_layout);
		this.mathLayout = (LinearLayout) super
				.findViewById(R.id.task_math_layout);
		this.englishLayout = (LinearLayout) super
				.findViewById(R.id.task_english_layout);

		this.chineseImageView = (LinearLayout) super
				.findViewById(R.id.chinese_image);
		this.mathImageView = (LinearLayout) super.findViewById(R.id.math_image);
		this.englishImageView = (LinearLayout) super
				.findViewById(R.id.english_image);

		this.chineseTextView = (TextView) super.findViewById(R.id.chinese_text);
		this.mathTextView = (TextView) super.findViewById(R.id.math_text);
		this.englishTextView = (TextView) super.findViewById(R.id.english_text);

		// 增加点击事件；
		this.chineseLayout.setOnClickListener(myClickListener);
		this.mathLayout.setOnClickListener(myClickListener);
		this.englishLayout.setOnClickListener(myClickListener);
	}

	private OnClickListener myClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			clearSelections();
			clickTopLayout(view.getId());
		}
	};

	// 恢复所有为灰色；
	public void clearSelections() {

		// 图片的恢复；
		this.chineseImageView.setBackgroundResource(R.drawable.linearlayout_frame_circle_gray);
		this.mathImageView.setBackgroundResource(R.drawable.linearlayout_frame_circle_gray);
		this.englishImageView.setBackgroundResource(R.drawable.linearlayout_frame_circle_gray);

		// 文字的恢复；
		this.chineseTextView.setTextColor(getResources().getColor(
				R.color.textGray));
		this.mathTextView.setTextColor(getResources()
				.getColor(R.color.textGray));
		this.englishTextView.setTextColor(getResources().getColor(
				R.color.textGray));

	}

	// 点击某个按钮；
	public void clickTopLayout(int id) {

		switch (id) {
		case R.id.task_chinese_layout:
			this.chineseTextView.setTextColor(getResources().getColor(
					R.color.textWhite));
			this.chineseImageView.setBackgroundResource(R.drawable.btn_circle_red_round);
			break;

		case R.id.task_math_layout:
			this.mathTextView.setTextColor(getResources().getColor(
					R.color.textWhite));
			this.mathImageView.setBackgroundResource(R.drawable.btn_circle_red_round);
			break;

		case R.id.task_english_layout:
			this.englishTextView.setTextColor(getResources().getColor(
					R.color.textWhite));
			this.englishImageView.setBackgroundResource(R.drawable.btn_circle_red_round);
			break;

		default:
			break;
		}
		showSelectionFragment(id);
	}

	// 显示 选中的fragment;
	public void showSelectionFragment(int id) {

		// 开启一个Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);
		switch (id) {
		case R.id.task_chinese_layout:
			if (chineseFragment == null) {

				if (this.titleString.equals("单元测试完成情况")) {
					chineseFragment = new TaskConditionChooseContentFragment(
							unitTest_chinese);
				} else if (this.titleString.equals("期中测试完成情况")) {
					chineseFragment = new TaskConditionChooseContentFragment(
							midTermTest_chinese);
				} else if (this.titleString.equals("期末测试完成情况")) {
					chineseFragment = new TaskConditionChooseContentFragment(
							finalTermTest_chinese);
				} else { // 家庭作业完成情况；
					chineseFragment = new TaskConditionChooseContentFragment();
				}

				transaction.add(R.id.task_condition_content, chineseFragment);

			} else {
				// 如果ContactsFragment不为空，则直接将它显示出来
				transaction.show(chineseFragment);
			}
			TaskConditionChooseContentFragment.subjectString = "语文";
			break;

		case R.id.task_math_layout:
			if (mathFragment == null) {
				// 如果ContactsFragment为空，则创建一个并添加到界面上

				if (this.titleString.equals("单元测试完成情况")) {
					mathFragment = new TaskConditionChooseContentFragment(
							unitTest_math);
				} else if (this.titleString.equals("期中测试完成情况")) {
					mathFragment = new TaskConditionChooseContentFragment(
							midTermTest_math);
				} else if (this.titleString.equals("期末测试完成情况")) {
					mathFragment = new TaskConditionChooseContentFragment(
							finalTermTest_math);
				} else { // 家庭作业完成情况；
					mathFragment = new TaskConditionChooseContentFragment();
				}

				transaction.add(R.id.task_condition_content, mathFragment);

			} else {
				// 如果ContactsFragment不为空，则直接将它显示出来

				transaction.show(mathFragment);
			}
			TaskConditionChooseContentFragment.subjectString = "数学";
			break;

		case R.id.task_english_layout:
			if (englishFragment == null) {
				// 如果ContactsFragment为空，则创建一个并添加到界面上

				if (this.titleString.equals("单元测试完成情况")) {
					englishFragment = new TaskConditionChooseContentFragment(
							unitTest_english);
				} else if (this.titleString.equals("期中测试完成情况")) {
					englishFragment = new TaskConditionChooseContentFragment(
							midTermTest_english);
				} else if (this.titleString.equals("期末测试完成情况")) {
					englishFragment = new TaskConditionChooseContentFragment(
							finalTermTest_english);
				} else { // 家庭作业完成情况；
					englishFragment = new TaskConditionChooseContentFragment();
				}
				transaction.add(R.id.task_condition_content, englishFragment);
			} else {
				// 如果ContactsFragment不为空，则直接将它显示出来
				transaction.show(englishFragment);
			}
			TaskConditionChooseContentFragment.subjectString = "英语";
			break;

		default:
			break;
		}

		transaction.commit();
	}

	// 隐藏fragment;
	private void hideFragments(FragmentTransaction transaction) {
		if (chineseFragment != null) {
			transaction.hide(chineseFragment);
		}
		if (mathFragment != null) {
			transaction.hide(mathFragment);
		}
		if (englishFragment != null) {
			transaction.hide(englishFragment);
		}
	}

	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(TaskConditionChooseActivity.this,
				toastMessage, Toast.LENGTH_SHORT);
		toast.show();
	}

	/**
	 * 获取发生过的单元测试
	 * 
	 */
	public void get_unitTestList(String subjectID, String classID, String termID) {
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, DataTable>() {

			// public String subjectID = null;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// 根据时间类型有不同的方法名和参数
				DataTable dt = new DataTable();
				// 方法名
				String methodName = "report_getAll_UnitTestList";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				// this.subjectID = params[0].toString();
				paramsMap.put("subjectID", params[0].toString());
				paramsMap.put("classID", params[1].toString());
				paramsMap.put("termID", params[2].toString());
				paramsMap.put("TokenID", tokenID);
				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 从本地获取学校URL
				SharedPreferences pre = getSharedPreferences("TTQAndroid",
						MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				if (schoolURL == null) {// 如果没有值
					return null;
				}
				getdatatool.setURL(schoolURL);
				try {
					dt = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "get_unitTestList()...出错了。。。");
					e.printStackTrace();
				}
				return dt;
			}

			protected void onPostExecute(DataTable result) {
				if (result != null) {
					System.out.println(result.toString());// 测试代码，之后需要删除
					int count = result.getRowCount();
					try {
						for (int i = 0; i < count; i++) {
							String unitTestName = result.getCell(i, "unitInfo");
							String subjectID = result.getCell(i, "subjectID");
							String useID = result.getCell(i, "useID");
							
							TestInfo testInfo = new TestInfo(useID, unitTestName);
							
							if (subjectID.equals("1")) {
								
								TaskConditionChooseActivity.this.unitTest_chinese.add(testInfo);
							} else if (subjectID.equals("2")) {
								TaskConditionChooseActivity.this.unitTest_math.add(testInfo);
							} else if (subjectID.equals("3")) {
								TaskConditionChooseActivity.this.unitTest_english.add(testInfo);
							}
						}
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// 关闭刷新；
				refreshView.dismiss();
				// 获取完数据后，初始化界面
				TaskConditionChooseActivity.this.initView();
				TaskConditionChooseActivity.this
						.showSelectionFragment(R.id.task_chinese_layout);
			};
		}.execute(subjectID, classID, termID);
	}

	/**
	 * 获取发生过的期中测试
	 * 
	 */
	public void get_MidTermTestList(String subjectID, String classID,
			String termID) {
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, DataTable>() {
			// public String subjectID = null;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// 根据时间类型有不同的方法名和参数
				DataTable dt = new DataTable();
				// 方法名
				String methodName = "report_getAll_MidTermTestList";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				// this.subjectID = params[0].toString();
				paramsMap.put("subjectID", params[0].toString());
				paramsMap.put("classID", params[1].toString());
				paramsMap.put("termID", params[2].toString());
				paramsMap.put("TokenID", tokenID);
				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 从本地获取学校URL
				SharedPreferences pre = getSharedPreferences("TTQAndroid",
						MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				if (schoolURL == null) {// 如果没有值
					return null;
				}
				getdatatool.setURL(schoolURL);
				try {
					dt = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "get_MidTermTestList()...出错了。。。");
					e.printStackTrace();
				}
				return dt;
			}

			protected void onPostExecute(DataTable result) {
				if (result != null) {
					System.out.println(result.toString());// 测试代码，之后需要删除
					int count = result.getRowCount();
					try {
						for (int i = 0; i < count; i++) {
							String testName = result.getCell(i, "testName");
							String subjectID = result.getCell(i, "subjectID");
							String useID = result.getCell(i, "useID");
							
							TestInfo testInfo = new TestInfo(useID, testName);
							
							if (subjectID.equals("1")) {
								TaskConditionChooseActivity.this.midTermTest_chinese.add(testInfo);
							} else if (subjectID.equals("2")) {
								TaskConditionChooseActivity.this.midTermTest_math.add(testInfo);
							} else if (subjectID.equals("3")) {
								TaskConditionChooseActivity.this.midTermTest_english.add(testInfo);
							}

						}
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// 关闭刷新；
				refreshView.dismiss();
				// 获取完数据后，初始化界面

				TaskConditionChooseActivity.this.initView();
				TaskConditionChooseActivity.this
						.showSelectionFragment(R.id.task_chinese_layout);
			};
		}.execute(subjectID, classID, termID);
	}

	/**
	 * 获取发生过的期中测试
	 * 
	 */
	public void get_FinalTermTestList(String subjectID, String classID,
			String termID) {
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		// 用异步任务来访问访问网络
		new AsyncTask<Object, Object, DataTable>() {
			// public String subjectID = null;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				refreshView.show();
			}

			@Override
			protected DataTable doInBackground(Object... params) {
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// 根据时间类型有不同的方法名和参数
				DataTable dt = new DataTable();
				// 方法名
				String methodName = "report_getAll_FinalTermTestList";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				// this.subjectID = params[0].toString();
				paramsMap.put("subjectID", params[0].toString());
				paramsMap.put("classID", params[1].toString());
				paramsMap.put("termID", params[2].toString());
				paramsMap.put("TokenID", tokenID);
				
				// 开始访问数据
				GetDataByWS getdatatool = GetDataByWS.getInstance();
				// 从本地获取学校URL
				SharedPreferences pre = getSharedPreferences("TTQAndroid",
						MODE_PRIVATE);
				String schoolURL = pre.getString("school_service_url", null);
				if (schoolURL == null) {// 如果没有值
					return null;
				}
				getdatatool.setURL(schoolURL);
				try {
					dt = getdatatool.getDataAsTable(methodName, paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "get_FinalTermTestList()...出错了。。。");
					e.printStackTrace();
				} 
				return dt;
			}

			protected void onPostExecute(DataTable result) {
				if (result != null) {
					System.out.println(result.toString());// 测试代码，之后需要删除
					int count = result.getRowCount();
					try {
						for (int i = 0; i < count; i++) {
							String testName = result.getCell(i, "testName");
							String subjectID = result.getCell(i, "subjectID");
							String useID = result.getCell(i, "useID");
							
							TestInfo testInfo = new TestInfo(useID, testName);
							
							if (subjectID.equals("1")) {
								TaskConditionChooseActivity.this.finalTermTest_chinese.add(testInfo);
							} else if (subjectID.equals("2")) {
								TaskConditionChooseActivity.this.finalTermTest_math.add(testInfo);
							} else if (subjectID.equals("3")) {
								TaskConditionChooseActivity.this.finalTermTest_english.add(testInfo);
							}

						}
					} catch (dataTableWrongException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// 关闭刷新；
				refreshView.dismiss();
				// 获取完数据后，初始化界面
				TaskConditionChooseActivity.this.initView();
				TaskConditionChooseActivity.this
						.showSelectionFragment(R.id.task_chinese_layout);
			};
		}.execute(subjectID, classID, termID);
	}
}
