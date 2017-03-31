package com.push.server.other;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.push.server.cache.MessageCache;
import com.push.server.cache.MessageCount;
import com.push.server.cache.OfflineUserCache;
import com.push.server.database.DBOperationImpl;
import com.push.server.tool.ConfigMap;
import com.push.server.tool.XMLParser;

public class MessageManager {
	
	private MessageCache messageCache = new MessageCache();
	private MessageCount messageCount = new MessageCount();
	private OfflineUserCache offlineUserCache = new OfflineUserCache();
	//间隔时间
	private static int day = Integer.parseInt(ConfigMap.getValue(XMLParser.PUSH_SERVER_CLEARCACHEINTERVAL));
	private static final long PERIOD_SECONDS = day * 24 * 60 * 60;
	//执行时间
	private static int HOUR = 2;
	private static int MINUTE = 0;
	private static int SECOND = 0;
	
	private DBOperationImpl dbOperation = new DBOperationImpl();
	
	private Logger log = Logger.getLogger(MessageManager.class.getName());
	
	public void startThread(){
		long initialDelay = 0;
		if(new Date().before(firstTime())){
			initialDelay = firstTime().getTime() - new Date().getTime();
			initialDelay = initialDelay/1000;
		}else {
			initialDelay = new Date().getTime() - firstTime().getTime();
			initialDelay = 24*60*60 - initialDelay/1000;
		}
		
System.out.println(initialDelay);
		ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
		service.scheduleAtFixedRate(runnable, initialDelay, PERIOD_SECONDS, TimeUnit.SECONDS);
	}
	
	public Date firstTime(){
		Calendar calendar = Calendar.getInstance();
        
		//设置时间点
		calendar.set(Calendar.HOUR_OF_DAY, HOUR);
		calendar.set(Calendar.MINUTE, MINUTE);
		calendar.set(Calendar.SECOND, SECOND);
		Date date=calendar.getTime(); //第一次执行定时任务的时间
		
		return date;
	}
	
	Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			log.info("start clear cache");
			clearCache();
		}
	};
	
	public void clearCache(){
		Map<Integer, Long> timeMap = messageCache.getTimeMap();
		if(timeMap.size() == 0){
			log.error("The message cache has no data");
			return ;
		}
System.out.println(timeMap.size());
		Iterator<Entry<Integer, Long>> iter = timeMap.entrySet().iterator();
		List<Integer> messageList = new ArrayList<Integer>();
		while(iter.hasNext()){
			Map.Entry<Integer, Long> entry = (Map.Entry<Integer, Long>) iter.next();
			int messageID = (int)entry.getKey();
			long time = (long) entry.getValue();
			Date date = new Date(time);
			date = addDay(date, day);

			if(date.before(firstTime())){
System.out.println("date:"+date+"  firstTime:"+firstTime());
				int count = messageCount.getCount(messageID);
				Map<String, ArrayList<Integer>> userCache = offlineUserCache.getOfflineUser();
				if(userCache.size() == 0){
					log.info("The offline user cache has no data");
					return ;
				}
				
				Iterator<Entry<String, ArrayList<Integer>>> iterUser = userCache.entrySet().iterator();
				while(iterUser.hasNext()){
					Map.Entry<String, ArrayList<Integer>> entryUser = (Map.Entry<String, ArrayList<Integer>>) iterUser.next();
					String userID = (String) entryUser.getKey();

					ArrayList<Integer> messageIDs = (ArrayList<Integer>) entryUser.getValue();
					List<Integer> messageIDList = new ArrayList<Integer>();
					for(int i=0; i<messageIDs.size(); i++){
						
						if((int)messageIDs.get(i) == messageID){
System.out.println("user ID:"+userID+" message ID:"+messageIDs.get(i));
							log.info(messageID+":message count minus one");
							messageIDList.add(messageID);
							count = count - 1;
							break;
						}
					}
					
					if(messageIDList.size() != 0){
System.out.println("message list:"+messageIDList.size());
						messageIDs.removeAll(messageIDList);
						dbOperation.deleteOfflineuser(userID, messageID);
						log.info("userID:"+userID+" and messageID"+messageID+"is removed from cache and database");
					}
				}
				
				if(count == 0){
					messageList.add(messageID);
				}
				
			}
		}
		if(messageList.size() != 0){
			messageCache.removeAll(messageList);
			log.info("since the message is useless, and the message is removed");
		}
	}
	
	public Date addDay(Date date, int num) {
		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.DAY_OF_MONTH, num);
		return startDT.getTime();
	}
}
