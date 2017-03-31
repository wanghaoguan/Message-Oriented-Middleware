package com.push.server.decode;

public class MessageInfo {

	private MessageHeader header;
	private String body;
	
	public MessageHeader getHeader() {
		return header;
	}
	public void setHeader(MessageHeader header) {
		this.header = header;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	@Override
	public String toString() {
		return "Message [header=" + header + ", body=" + body + "]";
	}
	
}
