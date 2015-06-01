package com.huateng.cmupay.thrid.action;

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
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.constant.TUPayConstant;
import com.huateng.cmupay.constant.TUPayConstant.UnPayRspCode;
import com.huateng.cmupay.constant.TUPayConstant.UnionPayMsg;
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.controller.cache.MerCodeCache;
import com.huateng.cmupay.controller.cache.SysMapCache;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.message.SendCrmJmsMessageImpl;
import com.huateng.cmupay.jms.message.SendTpayJmsMessageImpl;
import com.huateng.cmupay.logFormat.MobileMarketMessageLogger;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.TpayCsysMerCodeInfo;
import com.huateng.cmupay.models.TpayCsysTxnLog;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopCertificationPayReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopCertificationPayRspVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.TpayMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmChargeReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmChargeResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.cmupay.utils.StringFormat;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;

/**
 * （移动商城）银联认证支付、缴费action
 *
 */
@Controller("mmarkertCertificationPayAction")
@Scope("prototype")
public class MobileMarketCertificationPayAction extends AbsBaseAction<MobileShopMsgVo, MobileShopMsgVo> {

	private MobileMarketMessageLogger marketOperLogger = MobileMarketMessageLogger.getLogger(this.getClass());
	private final Logger marketLogger = LoggerFactory.getLogger("MMARKET_FILE");
	
	//前线库--new
	@Autowired
	private ITpayCsysTxnLogService tpayCsysTxnLogService;
	
	@Autowired
	private SendCrmJmsMessageImpl sendCrmJmsMessage;
	@Autowired
	private SendTpayJmsMessageImpl sendTpayJmsMessage;
	
	private static final String INFO = "INFO";
	private static final String WARN = "WARN";
	private static final String DEBUG = "DEBUG";
	private static final String ERROR = "ERROR";
	
	@Value("${PAYBACK_PATH}")
	private String payBackPath;//银联认证支付、缴费交易地址
	
	@Override
	public MobileShopMsgVo execute(MobileShopMsgVo msgVo)
			throws AppBizException {
		
		//日志
		marketLogger.debug("MobileMarketCertificationPayAction execute(Object) - start");
		
		// 请求报文报文头
		MobileShopMsgVo reqMsg = msgVo;
		//请求报文体
		MobileShopCertificationPayReqVo reqBody = new MobileShopCertificationPayReqVo();
		//应答报文体
		MobileShopCertificationPayRspVo resBody = new MobileShopCertificationPayRspVo();
		//响应报文头
		MobileShopMsgVo resMsg = new MobileShopMsgVo();
		
		//流水保存到新表
		TpayCsysTxnLog tpayLog = new TpayCsysTxnLog();
		
		String transIDH = null;
		
		try {
			//请求报文xml转化为bean
			MsgHandle.unmarshaller(reqBody, (String) reqMsg.getBody());
			reqMsg.setBody(reqBody);
			//设置应答报文体默认内容
			resBody.setMerID(reqBody.getMerID());
			resBody.setOrderID(reqBody.getOrderID());
			resBody.setOrderTime(reqBody.getOrderTime());
			resBody.setBankAcctID(reqBody.getBankAcctID());
			resBody.setPayment(reqBody.getPayment());
			resBody.setSettleDate(reqMsg.getReqDate());
			resBody.setSettleOrg(CommonConstant.BankOrgCode.TPAY.getValue());//默认银联
			
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
			
			//缴费、支付必填字段校验
			if(reqBody.getOrderType().equalsIgnoreCase(CommonConstant.payStatus.UPAY_STATUS.getValue())){
				//reqBody.setSubMerId(null);
				
				if(null == reqBody.getChargeMoney() || reqBody.getChargeMoney() < 0){
					printLog("报文体校验失败", "019A52:ChargeMoney参数不正确", reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog("019A52", "ChargeMoney参数不正确",CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					return resMsg;
				}
				tpayLog.setChargeMoney(Integer.parseInt(String.valueOf(reqBody.getChargeMoney())));
				
				if(StringUtils.isBlank(reqBody.getIDValue())){
					printLog("报文体校验失败", "019A18:IDValue格式不正确", reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog("019A18", "IDValue格式不正确",CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					return resMsg;
				}
				tpayLog.setIdValue(reqBody.getIDValue());
				
				if(StringUtils.isBlank(reqBody.getIDType())){
					printLog("报文体校验失败", "019A17:IDType参数不正确", reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog("019A17", "IDType参数不正确",CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					return resMsg;
				}
				tpayLog.setIdType(reqBody.getIDType());
				tpayLog.setMainFlag(null);
				
				if(StringUtils.isBlank(reqBody.getHomeProv())){
					printLog("报文体校验失败", "019A44:HomeProv参数不正确", reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog("019A44", "HomeProv参数不正确",CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					return resMsg;
				}
				
			}else if(reqBody.getOrderType().equalsIgnoreCase(CommonConstant.payStatus.TUPAY_STATUS.getValue())){
				//清空支付不需要的值
				tpayLog.setIdValue(null);
				tpayLog.setIdType(null);
				tpayLog.setChargeMoney(null);
				
				if(StringUtils.isBlank(reqBody.getIDType())){
					printLog("报文体校验失败", "x:SubMerId参数不正确", reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog("x", "SubMerId参数不正确",CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					return resMsg;
				}
			}
			
			String idValue = reqBody.getIDValue();
			int phoneType = 0;
			String chlFlag = null;
			String forwardOrg = null;
			
			ProvincePhoneNum provincePhoneNum = null;
			
			if(reqBody.getOrderType().equalsIgnoreCase(CommonConstant.payStatus.UPAY_STATUS.getValue())){
				
				provincePhoneNum = findProvinceByMobileNumber(idValue);
				
				//手机号码归属地校验
				String idProvince = provincePhoneNum == null ? null : provincePhoneNum.getProvinceCode();
				if(null == idProvince){
					printLog("手机号码不正确", chlFlag, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog(RspCodeConstant.Market.MARKET_012A11.getValue(), RspCodeConstant.Market.MARKET_012A11.getDesc(),
							CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					
					return resMsg;
				}
				
				//手机号码归属地是否与归属省一致
				forwardOrg = SysMapCache.getProvCd(idProvince).getSysCd();
				if(!reqBody.getHomeProv().equals(idProvince)){
					printLog("省代码和手机号码归属省不一致", chlFlag, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog(RspCodeConstant.Market.MARKET_014A10.getValue(), RspCodeConstant.Market.MARKET_014A10.getDesc(),
							CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					
					return resMsg;
				}
				
			}
			
			
			//校验权限   支付交易不用发送到省，只校验移动商城到银联的权限；缴费交易直接校验移动商城到省的权限
			if(reqBody.getOrderType().equalsIgnoreCase(CommonConstant.payStatus.UPAY_STATUS.getValue())){
				
				phoneType = provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType();
				chlFlag = forwardOrg;
				
			}else if(reqBody.getOrderType().equalsIgnoreCase(CommonConstant.payStatus.TUPAY_STATUS.getValue())){
				
				phoneType = CommonConstant.PhoneNumType.CHINA_MOBILE.getType();
				chlFlag = CommonConstant.BankOrgCode.TPAY.getValue();
				
			}
			
			String  orgFlag = offOrgTrans(reqMsg.getReqSys(), chlFlag, transCode, phoneType);
			if(orgFlag != null){
				printLog("此业务渠道无此权限", chlFlag, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(RspCodeConstant.Market.MARKET_013A18.getValue(), RspCodeConstant.Market.MARKET_013A18.getDesc(),
						CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				
				return resMsg;
			}
			
			//支付直接发往银联、缴费先发往银联再发往省
			boolean isTpayRspScuuess = false;//银联是否应答成功
			//初始化发送到银联数据 key-value方式传递
			TpayMsgVo tpayMsg = initTpayData(transCode, reqBody);
			
			//发往银联
			printLog("begin 核心向银联转发缴费(支付)请求", reqBody.getOrderID(), reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
			
			tpayMsg = sendTpayJmsMessage.sendMsg(tpayMsg);
			
			printLog("end 核心向银联转发缴费(支付)请求", reqBody.getOrderID(), reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
			
			Map<String,String> tpayRspData = tpayMsg.getTpayRspData();
			
			//返回报文体是否为空
			if(null == tpayRspData){
				printLog("缴费(支付)银联返回报文体为空", reqBody.getOrderID(), reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(RspCodeConstant.Market.MARKET_015A07.getValue(), RspCodeConstant.Market.MARKET_015A07.getDesc(),
						CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				
				return resMsg;
			}
			
			//是否超时
			if(RspCodeConstant.Upay.UPAY_U99998.getValue().equalsIgnoreCase(tpayRspData.get(UnionPayMsg.RESPCODE.getValue()))){
				printLog("缴费(支付)银联返回超时", reqBody.getOrderID(), reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				
				initRspLog(RspCodeConstant.Market.MARKET_015A07.getValue(), RspCodeConstant.Market.MARKET_015A07.getDesc(),
						CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				
				return resMsg;
			}
			
			//初始化流水表银联返回数据
			initTpayRspLog(tpayLog, tpayRspData);
			
			//交易成功
			if(UnPayRspCode.UNPAY_00.getValue().equalsIgnoreCase(tpayRspData.get(UnionPayMsg.RESPCODE.getValue()))){
				
				isTpayRspScuuess = true;
				resBody.setSettleDate(tpayLog.getIntTxnTime().substring(0, 4) + tpayRspData.get(UnionPayMsg.SETTLEDATE.getValue()));
				//银联应答成功不更新库，等待省端应答
				printLog("银联应答成功", reqBody.getOrderID(), reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
				
				//支付交易到此结束
				if(reqBody.getOrderType().equalsIgnoreCase(CommonConstant.payStatus.TUPAY_STATUS.getValue())){
					initRspLog(RspCodeConstant.Market.MARKET_010A00.getValue(), RspCodeConstant.Market.MARKET_010A00.getDesc(),
							CommonConstant.TxnStatus.TxnSuccess.getValue(), resMsg, resBody, tpayLog);
					
					printLog("更新流水表记录", reqBody.getOrderID(), reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
				}
				
			}else{
				
				isTpayRspScuuess = false;
				String unpayErrCode = tpayRspData.get(UnionPayMsg.RESPCODE.getValue());
				String marketErrCode = TUPayConstant.getMMarketErrorCode(unpayErrCode);
				String marketerrDesc = RspCodeConstant.Market.getDescByValue(marketErrCode);
				printLog("银联应答失败", reqBody.getOrderID(), reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
				initRspLog(marketErrCode, marketerrDesc,CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
				
				return resMsg;
			}
			
			
			//缴费交易银联应答成功后继续发往省充值
			if(reqBody.getOrderType().equalsIgnoreCase(CommonConstant.payStatus.UPAY_STATUS.getValue()) && isTpayRspScuuess){
				//tpayLog.setRcvStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
				//初始化发往省数据
				CrmMsgVo crmMsg = initCrmReqData(tpayLog,transCodeInfo,forwardOrg);
				
				printLog("begin 核心向CRM转发缴费(支付)请求", forwardOrg, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
				//发往省冲正
				crmMsg = sendCrmJmsMessage.sendMsg(crmMsg);
				
				printLog("end 核心向CRM转发缴费(支付)请求", forwardOrg, reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
				
				
				//返回报文体是否为空
				if(null == crmMsg.getBody()){
					printLog("缴费(支付)省返回报文体为空", reqBody.getOrderID(), reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog(RspCodeConstant.Market.MARKET_015A07.getValue(), RspCodeConstant.Market.MARKET_015A07.getDesc(),
							CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					
					return resMsg;
				}
				
				//是否超时
				if(RspCodeConstant.Upay.UPAY_U99998.getValue().equalsIgnoreCase(crmMsg.getRspCode())){
					printLog("缴费(支付)省返回超时", reqBody.getOrderID(), reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					
					initRspLog(RspCodeConstant.Market.MARKET_015A07.getValue(), RspCodeConstant.Market.MARKET_015A07.getDesc(),
							CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					
					return resMsg;
				}
				
				//初始化流水表省返回落地方参数  rcv
				initCrmRspData(tpayLog, forwardOrg, crmMsg);
				
				//封装报文体 body
				CrmChargeResVo crmResVo = new CrmChargeResVo();
				MsgHandle.unmarshaller(crmResVo, (String) crmMsg.getBody());
				
				//CRM应答成功
				if(RspCodeConstant.Wzw.WZW_0000.getValue().equalsIgnoreCase(crmMsg.getRspCode()) && 
						RspCodeConstant.Crm.CRM_0000.getValue().equalsIgnoreCase(crmResVo.getRspCode())){

					printLog("CRM应答成功", reqBody.getOrderID(), reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
					
					initRspLog(RspCodeConstant.Market.MARKET_010A00.getValue(), RspCodeConstant.Market.MARKET_010A00.getDesc(),
							CommonConstant.TxnStatus.TxnSuccess.getValue(), resMsg, resBody, tpayLog);
					
					printLog("更新流水表记录", reqBody.getOrderID(), reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), INFO);
					
				}else{
					
					String errCode = crmResVo.getRspCode();
					errCode = BankErrorCodeCache.getBankErrCode(errCode);
					String errDesc = RspCodeConstant.Bank.getDescByValue(errCode);
					printLog("CRM应答失败", reqBody.getOrderID(), reqMsg.getReqTransID(), transIDH, reqMsg.getReqSys(), WARN);
					initRspLog(errCode, errDesc,CommonConstant.TxnStatus.TxnFail.getValue(), resMsg, resBody, tpayLog);
					
					return resMsg;
				}
			}

			marketLogger.debug("MobileMarketCertificationPayAction execute(Object) - end");
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
	 * 初始化省应答数据
	 * @param reqMsg
	 * @param tpayLog
	 * @param forwardOrg
	 * @param crmMsg
	 */
	private void initCrmRspData(TpayCsysTxnLog tpayLog, String forwardOrg, CrmMsgVo crmMsg) {
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
		tpayLog.setRcvCnlType(tpayLog.getReqCnlType());
		tpayLog.setRcvStartTm(tpayLog.getIntTxnTime());
		tpayLog.setRcvEndTm(crmMsg.getTransIDHTime());
	}

	/**
	 * 初始化发往省数据
	 * @param reqMsg
	 * @param reqBody
	 * @param tpayLog
	 * @param transIDH
	 * @param transIDHTime
	 * @param transCodeInfo
	 * @param idValue
	 * @param forwardOrg
	 * @return
	 */
	private CrmMsgVo initCrmReqData(TpayCsysTxnLog tpayLog,UpayCsysTransCode transCodeInfo, String forwardOrg) {
		CrmMsgVo crmMsg = new CrmMsgVo();
		crmMsg.setTransCode(transCodeInfo);
		crmMsg.setVersion(ExcConstant.CRM_VERSION);
		crmMsg.setTestFlag(testFlag);
		crmMsg.setBIPCode(CommonConstant.Bip.Biz22.getValue());
		crmMsg.setActivityCode(CommonConstant.CrmTrans.Crm07.getValue());
		crmMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
		crmMsg.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
		crmMsg.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
		crmMsg.setRouteType(CommonConstant.RouteType.RoutePhone.getValue());
		crmMsg.setRouteValue(tpayLog.getIdValue());
		crmMsg.setSessionID(tpayLog.getIntTxnSeq()); 
		crmMsg.setTransIDO(tpayLog.getIntTxnSeq());
		crmMsg.setTransIDOTime(StrUtil.subString(tpayLog.getIntTxnTime(),0, 14));
		crmMsg.setMsgSender(CommonConstant.BankOrgCode.CMCC.getValue());
		crmMsg.setMsgReceiver(forwardOrg);
		
		CrmChargeReqVo crmReqVo = new CrmChargeReqVo();
		crmReqVo.setIDType(tpayLog.getIdType());
		crmReqVo.setIDValue(tpayLog.getIdValue());
		crmReqVo.setTransactionID(Serial.genSerialNum(CommonConstant.Sequence.OprId.toString()));
		crmReqVo.setBusiTransID(tpayLog.getReqTransId());
		crmReqVo.setPayTransID(tpayLog.getPayTransId());//银联扣款流水
		crmReqVo.setActionDate(tpayLog.getIntTxnDate());
		crmReqVo.setActionTime(StrUtil.subString(tpayLog.getIntTxnTime(), 0, 14));
		crmReqVo.setChargeMoney(String.valueOf(tpayLog.getChargeMoney()));
		crmReqVo.setOrganID(tpayLog.getReqDomain());
		crmReqVo.setCnlTyp(tpayLog.getReqCnlType());
		crmReqVo.setPayedType(CommonConstant.PayType.PayPre.getValue());
		crmReqVo.setSettleDate(tpayLog.getSettleDate());
		crmReqVo.setOrderNo(tpayLog.getOrderId());
		crmReqVo.setProductNo(tpayLog.getProductNo());
		crmReqVo.setPayment(String.valueOf(tpayLog.getPayAmt()));
		crmReqVo.setOrderCnt(String.valueOf(tpayLog.getOrderCnt()));
		crmReqVo.setCommision(String.valueOf(tpayLog.getCommision()));
		crmReqVo.setRebateFee(String.valueOf(tpayLog.getRebateFee()));
		crmReqVo.setProdDiscount(String.valueOf(tpayLog.getProdDiscount()));
		crmReqVo.setCreditCardFee(String.valueOf(tpayLog.getCreditCardFee()));
		crmReqVo.setServiceFee(String.valueOf(tpayLog.getServiceFee()));
		crmReqVo.setActivityNo(tpayLog.getActivityNo());
		crmReqVo.setProductShelfNo(tpayLog.getProductShelfNo());
		
		crmMsg.setBody(crmReqVo);
		return crmMsg;
	}
	
	/**
	 * 设置银联应答参数
	 * @param tpayLog
	 * @param tpayRspData
	 */
	private void initTpayRspLog(TpayCsysTxnLog tpayLog,
			Map<String, String> tpayRspData) {
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
		TpayCsysMerCodeInfo merInfo = MerCodeCache.getMerInfoByMerId(tpayRspData.get(UnionPayMsg.MERID.getValue()),CommonConstant.BankOrgCode.TPAY.getValue());
		tpayLog.setMerId(merInfo.getOrgCode());
		tpayLog.setThrMerId(tpayRspData.get(UnionPayMsg.MERID.getValue()));
		tpayLog.setThrTransId(tpayRspData.get(UnionPayMsg.QUERYID.getValue()));//第三方支付流水
		tpayLog.setPayTransId(tpayRspData.get(UnionPayMsg.QUERYID.getValue())); //银行扣款流水号
	}
	
	/**
	 * 银联请求参数封装
	 * @param activityCode
	 * @param txnLog
	 * @return
	 */
	private TpayMsgVo initTpayData(String transCode,MobileShopCertificationPayReqVo reqBody){
		TpayMsgVo tpayMsgVo = new TpayMsgVo();
		
		//第三方支付机构代码  
		TpayCsysMerCodeInfo merInfo = MerCodeCache.getMerInfoByOrgCode(reqBody.getMerID(), CommonConstant.BankOrgCode.TPAY.getValue(), reqBody.getOrderType(),reqBody.getShopMerId());
		
		//银行代码表
		/*TUpayBankThrCode tUpayBankThrCode = new TUpayBankThrCode();
		tUpayBankThrCode.setThrOrgId(CommonConstant.BankOrgCode.TPAY.getValue());
		tUpayBankThrCode.setBankId(reqBody.getBankID());
		tUpayBankThrCode = BankThrCodeCache.getBankThrCode(tUpayBankThrCode);*/
		
		Map<String, String> data = new HashMap<String, String>();
		
		// 01版本号
		data.put("version", "3.0.0");
		// 02编码方式
		data.put("encoding", "UTF-8");
		// 05交易类型  取值 01
		data.put("txnType", "01");
		// 06交易子类 01 自助消费
		data.put("txnSubType", "01");
		// 07产品类型
		data.put("bizType", "000301");
		// 08消息通知地址
		data.put("frontUrl", "");
		// 09后台通知地址
		data.put("backUrl", "");
		// 10接入商户类型  0 商户直接接入
		data.put("accessType", "0");
		// 11商户类型  0 普通商户
		data.put("merType", "0");
		// 12商户代码
		data.put("merId", merInfo.getMerId());
		// 13二级商户代码
		data.put("subMerId", "");
		// 14二级商户名称
		data.put("subMerName", "");
		// 15二级商户简称
		data.put("subMerAbbr", "");
		// 16商户订单号
		data.put("orderId", reqBody.getOrderID());
		// 17订单发送时间
		data.put("txnTime", reqBody.getOrderTime());
		// 18账号类型
		data.put("accType", "01");
		// 19账号    加密（data.put("accNo",MpiUtil.encrptPan(reqBody.getBankAcctID(), "UTF-8"));）
		data.put("accNo", reqBody.getBankAcctID());
		// 20交易金额
		data.put("txnAmt", String.valueOf(reqBody.getPayment()));
		// 21币种
		data.put("currencyCode", "156");
		// 22持卡人身份信息 customerInfo
		data.put("customerInfo", reqBody.getCustomerInfo());
		// 23订单超时时间隔
		data.put("orderTimeoutInterval", "");
		// 24订单支付超时时间
		data.put("payTimeoutTime", reqBody.getPayTimeoutTime());
		// 25默认支付方式defaultPayType
		data.put("defaultPayType", "");
		// 26发卡机构代码
		data.put("issInsCode", "");
		// 27商户摘要
		data.put("merNote", "");
		// 28终端号 termId
		data.put("termId", "");
		// 29终端类型 termType
		data.put("termType", "");
		// 30交互方式 interactMode
		data.put("interactMode", "");
		// 31商户端用户ID merUserId
		data.put("merUserId", "");
		// 32商品风险类别标识：支付001，缴费111
		data.put("shippingFlag", "");
		/*if (CommonConstant.payStatus.TUPAY_STATUS.getValue().equals(
				reqBody.getOrderType())) {
			data.put("shippingFlag", "001");
		} else if (CommonConstant.payStatus.UPAY_STATUS.getValue().equals(
				reqBody.getOrderType())) {
			data.put("shippingFlag", "111");
		}*/
		// 33收货国家代码 shippingCountryCode
		data.put("shippingCountryCode", "");
		// 34收货省代码 shippingProvinceCode
		data.put("shippingProvinceCode", "");
		// 35收货市代码 shippingCityCode
		data.put("shippingCityCode", "");
		// 36收货地区代码 shippingDistrictCode
		data.put("shippingDistrictCode", "");
		// 37收货街道地址 shippingStreet
		data.put("shippingStreet", "");
		// 38商品类别 commodityCategory
		data.put("commodityCategory", "");
		// 39商品名称 commodityName
		data.put("commodityName", "");
		// 40商品URL commodityUrl
		data.put("commodityUrl", "");
		// 41商品单价 commodityUnitPrice
		data.put("commodityUnitPrice", "");
		// 42商品数量 commodityQty
		data.put("commodityQty", "");
		// 43请求方保留域 reqReserved
		data.put("reqReserved", "");
		// 44保留域 reserved
		data.put("reserved", "");
		// 45持卡人IP customerIp
		data.put("customerIp", "");
		// 46商户端用户注册时间 merUserRegDt
		data.put("merUserRegDt", "");
		// 47 商户端用户注册邮箱 merUserEmail
		data.put("merUserEmail", "");
		// 48加密证书ID encryptCertId
		data.put("encryptCertId", "");
		// 49终端信息域 userMac
		data.put("userMac", "");
		// 50 分期付款期数 numberOfInstallments
		data.put("numberOfInstallments", "");
		
		tpayMsgVo.setTpayReqData(data);
		tpayMsgVo.setMsgReceiver(CommonConstant.BankOrgCode.TPAY.getValue());
		tpayMsgVo.setTransIDO(transCode);
		tpayMsgVo.setReqPathAppend(payBackPath);
		
		return tpayMsgVo;
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
	private void initRspLog(String rspCode,String rspDesc,String status,MobileShopMsgVo resMsg,MobileShopCertificationPayRspVo resBody,TpayCsysTxnLog tpayLog){
		
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
	 * 初始化交易流水表
	 * @param txnLog
	 * @param seqId
	 * @param transIDH
	 * @param intTxnDate
	 * @param transIDHTime
	 * @param reqMsg
	 * @param transCode
	 * @param resMsg
	 * @param reqBody
	 * @throws Exception 
	 */
	private void initLog(TpayCsysTxnLog txnLog, Long seqId, String transIDH,
			String intTxnDate, String transIDHTime, MobileShopMsgVo reqMsg,
			UpayCsysTransCode transCode, MobileShopMsgVo resMsg,MobileShopCertificationPayReqVo reqBody ) throws Exception {
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
		
		txnLog.setOrderId(reqBody.getOrderID());
		txnLog.setOrderTm(StrUtil.subString(reqMsg.getReqDateTime(), 0, 14));
		txnLog.setBankAccId(reqBody.getBankAcctID().length() == 4 ? reqBody.getBankAcctID() : StringFormat.encryStr(reqBody.getBankAcctID(), "*", 4, "right"));
		txnLog.setPayAmt(reqBody.getPayment());
		txnLog.setNeedPayAmt(reqBody.getPayment());
		//txnLog.setChargeMoney(Integer.parseInt(String.valueOf(null == reqBody.getChargeMoney() ? 0 :reqBody.getChargeMoney())));
		txnLog.setProductNo(reqBody.getProdID());
		txnLog.setOrderCnt(Integer.parseInt(String.valueOf(null == reqBody.getProdCnt() ? 0 : reqBody.getProdCnt())));
		txnLog.setServiceFee(Integer.parseInt(String.valueOf(null == reqBody.getServiceFee() ? 0 : reqBody.getServiceFee())));
		txnLog.setRebateFee(Integer.parseInt(String.valueOf(null == reqBody.getRebateFee() ? 0 : reqBody.getRebateFee())));
		txnLog.setCommision(Integer.parseInt(String.valueOf(null == reqBody.getCommision() ? 0 : reqBody.getCommision())));
		txnLog.setCreditCardFee(Integer.parseInt(String.valueOf(null == reqBody.getCreditCardFee() ? 0 : reqBody.getCreditCardFee())));
		txnLog.setProdDiscount(Integer.parseInt(String.valueOf(null == reqBody.getProdDiscount() ? 0 : reqBody.getProdDiscount())));
		txnLog.setActivityNo(reqBody.getActivityNO());
		txnLog.setProductShelfNo(reqBody.getProdShelfNO());
		txnLog.setBankId(reqBody.getBankID());
		txnLog.setPayStatus(reqBody.getOrderType());
		
	}

}
