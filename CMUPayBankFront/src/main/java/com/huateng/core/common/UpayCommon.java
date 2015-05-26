package com.huateng.core.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class UpayCommon {
	private static Map<String, String> transCodeMap = new HashMap<String, String>();
	/**
	 * 系统编码和生编码关系
	 */
	private static Map<String, String> params = new HashMap<String, String>();
	static {
		params.put("0001", "0001");// 中国移动系统
		params.put("0002", "0002");// 招商银行
		params.put("0004", "0004");// 建设银行
		params.put("0005", "0005");// 浦发银行
		params.put("0006", "0006");// 中国银行
		params.put("0007", "0007");// 工商银行
		params.put("0008", "0008");// 交通银行
		params.put("471", "4711");// 内蒙古
		params.put("100", "1001");// 北京
		params.put("220", "2201");// 天津
		params.put("531", "5311");// 山东
		params.put("311", "3111");// 河北
		params.put("351", "3511");// 山西
		params.put("551", "5511");// 安徽
		params.put("210", "2101");// 上海
		params.put("250", "2501");// 江苏
		params.put("571", "5711");// 浙江
		params.put("591", "5911");// 福建
		params.put("898", "8981");// 海南
		params.put("200", "2001");// 广东
		params.put("771", "7711");// 广西
		params.put("971", "9711");// 青海
		params.put("270", "2701");// 湖北
		params.put("731", "7311");// 湖南
		params.put("791", "7911");// 江西
		params.put("371", "3711");// 河南
		params.put("891", "8911");// 西藏
		params.put("280", "2801");// 四川
		params.put("230", "2301");// 重庆
		params.put("290", "2901");// 陕西
		params.put("851", "8511");// 贵州
		params.put("871", "8711");// 云南
		params.put("931", "9311");// 甘肃
		params.put("951", "9511");// 宁夏
		params.put("991", "9911");// 新疆
		params.put("431", "4311");// 吉林
		params.put("240", "2401");// 辽宁
		params.put("451", "4511");// 黑龙江
		params.put("999", "999");// 交换中心
		params.put("997", "999");// 南方基地
		
		transCodeMap.put("010001", "签约预处理");
		transCodeMap.put("010002", "签约结果通知");
		transCodeMap.put("010003", "解约");
		transCodeMap.put("010004", "用户签约关系查询");
		transCodeMap.put("010005", "用户签约关系变更");
		transCodeMap.put("010006", "用户身份验证");
		transCodeMap.put("010007", "");
		transCodeMap.put("010008", "");
		transCodeMap.put("010009", "");
		transCodeMap.put("010010", "");
		transCodeMap.put("010011", "用户应缴费用查询");
		transCodeMap.put("010012", "缴费");
		transCodeMap.put("010013", "退费");
		transCodeMap.put("010014", "支付结果通知");
		transCodeMap.put("010015", "缴费相关接口预留");
		transCodeMap.put("010016", "");
		transCodeMap.put("010017", "");
		transCodeMap.put("010001", "");
		transCodeMap.put("010018", "");
		transCodeMap.put("010019", "");
		transCodeMap.put("010020", "");
		transCodeMap.put("010021", "发票打印信息查询");
		transCodeMap.put("010022", "发票打印结果通知");
		transCodeMap.put("010023", "");
		transCodeMap.put("010024", "");
		transCodeMap.put("010025", "");
		transCodeMap.put("010026", "");
		transCodeMap.put("010027", "");
		transCodeMap.put("010028", "");
		transCodeMap.put("010029", "");
		transCodeMap.put("010030", "");
		transCodeMap.put("011000", "交易状态查询");
		transCodeMap.put("011001", "交易冲正");
		
		transCodeMap.put("020001", "银行账号是否可签约查询");
		transCodeMap.put("020002", "签约");
		transCodeMap.put("020003", "解约通知");
		transCodeMap.put("020004", "");
		transCodeMap.put("020005", "");
		transCodeMap.put("020006", "");
		transCodeMap.put("020007", "");
		transCodeMap.put("020008", "");
		transCodeMap.put("020009", "");
		transCodeMap.put("020010", "");
		transCodeMap.put("020011", "支付");
		transCodeMap.put("020012", "退费");
		transCodeMap.put("020013", "");
		transCodeMap.put("020014", "");
		transCodeMap.put("020015", "");
		transCodeMap.put("020016", "");
		transCodeMap.put("020017", "");
		transCodeMap.put("020018", "");
		transCodeMap.put("020019", "");
		transCodeMap.put("020020", "");
		transCodeMap.put("021000", "交易状态查询");
		transCodeMap.put("021001", "交易冲正");
	}

	/**
	 * 根据系统编码取省代码
	 * 
	 * @param sysCode
	 * @return
	 */
	public static String getProvCodeBySysCode(String sysCode) {
		if (params.containsValue(sysCode)) {
			Set<Entry<String, String>> entrySet = params.entrySet();
			for (Entry<String, String> entry : entrySet) {
				if (entry.getValue().equals(sysCode)) {
					return entry.getKey();
				}
			}
		}
		return "";
	}

	/**
	 * 根据省代码取系统编码
	 * 
	 * @param provCode
	 * @return
	 */
	public static String getSysCodeByProvCode(String provCode) {
		return params.get(provCode);
	}

	/**
	 * 获取交易描述信息
	 * 
	 * @param transCode
	 * @return
	 */
	public static String getTransDesc(String transCode) {
		String desc = transCodeMap.get(transCode);
		if (desc == null || "".equals(desc)) {
			desc = transCode;
		}
		return desc;
	}
}
