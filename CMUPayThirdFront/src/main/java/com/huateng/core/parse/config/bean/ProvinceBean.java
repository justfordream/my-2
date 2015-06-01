package com.huateng.core.parse.config.bean;


/**
 * 省编码信息
 * 
 * @author Gary
 * 
 */
public class ProvinceBean {
	/**
	 * 省名称
	 */
	private String name;
	/**
	 * 省代码
	 */
	private String code;
	/**
	 * 发起方交换节点代码
	 */
	private String osnDuns;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getOsnDuns() {
		return osnDuns;
	}

	public void setOsnDuns(String osnDuns) {
		this.osnDuns = osnDuns;
	}

}
