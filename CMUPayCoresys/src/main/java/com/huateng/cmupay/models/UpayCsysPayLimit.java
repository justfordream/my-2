package com.huateng.cmupay.models;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明 缴费额度明细
 * 对应的数据库表：UPAY_CSYS_PAY_LIMIT
 */
public class UpayCsysPayLimit  {
	//用户号码归属地
    private String idProvince;
    //用户号码标识类型
    private String idType;
    //月最高缴费额度
    private Long monthMaxAmount;
    //当月已缴费金额
    private Long monthAmount;
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
    //用户号码
    private String idValue;
    //月份
    private String payMonth;
    //日最高缴费额度
    private Long dayMaxAmount;
    //缴费额度类型
    private String amountCat;
    //当日已缴费金额
    private Long dayAmount;
    
    public Long getDayAmount() {
		return dayAmount;
	}

	public void setDayAmount(Long dayAmount) {
		this.dayAmount = dayAmount;
	}

	public Long getDayMaxAmount() {
		return dayMaxAmount;
	}

	public void setDayMaxAmount(Long dayMaxAmount) {
		this.dayMaxAmount = dayMaxAmount;
	}

	

	public String getAmountCat() {
		return amountCat;
	}

	public void setAmountCat(String amountCat) {
		this.amountCat = amountCat;
	}

	public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }

    public String getPayMonth() {
        return payMonth;
    }

    public void setPayMonth(String payMonth) {
        this.payMonth = payMonth;
    }

    public String getIdProvince() {
        return idProvince;
    }

    public void setIdProvince(String idProvince) {
        this.idProvince = idProvince;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public Long getMonthMaxAmount() {
        return monthMaxAmount;
    }

    public void setMonthMaxAmount(Long monthMaxAmount) {
        this.monthMaxAmount = monthMaxAmount;
    }

    public Long getMonthAmount() {
        return monthAmount;
    }

    public void setMonthAmount(Long monthAmount) {
        this.monthAmount = monthAmount;
    }
    public void addMonthAmount(Long addMonthAmount) {
        this.monthAmount = this.monthAmount.longValue()+addMonthAmount.longValue() ;
    }
    public void delMonthAmount(Long addMonthAmount) {
        this.monthAmount = this.monthAmount.longValue()-addMonthAmount.longValue() ;
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
    
    public boolean isOverMonthLimit(){
    	return monthAmount > monthMaxAmount;
    }
    
    public boolean isOverDaylyLimit(){
    	return dayAmount > dayMaxAmount;
    }
}