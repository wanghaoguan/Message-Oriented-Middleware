package com.ttqeducation.teacher.activitys.teach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;





import com.ttqeducation.teacher.beans.dataTableWrongException;
import com.ttqeducation.teacher.network.GetDataByWS;
import com.ttqeducation.teacher.beans.DataTable;
import com.ttqeducation.teacher.R;
import com.ttqeducation.teacher.myViews.RefreshView;
import com.ttqeducation.teacher.myViews.MyListView;
import com.ttqeducation.teacher.beans.TaskCompletion;
import com.ttqeducation.teacher.tools.DesUtil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 根据传进来的useID查询作业详细信息
 * @author Dell
 *
 */
public class TaskResultDetailActivity extends Activity {

	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；
		
	private MyAdapter mAdapter = null; // 得到一个MyAdapter对象,用于与listview绑定；;
	private MyListView myListView = null;
	private RefreshView refreshView = null;
	private List<TaskCompletion> listTaskCompletions = new ArrayList<TaskCompletion>();
	
	// 条件变量，从上一个界面获取
	private String taskName = ""; // 测试名称；如：家庭作业完成情况;
	private int useID = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_result_detail);
		getDataFromIntent();
		this.getClassAnswerDetail(this.useID);
	}
	
	
	private void getDataFromIntent() {
		// TODO Auto-generated method stub
		this.useID=getIntent().getIntExtra("useID", 0);
		this.taskName=getIntent().getStringExtra("taskName");
		Log.i("hxy",Integer.toString( this.useID));
	}
	
	/**
	 * 根据useID查找题目详细答题情况
	 * @param useID
	 */	
	private void getClassAnswerDetail(int useID) {
		// TODO Auto-generated method stub
		this.refreshView = new RefreshView(this, R.style.full_screen_dialog);
		//异步访问网络
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
				DesUtil.addTokenIDToSchoolWS();		// 向服务端添加tokenID;
				String tokenID = "";
				try {
					tokenID = DesUtil.EncryptAsDoNet(DesUtil.tokenID, "Admin203");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				DataTable dt_result = new DataTable();
				// 方法名
				String methodName = "APP_getClassAnswerDetail_byUseID";
				// 存放参数的map
				Map<String, String> paramsMap = new HashMap<String, String>();
				paramsMap.put("useID", params[0].toString());
				paramsMap.put("TokenID", tokenID);
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
					dt_result = getdatatool.getDataAsTable(methodName,
							paramsMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("error", "get_studentDailyTaskDetail()...出错了。。。");
					e.printStackTrace();
				}
				return dt_result;
			}

			@Override
			protected void onPostExecute(DataTable result) {
				if(result !=null){
					TaskCompletion taskCompletion;
					int count=result.getRowCount();
					for(int i=0;i<count;i++){
						try{
							String questionID = result.getCell(i, "questionID");
							String questionCode = result.getCell(i, "questionCode");
							String rightPercent = result.getCell(i, "rightPercent");
							
							taskCompletion = new TaskCompletion(questionID ,questionCode, rightPercent);
							listTaskCompletions.add(taskCompletion);
						}catch (dataTableWrongException e) {
							e.printStackTrace();
						}
						
					}
				}
				
				refreshView.dismiss();
				// 获取完数据后，初始化界面
				TaskResultDetailActivity.this.initView();
			}
					
			
		}.execute(useID);
	}


	public void initView() {
		
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		// 修改标题栏标题，把 情况 改为 详情；
		this.titleTextView.setText(""
				+ this.taskName.substring(0, (this.taskName.length() - 2))
				+ "详情");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				TaskResultDetailActivity.this.finish();
			}
		});
		
		this.myListView = (MyListView) super
				.findViewById(R.id.listView_task_details);
		this.myListView.setPullRefreshEnable(false); // 不提供下拉刷新功能；
		this.myListView.setPullLoadEnable(false); // 不提供上拉加载功能；
		
		this.mAdapter = new MyAdapter(this);
		this.myListView.setAdapter(this.mAdapter);
		
		if(this.listTaskCompletions.size() == 0){
			super.findViewById(R.id.noInfo_textView).setVisibility(View.VISIBLE);
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
			// TODO Auto-generated method stub
			return listTaskCompletions.size(); // 返回数组的长度
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

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewChild viewChild;
			if(view==null){
				view=mInflater.inflate(R.layout.task_detail_item_one, null);
				viewChild = new ViewChild();
				
				/* 得到各个控件的对象 */
				viewChild.taskQuestionNameTextview=(TextView)view
						.findViewById(R.id.task_questionName_textview);
				viewChild.taskQuestionRightPercent=(TextView)view
						.findViewById(R.id.task_questionRightPercent_textview);
				view.setTag(viewChild);// 绑定ViewHolder对象
			}else{
				viewChild=(ViewChild) view.getTag(); // 取出ViewHolder对象
			} 
			
			/* 设置TextView显示的内容，即我们存放在动态数组中的数据 */
			viewChild.taskQuestionNameTextview.setText(listTaskCompletions.get(position)
					.getQuestionCode());
			viewChild.taskQuestionRightPercent.setText((int)(Float.parseFloat(listTaskCompletions.get(position)
					.getRightPercent())*100)+"%" );
			return view;
		}
		
	}
	
	// 存放列表子项的控件，用于在MyAdapter中设置;
		public final class ViewChild {
			
			public TextView taskQuestionNameTextview; // 作业名；
			public TextView taskQuestionRightPercent; // 作业页码；
		}

}
