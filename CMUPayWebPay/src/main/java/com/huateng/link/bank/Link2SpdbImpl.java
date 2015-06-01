package com.huateng.link.bank;


import org.apache.log4j.Logger;

import com.huateng.bean.CmuData;
import com.huateng.bean.ShopData;
import com.huateng.link.Link2External;
import com.huateng.vo.MsgData;

/**
 * 浦发请求信息组装
 * 
 * @author Gary
 * 
 */
public class Link2SpdbImpl extends Link2External {

	private final static Logger logger = Logger.getLogger(Link2SpdbImpl.class);

	@Override
	public MsgData transferSign(CmuData cmuData) {
		logger.debug("发送支付请求到银行:[" + cmuData.getBankID() + "]");
		MsgData msgData = new MsgData();
		return msgData;
	}

	@Override
	public MsgData transferPay(CmuData cmuData) {
		logger.debug("发送支付请求到银行:[" + cmuData.getBankID() + "]");
		MsgData msgData = new MsgData();
		return msgData;
	}


	@Override
	public MsgData transferShopPay(ShopData cmuData) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public MsgData transferPayShopOpen(ShopData cmuData) {
		// TODO Auto-generated method stub
		return null;
	}
}
