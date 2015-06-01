/**
 * 
 */
package com.huateng.cmupay.constant;

import com.huateng.cmupay.controller.cache.CrmErrorCodeCache;

/**
 * 返回代码 常量
 * 
 * @author cmt
 * 
 */
public class RspCodeConstant {

	public enum Wzw {
		// 移动1和2级通用返回码
		WZW_0000("0000", "交易成功"),
		// 移动1级返回码
		WZW_2998("2998", "失败"), WZW_0105("0105", "OSN发现Message Header 格式错误"), WZW_2011(
				"2011", "SP暂停服务"), WZW_2008("2008", "用户无使用该业务的权限"), WZW_2009(
				"2009", "业务暂停服务"), WZW_0113("0113", "HSN内部错误")
				
		/*
		 * , WZW_0101("0101", "OSN发现超时，根据交易属性已发冲正"), WZW_0102( "0102",
		 * "OSN发现超时，根据交易属性不发冲正"), WZW_0103("0103", "OSN发现具有重复发起方交易流水号的交易"),
		 * WZW_0104("0104", "OSN无法根据请求报文找到落地方交换节点"), WZW_0105("0105",
		 * "OSN发现Message Header 格式错误"), WZW_0106("0106",
		 * "OSN发现Message Body格式错误"), WZW_0108("0108",
		 * "HSN发现落地方机构应答Message格式错误"), WZW_0110("0110", "OSN超时，冲正失败"), WZW_0111(
		 * "0111", "OSN内部错误"), WZW_0113("0113", "HSN内部错误"), WZW_0114( "0114",
		 * "OSN至HSN超时"), WZW_0118("0118", "OPARTY未签到此交易"), WZW_0119( "0119",
		 * "HPARTY未签到此交易"),
		 * 
		 * WZW_0126("0126", "请求报文的BIPcode和ACTIVITY CODE不一致"), WZW_0127("0127",
		 * "BMC缺少对应的对帐记录 CODE不一致"), WZW_0128("0128",
		 * "对帐结果报文中的数量字段与报文中实际的记录数不同"), WZW_0129("0129", "OSN拒绝发起方与落地方相同的业务"),
		 * WZW_0130("0130", "发起方节点拒绝请求"), WZW_0131( "0131", "发起方节点已拥塞"),
		 * WZW_0132("0132", "落地方机构拥塞"), WZW_0133( "0133", "落地方机构故障"),
		 * WZW_0134("0134", "请落地方机构超时"),
		 * 
		 * WZW_0202("0202", "OSN已经对此交易发起冲正"), WZW_0203("0203", "未找到被冲正的交易"),
		 * WZW_0205( "0205", "隔日冲正"), WZW_0206("0206", "根据交易类型定义被冲正的交易不可以冲正"),
		 * WZW_0207( "0207", "OSN发现原交易已经失败,不需要冲正"), WZW_0291("0291", "冲正失败"),
		 * 
		 * WZW_0300("0300", "MC密钥产生失败"), WZW_0301("0301", "OSN密码重加密失败"),
		 * WZW_0310( "0310", "签到失败"), WZW_0320("0320", "签退失败"),
		 * 
		 * WZW_0401("0401", "BMC未完成此交易日的报文处理"), WZW_0402("0402",
		 * "只允许查询前一逻辑交易日之前的报文"),
		 * 
		 * WZW_1001("1001", "手续费计算错误"), WZW_1002("1002", "缴费实付总金额不等于分帐号缴费金额的和"),
		 * WZW_1003( "1003", "智能网方式mzone用户不能异地缴费"),
		 * 
		 * WZW_2001("2001", "标识类型错误"), WZW_2002("2002", "标识值错误"),
		 * WZW_2003("2003", "业务类型错误"), WZW_2004("2004", "操作代码错误"),
		 * WZW_2005("2005", "操作时间错误"), WZW_2006("2006", "SP企业代码错误"),
		 * WZW_2007("2007", "SP业务代码错误"), WZW_2008("2008", "用户无使用该业务的权限"),
		 * WZW_2009("2009", "业务暂停服务"), WZW_2010("2010", "业务尚未开通"),
		 * 
		 * WZW_2011("2011", "SP暂停服务"), WZW_2012("2012", "用户没有定购该业务"), WZW_2013(
		 * "2013", "用户不能取消该业务"), WZW_2014("2014", "资料受理信息错误"), WZW_2015( "2015",
		 * "非法用户状态，无法开通业务"),
		 * 
		 * WZW_2101("2101", "务级别错误"), WZW_2102("2102", "客服密码验证未通过"), WZW_2103(
		 * "2103", "客户证件类型错误"), WZW_2104("2104", "客户证件号码错误"), WZW_2105( "2105",
		 * "开始日期或终止日期错误"), WZW_2106("2106", "备卡sim卡号不存在"), WZW_2107( "2107",
		 * "手机号对应的用户不存在"), WZW_2108("2108", "vip卡号对应的大客户不存在"), WZW_2109( "2109",
		 * "重复进行17201业务受理，如已开通（注销、暂停）17201用户再次申请开通（注销、暂停）"), WZW_2110( "2110",
		 * "重复进行WLAN业务受理，入已开通（注销、暂停）WLAN业务的用户再次申请开通（注销、暂停）"), WZW_2111( "2111",
		 * "异地写卡业务中，获取写卡信息失败，失败原因：找不到该号码所述字段"),
		 * 
		 * WZW_2112("2112", "此手机号码已经成功申请过凭证式业务包开通"), WZW_2113("2113",
		 * "用户还未申请开通业务，就更改或申请密码"), WZW_2114("2114", "大客户卡过期"), WZW_2115( "2115",
		 * "号码并没有对应的用户存在"), WZW_2116("2116", "客户已经欠费停机"), WZW_2117( "2117",
		 * "客户已经销号"), WZW_2118("2118", "客户归属地限制业务，不能通过跨区为其服务"),
		 * 
		 * WZW_2211("2211", "异地写卡业务中，获取写卡信息失败，失败原因：该号段数据量不足"), WZW_2212("2212",
		 * "异地写卡业务中，获取写卡信息失败，失败原因：申请备拒绝"),
		 * 
		 * WZW_3001("3001", "标识类型错误"), WZW_3002("3003", "标识值错误"),
		 * WZW_3003("3003", "业务类型错误"),
		 * 
		 * WZW_4001("4001", "数据类型不匹配"), WZW_4002("4002", "数据不在规定的可选值(枚举)之内"),
		 * WZW_4003( "4003", "数据长度小于规定的最小长度"), WZW_4004("4004",
		 * "数据长度大于规定的最大长度"), WZW_4005( "4005", "数据不符合规定的格式(如日期格式)"),
		 * WZW_4006("4006", "数据值小于规定的最小值"), WZW_4007( "4007", "数据值大于规定的最大值")
		 */;

		String value;
		String desc;

		Wzw(String value, String desc) {
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

		public static String getDescByValue(String value) {
			String rtn = "";
			int len = Wzw.values().length;
			Wzw wzw[] = Wzw.values();

			Wzw temp = null;
			for (int i = 0; i < len; i++) {
				temp = wzw[i];
				if (temp.toString().equals(value)
						|| temp.getValue().equals(value)) {
					rtn = temp.getDesc();
					break;
				}
			}
			return rtn;
		}

	}

	public enum Crm {
		CRM_0000("0000", "成功"),
		// 移动 2级返回码
		CRM_2A00("2A00", "签约信息为其它银行账号关联"), CRM_2A01("2A01", "用户已经签约其它账户"), CRM_2A02(
				"2A02", "银行账户信息不存在"), CRM_2A03("2A03", "银行帐户异常"), CRM_2A04(
				"2A04", "副号码状态异常"), CRM_2A05("2A05", "无权限"), CRM_2A06("2A06",
				"发票未打印"), CRM_2A07("2A07", "发票已打印"), CRM_2A08("2A08", "用户状态异常"), CRM_2A09(
				"2A09", "不存在该用户的签约关系"), CRM_2A10("2A10", "该签约关系已存在"),

		CRM_2A11("2A11", "该用户不存在"), CRM_2A12("2A12", "银行账号余额不足"), CRM_2A13(
				"2A13", "主号签约信息不存在"), CRM_2A14("2A14", "副号不存在"), CRM_2A15(
				"2A15", "主副号绑定关系已存在"), CRM_2A16("2A16", "副号已绑定其他主号"), CRM_2A17(
				"2A17", "副号绑定信息不存在"), CRM_2A18("2A18", "信息不一致"), CRM_2A19(
				"2A19", "未实名登记"), CRM_2A20("2A20", "证件类型不符"), CRM_2A99("2A99",
				"其他错误"), CRM_2A21("2A21", "非试点省用户"), CRM_2A22("2A22", "非移动用户"),

		CRM_3A00("3A00", "帐号签约已超过最大签约个数"), CRM_3A01("3A01", "账户的累计额度超限"), CRM_3A02(
				"3A02", "退票需隔日"), CRM_3A03("3A03", "不容许加为副号码"), CRM_3A04(
				"3A04", "原交易失败，不需要冲正/退款"), CRM_3A05("3A05", "非当天交易不允许冲正"), CRM_3A06(
				"3A06", "无法打印非银行渠道缴费发票"), CRM_3A07("3A07", "无法打印其它银行缴费发票"), CRM_3A08(
				"3A08", "非号码归属地银行无法打印发票"), CRM_3A09("3A09", "号码归属省不允许银行代打发票"), CRM_3A10(
				"3A10", "不允许设置为副号"),CRM_3A31("3A31","单笔金额超限"),CRM_3A32("3A32","日累计金额超限"),
				CRM_3A33("3A33","月累计金额超限"),

		CRM_3A11("3A11", "证件类型不符"), CRM_3A12("3A12", "副号码把主号码设置成黑名单了，导致失败"), CRM_3A13(
				"3A13", "用户其它业务与签约存在冲突"), CRM_3A14("3A14", "该笔交易已被冲正"), CRM_3A15(
				"3A15", "该笔交易已完成退费"), CRM_3A16("3A16", "账号长度不对"), CRM_3A17(
				"3A17", "该交易为重复交易"), CRM_3A18("3A18", "此业务渠道无此权限"), CRM_3A19(
				"3A19", "未完成对账"), CRM_3A20("3A20", "超过时限，不允许退费"),

		CRM_3A21("3A21", "主号绑定副号已超过最大绑定个数"), CRM_3A22("3A22",
				"该用户已签约为主号，不能绑定为副号"), CRM_3A23("3A23", "该用户已绑定为副号，不能签约为主号"), CRM_3A24(
				"3A24", "主号码设置关联副号码48小时内不可为副号码发起主动缴费"), CRM_3A25("3A25",
				"接收方机构权限关闭"), CRM_3A26("3A26", "银行缴费成功，省公司充值失败"), CRM_3A28(
				"3A28", "后续费账户不允许更改阀值"), CRM_3A29("3A29", "主号填写错误"), CRM_3A30(
				"3A30", "签约协议号填写错误"),CRM_3A34("3A34","重复交易，该交易已经成功处理"), CRM_3A35("3A35", "该机构该业务暂未开通"), CRM_3A36("3A36", "该营销活动不存在"),
				CRM_3A37("3A37", "营销活动已过期"),

		CRM_4A00("4A00", "缴费记录不存在"), CRM_4A01("4A01", "非本省用户"), CRM_4A02(
				"4A02", "交易类型错误"), CRM_4A03("4A03", "产品服务范围错误"), CRM_4A04(
				"4A04", "请求报文数据错误"), CRM_4A05("4A05", "该笔交易不存在"), CRM_4A06(
				"4A06", "签名验证失败"), CRM_4A07("4A07", "crm签名验证失败"), CRM_4A08(
				"4A08", "银行端签名验证失败"), CRM_4A30("4A30", "交易处理中"), CRM_4A99(
				"4A99", "参数不正确（参数有误的总的返回码）"),

		CRM_5A00("5A00", "计费记录错误"), CRM_5A01("5A01", "扣款失败"), CRM_5A02("5A02",
				"充值失败"), CRM_5A03("5A03", "系统错误"), CRM_5A04("5A04", "停机维护"), CRM_5A05(
				"5A05", "解析报文失败"), CRM_5A06("5A06", "系统未知错误"), CRM_5A07("5A07",
				"超时未收到响应"), CRM_5A08("5A08", "超过每秒最大交易数量"), CRM_5A09("5A09",
				"系统繁忙"), CRM_5A10("5A10", "银行端返回失败"), CRM_5A11("5A11",
				"crm端返回失败"), CRM_5A12("5A12", "银行端交易超时"), CRM_5A13("5A13",
				"crm端交易超时"),

		// 通用返回码
		CRM_2998("2998", "失败"),

		CRM_0101("0101", "OSN发现超时，根据交易属性已发冲正"), CRM_0102("0102",
				"OSN发现超时，根据交易属性不发冲正"), CRM_0103("0103", "OSN发现具有重复发起方交易流水号的交易"), CRM_0104(
				"0104", "OSN无法根据请求报文找到落地方交换节点"), CRM_0105("0105",
				"OSN发现Message Header 格式错误"), CRM_0106("0106",
				"OSN发现Message Body格式错误"), CRM_0108("0108",
				"HSN发现落地方机构应答Message格式错误"), CRM_0110("0110", "OSN超时，冲正失败"),

		CRM_0111("0111", "OSN内部错误"), CRM_0113("0113", "HSN内部错误"), CRM_0114(
				"0114", "OSN至HSN超时"), CRM_0118("0118", "OPARTY未签到此交易"), CRM_0119(
				"0119", "HPARTY未签到此交易"),

		CRM_0126("0126", "请求报文的BIPcode和ACTIVITY CODE不一致"), CRM_0127("0127",
				"BMC缺少对应的对帐记录 CODE不一致"), CRM_0128("0128",
				"对帐结果报文中的数量字段与报文中实际的记录数不同"), CRM_0129("0129",
				"OSN拒绝发起方与落地方相同的业务"), CRM_0130("0130", "发起方节点拒绝请求"), CRM_0131(
				"0131", "发起方节点已拥塞"), CRM_0132("0132", "落地方机构拥塞"), CRM_0133(
				"0133", "落地方机构故障"), CRM_0134("0134", "请落地方机构超时"),

		CRM_0202("0202", "OSN已经对此交易发起冲正"), CRM_0203("0203", "未找到被冲正的交易"), CRM_0205(
				"0205", "隔日冲正"), CRM_0206("0206", "根据交易类型定义被冲正的交易不可以冲正"), CRM_0207(
				"0207", "OSN发现原交易已经失败,不需要冲正"), CRM_0291("0291", "冲正失败"),

		CRM_0300("0300", "MC密钥产生失败"), CRM_0301("0301", "OSN密码重加密失败"), CRM_0310(
				"0310", "签到失败"), CRM_0320("0320", "签退失败"),

		CRM_0401("0401", "BMC未完成此交易日的报文处理"), CRM_0402("0402",
				"只允许查询前一逻辑交易日之前的报文"),

		CRM_1001("1001", "手续费计算错误"), CRM_1002("1002", "缴费实付总金额不等于分帐号缴费金额的和"), CRM_1003(
				"1003", "智能网方式mzone用户不能异地缴费"),

		CRM_2001("2001", "标识类型错误"), CRM_2002("2002", "标识值错误"), CRM_2003("2003",
				"业务类型错误"), CRM_2004("2004", "用户已单向停机"), CRM_2005("2005",
				"用户已停机"), CRM_2006("2006", "用户预销户"), CRM_2007("2007", "用户销户"), CRM_2008(
				"2008", "用户预开通"), CRM_2009("2009", "用户状态非法"), CRM_2010("2010",
				"用户的业务已暂停"), CRM_2011("2011", "SP暂停服务"), CRM_2012("2012",
				"用户没有定购该业务"), CRM_2013("2013", "用户不能取消该业务"), CRM_2014("2014",
				"资料受理信息错误"), CRM_2015("2015", "非法用户状态，无法开通业务"),

		CRM_2101("2101", "服务级别错误"), CRM_2102("2102", "客服密码验证未通过"), CRM_2103(
				"2103", "客户证件类型错误"), CRM_2104("2104", "客户证件号码错误"), CRM_2105(
				"2105", "开始日期或终止日期错误"), CRM_2106("2106", "备卡sim卡号不存在"), CRM_2107(
				"2107", "手机号对应的用户不存在"), CRM_2108("2108", "vip卡号对应的大客户不存在"), CRM_2109(
				"2109", "重复进行17201业务受理，如已开通（注销、暂停）17201用户再次申请开通（注销、暂停）"), CRM_2110(
				"2110", "重复进行WLAN业务受理，入已开通（注销、暂停）WLAN业务的用户再次申请开通（注销、暂停）"), CRM_2111(
				"2111", "异地写卡业务中，获取写卡信息失败，失败原因：找不到该号码所述字段"), CRM_2112("2112",
				"此手机号码已经成功申请过凭证式业务包开通"), CRM_2113("2113", "用户还未申请开通业务，就更改或申请密码"), CRM_2114(
				"2114", "大客户卡过期"), CRM_2115("2115", "号码并没有对应的用户存在"), CRM_2116(
				"2116", "客户已经欠费停机"), CRM_2117("2117", "客户已经销号"), CRM_2118(
				"2118", "客户归属地限制业务，不能通过跨区为其服务"),

		CRM_3001("3001", "标识类型错误"), CRM_3002("3002", "标识值错误"), CRM_3003("3003",
				"业务类型错误"),

		GATA_CODE_00002("UPAY00002", "UPAY00002"), GATA_CODE_10002("UPAY10002",
				"UPAY10002"),

		CRM_4001("4001", "数据类型不匹配"), CRM_4002("4002", "数据不在规定的可选值(枚举)之内"), CRM_4003(
				"4003", "数据长度小于规定的最小长度"), CRM_4004("4004", "数据长度大于规定的最大长度"), CRM_4005(
				"4005", "数据不符合规定的格式(如日期格式)"), CRM_4006("4006", "数据值小于规定的最小值"), CRM_4007(
				"4007", "数据值大于规定的最大值"),

		CRM_2211("2211", "异地写卡业务中，获取写卡信息失败，失败原因：该号段数据量不足"), CRM_2212("2212",
				"异地写卡业务中，获取写卡信息失败，失败原因：申请备拒绝"),
		
		//新增加2A99返回码,应对省返回的5999
		CRM_5999("2A99", "其他错误");
		
		
		
		String value;
		String desc;

		Crm(String value, String desc) {
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

		public static String getDescByValue(String value) {
			String rtn = "";
			int len = Crm.values().length;
			Crm crm[] = Crm.values();
			
			
			Crm temp = null;
			for (int i = 0; i < len; i++) {
				temp = crm[i];
				if (temp.toString().equals(value)
						|| temp.getValue().equals(value)) {
					rtn = temp.getDesc();
					break;
				}
			}
			return rtn;
		}
		
		/*
		 * 
		 * 添加通过传过来返回码得到Crm枚举对象,二级返回码专用
		 */
		private static Crm getCrmSecondByCode(String code){
			Crm crm = null;
			if(null == code || "".equals(code.trim()) || code.trim().length() != 4){
				return null;
			}
			try{
				crm = Crm.valueOf("CRM_"+code);
			}catch(IllegalArgumentException ie){
				crm = null;
			}catch(Exception e){
				crm = null;
			}
			return crm;
		}
		
		/**
		 * 
		 * 通过code获得省端二级返回码Value
		 */
		public static String getCrmSecondValueByCode(String code){
			Crm crm = getCrmSecondByCode(code);
			String rtnValue = ( crm == null ? CrmErrorCodeCache.getCrmErrCode(code) : crm.getValue() );
			return rtnValue;
		}
		
		/**
		 * 
		 * 通过code获得省端二级返回码Desc
		 */
		public static String getCrmSecondDescByCode(String code){
			Crm crm = getCrmSecondByCode(code);
			String rtnDesc = ( crm == null ?"":crm.getDesc() );
			return rtnDesc;
		}
		
		
	}
	/**
	 * 移动商城rsptype
	 * */
	public enum MarketRspType{
		MARKETRSPTYPE_00("0","成功"),MARKETRSPTYPE_01("1","系统错误"),MARKETRSPTYPE_02("2","业务错误");
		String value;
		String desc;
		
		MarketRspType(String value,String desc){
			this.value=value;
			this.desc=desc;
		}
		@Override
		public String toString(){
			return this.value;
		}
		
		public String getValue(){
			return this.value;
		}
		
		public String getDesc(){
			return this.desc;
		}
		
		public static String getDescByValue(String value) {
			String rtn = "";
			int len = Upay.values().length;
			Upay upay[] = Upay.values();

			Upay temp = null;
			for (int i = 0; i < len; i++) {
				temp = upay[i];
				if (temp.toString().equals(value)
						|| temp.getValue().equals(value)) {
					rtn = temp.getDesc();
					break;
				}
			}
			return rtn;
		}
	}
	/**
	 * 移动商城查询请求类型
	 * */
	public enum MarketQueryType{
		MARKETQUERYTYPE_01("01","通过报文体ReqTransID查询"),MARKETQUERYTYPE_02("02","通过原充值交易的OrderID查询");
		String value;
		String desc;
		
		MarketQueryType(String value,String desc){
			this.value=value;
			this.desc=desc;
		}
		@Override
		public String toString(){
			return this.value;
		}
		
		public String getValue(){
			return this.value;
		}
		
		public String getDesc(){
			return this.desc;
		}
		
		public static String getDescByValue(String value) {
			String rtn = "";
			int len = Upay.values().length;
			Upay upay[] = Upay.values();

			Upay temp = null;
			for (int i = 0; i < len; i++) {
				temp = upay[i];
				if (temp.toString().equals(value)
						|| temp.getValue().equals(value)) {
					rtn = temp.getDesc();
					break;
				}
			}
			return rtn;
		}
	}
	
	/**
	 * 移动商城内部应答码
	 * 
	 * @author Administrator
	 * 
	 */
	public enum MarketInnerCode {
		MARKET_014A10("A0143","省代码和手机号码归属省不一致"),
		MARKET_012A17("A0092","非移动用户"),
		MARKET_014A04("A0137","请求报文数据错误"),
		MARKET_013A18("A0113","此业务渠道无此权限"),
		MARKET_013A17("A0112","重发交易"),
		MARKET_014A05("A0138","该交易不存在"),
		MARKET_015A03("A0147", "系统错误"), 
		MARKET_010A00("A0008", "成功");
		private String value;
		private String desc;

		private MarketInnerCode(String value, String desc) {
			this.value = value;
			this.desc = desc;
		}

		public String getValue() {
			return value;
		}

		public String getDesc() {
			return desc;
		}
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
	/**
	 * 浙江天猫端应答码
	 * @author panlg
	 *
	 */
	public enum Tmall{
		TMALL_030A01("030A01", "重发交易日期超过两天");
		
		String value;
		String desc;

		Tmall(String value, String desc) {
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

		public static String getDescByValue(String value) {
			String rtn = "";
			int len = Upay.values().length;
			Upay upay[] = Upay.values();

			Upay temp = null;
			for (int i = 0; i < len; i++) {
				temp = upay[i];
				if (temp.toString().equals(value)
						|| temp.getValue().equals(value)) {
					rtn = temp.getDesc();
					break;
				}
			}
			return rtn;
		}
	}
	
	
	/**
	 * 机构编码
	 * @author Administrator
	 *
	 */
	public enum OrgId{
		ORGID_0055("0055", "移动商城");
		
		String value;
		String desc;

		OrgId(String value, String desc) {
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

		public static String getDescByValue(String value) {
			String rtn = "";
			int len = Upay.values().length;
			Upay upay[] = Upay.values();

			Upay temp = null;
			for (int i = 0; i < len; i++) {
				temp = upay[i];
				if (temp.toString().equals(value)
						|| temp.getValue().equals(value)) {
					rtn = temp.getDesc();
					break;
				}
			}
			return rtn;
		}
		
	}

	public enum Bank {

		// 中国移动系统提供的应答码
		BANK_010A00("010A00", "成功"), BANK_019A01("019A01", "ActivityCode参数不正确"), BANK_019A02(
				"019A02", "ReqSys参数不正确"), BANK_019A03("019A03",
				"ReqChannel参数不正确"), BANK_019A04("019A04", "ReqDate参数不正确"), BANK_019A05(
				"019A05", "ReqTransID参数不正确"), BANK_019A06("019A06",
				"ReqDateTime参数不正确"), BANK_019A07("019A07", "ActionCode参数不正确"), BANK_019A08(
				"019A08", "RcvSys参数不正确"), BANK_019A09("019A09", "RcvDate参数不正确"), BANK_019A10(
				"019A10", "RcvTransID参数不正确"),

		BANK_019A11("019A11", "RcvDateTime参数不正确"), BANK_019A12("019A12",
				"RspCode参数不正确"), BANK_019A13("019A13", "RspDesc参数不正确"), BANK_019A14(
				"019A14", "SignFlag参数不正确"), BANK_019A15("019A15", "CerID参数不正确"), BANK_019A16(
				"019A16", "SignValue参数不正确"), BANK_019A17("019A17",
				"IDType参数不正确"), BANK_019A18("019A18", "IDValue参数不正确"), BANK_019A19(
				"019A19", "SessionID参数不正确"), BANK_019A20("019A20", "SubID参数不正确"),

		BANK_019A21("019A21", "SubTime参数不正确"), BANK_019A22("019A22",
				"BankAcctID参数不正确"), BANK_019A23("019A23", "BankAcctType参数不正确"), BANK_019A24(
				"019A24", "PayType参数不正确"), BANK_019A25("019A25",
				"RechThreshold参数不正确"), BANK_019A26("019A26", "RechAmount参数不正确"), BANK_019A27(
				"019A27", "OrderID参数不正确"), BANK_019A28("019A28", "MerVAR参数不正确"), BANK_019A29(
				"019A29", "Payed参数不正确"), BANK_019A30("019A30",
				"OrigReqSys参数不正确"),

		BANK_019A31("019A31", "OriReqDate参数不正确"), BANK_019A32("019A32",
				"OriReqTransID参数不正确"), BANK_019A33("019A33",
				"RevokeReason参数不正确"), BANK_019A34("019A34", "BankProv参数不正确"), BANK_019A35(
				"019A35", "InvoiceID参数不正确"), BANK_019A36("019A36",
				"PrintStatus参数不正确"), BANK_019A37("019A37", "InvoiceCode参数不正确"), BANK_019A38(
				"019A38", "InvoiceNo参数不正确"), BANK_019A39("019A39",
				"PrintDate参数不正确"), BANK_019A40("019A40", "Opr参数不正确"),

		BANK_019A41("019A41", "UserName参数不正确"), BANK_019A42("019A42",
				"UserIDType参数不正确"), BANK_019A43("019A43", "UserID参数不正确"), BANK_019A44(
				"019A44", "HomeProv参数不正确"), BANK_019A45("019A45",
				"TransType参数不正确"), BANK_019A46("019A46", "OriTransType参数不正确"), BANK_019A47(
				"019A47", "OriRcvDate参数不正确"), BANK_019A48("019A48",
				"OriRcvTransID参数不正确"), BANK_019A49("019A49", "OriRspCode参数不正确"), BANK_019A50(
				"019A50", "OriRspDesc参数不正确"), BANK_019A51("019A51", "保留以后备用"),

		BANK_012A00("012A00", "签约信息为其它银行账号关联"), BANK_012A01("012A01",
				"用户已经签约其它账户"), BANK_012A02("012A02", "银行账户信息不存在"), BANK_012A03(
				"012A03", "银行帐户异常"), BANK_012A04("012A04", "副号码状态异常"), BANK_012A05(
				"012A05", "无权限"), BANK_012A06("012A06", "发票未打印"), BANK_012A07(
				"012A07", "发票已打印"), BANK_012A08("012A08", "用户状态异常"), BANK_012A09(
				"012A09", "不存在该用户的签约关系"), BANK_012A10("012A10", "该签约关系已存在"), BANK_012A11(
				"012A11", "该用户不存在"), BANK_012A12("012A012", "银行账号余额不足"), BANK_012A13(
				"012A13", "信息不一致"), BANK_012A14("012A14", "未实名登记"), BANK_012A15(
				"012A15", "证件类型不符"), BANK_012A16("012A16", "非试点省用户"), BANK_012A17(
				"012A17", "非移动用户"), BANK_012A18("012A18", "该机构该业务暂未开通"), BANK_012A99(
				"012A99", "其他错误"),

		BANK_013A00("013A00", "帐号签约已超过最大签约个数"), BANK_013A01("013A01",
				"账户的累计额度超限"), BANK_013A02("013A02", "退票需隔日"), BANK_013A03(
				"013A03", "不容许加为副号码"), BANK_013A04("013A04", "原交易失败，不需要冲正/退款"), BANK_013A05(
				"013A05", "非当天交易不允许冲正"), BANK_013A06("013A06", "无法打印非银行渠道缴费发票"), BANK_013A07(
				"013A07", "无法打印其它银行缴费发票"), BANK_013A08("013A08",
				"非号码归属地银行无法打印发票"), BANK_013A09("013A09", "号码归属省不允许银行代打发票"), BANK_013A10(
				"013A10", "不允许设置为副号"),

		BANK_013A11("013A11", "证件类型不符"), BANK_013A12("013A12",
				"副号码把主号码设置成黑名单了，导致失败"), BANK_013A13("013A13", "用户其它业务与签约存在冲突"), BANK_013A14(
				"013A14", "该笔交易已被冲正"), BANK_013A15("013A15", "该笔交易已完成退费"), BANK_013A16(
				"013A16", "账号长度不对"), BANK_013A17("013A17", "该交易为重复交易"), BANK_013A18(
				"013A18", "此业务渠道无此权限"), BANK_013A19("013A19", "未完成对账"), BANK_013A20(
				"013A20", "超过时限，不允许退费"), BANK_013A21("013A21", "不允许冲正"), BANK_013A22(
				"013A22", "该用户已绑定为副号，不能签约为主号"), BANK_013A23("013A23",
				"查询银行和签约银行不同，不允许查询"), BANK_013A24("013A24",
				"主号码设置关联副号码48小时内不可为副号码发起主动缴费"), BANK_013A25("013A25",
				"接收方交易权限关闭"), BANK_013A26("013A26","银行缴费成功，省公司充值失败"), 
				BANK_013A28("013A28","后付费账户不允许更改阀值"),BANK_013A29("013A29","主号填写错误"),
				BANK_013A30("013A30", "签约协议号填写错误"),BANK_013A31("013A31", "单笔金额超限"),
				BANK_013A32("013A32", "日累计金额超限"),BANK_013A33("013A33", "月累计金额超限"),
				BANK_013A34("013A34", "重复交易，该交易已成功"),
				BANK_013A35("013A35", "超过交易限制期限"),BANK_013A36("013A36", "该营销活动不存在"),
				BANK_013A37("013A37", "营销活动已过期"),BANK_013A38("013A38", "不能查询三天前的交易数据"),
				BANK_013A38_30("013A38", "不能查询三十天前的交易数据"),

		BANK_014A00("014A00", "缴费记录不存在"), BANK_014A01("014A01", "非本省用户"), BANK_014A02(
				"014A02", "交易类型错误"), BANK_014A03("014A03", "产品服务范围错误"), BANK_014A04(
				"014A04", "请求报文数据错误"), BANK_014A05("014A05", "该笔交易不存在"), BANK_014A06(
				"014A06", "签名验证失败"), BANK_014A07("014A07", "证书失效"), BANK_014A08(
				"014A08", "该笔交易正在处理中"),BANK_014A09("014A09", "交易信息与产品定义不符"),
				
				BANK_014A10("014A10", "省代码和手机号码归属省不一致"),/*BANK_014A30("014A30", "正在处理中"),*/

		BANK_015A00("015A00", "计费记录错误"), BANK_015A01("015A01", "扣款失败"), BANK_015A02(
				"015A02", "充值失败"), BANK_015A03("015A03", "系统错误"), BANK_015A04(
				"015A04", "停机维护"), BANK_015A05("015A05", "解析报文失败"), BANK_015A06(
				"015A06", "未知错误"), BANK_015A07("015A07", "超时未收到响应"), BANK_015A08(
				"015A08", "超过每秒最大交易数量"), BANK_015A09("015A09", "系统繁忙"),BANK_015A10("015A10", "号码归属省该业务处于签退状态"), /*BANK_015A14(
				"015A14", "网状网错误"),*/

		// 银行系统提供的应答码
		BANK_020A00("020A00", "成功"), BANK_029A01("029A01", "ActivityCode参数不正确"), BANK_029A02(
				"029A02", "ReqSys参数不正确"), BANK_029A03("029A03",
				"ReqChannel参数不正确"), BANK_029A04("029A04", "ReqDate参数不正确"), BANK_029A05(
				"029A05", "ReqTransID参数不正确"), BANK_029A06("029A06",
				"ReqDateTime参数不正确"), BANK_029A07("029A07", "ActionCode参数不正确"), BANK_029A08(
				"029A08", "RcvSys参数不正确"), BANK_029A09("029A09", "RcvDate参数不正确"), BANK_029A10(
				"029A10", "RcvTransID参数不正确"),

		BANK_029A11("029A11", "RcvDateTime参数不正确"), BANK_029A12("029A12",
				"RspCode参数不正确"), BANK_029A13("029A13", "RspDesc参数不正确"), BANK_029A14(
				"029A14", "SignFlag参数不正确"), BANK_029A15("029A15", "CerID参数不正确"), BANK_029A16(
				"029A16", "SignValue参数不正确"), BANK_029A17("029A17",
				"IDType参数不正确"), BANK_029A18("029A18", "IDValue参数不正确"), BANK_029A19(
				"029A19", "SessionID参数不正确"), BANK_029A20("029A20", "SubID参数不正确"),

		BANK_029A21("029A21", "SubTime参数不正确"), BANK_029A22("029A22",
				"BankAcctID参数不正确"), BANK_029A23("029A23", "BankAcctType参数不正确"), BANK_029A24(
				"029A24", "PayType参数不正确"), BANK_029A25("029A25",
				"RechThreshold参数不正确"), BANK_029A26("029A26", "RechAmount参数不正确"), BANK_029A27(
				"029A27", "OrderID参数不正确"), BANK_029A28("029A28", "MerVAR参数不正确"), BANK_029A29(
				"029A29", "Payed参数不正确"), BANK_029A30("029A30",
				"OrigReqSys参数不正确"),

		BANK_029A31("029A31", "OriReqDate参数不正确"), BANK_029A32("029A32",
				"OriReqTransID参数不正确"), BANK_029A33("029A33",
				"RevokeReason参数不正确"), BANK_029A34("029A34", "BankProv参数不正确"), BANK_029A35(
				"029A35", "InvoiceID参数不正确"), BANK_029A36("029A36",
				"PrintStatus参数不正确"), BANK_029A37("029A37", "InvoiceCode参数不正确"), BANK_029A38(
				"029A38", "InvoiceNo参数不正确"), BANK_029A39("029A39",
				"PrintDate参数不正确"), BANK_029A40("029A40", "Opr参数不正确"),

		BANK_029A41("029A41", "UserName参数不正确"), BANK_029A42("029A42",
				"UserIDType参数不正确"), BANK_029A43("029A43", "UserID参数不正确"), BANK_029A44(
				"029A44", "HomeProv参数不正确"), BANK_029A45("029A45",
				"TransType参数不正确"), BANK_029A46("029A46", "OriTransType参数不正确"), BANK_029A47(
				"029A47", "OriRcvDate参数不正确"), BANK_029A48("029A48",
				"OriRcvTransID参数不正确"), BANK_029A49("029A49", "OriRspCode参数不正确"), BANK_029A50(
				"029A50", "OriRspDesc参数不正确"), BANK_029A51("029A51", "保留以后备用"),

		BANK_022A00("022A00", "签约信息为其它银行账号关联"), BANK_022A01("022A01",
				"用户已经签约其它账户"), BANK_022A02("022A02", "银行账户信息不存在"), BANK_022A03(
				"022A03", "银行帐户异常"), BANK_022A04("022A04", "副号码状态异常"), BANK_022A05(
				"022A05", "无权限"), BANK_022A06("022A06", "发票未打印"), BANK_022A07(
				"022A07", "发票已打印"), BANK_022A08("022A08", "用户状态异常"), BANK_022A09(
				"022A09", "不存在该用户的签约关系"), BANK_022A10("022A10", "该签约关系已存在"), BANK_022A11(
				"022A01", "该用户不存在"), BANK_022A12("022A02", "银行账号余额不足"),

		BANK_023A00("023A00", "帐号签约已超过最大签约个数"), BANK_023A01("023A01",
				"账户的累计额度超限"), BANK_023A02("023A02", "退票需隔日"), BANK_023A03(
				"023A03", "不容许加为副号码"), BANK_023A04("023A04", "原交易失败，不需要冲正/退款"), BANK_023A05(
				"023A05", "非当天交易不允许冲正"), BANK_023A06("023A06", "无法打印非银行渠道缴费发票"), BANK_023A07(
				"023A07", "无法打印其它银行缴费发票"), BANK_023A08("023A08",
				"非号码归属地银行无法打印发票"), BANK_023A09("023A09", "号码归属省不允许银行代打发票"), BANK_023A10(
				"023A10", "不允许设置为副号"),

		BANK_023A11("023A11", "证件类型不符"), BANK_023A12("023A12",
				"副号码把主号码设置成黑名单了，导致失败"), BANK_023A13("023A13", "用户其它业务与签约存在冲突"), BANK_023A14(
				"023A14", "该笔交易已被冲正"), BANK_023A15("023A15", "该笔交易已完成退费"), BANK_023A16(
				"023A16", "账号长度不对"), BANK_023A17("023A17", "该交易为重复交易"), BANK_023A18(
				"023A18", "此业务渠道无此权限"), BANK_023A19("023A19", "单笔金额超限"), BANK_023A20(
				"023A20", "日累计金额超限"), BANK_023A21("023A21", "月累计金额超限"), BANK_023A22(
				"023A22", "接收方机构权限关闭"), /*BANK_023A23("023A23",
				"查询银行和签约银行不同，不允许查询"), BANK_023A24("023A24",
				"主号码设置关联副号码48小时内不可为副号码发起主动缴费"),*/

		BANK_024A00("024A00", "缴费记录不存在"), BANK_024A01("024A01", "非本省用户"), BANK_024A02(
				"024A02", "交易类型错误"), BANK_024A03("024A03", "产品服务范围错误"), BANK_024A04(
				"024A04", "请求报文数据错误"), BANK_024A05("024A05", "该笔交易不存在"), BANK_024A06(
				"024A06", "签名验证失败"),

		BANK_025A00("025A00", "计费记录错误"), BANK_025A01("025A01", "扣款失败"), BANK_025A02(
				"025A02", "充值失败"), BANK_025A03("025A03", "系统错误"), BANK_025A04(
				"025A04", "停机维护"), BANK_025A05("025A05", "解析报文失败"), BANK_025A06(
				"025A06", "未知错误"), BANK_025A07("025A07", "超时未收到响应"), BANK_025A08(
				"025A08", "超过每秒最大交易数量"), BANK_025A09("025A09", "系统繁忙");

		String value;
		String desc;

		Bank(String value, String desc) {
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

		public static String getDescByValue(String value) {
			String rtn = "";
			int len = Bank.values().length;
			Bank bank[] = Bank.values();

			Bank temp = null;
			for (int i = 0; i < len; i++) {
				temp = bank[i];
				if (temp.toString().equals(value)
						|| temp.getValue().equals(value)) {
					rtn = temp.getDesc();
					break;
				}
			}
			return rtn;
		}

	}

	public enum Upay {
		UPAY_U99999("U99999", "未知错误"), UPAY_U99998("U99998", "超时"), UPAY_U99997(
				"U99997", "路由关闭");

		String value;
		String desc;

		Upay(String value, String desc) {
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

		public static String getDescByValue(String value) {
			String rtn = "";
			int len = Upay.values().length;
			Upay upay[] = Upay.values();

			Upay temp = null;
			for (int i = 0; i < len; i++) {
				temp = upay[i];
				if (temp.toString().equals(value)
						|| temp.getValue().equals(value)) {
					rtn = temp.getDesc();
					break;
				}
			}
			return rtn;
		}
	}

	public enum Gate {
		GATE_0000("0000", "成功"), GATE_9999("9999", "失败");

		String value;
		String desc;

		Gate(String value, String desc) {
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

		public static String getDescByValue(String value) {
			String rtn = "";
			int len = Gate.values().length;
			Gate gate[] = Gate.values();

			Gate temp = null;
			for (int i = 0; i < len; i++) {
				temp = gate[i];
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
