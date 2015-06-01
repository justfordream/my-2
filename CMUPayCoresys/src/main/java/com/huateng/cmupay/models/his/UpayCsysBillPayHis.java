package com.huateng.cmupay.models.his;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  交易成功明细历史表
 * 对应的数据库表：UPAY_CSYS_BILL_PAY_HIS
 */
public class UpayCsysBillPayHis {
	//流水号
    private Long seqId;
    //内部交易流水号
    private String intTxnSeq;
    //内部交易代码
    private String intTransCode;
    //内部交易日期
    private String intTxnDate;
    //内部交易时间
    private String intTxnTime;
    //业务大类
    private String bussType;
    //发起方交易方式
    private String payMode;
    //发起方渠道标识
    private String reqCnlType;
    //路由信息表外部路由信息
    private String outerRouteInfo;
    //路由信息表接受方路由信息
    private String rcvRouteInfo;
    //用户号码归属地
    private String idProvince;
    //用户号码标识类型
    private String idType;
    //用户号码
    private String idValue;
    //银行编码
    private String bankId;
    //银行账号类型
    private String bankAcctType;
    //银行账号
    private String bankAccId;
    //缴费方式
    private String payType;
    //应缴费金额
    private Long needPayAmt;
    //实际缴费金额
    private Long payAmt;
    //返销标示
    private String backFlag;
    //退款标示
    private String refundFlag;
    //冲正标示
    private String reverseFlag;
    //已开票标示
    private String invoiceFlag;
    //状态
    private String status;
    //最后修改操作员
    private String lastUpdOprid;
    //最后修改时间
    private String lastUpdTime;
    //保留域1
    private String reserved1;
    //保留域2
    private String reserved2;
    //保留域3
    private String reserved3;
    //会计日期
    private String accountDate;
	public Long getSeqId() {
		return seqId;
	}
	public void setSeqId(Long seqId) {
		this.seqId = seqId;
	}
	public String getIntTxnSeq() {
		return intTxnSeq;
	}
	public void setIntTxnSeq(String intTxnSeq) {
		this.intTxnSeq = intTxnSeq;
	}
	public String getIntTransCode() {
		return intTransCode;
	}
	public void setIntTransCode(String intTransCode) {
		this.intTransCode = intTransCode;
	}
	public String getIntTxnDate() {
		return intTxnDate;
	}
	public void setIntTxnDate(String intTxnDate) {
		this.intTxnDate = intTxnDate;
	}
	public String getIntTxnTime() {
		return intTxnTime;
	}
	public void setIntTxnTime(String intTxnTime) {
		this.intTxnTime = intTxnTime;
	}
	public String getBussType() {
		return bussType;
	}
	public void setBussType(String bussType) {
		this.bussType = bussType;
	}
	public String getPayMode() {
		return payMode;
	}
	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}
	public String getReqCnlType() {
		return reqCnlType;
	}
	public void setReqCnlType(String reqCnlType) {
		this.reqCnlType = reqCnlType;
	}
	public String getOuterRouteInfo() {
		return outerRouteInfo;
	}
	public void setOuterRouteInfo(String outerRouteInfo) {
		this.outerRouteInfo = outerRouteInfo;
	}
	public String getRcvRouteInfo() {
		return rcvRouteInfo;
	}
	public void setRcvRouteInfo(String rcvRouteInfo) {
		this.rcvRouteInfo = rcvRouteInfo;
	}
	public String getIdProvince() {
		return idProvince;
	}
	public void setIdProvince(String idProvince) {
		this.idProvince = idProvince;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getIdValue() {
		return idValue;
	}
	public void setIdValue(String idValue) {
		this.idValue = idValue;
	}
	public String getBankId() {
		return bankId;
	}
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	public String getBankAcctType() {
		return bankAcctType;
	}
	public void setBankAcctType(String bankAcctType) {
		this.bankAcctType = bankAcctType;
	}
	public String getBankAccId() {
		return bankAccId;
	}
	public void setBankAccId(String bankAccId) {
		this.bankAccId = bankAccId;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public Long getNeedPayAmt() {
		return needPayAmt;
	}
	public void setNeedPayAmt(Long needPayAmt) {
		this.needPayAmt = needPayAmt;
	}
	public Long getPayAmt() {
		return payAmt;
	}
	public void setPayAmt(Long payAmt) {
		this.payAmt = payAmt;
	}
	public String getBackFlag() {
		return backFlag;
	}
	public void setBackFlag(String backFlag) {
		this.backFlag = backFlag;
	}
	public String getRefundFlag() {
		return refundFlag;
	}
	public void setRefundFlag(String refundFlag) {
		this.refundFlag = refundFlag;
	}
	public String getReverseFlag() {
		return reverseFlag;
	}
	public void setReverseFlag(String reverseFlag) {
		this.reverseFlag = reverseFlag;
	}
	public String getInvoiceFlag() {
		return invoiceFlag;
	}
	public void setInvoiceFlag(String invoiceFlag) {
		this.invoiceFlag = invoiceFlag;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLastUpdOprid() {
		return lastUpdOprid;
	}
	public void setLastUpdOprid(String lastUpdOprid) {
		this.lastUpdOprid = lastUpdOprid;
	}
	public String getLastUpdTime() {
		return lastUpdTime;
	}
	public void setLastUpdTime(String lastUpdTime) {
		this.lastUpdTime = lastUpdTime;
	}
	public String getReserved1() {
		return reserved1;
	}
	public void setReserved1(String reserved1) {
		this.reserved1 = reserved1;
	}
	public String getReserved2() {
		return reserved2;
	}
	public void setReserved2(String reserved2) {
		this.reserved2 = reserved2;
	}
	public String getReserved3() {
		return reserved3;
	}
	public void setReserved3(String reserved3) {
		this.reserved3 = reserved3;
	}
	public String getAccountDate() {
		return accountDate;
	}
	public void setAccountDate(String accountDate) {
		this.accountDate = accountDate;
	}
}