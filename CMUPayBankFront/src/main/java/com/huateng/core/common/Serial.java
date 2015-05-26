package com.huateng.core.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.bank.common.HostMsgCache;




/**
 * @author cmt
 *
 */
public class Serial {
	 private final static Logger logger = LoggerFactory.getLogger("Serial");
	private static final Object serial = new Object();
	//最大数字
	private static final int MAX = 9999;
	//数字长度
	private static final int NUMLEN = 4;

	private static Serial instance = null;
	private String lastTime = "";
	private int lastNo = 0;
	private int baseNum = 10000;
	
	// 表示实时流水
	private static final int serialCat = 10;
	// 生成序列长度
	private static final int serialLen = 30;
	
	public Serial() {
		
	}
	
	private static String getSerialStringByNum(int base, int value) {
		String num = new Integer(base + value).toString();
		
		return num.substring(1);
	}
	
	/**
	 * 根据当前时间产生一个新的序列号
	 * @param sourceId
	 * @return
	 */
	public static String genSerialNo (String sourceId) {
		synchronized (serial) {
			Date curTime = new Date();
			SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
			
			if (instance == null) {
				instance = new Serial();
				instance.lastNo = 0;
				instance.baseNum = (int)Math.pow(10, NUMLEN);
				instance.lastTime = s.format(curTime);
			}
			
			String now = s.format(curTime);
			if (now.compareTo(instance.lastTime) > 0) {
				//当前时间大于上一次记录时间，表示可以开始新的计数
				instance.lastNo = 1;
				instance.lastTime = now;
				return sourceId + now + getSerialStringByNum(instance.baseNum, instance.lastNo);
			}
			
			if (now.compareTo(instance.lastTime) < 0) {
				//当前时间小于上一次的记录时间，表示上一秒有超过10000个流水号生成
				now = instance.lastTime;
			}
			
			int serialNo = instance.lastNo + 1;
			if (serialNo <= MAX) {
				//当前没有超过最大允许流水号，返回上一个流水号+1作为新的流水号
				instance.lastNo = serialNo;
				return sourceId + now + getSerialStringByNum(instance.baseNum, instance.lastNo);
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
				return sourceId + endStr + getSerialStringByNum(instance.baseNum, instance.lastNo);
			} catch (ParseException e) {
				//e.printStackTrace();
				logger.error("",e);
			}
			
			return null;
		}
	}
	
	
	
	//新增生成序列号方法
		/**
		 * 根据当前时间产生一个新的序列号
		 * 
		 * @param sourceId
		 * @return
		 */
		public static synchronized String generateSerialNo(String sourceId) {
			// synchronized (this) {
			Date curTime = new Date();
			SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");

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

			int serialNo = instance.lastNo + 1;
			if (serialNo <= MAX) {
				// 当前没有超过最大允许流水号，返回上一个流水号+1作为新的流水号
				instance.lastNo = serialNo;
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
				//e.printStackTrace();
				logger.error("", e);
				throw new RuntimeException(e);

			}
			
		}
		
		public static String jointRule(String sourceId, String now) {
			String hostIP = HostMsgCache.getHostIP4Part();
			String str = serialCat  + sourceId + now
					+ getSerialStringByNum(instance.baseNum, instance.lastNo) +hostIP;
			int len = serialLen - str.length();
			
			str = str +  generateRandomString(len);
			return str;
		}
		
		public static String generateRandomString(int len) {
			final char[] mm = new char[] { '0', '1', '2', '3', '4', '5', '6', '7',
					'8', '9' };

			StringBuffer sb = new StringBuffer();
			Random random = new Random();

			for (int i = 0; i < len; i++) {
				sb.append(mm[random.nextInt(mm.length)]);
			}
			return sb.toString();

		}
	
	
	
	public static void main(String[] args) {
		String value;
		for (int i = 0; i < 10000; i ++) {
			value = genSerialNo("60");
			System.out.println("Get SerialNo=" + value);
		}
	}
}
