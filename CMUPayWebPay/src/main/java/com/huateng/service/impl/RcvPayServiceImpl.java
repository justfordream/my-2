package com.huateng.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.bean.CmuData;
import com.huateng.bean.CoreResultRsp;
import com.huateng.channel.CmuSwitchChannel;
import com.huateng.core.common.CommonFunction;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.exception.AppException;
import com.huateng.core.util.JacksonUtil;
import com.huateng.log.LogHandle;
import com.huateng.log.MessageLogger;
import com.huateng.service.RcvPayService;
import com.huateng.tcp.BaseTcp;
import com.huateng.utils.MessageCheck;
import com.huateng.vo.MsgData;

/**
 * 支付信息业务处理类
 * 
 * @author Gary
 * 
 */
@Service
public class RcvPayServiceImpl implements RcvPayService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private MessageLogger upayLog=MessageLogger.getLogger(RcvPayServiceImpl.class);
	@Autowired
	private BaseTcp baseTcp;
	@Autowired
	private CmuSwitchChannel cmuSwitchChannel;
	@Autowired
	private LogHandle logHandle;

	public void setBaseTcp(BaseTcp baseTcp) {
		this.baseTcp = baseTcp;
	}

	public MsgData sendPayRequest(CmuData cmuData) throws AppException {
		/*
		 * 来源信息验证
		 */
		logger.debug("...省份{}.支付请求参数校验",cmuData.getMerID());
		MessageCheck.checkCmuPay(cmuData);
		/*
		 * 验签
		 */
		logger.debug("..省份{}支付请求验签.",cmuData.getMerID());
		cmuSwitchChannel.validatePayLink(cmuData);
		
		cmuData.setTransCode(CoreConstant.TransCode.PAY.getCode());
		logHandle.info(false, CoreConstant.ErrorCode.SUCCESS.getCode(), CoreConstant.TransCode.CRM_PAY.getCode(),
				CoreConstant.TransCode.CRM_PAY.getDesc(), cmuData.getCmuID(),
				CommonFunction.getSysCodeByProvCode(cmuData.getCmuID()), CoreConstant.ErrorCode.SUCCESS.getDesc());
		CoreResultRsp coreRsp=null;
		if (cmuData.getStatus().equals(CoreConstant.CmuErrorCode.SUCCESS)) {
			String jsonStr = JacksonUtil.beanToStr(cmuData);
			logger.debug("省份{}支付请求发送到核心[{}]",cmuData.getMerID(),jsonStr);
			upayLog.debug("省份{}支付请求发送到核心[{}]",cmuData.getMerID(),jsonStr);
			String responseText = baseTcp.sendMessage(jsonStr);
			logger.debug("...省份{}...接收到核心返回支付请求响应信息:[{}]",cmuData.getMerID(),responseText);
			upayLog.debug("省份{}接收到核心返回支付请求响应信息:[{}]",cmuData.getMerID(),responseText);
			coreRsp = JacksonUtil.strToBean(CoreResultRsp.class, responseText);
			if (CoreConstant.CoreRsp.SUCCESS.getCode().equals(coreRsp.getRspCode())) {
				cmuData.setStatus(CoreConstant.CmuErrorCode.SUCCESS);
			} else {
				cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C+coreRsp.getRspCode());
			}
		}
		/*
		 * 转发信息至指定银行
		 */
		MsgData msgData = cmuSwitchChannel.transferPayLink(cmuData,coreRsp);
		return msgData;
	}

}
