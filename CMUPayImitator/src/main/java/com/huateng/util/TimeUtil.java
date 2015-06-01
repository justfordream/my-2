package main.java.com.huateng.util;

import java.text.SimpleDateFormat;

/**
 * 20120629
 * @author Administrator
 * 获得本地时间，格式yyyyMMddHHmmss
 * @return String
 */
public class TimeUtil {
	public String getLocalDatetime(){
		SimpleDateFormat localDate = new SimpleDateFormat("yyyyMMddHHmmss");
		return localDate.format(new java.util.Date());
	}
	
	public String getLocalDatetimeDetail(){
		SimpleDateFormat localDate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return localDate.format(new java.util.Date());
	}
	
	public String getLocalDatetime_1(){
		SimpleDateFormat localDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return localDate.format(new java.util.Date());
	}
	
	public String getLocalDate(){
		SimpleDateFormat localDate = new SimpleDateFormat("yyyyMMdd");
		return localDate.format(new java.util.Date());
	}
	
	public static void main(String[] agrs){
		TimeUtil s = new TimeUtil();
//		System.out.println(s.getLocalDatetimeDetail());
//		System.out.println(s.getLocalDatetime());
	}
}
