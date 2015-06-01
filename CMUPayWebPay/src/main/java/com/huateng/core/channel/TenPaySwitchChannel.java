package com.huateng.core.channel;

import com.huateng.bean.CoreResultPayRes;
import com.huateng.bean.TenData;
import com.huateng.core.exception.AppException;
import com.huateng.vo.MsgData;

public interface TenPaySwitchChannel {

	/**
	 * 移动商城支付、缴费验签
	 * 
	 * @param cmuData
	 */
	public void validateTenPayLink(TenData tenData);

	/**
	 * 移动商城发送支付、缴费请求
	 * 
	 * @param cmuData
	 * @param coreRsp
	 * @return
	 * @throws AppException
	 */
	public MsgData transferTenPayLink(CoreResultPayRes coreRsp);

}
