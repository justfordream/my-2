package com.huateng.core.common;

import java.net.InetAddress;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class HostMsgCache {
	
	private static  String hostIP4Part = "1";
	private static  String hostIP = "1.0.0.0";
	
	//protected static final  Logger logger = LoggerFactory.getLogger(HostMsgCache.class);

	
	/**
	 * 初始化数据字典
	 * 
	 * @author cmt
	 */
	@PostConstruct
	private void init() {
		try {
			String addr = InetAddress.getLocalHost().getHostAddress();
			if(addr==null){
				hostIP = "1.0.0.0"	;
			}else {
				hostIP = addr;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("未知异常:"+e.getMessage());
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
