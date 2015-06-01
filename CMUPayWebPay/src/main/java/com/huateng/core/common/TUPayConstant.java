/**
 * 
 */
package com.huateng.core.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 四位错误码，转六位错误码，银联操作
 * 
 * @author Administrator
 * 
 */
public class TUPayConstant {

	/**
	 * 银联的错误代码对应的移动商城的返回码
	 */
	private static Map<String, String> tpayParams = new HashMap<String, String>();
	static {
		tpayParams.put("9999", "015A03"); // 系统错误
		tpayParams.put("2A18", "012A13"); // 信息不一致
		tpayParams.put("3A25", "013A25"); // 接收方机构权限关闭
		tpayParams.put("3A25", "013A25"); // 接收方机构权限关闭

		tpayParams.put("3A17", "013A17"); // 重复交易
		tpayParams.put("3A18", "013A18"); // 无此业务渠道
		tpayParams.put("4A99", "014A04"); // 请求报文格式错误
		tpayParams.put("2A22", "012A17"); // 非移动用户
		tpayParams.put("4A10", "014A10"); // 归属不一致
		tpayParams.put("0000", "010A00"); // 成功
	}

	/**
	 * 根据核心的错误代码找到移动商城对应的错误代码
	 * 
	 * @return
	 */
	public static String getMMarketErrorCode(String coreErrorCode) {
		return tpayParams.get(coreErrorCode);
	}
	
	
	/**
	 * 移动商城应答码
	 * */
	public enum Market{
		MARKET_010A00("010A00","成功"),
		MARKET_013A17("013A17","重发交易"),
		MARKET_012A11("012A11","用户不存在"),
		MARKET_012A13("012A13","信息不一致"),
		MARKET_012A17("012A17","非移动用户"),
		MARKET_013A25("013A25","接收方机构权限关闭"),
		MARKET_013A34("013A34","重复交易，该交易已处理成功"),
		MARKET_014A04("014A04","请求报文数据错误"),
		MARKET_015A05("015A05","请求报文解析错误"),
		MARKET_015A03("015A03","系统错误"),
		MARKET_015A06("015A06","未知错误"),
		MARKET_015A07("015A07","超时未收到响应"),
		MARKET_012A16("012A16","非试点省用户"),
		MARKET_014A05("014A05","该交易不存在"),
		MARKET_014A10("014A10","省代码和手机号码归属省不一致"),
		MARKET_014A08("014A08","该交易正在处理中"),
		MARKET_013A36("013A36","重发交易日期超过两天"),
		MARKET_013A37("013A37","不能查询三天前的交易数据"),
		MARKET_015A02("015A02","充值失败"),
		MARKET_012A18("012A18","该省该业务暂未开通"),
		MARKET_013A02("013A02","退票需隔日"),
		MARKET_013A05("013A05","非当天交易不允许冲正"),
		MARKET_013A14("013A14","该笔交易已被冲正"),
		MARKET_013A15("013A15","该笔交易已完成退费"),
		MARKET_013A19("013A19","未完成对账"),
		MARKET_013A20("013A20","超过时限，不允许退费"),
		MARKET_013A04("013A04","原交易失败，不需要冲正/退款"),
		MARKET_013A18("013A18","此业务渠道无此权限"),
		
		MARKET_015A01("015A01","扣款失败"),
		MARKET_015A04("015A04","停机维护"),
		MARKET_015A11("015A11","交易状态未明，请查询对账结果"),
		MARKET_014A06("014A06","签名验证失败"),
		MARKET_014A11("014A11","批量文件格式错误"),
		MARKET_013A39("013A39","交易未通过，请尝试使用其他银行卡支付或选择其他支付方式"),
		MARKET_013A40("013A40","商户状态不正确"),
		MARKET_012A05("012A05","无权限"),
		MARKET_013A31("013A31","单笔金额超限"),
		MARKET_013A41("013A41","原交易状态不正确"),
		MARKET_013A42("013A42","与原交易信息不符"),
		MARKET_013A43("013A43","已超过最大查询次数或操作过于频繁"),
		MARKET_013A44("013A44","风险受限"),
		MARKET_013A45("013A45","交易不在受理时间范围内"),
		MARKET_013A46("013A46","扣款成功但交易超过规定支付时间"),
		MARKET_012A19("012A19","交易失败，详情请咨询您的发卡行或第三方支付机构"),
		MARKET_012A02("012A02","银行账户信息不存在"),
		MARKET_012A20("012A20","交易失败，发卡银行不支持该商户，请更换其他银行卡"),
		MARKET_012A03("012A03","银行帐户异常"),
		MARKET_012A12("012A12","银行账号余额不足"),
		MARKET_012A21("012A21","输入的密码、有效期或CVN2有误，交易失败"),
		MARKET_012A22("012A22","持卡人身份信息或手机号输入不正确，验证失败"),
		MARKET_012A23("012A23","密码输入次数超限"),
		MARKET_012A24("012A24","此银行卡暂不支持该业务"),
		MARKET_012A25("012A25","输入超时，交易失败"),
		MARKET_012A26("012A26","交易已跳转，等待持卡人输入"),
		MARKET_012A27("012A27","动态口令或短信验证码校验失败"),
		MARKET_012A28("012A28","用户尚未开通相关支付业务"),
		MARKET_012A29("012A29","支付卡已超过有效期"),
		MARKET_015A12("015A12","扣款成功，销账未知"),
		MARKET_015A13("015A13","扣款成功，销账失败"),
		MARKET_013A47("013A47","需要验密开通");
		
		String value;
		String desc;
		Market(String value,String desc){
			this.value=value;
			this.desc=desc;
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

		public static String getDescByValue(String value) {
			String rtn = "";
			int len = Market.values().length;
			Market market[] = Market.values();

			Market temp = null;
			for (int i = 0; i < len; i++) {
				temp = market[i];
				if (temp.toString().equals(value)
						|| temp.getValue().equals(value)) {
					rtn = temp.getDesc();
					break;
				}
			}
			return rtn;
		}
	}
}
