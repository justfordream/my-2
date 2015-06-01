package com.huateng.cmupay.hessian.message;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CorePayResultNoticeReq;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.json.JacksonUtils;

public class SendGateHessianMessageImpl {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private JmsTemplate template;
	private Destination destination;

	public Object sendMsg(CorePayResultNoticeReq msgVo) throws AppBizException {
		logger.info("###########start 发送报文到支付网关");
		final String jsonMsg = JacksonUtils.bean2Json(msgVo);
		final String appCd = CommonConstant.TransCode.PayNotice2Gate.getValue();
		final String reqTxnSeq = Serial
				.genSerialNo(CommonConstant.Sequence.Send2Gate.toString());
		template.send(destination, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {

				Message msg = session.createTextMessage(jsonMsg);
				// msg.setJMSReplyTo(respDest);
				// 消息选择器设定
				msg.setStringProperty("appCd", appCd);
				msg.setStringProperty("reqTxnSeq", reqTxnSeq);
				return msg;
			}
		});
		logger.info("########### end 发送报文到支付网关");
		return null;
	}

	public JmsTemplate getTemplate() {
		return template;
	}

	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

}
