package com.huateng.core.common;

import java.util.ResourceBundle;

/**
 * 
 * @author Gary
 * 
 */
public class CoreCommon {
	private static ResourceBundle bundle = ResourceBundle.getBundle("jms");

	/**
	 * 获取验签开关(open:开,close:关)
	 * 
	 * @return
	 */
	public static String getCheckSwitch() {
		return bundle.getString("check.switch");
	}

	/**
	 * 获取银行端加解密开关(open:开,close:关)
	 * 
	 * @return
	 */
	public static String getEncryptSwitch() {
		return bundle.getString("encrypt.status");
	}
	
	/**
	 * 获取验签开关(open:开,close:关)
	 * 
	 * @return
	 */
	public static String getEncryptInfo() {
		return bundle.getString("encrypt.tradecode");
	}

	/**
	 * 获取签名开关(open:开,close:关)
	 * 
	 * @return
	 */
	public static String getSignSwitch() {
		return bundle.getString("sign.switch");
	}
	/**
	 * 连接超时时间
	 * 
	 * @return
	 */
	public static String getConnectionTimeOut() {
		return bundle.getString("http.conn.timeout");
	}
	
	/**
	 * 接收响应超时时间
	 * 
	 * @return
	 */
	public static String getReceiveTimeOut() {
		return bundle.getString("http.rev.timeout");
	}
}
