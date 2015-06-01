package com.huateng.cmupay.parseMsg.reflect.handle;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.controller.service.system.IUpayCsysBatCutCtlService;
import com.huateng.cmupay.controller.service.system.IUpayCsysSeqMapInfoService;
import com.huateng.cmupay.models.UpayCsysRouteInfo;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.tools.ApplicationContextBean;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.utils.DateUtil;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * @author cmt
 * @version 创建时间：2013-3-2 上午9:37:42 类说明
 */
//@Component
public class BaseMsgVo {
	//去掉不拼的属性

	/**
	 * desc:平台交易数据库日切日期
	 */
	@XStreamOmitField
	private String txnDate = "";
	/**
	 * desc:平台交易今天时间
	 */
	@XStreamOmitField
	private String txnTime = "";
	/**
	 * desc:平台交易流水号
	 */
	@XStreamOmitField
	private String txnSeq = "";
	/**
	 * desc:交易流水表唯一流水号
	 */
	@SuppressWarnings("unused")
	@XStreamOmitField
	private Long seqId = 0L;
	/**
	 * desc:mq流水号
	 */
	@XStreamOmitField
	private String mqSeq = "";
	/**
	 * desc:路由信息
	 */
	@XStreamOmitField
	private UpayCsysRouteInfo routeInfo;
	/**
	 * desc:内部交易码
	 */
	@XStreamOmitField
	private UpayCsysTransCode transCode;
	
	public BaseMsgVo() {

//		IUpayCsysBatCutCtlService service1 =	(IUpayCsysBatCutCtlService)(ApplicationContextBean.getBean("upayCsysBatCutCtlService"));
//		String intTxnDate = service1.findCutOffDate(ExcConstant.CUT_OFF_DATE);
//		this.txnDate = intTxnDate;
//		IUpayCsysSeqMapInfoService service2 =	(IUpayCsysSeqMapInfoService)(ApplicationContextBean.getBean("upayCsysSeqMapInfoService"));
//		Long seqId = service2.selectSeqValue(ExcConstant.TXN_LOG_SEQ);
//		this.seqId = seqId;
//		this.txnTime = DateUtil.getDateyyyyMMddHHmmssSSS();
//		this.txnSeq = Serial.genSerialNo(CommonConstant.Sequence.OprId.toString());
		
		
		
	}
	




	public String getTxnDate() {
		if(null == txnDate || "".equals(txnDate)){
			IUpayCsysBatCutCtlService service1 =	(IUpayCsysBatCutCtlService)(ApplicationContextBean.getBean("upayCsysBatCutCtlService"));
			String intTxnDate = service1.findCutOffDate(ExcConstant.CUT_OFF_DATE);
			this.txnDate = intTxnDate;
		}
		return txnDate;
	}

	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}

	public String getTxnTime() {
		if(null == txnTime || "".equals(txnTime)){
			this.txnTime = DateUtil.getDateyyyyMMddHHmmssSSS();
		}
		return txnTime;
	}

	public void setTxnTime(String txnTime) {
		this.txnTime = txnTime;
	}




	public String getMqSeq() {
		return mqSeq;
	}

	public void setMqSeq(String mqSeq) {
		this.mqSeq = mqSeq;
	}

	public UpayCsysRouteInfo getRouteInfo() {
		return routeInfo;
	}

	public void setRouteInfo(UpayCsysRouteInfo routeInfo) {
		this.routeInfo = routeInfo;
	}

	public UpayCsysTransCode getTransCode() {
		return transCode;
	}

	public void setTransCode(UpayCsysTransCode transCode) {
		this.transCode = transCode;
	}

	public String getTxnSeq() {
		if(null == txnSeq || "".equals(txnSeq)){
			this.txnSeq = Serial.genSerialNo(CommonConstant.Sequence.IntSeq.toString());
		}
		return txnSeq;
	}

	public void setTxnSeq(String txnSeq) {
		this.txnSeq = txnSeq;
	}

	public Long getSeqId() {
		if(null == seqId || 0L == seqId){
			IUpayCsysSeqMapInfoService service2 =	(IUpayCsysSeqMapInfoService)(ApplicationContextBean.getBean("upayCsysSeqMapInfoService"));
			Long seqId = service2.selectSeqValue(ExcConstant.TXN_LOG_SEQ);
			this.seqId = seqId;
		}
		return seqId;
	}

	public void setSeqId(Long seqId) {
		this.seqId = seqId;
	}
	

}
