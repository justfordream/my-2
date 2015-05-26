package com.huateng.bank.logFormat;

/**
 * @author m
 * 
 */
public class ExcConstant {

	
	public static final String LOG_LEVEL_BUSINESS = "BUSINESS";
	public static final String LOG_LEVEL_INFO = "INFO";
	public static final String LOG_LEVEL_SUCC = "SUCC";

	public static final String LOG_LEVEL_WARNING = "WARNING";
	
	public static final String LOG_LEVEL_SERIOUS = "SERIOUS";

	public static final String LOG_LEVEL_ALARM = "ALARM";
	
	public static final String LOG_LEVEL_ERROR = "ERROR";

	public static final String LOG_LEVEL_DEBUG = "DEBUG";

	public static final String CRM_VERSION = "0100";

	public static final String BANK_REQ_SYS = "0001";
	public static final String CRM_RSP_CODE = "0000";

	public static final Integer TXN_LOG_SEQ = 10000001;

	public static final Integer BIND_INFO_SEQ = 10000000;
	public static final Integer BILL_PAY_SEQ = 10000002;
	public static final Long CUT_OFF_DATE = 1L;

	public static final String AMOUNT_CAT = "01";

	public static final Integer SUB_NUM = 1;

	/**
	 * 报文头参数名称
	 */
	public final static String XML_HEAD = "xmlhead";
	/**
	 * 报文体参数名称
	 */
	public final static String XML_BODY = "xmlbody";
	/**
	 * 附件参数名称
	 */
	public final static String ATTACH_FILE = "attachfile";

	public final static String XML_ROOT_ELEMENT_NAME = "InterBOSS";
	public final static String SVC_CONT_ELEMENT_NAME = "SvcCont";
	
	public final static String CDATA = "CDATA";
	// 充值返回结果报文头集合
	public final static String[] CHARGER_HEAD_CODE = { "0000", "0101", "0102"
			,"0110","0114","0134","0202","0203", "0205", "0206", "0207", "0291" };
	// 充值返回结果报文体集合
	public final static String[] CHARGER_BODY_CODE = { "0000", "5A07", "5A12",
			"5A13" };
	// 冲正返回结果集合
	public final static String[] BANK_REVERT_RES = { "015A07", "020A00", "025A07",
			"020A00" };
	// 交易处理中状态
	public final static String[] TXN_FAILED_OVER_STATUS = {"01", "15", "16", "17",
			"18", /*"19", "21" */};
	// 交易处理失败状态
	public final static String[] TXN_PROCESSING_STATUS = { "99", "11", "12",
			"13", "14", /*"20", "22"*/ };

	public final static String [] CRM_TIMEOUT_CODE ={"U99998","5A07","5A12","5A13"};
	public final static String DOMAIN_BOSS = "BOSS";
	public final static String DOMAIN_UPSS = "UPSS";

}
