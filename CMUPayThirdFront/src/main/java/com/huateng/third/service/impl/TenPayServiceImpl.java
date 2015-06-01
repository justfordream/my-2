/**
 * 
 */
package com.huateng.third.service.impl;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.core.base.BaseServiceImpl;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.CoreConstant.TenPayMsg;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.util.DateUtil;
import com.huateng.log.LogHandle;
import com.huateng.third.logFormat.MessageLogger;
import com.huateng.third.service.TUPayRemoteService;
import com.huateng.third.service.TenPayService;

/**
 * @author Manzhizhen
 *
 */
public class TenPayServiceImpl extends BaseServiceImpl implements TenPayService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private MessageLogger log = MessageLogger.getLogger(getClass());
	
	/**
	 * 验签服务
	 */
	@Autowired
	private TUPayRemoteService tenpaySecurityRemoting;

	private LogHandle logHandle;

	/**
	 * 验签开关
	 */
	private @Value("${check.tenpay.switch}")
	String checkSwitch;

	/**
	 * 签名开关
	 */
	private @Value("${check.tenpay.switch}")
	String signSwitch;


	@Override
	public Map<String, String> checkSign(String client,
			Map<String, String> paramsMaps) throws ServiceException {
		
		if (MapUtils.isEmpty(paramsMaps)) {
			throw new ServiceException("UPAY-B-015A05:财付通验签解析报文失败，获得的Map为空!");// 解析报文失败
		}

		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(checkSwitch)) {
			logger.debug("......调用支付前置(财付通)验签方法......");
			/*
			 * 全网监控日志
			 */
			this.logHandle.info(false,
					CoreConstant.ErrorCode.SUCCESS.getCode(),
					TenPayMsg.OUT_TRADE_NO.getValue(),
					"", "",
					CoreConstant.TEN_PAY_ORG_ID,
					CoreConstant.TEN_PAY_ORG_ID,
					CoreConstant.ErrorCode.SUCCESS.getDesc());
			log.debug(
					"订单号：{},财付通订单号：{}",
					new Object[] {paramsMaps.get(TenPayMsg.OUT_TRADE_NO.getValue()), 
							paramsMaps.get(TenPayMsg.TRANSACTION_ID.getValue())});

			boolean isValid = tenpaySecurityRemoting.tenPayVerify(paramsMaps);
			if (!isValid) {
				logger.error("......财付通验签失败......验签报文:{}", paramsMaps);
				log.error("财付通验签失败,验签报文:{}", paramsMaps);
				throw new ServiceException("UPAY-B-014A06");// 签名验证失败
			}

			logger.debug("......财付通验签成功......验签报文:{}", paramsMaps);
			log.info("财付通验签成功,验签报文:{}", paramsMaps);
		}

		return paramsMaps;
	}

	@Override
	public Map<String, String> sign(String client,
			Map<String, String> paramsMaps) throws ServiceException {
		Map<String, String> afterParamsMaps = paramsMaps;
		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(signSwitch)) {
			logger.debug("......调用支付前置(财付通)签名方法......");
			if (MapUtils.isNotEmpty(paramsMaps)) {
				try {
					afterParamsMaps = tenpaySecurityRemoting.tenPaySign(paramsMaps);
					logger.debug("......签名成功,签名前报文{}", paramsMaps);
					log.info("财付通签名成功,签名前报文{}", DateUtil.printMapLog(paramsMaps));
					logger.info("......签名成功,签名后报文{}", DateUtil.printMapLog(afterParamsMaps));
					log.info("财付通签名成功,签名后报文{}", DateUtil.printMapLog(afterParamsMaps));
				} catch (Exception e) {
					logger.error("", e);
					logger.error("......财付通签名失败,签名前报文{}", paramsMaps);
					log.error("财付通签名失败,签名前报文{}", paramsMaps);
				}
			}
		}
		
		return afterParamsMaps;
	}
}
