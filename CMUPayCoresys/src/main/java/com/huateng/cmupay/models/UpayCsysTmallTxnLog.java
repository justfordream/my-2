package com.huateng.cmupay.models;

import java.math.BigDecimal;


/** 
 * @author panlg  
 * @version 创建时间：2013-11-18 下午17:12:57 
 * 类说明 天猫交易流水表(全网方案)
 * 对应的数据库表：UPAY_CSYS_TMALL_TXN_LOG
 */

public class UpayCsysTmallTxnLog {
    private Long seqId;

    private String intTxnSeq;

    private String intTransCode;

    private String intTxnDate;

    private String intTxnTime;

    private String settleDate;

    private String payMode;

    private String bussType;

    private String bussChl;

    private String tmallActivityCode;

    private String tmallOrgId;

    private String tmallRouteInfo;

    private String tmallTransId;

    private String tmallTransDt;

    private String tmallTransTm;

    private String tmallTranshId;

    private String tmallTranshDt;

    private String tmallTranshTm;

    private String tmallCnlType;

    private String crmBipCode;

    private String crmActivityCode;

    private String crmOrgId;

    private String crmRouteType;

    private String crmRouteVal;

    private String crmRouteInfo;

    private String crmSessionId;

    private String crmTransId;

    private String crmTransDt;

    private String crmTransTm;

    private String crmTranshId;

    private String crmTranshDt;

    private String crmTranshTm;

    private String crmOprId;

    private String crmOprDt;

    private String crmOprTm;

    private String crmCnlType;

    private String crmStartTm;

    private String crmEndTm;

    private String snTransIdc;

    private String snConvId;

    private String snCutOffDay;

    private String snOsnTm;

    private String snOsnduns;

    private String snHsnduns;

    private String snOrgiOrgId;

    private String snHomeOrgId;

    private String idProvince;

    private String orderId;

    private String payTransId;

    private String idType;

    private String idValue;

    private String homeProv;

    private Long payment;

    private Long chargeMoney;

    private Long prodCnt;

    private String prodId;

    private Long commision;

    private Long rebateFee;

    private Long prodDiscount;

    private Long creditCardFee;

    private Long serviceFee;

    private String payedType;

    private String activityNo;

    private String prodShelfNo;

    private Short resendCount;

    private String userCat;

    private String tmallRspCode;

    private String tmallRspDesc;

    private String crmRspType;

    private String crmRspCode;

    private String crmSubRspCode;

    private String crmRspDesc;

    private String crmSubRspDesc;

    private String oriOrgId;

    private String oriOprTransId;

    private String oriTransDate;

    private String backFlag;

    private String refundFlag;

    private String reverseFlag;

    private String reconciliationFlag;

    private String status;

    private String lastUpdOprid;

    private String lastUpdTime;

    private String lSeqId;

    private String reserved1;

    private String reserved2;

    private String reserved3;

    private String reserved4;

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

    public String getTmallActivityCode() {
        return tmallActivityCode;
    }

    public void setTmallActivityCode(String tmallActivityCode) {
        this.tmallActivityCode = tmallActivityCode;
    }

    public String getTmallOrgId() {
        return tmallOrgId;
    }

    public void setTmallOrgId(String tmallOrgId) {
        this.tmallOrgId = tmallOrgId;
    }

    public String getTmallRouteInfo() {
        return tmallRouteInfo;
    }

    public void setTmallRouteInfo(String tmallRouteInfo) {
        this.tmallRouteInfo = tmallRouteInfo;
    }

    public String getTmallTransId() {
        return tmallTransId;
    }

    public void setTmallTransId(String tmallTransId) {
        this.tmallTransId = tmallTransId;
    }

    public String getTmallTransDt() {
        return tmallTransDt;
    }

    public void setTmallTransDt(String tmallTransDt) {
        this.tmallTransDt = tmallTransDt;
    }

    public String getTmallTransTm() {
        return tmallTransTm;
    }

    public void setTmallTransTm(String tmallTransTm) {
        this.tmallTransTm = tmallTransTm;
    }

    public String getTmallTranshId() {
        return tmallTranshId;
    }

    public void setTmallTranshId(String tmallTranshId) {
        this.tmallTranshId = tmallTranshId;
    }

    public String getTmallTranshDt() {
        return tmallTranshDt;
    }

    public void setTmallTranshDt(String tmallTranshDt) {
        this.tmallTranshDt = tmallTranshDt;
    }

    public String getTmallTranshTm() {
        return tmallTranshTm;
    }

    public void setTmallTranshTm(String tmallTranshTm) {
        this.tmallTranshTm = tmallTranshTm;
    }

    public String getTmallCnlType() {
        return tmallCnlType;
    }

    public void setTmallCnlType(String tmallCnlType) {
        this.tmallCnlType = tmallCnlType;
    }

    public String getCrmBipCode() {
        return crmBipCode;
    }

    public void setCrmBipCode(String crmBipCode) {
        this.crmBipCode = crmBipCode;
    }

    public String getCrmActivityCode() {
        return crmActivityCode;
    }

    public void setCrmActivityCode(String crmActivityCode) {
        this.crmActivityCode = crmActivityCode;
    }

    public String getCrmOrgId() {
        return crmOrgId;
    }

    public void setCrmOrgId(String crmOrgId) {
        this.crmOrgId = crmOrgId;
    }

    public String getCrmRouteType() {
        return crmRouteType;
    }

    public void setCrmRouteType(String crmRouteType) {
        this.crmRouteType = crmRouteType;
    }

    public String getCrmRouteVal() {
        return crmRouteVal;
    }

    public void setCrmRouteVal(String crmRouteVal) {
        this.crmRouteVal = crmRouteVal;
    }

    public String getCrmRouteInfo() {
        return crmRouteInfo;
    }

    public void setCrmRouteInfo(String crmRouteInfo) {
        this.crmRouteInfo = crmRouteInfo;
    }

    public String getCrmSessionId() {
        return crmSessionId;
    }

    public void setCrmSessionId(String crmSessionId) {
        this.crmSessionId = crmSessionId;
    }

    public String getCrmTransId() {
        return crmTransId;
    }

    public void setCrmTransId(String crmTransId) {
        this.crmTransId = crmTransId;
    }

    public String getCrmTransDt() {
        return crmTransDt;
    }

    public void setCrmTransDt(String crmTransDt) {
        this.crmTransDt = crmTransDt;
    }

    public String getCrmTransTm() {
        return crmTransTm;
    }

    public void setCrmTransTm(String crmTransTm) {
        this.crmTransTm = crmTransTm;
    }

    public String getCrmTranshId() {
        return crmTranshId;
    }

    public void setCrmTranshId(String crmTranshId) {
        this.crmTranshId = crmTranshId;
    }

    public String getCrmTranshDt() {
        return crmTranshDt;
    }

    public void setCrmTranshDt(String crmTranshDt) {
        this.crmTranshDt = crmTranshDt;
    }

    public String getCrmTranshTm() {
        return crmTranshTm;
    }

    public void setCrmTranshTm(String crmTranshTm) {
        this.crmTranshTm = crmTranshTm;
    }

    public String getCrmOprId() {
        return crmOprId;
    }

    public void setCrmOprId(String crmOprId) {
        this.crmOprId = crmOprId;
    }

    public String getCrmOprDt() {
        return crmOprDt;
    }

    public void setCrmOprDt(String crmOprDt) {
        this.crmOprDt = crmOprDt;
    }

    public String getCrmOprTm() {
        return crmOprTm;
    }

    public void setCrmOprTm(String crmOprTm) {
        this.crmOprTm = crmOprTm;
    }

    public String getCrmCnlType() {
        return crmCnlType;
    }

    public void setCrmCnlType(String crmCnlType) {
        this.crmCnlType = crmCnlType;
    }

    public String getCrmStartTm() {
        return crmStartTm;
    }

    public void setCrmStartTm(String crmStartTm) {
        this.crmStartTm = crmStartTm;
    }

    public String getCrmEndTm() {
        return crmEndTm;
    }

    public void setCrmEndTm(String crmEndTm) {
        this.crmEndTm = crmEndTm;
    }

    public String getSnTransIdc() {
        return snTransIdc;
    }

    public void setSnTransIdc(String snTransIdc) {
        this.snTransIdc = snTransIdc;
    }

    public String getSnConvId() {
        return snConvId;
    }

    public void setSnConvId(String snConvId) {
        this.snConvId = snConvId;
    }

    public String getSnCutOffDay() {
        return snCutOffDay;
    }

    public void setSnCutOffDay(String snCutOffDay) {
        this.snCutOffDay = snCutOffDay;
    }

    public String getSnOsnTm() {
        return snOsnTm;
    }

    public void setSnOsnTm(String snOsnTm) {
        this.snOsnTm = snOsnTm;
    }

    public String getSnOsnduns() {
        return snOsnduns;
    }

    public void setSnOsnduns(String snOsnduns) {
        this.snOsnduns = snOsnduns;
    }

    public String getSnHsnduns() {
        return snHsnduns;
    }

    public void setSnHsnduns(String snHsnduns) {
        this.snHsnduns = snHsnduns;
    }

    public String getSnOrgiOrgId() {
        return snOrgiOrgId;
    }

    public void setSnOrgiOrgId(String snOrgiOrgId) {
        this.snOrgiOrgId = snOrgiOrgId;
    }

    public String getSnHomeOrgId() {
        return snHomeOrgId;
    }

    public void setSnHomeOrgId(String snHomeOrgId) {
        this.snHomeOrgId = snHomeOrgId;
    }

    public String getIdProvince() {
        return idProvince;
    }

    public void setIdProvince(String idProvince) {
        this.idProvince = idProvince;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPayTransId() {
        return payTransId;
    }

    public void setPayTransId(String payTransId) {
        this.payTransId = payTransId;
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

    public String getHomeProv() {
        return homeProv;
    }

    public void setHomeProv(String homeProv) {
        this.homeProv = homeProv;
    }

    public Long getPayment() {
        return payment;
    }

    public void setPayment(Long payment) {
        this.payment = payment;
    }

    public Long getChargeMoney() {
        return chargeMoney;
    }

    public void setChargeMoney(Long chargeMoney) {
        this.chargeMoney = chargeMoney;
    }

    public Long getProdCnt() {
        return prodCnt;
    }

    public void setProdCnt(Long prodCnt) {
        this.prodCnt = prodCnt;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public Long getCommision() {
        return commision;
    }

    public void setCommision(Long commision) {
        this.commision = commision;
    }

    public Long getRebateFee() {
        return rebateFee;
    }

    public void setRebateFee(Long rebateFee) {
        this.rebateFee = rebateFee;
    }

    public Long getProdDiscount() {
        return prodDiscount;
    }

    public void setProdDiscount(Long prodDiscount) {
        this.prodDiscount = prodDiscount;
    }

    public Long getCreditCardFee() {
        return creditCardFee;
    }

    public void setCreditCardFee(Long creditCardFee) {
        this.creditCardFee = creditCardFee;
    }

    public Long getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Long serviceFee) {
        this.serviceFee = serviceFee;
    }

    public String getPayedType() {
        return payedType;
    }

    public void setPayedType(String payedType) {
        this.payedType = payedType;
    }

    public String getActivityNo() {
        return activityNo;
    }

    public void setActivityNo(String activityNo) {
        this.activityNo = activityNo;
    }

    public String getProdShelfNo() {
        return prodShelfNo;
    }

    public void setProdShelfNo(String prodShelfNo) {
        this.prodShelfNo = prodShelfNo;
    }

    public Short getResendCount() {
        return resendCount;
    }

    public void setResendCount(Short resendCount) {
        this.resendCount = resendCount;
    }

    public String getUserCat() {
        return userCat;
    }

    public void setUserCat(String userCat) {
        this.userCat = userCat;
    }

    public String getTmallRspCode() {
        return tmallRspCode;
    }

    public void setTmallRspCode(String tmallRspCode) {
        this.tmallRspCode = tmallRspCode;
    }

    public String getTmallRspDesc() {
        return tmallRspDesc;
    }

    public void setTmallRspDesc(String tmallRspDesc) {
        this.tmallRspDesc = tmallRspDesc;
    }

    public String getCrmRspType() {
        return crmRspType;
    }

    public void setCrmRspType(String crmRspType) {
        this.crmRspType = crmRspType;
    }

    public String getCrmRspCode() {
        return crmRspCode;
    }

    public void setCrmRspCode(String crmRspCode) {
        this.crmRspCode = crmRspCode;
    }

    public String getCrmSubRspCode() {
        return crmSubRspCode;
    }

    public void setCrmSubRspCode(String crmSubRspCode) {
        this.crmSubRspCode = crmSubRspCode;
    }

    public String getCrmRspDesc() {
        return crmRspDesc;
    }

    public void setCrmRspDesc(String crmRspDesc) {
        this.crmRspDesc = crmRspDesc;
    }

    public String getCrmSubRspDesc() {
        return crmSubRspDesc;
    }

    public void setCrmSubRspDesc(String crmSubRspDesc) {
        this.crmSubRspDesc = crmSubRspDesc;
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

    public String getOriTransDate() {
        return oriTransDate;
    }

    public void setOriTransDate(String oriTransDate) {
        this.oriTransDate = oriTransDate;
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

    public String getReserved4() {
        return reserved4;
    }

    public void setReserved4(String reserved4) {
        this.reserved4 = reserved4;
    }
}