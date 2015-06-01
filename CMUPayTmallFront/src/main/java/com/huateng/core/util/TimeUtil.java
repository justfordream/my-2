package com.huateng.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 * 
 * @author Gary
 * 
 */
public class TimeUtil {
	public static String FORMAT_TIME = "HH:mm:ss";
	public static String FORMAT_YMD = "yyyyMMdd";
	public static String FORMAT_YMDHMSS = "yyyyMMddHHmmssSSS";
	/**
	 * 获取当前时间
	 * 
	 * @return 字符串格式
	 */
	public static String getCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat(FORMAT_TIME);
		return df.format(new Date());
	}
	
	public static String now() {
		SimpleDateFormat df = new SimpleDateFormat(FORMAT_YMDHMSS);
		return df.format(new Date());
	}
	
	
	/**
	 * 获取指定日期格式系统日期的字符型日期
	 * 
	 * @param p_format
	 *            日期格式 格式1:"yyyy-MM-dd" 格式2:"yyyy-MM-dd hh:mm:ss EE"
	 *            格式3:"yyyy年MM月dd日 hh:mm:ss EE" 说明: 年-月-日 时:分:秒 星期 注意MM/mm大小写
	 * @return String 系统时间字符串
	 * @author
	 * @Date:
	 */
	public static String getSystemOfDateByFormat(String p_format) {
		long time = System.currentTimeMillis();
		Date d = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat(p_format);
		String dateStr = sdf.format(d);
		return dateStr;
	}
	
	
	public static void main(String[] args) {
		System.out.println(TimeUtil.getCurrentTime());
	}
}
