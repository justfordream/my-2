package com.huateng.core.common;

import javax.jms.Message;

import com.huateng.mmarket.bean.head.GPay;



/**
 * @author cmt
 *
 */
public interface IClientSendMessage {


	public void recvmessagefromserver(Message recvmessage);

	void aSyncSendMsg(final String seq, final String orgId, final String context, final String txnCd, 
			 final Long hNum);
	public String sendMsg(final String seq, final String orgId, final String context, final String txnCd, 
			 final Long hNum,GPay pay) throws Exception;
	public String sendTextRtnMessage(final String seq, final String orgId, final String context, final String txnCd, 
			 final Long hNum,GPay pay)
			throws Exception;
	
	public String getResponseXML(String body, String context)throws Exception;
}
