package com.huateng.core.common;

/**
 * 公共静态变量类
 * 
 * @author Gary
 * 
 */
public class CoreConstant {
	public final static String ENCODING = "UTF-8";
	public final static String CMU_URL = "";
	public final static String OPERATE_SIGN = "0";
	public final static String OPERATE_PAY = "1";
	public final static String CLOSE_URL = "";
	public static final String LOG_LEVEL_BUSINESS = "BUSINESS";
	public static final String LOG_LEVEL_INFO = "INFO";
	public static final String LOG_LEVEL_SUCC = "SUCC";
	public static final String LOG_LEVEL_WARNING = "WARNING";
	public static final String LOG_LEVEL_SERIOUS = "SERIOUS";
	public static final String LOG_LEVEL_ALARM = "ALARM";
	public static final String LOG_LEVEL_ERROR = "ERROR";
	public static final String LOG_LEVEL_DEBUG = "DEBUG";
	public final static String CCB_IBS_VERSION = "V5";
	public final static String CUR_CODE = "01";
	public final static String GETWARE = "W2Z1";
	public final static String SIGN_TYPE = "1";
	public final static String PAY_TYPE = "2";
	public final static String ENCODE = "UTF-8";
	public final static String ALL = "all";

	public final static String TRANS_PAY_CODE = "UPAY10002";
	public final static String TRANS_SIGN_CODE = "UPAY00002";

	public final static String TRANS_SHOPPAY_CODE = "UPAY10001";

	public final static String CONNNECTION_TIMEOUT = "60000";
	public final static String SO_TIMEOUT = "60000";

	/**
	 * 传输
	 */
	public final static String TRANSFER = "transfer";
	/**
	 * 省网厅标识
	 */
	public final static String CMU = "cmu";

	/**
	 * 验签开关
	 */
	public final static String CHECK_STATUS = "open";

	/**
	 * 内部交易码
	 * 
	 * @author Gary
	 * 
	 */
	public enum TransCode {
		SIGN("15100020", "网厅签约"), PAY("15010012", "网厅缴费"), SIGN_RST("15100010",
				"银行的签约请求"), PAY_RST("15010013", "银行的缴费请求"), CRM_PAY("T1000158",
				"核心的缴费结果通知"), CRM_SIGN("T1000155", "核心的签约结果通知"), SHOP_PAY(
				"18100001", "移动商城缴费"), TSHOP_PAY("18100011", "银联缴费"), TSHOP_PAY_NOTICE(
				"18100012", "银联缴费结果通知"), PAY_SHOP_OPEN("18100030", "移动商城开通认证支付"), PAY_SHOP_OPEN_NOTICE(
				"18100033", "银联开通认证支付结果通知"), TEN_SHOP_PAY("18100011", "财付通缴费"), TEN_SHOP_PAY_NOTICE(
				"18100012", "财付通缴费结果通知"), ALI_SHOP_PAY("18100011", "财付通缴费"), ALI_SHOP_PAY_NOTICE(
				"18100012", "财付通缴费结果通知");

		private String code;
		private String desc;

		private TransCode(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}

		public static String getDescByCode(String code) {
			String rtn = "";
			int len = TransCode.values().length;
			TransCode bank[] = TransCode.values();

			TransCode temp = null;
			for (int i = 0; i < len; i++) {
				temp = bank[i];
				if (temp.toString().equals(code) || temp.getDesc().equals(code)) {
					rtn = temp.getDesc();
					break;
				}
			}
			return rtn;
		}
	}

	/**
	 * 核心返回码
	 * 
	 * @author Gary
	 * 
	 */
	public enum checkStatus {
		provice("00", "省份发起"), mobileShop("01", "移动商城发起");

		private String code;
		private String desc;

		private checkStatus(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
	}

	/**
	 * 发起方渠道
	 * 
	 * @author Gary
	 * 
	 */
	public enum requsetChl {
		reqMobileShop("81", "移动商城发起");
		private String code;
		private String desc;

		private requsetChl(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
	}

	/**
	 * 核心返回码
	 * 
	 * @author Gary
	 * 
	 */
	public enum CoreRsp {
		SUCCESS("0000", "成功"), FAILED("9999", "失败");

		private String code;
		private String desc;

		private CoreRsp(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
	}

	/**
	 * 核心返回码
	 * 
	 * @author Gary
	 * 
	 */
	public enum CoreBank {
		BANK_SING("520280", "签约"), BANK_PAY("520290", "缴费");

		private String code;
		private String desc;

		private CoreBank(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
	}

	/**
	 * 
	 * 银行编码
	 * 
	 * @author Gary
	 * 
	 */
	public enum BankCode {
		CCB("0004", "建设银行"), SPDB("0005", "浦发银行"), TPAY("0057", "银联"), ALI(
				"0058", "支付宝"), TEN("0059", "财付通");

		private String code;
		private String desc;

		private BankCode(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
	}

	/**
	 * 用户类型
	 * 
	 * @author Gary
	 * 
	 */
	public enum CustType {
		PRE_PAY("1", "预付费"), AFTER_PAY("2", "后付费");

		private String code;
		private String desc;

		private CustType(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
	}

	/**
	 * 判断支付成功还是失败
	 * 
	 * @author Gary
	 * 
	 */
	public enum payStatus {
		paySuccess("Y", "成功"), payError("N", "失败");

		private String code;
		private String desc;

		private payStatus(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
	}

	/**
	 * 判断是缴费还是支付
	 * 
	 * @author Administrator
	 * 
	 */
	public enum ShopPayStatus {
		TUPAY_STATUS("01", "支付交易"), UPAY_STATUS("02", "缴费交易");

		String value;
		String desc;

		ShopPayStatus(String value, String desc) {
			this.value = value;
			this.desc = desc;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public String getValue() {
			return this.value;
		}

		public String getDesc() {
			return this.desc;
		}
	}

	/**
	 * 缴费方式
	 * 
	 * @author Gary
	 * 
	 */
	public enum PayType {
		ACTIVE("0", "主动缴费"), AUTO("1", "自动缴费(即自动+主动)");

		private String code;
		private String desc;

		private PayType(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
	}

	public enum ErrorCode {
		SUCCESS("010A00", "成功"), CODE_015A06("015A06", "未知错误");

		private String code;
		private String desc;

		private ErrorCode(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
	}

	/**
	 * 错误码
	 * 
	 * @author Gary
	 * 
	 */
	public class CmuErrorCode {
		public static final String UPAY_C = "UPAY-C-";
		public static final String SUCCESS = "UPAY-C-A0000";
		public static final String FAILED = "UPAY-C-A9999";
		public static final String ERROR = "UPAY-C-A5A10";

		public static final String UPAY_C_A0001 = "UPAY-C-A0001";
		public static final String UPAY_C_A0002 = "UPAY-C-A0002";
		public static final String UPAY_C_A0003 = "UPAY-C-A0003";
		public static final String UPAY_C_A0004 = "UPAY-C-A0004";
		public static final String UPAY_C_A0005 = "UPAY-C-A0005";
		public static final String UPAY_C_A0006 = "UPAY-C-A0006";
		public static final String UPAY_C_A0007 = "UPAY-C-A0007";
		public static final String UPAY_C_A0008 = "UPAY-C-A0008";
		public static final String UPAY_C_A0009 = "UPAY-C-A0009";
		public static final String UPAY_C_A0010 = "UPAY-C-A0010";
		public static final String UPAY_C_A0011 = "UPAY-C-A0011";
		public static final String UPAY_C_A0012 = "UPAY-C-A0012";
		public static final String UPAY_C_A0013 = "UPAY-C-A0013";
		public static final String UPAY_C_A0014 = "UPAY-C-A0014";
		public static final String UPAY_C_A0015 = "UPAY-C-A0015";
		public static final String UPAY_C_A0016 = "UPAY-C-A0016";
		public static final String UPAY_C_A0017 = "UPAY-C-A0017";
		public static final String UPAY_C_A0018 = "UPAY-C-A0018";
		public static final String UPAY_C_A0019 = "UPAY-C-A0019";
		public static final String UPAY_C_A0020 = "UPAY-C-A0020";
		public static final String UPAY_C_A0021 = "UPAY-C-A0021";

		public static final String UPAY_C_A0025 = "UPAY-C-A0025";
		public static final String UPAY_C_A0026 = "UPAY-C-A0026";
		public static final String UPAY_C_A0027 = "UPAY-C-A0027";

		public static final String UPAY_C_2A22 = "UPAY-C-2A22";
		public static final String UPAY_C_2A10 = "UPAY-C-2A10";
		public static final String UPAY_C_2A18 = "UPAY-C-2A18";
		public static final String UPAY_C_3A25 = "UPAY-C-3A25";
	}

	/**
	 * 错误码描述
	 * 
	 * @author zhaojunnan
	 * 
	 */
	public class BankErrorDesc {
		public static final String UPAY_B_A0008 = "成功";
	}

	/**
	 * 返回给移动商城的错误码
	 * 
	 * @author Administrator
	 * 
	 */
	public enum BankResultCode {
		UPAY_B_010A00("010A00", "成功"), UPAY_B_014A06("014A06", "验签失败"),

		UPAY_B_025A05("025A05", "解析报文失败"), UPAY_B_019A27("019A27",
				"OrderID参数不正确"), UPAY_B_019A51("019A51", "Payment参数不正确"), UPAY_B_019A28(
				"019A28", "MerVAR参数不正确"), UPAY_B_019A62("019A62",
				"Reserve1 参数不正确"), UPAY_B_019A64("019A64", "Reserve3参数不正确"), UPAY_B_014A04(
				"014A04", "请求报文数据错误"),

		UPAY_B_019A17("019A17", "IDType参数不正确"), UPAY_B_019A18("019A18",
				"IDValue参数不正确"), UPAY_B_019A52("019A52", "ChargeMoney参数不正确"), UPAY_B_019A44(
				"019A44", "HomeProv参数不正确");

		private String code;
		private String desc;

		private BankResultCode(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
	}

	/**
	 * 错误码
	 * 
	 * @author Gary
	 * 
	 */
	public class BankErrorCode {
		public static final String UPAY_B = "UPAY-B-";

		public static final String SUCCESS = "UPAY-B-B0000";
		public static final String FAILED = "UPAY-B-B9999";

		public static final String UPAY_B_B0001 = "UPAY-B-B0001";

		public static final String A0008 = "A0008";

		public static final String UPAY_B_A0008 = "UPAY-B-A0008";
		public static final String UPAY_B_A0009 = "UPAY-B-A0009";
		public static final String UPAY_B_A0010 = "UPAY-B-A0010";
		public static final String UPAY_B_A0011 = "UPAY-B-A0011";
		public static final String UPAY_B_A0012 = "UPAY-B-A0012";
		public static final String UPAY_B_A0013 = "UPAY-B-A0013";
		public static final String UPAY_B_A0014 = "UPAY-B-A0014";
		public static final String UPAY_B_A0015 = "UPAY-B-A0015";
		public static final String UPAY_B_A0016 = "UPAY-B-A0016";
		public static final String UPAY_B_A0017 = "UPAY-B-A0017";
		public static final String UPAY_B_A0018 = "UPAY-B-A0018";
		public static final String UPAY_B_A0019 = "UPAY-B-A0019";
		public static final String UPAY_B_A0020 = "UPAY-B-A0020";
		public static final String UPAY_B_A0021 = "UPAY-B-A0021";
		public static final String UPAY_B_A0022 = "UPAY-B-A0022";
		public static final String UPAY_B_A0023 = "UPAY-B-A0023";
		public static final String UPAY_B_A0024 = "UPAY-B-A0024";
		public static final String UPAY_B_A0025 = "UPAY-B-A0025";
		public static final String UPAY_B_A0026 = "UPAY-B-A0026";
		public static final String UPAY_B_A0027 = "UPAY-B-A0027";
		public static final String UPAY_B_A0028 = "UPAY-B-A0028";
		public static final String UPAY_B_A0029 = "UPAY-B-A0029";
		public static final String UPAY_B_A0030 = "UPAY-B-A0030";
		public static final String UPAY_B_A0031 = "UPAY-B-A0031";
		public static final String UPAY_B_A0032 = "UPAY-B-A0032";
		public static final String UPAY_B_A0033 = "UPAY-B-A0033";
		public static final String UPAY_B_A0034 = "UPAY-B-A0034";
		public static final String UPAY_B_A0035 = "UPAY-B-A0035";
		public static final String UPAY_B_A0036 = "UPAY-B-A0036";
		public static final String UPAY_B_A0037 = "UPAY-B-A0037";
		public static final String UPAY_B_A0038 = "UPAY-B-A0038";
		public static final String UPAY_B_A0039 = "UPAY-B-A0039";
		public static final String UPAY_B_A0040 = "UPAY-B-A0040";
		public static final String UPAY_B_A0041 = "UPAY-B-A0041";
		public static final String UPAY_B_A0042 = "UPAY-B-A0042";
		public static final String UPAY_B_A0043 = "UPAY-B-A0043";
		public static final String UPAY_B_A0044 = "UPAY-B-A0044";
		public static final String UPAY_B_A0045 = "UPAY-B-A0045";
		public static final String UPAY_B_A0046 = "UPAY-B-A0046";
		public static final String UPAY_B_A0047 = "UPAY-B-A0047";
		public static final String UPAY_B_A0048 = "UPAY-B-A0048";
		public static final String UPAY_B_A0049 = "UPAY-B-A0049";
		public static final String UPAY_B_A0050 = "UPAY-B-A0050";
		public static final String UPAY_B_A0051 = "UPAY-B-A0051";
		public static final String UPAY_B_A0052 = "UPAY-B-A0052";
		public static final String UPAY_B_A0053 = "UPAY-B-A0053";
		public static final String UPAY_B_A0054 = "UPAY-B-A0054";
		public static final String UPAY_B_A0055 = "UPAY-B-A0055";
		public static final String UPAY_B_A0056 = "UPAY-B-A0056";
		public static final String UPAY_B_A0057 = "UPAY-B-A0057";
		public static final String UPAY_B_A0058 = "UPAY-B-A0058";
		public static final String UPAY_B_A0059 = "UPAY-B-A0059";
		public static final String UPAY_B_A0060 = "UPAY-B-A0060";
		public static final String UPAY_B_A0061 = "UPAY-B-A0061";
		public static final String UPAY_B_A0062 = "UPAY-B-A0062";
		public static final String UPAY_B_A0063 = "UPAY-B-A0063";
		public static final String UPAY_B_A0064 = "UPAY-B-A0064";
		public static final String UPAY_B_A0065 = "UPAY-B-A0065";
		public static final String UPAY_B_A0066 = "UPAY-B-A0066";
		public static final String UPAY_B_A0067 = "UPAY-B-A0067";
		public static final String UPAY_B_A0068 = "UPAY-B-A0068";
		public static final String UPAY_B_A0069 = "UPAY-B-A0069";
		public static final String UPAY_B_A0070 = "UPAY-B-A0070";
		public static final String UPAY_B_A0071 = "UPAY-B-A0071";
		public static final String UPAY_B_A0072 = "UPAY-B-A0072";
		public static final String UPAY_B_A0073 = "UPAY-B-A0073";
		public static final String UPAY_B_A0074 = "UPAY-B-A0074";
		public static final String UPAY_B_A0075 = "UPAY-B-A0075";
		public static final String UPAY_B_A0076 = "UPAY-B-A0076";
		public static final String UPAY_B_A0077 = "UPAY-B-A0077";
		public static final String UPAY_B_A0078 = "UPAY-B-A0078";
		public static final String UPAY_B_A0079 = "UPAY-B-A0079";
		public static final String UPAY_B_A0080 = "UPAY-B-A0080";
		public static final String UPAY_B_A0081 = "UPAY-B-A0081";
		public static final String UPAY_B_A0082 = "UPAY-B-A0082";
		public static final String UPAY_B_A0083 = "UPAY-B-A0083";
		public static final String UPAY_B_A0084 = "UPAY-B-A0084";
		public static final String UPAY_B_A0085 = "UPAY-B-A0085";
		public static final String UPAY_B_A0086 = "UPAY-B-A0086";
		public static final String UPAY_B_A0087 = "UPAY-B-A0087";
		public static final String UPAY_B_A0088 = "UPAY-B-A0088";
		public static final String UPAY_B_A0089 = "UPAY-B-A0089";
		public static final String UPAY_B_A0090 = "UPAY-B-A0090";
		public static final String UPAY_B_A0091 = "UPAY-B-A0091";
		public static final String UPAY_B_A0092 = "UPAY-B-A0092";
		public static final String UPAY_B_A0093 = "UPAY-B-A0093";
		public static final String UPAY_B_A0094 = "UPAY-B-A0094";
		public static final String UPAY_B_A0095 = "UPAY-B-A0095";
		public static final String UPAY_B_A0096 = "UPAY-B-A0096";
		public static final String UPAY_B_A0097 = "UPAY-B-A0097";
		public static final String UPAY_B_A0098 = "UPAY-B-A0098";
		public static final String UPAY_B_A0099 = "UPAY-B-A0099";
		public static final String UPAY_B_A0100 = "UPAY-B-A0100";
		public static final String UPAY_B_A0101 = "UPAY-B-A0101";
		public static final String UPAY_B_A0102 = "UPAY-B-A0102";
		public static final String UPAY_B_A0103 = "UPAY-B-A0103";
		public static final String UPAY_B_A0104 = "UPAY-B-A0104";
		public static final String UPAY_B_A0105 = "UPAY-B-A0105";
		public static final String UPAY_B_A0106 = "UPAY-B-A0106";
		public static final String UPAY_B_A0107 = "UPAY-B-A0107";
		public static final String UPAY_B_A0108 = "UPAY-B-A0108";
		public static final String UPAY_B_A0109 = "UPAY-B-A0109";
		public static final String UPAY_B_A0110 = "UPAY-B-A0110";
		public static final String UPAY_B_A0111 = "UPAY-B-A0111";
		public static final String UPAY_B_A0112 = "UPAY-B-A0112";
		public static final String UPAY_B_A0113 = "UPAY-B-A0113";
		public static final String UPAY_B_A0114 = "UPAY-B-A0114";
		public static final String UPAY_B_A0115 = "UPAY-B-A0115";
		public static final String UPAY_B_A0116 = "UPAY-B-A0116";
		public static final String UPAY_B_A0117 = "UPAY-B-A0117";
		public static final String UPAY_B_A0118 = "UPAY-B-A0118";
		public static final String UPAY_B_A0119 = "UPAY-B-A0119";
		public static final String UPAY_B_A0120 = "UPAY-B-A0120";
		public static final String UPAY_B_A0121 = "UPAY-B-A0121";
		public static final String UPAY_B_A0122 = "UPAY-B-A0122";
		public static final String UPAY_B_A0123 = "UPAY-B-A0123";
		public static final String UPAY_B_A0124 = "UPAY-B-A0124";
		public static final String UPAY_B_A0125 = "UPAY-B-A0125";
		public static final String UPAY_B_A0126 = "UPAY-B-A0126";
		public static final String UPAY_B_A0127 = "UPAY-B-A0127";
		public static final String UPAY_B_A0128 = "UPAY-B-A0128";
		public static final String UPAY_B_A0129 = "UPAY-B-A0129";
		public static final String UPAY_B_A0130 = "UPAY-B-A0130";
		public static final String UPAY_B_A0131 = "UPAY-B-A0131";
		public static final String UPAY_B_A0132 = "UPAY-B-A0132";
		public static final String UPAY_B_A0133 = "UPAY-B-A0133";
		public static final String UPAY_B_A0134 = "UPAY-B-A0134";
		public static final String UPAY_B_A0135 = "UPAY-B-A0135";
		public static final String UPAY_B_A0136 = "UPAY-B-A0136";
		public static final String UPAY_B_A0137 = "UPAY-B-A0137";
		public static final String UPAY_B_A0138 = "UPAY-B-A0138";
		public static final String UPAY_B_A0139 = "UPAY-B-A0139";
		public static final String UPAY_B_A0140 = "UPAY-B-A0140";
		public static final String UPAY_B_A0141 = "UPAY-B-A0141";
		public static final String UPAY_B_A0142 = "UPAY-B-A0142";
		public static final String UPAY_B_A0143 = "UPAY-B-A0143";
		public static final String UPAY_B_A0144 = "UPAY-B-A0144";
		public static final String UPAY_B_A0145 = "UPAY-B-A0145";
		public static final String UPAY_B_A0146 = "UPAY-B-A0146";
		public static final String UPAY_B_A0147 = "UPAY-B-A0147";
		public static final String UPAY_B_A0148 = "UPAY-B-A0148";
		public static final String UPAY_B_A0149 = "UPAY-B-A0149";
		public static final String UPAY_B_A0150 = "UPAY-B-A0150";
		public static final String UPAY_B_A0151 = "UPAY-B-A0151";
		public static final String UPAY_B_A0152 = "UPAY-B-A0152";
		public static final String UPAY_B_A0153 = "UPAY-B-A0153";
		public static final String UPAY_B_A0154 = "UPAY-B-A0154";

	}
}
