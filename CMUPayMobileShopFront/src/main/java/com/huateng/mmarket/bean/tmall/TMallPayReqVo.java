package com.huateng.mmarket.bean.tmall;

import com.huateng.mmarket.bean.CustomAnnotation;

/**
 * @author cmt
 * @version 创建时间：2013-3-8 下午6:12:57 类说明 银行侧银行缴费请求报文体
 */
public class TMallPayReqVo {

	@CustomAnnotation(path = "Body.SubID")
	private String SubID;

	@CustomAnnotation(path = "Body.IDType")
	private String IDType;

	@CustomAnnotation(path = "Body.IDValue")
	private String IDValue;

	@CustomAnnotation(path = "Body.Payed")
	private String Payed;

	@CustomAnnotation(path = "Body.HomeProv")
	private String HomeProv;

	public String getSubID() {
		return SubID;
	}

	public void setSubID(String subID) {
		SubID = subID;
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

	public String getPayed() {
		return Payed;
	}

	public void setPayed(String payed) {
		Payed = payed;
	}

	public String getHomeProv() {
		return HomeProv;
	}

	public void setHomeProv(String homeProv) {
		HomeProv = homeProv;
	}

}
