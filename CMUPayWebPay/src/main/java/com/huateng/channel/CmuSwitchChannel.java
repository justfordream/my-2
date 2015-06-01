package com.huateng.channel;

import com.huateng.bean.CmuData;
import com.huateng.bean.CoreResultRsp;
import com.huateng.bean.ShopData;
import com.huateng.core.exception.AppException;
import com.huateng.vo.MsgData;

public interface CmuSwitchChannel {
	/**
	 * 校验签约信息
	 * 
	 * @param signReq
	 */
	public void validateSignLink(CmuData cmuData) throws AppException;

	/**
	 * 校验支付信息
	 * 
	 * @param payReq
	 */
	public void validatePayLink(CmuData cmuData) throws AppException;

	/**
	 * 校验开通认证支付信息
	 * 
	 * @param payReq
	 */
	public void validatePayShopOpenLink(ShopData shopData) throws AppException;

	/**
	 * 发送签约信息
	 * 
	 * @param signReq
	 */
	public MsgData transferSignLink(CmuData cmuData) throws AppException;

	/**
	 * 发送认证支付信息
	 * 
	 * @param signReq
	 */
	public MsgData transferePayShopOpenLink(ShopData cmuData, CoreResultRsp coreRsp)
			throws AppException;

	/**
	 * 发送支付信息
	 * 
	 * @param payReq
	 */
	public MsgData transferPayLink(CmuData cmuData, CoreResultRsp coreRsp)
			throws AppException;

	public void validateShopPayLink(ShopData cmuData);

	/**
	 * 移动商城发送缴费请求
	 * 
	 * @param cmuData
	 * @param coreRsp
	 * @return
	 * @throws AppException
	 */
	public MsgData transferShopPayLink(ShopData cmuData, CoreResultRsp coreRsp)
			throws AppException;
}
