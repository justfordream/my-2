package com.huateng.action.ten;

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
 * 
 * @author chenjun
 * @createDate 2014-05-22
 *
 */
public class RcvTenPayAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Value("${TEN.B2C.NOTICE.URL}")
	private String url;
	
	public String receive() throws Exception {

		HttpServletRequest req = SessionRequestUtil.getRequest();
		HttpServletResponse resp = SessionRequestUtil.getResponse();
		resp.setHeader("Cache-Control", "no-cache, must-revalidate");
		resp.setHeader("Pragma", "no-cache");
		resp.setContentType("text/html;charset=utf-8");

		// 获取请求参数
		Map<String, String> data = getAllRequestParam(req);
		String redirectHtml = getBackResult(data,url ,true);
		resp.getWriter().write(redirectHtml.toString());
		return NONE;
	}

	public String getBackResult(Map<String, String> data,String url, boolean isPost) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MMdd");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
		Date curData = new Date();
		String curData1 = sdf1.format(curData);
		String curData2 = sdf2.format(curData);
		String curData3 = sdf3.format(curData);
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
				.append("<th colspan=\"6\" align=\"center\">财付通在线交易测试-财付通接收到的缴费充值参数</th>");

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
				.append("<th colspan=\"6\" align=\"center\">财付通在线交易测试-财付通返回缴费支付参数</th>");

		parameter
				.append("<form id=\"requestPay\" name=\"requestPay\" action=\""+url+"\" method=\""
						+ (isPost ? "post" : "get") + "\">\n");

		parameter.append("<tr>");
		parameter.append("<td>签名方式sign_type</td>");
		setParameter(data, parameter, "sign_type");
		parameter.append("<td>接口版本service_version</td>");
		setParameter(data, parameter, "service_version");
		parameter.append("<td>字符集input_charset</td>");
		setParameter(data, parameter, "input_charset");
		parameter.append("</tr>");
		parameter.append("<tr>");
		parameter.append("<td>签名sign</td>");
		parameter.append("<td><input   name=\"sign\" value=\"\"/></td>\n");
		parameter.append("<td>密钥序号sign_key_index</td>");
		setParameter(data, parameter, "sign_key_index");
		parameter.append("<td>交易模式trade_mode</td>");
		parameter.append("<td><input   name=\"trade_mode\" value=\"" + 1
				+ "\"/></td>\n");
		parameter.append("</tr>");
		parameter.append("<tr>");
		parameter.append("<td>交易状态trade_state</td>");
		parameter.append("<td><input   name=\"trade_state\" value=\"" + 0
				+ "\"/></td>\n");
		parameter.append("<td>支付结果信息pay_info</td>");
		parameter.append("<td><input   name=\"pay_info\" value=\"\"/></td>\n");
		parameter.append("<td>商户号partner</td>");
		setParameter(data, parameter, "partner");
		parameter.append("</tr>");
		parameter.append("<tr>");
		parameter.append("<td>付款银行bank_type</td>");
		parameter.append("<td><input   name=\"bank_type\" value=\"" + 4637
				+ "\"/></td>\n");
		parameter.append("<td>银行订单号bank_billno</td>");
		parameter.append("<td><input   name=\"bank_billno\" value=\""
				+ (getNum() + getNum() + getNum() + getNum()).substring(0, 31)
				+ "\"/></td>\n");
		parameter.append("<td>总金额total_fee</td>");
		setParameter(data, parameter, "total_fee");
		parameter.append("</tr>");
		parameter.append("<tr>");
		parameter.append("<td>币种fee_type</td>");
		setParameter(data, parameter, "fee_type");
		parameter.append("<td>通知ID notify_id</td>");
		parameter
				.append("<td><input   name=\"notify_id\" value=\""
						+ (getNum() + getNum() + getNum() + getNum() + getNum() + getNum())
								.substring(0, 20) + "\"/></td>\n");
		parameter.append("<td>财付通订单号transaction_id</td>");
		parameter.append("<td><input   name=\"transaction_id\" value=\""
				+ data.get("partner") + curData3
				+ (getNum() + getNum() + getNum()).substring(0, 9)
				+ "\"/></td>\n");
		parameter.append("</tr>");
		parameter.append("<tr>");
		parameter.append("<td>商户订单号out_trade_no</td>");
		setParameter(data, parameter, "out_trade_no");
		parameter.append("<td>商家数据包attach</td>");
		setParameter(data, parameter, "attach");
		parameter.append("<td>支付完成时间time_end</td>");
		parameter.append("<td><input   name=\"time_end\" value=\"" + curData1
				+ "\"/></td>\n");
		parameter.append("</tr>");
		parameter.append("<tr>");
		parameter.append("<td>物流费用transport_fee</td>");
		setParameter(data, parameter, "transport_fee");
		parameter.append("<td>物品费用product_fee</td>");
		setParameter(data, parameter, "product_fee");
		parameter.append("<td>折扣价格discount</td>");
		parameter.append("<td><input   name=\"discount\" value=\"\"/></td>\n");

		parameter.append("</tr>");
		parameter.append("<tr>");
		parameter.append("<td>买家别名buyer_alias</td>");
		parameter
				.append("<td><input   name=\"buyer_alias\" value=\"\"/></td>\n");
		parameter.append("<td>返回URL return_url</td>");
		setParameter(data, parameter, "return_url");
		parameter.append("<td>通知URL	notify_url</td>");
		setParameter(data, parameter, "notify_url");
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
