package com.huateng.core.common;


/**
 * 前置和核心平台公共变量类
 * 
 * @author Gary
 * 
 */
public class CoreConstant {
	/**
	 * 报文字符集
	 */
	public final static String MSG_ENCODING = "UTF-8";
	/**
	 * 签名验签开
	 */
	public final static String SWITCH_OPEN = "open";
	/**
	 * 签名验签关
	 */
	public final static String SWITCH_CLOSE = "close";
	
	
	public final static String START_INDEX="<?xml version=\"1.0\" encoding=\"UTF-8\"?><Body>";
	public final static String END_INDEX="</Body>";
	/**
	 * 
	 * @author Gary
	 * 
	 */
	public class RequestMsg {
		/**
		 * 交易流水号
		 */
		public final static String REQ_TXN_SEQ = "reqTxnSeq";
	}

	/**
	 * 
	 * @author Gary
	 * 
	 */
	public class RouteMsg {
		/**
		 * 路由编号
		 */
		public final static String REQ_ROUTE_INFO = "routeInfo";
		/**
		 * 请求协议类型(http or https)
		 */
		public final static String REQ_PROTOCOL = "protocol";
		/**
		 * 请求IP
		 */
		public final static String REQ_IP = "reqIp";
		/**
		 * 请求PORT
		 */
		public final static String REQ_PORT = "reqPort";
		/**
		 * 请求路径
		 */
		public final static String REQ_PATH = "reqPath";
	}

	public class SignFlag {
		public final static String YES = "1";
		public final static String NO = "0";
	}

	public class BankEncrypt{
		public final static int CCB_CNCRYPT=0;//建设银行
		public final static int SPDB_CNCRYPT=1;//浦发银行
	}
	/**
	 * 错误码
	 * 
	 * @author Gary
	 * 
	 */
	public class BankErrorCode {
		public static final String SUCCESS = "UPAY-B-B0000";
		public static final String FAILED = "UPAY-B-B9999";

		public static final String UPAY_B_B0001 = "UPAY-B-B0001";

	}

	public enum ActionCode {
		REQ("0", "请求"), RES("1", "响应");

		private String code;
		private String desc;

		private ActionCode(String code, String desc) {
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
	public enum ErrorCode{
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
	
	public enum EncryptActiveCode {
		CRM_SIGN_CODE("020002", "签约"), CRM_BANK_CHECK("020001", "银行帐号校验");

		private String code;
		private String desc;

		private EncryptActiveCode(String code, String desc) {
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
}
