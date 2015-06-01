package com.huateng.service;

import com.huateng.bean.ShopData;
import com.huateng.core.exception.AppException;
import com.huateng.vo.MsgData;

public interface RcvShopPayService {

	/**
	 * 支付信息请求
	 * 
	 * @param json
	 *            支付对象
	 * @return
	 */
	public MsgData sendPayRequest(ShopData cmuData) throws AppException;
}
