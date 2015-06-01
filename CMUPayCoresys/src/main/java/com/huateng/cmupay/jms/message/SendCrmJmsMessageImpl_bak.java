package com.huateng.cmupay.jms.message;

import java.util.HashMap;
import java.util.Map;
import javax.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.jms.common.AbsSendJmsMessage;
import com.huateng.cmupay.jms.common.JmsMsgHeader;
import com.huateng.cmupay.models.UpayCsysRouteInfo;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.utils.Serial;

/**
 * @author cmt
 * @version 创建时间：2013-2-19 下午6:05:53 类说明
 */

public class SendCrmJmsMessageImpl_bak extends
		AbsSendJmsMessage<CrmMsgVo, CrmMsgVo> {
	/**
	 * Logger for this class
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	protected Message putMessageProperties(Message msg, JmsMsgHeader header,
			String context) {

		return msg;
	}

	// 发往crm
	public CrmMsgVo sendMsg(CrmMsgVo vo) throws AppBizException {

		logger.debug("SendCrmJmsMessageImpl sendMsg(CrmMsgVo) - start");

		Map<String, String> paramRoute = new HashMap<String, String>();
		paramRoute.put("orgId", vo.getMsgReceiver());
		// paramRoute.put("reqBipCode", reqBipCode);

		UpayCsysRouteInfo routeInfo = findRouteInfo(paramRoute);
		// msgVo.setRouteInfo(routeInfo);
		if (routeInfo == null) {
			logger.error("crm接收方交易路由关闭。");
			log.warn("内部交易流水:{},省接收方交易路由关闭,机构号:{}",new Object[]{vo.getTxnSeq(),vo.getMsgReceiver()});
			throw new AppBizException(RspCodeConstant.Upay.UPAY_U99997.getValue(),
					RspCodeConstant.Upay.UPAY_U99997.getDesc() + "crm接收方交易路由关闭。");
		}

		CrmMsgVo crmMsgVoRtn = new CrmMsgVo();
		String strXml = MsgHandle.marshaller(vo);

		JmsMsgHeader header = new JmsMsgHeader();
		header.setProtocol(routeInfo.getProtocol());
		header.setReqIp(routeInfo.getReqIp());
		header.setReqPort(routeInfo.getReqPort());
		header.setReqPath(routeInfo.getReqPath());
		header.setRouteInfo(routeInfo.getRouteInfo());
		header.setReceiver(CommonConstant.PlatformCd.CrmSys.toString());
		header.setAppCd(vo.getActivityCode());
		//
		header.setMqSeq(Serial.genSerialNo(CommonConstant.Sequence.SendCrmMqSeq
				.toString()));

		String jmsXmlRtn = "";

		try {
			//logger.info("message send to crm:" + strXml );
			log.info("->CRM: "+strXml);
			jmsXmlRtn = super.sendMsg(header, strXml);
			log.info("<-CRM: "+ jmsXmlRtn);
		} catch (AppBizException e) {
			logger.error("activemq AppBizException", e);
			log.error("MQ异常,内部流水号:{}",new Object[]{vo.getTxnSeq()});
			log.error("MQ异常",e);
			if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(e.getCode())) {
				vo.setRspCode(RspCodeConstant.Upay.UPAY_U99998.getValue());
				vo.setRspDesc(RspCodeConstant.Upay.UPAY_U99998.getDesc());
				return vo;
			} else if (RspCodeConstant.Upay.UPAY_U99999.getValue().equals(e.getCode())) {
				e.setCode(RspCodeConstant.Upay.UPAY_U99999.getValue());
				throw e;
			} else {
				e.setCode(RspCodeConstant.Upay.UPAY_U99999.getValue());
				throw e;
			}
		}catch(Exception e){
			logger.error("activemq Exception", e);
			log.error("MQ异常,内部流水号:{}",new Object[]{vo.getTxnSeq()});
			log.error("MQ异常",e);
			AppBizException	e1 = new AppBizException(RspCodeConstant.Upay.UPAY_U99999.getValue(),e);
			throw e1;
		}
		//logger.info("message received from crm:" + jmsXmlRtn);
		MsgHandle.unmarshaller(crmMsgVoRtn, jmsXmlRtn);
		logger.debug("SendCrmJmsMessageImpl sendMsg(CrmMsgVo) - end");
		return crmMsgVoRtn;

	}

}
