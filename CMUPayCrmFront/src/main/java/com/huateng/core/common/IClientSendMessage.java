package com.huateng.core.common;

import javax.jms.Message;



/**
 * @author cmt
 *
 */
public interface IClientSendMessage {


	public void recvmessagefromserver(Message recvmessage);

	void aSyncSendMsg(final String seq, final String orgId, final String context, final String txnCd, 
			 final Long hNum);
	Message sendMsg(final String seq, final String orgId, final String context, final String txnCd, 
			 final Long hNum) throws Exception;
	String sendTextRtnMessage(final String seq, final String orgId, final String context, final String txnCd, 
			 final Long hNum)
			throws Exception;
}
