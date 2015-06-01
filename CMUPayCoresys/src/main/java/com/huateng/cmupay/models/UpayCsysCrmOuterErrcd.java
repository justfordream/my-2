package com.huateng.cmupay.models;

import java.io.Serializable;


/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  省厅端外部机构错误代码
 * 对应的数据库表：UPAY_CSYS_CRM_OUTER_ERRCD
 */
public class UpayCsysCrmOuterErrcd implements Serializable{
  
	private static final long serialVersionUID = 1L;
	
	//外部错误代码
    private String errCode;
    //外部平台号
    private String platformCd;
    //错误标志
    private String errFlag;
    //错误描述
    private String errDesc;
    //crm内部错误代码
    private String innerErrCode;
    //保留域1
    private String reserved1;
    //保留域2
    private String reserved2;
    //保留域3
    private String reserved3;

    public String getErrCode() {
        return errCode;
    }
    public String getPlatformCd() {
        return platformCd;
    }
    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }
    public void setPlatformCd(String platformCd) {
        this.platformCd = platformCd;
    }

    public String getErrFlag() {
        return errFlag;
    }

    public void setErrFlag(String errFlag) {
        this.errFlag = errFlag;
    }

    public String getErrDesc() {
        return errDesc;
    }

    public void setErrDesc(String errDesc) {
        this.errDesc = errDesc;
    }

    public String getInnerErrCode() {
        return innerErrCode;
    }

    public void setInnerErrCode(String innerErrCode) {
        this.innerErrCode = innerErrCode;
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