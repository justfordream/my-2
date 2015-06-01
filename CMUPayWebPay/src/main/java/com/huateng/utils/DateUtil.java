package com.huateng.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class DateUtil {
 
    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"; 
    
    /**
     * 获取格式化时间
     * @param format
     * @param date
     * @return
     */
    public static String getDateFormat(String format,Date date){
        
        if(StringUtils.isBlank(format)){
            
            format = DATE_FORMAT;
        }
        
        if(date == null){
            
            date = new Date();
        }
        
        return new SimpleDateFormat(format).format(new Date());
    }
    
    /**
     * 获取默认格式下时间显示  yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String getDateFormat(Date date){
        
        return getDateFormat(null, date);
    }
    
    /**
     * 获取当前时间 格式 yyyyMMdd
     * @return
     */
    public static String getDateFormatYMD(){
        
        return getDateFormat("yyyyMMdd", new Date());
        
    }
    
    /**
     * 获取当前时间 格式 yyyyMMddHHmmss
     * @return
     */
    public static String getDateFormatYMDHMS(){
        
        return getDateFormat("yyyyMMddHHmmss", new Date());
    }
    
    
    /**
     * 获取当前时间 格式 yyyyMMddHHmmss
     * @return
     */
    public static String getDateFormatYMDHMSSS(){
        
        return getDateFormat("yyyyMMddHHmmssSSS", new Date());
    }
    
    /**
     * 转换时间显示方式
     * @param format
     * @param date
     * @param parse
     * @return
     */
    public static String getDateParseFormat(String format ,String date,String parse){
        
        try {
            
            return new SimpleDateFormat(format).format(new SimpleDateFormat(parse).parse(date));
       
        } catch (Exception e) {
           
            e.printStackTrace();
        }
        
        return "";
    }
   
    /**
     * 获取当前date前后几个月后的日期
     * span 跨度  
     * @return
     */
    public static String getDateMonthSpan(int span,String format){
        
        return getDateSpan(span, format, Calendar.MONTH);
    }
    
    /**
     * 获取date前后几个日
     * span 跨度  
     * @return
     */
    public static String getDateDaySpan(int span,String format){
        
        return getDateSpan(span, format, Calendar.DAY_OF_MONTH);
    }
    
    /**
     * 获取当前date前后几个月后的日期
     * span 跨度  
     * type标识： 月Calendar.DAY_OF_MONTH  年  日 
     * @return
     */
    public static String getDateSpan(int span,String format,int type){
        
        Calendar calendar = Calendar.getInstance();
        
        calendar.add(type, span);

        return new SimpleDateFormat(format).format(calendar.getTime());
    }
    
	public static String getDateyyyyMMddHHmmss() {
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar calendar = Calendar.getInstance();
		return myFormat.format(calendar.getTime());
	}
    
    /**
     * 获取跨度列表
     * @throws Exception 
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getSpanDayList(String begDate , String endDate ,String format) throws Exception{
        
        List list = new ArrayList<String>();
        
        Date b = new SimpleDateFormat("yyyyMMdd").parse(begDate);
        Date e = new SimpleDateFormat("yyyyMMdd").parse(begDate);
        
        Calendar calendarBegin = Calendar.getInstance();
        
        calendarBegin.setTime(b);
        
        Calendar calendarEnd = Calendar.getInstance();
        
        calendarBegin.setTime(e);
        
        while(calendarBegin.before(calendarEnd)){
            
            String lday = getDateFormat(format,calendarEnd.getTime());
            
            list.add(lday);
            
            calendarEnd.add(Calendar.DAY_OF_MONTH, -1);
        }
        
        return list;
    }
}
