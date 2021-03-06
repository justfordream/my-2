/**
 * 
 */
package com.huateng.cmupay.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * 第三方支付交易常量
 * @author Manzhizhen
 *
 */
public class TUPayConstant {
	
	/**
	 * 银联的错误代码对应的移动商城的返回码
	 */
	private static Map<String, String> tpayParams = new HashMap<String, String>();
	static {
		tpayParams.put("00","010A00");//  成功
		tpayParams.put("01","015A01");//  交易失败。详情请咨询95516
		tpayParams.put("02","015A04");//  系统未开放或暂时关闭，请稍后再试
		tpayParams.put("03","015A07");//  交易通讯超时，请发起查询交易
		tpayParams.put("04","015A11");//  交易状态未明，请查询对账结果
		tpayParams.put("05","014A08");//  交易已受理，请稍后查询交易结果
		tpayParams.put("10","014A04");//  报文格式错误
		tpayParams.put("11","014A06");//  验证签名失败
		tpayParams.put("12","013A17");//  重复交易
		tpayParams.put("13","014A04");//  报文交易要素缺失
		tpayParams.put("14","014A11");//  批量文件格式错误
		
		tpayParams.put("30","013A39");//  交易未通过，请尝试使用其他银联卡支付或联系95516
		tpayParams.put("31","013A40");//  商户状态不正确
		tpayParams.put("32","012A05");//  无此交易权限
		tpayParams.put("33","013A31");//  交易金额超限
		tpayParams.put("34","014A05");//  查无此交易
		tpayParams.put("35","013A41");//  原交易状态不正确
		tpayParams.put("36","013A42");//  与原交易信息不符
		tpayParams.put("37","013A43");//  已超过最大查询次数或操作过于频繁
		tpayParams.put("38","013A44");//  风险受限
		tpayParams.put("39","013A45");//  交易不在受理时间范围内
		tpayParams.put("40","015A06");//  绑定关系检查失败                 没有对应，暂定为015A06
		tpayParams.put("41","015A06");//  批量状态不正确，无法下载           没有对应，暂定为015A06
		tpayParams.put("42","013A46");//  扣款成功但交易超过规定支付时间
		
		tpayParams.put("60","012A19");//  交易失败，详情请咨询您的发卡行
		tpayParams.put("61","012A02");//  输入的卡号无效，请确认后输入
		tpayParams.put("62","012A20");//  交易失败，发卡银行不支持该商户，请更换其他银行卡
		tpayParams.put("63","012A03");//  卡状态不正确
		tpayParams.put("64","012A12");//  卡上的余额不足
		tpayParams.put("65","012A21");//  输入的密码、有效期或CVN2有误，交易失败
		tpayParams.put("66","012A22");//  持卡人身份信息或手机号输入不正确，验证失败
		tpayParams.put("67","012A23");//  密码输入次数超限
		tpayParams.put("68","012A24");//  您的银行卡暂不支持该业务，请向您的银行或95516咨询
		tpayParams.put("69","012A25");//  您的输入超时，交易失败
		tpayParams.put("70","012A26");//  交易已跳转，等待持卡人输入
		tpayParams.put("71","012A27");//  动态口令或短信验证码校验失败
		tpayParams.put("72","012A28");//  您尚未在{}银行网点柜面或个人网银签约加办银联无卡支付业务，请去柜面或网银开通或拨打{}
		tpayParams.put("73","012A29");//  支付卡已超过有效期
		tpayParams.put("74","015A12");//  扣款成功，销账未知
		tpayParams.put("75","015A13");//  扣款成功，销账失败
		tpayParams.put("76","013A47");//  需要验密开通
		tpayParams.put("77","015A06");//  银行卡未开通认证支付          没有对应，暂定为015A06
	}

	/**
	 * 移动商城的错误代码找到银联的错误代码
	 * @return
	 */
	public static String getTUpayErrorCode(String mmarketErrorCode) {
		if (tpayParams.containsValue(mmarketErrorCode)) {
			Set<Entry<String, String>> entrySet = tpayParams.entrySet();
			for (Entry<String, String> entry : entrySet) {
				if (entry.getValue().equals(mmarketErrorCode)) {
					return entry.getKey();
				}
			}
		}
		return "";
	}
	
	/**
	 * 根据银联的错误代码找到移动商城对应的错误代码
	 * @return
	 */
	public static String getMMarketErrorCode(String tupayErrorCode) {
		return StringUtils.isBlank(tpayParams.get(tupayErrorCode))?"015A06":tpayParams.get(tupayErrorCode);
	}
	/**
	 * 
	 * */
	
	/*
	 * 银联报文字段常量 
	 */
	public enum UnionPayMsg {
		ACTIVATESTATUS("activateStatus",""),
		CUSTOMERINFO("customerInfo",""),
		CHECKFLAG("checkFlag",""),
		TEMPORARYPAYINFO("temporaryPayInfo",""),
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
		VERSION("version","3.0.0"),	
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
		ENCODING("encoding","UTF-8"),
		REQRESERVED("reqReserved",""),
		RESERVED("reserved",""),
		TXNAMT("txnAmt",""),
		CURRENCYCODE("currencyCode","");
		
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
	 * 支付宝应答报文常用字段
	 *
	 */
	public enum ALIPayMsg{
		NOTIFY_TIME("notify_time",""),
		NOTIFY_TYPE("notify_type", ""),
		NOTIFY_ID("notify_id", ""),
		SIGN_TYPE("sign_type", ""),
		SIGN("sign", ""),
		OUT_TRADE_NO("out_trade_no", ""),
		SUBJECT("subject", ""),
		PAYMENT_TYPE("payment_type", ""),
		TRADE_NO("trade_no", ""),
		TRADE_STATUS("trade_status", ""),
		GMT_CREATE("gmt_create", ""),
		GMT_PAYMENT("gmt_payment", ""),
		GMT_CLOSE("gmt_close", ""),
		REFUND_STATUS("refund_status", ""),
		GMT_REFUND("gmt_refund", ""),
		SELLER_EMAIL("seller_email", ""),
		BUYER_EMAIL("buyer_email", ""),
		SELLER_ID("seller_id", ""),
		BUYER_ID("buyer_id", ""),
		PRICE("price", ""),
		TOTAL_FEE("total_fee", ""),
		QUANTITY("quantity", ""),
		BODY("body", ""),
		DISCOUNT("discount", ""),
		IS_TOTAL_FEE_ADJUST("is_total_fee_adjust", ""),
		USE_COUPON("use_coupon", ""),
		EXTRA_COMMON_PARAM("extra_common_param", ""),
		OUT_CHANNEL_TYPE("out_channel_type", ""),
		OUT_CHANNEL_AMOUNT("out_channel_amount", ""),
		OUT_CHANNEL_INST("out_channel_inst", ""),
		BUSINESS_SCENE("business_scene", ""),
		TXN_TYPE("txn_type","01");
		
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
	
	/**
	 * 交易类型
	 * */
	public enum TpayType{
		TPAYTYPE_01("01","消费"),TPAYTYPE_02("02","预授权"),TPAYTYPE_03("03","预授权完成"),	TPAYTYPE_04("04","退货"),
		TPAYTYPE_11("11","代收"),TPAYTYPE_12("12","代付"),	TPAYTYPE_13("13","账单支付"),TPAYTYPE_31("31","消费撤销"),
		TPAYTYPE_32("32","预授权撤销"),TPAYTYPE_33("33","预授权完成撤销"),TPAYTYPE_71("71","余额查询"),	TPAYTYPE_72("72","实名认证-建立绑定关系"),
		TPAYTYPE_73("73","账单查询"),TPAYTYPE_74("74","解除绑定关系"),TPAYTYPE_75("75","查询绑定关系"),TPAYTYPE_76("76","文件传输"),
		TPAYTYPE_77("77","发送短信验证码交易"),TPAYTYPE_78("78","银联在线支付开通查询交易"),TPAYTYPE_79("79","银联在线支付开通交易");
		String value;
		String desc;

		TpayType(String value, String desc) {
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
	 * 接入类型
	 * */
	public enum AccessType{
		ACCESSTYPE_0("0","商户直连接入"),
		ACCESSTYPE_1("1","收单机构接入");
		
		String value;
		String desc;

		AccessType(String value, String desc) {
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
	 * 产品类型 
	 * */
	public enum BizType{
		BIZTYPE_000000("000000","默认值"),
		BIZTYPE_000101("000101","基金业务之股票基金"),
		BIZTYPE_000102("000102","基金业务之货币基金"),
		BIZTYPE_000201("000201","B2C网关支付"),
		BIZTYPE_000301("000301","认证支付2.0"),
		BIZTYPE_000401("000401","代付"),
		BIZTYPE_000501("000501","代收"),
		BIZTYPE_000601("000601","账单支付"),
		BIZTYPE_000701("000701","预授权"),
		BIZTYPE_000801("000801","跨行收单");
		
		String value;
		String desc;

		BizType(String value, String desc) {
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
	public enum UnPayRspCode {
		UNPAY_00("00", "成功"),
		//因互联网系统原因导致的错误
		UNPAY_01("01", "交易失败。详情请咨询95516"),
		UNPAY_02("02", "系统未开放或暂时关闭，请稍后再试"),
		UNPAY_03("03", "交易通讯超时，请发起查询交易"),
		UNPAY_04("04", "交易状态未明，请查询对账结果"),
		UNPAY_05("05", "交易已受理，请稍后查询交易结果"),
		//有关商户端上送报文格式检查导致的错误
		UNPAY_10("10", "报文格式错误"),
		UNPAY_11("11", "验证签名失败"),
		UNPAY_12("12", "重复交易"),
		UNPAY_13("13", "报文交易要素缺失"),
		UNPAY_14("14", "批量文件格式错误"),
		//有关商户/收单机构相关业务检查导致的错误
		UNPAY_30("30", "交易未通过，请尝试使用其他银联卡支付或联系95516"),
		UNPAY_31("31", "商户状态不正确"),
		UNPAY_32("32", "无此交易权限"),
		UNPAY_33("33", "交易金额超限"),
		UNPAY_34("34", "查无此交易"),
		UNPAY_35("35", "原交易状态不正确"),
		UNPAY_36("36", "与原交易信息不符"),
		UNPAY_37("37", "已超过最大查询次数或操作过于频繁"),
		UNPAY_38("38", "风险受限"),
		UNPAY_39("39", "交易不在受理时间范围内"),
		UNPAY_40("40", "绑定关系检查失败"),
		UNPAY_41("41", "批量状态不正确，无法下载"),
		UNPAY_42("42", "扣款成功但交易超过规定支付时间"),
		//有关持卡人或者发卡行（渠道）相关的问题导致的错误
		UNPAY_60("60", "交易失败，详情请咨询您的发卡行"),
		UNPAY_61("61", "输入的卡号无效，请确认后输入"),
		UNPAY_62("62", "交易失败，发卡银行不支持该商户，请更换其他银行卡"),
		UNPAY_63("63", "卡状态不正确"),
		UNPAY_64("64", "卡上的余额不足"),
		UNPAY_65("65", "输入的密码、有效期或CVN2有误，交易失败"),
		UNPAY_66("66", "持卡人身份信息或手机号输入不正确，验证失败"),
		UNPAY_67("67", "密码输入次数超限"),
		UNPAY_68("68", "您的银行卡暂不支持该业务，请向您的银行或95516咨询"),
		UNPAY_69("69", "您的输入超时，交易失败"),
		UNPAY_70("70", "交易已跳转，等待持卡人输入"),
		UNPAY_71("71", "动态口令或短信验证码校验失败"),
		UNPAY_72("72", "您尚未在{}银行网点柜面或个人网银签约加办银联无卡支付业务，请去柜面或网银开通或拨打{}"),
		UNPAY_73("73", "支付卡已超过有效期"),
		UNPAY_74("74", "扣款成功，销账未知"),
		UNPAY_75("75", "扣款成功，销账失败"),
		UNPAY_76("76", "需要验密开通"),
		UNPAY_77("77", "银行卡未开通认证支付");
		
		String value;
		String desc;

		UnPayRspCode(String value, String desc) {
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
	 * 版本号
	 * */
	public enum Version{
		TPAY_VERSION("3.0.0", "银联版本号");
		String value;
		String desc;

		Version(String value, String desc) {
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
	 * 编码方式
	 * */
	public enum CodeType{
		TPAY_CODETYPE("UTF-8","默认");
		String value;
		String desc;
		CodeType(String value, String desc) {
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
	 * 交易子类
	 * */
	public enum TxnSubType{
		TXNSUBTYPE_ACCQUERY("00","账户查询"),
		TXNSUBTYPE_PHOQUERY("01","手机号查询"),
		TXNSUBTYPE_01("01","自助消费，通过地址的方式区分前台消费和后台消费（含无跳转支付）"),
		TXNSUBTYPE_02("02","订购"),
		TXNSUBTYPE_03("03","分期付款");
		String value;
		String desc;
		TxnSubType(String value, String desc) {
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
}
