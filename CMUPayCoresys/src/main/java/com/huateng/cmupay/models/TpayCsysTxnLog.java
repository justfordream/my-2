package com.huateng.cmupay.models;


public class TpayCsysTxnLog {
    private Long seqId;

    private String intTxnSeq;

    private String intTransCode;

    private String intTxnDate;

    private String intTxnTime;

    private String settleDate;

    private String payMode;

    private String bussType;

    private String bussChl;

    private String reqBipCode;

    private String reqActivityCode;

    private String reqDomain;

    private String reqSessionId;

    private String reqTransId;

    private String reqTransDt;

    private String reqTransTm;

    private String reqTranshId;

    private String reqTranshDt;

    private String reqTranshTm;

    private String reqOprId;

    private String reqOprDt;

    private String reqOprTm;

    private String reqCnlType;

    private String outerStartTm;

    private String outerEndTm;

    private String rcvDomain;

    private String rcvSessionId;

    private String rcvTransId;

    private String rcvTransDt;

    private String rcvTransTm;

    private String rcvTranshId;

    private String rcvTranshDt;

    private String rcvTranshTm;

    private String rcvOprId;

    private String rcvOprDt;

    private String rcvOprTm;

    private String rcvCnlType;

    private String rcvStartTm;

    private String rcvEndTm;

    private String mainFlag;

    private String mainIdProvince;

    private String mainIdType;

    private String mainIdValue;

    private String idProvince;

    private String idType;

    private String idValue;

    private String signStatus;

    private String bankId;

    private String bankAcctType;

    private String bankAccId;

    private String oriOrgId;

    private String oriOprTransId;

    private String oriReqDate;

    private String orderId;

    private String orderTm;

    private String merId;

    private String merVar;

    private String subId;

    private String subTime;

    private String payType;

    private Long rechAmount;

    private Long rechThreshold;

    private Long needPayAmt;

    private Long payAmt;

    private String payedType;

    private String chlRspType;

    private String chlRspCode;

    private String chlSubRspCode;

    private String chlRspDesc;

    private String chlSubRspDesc;

    private String outerRspType;

    private String outerRspCode;

    private String outerSubRspCode;

    private String outerRspDesc;

    private String outerSubRspDesc;

    private String rcvRspType;

    private String rcvRspCode;

    private String rcvSubRspCode;

    private String rcvRspDesc;

    private String rcvSubRspDesc;

    private String backFlag;

    private String refundFlag;

    private String reverseFlag;

    private String reconciliationFlag;

    private String status;

    private String lastUpdOprid;

    private String lastUpdTime;

    private String backUrl;

    private String serverUrl;

    private String lSeqId;

    private String clientIp;

    private Integer orderCnt;

    private String productNo;

    private Integer commision;

    private Integer rebateFee;

    private Integer prodDiscount;

    private Integer creditCardFee;

    private Integer serviceFee;

    private String activityNo;

    private String productShelfNo;

    private String isDel;

    private String payTransId;

    private String discount;

    private Integer chargeMoney;

    private String thrVersion;

    private String thrTxnType;

    private String thrSubTxnType;

    private String thrProductType;

    private String accessType;

    private String acquirerOgrCode;

    private String payTimeoutDt;

    private String thrPayType;

    private String thrTransId;

    private String payStatus;

    private String cardOrgCode;

    private Integer settleAmt;

    private String traceNo;

    private String traceTime;
    
    // 5月19号新增字段
    private String mobileShopMerId;
    
    private String smsType;
    
    private String customerInfo;
    
    private String activateStatus;
    
    private String batchNo;
    
    private String batchResult;
    
    private String goodsName;
    
    private String bankOrderId;
    
    private String notifyId;
    
    private String tradeLockedStatus;
    
    private String payCardType;
    
    private String checkFlag;
    
    private String timeEnd;
    
    private String thrPhishingKey;
    
    private String thrBankType;
    
    private String thrChannelType;
    
    private String thrChannelAmount;
    
    private String thrMerId;
    
    private String thrNotifyType;
    
    private String thrRefundId;
    
    private String thrRefundChannel;

    private String reserved1;

    private String reserved2;

    private String reserved3;
    
    private String revokeReason;

    public String getRevokeReason() {
		return revokeReason;
	}

	public void setRevokeReason(String revokeReason) {
		this.revokeReason = revokeReason;
	}

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

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getBussType() {
        return bussType;
    }

    public void setBussType(String bussType) {
        this.bussType = bussType;
    }

    public String getBussChl() {
        return bussChl;
    }

    public void setBussChl(String bussChl) {
        this.bussChl = bussChl;
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

    public String getReqDomain() {
        return reqDomain;
    }

    public void setReqDomain(String reqDomain) {
        this.reqDomain = reqDomain;
    }

    public String getReqSessionId() {
        return reqSessionId;
    }

    public void setReqSessionId(String reqSessionId) {
        this.reqSessionId = reqSessionId;
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

    public String getReqTranshId() {
        return reqTranshId;
    }

    public void setReqTranshId(String reqTranshId) {
        this.reqTranshId = reqTranshId;
    }

    public String getReqTranshDt() {
        return reqTranshDt;
    }

    public void setReqTranshDt(String reqTranshDt) {
        this.reqTranshDt = reqTranshDt;
    }

    public String getReqTranshTm() {
        return reqTranshTm;
    }

    public void setReqTranshTm(String reqTranshTm) {
        this.reqTranshTm = reqTranshTm;
    }

    public String getReqOprId() {
        return reqOprId;
    }

    public void setReqOprId(String reqOprId) {
        this.reqOprId = reqOprId;
    }

    public String getReqOprDt() {
        return reqOprDt;
    }

    public void setReqOprDt(String reqOprDt) {
        this.reqOprDt = reqOprDt;
    }

    public String getReqOprTm() {
        return reqOprTm;
    }

    public void setReqOprTm(String reqOprTm) {
        this.reqOprTm = reqOprTm;
    }

    public String getReqCnlType() {
        return reqCnlType;
    }

    public void setReqCnlType(String reqCnlType) {
        this.reqCnlType = reqCnlType;
    }

    public String getOuterStartTm() {
        return outerStartTm;
    }

    public void setOuterStartTm(String outerStartTm) {
        this.outerStartTm = outerStartTm;
    }

    public String getOuterEndTm() {
        return outerEndTm;
    }

    public void setOuterEndTm(String outerEndTm) {
        this.outerEndTm = outerEndTm;
    }

    public String getRcvDomain() {
        return rcvDomain;
    }

    public void setRcvDomain(String rcvDomain) {
        this.rcvDomain = rcvDomain;
    }

    public String getRcvSessionId() {
        return rcvSessionId;
    }

    public void setRcvSessionId(String rcvSessionId) {
        this.rcvSessionId = rcvSessionId;
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

    public String getRcvTranshId() {
        return rcvTranshId;
    }

    public void setRcvTranshId(String rcvTranshId) {
        this.rcvTranshId = rcvTranshId;
    }

    public String getRcvTranshDt() {
        return rcvTranshDt;
    }

    public void setRcvTranshDt(String rcvTranshDt) {
        this.rcvTranshDt = rcvTranshDt;
    }

    public String getRcvTranshTm() {
        return rcvTranshTm;
    }

    public void setRcvTranshTm(String rcvTranshTm) {
        this.rcvTranshTm = rcvTranshTm;
    }

    public String getRcvOprId() {
        return rcvOprId;
    }

    public void setRcvOprId(String rcvOprId) {
        this.rcvOprId = rcvOprId;
    }

    public String getRcvOprDt() {
        return rcvOprDt;
    }

    public void setRcvOprDt(String rcvOprDt) {
        this.rcvOprDt = rcvOprDt;
    }

    public String getRcvOprTm() {
        return rcvOprTm;
    }

    public void setRcvOprTm(String rcvOprTm) {
        this.rcvOprTm = rcvOprTm;
    }

    public String getRcvCnlType() {
        return rcvCnlType;
    }

    public void setRcvCnlType(String rcvCnlType) {
        this.rcvCnlType = rcvCnlType;
    }

    public String getRcvStartTm() {
        return rcvStartTm;
    }

    public void setRcvStartTm(String rcvStartTm) {
        this.rcvStartTm = rcvStartTm;
    }

    public String getRcvEndTm() {
        return rcvEndTm;
    }

    public void setRcvEndTm(String rcvEndTm) {
        this.rcvEndTm = rcvEndTm;
    }

    public String getMainFlag() {
        return mainFlag;
    }

    public void setMainFlag(String mainFlag) {
        this.mainFlag = mainFlag;
    }

    public String getMainIdProvince() {
        return mainIdProvince;
    }

    public void setMainIdProvince(String mainIdProvince) {
        this.mainIdProvince = mainIdProvince;
    }

    public String getMainIdType() {
        return mainIdType;
    }

    public void setMainIdType(String mainIdType) {
        this.mainIdType = mainIdType;
    }

    public String getMainIdValue() {
        return mainIdValue;
    }

    public void setMainIdValue(String mainIdValue) {
        this.mainIdValue = mainIdValue;
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

    public String getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(String signStatus) {
        this.signStatus = signStatus;
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

    public String getOriOrgId() {
        return oriOrgId;
    }

    public void setOriOrgId(String oriOrgId) {
        this.oriOrgId = oriOrgId;
    }

    public String getOriOprTransId() {
        return oriOprTransId;
    }

    public void setOriOprTransId(String oriOprTransId) {
        this.oriOprTransId = oriOprTransId;
    }

    public String getOriReqDate() {
        return oriReqDate;
    }

    public void setOriReqDate(String oriReqDate) {
        this.oriReqDate = oriReqDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderTm() {
        return orderTm;
    }

    public void setOrderTm(String orderTm) {
        this.orderTm = orderTm;
    }

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

    public String getMerVar() {
        return merVar;
    }

    public void setMerVar(String merVar) {
        this.merVar = merVar;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public String getSubTime() {
        return subTime;
    }

    public void setSubTime(String subTime) {
        this.subTime = subTime;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Long getRechAmount() {
        return rechAmount;
    }

    public void setRechAmount(Long rechAmount) {
        this.rechAmount = rechAmount;
    }

    public Long getRechThreshold() {
        return rechThreshold;
    }

    public void setRechThreshold(Long rechThreshold) {
        this.rechThreshold = rechThreshold;
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

    public String getPayedType() {
        return payedType;
    }

    public void setPayedType(String payedType) {
        this.payedType = payedType;
    }

    public String getChlRspType() {
        return chlRspType;
    }

    public void setChlRspType(String chlRspType) {
        this.chlRspType = chlRspType;
    }

    public String getChlRspCode() {
        return chlRspCode;
    }

    public void setChlRspCode(String chlRspCode) {
        this.chlRspCode = chlRspCode;
    }

    public String getChlSubRspCode() {
        return chlSubRspCode;
    }

    public void setChlSubRspCode(String chlSubRspCode) {
        this.chlSubRspCode = chlSubRspCode;
    }

    public String getChlRspDesc() {
        return chlRspDesc;
    }

    public void setChlRspDesc(String chlRspDesc) {
        this.chlRspDesc = chlRspDesc;
    }

    public String getChlSubRspDesc() {
        return chlSubRspDesc;
    }

    public void setChlSubRspDesc(String chlSubRspDesc) {
        this.chlSubRspDesc = chlSubRspDesc;
    }

    public String getOuterRspType() {
        return outerRspType;
    }

    public void setOuterRspType(String outerRspType) {
        this.outerRspType = outerRspType;
    }

    public String getOuterRspCode() {
        return outerRspCode;
    }

    public void setOuterRspCode(String outerRspCode) {
        this.outerRspCode = outerRspCode;
    }

    public String getOuterSubRspCode() {
        return outerSubRspCode;
    }

    public void setOuterSubRspCode(String outerSubRspCode) {
        this.outerSubRspCode = outerSubRspCode;
    }

    public String getOuterRspDesc() {
        return outerRspDesc;
    }

    public void setOuterRspDesc(String outerRspDesc) {
        this.outerRspDesc = outerRspDesc;
    }

    public String getOuterSubRspDesc() {
        return outerSubRspDesc;
    }

    public void setOuterSubRspDesc(String outerSubRspDesc) {
        this.outerSubRspDesc = outerSubRspDesc;
    }

    public String getRcvRspType() {
        return rcvRspType;
    }

    public void setRcvRspType(String rcvRspType) {
        this.rcvRspType = rcvRspType;
    }

    public String getRcvRspCode() {
        return rcvRspCode;
    }

    public void setRcvRspCode(String rcvRspCode) {
        this.rcvRspCode = rcvRspCode;
    }

    public String getRcvSubRspCode() {
        return rcvSubRspCode;
    }

    public void setRcvSubRspCode(String rcvSubRspCode) {
        this.rcvSubRspCode = rcvSubRspCode;
    }

    public String getRcvRspDesc() {
        return rcvRspDesc;
    }

    public void setRcvRspDesc(String rcvRspDesc) {
        this.rcvRspDesc = rcvRspDesc;
    }

    public String getRcvSubRspDesc() {
        return rcvSubRspDesc;
    }

    public void setRcvSubRspDesc(String rcvSubRspDesc) {
        this.rcvSubRspDesc = rcvSubRspDesc;
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

    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getlSeqId() {
        return lSeqId;
    }

    public void setlSeqId(String lSeqId) {
        this.lSeqId = lSeqId;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Integer getOrderCnt() {
        return orderCnt;
    }

    public void setOrderCnt(Integer orderCnt) {
        this.orderCnt = orderCnt;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public Integer getCommision() {
        return commision;
    }

    public void setCommision(Integer commision) {
        this.commision = commision;
    }

    public Integer getRebateFee() {
        return rebateFee;
    }

    public void setRebateFee(Integer rebateFee) {
        this.rebateFee = rebateFee;
    }

    public Integer getProdDiscount() {
        return prodDiscount;
    }

    public void setProdDiscount(Integer prodDiscount) {
        this.prodDiscount = prodDiscount;
    }

    public Integer getCreditCardFee() {
        return creditCardFee;
    }

    public void setCreditCardFee(Integer creditCardFee) {
        this.creditCardFee = creditCardFee;
    }

    public Integer getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Integer serviceFee) {
        this.serviceFee = serviceFee;
    }

    public String getActivityNo() {
        return activityNo;
    }

    public void setActivityNo(String activityNo) {
        this.activityNo = activityNo;
    }

    public String getProductShelfNo() {
        return productShelfNo;
    }

    public void setProductShelfNo(String productShelfNo) {
        this.productShelfNo = productShelfNo;
    }

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }

    public String getPayTransId() {
        return payTransId;
    }

    public void setPayTransId(String payTransId) {
        this.payTransId = payTransId;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public Integer getChargeMoney() {
        return chargeMoney;
    }

    public void setChargeMoney(Integer chargeMoney) {
        this.chargeMoney = chargeMoney;
    }

    public String getThrVersion() {
        return thrVersion;
    }

    public void setThrVersion(String thrVersion) {
        this.thrVersion = thrVersion;
    }

    public String getThrTxnType() {
        return thrTxnType;
    }

    public void setThrTxnType(String thrTxnType) {
        this.thrTxnType = thrTxnType;
    }

    public String getThrSubTxnType() {
        return thrSubTxnType;
    }

    public void setThrSubTxnType(String thrSubTxnType) {
        this.thrSubTxnType = thrSubTxnType;
    }

    public String getThrProductType() {
        return thrProductType;
    }

    public void setThrProductType(String thrProductType) {
        this.thrProductType = thrProductType;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public String getAcquirerOgrCode() {
        return acquirerOgrCode;
    }

    public void setAcquirerOgrCode(String acquirerOgrCode) {
        this.acquirerOgrCode = acquirerOgrCode;
    }

    public String getPayTimeoutDt() {
        return payTimeoutDt;
    }

    public void setPayTimeoutDt(String payTimeoutDt) {
        this.payTimeoutDt = payTimeoutDt;
    }

    public String getThrPayType() {
        return thrPayType;
    }

    public void setThrPayType(String thrPayType) {
        this.thrPayType = thrPayType;
    }

    public String getThrTransId() {
        return thrTransId;
    }

    public void setThrTransId(String thrTransId) {
        this.thrTransId = thrTransId;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getCardOrgCode() {
        return cardOrgCode;
    }

    public void setCardOrgCode(String cardOrgCode) {
        this.cardOrgCode = cardOrgCode;
    }

    public Integer getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(Integer settleAmt) {
        this.settleAmt = settleAmt;
    }

    public String getTraceNo() {
        return traceNo;
    }

    public void setTraceNo(String traceNo) {
        this.traceNo = traceNo;
    }

    public String getTraceTime() {
        return traceTime;
    }

    public void setTraceTime(String traceTime) {
        this.traceTime = traceTime;
    }
    
    public String getMobileShopMerId() {
		return mobileShopMerId;
	}

	public void setMobileShopMerId(String mobileShopMerId) {
		this.mobileShopMerId = mobileShopMerId;
	}

	public String getSmsType() {
		return smsType;
	}

	public void setSmsType(String smsType) {
		this.smsType = smsType;
	}

	public String getCustomerInfo() {
		return customerInfo;
	}

	public void setCustomerInfo(String customerInfo) {
		this.customerInfo = customerInfo;
	}

	public String getActivateStatus() {
		return activateStatus;
	}

	public void setActivateStatus(String activateStatus) {
		this.activateStatus = activateStatus;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getBatchResult() {
		return batchResult;
	}

	public void setBatchResult(String batchResult) {
		this.batchResult = batchResult;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getBankOrderId() {
		return bankOrderId;
	}

	public void setBankOrderId(String bankOrderId) {
		this.bankOrderId = bankOrderId;
	}

	public String getNotifyId() {
		return notifyId;
	}

	public void setNotifyId(String notifyId) {
		this.notifyId = notifyId;
	}

	public String getTradeLockedStatus() {
		return tradeLockedStatus;
	}

	public void setTradeLockedStatus(String tradeLockedStatus) {
		this.tradeLockedStatus = tradeLockedStatus;
	}

	public String getPayCardType() {
		return payCardType;
	}

	public void setPayCardType(String payCardType) {
		this.payCardType = payCardType;
	}

	public String getCheckFlag() {
		return checkFlag;
	}

	public void setCheckFlag(String checkFlag) {
		this.checkFlag = checkFlag;
	}

	public String getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(String timeEnd) {
		this.timeEnd = timeEnd;
	}

	public String getThrPhishingKey() {
		return thrPhishingKey;
	}

	public void setThrPhishingKey(String thrPhishingKey) {
		this.thrPhishingKey = thrPhishingKey;
	}

	public String getThrBankType() {
		return thrBankType;
	}

	public void setThrBankType(String thrBankType) {
		this.thrBankType = thrBankType;
	}

	public String getThrChannelType() {
		return thrChannelType;
	}

	public void setThrChannelType(String thrChannelType) {
		this.thrChannelType = thrChannelType;
	}

	public String getThrChannelAmount() {
		return thrChannelAmount;
	}

	public void setThrChannelAmount(String thrChannelAmount) {
		this.thrChannelAmount = thrChannelAmount;
	}

	public String getThrMerId() {
		return thrMerId;
	}

	public void setThrMerId(String thrMerId) {
		this.thrMerId = thrMerId;
	}

	public String getThrNotifyType() {
		return thrNotifyType;
	}

	public void setThrNotifyType(String thrNotifyType) {
		this.thrNotifyType = thrNotifyType;
	}

	public String getThrRefundId() {
		return thrRefundId;
	}

	public void setThrRefundId(String thrRefundId) {
		this.thrRefundId = thrRefundId;
	}

	public String getThrRefundChannel() {
		return thrRefundChannel;
	}

	public void setThrRefundChannel(String thrRefundChannel) {
		this.thrRefundChannel = thrRefundChannel;
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