<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>结果通知</title>
</head>
<body>
	<center>
		<div>
		<%

	 String RspCode = request.getParameter("RspCode");
	 String RspInfo = request.getParameter("RspInfo");
	 String OrderID = request.getParameter("OrderID");
	 String OrderTime = request.getParameter("OrderTime");
	 String Payed = request.getParameter("Payed");
	 RspInfo=new String(RspInfo.getBytes("iso8859-1"),"utf-8");
     %>
			<table width="30%">
			    <tr>
			       <td><center><h3>模拟省网厅接收参数</h3></center></td>
			    </tr>
				<tr>
					<td>返回状态码->RspCode:</td>
					<td><%=RspCode%></td>
				</tr>
				<tr>
					<td>返回信息->RspInfo:</td>
					<td><%=RspInfo%></td>
				</tr>
				<tr>
					<td>订单号->OrderID::</td>
					<td><%=OrderID%></td>
				</tr>
				<tr>
					<td>缴费时间->OrderTime:</td>
					<td><%=OrderTime%></td>
				</tr>
				<tr>
					<td>缴费金额->Payed:</td>
					<td><%=Payed%></td>
				</tr>
				</table>
		</div>
	</center>
</body>
</html>