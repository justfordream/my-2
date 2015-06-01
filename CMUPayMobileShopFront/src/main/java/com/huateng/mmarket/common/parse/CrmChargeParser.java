//package com.huateng.mmarket.common.parse;
//
//
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Value;
//
//import com.huateng.core.common.CommonConstant;
////import com.huateng.core.common.CoreCommon;
//import com.huateng.core.common.HessianMsgHeader;
//import com.huateng.core.common.Serial;
//import com.huateng.core.parse.CrmMessageParser;
//import com.huateng.core.util.JacksonUtils;
//import com.huateng.core.util.StrUtil;
//import com.huateng.mmarket.bean.crm.CrmChargeReqVo;
//import com.huateng.mmarket.bean.crm.CrmChargeResVo;
//import com.huateng.mmarket.bean.crm.CrmMsgVo;
//import com.huateng.mmarket.bean.head.GPay;
//import com.huateng.mmarket.bean.head.Header;
//import com.huateng.mmarket.bean.tmall.TMallPayReqVo;
//import com.huateng.mmarket.common.ExcConstant;
//import com.huateng.mmarket.common.MsgHandle;
//
///**
// * @author qingxue.li
// * 充值
// */
//public class CrmChargeParser implements CrmMessageParser {
//	/**
//	 *请求协议类型(http or https)
//	 */
//	private @Value("${MARKET_CRM_PROTOCAL}") String protocal;
//	/**
//	 * 请求IP
//	 */
//	private @Value("${MARKET_CRM_IP}") String ip;
//	/**
//	 *请求PORT
//	 */
//	private @Value("${MARKET_CRM_PORT}") String port;
//	/**
//	 * 请求路径
//	 */
//	private @Value("${MARKET_CRM_REQPATH}") String reqPath;
//	/**
//	 *routeInfo
//	 */
//	private @Value("${MARKET_CRM_ROUTEINFO}") String routeInfo;
//	
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@Override
//	public Map assemblyCrmReqMsg(GPay gPay,String txnSeq,String transIDHTime) {
//		Map result = new HashMap();
//		CrmChargeReqVo  crmChargeReqVo = null;
//		TMallPayReqVo payReq = null;
//		CrmMsgVo crmMsgVo = null;
//		HessianMsgHeader hessianHeader = null;
//		String[] crmXmls = new String[2];
//		String tradeReqMessage = "";
//		if(gPay != null){
//			if(gPay.getHeader() != null){
//				 crmMsgVo = new CrmMsgVo();
//				 Header header = gPay.getHeader(); 
//				 /*
//				  * 组装报文头信息
//				  */
//				 //报文版本号
//				 crmMsgVo.setVersion(ExcConstant.CRM_VERSION);
//				 //测试标记
//				 crmMsgVo.setTestFlag("0");
//				 //业务功能码
//				 crmMsgVo.setBIPCode(CommonConstant.Bip.Bis15.getValue());			 
//				 //交易动作代码
//				 crmMsgVo.setActionCode(CommonConstant.ActionCode.Requset.getValue());		
//				 //交易代码
//				 crmMsgVo.setActivityCode(CommonConstant.CrmTrans.Crm07.getValue());
//				 //发起方应用域代码
//				 crmMsgVo.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());			 
//				 //路由类型
//				 crmMsgVo.setRouteType(CommonConstant.RouteType.RouteProvince.getValue());
//				 //归属方应用域代码
//				 crmMsgVo.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
//				 //业务流水号
//				 crmMsgVo.setSessionID(txnSeq);
//				//发起方交易流水号
//				 crmMsgVo.setTransIDO(txnSeq);
//				 //处理时间
//				 crmMsgVo.setTransIDOTime(StrUtil.subString(transIDHTime, 0, 14));	
//				 /*
//				  * 组装报文体信息
//				  */
//				 if(StringUtils.isNotBlank(gPay.getBody())){
//					 String bodyXml = gPay.getBody();
//					 payReq = new TMallPayReqVo(); 
//					 crmChargeReqVo = new CrmChargeReqVo();
//					 MsgHandle.unmarshaller(payReq, "<Body>"+bodyXml+"</Body>");			 				 
//					 //标识类型
//					 if(StringUtils.isNotBlank(payReq.getIDType())){
//						 crmChargeReqVo.setIDType(payReq.getIDType());
//					 }
//					 //用户号码
//					 if(StringUtils.isNotBlank(payReq.getIDValue())){
//						 crmChargeReqVo.setIDValue(payReq.getIDValue());
//						 //路由关键值					 
//						 crmMsgVo.setRouteValue(payReq.getIDValue());					
//					 }
//					 //充值金额
//					 if(StringUtils.isNotBlank(payReq.getPayed())){
//						 crmChargeReqVo.setPayed(payReq.getPayed());
//					 }
//					 //操作流水号
//					 crmChargeReqVo.setTransactionID(Serial.genSerialNos(CommonConstant.Sequence.OprId.getValue()));				 			 
//					 //交易时间			 
//					 crmChargeReqVo.setActionTime(StrUtil.subString(transIDHTime, 0, 14));
//					 //银行编码	
//					 if(StringUtils.isNotBlank(header.getReqSys())){
//						 crmChargeReqVo.setBankID(header.getReqSys());
//					 }
//					 //渠道标志#
//					 if(StringUtils.isNotBlank(header.getReqChannel())){
//						 crmChargeReqVo.setCnlTyp(header.getReqChannel());	
//					 }				 		 
//					 //缴费类型
//					 crmChargeReqVo.setPayedType(CommonConstant.PayType.PayPre.getValue());
//					 //交易账期&&操作请求日期
//					 if(StringUtils.isNotBlank(header.getReqDate())){
//						 crmChargeReqVo.setSettleDate(header.getReqDate());
//						 crmChargeReqVo.setActionDate(header.getReqDate());
//					 }				 
//					 String xmlBody = MsgHandle.marshaller(crmChargeReqVo);
//					 crmXmls[1] = xmlBody;
//				 }	
//				 //组装路由信息	
//				 hessianHeader = new HessianMsgHeader();				 
//				 hessianHeader.setAppCd(CommonConstant.CrmTrans.Crm07.getValue());
//				 hessianHeader.setMqSeq(CommonConstant.Sequence.SendCrmMqSeq.toString());
//				 hessianHeader.setReceiver(CommonConstant.PlatformCd.CrmSys.toString());
//				 hessianHeader.setProtocol(protocal);				
//				 hessianHeader.setReqIp(ip);
//				 hessianHeader.setReqPath(reqPath);
//				 hessianHeader.setReqPort(port);
//				 hessianHeader.setRouteInfo(routeInfo);
//				 
//				 //组装应急追踪报文
//				 tradeReqMessage = this.assemblyTrackReqMessage(gPay, payReq);
//				 
//				 String xmlHead = MsgHandle.marshaller(crmMsgVo);
//				 crmXmls[0] = xmlHead;		 
//				 result.put("headerJson", JacksonUtils.bean2Json(hessianHeader));
//				 result.put("xmlMsg", crmXmls);
//				 result.put("tradeReqMessage", tradeReqMessage); 
//				
//			}else{
//				
//			}			
//		}else{
//			
//		}
//		
//	     return result;
//	}
//
//	
//	public String assemblyTrackReqMessage(GPay gPay,TMallPayReqVo payReq){
//		StringBuilder content = new StringBuilder();
//		
//		if(gPay == null){
//			content.append("");
//		}else{
//			if(gPay.getHeader() == null){
//				content.append("");
//			}else{
//				Header header = gPay.getHeader();				
//				//天猫交易代码
//				content.append("tmall_activity_code:"+header.getActivityCode());
//				content.append("|");
//				//天猫方机构代码
//				content.append("tmall_org_id:"+header.getReqSys());
//				content.append("|");			
//				//天猫请求流水号
//				content.append("tmall_trans_id:"+header.getReqTransID());
//				content.append("|");			
//				//天猫请求的日期
//				content.append("tmall_trans_dt:"+header.getReqDate());
//				content.append("|");	
//				//天猫请求的时间
//				content.append("tmall_trans_tm:"+header.getReqDateTime());
//				content.append("|");		
//			    //天猫渠道标识
//				content.append("tmall_cnl_type:"+header.getReqChannel());
//				content.append("|");
//	   		    //用户号码标识类型		
//				content.append("id_type:"+payReq.getIDType());
//				content.append("|");
//	   		    //用户号码		
//				content.append("id_value:"+payReq.getIDValue());
//				content.append("|");
//	   		    //实际缴费金额		
//				content.append("pay_amt:"+payReq.getPayed());
//				content.append("|");
//			}			
//		}		
//		return content.toString();
//	}
//
//
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	@Override
//	public Map assemblyTMallResMsg(GPay gpay, String crmResXml, String txnSeq,
//			String transIdHTime) {
//		Map result = new HashMap();
//		String resXml = "";
//		CrmMsgVo crmMsgVo = null;
//		CrmChargeResVo  crmChargeResVo = null;
//		GPay gpayRes = new GPay();
//		Header header = null;
//		String tradeReqMessage = "";
//		if(StringUtils.isBlank(crmResXml)){
//			
//		}else{
//			crmMsgVo = new CrmMsgVo();
//			crmChargeResVo = new CrmChargeResVo();
//			//解析报文头
//			MsgHandle.unmarshaller(crmMsgVo, crmResXml);
//			//解析报文体
//			MsgHandle.unmarshaller(crmChargeResVo,(String)crmMsgVo.getBody());			
//			header = gpay.getHeader();
//			header.setRcvDate(StrUtil.subString(transIdHTime, 0, 8));
//			header.setRcvDateTime(transIdHTime);
//			header.setRcvTransID(txnSeq);
//			header.setActionCode(CommonConstant.ActionCode.Respone.getValue());
//			//应答/错误代码
//			//TODO 这个地方需要判断应答码是否充值成功，需明确天猫应答码
//			if(StringUtils.isNotBlank(crmChargeResVo.getRspCode())){
//				header.setRspCode(crmChargeResVo.getRspCode());
//			}
//			//应答/错误描述
//			if(StringUtils.isNotBlank(crmChargeResVo.getRspInfo())){
//				header.setRspDesc(crmChargeResVo.getRspInfo());
//			}					
//			gpayRes.setHeader(header);	
//			resXml = TMallXMLParser.parseGPay(gpayRes);
//		}	
//		tradeReqMessage = this.assemblyTrackResMessage(gpayRes);
//		result.put("resXml", resXml);
//		result.put("tradeResMessage", tradeReqMessage);
//		return result;
//	}
//	
//	public String assemblyTrackResMessage(GPay gpayRes){
//		StringBuilder content = new StringBuilder();
//		if(gpayRes == null){
//			content.append("");
//		}else{
//			if(gpayRes.getHeader() == null){
//				content.append("");
//			}else{
//				Header header = gpayRes.getHeader();
//				//天猫落地流水号
//				content.append("tmall_transh_id:"+header.getRcvTransID());
//				content.append("|");			
//				//天猫落地的日期
//				content.append("tmall_transh_dt:"+header.getRcvDate());
//				content.append("|");			
//				//天猫落地的时间
//				content.append("tmall_transh_tm:"+header.getRcvDateTime());
//				content.append("|");
//	   		    //天猫应答代码		
//				content.append("tmall_rsp_code:"+header.getRspCode());
//				content.append("|");
//				//天猫应答描述
//				content.append("tmall_rsp_desc:"+header.getRspDesc());
//				content.append("|");				
//			}
//		}	
//		return content.toString();
//	}
//
//
//	public String getProtocal() {
//		return protocal;
//	}
//
//
//	public void setProtocal(String protocal) {
//		this.protocal = protocal;
//	}
//
//
//	public String getIp() {
//		return ip;
//	}
//
//
//	public void setIp(String ip) {
//		this.ip = ip;
//	}
//
//
//	public String getPort() {
//		return port;
//	}
//
//
//	public void setPort(String port) {
//		this.port = port;
//	}
//
//
//	public String getReqPath() {
//		return reqPath;
//	}
//
//
//	public void setReqPath(String reqPath) {
//		this.reqPath = reqPath;
//	}
//
//
//	public String getRouteInfo() {
//		return routeInfo;
//	}
//
//
//	public void setRouteInfo(String routeInfo) {
//		this.routeInfo = routeInfo;
//	}
//	
//	
//
//}
