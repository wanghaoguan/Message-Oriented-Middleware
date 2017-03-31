package com.ttqeducation.activitys.others;

/**
 * 吕杰
 * 用户协议；
 */


import com.ttqeducation.R;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserProtocolActivity extends Activity {

	// 标题栏部分；
	private LinearLayout titleBackLayout = null; // 标题栏返回按钮；
	private TextView titleTextView = null;			// 标题栏文字；

	private WebView webView = null;
	private  final String  URL = "http://121.43.151.57:8020/document/userprotocal.htm";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_protocol);

		initView();
	}

	public void initView() {
		// 标题栏部分 实例化；
		this.titleTextView = (TextView) (super.findViewById(R.id.action_bar).findViewById(R.id.title_text));
		this.titleTextView.setText("用户协议");
		this.titleBackLayout = (LinearLayout) (super
				.findViewById(R.id.action_bar).findViewById(R.id.title_back_layout));
		this.titleBackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				UserProtocolActivity.this.finish();
			}
		});
		
		this.webView = (WebView) findViewById(R.id.webView);
		this.webView.loadUrl(URL);
		
		//启用支持javascript
		WebSettings settings = this.webView.getSettings();
		settings.setJavaScriptEnabled(true);
		
		//覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		webView.setWebViewClient(new WebViewClient(){
           @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	            // TODO Auto-generated method stub
	            //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
	            view.loadUrl(url);
	            return true;
	        }
        });
	}

	
	// 窗口提示信息；
	public void showToast(String toastMessage) {
		Toast toast = Toast.makeText(UserProtocolActivity.this, toastMessage,
				Toast.LENGTH_SHORT);
		toast.show();
	}

}
