package com.huateng.vo;

/**
 * 省级请求字段
 * 
 * @author 马博阳
 * 
 */
public class CmuData {

    /**
     * 订单号
     */
    private String orderId;

    /**
     * SubID签约协议号
     */
    private String subID;

    /**
     * 用户ID/用户充值ID
     */
    private String userID;

    /**
     * 用户ID标识/用户充值ID标识
     */
    private String userIDType;

    /**
     * 省级ID
     */
    private String cmuId;

    /**
     * 银行ID
     */
    private String bankID;

    /**
     * 商户id
     */
    private String merId;

    /**
     * 请求方式
     */
    private String txnType;

    /**
     * 交易时间
     */
    private String transTime;

    /**
     * 交易金额
     */
    private String transAmt;

    /**
     * 签名串
     */
    private String sig;

    /**
     * 后台通知url
     */
    private String ServerURL;

    /**
     * 前台通知URL
     */
    private String BackURL;

    /**
     * 语言描述
     */
    private String lang;

    /**
     * 信息状态
     */
    private String status;

    /**
     * 错误描述
     */
    private String error;

    /**
     * 币种
     */
    private String curType;

    /**
     * 省公司自定义，当统一支付系统通知支付结果时，携带此变量，省公司可以用此变量维护session等
     */
    private String merVAR;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getCurType() {
        return curType;
    }

    public void setCurType(String curType) {
        this.curType = curType;
    }

    public String getServerURL() {
        return ServerURL;
    }

    public void setServerURL(String serverURL) {
        ServerURL = serverURL;
    }

    public String getBackURL() {
        return BackURL;
    }

    public void setBackURL(String backURL) {
        BackURL = backURL;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCmuId() {
        return cmuId;
    }

    public void setCmuId(String cmuId) {
        this.cmuId = cmuId;
    }

    public String getTransTime() {
        return transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTransAmt() {
        return transAmt;
    }

    public void setTransAmt(String transAmt) {
        this.transAmt = transAmt;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserIDType() {
        return userIDType;
    }

    public void setUserIDType(String userIDType) {
        this.userIDType = userIDType;
    }

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

    public String getMerVAR() {
        return merVAR;
    }

    public void setMerVAR(String merVAR) {
        this.merVAR = merVAR;
    }

    public String getSubID() {
        return subID;
    }

    public void setSubID(String subID) {
        this.subID = subID;
    }

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

}
