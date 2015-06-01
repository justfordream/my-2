<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page
	import="java.text.SimpleDateFormat,java.util.Calendar,com.huateng.utils.UUIDGenerator"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>网厅支付</title>
</head>
<%
	SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	Calendar calendar = Calendar.getInstance();
	String time = myFormat.format(calendar.getTime());
%>
<body>
	<center>
		<form action="<%=request.getContextPath()%>/rcvShopPay.action"
			method="post">
			<table width="80%">
				<tr>
					<td colspan="4"><center>
							<h3>移动商城模拟缴费</h3>
						</center>
					</td>
				</tr>
				<tr>
					<td>商户标识[MerID]必填:</td>
					<td><input type="text" name="MerID" readonly="readonly"
						value="0055" size="30">
					</td>
				</tr>
				<tr>
					<td>订单号[OrderID]必填:</td>
					<td><input type="text" name="OrderID"
						value="101010000000000000<%=time%>" maxlength="32" size="30">
					</td>
				</tr>
				<tr>
					<td>下单时间[OrderTime]必填:</td>
					<td><input type="text" name="OrderTime" readonly="readonly"
						value="<%=time%>" size="30">
					</td>
				</tr>
				<tr>
					<td>订单总金额，单位为“分”[Payment]必填:</td>
					<td><input type="text" name="Payment" value="30" size="30">
					</td>
				</tr>
				<tr>
					<td>币种，默认人民币，填“RMB”[CurType]必填:</td>
					<td><input type="text" name="CurType" value="01" size="30">
					</td>
				</tr>
				<tr>
					<td>订单的充值金额，单位为“分”[ChargeMoney]缴费必填:</td>
					<td><input type="text" name="ChargeMoney" value="30" size="30">
					</td>
				</tr>
				<tr>
					<td>手机号码[IDValue]缴费必填:</td>
					<td><input type="text" name="IDValue" value="" size="30">
					</td>
				</tr>
				<tr>
					<td>移动用户标识类型 01 [IDType]缴费必填:</td>
					<td><input type="text" name="IDType" value="01" size="30">
					</td>
				</tr>
				<tr>
					<td>省份[HomeProv]缴费必填:</td>
					<td><select name="HomeProv" id="HomeProv">
							<option value="">null</option>
							<option value="311">河北(311)</option>
							<option value="100">北京(100)</option>
							<option value="210">上海(210)</option>
							<option value="250">江苏(250)</option>
							<option value="571">浙江(571)</option>
							<option value="591">福建(591)</option>
							<option value="471">内蒙古(471)</option>
							<option value="220">天津(220)</option>
							<option value="531">山东(531)</option>
							<option value="351">山西(351)</option>
							<option value="551">安徽(551)</option>
							<option value="898">海南(898)</option>

							<option value="200">广东(200)</option>
							<option value="771">广西(771)</option>
							<option value="971">青海(971)</option>
							<option value="270">湖北(270)</option>
							<option value="731">湖南(731)</option>

							<option value="791">江西(791)</option>
							<option value="371">河南(371)</option>
							<option value="891">西藏(891)</option>
							<option value="280">四川(280)</option>
							<option value="230">重庆(230)</option>

							<option value="290">陕西(290)</option>
							<option value="851">贵州(851)</option>
							<option value="871">云南(871)</option>
							<option value="931">甘肃(931)</option>
							<option value="951">宁夏(951)</option>

							<option value="991">新疆(991)</option>
							<option value="431">吉林(431)</option>
							<option value="240">辽宁(240)</option>
							<option value="451">黑龙江(451)</option>
					</select>
					</td>
				</tr>
				<tr>
					<td>产品数量[ProdCnt]:</td>
					<td><input type="text" name="ProdCnt" value="1" size="30">
					</td>
				</tr>
				<tr>
					<td>充值产品编号[ProdID]:</td>
					<td><input type="text" name="ProdID"
						value="202010000000000000<%=time%>" size="30">
					</td>
				</tr>
				<tr>
					<td>佣金[Commision]:</td>
					<td><input type="text" name="Commision" value="" size="30">
					</td>
				</tr>
				<tr>
					<td>积分返点费用 单位为“分”[RebateFee]:</td>
					<td><input type="text" name="RebateFee" value="" size="30">
					</td>
				</tr>
				<tr>
					<td>产品折减金额 单位为“分”[ProdDiscount]:</td>
					<td><input type="text" name="ProdDiscount" value="" size="30">
					</td>
				</tr>
				<tr>
					<td>信用卡费用 单位为“分”[CreditCardFee]:</td>
					<td><input type="text" name="CreditCardFee" value="" size="30">
					</td>
				</tr>
				<tr>
					<td>服务费用 单位为“分”[ServiceFee]:</td>
					<td><input type="text" name="ServiceFee" value="" size="30">
					</td>
				</tr>
				<tr>
					<td>营销活动编号[ActivityNO]:</td>
					<td><input type="text" name="ActivityNO" value="" size="30">
					</td>
				</tr>
				<tr>
					<td>商品上架编码[ProdShelfNO]:</td>
					<td><input type="text" name="ProdShelfNO" value="" size="30">
					</td>
				</tr>
				<tr>
					<td>统一支付系统向此URL发送充值结果通知[MerURL]:</td>
					<td><input type="text" name="MerURL"
						value="http://127.0.0.1:8080/CMUPayImitator/ReceiveMsgAutoResponServletForMobileShop"
						size="40">
					</td>
				</tr>
				<tr>
					<td>此变量维护session[MerVAR]必填:</td>
					<td><input type="text" name="MerVAR" value="0" size="30">
					</td>
				</tr>
				<tr>
					<td>用户界面返回URL，银行发送支付结果通知[BackURL]:</td>
					<td><input type="text" name="BackURL"
						value="http://127.0.0.1:8080/CMUPayWebPay/CmuShopResult.jsp"
						size="40">
					</td>
				</tr>
				<tr>
					<td>语言，默认为空[Lang]:</td>
					<td><input type="text" name="Lang" value="" size="30">
					</td>
				</tr>
				<tr>
					<td>银行编码[BankID]必填:</td>
					<td><select name="BankID" id="BankID">
							<option value="0001">0001-中国移动系统</option>
							<option value="0002">0002-招商银行</option>
							<option value="0003">0003-农业银行</option>
							<option value="0004">0004-建设银行</option>
							<option value="0005">0005-浦发银行</option>
							<option value="0006">0006-中国银行</option>
							<option value="0007">0007-工商银行</option>
							<option value="0008">0008-交通银行</option>
							<option value="0009">0009-广发银行</option>
							<option value="0010">0010-中信银行</option>
							<option value="0011">0011-民生银行</option>
							<option value="0012">0012-兴业银行</option>
							<option value="0013">0013-光大银行</option>
							<option value="0014">0014-华夏银行</option>
							<option value="0015">0015-上海银行</option>
							<option value="0016">0016-上海农商</option>
							<option value="0017">0017-邮政储蓄</option>
							<option value="0018">0018-北京银行</option>
							<option value="0019">0019-北京农商</option>
							<option value="0020">0020-平安银行</option>
							<option value="0051">0051-浙江电商运营中心</option>
							<option value="0052">0052-天猫商城</option>
							<option value="0053">0053-淘宝商城</option>
							<option value="0054">0054-京东商城</option>
							<option value="0055">0055-移动商城</option>
							<option value="0056">0056-移动手机支付</option>
							<option value="0057">0057-银联</option>
							<option value="0058">0058-支付宝</option>
							<option value="0059">0059-财付通</option>
					</select>
					</td>
				</tr>
				<tr>
					<td>客户端IP[CLIENTIP]必填:</td>
					<td><input type="text" name="CLIENTIP" value="127.0.0.1"
						size="30">
					</td>
				</tr>
				<tr>
					<td>填UPAY10001， 支付请求时填写[MCODE]必填:</td>
					<td><input type="text" name="MCODE" value="UPAY10001"
						size="30">
					</td>
				</tr>
				<tr>
					<td>订单类型[OrderType]:</td>
					<td><select name="OrderType" id="Reserve1">
							<option value="01">支付01</option>
							<option value="02">缴费02</option>
					</select>
					</td>
				</tr>
				<tr>
					<td>订单超时时间[PayTimeoutTime]:</td>
					<td><input type="text" name="PayTimeoutTime"
						value="20151111111111" size="30">
					</td>
				</tr>
				<tr>
					<td>移动商城的子商户号[ShopMerId]支付必填:</td>
					<td><input type="text" name="ShopMerId" value="" size="30">
					</td>
				</tr>
				<tr>
					<td>保留字段1[Reserve1]:</td>
					<td><input type="text" name="Reserve1" value="" size="30">
					</td>
				</tr>
				<tr>
					<td>保留字段2[Reserve2]:</td>
					<td><input type="text" name="Reserve2" value="" size="30">
					</td>
				</tr>
				<tr>
					<td>保留字段3[Reserve3]:</td>
					<td><input type="text" name="Reserve3" value="" size="30">
					</td>
				</tr>
				<tr>
					<td>保留字段4[Reserve4]:</td>
					<td><input type="text" name="Reserve4" value="" size="30">
					</td>
				</tr>
				<tr>
					<td colspan="4" align="center"><center>
							<input type="submit" name="dd" value="提交支付请求">
						</center>
					</td>
				</tr>


			</table>

		</form>
	</center>
	<div></div>
</body>
</html>