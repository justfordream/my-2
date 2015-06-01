/**
 * 
 */
package com.huateng.cmupay.controller.cache;

import java.net.InetAddress;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * 路由表索引类
 * 
 * @author cmt
 * 
 */

@Component
public class HostMsgCache {
	
	private static  final Logger logger = LoggerFactory.getLogger(HostMsgCache.class);
	
	private static  String hostIP4Part = "1";
	private static  String hostIP = "1.0.0.0";
	//此处是防止重复的关键， 如果启动服务的时候，同一个应用的任何两个启动随机数有相同的， 本应用应该重新启动。
	//防止单机部署多应用产生重复流水的情况。
	private static  int initRandom = 0;

	/**
	 * 初始化数据字典
	 * 
	 * @author cmt
	 */
	@PostConstruct
	private void init() {
		try {
			String addr = InetAddress.getLocalHost().getHostAddress();
			System.out.println("本机ip地址是："+addr);
			logger.info("本机ip地址是："+ addr);
			if(addr==null){
				hostIP = "1.0.0.0"	;
			}
			else {
				hostIP = addr;
			}
			 hostIP4Part  = hostIP.split("\\.")[3];
			 if(hostIP4Part.length()<3){
				 initRandom = Integer.parseInt(hostIP4Part + generateRandomString(3-hostIP4Part.length())); 
			 }else{
				 initRandom = Integer.parseInt(generateRandomString(3));
			 }
			 
			
			System.out.println("初始启动随机数是："+ initRandom + "，此处是防止重复的关键， 如果启动服务的时候，同一个应用的任何两个启动随机数有相同的， 本应用应该重新启动。");
			logger.info("初始启动随机数是："+ initRandom + "，此处是防止重复的关键， 如果启动服务的时候，同一个应用的任何两个启动随机数有相同的， 本应用应该重新启动。");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("未知异常:"+e.getMessage());
		}
		
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
	
	
	/**
	 * 重载数据字典
	 * 
	 * @author cmt
	 */
	public void reLoad() {
		init();
	}

	/**
	 * @return
	 */
	public static String getHostIP4Part() {
		
		// hostIP4Part  = hostIP.split("\\.")[3];
		 
		 return hostIP4Part;
	}
	
	/**
	 * @return
	 */
	public static String getRandomIP4Part() {
		
		 return String.valueOf(initRandom);
		 
	}
	
	public static String getHostIP() {
		
		 
		 return hostIP;
	}

	public static void main(String[] args) {
		HostMsgCache cache =new HostMsgCache();
		cache.init();
	}

}
