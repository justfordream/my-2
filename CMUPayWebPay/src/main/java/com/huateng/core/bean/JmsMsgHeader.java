package com.huateng.core.bean;

import java.io.Serializable;

/**
 * JMS公用的报文头
 * 
 * @author cmt
 * 
 */
public class JmsMsgHeader implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 报文交易码
	 */
	private String appCd;
	/**
	 * 发送方会话流水号
	 */
	private String mqSeq;

	/**
	 * 消息选择器
	 */
	private String receiver="";
	
	/**
	 * 路由编号
	 */
	private  String routeInfo = "routeInfo";
	/**
	 * 请求协议类型(http or https)
	 */
	private  String protocol = "protocol";
	/**
	 * 请求IP
	 */
	private  String reqIp = "reqIp";
	/**
	 * 请求PORT
	 */
	private  String reqPort = "reqPort";
	/**
	 * 请求路径
	 */
	private  String reqPath = "reqPath";
	
	public String getAppCd() {
		return appCd;
	}
	public void setAppCd(String appCd) {
		this.appCd = appCd;
	}
	
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getMqSeq() {
		return mqSeq;
	}
	public void setMqSeq(String mqSeq) {
		this.mqSeq = mqSeq;
	}
	public String getRouteInfo() {
		return routeInfo;
	}
	public void setRouteInfo(String routeInfo) {
		this.routeInfo = routeInfo;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getReqIp() {
		return reqIp;
	}
	public void setReqIp(String reqIp) {
		this.reqIp = reqIp;
	}
	public String getReqPort() {
		return reqPort;
	}
	public void setReqPort(String reqPort) {
		this.reqPort = reqPort;
	}
	public String getReqPath() {
		return reqPath;
	}
	public void setReqPath(String reqPath) {
		this.reqPath = reqPath;
	}
	

}
