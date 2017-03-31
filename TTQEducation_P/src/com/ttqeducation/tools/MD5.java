package com.ttqeducation.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	public static String getMD5(String val) throws NoSuchAlgorithmException{  
        MessageDigest md5 = MessageDigest.getInstance("MD5");  
        md5.update(val.getBytes());  
        byte[] m = md5.digest();//加密  
        return getString(m);  
	}  
    private static String getString(byte[] b){  
    	String pwd = "";  
	    for(int i = 0; i < b.length; i ++){  
	     pwd=pwd+Integer.toHexString(b[i] & 0xFF);;  
	    }  
	    return pwd.toString();
    }
}
