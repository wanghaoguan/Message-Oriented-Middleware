package com.push.server.login;

import com.push.server.decode.DecodeLoginInfo;

public interface Login {
	
	public String login();
	
	public void androidOnlineUserLogin(DecodeLoginInfo loginInfo);
	
	public void iosOnlineUserLogin(DecodeLoginInfo loginInfo);

	public void offlineUserCacheLogin(DecodeLoginInfo loginInfo);
	
	public void offlineUserLogin(DecodeLoginInfo loginInfo);
}
