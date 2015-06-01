package com.huateng.cmupay.models;

import java.io.Serializable;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明 系统编码映射关系表
 * 对应的数据库表：UPAY_CSYS_SYS_MAP_INFO
 */
public class UpayCsysSysMapInfo implements Serializable{
  
	private static final long serialVersionUID = 1L;


    //系统编码
	private String sysCd;
	//系统名称
    private String sysNm;
    //机构交换节点
    private String dunsCd;
    //机构DOMAIN
    private String domain;
    //省代码
    private String areaCd;
    //保留域1
    private String reserved;
    //保留域2
    private String reserved2;

    public String getSysCd() {
        return sysCd;
    }

    public void setSysCd(String sysCd) {
        this.sysCd = sysCd;
    }

    public String getSysNm() {
        return sysNm;
    }

    public void setSysNm(String sysNm) {
        this.sysNm = sysNm;
    }

    public String getDunsCd() {
        return dunsCd;
    }

    public void setDunsCd(String dunsCd) {
        this.dunsCd = dunsCd;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getAreaCd() {
        return areaCd;
    }

    public void setAreaCd(String areaCd) {
        this.areaCd = areaCd;
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }
}