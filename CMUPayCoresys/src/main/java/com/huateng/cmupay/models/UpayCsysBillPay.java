package com.huateng.cmupay.models;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  交易成功明细表
 * 对应的数据库表：UPAY_CSYS_BILL_PAY
 */
public class UpayCsysBillPay {
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
    //银行日期
    private String settleDate;
    //平台对账方
    private String diffPower;
    //业务渠道
    private String bussChl;
    //发起方机构编码
    private String reqDomain;
    //发起方渠道标识
    private String reqCnlType;
    //发起方业务代码
    private String reqBipCode;
    //发起方交易代码
    private String reqActivityCode;
    //发起方交易请求流水号
    private String reqTransId;
    //发起方交易请求的日期
    private String reqTransDt;
    //发起方交易请求的时间
    private String reqTransTm;
    //发起方操作流水号
    private String reqOprId;
    //外部机构编码
    private String outerDomain;
    //外部交易代码
    private String outerActivityCode;
    //外部交易流水号
    private String outerTransId;
    //外部交易的日期
    private String outerTransDt;
    //外部交易的时间
    private String outerTransTm;
    //外部方操作流水号
    private String outerOprId;
    //路由信息表外部路由信息
    private String outerRouteInfo;
    //接收方机构编码
    private String rcvDomain;
    //接收方交易代码
    private String rcvActivityCode;
    //接收方交易流水号
    private String rcvTransId;
    //接收方交易请求的日期
    private String rcvTransDt;
    //收方交易的时间
    private String rcvTransTm;
    //接收方操作流水号
    private String rcvOprId;
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
    //缴费类型
    private String payedType;
    //应缴费金额
    private Long needPayAmt;
    //实际缴费金额
    private Long payAmt;
    //酬金
    private Long commisionAmt;
    //返销标示
    private String backFlag;
    //退款标示
    private String refundFlag;
    //冲正标示
    private String reverseFlag;
    //已开票标示
    private String invoiceFlag;
    //是否已经对账
    private String reconciliationFlag;
    //状态
    private String status;
    //最后修改操作员
    private String lastUpdOprid;
    //最后修改时间
    private String lastUpdTime;
    //原流水号
    private String lSeqId;
    //保留域1
    private String reserved1;
    //保留域2
    private String reserved2;
    //保留域3
    private String reserved3;

  
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

    public Long getSeqId() {
		return seqId;
	}

	public void setSeqId(Long seqId) {
		this.seqId = seqId;
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

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public String getDiffPower() {
        return diffPower;
    }

    public void setDiffPower(String diffPower) {
        this.diffPower = diffPower;
    }

    public String getBussChl() {
        return bussChl;
    }

    public void setBussChl(String bussChl) {
        this.bussChl = bussChl;
    }

    public String getReqDomain() {
        return reqDomain;
    }

    public void setReqDomain(String reqDomain) {
        this.reqDomain = reqDomain;
    }

    public String getReqCnlType() {
        return reqCnlType;
    }

    public void setReqCnlType(String reqCnlType) {
        this.reqCnlType = reqCnlType;
    }

    public String getReqBipCode() {
        return reqBipCode;
    }

    public void setReqBipCode(String reqBipCode) {
        this.reqBipCode = reqBipCode;
    }

    public String getReqActivityCode() {
        return reqActivityCode;
    }

    public void setReqActivityCode(String reqActivityCode) {
        this.reqActivityCode = reqActivityCode;
    }

    public String getReqTransId() {
        return reqTransId;
    }

    public void setReqTransId(String reqTransId) {
        this.reqTransId = reqTransId;
    }

    public String getReqTransDt() {
        return reqTransDt;
    }

    public void setReqTransDt(String reqTransDt) {
        this.reqTransDt = reqTransDt;
    }

    public String getReqTransTm() {
        return reqTransTm;
    }

    public void setReqTransTm(String reqTransTm) {
        this.reqTransTm = reqTransTm;
    }

    public String getReqOprId() {
        return reqOprId;
    }

    public void setReqOprId(String reqOprId) {
        this.reqOprId = reqOprId;
    }

    public String getOuterDomain() {
        return outerDomain;
    }

    public void setOuterDomain(String outerDomain) {
        this.outerDomain = outerDomain;
    }

    public String getOuterActivityCode() {
        return outerActivityCode;
    }

    public void setOuterActivityCode(String outerActivityCode) {
        this.outerActivityCode = outerActivityCode;
    }

    public String getOuterTransId() {
        return outerTransId;
    }

    public void setOuterTransId(String outerTransId) {
        this.outerTransId = outerTransId;
    }

    public String getOuterTransDt() {
        return outerTransDt;
    }

    public void setOuterTransDt(String outerTransDt) {
        this.outerTransDt = outerTransDt;
    }

    public String getOuterTransTm() {
        return outerTransTm;
    }

    public void setOuterTransTm(String outerTransTm) {
        this.outerTransTm = outerTransTm;
    }

    public String getOuterOprId() {
        return outerOprId;
    }

    public void setOuterOprId(String outerOprId) {
        this.outerOprId = outerOprId;
    }

    public String getOuterRouteInfo() {
        return outerRouteInfo;
    }

    public void setOuterRouteInfo(String outerRouteInfo) {
        this.outerRouteInfo = outerRouteInfo;
    }

    public String getRcvDomain() {
        return rcvDomain;
    }

    public void setRcvDomain(String rcvDomain) {
        this.rcvDomain = rcvDomain;
    }

    public String getRcvActivityCode() {
        return rcvActivityCode;
    }

    public void setRcvActivityCode(String rcvActivityCode) {
        this.rcvActivityCode = rcvActivityCode;
    }

    public String getRcvTransId() {
        return rcvTransId;
    }

    public void setRcvTransId(String rcvTransId) {
        this.rcvTransId = rcvTransId;
    }

    public String getRcvTransDt() {
        return rcvTransDt;
    }

    public void setRcvTransDt(String rcvTransDt) {
        this.rcvTransDt = rcvTransDt;
    }

    public String getRcvTransTm() {
        return rcvTransTm;
    }

    public void setRcvTransTm(String rcvTransTm) {
        this.rcvTransTm = rcvTransTm;
    }

    public String getRcvOprId() {
        return rcvOprId;
    }

    public void setRcvOprId(String rcvOprId) {
        this.rcvOprId = rcvOprId;
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

    public String getPayedType() {
        return payedType;
    }

    public void setPayedType(String payedType) {
        this.payedType = payedType;
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

    public Long getCommisionAmt() {
        return commisionAmt;
    }

    public void setCommisionAmt(Long commisionAmt) {
        this.commisionAmt = commisionAmt;
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

    public String getReconciliationFlag() {
        return reconciliationFlag;
    }

    public void setReconciliationFlag(String reconciliationFlag) {
        this.reconciliationFlag = reconciliationFlag;
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

    public String getlSeqId() {
        return lSeqId;
    }

    public void setlSeqId(String lSeqId) {
        this.lSeqId = lSeqId;
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
}