package com.huateng.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.bean.CCBData;
import com.huateng.bean.CoreResultPayRes;
import com.huateng.bean.CoreResultReq;
import com.huateng.bean.CoreResultSignRes;
import com.huateng.channel.CCBSwitchChannel;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.TUPayConstant;
import com.huateng.core.exception.AppException;
import com.huateng.core.parse.error.ErrorConfigUtil;
import com.huateng.core.parse.error.bean.ErrorBean;
import com.huateng.core.util.JacksonUtil;
import com.huateng.log.MessageLogger;
import com.huateng.service.CCBService;
import com.huateng.tcp.BaseTcp;
import com.huateng.vo.MsgData;

/**
 * 建行业务处理接口
 * 
 * @author Gary
 * 
 */
@Service
public class CCBServiceImpl implements CCBService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private MessageLogger upayLog=MessageLogger.getLogger(CCBServiceImpl.class);
	@Autowired
	private CCBSwitchChannel ccbSwitchChannel;
	@Autowired
	private BaseTcp baseTcp;

	public MsgData signNotice(boolean isPage, CCBData ccbData)
			throws AppException {
		MsgData msgData = new MsgData();
		try {
			logger.debug("......银行签约结果信息校验......");
			this.checkSignInfo(ccbData);
			
			logger.debug("......银行签约结果信息验签......");
			this.ccbSwitchChannel.validateSignLink(ccbData);
			
			/** 组装发送给核心的消息*/
			CoreResultReq coreData = new CoreResultReq();
			this.assemulyCoreSign(coreData, ccbData);
			String jsonStr = JacksonUtil.beanToStr(coreData);
			String responseText = "";
			boolean isSend = true;
			if (isSend) {
				logger.debug("......银行签约结果信息发送回核心[{}]",jsonStr);
				upayLog.info("银行签约结果信息发送回核心[{}]",jsonStr);
				
				responseText = baseTcp.sendMessage(jsonStr);
				
				logger.debug("接收核心签约结果反馈信息[{}]",responseText);
				upayLog.info("接收核心签约结果反馈信息[{}]",responseText);
			}
			/** 转换核心返回的结果*/
			CoreResultSignRes coreRsp = JacksonUtil.strToBean(
					CoreResultSignRes.class, responseText);
			if (StringUtils.equals(ccbData.getSuccess(), CoreConstant.payStatus.payError.getCode())) {
				ErrorBean bean = ErrorConfigUtil.getErrorBean(
						ErrorConfigUtil.CMU, CoreConstant.CmuErrorCode.ERROR);
				coreRsp.setRspCode(bean.getOuterCode());
				coreRsp.setRspInfo(bean.getErrorMsg());
			}
			/** 组装返回数据*/
			coreRsp.setSubID(ccbData.getSubId());
			msgData = this.ccbSwitchChannel.transferSignLink(coreRsp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException(e);
		}
		return msgData;
	}

	public MsgData payNotice(boolean isPage, CCBData ccbData)
			throws AppException {

		MsgData msgData = new MsgData();
		try {
			/** 检查信息*/
			logger.debug("......银行支付结果信息校验......");
			this.checkPayInfo(ccbData);
			
			/** 验签*/
			logger.debug("......银行支付结果信息验签......");
			this.ccbSwitchChannel.validatePayLink(ccbData);

			/** 组装发送给核心的消息*/
			CoreResultReq coreData = new CoreResultReq();
			this.assemulyCorePay(coreData, ccbData);
			String jsonStr = JacksonUtil.beanToStr(coreData);
			String responseText = "";
			boolean isSend = true;
			if (isSend) {
				/** 发送信息给核心*/
				logger.debug("......银行签约支付信息发送回核心[" + jsonStr + "]");
				responseText = baseTcp.sendMessage(jsonStr);
				logger.debug("......接收核心支付结果反馈信息[" + responseText + "]");
			}
			/** 转换核心返回的结果*/
			CoreResultPayRes coreRsp = JacksonUtil.strToBean(CoreResultPayRes.class, responseText);
			coreRsp.setCurType(ccbData.getCurCode());
			ErrorBean bean = ErrorConfigUtil.getErrorBean(ErrorConfigUtil.CMU,CoreConstant.CmuErrorCode.ERROR);
			if (StringUtils.equals(ccbData.getSuccess(), CoreConstant.payStatus.payError.getCode())) {
				coreRsp.setRspCode(bean.getOuterCode());
				coreRsp.setRspInfo(bean.getErrorMsg());
				
			}
			
			// 如果是移动商城发起的，错误码需要转化为六位
			if ("0055".equals(coreRsp.getMerID())) {
				coreRsp.setRspCode(TUPayConstant
						.getMMarketErrorCode(coreRsp.getRspCode()));
				coreRsp.setRspInfo(TUPayConstant.Market
						.getDescByValue(coreRsp.getRspCode()));
			}
			
			
			String remark=ccbData.getRemark1();
			if(StringUtils.equals(remark,CoreConstant.checkStatus.mobileShop.getCode())){
				logger.debug("拼装移动商城的返回结果");
				/** 组装移动商城的返回数据 */
				msgData = this.ccbSwitchChannel.transferShopPayLink(coreRsp);
			}else{
				logger.debug("拼装省份的返回结果");
				/** 组装省份的返回数据 */
				msgData = this.ccbSwitchChannel.transferPayLink(coreRsp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException(e);
		}

		return msgData;
	}

	private void assemulyCoreSign(CoreResultReq coreData, CCBData ccbData) {
		coreData.setTransCode(CoreConstant.TransCode.SIGN_RST.getCode());
		coreData.setOrderID(ccbData.getOrderId());
	}

	private void assemulyCorePay(CoreResultReq coreData, CCBData ccbData) {
		coreData.setTransCode(CoreConstant.TransCode.PAY_RST.getCode());
		coreData.setOrderID(ccbData.getOrderId());
	}

	/**
	 * 校验签约结果信息
	 * 
	 * @param reqData
	 */
	private void checkSignInfo(CCBData ccbData) {
		ccbData.setStatus(CoreConstant.BankErrorCode.SUCCESS);
	}

	/**
	 * 校验支付结果信息
	 * 
	 * @param reqData
	 */
	private void checkPayInfo(CCBData ccbData) {
		ccbData.setStatus(CoreConstant.BankErrorCode.SUCCESS);
	}
}
