package com.huateng.tcp.data;

/**
 * 省公司网厅->中国移动统一支付系统的“支付”交易
 * 
 * @author 马博阳
 * 
 */
public class ReqPayCmu {

    /** 银行编码 */
    private String bankID;

    /** 省公司在银行侧的签名数据 */
    private String sig;

    /** 语言，默认为中文版 */
    private String lang;

    /** 用户界面返回URL */
    private String backURL;

    /** 省公司自定义，当统一支付系统通知支付结果时，携带此变量，省公司可以用此变量维护session等 */
    private String merVAR;

    /** 统一支付系统向此URL发送支付结果通知 */
    private String merURL;

    /** 中国移动充值用户的标识的类型 */
    private String chargeUserIDType;

    /** 为该用户充值 */
    private String chargeUserID;

    /** 币种，默认人民币 */
    private String curType;

    /** 订单金额，单位为“分” */
    private String amount;

    /** 交易日期时间 */
    private String orderTime;

    /** 订单标识 */
    private String orderID;

    /** 商户标识 */
    private String merID;

    public String getBankID() {
        return bankID;
    }

    public void setBankID(String bankID) {
        this.bankID = bankID;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getBackURL() {
        return backURL;
    }

    public void setBackURL(String backURL) {
        this.backURL = backURL;
    }

    public String getMerVAR() {
        return merVAR;
    }

    public void setMerVAR(String merVAR) {
        this.merVAR = merVAR;
    }

    public String getMerURL() {
        return merURL;
    }

    public void setMerURL(String merURL) {
        this.merURL = merURL;
    }

    public String getChargeUserIDType() {
        return chargeUserIDType;
    }

    public void setChargeUserIDType(String chargeUserIDType) {
        this.chargeUserIDType = chargeUserIDType;
    }

    public String getChargeUserID() {
        return chargeUserID;
    }

    public void setChargeUserID(String chargeUserID) {
        this.chargeUserID = chargeUserID;
    }

    public String getCurType() {
        return curType;
    }

    public void setCurType(String curType) {
        this.curType = curType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getMerID() {
        return merID;
    }

    public void setMerID(String merID) {
        this.merID = merID;
    }

}
