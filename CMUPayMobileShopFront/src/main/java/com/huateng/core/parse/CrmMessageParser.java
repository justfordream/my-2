package com.huateng.core.parse;

import java.util.Map;

import com.huateng.mmarket.bean.head.GPay;

/**
 * @author qingxue.li
 * 
 *         天猫报文to移动报文转换
 */
public interface CrmMessageParser {

	/**
	 * 组装请求报文
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map assemblyCrmReqMsg(GPay gPay,String txnSeq,String transIDHTime);
	
	
	/**
	 * 组装应答报文
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map assemblyTMallResMsg(GPay gpay, String crmResXml,String txnSeq,String transIdHTime);

}
