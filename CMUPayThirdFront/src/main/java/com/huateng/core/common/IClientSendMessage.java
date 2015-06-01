package com.huateng.core.common;

import java.io.Serializable;
import java.util.Map;

import javax.jms.Message;



/**
 * 支付前置发送消息到核心的接口
 * @author cmt
 *
 */
public interface IClientSendMessage {


	public void recvmessagefromserver(Message recvmessage);

	/**
	 * 发送异步消息，无需对方返回
	 * @param destId  目标队列在配置文件（applicationContext-tupay-jms.xml）中的编号，不填则默认
	 * @param objectMsg
	 */
	void aSyncSendMsg(String destId, final Serializable objectMsg, final Map<String, String> paramMap);
	
	Message sendMsg(final String seq, final String destId, final Map<String, String> paramsMap, final String txnCd, 
			 final Long hNum) throws Exception;
	
	String sendTextRtnMessage(final String seq, final String destId, final Map<String, String> paramsMap, final String txnCd, 
			 final Long hNum)
			throws Exception;
}
