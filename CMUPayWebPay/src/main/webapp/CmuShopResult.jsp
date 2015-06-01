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
		 String code = request.getParameter("RspCode");
		 String msg = request.getParameter("RspInfo");
		 String OrderID = request.getParameter("OrderID");
		 String merVar = request.getParameter("MerVAR");
		 String Sig = request.getParameter("Sig");
		 msg=new String(msg.getBytes("iso8859-1"),"utf-8");
     %>
			<table width="30%">
			    <tr>
			       <td><center><h3>模拟移动商城接收参数</h3></center></td>
			    </tr>
				<tr>
					<td>订单号->OrderID::</td>
					<td><%=OrderID%></td>
					</tr>
				<tr>
				<tr>
					<td>返回状态码->RspCode:</td>
					<td><%=code%></td>
					</tr>
				<tr>
					<td>返回信息->RspInfo:</td>
					<td><%=msg%></td>
					</tr>
					<td>维护字段->merVar:</td>
					<td><%=merVar%></td>
					</tr>
				<tr>
					<td>签名字段->Sig:</td>
					<td><%=Sig%></td>  
				</tr>
				</table>
		</div>
	</center>
</body>
</html>