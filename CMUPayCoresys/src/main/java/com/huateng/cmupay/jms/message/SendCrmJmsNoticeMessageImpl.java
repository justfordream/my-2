package com.huateng.cmupay.jms.message;

import java.util.HashMap;
import java.util.Map;
import javax.jms.Message;
import org.springframework.stereotype.Component;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.jms.common.AbsSendJmsNoticeMessage;
import com.huateng.cmupay.jms.common.JmsMsgHeader;
import com.huateng.cmupay.models.UpayCsysRouteInfo;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.utils.Serial;

/**
 * @author cmt
 * @version 创建时间：2013-2-19 下午6:05:53 类说明
 */
@Component("sendCrmJmsNoticeMessage")
public class SendCrmJmsNoticeMessageImpl extends
		AbsSendJmsNoticeMessage<CrmMsgVo> {


	@Override
	protected Message putMessageProperties(Message msg, JmsMsgHeader header,
			String context) {

		return msg;
	}

	// 发往crm
	public void sendMsg(CrmMsgVo vo) throws AppBizException {

		logger.info("SendCrmJmsMessageImpl sendMsg(CrmMsgVo) - start");

		Map<String, String> paramRoute = new HashMap<String, String>();
		paramRoute.put("orgId", vo.getMsgReceiver());
		// paramRoute.put("reqBipCode", reqBipCode);

		UpayCsysRouteInfo routeInfo = findRouteInfo(paramRoute);
		// msgVo.setRouteInfo(routeInfo);
		if (routeInfo == null) {
			logger.error("crm接收方交易路由关闭。");
			log.error("内部交易流水:{},省接收方交易路由关闭");
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
		header.setMqSeq(Serial.genSerialNo(CommonConstant.Sequence.SendCrmMqSeq
				.toString()));
		String jmsXmlRtn = "";
		try {
			logger.info("IntTxnSeq:{},RcvTransId:{}",new Object[]{vo.getTxnSeq(), vo.getTransIDH()});
//			logger.info("message send to crm:" + strXml);
//			log.info(" send message to crm,rcvTransId:{} ,intTxnSeq:{}",
//					vo.getTransIDH(), vo.getTxnSeq());
			log.info("内部流水:{},接收方交易流水:{},请求报文:{}",new Object[]{vo.getTxnSeq(), vo.getTransIDH(),strXml});
			super.sendMsg(header, strXml);
			// log.info("<-CRM: "+ jmsXmlRtn);
		} catch (AppBizException e) {
			logger.error("运行期异常:{}",e);
			logger.error("内部流水:{}",vo.getTxnSeq());
			log.error("运行期异常:{}",e);
			log.error("内部流水:{}",vo.getTxnSeq());
//			logger.error("activemq AppBizException", e);
			throw e;
		} catch (Exception e) {
			logger.error("未知异常:{}",e);
			logger.error("内部流水:{}",vo.getTxnSeq());
			log.error("未知异常:{}",e);
			log.error("内部流水:{}",vo.getTxnSeq());
//			logger.error("activemq Exception", e);
			AppBizException e1 = new AppBizException(
					RspCodeConstant.Upay.UPAY_U99999.getValue(), e);
			throw e1;
		}
		logger.info("IntTxnSeq:{},RcvTransId:{}",new Object[]{vo.getTxnSeq(), vo.getTransIDH()});
//		logger.info("message received from crm:" + jmsXmlRtn);
		MsgHandle.unmarshaller(crmMsgVoRtn, jmsXmlRtn);
		logger.info("SendCrmJmsMessageImpl sendMsg(CrmMsgVo) - end");

	}

}
