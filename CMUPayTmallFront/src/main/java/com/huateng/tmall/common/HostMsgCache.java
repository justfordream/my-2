/**
 * 
 */
package com.huateng.tmall.common;

import java.net.InetAddress;

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
	private static Logger logger = LoggerFactory.getLogger("HostMsgCache");	
	private static  String hostIP4Part = "1";
	private static  String hostIP = "1.0.0.0";
	

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
			
			if(addr==null){
				hostIP = "1.0.0.0"	;
			}
			else {
				hostIP = addr;
			}
			
		} catch (Exception e) {
			logger.error("",e);
		}
		
	}
	
	
	public static void main(String[] args) {
		HostMsgCache cache =new HostMsgCache();
		cache.init();
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
		
		 hostIP4Part  = hostIP.split("\\.")[3];
		 
		 return hostIP4Part;
	}
	
	public static String getHostIP() {
		
		 
		 return hostIP;
	}

	

}
