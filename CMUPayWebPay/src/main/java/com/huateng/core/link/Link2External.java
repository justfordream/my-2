package com.huateng.core.link;

import com.huateng.core.bean.ReplayData;

/**
 * 请求信息转发至外部机构
 * 
 * @author Gary
 * 
 */
public interface Link2External {
	/**
	 * 发送数据到外部
	 */
	public ReplayData transfer();

	/**
	 * 数据验证（省中心，银行和支付网关）
	 * 
	 * @param sign签名串
	 * @param object待签名对象
	 * @return
	 */
	public boolean validateInfo(String sign, Object object);
}
