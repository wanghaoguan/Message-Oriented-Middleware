package com.push.server.tool;

public class DataConvert {

	/*
	 * int 转换为 byte[]
	 */
	public static byte[] intToByteArray(int a){
		byte[] b = new byte[] { 
		        (byte) ((a >> 24) & 0xFF), 
		        (byte) ((a >> 16) & 0xFF),    
		        (byte) ((a >> 8) & 0xFF),    
		        (byte) (a & 0xFF) 
		    }; 
		return b;
	}
	
	/*
	 * byte[] 转换为 int
	 */
	public static int byteArraayToInt(byte[] a){
		int b =  a[3] & 0xFF | 
	            (a[2] & 0xFF) << 8 | 
	            (a[1] & 0xFF) << 16 | 
	            (a[0] & 0xFF) << 24;
		return b;
	}
	
	/*
	 * 合并两个byte[]，并返回合并后的byte[]
	 */
	public static byte[] mergeByteArray(byte[] b1,byte[] b2){
		
		int lengthb1 = b1.length;
		int lengthb2 = b2.length;
		byte[] merge = new byte[lengthb1+lengthb2];
		for(int i=0; i<lengthb1; i++){
			merge[i] = b1[i];
		}
		for(int i=0; i<lengthb2; i++){
			merge[lengthb1+i] = b2[i];
		}
		return merge;
	}
	
	/*
	 * 获取指定起始位置和结束位置的byte[]
	 */
	public static byte[] cutOutByte(byte[] b,int start,int end){
		int length = end-start;
		byte[] cutByte = new byte[length];
		for(int i=start,j=0; i<end; i++){
			cutByte[j] = b[i];j++;
		}
		return cutByte;
	}
	
	
}
