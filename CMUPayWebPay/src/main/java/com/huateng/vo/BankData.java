package com.huateng.vo;

/**
 * 请求 银行 字段
 * 
 * @author 马博阳
 * 
 */
public class BankData {

    /**
     * 请求地址
     */
    protected String sendUrl;

    /**
     * 返回地址
     */
    //protected String returnUrl;

    /**
     * 后台通知地址
     */
    protected String notifyUrl;

    /**
     * 证书地址
     */
    protected String pfxPath;

    /**
     * jks地址
     */
    protected String jksPath;

    /**
     * 商户证书pfx密码
     */
    protected String pfxKey;

    /**
     * jks密码
     */
    protected String jksKey;

    /**
     * 交易代码
     */
    protected String tradeCode;

    /**
     * 商户号
     */
    protected String merId;
    /**
     * 商户柜台代码
     */
    protected String posId;
    /**
     * 分行号
     */
    protected String branchId;

    /**
     * 证书cert
     */
    protected String certPath;

    /**
     * 证书cert密码
     */
    protected String certKey;

    public String getSendUrl() {
        return sendUrl;
    }

    public void setSendUrl(String sendUrl) {
        this.sendUrl = sendUrl;
    }

//    public String getReturnUrl() {
//        return returnUrl;
//    }
//
//    public void setReturnUrl(String returnUrl) {
//        this.returnUrl = returnUrl;
//    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getPfxPath() {
        return pfxPath;
    }

    public void setPfxPath(String pfxPath) {
        this.pfxPath = pfxPath;
    }

    public String getJksPath() {
        return jksPath;
    }

    public void setJksPath(String jksPath) {
        this.jksPath = jksPath;
    }

    public String getPfxKey() {
        return pfxKey;
    }

    public void setPfxKey(String pfxKey) {
        this.pfxKey = pfxKey;
    }

    public String getJksKey() {
        return jksKey;
    }

    public void setJksKey(String jksKey) {
        this.jksKey = jksKey;
    }

    public String getTradeCode() {
        return tradeCode;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

    public String getPosId() {
		return posId;
	}

	public void setPosId(String posId) {
		this.posId = posId;
	}

	public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getCertKey() {
        return certKey;
    }

    public void setCertKey(String certKey) {
        this.certKey = certKey;
    }
}
