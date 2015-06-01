package com.huateng.cmupay.models;

import java.io.Serializable;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  数据字典值明细表
 * 对应的数据库表：UPAY_CSYS_DICT_CODE
 */
public class UpayCsysDictCode implements Serializable{
	  
	private static final long serialVersionUID = 1L;

	//明细编号
    private Integer codeId;
    //字典值1
    private String codeValue1;
    //字典值2
    private String codeValue2;
    //字典值描述
    private String codeDesc;
    //字典编号
    private String dictId;
    //优先级
    private Integer priority;
    //字典状态
    private String dictStatus;
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

    public Integer getCodeId() {
        return codeId;
    }

    public void setCodeId(Integer codeId) {
        this.codeId = codeId;
    }

    public String getCodeValue1() {
        return codeValue1;
    }

    public void setCodeValue1(String codeValue1) {
        this.codeValue1 = codeValue1;
    }

    public String getCodeValue2() {
        return codeValue2;
    }

    public void setCodeValue2(String codeValue2) {
        this.codeValue2 = codeValue2;
    }

    public String getCodeDesc() {
        return codeDesc;
    }

    public void setCodeDesc(String codeDesc) {
        this.codeDesc = codeDesc;
    }

    public String getDictId() {
        return dictId;
    }

    public void setDictId(String dictId) {
        this.dictId = dictId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getDictStatus() {
        return dictStatus;
    }

    public void setDictStatus(String dictStatus) {
        this.dictStatus = dictStatus;
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