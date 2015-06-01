package com.huateng.action.cmu;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.bean.CmuData;
import com.huateng.core.common.RemoteMsg;
import com.huateng.core.exception.AppException;
import com.huateng.core.util.IpUtil;
import com.huateng.core.util.RequestUtil;
import com.huateng.filter.HttpStringFilter;
import com.huateng.log.MessageLogger;
import com.huateng.service.RcvSignService;
import com.huateng.utils.SessionRequestUtil;
import com.huateng.vo.MsgData;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 接收省级"签约"请求信息
 * 
 * @author Gary
 * 
 */
public class RcvSignCmuAction extends ActionSupport {

	private static final long serialVersionUID = 8728548024493448013L;

	private Logger logger = LoggerFactory.getLogger(getClass());
	private MessageLogger upayLog=MessageLogger.getLogger(RcvSignCmuAction.class);
	@Autowired
	private RcvSignService rcvSignService;
	@Value("${SPECIAL_FILETER_LIST}")
	private String fileterStr;
	/**
	 * 接受省中心签约请求信息
	 */
	public String recieve() {
		PrintWriter writer = null;
		try {
			HttpServletRequest request = SessionRequestUtil.getRequest();
			HttpServletResponse response = SessionRequestUtil.getResponse();
			this.settingResponse(response);
			CmuData cmuData = new CmuData();
			/**
			 * 过滤特殊字符串
			 */
			Map<String, String> requestMaps = HttpStringFilter.filterRequestParams(request,fileterStr);
			
			//请求来源信息
			RemoteMsg remoteMsg=IpUtil.getRemoteMsg(request);
			
			logger.info("请求来源IP[{}]，请求来源URL[{}]",remoteMsg.getIp(),remoteMsg.getRequestURL());
			upayLog.info("请求来源IP[{}]，请求来源URL[{}]",remoteMsg.getIp(),remoteMsg.getRequestURL());
			
			/**
			 * 获取参数
			 */
			this.assemlyCmuData(requestMaps, cmuData);
            
			MsgData msgData = rcvSignService.sendSignRequest(cmuData);

			logger.info("发送签约请求[{}]", msgData.getRedirectHtml());
			upayLog.info("发送签约请求[{}]", msgData.getRedirectHtml());

			writer = SessionRequestUtil.getResponse().getWriter();

			writer.write(msgData.getRedirectHtml());

			writer.flush();
		} catch (AppException e) {
			logger.error("支付网关签约异常:",e);
			upayLog.error("支付网关签约异常:",e);
		} catch (Exception e) {
			logger.error("支付网关签约异常:",e);
			upayLog.error("支付网关签约异常:",e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}

		return NONE;
	}

	/**
	 * 设置响应公共信息
	 * 
	 * @param response
	 */
	public void settingResponse(HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/html;charset=utf-8");
	}

	/**
	 * 转换请求信息为签约对象
	 * 
	 * @param request
	 *            Requst 请求对象
	 * @param cmuData
	 *            签约对象
	 */
	private void assemlyCmuData(Map<String, String> requestMaps, CmuData cmuData) {
		/* 获取请求信息 缺少省中心Id标识 */
		String SessionID = requestMaps.get("SessionID");
		String IDType = requestMaps.get("IDType");
		String IDValue = requestMaps.get("IDValue");
		String Lang = requestMaps.get("Lang");
		String BankID = requestMaps.get("BankID");
		String SubTime = requestMaps.get("SubTime");
		String TransactionID = requestMaps.get("TransactionID");
		String OrigDomain = requestMaps.get("OrigDomain");
		String CLIENTIP = requestMaps.get("CLIENTIP");
		String MCODE = requestMaps.get("MCODE");
		String payType = requestMaps.get("PayType");
		String rechThreshold = requestMaps.get("RechThreshold");
		String rechAmount = requestMaps.get("RechAmount");
		String ServerURL = RequestUtil.paseDecode(requestMaps.get("ServerURL"));
		String BackURL = RequestUtil.paseDecode(requestMaps.get("BackURL"));
		String Sig = RequestUtil.paseDecode(requestMaps.get("Sig"));

		logger.info("......接收[({})]网厅签约请求,请求参数......[SessionID:{}],[IDType:{}],"
						+ "[IDValue:{}],[ServerURL:{}],[BackURL:{}],[Lang:{}],[Sig:{}],[BankID:{}],[SubTime:{}],"
						+ "[TransactionID:{}],[OrigDomain:{}],[CLIENTIP:{}],[MCODE:{}],[PayType:{}],"
						+ "[RechThreshold:{}],[RechAmount:{}]", new Object[] {
						OrigDomain, SessionID, IDType, IDValue, ServerURL,
						BackURL, Lang, Sig, BankID, SubTime, TransactionID,
						OrigDomain, CLIENTIP, MCODE, payType, rechThreshold,
						rechAmount });
		upayLog.debug("接收[({})]网厅签约请求,请求参数[SessionID:{}],[IDType:{}],"
				+ "[IDValue:{}],[ServerURL:{}],[BackURL:{}],[Lang:{}],[Sig:{}],[BankID:{}],[SubTime:{}],"
				+ "[TransactionID:{}],[OrigDomain:{}],[CLIENTIP:{}],[MCODE:{}],[PayType:{}],"
				+ "[RechThreshold:{}],[RechAmount:{}]", new Object[] {
				OrigDomain, SessionID, IDType, IDValue, ServerURL,
				BackURL, Lang, Sig, BankID, SubTime, TransactionID,
				OrigDomain, CLIENTIP, MCODE, payType, rechThreshold,rechAmount });
		/* 组装signReq */
		cmuData.setSessionID(SessionID);
		cmuData.setIDType(IDType);
		cmuData.setIDValue(IDValue);
		cmuData.setServerURL(ServerURL);
		cmuData.setBackURL(BackURL);
		cmuData.setLang(Lang);
		cmuData.setSig(Sig);
		cmuData.setBankID(BankID);
		cmuData.setSubTime(SubTime);
		cmuData.setTransactionID(TransactionID);
		cmuData.setOrigDomain(OrigDomain);
		cmuData.setCLIENTIP(CLIENTIP);
		cmuData.setMCODE(MCODE);
		cmuData.setCmuID(OrigDomain);
		cmuData.setPayType(payType);
		cmuData.setRechThreshold(rechThreshold);
		cmuData.setRechAmount(rechAmount);
	}
}
