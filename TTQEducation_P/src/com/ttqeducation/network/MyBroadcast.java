package com.ttqeducation.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * 广播接收器；用于手机从启，启动服务；
 * @author lvjie
 *
 */
public class MyBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i("lvjie", "MyBroadcast--->onReceive()...成功启动服务");
		Toast.makeText(context, "开机启动", Toast.LENGTH_LONG);

		// 启动服务；
		Intent serviceIntent = new Intent(context,PushService.class);
		context.startService(serviceIntent);

	}

}
