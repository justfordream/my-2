package com.huateng.cmupay.models.his;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  交易流水历史表
 * 对应的数据库表：UPAY_CSYS_TXN_LOG_HIS
 */
public class UpayCsysTxnLogHis {
	//会记日期
    private String accountDate;
    //流水号
    private Long seqId;
    //内部交易流水号
    private String intTxnSeq;
    //内部交易代码
    private String intTransCode;
    //内部交易日期
    private String intTxnDate;
    //内部交易时间
    private String intTxnTime;
    //银行日期
    private String settleDate;
    //发起方交易方式
    private String payMode;
    //业务大类
    private String bussType;
    //业务渠道
    private String bussChl;
    //发起方报文版本号
    private String reqVersion;
    //发起方业务代码
    private String reqBipCode;
    //发起方交易代码
    private String reqActivityCode;
    //发起方机构代码
    private String reqDomain;
    //发起方路由类型
    private String reqRouteType;
    //发起方路由信息
    private String reqRouteVal;
    //发起方业务流水号
    private String reqSessionId;
    //发起方交易请求流水号
    private String reqTransId;
    //发起方交易请求的日期
    private String reqTransDt;
    //发起方交易请求的时间
    private String reqTransTm;
    //发起方交易落地流水号
    private String reqTranshId;
    //发起方交易落地的日期
    private String reqTranshDt;
    //发起方交易落地的时间
    private String reqTranshTm;
    //发起方操作流水号
    private String reqOprId;
    //发起方操作请求日期
    private String reqOprDt;
    //发起方操作请求时间
    private String reqOprTm;
    //发起方渠道标识
    private String reqCnlType;
    //外部报文版本号
    private String outerVersion;
    //外部业务代码
    private String outerBipCode;
    //外部交易代码
    private String outerActivityCode;
    //外部机构代码
    private String outerDomain;
    //外部路由表路由信息
    private String outerRouteInfo;
    //外部路由类型
    private String outerRouteType;
    //外部路由信息
    private String outerRouteVal;
    //外部业务流水号
    private String outerSessionId;
    //外部交易发起方流水号
    private String outerTransId;
    //外部交易发起方日期
    private String outerTransDt;
    //外部交易发起方时间
    private String outerTransTm;
    //外部交易落地方流水号
    private String outerTranshId;
    //外部交易落地方日期
    private String outerTranshDt;
    //外部交易落地方时间
    private String outerTranshTm;
    //外部方操作流水号
    private String outerOprId;
    //外部方操作请求日期
    private String outerOprDt;
    //外部方操作请求时间
    private String outerOprTm;
    //外部渠道标识
    private String outerCnlType;
    //接收方报文版本号
    private String rcvVersion;
    //接收方业务代码
    private String rcvBipCode;
    //接收方交易代码
    private String rcvActivityCode;
    //接收方机构代码
    private String rcvDomain;
    //接收方路由类型
    private String rcvRouteType;
    //接收方路由信息
    private String rcvRouteVal;
    //接受方路由表路由信息
    private String rcvRouteInfo;
    //接收方业务流水号
    private String rcvSessionId;
    //接收方交易发起方流水号
    private String rcvTransId;
    //接收方交易发起方日期
    private String rcvTransDt;
    //接收方交易发起方时间
    private String rcvTransTm;
    //接收方交易落地方流水号
    private String rcvTranshId;
    //接收方交易落地方日期
    private String rcvTranshDt;
    //收方交易落地方时间
    private String rcvTranshTm;
    //接收方操作流水号
    private String rcvOprId;
    //接受方操作请求日期
    private String rcvOprDt;
    //接受方操作请求时间
    private String rcvOprTm;
    //接收方渠道标识
    private String rcvCnlType;
    //sn交易流水号
    private String snTransIdc;
    //sn处理标识
    private String snConvId;
    //sn日切点
    private String snCutOffDay;
    //sn处理时间
    private String snOsnTm;
    //sn发起方交换节点代码
    private String snOsnduns;
    //sn接收方交换节点代码
    private String snHsnduns;
    //sn发起方机构编码
    private String snOrgiOrgId;
    //n接受方机构编码
    private String snHomeOrgId;
    //主副号码绑定标识
    private String mainFlag;
    //主号码归属地
    private String mainIdProvince;
    //主号码标识类型
    private String mainIdType;
    //主号码用户号码
    private String mainIdValue;
    //用户号码归属地
    private String idProvince;
    //用户号码标识类型
    private String idType;
    //用户号码
    private String idValue;
    //用户状态
    private String userStatus;
    //用户类型
    private String userCat;
    //用户余额
    private Long balance;
    //签约状态
    private String signStatus;
    //用户证件类型
    private String userType;
    //用户证件号码
    private String userId;
    //用户姓名
    private String userName;
    //银行编码
    private String bankId;
    //银行账号类型
    private String bankAcctType;
    //银行账号
    private String bankAccId;
    //操作系统标识（原系统标示）
    private String oriOrgId;
    //操作流水号（原交易流水号）
    private String oriOprTransId;
    //操作请求日期（原交易请求日期）
    private String oriReqDate;
    //订单号
    private String orderId;
    //签约协议号
    private String subId;
    //签约关系生成时间
    private String subTime;
    //缴费方式
    private String payType;
    //充值额度
    private Long rechAmount;
    //充值阀值
    private Long rechThreshold;
    //最大充值额度
    private Long maxRechAmount;
    //最大充值阀值
    private Long maxRechThreshold;
    //应缴费金额
    private Long needPayAmt;
    //实际缴费金额
    private Long payAmt;
    //缴费类型
    private String payedType;
    //发起方应答类型
    private String chlRspType;
    //发起方应答代码
    private String chlRspCode;
    //发起方二级应答码
    private String chlSubRspCode;
    //发起方应答描述
    private String chlRspDesc;
    //发起方二级应答描述
    private String chlSubRspDesc;
    //外部应答类型
    private String outerRspType;
    //外部应答代码
    private String outerRspCode;
    //外部二级应答码
    private String outerSubRspCode;
    //外部应答描述
    private String outerRspDesc;
    //外部二级应答描述
    private String outerSubRspDesc;
    //接收方应答类型
    private String rcvRspType;
    //接收方应答代码
    private String rcvRspCode;
    //接收方二级应答码
    private String rcvSubRspCode;
    //接收方应答描述
    private String rcvRspDesc;
    //接收方二级应答描述
    private String rcvSubRspDesc;
    //返销标示
    private String backFlag;
    //退款标示
    private String refundFlag;
    //冲正标示
    private String reverseFlag;
    //是否已经对账
    private String reconciliationFlag;
    //状态
    private String status;
    //最后修改操作员
    private String lastUpdOprid;
    //最后修改时间
    private String lastUpdTime;
    //前台结果通知
    private String backUrl;
    //后台结果通知
    private String serverUrl;
    //原流水号
    private String lSeqId;
    //保留域1(对账方)
    private String reserved1;
    //保留域2
    private String reserved2;
    //保留域3
    private String reserved3;
    //订单时间
    private String orderTm;
    //订单商户
    private String merId;
    //订单商户自定义变量
    private String merVar;
    //客户端IP
    private String clientIp;
    
    public String getOrderTm() {
		return orderTm;
	}

	public void setOrderTm(String orderTm) {
		this.orderTm = orderTm;
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public String getMerVar() {
		return merVar;
	}

	public void setMerVar(String merVar) {
		this.merVar = merVar;
	}

	public Long getSeqId() {
		return seqId;
	}

	public void setSeqId(Long seqId) {
		this.seqId = seqId;
	}

	public String getAccountDate() {
        return accountDate;
    }

    public void setAccountDate(String accountDate) {
        this.accountDate = accountDate;
    }

    public String getIntTxnSeq() {
        return intTxnSeq;
    }

    public void setIntTxnSeq(String intTxnSeq) {
        this.intTxnSeq = intTxnSeq;
    }

    public String getIntTransCode() {
        return intTransCode;
    }

    public void setIntTransCode(String intTransCode) {
        this.intTransCode = intTransCode;
    }

    public String getIntTxnDate() {
        return intTxnDate;
    }

    public void setIntTxnDate(String intTxnDate) {
        this.intTxnDate = intTxnDate;
    }

    public String getIntTxnTime() {
        return intTxnTime;
    }

    public void setIntTxnTime(String intTxnTime) {
        this.intTxnTime = intTxnTime;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getBussType() {
        return bussType;
    }

    public void setBussType(String bussType) {
        this.bussType = bussType;
    }

    public String getBussChl() {
        return bussChl;
    }

    public void setBussChl(String bussChl) {
        this.bussChl = bussChl;
    }

    public String getReqVersion() {
        return reqVersion;
    }

    public void setReqVersion(String reqVersion) {
        this.reqVersion = reqVersion;
    }

    public String getReqBipCode() {
        return reqBipCode;
    }

    public void setReqBipCode(String reqBipCode) {
        this.reqBipCode = reqBipCode;
    }

    public String getReqActivityCode() {
        return reqActivityCode;
    }

    public void setReqActivityCode(String reqActivityCode) {
        this.reqActivityCode = reqActivityCode;
    }

    public String getReqDomain() {
        return reqDomain;
    }

    public void setReqDomain(String reqDomain) {
        this.reqDomain = reqDomain;
    }

    public String getReqRouteType() {
        return reqRouteType;
    }

    public void setReqRouteType(String reqRouteType) {
        this.reqRouteType = reqRouteType;
    }

    public String getReqRouteVal() {
        return reqRouteVal;
    }

    public void setReqRouteVal(String reqRouteVal) {
        this.reqRouteVal = reqRouteVal;
    }

    public String getReqSessionId() {
        return reqSessionId;
    }

    public void setReqSessionId(String reqSessionId) {
        this.reqSessionId = reqSessionId;
    }

    public String getReqTransId() {
        return reqTransId;
    }

    public void setReqTransId(String reqTransId) {
        this.reqTransId = reqTransId;
    }

    public String getReqTransDt() {
        return reqTransDt;
    }

    public void setReqTransDt(String reqTransDt) {
        this.reqTransDt = reqTransDt;
    }

    public String getReqTransTm() {
        return reqTransTm;
    }

    public void setReqTransTm(String reqTransTm) {
        this.reqTransTm = reqTransTm;
    }

    public String getReqTranshId() {
        return reqTranshId;
    }

    public void setReqTranshId(String reqTranshId) {
        this.reqTranshId = reqTranshId;
    }

    public String getReqTranshDt() {
        return reqTranshDt;
    }

    public void setReqTranshDt(String reqTranshDt) {
        this.reqTranshDt = reqTranshDt;
    }

    public String getReqTranshTm() {
        return reqTranshTm;
    }

    public void setReqTranshTm(String reqTranshTm) {
        this.reqTranshTm = reqTranshTm;
    }

    public String getReqOprId() {
        return reqOprId;
    }

    public void setReqOprId(String reqOprId) {
        this.reqOprId = reqOprId;
    }

    public String getReqOprDt() {
        return reqOprDt;
    }

    public void setReqOprDt(String reqOprDt) {
        this.reqOprDt = reqOprDt;
    }

    public String getReqOprTm() {
        return reqOprTm;
    }

    public void setReqOprTm(String reqOprTm) {
        this.reqOprTm = reqOprTm;
    }

    public String getReqCnlType() {
        return reqCnlType;
    }

    public void setReqCnlType(String reqCnlType) {
        this.reqCnlType = reqCnlType;
    }

    public String getOuterVersion() {
        return outerVersion;
    }

    public void setOuterVersion(String outerVersion) {
        this.outerVersion = outerVersion;
    }

    public String getOuterBipCode() {
        return outerBipCode;
    }

    public void setOuterBipCode(String outerBipCode) {
        this.outerBipCode = outerBipCode;
    }

    public String getOuterActivityCode() {
        return outerActivityCode;
    }

    public void setOuterActivityCode(String outerActivityCode) {
        this.outerActivityCode = outerActivityCode;
    }

    public String getOuterDomain() {
        return outerDomain;
    }

    public void setOuterDomain(String outerDomain) {
        this.outerDomain = outerDomain;
    }

    public String getOuterRouteInfo() {
        return outerRouteInfo;
    }

    public void setOuterRouteInfo(String outerRouteInfo) {
        this.outerRouteInfo = outerRouteInfo;
    }

    public String getOuterRouteType() {
        return outerRouteType;
    }

    public void setOuterRouteType(String outerRouteType) {
        this.outerRouteType = outerRouteType;
    }

    public String getOuterRouteVal() {
        return outerRouteVal;
    }

    public void setOuterRouteVal(String outerRouteVal) {
        this.outerRouteVal = outerRouteVal;
    }

    public String getOuterSessionId() {
        return outerSessionId;
    }

    public void setOuterSessionId(String outerSessionId) {
        this.outerSessionId = outerSessionId;
    }

    public String getOuterTransId() {
        return outerTransId;
    }

    public void setOuterTransId(String outerTransId) {
        this.outerTransId = outerTransId;
    }

    public String getOuterTransDt() {
        return outerTransDt;
    }

    public void setOuterTransDt(String outerTransDt) {
        this.outerTransDt = outerTransDt;
    }

    public String getOuterTransTm() {
        return outerTransTm;
    }

    public void setOuterTransTm(String outerTransTm) {
        this.outerTransTm = outerTransTm;
    }

    public String getOuterTranshId() {
        return outerTranshId;
    }

    public void setOuterTranshId(String outerTranshId) {
        this.outerTranshId = outerTranshId;
    }

    public String getOuterTranshDt() {
        return outerTranshDt;
    }

    public void setOuterTranshDt(String outerTranshDt) {
        this.outerTranshDt = outerTranshDt;
    }

    public String getOuterTranshTm() {
        return outerTranshTm;
    }

    public void setOuterTranshTm(String outerTranshTm) {
        this.outerTranshTm = outerTranshTm;
    }

    public String getOuterOprId() {
        return outerOprId;
    }

    public void setOuterOprId(String outerOprId) {
        this.outerOprId = outerOprId;
    }

    public String getOuterOprDt() {
        return outerOprDt;
    }

    public void setOuterOprDt(String outerOprDt) {
        this.outerOprDt = outerOprDt;
    }

    public String getOuterOprTm() {
        return outerOprTm;
    }

    public void setOuterOprTm(String outerOprTm) {
        this.outerOprTm = outerOprTm;
    }

    public String getOuterCnlType() {
        return outerCnlType;
    }

    public void setOuterCnlType(String outerCnlType) {
        this.outerCnlType = outerCnlType;
    }

    public String getRcvVersion() {
        return rcvVersion;
    }

    public void setRcvVersion(String rcvVersion) {
        this.rcvVersion = rcvVersion;
    }

    public String getRcvBipCode() {
        return rcvBipCode;
    }

    public void setRcvBipCode(String rcvBipCode) {
        this.rcvBipCode = rcvBipCode;
    }

    public String getRcvActivityCode() {
        return rcvActivityCode;
    }

    public void setRcvActivityCode(String rcvActivityCode) {
        this.rcvActivityCode = rcvActivityCode;
    }

    public String getRcvDomain() {
        return rcvDomain;
    }

    public void setRcvDomain(String rcvDomain) {
        this.rcvDomain = rcvDomain;
    }

    public String getRcvRouteType() {
        return rcvRouteType;
    }

    public void setRcvRouteType(String rcvRouteType) {
        this.rcvRouteType = rcvRouteType;
    }

    public String getRcvRouteVal() {
        return rcvRouteVal;
    }

    public void setRcvRouteVal(String rcvRouteVal) {
        this.rcvRouteVal = rcvRouteVal;
    }

    public String getRcvRouteInfo() {
        return rcvRouteInfo;
    }

    public void setRcvRouteInfo(String rcvRouteInfo) {
        this.rcvRouteInfo = rcvRouteInfo;
    }

    public String getRcvSessionId() {
        return rcvSessionId;
    }

    public void setRcvSessionId(String rcvSessionId) {
        this.rcvSessionId = rcvSessionId;
    }

    public String getRcvTransId() {
        return rcvTransId;
    }

    public void setRcvTransId(String rcvTransId) {
        this.rcvTransId = rcvTransId;
    }

    public String getRcvTransDt() {
        return rcvTransDt;
    }

    public void setRcvTransDt(String rcvTransDt) {
        this.rcvTransDt = rcvTransDt;
    }

    public String getRcvTransTm() {
        return rcvTransTm;
    }

    public void setRcvTransTm(String rcvTransTm) {
        this.rcvTransTm = rcvTransTm;
    }

    public String getRcvTranshId() {
        return rcvTranshId;
    }

    public void setRcvTranshId(String rcvTranshId) {
        this.rcvTranshId = rcvTranshId;
    }

    public String getRcvTranshDt() {
        return rcvTranshDt;
    }

    public void setRcvTranshDt(String rcvTranshDt) {
        this.rcvTranshDt = rcvTranshDt;
    }

    public String getRcvTranshTm() {
        return rcvTranshTm;
    }

    public void setRcvTranshTm(String rcvTranshTm) {
        this.rcvTranshTm = rcvTranshTm;
    }

    public String getRcvOprId() {
        return rcvOprId;
    }

    public void setRcvOprId(String rcvOprId) {
        this.rcvOprId = rcvOprId;
    }

    public String getRcvOprDt() {
        return rcvOprDt;
    }

    public void setRcvOprDt(String rcvOprDt) {
        this.rcvOprDt = rcvOprDt;
    }

    public String getRcvOprTm() {
        return rcvOprTm;
    }

    public void setRcvOprTm(String rcvOprTm) {
        this.rcvOprTm = rcvOprTm;
    }

    public String getRcvCnlType() {
        return rcvCnlType;
    }

    public void setRcvCnlType(String rcvCnlType) {
        this.rcvCnlType = rcvCnlType;
    }

    public String getSnTransIdc() {
        return snTransIdc;
    }

    public void setSnTransIdc(String snTransIdc) {
        this.snTransIdc = snTransIdc;
    }

    public String getSnConvId() {
        return snConvId;
    }

    public void setSnConvId(String snConvId) {
        this.snConvId = snConvId;
    }

    public String getSnCutOffDay() {
        return snCutOffDay;
    }

    public void setSnCutOffDay(String snCutOffDay) {
        this.snCutOffDay = snCutOffDay;
    }

    public String getSnOsnTm() {
        return snOsnTm;
    }

    public void setSnOsnTm(String snOsnTm) {
        this.snOsnTm = snOsnTm;
    }

    public String getSnOsnduns() {
        return snOsnduns;
    }

    public void setSnOsnduns(String snOsnduns) {
        this.snOsnduns = snOsnduns;
    }

    public String getSnHsnduns() {
        return snHsnduns;
    }

    public void setSnHsnduns(String snHsnduns) {
        this.snHsnduns = snHsnduns;
    }

    public String getSnOrgiOrgId() {
        return snOrgiOrgId;
    }

    public void setSnOrgiOrgId(String snOrgiOrgId) {
        this.snOrgiOrgId = snOrgiOrgId;
    }

    public String getSnHomeOrgId() {
        return snHomeOrgId;
    }

    public void setSnHomeOrgId(String snHomeOrgId) {
        this.snHomeOrgId = snHomeOrgId;
    }

    public String getMainFlag() {
        return mainFlag;
    }

    public void setMainFlag(String mainFlag) {
        this.mainFlag = mainFlag;
    }

    public String getMainIdProvince() {
        return mainIdProvince;
    }

    public void setMainIdProvince(String mainIdProvince) {
        this.mainIdProvince = mainIdProvince;
    }

    public String getMainIdType() {
        return mainIdType;
    }

    public void setMainIdType(String mainIdType) {
        this.mainIdType = mainIdType;
    }

    public String getMainIdValue() {
        return mainIdValue;
    }

    public void setMainIdValue(String mainIdValue) {
        this.mainIdValue = mainIdValue;
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

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserCat() {
        return userCat;
    }

    public void setUserCat(String userCat) {
        this.userCat = userCat;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public String getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(String signStatus) {
        this.signStatus = signStatus;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankAcctType() {
        return bankAcctType;
    }

    public void setBankAcctType(String bankAcctType) {
        this.bankAcctType = bankAcctType;
    }

    public String getBankAccId() {
        return bankAccId;
    }

    public void setBankAccId(String bankAccId) {
        this.bankAccId = bankAccId;
    }

    public String getOriOrgId() {
        return oriOrgId;
    }

    public void setOriOrgId(String oriOrgId) {
        this.oriOrgId = oriOrgId;
    }

    public String getOriOprTransId() {
        return oriOprTransId;
    }

    public void setOriOprTransId(String oriOprTransId) {
        this.oriOprTransId = oriOprTransId;
    }

    public String getOriReqDate() {
        return oriReqDate;
    }

    public void setOriReqDate(String oriReqDate) {
        this.oriReqDate = oriReqDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public String getSubTime() {
        return subTime;
    }

    public void setSubTime(String subTime) {
        this.subTime = subTime;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Long getRechAmount() {
        return rechAmount;
    }

    public void setRechAmount(Long rechAmount) {
        this.rechAmount = rechAmount;
    }

    public Long getRechThreshold() {
        return rechThreshold;
    }

    public void setRechThreshold(Long rechThreshold) {
        this.rechThreshold = rechThreshold;
    }

    public Long getMaxRechAmount() {
        return maxRechAmount;
    }

    public void setMaxRechAmount(Long maxRechAmount) {
        this.maxRechAmount = maxRechAmount;
    }

    public Long getMaxRechThreshold() {
        return maxRechThreshold;
    }

    public void setMaxRechThreshold(Long maxRechThreshold) {
        this.maxRechThreshold = maxRechThreshold;
    }

    public Long getNeedPayAmt() {
        return needPayAmt;
    }

    public void setNeedPayAmt(Long needPayAmt) {
        this.needPayAmt = needPayAmt;
    }

    public Long getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(Long payAmt) {
        this.payAmt = payAmt;
    }

    public String getPayedType() {
        return payedType;
    }

    public void setPayedType(String payedType) {
        this.payedType = payedType;
    }

    public String getChlRspType() {
        return chlRspType;
    }

    public void setChlRspType(String chlRspType) {
        this.chlRspType = chlRspType;
    }

    public String getChlRspCode() {
        return chlRspCode;
    }

    public void setChlRspCode(String chlRspCode) {
        this.chlRspCode = chlRspCode;
    }

    public String getChlSubRspCode() {
        return chlSubRspCode;
    }

    public void setChlSubRspCode(String chlSubRspCode) {
        this.chlSubRspCode = chlSubRspCode;
    }

    public String getChlRspDesc() {
        return chlRspDesc;
    }

    public void setChlRspDesc(String chlRspDesc) {
        this.chlRspDesc = chlRspDesc;
    }

    public String getChlSubRspDesc() {
        return chlSubRspDesc;
    }

    public void setChlSubRspDesc(String chlSubRspDesc) {
        this.chlSubRspDesc = chlSubRspDesc;
    }

    public String getOuterRspType() {
        return outerRspType;
    }

    public void setOuterRspType(String outerRspType) {
        this.outerRspType = outerRspType;
    }

    public String getOuterRspCode() {
        return outerRspCode;
    }

    public void setOuterRspCode(String outerRspCode) {
        this.outerRspCode = outerRspCode;
    }

    public String getOuterSubRspCode() {
        return outerSubRspCode;
    }

    public void setOuterSubRspCode(String outerSubRspCode) {
        this.outerSubRspCode = outerSubRspCode;
    }

    public String getOuterRspDesc() {
        return outerRspDesc;
    }

    public void setOuterRspDesc(String outerRspDesc) {
        this.outerRspDesc = outerRspDesc;
    }

    public String getOuterSubRspDesc() {
        return outerSubRspDesc;
    }

    public void setOuterSubRspDesc(String outerSubRspDesc) {
        this.outerSubRspDesc = outerSubRspDesc;
    }

    public String getRcvRspType() {
        return rcvRspType;
    }

    public void setRcvRspType(String rcvRspType) {
        this.rcvRspType = rcvRspType;
    }

    public String getRcvRspCode() {
        return rcvRspCode;
    }

    public void setRcvRspCode(String rcvRspCode) {
        this.rcvRspCode = rcvRspCode;
    }

    public String getRcvSubRspCode() {
        return rcvSubRspCode;
    }

    public void setRcvSubRspCode(String rcvSubRspCode) {
        this.rcvSubRspCode = rcvSubRspCode;
    }

    public String getRcvRspDesc() {
        return rcvRspDesc;
    }

    public void setRcvRspDesc(String rcvRspDesc) {
        this.rcvRspDesc = rcvRspDesc;
    }

    public String getRcvSubRspDesc() {
        return rcvSubRspDesc;
    }

    public void setRcvSubRspDesc(String rcvSubRspDesc) {
        this.rcvSubRspDesc = rcvSubRspDesc;
    }

    public String getBackFlag() {
        return backFlag;
    }

    public void setBackFlag(String backFlag) {
        this.backFlag = backFlag;
    }

    public String getRefundFlag() {
        return refundFlag;
    }

    public void setRefundFlag(String refundFlag) {
        this.refundFlag = refundFlag;
    }

    public String getReverseFlag() {
        return reverseFlag;
    }

    public void setReverseFlag(String reverseFlag) {
        this.reverseFlag = reverseFlag;
    }

    public String getReconciliationFlag() {
        return reconciliationFlag;
    }

    public void setReconciliationFlag(String reconciliationFlag) {
        this.reconciliationFlag = reconciliationFlag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getlSeqId() {
        return lSeqId;
    }

    public void setlSeqId(String lSeqId) {
        this.lSeqId = lSeqId;
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

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}
    
}