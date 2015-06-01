package com.huateng.cmupay.models;

import com.huateng.cmupay.constant.CommonConstant;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明  签约关系表
 * 对应的数据库表：UPAY_CSYS_BIND_INFO
 */
public class UpayCsysBindInfo {
	//绑定内部流水号
	private Long seqId;
	//预签约交易流水号
	private String preTxnSeq;
	//预签约日期
	private String preTxnDate;
	//预签约交易时间
	private String preTxnTime;
	//签约交易流水号
	private String signTxnSeq;
	//签约日期
	private String signTxnDate;
	//签约交易时间
	private String signTxnTime;
	//签约生成时间
	private String signSubTime;
	//签约发起方机构
	private String signOrgId;
	//签约发起方渠道
	private String signCnlType;
	//解约交易流水号
	private String clTxnSeq;
	//解约日期
	private String clTxnDate;
	//解约交易时间
	private String clTxnTime;
	//解约生成时间
	private String clSubTime;
	//解约发起方机构
	private String clOrgId;
	//解约发起方渠道
	private String clCnlType;
	//签约协议号
	private String subId;
	//主副号码绑定标识
	private String mainFlag;
	//用户姓名
	private String userName;
	//银行日期
	private String settleDate;
	//缴费方式
	private String payType;
	//关联主号码归属地
	private String mainIdProvince;
	//关联主号码标识类型
	private String mainIdType;
	//关联主号码用户号码
	private String mainIdValue;
	//用户号码归属地
	private String idProvince;
	//用户号码标识类型
	private String idType;
	//用户类型
	private String userCat;
	//用户号码
	private String idValue;
	//充值额度
	private Long rechAmount;
	//充值阀值
	private Long rechThreshold;
	//最大充值额度
	private Long maxRechAmount;
	//最大充值阀值
	private Long maxRechThreshold;
	//银行编码
	private String bankId;
	//银行账号类型
	private String bankAcctType;
	//银行账号
	private String bankAccId;
	//用户证件类型
	private String userType;
	//用户证件号码
	private String userId;
	//签约状态
	private String status;
	//最后修改操作员
	private String lastUpdOprid;
	//最后修改时间
	private String lastUpdTime;
	//保留域1（sessionid等待会话标识）
	private String reserved1;
	//保留域2
	private String reserved2;
	//保留域3
	private String reserved3;
	//主号码关联副号码数量
	private Integer subNum;
	//预签约发起方机构
	private String preOrgId;
	//预签约发起方渠道
	private String preCnlType;
	//预签约落地方机构
	private String preOrgIdh;

	public Integer getSubNum() {
		return subNum;
	}

	public void setSubNum(Integer subNum) {
		this.subNum = subNum;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getSeqId() {
		return seqId;
	}

	public void setSeqId(Long seqId) {
		this.seqId = seqId;
	}

	public String getPreTxnSeq() {
		return preTxnSeq;
	}

	public void setPreTxnSeq(String preTxnSeq) {
		this.preTxnSeq = preTxnSeq;
	}

	public String getPreTxnDate() {
		return preTxnDate;
	}

	public void setPreTxnDate(String preTxnDate) {
		this.preTxnDate = preTxnDate;
	}

	public String getPreTxnTime() {
		return preTxnTime;
	}

	public void setPreTxnTime(String preTxnTime) {
		this.preTxnTime = preTxnTime;
	}

	public String getSignTxnSeq() {
		return signTxnSeq;
	}

	public void setSignTxnSeq(String signTxnSeq) {
		this.signTxnSeq = signTxnSeq;
	}

	public String getSignTxnDate() {
		return signTxnDate;
	}

	public void setSignTxnDate(String signTxnDate) {
		this.signTxnDate = signTxnDate;
	}

	public String getSignTxnTime() {
		return signTxnTime;
	}

	public void setSignTxnTime(String signTxnTime) {
		this.signTxnTime = signTxnTime;
	}

	public String getSignSubTime() {
		return signSubTime;
	}

	public void setSignSubTime(String signSubTime) {
		this.signSubTime = signSubTime;
	}

	public String getSignOrgId() {
		return signOrgId;
	}

	public void setSignOrgId(String signOrgId) {
		this.signOrgId = signOrgId;
	}

	public String getSignCnlType() {
		return signCnlType;
	}

	public void setSignCnlType(String signCnlType) {
		this.signCnlType = signCnlType;
	}

	public String getClTxnSeq() {
		return clTxnSeq;
	}

	public void setClTxnSeq(String clTxnSeq) {
		this.clTxnSeq = clTxnSeq;
	}

	public String getClTxnDate() {
		return clTxnDate;
	}

	public void setClTxnDate(String clTxnDate) {
		this.clTxnDate = clTxnDate;
	}

	public String getClTxnTime() {
		return clTxnTime;
	}

	public void setClTxnTime(String clTxnTime) {
		this.clTxnTime = clTxnTime;
	}

	public String getClSubTime() {
		return clSubTime;
	}

	public void setClSubTime(String clSubTime) {
		this.clSubTime = clSubTime;
	}

	public String getClOrgId() {
		return clOrgId;
	}

	public void setClOrgId(String clOrgId) {
		this.clOrgId = clOrgId;
	}

	public String getClCnlType() {
		return clCnlType;
	}

	public void setClCnlType(String clCnlType) {
		this.clCnlType = clCnlType;
	}

	public String getSubId() {
		return subId;
	}

	public void setSubId(String subId) {
		this.subId = subId;
	}

	public String getMainFlag() {
		return mainFlag;
	}

	public void setMainFlag(String mainFlag) {
		this.mainFlag = mainFlag;
	}



	

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
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

	public String getUserCat() {
		return userCat;
	}

	public void setUserCat(String userCat) {
		this.userCat = userCat;
	}

	public String getIdValue() {
		return idValue;
	}

	public void setIdValue(String idValue) {
		this.idValue = idValue;
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

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getPreOrgId() {
		return preOrgId;
	}

	public void setPreOrgId(String preOrgId) {
		this.preOrgId = preOrgId;
	}

	public String getPreCnlType() {
		return preCnlType;
	}

	public void setPreCnlType(String preCnlType) {
		this.preCnlType = preCnlType;
	}

	public String getPreOrgIdh() {
		return preOrgIdh;
	}

	public void setPreOrgIdh(String preOrgIdh) {
		this.preOrgIdh = preOrgIdh;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"").append("mainFlag:").append(this.mainFlag).append("\",");
		sb.append("\"").append("idValue:").append(this.idValue).append("\",");
		sb.append("\"").append("subId:").append(this.subId).append("\",");
		sb.append("\"").append("status:").append(this.status).append("\",");
		if(CommonConstant.Mainflag.Slave.getValue().equals(this.mainFlag)){
			sb.append("\"").append("mainIdValue:").append(this.mainIdValue).append("\",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append("}");
		return sb.toString();
	}

}