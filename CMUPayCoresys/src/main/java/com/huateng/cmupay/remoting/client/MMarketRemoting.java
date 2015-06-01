package com.huateng.cmupay.remoting.client;

/**
 * 移动商城前置提供Hessian接口
 * @author Manzhizhen
 *
 */
public interface MMarketRemoting {

	public String sendMsg(String marketUrl, String ...args);

}
