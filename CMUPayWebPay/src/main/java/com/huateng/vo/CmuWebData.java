package com.huateng.vo;

import java.math.BigDecimal;

/**
 * 省级请求字段
 * 
 * @author 马博阳
 * 
 */
public class CmuWebData {
//
//    /**
//     * 订单号
//     */
//    private String orderId;

    /**
     * SubID签约协议号
     */
    private String subID;

//    /**
//     * 用户ID/用户充值ID
//     */
//    private String userID;
//
//    /**
//     * 用户ID标识/用户充值ID标识
//     */
//    private String userIDType;

    /**
     * 省级ID
     */
    private String cmuId;

//    /**
//     * 银行ID
//     */
//    private String bankID;
//
//    /**
//     * 商户id
//     */
//    private String merId;

    /**
     * 请求方式
     */
    private String txnType;

//    /**
//     * 交易时间
//     */
//    private String transTime;
//
//    /**
//     * 交易金额
//     */
//    private String transAmt;
//
//    /**
//     * 签名串
//     */
//    private String sig;

//    /**
//     * 后台通知url
//     */
//    private String ServerURL;

//    /**
//     * 前台通知URL
//     */
//    private String BackURL;

//    /**
//     * 语言描述
//     */
//    private String lang;

    /**
     * 信息状态
     */
    private String status;

    /**
     * 错误描述
     */
    private String error;

//    /**
//     * 币种
//     */
//    private String curType;

//    /**
//     * 省公司自定义，当统一支付系统通知支付结果时，携带此变量，省公司可以用此变量维护session等
//     */
//    private String merVAR;
    
    //Sign Property-------------------------------------------------
    /**
	 * 等待会话标识，省公司自定义，统一支付系统在签约结果通知请求中应该携带此参数
	 */
	private String SessionID;
	/**
	 * 后台结果回传URL
	 */
	private String ServerURL;
	/**
	 * 签约请求生成时间 格式为YYYYMMDDHH24MISS
	 */
	private String SubTime;
	/**
	 * 操作流水号
	 */
	private String TransactionID;
	/**
	 * 发起方应用域编码
	 */
	private String OrigDomain;
    //Pay Property-------------------------------------------------
	/**
	 * 商户标识
	 */
	private String MerID;
	/**
	 * 订单标识
	 */
	private String OrderID;

	/**
	 * 交易日期时间
	 */
	private String OrderTime;
	/**
	 * 订单金额，单位为“分”
	 */
	private BigDecimal Payed;
	/**
	 * 币种，默认人民币
	 */
	private String CurType;
	/**
	 * 统一支付系统向此URL发送支付结果通知
	 */
	private String MerURL;

	/**
	 * 省公司自定义，当统一支付系统通知支付结果时，携带此变量，省公司可以用此变量维护session等
	 */
	private String MerVAR;
    //Common Property-----------------------------------------------
    /**
	 * 中国移动用户标识类型
	 */
	private String IDType;

	/**
	 * 中国移动用户ID
	 */
	private String IDValue;
	/**
	 * 用户界面返回URL
	 */
	private String BackURL;

	/**
	 * 语言，默认为中文版
	 */
	private String Lang;

	/**
	 * 省公司在银行侧的签名数据
	 */
	private String Sig;

	/**
	 * 银行编码
	 */
	private String BankID;
	/**
	 * 客户端IP
	 */
	private String CLIENTIP;
	/**
	 * UPAY00001 签约请求时填写
	 */
	private String MCODE;
    
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
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

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getSubID() {
        return subID;
    }

    public void setSubID(String subID) {
        this.subID = subID;
    }

	public String getSessionID() {
		return SessionID;
	}

	public void setSessionID(String sessionID) {
		SessionID = sessionID;
	}

	public String getSubTime() {
		return SubTime;
	}

	public void setSubTime(String subTime) {
		SubTime = subTime;
	}

	public String getTransactionID() {
		return TransactionID;
	}

	public void setTransactionID(String transactionID) {
		TransactionID = transactionID;
	}

	public String getOrigDomain() {
		return OrigDomain;
	}

	public void setOrigDomain(String origDomain) {
		OrigDomain = origDomain;
	}

	public String getMerID() {
		return MerID;
	}

	public void setMerID(String merID) {
		MerID = merID;
	}

	public String getOrderID() {
		return OrderID;
	}

	public void setOrderID(String orderID) {
		OrderID = orderID;
	}

	public String getOrderTime() {
		return OrderTime;
	}

	public void setOrderTime(String orderTime) {
		OrderTime = orderTime;
	}

	public BigDecimal getPayed() {
		return Payed;
	}

	public void setPayed(BigDecimal payed) {
		Payed = payed;
	}

	public String getCurType() {
		return CurType;
	}

	public void setCurType(String curType) {
		CurType = curType;
	}

	public String getMerURL() {
		return MerURL;
	}

	public void setMerURL(String merURL) {
		MerURL = merURL;
	}

	public String getMerVAR() {
		return MerVAR;
	}

	public void setMerVAR(String merVAR) {
		MerVAR = merVAR;
	}

	public String getIDType() {
		return IDType;
	}

	public void setIDType(String iDType) {
		IDType = iDType;
	}

	public String getIDValue() {
		return IDValue;
	}

	public void setIDValue(String iDValue) {
		IDValue = iDValue;
	}

	public String getLang() {
		return Lang;
	}

	public void setLang(String lang) {
		Lang = lang;
	}

	public String getSig() {
		return Sig;
	}

	public void setSig(String sig) {
		Sig = sig;
	}

	public String getBankID() {
		return BankID;
	}

	public void setBankID(String bankID) {
		BankID = bankID;
	}

	public String getCLIENTIP() {
		return CLIENTIP;
	}

	public void setCLIENTIP(String cLIENTIP) {
		CLIENTIP = cLIENTIP;
	}

	public String getMCODE() {
		return MCODE;
	}

	public void setMCODE(String mCODE) {
		MCODE = mCODE;
	}

}
