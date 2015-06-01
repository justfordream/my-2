package com.huateng.service;

import com.huateng.bean.CmuData;
import com.huateng.core.exception.AppException;
import com.huateng.vo.MsgData;

/**
 * 支付信息业务处理接口
 * 
 * @author Gary
 * 
 */
public interface RcvPayService {

	/**
	 * 支付信息请求
	 * 
	 * @param json
	 *            支付对象
	 * @return
	 */
	public MsgData sendPayRequest(CmuData cmuData) throws AppException;

}
