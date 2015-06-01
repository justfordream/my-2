package com.huateng.core.remoting;

/**
 * 天猫应急接口
 */
public interface CrmTmallRemoting {
	
	public String sendMsg(String header, String... args);
}