<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%@ page import="java.text.SimpleDateFormat,java.util.Calendar,com.huateng.utils.UUIDGenerator" %>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>网厅支付</title>
  <%
    SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	Calendar calendar = Calendar.getInstance();
	String time= myFormat.format(calendar.getTime());
  %>
</head>
<body>
	<center>		
		<form action="<%=request.getContextPath()%>/rcvCmuPay.action"
			method="post">
			<table>
			    <tr>
			        <td colspan="2"><center><h3>模拟省网厅发起支付</h3></center></td>
			    </tr>
				<tr>
					<td>商户标识[MerID]:</td>
					<td align="left"><select name="MerID" id="MerID">
							<option value="3111">河北(3111)</option>
							<option value="1001">北京(1001)</option>
							<option value="2101">上海(2101)</option>
							<option value="2501">江苏(2501)</option>
							<option value="5711">浙江(5711)</option>
							<option value="5911">福建(5911)</option>
							<option value="4711">内蒙古(4711)</option>
							<option value="2201">天津(2201)</option>
							<option value="5311">山东(5311)</option>
							<option value="3511">山西(3511)</option>
							<option value="5511">安徽(5511)</option>
							<option value="8981">海南(8981)</option>

							<option value="2001">广东(2001)</option>
							<option value="7711">广西(7711)</option>
							<option value="9711">青海(9711)</option>
							<option value="2701">湖北(2701)</option>
							<option value="7311">湖南(7311)</option>

							<option value="7911">江西(7911)</option>
							<option value="3711">河南(3711)</option>
							<option value="8911">西藏(8911)</option>
							<option value="2801">四川(2801)</option>
							<option value="2301">重庆(2301)</option>

							<option value="2901">陕西(2901)</option>
							<option value="8511">贵州(8511)</option>
							<option value="8711">云南(8711)</option>
							<option value="9311">甘肃(9311)</option>
							<option value="9511">宁夏(9511)</option>

							<option value="9911">新疆(9911)</option>
							<option value="4311">吉林(4311)</option>
							<option value="2401">辽宁(2401)</option>
							<option value="4511">黑龙江(4511)</option>
					</select></td>
				</tr>
				<tr>
					<td>订单标识[OrderID]:</td>
					<td><input type="text" name="OrderID" value="101010<%=time %>" size="80"></td>
				</tr>
				<tr>
					<td>交易日期时间[OrderTime]:</td>
					<td><input type="text" name="OrderTime" value="<%=time %>" size="80"></td>
				</tr>
				<tr>
					<td>订单金额，单位为“分”[Payed]:</td>
					<td><input type="text" name="Payed" value="10" size="80"></td>
				</tr>
				<tr>
					<td>币种，默认人民币[CurType]:</td>
					<td><input type="text" name="CurType" value="01" size="80"></td>
				</tr>
				<tr>
					<td>为该用户充值[IDValue]:</td>
					<td><input type="text" name="IDValue" value="" size="80"></td>
				</tr>
				<tr>
					<td>用户的标识的类型[IDType]:</td>
					<td><input type="text" name="IDType" value="01" size="80"></td>
				</tr>
				<tr>
					<td>统一支付系统向此URL发送支付结果通知[MerURL]:</td>
					<td align="left">
					<input type="text" name="MerURL" readonly="readonly" id="MerURL" value="http://127.0.0.1:8080/CMUPayWebPay/redirectResultCmu.action" size="80">
					</td>
				</tr>
				<tr>
					<td>统一支付系统通知支付结果时，携带此变量[MerVAR]:</td>
					<td><input type="text" name="MerVAR" value="<%=time %>" size="80"></td>
				</tr>
				<tr>
					<td>用户界面返回URL[BackURL]:</td>
					<td align="left">
					   <input type="text" name="BackURL" readonly="readonly" id="BackURL" value="http://127.0.0.1:8080/CMUPayWebPay/CmuResult.jsp" size="80">
					</td>
				</tr>
				<tr>
					<td>语言，默认为中文版[Lang]:</td>
					<td><input type="text" name="Lang" value="china" size="80"></td>
				</tr>
				<tr>
					<td>省公司在银行侧的签名数据[Sig]:</td>
					<td><input type="text" name="Sig" value="<%=time %>" size="80"></td>
				</tr>
				<tr>
					<td>银行编码[BankID]:</td>
					<td align="left"><select name="BankID" id="BankID">
					        <option value="0009">0009-模拟银行
							<option value="0004">0004-建设银行
							<option value="0005">0005-浦发银行
					</select></td>
				</tr>
				<tr>
					<td>客户端IP[CLIENTIP]:</td>
					<td><input type="text" name="CLIENTIP" value="192.168.214.239"
						size="80"></td>
				</tr>
				<tr>
					<td>UPAY10001 支付请求时填写[MCODE]:</td>
					<td><input type="text" name="MCODE" value="UPAY10001"
						size="80"></td>
				</tr>
			</table>
			<input type="submit" name="dd" value="提交支付请求">
		</form>
	</center>
	<div><font color="red">注：订单号必须20位，手机号码必须是正确省份的手机号码</font></div>
</body>
</html>