package com.huateng.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.bean.CCBReqData;
import com.huateng.bean.CmuData;
import com.huateng.bean.ShopData;
import com.huateng.core.common.CoreConstant;
import com.huateng.iconstant.IConstant;
import com.huateng.log.MessageLogger;

/**
 * 信息检查类
 * 
 * @author Gary
 * 
 */
public class MessageCheck {
	private static Logger logger = LoggerFactory.getLogger(MessageCheck.class);
	private static MessageLogger upayLog = MessageLogger
			.getLogger(MessageCheck.class);
	private static final String AOTU_PAY = "1";

	/**
	 * 校验签约信息是否正常
	 * 
	 * @param cmuData
	 */
	public static void checkCmuSign(CmuData cmuData) {
		if (StringUtils.isBlank(cmuData.getSessionID())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0003);
		} else if (StringUtils.isBlank(cmuData.getIDType())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0001);
		} else if (StringUtils.isBlank(cmuData.getIDValue())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0002);
		} else if (StringUtils.isBlank(cmuData.getServerURL())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0004);
		} else if (StringUtils.isBlank(cmuData.getBackURL())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0005);
		} else if (StringUtils.isBlank(cmuData.getSig())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0007);
		} else if (StringUtils.isBlank(cmuData.getBankID())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0008);
		} else if (StringUtils.isBlank(cmuData.getSubTime())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0009);
		} else if (StringUtils.isBlank(cmuData.getTransactionID())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0010);
		} else if (StringUtils.isBlank(cmuData.getOrigDomain())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0011);
		} else if (StringUtils.isBlank(cmuData.getCLIENTIP())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0012);
		} else if (StringUtils.isBlank(cmuData.getMCODE())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0013);
		} else if (StringUtils.isBlank(cmuData.getPayType())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0025);
		} else if (StringUtils.equals(cmuData.getPayType(), AOTU_PAY)
				&& StringUtils.isBlank(cmuData.getRechAmount())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0026);
		} else if (StringUtils.equals(cmuData.getPayType(), AOTU_PAY)
				&& StringUtils.isBlank(cmuData.getRechThreshold())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0027);
		} else if (StringUtils.equals(cmuData.getPayType(), AOTU_PAY)
				&& !AmountUtil.isInteger((cmuData.getRechThreshold()))) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0027);
		} else if (StringUtils.equals(cmuData.getPayType(), AOTU_PAY)
				&& !AmountUtil.isInteger((cmuData.getRechAmount()))) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0026);
		} else {
			cmuData.setStatus(CoreConstant.CmuErrorCode.SUCCESS);
			logger.debug("...省份{}...签约请求参数校验成功......", cmuData.getOrigDomain());
			upayLog.info("...省份{}...签约请求参数校验成功......", cmuData.getOrigDomain());
			return;
		}
		if (!cmuData.getStatus().equals(CoreConstant.CmuErrorCode.SUCCESS)) {
			logger.debug("...省份{}...签约请求参数校验失败,失败原因[" + cmuData.getStatus()
					+ "]......", cmuData.getOrigDomain());
			upayLog.warn("...省份{}...签约请求参数校验失败,失败原因[" + cmuData.getStatus()
					+ "]......", cmuData.getOrigDomain());
		}
	}

	/**
	 * 校验支付信息是否正常
	 * 
	 * @param cmuData
	 */
	public static void checkCmuPay(CmuData cmuData) {
		if (StringUtils.isBlank(cmuData.getMerID())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0014);
		} else if (StringUtils.isBlank(cmuData.getOrderID())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0015);
		} else if (StringUtils.isBlank(cmuData.getOrderTime())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0016);
		} else if (StringUtils.isBlank(cmuData.getPayed())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0017);
		} else if (StringUtils.isBlank(cmuData.getCurType())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0018);
		} else if (StringUtils.isBlank(cmuData.getIDType())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0001);
		} else if (StringUtils.isBlank(cmuData.getIDValue())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0002);
		} else if (StringUtils.isBlank(cmuData.getServerURL())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0019);
		} else if (StringUtils.isBlank(cmuData.getMerVAR())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0020);
		} else if (StringUtils.isBlank(cmuData.getBackURL())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0005);
		} else if (StringUtils.isBlank(cmuData.getSig())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0007);
		} else if (StringUtils.isBlank(cmuData.getBankID())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0008);
		} else if (StringUtils.isBlank(cmuData.getCLIENTIP())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0012);
		} else if (StringUtils.isBlank(cmuData.getMCODE())) {
			cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C_A0013);
		} else {
			cmuData.setStatus(CoreConstant.CmuErrorCode.SUCCESS);
			logger.debug("...省份{}...支付请求参数校验成功......", cmuData.getMerID());
			upayLog.info("...省份{}...支付请求参数校验成功......", cmuData.getMerID());
			return;
		}
		if (!cmuData.getStatus().equals(CoreConstant.CmuErrorCode.SUCCESS)) {
			logger.debug("...省份{}...支付请求参数校验失败,失败原因[" + cmuData.getStatus()
					+ "]......", cmuData.getMerID());
			upayLog.warn("...省份{}...支付请求参数校验失败,失败原因[" + cmuData.getStatus()
					+ "]......", cmuData.getMerID());
		}
	}

	/**
	 * 校验开通认证信息是否正常
	 * 
	 * @param cmuData
	 */
	public static void checkPayShopOpen(ShopData cmuData) {
		// MerID
		if (StringUtils.isBlank(cmuData.getMerID())) {
			cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_025A05
					.getCode());
			cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_025A05
					.getDesc());
			return;
		}

		// OrderID
		if (StringUtils.isBlank(cmuData.getOrderID())) {
			cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_019A27
					.getCode());
			cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_019A27
					.getDesc());
			return;
		}

		// OrderTime
		if (StringUtils.isBlank(cmuData.getOrderTime())) {
			cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_025A05
					.getCode());
			cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_025A05
					.getDesc());
			return;
		}

		// Payment
		if (StringUtils.isBlank(cmuData.getPayment())) {
			cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_019A51
					.getCode());
			cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_019A51
					.getDesc());
			return;
		}

		// //BankAcctID
		// if (StringUtils.isBlank(cmuData.getBankAcctID())) {
		// cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_025A05
		// .getCode());
		// cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_025A05
		// .getDesc());
		// return;
		// }

		// MerURL
		if (StringUtils.isBlank(cmuData.getServerURL())) {
			cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_025A05
					.getCode());
			cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_025A05
					.getDesc());
			return;
		}

		// MerVAR
		if (StringUtils.isBlank(cmuData.getMerVar())) {
			cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_019A28
					.getCode());
			cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_019A28
					.getDesc());
			return;
		}

		// BackURL
		if (StringUtils.isBlank(cmuData.getBackURL())) {
			cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_025A05
					.getCode());
			cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_025A05
					.getDesc());
			return;
		}

		// CLIENTIP
		if (StringUtils.isBlank(cmuData.getClientIp())) {
			cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_025A05
					.getCode());
			cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_025A05
					.getDesc());
			return;
		}

		// Sig
		if (StringUtils.isBlank(cmuData.getSig())) {
			cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_025A05
					.getCode());
			cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_025A05
					.getDesc());
			return;
		}

		// 校验成功设置为010A00
		cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_010A00.getCode());
	}

	/**
	 * 校验支付信息是否正常
	 * 
	 * @param cmuData
	 */
	public static void checkShopPay(ShopData cmuData) {

		if (StringUtils.isBlank(cmuData.getOrderID())) {
			cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_019A27
					.getCode());
			cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_019A27
					.getDesc());
			return;
		}

		if (StringUtils.isBlank(cmuData.getPayment())) {
			cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_019A51
					.getCode());
			cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_019A51
					.getDesc());
			return;
		}

		if (StringUtils.isBlank(cmuData.getMerVar())) {
			cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_019A28
					.getCode());
			cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_019A28
					.getDesc());
			return;
		}

		if (StringUtils.isBlank(cmuData.getOrderType())) {
			cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_025A05
					.getCode());
			cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_025A05
					.getDesc());
			return;
		}

		if (cmuData.getOrderType().equals(
				CoreConstant.ShopPayStatus.TUPAY_STATUS.getValue())
				&& StringUtils.isBlank(cmuData.getShopMerId())) {
			cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_025A05
					.getCode());
			cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_025A05
					.getDesc());
			return;
		}

		if (StringUtils.isBlank(cmuData.getMerID())
				|| StringUtils.isBlank(cmuData.getOrderTime())
				|| StringUtils.isBlank(cmuData.getCurType())
				|| StringUtils.isBlank(cmuData.getBankID())
				|| StringUtils.isBlank(cmuData.getClientIp())
				|| StringUtils.isBlank(cmuData.getMcode())) {
			cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_014A04
					.getCode());
			cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_014A04
					.getDesc());
			return;
		}

		// 如果是缴费请求
		if (CoreConstant.ShopPayStatus.UPAY_STATUS.getValue().equals(
				cmuData.getOrderType())) {

			if (StringUtils.isBlank(cmuData.getIDType())) {
				cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_019A17
						.getCode());
				cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_019A17
						.getDesc());
				return;
			} else if (StringUtils.isBlank(cmuData.getIDValue())) {
				cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_019A18
						.getCode());
				cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_019A18
						.getDesc());
				return;
			} else if (StringUtils.isBlank(cmuData.getChargeMoney())) {
				cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_019A52
						.getCode());
				cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_019A52
						.getDesc());
				return;
			} else if (StringUtils.isBlank(cmuData.getHomeProv())) {
				cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_019A44
						.getCode());
				cmuData.setStatusDesc(CoreConstant.BankResultCode.UPAY_B_019A44
						.getDesc());
				return;
			}
		}
		cmuData.setStatus(CoreConstant.BankResultCode.UPAY_B_010A00.getCode());
	}

	/**
	 * 校验信息
	 * 
	 * @param reqData
	 */
	public static void checkCcbBankInfo(CCBReqData reqData) {
		if (StringUtils.isBlank(reqData.getTxCode())) {
			reqData.setError("the TXCODE of sign/pay is empty");
		} else if (StringUtils.isBlank(reqData.getMerchantId())) {
			reqData.setError("the MERCHANTID of sign/pay is empty");
		} else {
			reqData.setStatus(IConstant.BANK_DATA_SUCCESS);
			return;
		}
		reqData.setStatus(IConstant.BANK_DATA_EMPTY);
	}

	/**
	 * 校验信息是否正常
	 * 
	 * @param response
	 * @param cmuData
	 *            若信息校验通过，则属性status设置为通过，若校验失败，标识失败原因
	 */
	public static void checkCmuInfo(CmuData cmuData) {

		if (StringUtils.isBlank(cmuData.getBankID())) {

			cmuData.setErrorMsg("the bankId of crm sign/pay is empty");

		} else if (StringUtils.isBlank(cmuData.getOrderID())) {

			cmuData.setErrorMsg("the orderId of crm sign/pay is empty");

		} else if (StringUtils.isBlank(cmuData.getCmuID())) {

			cmuData.setErrorMsg("the cmuId of crm sign/pay is empty");

		} else if (StringUtils.isBlank(cmuData.getSig())) {

			cmuData.setErrorMsg("the sig of crm sign/pay is empty");

		} else {

			cmuData.setStatus(IConstant.CMU_DATA_SUCCESS);

			return;
		}

		cmuData.setStatus(IConstant.CMU_DATA_EMPTY);
	}

	public static void main(String[] args) {
		System.out.println("11111111111");
	}

}
