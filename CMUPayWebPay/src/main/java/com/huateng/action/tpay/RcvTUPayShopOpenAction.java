package com.huateng.action.tpay;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;

import com.huateng.utils.SessionRequestUtil;
import com.opensymphony.xwork2.ActionSupport;

public class RcvTUPayShopOpenAction extends ActionSupport {

	@Value("${TPAY.B2C.PAY.SHOP.OPENURL_TEST}")
	private String url;

	public String receive() throws Exception {

		HttpServletRequest req = SessionRequestUtil.getRequest();
		HttpServletResponse resp = SessionRequestUtil.getResponse();
		resp.setHeader("Cache-Control", "no-cache, must-revalidate");
		resp.setHeader("Pragma", "no-cache");
		resp.setContentType("text/html;charset=utf-8");

		// 获取请求参数
		Map<String, String> data = getAllRequestParam(req);
		String redirectHtml = getBackResult(data, url, true);
		resp.getWriter().write(redirectHtml.toString());
		return NONE;
	}

	public String getBackResult(Map<String, String> data, String url,
			boolean isPost) {

		StringBuffer parameter = new StringBuffer("<html>\n");

		parameter.append("<head>\n");
		parameter
				.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		parameter
				.append("<meta http-equiv=\"content-language\" content=\"zh-CN\" />");

		parameter.append("<title>Title for Page</title>\n");

		parameter.append("</head>\n");

		parameter.append("<body>\n");

		List<String> keys = new ArrayList<String>(data.keySet());

		parameter
				.append("<table width=\"1000px\" border=\"1\" align=\"center\">");

		parameter
				.append("<th colspan=\"6\" align=\"center\">银联在线交易测试-银联接收到的开通认证支付参数</th>");

		parameter.append("<tr>");
		for (int i = 0; i < keys.size(); i++) {

			if (i == 0) {
				parameter.append("<tr>");
			} else if (i != 0 && i % 3 == 0) {
				parameter.append("</tr><tr>");
			}
			String name = keys.get(i);
			String value = (String) data.get(name);
			parameter.append("<td>" + name + "</td>");
			parameter.append("<td><input   name=\"" + name + "\" value=\""
					+ value + "\"/></td>\n");

			if (keys.size() == (i - 1)) {
				parameter.append("</tr>");
			}
		}
		parameter.append("<tr>");

		parameter.append("</table>");

		parameter.append("</br>");
		parameter.append("</br>");
		parameter.append("</br>");
		parameter.append("</br>");
		parameter.append("</br>");
		parameter.append("</br>");

		parameter
				.append("<table width=\"1000px\" border=\"1\" align=\"center\">");
		parameter
				.append("<th colspan=\"6\" align=\"center\">银联在线交易测试-银联返回开通认证支付参数</th>");

		parameter
				.append("<form id=\"requestPay\" name=\"requestPay\" action=\""
						+ url + "\" method=\"" + (isPost ? "post" : "get")
						+ "\">\n");

		parameter.append("<tr>");

		// 01 版本号 version
		parameter.append("<td>版本号  version</td>");
		setParameter(data, parameter, "version");

		// 02 证书ID certId
		parameter.append("<td>订单标识 certId</td>");
		parameter.append("<td><input   name=\"certId\" value=\"\"/></td>\n");

		// 03 签名 signature
		parameter.append("<td>签名	signature</td>");
		setParameter(data, parameter, "signature");

		parameter.append("</tr>");
		parameter.append("<tr>");

		// 04 编码方式 encoding
		parameter.append("<td>支付结果 encoding</td>");
		setParameter(data, parameter, "encoding");

		// 05 交易类型 txnType
		parameter.append("<td>交易类型 txnType</td>");
		parameter.append("<td><input   name=\"txnType\" value=\"79\"/></td>\n");

		// 06 交易子类 txnSubType
		parameter.append("<td>交易子类 txnSubType</td>");
		parameter
				.append("<td><input   name=\"txnSubType\" value=\"00\"/></td>\n");

		parameter.append("</tr>");
		parameter.append("<tr>");

		// 07 产品类型 bizType
		parameter.append("<td>产品类型 bizType</td>");
		setParameter(data, parameter, "bizType");

		// 08 接入类型 accessType
		parameter.append("<td>支付卡类型 accessType</td>");
		setParameter(data, parameter, "accessType");

		// 09 商户代码 merId
		parameter.append("<td>商户代码	merId</td>");
		setParameter(data, parameter, "merId");

		parameter.append("</tr>");
		parameter.append("<tr>");

		// 10 帐号 accNo
		parameter.append("<td>帐号	accNo</td>");
		parameter.append("<td><input   name=\"accNo\" value=\""
				+ data.get("accNo") + "\"/></td>\n");

		// 11 请求方保留域 reqReserved
		parameter.append("<td>请求方保留域	reqReserved</td>");
		parameter
				.append("<td><input   name=\"reqReserved\"  readonly=\"readonly\" value=\""
						+ data.get("reqReserved") + "\"/></td>\n");

		// 12 保留域 reserved
		parameter.append("<td>保留域	reserved</td>");
		parameter.append("<td><input   name=\"reserved\" value=\"\"/></td>\n");

		parameter.append("</tr>");
		parameter.append("<tr>");

		// 13 应答码 respCode
		parameter.append("<td>应答码	respCode</td>");
		parameter
				.append("<td><input   name=\"respCode\" value=\"00\"/></td>\n");

		// 14 应答信息 respMsg
		parameter.append("<td>应答信息	respMsg</td>");
		parameter.append("<td><input   name=\"respMsg\" value=\"成功\"/></td>\n");

		// 15 开通状态 activateStatus
		parameter.append("<td>开通状态		activateStatus</td>");
		parameter
				.append("<td><input   name=\"activateStatus\" value=\"1\"/></td>\n");

		parameter.append("</tr>");
		parameter.append("<tr>");

		// 16 支付卡类型 payCardType
		parameter.append("<td>支付卡类型	payCardType</td>");
		parameter
				.append("<td><input   name=\"payCardType\" value=\"\"/></td>\n");

		// 17 银行卡验证信息及身份信息 customerInfo
		parameter.append("<td>支付卡类型 customerInfo</td>");
		setParameter(data, parameter, "customerInfo");

		// 18 小额临时支付信息域 temporaryPayInfo
		parameter.append("<td>小额临时支付信息域 temporaryPayInfo</td>");
		parameter
				.append("<td><input   name=\"temporaryPayInfo\" value=\"\"/></td>\n");

		parameter.append("</tr>");

		parameter
				.append("<tr><td colspan=\"6\" align=\"center\"><center><input type=\"submit\" name=\"submit\" value=\"submit\"></center></td></tr>");

		parameter.append("</form>");

		parameter.append("</table>");

		parameter.append("<script type=\"text/javascript\" >\n");

		// parameter.append("<!--\n");
		//
		// parameter.append("document.requestPay.submit();\n");
		//
		// parameter.append("//-->\n");

		parameter.append("</script>\n");

		parameter.append("</body>\n");

		parameter.append("</html>\n");

		return parameter.toString();
	}

	public String getNum() {
		Random random = new Random();
		boolean isOK = true;
		int num = 0;
		while (isOK) {
			num = random.nextInt();
			if (num > 0) {
				break;
			}
		}
		return String.valueOf(num);
	}

	private void setParameter(Map<String, String> data, StringBuffer parameter,
			String name) {

		String value = data.get(name);
		if (null == value) {
			parameter.append("<td><input  name=\"" + name + "\" /></td>\n");
		} else {
			parameter.append("<td><input   name=\"" + name + "\" value=\""
					+ data.get(name) + "\"/></td>\n");
		}
	}

	public static Map<String, String> getAllRequestParam(
			final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
			}
		}
		return res;
	}

}
