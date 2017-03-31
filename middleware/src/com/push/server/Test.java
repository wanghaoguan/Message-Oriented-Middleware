package com.push.server;

import java.util.ArrayList;
import java.util.List;
import com.push.server.push.PushToIOS;

public class Test {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		PushToIOS p = new PushToIOS();
		List<String> tokens = new ArrayList<String>();
		String message = "1,ÎÒÊÇÄã´ó¸ç£¬¹þ¹þ¹þ~~~~~~";
		
		tokens.add("2708658aa6255de7cfb5d4048bd85c45c7c2f7982578d66b6c53fd76f2ea6c71");
		p.apns("0", tokens, message, 2, "");
	}

}
