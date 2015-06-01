package com.huateng.cmupay.models;

import java.io.Serializable;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明 内部交易代码表
 * 对应的数据库表：UPAY_CSYS_TRANS_CODE
 */
public class UpayCsysTransCode implements Serializable{
	  
	private static final long serialVersionUID = 1L;
	//全局交易码
    private String transCode;
    //外部业务代码
    private String reqBipCode;
    //交易代码
    private String reqActivityCode;
    //发起方交易方式
    private String payMode;
    //业务大类
    private String bussType;
    //业务渠道
    private String bussChl;
    //
//    private String txnCat;
    //交易名称
    private String transName;
    //备注
    private String misc;
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

    public String getBussChl() {
		return bussChl;
	}

	public void setBussChl(String bussChl) {
		this.bussChl = bussChl;
	}

//	public String getTxnCat() {
//		return txnCat;
//	}
//
//	public void setTxnCat(String txnCat) {
//		this.txnCat = txnCat;
//	}

	public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
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

   

    public String getTransName() {
        return transName;
    }

    public void setTransName(String transName) {
        this.transName = transName;
    }

    public String getMisc() {
        return misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
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
}