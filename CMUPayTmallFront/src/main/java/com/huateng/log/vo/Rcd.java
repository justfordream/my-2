package com.huateng.log.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("rcd")
public class Rcd {

	private String seq;
	private String logcode;
	private String orderno;
	private String provcode;
	private String intertype;
	private String opertype;
	private String interno;
	private String asys;
	private String zsys;
	private String calltime;
	private String dealcode;
	private String errinfo;

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getLogcode() {
		return logcode;
	}

	public void setLogcode(String logcode) {
		this.logcode = logcode;
	}

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	public String getProvcode() {
		return provcode;
	}

	public void setProvcode(String provcode) {
		this.provcode = provcode;
	}

	public String getIntertype() {
		return intertype;
	}

	public void setIntertype(String intertype) {
		this.intertype = intertype;
	}

	public String getOpertype() {
		return opertype;
	}

	public void setOpertype(String opertype) {
		this.opertype = opertype;
	}

	public String getInterno() {
		return interno;
	}

	public void setInterno(String interno) {
		this.interno = interno;
	}

	public String getAsys() {
		return asys;
	}

	public void setAsys(String asys) {
		this.asys = asys;
	}

	public String getZsys() {
		return zsys;
	}

	public void setZsys(String zsys) {
		this.zsys = zsys;
	}

	public String getCalltime() {
		return calltime;
	}

	public void setCalltime(String calltime) {
		this.calltime = calltime;
	}

	public String getDealcode() {
		return dealcode;
	}

	public void setDealcode(String dealcode) {
		this.dealcode = dealcode;
	}

	public String getErrinfo() {
		return errinfo;
	}

	public void setErrinfo(String errinfo) {
		this.errinfo = errinfo;
	}

}
