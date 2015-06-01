package com.huateng.tmall.bean.tmall;

import com.huateng.tmall.bean.CustomAnnotation;

/**
 * @author qingxue.li
 * 类说明  银行侧交易状态查询响应报文体
 */
public class TMallTransQueryResVo {
	@CustomAnnotation(path = "Body.OriRcvDate")
	private String oriRcvDate;
	@CustomAnnotation(path = "Body.OriRcvTransID")
	private String oriRcvTransID;
	@CustomAnnotation(path = "Body.OriRspCode")
	private String oriRspCode;
	@CustomAnnotation(path = "Body.OriRspDesc")
	private String oriRspDesc;

	public String getOriRcvDate() {
		return oriRcvDate;
	}

	public void setOriRcvDate(String oriRcvDate) {
		this.oriRcvDate = oriRcvDate;
	}

	public String getOriRcvTransID() {
		return oriRcvTransID;
	}

	public void setOriRcvTransID(String oriRcvTransID) {
		this.oriRcvTransID = oriRcvTransID;
	}

	public String getOriRspCode() {
		return oriRspCode;
	}

	public void setOriRspCode(String oriRspCode) {
		this.oriRspCode = oriRspCode;
	}

	public String getOriRspDesc() {
		return oriRspDesc;
	}

	public void setOriRspDesc(String oriRspDesc) {
		this.oriRspDesc = oriRspDesc;
	}

}
