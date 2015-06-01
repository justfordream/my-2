package com.huateng.core.common;


/**
 * 前置和核心平台公共变量类
 * 
 * @author Gary
 * 
 */
public class CoreConstant {
	/**
	 * 报文常量
	 * */
	public final static String MSG_CODE = "xmldata=";
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
	
	/**
	 * 天猫方案<正常、应急>
	 * 
	 * @author qingxue.li
	 * 
	 */
	public enum EmergencySwitch{
        EMERGENCY("1","0"," 应急流程"),NORMAL("0","1","正常流程");
        
		private String flag;
		private String is_his;
		private String desc;
		

		private EmergencySwitch(String flag, String is_his,String desc) {
			this.flag = flag;
			this.is_his = is_his;
			this.desc = desc;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		public String getFlag() {
			return flag;
		}
		public void setFlag(String flag) {
			this.flag = flag;
		}
		public String getIs_his() {
			return is_his;
		}
		public void setIs_his(String is_his) {
			this.is_his = is_his;
		}		
	}
	
}
