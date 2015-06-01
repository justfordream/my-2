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
	 * 加解密开
	 */
	public final static String SWITCH_OPEN = "open";
	/**
	 * 加解密关
	 */
	public final static String SWITCH_CLOSE = "close";

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
	 * 交易类型信息
	 * @author qingxue.li
	 */
	public class BIPType{
		/**
		 * 支付业务编码
		 */
		public final static String CHARGE = "BIP1A164";
		/**
		 * 支付结果通知交易编码
		 */
		public final static String CHARGE_RESULT_NOTICE = "T1000164";	
		
		/**
		 * 签约业务编码(网厅)
		 */
		public final static String NET_BIND = "BIP1A152";
		/**
		 * 签约业务编码(银行渠道)
		 */
		public final static String BANK_BIND = "BIP1A153";
		/**
		 * 签约结果通知交易编码
		 */
		public final static String BIND_RESULT_NOTICE = "T1000156";	
				
		/**
		 * 更新密钥业务编码
		 */
		public final static String UPDATE_KEY = "BIP0B002";
		/**
		 * 通知更新密钥交易编码
		 */
		public final static String NOTICE_UPDATE_KEY = "T0121005";		
	}
		
	/**
	 * 密钥更新发起方
	 */
	public class UpayKeyUpdateProposer{
		/**
		 * 管控台
		 */
		public final static String CONSOLE = "console";
		/**
		 * 网状网
		 */
		public final static String CENTER = "center";
	}
	
	/**
	 * 密钥更新状态
	 */
	public class UpayKeyStatus{
		/**
		 * 状态[0:更新成功;1：更新失败;2:未更新]
		 */
		public final static String SUCC = "0";
		public final static String FAIL = "1";
		public final static String NO_UPDATE = "2";
		
	}
}
