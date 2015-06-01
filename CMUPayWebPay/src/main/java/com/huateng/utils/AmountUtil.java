package com.huateng.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * 金额转换和显示格式
 * 
 * @author 马博阳
 */
public class AmountUtil {

	private final static String DOLLAR_FORMAT = "###############0.00";

	private final static String FEN_FORMAT = "#################0";

	/**
	 * 左移2位
	 * 
	 * @param amout
	 * @return
	 */
	public static String toDollar(String amout) {

		return new BigDecimal(amout).divide(new BigDecimal(100)).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
	}

	/**
	 * 右移2位
	 * 
	 * @param amout
	 * @return
	 */
	public static String toFen(String amout) {

		return String.valueOf(new BigDecimal(amout).movePointRight(2).longValue());
	}

	/**
	 * 显示精度,默认9.99
	 * 
	 * @param amout
	 * @return
	 */
	public static String dollarFormat(String amout, String format) {

		if (StringUtils.isBlank(format)) {

			format = DOLLAR_FORMAT;
		}

		DecimalFormat df = new DecimalFormat(format);

		return df.format(new BigDecimal(amout));
	}

	/**
	 * 显示精度,默认0
	 * 
	 * @param amout
	 * @return
	 */
	public static String fenFormat(String amout, String format) {

		if (StringUtils.isBlank(format)) {

			format = FEN_FORMAT;
		}

		DecimalFormat df = new DecimalFormat(format);

		return df.format(new BigDecimal(amout));
	}

	/**
	 * 分转换为元.
	 * 
	 * @param fen
	 *            分
	 * @return 元
	 */
	public static String fromFenToYuan(final String fen) {
		if(StringUtils.isBlank(fen))
			return fen;
		String yuan = fen;
		final int MULTIPLIER = 100;
		Pattern pattern = Pattern.compile("^[1-9][0-9]*{1}");
		Matcher matcher = pattern.matcher(fen);
		if (matcher.matches()) {
			yuan = new BigDecimal(fen).divide(new BigDecimal(MULTIPLIER)).setScale(2).toString();
		}
		return yuan;
	}

	/**
	 * 元转换为分.
	 * 
	 * @param yuan
	 *            元
	 * @return 分
	 */
	public static String fromYuanToFen(final String yuan) {
		if(StringUtils.isBlank(yuan))
			return yuan;
		String fen = yuan;
		Pattern pattern = Pattern.compile("^[0-9]+(.[0-9]{2})?{1}");
		Matcher matcher = pattern.matcher(yuan);
		if (matcher.matches()) {
			try {
				NumberFormat format = NumberFormat.getInstance();
				Number number = format.parse(yuan);
				double temp = number.doubleValue() * 100.0;
				// 默认情况下GroupingUsed属性为true 不设置为false时,输出结果为2,012
				format.setGroupingUsed(false);
				// 设置返回数的小数部分所允许的最大位数
				format.setMaximumFractionDigits(0);
				fen = format.format(temp);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return fen;
	}

	/**
	 * 判断是否是整数
	 * @param value
	 * @return
	 */
	public static boolean isInteger(String value){
		Pattern pattern = Pattern.compile("^[0-9]*$");
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
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

}
