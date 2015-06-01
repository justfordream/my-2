<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>网厅支付</title>
<%
	String rspCode = request.getParameter("RspCode");
	if (rspCode == null) {
		rspCode = "";
	}
	String rspInfo = request.getParameter("RspInfo");
	if (rspInfo == null) {
		rspInfo = "";
	}
	System.out.println("rspInfo=============" + rspInfo);
%>
</head>
<body>
	<center style="color: red">
		RspCode:<%=rspCode%><br> RspInfo:<%=rspInfo%>
	</center>
	<center>
		<form action="http://127.0.0.1:8080/CMUPayWebPay/rcvCmuPay.action" method="post">
			<table>
				<tr>
					<td>商户标识[MerID]:</td>
					<td><input type="text" name="MerID" value="0001" size="100"></td>
				</tr>
				<tr>
					<td>订单标识[OrderID]:</td>
					<td><input type="text" name="OrderID" value="001002003" size="100"></td>
				</tr>
				<tr>
					<td>交易日期时间[OrderTime]:</td>
					<td><input type="text" name="OrderTime" value="20130303211010" size="100"></td>
				</tr>
				<tr>
					<td>订单金额，单位为“分”[Payed]:</td>
					<td><input type="text" name="Payed" value="10" size="100"></td>
				</tr>
				<tr>
					<td>币种，默认人民币[CurType]:</td>
					<td><input type="text" name="CurType" value="0" size="100"></td>
				</tr>
				<tr>
					<td>为该用户充值[IDValue]:</td>
					<td><input type="text" name="IDValue" value="13825376543" size="100"></td>
				</tr>
				<tr>
					<td>用户的标识的类型[IDType]:</td>
					<td><input type="text" name="IDType" value="01" size="100"></td>
				</tr>
				<tr>
					<td>统一支付系统向此URL发送支付结果通知[MerURL]:</td>
					<td><input type="text" name="MerURL" value="http://localhost:8080/CMUPayWebPay/redirectResultCmu.action"
						size="100"></td>
				</tr>
				<tr>
					<td>统一支付系统通知支付结果时，携带此变量[MerVAR]:</td>
					<td><input type="text" name="MerVAR" value="00000" size="100"></td>
				</tr>
				<tr>
					<td>用户界面返回URL[BackURL]:</td>
					<td><input type="text" name="BackURL" value="http://127.0.0.1:8080/CMUPayWebPay/CmuPay.jsp" size="100"></td>
				</tr>
				<tr>
					<td>语言，默认为中文版[Lang]:</td>
					<td><input type="text" name="Lang" value="china" size="100"></td>
				</tr>
				<tr>
					<td>省公司在银行侧的签名数据[Sig]:</td>
					<td><input type="text" name="Sig" value="${sig }" size="100"></td>
				</tr>
				<tr>
					<td>银行编码[BankID]:</td>
					<td><input type="text" name="BankID" value="0004" size="100"></td>
				</tr>
				<tr>
					<td>客户端IP[CLIENTIP]:</td>
					<td><input type="text" name="CLIENTIP" value="192.168.1.1" size="100"></td>
				</tr>
				<tr>
					<td>UPAY10001 支付请求时填写[MCODE]:</td>
					<td><input type="text" name="MCODE" value="UPAY10001" size="100"></td>
				</tr>
				<tr>
					<td>发起方机构(此字段为临时字段,需求中没有)HomeProv:</td>
					<td><input type="text" name="HomeProv" value="200" size="100"></td>
				</tr>
			</table>
			<input type="submit" name="dd" value="提交支付请求">
		</form>
	</center>
	<div>参加摘要运算的字段：
		MERID=00000000000000000000000000000000|ORDERID=00000000000000000000000000000000|ORDERTIME=20130303211010&PAYED=5000&CURTYPE=0|IDVALUE=13825376543|IDTYPE=01|MERURL=HTTPS://IBSBJSTAR.CCB.COM.CN/APP/B2CMAINPLAT?CCB_IBSVERSION=V5|MERVAR=00000|BACKURL=HTTPS://IBSBJSTAR.CCB.COM.CN/APP/B2CMAINPLAT?CCB_IBSVERSION=V5|BANKID=0004|
		CLIENTIP=192.168.1.1| MCODE= UPAY10001 注：字符串中变量名必须是大写字母</div>
</body>
</html>