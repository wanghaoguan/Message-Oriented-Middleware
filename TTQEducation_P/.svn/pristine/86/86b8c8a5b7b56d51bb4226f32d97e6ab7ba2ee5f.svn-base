package com.ttqeducation.activitys.study;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ttqeducation.R;
import com.ttqeducation.activitys.system.ChooseSchoolActivity;
import com.ttqeducation.beans.TestInfo;
import com.ttqeducation.beans.UserInfo;
import com.ttqeducation.myViews.PickerView;
import com.ttqeducation.tools.GeneralTools;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TaskConditionChooseContentFragment extends Fragment {

	// 下面两个变量的赋值是在 TaskConditionChooseActivity中；
	public static String subjectString = ""; // 用来存放当前的科目；语文 数学 外语；
	public static String titleString = ""; // 用来存放标题栏的标题，方便接下来的界面参数传递；

	// 界面控件；
	private ImageView selectImageView = null; // 中间的查询按钮；
	
//	private ImageView dateImageView = null; // 日期按钮；
//	private ImageView weekImageView = null; // 周按钮；
	
	private TextView dateTextView = null; // 日期 文字；
	private TextView weekTextView = null; // 周 文字；
	
	private DatePicker datePicker = null; // 日期 控件；
//	private DatePicker weekPicker = null; // 周 控件；
	private PickerView weekPicker = null; // 周 控件；
	List<TestInfo> listWeekNum = new ArrayList<TestInfo>();		// 用来存放周数；
	
	private LinearLayout weekLayout = null; // 用来显示或隐藏；

	// 进入单元测试需要；
	private PickerView unitTestPickerView = null; // 当进入单元测试时， 显示；
	List<TestInfo> listUnitTestInfo = null; // 存放单元测试或期中，期末测试名称；
	private boolean isOfferClick = true;	// 这是针对单元，期中，期末测试来的，没有测试名，则不提供点击事件；

	// 代码逻辑需要的变量；
	private boolean isClickDate = true; // 默认的是日期选择类型；
	private boolean isClickWeek = false;
	
	public TaskConditionChooseContentFragment(){}
	public TaskConditionChooseContentFragment(List<TestInfo>  listTestNames){
		this.listUnitTestInfo = listTestNames;	
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View layout = inflater.inflate(R.layout.fragment_task_condition_choose_content, container,false);
		initView(layout);
		return layout;
	}

	public void initView(View view) {
		// 中间查询的图片；
		this.selectImageView = (ImageView) view
				.findViewById(R.id.select_imageView);

		// 日期相关条件；
		this.dateTextView = (TextView) view.findViewById(R.id.date_textView);
		this.datePicker = (DatePicker) view.findViewById(R.id.datePicker);

		// 周
		this.weekTextView = (TextView) view.findViewById(R.id.week_textView);
		this.weekPicker = (PickerView) view.findViewById(R.id.weekPicker);
		this.weekPicker.setSelectTestSize(50);
		this.weekPicker.setOthersTestSize(40);
		this.weekLayout = (LinearLayout) view.findViewById(R.id.week_layout);

		// 进入单元测试时需要显示出来；
		this.unitTestPickerView = (PickerView) view
				.findViewById(R.id.pickerView_units);
		this.unitTestPickerView.setSelectTestSize(26); 
		this.unitTestPickerView.setOthersTestSize(20);

		
		setMaxWeekPicker(UserInfo.getInstance().currentWeek);		// 设置周 取值范围；


		// 添加点击事件；
		this.selectImageView.setOnClickListener(new MyClickListener());
		this.dateTextView.setOnClickListener(new MyClickListener());
		this.weekTextView.setOnClickListener(new MyClickListener());

		if (TaskConditionChooseContentFragment.titleString.equals("单元测试完成情况")) {
			// 当进入的是单元测试界面，则需要隐藏周按钮及对应的文字，还需把日期文字改为单元；
			initUnitTask("单元");
		} else if (TaskConditionChooseContentFragment.titleString
				.equals("期中测试完成情况")) {
			// 当进入的是期中测试界面，则需要隐藏周按钮及对应的文字，还需把日期文字改为期中；
			initUnitTask("期中");
		} else if (TaskConditionChooseContentFragment.titleString
				.equals("期末测试完成情况")) {
			// 当进入的是期末测试界面，则需要隐藏周按钮及对应的文字，还需把日期文字改为期末；
			initUnitTask("期末");
		}
	}

	// 专门针对 单元 期中 期末 测试进行一定的特殊处理 初始化；
	public void initUnitTask(String selectStr) {

		this.weekTextView.setVisibility(View.GONE);

		this.dateTextView.setText(selectStr);
		this.dateTextView.setOnClickListener(null);		// 取消点击；

		this.weekLayout.setVisibility(View.GONE);
		this.datePicker.setVisibility(View.GONE);
		this.unitTestPickerView.setVisibility(View.VISIBLE);
		generateUnitTestData(); // 模拟数据； 真正不需要；
		this.unitTestPickerView.setData(listUnitTestInfo); // 控件与数据绑定；
	}

	// 产生 单元测试数据；注意：当为期中，期末测试完成情况时，同样也向 listUnitTestInfo 中加入数据即可；
	public void generateUnitTestData() {
		if(this.listUnitTestInfo == null){
			this.listUnitTestInfo = new ArrayList<TestInfo>();
		}else if(this.listUnitTestInfo.size()>=1){
			this.isOfferClick = true;
			return;
		}

		TestInfo testInfo = new TestInfo(""+1, "无 ");
		listUnitTestInfo.add(testInfo);
		this.isOfferClick = false;
	}

	private class MyClickListener implements OnClickListener {
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			clickBtn(view.getId());
		}
	}

	// 点击
	public void clickBtn(int id) {
		switch (id) {
		case R.id.select_imageView: // 表示的是点击了 中间的查询按钮；王勤为的代码只需在此添加，然后开辟线程；
			goNextActivity();
			break;
		case R.id.date_textView: // 表示的是点击了 日期按钮；

			if (TaskConditionChooseContentFragment.titleString
					.equals("单元测试完成情况")) {
				// 不做任何变化；
			} else {
				// 修改界面显示状态；
				this.dateTextView.setBackgroundResource(R.drawable.btn_circle_red);
				this.dateTextView.setTextColor(getResources().getColor(
						R.color.white));
				this.datePicker.setVisibility(View.VISIBLE);
				this.isClickDate = true;

				this.weekTextView.setBackgroundResource(R.drawable.btn_circle_gray);
				this.weekTextView.setTextColor(getResources().getColor(
						R.color.textGray));
				this.weekLayout.setVisibility(View.GONE);
				this.isClickWeek = false;
			}
			break;

		case R.id.week_textView: // 表示的是点击了 周按钮；

			
			// 修改界面显示状态；
			this.weekTextView.setBackgroundResource(R.drawable.btn_circle_red);
			this.weekTextView.setTextColor(getResources().getColor(
					R.color.white));
			this.weekLayout.setVisibility(View.VISIBLE);
			this.isClickWeek = true;

			this.dateTextView.setBackgroundResource(R.drawable.btn_circle_gray);
			this.dateTextView.setTextColor(getResources().getColor(
					R.color.textGray));
			this.datePicker.setVisibility(View.GONE);
			this.isClickDate = false;

			break;

		default:
			break;
		}
	}

	// 进入下一个界面；
	public void goNextActivity() {

		// 没有联网，不可以进入到下一个界面；
		if(!GeneralTools.getInstance().isOpenNetWork1(getActivity())){
			showToast("未连接到互联网，请检查网络配置!");
			return;
		}
		
		if (TaskConditionChooseContentFragment.titleString.equals("单元测试完成情况")) {

//			if(this.isOfferClick){
//				Intent intent = new Intent(getActivity(), TaskResultsActivity.class);
//				intent.putExtra("timeType", "unit");
//				intent.putExtra("valueStr", this.unitTestPickerView.getTextKey());
//				intent.putExtra("testName", this.unitTestPickerView.getTextValue());
//				startActivity(intent);
//			}
			// 暂时测试， 以后要去掉；
			Intent intent = new Intent(getActivity(), TaskResultsActivity.class);
			intent.putExtra("timeType", "unit");
			intent.putExtra("valueStr", this.unitTestPickerView.getTextKey());
			intent.putExtra("taskName", titleString);
			intent.putExtra("subjectName", subjectString);
			intent.putExtra("testName", this.unitTestPickerView.getTextValue());
			startActivity(intent);
			

		} else if (TaskConditionChooseContentFragment.titleString
				.equals("期中测试完成情况")) {
			
			if(this.isOfferClick){
				Intent intent = new Intent(getActivity(), TaskResultsActivity.class);
				intent.putExtra("timeType", "midterm");
				intent.putExtra("valueStr", this.unitTestPickerView.getTextKey());
				intent.putExtra("taskName", titleString);
				intent.putExtra("subjectName", subjectString);
				intent.putExtra("testName", this.unitTestPickerView.getTextValue());
				startActivity(intent);
			}
			

		} else if (TaskConditionChooseContentFragment.titleString
				.equals("期末测试完成情况")) {

			if(this.isOfferClick){
				Intent intent = new Intent(getActivity(), TaskResultsActivity.class);
				intent.putExtra("timeType", "finalterm");
				intent.putExtra("valueStr", this.unitTestPickerView.getTextKey());
				intent.putExtra("taskName", titleString);
				intent.putExtra("subjectName", subjectString);
				intent.putExtra("testName", this.unitTestPickerView.getTextValue());
				startActivity(intent);
			}
			

		} else { // 家庭作业，课堂作业 如下进入；
			// 修改界面显示状态；
			if (this.isClickDate) {

				int month = this.datePicker.getMonth() + 1;
				String monthString = "";
				if (month < 10) {
					monthString = "0" + month + "";
				} else {
					monthString = month + "";
				}

				int day = this.datePicker.getDayOfMonth();
				String dayString = "";
				if (day < 10) {
					dayString = "0" + day + "";
				} else {
					dayString = day + "";
				}

				Intent intent = new Intent(getActivity(),
						TaskResultsActivity.class);
				intent.putExtra("timeType", "day");
				intent.putExtra("valueStr", this.datePicker.getYear() + "-"
						+ monthString + "-" + dayString);
				startActivity(intent);

			} else if (this.isClickWeek) {
				Intent intent = new Intent(getActivity(),
						TaskResultsActivity.class);
				intent.putExtra("timeType", "week");
//				intent.putExtra("valueStr", this.weekPicker.getYear() + "");
				intent.putExtra("valueStr", this.weekPicker.getTextValue() + "");
				startActivity(intent);
			}

		}
	}

	// 设置周按钮的最大显示周
	private void setMaxWeekPicker(int max){
		if(this.weekPicker == null){
			return;
		}
		if(max<1){		// 默认为50周；
			max=50;
		}
		
		for(int i=1; i<=max; i++){
			TestInfo weekInfo = new TestInfo(i+"");
			listWeekNum.add(weekInfo);
		}
		this.weekPicker.setData(listWeekNum);
		
	}
	
	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(getActivity(), toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}

}
