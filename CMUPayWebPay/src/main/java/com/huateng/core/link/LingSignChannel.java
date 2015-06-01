package com.huateng.core.link;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.cmupay.service.RemoteService;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.util.ApplicationContextBean;

public class LingSignChannel {

	private static Logger logger = LoggerFactory.getLogger(LingSignChannel.class);

	private static RemoteService cmuSecurityRemoting;

	/**
	 * 进行签名
	 * 
	 * @param cmuId
	 * @param sigStr
	 * @return
	 */
	private String validateSigKey(String cmuId, String sigStr,String checkSwitch ) {
		logger.debug("...省份{}...返回给省份的签名字符串：" + sigStr, cmuId);
		String result = "";
		logger.debug("......签名开关......" + checkSwitch);
		if (cmuSecurityRemoting == null) {
			cmuSecurityRemoting = (RemoteService) ApplicationContextBean.getBean("cmuSecurityRemoting");
		}
		if (CoreConstant.CHECK_STATUS.equals(checkSwitch)) {
			result = cmuSecurityRemoting.sign(cmuId, sigStr);
		}
		logger.debug("......签约签名结果：" + result);
		return result;
	}

	/**
	 * 签约签名
	 * 
	 * @param formParams
	 * @param cmuData
	 * @return
	 */
	public  String getSignKey(Map<String, String> formParams,String checkSwitch) {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotBlank(formParams.get("SessionID"))) {
			sb.append("SESSIONID=" + formParams.get("SessionID") + "|");
		} else {
			sb.append("SESSIONID=|");
		}
		if (StringUtils.isNotBlank(formParams.get("RspCode"))) {
			sb.append("RSPCODE=" + formParams.get("RspCode") + "|");
		} else {
			sb.append("RSPCODE=|");
		}
		if (StringUtils.isNotBlank(formParams.get("RspInfo"))) {
			sb.append("RSPINFO=" + formParams.get("RspInfo") + "|");
		} else {
			sb.append("RSPINFO=|");
		}
		if (StringUtils.isNotBlank(formParams.get("SubID"))) {
			sb.append("SUBID=" + formParams.get("SubID") + "|");
		} else {
			sb.append("SUBID=|");
		}
		if (StringUtils.isNotBlank(formParams.get("SubTime"))) {
			sb.append("SUBTIME=" + formParams.get("SubTime") + "|");
		} else {
			sb.append("SUBTIME=|");
		}
		if (StringUtils.isNotBlank(formParams.get("BankAcctID"))) {
			sb.append("BANKACCTID=" + formParams.get("BankAcctID") + "|");
		} else {
			sb.append("BANKACCTID=|");
		}
		if (StringUtils.isNotBlank(formParams.get("BankAcctType"))) {
			sb.append("BANKACCTTYPE=" + formParams.get("BankAcctType") + "|");
		} else {
			sb.append("BANKACCTTYPE=|");
		}
		if (StringUtils.isNotBlank(formParams.get("BankID"))) {
			sb.append("BANKID=" + formParams.get("BankID") + "|");
		} else {
			sb.append("BANKID=|");
		}
		if (StringUtils.isNotBlank(formParams.get("TransactionID"))) {
			sb.append("TRANSACTIONID=" + formParams.get("TransactionID") + "|");
		} else {
			sb.append("TRANSACTIONID=|");
		}
		if (StringUtils.isNotBlank(formParams.get("OrigDomain"))) {
			sb.append("ORIGDOMAIN=" + formParams.get("OrigDomain") + "|");
		} else {
			sb.append("ORIGDOMAIN=|");
		}
		if (StringUtils.isNotBlank(formParams.get("CLIENTIP"))) {
			sb.append("CLIENTIP=" + formParams.get("CLIENTIP") + "|");
		} else {
			sb.append("CLIENTIP=|");
		}
		if (StringUtils.isNotBlank(formParams.get("ActionDate"))) {
			sb.append("ACTIONDATE=" + formParams.get("ActionDate") + "|");
		} else {
			sb.append("ACTIONDATE=|");
		}
		if (StringUtils.isNotBlank(formParams.get("UserIDType"))) {
			sb.append("USERIDTYPE=" + formParams.get("UserIDType") + "|");
		} else {
			sb.append("USERIDTYPE=|");
		}
		if (StringUtils.isNotBlank(formParams.get("UserID"))) {
			sb.append("USERID=" + formParams.get("UserID") + "|");
		} else {
			sb.append("USERID=|");
		}
		if (StringUtils.isNotBlank(formParams.get("UserName"))) {
			sb.append("USERNAME=" + formParams.get("UserName") + "|");
		} else {
			sb.append("USERNAME=|");
		}
		if (StringUtils.isNotBlank(formParams.get("PayType"))) {
			sb.append("PAYTYPE=" + formParams.get("PayType") + "|");
		} else {
			sb.append("PAYTYPE=|");
		}
		if (StringUtils.isNotBlank(formParams.get("RechAmount"))
				&& !"null".equals(formParams.get("RechAmount"))) {
			sb.append("RECHAMOUNT=" + formParams.get("RechAmount") + "|");
		} else {
			sb.append("RECHAMOUNT=|");
		}
		if (StringUtils.isNotBlank(formParams.get("RechThreshold"))
				&& !"null".equals(formParams.get("RechThreshold"))) {
			sb.append("RECHTHRESHOLD=" + formParams.get("RechThreshold") + "|");
		} else {
			sb.append("RECHTHRESHOLD=|");
		}
		if (StringUtils.isNotBlank(formParams.get("UserCat"))) {
			sb.append("USERCAT=" + formParams.get("UserCat") + "|");
		} else {
			sb.append("USERCAT=|");
		}
		if (StringUtils.isNotBlank(formParams.get("MCODE"))) {
			sb.append("MCODE=" + formParams.get("MCODE"));
		} else {
			sb.append("MCODE=");
		}
		return this.validateSigKey(null, sb.toString(),checkSwitch);
	}

	/**
	 * 缴费签名
	 * 
	 * @param formParams
	 * @return
	 */
	public String getPayKey(Map<String, String> formParams,String checkSwitch) {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotBlank(formParams.get("MerID"))) {
			sb.append("MERID=" + formParams.get("MerID") + "|");
		} else {
			sb.append("MERID=|");
		}
		if (StringUtils.isNotBlank(formParams.get("OrderID"))) {
			sb.append("ORDERID=" + formParams.get("OrderID") + "|");
		} else {
			sb.append("ORDERID=|");
		}
		if (StringUtils.isNotBlank(formParams.get("RspCode"))) {
			sb.append("RSPCODE=" + formParams.get("RspCode") + "|");
		} else {
			sb.append("RSPCODE=|");
		}
		if (StringUtils.isNotBlank(formParams.get("RspInfo"))) {
			sb.append("RSPINFO=" + formParams.get("RspInfo") + "|");
		} else {
			sb.append("RSPINFO=|");
		}
		if (StringUtils.isNotBlank(formParams.get("MerVAR"))) {
			sb.append("MERVAR=" + formParams.get("MerVAR") + "|");
		} else {
			sb.append("MERVAR=|");
		}
		if (StringUtils.isNotBlank(formParams.get("OrderTime"))) {
			sb.append("ORDERTIME=" + formParams.get("OrderTime") + "|");
		} else {
			sb.append("ORDERTIME=|");
		}
		if (StringUtils.isNotBlank(formParams.get("Payed"))) {
			sb.append("PAYED=" + formParams.get("Payed") + "|");
		} else {
			sb.append("PAYED=|");
		}
		if (StringUtils.isNotBlank(formParams.get("CurType"))) {
			sb.append("CURTYPE=" + formParams.get("CurType") + "|");
		} else {
			sb.append("CURTYPE=|");
		}
		if (StringUtils.isNotBlank(formParams.get("MCODE"))) {
			sb.append("MCODE=" + formParams.get("MCODE"));
		} else {
			sb.append("MCODE=");
		}
		return this.validateSigKey(null, sb.toString(),checkSwitch);
	}
	
	/**
	 * 缴费签名
	 * 
	 * @param formParams
	 * @return
	 */
	public String getShopPayKey(Map<String, String> formParams,String checkSwitch) {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotBlank(formParams.get("OrderID"))) {
			sb.append("ORDERID=" + formParams.get("OrderID") + "|");
		} else {
			sb.append("ORDERID=|");
		}
		if (StringUtils.isNotBlank(formParams.get("RspCode"))) {
			sb.append("RSPCODE=" + formParams.get("RspCode") + "|");
		} else {
			sb.append("RSPCODE=|");
		}
		if (StringUtils.isNotBlank(formParams.get("RspInfo"))) {
			sb.append("RSPINFO=" + formParams.get("RspInfo") + "|");
		} else {
			sb.append("RSPINFO=|");
		}
		if (StringUtils.isNotBlank(formParams.get("MerVAR"))) {
			sb.append("MERVAR=" + formParams.get("MerVAR"));
		} else {
			sb.append("MERVAR=");
		}
		return this.validateSigKey(null, sb.toString(),checkSwitch);
	}
	
}
