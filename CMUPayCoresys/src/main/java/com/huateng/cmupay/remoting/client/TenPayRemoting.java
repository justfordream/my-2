/**
 * 
 */
package com.huateng.cmupay.remoting.client;

import java.util.Map;

/**
 * 支付结果通知查询接口
 * @author Manzhizhen
 *
 */
public interface TenPayRemoting {
	public Object sendMsg(String header, Map<String, String> paramsMap);
}
