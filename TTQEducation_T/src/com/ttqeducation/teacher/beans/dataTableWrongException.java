package com.ttqeducation.teacher.beans;

/**
 * 自定义的异常类，为DataTble准备的异常类
 * 
 * @author 王勤为
 * 
 */
public class dataTableWrongException extends Exception {

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;

	public dataTableWrongException() {
		super();
	}

	public dataTableWrongException(String msg) {
		super(msg);
	}

	public dataTableWrongException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public dataTableWrongException(Throwable cause) {
		super(cause);
	}
}
