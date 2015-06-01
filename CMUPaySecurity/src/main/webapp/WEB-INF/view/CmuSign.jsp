<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>网厅签约</title>
<%
	String rspCode = request.getParameter("RspCode");
	if (rspCode == null) {
		rspCode = "";
	}
	String rspInfo = request.getParameter("RspInfo");
	if (rspInfo == null) {
		rspInfo = "";
	}
	System.out.println("rspInfo============="+rspInfo);
	
%>
</head>
<body>
	<center style="color: red">
		RspCode:<%=rspCode%><br> RspInfo:<%=rspInfo%>
	</center>
	<center>
		<form action="http://127.0.0.1:8080/CMUPayWebPay/rcvCmuSign.action" method="post">
			<table>
				<tr>
					<td>会话标识[SessionID]:</td>
					<td><input type="text" name="SessionID" value="00000000000000000000" size="100"></td>
				</tr>
				<tr>
					<td>用户标识类型[IDType]:</td>
					<td><input type="text" name="IDType" value="01" size="100"></td>
				</tr>
				<tr>
					<td>用户ID[IDValue]:</td>
					<td><input type="text" name="IDValue" value="13823615234" size="100"></td>
				</tr>
				<tr>
					<td>后台结果回传URL[ServerURL]:</td>
					<td><input type="text" name="ServerURL" value="http://localhost:8080/CMUPayWebPay/redirectResultCmu.action"
						size="100"></td>
				</tr>
				<tr>
					<td>用户界面返回URL[BackURL]:</td>
					<td><input type="text" name="BackURL" value="http://127.0.0.1:8080/CMUPayWebPay/CmuSign.jsp" size="100"></td>
				</tr>
				<tr>
					<td>语言，默认为中文版[Lang]:</td>
					<td><input type="text" name="Lang" value="china" size="100"></td>
				</tr>
				<tr>
					<td>省公司在银行侧的签名数据[Sig]:</td>
					<td><input type="text" name="Sig" value="${sig}" size="100"></td>
				</tr>
				<tr>
					<td>银行编码[BankID]:</td>
					<td><input type="text" name="BankID" value="0004" size="100"></td>
				</tr>
				<tr>
					<td>签约请求生成时间 格式为YYYYMMDDHH24MISS[SubTime]:</td>
					<td><input type="text" name="SubTime" value="20130303211010" size="100"></td>
				</tr>
				<tr>
					<td>操作流水号[TransactionID]:</td>
					<td><input type="text" name="TransactionID" value="00000000000000000000000000000001" size="100"></td>
				</tr>
				<tr>
					<td>发起方应用域编码[OrigDomain]:</td>
					<td><input type="text" name="OrigDomain" value="0001" size="100"></td>
				</tr>
				<tr>
					<td>客户端IP[CLIENTIP]:</td>
					<td><input type="text" name="CLIENTIP" value="192.168.1.1" size="100"></td>
				</tr>
				<tr>
					<td>UPAY00001 签约请求时填写[MCODE]:</td>
					<td><input type="text" name="MCODE" value="UPAY00001" size="100"></td>
				</tr>
				<tr>
					<td>发起方机构(此字段为临时字段,需求中没有)[HomeProv]:</td>
					<td><input type="text" name="HomeProv" value="200" size="100"></td>
				</tr>
			</table>
			<input type="submit" value="提交签约请求">
		</form>
	</center>
	<div>参加摘要运算的字段： SESSIONID=00000000000000000000|IDTYPE=01&RIDVALUE=13823615234|
		SERVERURL=HTTPS://IBSBJSTAR.CCB.COM.CN/APP/B2CMAINPLAT?CCB_IBSVERSION=V5|BACKURL=HTTPS://IBSBJSTAR.CCB.COM.CN/APP/B2CMAINPLAT?CCB_IBSVERSION=V5|BANKID=0004|SUBTIME=20130303211010|
		TRANSACTIONID=00000000000000000000000000000001| ORIGDOMAIN=0001| CLIENTIP=192.168.1.1| MCODE= UPAY00001
		注：字符串中变量名必须是大写字母</div>
</body>
</html>