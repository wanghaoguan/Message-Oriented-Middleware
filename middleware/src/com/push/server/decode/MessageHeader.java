package com.push.server.decode;

public class MessageHeader {

	private int length;
	private int type;
	
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "MessageHender [length=" + length + ", type=" + type + "]";
	}
	
}
