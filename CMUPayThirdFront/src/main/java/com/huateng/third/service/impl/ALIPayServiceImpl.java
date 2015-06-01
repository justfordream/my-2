package com.huateng.third.service.impl;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.core.base.BaseServiceImpl;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.CoreConstant.ALIPayMsg;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.util.DateUtil;
import com.huateng.log.LogHandle;
import com.huateng.third.logFormat.MessageLogger;
import com.huateng.third.service.ALIPayService;
import com.huateng.third.service.TUPayRemoteService;

/**
 * 将银联消息发送到核心的服务实现类 包括发送，签名和验签等
 *
 */
public class ALIPayServiceImpl extends BaseServiceImpl implements ALIPayService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private MessageLogger log = MessageLogger.getLogger(getClass());
	/**
	 * 验签服务
	 */
	@Autowired
	private TUPayRemoteService tupaySecurityRemoting;

	private LogHandle logHandle;

	/**
	 * 验签开关
	 */
	private @Value("${check.switch}")
	String checkSwitch;

	/**
	 * 签名开关
	 */
	private @Value("${check.switch}")
	String signSwitch;

	public void setLogHandle(LogHandle logHandle) {
		this.logHandle = logHandle;
	}

	@Override
	public Map<String, String> checkSign(String client,
			Map<String, String> paramsMaps) throws ServiceException {

		if (MapUtils.isEmpty(paramsMaps)) {
			throw new ServiceException("UPAY-B-015A05:验签解析报文失败，获得的Map为空!");// 解析报文失败
		}

		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(checkSwitch)) {
			logger.debug("......调用支付前置验签方法......");
			/*
			 * 全网监控日志
			 */
			this.logHandle.info(false,
					CoreConstant.ErrorCode.SUCCESS.getCode(),
					paramsMaps.get(ALIPayMsg.ORDERID.getValue()),
					ALIPayMsg.TXNTYPE.getDesc(), "",
					CoreConstant.ALI_PAY_ORG_ID,
					CoreConstant.ALI_PAY_ORG_ID,
					CoreConstant.ErrorCode.SUCCESS.getDesc());
			log.debug(
					"交易代码：{}，发送方订单号：{}",
					new Object[] {
							ALIPayMsg.TXNTYPE.getDesc(),
							paramsMaps.get(ALIPayMsg.ORDERID.getValue())});

			boolean isValid = tupaySecurityRemoting.alipayVerify(paramsMaps);
			if (!isValid) {
				logger.error("......验签失败......验签报文:{}", paramsMaps);
				log.error("验签失败,验签报文:{}", paramsMaps);
				throw new ServiceException("UPAY-B-014A06");// 签名验证失败
			}

			logger.debug("......验签成功......验签报文:{}", paramsMaps);
			log.info("验签成功,验签报文:{}", paramsMaps);
		}

		return paramsMaps;
	}

	@Override
	public Map<String, String> sign(String client,
			Map<String, String> paramsMaps) throws ServiceException {
		Map<String, String> afterParamsMaps = paramsMaps;
		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(signSwitch)) {
			logger.debug("......调用支付前置签名方法......");
			if (MapUtils.isNotEmpty(paramsMaps)) {
				try {
					afterParamsMaps = tupaySecurityRemoting.alipaySign(paramsMaps);
					logger.debug("......签名成功,签名前报文{}", paramsMaps);
					log.info("签名成功,签名前报文{}", DateUtil.printMapLog(paramsMaps));
					logger.info("......签名成功,签名后报文{}", DateUtil.printMapLog(afterParamsMaps));
					log.info("签名成功,签名后报文{}", DateUtil.printMapLog(afterParamsMaps));
				} catch (Exception e) {
					logger.error("", e);
					logger.error("......签名失败,签名前报文{}", paramsMaps);
					log.error("签名失败,签名前报文{}", paramsMaps);
				}
			}
		}
		return afterParamsMaps;
	}

}
