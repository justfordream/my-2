package com.huateng.link.bank;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.bean.CmuData;
import com.huateng.bean.ShopData;
import com.huateng.core.common.CommonFunction;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.util.MD5Util;
import com.huateng.link.Link2External;
import com.huateng.utils.AmountUtil;
import com.huateng.utils.WebBackToMer;
import com.huateng.vo.MsgData;

/**
 * 建设银行数据传输类
 * 
 * @author Gary
 * 
 */
public class Link2TestImpl extends Link2External {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private  final String PUB="000000";
	
	@Override
	public MsgData transferSign(CmuData cmuData) {
		logger.debug("......向到银行:[" + cmuData.getBankID() + "][" + this.sendUrl
				+ "]发送签约请求");
		MsgData msgData = new MsgData();

		HashMap<String, String> newMap = new LinkedHashMap<String, String>();
		newMap.put("CCB_IBSVersion", "V5");
		newMap.put("TXCODE", "520280");
		newMap.put("MERCHANTID", this.getMerId());
		newMap.put("POSID", this.getPosId());
		newMap.put("BRANCHID", this.getBranchId());
		newMap.put("ORDERID", cmuData.getSessionID());
		newMap.put("PAYMENT", "");
		newMap.put("BEGGINGNO", cmuData.getIDValue());
		newMap.put("AREA", CommonFunction.getProvCodeBySysCode(cmuData.getCmuID()));
		newMap.put("PAYACC", "");
		newMap.put("PAYTYPE", cmuData.getPayType());// TODO 缴费方式(0-主动缴费1-自动缴费(即自动+主动))
		newMap.put("CURCODE", "01");
		newMap.put("CLIENTIP", cmuData.getCLIENTIP());
		newMap.put("REMARK1", "");
		newMap.put("REMARK2", cmuData.getSessionID());
		newMap.put("GATEWAY", "W2Z1");
		newMap.put("CUSTTYPE", "0");// TODO 用户类型(1-预付费2-后付费)
		newMap.put("PUB", PUB);
		newMap.put("MAC", this.signMac(newMap));
		if (CoreConstant.PayType.AUTO.getCode().equals("1")) {
			newMap.put("LIMVAL", cmuData.getRechThreshold());// 充值阈值
			newMap.put("PAYAMT", cmuData.getRechAmount());// 充值额度
		}
		newMap.put("BACKURL", cmuData.getBackURL());
		newMap.remove("PUB");
		String redirectHtml = WebBackToMer.getBackResult(newMap, this.sendUrl,
				false);
		msgData.setRedirectHtml(redirectHtml);
		return msgData;
	}

	@Override
	public MsgData transferPay(CmuData cmuData) {
		logger.debug("......向到银行:[" + cmuData.getBankID() + "][" + this.sendUrl
				+ "]发送支付请求");
		MsgData msgData = new MsgData();
		HashMap<String, String> newMap = new LinkedHashMap<String, String>();
		newMap.put("CCB_IBSVersion", "V5");
		newMap.put("TXCODE", "520290");
		newMap.put("MERCHANTID", this.getMerId());
		newMap.put("POSID", this.getPosId());
		newMap.put("BRANCHID", this.getBranchId());
		newMap.put("ORDERID", cmuData.getOrderID());
		newMap.put("PAYMENT", AmountUtil.fromFenToYuan(cmuData.getPayed())); // 分转换成元
		newMap.put("BEGGINGNO", cmuData.getIDValue());
		newMap.put("AREA", CommonFunction.getProvCodeBySysCode(cmuData.getCmuID()));
		newMap.put("PAYACC", "");
		newMap.put("CURCODE", cmuData.getCurType());
		newMap.put("CLIENTIP", cmuData.getCLIENTIP());
		newMap.put("REMARK1", "00");
		newMap.put("REMARK2", cmuData.getMerVAR());
		newMap.put("GATEWAY", "W2Z1");
		newMap.put("CUSTTYPE", "");
		newMap.put("PUB", PUB);
		newMap.put("MAC", this.payMac(newMap));
		newMap.put("BACKURL", cmuData.getBackURL());
		newMap.remove("PUB");
		String redirectHtml = WebBackToMer.getBackResult(newMap, this.sendUrl,
				false);
		msgData.setRedirectHtml(redirectHtml);
		return msgData;
	}

	private String payMac(Map<String, String> params) {
		StringBuffer sb = new StringBuffer();
		Set<Entry<String, String>> entrySet = params.entrySet();
		for (Entry<String, String> entry : entrySet) {
			String key = entry.getKey();
			String value = entry.getValue() == null ? "" : entry.getValue();
			if ("CCB_IBSVersion".equals(key)) {
				continue;
			}
			sb.append(key + "=" + value + "&");
		}
		String mac = sb.toString();
		mac = mac.substring(0, mac.lastIndexOf("&"));
		logger.debug("......MD5原字符串:" + mac);
		return MD5Util.getMD5ofStrByLowerCase(mac);
	}

	private String signMac(Map<String, String> params) {
		StringBuffer sb = new StringBuffer();
		Set<Entry<String, String>> entrySet = params.entrySet();
		for (Entry<String, String> entry : entrySet) {
			String key = entry.getKey();
			String value = entry.getValue() == null ? "" : entry.getValue();
			if ("CCB_IBSVersion".equals(key)) {
				continue;
			}
			sb.append(key + "=" + value + "&");
		}
		String mac = sb.toString();
		mac = mac.substring(0, mac.lastIndexOf("&"));
		logger.debug("......MD5原字符串:" + mac);
		return MD5Util.getMD5ofStrByLowerCase(mac);
	}
	
	@Override
	public MsgData transferShopPay(ShopData cmuData) {
		logger.debug("......向到银行:[" + cmuData.getBankID() + "][" + this.sendUrl
				+ "]发送支付请求");
		MsgData msgData = new MsgData();
		
		HashMap<String, String> newMap = new LinkedHashMap<String, String>();
		newMap.put("CCB_IBSVersion", "V5");
		newMap.put("TXCODE", "520290");
		newMap.put("MERCHANTID", this.getMerId());
		newMap.put("POSID", this.getPosId());
		newMap.put("BRANCHID", this.getBranchId());
		newMap.put("ORDERID", cmuData.getOrderID());
		newMap.put("PAYMENT", AmountUtil.fromFenToYuan(cmuData.getPayment())); // 分转换成元
		newMap.put("BEGGINGNO", cmuData.getIDValue());
		newMap.put("AREA", cmuData.getHomeProv());
		newMap.put("PAYACC", "");
		newMap.put("CURCODE", cmuData.getCurType());
		newMap.put("CLIENTIP", cmuData.getClientIp());
		newMap.put("REMARK1", "01");
		newMap.put("REMARK2", cmuData.getMerVar());
		newMap.put("GATEWAY", "W2Z1");
		newMap.put("CUSTTYPE", "");
		newMap.put("PUB", PUB);
		newMap.put("MAC", this.payMac(newMap));
		newMap.put("BACKURL", cmuData.getBackURL());
		newMap.remove("PUB");
		String redirectHtml = WebBackToMer.getBackResult(newMap, this.sendUrl,
				false);
		msgData.setRedirectHtml(redirectHtml);
		return msgData;
	}
	
	@Override
	public MsgData transferPayShopOpen(ShopData cmuData) {
		// TODO Auto-generated method stub
		return null;
	}
}
