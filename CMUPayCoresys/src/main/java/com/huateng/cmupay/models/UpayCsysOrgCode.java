package com.huateng.cmupay.models;

import java.io.Serializable;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  机构信息表
 * 对应的数据库表：UPAY_CSYS_ORG_CODE
 */
public class UpayCsysOrgCode implements Serializable{
  
	private static final long serialVersionUID = 1L;
	//机构编号
    private String orgId;
    //机构名称
    private String orgName;
    //机构类型
    private String orgType;
    //状态
    private String status;
    //上级机构号
    private String fathOrgId;
    //机构级别
    private String orgLevel;
    //最后修改操作员
    private String lastUpdOprid;
    //最后修改时间
    private String lastUpdTime;
    //联系人
    private String contactMan;
    //联系电话
    private String contactTel;
    //联系地址
    private String contactAddr;
    //备注
    private String misc;
    //是否历史
    private String isHistory;
    //保留域1
    private String reserved1;
    //保留域2
    private String reserved2;
    //保留域3
    private String reserved3;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFathOrgId() {
        return fathOrgId;
    }

    public void setFathOrgId(String fathOrgId) {
        this.fathOrgId = fathOrgId;
    }

    public String getOrgLevel() {
        return orgLevel;
    }

    public void setOrgLevel(String orgLevel) {
        this.orgLevel = orgLevel;
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

    public String getContactMan() {
        return contactMan;
    }

    public void setContactMan(String contactMan) {
        this.contactMan = contactMan;
    }

    public String getContactTel() {
        return contactTel;
    }

    public void setContactTel(String contactTel) {
        this.contactTel = contactTel;
    }

    public String getContactAddr() {
        return contactAddr;
    }

    public void setContactAddr(String contactAddr) {
        this.contactAddr = contactAddr;
    }

    public String getMisc() {
        return misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }

    public String getIsHistory() {
        return isHistory;
    }

    public void setIsHistory(String isHistory) {
        this.isHistory = isHistory;
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