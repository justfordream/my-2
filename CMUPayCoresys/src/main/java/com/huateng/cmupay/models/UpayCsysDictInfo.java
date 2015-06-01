package com.huateng.cmupay.models;

import java.io.Serializable;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  数据字典
 * 对应的数据库表：UPAY_CSYS_DICT_INFO
 */
public class UpayCsysDictInfo implements Serializable{
	  
	private static final long serialVersionUID = 1L;
	//流水号
    private Long seqId;
    //字典编号
    private String dictId;
    //字典英文名
    private String dictEng;
    //字典中文名
    private String dictChn;
    //字典状态
    private String dictStatus;
    //字典描述
    private String notes;
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

    public Long getSeqId() {
        return seqId;
    }

    public void setSeqId(Long seqId) {
        this.seqId = seqId;
    }

    public String getDictId() {
        return dictId;
    }

    public void setDictId(String dictId) {
        this.dictId = dictId;
    }

    public String getDictEng() {
        return dictEng;
    }

    public void setDictEng(String dictEng) {
        this.dictEng = dictEng;
    }

    public String getDictChn() {
        return dictChn;
    }

    public void setDictChn(String dictChn) {
        this.dictChn = dictChn;
    }

    public String getDictStatus() {
        return dictStatus;
    }

    public void setDictStatus(String dictStatus) {
        this.dictStatus = dictStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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