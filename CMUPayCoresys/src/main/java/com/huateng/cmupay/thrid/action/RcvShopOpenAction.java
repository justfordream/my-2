package com.huateng.cmupay.thrid.action;

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
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.constant.TUPayConstant;
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.controller.cache.MerCodeCache;
import com.huateng.cmupay.controller.cache.ProvAreaCache;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogHisService;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.message.SendTpayJmsMessageImpl;
import com.huateng.cmupay.logFormat.MobileMarketMessageLogger;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.TpayCsysMerCodeInfo;
import com.huateng.cmupay.models.TpayCsysTxnLog;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopOpenReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopOpenResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopQueryMsgReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopQueryMsgResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.TpayMsgVo;
import com.huateng.cmupay.utils.StringFormat;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;

@Controller("mmarketTPayOpenAction")
@Scope("prototype")
public class RcvShopOpenAction extends AbsBaseAction<MobileShopMsgVo, MobileShopMsgVo>{
	private MobileMarketMessageLogger marketOperLogger = MobileMarketMessageLogger.getLogger(this.getClass());
	private final Logger marketLogger = LoggerFactory.getLogger("MMARKET_FILE");
	
	@Autowired
	private SendTpayJmsMessageImpl sendTpayJmsMessage;
	
	@Autowired
	private ITpayCsysTxnLogHisService tpayCsysTxnLogHisService;

	@Autowired
	protected ITpayCsysTxnLogService tpayCsysTxnLogService;
	
	private @Value("${OPEN_PATH}") String openPath;
	
	//银行账户
	String bankAcctID;
	//加密后的银行账户
	String mBankAcctID;
	
	@Override
	public MobileShopMsgVo execute(MobileShopMsgVo msgVo)
			throws AppBizException {
		marketLogger.debug("RcvShopOpenAction execute(Object) - start");
		//获取年份
		Date date =new Date();
		int year=date.getYear()+1900;
		//转码类
		TpayCsysTxnLog tTxnLog=new TpayCsysTxnLog();
		MobileShopMsgVo reqMsg = msgVo;
		MobileShopOpenReqVo reqBody = new MobileShopOpenReqVo();
		MobileShopOpenResVo resBody = new MobileShopOpenResVo();
		String transIDH="";
		MobileShopMsgVo resMsg = new MobileShopMsgVo();
		try{
			
			//请求报文xml转化为bean
			MsgHandle.unmarshaller(reqBody, (String) reqMsg.getBody());
			//请求报文体bean中加入请求报文体
			reqMsg.setBody(reqBody);
			//获取平台交易流水号
			transIDH = msgVo.getTxnSeq();
			//获取平台交易时间17位
			String transIDHTime = msgVo.getTxnTime();
			//获取平台交易时间14位
			String trans14IDHTime = transIDHTime.substring(0,transIDHTime.length()-3);
			//平台交易数据库日切日期
			String intTxnDate = msgVo.getTxnDate();
			//交易流水表唯一流水号
			Long seqId = msgVo.getSeqId();
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
			resMsg.setRcvDate(transIDHTime.substring(0, 8));
			resMsg.setRcvDateTime(transIDHTime);
			resMsg.setRcvTransID(transIDH);
			resMsg.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			
			//银行账户
			bankAcctID=reqBody.getBankAcctID();
			//加密后的银行账户
			mBankAcctID=StringFormat.encryStr(bankAcctID, "*", 4, "right");
			
			//内部交易码
			UpayCsysTransCode transCode = msgVo.getTransCode();
			
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
				marketLogger.debug("RcvShopOpenAction execute(Object) - end");
				return resMsg;
			}
			
			/**
			 * BankAcctID和CustomerInfo 至少要有一个不为空
			 * */
			if(reqBody.getBankAcctID()==null&&reqBody.getCustomerInfo()==null){
				marketOperLogger.error("报文格式错误,内部交易流水:{},发起方:{},BankAcctID和CustomerInfo 为空", transIDH, reqMsg.getReqSys());
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_014A04.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_014A04.getDesc());
				marketLogger.debug("RcvShopOpenAction execute(Object) - end ");
				return resMsg;
			}
			//添加至交易流水表
			initLog(tTxnLog, seqId, transIDH,intTxnDate,transIDHTime,reqMsg,transCode,resMsg,reqBody,mBankAcctID);
			
			/**
			 * 判断是否重复交易
			 * */
			Map<String,Object> param1 =new HashMap<String,Object>();
			param1.put("reqTransId", reqMsg.getReqTransID());
			param1.put("reqDomain", reqMsg.getReqSys());
			TpayCsysTxnLog transLog_isExist=tpayCsysTxnLogService.findObj(param1);
			if(transLog_isExist!=null){
				marketLogger.info("重复交易:流水号{},机构编号{},银行账户{}",new Object[] {reqMsg.getReqTransID(),reqMsg.getReqSys(),mBankAcctID});
				marketLogger.debug("重复交易:流水号{},机构编号{},银行账户{}",new Object[] {reqMsg.getReqTransID(),reqMsg.getReqSys(),bankAcctID});
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_013A17.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_013A17.getDesc());
				marketLogger.debug("RcvShopOpenAction execute(Object) - end");
				return resMsg;
			}else{
				tpayCsysTxnLogService.add(tTxnLog);
			}
			// 更新交易流水
			tTxnLog.setRcvOprDt(tTxnLog.getIntTxnDate());
			tTxnLog.setRcvOprTm(StrUtil.subString(transIDHTime,0, 14));
			tTxnLog.setOriOrgId(reqMsg.getReqSys());
			tTxnLog.setReqSessionId(transIDH);
			tTxnLog.setReqCnlType(msgVo.getReqChannel());
			tTxnLog.setReqTransId(reqMsg.getReqTransID());
			tTxnLog.setReqTranshTm(transIDHTime);
			tTxnLog.setReqTransDt(intTxnDate);
			tTxnLog.setReqOprId(reqMsg.getReqTransID());
			tTxnLog.setReqOprTm(reqMsg.getReqDateTime());
			// 查询该交易的号码段属于移动还是联通电信的。
			String forwardOrg=CommonConstant.BankOrgCode.TPAY.getValue();
			//校验落地方机构权限
			String checkFlag = offOrgTrans(reqMsg.getReqSys(), forwardOrg, msgVo.getTransCode().getTransCode(),CommonConstant.PhoneNumType.CHINA_MOBILE.getType());
			//校验落地方机构权限
			if (checkFlag == null) {
				marketLogger.debug("sendCrmJmsMessage.sendMsg(forwardMsg) - start,intTxnSeq:{}",new Object[]{transIDH});
				/**
				 * 发往银联
				 * */
				//创建map对象
				Map<String,String> paramMap =new HashMap<String,String>();
				Map<String,String> resultMap =new HashMap<String,String>();
				//创建vo
				TpayMsgVo tpayMsgVo =new TpayMsgVo();
				//请求值
				toUnion(tTxnLog,trans14IDHTime,paramMap,reqBody);
				//vo设值   
				tpayMsgVo.setTpayReqData(paramMap);
				tpayMsgVo.setTransIDO(transIDH);
				tpayMsgVo.setReqPathAppend(openPath);
				tpayMsgVo.setMsgReceiver(CommonConstant.BankOrgCode.TPAY.getValue());
				//发往银联
				tpayMsgVo=sendTpayJmsMessage.sendMsg(tpayMsgVo);
				//获取返回值
				resultMap=tpayMsgVo.getTpayRspData();
				//入库更新
				toTxnLog(resultMap,tTxnLog,year);
				//map转化为vo
				toResMsg(resMsg,reqMsg,resBody,reqBody,resultMap,transIDHTime);
				//待确认
				tTxnLog.setRcvTranshId(resultMap.get(TUPayConstant.UnionPayMsg.QUERYID.getValue()));
				tTxnLog.setRcvTranshTm(resultMap.get(TUPayConstant.UnionPayMsg.RESPTIME.getValue()));
				if(resultMap.get(TUPayConstant.UnionPayMsg.RESPTIME.getValue())!=null){
					tTxnLog.setRcvTranshDt(resultMap.get(TUPayConstant.UnionPayMsg.RESPTIME.getValue()).substring(0,8));
				}
				marketLogger.debug("sendCrmJmsMessage.sendMsg(forwardMsg) - end,intTxnSeq:{}",new Object[]{transIDH});
				if(resultMap==null || resultMap.size()==0 || resultMap.get(TUPayConstant.UnionPayMsg.RESPCODE.getValue()).equals("U99998")){
					marketOperLogger.error("银联前置响应超时!内部交易流水:{},发起方:{},接收方:{}",new Object[] { transIDH,
									reqMsg.getReqSys(),forwardOrg});
					marketLogger.error("银联前置响应超时!内部交易流水:{},发起方:{},接收方:{}",new Object[] { transIDH,
									reqMsg.getReqSys(),forwardOrg});
					resMsg.setRspCode(RspCodeConstant.Market.MARKET_015A07.getValue());
					resMsg.setRspDesc(RspCodeConstant.Market.MARKET_015A07.getDesc());
					tTxnLog.setChlRspCode(RspCodeConstant.Market.MARKET_015A07.getValue());
					tTxnLog.setChlRspDesc(RspCodeConstant.Market.MARKET_015A07.getDesc());
					tpayCsysTxnLogService.modify(tTxnLog);
					marketLogger.debug("RcvShopOpenAction execute(Object) - end");
					return resMsg;
				}
				
				/**
				 * rcv字段
				 * */
				tTxnLog.setThrVersion(TUPayConstant.Version.TPAY_VERSION.getValue());
				tTxnLog.setRcvDomain(forwardOrg);
				tTxnLog.setRcvSessionId(transIDH);
				tTxnLog.setRcvTransId(transIDH);
				tTxnLog.setRcvTransDt(intTxnDate);
				tTxnLog.setRcvTransTm(trans14IDHTime);
//				tTxnLog.setRcvCnlType(oriTxnLog.getRcvCnlType());
				tTxnLog.setRcvStartTm(trans14IDHTime);
				tTxnLog.setRcvEndTm(resultMap.get(TUPayConstant.UnionPayMsg.RESPTIME.getValue()));
				
				if (TUPayConstant.UnPayRspCode.UNPAY_00.getValue().equals(
						resultMap.get(TUPayConstant.UnionPayMsg.RESPCODE.getValue()))) {
					marketOperLogger.succ("银联响应成功!内部交易流水:{},发起方:{},接收方:{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardOrg});
					marketLogger.info("银联响应成功!内部交易流水:{},发起方:{},接收方:{},银行账户{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardOrg,mBankAcctID});
					marketLogger.debug("银联响应成功!内部交易流水:{},发起方:{},接收方:{},银行账户{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardOrg,bankAcctID});
					tTxnLog.setRcvRspCode(resultMap.get(TUPayConstant.UnionPayMsg.RESPCODE.getValue()));
					tTxnLog.setRcvRspDesc(resultMap.get(TUPayConstant.UnionPayMsg.RESPMSG.getValue()));
					tTxnLog.setRcvRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_00.getValue());
					tTxnLog.setChlRspCode(RspCodeConstant.Market.MARKET_010A00.getValue());
					tTxnLog.setChlRspDesc(RspCodeConstant.Market.MARKET_010A00.getDesc());
					tTxnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_00.getValue());
					resMsg.setRspCode(RspCodeConstant.Market.MARKET_010A00.getValue());
					resMsg.setRspDesc(RspCodeConstant.Market.MARKET_010A00.getDesc());
//					resBody.setOriResultCode(subCode);
//					resBody.setOriResultDesc(RspCodeConstant.Market.getDescByValue(subCode));
					resMsg.setBody(resBody);
				} else {
					marketOperLogger.error("银联响应失败!内部交易流水:{},发起方:{},接收方:{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardOrg});
					marketLogger.error("银联响应失败!内部交易流水:{},发起方:{},接收方:{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardOrg });
					
					String code=resultMap.get(TUPayConstant.UnionPayMsg.RESPCODE.getValue());
					String desc=RspCodeConstant.Bank.getDescByValue(code);
					tTxnLog.setRcvRspCode(resultMap.get(TUPayConstant.UnionPayMsg.RESPCODE.getValue()));
					tTxnLog.setRcvRspDesc(resultMap.get(TUPayConstant.UnionPayMsg.RESPMSG.getValue()));
					tTxnLog.setRcvRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_02.getValue());
					tTxnLog.setChlRspCode(code);
					tTxnLog.setChlRspDesc(desc);
					tTxnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_02.getValue());
					resMsg.setRspCode(code);
					resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(desc));
//					resBody.setOriResultCode(errNameBody);
//					resBody.setOriResultDesc(RspCodeConstant.Bank.getDescByValue(errNameBody));
				}
				tTxnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
				tTxnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				tpayCsysTxnLogService.modify(tTxnLog);
				
				marketLogger.debug("RcvShopOpenAction execute(Object) - end");
				return resMsg;
			}else {
				marketOperLogger.error("落地方机构状态异常:{},内部交易流水:{},发起方:{},接收方:{}",new Object[] { forwardOrg, transIDH,
						reqMsg.getReqSys(),forwardOrg });
				marketLogger.error("落地方机构状态异常:{},内部交易流水:{},发起方:{},接收方:{}",new Object[] { forwardOrg, transIDH,
						reqMsg.getReqSys(),forwardOrg});
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				marketLogger.debug("MarketTransResultQueryAction execute(Object) - end");
				
				tTxnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				tTxnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				tTxnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				tTxnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				tTxnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_02.getValue());
				tpayCsysTxnLogService.modify(tTxnLog);
				marketLogger.debug("RcvShopOpenAction execute(Object) - end");
				return resMsg;
			}
			
		}catch (AppRTException e) {
			String errCode = e.getCode();
			marketOperLogger.error("运行异常!内部交易流水号:{},业务发起方:{}",
					new Object[] {RspCodeConstant.Bank.getDescByValue(errCode),
							transIDH, reqMsg.getReqSys() });
			marketLogger.error("运行异常,代码:{},内部交易流水号:{},业务发起方:{}",
					new Object[] { errCode,
							transIDH, reqMsg.getReqSys() });
			marketLogger.error("运行异常:",e);
			tTxnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			tTxnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			tTxnLog.setChlRspCode(errCode);
			tTxnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			tTxnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_01.getValue());
			tpayCsysTxnLogService.modify(tTxnLog);
			resMsg.setRspCode(errCode);
			resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			marketLogger.debug("RcvShopOpenAction execute(Object) - end");
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
			tTxnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			tTxnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			tTxnLog.setChlRspCode(errCode);
			tTxnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			tTxnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_01.getValue());
			tpayCsysTxnLogService.modify(tTxnLog);
			resMsg.setRspCode(errCode);
			resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			marketLogger.debug("RcvShopOpenAction execute(Object) - end");
			return resMsg;
		} catch (Exception e) {
			String errCode = RspCodeConstant.Bank.BANK_015A06.getValue();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			marketOperLogger.error("系统异常!内部交易流水号:{},业务发起方:{}",
					new Object[] {transIDH, reqMsg.getReqSys() });
			marketLogger.info("系统异常!内部交易流水号:{},业务发起方:{},银行账户{}",
					new Object[] {transIDH, reqMsg.getReqSys(),mBankAcctID});
			marketLogger.debug("系统异常!内部交易流水号:{},业务发起方:{},银行账户{}",
					new Object[] {transIDH, reqMsg.getReqSys(),bankAcctID});
			marketLogger.error("系统异常:",e);
			tTxnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			tTxnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			tTxnLog.setChlRspCode(errCode);
			tTxnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			tTxnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_01.getValue());
			tpayCsysTxnLogService.modify(tTxnLog);
			resMsg.setRspCode(errCode);
			resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			marketLogger.debug("RcvShopOpenAction execute(Object) - end");
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
	private void initLog(TpayCsysTxnLog tTxnLog, Long seqId, String transIDH,
			String intTxnDate, String transIDHTime, MobileShopMsgVo reqMsg,
			UpayCsysTransCode transCode, MobileShopMsgVo resMsg,MobileShopOpenReqVo reqBody,String mBankAcctID) {
		tTxnLog.setMerId(reqBody.getMerID());
		tTxnLog.setBankAccId(mBankAcctID);
		tTxnLog.setCustomerInfo(reqBody.getCustomerInfo());
		
		tTxnLog.setSettleDate(reqMsg.getReqDate());
		tTxnLog.setSeqId(seqId);
		tTxnLog.setIntTxnSeq(transIDH);
		tTxnLog.setIntTxnDate(intTxnDate);
		tTxnLog.setIntTxnTime(transIDHTime);
		tTxnLog.setBussType(transCode.getBussType());
		tTxnLog.setBussChl(transCode.getBussChl());
		tTxnLog.setIntTransCode(transCode.getTransCode());
		tTxnLog.setPayMode(transCode.getPayMode());
		tTxnLog.setStatus(CommonConstant.TxnStatus.InitStatus.getValue());
		tTxnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
		tTxnLog.setReqActivityCode(reqMsg.getActivityCode());
		tTxnLog.setReqDomain(reqMsg.getReqSys());
		tTxnLog.setReqSessionId(transIDH);
		tTxnLog.setReqTransId(reqMsg.getReqTransID());
		tTxnLog.setReqTransTm(reqMsg.getReqDateTime());
		tTxnLog.setReqTransDt(StrUtil.subString(reqMsg.getReqDateTime(), 0, 8));
		tTxnLog.setReqTranshId(transIDH);
		tTxnLog.setReqTranshDt(StrUtil.subString(transIDHTime,0,8));
		tTxnLog.setReqTranshTm(transIDHTime);
		tTxnLog.setReqOprDt(StrUtil.subString(reqMsg.getRcvDateTime(), 0, 8));
		tTxnLog.setReqOprTm(reqMsg.getRcvDateTime());
		tTxnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
		tTxnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
		tTxnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
		tTxnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
	}
	
	/**
	 * 发往支付前置的值
	 * */
	private void toUnion(TpayCsysTxnLog tTxnLog,String trans14IDHTime,Map<String,String> paramMap,MobileShopOpenReqVo reqBody){
//		版本号	
		paramMap.put("version",TUPayConstant.Version.TPAY_VERSION.getValue());
//		编码方式	
		paramMap.put("encoding",TUPayConstant.CodeType.TPAY_CODETYPE.getValue());
//		交易类型	
		paramMap.put("txnType",TUPayConstant.TpayType.TPAYTYPE_78.getValue());
//		交易子类
		/**
		 * 当原请求报文中QueryType无值，或者无其他有效值时取默认值00,否则位置原报文传过来的值
		 * */
		if(!reqBody.getQueryType().equals(TUPayConstant.TxnSubType.TXNSUBTYPE_PHOQUERY.getValue())){
			paramMap.put("txnSubType",TUPayConstant.TxnSubType.TXNSUBTYPE_ACCQUERY.getValue());
		}
//		产品类型	
		paramMap.put("bizType",TUPayConstant.BizType.BIZTYPE_000301.getValue());
//		接入类型	
		paramMap.put("accessType",TUPayConstant.AccessType.ACCESSTYPE_0.getValue());
//		商户代码	 * 等文档更新，通过shopMerID关联商户代码表的father_mer_id查询merID
		TpayCsysMerCodeInfo tpayCsysMerCodeInfo=MerCodeCache.getMerInfoByMerId(paramMap.get(TUPayConstant.UnionPayMsg.MERID.getValue()),CommonConstant.BankOrgCode.TPAY.getValue());
		if(tpayCsysMerCodeInfo!=null){
			paramMap.put("merId",reqBody.getQueryType());
		}
//		帐号	
		paramMap.put("accNo",reqBody.getBankAcctID());
//		银行卡验证信息及身份信息	
		paramMap.put("customerInfo",reqBody.getCustomerInfo());
	}
	
	/**
	 * 返回的需要入库的值
	 * */
	private void toTxnLog(Map<String,String>paramMap,TpayCsysTxnLog tTxnLog,int year){
		tTxnLog.setThrMerId(TUPayConstant.UnionPayMsg.MERID.getValue());
		tTxnLog.setBankAccId(paramMap.get(TUPayConstant.UnionPayMsg.ACCNO.getValue()));
		tTxnLog.setActivateStatus(paramMap.get(TUPayConstant.UnionPayMsg.ACTIVATESTATUS.getValue()));
		tTxnLog.setPayCardType(paramMap.get(TUPayConstant.UnionPayMsg.PAYCARDTYPE.getValue()));
		tTxnLog.setCustomerInfo(paramMap.get(TUPayConstant.UnionPayMsg.CUSTOMERINFO.getValue()));
		tTxnLog.setCheckFlag(paramMap.get(TUPayConstant.UnionPayMsg.CHECKFLAG.getValue()));
		tTxnLog.setThrVersion(paramMap.get(TUPayConstant.UnionPayMsg.VERSION.getValue()));
		tTxnLog.setThrTxnType(paramMap.get(TUPayConstant.UnionPayMsg.TXNTYPE.getValue()));
		tTxnLog.setThrSubTxnType(paramMap.get(TUPayConstant.UnionPayMsg.TXNSUBTYPE.getValue()));
		tTxnLog.setAccessType(paramMap.get(TUPayConstant.UnionPayMsg.ACCESSTYPE.getValue()));
		TpayCsysMerCodeInfo tpayCsysMerCodeInfo=MerCodeCache.getMerInfoByMerId(paramMap.get(TUPayConstant.UnionPayMsg.MERID.getValue()),CommonConstant.BankOrgCode.TPAY.getValue());
		if(tpayCsysMerCodeInfo!=null){
			tTxnLog.setMerId(tpayCsysMerCodeInfo.getOrgCode());
		}
		tTxnLog.setRcvRspCode(paramMap.get(TUPayConstant.UnionPayMsg.RESPCODE.getValue()));
		tTxnLog.setRcvRspDesc(paramMap.get(TUPayConstant.UnionPayMsg.RESPMSG.getValue()));
	}
	
	/**
	 * 报文返回值设置
	 * */
	private void toResMsg(MobileShopMsgVo resMsg,MobileShopMsgVo reqMsg,MobileShopOpenResVo resBody,MobileShopOpenReqVo reqBody,Map<String,String> map,String transIDHTime){
		//报文头
		resMsg.setActivityCode(reqMsg.getActivityCode());
		resMsg.setReqSys(reqMsg.getReqSys());
		resMsg.setReqChannel(reqMsg.getReqChannel());
		resMsg.setReqDate(reqMsg.getReqDate());
		resMsg.setReqTransID(reqMsg.getReqTransID());
		resMsg.setReqDateTime(reqMsg.getReqDateTime());
		resMsg.setActionCode(CommonConstant.ActionCode.Respone.getValue());
		resMsg.setRcvSys(CommonConstant.BankOrgCode.TPAY.getValue());
		resMsg.setRcvDate(transIDHTime.substring(0, 8));
		//报文体
		resBody.setMerId(reqBody.getMerID());
		resBody.setActivateStatus(TUPayConstant.UnionPayMsg.ACTIVATESTATUS.getValue());
	}
}