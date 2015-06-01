package com.huateng.cmupay.cert;

import java.io.Serializable;
import java.util.Date;

/**
 * 证书信息
 * 
 * @author Gary
 * 
 */
public class CertInfo implements Serializable {
	private static final long serialVersionUID = 3950695142752611599L;
	/**
	 * 有效期的起始日期。
	 */
	private Date beginDate;
	/**
	 * 有效期的终止日期。
	 */
	private Date endDate;
	/**
	 * 序列号
	 */
	private String serialNumber;
	/**
	 * 版本号
	 */
	private int version;

	/**
	 * 获取证书有效期的 notBefore 日期。
	 * @return the beginDate 有效期的起始日期。
	 */
	public Date getBeginDate() {
		return beginDate;
	}

	/**
	 * @param beginDate the beginDate to set
	 */
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	/**
	 * 获取证书有效期的 notAfter 日期。
	 * @return the endDate 有效期的终止日期。
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * 序列号是证书颁发机构为每个证书所分配的一个整数。给定的 CA 所发布的每个证书的序列号必须是唯一的（即发布方名称和序列号标识一个唯一的证书）。
	 * 
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * @param serialNumber
	 *            the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * 获取证书的 version（版本号）值
	 * 
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

}
