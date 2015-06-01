package com.huateng.cmupay.models;

import java.io.Serializable;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  日终处理控制
 * 对应的数据库表：UPAY_CSYS_BAT_CUT_CTL
 */
public class UpayCsysBatCutCtl implements Serializable{
  
	private static final long serialVersionUID = 1L;
	
	//全局索引
	private Integer globalIdx;
	//上一清算日期
    private String lastDate;
    //当前清算日期
    private String currDate;
    //日切允许标志
    private String cutFlag;
    //日切状态
    private String dayCutStat;
    //日切时间
    private String dayCutTime;
    //预留字段1
    private String rsvdFld1;
    //预留字段2
    private String rsvdFld2;

    public Integer getGlobalIdx() {
        return globalIdx;
    }

    public void setGlobalIdx(Integer globalIdx) {
        this.globalIdx = globalIdx;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public String getCurrDate() {
        return currDate;
    }

    public void setCurrDate(String currDate) {
        this.currDate = currDate;
    }

    public String getCutFlag() {
        return cutFlag;
    }

    public void setCutFlag(String cutFlag) {
        this.cutFlag = cutFlag;
    }

    public String getDayCutStat() {
        return dayCutStat;
    }

    public void setDayCutStat(String dayCutStat) {
        this.dayCutStat = dayCutStat;
    }

    public String getDayCutTime() {
        return dayCutTime;
    }

    public void setDayCutTime(String dayCutTime) {
        this.dayCutTime = dayCutTime;
    }

    public String getRsvdFld1() {
        return rsvdFld1;
    }

    public void setRsvdFld1(String rsvdFld1) {
        this.rsvdFld1 = rsvdFld1;
    }

    public String getRsvdFld2() {
        return rsvdFld2;
    }

    public void setRsvdFld2(String rsvdFld2) {
        this.rsvdFld2 = rsvdFld2;
    }
}