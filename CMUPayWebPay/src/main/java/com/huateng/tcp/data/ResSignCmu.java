package com.huateng.tcp.data;

/**
 * 响应网厅
 * @author 马博阳
 *
 */
public class ResSignCmu {

    /** 等待会话标识，省公司自定义，统一支付系统在签约结果通知请求中应该携带此参数 */
    private String sessionID;

    /** 签约结果，标识签约是否成功以及失败的原因 */
    private String result;

    /** 对于签约结果的描述信息 */
    private String desc;

    /** 签约协议号 */
    private String subID;

    /** 签约关系生成时间，格式为YYYYMMDDHH24MISS */
    private String subTime;

    /** 银行账号信息 */
    private String userAccount;

    /** 银行卡类型 0借记卡 1信用卡 */
    private String accountCat;

    /** 银行编码 */
    private String bankID;

    /** 银行系统签名数据 */
    private String sig;

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
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

    public String getSubID() {
        return subID;
    }

    public void setSubID(String subID) {
        this.subID = subID;
    }

    public String getSubTime() {
        return subTime;
    }

    public void setSubTime(String subTime) {
        this.subTime = subTime;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getAccountCat() {
        return accountCat;
    }

    public void setAccountCat(String accountCat) {
        this.accountCat = accountCat;
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

}
