/**
 * 
 */
package com.huateng.core.channel;

import com.huateng.bean.CoreResultPayRes;
import com.huateng.core.exception.AppException;
import com.huateng.vo.MsgData;
import com.huateng.vo.TPayShopOpenData;
import com.huateng.vo.TpayData;

/**
 * @author Administrator
 * 
 */
public interface TpaySwitchChannel {
	/**
	 * 移动商城支付、缴费验签
	 * 
	 * @param cmuData
	 */
	public void validateTPayLink(TpayData tpayData);

	/**
	 * 移动商城发送支付、缴费请求
	 * 
	 * @param cmuData
	 * @param coreRsp
	 * @return
	 * @throws AppException
	 */
	public MsgData transferTPayLink(CoreResultPayRes coreRsp);

	/**
	 * 开通认证支付验签
	 * 
	 * @param tPayShopOpenData
	 */
	public void validateTPayShopOpenLink(TPayShopOpenData tPayShopOpenData);

	/**
	 * 移动商城发送支付、缴费请求
	 * 
	 * @param cmuData
	 * @param coreRsp
	 * @return
	 * @throws AppException
	 */
	public MsgData transferTPayShopOpenLink(CoreResultPayRes coreRsp,
			TPayShopOpenData tPayShopOpenData);

}
