package com.huateng.iconstant;

/**
 * 静态公共类
 * 
 * @author Gary
 * 
 */
public class IConstant {

	/**
	 * 证书标识
	 */
	public final static String CERT_MARK = "CERTHELPER";

	/**
	 * spdb
	 */
	public final static String SPDB_SYSTEM_ERROR = "90";

	/**
	 * cmuData信息成功状态
	 */
	public final static String CMU_DATA_SUCCESS = "C_00";
	/**
	 * 信息为空
	 */
	public final static String CMU_DATA_EMPTY = "C_99";

	/**
	 * 信息验签失败
	 */
	public final static String DATA_SIGN_ERROR = "C_90";

	/**
	 * BANKData信息成功状态
	 */
	public final static String BANK_DATA_SUCCESS = "C_00";
	/**
	 * 信息为空
	 */
	public final static String BANK_DATA_EMPTY = "C_99";
	/**
	 * 签约响应
	 * 
	 * @author Gary
	 * 
	 */
	public enum CmuRsp {
		SUCCESS("99", "成功"), FAILED("00", "失败");

		private String code;
		private String desc;

		private CmuRsp(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public String getValue() {
			return this.code;
		}

		public String getDesc() {
			return this.desc;
		}

	}

	/**
	 * 支付响应
	 * 
	 * @author Gary
	 * 
	 */
	public enum BankRsp {
		SUCCESS("99", "成功"), FAILED("00", "失败");

		private String code;
		private String desc;

		private BankRsp(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public String getValue() {
			return this.code;
		}

		public String getDesc() {
			return this.desc;
		}

	}
}
