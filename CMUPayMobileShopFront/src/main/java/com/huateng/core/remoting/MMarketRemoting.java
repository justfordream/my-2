package com.huateng.core.remoting;

/**
 * 移动商城前置提供Hessian接口
 * @author Manzhizhen
 *
 */
public interface MMarketRemoting {
	public String sendMsg(String marketUrl, String ...args);
}
