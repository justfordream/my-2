package com.huateng.tcp;

/**
 * 与核心交互接口
 * 
 * @author Gary
 * 
 */
public interface BaseTcp {

	/**
	 * 发送消息
	 * 
	 * @param message
	 *            要发送的信息类
	 * @return 返回收到的结果信息
	 */
	public String sendMessage(String message);

}
