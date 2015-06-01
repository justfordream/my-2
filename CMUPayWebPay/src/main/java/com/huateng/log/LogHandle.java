package com.huateng.log;

/**
 * 全网监控日志接口
 * 
 * @author Gary
 * 
 */
public interface LogHandle {
	/**
	 * 业务信息日志
	 * 
	 * @param isClient
	 *            当前系统为发起方：true 当前系统为接收方：false
	 * @param code
	 *            应答码
	 * @param tradeCode
	 *            交易码
	 * @param operType
	 *            接口描述
	 * @param provCode
	 *            省编码
	 * @param otherSystem
	 *            对方交换系统编码
	 * @param desc
	 *            日志描述
	 */
	public void info(boolean isClient, String code, String tradeCode, String operType, String provCode,
			String otherSystem, String desc);
}
