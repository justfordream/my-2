package com.huateng.channel.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.bean.CCBData;
import com.huateng.bean.CoreResultPayRes;
import com.huateng.bean.CoreResultSignRes;
import com.huateng.channel.CCBSwitchChannel;
import com.huateng.cmupay.service.RemoteService;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.link.LingSignChannel;
import com.huateng.core.util.RequestUtil;
import com.huateng.core.util.SignatureVerification;
import com.huateng.log.MessageLogger;
import com.huateng.utils.AmountUtil;
import com.huateng.utils.WebBackToMer;
import com.huateng.vo.MsgData;

public class CCBSwitchChannelImpl implements CCBSwitchChannel {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private MessageLogger upayLog=MessageLogger.getLogger(CCBSwitchChannelImpl.class);
	@Autowired
	private RemoteService bankSecurityRemoting;
	
	@Value("${BANK_CHECK_STATUS}")
	private String bankCheckSwitch;
	
	@Value("${BANK_OPEN_LIST}")
	private  String bankOpenList;
	
	@Value("${CHECK_STATUS}")
	private String checkSwitch;
	/**
	 * 签约验签
	 */
	public void validateSignLink(CCBData ccbData) {
		
		if (StringUtils.equals(ccbData.getStatus(),CoreConstant.BankErrorCode.SUCCESS)) {
			String plainText = ccbData.assemlyPlainText();
			logger.info("..开始签约验签...{}",plainText);
			boolean result = true;
			logger.debug("......验签开关......{}", bankCheckSwitch);
			if (CoreConstant.CHECK_STATUS.equals(bankCheckSwitch) && this.isCheckProBank(CoreConstant.BankCode.CCB.getCode())) {
				logger.debug("开始验签[{}],[{}],[{}]",new Object[]{plainText,ccbData.getSign(),bankSecurityRemoting.getBackCCBPayKey()});
				result=SignatureVerification.verifySign(plainText, ccbData.getSign(), bankSecurityRemoting.getBackCCBPayKey());
			}
			if (!result) {
				logger.debug("......验签失败,原串[{}],签名串[{}]......",plainText,ccbData.getSign());
				upayLog.error("验签失败,原串[{}]",plainText);
				ccbData.setStatus(CoreConstant.BankErrorCode.FAILED);
			} else {
				logger.debug("......验签成功......");
			}
		}
	}

	/**
	 * 缴费验签
	 */
	public void validatePayLink(CCBData ccbData) {
		
		if (StringUtils.equals(ccbData.getStatus(),
				CoreConstant.BankErrorCode.SUCCESS)) {
			String plainText = ccbData.assemlyPlainText();
			logger.debug("......开始缴费验签...{}...",plainText);
			boolean result = true;
			logger.debug("......验签开关......{}" , bankCheckSwitch);
			if (CoreConstant.CHECK_STATUS.equals(bankCheckSwitch) && this.isCheckProBank(CoreConstant.BankCode.CCB.getCode())) {
                logger.debug("开始验签=====[{}],[{}],[{}]",new Object[]{plainText,ccbData.getSign(),bankSecurityRemoting.getBackCCBPayKey()});
				result=SignatureVerification.verifySign(plainText, ccbData.getSign(), bankSecurityRemoting.getBackCCBPayKey());
			}
			if (!result) {
				ccbData.setStatus(CoreConstant.BankErrorCode.FAILED);
				logger.error("......验签失败,原串[{}],签名串[{}]......",plainText,ccbData.getSign());
				upayLog.error("验签失败,原串[{}]",plainText);
			} else {
				logger.debug("......验签成功......");
			}
		}
	}

	/**
	 * 发送信息
	 */
	public MsgData transferSignLink(CoreResultSignRes coreRsp) {
		MsgData msgData = new MsgData();
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
			params.put("BankAcctID", AmountUtil.formatCodeString(coreRsp.getBankAcctID()));
		} else {
			params.put("BankAcctID", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getBankAcctType())) {
			params.put("BankAcctType", coreRsp.getBankAcctType());
		} else {
			params.put("BankAcctType", "");
		}
		params.put("BankID", "0004");
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
			params.put("UserID", AmountUtil.formatCodeString(coreRsp.getUserID()));
		} else {
			params.put("UserID", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getUserName())) {
			params.put("UserName", AmountUtil.formatNameString(coreRsp.getUserName()));
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
				&& !"null".equals(coreRsp.getRechAmount())) {
			params.put("RechThreshold", coreRsp.getRechThreshold());
		} else {
			params.put("RechThreshold", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getUserCat())) {
			params.put("UserCat", coreRsp.getUserCat());
		} else {
			params.put("UserCat", "");
		}
		params.put("MCODE", CoreConstant.TRANS_SIGN_CODE);
		String sig = new LingSignChannel().getSignKey(params,checkSwitch);
		params.put("Sig", RequestUtil.paseEncode(sig));
		String toHtml = WebBackToMer.getBackResult(params,
				coreRsp.getBackURL(), true);
		msgData.setRedirectHtml(toHtml);
		msgData.setUrl(coreRsp.getServerURL());
		msgData.setParams(params);
		return msgData;
	}

	public MsgData transferPayLink(CoreResultPayRes coreRsp) {
		MsgData msgData = new MsgData();
		Map<String, String> params = new HashMap<String, String>();
		if (StringUtils.isNotBlank(coreRsp.getMerID())) {
			params.put("MerID", coreRsp.getMerID().trim());
		} else {
			params.put("MerID", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getOrderID())) {
			params.put("OrderID", coreRsp.getOrderID().trim());
		} else {
			params.put("OrderID", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getRspCode()))
			params.put("RspCode", coreRsp.getRspCode().trim());
		else {
			params.put("RspCode", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getRspInfo()))
			params.put("RspInfo", coreRsp.getRspInfo().trim());
		else {
			params.put("RspInfo", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getMerVAR()))
			params.put("MerVAR", coreRsp.getMerVAR().trim());
		else {
			params.put("MerVAR", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getOrderTime()))
			params.put("OrderTime", coreRsp.getOrderTime().trim());
		else {
			params.put("OrderTime", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getPayed()))
			params.put("Payed", coreRsp.getPayed().trim());
		else {
			params.put("Payed", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getCurType()))
			params.put("CurType", coreRsp.getCurType().trim());
		else {
			params.put("CurType", "");
		}
		params.put("MCODE", CoreConstant.TRANS_PAY_CODE);
		params.put("Sig", RequestUtil.paseEncode(new LingSignChannel().getPayKey(params,checkSwitch)));
		String toHtml = WebBackToMer.getBackResult(params,coreRsp.getBackURL(), true);
		msgData.setRedirectHtml(toHtml);
		msgData.setUrl(coreRsp.getServerURL());
		msgData.setParams(params);
		return msgData;
	}
	
	/**
	 * 移动商城返回结果
	 * @param coreRsp
	 * @return
	 */
	public MsgData transferShopPayLink(CoreResultPayRes coreRsp) {
		MsgData msgData = new MsgData();
		Map<String, String> params = new HashMap<String, String>();
		if (StringUtils.isNotBlank(coreRsp.getOrderID())) {
			params.put("OrderID", coreRsp.getOrderID().trim());
		} else {
			params.put("OrderID", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getRspCode()))
			params.put("RspCode", coreRsp.getRspCode().trim());
		else {
			params.put("RspCode", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getRspInfo()))
			params.put("RspInfo", coreRsp.getRspInfo().trim());
		else {
			params.put("RspInfo", "");
		}
		if (StringUtils.isNotBlank(coreRsp.getMerVAR()))
			params.put("MerVAR", coreRsp.getMerVAR().trim());
		else {
			params.put("MerVAR", "");
		}
		params.put("Sig", RequestUtil.paseEncode(new LingSignChannel().getShopPayKey(params,checkSwitch)));
		String toHtml = WebBackToMer.getBackResult(params,coreRsp.getBackURL(), true);
		msgData.setRedirectHtml(toHtml);
		msgData.setUrl(coreRsp.getServerURL());
		msgData.setParams(params);
		return msgData;
	}
	
	/**
	 * 
	 * 根据省份判断是否加密、解密 (all 全部打开,部分打开 4位省份代码用逗号隔开)
	 * 
	 * @return true 需要加密
	 */
	public  boolean isCheckProBank(String sysProCode) {
		String proBanks = bankOpenList;
		if (StringUtils.equals(proBanks, CoreConstant.ALL)) {
			return true;
		} else {
			List<String> proBankList = new ArrayList<String>();
			String[] proBank = proBanks.split(",");
			boolean isCheckCode = false;
			if (proBank.length == 1) {
				proBankList.add(proBanks);
			} else {
				for (String proCode : proBank) {
					proBankList.add(proCode);
				}
			}
			for (String sysCode : proBankList) {
				if (StringUtils.equals(sysProCode, sysCode)) {
					isCheckCode = true;
					break;
				}
			}
			return isCheckCode;
		}
	}
}
