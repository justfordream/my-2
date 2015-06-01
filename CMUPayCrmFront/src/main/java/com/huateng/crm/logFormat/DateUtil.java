/**
 * 
 */
package com.huateng.crm.logFormat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期、时间类
 * @author m
 *
 */
public class DateUtil {

	/**
	 * 日期转换 
	 * @author cmt
	 * @param time
	 * @param fmt:yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String formatTime(Timestamp time,String fmt) {
		if (time == null) {
			return "";
		}
		SimpleDateFormat myFormat = new SimpleDateFormat(fmt);
		return myFormat.format(time);
	}
	
	
	/**
	 * 获取系统当前时间（秒）
	 * @author cmt
	 * @return
	 */
	public static Timestamp getTime() {
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		String mystrdate = myFormat.format(calendar.getTime());
		return Timestamp.valueOf(mystrdate);
	}
	
	/**
	 * 获取当前日期(时间00:00:00)
	 * @author cmt
	 * @return
	 */
	public static Timestamp getDateFirst(){
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		Calendar calendar = Calendar.getInstance();
		String mystrdate = myFormat.format(calendar.getTime());
		return Timestamp.valueOf(mystrdate);
	}
	
	/**
	 * 获取当前日期(时间23:59:59)
	 * @author cmt
	 * @return
	 */
	public static Timestamp getDateLast(){
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		Calendar calendar = Calendar.getInstance();
		String mystrdate = myFormat.format(calendar.getTime());
		return Timestamp.valueOf(mystrdate);
	}
	
	/**
	 * 获取当前日期
	 * @author cmt
	 * @return
	 */
	public static Date getDate(){
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime();
	}
	
	/**
	 * yyyy-MM-dd HH:mm:ss 转换成Timestamp
	 * @author cmt
	 * @param timeString
	 * @return
	 */
	public static Timestamp getTime(String timeString){
		return Timestamp.valueOf(timeString);
	}
	
	
	/**
	 * 自定义格式的字符串转换成日期
	 * @author cmt
	 * @param timeString
	 * @param fmt
	 * @return
	 * @throws Exception
	 */
	public static Timestamp getTime(String timeString,String fmt) throws Exception{
		SimpleDateFormat myFormat = new SimpleDateFormat(fmt);
		Date date= myFormat.parse(timeString);
		myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return getTime(myFormat.format(date));
	}
	
	/**
	 * 格式化日期
	 * @author cmt
	 * @param date
	 * @param fmt
	 * @return
	 * @throws Exception
	 */
	public static String formatDate(Date date,String fmt) throws Exception{
		if (date == null) {
			return "";
		}
		SimpleDateFormat myFormat = new SimpleDateFormat(fmt);
		return myFormat.format(date);
	}
	
	/**
	 * 返回日期或者时间，如果传入的是日期，返回日期的00:00:00时间
	 * @author cmt
	 * @param timeString
	 * @return
	 * @throws Exception
	 */
	public static Timestamp getDateFirst(String timeString) throws Exception{
		if (timeString == null || timeString.equals("")) {
			return null;
		}
		if (timeString.length() > 10) {
			return getTime(timeString, "yyyy-MM-dd HH:mm:ss");
		} else {
			return getTime(timeString, "yyyy-MM-dd");
		}
	}
	
	
	/**
	 * 返回日期或者时间，如果传入的是日期，返回日期的23:59:59时间
	 * @author cmt
	 * @param timeString
	 * @return
	 * @throws Exception
	 */
	public static Timestamp getDateLast(String timeString) throws Exception{
		if (timeString == null || timeString.equals("")) {
			return null;
		}
		if (timeString.length() > 10) {
			return getTime(timeString, "yyyy-MM-dd HH:mm:ss");
		} else {
			return getTime(timeString +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
		}
	}
	
	/**
	 * 获取本周 周一时间，返回 格式yyyy-MM-dd 00:00:00
	 * @author cmt
	 * @return
	 */
	public static Timestamp getMonday(){
		Calendar calendar= Calendar.getInstance(); 
		int dayofweek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayofweek == 0)
			dayofweek = 7;
		calendar.add(Calendar.DATE, -dayofweek + 1);
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String mystrdate = myFormat.format(calendar.getTime());
		return Timestamp.valueOf(mystrdate);
	}
	
	
	/**
	 * 获取本周 周日 时间，返回格式yyyy-MM-dd 23:59:59
	 * @author cmt
	 * @return
	 */
	public static Timestamp getSunday(){
		Calendar calendar= Calendar.getInstance(); 
		int dayofweek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayofweek == 0)
			dayofweek = 7;
		calendar.add(Calendar.DATE, -dayofweek + 7);
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		String mystrdate = myFormat.format(calendar.getTime());
		return Timestamp.valueOf(mystrdate);
	}
	
	
	/**
	 * 增加天数
	 * @author cmt
	 * @param time
	 * @param day
	 * @return
	 */
	public static Timestamp addDay(Timestamp time,Integer day){
		Timestamp time2=new Timestamp(time.getTime()+day*1000l*60*60*24);
		return time2;
	}
	
	/**
	 * 比较2个日期格式的字符串 
	 * @author cmt
	 * @param str1  格式：yyyyMMdd
	 * @param str2 格式：yyyyMMdd
	 * @return
	 */
	public static Integer compareDate(String str1,String str2) throws Exception{

		return Integer.parseInt(str1)-Integer.parseInt(str2);
		
	}
	
	/**
	 * 2个时间的相差天数
	 * @author cmt
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static Integer getDay(Timestamp time1,Timestamp time2){
		Long dayTime=(time1.getTime()-time2.getTime())/(1000*60*60*24);
		return dayTime.intValue();
	}
	
	/**
	 * 获取系统当前时间（分）
	 * @author cmt
	 * @return
	 */
	public static String getMinute() {
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMddHHmm");
		return myFormat.format(new Date());
	}
		
	
	/**
	 * 转换成时间 字符串格式必须为 yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd
	 * @author cmt
	 * @return
	 * @throws ParseException 
	 */
	public static Date parseToDate(String val) throws ParseException{
		Date date = null;
		if(val != null && val.trim().length() != 0 && !val.trim().toLowerCase().equals("null")){
			val = val.trim();
			if(val.length()>10){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				date = sdf.parse(val);
			}
			if(val.length() <= 10){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				date = sdf.parse(val);
			}
		}	
		return date;
	}
	
	/**
	 * 获取上月的第一天yyyy-MM-dd 00:00:00和最后一天yyyy-MM-dd 23:59:59
	 * @author cmt
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static Map<String,String> getPreMonth(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		Calendar cal = Calendar.getInstance();
		GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		calendar.add(Calendar.MONTH, -1);
		Date theDate = calendar.getTime();
		gcLast.setTime(theDate);
		gcLast.set(Calendar.DAY_OF_MONTH, 1);
		String day_first_prevM = df.format(gcLast.getTime());
		StringBuffer str = new StringBuffer().append(day_first_prevM).append(
		" 00:00:00");
		day_first_prevM = str.toString(); //上月第一天

		calendar.add(cal.MONTH, 1);
		calendar.set(cal.DATE, 1);
		calendar.add(cal.DATE, -1);
		String day_end_prevM = df.format(calendar.getTime());
		StringBuffer endStr = new StringBuffer().append(day_end_prevM).append(
		" 23:59:59");
		day_end_prevM = endStr.toString();  //上月最后一天

		Map<String, String> map = new HashMap<String, String>();
		map.put("prevMonthFD", day_first_prevM);
		map.put("prevMonthPD", day_end_prevM);
		return map;
	}
	
	
	/**
	 * 获取上周 周一时间，返回 格式yyyy-MM-dd 00:00:00
	 * @author cmt
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static Timestamp getPreMonday(){
		Calendar calendar= Calendar.getInstance(); 
		int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);
		System.out.println(dayofweek);
		if (dayofweek == 1){
			calendar.add(calendar.WEEK_OF_MONTH,-1); 
		}
		
		calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
		calendar.add(calendar.WEEK_OF_MONTH,-1);
		
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String mystrdate = myFormat.format(calendar.getTime());
		return Timestamp.valueOf(mystrdate);
	}
	
	/**
	 * 获取上周 周日时间，返回 格式yyyy-MM-dd 23:59:59
	 * @author cmt
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static Timestamp getPreSunday(){
		Calendar calendar= Calendar.getInstance(); 
		int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayofweek != 1){
			calendar.add(calendar.WEEK_OF_MONTH,+1); 
		}
		
		calendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY); 
		calendar.add(calendar.WEEK_OF_MONTH,-1); 

		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		String mystrdate = myFormat.format(calendar.getTime());
		return Timestamp.valueOf(mystrdate);
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
	
	/**
	 * 获取当前月份，格式yyyyMM
	 * 
	 * @return
	 */
	public static String getMonth() {
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMM");
		return myFormat.format(getDate());
	}
}
