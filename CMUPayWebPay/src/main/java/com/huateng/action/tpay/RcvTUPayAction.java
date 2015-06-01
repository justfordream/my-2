/**
 * 
 */
package com.huateng.action.tpay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

/**
 * 模拟银行端，接收缴费充值请求
 * 
 * @author zhaojunnan
 * 
 */
public class RcvTUPayAction extends ActionSupport {

	@Value("${TPAY_SHOP_CHECK_STATUS}")
	private String tpayShopCheckStatus;

	@Value("${TPAY.B2C.PAY.URL_TEST}")
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
				.append("<th colspan=\"6\" align=\"center\">银联在线交易测试-银联接收到的缴费充值参数</th>");

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
				.append("<th colspan=\"6\" align=\"center\">银联在线交易测试-银联返回缴费充值参数</th>");

		parameter
				.append("<form id=\"requestPay\" name=\"requestPay\" action=\""
						+ url + "\" method=\"" + (isPost ? "post" : "get")
						+ "\">\n");

		parameter.append("<tr>");
		// 01 版本号 version
		parameter.append("<td>版本号 version</td>");
		setParameter(data, parameter, "version");
		// 02 编码方式 encoding
		parameter.append("<td>编码方式 encoding</td>");
		setParameter(data, parameter, "encoding");
		// 03 证书ID certId
		parameter.append("<td>证书ID certId</td>");
		setParameter(data, parameter, "certId");
		parameter.append("</tr>");
		parameter.append("<tr>");
		// 04 签名 signature
		parameter.append("<td>签名 signature</td>");
		setParameter(data, parameter, "signature");
		// 05 交易类型 txnType
		parameter.append("<td>交易类型 txnType</td>");
		setParameter(data, parameter, "txnType");
		// 06 交易子类 txnSubType
		parameter.append("<td>交易子类 txnSubType</td>");
		setParameter(data, parameter, "txnSubType");
		parameter.append("</tr>");
		parameter.append("<tr>");
		// 07 产品类型 bizType
		parameter.append("<td> 产品类型 bizType</td>");
		setParameter(data, parameter, "bizType");
		// 08 接入类型 accessType
		parameter.append("<td>接入类型 accessType</td>");
		setParameter(data, parameter, "accessType");
		// 09 商户代码 merId
		parameter.append("<td>商户代码 merId</td>");
		setParameter(data, parameter, "merId");
		parameter.append("</tr>");
		parameter.append("<tr>");
		// 10 商户订单号 orderId
		parameter.append("<td>商户订单号 orderId</td>");
		setParameter(data, parameter, "orderId");
		// 11 账号 accNo
		parameter.append("<td>账号 accNo</td>");
		parameter.append("<td><input   name=\"accNo\" value=\""
				+ (getNum() + getNum() + getNum() + getNum()).substring(0, 21)
				+ "\"/></td>\n");
		// 12 订单发送时间 txnTime
		parameter.append("<td>订单发送时间 txnTime</td>");
		setParameter(data, parameter, "txnTime");
		parameter.append("</tr>");
		parameter.append("<tr>");
		// 13 交易金额 txnAmt
		parameter.append("<td>交易金额 txnAmt</td>");
		setParameter(data, parameter, "txnAmt");
		// 14 交易币种 currencyCode
		parameter.append("<td>交易币种 currencyCode</td>");
		parameter.append("<td><input   name=\"currencyCode\" value=\"" + "156"
				+ "\"/></td>\n");
		// 15 请求方保留域 reqReserved
		parameter.append("<td>请求方保留域 reqReserved</td>");
		setParameter(data, parameter, "reqReserved");
		parameter.append("</tr>");
		parameter.append("<tr>");
		// 16 保留域 reserved
		parameter.append("<td>保留域 reserved</td>");
		setParameter(data, parameter, "reserved");
		// 17 银联交易流水号 queryId
		parameter.append("<td>银联交易流水号 queryId</td>");
		parameter.append("<td><input   name=\"queryId\" value=\""
				+ (getNum() + getNum() + getNum() + getNum()).substring(0, 21)
				+ "\"/></td>\n");
		// 18 响应码 respCode
		parameter.append("<td>响应码 respCode</td>");
		parameter.append("<td><input   name=\"respCode\" value=\"" + "00"
				+ "\"/></td>\n");
		parameter.append("</tr>");
		parameter.append("<tr>");
		// 19 响应信息 respMsg
		parameter.append("<td>响应信息 respMsg</td>");
		parameter.append("<td><input   name=\"respMsg\" value=\"" + "成功"
				+ "\"/></td>\n");
		// 20 响应时间 respTime
		parameter.append("<td>响应时间 respTime</td>");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MMdd");
		Date curData = new Date();
		String curData1 = sdf1.format(curData);
		String curData2 = sdf2.format(curData);
		parameter.append("<td><input   name=\"respTime\" value=\"" + curData1
				+ "\"/></td>\n");
		// 21 清算金额 settleAmt
		parameter.append("<td>清算金额 settleAmt</td>");
		setParameter(data, parameter, "settleAmt");
		parameter.append("</tr>");
		parameter.append("<tr>");
		// 22 清算币种 settleCurrencyCode
		parameter.append("<td>清算币种 settleCurrencyCode</td>");
		parameter.append("<td><input   name=\"settleCurrencyCode\" value=\""
				+ "156" + "\"/></td>\n");
		// 23 清算日期 settleDate
		parameter.append("<td> 清算日期 settleDate</td>");
		parameter.append("<td><input   name=\"settleDate\" value=\"" + curData2
				+ "\"/></td>\n");
		// 24 系统跟踪号 traceNo
		parameter.append("<td>系统跟踪号 traceNo</td>");
		parameter.append("<td><input   name=\"traceNo\" value=\""
				+ (getNum() + getNum()).substring(0, 6) + "\"/></td>\n");
		parameter.append("</tr>");
		parameter.append("<tr>");
		// 25 交易传输时间 traceTime
		parameter.append("<td>交易传输时间 traceTime</td>");
		parameter.append("<td><input   name=\"traceTime\" value=\"" + curData1
				+ "\"/></td>\n");
		// 26 兑换日期 exchangeDate
		parameter.append("<td>兑换日期 exchangeDate</td>");
		parameter.append("<td><input   name=\"exchangeDate\" value=\""
				+ curData2 + "\"/></td>\n");
		// 27 汇率 exchangeRate
		parameter.append("<td>汇率 exchangeRate</td>");
		setParameter(data, parameter, "exchangeRate");
		parameter.append("</tr>");
		parameter.append("<tr>");
		// 消息通知地址
		parameter.append("<td>消息通知地址 frontUrl</td>");
		setParameter(data, parameter, "frontUrl");
		// 后台通知地址
		parameter.append("<td>后台通知地址 backUrl</td>");
		setParameter(data, parameter, "backUrl");
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
