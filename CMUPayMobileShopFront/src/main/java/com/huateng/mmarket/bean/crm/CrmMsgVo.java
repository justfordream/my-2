package com.huateng.mmarket.bean.crm;

import com.huateng.mmarket.bean.CustomAnnotation;
import com.huateng.mmarket.bean.ParentAnnotation;

/**
 * @author cmt
 * @version 创建时间：2013-3-8 下午6:12:57 类说明 移动侧报文消息头bean
 */
public class CrmMsgVo  {

	public CrmMsgVo() {
		super();
	}

	// 约束 1
	@CustomAnnotation(path = "InterBOSS.Version")
	private String version;// 报文版本号
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.TestFlag")
	private String testFlag;// 测试标记 0非测试交易 1测试交易
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.BIPType.BIPCode")
	private String BIPCode;// 业务功能代码
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.BIPType.ActivityCode")
	private String activityCode;// 交易代码
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.BIPType.ActionCode")
	private String actionCode;// 交易动作代码 0请求 1应答
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.RoutingInfo.OrigDomain")
	private String origDomain;// 发起方应用域代码
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.RoutingInfo.RouteType")
	private String routeType;// 路由类型
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.RoutingInfo.Routing.HomeDomain")
	private String homeDomain;// 归属方应用域代码
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.RoutingInfo.Routing.RouteValue")
	private String routeValue;
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.TransInfo.SessionID")
	private String sessionID;// 业务流水号
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.TransInfo.TransIDO")
	private String transIDO;// 发起方交易流水号
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.TransInfo.TransIDOTime")
	private String transIDOTime;// 处理时间 发起方发起请求的时间
	// 约束 ?
	@CustomAnnotation(path = "InterBOSS.TransInfo.TransIDH")
	private String transIDH;// 落地方交易流水号
	// 约束 ?
	@CustomAnnotation(path = "InterBOSS.TransInfo.TransIDHTime")
	private String transIDHTime;// 落地方处理请求的时间
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.SNReserve.TransIDC", power = '0')
	private String transIDC;
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.SNReserve.ConvID", power = '0')
	private String convID;
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.SNReserve.CutOffDay", power = '0')
	private String cutOffDay;
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.SNReserve.OSNTime", power = '0')
	private String OSNTime;
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.SNReserve.OSNDUNS", power = '0')
	private String OSNDUNS;
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.SNReserve.HSNDUNS", power = '0')
	private String HSNDUNS;
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.SNReserve.MsgSender", power = '0')
	private String msgSender;
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.SNReserve.MsgReceiver", power = '0')
	private String msgReceiver;
	// 约束 ?
	@CustomAnnotation(path = "InterBOSS.SNReserve.Priority", power = '0')
	private String priority;
	// 约束 ?
	@CustomAnnotation(path = "InterBOSS.SNReserve.ServiceLevel", power = '0')
	private String serviceLevel;
	// 约束 ?
	@CustomAnnotation(path = "InterBOSS.SNReserve.SvcContType", power = '0')
	private String svcContType;
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.Response.RspType")
	private String rspType;
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.Response.RspCode")
	private String rspCode;
	// 约束 1
	@CustomAnnotation(path = "InterBOSS.Response.RspDesc")
	private String rspDesc;

	@ParentAnnotation
	@CustomAnnotation(path = "InterBOSS.SvcCont")
	private Object body;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTestFlag() {
		return testFlag;
	}

	public void setTestFlag(String testFlag) {
		this.testFlag = testFlag;
	}

	public String getBIPCode() {
		return BIPCode;
	}

	public void setBIPCode(String bIPCode) {
		this.BIPCode = bIPCode;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public String getActionCode() {
		return actionCode;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}

	public String getOrigDomain() {
		return origDomain;
	}

	public void setOrigDomain(String origDomain) {
		this.origDomain = origDomain;
	}

	public String getRouteType() {
		return routeType;
	}

	public void setRouteType(String routeType) {
		this.routeType = routeType;
	}

	public String getHomeDomain() {
		return homeDomain;
	}

	public void setHomeDomain(String homeDomain) {
		this.homeDomain = homeDomain;
	}

	public String getRouteValue() {
		return routeValue;
	}

	public void setRouteValue(String routeValue) {
		this.routeValue = routeValue;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getTransIDO() {
		return transIDO;
	}

	public void setTransIDO(String transIDO) {
		this.transIDO = transIDO;
	}

	public String getTransIDOTime() {
		return transIDOTime;
	}

	public void setTransIDOTime(String transIDOTime) {
		this.transIDOTime = transIDOTime;
	}

	public String getTransIDH() {
		return transIDH;
	}

	public void setTransIDH(String transIDH) {
		this.transIDH = transIDH;
	}

	public String getTransIDHTime() {
		return transIDHTime;
	}

	public void setTransIDHTime(String transIDHTime) {
		this.transIDHTime = transIDHTime;
	}

	public String getTransIDC() {
		return transIDC;
	}

	public void setTransIDC(String transIDC) {
		this.transIDC = transIDC;
	}

	public String getConvID() {
		return convID;
	}

	public void setConvID(String convID) {
		this.convID = convID;
	}

	public String getCutOffDay() {
		return cutOffDay;
	}

	public void setCutOffDay(String cutOffDay) {
		this.cutOffDay = cutOffDay;
	}

	public String getOSNTime() {
		return OSNTime;
	}

	public void setOSNTime(String OSNTime) {
		this.OSNTime = OSNTime;
	}

	public String getOSNDUNS() {
		return OSNDUNS;
	}

	public void setOSNDUNS(String OSNDUNS) {
		this.OSNDUNS = OSNDUNS;
	}

	public String getHSNDUNS() {
		return HSNDUNS;
	}

	public void setHSNDUNS(String HSNDUNS) {
		this.HSNDUNS = HSNDUNS;
	}

	public String getMsgSender() {
		return msgSender;
	}

	public void setMsgSender(String msgSender) {
		this.msgSender = msgSender;
	}

	public String getMsgReceiver() {
		return msgReceiver;
	}

	public void setMsgReceiver(String msgReceiver) {
		this.msgReceiver = msgReceiver;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getServiceLevel() {
		return serviceLevel;
	}

	public void setServiceLevel(String serviceLevel) {
		this.serviceLevel = serviceLevel;
	}

	public String getSvcContType() {
		return svcContType;
	}

	public void setSvcContType(String svcContType) {
		this.svcContType = svcContType;
	}

	public String getRspType() {
		return rspType;
	}

	public void setRspType(String rspType) {
		this.rspType = rspType;
	}

	public String getRspCode() {
		return rspCode;
	}

	public void setRspCode(String rspCode) {
		this.rspCode = rspCode;
	}

	public String getRspDesc() {
		return rspDesc;
	}

	public void setRspDesc(String rspDesc) {
		this.rspDesc = rspDesc;
	}

	public Object getBody() {
		if (body == null || "".equals(body)) {
			return "";
		}
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public static void main(String[] args) {

		String times = "((201[3-9](0[13578]|10|12)(0[1-9]|[12][0-9]|3[0-1]))"
				+ "|(201[3-9](0[469]|11)(0[1-9]|[12][0-9]|30))"
				+ "|(201[3-9]02(0[1-9]|[12][0-9])))"
				+ "(0[0-9]|1[0-9]|2[0-3])([0-5][0-9][0-5][0-9])";

		String str = "20130514340240";
		System.out.println(str.matches(times));

	}

}
