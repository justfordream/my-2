package com.huateng.cmupay.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * @author cmt
 * 
 */
public class StringFormat {
	public static String toString(String pattern, Object obj) {
		if (obj == null) {
			return "";
		}
		if ((obj instanceof String)) {
			return (String) obj;
		}

		if (pattern == null) {
			return obj.toString();
		}

		if ((obj instanceof Date)) {
			return toString(pattern, (Date) obj);
		}

		if ((obj instanceof BigDecimal)) {
			return toString(pattern, (BigDecimal) obj);
		}

		if ((obj instanceof Double)) {
			return toString(pattern, ((Double) obj).doubleValue());
		}

		if ((obj instanceof Float)) {
			return toString(pattern, ((Float) obj).floatValue());
		}

		if ((obj instanceof Long)) {
			return toString(pattern, ((Long) obj).longValue());
		}

		if ((obj instanceof Integer)) {
			return toString(pattern, ((Integer) obj).intValue());
		}

		return obj.toString();
	}

	public static String toString(String pattern, Date d) {
		if (d == null)
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(d);
	}

	public static Long paseLong(String str) {
		if (StringUtils.isBlank(str))
			return null;
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Integer paseInt(String str) {
		if (StringUtils.isBlank(str))
			return null;
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return null;
		}
	}

	public static String toString(String pattern, BigDecimal n) {
		if (n == null)
			return "";
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(n);
	}

	public static String toString(String pattern, double n) {
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(n);
	}

	public static String toString(String pattern, float n) {
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(n);
	}

	public static String toString(String pattern, int n) {
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(n);
	}

	public static String toString(String pattern, long n) {
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(n);
	}

	public static String roundString(String value, boolean leftAlign,
			int maxLength) {
		try {
			StringBuffer sb = new StringBuffer(value);
			int length = value.getBytes("GBK").length;
			if (length < maxLength) {
				if (leftAlign) {
					for (int i = 0; i < maxLength - length; i++)
						sb.append(' ');
				} else {
					for (int i = 0; i < maxLength - length; i++) {
						sb.insert(0, ' ');
					}
				}
			}

			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

	}

	public static Date fromStringToDate(String pattern, String value)
			throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.parse(value);
	}

	public static BigDecimal fromStringToBigDecimal(String pattern, String value) {
		DecimalFormat df = new DecimalFormat(pattern);
		return new BigDecimal(df.parse(value, new ParsePosition(0))
				.doubleValue());
	}

	public static String moneyToString(BigDecimal value, String symbol,
			Integer scale) {
		if (value == null)
			return "";
		String svalue = doubleToNumberString(value.doubleValue(), null, scale);
		if (symbol != null)
			return symbol + svalue;
		return svalue;
	}

	public static String doubleToNumberString(double value, Integer width,
			Integer scale) {
		DecimalFormat df = new DecimalFormat();

		if ((width != null) && (scale != null)) {
			int integerDigits = 3 * (width.intValue() - scale.intValue()) / 4;
			df.setMaximumIntegerDigits(integerDigits);
		}

		df.setMinimumIntegerDigits(1);
		if (scale != null) {
			df.setMinimumFractionDigits(scale.intValue());
			df.setMaximumFractionDigits(scale.intValue());
		}
		return df.format(value);
	}

	public static String longToNumberString(long value, int width) {
		int integerDigits = 3 * (width + 1) / 4;

		DecimalFormat df = new DecimalFormat();
		df.setMaximumIntegerDigits(integerDigits);
		df.setMinimumIntegerDigits(1);
		df.setMinimumFractionDigits(0);
		df.setMaximumFractionDigits(0);
		return df.format(value);
	}

	public static Date toTime(String aString, String sep) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("hh" + sep + "mm"
				+ sep + "ss");
		return formatter.parse(aString);
	}

	public static Date toDate(String aString, String sep) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy" + sep + "MM"
				+ sep + "dd");
		return formatter.parse(aString);
	}

	public static Date toDateTime(String aString, String daySep,
			String dayTimeSep, String timeSep) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy" + daySep
				+ "MM" + daySep + "dd" + dayTimeSep + "HH" + timeSep + "mm"
				+ timeSep + "ss");

		return formatter.parse(aString);
	}

	public static String DateToString(Date aDate, String sep) {
		if (aDate == null)
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + sep + "MM" + sep
				+ "dd");
		return sdf.format(aDate);
	}

	public static String DateTimeToString(Date aDate, String daySep,
			String dayTimeSep, String timeSep) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + daySep + "MM"
				+ daySep + "dd" + dayTimeSep + "HH" + timeSep + "mm" + timeSep
				+ "ss");

		return sdf.format(aDate);
	}

	public static Boolean toBoolean(String value) {
		if (value == null) {
			return null;
		}
		if (value.equals("1") == true) {
			return Boolean.TRUE;
		}

		if (value.equals("0") == true) {
			return Boolean.FALSE;
		}

		return Boolean.valueOf(value);
	}

	/**
	 * 只保留最后四位代码
	 * 
	 * @param str
	 * @return
	 */
	public static String formatCodeString(String str) {
		if (StringUtils.isBlank(str))
			return str;
		if (str.length() < 4)
			return str;
		int number = str.length() - 4;
		String newStr = str.substring(number);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < number; i++) {
			sb.append("x");
		}
		return sb.toString() + newStr;
	}

	/**
	 * 只保留最后一位代码
	 * @param str
	 * @return
	 */
	public static String formatNameString(String str) {
		if (StringUtils.isBlank(str))
			return str;
		if (str.length() < 1)
			return str;
		int number = str.length() - 1;
		String newStr = str.substring(number);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < number; i++) {
			sb.append("x");
		}
		return sb.toString() + newStr;
	}
	
	/**
	 * 
	 * @param strContent 原字符串
	 * @param replceFlag 替换字符 如 “*”
	 * @param keepLength 保留长度
	 * @param keepDirection 从左、右开始保留    默认从右开始保留
	 * @return
	 * @throws Exception
	 */
	public static String encryStr(String strContent,String replceFlag,int keepLength,String keepDirection) throws Exception{
		String result = "";
		if(keepLength > strContent.length())
			return strContent;
		//保留字符
		String strKeep = "";
		if("LEFT".equalsIgnoreCase(keepDirection)){
			strKeep = strContent.substring(0,keepLength);
		}else{
			strKeep = strContent.substring(strContent.length()-keepLength,strContent.length());
		}
	
		//需替换字符串长度
		int replaceLangth = strContent.length() - keepLength;
		StringBuffer replceStrBuffer = new StringBuffer();
		for(int i=0,j=replaceLangth;i<j;i++){
			replceStrBuffer.append(replceFlag);
		}
		
		if("LEFT".equalsIgnoreCase(keepDirection)){
			result = strKeep + replceStrBuffer.toString();
		}else{
			result = replceStrBuffer.toString() + strKeep;
		}

		return result;
	}

}