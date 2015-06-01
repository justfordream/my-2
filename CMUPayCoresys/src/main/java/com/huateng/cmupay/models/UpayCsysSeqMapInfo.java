package com.huateng.cmupay.models;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明 序列位图信息表
 * 对应的数据库表：UPAY_CSYS_SEQ_MAP_INFO
 */
public class UpayCsysSeqMapInfo {
	//序列标识号
    private Integer seqCode;
    //序列英文名称
    private String seqEng;
    //序列中文名称
    private String seqChn;
    //序列值
    private Long seqValue;
    //保留域1
    private String reserved1;
    //保留域2
    private String reserved2;
    //保留域3
    private String reserved3;

    public Integer getSeqCode() {
        return seqCode;
    }

    public void setSeqCode(Integer seqCode) {
        this.seqCode = seqCode;
    }

    public String getSeqEng() {
        return seqEng;
    }

    public void setSeqEng(String seqEng) {
        this.seqEng = seqEng;
    }

    public String getSeqChn() {
        return seqChn;
    }

    public void setSeqChn(String seqChn) {
        this.seqChn = seqChn;
    }

    public Long getSeqValue() {
        return seqValue;
    }

    public void setSeqValue(Long seqValue) {
        this.seqValue = seqValue;
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