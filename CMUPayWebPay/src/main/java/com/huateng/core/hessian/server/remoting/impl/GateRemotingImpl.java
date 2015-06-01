package com.huateng.core.hessian.server.remoting.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.action.cmu.RcvPayCmuAction;
import com.huateng.bean.CoreResultPayRes;
import com.huateng.bean.CoreResultSignRes;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.hessian.server.remoting.GateRemoting;
import com.huateng.core.link.LingSignChannel;
import com.huateng.core.util.HttpClientUtil;
import com.huateng.core.util.JacksonUtil;
import com.huateng.core.util.RequestUtil;
import com.huateng.log.MessageLogger;

public class GateRemotingImpl implements GateRemoting {
	private Logger logger = LoggerFactory.getLogger(GateRemotingImpl.class);

	private MessageLogger upayLog = MessageLogger.getLogger(RcvPayCmuAction.class);
	
	private String checkSwitch;
	
	private String httTimeOut;
	
	
	public String getHttTimeOut() {
		return httTimeOut;
	}

	public void setHttTimeOut(String httTimeOut) {
		this.httTimeOut = httTimeOut;
	}

	public String getCheckSwitch() {
		return checkSwitch;
	}

	public void setCheckSwitch(String checkSwitch) {
		this.checkSwitch = checkSwitch;
	}

	@Override
	public String sendMsg(String header, String... args) {
		Map<String, String> params=null;
		String responseText = args[0];
		try {
			logger.info("支付网关监听到了,接收的核心信息 : {}", responseText);
			upayLog.debug("支付网关监听到了,接收的核心信息 : {}", responseText);
			if (StringUtils.equals(header, CoreConstant.PAY_TYPE)) {
				CoreResultPayRes payRes = JacksonUtil.strToBean(CoreResultPayRes.class, responseText);
				if (StringUtils.isNotBlank(payRes.getServerURL())) {
					if(StringUtils.equals(payRes.getBussChl(), CoreConstant.requsetChl.reqMobileShop.getCode())){
						params=this.initShopPayParams(payRes);
					}else{
						params = this.initPayParams(payRes);
					}
					String resMsg = HttpClientUtil.formSubmit(payRes.getServerURL(), params, CoreConstant.ENCODE,httTimeOut);
					logger.info("支付网关通知省份缴费完成{},订单号{}" , resMsg,payRes.getOrderID());
				} else {
					logger.info("支付网关通知省份缴费失败，订单号{}，{}",payRes.getOrderID(),responseText);
					upayLog.error("支付网关通知省份缴费失败，通知地址为空");
				}
			} else if (StringUtils.equals(header, CoreConstant.SIGN_TYPE)) {
				CoreResultSignRes signRes = JacksonUtil.strToBean(CoreResultSignRes.class, responseText);
				params = initSignParams(signRes);
				if (StringUtils.isNotBlank(signRes.getServerURL())) {
					String resMsg = HttpClientUtil.formSubmit(signRes.getServerURL(), params, CoreConstant.ENCODE,httTimeOut);
					logger.info("支付网关通知省份签约完成{},订单号{}" , resMsg,signRes.getSessionID());
				} else {
					logger.info("支付网关通知省份签约失败，通知地址为空{}",responseText);
					upayLog.error("支付网关通知省份缴费失败，通知地址为空{}",responseText);
				}
			}
			return "success";
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("省份的后台通知错误{}{}",responseText, ex.getMessage());
			upayLog.error("支付网关通知省份缴费失败{}",responseText);
			return "error";
		}
	}

	private Map<String, String> initSignParams(CoreResultSignRes coreRsp) {
		Map<String, String> params = new HashMap<String, String>();
		if (StringUtils.isNotBlank(coreRsp.getSessionID())) {
			params.put("SessionID", coreRsp.getSessionID());
		} else {
			params.put("SessionID", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getRspCode())) {
			params.put("RspCode", coreRsp.getRspCode());
		} else {
			params.put("RspCode", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getRspInfo())) {
			params.put("RspInfo", coreRsp.getRspInfo());
		} else {
			params.put("RspInfo", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getSubID())) {
			params.put("SubID", coreRsp.getSubID());
		} else {
			params.put("SubID", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getSubTime())) {
			params.put("SubTime", coreRsp.getSubTime());
		} else {
			params.put("SubTime", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getBankAcctID())) {
			params.put("BankAcctID", coreRsp.getBankAcctID());
		} else {
			params.put("BankAcctID", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getBankAcctType())) {
			params.put("BankAcctType", coreRsp.getBankAcctType());
		} else {
			params.put("BankAcctType", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getBankID())) {
			params.put("BankID", coreRsp.getBankID());
		} else {
			params.put("BankID", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getTransactionID())) {
			params.put("TransactionID", coreRsp.getTransactionID());
		} else {
			params.put("TransactionID", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getOrigDomain())) {
			params.put("OrigDomain", coreRsp.getOrigDomain());
		} else {
			params.put("OrigDomain", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getCLIENTIP())) {
			params.put("CLIENTIP", coreRsp.getCLIENTIP());
		} else {
			params.put("CLIENTIP", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getActionDate())) {
			params.put("ActionDate", coreRsp.getActionDate());
		} else {
			params.put("ActionDate", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getUserIDType())) {
			params.put("UserIDType", coreRsp.getUserIDType());
		} else {
			params.put("UserIDType", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getUserID())) {
			params.put("UserID", coreRsp.getUserID());
		} else {
			params.put("UserID", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getUserName())) {
			params.put("UserName", coreRsp.getUserName());
		} else {
			params.put("UserName", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getPayType())) {
			params.put("PayType", coreRsp.getPayType());
		} else {
			params.put("PayType", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getRechAmount())
				&& !"null".equals(coreRsp.getRechAmount())) {
			params.put("RechAmount", coreRsp.getRechAmount());
		} else {
			params.put("RechAmount", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getRechThreshold())
				&& !"null".equals(coreRsp.getRechThreshold())) {
			params.put("RechThreshold", coreRsp.getRechThreshold());
		} else {
			params.put("RechThreshold", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getUserCat())) {
			params.put("UserCat", coreRsp.getUserCat());
		} else {
			params.put("UserCat", "");
		}
		params.put("MCODE", "UPAY00002");
		params.put("Sig", new LingSignChannel().getSignKey(params,checkSwitch));
		return params;
	}

	public Map<String, String> initPayParams(CoreResultPayRes r) {
		Map<String, String> params = new HashMap<String, String>();
		if (StringUtils.isNotBlank(r.getMerID()))
			params.put("MerID", r.getMerID().trim());
		else {
			params.put("MerID", "");
		}
		if (StringUtils.isNotBlank(r.getOrderID()))
			params.put("OrderID", r.getOrderID().trim());
		else {
			params.put("OrderID", "");
		}
		if (StringUtils.isNotBlank(r.getRspCode()))
			params.put("RspCode", r.getRspCode().trim());
		else {
			params.put("RspCode", "");
		}
		if (StringUtils.isNotBlank(r.getRspInfo())) {
			params.put("RspInfo", r.getRspInfo().trim());
		} else {
			params.put("RspInfo", "");
		}
		if (StringUtils.isNotBlank(r.getMerVAR()))
			params.put("MerVAR", r.getMerVAR().trim());
		else {
			params.put("MerVAR", "");
		}
		if (StringUtils.isNotBlank(r.getOrderTime()))
			params.put("OrderTime", r.getOrderTime().trim());
		else {
			params.put("OrderTime", "");
		}
		if (StringUtils.isNotBlank(r.getPayed()))
			params.put("Payed", r.getPayed().trim());
		else {
			params.put("Payed", "");
		}
		if (StringUtils.isNotBlank(r.getCurType()))
			params.put("CurType", r.getCurType().trim());
		else {
			params.put("CurType", "");
		}
		if (StringUtils.isNotBlank(r.getMCODE()))
			params.put("MCODE", r.getMCODE().trim());
		else {
			params.put("MCODE", "");
		}
		if (StringUtils.isNotBlank(r.getServerURL()))
			params.put("ServerURL", r.getServerURL().trim());
		else {
			params.put("ServerURL", "");
		}
		if (StringUtils.isNotBlank(r.getBackURL()))
			params.put("BackURL", r.getBackURL().trim());
		else {
			params.put("BackURL", "");
		}
		String sig = new LingSignChannel().getPayKey(params,checkSwitch);
		params.put("Sig", RequestUtil.paseEncode(sig));
		return params;
	}
	
	/**
	 * 移动商城返回
	 * @param r
	 * @return
	 */
	public Map<String, String> initShopPayParams(CoreResultPayRes r) {
		Map<String, String> params = new HashMap<String, String>();
		if (StringUtils.isNotBlank(r.getOrderID()))
			params.put("OrderID", r.getOrderID().trim());
		else {
			params.put("OrderID", "");
		}
		if (StringUtils.isNotBlank(r.getRspCode()))
			params.put("RspCode", r.getRspCode().trim());
		else {
			params.put("RspCode", "");
		}
		if (StringUtils.isNotBlank(r.getRspInfo())) {
			params.put("RspInfo", r.getRspInfo().trim());
		} else {
			params.put("RspInfo", "");
		}
		if (StringUtils.isNotBlank(r.getMerVAR()))
			params.put("MerVAR", r.getMerVAR().trim());
		else {
			params.put("MerVAR", "");
		}
		if (StringUtils.isNotBlank(r.getOrderTime()))
			params.put("OriReqDate", r.getOrderTime().trim());
		else {
			params.put("OriReqDate", "");
		}
		String sig = new LingSignChannel().getPayKey(params,checkSwitch);
		params.put("Sig", RequestUtil.paseEncode(sig));
		return params;
	}

}
