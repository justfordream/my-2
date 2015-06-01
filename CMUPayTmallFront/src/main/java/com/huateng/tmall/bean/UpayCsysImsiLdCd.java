package com.huateng.tmall.bean;

import java.io.Serializable;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  号码段信息表
 * 对应的数据库表：UPAY_CSYS_IMSI_LD_CD
 */
public class UpayCsysImsiLdCd implements Serializable{
	  
	private static final long serialVersionUID = 1L;
	//号码段id
	private String imsiAreaId;
	//生效时间
	private String effcTm;
	//号码段
	private String msisdnAreaId;
	//长途区号
    private String ldAreaCd;
    private String imsiType;
    //失效时间
    private String expiredTm;
    private String accordingFileNm;
    //地区名称
    private String ldAreaNm;
    //保留域1
    private String reserved1;
    //保留域2
    private String reserved2;
    //保留域3
    private String reserved3;

    
    public String getImsiAreaId() {
        return imsiAreaId;
    }

    public void setImsiAreaId(String imsiAreaId) {
        this.imsiAreaId = imsiAreaId;
    }

    public String getEffcTm() {
        return effcTm;
    }

    public void setEffcTm(String effcTm) {
        this.effcTm = effcTm;
    }

    
    public String getMsisdnAreaId() {
        return msisdnAreaId;
    }

    public void setMsisdnAreaId(String msisdnAreaId) {
        this.msisdnAreaId = msisdnAreaId;
    }

    public String getLdAreaCd() {
        return ldAreaCd;
    }

    public void setLdAreaCd(String ldAreaCd) {
        this.ldAreaCd = ldAreaCd;
    }

    public String getImsiType() {
        return imsiType;
    }

    public void setImsiType(String imsiType) {
        this.imsiType = imsiType;
    }

    public String getExpiredTm() {
        return expiredTm;
    }

    public void setExpiredTm(String expiredTm) {
        this.expiredTm = expiredTm;
    }

    public String getAccordingFileNm() {
        return accordingFileNm;
    }

    public void setAccordingFileNm(String accordingFileNm) {
        this.accordingFileNm = accordingFileNm;
    }

    public String getLdAreaNm() {
        return ldAreaNm;
    }

    public void setLdAreaNm(String ldAreaNm) {
        this.ldAreaNm = ldAreaNm;
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