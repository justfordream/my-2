<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.text.SimpleDateFormat,java.util.Calendar" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>模拟银行返回结果</title>
</head>
 <%
    SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	Calendar calendar = Calendar.getInstance();
	String time= myFormat.format(calendar.getTime());
  %>
<body>
	<center>
		<form action="<%=request.getContextPath()%>/rcvCCBResult.action" method="post">
			<table>
			    <tr>
			       <td colspan="4"> <center><h3>模拟建设银行银行接收参数并返回参数</h3></center> </td>
			    </tr>
				<tr>
					<td>商户柜台代码[POSID]:</td>
					<td><input type="text" name="POSID" value="${bankRequest.posId}" size="20"></td>
					<td>分行代码[BRANCHID]:</td>
					<td><input type="text" name="BRANCHID" value="${bankRequest.branChId}" size="20"></td>
				</tr>
				<tr>
					<td>订单号[ORDERID]:</td>
					<td><input type="text" name="ORDERID" value="${bankRequest.orderId}" size="20"></td>
					<td>签约协议号(签约的时候不能为空)[SUBID]:</td>
					<td><input type="text" name="SUBID" value="10101010<%=time %>" size="20"></td>
				</tr>
				<tr>
					<td>付款金额 单位‘元’[PAYMENT]:</td>
					<td><input type="text" name="PAYMENT" value="${bankRequest.payMent}" size="20"></td>
					<td>币种 ‘人民币’[CURCODE]:</td>
					<td><input type="text" name="CURCODE" value="01" size="20"></td>
				</tr>
				<tr>
					<td>缴费类型[PayType]0主动缴费1自动缴费 :</td>
					<td><input type="text" name=PayType value="${bankRequest.payType}" size="20"></td>
					<td>充值阀值[RechThreshold]:</td>
					<td><input type="text" name="RechThreshold" value="${bankRequest.liMval}" size="20"></td>
				</tr>
				<tr>
					<td>充值额度[RechAmount]:</td>
					<td><input type="text" name="RechAmount" value="${bankRequest.payAmt}" size="20"></td>
					<td>交易账号后4位[ACCNO]:</td>
					<td><input type="text" name="ACCNO" value="0000" size="20"></td>
				</tr>
				<tr>
					<td>Referer信息[REFERER]:</td>
					<td><input type="text" name="REFERER" value="<%=time %>" size="20"></td>
					<td>成功标志Y－成功；N－失败；E－客户放弃支付[SUCCESS]:</td>
					<td><input type="text" name="SUCCESS" value="Y" size="20"></td>
				</tr>
				<tr>
					<td>交易编号-手机号[BEGGINGNO]:</td>
					<td><input type="text" name="BEGGINGNO" value="15002005322" size="20"></td>
					<td>交易类型 0-签约类；1-支付类[TXTYPE]:</td>
					<td><input type="text" name="TXTYPE" value="${bankRequest.bussType }" size="20"></td>
				</tr>
				<tr>
					<td>备注一[REMARK1]:</td>
					<td><input type="text" name="REMARK1" value="${bankRequest.remark1}" size="20"></td>
					<td>备注二[REMARK2]:</td>
					<td><input type="text" name="REMARK2" value="${bankRequest.remark2}" size="20"></td>
				</tr>
				<tr>
					<td>客户端IP[CLIENTIP]:</td>
					<td><input type="text" name="CLIENTIP" value="192.168.214.239" size="20"></td>
					<td>数字签名[SIGN]:</td>
					<td><input type="text" name="SIGN" value="000000000" size="20"></td>
				</tr>
			</table>
			<input type="submit" name="dd" value="支付成功返回请求">
		</form>
	</center>
	<div></div>
</body>
</html>