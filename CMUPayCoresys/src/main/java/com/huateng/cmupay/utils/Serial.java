package com.huateng.cmupay.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.huateng.cmupay.controller.cache.HostMsgCache;

/**
 * @author cmt
 * 
 */
public class Serial {
	// 最大数字
	private static final int MAX = 999999;
	// 数字长度
	private static final int NUMLEN = 6;

	private static Serial instance = null;
	private String lastTime = "";
	private int lastNo = 0;
	private int baseNum = 1000000;
	private int baseNumber= 0;
	// 表示实时流水
	private static final int serialCat = 10;
	// 生成序列长度
	private static final int serialLen = 30;
	private static final int serialLen1 = 32;
	public Serial() {

	}

	private static String getSerialStringByNum(int base, int value) {
		String num = new Integer(base + value).toString();

		return num.substring(1);
	}

	/**
	 * 根据当前时间产生一个新的序列号
	 * 
	 * @param sourceId
	 * @return
	 */
	public static synchronized String genSerialNo(String sourceId) {
		// synchronized (this) {
		Date curTime = new Date();
		SimpleDateFormat s = new SimpleDateFormat("yyMMddHHmmss");

		if (instance == null) {
			instance = new Serial();
			instance.lastNo = 0;
			instance.baseNum = (int) Math.pow(10, NUMLEN);
			instance.lastTime = s.format(curTime);
		}
		String now = s.format(curTime);
		// 前面添加主机ip地址最后3位
		//now = HostMsgCache.getHostIP4Part() + now;
		if (now.compareTo(instance.lastTime) > 0) {
			// 当前时间大于上一次记录时间，表示可以开始新的计数
			instance.lastNo = 1;
			instance.lastTime = now;
			return jointRule(sourceId, now);
		}

		if (now.compareTo(instance.lastTime) < 0) {
			// 当前时间小于上一次的记录时间，表示上一秒有超过10000个流水号生成
			now = instance.lastTime;
		}

		int serialNos = instance.lastNo + 1;
		if (serialNos <= MAX) {
			// 当前没有超过最大允许流水号，返回上一个流水号+1作为新的流水号
			instance.lastNo = serialNos;
			return jointRule(sourceId, now);
		}

		try {
			Date last = s.parse(now);
			Calendar cal = Calendar.getInstance();
			cal.setTime(last);
			cal.add(Calendar.SECOND, 1);
			Date endTime = cal.getTime();
			String endStr = s.format(endTime);
			instance.lastNo = 1;
			instance.lastTime = endStr;
			return jointRule(sourceId, endStr);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);

		}
		// }
	}
	/**
	 * 根据当前时间产生一个内部操作流水号
	 * 
	 * @param sourceId
	 * @return
	 */
	public static synchronized String genSerialNos(String sourceId) {
		// synchronized (this) {
		Date curTime = new Date();
//		SimpleDateFormat s = new SimpleDateFormat("yyMMddHHmmss");
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmssSSS");

		if (instance == null) {
			instance = new Serial();
			instance.lastNo = 0;
			instance.baseNum = (int) Math.pow(10, NUMLEN);
			instance.lastTime = s.format(curTime);
		}
		String now = s.format(curTime);
		// 前面添加主机ip地址最后3位
		//now = HostMsgCache.getHostIP4Part() + now;
		if (now.compareTo(instance.lastTime) > 0) {
			// 当前时间大于上一次记录时间，表示可以开始新的计数
			instance.lastNo = 1;
			instance.lastTime = now;
			return jointRule1(sourceId, now);
		}

		if (now.compareTo(instance.lastTime) < 0) {
			// 当前时间小于上一次的记录时间，表示上一秒有超过10000个流水号生成
			now = instance.lastTime;
		}

		int serialNo = instance.lastNo + 1;
		if (serialNo <= MAX) {
			// 当前没有超过最大允许流水号，返回上一个流水号+1作为新的流水号
			instance.lastNo = serialNo;
			return jointRule1(sourceId, now);
		}

		try {
			Date last = s.parse(now);
			Calendar cal = Calendar.getInstance();
			cal.setTime(last);
			cal.add(Calendar.SECOND, 1);
			Date endTime = cal.getTime();
			String endStr = s.format(endTime);
			instance.lastNo = 1;
			instance.lastTime = endStr;
			return jointRule1(sourceId, endStr);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);

		}
		// }
	}
	/**
	 * 根据当前时间产生一个内部流水号
	 * 规则:
	 * 10.消息方式的交易
	 * 11.文件方式的交易
	 * 2位交易方式类型+3位省编码+17位时间精确到毫秒+10流水
	 * */
	public static synchronized String genSerialNum(String sourceId){
		Date curTime = new Date(); 
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmssSSS");

		if (instance == null) {
			instance = new Serial();
			instance.lastNo = 0;
			instance.baseNum = (int) Math.pow(10, NUMLEN);
			instance.lastTime = s.format(curTime);
		}
		String now = s.format(curTime);
		// 前面添加主机ip地址最后3位
		//now = HostMsgCache.getHostIP4Part() + now;
		if (now.compareTo(instance.lastTime) > 0) {
			// 当前时间大于上一次记录时间，表示可以开始新的计数
			instance.lastNo = 1;
			instance.lastTime = now;
			return jointRule2(sourceId, now);
		}

		if (now.compareTo(instance.lastTime) < 0) {
			// 当前时间小于上一次的记录时间，表示上一秒有超过10000个流水号生成
			now = instance.lastTime;
		}

		int serialNo = instance.lastNo + 1;
		if (serialNo <= MAX) {
			// 当前没有超过最大允许流水号，返回上一个流水号+1作为新的流水号
			instance.lastNo = serialNo;
			return jointRule2(sourceId, now);
		}

		try {
			Date last = s.parse(now);
			Calendar cal = Calendar.getInstance();
			cal.setTime(last);
			cal.add(Calendar.SECOND, 1);
			Date endTime = cal.getTime();
			String endStr = s.format(endTime);
			instance.lastNo = 1;
			instance.lastTime = endStr;
			return jointRule2(sourceId, endStr);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);

		}
	}
	
	public static String jointRule2(String sourceId,String now){
		String hostIP = HostMsgCache.getRandomIP4Part();
		String s=instance.baseNumber+1+"";
		String rs="";
		if(instance.baseNumber>2000000000){
			instance.baseNumber=0;
		}
		for(int i = 0;i<10-s.length();i++){
			rs=rs+"0";
		}
		rs=rs+s;
		String str = serialCat  + sourceId + now
				+ rs;
		instance.baseNumber++;
		return str; 
	}
	
	public static String jointRule1(String sourceId, String now) {
		String hostIP = HostMsgCache.getRandomIP4Part();
		String str = serialCat  + sourceId + now
				+ getSerialStringByNum(instance.baseNum, instance.lastNo) +hostIP;
		int len = serialLen1 - str.length();
		
		str = str +  generateRandomString(len);
		return str;
	}
	
	
	public static String jointRule(String sourceId, String now) {
		String hostIP = HostMsgCache.getRandomIP4Part();
		String str = serialCat  + sourceId + now
				+ getSerialStringByNum(instance.baseNum, instance.lastNo) +hostIP;
		int len = serialLen - str.length();
		
		str = str +  generateRandomString(len);
		return str;
	}
	public static String generateRandomString(int len) {
		final char[] mm = new char[] { '0', '1', '2', '3', '4', '5', '6', '7',
				'8', '9' };

		StringBuilder sb = new StringBuilder();
		Random random = new Random();

		for (int i = 0; i < len; i++) {
			sb.append(mm[random.nextInt(mm.length)]);
		}
		return sb.toString();

	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 999999; i++) {
			System.out.println(genSerialNos("999"));
		}
	}
}
