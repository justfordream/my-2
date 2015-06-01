package com.huateng.cmupay.thrid.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.action.AbsBaseAction;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.DictConst;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.controller.cache.DictCodeCache;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogService;
import com.huateng.cmupay.controller.service.system.IUpayBatParamService;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogHisService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.message.SendCrmJmsMessageImpl;
import com.huateng.cmupay.logFormat.MobileMarketMessageLogger;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.TpayCsysTxnLog;
import com.huateng.cmupay.models.UpayCsysDictCode;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.his.UpayBatParam;
import com.huateng.cmupay.models.his.UpayCsysTxnLogHis;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopRevokeMsgReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopRevokeMsgRspVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmReverseMsgReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmReverseMsgResVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;

@Controller("mmarkertRevokeUpayAction")
@Scope("prototype")
public class MobileMarketRevokeUpayAction extends AbsBaseAction<MobileShopMsgVo, MobileShopMsgVo> {
	private MobileMarketMessageLogger marketOperLogger = MobileMarketMessageLogger.getLogger(this.getClass());
	private final Logger marketLogger = LoggerFactory.getLogger("MMARKET_FILE");
	//后线库 --old
	@Autowired
	private IUpayCsysTxnLogHisService upayCsysTxnLogHisService;
	//前线库--new
	@Autowired
	private ITpayCsysTxnLogService tpayCsysTxnLogService;
	@Autowired
	private IUpayBatParamService upayBatParamService;
	@Autowired
	private SendCrmJmsMessageImpl sendCrmJmsMessage;
	
	private static final String REVERSE_CODE = "012054";//冲正
	private static final String REFUND_CODE = "012055";//退费
	private static final String INFO = "INFO";
	private static final String WARN = "WARN";
	private static final String DEBUG = "DEBUG";
	private static final String ERROR = "ERROR";
	private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	private static final String reconciliationFlag = "scdz";//移动商城对账参数
	
	@Override
	public MobileShopMsgVo execute(MobileShopMsgVo msgVo) throws AppBizException {
		//日志
		marketLogger.debug("MobileMarketRevokeUpayAction execute(Object) - start");
		
		// 请求报文报文头
		MobileShopMsgVo reqMsg = msgVo;
		//请求报文体
		MobileShopRevokeMsgReqVo reqBody = new MobileShopRevokeMsgReqVo();
		//响应报文体
		MobileShopRevokeMsgRspVo resBody = new MobileShopRevokeMsgRspVo();
		//响应报文头
		MobileShopMsgVo resMsg = new MobileShopMsgVo() ;
		
		//交易流水表,移动商城充值退费查询旧表
		UpayCsysTxnLog txnLog = null;
		UpayCsysTxnLogHis txnLogHis = null;
		//流水将保存到新表
		TpayCsysTxnLog tpayLog = new TpayCsysTxnLog();
		
		String transIDH = null;
		
		try {
			//请求报文xml转化为bean
			MsgHandle.unmarshaller(reqBody, (String) reqMsg.getBody());
			//请求报文体bean中加入请求报文体
			reqMsg.setBody(reqBody);
			resBody.setOrderID(reqBody.getOrderId());
			resBody.setOriOrderID(reqBody.getOriOrderID());
			resBody.setOriReqSys(reqBody.getOriReqSys());
			// 退单交易的帐期日  resBody.setSettleDate(null);
			//获取平台内部交易流水号
			transIDH = reqMsg.getTxnSeq();
			//获取平台内部交易时间
			String transIDHTime = reqMsg.getTxnTime();
			//获取平台内部交易数据库日切日期
			String intTxnDate = reqMsg.getTxnDate();
			//交易流水表唯一流水号
			Long seqId = reqMsg.getSeqId();
			
			/*
			 *初始化resMsg的值 
			 */
			resMsg.setActivityCode(reqMsg.getActivityCode());
			resMsg.setReqSys(reqMsg.getReqSys());
			resMsg.setReqChannel(reqMsg.getReqChannel());
			resMsg.setReqDate(reqMsg.getReqDate());
			resMsg.setReqTransID(reqMsg.getReqTransID());
			resMsg.setReqDateTime(reqMsg.getReqDateTime());
			resMsg.setActionCode(CommonConstant.ActionCode.Respone.getValue());//应答
			resMsg.setRcvSys(reqMsg.getRcvSys());
			resMsg.setRcvDate(transIDHTime.substring(0, 8));
			resMsg.setRcvTransID(transIDH);
			resMsg.setRcvDateTime(transIDHTime);

			//内/外部交易码
			UpayCsysTransCode transCodeInfo = msgVo.getTransCode();
			String transCode = transCodeInfo.getTransCode();
			String activityCode = msgVo.getActivityCode();
			//初始化流水表数据
			initLog(tpayLog, seqId, transIDH,intTxnDate,transIDHTime,reqMsg,transCodeInfo,resMsg,reqBody );
			//判断是否重复交易
			Map<String,Object> param1 =new HashMap<String,Object>();
			param1.put("reqTransId", reqMsg.getReqTransID());
			param1.put("reqDomain", reqMsg.getReqSys());
			TpayCsysTxnLog txnLog_isExit=tpayCsysTxnLogService.findObj(param1);
			if(txnLog_isExit != null){
				marketLogger.warn("重复交易:流水号{},机构编号{}",reqMsg.getReqTransID(),reqMsg.getReqSys());
				resMsg.setRspCode(RspCodeConstant.Market.MARKET_013A34.getValue());
				resMsg.setRspDesc(RspCodeConstant.Market.MARKET_013A34.getDesc());
				resMsg.setBody(resBody);
				return resMsg;
			}else{
				tpayCsysTxnLogService.add(tpayLog);
			}
			
			//报文格式校验
			String checkrtn = validateModel(reqBody);
			if (StringUtils.isNotBlank(StringUtil.toTrim(checkrtn))) {
				String []str=checkrtn.split(":");
				String code = str[0];
				String desc = str[1];

				printLog("报文体校验失败", checkrtn, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(code, desc,CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				return resMsg;
			}
			
			//22:30--00:30时间段内不允许交易
			if(!isValidTime()){
				printLog("交易不在受理时间范围内", reqBody.getOriOrderID(), reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(RspCodeConstant.Market.MARKET_013A45.getValue(), RspCodeConstant.Market.MARKET_013A45.getDesc(),
						CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				return resMsg;
			}
			
			//查询原交易 ，通过交易代码判断查询前线库（冲正）、后线库（退费）
			Map<String,Object> param2 =new HashMap<String,Object>();
			param2.put("orderId", reqBody.getOriOrderID());
			param2.put("reqDomain", reqBody.getOriReqSys());
			param2.put("reqTransDt", reqBody.getOriReqDate());

			//充值-冲正(查询前线库)、充值-退费（查询后线库）
			if(REVERSE_CODE.equalsIgnoreCase(activityCode)){
				txnLog = upayCsysTxnLogService.findObj(param2);
			}else if(REFUND_CODE.equalsIgnoreCase(activityCode)){
				txnLogHis = upayCsysTxnLogHisService.findHisStlObj(param2);
			}
			
			//原交易是否存在
			if(txnLog == null && txnLogHis == null){
				printLog("原交易不存在", reqBody.getOriOrderID(), reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(RspCodeConstant.Market.MARKET_014A05.getValue(), RspCodeConstant.Market.MARKET_014A05.getDesc(),
						CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				return resMsg;
			}
			
			//结算日期
			String settleDate = reqBody.getOriReqDate();

			//原交易流水
			String oriReqTransId = txnLog != null ? txnLog.getReqTransId() : txnLogHis.getReqTransId();

			//原交易是否发送到省、是否成功(正在处理中、失败)
			String forwardOrg = txnLog != null?txnLog.getRcvDomain():txnLogHis.getRcvDomain();//转发方机构代码
			if(StringUtils.isBlank(forwardOrg)){
				if(CommonConstant.TxnStatus.InitStatus.getValue().equalsIgnoreCase(txnLog != null? txnLog.getStatus():txnLogHis.getStatus())){
					printLog("原交易未发送到省充值", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog(RspCodeConstant.Market.MARKET_014A08.getValue(), RspCodeConstant.Market.MARKET_014A08.getDesc(),
							CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					return resMsg;
				}else if(CommonConstant.TxnStatus.TxnFail.getValue().equalsIgnoreCase(txnLog != null? txnLog.getStatus():txnLogHis.getStatus())){
					printLog("原交易失败", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog(RspCodeConstant.Market.MARKET_013A04.getValue(), RspCodeConstant.Market.MARKET_013A04.getDesc(),
							CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					return resMsg;
				}
			}
			
			//添加原交易中充值信息到退费交易中
			addInitLgs(tpayLog, txnLog ,txnLogHis);
			
			//是否当日交易   通过settledate作为原交易时间
			if(!intTxnDate.equals(settleDate) && REVERSE_CODE.equalsIgnoreCase(activityCode)){
				printLog("非当日交易不允许冲正", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(RspCodeConstant.Market.MARKET_013A05.getValue(), RspCodeConstant.Market.MARKET_013A05.getDesc(),
						CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				return resMsg;
			}
			
			//退费需隔日
			if(intTxnDate.equals(settleDate) && REFUND_CODE.equalsIgnoreCase(activityCode)){
				printLog("当日交易不允许退费", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(RspCodeConstant.Market.MARKET_013A02.getValue(), RspCodeConstant.Market.MARKET_013A02.getDesc(),
						CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				return resMsg;
			}
			
			//超过时限，不允许退费
			if(REFUND_CODE.equalsIgnoreCase(activityCode)){
				Date reqDate = format.parse(reqMsg.getReqDate());
				Date oriReqDate = format.parse(txnLog.getSettleDate());
				long dateO = (reqDate.getTime() - oriReqDate.getTime()) / (24 * 60 * 60 * 1000);//相隔天数
				
				boolean flag = dateO < Long.valueOf(
						DictCodeCache.getDictCode(DictConst.DictId.RevokeDaysMax.getValue(),
								DictConst.CodeId.RevokeDaysMax.getValue()).getCodeValue2());
				if(!flag){
					printLog("超过时限不允许退费", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog(RspCodeConstant.Market.MARKET_013A20.getValue(), RspCodeConstant.Market.MARKET_013A20.getDesc(),
							CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					return resMsg;
				}
			}
			
			//原交易是否已冲正（退费、冲正均需判断）
			String reverseFlag = txnLog != null ? txnLog.getReverseFlag() : txnLogHis.getReverseFlag();
			if(CommonConstant.YesOrNo.Yes.getValue().equalsIgnoreCase(reverseFlag)){
				printLog("原交易已冲正", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(RspCodeConstant.Market.MARKET_013A14.getValue(), RspCodeConstant.Market.MARKET_013A14.getDesc(),
						CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				return resMsg;
			}
			
			//原交易是否已完成退费(退费需判断)
			String refundFlag = txnLog != null ? txnLog.getRefundFlag() :txnLogHis.getRefundFlag();
			if(CommonConstant.YesOrNo.Yes.getValue().equalsIgnoreCase(refundFlag) && REFUND_CODE.equalsIgnoreCase(activityCode)){
				printLog("原交易已退费", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(RspCodeConstant.Market.MARKET_013A15.getValue(), RspCodeConstant.Market.MARKET_013A15.getDesc(),
						CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				
				return resMsg;
				
			}
			
			//原交易是否已完成对账（退费需判断）
			if(REFUND_CODE.equalsIgnoreCase(activityCode)){
				Map<String, Object> reconMap = new HashMap<String, Object>();
				reconMap.put("paramType", reconciliationFlag);// 设置查询标识
				reconMap.put("paramCode1", settleDate);// 结算日期
				reconMap.put("status", "00");// 设置参数为有效
				reconMap.put("paramValue", "00");// 设置为已对账
				
				UpayBatParam batParamInfo = upayBatParamService.findObj(reconMap);
				
				if(StringUtils.isBlank(settleDate) || batParamInfo == null){
					printLog("原交易未完成对账", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog(RspCodeConstant.Market.MARKET_013A19.getValue(), RspCodeConstant.Market.MARKET_013A19.getDesc(),
							CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					
					return resMsg;
				}
			}
			
			//校验权限
			String idValue = txnLog != null ? txnLog.getIdValue() : txnLogHis.getIdValue();
			ProvincePhoneNum provincePhoneNum = findProvinceByMobileNumber(idValue);
			String  orgFlag = offOrgTrans(reqMsg.getReqSys(), forwardOrg, transCode,
					provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
			if(orgFlag != null){
				printLog("此业务渠道无此权限", forwardOrg, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(RspCodeConstant.Market.MARKET_012A18.getValue(), RspCodeConstant.Market.MARKET_012A18.getDesc(),
						CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				
				return resMsg;
			}
			
			//发往省退费、冲正（同一个接口） UPSS发起按手机号码路由 //TODO   bipcode待定
			CrmMsgVo crmMsg = new CrmMsgVo();
			crmMsg.setTransCode(transCodeInfo);
			crmMsg.setVersion(ExcConstant.CRM_VERSION);
			crmMsg.setTestFlag(testFlag);
			//冲正  166  退费  167
			if(REVERSE_CODE.contains(activityCode)){
				crmMsg.setBIPCode(CommonConstant.Bip.Bis16.getValue());
			}else if(REFUND_CODE.contains(activityCode)){
				crmMsg.setBIPCode(CommonConstant.Bip.Bis17.getValue());
			}

			crmMsg.setActivityCode(CommonConstant.CrmTrans.Crm08.getValue());
			crmMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
			crmMsg.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
			crmMsg.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
			crmMsg.setRouteType(CommonConstant.RouteType.RoutePhone.getValue());
			crmMsg.setRouteValue(idValue);
			crmMsg.setSessionID(transIDH); 
			crmMsg.setTransIDO(transIDH);
			crmMsg.setTransIDOTime(StrUtil.subString(transIDHTime,0, 14));
			crmMsg.setMsgSender(CommonConstant.BankOrgCode.CMCC.getValue());
			crmMsg.setMsgReceiver(forwardOrg);
			
			CrmReverseMsgReqVo crmReqVo = new CrmReverseMsgReqVo();
			crmReqVo.setOriReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
			crmReqVo.setOriTransactionID(txnLog != null ? txnLog.getRcvOprId() : txnLogHis.getRcvOprId());
			crmReqVo.setOriActionDate(txnLog != null ? txnLog.getIntTxnDate() : txnLogHis.getIntTxnDate());
			crmReqVo.setRevokeReason(reqBody.getRevokeReason());
			crmReqVo.setTransactionID(Serial.genSerialNum(CommonConstant.Sequence.OprId.toString()));
			crmReqVo.setSettleDate(reqMsg.getReqDate());
			
			crmMsg.setBody(crmReqVo);
			
			printLog("begin 核心向CRM转发冲正(退费)请求", forwardOrg, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
			//发往省冲正
			crmMsg = sendCrmJmsMessage.sendMsg(crmMsg);
			
			printLog("end 核心向CRM转发冲正(退费)请求", forwardOrg, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
			
			//返回报文体是否为空
			if(null == crmMsg.getBody()){
				printLog("冲正(退费)省返回报文体为空", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(RspCodeConstant.Market.MARKET_015A07.getValue(), RspCodeConstant.Market.MARKET_015A07.getDesc(),
						CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				
				return resMsg;
			}
			
			//是否超时
			if(RspCodeConstant.Upay.UPAY_U99998.getValue().equalsIgnoreCase(crmMsg.getRspCode())){
				printLog("冲正(退费)省返回超时", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(RspCodeConstant.Market.MARKET_015A07.getValue(), RspCodeConstant.Market.MARKET_015A07.getDesc(),
						CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				
				return resMsg;
			}
			
			//初始化流水表省返回落地方参数  rcv
			initCrmRspLog(reqMsg, txnLog, txnLogHis, tpayLog, forwardOrg, crmMsg);
			
			//封装报文体 body
			CrmReverseMsgResVo crmResVo = new CrmReverseMsgResVo();
			MsgHandle.unmarshaller(crmResVo, (String) crmMsg.getBody());
			
			//交易成功
			if(RspCodeConstant.Wzw.WZW_0000.getValue().equalsIgnoreCase(crmMsg.getRspCode()) && 
					RspCodeConstant.Crm.CRM_0000.getValue().equalsIgnoreCase(crmResVo.getRspCode())){
				printLog("CRM应答成功", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
				
				resBody.setSettleDate(crmResVo.getSettleDate());
				initRspLog(RspCodeConstant.Market.MARKET_010A00.getValue(), RspCodeConstant.Market.MARKET_010A00.getDesc(),
						CommonConstant.TxnStatus.TxnSuccess.getValue(), resMsg, resBody, tpayLog);
				
				printLog("更新流水表记录", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
				
				//退费 需要修改原交易历史表数据（UPAY_CSYS_TXN_LOG_HIS_STL、UPAY_CSYS_TXN_LOG_HIS）
				if(REFUND_CODE.equalsIgnoreCase(activityCode)){
					
					addInitRefundHisLog(txnLogHis);
					printLog("更新流水历史表记录", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
				}else if(REVERSE_CODE.equalsIgnoreCase(activityCode)){
					
					addInitReverseHisLog(txnLog);
					printLog("更新流水历史表记录", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
				}

				marketLogger.debug("MobileMarketRevokeUpayAction execute(Object) - end");
				return resMsg;
			}else{
				String errCode = crmResVo.getRspCode();
				errCode = BankErrorCodeCache.getBankErrCode(errCode);
				String errDesc = RspCodeConstant.Bank.getDescByValue(errCode);
				printLog("CRM应答失败", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				initRspLog(errCode, errDesc,CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				
				return resMsg;
			}
			
			
		} catch (AppRTException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			String errDesc = RspCodeConstant.Bank.getDescByValue(errCode);
			printLog("运行异常", "", reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), ERROR);
			marketLogger.error("运行异常:",e);
			initRspLog(errCode, errDesc,CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
		}catch (AppBizException e){
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			String errDesc = RspCodeConstant.Bank.getDescByValue(errCode);
			printLog("业务异常", "", reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), ERROR);
			marketLogger.error("业务异常:",e);
			initRspLog(errCode, errDesc,CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
		}catch (Exception e) {
			printLog("系统异常", "", reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), ERROR);
			marketLogger.error("系统异常:",e);
			initRspLog(RspCodeConstant.Market.MARKET_015A06.getValue(), RspCodeConstant.Market.MARKET_015A06.getDesc(),
					CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
		}
		
		return resMsg;
	}
	
	/**
	 * 校验交易时间是否在允许退费交易时间段内
	 * @return
	 * @throws ParseException
	 */
	private Boolean isValidTime() throws ParseException {
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar c = Calendar.getInstance();
		Date dateNow = c.getTime();
		String dateStr = f.format(dateNow).substring(0,8);
		
		UpayCsysDictCode dictCodeInfo = DictCodeCache.getDictCode(DictConst.DictId.TimeQuantum.getValue(),DictConst.CodeId.TimeQuantum.getValue());
		String time1 = dictCodeInfo.getCodeValue1();
		String time2 = dictCodeInfo.getCodeValue2();
		Date date1 = f.parse(dateStr + time1);
		Date date2 = f.parse(dateStr + time2);
		
		Boolean isValidTime = (dateNow.after(date1) && dateNow.before(date2)) ? true : false;
		return isValidTime;
	}
	
	/**
	 * 初始化省返回数据
	 * @param reqMsg
	 * @param txnLog
	 * @param tpayLog
	 * @param forwardOrg
	 * @param crmMsg
	 */
	private void initCrmRspLog(MobileShopMsgVo reqMsg, UpayCsysTxnLog txnLog,UpayCsysTxnLogHis txnLogHis,
			TpayCsysTxnLog tpayLog, String forwardOrg, CrmMsgVo crmMsg) {
		tpayLog.setRcvTransId(tpayLog.getIntTxnSeq());
		tpayLog.setRcvTransDt(tpayLog.getIntTxnDate());
		tpayLog.setRcvTransTm(tpayLog.getIntTxnTime());
		tpayLog.setRcvTranshId(crmMsg.getTransIDH());
		tpayLog.setRcvTranshTm(crmMsg.getTransIDHTime());
		tpayLog.setRcvTranshDt(StrUtil.subString(crmMsg.getTransIDHTime(), 0, 8));
		tpayLog.setRcvDomain(forwardOrg);
		tpayLog.setRcvSessionId(crmMsg.getSessionID());
		tpayLog.setRcvOprId(tpayLog.getIntTxnSeq());
		tpayLog.setRcvOprDt(tpayLog.getIntTxnDate());
		tpayLog.setRcvOprTm(tpayLog.getIntTxnTime());
		tpayLog.setRcvRspType(crmMsg.getRspType());
		tpayLog.setRcvRspCode(crmMsg.getRspCode());
		tpayLog.setRcvRspDesc(crmMsg.getRspDesc());
		
		tpayLog.setRcvCnlType(txnLog!=null?txnLog.getRcvCnlType():txnLogHis.getRcvCnlType());
		tpayLog.setRcvStartTm(reqMsg.getRcvDateTime());
		tpayLog.setRcvEndTm(crmMsg.getTransIDHTime());
	}
	
	/**
	 * 打印日志信息
	 * @param display 日志主信息
	 * @param tag 日志次信息
	 * @param reqTransId 商城流水
	 * @param innerTransId 平台流水
	 * @param reqSys 发起方机构
	 */
	private void printLog(String display,String tag,String reqTransId,String innerTransId,String reqSys,String flag){
		if(WARN.equalsIgnoreCase(flag)){
			marketOperLogger.warn("{}:{}, 商城操作流水:{} ,内部交易流水号:{},发起方:{}",
					new Object[] { display,tag, reqTransId, innerTransId, reqSys});
			marketLogger.warn("{}:{}, 商城操作流水:{} ,内部交易流水号:{},发起方:{}",
					new Object[] { display,tag, reqTransId, innerTransId, reqSys});
		}else if(INFO.equalsIgnoreCase(flag)){
			marketOperLogger.info("{}:{}, 商城操作流水:{} ,内部交易流水号:{},发起方:{}",
					new Object[] { display,tag, reqTransId, innerTransId, reqSys});
			marketLogger.info("{}:{}, 商城操作流水:{} ,内部交易流水号:{},发起方:{}",
					new Object[] { display,tag, reqTransId, innerTransId, reqSys});
		}if(DEBUG.equalsIgnoreCase(flag)){
			marketOperLogger.debug("{}:{}, 商城操作流水:{} ,内部交易流水号:{},发起方:{}",
					new Object[] { display,tag, reqTransId, innerTransId, reqSys});
			marketLogger.debug("{}:{}, 商城操作流水:{} ,内部交易流水号:{},发起方:{}",
					new Object[] { display,tag, reqTransId, innerTransId, reqSys});
		}if(ERROR.equalsIgnoreCase(flag)){
			marketOperLogger.error("{}:{}, 商城操作流水:{} ,内部交易流水号:{},发起方:{}",
					new Object[] { display,tag, reqTransId, innerTransId, reqSys});
			marketLogger.error("{}:{}, 商城操作流水:{} ,内部交易流水号:{},发起方:{}",
					new Object[] { display,tag, reqTransId, innerTransId, reqSys});
		}
		
	}
	/**
	 * 应答报文、更新流水
	 * @param rspCode
	 * @param rspDesc
	 * @param status
	 * @param resMsg
	 * @param resBody
	 * @param tpayLog
	 */
	private void initRspLog(String rspCode,String rspDesc,String status,MobileShopMsgVo resMsg,MobileShopRevokeMsgRspVo resBody,TpayCsysTxnLog tpayLog){
		
		resMsg.setRspCode(rspCode);
		resMsg.setRspDesc(rspDesc);
		resMsg.setBody(resBody);
		
		tpayLog.setChlRspCode(rspCode);
		tpayLog.setChlRspDesc(rspDesc);
		tpayLog.setChlSubRspCode(rspCode);
		tpayLog.setChlSubRspDesc(rspDesc);
		tpayLog.setStatus(status);
		tpayLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		tpayCsysTxnLogService.modify(tpayLog);
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
	private void initLog(TpayCsysTxnLog txnLog, Long seqId, String transIDH,
			String intTxnDate, String transIDHTime, MobileShopMsgVo reqMsg,
			UpayCsysTransCode transCode, MobileShopMsgVo resMsg,MobileShopRevokeMsgReqVo reqBody ) {
		txnLog.setSettleDate(reqMsg.getReqDate());
		txnLog.setSeqId(seqId);
		txnLog.setIntTxnSeq(transIDH);
		txnLog.setIntTxnDate(intTxnDate);
		txnLog.setIntTxnTime(transIDHTime);
		txnLog.setBussType(transCode.getBussType());
		txnLog.setBussChl(transCode.getBussChl());
		txnLog.setIntTransCode(transCode.getTransCode());
		txnLog.setPayMode(transCode.getPayMode());
		txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.getValue());
		txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReqActivityCode(reqMsg.getActivityCode());
		
		txnLog.setReqDomain(reqMsg.getReqSys());
		txnLog.setReqCnlType(reqMsg.getReqChannel());
		txnLog.setReqSessionId(transIDH);

		txnLog.setReqTransId(reqMsg.getReqTransID());
		txnLog.setReqTransTm(reqMsg.getReqDateTime());
		txnLog.setReqTransDt(StrUtil.subString(reqMsg.getReqDateTime(), 0, 8));

		txnLog.setReqTranshId(transIDH);
		txnLog.setReqTranshDt(StrUtil.subString(transIDHTime,0,8));
		txnLog.setReqTranshTm(transIDHTime);
		
		txnLog.setReqOprId(reqMsg.getReqTransID());
		txnLog.setReqOprDt(StrUtil.subString(reqMsg.getRcvDateTime(), 0, 8));
		txnLog.setReqOprTm(reqMsg.getRcvDateTime());

		

		txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		
		txnLog.setOrderId(reqBody.getOrderId());
		txnLog.setOrderTm(StrUtil.subString(reqMsg.getReqDateTime(), 0, 14));
		
		txnLog.setRevokeReason(reqBody.getRevokeReason());
		
	}
	
	/**
	 * 原交易充值信息
	 * @param tpayLog
	 * @param txnLog
	 * @param txnLogHis
	 */
	private void addInitLgs(TpayCsysTxnLog tpayLog,UpayCsysTxnLog txnLog, UpayCsysTxnLogHis txnLogHis){
		tpayLog.setMainFlag(txnLog != null?txnLog.getMainFlag():txnLogHis.getMainFlag());//主副号码绑定标识
		tpayLog.setMainIdProvince(txnLog != null?txnLog.getMainIdProvince():txnLogHis.getMainIdProvince());// 主号码归属地
		tpayLog.setMainIdType(txnLog != null?txnLog.getMainIdType():txnLogHis.getMainIdType());// 主号码标识类型
		tpayLog.setMainIdValue(txnLog != null?txnLog.getMainIdValue():txnLogHis.getMainIdValue());// 主号码用户号码
		tpayLog.setIdProvince(txnLog != null?txnLog.getIdProvince():txnLogHis.getIdProvince());// 用户号码归属地
		tpayLog.setIdType(txnLog != null?txnLog.getIdType():txnLogHis.getIdType());// 用户号码标识类型
		tpayLog.setIdValue(txnLog != null?txnLog.getIdValue():txnLogHis.getIdValue());// 用户号码
		tpayLog.setSignStatus(txnLog != null?txnLog.getSignStatus():txnLogHis.getSignStatus());// 签约状态
		tpayLog.setBankId(null);// 银行编号(充值冲正、退费不需要记录)
		tpayLog.setBankAcctType(txnLog != null?txnLog.getBankAcctType():txnLogHis.getBankAcctType());// 银行帐号类型
		tpayLog.setBankAccId(txnLog != null?txnLog.getBankAccId():txnLogHis.getBankAccId());// 银行帐号
		
		tpayLog.setNeedPayAmt(txnLog != null?txnLog.getNeedPayAmt():txnLogHis.getNeedPayAmt());// 应缴金额
		tpayLog.setPayAmt(-(txnLog != null?txnLog.getPayAmt():txnLogHis.getPayAmt()));//原交易缴费金额    充值冲正、退费为整笔回退，直接填原交易缴费金额
		
		tpayLog.setOriOrgId(txnLog != null?txnLog.getReqDomain():txnLogHis.getReqDomain());//原交易发起机构
		tpayLog.setOriOprTransId(txnLog != null?txnLog.getReqTransId():txnLogHis.getReqTransId());//原交易流水
		tpayLog.setOriReqDate(txnLog != null?txnLog.getReqTransDt():txnLogHis.getReqTransDt());//原交易发起时间
		tpayCsysTxnLogService.modify(tpayLog);
	}
	
	/**
	 * 修改历史表退费标识
	 * @param logHis
	 */
	private void addInitRefundHisLog(UpayCsysTxnLogHis logHis){
		// 修改UPAY_CSYS_TXN_LOG_HIS_STL表
		UpayCsysTxnLogHis hisStlLog = new UpayCsysTxnLogHis();
		hisStlLog.setRefundFlag(CommonConstant.YesOrNo.Yes.toString());// 退费标识
		hisStlLog.setBackFlag(CommonConstant.YesOrNo.Yes.toString());// 返销标识
		hisStlLog.setSeqId(logHis.getSeqId());
		hisStlLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysTxnLogHisService.modifyHisStl(hisStlLog);
		
		// 修改UPAY_CSYS_TXN_LOG_HIS表
		UpayCsysTxnLogHis hisLog = new UpayCsysTxnLogHis();
		hisLog.setRefundFlag(CommonConstant.YesOrNo.Yes.toString());// 退费标识
		hisLog.setBackFlag(CommonConstant.YesOrNo.Yes.toString());// 返销标识
		hisLog.setSeqId(logHis.getSeqId());
		hisLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysTxnLogHisService.modify(hisLog);
		
	}
	
	/**
	 * 修改原交易冲正标示
	 * @param logHis
	 */
	private void addInitReverseHisLog(UpayCsysTxnLog logHis){
		UpayCsysTxnLog hisLog = new UpayCsysTxnLog();
		hisLog.setReverseFlag(CommonConstant.YesOrNo.Yes.toString());// 冲正标识
		hisLog.setBackFlag(CommonConstant.YesOrNo.Yes.toString());// 返销标识
		hisLog.setSeqId(logHis.getSeqId());
		hisLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		upayCsysTxnLogService.modify(hisLog);
	}

}
