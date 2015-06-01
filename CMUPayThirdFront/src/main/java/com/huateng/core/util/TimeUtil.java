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

	/**
	 * 获取当前时间
	 * 
	 * @return 字符串格式
	 */
	public static String getCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat(FORMAT_TIME);
		return df.format(new Date());
	}

	public static void main(String[] args) {
		System.out.println(TimeUtil.getCurrentTime());
	}
}
