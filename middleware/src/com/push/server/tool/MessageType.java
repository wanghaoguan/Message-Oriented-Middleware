package com.push.server.tool;

public enum MessageType {

	ANDROIDLOGIN_REQ((int) 0),//android�û���¼����
	ANDROIDLOGIN_RESP((int) 1),//android�û���¼����
	HEARTBEAT_REQ((int) 2),//����
	PUSH_REQ((int) 3),//��������
	PUSH_RESP((int) 4),//���ͷ���
	SENDMESSAGE((int)5),//������Ϣ
	OTHERDEVLOGIN_RESP((int)8),//��ͬ�û�ʹ�������豸��¼��������Ϣ
	OFFLINENOTICE((int)9),
	
	IOSLOGIN_REQ((int) 6),//ios�û���¼����
	IOSLOGIN_RESP((int) 10),//ios�û���¼����
	IOSLOGOUT_REQ((int) 7),//ios�û�ע������
	
	UNREADMESSAGENUM_RESP((int) 11),
	
	TTQPUSH_REQ((int) 12),; //
	
	private int value;
	
	private MessageType(int value){
		this.value = value;
	}
	
	public int  value(){
		return this.value;
	}
}
