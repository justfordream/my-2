package com.huateng.cmupay.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.controller.cache.ProvAreaCache;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogHisService;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogService;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogHisService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.message.SendCrmJmsMessageImpl;
import com.huateng.cmupay.logFormat.MobileMarketMessageLogger;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.TpayCsysTxnLog;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.his.UpayCsysTxnLogHis;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopQueryMsgReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopQueryMsgResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmTransQueryReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmTransQueryResVo;
import com.huateng.cmupay.utils.StringFormat;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;
import com.huateng.toolbox.utils.DateUtil;


@Controller("mmarkertResultQueryAction")
@Scope("prototype")
public class MobileMarketQueryAction extends AbsBaseAction<MobileShopMsgVo, MobileShopMsgVo>{
	
	
	private MobileMarketMessageLogger marketOperLogger = MobileMarketMessageLogger.getLogger(this.getClass());
	private final Logger marketLogger = LoggerFactory.getLogger("MMARKET_FILE");
	@Autowired
	private SendCrmJmsMessageImpl sendCrmJmsMessage;
	
	@Autowired
	private IUpayCsysTxnLogHisService upayCsysTxnLogHisService;
	
	@Autowired
	private ITpayCsysTxnLogHisService tpayCsysTxnLogHisService;
	
	@Override
	public MobileShopMsgVo execute(MobileShopMsgVo msgVo) throws AppBizException {
		marketLogger.debug("MarketTransResultQueryAction execute(Object) - start");
		
		// 请求报文
		MobileShopMsgVo reqMsg = msgVo;
		MobileShopQueryMsgReqVo reqBody = new MobileShopQueryMsgReqVo();
		MobileShopQueryMsgResVo resBody = new MobileShopQueryMsgResVo();
		String transIDH="";
		MobileShopMsgVo resMsg = new MobileShopMsgVo();
		
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		try {
			//请求报文xml转化为bean
			MsgHandle.unmarshaller(reqBody, (String) reqMsg.getBody());
			//请求报文体bean中加入请求报文体
			reqMsg.setBody(reqBody);
			//获取平台交易流水号
			transIDH = msgVo.getTxnSeq();
			//获取平台交易时间17位
			String transIDHTime = msgVo.getTxnTime();
			//获取平台交易时间14位
//			String trans14IDHTime = transIDHTime.substring(0,transIDHTime.length()-3);
			//平台交易数据库日切日期
			String intTxnDate = msgVo.getTxnDate();
			//交易流水表唯一流水号
			Long seqId = msgVo.getSeqId();
			//
//			String oprId=Serial.genSerialNum(CommonConstant.Sequence.OprId.toString());
			
			/*
			 *初始化resMsg的值 
			 */
			resMsg.setActivityCode(reqMsg.getActivityCode());
			resMsg.setReqSys(reqMsg.getReqSys());
			resMsg.setReqChannel(reqMsg.getReqChannel());
			resMsg.setReqDate(reqMsg.getReqDate());
			resMsg.setReqTransID(reqMsg.getReqTransID());
			resMsg.setReqDateTime(reqMsg.getReqDateTime());

			resMsg.setRcvSys(reqMsg.getRcvSys());


			
			resMsg.setRspCode(reqMsg.getRspCode());
			resMsg.setRspDesc(reqMsg.getRspDesc());
			
			//给相应报文头添加值
			resMsg.setRcvDate(transIDHTime.substring(0, 8));
			resMsg.setRcvDateTime(transIDHTime);
			resMsg.setRcvTransID(transIDH);
			resMsg.setActionCode(CommonConstant.ActionCode.Respone.getValue());

			//内部交易码
			UpayCsysTransCode transCode = msgVo.getTransCode();
			
			/**
			 * 根据交易代码判断是缴费查询还是充值查询
			 * */
			
			txnLog.setOriReqDate(reqBody.getOriReqDate());
			resBody.setQueryType(reqBody.getQueryType());
			
			/**
			 * 报文格式校验(不更新流水)
			 * */
			String checkrtn = validateModel(reqBody);
			if (!"".equals(StringUtil.toTrim(checkrtn))) {
				marketOperLogger.error("报文体校验失败:{},内部交易流水:{},发起方:{}", new Object[] {
						checkrtn, transIDH, reqMsg.getReqSys() });
				marketLogger.error("报文体校验失败:{},内部交易流水:{},发起方:{}", new Object[] {
						checkrtn, transIDH, reqMsg.getReqSys() });
				String []str=checkrtn.split(":");
				String value=checkrtn.split(":")[0];
				String desc =checkrtn.split(":")[1];
				if(str.length>2){
					desc=desc.substring(0,desc.length()-6);
				}
				//应答/错误代码
				resMsg.setRspCode(value);
				resMsg.setRspDesc(desc);
				return resMsg;
			}
			
			
			if(reqBody.getQueryType().equals(RspCodeConstant.MarketQueryType.MARKETQUERYTYPE_01.getValue())){
				txnLog.setOriOprTransId(reqBody.getOriReqTransID());
				resBody.setOriRcvTransID(reqBody.getOriReqTransID());
			}else if(reqBody.getQueryType().equals(RspCodeConstant.MarketQueryType.MARKETQUERYTYPE_02.getValue())){
				txnLog.setOriOprTransId(reqBody.getOriOrderID());
				resBody.setOriOrderID(reqBody.getOriOrderID());
				
			}
			
			/**
			 * 根据querytype做相应的oriOrderID与oriReqTransID的校验
			 * */
			String oriOrderID=reqBody.getOriOrderID();
			String oriReqTransID=reqBody.getOriReqTransID();
			String querytype=reqBody.getQueryType();
			if((querytype.equals(RspCodeConstant.MarketQueryType.MARKETQUERYTYPE_01.getValue()))&&oriReqTransID==null){
				marketOperLogger.error("报文格式错误,内部交易流水:{},发起方:{},oriOrderID 为空", transIDH, reqMsg.getReqSys());
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_019A32.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_019A32.getDesc());
				return resMsg;
			}else if(querytype.equals(RspCodeConstant.MarketQueryType.MARKETQUERYTYPE_02.getValue())&&oriOrderID==null){
				marketOperLogger.error("报文格式错误,内部交易流水:{},发起方:{},oriReqTransID 为空", transIDH, reqMsg.getReqSys());
				resMsg.setRspCode(RspCodeConstant.Market.MARKET_014A04.getValue());
				resMsg.setRspDesc(RspCodeConstant.Market.MARKET_014A04.getDesc()+"OriOrderID参数不正确");
				return resMsg;
			}
			//添加至交易流水表
			initLog(txnLog, seqId, transIDH,intTxnDate,transIDHTime,reqMsg,transCode,resMsg,reqBody );
			/**
			 * 判断是否重复交易
			 * */
			Map<String,Object> param1 =new HashMap<String,Object>();
			param1.put("reqTransId", reqMsg.getReqTransID());
			param1.put("reqDomain", reqMsg.getReqSys());
			UpayCsysTxnLog transLog_isExist=upayCsysTxnLogService.findObj(param1);
			if(transLog_isExist!=null){
				marketLogger.info("重复交易:流水号{},机构编号{}",reqMsg.getReqTransID(),reqMsg.getReqSys());
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_013A17.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_013A17.getDesc());
				return resMsg;
			}else{
				upayCsysTxnLogService.add(txnLog);
			}
			
			/**
			 * 说明:只支持三十天以内的数据，否则直接返回
			 * */
			if(DateUtil.getsubDate(intTxnDate,reqBody.getOriReqDate())>=30){
				marketOperLogger.error("只支持三十天以内的交易查询,内部交易流水:{},发起方:{}", transIDH, reqMsg.getReqSys());
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_013A38_30.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_013A38_30.getDesc());
				txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_02.getValue());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_013A38_30.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_013A38_30.getDesc());
				return resMsg;
			}
			
			// 查询原交易及处理
			Map<String, Object> params = new HashMap<String, Object>();
			if(reqBody.getQueryType().equals(RspCodeConstant.MarketQueryType.MARKETQUERYTYPE_01.getValue())){
				params.put("reqTransId", oriReqTransID);
			}else{
				params.put("orderId", oriOrderID);
			}
			params.put("reqDomain", reqBody.getOriReqSys());
			params.put("settleDate", reqBody.getOriReqDate());
			/**
			 * Author:oul
			 * 添加时间:2014/4/12
			 * 说明:判断是查询新流水表还是旧的流水表
			 * */
			//新流水对象
			TpayCsysTxnLog newTransLog = tpayCsysTxnLogService.findObj(params);
			if(newTransLog==null){
				newTransLog=tpayCsysTxnLogHisService.findObj(params);
			}
			UpayCsysTxnLog transLog = new UpayCsysTxnLog();
			if(newTransLog!=null){
				initTxnLog(transLog,newTransLog);
			}else{
				transLog=upayCsysTxnLogService.findObj(params);
				if(transLog==null){
					UpayCsysTxnLogHis upayTxnLoghis=upayCsysTxnLogHisService.findObj(params);
					if(upayTxnLoghis!=null){
						BeanUtils.copyProperties(upayTxnLoghis, transLog);
					}
				}
			}
			
			if (null == transLog) {
				marketOperLogger.info("原交易不存在:流水号{},机构编号{}",reqMsg.getReqTransID(),reqMsg.getReqSys());
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
				txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_00.getValue());
				txnLog.setChlSubRspCode(RspCodeConstant.Market.MARKET_014A05.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Market.MARKET_014A05.getDesc());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
				upayCsysTxnLogService.modify(txnLog);
				
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
				resBody.setOriResultCode(RspCodeConstant.Market.MARKET_014A05.getValue());
				resBody.setOriResultDesc(RspCodeConstant.Market.MARKET_014A05.getDesc());
				resMsg.setBody(resBody);
				return resMsg;
			}
			
			resBody.setOriResultTime(StringUtil.repNull((StrUtil.subString(transLog.getRcvTranshTm(),0, 14))));
			
			resBody.setOriRcvDate(StringUtil.repNull(transLog.getReqTranshDt()));
			resBody.setOriRcvTransID(StringUtil.repNull(transLog.getReqTranshId()));
			
			txnLog.setIdValue(transLog.getIdValue());
			txnLog.setIdType(transLog.getIdType());
			txnLog.setIdProvince(transLog.getIdProvince());
			String forwardOrg = transLog.getRcvDomain();// 转发方机构代码
			if(StringUtils.isBlank(forwardOrg) && CommonConstant.TxnStatus.InitStatus.getValue().equals(transLog.getStatus())){
				marketOperLogger.error("原交易未发送到省充值:{},内部交易流水:{},发起方:{}",
						new Object[] { reqMsg.getReqSys(), transIDH,reqMsg.getReqSys() });
				marketLogger.error("原交易未发送到省充值:{},内部交易流水:{},发起方:{}",
						new Object[] { reqMsg.getReqSys(), transIDH,reqMsg.getReqSys() });
				
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Market.MARKET_014A08.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Market.MARKET_014A08.getDesc());
				txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_00.getValue());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
				resBody.setOriResultCode(RspCodeConstant.Market.MARKET_014A08.getValue());
				resBody.setOriResultDesc(RspCodeConstant.Market.MARKET_014A08.getDesc());
				resMsg.setBody(resBody);
				return resMsg;
			}else if(StringUtils.isBlank(forwardOrg) && CommonConstant.TxnStatus.TxnFail.getValue().equals(transLog.getStatus())){
				marketOperLogger.error("原交易失败:{},内部交易流水:{},发起方:{}",
						new Object[] { reqMsg.getReqSys(), transIDH,reqMsg.getReqSys() });
				marketLogger.error("原交易失败:{},内部交易流水:{},发起方:{}",
						new Object[] { reqMsg.getReqSys(), transIDH,reqMsg.getReqSys() });
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
				txnLog.setChlSubRspCode(transLog.getChlSubRspCode());
				txnLog.setChlSubRspDesc(transLog.getChlSubRspDesc());
				txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_00.getValue());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
				resBody.setOriResultCode(transLog.getChlSubRspCode());
				resBody.setOriResultDesc(transLog.getChlSubRspDesc());
				resMsg.setBody(resBody);
				return resMsg;
			}

			/** 报文头 */
			CrmMsgVo forwardMsg = new CrmMsgVo();
			forwardMsg.setTransCode(transCode);
			forwardMsg.setVersion(ExcConstant.CRM_VERSION);
			forwardMsg.setTestFlag(testFlag);
			forwardMsg.setBIPCode(CommonConstant.Bip.Bis18.getValue());
			forwardMsg
					.setActivityCode(CommonConstant.CrmTrans.Crm09.getValue());
			forwardMsg.setActionCode(CommonConstant.ActionCode.Requset
					.getValue());
			forwardMsg.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
			forwardMsg.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
			String routeType=CommonConstant.RouteType.RoutePhone.getValue();
			forwardMsg.setRouteType(routeType);
			forwardMsg.setRouteValue(transLog.getIdValue());
//			forwardMsg.setSessionID(reqMsg.getReqTransID()); // 待确认
			forwardMsg.setSessionID(transIDH);
			forwardMsg.setTransIDO(transIDH);
			forwardMsg.setTransIDOTime(StrUtil.subString(transIDHTime, 0, 14));
			forwardMsg.setMsgSender(CommonConstant.BankOrgCode.CMCC.getValue());
			forwardMsg.setMsgReceiver(forwardOrg);//

			/** 报文体 */
			CrmTransQueryReqVo forwardBody = new CrmTransQueryReqVo();
			forwardBody.setOriActionDate(transLog.getIntTxnDate());
			
			forwardBody.setOriReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
			forwardBody.setOriTransactionID(transLog.getRcvOprId());
			forwardBody.setOriActivityCode(CommonConstant.CrmTrans.Crm07.getValue());
			
			forwardMsg.setBody(forwardBody);
			
			
			// 更新交易流水
			txnLog.setRcvOprId(forwardBody.getOriTransactionID());
			txnLog.setRcvOprDt(txnLog.getIntTxnDate());
			txnLog.setRcvOprTm(StrUtil.subString(forwardMsg.getTransIDOTime(),0, 14));
			txnLog.setRcvActivityCode(forwardMsg.getActivityCode());
			txnLog.setRcvBipCode(forwardMsg.getBIPCode());
			txnLog.setOriOrgId(reqMsg.getReqSys());
//			txnLog.setReqRouteType(forwardMsg.getRouteType());
//			txnLog.setReqRouteVal(forwardMsg.getRouteValue());
			txnLog.setReqSessionId(transIDH);
			txnLog.setReqCnlType(msgVo.getReqChannel());
			txnLog.setReqTransId(reqMsg.getReqTransID());
			txnLog.setReqTranshTm(transIDHTime);
			txnLog.setReqTransDt(intTxnDate);
			txnLog.setReqOprId(reqMsg.getReqTransID());
			txnLog.setReqOprDt(forwardBody.getOriActionDate());
			txnLog.setReqOprTm(forwardMsg.getTransIDHTime());
			// 查询该交易的号码段属于移动还是联通电信的。
			ProvincePhoneNum provincePhoneNum = ProvAreaCache.getProvAreaByPrimary(transLog.getIdValue());
			//校验落地方机构权限
			String checkFlag = offOrgTrans(reqMsg.getReqSys(), forwardOrg, msgVo.getTransCode().getTransCode(),
					provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
			
			//校验落地方机构权限
			if (checkFlag == null) {
				marketLogger.debug("sendCrmJmsMessage.sendMsg(forwardMsg) - start,intTxnSeq:{}",new Object[]{transIDH});
				forwardMsg = sendCrmJmsMessage.sendMsg(forwardMsg);
				marketLogger.debug("sendCrmJmsMessage.sendMsg(forwardMsg) - end,intTxnSeq:{}",new Object[]{transIDH});
				txnLog.setRcvTranshId(forwardMsg.getTransIDH());
				txnLog.setRcvTranshTm(forwardMsg.getTransIDHTime());
				txnLog.setRcvTranshDt(StrUtil.subString(forwardMsg.getTransIDHTime(), 0, 8));
				/*------5*/
				CrmTransQueryResVo forwardRtBody = new CrmTransQueryResVo();
				MsgHandle.unmarshaller(forwardRtBody, forwardMsg.getBody().toString());
				
				if(RspCodeConstant.Upay.UPAY_U99998.getValue().equals(forwardMsg.getRspCode())){
					marketOperLogger.error("CRM前置响应超时!内部交易流水:{},发起方:{},接收方:{},手机号:{}",new Object[] { transIDH,
									reqMsg.getReqSys(),forwardMsg.getMsgReceiver() ,transLog.getIdValue()});
					marketLogger.error("CRM前置响应超时!内部交易流水:{},发起方:{},接收方:{},手机号:{}",new Object[] { transIDH,
									reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue() });
					
					resMsg.setRspCode(RspCodeConstant.Market.MARKET_015A07.getValue());
					resMsg.setRspDesc(RspCodeConstant.Market.MARKET_015A07.getDesc());
					txnLog.setChlRspCode(RspCodeConstant.Market.MARKET_015A07.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Market.MARKET_015A07.getDesc());
					upayCsysTxnLogService.modify(txnLog);
					return resMsg;
				}
				
				if (forwardMsg.getBody() == null
						|| "".equals(forwardMsg.getBody().toString())
						|| "null".equalsIgnoreCase(forwardMsg.getBody()
								.toString())) {
					
					marketOperLogger.error("CRM返回报文体为空!内部交易流水:{},发起方:{},接收方:{},手机号:{}",new Object[] { transIDH,
							reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue() });
					marketLogger.error("CRM返回报文体为空!内部交易流水:{},发起方:{},接收方:{},手机号:{}",new Object[] { transIDH,
							reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue() });
					resMsg.setRspCode(RspCodeConstant.Market.MARKET_015A06.getValue());
					resMsg.setRspDesc(RspCodeConstant.Market.MARKET_015A06.getDesc());
					txnLog.setChlRspCode(RspCodeConstant.Market.MARKET_015A06.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Market.MARKET_015A06.getDesc());
					upayCsysTxnLogService.modify(txnLog);
					return resMsg;
				}
				
				/**
				 * rcv字段
				 * */
				txnLog.setRcvVersion(forwardMsg.getVersion());
				txnLog.setRcvBipCode(forwardMsg.getBIPCode());
				txnLog.setRcvActivityCode(forwardMsg.getActivityCode());
				txnLog.setRcvDomain(forwardOrg);
				txnLog.setRcvRouteType(forwardMsg.getRouteType());
				txnLog.setRcvRouteVal(forwardMsg.getRouteValue());
				txnLog.setRcvRouteInfo(forwardMsg.getRouteInfo()==null?null:forwardMsg.getRouteInfo().toString());
				txnLog.setRcvSessionId(forwardMsg.getSessionID());
				txnLog.setRcvTransId(forwardMsg.getTransIDO());
				txnLog.setRcvTransDt(txnLog.getIntTxnDate());
				txnLog.setRcvTransTm(StrUtil.subString(forwardMsg.getTransIDOTime(),0,14));
				txnLog.setRcvCnlType(transLog.getRcvCnlType());
				txnLog.setRcvStartTm(reqMsg.getRcvDateTime());
				txnLog.setRcvEndTm(forwardMsg.getTransIDHTime());
				
				

				if (RspCodeConstant.Wzw.WZW_0000.getValue().equals(
						forwardMsg.getRspCode())
						&& RspCodeConstant.Crm.CRM_0000.getValue().equals(
								forwardRtBody.getRspCode())) {
					marketOperLogger.succ("CRM响应成功!内部交易流水:{},发起方:{},接收方:{},手机号:{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue()});
					marketLogger.info("CRM响应成功!内部交易流水:{},发起方:{},接收方:{},手机号:{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue() });
					txnLog.setRcvRspCode(forwardMsg.getRspCode());
					txnLog.setRcvRspDesc(forwardMsg.getRspDesc());
					txnLog.setRcvRspType(forwardMsg.getRspType());
					txnLog.setRcvSubRspCode(forwardRtBody.getRspCode());
					txnLog.setRcvSubRspDesc(forwardRtBody.getRspInfo());
					
					txnLog.setChlRspCode(RspCodeConstant.Market.MARKET_010A00.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Market.MARKET_010A00.getDesc());
					txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_00.getValue());
					txnLog.setChlSubRspCode(RspCodeConstant.Market.MARKET_010A00.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Market.MARKET_010A00.getDesc());
					
					resMsg.setRspCode(RspCodeConstant.Market.MARKET_010A00.getValue());
					resMsg.setRspDesc(RspCodeConstant.Market.MARKET_010A00.getDesc());
					resBody.setOriResultCode(RspCodeConstant.Market.MARKET_010A00.getValue());
					resBody.setOriResultDesc(RspCodeConstant.Market.MARKET_010A00.getDesc());
				} else {
					marketOperLogger.error("CRM响应失败!内部交易流水:{},发起方:{},接收方:{},手机号:{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue()});
					marketLogger.error("CRM响应失败!内部交易流水:{},发起方:{},接收方:{},手机号:{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue() });
					
					String errNameHead = BankErrorCodeCache.getBankErrCode(forwardMsg.getRspCode());
					if("012998".equals(errNameHead)){
						errNameHead=RspCodeConstant.Bank.BANK_015A06.getValue();
					}
					String errNameBody = BankErrorCodeCache.getBankErrCode(forwardRtBody.getRspCode());
					if("012998".equals(errNameBody)){
						errNameBody = RspCodeConstant.Bank.BANK_015A06.getValue();
					}
					
					txnLog.setRcvRspCode(forwardMsg.getRspCode());
					txnLog.setRcvRspDesc(forwardMsg.getRspDesc());
					txnLog.setRcvRspType(forwardMsg.getRspType());
					txnLog.setRcvSubRspCode(forwardRtBody.getRspCode());
					txnLog.setRcvSubRspDesc(forwardRtBody.getRspInfo());
					
					txnLog.setChlRspCode(errNameHead);
					txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errNameHead));
					txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_02.getValue());
					txnLog.setChlSubRspCode(errNameBody);
					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errNameBody));
					
					resMsg.setRspCode(errNameHead);
					resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errNameHead));
					resBody.setOriResultCode(errNameBody);
					resBody.setOriResultDesc(RspCodeConstant.Bank.getDescByValue(errNameBody));
				}
				txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				
				
				resMsg.setBody(resBody);
				marketLogger.debug("TmallTransResultQueryAction execute(Object) - end");
				return resMsg;
			} else {
				
				marketOperLogger.error("落地方机构状态异常:{},内部交易流水:{},发起方:{},接收方:{},手机号:{}",new Object[] { forwardOrg, transIDH,
						reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue() });
				marketLogger.error("落地方机构状态异常:{},内部交易流水:{},发起方:{},接收方:{},手机号:{}",new Object[] { forwardOrg, transIDH,
						reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue() });
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				marketLogger.debug("MarketTransResultQueryAction execute(Object) - end");
				
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_02.getValue());
				upayCsysTxnLogService.modify(txnLog);
				return resMsg;
			}
		} catch (AppRTException e) {
			String errCode = e.getCode();
			marketOperLogger.error("运行异常!内部交易流水号:{},业务发起方:{}",
					new Object[] {RspCodeConstant.Bank.getDescByValue(errCode),
							transIDH, reqMsg.getReqSys() });
			marketLogger.error("运行异常,代码:{},内部交易流水号:{},业务发起方:{}",
					new Object[] { errCode,
							transIDH, reqMsg.getReqSys() });
			marketLogger.error("运行异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_01.getValue());
			upayCsysTxnLogService.modify(txnLog);
			resMsg.setRspCode(errCode);
			resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			return resMsg;
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			marketOperLogger.error("业务异常!内部交易流水号:{},业务发起方:{}",
					new Object[] {RspCodeConstant.Bank.getDescByValue(errCode),
							transIDH, reqMsg.getReqSys() });
			marketLogger.error("业务异常,代码:{},内部交易流水号:{},业务发起方:{}",
					new Object[] { errCode,
							transIDH, reqMsg.getReqSys() });
			marketLogger.error("业务异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_01.getValue());
			upayCsysTxnLogService.modify(txnLog);
			resMsg.setRspCode(errCode);
			resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			return resMsg;
		} catch (Exception e) {
			String errCode = RspCodeConstant.Bank.BANK_015A06.getValue();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			marketOperLogger.error("系统异常!内部交易流水号:{},业务发起方:{}",
					new Object[] {transIDH, reqMsg.getReqSys() });
			marketLogger.info("系统异常!内部交易流水号:{},业务发起方:{}",
					new Object[] {transIDH, reqMsg.getReqSys() });
			marketLogger.error("系统异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_01.getValue());
			upayCsysTxnLogService.modify(txnLog);
			resMsg.setRspCode(errCode);
			resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			return resMsg;
		}
	}

	/**
	 * 初始化交易流水
	 * 
	 * @param txnLog
	 * @param seqId
	 * @param transIDH
	 * @param intTxnDate
	 * @param transIDHTime
	 * @param reqMsg
	 * @param transCode
	 * @param resMsg
	 * @param reqBody
	 */
	private void initLog(UpayCsysTxnLog txnLog, Long seqId, String transIDH,
			String intTxnDate, String transIDHTime, MobileShopMsgVo reqMsg,
			UpayCsysTransCode transCode, MobileShopMsgVo resMsg,MobileShopQueryMsgReqVo reqBody ) {
		txnLog.setSettleDate(reqMsg.getReqDate());
		txnLog.setSeqId(seqId);
		txnLog.setIntTxnSeq(transIDH);
		txnLog.setIntTxnDate(intTxnDate);
		txnLog.setIntTxnTime(transIDHTime);
		txnLog.setIntMqSeq(reqMsg.getMqSeq());
		txnLog.setBussType(transCode.getBussType());
		txnLog.setBussChl(transCode.getBussChl());
		txnLog.setIntTransCode(transCode.getTransCode());
		txnLog.setPayMode(transCode.getPayMode());
		txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.getValue());
		txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReqActivityCode(reqMsg.getActivityCode());
		
		txnLog.setReqDomain(reqMsg.getReqSys());
		txnLog.setReqSessionId(transIDH);

		txnLog.setReqTransId(reqMsg.getReqTransID());
		txnLog.setReqTransTm(reqMsg.getReqDateTime());
		txnLog.setReqTransDt(StrUtil.subString(reqMsg.getReqDateTime(), 0, 8));

		txnLog.setReqTranshId(transIDH);
		txnLog.setReqTranshDt(StrUtil.subString(transIDHTime,0,8));
		txnLog.setReqTranshTm(transIDHTime);
		
		txnLog.setReqOprDt(StrUtil.subString(reqMsg.getRcvDateTime(), 0, 8));
		txnLog.setReqOprTm(reqMsg.getRcvDateTime());

		

		txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
	}
	/**
	 * 从新流水表对象赋值给旧流水表对象
	 * */
	public void initTxnLog(UpayCsysTxnLog oldtransLog,TpayCsysTxnLog newTransLog){
		oldtransLog.setSeqId(newTransLog.getSeqId());
		oldtransLog.setIntTxnSeq(newTransLog.getIntTxnSeq());
	    oldtransLog.setIntTransCode(newTransLog.getIntTransCode());
	    oldtransLog.setIntTxnDate(newTransLog.getIntTxnDate());
	    oldtransLog.setIntTxnTime(newTransLog.getIntTxnTime());
	    oldtransLog.setSettleDate(newTransLog.getSettleDate());
	    oldtransLog.setPayMode(newTransLog.getPayMode());
	    oldtransLog.setBussType(newTransLog.getBussType());
	    oldtransLog.setBussChl(newTransLog.getBussChl());
        oldtransLog.setReqBipCode(newTransLog.getReqBipCode());
	    oldtransLog.setReqActivityCode(newTransLog.getReqActivityCode());
	    oldtransLog.setReqDomain(newTransLog.getReqDomain());
	    oldtransLog.setReqSessionId(newTransLog.getReqSessionId());
	    oldtransLog.setReqTransId(newTransLog.getReqTransId());
	    oldtransLog.setReqTransDt(newTransLog.getReqTransDt());
	    oldtransLog.setReqTransTm(newTransLog.getReqTransTm());
	    oldtransLog.setReqTranshId(newTransLog.getReqTranshId());
	    oldtransLog.setReqTranshDt(newTransLog.getReqTranshDt());
	    oldtransLog.setReqTranshTm(newTransLog.getReqTranshTm());
	    oldtransLog.setReqOprId(newTransLog.getReqOprId());
	    oldtransLog.setReqOprDt(newTransLog.getReqOprDt());
	    oldtransLog.setReqOprTm(newTransLog.getReqOprTm());
	    oldtransLog.setReqCnlType(newTransLog.getReqCnlType());
	    oldtransLog.setOuterStartTm(newTransLog.getOuterStartTm());
	    oldtransLog.setOuterEndTm(newTransLog.getOuterEndTm());
	    oldtransLog.setRcvDomain(newTransLog.getRcvDomain());
	    oldtransLog.setRcvSessionId(newTransLog.getRcvSessionId());
	    oldtransLog.setRcvTransId(newTransLog.getRcvTransId());
	    oldtransLog.setRcvTransDt(newTransLog.getRcvTransDt());
	    oldtransLog.setRcvTransTm(newTransLog.getRcvTransTm());
	    oldtransLog.setRcvTranshId(newTransLog.getRcvTranshId());
	    oldtransLog.setRcvTranshDt(newTransLog.getRcvTranshDt());
	    oldtransLog.setRcvTranshTm(newTransLog.getRcvTranshTm());
	    oldtransLog.setRcvOprId(newTransLog.getRcvOprId());
	    oldtransLog.setRcvOprDt(newTransLog.getRcvOprDt());
	    oldtransLog.setRcvOprTm(newTransLog.getRcvOprTm());
	    oldtransLog.setRcvCnlType(newTransLog.getRcvCnlType());
	    oldtransLog.setRcvStartTm(newTransLog.getRcvStartTm());
	    oldtransLog.setRcvEndTm(newTransLog.getRcvEndTm());
	    oldtransLog.setMainFlag(newTransLog.getMainFlag());
	    oldtransLog.setMainIdProvince(newTransLog.getMainIdProvince());
	    oldtransLog.setMainIdType(newTransLog.getMainIdType());
	    oldtransLog.setMainIdValue(newTransLog.getMainIdValue());
	    oldtransLog.setIdProvince(newTransLog.getIdProvince());
	    oldtransLog.setIdType(newTransLog.getIdType());
	    oldtransLog.setIdValue(newTransLog.getIdValue());
	    oldtransLog.setSignStatus(newTransLog.getSignStatus());
	    oldtransLog.setBankId(newTransLog.getBankId());
	    oldtransLog.setBankAcctType(newTransLog.getBankAcctType());
	    oldtransLog.setBankAccId(newTransLog.getBankAccId());
	    oldtransLog.setOriOrgId(newTransLog.getOriOrgId());
	    oldtransLog.setOriOprTransId(newTransLog.getOriOprTransId());
	    oldtransLog.setOriReqDate(newTransLog.getOriReqDate());
	    oldtransLog.setOrderId(newTransLog.getOrderId());
	    oldtransLog.setOrderTm(newTransLog.getOrderTm());
	    oldtransLog.setMerId(newTransLog.getMerId());
	    oldtransLog.setMerVar(newTransLog.getMerVar());
	    oldtransLog.setSubId(newTransLog.getSubId());
	    oldtransLog.setSubTime(newTransLog.getSubTime());
	    oldtransLog.setPayType(newTransLog.getPayType());
	    oldtransLog.setRechAmount(newTransLog.getRechAmount());
	    oldtransLog.setRechThreshold(newTransLog.getRechThreshold());
	    oldtransLog.setNeedPayAmt(newTransLog.getNeedPayAmt());
	    oldtransLog.setPayAmt(newTransLog.getPayAmt());
	    oldtransLog.setPayedType(newTransLog.getPayedType());
	    oldtransLog.setChlRspType(newTransLog.getChlRspType());
	    oldtransLog.setChlRspCode(newTransLog.getChlRspCode());
	    oldtransLog.setChlSubRspCode(newTransLog.getChlSubRspCode());
	    oldtransLog.setChlRspDesc(newTransLog.getChlRspDesc());
	    oldtransLog.setChlSubRspDesc(newTransLog.getChlSubRspDesc());
	    oldtransLog.setOuterRspType(newTransLog.getOuterRspType());
	    oldtransLog.setOuterRspCode(newTransLog.getOuterRspCode());
	    oldtransLog.setOuterSubRspCode(newTransLog.getOuterSubRspCode());
	    oldtransLog.setOuterRspDesc(newTransLog.getOuterRspDesc());
	    oldtransLog.setOuterSubRspDesc(newTransLog.getOuterSubRspDesc());
	    oldtransLog.setRcvRspType(newTransLog.getRcvRspType());
	    oldtransLog.setRcvRspCode(newTransLog.getRcvRspCode());
	    oldtransLog.setRcvSubRspCode(newTransLog.getRcvSubRspCode());
	    oldtransLog.setRcvRspDesc(newTransLog.getRcvRspDesc());
	    oldtransLog.setRcvSubRspDesc(newTransLog.getRcvSubRspDesc());
	    oldtransLog.setBackFlag(newTransLog.getBackFlag());
	    oldtransLog.setRefundFlag(newTransLog.getRefundFlag());
	    oldtransLog.setReverseFlag(newTransLog.getReverseFlag());
	    oldtransLog.setReconciliationFlag(newTransLog.getReconciliationFlag());
	    oldtransLog.setStatus(newTransLog.getStatus());
	    oldtransLog.setLastUpdOprid(newTransLog.getLastUpdOprid());
	    oldtransLog.setLastUpdTime(newTransLog.getLastUpdTime());
	    oldtransLog.setBackUrl(newTransLog.getBackUrl());
	    oldtransLog.setServerUrl(newTransLog.getServerUrl());
	    oldtransLog.setlSeqId(newTransLog.getlSeqId());
	    oldtransLog.setClientIp(newTransLog.getClientIp());
	    oldtransLog.setOrderCnt(StringFormat.paseLong((newTransLog.getOrderCnt()+"")));
	    oldtransLog.setProductNo(newTransLog.getProductNo());
	    oldtransLog.setCommision(StringFormat.paseLong(newTransLog.getCommision()+""));
	    oldtransLog.setRebateFee(StringFormat.paseLong(newTransLog.getRebateFee()+""));
	    oldtransLog.setProdDiscount(StringFormat.paseLong(newTransLog.getProdDiscount()+""));
	    oldtransLog.setCreditCardFee(StringFormat.paseLong(newTransLog.getCreditCardFee()+""));
	    oldtransLog.setServiceFee(StringFormat.paseLong(newTransLog.getServiceFee()+""));
	    oldtransLog.setActivityNo(newTransLog.getActivityNo());
	    oldtransLog.setProductShelfNo(newTransLog.getProductShelfNo());
//	    oldtransLog.setIsDel(newTransLog.getIsDel());
	    oldtransLog.setPayTransID(newTransLog.getPayTransId());
//	    oldtransLog.setDiscount(newTransLog.getDiscount());
//	    oldtransLog.setChargeMoney(newTransLog.getChargeMoney());
//	    oldtransLog.setThrVersion(newTransLog.getThrVersion());
//	    oldtransLog.setThrTxnType(newTransLog.getThrTxnType());
//	    oldtransLog.setThrSubTxnType(newTransLog.getThrSubTxnType());
//	    oldtransLog.setThrProductType(newTransLog.getThrProductType());
//	    oldtransLog.setAccessType(newTransLog.getAccessType());
//	    oldtransLog.setAcquirerOgrCode(newTransLog.getAcquirerOgrCode());
//	    oldtransLog.setPayTimeoutDt(newTransLog.getPayTimeoutDt());
//	    oldtransLog.setThrPayType(newTransLog.getThrPayType());
//	    oldtransLog.setThrTransId(newTransLog.getThrTransId());
//	    oldtransLog.setPayStatus(newTransLog.getPayStatus());
//	    oldtransLog.setCardOrgCode(newTransLog.getCardOrgCode());
//	    oldtransLog.setSettleAmt(newTransLog.getSettleAmt());
//	    oldtransLog.setTraceNo(newTransLog.getTraceNo());
//	    oldtransLog.setTraceTime(newTransLog.getTraceTime());
	    oldtransLog.setReserved1(newTransLog.getReserved1());
	    oldtransLog.setReserved2(newTransLog.getReserved2());
	    oldtransLog.setReserved3(newTransLog.getReserved3());
	}
	 
}
