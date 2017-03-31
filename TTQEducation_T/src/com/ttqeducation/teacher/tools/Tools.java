package com.ttqeducation.teacher.tools;

public class Tools {
	
	/**
	 * 把整形转化为字节数组类型；数组的长度永远为4；b.length = 4;
	 * @param a
	 * @return b
	 */
	public static byte[] intToByteArray(int a) {
		byte[] b = new byte[] { 
		        (byte) ((a >> 24) & 0xFF), 
		        (byte) ((a >> 16) & 0xFF),    
		        (byte) ((a >> 8) & 0xFF),    
		        (byte) (a & 0xFF) 
		    }; 
		return b;

	}
	
	/**
	 * 把字节数组转化为整形；注意：只读取前四个字节；
	 * @param a
	 * @return
	 */
	public static int getInt(byte[] a)
    {
		int b =  a[3] & 0xFF | 
	            (a[2] & 0xFF) << 8 | 
	            (a[1] & 0xFF) << 16 | 
	            (a[0] & 0xFF) << 24;
		return b;

    }

	/**
	 * 把两个字节数组合并成一个字节数组，合并后的长度为两个字节数组之和；
	 * @param b1
	 * @param b2
	 * @return
	 */
	public static byte[] megerBytes(byte[] b1,byte[] b2){
		byte[] b = new byte[b1.length+b2.length];
		System.arraycopy(b1, 0, b, 0, b1.length);
		System.arraycopy(b2, 0, b, b1.length, b2.length);
		return b;
	}
	
	
}
