package com.huateng.core.common;


/**
 * 前置和核心平台公共变量类
 * 
 * @author Gary
 * 
 */
public class CoreConstant {
	/**
	 * 银联机构代码
	 */
	public final static String UNION_PAY_ORG_ID = "0057";
	
	/**
	 * 支付宝机构代码
	 */
	public final static String ALI_PAY_ORG_ID = "0058";
	
	/**
	 * 财付通机构代码
	 */
	public final static String TEN_PAY_ORG_ID = "0059";
	
	/**
	 * 支付宝消息目标
	 */
	public final static String ALI_PAY_DEST_ID = "9998";
	
	/**
	 * 财付通消息目标
	 */
	public final static String TEN_PAY_DEST_ID = "9997";
	
	/**
	 * 银联机构报文版本号
	 */
	public final static String UNION_PAY_VERSION = "3.0.0";
	
	/*
	 * 银联报文字段常量 
	 */
	public enum UnionPayMsg {
		RESPCODE("respCode", ""), 
		RESPMSG("respMsg", ""), 
		ORDERID("orderId", ""), 
		MERID("merId", ""), 
		TXNTIME("txnTime", ""), 
		QUERYID("queryId", ""),
		SETTLEDATE("settleDate", ""),
		TRACETIME("traceTime", ""),
		TRACENO("traceNo", ""),
		TXNTYPE("txnType", ""),
		ORIGQRYID("origQryId",""),
		MERTYPE("merType",""),
		ACCESSTYPE("accessType",""),
		BIZTYPE("bizType",""),
		TXNSUBTYPE("txnSubType",""),
		SIGNATURE("signature",""),
		CERTID("certId",""),
		VERSION("version",""),
		SETTLECURRENCYCODE("settleCurrencyCode",""),
		SETTLEAMT("settleAmt",""),
		EXCHANGERATE("exchangeRate",""),
		EXCHANGEDATE("exchangeDate",""),
		ACCNO("accNo",""),
		PAYCARDTYPE("payCardType",""),
		PAYTYPE("payType",""),
		ORIGRESPCODE("origRespCode",""),
		ORIGRESPMSG("origRespMsg",""),
		RESPTIME("respTime",""),
		ENCODING("encoding",""),
		REQRESERVED("reqReserved",""),
		RESERVED("reserved","");
		
		String value;
		String desc;

		UnionPayMsg(String value, String desc) {
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
	 * 支付宝报文常用字段
	 *
	 */
	public enum ALIPayMsg{
		ENCODING("encoding",""),
		TXNTYPE("txnType", "01"),
		ORDERID("out_trade_no", "");
		
		String value;
		String desc;

		ALIPayMsg(String value, String desc) {
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
	
	/*
	 * 财付通交易代码常量 
	 */
	public enum TenPayTxnCode {
		NOTICE_TXN("01", "财付通支付结果通知");
		
		String value;
		String desc;

		TenPayTxnCode(String value, String desc) {
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
	
	/*
	 * 财付通报文字段常量 
	 */
	public enum TenPayMsg {
		SIGN_TYPE("sign_type", "MD5"), 
		SERVICE_VERSION("service_version", "1.0"), 
		INPUT_CHARSET("input_charset", "UTF-8"), 
		SIGN("sign", ""), 
		SIGN_KEY_INDEX("sign_key_index", "1"), 
		TRADE_MODE("trade_mode", "1"),
		TRADE_STATE("trade_state", ""),
		PAY_INFO("pay_info", ""),
		PARTNER("partner", ""),
		BANK_TYPE("bank_type", ""),
		BANK_BILLNO("bank_billno",""),
		TOTAL_FEE("total_fee",""),
		FEE_TYPE("fee_type","1"),
		NOTIFY_ID("notify_id",""),
		TRANSACTION_ID("transaction_id",""),
		OUT_TRADE_NO("out_trade_no",""),
		ATTACH("attach",""),
		TIME_END("time_end","3.0.0"),	
		TRANSPORT_FEE("transport_fee",""),
		PRODUCT_FEE("product_fee",""),
		DISCOUNT("discount",""),
		BUYER_ALIAS("buyer_alias",""),
		RETCODE("retcode","0"),
		RETMSG("retmsg","");
		
		String value;
		String desc;

		TenPayMsg(String value, String desc) {
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
	 * 第三方支付机构号，用于核心监听支付前置时区分是哪个第三方支付机构发来的请求，便于转换内部交易代码
	 */
	public final static String THIRD_PAY_ORG_ID = "THIRD_PAY_ORG_ID";
	
	/**
	 * 交易代码
	 */
	public final static String TXN_CODE = "#TXN_CODE";
	
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
