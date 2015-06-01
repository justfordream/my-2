package com.huateng.cmupay.parseMsg.reflect.vo.bank;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  银行侧冲正响应报文体
 */
public class BankReverseMsgRespVo {

	//二级应答码-----0000-成功；0001-找不到该交易；0002-无法冲正；9999-其他原因异常
			//原交易发起方系统标识
			@CustomAnnotation(path = "SvcCont.AuthReq.OrigReqSys")
			private String OrigRspSys;
			//原交易发起方操作请求日期
			@CustomAnnotation(path = "SvcCont.AuthReq.ActionDate")
			private String ActionDate;
			//原交易发起方操作流水号
			@CustomAnnotation(path = "SvcCont.AuthReq.TransactionID")
			private String TransactionID;
			//二级应答码
			private String RspCode;
			//应答描述
			private String RspInfo;
			public String getOrigRspSys() {
				return OrigRspSys;
			}
			public void setOrigRspSys(String origRspSys) {
				OrigRspSys = origRspSys;
			}
			public String getActionDate() {
				return ActionDate;
			}
			public void setActionDate(String actionDate) {
				ActionDate = actionDate;
			}
			public String getTransactionID() {
				return TransactionID;
			}
			public void setTransactionID(String transactionID) {
				TransactionID = transactionID;
			}
			public String getRspCode() {
				return RspCode;
			}
			public void setRspCode(String rspCode) {
				RspCode = rspCode;
			}
			public String getRspInfo() {
				return RspInfo;
			}
			public void setRspInfo(String rspInfo) {
				RspInfo = rspInfo;
			}
			
}
