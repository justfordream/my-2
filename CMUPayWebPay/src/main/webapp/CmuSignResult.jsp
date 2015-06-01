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
    
     String SessionID = request.getParameter("SessionID");
	 String SubID = request.getParameter("SubID");
	 String SubTime = request.getParameter("SubTime");
	 String RspCode = request.getParameter("RspCode");
	 String RspInfo = request.getParameter("RspInfo");
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
					<td>订单号->SessionID::</td>
					<td><%=SessionID%></td>
				</tr>
				<tr>
					<td>签约协议号->SubID:</td>
					<td><%=SubID%></td>
				</tr>
				<tr>
					<td>签约时间->SubTime:</td>
					<td><%=SubTime%></td>
				</tr>
				</table>
		</div>
	</center>
</body>
</html>