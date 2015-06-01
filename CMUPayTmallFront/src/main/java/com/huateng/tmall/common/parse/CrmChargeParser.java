package com.huateng.tmall.common.parse;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


import com.huateng.core.common.CommonConstant;
import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.HessianMsgHeader;
import com.huateng.core.common.Serial;
import com.huateng.core.common.UpayCommon;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.parse.CrmMessageParser;
import com.huateng.core.util.DateUtil;
import com.huateng.core.util.JacksonUtils;
import com.huateng.core.util.StrUtil;
import com.huateng.log.MessageLogger;
import com.huateng.tmall.bean.UpayCsysOrgCode;
import com.huateng.tmall.bean.UpayCsysOrgMapTransCode;
import com.huateng.tmall.bean.crm.CrmChargeReqVo;
import com.huateng.tmall.bean.crm.CrmChargeResVo;
import com.huateng.tmall.bean.crm.CrmMsgVo;
import com.huateng.tmall.bean.crm.TmallConsumeReqVo;
import com.huateng.tmall.bean.crm.TmallConsumeResVo;
import com.huateng.tmall.bean.head.GPay;
import com.huateng.tmall.bean.head.Header;
import com.huateng.tmall.bean.mapper.UpayCsysImsiLdCdMapper;
import com.huateng.tmall.bean.mapper.UpayCsysOrgCodeMapper;
import com.huateng.tmall.bean.mapper.UpayCsysOrgMapTransCodeMapper;
import com.huateng.tmall.bean.tmall.TMallPayReqVo;
import com.huateng.tmall.common.ExcConstant;
import com.huateng.tmall.common.MsgHandle;


/**
 * @author qingxue.li
 * 充值
 */
public class CrmChargeParser implements CrmMessageParser {
	/**
	 *请求协议类型(http or https)
	 */
	private @Value("${TMALL_CRM_PROTOCAL}") String protocal;
	/**
	 * 请求IP
	 */
	private @Value("${TMALL_CRM_IP}") String ip;
	/**
	 *请求PORT
	 */
	private @Value("${TMALL_CRM_PORT}") String port;
	/**
	 * 请求路径
	 */
	private @Value("${TMALL_CRM_REQPATH}") String reqPath;
	/**
	 *routeInfo
	 */
	/*private @Value("${TMALL_CRM_ROUTEINFO}") String routeInfo;	*/
	/**
	 * TestFlag
	 */
	private @Value("${TEST_FLAG}") String testFlag;
	
	@Autowired
	private UpayCsysImsiLdCdMapper upayCsysImsiLdCdMapper;
	
	@Autowired
	private UpayCsysOrgCodeMapper upayCsysOrgCodeMapper;
	
	@Autowired
	private UpayCsysOrgMapTransCodeMapper upayCsysOrgMapTransCodeMapper;
	
	private Logger logger =  LoggerFactory.getLogger("CrmChargeParser");
	private MessageLogger log = MessageLogger.getLogger(getClass());
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map assemblyCrmReqMsg(GPay gPay,String txnSeq,String transIDHTime) throws ServiceException {
		Map result = new HashMap();
		CrmChargeReqVo  crmChargeReqVo = null;
		TMallPayReqVo payReq = null;
		CrmMsgVo crmMsgVo = null;
		HessianMsgHeader hessianHeader = null;
		String[] crmXmls = new String[2];
		String tradeReqMessage = "";
		if(gPay != null){
			if(gPay.getHeader() != null){
				 crmMsgVo = new CrmMsgVo();
				 Header header = gPay.getHeader(); 
				 /*
				  * 组装报文头信息
				  */
				 //报文版本号
				 crmMsgVo.setVersion(ExcConstant.CRM_VERSION);
				 //测试标记
				 crmMsgVo.setTestFlag(testFlag);
				 //业务功能码
				 crmMsgVo.setBIPCode(CommonConstant.Bip.Biz22.getValue());			 
				 //交易动作代码
				 crmMsgVo.setActionCode(CommonConstant.ActionCode.Requset.getValue());		
				 //交易代码
				 crmMsgVo.setActivityCode(CommonConstant.CrmTrans.Crm17.getValue());
				 //发起方应用域代码
				 crmMsgVo.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());			 
				 //路由类型
				 crmMsgVo.setRouteType(CommonConstant.RouteType.RoutePhone.getValue());
				 //归属方应用域代码
				 crmMsgVo.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
				 //业务流水号
				 crmMsgVo.setSessionID(txnSeq);
				//发起方交易流水号
				 crmMsgVo.setTransIDO(txnSeq);
				 //处理时间
				 crmMsgVo.setTransIDOTime(StrUtil.subString(transIDHTime, 0, 14));	
				 				 
				 /*
				  * 组装报文体信息
				  */
				 if(StringUtils.isNotBlank(gPay.getBody())){
					 String bodyXml = gPay.getBody();
					 payReq = new TMallPayReqVo(); 
					 crmChargeReqVo = new CrmChargeReqVo();
					 MsgHandle.unmarshaller(payReq, "<Body>"+bodyXml+"</Body>");			 				 
					 //标识类型
					 if(StringUtils.isNotBlank(payReq.getIDType())){
						 crmChargeReqVo.setIDType(payReq.getIDType());
					 }
					 //用户号码
					 if(StringUtils.isNotBlank(payReq.getIDValue())){
						 crmChargeReqVo.setIDValue(payReq.getIDValue());
						 //路由关键值					 
						 crmMsgVo.setRouteValue(payReq.getIDValue());					
					 }
					 //充值金额
					 if(StringUtils.isNotBlank(payReq.getPayed())){
						 crmChargeReqVo.setPayed(payReq.getPayed());
					 }
					 //操作流水号
					 crmChargeReqVo.setTransactionID(Serial.genSerialNos(CommonConstant.Sequence.OprId.getValue()));	
					 //crmChargeReqVo.setTransactionID(Serial.genSerialNoss(CommonConstant.Sequence.OprId.getValue()));  
					 //交易时间			 
					 crmChargeReqVo.setActionTime(StrUtil.subString(transIDHTime, 0, 14));
					 //银行编码	
					 if(StringUtils.isNotBlank(header.getReqSys())){
						 crmChargeReqVo.setBankID(header.getReqSys());
					 }
					 //渠道标志#
					 if(StringUtils.isNotBlank(header.getReqChannel())){
						 crmChargeReqVo.setCnlTyp(header.getReqChannel());	
					 }				 		 
					 //缴费类型
					 crmChargeReqVo.setPayedType(CommonConstant.PayType.PayPre.getValue());
					 //交易账期  与 操作请求日期
					 if(StringUtils.isNotBlank(header.getReqDate())){
						 crmChargeReqVo.setSettleDate(header.getReqDate());
						 crmChargeReqVo.setActionDate(header.getReqDate());
					 }	
					 crmChargeReqVo.setBusiTransID(header.getReqTransID());
					 crmChargeReqVo.setPayTransID(payReq.getPayTransID());
					 crmChargeReqVo.setChargeMoney(payReq.getChargeMoney()+"");
					 crmChargeReqVo.setOrganID(header.getReqSys());
					 crmChargeReqVo.setOrderNo(payReq.getOrderID());
					 crmChargeReqVo.setProductNo(payReq.getProdId());
					 crmChargeReqVo.setPayment(payReq.getPayment()+"");
					 crmChargeReqVo.setOrderCnt(payReq.getProdCnt()+"");
					 crmChargeReqVo.setCommision(payReq.getCommision()+"");
					 crmChargeReqVo.setRebateFee(payReq.getRebateFee()+"");
					 crmChargeReqVo.setProdDiscount(payReq.getProdDiscount()+"");
					 crmChargeReqVo.setCreditCardFee(payReq.getCreditCardFee()+"");
					 crmChargeReqVo.setServiceFee(payReq.getServiceFee()+"");
					 crmChargeReqVo.setActivityNo(payReq.getActivityNO());
					 crmChargeReqVo.setProductShelfNo(payReq.getProdShelfNO());
					 crmChargeReqVo.setReserve1(payReq.getReserve1());
					 crmChargeReqVo.setReserve2(payReq.getReserve2());
					 crmChargeReqVo.setReserve3(payReq.getReserve3());
					 crmChargeReqVo.setReserve4(payReq.getReserve4());
					 String xmlBody = MsgHandle.marshaller(crmChargeReqVo);
					 crmXmls[1] = xmlBody;
				 }	
				 
				 
				 crmMsgVo.setMsgSender(CommonConstant.BankOrgCode.CMCC.getValue());
				 String msgReceiver = null;
				String num =  StringUtils.substring(crmChargeReqVo.getIDValue(), 0, 7);
				 int type = 0; // 号码类型：1为移动号码，2为联通电信，0为未知号码
				 //if(null != payReq.getHomeProv() && (payReq.getHomeProv().equals("220") || payReq.getHomeProv().equals("898"))){
					 msgReceiver = upayCsysImsiLdCdMapper.selectProvinceByMobileNumber(num);
					 if(msgReceiver == null){
						 msgReceiver = upayCsysImsiLdCdMapper.selectProvinceByUnicomNumber(num);
						 if(msgReceiver != null) {
							 type = 2;
							 logger.info("省代码:{} 号码段:{} 号码类型：{}",new Object[]{msgReceiver,num,"联通电信号码"});
						 }
					 } else {
						 type = 1;
						 logger.info("省代码:{} 号码段:{} 号码类型：{}",new Object[]{msgReceiver,num,"移动号码"});
					 }
					 if(type == 0){
						 logger.info("省代码:{} 号码段:{} 号码类型：{}",new Object[]{msgReceiver,num,"未知省份号码"});
					 }
				/* }else{
					  msgReceiver = upayCsysImsiLdCdMapper.selectProvinceByMobileNumber(StringUtils.substring(crmChargeReqVo.getIDValue(), 0, 7));
				 }*/
				 
				
				 crmMsgVo.setMsgReceiver(msgReceiver);				 
				//-----------------判断机构权限--------------------
				 
				 String checkFlag = this.offOrgTrans(gPay.getHeader().getReqSys(), UpayCommon.getSysCodeByProvCode(crmMsgVo.getMsgReceiver()), CommonConstant.TransCode.tmallCharge.getValue(),type);
				 if(checkFlag != null){
					  throw new ServiceException("UPAY-B-012A16");
				 }
				 
				//-------------------------------- 
				 
				 //组装路由信息	
				 hessianHeader = new HessianMsgHeader();				 
				 hessianHeader.setAppCd(CommonConstant.CrmTrans.Crm17.getValue());
				 hessianHeader.setMqSeq(CommonConstant.Sequence.SendCrmMqSeq.toString());
				 hessianHeader.setReceiver(CommonConstant.PlatformCd.CrmSys.toString());
//				 hessianHeader.setProtocol(CoreCommon.getTmallProtocal());
//				 hessianHeader.setReqIp(CoreCommon.getTmallIP());
//				 hessianHeader.setReqPath(CoreCommon.getTmallReqPath());
//				 hessianHeader.setReqPort(CoreCommon.getTmallPort());
//				 hessianHeader.setRouteInfo(CoreCommon.getTmallRouteInfo());
				 hessianHeader.setProtocol(protocal);				
				 hessianHeader.setReqIp(ip);
				 hessianHeader.setReqPath(reqPath);
				 hessianHeader.setReqPort(port);
				 hessianHeader.setRouteInfo("");
				 
				 //组装应急追踪报文
				 tradeReqMessage = this.assemblyTrackReqMessage(gPay, payReq,txnSeq,transIDHTime, crmChargeReqVo,crmMsgVo);
				 
				 String xmlHead = MsgHandle.marshaller(crmMsgVo);
				 crmXmls[0] = xmlHead;		 
				 result.put("headerJson", JacksonUtils.bean2Json(hessianHeader));
				 result.put("xmlMsg", crmXmls);
				 result.put("tradeReqMessage", tradeReqMessage); 
				
			}else{
				
			}			
		}else{
			
		}
		
	     return result;
	}

	
	public String assemblyTrackReqMessage(GPay gPay,TMallPayReqVo payReq,String txnSeq,String transIDHTime,CrmChargeReqVo crmChargeReqVo,CrmMsgVo crmMsgVo){
		StringBuilder content = new StringBuilder();
		
		if(gPay == null){
			content.append("");
		}else{
			if(gPay.getHeader() == null){
				content.append("");
			}else{
				Header header = gPay.getHeader();
				//内部交易流水号#
				content.append("int_txn_seq:"+txnSeq);
				content.append("|");
				//内部交易代码#
				content.append("int_trans_code:"+CommonConstant.TransCode.tmallCharge.getValue());
				content.append("|");
				//内部交易日期#
				content.append("int_txn_date:"+DateUtil.getDateyyyyMMdd());
				content.append("|");
				//内部交易时间#
				content.append("int_txn_time:"+transIDHTime);
				content.append("|");
				//对账日期#
				content.append("settle_date:"+header.getReqDate());
				content.append("|");
				//发起方交易方式#
				content.append("pay_mode:"+CommonConstant.PayMode.onlineTrade.getValue());
				content.append("|");
				//业务大类#
				content.append("buss_type:"+CommonConstant.BussType.OnlineConsumeBus.getValue());
				content.append("|");
				//业务渠道#
				content.append("buss_chl:"+CommonConstant.BussChl.tmall.getValue());
				content.append("|");				
				//------------------------------------------------------
				//天猫交易代码
				content.append("tmall_activity_code:"+header.getActivityCode());
				content.append("|");
				//天猫方机构代码
				content.append("tmall_org_id:"+header.getReqSys());
				content.append("|");			
				//天猫请求流水号
				content.append("tmall_trans_id:"+header.getReqTransID());
				content.append("|");			
				//天猫请求的日期
				content.append("tmall_trans_dt:"+header.getReqDate());
				content.append("|");	
				//天猫请求的时间
				content.append("tmall_trans_tm:"+header.getReqDateTime());
				content.append("|");		
			    //天猫渠道标识
				content.append("tmall_cnl_type:"+header.getReqChannel());
				content.append("|");
				//移动业务编码
				content.append("crm_bip_code:"+crmMsgVo.getBIPCode());
				content.append("|");	
				//移动交易编码
				content.append("crm_activity_code:"+crmMsgVo.getActivityCode());
				content.append("|");	

	   		   /* //实际缴费金额#		
				content.append("pay_amt:"+payReq.getPayed());
				content.append("|");*/

				//移动机构编码
				content.append("crm_org_id:"+UpayCommon.getSysCodeByProvCode(crmMsgVo.getMsgReceiver()));
				content.append("|");
				
				//移动路由类型
				content.append("crm_route_type:"+crmMsgVo.getRouteType());
				content.append("|");
				//移动路由值
				content.append("crm_route_val:"+crmMsgVo.getRouteValue());
				content.append("|");
				//移动回话标识
				content.append("crm_session_id:"+crmMsgVo.getSessionID());
				content.append("|");
				//移动请求流水号
				content.append("crm_trans_id:"+crmMsgVo.getTransIDO());
				content.append("|");
				//移动请求日期
				content.append("crm_trans_dt:"+header.getReqDate());
				content.append("|");
				//移动请求时间
				content.append("crm_trans_tm:"+transIDHTime);
				content.append("|");				

			    //移动操作流水号		
				content.append("crm_opr_id:"+crmChargeReqVo.getTransactionID());
				content.append("|");
			    //移动操作日期		
				content.append("crm_opr_dt:"+crmChargeReqVo.getActionDate());
				content.append("|");
			    //移动操作时间		
				content.append("crm_opr_tm:"+crmChargeReqVo.getActionTime());
				content.append("|");
				
				//发起方渠道
				content.append("crm_cnl_type:"+header.getReqChannel());
				content.append("|");
				
			    //移动开始时间		
				content.append("crm_start_tm:"+transIDHTime);
				content.append("|");
				//省代码
				content.append("id_province:"+crmMsgVo.getMsgReceiver());
				content.append("|");

				
				//
				content.append("order_id:"+crmChargeReqVo.getOrderNo());
				content.append("|");
				
				content.append("pay_trans_id:"+crmChargeReqVo.getPayTransID());
				content.append("|");
				
	   		    //用户号码标识类型		
				content.append("id_type:"+payReq.getIDType());
				content.append("|");
	   		    //用户号码		
				content.append("id_value:"+payReq.getIDValue());
				content.append("|");
				
				content.append("home_prov:"+payReq.getHomeProv());
				content.append("|");
				
				content.append("payment:"+crmChargeReqVo.getPayment());
				content.append("|");
				
				content.append("charge_money:"+ crmChargeReqVo.getChargeMoney());
				content.append("|");
				
				content.append("prod_cnt:"+ crmChargeReqVo.getOrderCnt());
				content.append("|");
				
				content.append("prod_id:"+ crmChargeReqVo.getProductNo());
				content.append("|");
				
				content.append("commision:"+ crmChargeReqVo.getCommision());
				content.append("|");
				
				content.append("rebate_fee:"+ crmChargeReqVo.getRebateFee());
				content.append("|");
				
				content.append("prod_discount:"+ crmChargeReqVo.getProdDiscount());
				content.append("|");
				
				content.append("credit_card_fee:"+ crmChargeReqVo.getCreditCardFee());
				content.append("|");
				
				content.append("service_fee:"+ crmChargeReqVo.getServiceFee());
				content.append("|");
				
			    //缴费类型		
				content.append("payed_type:"+CommonConstant.PayType.PayPre.getValue());
				content.append("|");
				
				
				
				content.append("activity_no:"+ crmChargeReqVo.getActivityNo());
				content.append("|");
				
				content.append("prod_shelf_no:"+ crmChargeReqVo.getProductShelfNo());
				content.append("|");
				
				/*content.append("ori_org_id:"+ header.getReqSys());
				content.append("|");*/
			}			
		}		
		return content.toString();
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map assemblyTMallResMsg(GPay gpay, String crmResXml, String txnSeq,
			String transIdHTime) {
		Map result = new HashMap();
		String resXml = "";
		CrmMsgVo crmMsgVo = null;
		CrmChargeResVo  crmChargeResVo = null;
		//GPay gpayRes = new GPay();
		GPay gpayRes = gpay;
		Header header1 = gpayRes.getHeader();
		Header header = new Header();
		String tradeReqMessage = "";
		TmallConsumeResVo resBody = new TmallConsumeResVo();
		TmallConsumeReqVo reqBody = new TmallConsumeReqVo();
		if(StringUtils.isBlank(crmResXml)){
			log.warn("天猫应急解析省侧报文为空{}", crmResXml);
			logger.warn("天猫应急解析省侧报文为空{}", crmResXml);
		}else{
			crmMsgVo = new CrmMsgVo();
			crmChargeResVo = new CrmChargeResVo();
			//解析报文头
			MsgHandle.unmarshaller(crmMsgVo, crmResXml);
			//解析报文体
			MsgHandle.unmarshaller(crmChargeResVo,(String)crmMsgVo.getBody());		
		String bodycontent = 	"<?xml version='1.0' encoding='UTF-8'?><Body>"+(String) gpayRes.getBody()+"</Body>";
			MsgHandle.unmarshaller(reqBody, bodycontent);
			GPay gpayres1 =  new GPay();
			header.setActionCode(CoreConstant.ACTION_CODE_REQEUEST);
			header.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
			header.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
			header.setRcvSys(CommonConstant.BankOrgCode.TMALL.getValue());
			header.setActivityCode(CoreConstant.tmall_response_code);
			header.setReqDate(header1.getReqDate());
			header.setReqDateTime(header1.getReqDateTime());
			resBody.setOriReqTransID(header1.getReqTransID());
			resBody.setOriReqDate(header1.getReqDate());
			resBody.setOrderID(reqBody.getOrderID());
			/*if("".equals((String)crmMsgVo.getBody())){
				resBody.setResultCode(CoreConstant.ErrorCode.BANK_015A07.getCode());
				resBody.setResultDesc(CoreConstant.ErrorCode.BANK_015A07.getDesc());
			}else */
				if(CommonConstant.RspCode.SUCCESS.getValue().equals(crmMsgVo.getRspCode()) && CommonConstant.RspCode.SUCCESS.getValue().equals(crmChargeResVo.getRspCode())){
				resBody.setResultCode(CoreConstant.ErrorCode.SUCCESS.getCode());
				resBody.setResultDesc(CoreConstant.ErrorCode.SUCCESS.getDesc());
			  } else{/*else if(CommonConstant.RspCode.CRM_3A36.getValue().equals(crmMsgVo.getRspCode())){
				resBody.setResultCode(CoreConstant.ErrorCode.TMALL_013A36.getCode());
				resBody.setResultDesc(CoreConstant.ErrorCode.TMALL_013A36.getDesc());
			}else if(CommonConstant.RspCode.CRM_3A37.getValue().equals(crmMsgVo.getRspCode())){
				resBody.setResultCode(CoreConstant.ErrorCode.TMALL_013A37.getCode());
				resBody.setResultDesc(CoreConstant.ErrorCode.TMALL_013A37.getDesc());
			}*/
			 
				log.warn("省侧天猫应急充值失败,省一级返回码:{}，二级返回码:{}", new Object[]{crmMsgVo.getRspCode(),crmChargeResVo.getRspCode()} );
				logger.warn("省侧天猫应急充值失败,省一级返回码:{}，二级返回码:{}", new Object[]{crmMsgVo.getRspCode(),crmChargeResVo.getRspCode()} );
				resBody.setResultCode(CoreConstant.ErrorCode.BANK_015A02.getCode());
				resBody.setResultDesc(CoreConstant.ErrorCode.BANK_015A02.getDesc());
			 }

			
			resBody.setResultTime(crmMsgVo.getTransIDHTime());
			header.setReqTransID(txnSeq);
			String xmlBody = MsgHandle.marshaller(resBody);
			xmlBody=xmlBody.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Body>", "").replace("</Body>", "");
				
			gpayres1.setBody(xmlBody);
		   // gpayRes.setBody(xmlBody);
			//header.setRcvDate(StrUtil.subString(transIdHTime, 0, 8));
			//header.setRcvDateTime(transIdHTime);
			//header.setRcvTransID(txnSeq);
			//header.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			//应答/错误代码
			//TODO 这个地方需要判断应答码是否充值成功，需明确天猫应答码
			/*if(StringUtils.isNotBlank(crmChargeResVo.getRspCode())){
				header.setRspCode(crmChargeResVo.getRspCode());
			}*/
			//应答/错误描述
			/*if(StringUtils.isNotBlank(crmChargeResVo.getRspInfo())){
				header.setRspDesc(crmChargeResVo.getRspInfo());
			}*/				
			//当前无法根据省前置返回的应答码确认银行前置的返回码，暂这样处理
			/*if(StringUtils.isNotBlank(crmChargeResVo.getRspCode())){
				if(crmChargeResVo.getRspCode().trim().equals(CommonConstant.RspCode.SUCCESS.getValue())){
					header.setRspCode(CoreConstant.ErrorCode.SUCCESS.getCode());
					header.setRspDesc(CoreConstant.ErrorCode.SUCCESS.getDesc());
				}else{
					header.setRspCode(CoreConstant.ErrorCode.CODE_015A06.getCode());
					header.setRspDesc(CoreConstant.ErrorCode.CODE_015A06.getDesc());
				}
			}else{
				header.setRspCode(CoreConstant.ErrorCode.CODE_015A06.getCode());
				header.setRspDesc(CoreConstant.ErrorCode.CODE_015A06.getDesc());
			}*/
			
			gpayres1.setHeader(header);	
			//gpayRes.setBody("");
			resXml = TMallXMLParser.parseGPay(gpayres1);
		}	
		tradeReqMessage = this.assemblyTrackResMessage(header,crmMsgVo,crmChargeResVo,transIdHTime);
		result.put("resXml", resXml);
		result.put("tradeResMessage", tradeReqMessage);
		return result;
	}
	
	public String assemblyTrackResMessage(Header header,CrmMsgVo  crmMsgVo,CrmChargeResVo crmChargeResVo,String transIdHTime){
		StringBuilder content = new StringBuilder();
		/*if(gpayRes == null){
			content.append("");
		}else{*/
			/*if(gpayRes.getHeader() == null){
				content.append("");
			}else{*/
				
				//Header header = gpayRes.getHeader();
				//天猫落地流水号
				content.append("tmall_transh_id:"+header.getReqTransID());
				content.append("|");			
				//天猫落地的日期
				content.append("tmall_transh_dt:"+header.getReqDate());
				content.append("|");			
				//天猫落地的时间
				content.append("tmall_transh_tm:"+header.getReqDateTime());
				content.append("|");
				//移动应答流水号
				content.append("crm_transh_id:"+crmMsgVo.getTransIDH());
				content.append("|");
				//移动应答日期
				content.append("crm_transh_dt:"+StrUtil.subString(crmMsgVo.getTransIDHTime(),0,8));
				content.append("|");
				//移动应答时间
				content.append("crm_transh_tm:"+crmMsgVo.getTransIDHTime());
				content.append("|");
				//接收移动应答
				content.append("crm_end_tm:"+transIdHTime);
				content.append("|");

				//用户付费类型
			/*	content.append("user_cat:"+crmChargeResVo.getUserCat()); 
				content.append("|");*/
				 //天猫应答代码	
				//天猫应答描述
			   if(CommonConstant.RspCode.SUCCESS.getValue().equals(crmMsgVo.getRspCode()) && CommonConstant.RspCode.SUCCESS.getValue().equals(crmChargeResVo.getRspCode())){
					content.append("tmall_rsp_code:"+CoreConstant.ErrorCode.SUCCESS.getCode());
					content.append("|");
					content.append("tmall_rsp_desc:"+CoreConstant.ErrorCode.SUCCESS.getDesc());
					content.append("|");
				}else{ 
					content.append("tmall_rsp_code:"+CoreConstant.ErrorCode.BANK_015A02.getCode());
					content.append("|");
					content.append("tmall_rsp_desc:"+CoreConstant.ErrorCode.BANK_015A02.getDesc());
					content.append("|");
				}
			      //移动返回类型
				   content.append("crm_rsp_type:"+crmMsgVo.getRspType());
				   content.append("|");
					//移动一级应答码
					content.append("crm_rsp_code:"+crmMsgVo.getRspCode());
					content.append("|");
					//移动二级应答码
					content.append("crm_sub_rsp_code:"+crmChargeResVo.getRspCode());
					content.append("|");
					//移动一级应答信息
					content.append("crm_rsp_desc:"+crmMsgVo.getRspDesc());
					content.append("|");
					//移动二级应答信息
					content.append("crm_sub_rsp_desc:"+crmChargeResVo.getRspInfo());
					content.append("|");
			
	   		    //返销标示#		
				content.append("back_flag:"+CommonConstant.YesOrNo.No.getValue());
				content.append("|");
				 //退款标示#		
				content.append("refund_flag:"+CommonConstant.YesOrNo.No.getValue());
				content.append("|");
				 //冲正标识#		
				content.append("reverse_flag:"+CommonConstant.YesOrNo.No.getValue());
				content.append("|");
				 //是否已经对账#
				content.append("reconciliation_flag:"+CommonConstant.YesOrNo.No.getValue());
				content.append("|");
				//状态
				String status = null;
				if(CommonConstant.RspCode.SUCCESS.getValue().equals(crmMsgVo.getRspCode()) &&  CommonConstant.RspCode.SUCCESS.getValue().equals(crmChargeResVo.getRspCode())  ){
					status=CommonConstant.TxnStatus.TxnSuccess.getValue();
				}else{
					 status=CommonConstant.TxnStatus.TxnFail.getValue();
					
				}
				
				content.append("status:"+status);				
				content.append("|");	
				//最后修改时间#
				content.append("last_upd_time:"+DateUtil.getDateyyyyMMddHHmmssSSS());
				content.append("|");
			//}
		//}	
		return content.toString();
	}

	/**
	 * 验证机构的状态及机构交易权限，校验顺序：发起方就够，接收方机构，机构交易权限
	 * @param reqOrg 发起方机构代码
	 * @param rcvOrg 接收方机构代码
	 * @param transCode 内部交易代码
	 * @param type  号码类型：1为移动号码，2为联通电信，0为未知号码
	 * @return  reqOrg or rcvOrg,if any org check failed
	 * 			transCode if org trans_code check failed
	 */
	public String offOrgTrans(String reqOrg, String rcvOrg,String transCode,int type){
		List<String> orgList = new ArrayList<String>();
		if (!StringUtils.isBlank(reqOrg)) {
			orgList.add(reqOrg);
		}
		if (!StringUtils.isBlank(rcvOrg)) {
			orgList.add(rcvOrg);
		}
		String offOrg = this.offOrg(orgList);
		if(!StringUtils.isBlank(offOrg)||"null".equalsIgnoreCase(offOrg)){
			logger.warn("机构:{}交易:{}权限关闭",offOrg,transCode);
			log.warn("机构:{}交易:{}权限关闭",offOrg,transCode);
			return new StringBuffer(offOrg).append(",").append(transCode).toString();
		}
		if(type == 1){
			String o2o = this.getOrgMapTransCode(reqOrg, rcvOrg);
			if (o2o == null || "".equals(o2o) ||"null".equalsIgnoreCase(o2o)) {
				logger.warn("机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
				log.warn("机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
				return new StringBuffer(reqOrg).append(",").append(rcvOrg).append(",").append(transCode).toString();
			}
			String transListStr = o2o.trim();
			if (StringUtils.isBlank(transListStr)) {
				logger.warn("机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
				log.warn("机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
				return new StringBuffer(reqOrg).append(",").append(rcvOrg).append(",").append(transCode).toString();
			}
			String transArr[] = transListStr
					.split(CommonConstant.SpeSymbol.COMMA_MARK.getValue());
			if (transArr.length == 0) {
				logger.warn("机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
				log.warn("机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
				return new StringBuffer(reqOrg).append(",").append(rcvOrg).append(",").append(transCode).toString();
			}
			List<String> transListTrim = new ArrayList<String>();
			for (String s : transArr) {
				transListTrim.add(s.trim());
			}
			if (!transListTrim.contains(transCode)) {
				logger.warn("机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
				log.warn("机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
				return new StringBuffer(reqOrg).append(",").append(rcvOrg).append(",").append(transCode).toString();
			}
		}else if(type == 2 ){
			String o2o = this.getOrgUnMapTransCode(reqOrg, rcvOrg);
			if (o2o == null || "".equals(o2o) ||"null".equalsIgnoreCase(o2o)) {
				logger.warn("联通电信号码机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
				log.warn("联通电信号码机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
				return new StringBuffer(reqOrg).append(",").append(rcvOrg).append(",").append(transCode).toString();
			}
			String transListStr = o2o.trim();
			if (StringUtils.isBlank(transListStr)) {
				logger.warn("联通电信号码机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
				log.warn("联通电信号码机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
				return new StringBuffer(reqOrg).append(",").append(rcvOrg).append(",").append(transCode).toString();
			}
			String transArr[] = transListStr
					.split(CommonConstant.SpeSymbol.COMMA_MARK.getValue());
			if (transArr.length == 0) {
				logger.warn("联通电信号码机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
				log.warn("联通电信号码机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
				return new StringBuffer(reqOrg).append(",").append(rcvOrg).append(",").append(transCode).toString();
			}
			List<String> transListTrim = new ArrayList<String>();
			for (String s : transArr) {
				transListTrim.add(s.trim());
			}
			if (!transListTrim.contains(transCode)) {
				logger.warn("联通电信号码机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
				log.warn("联通电信号码机构:{},{}交易:{}权限关闭",new Object[]{reqOrg,rcvOrg,transCode});
				return new StringBuffer(reqOrg).append(",").append(rcvOrg).append(",").append(transCode).toString();
			}
			
		} 
		else if(type == 0){
			logger.warn("该号码未知，不属于移动联通电信号段！机构:{},{}交易:{}权限关闭", new Object[]{reqOrg,rcvOrg,transCode});
			log.warn("该号码未知，不属于移动联通电信号段！机构:{},{}交易:{}权限关闭", new Object[]{reqOrg,rcvOrg,transCode});
			return new StringBuffer(reqOrg).append(",").append(rcvOrg).append(",").append(transCode).toString(); 
		}

				
		return null;
	}
		
	public String getOrgMapTransCode(String reqOrg,
			String rcvOrg) {
		String o2o = null;
		try {
			UpayCsysOrgMapTransCode o3o = null;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("reqOrgId", reqOrg);
			params.put("rcvOrgId", rcvOrg);
			params.put("status", CommonConstant.IsActive.True.getValue());
			params.put("isHistory", CommonConstant.IsHistory.Normal.getValue());
			o3o = upayCsysOrgMapTransCodeMapper.selectByParams(params);			
			if(null == o3o || "".equals(o3o.getReqOrgId().trim())){
				o2o = null;
			}else{
				o2o = o3o.getTransCodeCollect();
			}
		} catch (Exception e) {
			logger.error("内存数据库调用异常",e);
		}		
		return o2o;		
	}
		/*
		 * 查询联通号码权限
		 */
	public String getOrgUnMapTransCode(String reqOrg,
			String rcvOrg) {
		String o2o = null;
		try {
			UpayCsysOrgMapTransCode o3o = null;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("reqOrgId", reqOrg);
			params.put("rcvOrgId", rcvOrg);
			params.put("status", CommonConstant.IsActive.True.getValue());
			params.put("isHistory", CommonConstant.IsHistory.Normal.getValue());
			o3o = upayCsysOrgMapTransCodeMapper.selectByUnParams(params);			
			if(null == o3o || "".equals(o3o.getReqOrgId().trim())){
				o2o = null;
			}else{
				o2o = o3o.getTransCodeCollect();
			}
		} catch (Exception e) {
			logger.error("联通电信内存数据库调用异常",e);
		}		
		return o2o;		
	}
    public String offOrg(List<String> orgStrList){    	
    	List<UpayCsysOrgCode> orgList = this.getOrgCodeIn(orgStrList);
		if (orgList == null ||orgList.size()==0 || orgList.isEmpty()) {
			return null;
		}
		for (UpayCsysOrgCode org : orgList) {
			if(org == null ){
				continue;
			}
			if (org.getStatus()
					.equals(CommonConstant.IsActive.False.getValue())
					|| org.getIsHistory().equals(
							CommonConstant.IsHistory.History.getValue())) {
				return org.getOrgId();
			}
		}
		return null;
	}
	
	List<UpayCsysOrgCode> getOrgCodeIn(List orgList) {		
		if (orgList == null || orgList.isEmpty())
			return null;
		List<UpayCsysOrgCode> orgCodeList = new ArrayList<UpayCsysOrgCode>();		
		try {		
			for (Object o : orgList) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("orgId", String.valueOf(o));
				params.put("status", CommonConstant.IsActive.True.getValue());
				params.put("isHistory",
						CommonConstant.IsHistory.Normal.getValue());
				UpayCsysOrgCode org = upayCsysOrgCodeMapper.selectByParams(params);
				if(org!= null){
					orgCodeList.add(org);
				}
			}
		} catch (Exception e) {
				logger.error("内存数据库调用异常，getOrgCodeIn",e);
		}
		
		return orgCodeList;	
	}
	
	

	public String getProtocal() {
		return protocal;
	}


	public void setProtocal(String protocal) {
		this.protocal = protocal;
	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public String getPort() {
		return port;
	}


	public void setPort(String port) {
		this.port = port;
	}


	public String getReqPath() {
		return reqPath;
	}


	public void setReqPath(String reqPath) {
		this.reqPath = reqPath;
	}


	public String getTestFlag() {
		return testFlag;
	}


	public void setTestFlag(String testFlag) {
		this.testFlag = testFlag;
	}
	
/*	public void setRouteInfo(String routeInfo) {
		this.routeInfo = routeInfo;
	}
	
	public String getRouteInfo() {
		return routeInfo;
	}*/

}
