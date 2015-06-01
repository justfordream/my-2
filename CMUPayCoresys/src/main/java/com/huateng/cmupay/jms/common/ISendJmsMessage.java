package com.huateng.cmupay.jms.common;

import com.huateng.cmupay.exception.AppBizException;

/**
 * @author cmt
 *

 */
public interface ISendJmsMessage {

	
	

	String sendMsg(JmsMsgHeader jmsMsgHeader, String jmsBody)
			throws AppBizException;
}
