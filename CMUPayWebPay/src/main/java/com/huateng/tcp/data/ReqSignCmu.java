package com.huateng.tcp.data;

/**
 * 省中心网厅请求参数
 * 
 * @author 马博阳
 * 
 */
public class ReqSignCmu {

    /** 等待会话标识，省公司自定义，统一支付系统在签约结果通知请求中应该携带此参数 */
    private String sessionID;

    /** 中国移动用户标识类型 */
    private String userIDType;

    /** 中国移动用户ID */
    private String userID;

    /** 后台结果回传URL */
    private String serverURL;

    /** 用户界面返回URL */
    private String backURL;

    /** 语言，默认为中文版 */
    private String lang;

    /** 省公司在银行侧的签名数据 */
    private String sig;

    /** 银行编码 */
    private String bankID;

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getUserIDType() {
        return userIDType;
    }

    public void setUserIDType(String userIDType) {
        this.userIDType = userIDType;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public String getBackURL() {
        return backURL;
    }

    public void setBackURL(String backURL) {
        this.backURL = backURL;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public String getBankID() {
        return bankID;
    }

    public void setBankID(String bankID) {
        this.bankID = bankID;
    }

}
