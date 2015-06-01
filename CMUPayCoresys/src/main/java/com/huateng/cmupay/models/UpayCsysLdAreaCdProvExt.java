package com.huateng.cmupay.models;

import java.io.Serializable;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  国内长途区号表
 * 对应的数据库表：UPAY_CSYS_LD_AREA_CD_PROV_EXT
 */
public class UpayCsysLdAreaCdProvExt  implements Serializable{
	  
	private static final long serialVersionUID = 1L;
	//长途区号
	private String ldAreaCd;
	//生效时间
    private String effcTm;
    private Short extFlag;
    //省代码
	private String provCd;
	//地区名称
    private String ldAreaNm;
    //失效时间
    private String expiredTm;
    private String accordingFileNm;
    //保留域1
    private String reserved1;
    //保留域2
    private String reserved2;
    //保留域3
    private String reserved3;
    
    public String getLdAreaCd() {
        return ldAreaCd;
    }

    public void setLdAreaCd(String ldAreaCd) {
        this.ldAreaCd = ldAreaCd;
    }

    public String getEffcTm() {
        return effcTm;
    }

    public void setEffcTm(String effcTm) {
        this.effcTm = effcTm;
    }

    public Short getExtFlag() {
        return extFlag;
    }

    public void setExtFlag(Short extFlag) {
        this.extFlag = extFlag;
    }
    public String getProvCd() {
        return provCd;
    }

    public void setProvCd(String provCd) {
        this.provCd = provCd;
    }

    public String getLdAreaNm() {
        return ldAreaNm;
    }

    public void setLdAreaNm(String ldAreaNm) {
        this.ldAreaNm = ldAreaNm;
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