package com.huateng.mmarket.common.parse;


import java.util.Map;

import com.huateng.core.parse.CrmMessageParser;
import com.huateng.mmarket.bean.head.GPay;

/**
 * @author qingxue.li
 * 交易结果查询
 */
public class CrmQueryResultParser implements CrmMessageParser {

	
	@SuppressWarnings("rawtypes")
	@Override
	public Map assemblyCrmReqMsg(GPay gPay, String txnSeq, String transIDHTime) {
		
		return null;
	}
	@SuppressWarnings("rawtypes")
	@Override
	public Map assemblyTMallResMsg(GPay gpay, String crmResXml, String txnSeq,
			String transIdHTime) {
		// TODO Auto-generated method stub
		return null;
	}

}
