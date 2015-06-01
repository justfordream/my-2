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
public class SeqIdSerial {
	// 最大数字
	private static final int MAX = 9999;
	// 数字长度
	private static final int NUMLEN = 4;

	private static SeqIdSerial instance = null;
	private String lastTime = "";
	private int lastNo = 0;
	private int baseNum = 10000;
	private static final int seqLen = 19;
	public SeqIdSerial() {

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
	public static synchronized String genSeqId(String sourceId) {
		Date curTime = new Date();
		SimpleDateFormat s = new SimpleDateFormat("yyMMddHHmmss");

		if (instance == null) {
			instance = new SeqIdSerial();
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
	public static String jointRule2(String sourceId, String now) {
		String hostIP = HostMsgCache.getRandomIP4Part();
		String str =  now
				+ getSerialStringByNum(instance.baseNum, instance.lastNo) +hostIP;
		int len = seqLen - str.length();
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
		Long value;
		for (int i = 0; i < 9999; i++) {
			value = Long.parseLong(genSeqId(""));
			System.out.println("Get SeqId=" + value);
		}
	}
}
