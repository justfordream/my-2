package com.huateng.cmupay.action;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.CommonConstant.CnlType;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.controller.cache.SysMapCache;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.business.crm.CrmChargeBus;
import com.huateng.cmupay.jms.business.crm.CrmChargeMobileBus;
import com.huateng.cmupay.logFormat.MobileMarketMessageLogger;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopMsgReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopMsgResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmChargeResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.cmupay.utils.StringFormat;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;

/**
 * 商城交易
 * 
 * @author oul
 *  
 */
@Controller("mmarkertPayAction")
@Scope("prototype")
public class MobileMarketPayAction extends AbsBaseAction<MobileShopMsgVo, MobileShopMsgVo> {
	private MobileMarketMessageLogger marketOperLogger = MobileMarketMessageLogger.getLogger(this.getClass());
	private final Logger marketLogger = LoggerFactory.getLogger("CMUPayCoresys_MMarket");
	@Autowired
	private CrmChargeMobileBus crmChargeMobileBus;

	@Override
	public MobileShopMsgVo execute(MobileShopMsgVo msgVo) throws AppBizException {
		
		//日志
		marketLogger.debug("MobileMarketPayAction execute(Object) - start");
		// 请求报文报文头
		MobileShopMsgVo reqMsg = msgVo;
		//请求报文体
		MobileShopMsgReqVo reqBody = new MobileShopMsgReqVo();
		//响应报文体
		MobileShopMsgResVo resBody = new MobileShopMsgResVo();
		//响应报文头
		MobileShopMsgVo resMsg = new MobileShopMsgVo() ;
		
		//交易流水表
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();

		try {
			//请求报文xml转化为bean
			MsgHandle.unmarshaller(reqBody, (String) reqMsg.getBody());
			//请求报文体bean中加入请求报文体
			reqMsg.setBody(reqBody);
			//获取平台交易流水号
			String transIDH = msgVo.getTxnSeq();
			//获取平台交易时间
			String transIDHTime = msgVo.getTxnTime();
			//平台交易数据库日切日期
			String intTxnDate = msgVo.getTxnDate();
			//交易流水表唯一流水号
			Long seqId = msgVo.getSeqId();
			//平台发给省的流水
			String oprId=Serial.genSerialNum(CommonConstant.Sequence.OprId.toString());
			
//			resMsg.setReqDate(intTxnDate);
			resMsg.setReqDate(reqMsg.getReqDate());
			resMsg.setReqTransID(transIDH);
			resMsg.setReqDateTime(transIDHTime);
			resMsg.setReqSys(reqMsg.getRcvSys());
			resMsg.setRcvSys(reqMsg.getReqSys());
			resMsg.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
			resMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
			resMsg.setActivityCode(CommonConstant.MarketTrans.Market03.getValue());

			resBody.setOrderID(reqBody.getOrderID());
			resBody.setOriReqDate(reqMsg.getReqDate());
			resBody.setOriReqTransID(reqMsg.getReqTransID());
			
			//内部交易码
			UpayCsysTransCode transCode = msgVo.getTransCode();
			/**
			 * 报文格式验证(不更新流水)
			 * */
			String checkrtn = validateModel(reqBody);
			if (!"".equals(StringUtil.toTrim(checkrtn))) {
				marketOperLogger.error("报文体校验失败:{},内部交易流水:{},发起方:{}", new Object[] {
						checkrtn, msgVo.getTxnSeq(), reqMsg.getReqSys() });
				marketLogger.error("报文体校验失败:{},内部交易流水:{},发起方:{}", new Object[] {
						checkrtn, msgVo.getTxnSeq(), reqMsg.getReqSys() });
				//格式化错误返回码
				Map<String,String> map=new HashMap<String,String>();
				map=checkXmlChange(checkrtn);
				resBody.setResultCode(map.get("code").toString());
				resBody.setResultDesc(map.get("desc").toString());
				//返回
				resMsg.setBody(resBody);
				return resMsg;
			}
			//判断交易是否是重发交易（在交易成功明细表里查询）
			Map<String, Object> paramsForQueryBusi = new HashMap<String, Object>();
			paramsForQueryBusi.put("reqTransId", msgVo.getReqTransID());
			paramsForQueryBusi.put("reqDomain", msgVo.getReqSys());
			paramsForQueryBusi.put("settleDate", msgVo.getReqDate());
			
			/**
			 * 查询是否重发交易
			 * */
			txnLog = upayCsysTxnLogService.findObj(paramsForQueryBusi);
			
			//标识，是否需要重发交易
			Boolean resendFlag=false;
			if(txnLog!=null&&txnLog.getStatus().equals(CommonConstant.TxnStatus.TxnSuccess.getValue())){
				/**
				 * 重复交易(不更新流水)
				 * */
				marketOperLogger.info("重复交易:{},内部交易流水:{},发起方:{}",
						new Object[] { msgVo.getTxnSeq(), reqBody.getIDValue(),
						reqMsg.getReqSys() });
				marketOperLogger.info("重复交易:{},内部交易流水:{},发起方:{}",
						new Object[] { msgVo.getTxnSeq(), reqBody.getIDValue(),
								reqMsg.getReqSys() });
				resBody.setResultCode(RspCodeConstant.Market.MARKET_013A34.getValue());
				resBody.setResultDesc(RspCodeConstant.Market.MARKET_013A34.getDesc());
				resMsg.setBody(resBody);
				return resMsg;
			}else if(txnLog!=null&&!txnLog.getStatus().equals(CommonConstant.TxnStatus.TxnSuccess.getValue())){
				/**
				 * 重发交易
				 * */
				txnLog.setSeqId(txnLog.getSeqId());
				txnLog.setReserved1(RspCodeConstant.MarketQueryType.MARKETQUERYTYPE_01.getValue());
				marketOperLogger.info("重发交易:{},内部交易流水:{},发起方:{}",
						new Object[] { msgVo.getTxnSeq(), reqBody.getIDValue(),
						reqMsg.getReqSys() });
				marketOperLogger.info("重发交易:{},内部交易流水:{},发起方:{}",
						new Object[] { msgVo.getTxnSeq(), reqBody.getIDValue(),
								reqMsg.getReqSys() });
				/**
				 * 重发交易非当天直接返回(不更新流水)
				 * */
				if(reqMsg.getReqDate()!=null&&!reqMsg.getReqDate().equals(intTxnDate)){
					marketOperLogger.info("非当天重复交易不重发:{},内部交易流水:{},发起方:{}",
							new Object[] { msgVo.getTxnSeq(), reqBody.getIDValue(),
							reqMsg.getReqSys() });
					resBody.setResultCode(RspCodeConstant.Bank.BANK_013A35.getValue());
					resBody.setResultDesc(RspCodeConstant.Bank.BANK_013A35.getDesc());
					resMsg.setBody(resBody);
					return resMsg;
				}
				resendFlag=true;
			}else{
				if(txnLog==null){
					txnLog=new UpayCsysTxnLog();
				}
				initLog(txnLog, reqMsg, resMsg, reqBody, seqId, transIDH, intTxnDate, transIDHTime,oprId);
				txnLog.setSettleDate(reqMsg.getReqDate());
				upayCsysTxnLogService.add(txnLog);
			}
			/**
			 * 手机号码归属地校验
			 * */
			ProvincePhoneNum provincePhoneNum = findProvinceByMobileNumber(reqBody.getIDValue());
			String idProvince = provincePhoneNum == null ? null : provincePhoneNum.getProvinceCode();
			if (null == idProvince) {
				marketOperLogger.error("手机号码不正确:{},内部交易流水:{},发起方:{}",
						new Object[] { msgVo.getTxnSeq(), reqBody.getIDValue(),
								reqMsg.getReqSys() });
				marketLogger.error("手机号码不正确:{},内部交易流水:{},发起方:{}",
						new Object[] { msgVo.getTxnSeq(), reqBody.getIDValue(),
								reqMsg.getReqSys() });
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setChlRspCode(RspCodeConstant.Market.MARKET_012A11.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Market.MARKET_012A11.getDesc());
				txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_02.getValue());
				txnLog.setChlSubRspCode(RspCodeConstant.Market.MARKET_012A11.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Market.MARKET_012A11.getDesc());
				upayCsysTxnLogService.modify(txnLog);
				
				resBody.setResultCode(RspCodeConstant.Market.MARKET_012A11.getValue());
				resBody.setResultDesc(RspCodeConstant.Market.MARKET_012A11.getDesc());
				resMsg.setBody(resBody);
				return resMsg;
			}
			/**
			 * 手机号码归属地是否与归属省一致
			 * */
			if(!reqBody.getHomeProv().equals(idProvince)){
				marketOperLogger.error("省代码和手机号码归属省不一致,内部交易流水:{},手机号码:{},发起方:{}",
						new Object[] { msgVo.getTxnSeq(), reqBody.getIDValue(),
								reqMsg.getReqSys() });
				marketLogger.error("省代码和手机号码归属省不一致,内部交易流水:{},手机号码:{},发起方:{}",
						new Object[] { msgVo.getTxnSeq(), reqBody.getIDValue(),
								reqMsg.getReqSys() });
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setChlRspCode(RspCodeConstant.Market.MARKET_014A10.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Market.MARKET_014A10.getDesc());
				txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_02.getValue());
				txnLog.setChlSubRspCode(RspCodeConstant.Market.MARKET_014A10.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Market.MARKET_014A10.getDesc());
				upayCsysTxnLogService.modify(txnLog);
				
				resBody.setResultCode(RspCodeConstant.Market.MARKET_014A10.getValue());
				resBody.setResultDesc(RspCodeConstant.Market.MARKET_014A10.getDesc());
				resMsg.setBody(resBody);
				return resMsg;
			}
			//归属省代码入库
			txnLog.setIdProvince(idProvince);
			txnLog.setReqTranshId(transIDH);
			//转发放机构
			String forwardOrg = SysMapCache.getProvCd(idProvince).getSysCd();

			/** 报文头 */
			CrmMsgVo forwardMsg = new CrmMsgVo();
			CrmMsgVo forwardRtMsg = new CrmMsgVo();
			
			forwardMsg.setTransCode(transCode);
			forwardMsg.setVersion(ExcConstant.CRM_VERSION);
			forwardMsg.setTestFlag(testFlag);
			forwardMsg.setBIPCode(CommonConstant.Bip.Biz22.getValue());
			forwardMsg.setActivityCode(CommonConstant.CrmTrans.Crm07.getValue());
			forwardMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
			forwardMsg.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
			forwardMsg.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
			forwardMsg.setRouteType(CommonConstant.RouteType.RoutePhone.getValue());
			forwardMsg.setRouteValue(reqBody.getIDValue());
			forwardMsg.setTransIDO(transIDH);
			forwardMsg.setMsgSender(CommonConstant.BankOrgCode.CMCC.getValue());
			forwardMsg.setMsgReceiver(forwardOrg);
			
   	       String checkFlag = offOrgTrans(reqMsg.getReqSys(), forwardOrg, msgVo.getTransCode().getTransCode(), 
	    		   provincePhoneNum == null ? CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType() : provincePhoneNum.getPhoneNumFlag());
			if (checkFlag == null) { 
				Map<String, String> params = new HashMap<String, String>();
				params.put("idType", reqBody.getIDType());
				params.put("idValue", reqBody.getIDValue());
				//补充值，以下值维持不变发往省
				if(resendFlag){
					params.put("transactionID", txnLog.getRcvOprId());
					params.put("actionDate", txnLog.getIntTxnDate());
					params.put("actionTime", StrUtil.subString(txnLog.getIntTxnTime(), 0, 14));
					forwardMsg.setTransIDOTime(StrUtil.subString(txnLog.getRcvTransTm(), 0, 14));
					forwardMsg.setSessionID(txnLog.getRcvTransId());
				}else{
					params.put("transactionID",oprId);
					params.put("actionDate", intTxnDate);
					params.put("actionTime", StrUtil.subString(transIDHTime, 0, 14));
					forwardMsg.setTransIDOTime(StrUtil.subString(transIDHTime, 0, 14));
					forwardMsg.setSessionID(transIDH);
				}
				params.put("cnlTyp", reqMsg.getReqChannel());
				params.put("payedType", CommonConstant.PayType.PayPre.getValue());
				params.put("settleDate", reqMsg.getReqDate());
				/**
				 * 新充值改造字段
				 * time:20131106
				 * author:ol
				 * */
				params.put("commision",txnLog.getCommision()==null?null:txnLog.getCommision().toString());
				params.put("rebateFee",txnLog.getRebateFee()==null?null:txnLog.getRebateFee().toString());
				params.put("busiTransID", reqMsg.getReqTransID());//移动商城流水号
				params.put("payTransID", reqBody.getPayTransID());//移动商城扣款流水号
				params.put("organID", reqMsg.getReqSys());//机构编码
				params.put("chargeMoney", String.valueOf(txnLog.getNeedPayAmt()+""));//充值金额
				params.put("orderNo", txnLog.getOrderId());//订单号
				params.put("productNo", txnLog.getProductNo());//产品编号
				params.put("payment", txnLog.getPayAmt()==null?null:txnLog.getPayAmt().toString());//订单总金额
				params.put("orderCnt", txnLog.getOrderCnt()==null?null:txnLog.getOrderCnt().toString());//订单总数量
				params.put("prodDiscount",txnLog.getProdDiscount()==null?null:txnLog.getProdDiscount().toString());//产品减折金额
				params.put("creditCardFee",txnLog.getCreditCardFee()==null?null:txnLog.getCreditCardFee().toString());//信用卡费用
				params.put("activityNo",txnLog.getActivityNo());//营销活动号
				params.put("productShelfNo", txnLog.getProductShelfNo());//商品上架编码
				params.put("serviceFee", reqBody.getServiceFee());
				marketLogger.debug(
						"开始手机:{}充值,内部交易流水:{},发起方:{},接收方:{}",
						new Object[] { reqBody.getIDValue(), msgVo.getTxnSeq(),
								reqMsg.getReqSys(), forwardMsg.getMsgReceiver() });
				forwardRtMsg = crmChargeMobileBus.execute(forwardMsg, params, txnLog, null);
				
				/**
				 * 非补充值执行赋值
				 * */
				if(!resendFlag){
					txnLog.setRcvTransId(transIDH);
					txnLog.setRcvTransDt(StrUtil.subString(transIDHTime,0,8));
					txnLog.setRcvTransTm(transIDHTime);
				}
				txnLog.setRcvTranshId(forwardRtMsg.getTransIDH());
				txnLog.setRcvTranshDt(StrUtil.subString(forwardRtMsg.getTransIDHTime(),0,8));
				txnLog.setRcvTranshTm(forwardRtMsg.getTransIDHTime());
				txnLog.setRcvSessionId(forwardRtMsg.getSessionID());
				
				marketLogger.debug(
						"手机:{}充值返回,内部交易流水:{},发起方:{},接收方:{}",
						new Object[] { reqBody.getIDValue(), msgVo.getTxnSeq(),
								reqMsg.getReqSys(), forwardMsg.getMsgReceiver() });
				CrmChargeResVo rtBody = null;
				if ("".equals(forwardRtMsg.getBody())) {
					marketOperLogger.error(
							"充值返回报文体为空,内部交易流水:{},发起方:{},接收方:{}",
							new Object[] { msgVo.getTxnSeq(),
									reqMsg.getReqSys(),
									forwardMsg.getMsgReceiver() });
					marketLogger.error(
							"充值返回报文体为空,内部交易流水:{},发起方:{},接收方:{}",
							new Object[] { msgVo.getTxnSeq(),
									reqMsg.getReqSys(),
									forwardMsg.getMsgReceiver() });
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					txnLog.setChlRspCode(RspCodeConstant.Market.MARKET_015A07.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Market.MARKET_015A07.getDesc());
					txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_01.getValue());
					txnLog.setChlSubRspCode(RspCodeConstant.Market.MARKET_015A07.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Market.MARKET_015A07.getDesc());
					upayCsysTxnLogService.modify(txnLog);
					resBody.setResultCode(RspCodeConstant.Market.MARKET_015A07.getValue());
					resBody.setResultDesc(RspCodeConstant.Market.MARKET_015A07.getDesc());
					resMsg.setBody(resBody);
					return resMsg;
				}else{
					rtBody =  (CrmChargeResVo) forwardRtMsg.getBody();
				}
				if (RspCodeConstant.Wzw.WZW_0000.getValue().equals(
						forwardRtMsg.getRspCode())
						&& RspCodeConstant.Crm.CRM_0000.getValue().equals(
								rtBody.getRspCode())) {
					marketOperLogger.succ(
							"充值返回成功,内部交易流水:{},发起方:{},接收方:{}",
							new Object[] { msgVo.getTxnSeq(),
									reqMsg.getReqSys(),
									forwardMsg.getMsgReceiver() });
					marketLogger.info(
							"充值返回成功,内部交易流水:{},发起方:{},接收方:{}",
							new Object[] { msgVo.getTxnSeq(),
									reqMsg.getReqSys(),
									forwardMsg.getMsgReceiver() });
					txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
					txnLog.setChlRspCode(RspCodeConstant.Market.MARKET_010A00.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Market.MARKET_010A00.getDesc());
					txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_00.getValue());
					txnLog.setChlSubRspCode(RspCodeConstant.Market.MARKET_010A00.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Market.MARKET_010A00.getDesc());
					upayCsysTxnLogService.modify(txnLog);
					//充值结果body
					resBody.setResultTime(StrUtil.subString(forwardRtMsg.getTransIDHTime(), 0, 14));
					resBody.setResultCode(RspCodeConstant.Market.MARKET_010A00.getValue());
					resBody.setResultDesc(RspCodeConstant.Market.MARKET_010A00.getDesc());
					resMsg.setBody(resBody);
					marketLogger.debug("MobileMarketPayAction execute(Object) - end");
					return resMsg;
				}else if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(
						forwardRtMsg.getRspCode())) {
					marketOperLogger.error(
							"充值返回超时,内部交易流水:{},发起方:{},接收方:{}",
							new Object[] { msgVo.getTxnSeq(),
									reqMsg.getReqSys(),
									forwardMsg.getMsgReceiver() });
					marketLogger.error(
							"充值返回超时,内部交易流水:{},发起方:{},接收方:{}",
							new Object[] { msgVo.getTxnSeq(),
									reqMsg.getReqSys(),
									forwardMsg.getMsgReceiver() });

					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());

					txnLog.setChlRspCode(RspCodeConstant.Market.MARKET_015A07.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Market.MARKET_015A07.getDesc());
					txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_01.getValue());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setChlSubRspCode(RspCodeConstant.Market.MARKET_015A07.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Market.MARKET_015A07.getDesc());
					upayCsysTxnLogService.modify(txnLog);
					
					resBody.setResultCode(RspCodeConstant.Market.MARKET_015A07.getValue());
					resBody.setResultDesc(RspCodeConstant.Market.MARKET_015A07.getDesc());
					
					resMsg.setBody(resBody);
					marketLogger.debug("MobileMarketPayAction execute(Object) - end");
					return resMsg;
				} else {
					marketOperLogger.error(
							"充值返回失败,内部交易流水:{},发起方:{},接收方:{},返回码:{}",
							new Object[] { msgVo.getTxnSeq(),
									reqMsg.getReqSys(),
									forwardMsg.getMsgReceiver(),
									rtBody.getRspCode() });
					marketLogger.error(
							"充值返回失败,内部交易流水:{},发起方:{},接收方:{},返回码:{}",
							new Object[] { msgVo.getTxnSeq(),
									reqMsg.getReqSys(),
									forwardMsg.getMsgReceiver(),
									rtBody.getRspCode() });
					String errNameHead = BankErrorCodeCache.getBankErrCode(forwardMsg.getRspCode());
					String errNameBody = BankErrorCodeCache.getBankErrCode(rtBody.getRspCode());
					if("012998".equals(errNameHead)){
						errNameHead = RspCodeConstant.Market.MARKET_015A06.getValue();
					}
					if("012998".equals(errNameBody)){
						errNameBody = RspCodeConstant.Market.MARKET_015A06.getValue();
					}
					marketLogger.debug(
							"内部交易流水:{}转换银行返回码crm 返回码:{} ，转化为银行的返回码:{}{}",
							new Object[] { msgVo.getTxnSeq(),
									rtBody.getRspCode(), errNameHead,errNameBody });
					
					if(RspCodeConstant.Wzw.WZW_2998.getValue().equals(forwardRtMsg.getRspCode())
							&&RspCodeConstant.Crm.CRM_3A34.getValue().equals(rtBody.getRspCode())){
						txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
						txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_00.getValue());
					}else{
						txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
						txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_02.getValue());
					}
					
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					txnLog.setChlRspCode(errNameHead);
					txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errNameHead));
					txnLog.setChlSubRspCode(errNameBody);
					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errNameBody));
					upayCsysTxnLogService.modify(txnLog);
					resBody.setResultCode(errNameBody);
					resBody.setResultDesc(RspCodeConstant.Bank.getDescByValue(errNameBody));
					resMsg.setBody(resBody);
					marketLogger.debug("MobileMarketPayAction execute(Object) - end");
					return resMsg;
				}
			} else {
				marketOperLogger.error("接收方机构状态异常,内部交易流水:{},发起方:{},接收方:{}",
						new Object[] { msgVo.getTxnSeq(), reqMsg.getReqSys(),
								forwardMsg.getMsgReceiver() });
				marketLogger.error(
						"接收方机构状态异常,内部交易流水:{},发起方:{},接收方:{}",
						new Object[] { new Object[] { msgVo.getTxnSeq(),
								reqMsg.getReqSys(), forwardMsg.getMsgReceiver() } });
				
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_02.getValue());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				
				upayCsysTxnLogService.modify(txnLog);
				
				resBody.setResultCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				resBody.setResultDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				resMsg.setBody(resBody);
				marketLogger.debug("MobileMarketPayAction execute(Object) - end");
				return resMsg;
			}
		} catch (AppRTException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			marketOperLogger.error(
					"运行异常!内部交易流水号:{},业务发起方:{}",
					new Object[] {
							RspCodeConstant.Bank.getDescByValue(errCode),
							reqMsg.getTxnSeq(), reqMsg.getReqSys() });
			marketLogger.error(
					"运行异常!内部交易流水号:{},业务发起方:{}}",
					new Object[] { 
							reqMsg.getTxnSeq(), reqMsg.getReqSys() });
			marketLogger.error("运行异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());

			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_02.getValue());
			upayCsysTxnLogService.modify(txnLog);
			
			resBody.setResultCode(errCode);
			resBody.setResultDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			resMsg.setBody(resBody);
			return resMsg;
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			marketOperLogger.error(
					"业务异常!内部交易流水号:{},业务发起方:{}",
					new Object[] {reqMsg.getTxnSeq(), reqMsg.getReqSys() });
			marketLogger.error(
					"业务异常!内部交易流水号:{},业务发起方:{}",
					new Object[] {
							reqMsg.getTxnSeq(), reqMsg.getReqSys() });
			marketLogger.error("业务异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_02.getValue());
			upayCsysTxnLogService.modify(txnLog);
			
			resBody.setResultCode(errCode);
			resBody.setResultDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			resMsg.setBody(resBody);
			return resMsg;
		} catch (Exception e) {
			marketOperLogger.error("系统异常!内部交易流水号:{},业务发起方:{}",
					new Object[] { reqMsg.getTxnSeq(), reqMsg.getReqSys() });
			marketLogger.error("系统异常!内部交易流水号:{},业务发起方:{}",
					new Object[] {reqMsg.getTxnSeq(), reqMsg.getReqSys() });
			marketLogger.error("系统异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Market.MARKET_015A06.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Market.MARKET_015A06.getDesc());
			txnLog.setChlSubRspCode(RspCodeConstant.Market.MARKET_015A06.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Market.MARKET_015A06.getDesc());
			txnLog.setChlRspType(RspCodeConstant.MarketRspType.MARKETRSPTYPE_02.getValue());
			upayCsysTxnLogService.modify(txnLog);
			
			resBody.setResultCode(RspCodeConstant.Market.MARKET_015A06.getValue());
			resBody.setResultDesc(RspCodeConstant.Market.MARKET_015A06.getDesc());
			resMsg.setBody(resBody);
			return resMsg;
		}
		
	}

	/**
	 * 初始化交易流水
	 * 
	 * @param txnLog
	 * @param reqMsg
	 * @param resMsg
	 * @param reqBody
	 * @param seqId
	 * @param transIDH
	 * @param intTxnDate
	 * @param transIDHTime
	 */
	private void initLog(UpayCsysTxnLog txnLog, MobileShopMsgVo reqMsg,
			MobileShopMsgVo resMsg, MobileShopMsgReqVo reqBody, Long seqId,
			String transIDH, String intTxnDate, String transIDHTime,String oprId) {
		UpayCsysTransCode transCode = reqMsg.getTransCode();

		txnLog.setPayTransID(reqBody.getPayTransID());
		txnLog.setReqSessionId(transIDH);
		txnLog.setRebateFee(StringFormat.paseLong(reqBody.getRebateFee()));
		txnLog.setCommision(StringFormat.paseLong(reqBody.getCommision()));
		txnLog.setCreditCardFee(StringFormat.paseLong(reqBody.getCreditCardFee()));
		txnLog.setActivityNo(reqBody.getActivityNO());
		txnLog.setProductNo(reqBody.getProdID());
		txnLog.setOrderCnt(StringFormat.paseLong(reqBody.getProdCnt()));
		txnLog.setProdDiscount(StringFormat.paseLong(reqBody.getProdDiscount()));
		txnLog.setProductShelfNo(reqBody.getProdShelfNO());
		
		txnLog.setPayAmt(StringFormat.paseLong(reqBody.getPayment()));
		txnLog.setNeedPayAmt(StringFormat.paseLong(reqBody.getChargeMoney()));
		txnLog.setServiceFee(StringFormat.paseLong(reqBody.getServiceFee()));
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
		txnLog.setSettleDate(reqMsg.getReqDate());
		txnLog.setReqDomain(reqMsg.getReqSys());
//		txnLog.setReqRouteType(CommonConstant.RouteType.RoutePhone.getValue());
//		txnLog.setReqRouteVal(reqBody.getIDValue());
		txnLog.setReqCnlType(reqMsg.getReqChannel());
		txnLog.setReqActivityCode(reqMsg.getActivityCode());
		
		txnLog.setReqTransId(reqMsg.getReqTransID());
		txnLog.setReqTransDt(reqMsg.getReqDate());
		txnLog.setReqTransTm(reqMsg.getReqDateTime());
		txnLog.setReqTranshDt(StrUtil.subString(transIDHTime,0,8));
		txnLog.setReqTranshTm(transIDHTime);
		txnLog.setReqOprId(reqMsg.getReqTransID());
		txnLog.setReqOprDt(StrUtil.subString(transIDHTime,0,8));
		txnLog.setReqOprTm(transIDHTime);
		
		txnLog.setIdType(reqBody.getIDType());
		txnLog.setIdValue(reqBody.getIDValue());
		txnLog.setBankId(reqMsg.getReqSys());
		txnLog.setOrderId(reqBody.getOrderID());
		txnLog.setReqOprDt(reqMsg.getReqDate());
		txnLog.setReqOprTm(reqMsg.getReqDateTime());
		txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
		txnLog.setMainFlag(null);
	}
	
	/**
	 * 验证返回码、描述格式化
	 * */
	public Map<String,String> checkXmlChange(String checkStr){
		Map<String,String> map=new HashMap<String,String>();
		String []tStr=checkStr.split(":");
		if(tStr.length==2&&tStr[1].length()>6){
			map.put("code",tStr[0]);
			map.put("desc",tStr[1]);
		}
		else if(tStr.length>2&&tStr[1].length()>6){
			map.put("code",tStr[0]);
			map.put("desc",tStr[1].substring(0,tStr[1].length()-6));
		}
		return map;
	}
}
