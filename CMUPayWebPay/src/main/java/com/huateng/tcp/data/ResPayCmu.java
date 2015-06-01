package com.huateng.tcp.data;

/**
 * 省公司网厅->中国移动统一支付系统的“支付”交易的响应
 * 
 * @author 马博阳
 * 
 */
public class ResPayCmu {

    /** 订单标识 */
    private String orderID;

    /** 支付结果，标识支付是否成功以及失败的原因 */
    private String result;

    /** 对于支付结果的描述信息 */
    private String desc;

    /** 省公司自定义，当统一支付系统通知支付结果时，携带此变量，省公司可以用此变量维护session等 */
    private String merVAR;

    /** 银行签名 */
    private String sig;

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMerVAR() {
        return merVAR;
    }

    public void setMerVAR(String merVAR) {
        this.merVAR = merVAR;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

}
