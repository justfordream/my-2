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
import com.huateng.service.RcvSignService;
import com.huateng.tcp.BaseTcp;
import com.huateng.utils.MessageCheck;
import com.huateng.vo.MsgData;

/**
 * 签约信息业务处理类
 * 
 * @author Gary
 * 
 */
@Service
public class RcvSignServiceImpl implements RcvSignService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private MessageLogger upayLog=MessageLogger.getLogger(RcvSignServiceImpl.class);
	@Autowired
	private BaseTcp baseTcp;
	@Autowired
	private CmuSwitchChannel cmuSwitchChannel;
	@Autowired
	private LogHandle logHandle;

	public MsgData sendSignRequest(CmuData cmuData) throws AppException {
		MsgData msgData = null;
		try {

			/*
			 * 来源信息验证
			 */
			logger.debug("......签约请求参数校验......");
			MessageCheck.checkCmuSign(cmuData);
			/*
			 * 验签
			 */
			logger.debug("......签约请求验签......");
			cmuSwitchChannel.validateSignLink(cmuData);
			/*
			 * 请求核心，记录流水
			 */
			cmuData.setTransCode(CoreConstant.TransCode.SIGN.getCode());

			logHandle.info(false, CoreConstant.ErrorCode.SUCCESS.getCode(), CoreConstant.TransCode.SIGN.getCode(),
					CoreConstant.TransCode.SIGN.getDesc(), cmuData.getCmuID(),
					CommonFunction.getSysCodeByProvCode(cmuData.getCmuID()), CoreConstant.ErrorCode.SUCCESS.getDesc());

			if (cmuData.getStatus().equals(CoreConstant.CmuErrorCode.SUCCESS)) {
				String jsonStr = JacksonUtil.beanToStr(cmuData);
				logger.debug("省份{}签约请求发送到核心[{}]",cmuData.getOrigDomain(),jsonStr);
				upayLog.debug("省份{}签约请求发送到核心[{}]",cmuData.getOrigDomain(),jsonStr);
				
				String responseText = baseTcp.sendMessage(jsonStr);

				logger.debug("省份{}接收到核心返回签约请求响应信息:[{}]",cmuData.getOrigDomain(),responseText);
				upayLog.debug("省份{}接收到核心返回签约请求响应信息:[{}]",cmuData.getOrigDomain(),responseText);
				CoreResultRsp coreRsp = JacksonUtil.strToBean(CoreResultRsp.class, responseText);
				if (CoreConstant.CoreRsp.SUCCESS.getCode().equals(coreRsp.getRspCode())) {
					cmuData.setStatus(CoreConstant.CmuErrorCode.SUCCESS);
				} else {
					cmuData.setStatus(CoreConstant.CmuErrorCode.UPAY_C+coreRsp.getRspCode());
				}
			}
			/*
			 * 转发信息至指定银行
			 */
			msgData = cmuSwitchChannel.transferSignLink(cmuData);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException(e);
		}

		return msgData;
	}
}
