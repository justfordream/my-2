package com.huateng.core.channel;

/**
 * 核心平台联通通道
 * 
 * @author Gary
 * 
 */
public interface CoreSwitchChannel {
	/**
	 * 发送JSON格式数据给核心
	 * 
	 * @param json
	 * @return
	 */
	public String sendJsonMsg(String json);
}
