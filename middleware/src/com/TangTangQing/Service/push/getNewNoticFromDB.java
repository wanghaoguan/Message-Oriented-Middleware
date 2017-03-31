package com.TangTangQing.Service.push;

import io.netty.channel.ChannelHandlerContext;

import com.TangTangQing.Service.GetService.GetTTQService;
import com.TangTangQing.Service.XMLReader.WebServiceDataSetXMLReader;
import com.push.server.decode.DecodePushInfoTTQ;
import com.push.server.push.PushMessage;

public class getNewNoticFromDB {
	
	private ChannelHandlerContext ctx = null;
	private String info = null;
	
	public getNewNoticFromDB(ChannelHandlerContext ctx, String info){
		this.ctx = ctx;
		this.info = info;
	}

	public void getNoticeByID(){
		try{
			String newNotice = GetTTQService.getService().getMidServiceSoap().appPushNotificationOneNoticeToMid(Integer.parseInt(info));
			DecodePushInfoTTQ pushInfo= WebServiceDataSetXMLReader.readnewNotice(newNotice);
			PushMessage pm= new PushMessage(ctx,info);
			pm.pushMessage(pushInfo);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
