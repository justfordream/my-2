package com.huateng.action.alipay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import com.huateng.utils.SessionRequestUtil;
import com.opensymphony.xwork2.ActionSupport;

public class RcvAliPayAction extends ActionSupport{

	private static final long serialVersionUID = 1L;
	@Value("${Ali.B2C.NOTICE.URL}")
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
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		SimpleDateFormat sdf2 = new SimpleDateFormat("MMdd");
//		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
		Date curData = new Date();
		String curData1 = sdf1.format(curData);
//		String curData2 = sdf2.format(curData);
//		String curData3 = sdf3.format(curData);
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
				.append("<table width=\"1000px\" border=\"0\" align=\"center\">");

		parameter
				.append("<th colspan=\"6\" align=\"center\">支付宝在线交易测试-支付宝接收到的缴费充值参数</th>");

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
				.append("<table width=\"1000px\" border=\"0\" align=\"center\">");
//		parameter
//				.append("<tr colspan=\"6\" align=\"center\">支付宝在线交易测试-支付宝返回缴费支付参数</tr>");

		parameter
				.append("<form id=\"requestPay\" name=\"requestPay\" action=\""
						+ url + "\" method=\"" + (isPost ? "post" : "get")
						+ "\">\n");
		parameter
		.append("<th colspan=\"6\" align=\"center\">支付宝即时交易测试-支付宝返回页面跳转同步通知参数</th>");
		
		parameter.append("<tr>");
		parameter.append("<td>成功标识is_success</td>");
		parameter.append("<td><input   name=\"is_success\" value=\"T\"/></td>\n");
		parameter.append("<td>签名方式sign_type</td>");
		parameter.append("<td><input name=\"sign_type\" value=\""+(data.get("sign_type") == null?"":data.get("sign_type"))+"\"/></td>");
		parameter.append("<td>签名 sign</td>");
		parameter.append("<td><input   name=\"sign\" value=\"\"/></td>\n");
		parameter.append("</tr>");
		parameter.append("<tr>");
		parameter.append("<td>商户网站唯一订单号out_trade_no</td>");
		parameter.append("<td><input name=\"out_trade_no\" value=\""+(data.get("out_trade_no") == null?"":data.get("out_trade_no"))+"\"/></td>");
		parameter.append("<td>商品名称subject</td>");
		parameter.append("<td><input name=\"subject\" value=\""+(data.get("subject") == null?"":data.get("subject"))+"\"/></td>");
		parameter.append("<td>支付类型payment_type</td>");
		parameter.append("<td><input name=\"payment_type\" value=\""+(data.get("payment_type") == null?"":data.get("payment_type"))+"\"/></td>");
		parameter.append("</tr>");
		parameter.append("<tr>");
		parameter.append("<td>接口名称exterface</td>");
		parameter.append("<td><input   name=\"exterface\" value=\"create_direct_pay_by_user\"/></td>\n");
		parameter.append("<td>支付宝交易号trade_no</td>");
		parameter.append("<td><input   name=\"trade_no\" value=\"\"/></td>\n");
		parameter.append("<td>交易状态trade_status</td>");
		parameter.append("<td><input   name=\"trade_status\" value=\"\"/></td>\n");
		parameter.append("</tr>");
		parameter.append("<tr>");
		parameter.append("<td>通知校验ID notify_id</td>");
		parameter.append("<td><input   name=\"notify_id\" value=\"\"/></td>\n");
		parameter.append("<td>通知时间notify_time</td>");
		parameter.append("<td><input   name=\"notify_time\" value=\"\"/></td>\n");
		parameter.append("<td>通知类型notify_type</td>");
		parameter.append("<td><input   name=\"notify_type\" value=\"\"/></td>\n");
		parameter.append("</tr>");
		parameter.append("<tr>");
		parameter.append("<td>卖家支付宝账号seller_email</td>");
		parameter.append("<td><input name=\"seller_email\" value=\""+(data.get("seller_email") == null?"":data.get("seller_email"))+"\"/></td>");
		parameter.append("<td>买家支付宝账号buyer_email</td>");
		parameter.append("<td><input name=\"buyer_email\" value=\""+(data.get("buyer_email") == null?"":data.get("buyer_email"))+"\"/></td>");
		parameter.append("<td>卖家支付宝账户号seller_id</td>");
		parameter.append("<td><input name=\"seller_id\" value=\""+(data.get("seller_id") == null?"":data.get("seller_id"))+"\"/></td>");
		parameter.append("</tr>");
		parameter.append("<tr>");
		parameter.append("<td>买家支付宝账户号buyer_id</td>");
		parameter.append("<td><input name=\"buyer_id\" value=\""+(data.get("buyer_id") == null?"":data.get("buyer_id"))+"\"/></td>");
		parameter.append("<td>交易金额total_fee</td>");
		parameter.append("<td><input name=\"total_fee\" value=\""+(data.get("total_fee") == null?"":data.get("total_fee"))+"\"/></td>");
		parameter.append("<td>商品描述body</td>");
		parameter.append("<td><input name=\"body\" value=\""+(data.get("body") == null?"":data.get("body"))+"\"/></td>");
		parameter.append("</tr>");
		parameter.append("<tr>");
		parameter.append("<td>公用回传参数extra_common_param</td>");
		parameter.append("<td><input name=\"extra_common_param\" value=\""+(data.get("extra_common_param") == null?"":data.get("extra_common_param"))+"\"/></td>");
		parameter.append("<td>信用支付购票员的代理人ID agent_user_id</td>");
		parameter.append("<td><input   name=\"agent_user_id\" value=\"\"/></td>\n");
		parameter.append("</tr>");
		
		parameter
		.append("<th colspan=\"6\" align=\"center\">支付宝即时交易测试-支付宝返回服务器异步通知参数</th>");
		
		parameter.append("<tr>");
		parameter.append("<td>通知时间notify_time</td>");
		parameter.append("<td><input   name=\"ht_notify_time\" value=\""+curData1+"\"/></td>");
		parameter.append("<td>通知类型notify_type</td>");
		parameter.append("<td><input   name=\"ht_notify_type\" value=\"trade_status_sync\"/></td>");
		parameter.append("<td>通知校验IDnotify_id</td>");
		parameter.append("<td><input   name=\"ht_notify_id\" value=\""+getUUID()+"\"/></td>");
		parameter.append("</tr>");
		
		parameter.append("<tr>");
		parameter.append("<td>签名方式sign_type</td>");
		parameter.append("<td><input   name=\"ht_sign_type\" value=\"MD5\"/></td>\n");
		parameter.append("<td>签名 sign</td>");
		parameter.append("<td><input   name=\"ht_sign\" value=\"\"/></td>\n");
		parameter.append("<td>商户网站唯一订单号out_trade_no</td>");
		parameter.append("<td><input   name=\"ht_out_trade_no\" value=\""+(data.get("out_trade_no") == null?"":data.get("out_trade_no"))+"\"/></td>");
		parameter.append("</tr>");
		
		parameter.append("<tr>");
		parameter.append("<td>商品名称subject</td>");
		parameter.append("<td><input   name=\"ht_subject\" value=\""+(data.get("subject") == null?"":data.get("subject"))+"\"/></td>");
		parameter.append("<td>支付类型payment_type</td>");
		parameter.append("<td><input   name=\"ht_payment_type\" value=\""+(data.get("payment_type") == null?"":data.get("payment_type"))+"\"/></td>");
		parameter.append("<td>支付宝交易号trade_no</td>");
		parameter.append("<td><input   name=\"ht_trade_no\" value=\""+(getNum() + getNum() + getNum() + getNum() + getNum() + getNum())
				.substring(0, 17)+"\"/></td>\n");
		parameter.append("</tr>");
		
		parameter.append("<tr>");
		parameter.append("<td>交易状态trade_status</td>");
		parameter.append("<td><input   name=\"ht_trade_status\" value=\"TRADE_FINISHED\"/></td>\n");
		parameter.append("<td>交易创建时间gmt_create</td>");
		parameter.append("<td><input   name=\"ht_gmt_create\" value=\""+curData1+"\"/></td>\n");
		parameter.append("<td>交易付款时间gmt_payment</td>");
		parameter.append("<td><input   name=\"ht_gmt_payment\" value=\""+curData1+"\"/></td>\n");
		parameter.append("</tr>");
		
		parameter.append("<tr>");
		parameter.append("<td>交易关闭时间gmt_close</td>");
		parameter.append("<td><input   name=\"ht_gmt_close\" value=\"\"/></td>\n");
		parameter.append("<td>退款状态refund_status</td>");
		parameter.append("<td><input   name=\"ht_refund_status\" value=\"\"/></td>\n");
		parameter.append("<td>退款时间gmt_refund</td>");
		parameter.append("<td><input   name=\"ht_gmt_refund\" value=\"\"/></td>\n");
		parameter.append("</tr>");
		
		parameter.append("<tr>");
		parameter.append("<td>卖家支付宝账号seller_email</td>");
		parameter.append("<td><input   name=\"ht_seller_email\" value=\""+(data.get("seller_email") == null?"":data.get("seller_email"))+"\"/></td>");
		parameter.append("<td>买家支付宝账号buyer_email</td>");
		parameter.append("<td><input   name=\"ht_buyer_email\" value=\""+(data.get("buyer_email") == null?"":data.get("buyer_email"))+"\"/></td>");
		parameter.append("<td>卖家支付宝账户号seller_id</td>");
		parameter.append("<td><input   name=\"ht_seller_id\" value=\""+(data.get("seller_id") == null?"":data.get("seller_id"))+"\"/></td>");
		parameter.append("</tr>");
		
		parameter.append("<tr>");
		parameter.append("<td>买家支付宝账户号buyer_id</td>");
		parameter.append("<td><input   name=\"ht_buyer_id\" value=\""+(data.get("buyer_id") == null?"":data.get("buyer_id"))+"\"/></td>");
		parameter.append("<td>商品单价price</td>");
		parameter.append("<td><input   name=\"ht_price\" value=\""+(data.get("price") == null?"":data.get("price"))+"\"/></td>");
		parameter.append("<td>交易金额total_fee</td>");
		parameter.append("<td><input   name=\"ht_total_fee\" value=\""+(data.get("total_fee") == null?"":data.get("total_fee"))+"\"/></td>");
		parameter.append("</tr>");
		
		parameter.append("<tr>");
		parameter.append("<td>购买数量quantity</td>");
		parameter.append("<td><input   name=\"ht_quantity\" value=\""+(data.get("quantity") == null?"":data.get("quantity"))+"\"/></td>");
		parameter.append("<td>商品描述body</td>");
		parameter.append("<td><input   name=\"ht_body\" value=\""+(data.get("body") == null?"":data.get("body"))+"\"/></td>");
		parameter.append("<td>折扣discount</td>");
		parameter.append("<td><input   name=\"ht_discount\" value=\"\"/></td>\n");
		parameter.append("</tr>");
		
		parameter.append("<tr>");
		parameter.append("<td>是否调整总价is_total_fee_adjust</td>");
		parameter.append("<td><input   name=\"ht_is_total_fee_adjust\" value=\"\"/></td>\n");
		parameter.append("<td>是否使用红包买家use_coupon</td>");
		parameter.append("<td><input   name=\"ht_use_coupon\" value=\"\"/></td>\n");
		parameter.append("<td>公用回传参数extra_common_param</td>");
		parameter.append("<td><input   name=\"ht_extra_common_param\" value=\""+(data.get("extra_common_param") == null?"":data.get("extra_common_param"))+"\"/></td>");
		parameter.append("</tr>");
		
		parameter.append("<tr>");
		parameter.append("<td>支付渠道组合信息out_channel_type</td>");
		parameter.append("<td><input   name=\"ht_out_channel_type\" value=\"\"/></td>\n");
		parameter.append("<td>支付金额组合信息out_channel_amount</td>");
		parameter.append("<td><input   name=\"ht_out_channel_amount\" value=\"\"/></td>\n");
		parameter.append("<td>实际支付渠道out_channel_inst</td>");
		parameter.append("<td><input   name=\"ht_out_channel_inst\" value=\"\"/></td>\n");
		parameter.append("</tr>");
		
		parameter.append("<tr>");
		parameter.append("<td>返回URL return_url</td>");
		parameter.append("<td><input   name=\"return_url\" value=\""+(data.get("return_url") == null?"":data.get("return_url"))+"\"/></td>");
		parameter.append("<td>通知URL	notify_url</td>");
		parameter.append("<td><input   name=\"notify_url\" value=\""+(data.get("notify_url") == null?"":data.get("notify_url"))+"\"/></td>");
		parameter.append("<td></td><td></td>");
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


	private String getUUID()
	{
		UUID uuid = UUID.randomUUID();
		String str = uuid.toString();
		// 去掉"-"符号
		String temp = str.replaceAll("-", "");
		return temp;
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
