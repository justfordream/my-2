package com.huateng.link;

import com.huateng.bean.CmuData;
import com.huateng.bean.ShopData;
import com.huateng.vo.BankData;
import com.huateng.vo.MsgData;

/**
 * 请求信息转发至外部机构 校验签名信息
 * 
 * @author 马博阳
 * 
 */
public abstract class Link2External extends BankData {

	public abstract MsgData transferSign(CmuData cmuData);

	public abstract MsgData transferPay(CmuData cmuData);

	public abstract MsgData transferShopPay(ShopData cmuData);

	public abstract MsgData transferPayShopOpen(ShopData cmuData);
}
