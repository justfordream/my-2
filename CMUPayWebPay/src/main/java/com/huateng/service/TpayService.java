/**
 * 
 */
package com.huateng.service;

import com.huateng.core.exception.AppException;
import com.huateng.vo.MsgData;
import com.huateng.vo.TPayShopOpenData;
import com.huateng.vo.TpayData;

/**
 * 银联业务处理接口
 * 
 * @author zhaojunnan
 * 
 */
public interface TpayService {

	public MsgData shopTpayNotice(boolean isPage, TpayData tpayData)
			throws AppException;

	public MsgData payShopOpenNotice(boolean isPage,
			TPayShopOpenData tPayShopOpenData) throws AppException;

}
