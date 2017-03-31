package com.ttqeducation.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期工具类，用来转换字符串到日期或时间戳。
 * 
 * @author zimt
 */
public final class DateUtil {

	private static final String TIME_PATTERN = "HH:mm";

	private DateUtil() {
	}

	public static java.util.Date addMonths(java.util.Date paramDate,
			int paramInt) {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(paramDate);
		localCalendar.add(2, paramInt);
		return localCalendar.getTime();
	}

	public static java.util.Date addDays(java.util.Date paramDate, int paramInt)
			throws Exception {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(paramDate);
		int i = localCalendar.get(6);
		localCalendar.set(6, i + paramInt);
		return localCalendar.getTime();
	}

	/**
	 * 获取当前日期的前一天的日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date getNextDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		date = calendar.getTime();
		return date;
	}

	/**
	 * 此方法根据输入时的指定的格式解析字符串，返回一个日期对象。
	 * 
	 * @param aMask
	 *            日期格式
	 * @param strDate
	 *            日期的字符串表示
	 * @return 一个转换后的日期对象
	 * @throws java.text.ParseException
	 *             当字符串不符合指定的格式时
	 * @see java.text.SimpleDateFormat
	 */
	public static Date convertStringToDate(String aMask, String strDate)
			throws ParseException {
		SimpleDateFormat df;
		Date date;
		df = new SimpleDateFormat(aMask);
		try {
			date = df.parse(strDate);
		} catch (ParseException pe) {
			// log.error("ParseException: " + pe);
			throw new ParseException(pe.getMessage(), pe.getErrorOffset());
		}

		return (date);
	}

	/**
	 * 把传入的日期按一定的格式进行转化为字符串类型；
	 * 
	 * @param aMask
	 *            转化格式
	 * @param aDate
	 *            日期
	 * @return
	 */
	public static String convertDateToString(String aMask, Date aDate) {
		SimpleDateFormat df = null;
		String returnValue = "";

		if (aDate == null) {
			return null;
		} else {
			df = new SimpleDateFormat(aMask);
			returnValue = df.format(aDate);
		}

		return (returnValue);
	}

	/**
	 * 获取指定日期月份的最后一天，patter为返回的日期的字符串格式
	 * 
	 * @param date
	 * @param patter
	 * @return
	 */
	public static String getlastDayOfMonth(Date date, String patter) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.roll(Calendar.DAY_OF_MONTH, -1);
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
				patter);
		return format.format(cal.getTime());
	}

	/**
	 * 获取上个月的最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getlastDayOfMonthDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.roll(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}

	/**
	 * 获取指定日期月份的第一天， patter为返回的的字符串日期格式
	 * 
	 * @param date
	 * @param patter
	 * @return
	 */
	public static String getFirstDayOfMonth(Date date, String patter) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
				patter);
		return format.format(cal.getTime());
	}

	/**
	 * 获取传入日期的第一天日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfMonthDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
		return cal.getTime();
	}

	/**
	 * 根据当前传入日期获取这个月的最后一天的日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date getEndDayOfMonthDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, cal.getMaximum(Calendar.DATE));
		return cal.getTime();
	}

	public static int getWeekNumOfDate(Date dt) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return w;
	}

	/**
	 * 获取传入的日期的前一个月日期；
	 * 
	 * @param dt
	 * @return
	 */
	public static Date getLastDateOfThisDate(Date dt) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		Date date = cal.getTime();
		return date;
	}

	/**
	 * 获取传入的日期的下一个月日期；
	 * 
	 * @param dt
	 * @return
	 */
	public static Date getNextDateOfThisDate(Date dt) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
		Date date = cal.getTime();
		return date;
	}

	/**
	 * 判断传入的日期是否是上午
	 * 
	 * @param dt
	 * @return
	 */
	public static boolean isMorning(Date dt) {
		GregorianCalendar ca = new GregorianCalendar();
		ca.setTime(dt);
		int ret = ca.get(GregorianCalendar.AM_PM); // 0 表示上午，1 表示上午；
		if (ret == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取当前日期的年；
	 * 
	 * @param dt
	 * @return
	 */
	public static int getYear(Date dt) {
		int year = -1;
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		year = cal.get(Calendar.YEAR);
		return year;
	}

	/**
	 * 获取当前日期的月；
	 * 
	 * @param dt
	 * @return
	 */
	public static int getMonth(Date dt) {
		int month = -1;
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		month = cal.get(Calendar.MONTH);
		return month + 1;
	}

	/**
	 * 获取当前日期的日；
	 * 
	 * @param dt
	 * @return
	 */
	public static int getDay(Date dt) {
		int day = -1;
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		day = cal.get(Calendar.DATE);
		return day;
	}

	/**
	 * 获取传入时间的时和分；
	 * 
	 * @param dt
	 * @return
	 */
	public static String getHourAndMinute(Date dt) {
		SimpleDateFormat adf = new SimpleDateFormat("HH:mm");
		String hourAndMinute = adf.format(dt);
		return hourAndMinute;
	}
	
	/** 
     * 计算两个日期之间相差的天数 
     * @param date1 
     * @param date2 
     * @return date2 - date1
     */  
    public static int daysBetween(Date startDate,Date endDate)  
    {  
    	Calendar fromCalendar = Calendar.getInstance();  
        fromCalendar.setTime(startDate);  
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);  
        fromCalendar.set(Calendar.MINUTE, 0);  
        fromCalendar.set(Calendar.SECOND, 0);  
        fromCalendar.set(Calendar.MILLISECOND, 0);  
  
        Calendar toCalendar = Calendar.getInstance();  
        toCalendar.setTime(endDate);  
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);  
        toCalendar.set(Calendar.MINUTE, 0);  
        toCalendar.set(Calendar.SECOND, 0);  
        toCalendar.set(Calendar.MILLISECOND, 0);  
  
        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));         
    }

	/**
	 * 获取传入的日期对应的星期
	 * 
	 * @param dt
	 * @return
	 */
	public static String getWeekOfDate(Date dt) {
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		return weekDays[getWeekNumOfDate(dt)];
	}
}
