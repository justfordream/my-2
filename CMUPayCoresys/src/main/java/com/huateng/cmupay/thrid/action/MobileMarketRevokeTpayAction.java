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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.action.AbsBaseAction;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.DictConst;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.constant.TUPayConstant;
import com.huateng.cmupay.constant.TUPayConstant.AccessType;
import com.huateng.cmupay.constant.TUPayConstant.TpayType;
import com.huateng.cmupay.constant.TUPayConstant.UnPayRspCode;
import com.huateng.cmupay.constant.TUPayConstant.UnionPayMsg;
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.controller.cache.DictCodeCache;
import com.huateng.cmupay.controller.cache.MerCodeCache;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogHisService;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogService;
import com.huateng.cmupay.controller.service.system.IUpayBatParamService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.message.SendCrmJmsMessageImpl;
import com.huateng.cmupay.jms.message.SendTpayJmsMessageImpl;
import com.huateng.cmupay.logFormat.MobileMarketMessageLogger;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.TpayCsysMerCodeInfo;
import com.huateng.cmupay.models.TpayCsysTxnLog;
import com.huateng.cmupay.models.UpayCsysDictCode;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.his.UpayBatParam;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopRevokeMsgReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopRevokeMsgRspVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.TpayMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmReverseMsgReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmReverseMsgResVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;

@Controller("mmarkertRevokeTpayAction")
@Scope("prototype")
public class MobileMarketRevokeTpayAction extends AbsBaseAction<MobileShopMsgVo, MobileShopMsgVo> {
	private MobileMarketMessageLogger marketOperLogger = MobileMarketMessageLogger.getLogger(this.getClass());
	private final Logger marketLogger = LoggerFactory.getLogger("MMARKET_FILE");
	//前线库--new
	@Autowired
	private ITpayCsysTxnLogService tpayCsysTxnLogService;
	//后线库 --old
	@Autowired
	private ITpayCsysTxnLogHisService tpayCsysTxnLogHisService;
	@Autowired
	private IUpayBatParamService upayBatParamService;
	@Autowired
	private SendCrmJmsMessageImpl sendCrmJmsMessage;
	@Autowired
	private SendTpayJmsMessageImpl sendTpayJmsMessage;
	@Value("${REVOKE_PATH}")
	private String revokePath;//银联的撤销、退货交易地址
	
	private static final String REVERSE_CODE = "012056,012058";//冲正
	private static final String REFUND_CODE = "012057,012059";//退费
	private static final String REFUND_PAY_CODE = "012059"; //支付-退费
	private static final String PAY_CODE = "012058,012059";//支付类业务
	private static final String RECHARGE_CODE = "012056,012057";//缴费类业务
	private static final String INFO = "INFO";
	private static final String WARN = "WARN";
	private static final String DEBUG = "DEBUG";
	private static final String ERROR = "ERROR";
	private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	
	private static final String version = "3.0.0";
	private static final String encoding = "UTF-8";
	private static final String txnSubType = "00";
	private static final String bizType = "000000";
	private static final String currencyCode = "156";
	private static final String reconciliationFlag = "tpaydz";//移动商城接入银联对账参数
	private static final String merType = "0";//普通商户

	
	@Override
	public MobileShopMsgVo execute(MobileShopMsgVo msgVo)
			throws AppBizException {
		//日志
		marketLogger.debug("MobileMarketRevokeTpayAction execute(Object) - start");
		
		// 请求报文报文头
		MobileShopMsgVo reqMsg = msgVo;
		//请求报文体
		MobileShopRevokeMsgReqVo reqBody = new MobileShopRevokeMsgReqVo();
		//响应报文体
		MobileShopRevokeMsgRspVo resBody = new MobileShopRevokeMsgRspVo();
		//响应报文头
		MobileShopMsgVo resMsg = new MobileShopMsgVo() ;
		
		//移动商城缴费、支付退费查询新表  (前线、后线公用一个bean)
		TpayCsysTxnLog txnLog = null;
		//流水保存到新表
		TpayCsysTxnLog tpayLog = new TpayCsysTxnLog();
		
		String transIDH = null;
		
		//第三方支付机构号   默认银联
		String thrOrgCode = CommonConstant.BankOrgCode.TPAY.getValue();
		
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
			
			
			//缴费、支付-冲正(查询前线库)、缴费、支付-退费（查询后线库）
			if(REVERSE_CODE.contains(activityCode)){
				txnLog = tpayCsysTxnLogService.findObj(param2);
			}else if(REFUND_CODE.contains(activityCode)){
				txnLog = tpayCsysTxnLogHisService.findHisStlObj(param2);
			}
			
			//原交易是否存在
			if(txnLog == null){
				printLog("原交易不存在", reqBody.getOriOrderID(), reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(RspCodeConstant.Market.MARKET_014A05.getValue(), RspCodeConstant.Market.MARKET_014A05.getDesc(),
						CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				return resMsg;
			}
			
			//支付-退费需要校验payment值    0<payment<=txnLog.getPayAmt()
			if(REFUND_PAY_CODE.equalsIgnoreCase(activityCode)){
				String payment = StringUtils.isBlank(reqBody.getPayment())?"0":reqBody.getPayment();
				if(Integer.parseInt(payment) <=0 || Integer.parseInt(payment) > txnLog.getPayAmt()){
					printLog("报文体校验失败", "019A51:Payment参数不正确", reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog("019A51", "Payment参数不正确",CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					return resMsg;
				}
				
				//校验第三方支付机构字段
				if(StringUtils.isBlank(reqBody.getOriBankID()) || !reqBody.getOriBankID().matches("\\d{4}")){
					printLog("报文体校验失败", "019A30:OriBankID参数不正确", reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog("019A30", "OriBankID参数不正确",CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					return resMsg;
				}else{
					thrOrgCode = reqBody.getOriBankID();
				}
			}
			
			
			//结算日期
			String settleDate = reqBody.getOriReqDate();

			//原交易流水
			String oriReqTransId = txnLog.getReqTransId();
			
			//缴费业务需判断原交易是否发送到省、是否成功(正在处理中、失败)|银联如果有交易流水则表明已收到到银联响应
			String forwardOrg = txnLog.getRcvDomain();//转发方机构代码
			if(RECHARGE_CODE.contains(activityCode) /*&& StringUtils.isBlank(forwardOrg)*/){
				if(CommonConstant.TxnStatus.InitStatus.getValue().equalsIgnoreCase(txnLog.getStatus())){
					printLog("原交易未发送到省充值", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog(RspCodeConstant.Market.MARKET_014A08.getValue(), RspCodeConstant.Market.MARKET_014A08.getDesc(),
							CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					return resMsg;
				}else if(CommonConstant.TxnStatus.TxnFail.getValue().equalsIgnoreCase(txnLog.getStatus())){
					printLog("原交易失败", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog(RspCodeConstant.Market.MARKET_013A04.getValue(), RspCodeConstant.Market.MARKET_013A04.getDesc(),
							CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					return resMsg;
				}
			}else if(PAY_CODE.contains(activityCode)){
				if(CommonConstant.TxnStatus.TxnFail.getValue().equalsIgnoreCase(txnLog.getStatus())){
					printLog("原交易失败", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog(RspCodeConstant.Market.MARKET_013A04.getValue(), RspCodeConstant.Market.MARKET_013A04.getDesc(),
							CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					return resMsg;
				}
			}
			
			//添加原交易中充值信息到退费交易中
			addInitLgs(tpayLog, txnLog, reqBody);
			
			//是否当日交易   通过settledate作为原交易时间
			if(!intTxnDate.equals(settleDate) && REVERSE_CODE.contains(activityCode)){
				printLog("非当日交易不允许冲正", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(RspCodeConstant.Market.MARKET_013A05.getValue(), RspCodeConstant.Market.MARKET_013A05.getDesc(),
						CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				return resMsg;
			}
			
			//退费需隔日
			if(intTxnDate.equals(settleDate) && REFUND_CODE.contains(activityCode)){
				printLog("当日交易不允许退费", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(RspCodeConstant.Market.MARKET_013A02.getValue(), RspCodeConstant.Market.MARKET_013A02.getDesc(),
						CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				return resMsg;
			}
			
			//超过时限，不允许退费
			if(REFUND_CODE.contains(activityCode)){
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
			String reverseFlag = txnLog.getReverseFlag();
			if(CommonConstant.YesOrNo.Yes.getValue().equalsIgnoreCase(reverseFlag)){
				printLog("原交易已冲正", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(RspCodeConstant.Market.MARKET_013A14.getValue(), RspCodeConstant.Market.MARKET_013A14.getDesc(),
						CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				return resMsg;
			}
			
			//原交易是否已完成退费(退费需判断)
			String refundFlag = txnLog.getRefundFlag();
			if(CommonConstant.YesOrNo.Yes.getValue().equalsIgnoreCase(refundFlag) && REFUND_CODE.contains(activityCode)){
				printLog("原交易已退费", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(RspCodeConstant.Market.MARKET_013A15.getValue(), RspCodeConstant.Market.MARKET_013A15.getDesc(),
						CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				
				return resMsg;
				
			}
			
			//原交易是否已完成对账（退费需判断）
			if(REFUND_CODE.contains(activityCode)){
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
			
			//校验权限  只判断移动商场到银联的权限
			String idValue = txnLog.getIdValue();
			int phoneType = 0;
			if(PAY_CODE.contains(activityCode)){
				phoneType = CommonConstant.PhoneNumType.CHINA_MOBILE.getType();
			}else if(RECHARGE_CODE.contains(activityCode)){
				ProvincePhoneNum provincePhoneNum = findProvinceByMobileNumber(idValue);
				phoneType = provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType();
			}
			
			String  orgFlag = offOrgTrans(reqMsg.getReqSys(), thrOrgCode, transCode, phoneType);
			if(orgFlag != null){
				printLog("此业务渠道无此权限", thrOrgCode, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(RspCodeConstant.Market.MARKET_013A18.getValue(), RspCodeConstant.Market.MARKET_013A18.getDesc(),
						CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				
				return resMsg;
			}
			
			boolean isCrmRspScuuess = false;
			//缴费业务需先发往省，成功后发往银联；支付业务直接发送到银联
			if(RECHARGE_CODE.contains(activityCode)){
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
				crmReqVo.setOriTransactionID(txnLog.getRcvOprId());
				crmReqVo.setOriActionDate(txnLog.getIntTxnDate());
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
				initCrmRspLog(reqMsg, txnLog, tpayLog, forwardOrg, crmMsg);
				
				//封装报文体 body
				CrmReverseMsgResVo crmResVo = new CrmReverseMsgResVo();
				MsgHandle.unmarshaller(crmResVo, (String) crmMsg.getBody());
				
				//CRM应答成功
				if(RspCodeConstant.Wzw.WZW_0000.getValue().equalsIgnoreCase(crmMsg.getRspCode()) && 
						RspCodeConstant.Crm.CRM_0000.getValue().equalsIgnoreCase(crmResVo.getRspCode())){
					isCrmRspScuuess = true;
					//crm应答成功只记录日志，银联应答成功才标识该交易完成
					printLog("CRM应答成功", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
					
				}else{
					isCrmRspScuuess = false;
					String errCode = crmResVo.getRspCode();
					errCode = BankErrorCodeCache.getBankErrCode(errCode);
					String errDesc = RspCodeConstant.Bank.getDescByValue(errCode);
					printLog("CRM应答失败", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					initRspLog(errCode, errDesc,CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					
					return resMsg;
				}
			}
			
			//发往银联
			if(PAY_CODE.contains(activityCode) || (RECHARGE_CODE.contains(activityCode) && isCrmRspScuuess)){
				
				//发送到银联 key-value方式传递
				TpayMsgVo tpayMsg = initTpayData(activityCode, transIDH, txnLog, reqBody, transIDHTime, thrOrgCode);
				//发往银联
				printLog("begin 核心向银联转发冲正(退费)请求", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
				
				tpayMsg = sendTpayJmsMessage.sendMsg(tpayMsg);
				
				printLog("end 核心向银联转发冲正(退费)请求", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
				
				Map<String,String> tpayRspData = tpayMsg.getTpayRspData();
				
				//返回报文体是否为空
				if(null == tpayRspData){
					printLog("冲正(退费)银联返回报文体为空", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog(RspCodeConstant.Market.MARKET_015A07.getValue(), RspCodeConstant.Market.MARKET_015A07.getDesc(),
							CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					
					return resMsg;
				}
				
				//是否超时
				if(RspCodeConstant.Upay.UPAY_U99998.getValue().equalsIgnoreCase(tpayRspData.get(UnionPayMsg.RESPCODE.getValue()))){
					printLog("冲正(退费)银联返回超时", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog(RspCodeConstant.Market.MARKET_015A07.getValue(), RspCodeConstant.Market.MARKET_015A07.getDesc(),
							CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					
					return resMsg;
				}
				
				//初始化流水表银联返回数据
				initTpayRspLog(tpayLog, tpayRspData,thrOrgCode);
				
				//交易成功
				if(UnPayRspCode.UNPAY_00.getValue().equalsIgnoreCase(tpayRspData.get(UnionPayMsg.RESPCODE.getValue()))){
					printLog("银联应答成功", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
					
					resBody.setSettleDate(tpayLog.getIntTxnTime().substring(0, 4) + tpayRspData.get(UnionPayMsg.SETTLEDATE.getValue()));
					initRspLog(RspCodeConstant.Market.MARKET_010A00.getValue(), RspCodeConstant.Market.MARKET_010A00.getDesc(),
							CommonConstant.TxnStatus.TxnSuccess.getValue(), resMsg, resBody, tpayLog);
					
					printLog("更新流水表记录", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);

					//退费 需要修改原交易历史表数据（TPAY_CSYS_TXN_LOG_HIS_STL、TPAY_CSYS_TXN_LOG_HIS）
					if(REFUND_CODE.contains(activityCode)){
						
						addInitRefundHisLog(txnLog);
						printLog("更新流水历史表记录", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
					}else if(REVERSE_CODE.contains(activityCode)){
						
						addInitReverseHisLog(txnLog);
						printLog("更新流水历史表记录", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
					}
				}else{
					String unpayErrCode = tpayRspData.get(UnionPayMsg.RESPCODE.getValue());
					String marketErrCode = TUPayConstant.getMMarketErrorCode(unpayErrCode);
					String marketerrDesc = RspCodeConstant.Market.getDescByValue(marketErrCode);
					printLog("银联应答失败", oriReqTransId, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					initRspLog(marketErrCode, marketerrDesc,CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					
					return resMsg;
				}
				
			}
			
			marketLogger.debug("MobileMarketRevokeTpayAction execute(Object) - end");
			return resMsg;
			
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

	private void initTpayRspLog(TpayCsysTxnLog tpayLog,
			Map<String, String> tpayRspData,String thrOrgCode) {
		tpayLog.setThrVersion(tpayRspData.get(UnionPayMsg.VERSION.getValue()));
		tpayLog.setThrTxnType(tpayRspData.get(UnionPayMsg.TXNTYPE.getValue()));
		tpayLog.setThrSubTxnType(tpayRspData.get(UnionPayMsg.TXNSUBTYPE.getValue()));
		tpayLog.setThrProductType(tpayRspData.get(UnionPayMsg.BIZTYPE.getValue()));
		tpayLog.setAccessType(tpayRspData.get(UnionPayMsg.ACCESSTYPE.getValue()));
		tpayLog.setSettleDate(tpayLog.getIntTxnTime().substring(0, 4) + tpayRspData.get(UnionPayMsg.SETTLEDATE.getValue()));
		tpayLog.setSettleAmt(Integer.parseInt(StringUtils.isBlank(tpayRspData.get(UnionPayMsg.SETTLEAMT.getValue()))?"0":tpayRspData.get(UnionPayMsg.SETTLEAMT.getValue())));
		tpayLog.setTraceNo(tpayRspData.get(UnionPayMsg.TRACENO.getValue()));
		tpayLog.setTraceTime(tpayRspData.get(UnionPayMsg.TRACETIME.getValue()));
		//银联返回的是自己的merId，流水表中需要存放省代码   4位
		TpayCsysMerCodeInfo merInfo = MerCodeCache.getMerInfoByMerId(tpayRspData.get(UnionPayMsg.MERID.getValue()),thrOrgCode);
		tpayLog.setMerId(merInfo.getOrgCode());
		tpayLog.setThrMerId(tpayRspData.get(UnionPayMsg.MERID.getValue()));
		tpayLog.setThrTransId(tpayRspData.get(UnionPayMsg.QUERYID.getValue()));
		
	}

	/**
	 * 初始化省返回数据
	 * @param reqMsg
	 * @param txnLog
	 * @param tpayLog
	 * @param forwardOrg
	 * @param crmMsg
	 */
	private void initCrmRspLog(MobileShopMsgVo reqMsg, TpayCsysTxnLog txnLog,
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
		tpayLog.setRcvCnlType(txnLog.getRcvCnlType());
		tpayLog.setRcvStartTm(reqMsg.getRcvDateTime());
		tpayLog.setRcvEndTm(crmMsg.getTransIDHTime());
	}
	
	/**
	 * 银联请求参数封装
	 * @param activityCode
	 * @param txnLog
	 * @return
	 */
	private TpayMsgVo initTpayData(String activityCode,String transCode,TpayCsysTxnLog txnLog,MobileShopRevokeMsgReqVo reqBody,String transIDHTime,String thrOrgCode){
		TpayMsgVo tpayMsgVo = new TpayMsgVo();
		
		String txnType = REVERSE_CODE.contains(activityCode) ? TpayType.TPAYTYPE_31.getValue() : (REFUND_CODE.contains(activityCode) ? TpayType.TPAYTYPE_04.getValue() : "");
		
		//第三方支付机构代码  
		//TpayCsysMerCodeInfo merInfo = MerCodeCache.getMerInfoByOrgCode(txnLog.getMerId(), CommonConstant.BankOrgCode.TPAY.getValue(), txnLog.getPayStatus());
		
		Map<String, String> data = new HashMap<String, String>();
		// 版本号               从3.0.0开始
		data.put("version", version);
		// 字符集编码
		data.put("encoding", encoding);
		// 交易类型           冲正(撤销)31 退费(退货)04 
		data.put("txnType", txnType);
		// 交易子类型       默认:00
		data.put("txnSubType", txnSubType);
		// 业务类型           默认:000000
		data.put("bizType", bizType);
		// 后台通知地址
		data.put("backUrl", "");
		// 接入类型           商户:0 收单:1
		data.put("accessType", AccessType.ACCESSTYPE_0.getValue());
		// 商户类型
		data.put("merType", merType);
		// 商户号码
		data.put("merId", txnLog.getThrMerId());
		// 二级商户代码    当前接入普通商户，sub**不填
		data.put("subMerId", "");
		// 二级商户全称
		data.put("subMerName", "");
		// 二级商户简称
		data.put("subMerAbbr", "");
		// 订单号
		data.put("orderId", reqBody.getOrderId());
		// 订单发送时间
		data.put("txnTime", transIDHTime);
		// 原始交易流水号 同原始消费交易的queryId 
		data.put("origQryId", txnLog.getThrTransId());
		// 交易金额 分
		data.put("txnAmt", txnLog.getPayAmt().toString());
		// 交易币种
		data.put("currencyCode", currencyCode);
		// 商户摘要
		data.put("merNote", "");
		// 终端号
		data.put("termId", "");
		// 请求方保留域
		data.put("reqReserved", "");
		// 保留域
		data.put("reserved", "");
		if(REFUND_CODE.contains(activityCode)){
			// 终端信息域
			data.put("userMac", "");
			//失败交易前台跳转地址
			data.put("frontFailUrl", "");
		}
		
		
		tpayMsgVo.setTpayReqData(data);
		tpayMsgVo.setMsgReceiver(thrOrgCode);
		tpayMsgVo.setTransIDO(transCode);
		tpayMsgVo.setReqPathAppend(revokePath);
		
		return tpayMsgVo;
	}
	
	/**
	 * 修改历史表退费标识
	 * @param hisStlLog
	 * @param logHis
	 */
	private void addInitRefundHisLog(TpayCsysTxnLog logHis){
		// 修改TPAY_CSYS_TXN_LOG_HIS_STL表
		TpayCsysTxnLog hisStlLog = new TpayCsysTxnLog();
		hisStlLog.setRefundFlag(CommonConstant.YesOrNo.Yes.toString());// 退费标识
		hisStlLog.setBackFlag(CommonConstant.YesOrNo.Yes.toString());// 返销标识
		hisStlLog.setSeqId(logHis.getSeqId());
		hisStlLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		tpayCsysTxnLogHisService.modifyHisStl(hisStlLog);
		
		// 修改TPAY_CSYS_TXN_LOG_HIS表
		TpayCsysTxnLog hisLog = new TpayCsysTxnLog();
		hisLog.setRefundFlag(CommonConstant.YesOrNo.Yes.toString());// 退费标识
		hisLog.setBackFlag(CommonConstant.YesOrNo.Yes.toString());// 返销标识
		hisLog.setSeqId(logHis.getSeqId());
		hisLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		tpayCsysTxnLogHisService.modify(hisLog);
		
	}
	
	/**
	 * 原交易充值信息
	 * @param tpayLog
	 * @param txnLog
	 * @param txnLogHis
	 */
	private void addInitLgs(TpayCsysTxnLog tpayLog,TpayCsysTxnLog txnLog,MobileShopRevokeMsgReqVo reqBody){
		tpayLog.setMainFlag(txnLog.getMainFlag());//主副号码绑定标识
		tpayLog.setMainIdProvince(txnLog.getMainIdProvince());// 主号码归属地
		tpayLog.setMainIdType(txnLog.getMainIdType());// 主号码标识类型
		tpayLog.setMainIdValue(txnLog.getMainIdValue());// 主号码用户号码
		tpayLog.setIdProvince(txnLog.getIdProvince());// 用户号码归属地
		tpayLog.setIdType(txnLog.getIdType());// 用户号码标识类型
		tpayLog.setIdValue(txnLog.getIdValue());// 用户号码
		tpayLog.setSignStatus(txnLog.getSignStatus());// 签约状态
		tpayLog.setBankId(txnLog.getBankId());// 银行编号
		tpayLog.setBankAcctType(txnLog.getBankAcctType());// 银行帐号类型
		tpayLog.setBankAccId(txnLog.getBankAccId());// 银行帐号
		
		tpayLog.setNeedPayAmt(txnLog.getNeedPayAmt());// 应缴金额
		//支付退费需要记录报文中传入的退款金额(后续支持多次退费)，其他均为整笔回退   
		if(REFUND_PAY_CODE.contains(tpayLog.getReqActivityCode()) && StringUtils.isNotBlank(reqBody.getPayment())){
			tpayLog.setPayAmt(-Long.valueOf(reqBody.getPayment()));
		}else{
			tpayLog.setPayAmt(-txnLog.getPayAmt());
		}
		
		tpayLog.setOriOrgId(txnLog.getReqDomain());//原交易发起机构
		tpayLog.setOriOprTransId(txnLog.getReqTransId());//原交易流水
		tpayLog.setOriReqDate(txnLog.getReqTransDt());//原交易发起时间
		tpayCsysTxnLogService.modify(tpayLog);
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
			marketOperLogger.warn("{}:{}, 请求交易流水号:{} ,内部交易流水号:{},发起方:{}",
					new Object[] { display,tag, reqTransId, innerTransId, reqSys});
			marketLogger.warn("{}:{}, 请求交易流水号:{} ,内部交易流水号:{},发起方:{}",
					new Object[] { display,tag, reqTransId, innerTransId, reqSys});
		}else if(INFO.equalsIgnoreCase(flag)){
			marketOperLogger.info("{}:{}, 请求交易流水号:{} ,内部交易流水号:{},发起方:{}",
					new Object[] { display,tag, reqTransId, innerTransId, reqSys});
			marketLogger.info("{}:{}, 请求交易流水号:{} ,内部交易流水号:{},发起方:{}",
					new Object[] { display,tag, reqTransId, innerTransId, reqSys});
		}if(DEBUG.equalsIgnoreCase(flag)){
			marketOperLogger.debug("{}:{}, 请求交易流水号:{} ,内部交易流水号:{},发起方:{}",
					new Object[] { display,tag, reqTransId, innerTransId, reqSys});
			marketLogger.debug("{}:{}, 请求交易流水号:{} ,内部交易流水号:{},发起方:{}",
					new Object[] { display,tag, reqTransId, innerTransId, reqSys});
		}if(ERROR.equalsIgnoreCase(flag)){
			marketOperLogger.error("{}:{}, 请求交易流水号:{} ,内部交易流水号:{},发起方:{}",
					new Object[] { display,tag, reqTransId, innerTransId, reqSys});
			marketLogger.error("{}:{}, 请求交易流水号:{} ,内部交易流水号:{},发起方:{}",
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
	 * 修改原交易冲正标示
	 * @param logHis
	 */
	private void addInitReverseHisLog(TpayCsysTxnLog logHis){
		TpayCsysTxnLog hisLog = new TpayCsysTxnLog();
		hisLog.setReverseFlag(CommonConstant.YesOrNo.Yes.toString());// 冲正标识
		hisLog.setBackFlag(CommonConstant.YesOrNo.Yes.toString());// 返销标识
		hisLog.setSeqId(logHis.getSeqId());
		hisLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		tpayCsysTxnLogService.modify(hisLog);
	}

}
