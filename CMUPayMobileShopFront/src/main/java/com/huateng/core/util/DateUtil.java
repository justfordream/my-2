package com.huateng.core.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 日期工具类
 * 
 * @author Gary
 * 
 */
public class DateUtil {
	public static String FORMAT_DATE = "yyyy-MM-dd";

	/**
	 * 获取当前日期
	 * 
	 * @return 字符串格式
	 */
	public static String getCurrentDate() {
		SimpleDateFormat df = new SimpleDateFormat(FORMAT_DATE);
		return df.format(new Date());
	}

	/**
	 * 获取当然日期
	 * 
	 * @return 日期格式
	 */
	public static Date getNowDate() {
		return new Date();
	}

	/**
	 * 获取当前日期和时间
	 * 
	 * @return
	 */
	public static String getCurrentFullDate() {
		return DateUtil.getCurrentDate() + " " + TimeUtil.getCurrentTime();
	}
	
	public static String getDateyyyyMMddHHmmssSSS() {
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Calendar calendar = Calendar.getInstance();
		return myFormat.format(calendar.getTime());
	}
	
	
	public static String getDateyyyyMMddHHmmss() {
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar calendar = Calendar.getInstance();
		return myFormat.format(calendar.getTime());
	}

	public static String getDateyyyyMMdd() {
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		return myFormat.format(calendar.getTime());
	}

	public static void main(String[] args) {
		System.out.println(DateUtil.getCurrentDate());
	}
}
