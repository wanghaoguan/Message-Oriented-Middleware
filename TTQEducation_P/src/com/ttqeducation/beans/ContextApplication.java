package com.ttqeducation.beans;

import android.app.Application;
import android.content.Context;

/**
 * 这个是为了普通类使用上下文环境而定义的类
 * 
 * @author 王勤为
 * 
 */
public class ContextApplication extends Application {

	private static Context context;

	public void onCreate() {
		super.onCreate();
		ContextApplication.context = getApplicationContext();
	}

	public static Context getAppContext() {
		return ContextApplication.context;
	}
}
