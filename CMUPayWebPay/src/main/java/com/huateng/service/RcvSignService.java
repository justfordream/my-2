package com.huateng.service;

import com.huateng.bean.CmuData;
import com.huateng.core.exception.AppException;
import com.huateng.vo.MsgData;

/**
 * 签约信息业务处理接口
 * 
 * @author Gary
 * 
 */
public interface RcvSignService {

	/**
	 * 签约信息请求
	 * 
	 * @param json
	 *            签约信息对象
	 * @return
	 */
	public MsgData sendSignRequest(CmuData cmuData) throws AppException;
}
