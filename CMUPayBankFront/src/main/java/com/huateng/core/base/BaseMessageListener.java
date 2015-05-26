package com.huateng.core.base;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.huateng.core.common.CoreConstant;

public abstract class BaseMessageListener implements MessageListener {

	private JmsTemplate template;
	private String md5key;
	/**
	 * JMS监听方法
	 */
	public abstract void onMessage(Message message);

	/**
	 * JMS交易处理结果消息回复
	 * 
	 * @param replyToDest
	 *            返回消息的队列
	 * @param replayMsg
	 *            回复的消息
	 * @param reqTxnSeq
	 *            队列流水号
	 */
	public void replayMsg(Destination replyToDest, final String replayMsg, final String reqTxnSeq) {
		if (replyToDest != null) {
			template.send(replyToDest, new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					Message msg = session.createTextMessage(replayMsg);
					msg.setStringProperty(CoreConstant.RequestMsg.REQ_TXN_SEQ, reqTxnSeq);
					return msg;
				}
			});
		}
	}

	public JmsTemplate getTemplate() {
		return template;
	}

	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	public String getMd5key() {
		return md5key;
	}

	public void setMd5key(String md5key) {
		this.md5key = md5key;
	}

}
