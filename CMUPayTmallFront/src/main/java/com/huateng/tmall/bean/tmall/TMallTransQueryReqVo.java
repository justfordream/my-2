package com.huateng.tmall.bean.tmall;

import com.huateng.tmall.bean.CustomAnnotation;

/**
 * @author qingxue.li
 * 类说明  银行侧交易状态查询请求报文体
 */
public class TMallTransQueryReqVo {
	@CustomAnnotation(path = "Body.OriReqSys")
	private String oriReqSys;

	@CustomAnnotation(path = "Body.OriReqDate")
	private String oriReqDate;

	@CustomAnnotation(path = "Body.OriReqTransID")
	private String oriReqTransID;

	@CustomAnnotation(path = "Body.OriActivityCode")
	private String oriActivityCode;

	public String getOriReqDate() {
		return oriReqDate;
	}

	public void setOriReqDate(String oriReqDate) {
		this.oriReqDate = oriReqDate;
	}

	public String getOriReqTransID() {
		return oriReqTransID;
	}

	public void setOriReqTransID(String oriReqTransID) {
		this.oriReqTransID = oriReqTransID;
	}

	public String getOriActivityCode() {
		return oriActivityCode;
	}

	public void setOriActivityCode(String oriActivityCode) {
		this.oriActivityCode = oriActivityCode;
	}

	public String getOriReqSys() {
		return oriReqSys;
	}

	public void setOriReqSys(String oriReqSys) {
		this.oriReqSys = oriReqSys;
	}

}
