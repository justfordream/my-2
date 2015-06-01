package com.huateng.core.channel.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.bean.CoreResultPayRes;
import com.huateng.bean.TenData;
import com.huateng.cmupay.service.TUPayRemoteService;
import com.huateng.core.channel.TenPaySwitchChannel;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.link.LingSignChannel;
import com.huateng.core.util.RequestUtil;
import com.huateng.link.Link2External;
import com.huateng.log.MessageLogger;
import com.huateng.utils.WebBackToMer;
import com.huateng.vo.MsgData;

public class TenPaySwitchChannelImpl implements TenPaySwitchChannel {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private MessageLogger upayLog = MessageLogger
			.getLogger(TenPaySwitchChannelImpl.class);

	/**
	 * 数据集合
	 */
	protected Map<String, Map<String, Map<String, Link2External>>> switchMaps;

	@Value("${TEN_PROVICE_LIST}")
	private String tenProviceList;

	@Value("${TEN_SHOP_CHECK_STATUS}")
	private String tenShopCheckStatus;

	@Value("${TEN_CHECK_STATUS}")
	private String tenCheckSwitch;

	@Autowired
	private TUPayRemoteService tPaySecurityService;

	@Override
	public void validateTenPayLink(TenData tenData) {
		try {
			if (StringUtils.equals(tenData.getStatus(),
					CoreConstant.BankResultCode.UPAY_B_010A00.getCode())) {

				String bankId = CoreConstant.BankCode.TEN.getCode();

				Map<String, String> plainMap = tenData.assemlyPayPlainMap();

				logger.info("财付通{}订单号：{}支付验签集合：{}", new Object[] { bankId,
						tenData.getOut_trade_no(), plainMap });
				upayLog.debug("财付通:{}订单号：{}支付验签字符串：{}", new Object[] { bankId,
						tenData.getOut_trade_no() });

				boolean result = true;

				logger.debug("财付通{}验签开关{}", new Object[] { bankId,
						tenShopCheckStatus });

				if (CoreConstant.CHECK_STATUS.equals(tenShopCheckStatus)
						&& this.isCheckProArea(bankId)) {
					result = tPaySecurityService.tenPayVerify(plainMap);
				}

				logger.debug("财付通{}订单号：{}支付请求验签结果：{}", new Object[] { bankId,
						tenData.getOut_trade_no(), result });
				upayLog.debug("财付通{}订单号：{}支付请求验签结果:{}", new Object[] { bankId,
						tenData.getOut_trade_no(), result });

				if (!result) {
					logger.debug("财付通{}订单号：{}支付请求验签失败.", bankId,
							tenData.getOut_trade_no());
					upayLog.error("财付通{}订单号：{}支付请求验签失败.", bankId,
							tenData.getOut_trade_no());
					tenData.setStatus(CoreConstant.BankResultCode.UPAY_B_014A06
							.getCode());
				} else {
					logger.debug("财付通{}.订单号：{}支付请求验签成功", bankId,
							tenData.getOut_trade_no());
				}

			}
		} catch (Exception e) {
			logger.error(
					"财付通{}.订单号：:{}缴费验签错误{}",
					new Object[] { CoreConstant.BankCode.TEN.getCode(),
							tenData.getOut_trade_no(), e.getMessage() });
			upayLog.error(
					"财付通{}.订单号：:{}缴费验签错误{}",
					new Object[] { CoreConstant.BankCode.TEN.getCode(),
							tenData.getOut_trade_no(), e.getMessage() });
			e.printStackTrace();
		}
	}

	@Override
	public MsgData transferTenPayLink(CoreResultPayRes coreRsp) {

		MsgData msgData = new MsgData();
		Map<String, String> params = new HashMap<String, String>();

		params.put("OrderID",
				StringUtils.isNotBlank(coreRsp.getOrderID()) ? coreRsp
						.getOrderID().trim() : "");
		if (StringUtils.isNotBlank(coreRsp.getOrderID())) {
			params.put("OrderID", coreRsp.getOrderID().trim());
		} else {
			params.put("OrderID", "");
		}

		params.put("RspCode",
				StringUtils.isNotBlank(coreRsp.getRspCode()) ? coreRsp
						.getRspCode().trim() : "");
		params.put("RspInfo",
				StringUtils.isNotBlank(coreRsp.getRspInfo()) ? coreRsp
						.getRspInfo().trim() : "");
		params.put("MerVAR",
				StringUtils.isNotBlank(coreRsp.getMerVAR()) ? coreRsp
						.getMerVAR().trim() : "");
		params.put("Sig", RequestUtil.paseEncode(new LingSignChannel()
				.getShopPayKey(params, tenCheckSwitch)));

		String toHtml = WebBackToMer.getBackResult(params,
				coreRsp.getBackURL(), true);

		msgData.setRedirectHtml(toHtml);
		// msgData.setUrl(coreRsp.getServerURL());
		msgData.setParams(params);

		return msgData;
	}

	/**
	 * 
	 * 根据省份判断是否加密、解密 (all 全部打开,部分打开 4位省份代码用逗号隔开)
	 * 
	 * @return true 需要加密
	 */
	public boolean isCheckProArea(String sysProCode) {
		String proAreas = tenProviceList;
		if (StringUtils.equals(proAreas, CoreConstant.ALL)) {
			return true;
		} else {
			List<String> proAreaList = new ArrayList<String>();
			String[] proArea = proAreas.split(",");
			boolean isCheckCode = false;
			if (proArea.length == 1) {
				proAreaList.add(proAreas);
			} else {
				for (String proCode : proArea) {
					proAreaList.add(proCode);
				}
			}
			for (String sysCode : proAreaList) {
				if (StringUtils.equals(sysProCode, sysCode)) {
					isCheckCode = true;
					break;
				}
			}
			return isCheckCode;
		}
	}

}
