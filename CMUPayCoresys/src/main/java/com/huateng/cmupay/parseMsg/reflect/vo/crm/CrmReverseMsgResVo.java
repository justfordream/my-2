package com.huateng.cmupay.parseMsg.reflect.vo.crm;

import com.huateng.cmupay.parseMsg.reflect.handle.CustomAnnotation;

/***
 * crm 冲正应答报文体
 * @author zeng.j
 *
 */
public class CrmReverseMsgResVo {
		//原交易发起方系统标识
		@CustomAnnotation(path = "SvcCont.RefundsRsp.OriReqSys")
		private String OriReqSys;
		//原交易发起方操作请求日期
		@CustomAnnotation(path = "SvcCont.RefundsRsp.OriActionDate")
		private String OriActionDate;
		//原操作流水号:发起方为UPSS，为充值操作流水号；发起方为省公司，为缴费操作流水号
		@CustomAnnotation(path = "SvcCont.RefundsRsp.OriTransactionID")
		private String OriTransactionID;
		//二级应答码
		@CustomAnnotation(path="SvcCont.RefundsRsp.RspCode")
		private String rspCode;
		//应答描述
		@CustomAnnotation(path="SvcCont.RefundsRsp.RspInfo")
		private String rspInfo;
		//操作流水号
		@CustomAnnotation(path = "SvcCont.RefundsRsp.TransactionID")
		private String TransactionID;
		//交易帐期:交易成功时，upss应答必填
		@CustomAnnotation(path = "SvcCont.RefundsRsp.SettleDate")
		private String SettleDate;

		public String getOriReqSys() {
			return OriReqSys;
		}
		public void setOriReqSys(String oriReqSys) {
			OriReqSys = oriReqSys;
		}
		public String getOriActionDate() {
			return OriActionDate;
		}
		public void setOriActionDate(String oriActionDate) {
			OriActionDate = oriActionDate;
		}
		public String getOriTransactionID() {
			return OriTransactionID;
		}
		public void setOriTransactionID(String oriTransactionID) {
			OriTransactionID = oriTransactionID;
		}
		public String getTransactionID() {
			return TransactionID;
		}
		public void setTransactionID(String transactionID) {
			TransactionID = transactionID;
		}
		public String getSettleDate() {
			return SettleDate;
		}
		public void setSettleDate(String settleDate) {
			SettleDate = settleDate;
		}
		public String getRspCode() {
			return rspCode;
		}
		public void setRspCode(String rspCode) {
			this.rspCode = rspCode;
		}
		public String getRspInfo() {
			return rspInfo;
		}
		public void setRspInfo(String rspInfo) {
			this.rspInfo = rspInfo;
		}
}
