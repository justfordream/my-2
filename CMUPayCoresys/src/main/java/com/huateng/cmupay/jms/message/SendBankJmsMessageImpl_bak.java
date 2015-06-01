package com.huateng.cmupay.jms.message;

import java.util.HashMap;
import java.util.Map;
import javax.jms.Message;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.jms.common.AbsSendJmsMessage;
import com.huateng.cmupay.jms.common.JmsMsgHeader;
import com.huateng.cmupay.models.UpayCsysRouteInfo;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.utils.Serial;

/**
 * @author cmt
 * @version 创建时间：2013-2-19 下午6:05:53 类说明
 */
public class SendBankJmsMessageImpl_bak extends
		AbsSendJmsMessage<BankMsgVo, BankMsgVo> {

	@Override
	protected Message putMessageProperties(Message msg, JmsMsgHeader header,
			String context) {

		return msg;
	}

	// 发往银行
	@Override
	public BankMsgVo sendMsg(BankMsgVo bankMsgVo) throws AppBizException {

		logger.debug("SendBankJmsMessageImpl sendMsg(BankMsgVo) - start");

		Map<String, String> paramRoute = new HashMap<String, String>();
		paramRoute.put("orgId", bankMsgVo.getRcvSys());
		// paramRoute.put("reqBipCode", reqBipCode);

		UpayCsysRouteInfo routeInfo = findRouteInfo(paramRoute);
		// msgVo.setRouteInfo(routeInfo);
		if (routeInfo == null) {
			logger.error("银行接收方交易路由关闭.bankID:" ,bankMsgVo.getRcvSys());
			throw new AppBizException(RspCodeConstant.Upay.UPAY_U99997.getValue(),
					RspCodeConstant.Upay.UPAY_U99997.getDesc() + "银行接收方交易路由关闭.");
		}

		String strXml = MsgHandle.marshaller(bankMsgVo);
		BankMsgVo bankMsgVoRtn = new BankMsgVo();
		JmsMsgHeader header = new JmsMsgHeader();
		header.setProtocol(routeInfo.getProtocol());
		header.setReqIp(routeInfo.getReqIp());
		header.setReqPort(routeInfo.getReqPort());
		header.setReqPath(routeInfo.getReqPath());
		header.setRouteInfo(routeInfo.getRouteInfo());
		header.setReceiver(bankMsgVo.getRcvSys());
		header.setAppCd(bankMsgVo.getActivityCode());
		//
		header.setMqSeq(Serial
				.genSerialNo(CommonConstant.Sequence.SendBankMqSeq.toString()));

		String jmsXmlRtn = "";

		try {
			//logger.debug("message send to bank is:" + strXml);
			log.info("->BANK:{}",new Object[]{strXml});
			jmsXmlRtn = super.sendMsg(header, strXml);
			log.info("<-BANK:{}",new Object[]{jmsXmlRtn});
		} catch (AppBizException e) {
			logger.error("activemq AppBizException", e);
			if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(e.getCode())) {
				bankMsgVoRtn.setRspCode(RspCodeConstant.Upay.UPAY_U99998.getValue());
				bankMsgVoRtn.setRspDesc(RspCodeConstant.Upay.UPAY_U99998.getDesc());
				return bankMsgVoRtn;
			} else if (RspCodeConstant.Upay.UPAY_U99999.getValue().equals(e.getCode())) {
				e.setCode(RspCodeConstant.Upay.UPAY_U99999.getValue());
				//e.setTextMessage(MessageHandler.getBankErrMsg("025A06"));
				throw e;
			} else {
				e.setCode(RspCodeConstant.Upay.UPAY_U99999.getValue());
				throw e;
			}
		}catch(Exception e){
			logger.error("activemq Exception", e);
			AppBizException	e1 = new AppBizException(RspCodeConstant.Upay.UPAY_U99999.getValue(),e);
			throw e1;
		}
		//logger.debug("message received from bank is:" + jmsXmlRtn);
		MsgHandle.unmarshaller(bankMsgVoRtn, jmsXmlRtn);

		logger.debug("SendBankJmsMessageImpl sendMsg(BankMsgVo) - end");
		return bankMsgVoRtn;

	}

}
