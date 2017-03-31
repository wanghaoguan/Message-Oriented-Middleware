package com.push.server.tool;

import java.util.HashMap;
import java.util.Map;

public class ConfigMap {

	private static Map<String, String> serverMap = new HashMap<String, String>();
	
	private ConfigMap(){}
	
	public static ConfigMap getInstance(){
		return new ConfigMap();
	}

	public synchronized void put(String key, String value){
		serverMap.put(key, value);
	}
	
	public static String getValue(String key){
		return serverMap.get(key);
	}
	
}
