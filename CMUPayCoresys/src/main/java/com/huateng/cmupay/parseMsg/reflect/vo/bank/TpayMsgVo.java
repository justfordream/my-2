package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import java.io.Serializable;
import java.util.Map;

import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;

public class TpayMsgVo extends BaseMsgVo {

	//发往银联 key-value
	private Map<String,String> tpayReqData;
	
	//银联应答 key-value
	private Map<String,String> tpayRspData;

	//各交易对应的.do、.action
	private String reqPathAppend;
	
	//第三方支付机构代码  银联0057
	private String msgReceiver;
	
	//核心发往银联的请求流水
	private String transIDO;


	public Map<String, String> getTpayReqData() {
		return tpayReqData;
	}

	public void setTpayReqData(Map<String, String> tpayReqData) {
		this.tpayReqData = tpayReqData;
	}

	public Map<String, String> getTpayRspData() {
		return tpayRspData;
	}

	public void setTpayRspData(Map<String, String> tpayRspData) {
		this.tpayRspData = tpayRspData;
	}

	public String getMsgReceiver() {
		return msgReceiver;
	}

	public void setMsgReceiver(String msgReceiver) {
		this.msgReceiver = msgReceiver;
	}

	public String getTransIDO() {
		return transIDO;
	}

	public void setTransIDO(String transIDO) {
		this.transIDO = transIDO;
	}

	public String getReqPathAppend() {
		return reqPathAppend;
	}

	public void setReqPathAppend(String reqPathAppend) {
		this.reqPathAppend = reqPathAppend;
	}
	


}
