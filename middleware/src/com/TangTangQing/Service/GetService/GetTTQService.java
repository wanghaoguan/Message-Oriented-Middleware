package com.TangTangQing.Service.GetService;


import com.TangTangQing.client.MidService;



public class GetTTQService {
	
	public static MidService schoolService;
	
	public static MidService getService(){
		try{
			if (schoolService==null){
				schoolService= new MidService();
				String result= schoolService.getMidServiceSoap().helloWorld();
				if(result.equals( "Hello World"))
					System.out.println("已连接到中间件服务器");
				return schoolService;
				
//				if(linkResult==1)
//				{
//					System.out.println("已连接学校服务器");	
//					return schoolService;
//				}
//				else
//				{
//					System.out.println("连接学校服务器失败");
//					return null;
//				}
			}
			else{
				return schoolService;
			}
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
