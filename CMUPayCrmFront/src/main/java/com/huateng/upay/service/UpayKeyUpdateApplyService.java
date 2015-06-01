package com.huateng.upay.service;

/**
 * 申请密钥更新
 * 
 * @author qingxue.li
 * 
 */
public interface UpayKeyUpdateApplyService {

	/**
	 * 
	 * 申请更新密钥
	 * 1)组织请求报文
	 * 2)发送报文至网状网
	 * 3)解析响应报文，更新密钥以及记录密钥更新信息入库
	 * @param proposer
	 *        请求发起者 (管控台,网状网)
	 */
	public boolean applyKeyUpdate(String proposer);
}
