package com.huateng.link.cmu;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.huateng.bean.CmuData;
import com.huateng.bean.ShopData;
import com.huateng.link.Link2External;
import com.huateng.utils.WebBackToMer;
import com.huateng.vo.MsgData;

public class Link2ShenZhenImpl extends Link2External {
	private final static Logger logger = Logger.getLogger(Link2ShenZhenImpl.class);

	@Override
	public MsgData transferSign(CmuData cmuData) {
		logger.debug("发送签约请求到商户:[" + cmuData.getMerID() + "]");
		String BackURL = cmuData.getBackURL();
		String ServerURL = cmuData.getServerURL();
		MsgData msgData = new MsgData();
		Map<String, String> params = new HashMap<String, String>();
		String redirectHtml = WebBackToMer.getBackResult(params, BackURL, false);
		msgData.setRedirectHtml(redirectHtml);
		String noticeBackHtml = WebBackToMer.getBackResult(params, ServerURL, false);
		msgData.setNoticeBackHtml(noticeBackHtml);
		return msgData;
	}

	@Override
	public MsgData transferPay(CmuData cmuData) {
		logger.debug("发送支付请求到商户:[" + cmuData.getMerID() + "]");
		MsgData msgData = new MsgData();
		String BackURL = cmuData.getBackURL();
		String ServerURL = cmuData.getServerURL();
		Map<String, String> params = new HashMap<String, String>();
		String redirectHtml = WebBackToMer.getBackResult(params, BackURL, false);
		msgData.setRedirectHtml(redirectHtml);
		String noticeBackHtml = WebBackToMer.getBackResult(params, ServerURL, false);
		msgData.setNoticeBackHtml(noticeBackHtml);
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
