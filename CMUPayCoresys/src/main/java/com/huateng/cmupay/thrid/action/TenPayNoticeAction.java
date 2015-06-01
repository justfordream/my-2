/**
 * 
 */
package com.huateng.cmupay.thrid.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.action.AbsBaseAction;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.constant.CommonConstant.BankOrgCode;
import com.huateng.cmupay.constant.CommonConstant.CnlType;
import com.huateng.cmupay.constant.RspCodeConstant.Market;
import com.huateng.cmupay.constant.RspCodeConstant.OrgId;
import com.huateng.cmupay.constant.TUPayConstant.TenPayMsg;
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.controller.cache.SysMapCache;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogTmpService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.jms.business.crm.CrmTransQueryBus;
import com.huateng.cmupay.jms.business.crm.TUpayCrmChargeBus;
import com.huateng.cmupay.jms.message.SendTenPayJmsMessageImpl;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.TpayCsysTxnLog;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopPayNoticeResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmChargeResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmTransQueryResVo;
import com.huateng.cmupay.remoting.client.MMarketRemoting;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;

/**
 * @author Manzhizhen
 *
 */
@Controller("tenPayNoticeAction")
@Scope("prototype")
public class TenPayNoticeAction extends
	AbsBaseAction<Map<String, Object>, Map<String, Object>>{
	
	@Autowired
	private ITpayCsysTxnLogTmpService tpayCsysTxnLogTmpService;
	@Autowired
	private TUpayCrmChargeBus tenPayCrmChargeBus;
	@Autowired
	private CrmTransQueryBus crmTransQueryBus;
	@Autowired
	private MMarketRemoting mmarketRemoting;
	@Autowired
	private SendTenPayJmsMessageImpl  sendTenPayJmsMessage;
	// 结果通知查询开关
	private @Value("${tenpay.verify.notify.switch}")String verifyNotifyId;

//	private static String[] dataEquals;
//	
//	static {
//		dataEquals = new String[]{TenPayMsg.SIGN_TYPE.getValue(), TenPayMsg.SERVICE_VERSION.getValue(),
//				TenPayMsg.INPUT_CHARSET.getValue(), TenPayMsg.SIGN.getValue(),
//				TenPayMsg.SIGN_KEY_INDEX.getValue(), TenPayMsg.TRADE_MODE.getValue(),
//				TenPayMsg.TRADE_STATE.getValue(), TenPayMsg.PAY_INFO.getValue(),
//				TenPayMsg.PARTNER.getValue(), TenPayMsg.BANK_TYPE.getValue(),
//				TenPayMsg.BANK_BILLNO.getValue(), TenPayMsg.TOTAL_FEE.getValue(),
//				TenPayMsg.FEE_TYPE.getValue(), TenPayMsg.NOTIFY_ID.getValue(),
//				TenPayMsg.TRANSACTION_ID.getValue(), TenPayMsg.OUT_TRADE_NO.getValue(),
//				TenPayMsg.ATTACH.getValue(), TenPayMsg.TIME_END.getValue(),
//				TenPayMsg.TRANSPORT_FEE.getValue(), TenPayMsg.PRODUCT_FEE.getValue(),
//				TenPayMsg.DISCOUNT.getValue(), TenPayMsg.BUYER_ALIAS.getValue()};
//	}
	
	@Override
	public Map<String, Object> execute(Map<String, Object> paramsMap)
			throws AppBizException {
		logger.debug("TenPayNoticeAction execute(Object) - start");
		
		// 交易流水临时表对象
		TpayCsysTxnLog transTmpLog = null;
		// 新建一个交易流水表对象
		TpayCsysTxnLog txnLog = new TpayCsysTxnLog();
		// 生成流水号
		Long seqId = upayCsysSeqMapInfoService.selectSeqValue(ExcConstant.TXN_LOG_SEQ);
		String intTxnTime =  DateUtil.getDateyyyyMMddHHmmssSSS();
		// 生成内部交易时间
		String intTxnDate = upayCsysBatCutCtlService.findCutOffDate(ExcConstant.CUT_OFF_DATE);
		
		String intTxnSeq = (String) paramsMap.get("#intTxnSeq");	// 获得内部交易流水
		UpayCsysTransCode upayCsysTransCode = (UpayCsysTransCode) paramsMap.get("#upayCsysTransCode");
		String intTransCode = upayCsysTransCode.getTransCode();	// 获得内部交易代码
		
		// 除去附加参数
		paramsMap.remove("#intTxnSeq");
		paramsMap.remove("#upayCsysTransCode");
		
		boolean isCrmChargeBus = false; // 是否充值成功

		String serviceVersion = (String) paramsMap.get(TenPayMsg.SERVICE_VERSION.getValue());
//		String tradeMode = (String) paramsMap.get(TenPayMsg.TRADE_MODE.getValue());
		String tradeState = (String) paramsMap.get(TenPayMsg.TRADE_STATE.getValue());
		String payInfo = (String) paramsMap.get(TenPayMsg.PAY_INFO.getValue());
		String partner = (String) paramsMap.get(TenPayMsg.PARTNER.getValue());
		String bankType = (String) paramsMap.get(TenPayMsg.BANK_TYPE.getValue());
		String bankBillno = (String) paramsMap.get(TenPayMsg.BANK_BILLNO.getValue());
		String totalFee = (String) paramsMap.get(TenPayMsg.TOTAL_FEE.getValue());
		String notifyId = (String) paramsMap.get(TenPayMsg.NOTIFY_ID.getValue());
		String transactionId = (String) paramsMap.get(TenPayMsg.TRANSACTION_ID.getValue());
		String outTradeNo = (String) paramsMap.get(TenPayMsg.OUT_TRADE_NO.getValue());
//		String attach = (String) paramsMap.get(TenPayMsg.ATTACH.getValue());
		String timeEnd = (String) paramsMap.get(TenPayMsg.TIME_END.getValue());
//		String discount = (String) paramsMap.get(TenPayMsg.DISCOUNT.getValue());
		
		// 结果通知验证查询
		if("open".equals(verifyNotifyId)) {
			try {
				Map<String, String> sendParamsMap = new HashMap<String, String>();
				sendParamsMap.put("#intTxnSeq", intTxnSeq);
				sendParamsMap.put("#orderId", outTradeNo);

				sendParamsMap.put(TenPayMsg.SIGN_TYPE.getValue(), TenPayMsg.SIGN_TYPE.getDesc());
				sendParamsMap.put(TenPayMsg.SERVICE_VERSION.getValue(), TenPayMsg.SERVICE_VERSION.getDesc());
				sendParamsMap.put(TenPayMsg.INPUT_CHARSET.getValue(), TenPayMsg.INPUT_CHARSET.getDesc());
				sendParamsMap.put(TenPayMsg.SIGN.getValue(), null);
				sendParamsMap.put(TenPayMsg.SIGN_KEY_INDEX.getValue(), TenPayMsg.SIGN_KEY_INDEX.getDesc());
				
				sendParamsMap.put(TenPayMsg.PARTNER.getValue(), partner);
				sendParamsMap.put(TenPayMsg.NOTIFY_ID.getValue(), notifyId);
				
				log.info("开始向财富通发起结果通知查询,内部交易流水号:{},内部交易代码:{},财付通商户号:{},订单号:{},支付完成时间:{}," +
						"财付通通知ID:{},财付通订单号:{},财付通返回码:{},财付通返回码描述:{},付款银行:{}", new Object[] {
						intTxnSeq, intTransCode, partner, outTradeNo, timeEnd, notifyId, transactionId, tradeState, payInfo, bankType});
				logger.info("无网关（财付通）支付流水记录,内部交易流水号:{},内部交易代码:{},财付通商户号:{},订单号:{},支付完成时间:{}," +
						"财付通通知ID:{},财付通订单号:{},财付通返回码:{},财付通返回码描述:{},付款银行:{}", new Object[] {
						intTxnSeq, intTransCode, partner, outTradeNo, timeEnd, notifyId, transactionId, tradeState, payInfo, bankType});

				@SuppressWarnings("unchecked")
				Map<String, String> resultMap = (Map<String, String>) sendTenPayJmsMessage.sendMsg(sendParamsMap);
				log.info("接收到财富通发起结果通知查询的应答,内部交易流水号:{},订单号:{},财付通通知ID:{},应答内容:{}", 
						new Object[] {intTxnSeq, outTradeNo, notifyId, resultMap});
				logger.info("接收到财富通发起结果通知查询的应答,内部交易流水号:{},订单号:{},财付通通知ID:{},应答内容:{}", 
						new Object[] {intTxnSeq, outTradeNo, notifyId, resultMap});
				
				if(MapUtils.isNotEmpty(resultMap)) {
					if(!TenPayMsg.RETCODE.getDesc().equals(resultMap.get(TenPayMsg.RETCODE.getValue()))) {
						log.warn("结果通知查询发现原结果通知不真实！！内部交易流水号:{},订单号:{},财付通通知ID:{},应答内容:{}", 
								new Object[] {intTxnSeq, outTradeNo, notifyId, resultMap});
						logger.warn("结果通知查询发现原结果通知不真实！！内部交易流水号:{},订单号:{},财付通通知ID:{},应答内容:{}", 
								new Object[] {intTxnSeq, outTradeNo, notifyId, resultMap});
					} else {
						log.info("结果通知查询发现原结果通知来源真实！内部交易流水号:{},订单号:{},财付通通知ID:{},应答内容:{}", 
								new Object[] {intTxnSeq, outTradeNo, notifyId, resultMap});
						logger.info("结果通知查询发现原结果通知来源真实！内部交易流水号:{},订单号:{},财付通通知ID:{},应答内容:{}", 
								new Object[] {intTxnSeq, outTradeNo, notifyId, resultMap});
					}
				} else {
					log.warn("财富通发起结果通知查询的应答报文格式不正确！内部交易流水号:{},订单号:{},财付通通知ID:{},应答内容:{}", 
							new Object[] {intTxnSeq, outTradeNo, notifyId, resultMap});
					logger.warn("财富通发起结果通知查询的应答报文格式不正确！内部交易流水号:{},订单号:{},财付通通知ID:{},应答内容:{}", 
							new Object[] {intTxnSeq, outTradeNo, notifyId, resultMap});
				}
			} catch(Exception e) {
				log.warn("结果通知查询出错：{},内部交易流水号:{},订单号:{},财付通通知ID:{}", 
						new Object[] {e.getMessage(), intTxnSeq, outTradeNo, notifyId});
				logger.warn("结果通知查询出错：{},内部交易流水号:{},订单号:{},财付通通知ID:{}", 
						new Object[] {e.getMessage(), intTxnSeq, outTradeNo, notifyId});
			}
		}
		
		
		try {
			// 校验报文格式是否正确
			String validateMsg = validataMapMessage(paramsMap);
			if(StringUtils.isNotBlank(validateMsg)) {
				log.error("财付通支付结果通知报文校验失败: {},内部交易流水号:{},内部交易代码:{},财付通商户号:{},订单号:{},支付完成时间:{}," +
						"财付通通知ID:{},财付通订单号:{},财付通返回码:{},财付通返回码描述:{},付款银行:{}", new Object[] {
						validateMsg, intTxnSeq, intTransCode, partner, outTradeNo, timeEnd, notifyId, transactionId, tradeState, payInfo, bankType});
				logger.error("财付通支付结果通知报文校验失败: {},内部交易流水号:{},内部交易代码:{},财付通商户号:{},订单号:{},支付完成时间:{}," +
						"财付通通知ID:{},财付通订单号:{},财付通返回码:{},财付通返回码描述:{},付款银行:{}", new Object[] {
						validateMsg, intTxnSeq, intTransCode, partner, outTradeNo, timeEnd, notifyId, transactionId, tradeState, payInfo, bankType});
				
				return null;
			}

			// 根据orderId和status查找支付流水
			logger.debug("查找流水 oderId: {},status: {}", outTradeNo,
					CommonConstant.TxnStatus.TxnSuccess.getValue());
			Map<String, Object> paramsData = new HashMap<String, Object>();
			paramsData.put("orderId", outTradeNo);
			paramsData.put("status",
					CommonConstant.TxnStatus.TxnSuccess.getValue());
			// 查询交易流水临时表
			transTmpLog = tpayCsysTxnLogTmpService.findObj(paramsData);
			if (null == transTmpLog) {
				log.error("无网关（财付通）支付流水记录,内部交易流水号:{},内部交易代码:{},财付通商户号:{},订单号:{},支付完成时间:{}," +
						"财付通通知ID:{},财付通订单号:{},财付通返回码:{},财付通返回码描述:{},付款银行:{}", new Object[] {
						intTxnSeq, intTransCode, partner, outTradeNo, timeEnd, notifyId, transactionId, tradeState, payInfo, bankType});
				logger.error("无网关（财付通）支付流水记录,内部交易流水号:{},内部交易代码:{},财付通商户号:{},订单号:{},支付完成时间:{}," +
						"财付通通知ID:{},财付通订单号:{},财付通返回码:{},财付通返回码描述:{},付款银行:{}", new Object[] {
						intTxnSeq, intTransCode, partner, outTradeNo, timeEnd, notifyId, transactionId, tradeState, payInfo, bankType});

				return null;
			}

			// 查询交易流水表
			TpayCsysTxnLog trans = tpayCsysTxnLogService.findObj(paramsData);
			if (null != trans) {
				if (CommonConstant.TxnStatus.TxnSuccess.getValue().equals(
						trans.getStatus())) {
					log.info("重复交易,已收到原交易结果通知,并充值成功,内部交易流水号:{},内部交易代码:{},财付通商户号:{},订单号:{},支付完成时间:{}," +
						"财付通通知ID:{},财付通订单号:{},财付通返回码:{},财付通返回码描述:{},付款银行:{}", new Object[] {
						intTxnSeq, intTransCode, partner, outTradeNo, timeEnd, notifyId, transactionId, tradeState, payInfo, bankType});
					logger.info("重复交易,已收到原交易结果通知,并充值成功,内部交易流水号:{},内部交易代码:{},财付通商户号:{},订单号:{},支付完成时间:{}," +
						"财付通通知ID:{},财付通订单号:{},财付通返回码:{},财付通返回码描述:{},付款银行:{}", new Object[] {
						intTxnSeq, intTransCode, partner, outTradeNo, timeEnd, notifyId, transactionId, tradeState, payInfo, bankType});
					
					return null;
				} else {
					logger.warn("已收到原交易结果通知,但充值失败,内部交易流水号:{},内部交易代码:{},财付通商户号:{},订单号:{},支付完成时间:{}," +
						"财付通通知ID:{},财付通订单号:{},财付通返回码:{},财付通返回码描述:{},付款银行:{}", new Object[] {
						intTxnSeq, intTransCode, partner, outTradeNo, timeEnd, notifyId, transactionId, tradeState, payInfo, bankType});
				}
			} else {
				// 支付交易从临时表迁移到交易表
				BeanUtils.copyProperties(transTmpLog, txnLog);
				txnLog.setSeqId(seqId);
				txnLog.setIntTxnSeq(intTxnSeq);
				txnLog.setIntTxnDate(intTxnDate);
				txnLog.setIntTxnTime(intTxnTime);
				txnLog.setSettleDate(timeEnd.substring(0, 8)); 
				txnLog.setSettleAmt(Integer.parseInt(totalFee));
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setBussType(upayCsysTransCode.getBussType());
				txnLog.setBussChl(upayCsysTransCode.getBussChl());
				txnLog.setIntTransCode(intTransCode);
				txnLog.setPayMode(upayCsysTransCode.getPayMode());
//				txnLog.setTraceNo(traceNo);
//				txnLog.setTraceTime(traceTime);
				txnLog.setThrTransId(transactionId);
				txnLog.setOuterRspCode(tradeState);
				txnLog.setOuterRspDesc(payInfo);
//				txnLog.setAccessType(accessType);
//				txnLog.setThrProductType(bizType);
//				txnLog.setOrderTm(txnTime);	// 支付网关
//				txnLog.setBankAccId(accNo);
				txnLog.setThrBankType(bankType);
				txnLog.setPayAmt(Long.parseLong(totalFee));
				txnLog.setReqBipCode(upayCsysTransCode.getReqBipCode());
				txnLog.setReqActivityCode(upayCsysTransCode.getReqActivityCode());
				txnLog.setThrVersion(serviceVersion);
//				txnLog.setThrTxnType(txnType);
//				txnLog.setThrSubTxnType(txnSubType);
				txnLog.setBankOrderId(bankBillno);
				txnLog.setNotifyId(notifyId);
				
				// 保存交易流水表数据
				tpayCsysTxnLogService.add(txnLog);
			}
			
			// 对于重复的交易，更新settleDate
			txnLog.setSettleDate(timeEnd.substring(0, 8)); 
			
			// 如果支付结果通知为支付成功
			if (TenPayMsg.TRADE_STATE.getDesc().equals(tradeState)) {
				
				 // 如果是缴费交易，则向省发起充值
				if(CommonConstant.payStatus.UPAY_STATUS.getValue().equals(transTmpLog.getPayStatus())) {
					// 向省发起充值
					logger.info("向省份发起充值请求,内部交易流水号:{},内部交易代码:{},财付通商户号:{},订单号:{},支付完成时间:{}," +
						"财付通通知ID:{},财付通订单号:{},财付通返回码:{},财付通返回码描述:{},付款银行:{}", new Object[] {
						intTxnSeq, intTransCode, partner, outTradeNo, timeEnd, notifyId, transactionId, tradeState, payInfo, bankType});
					
					/** 报文头 */
					// TransactionID设置成32位
					String reqOprId = Serial
							.genSerialNum(CommonConstant.Sequence.OprId.getValue());
					String transDate = DateUtil.getDateyyyyMMdd();
					String idValue = transTmpLog.getIdValue();
					
					ProvincePhoneNum provincePhoneNum = findProvinceByMobileNumber(idValue);
					String provinceTemp = provincePhoneNum == null ? null
							: provincePhoneNum.getProvinceCode();
					String forwadProvince = StringUtils.isBlank(transTmpLog
							.getIdProvince()) ? provinceTemp : transTmpLog
									.getIdProvince();
					
					if (null == forwadProvince) {
						log.error("手机号:{}不正确,内部交易流水号:{},内部交易代码:{},财付通商户号:{},订单号:{},支付完成时间:{}," +
						"财付通通知ID:{},财付通订单号:{},财付通返回码:{},财付通返回码描述:{},付款银行:{}", new Object[] {
						intTxnSeq, intTransCode, partner, outTradeNo, timeEnd, notifyId, transactionId, tradeState, payInfo, bankType});
						logger.error("手机号:{}不正确,内部交易流水号:{},内部交易代码:{},财付通商户号:{},订单号:{},支付完成时间:{}," +
						"财付通通知ID:{},财付通订单号:{},财付通返回码:{},财付通返回码描述:{},付款银行:{}", new Object[] {
						intTxnSeq, intTransCode, partner, outTradeNo, timeEnd, notifyId, transactionId, tradeState, payInfo, bankType});
						
						txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
								.getDateyyyyMMddHHmmssSSS());
						
						txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A17
								.getValue());
						txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A17
								.getDesc());
						txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A17
								.getValue());
						txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A17
								.getDesc());
						
						txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
								.getValue());
						tpayCsysTxnLogService.modify(txnLog);
						
					} else {
						txnLog.setIdProvince(forwadProvince);
						String forwardOrg = SysMapCache.getProvCd(forwadProvince)
								.getSysCd();
						
						String orgFlag = offOrgTrans(
								OrgId.ORGID_0055.getValue(),
								forwardOrg,
								intTransCode,
								provincePhoneNum != null ? provincePhoneNum
										.getPhoneNumFlag()
										: CommonConstant.PhoneNumType.UNKNOW_PHONENUM
										.getType());
						if (orgFlag != null) {
							logger.warn("CRM无权限!内部交易流水号:{},内部交易代码:{},财付通商户号:{},订单号:{},支付完成时间:{}," +
						"财付通通知ID:{},财付通订单号:{},财付通返回码:{},财付通返回码描述:{},付款银行:{}", new Object[] {
						intTxnSeq, intTransCode, partner, outTradeNo, timeEnd, notifyId, transactionId, tradeState, payInfo, bankType});
							log.info("CRM无权限!内部交易流水号:{},内部交易代码:{},财付通商户号:{},订单号:{},支付完成时间:{}," +
						"财付通通知ID:{},财付通订单号:{},财付通返回码:{},财付通返回码描述:{},付款银行:{}", new Object[] {
						intTxnSeq, intTransCode, partner, outTradeNo, timeEnd, notifyId, transactionId, tradeState, payInfo, bankType});
							
							txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
									.getDateyyyyMMddHHmmssSSS());
							
							txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A18
									.getValue());
							txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A18
									.getDesc() + orgFlag);
							txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A18
									.getValue());
							txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A18
									.getDesc() + orgFlag);
							
							txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
									.getValue());
							
							tpayCsysTxnLogService.modify(txnLog);
							
							// 开始去省端充值
						} else {
							CrmMsgVo forwardMsg = new CrmMsgVo();
							forwardMsg.setTransCode(upayCsysTransCode);
							forwardMsg.setVersion(ExcConstant.CRM_VERSION);
							forwardMsg.setTestFlag(testFlag);
							forwardMsg.setBIPCode(CommonConstant.Bip.Bis14.getValue());
							forwardMsg.setActivityCode(CommonConstant.CrmTrans.Crm07
									.getValue());
							forwardMsg.setActionCode(CommonConstant.ActionCode.Requset
									.getValue());
							forwardMsg.setOrigDomain(CommonConstant.OrgDomain.UPSS
									.getValue());
							forwardMsg.setHomeDomain(CommonConstant.OrgDomain.BOSS
									.getValue());
							forwardMsg.setRouteType(CommonConstant.RouteType.RoutePhone
									.getValue());
							forwardMsg.setRouteValue(transTmpLog.getIdValue());
							
							forwardMsg.setSessionID(String.valueOf(intTxnSeq)); //
							forwardMsg.setTransIDO(String.valueOf(intTxnSeq));
							
							forwardMsg
							.setTransIDOTime(StrUtil.subString(intTxnTime, 0, 14));
							forwardMsg.setMsgSender(CommonConstant.BankOrgCode.CMCC
									.getValue());
							forwardMsg.setMsgReceiver(forwardOrg);//
							
							txnLog.setRcvTransDt(StrUtil.subString(
									forwardMsg.getTransIDOTime(), 0, 8));
							txnLog.setRcvTransId(forwardMsg.getTransIDO());
							txnLog.setRcvTransTm(forwardMsg.getTransIDOTime());
							txnLog.setRcvOprId(reqOprId);
							txnLog.setRcvOprDt(intTxnDate);
							txnLog.setRcvOprTm(StrUtil.subString(intTxnTime, 0, 14));
							
							Map<String, String> params1 = new HashMap<String, String>();
							params1.put("idType", transTmpLog.getIdType());
							params1.put("idValue", transTmpLog.getIdValue());
							params1.put("transactionID", reqOprId);
							params1.put("actionDate", intTxnDate);
							params1.put("actionTime", StrUtil.subString(intTxnTime, 0, 14));
							// 新充值改造的字段
							params1.put("busiTransID",
									String.valueOf(transTmpLog.getReqTransId()));
							params1.put("organID", transTmpLog.getBankId());
							// bussChl 业务渠道
							params1.put("cnlTyp", transTmpLog.getBussChl());
							params1.put("chargeMoney",
									String.valueOf(txnLog.getNeedPayAmt()));
							params1.put("payedType",
									CommonConstant.PayType.PayPre.getValue());
							params1.put("settleDate", txnLog.getSettleDate());
							params1.put("orderNo", outTradeNo);
							params1.put("payment", String.valueOf(txnLog.getPayAmt()));
							params1.put("activityNo", txnLog.getActivityNo());
							
							// 新增字段
							params1.put("orderCnt", txnLog.getOrderCnt() == null ? null
									: txnLog.getOrderCnt().toString());// 订单总数量
							params1.put("prodDiscount",
									txnLog.getProdDiscount() == null ? null : txnLog
											.getProdDiscount().toString());// 产品减折金额
							params1.put("creditCardFee",
									txnLog.getCreditCardFee() == null ? null : txnLog
											.getCreditCardFee().toString());// 信用卡费用
							params1.put("productNo", txnLog.getProductNo());// 产品编号
							
							logger.debug("向省发起充值请求");
							CrmMsgVo rtMsg = tenPayCrmChargeBus.execute(forwardMsg, params1,
									txnLog, null);
							
							logger.debug("省份充值返回结果:{}", rtMsg.getBody());
							
							if (rtMsg.getBody() == null || rtMsg.getBody().equals("")) {
								log.error("省返回充值应答超时,充值失败,返回报文体为空,内部流水:{},发起方:{},订单号:{}", new Object[] {intTxnSeq,
										OrgId.ORGID_0055.getValue(), outTradeNo});
								logger.error("省返回充值应答超时,充值失败,返回报文体为空,内部流水:{},发起方:{}，订单号:{}",
										new Object[] {intTxnSeq, OrgId.ORGID_0055.getValue(), outTradeNo});
								txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
										.getValue());
								
								txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A07
										.getValue());
								txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A07
										.getDesc());
								txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A07
										.getValue());
								txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A07
										.getDesc());
								
								logger.debug(
										"intTxnSeq:{}txnLog 省返回充值应答超时,网关充值失败， 更新支付交易流水",
										intTxnSeq);
								tpayCsysTxnLogService.modify(txnLog);
								logger.debug("intTxnSeq:{}txnLog end 省返回充值应答超时,网关(财付通)充值失败，  更新支付交易流水",
										intTxnSeq);
								
							} else {
								CrmChargeResVo rtBody = (CrmChargeResVo) rtMsg.getBody();
								txnLog.setRcvDomain(forwardMsg.getMsgReceiver());
								txnLog.setRcvRspCode(rtMsg.getRspCode());
								txnLog.setRcvRspDesc(rtMsg.getRspDesc());
								txnLog.setRcvRspType(rtMsg.getRspType());
								txnLog.setChlSubRspCode(rtBody.getRspCode());
								txnLog.setChlSubRspDesc(rtBody.getRspInfo());
								txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
										.getDateyyyyMMddHHmmssSSS());
								txnLog.setIdProvince(forwadProvince);
								String timeStr = rtMsg.getTransIDOTime();
								txnLog.setRcvTranshTm(timeStr);
								txnLog.setRcvTranshDt((timeStr != null && timeStr.length() > 8) ? timeStr.substring(0, 8) : null);
								
								logger.info("支付结果通知返回码:{}描述:{},内部流水:{}",
										new Object[] { rtBody.getRspCode(),
										rtBody.getRspInfo(), intTxnSeq });
								
								if (RspCodeConstant.Wzw.WZW_0000.getValue().equals(
										rtMsg.getRspCode())
										&& RspCodeConstant.Crm.CRM_0000.getValue().equals(
												rtBody.getRspCode())) {
									isCrmChargeBus = true;
									log.succ("{}充值成功,内部流水:{},发起方:{},接收方:{},订单号：{}", new Object[] {
											idValue, intTxnSeq, OrgId.ORGID_0055, forwardOrg, outTradeNo });
									
									txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess
											.getValue());
									
									txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00
											.getValue());
									txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00
											.getDesc());
									txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00
											.getValue());
									txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_010A00
											.getDesc());
									
									logger.info("{}充值成功,内部流水:{},发起方:{},接收方:{},订单号：{}", new Object[] {
											idValue, intTxnSeq, OrgId.ORGID_0055, forwardOrg, outTradeNo});
									
								} else if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(
										rtBody.getRspCode())
										|| RspCodeConstant.Crm.CRM_5A07.getValue().equals(
												rtBody.getRspCode())
												|| RspCodeConstant.Crm.CRM_5A12.getValue().equals(
														rtBody.getRspCode())
														|| RspCodeConstant.Crm.CRM_5A13.getValue().equals(
																rtBody.getRspCode())) {
									
									log.error(
											"{}充值失败,向省{}发起交易结果查询,内部流水:{},发起方:{},订单号：{}",
											new Object[] { idValue, forwardOrg, intTxnSeq,
													OrgId.ORGID_0055, outTradeNo});
									logger.error(
											"{}充值失败,向省{}发起交易结果查询,内部流水:{},发起方:{}",
											new Object[] { idValue, forwardOrg, intTxnSeq,
													OrgId.ORGID_0055 });
									// 超时查询交易状态
									Map<String, Object> params = new HashMap<String, Object>();
									params.put("idProvince", forwadProvince);
									params.put("actionDate", transDate);
									params.put("oriReqSys",
											CommonConstant.BankOrgCode.CMCC.getValue());
									params.put("oprId", reqOprId);
									params.put("activityCode", forwardMsg.getActivityCode());
									// 修改添加idValue 20131212 add by weiyi
									params.put("pidValue", transTmpLog.getIdValue());
									
									CrmMsgVo transQueryRtMsg = null;
									// 查询充值记录
									transQueryRtMsg = crmTransQueryBus.execute(null, params,
											null, null);
									CrmTransQueryResVo transQueryRtBody = (CrmTransQueryResVo) transQueryRtMsg
											.getBody();
									
									if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(
											rtBody.getRspCode())) {
										log.error("向省{}发起交易结果查询超时,内部流水:{}", forwardOrg, intTxnSeq);
										txnLog.setRcvRspType(CommonConstant.CrmRspType.BusErr
												.getValue());
										txnLog.setRcvRspCode(RspCodeConstant.Wzw.WZW_2998
												.getValue());
										txnLog.setRcvRspDesc(RspCodeConstant.Wzw.WZW_2998
												.getDesc());
										txnLog.setRcvSubRspCode(RspCodeConstant.Upay.UPAY_U99998
												.getValue());
										txnLog.setRcvSubRspDesc(RspCodeConstant.Upay.UPAY_U99998
												.getDesc());
									} else {
										txnLog.setRcvRspCode(transQueryRtMsg.getRspCode());
										txnLog.setRcvRspDesc(transQueryRtMsg.getRspDesc());
										txnLog.setRcvRspType(transQueryRtMsg.getRspType());
										txnLog.setRcvSubRspCode(transQueryRtBody.getRspCode());
										txnLog.setRcvSubRspDesc(transQueryRtBody.getRspInfo());
									}
									
									txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
									txnLog.setIdProvince(forwadProvince);
									
									if (RspCodeConstant.Wzw.WZW_0000.getValue().equals(transQueryRtMsg.getRspCode())
											&& RspCodeConstant.Crm.CRM_0000.getValue().equals(transQueryRtBody.getRspCode())) {
										log.succ("{}充值超时查询省:{}交易结果为充值成功", idValue, forwardOrg);
										logger.info("{}充值超时查询省:{}交易结果为充值成功", idValue,forwardOrg);
										txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
										
										txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
										txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
										txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00
												.getValue());
										txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_010A00
												.getDesc());
										
									} else {
										if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(
												rtBody.getRspCode())) {
											log.error("{}向省{}发起交易结果查询超时,返回码:{},描述:{},内部流水:{}",
													new Object[] { idValue, forwardOrg,transQueryRtBody.getRspCode(),
													transQueryRtBody.getRspInfo(),intTxnSeq });
											logger.error("{}向省{}发起交易结果查询超时,返回码:{},描述:{},内部流水:{}",
													new Object[] { idValue, forwardOrg,transQueryRtBody.getRspCode(),
															transQueryRtBody.getRspInfo(),intTxnSeq });
											txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
											txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A07.getValue());
											txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A07.getDesc());
											txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A07
													.getValue());
											txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A07
													.getDesc());
											
										} else {
											log.error(
													"{}充值超时查询省:{}交易结果为充值失败,返回码:{},描述:{},内部流水:{}",
													new Object[] { idValue, forwardOrg,transQueryRtBody.getRspCode(),
															transQueryRtBody.getRspInfo(),intTxnSeq });
											logger.error(
													"{}充值超时查询省:{}交易结果为充值失败,返回码:{},描述:{},内部流水:{}",
													new Object[] { idValue, forwardOrg,
															transQueryRtBody.getRspCode(),transQueryRtBody.getRspInfo(),intTxnSeq });
											txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
											txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A02.getValue());
											txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A02.getDesc());
											txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A02
													.getValue());
											txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A02
													.getDesc());
										}
									}
								} else {
									String errCode = rtBody.getRspCode();
									String errDesc = rtBody.getRspInfo();
									log.error("{}充值失败,向省{}发起交易结果查询,内部流水:{},发起方:{},返回码:{},描述:{}",
											new Object[] { idValue, forwardOrg, intTxnSeq,OrgId.ORGID_0055, errCode, errDesc });
									logger.error(
											"{}充值失败,向省{}发起交易结果查询,内部流水:{},发起方:{},返回码:{},描述:{}",
											new Object[] { idValue, forwardOrg, intTxnSeq,OrgId.ORGID_0055, errCode, errDesc });
									txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
									txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A02.getValue());
									txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A02.getDesc());
									txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A02
											.getValue());
									txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A02
											.getDesc());
								}
								
								logger.debug("intTxnSeq:{}txnLog start 更新支付交易流水", intTxnSeq);
								tpayCsysTxnLogService.modify(txnLog);
								logger.debug("intTxnSeq:{}txnLog end   更新支付交易流水", intTxnSeq);
								logger.info("intTxnSeq:{},订单号{},支付网关(财付通) 省端充值成功，流程完成 ", new Object[] {
										intTxnSeq, outTradeNo });
							}
						}
					}
				// 支付类交易
				} else {
					txnLog.setChlRspCode(Market.MARKET_010A00.getValue());
					txnLog.setChlRspDesc(Market.MARKET_010A00.getDesc());
					txnLog.setChlSubRspCode(Market.MARKET_010A00
							.getValue());
					txnLog.setChlSubRspDesc(Market.MARKET_010A00
							.getDesc());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.toString());
					txnLog.setRcvDomain(BankOrgCode.TENPAY.getValue());
					tpayCsysTxnLogService.modify(txnLog);
					logger.info("支付类的交易直接修改本地流水，intTxnSeq:{},订单号{},移动商城支付成功，流程完成", new Object[] {intTxnSeq, outTradeNo });
				}
			} else {
				txnLog.setChlRspCode(Market.MARKET_010A00.getValue());
				txnLog.setChlRspDesc(Market.MARKET_010A00.getDesc() + "-" + payInfo);
				txnLog.setChlSubRspCode(Market.MARKET_010A00.getValue());
				txnLog.setChlSubRspDesc(Market.MARKET_010A00.getDesc() + "-" + payInfo);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				
				tpayCsysTxnLogService.modify(txnLog);
				logger.error("intTxnSeq:{},订单号{},财付通扣款失败，返回财付通扣款失败!财付通返回码：{}财付通返回码描述：{}",
						new Object[] { intTxnSeq, outTradeNo, tradeState, payInfo});
				log.error("intTxnSeq:{},订单号{},财付通扣款失败，返回财付通扣款失败!财付通返回码：{}财付通返回码描述：{}", new Object[] {
						intTxnSeq, outTradeNo, tradeState, payInfo});
			}
			
			
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			log.error("系统异常!内部交易流水号:{},业务发起方:{}",
					new Object[] { intTxnSeq, OrgId.ORGID_0055 });
			logger.error("系统异常,代码:{},内部交易流水号:{},业务发起方:{}", new Object[] {
					errCode, intTxnSeq, OrgId.ORGID_0055 });
			logger.error("系统异常:", e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());

			if (isCrmChargeBus) {
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00
						.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00
						.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_010A00
						.getDesc());
			} else {
				txnLog.setChlRspCode(errCode);
				txnLog.setChlRspDesc(RspCodeConstant.Bank
						.getDescByValue(errCode));
				txnLog.setChlSubRspCode(errCode);
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank
						.getDescByValue(errCode));
			}

			tpayCsysTxnLogService.modify(txnLog);
		} catch (Exception e) {
			log.error("系统异常!内部交易流水号:{},业务发起方:{}",
					new Object[] { intTxnSeq, OrgId.ORGID_0055 });
			logger.error("系统异常!内部交易流水号:{},业务发起方:{}", new Object[] { intTxnSeq,
					OrgId.ORGID_0055});
			logger.error("系统异常:", e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());

			if (isCrmChargeBus) {
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00
						.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00
						.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_010A00
						.getDesc());
			} else {
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A06
						.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()
						+ ":" + e.getMessage());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A06
						.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A06
						.getDesc() + ":" + e.getMessage());
			}

			tpayCsysTxnLogService.modify(txnLog);
		}
		
		/*
		 *  开始往移动商城发送充值结果通知
		 */
		MobileShopMsgVo mobileShopMsgVo = new MobileShopMsgVo();
		// 设置报文头基本信息
		if(CommonConstant.payStatus.TUPAY_STATUS.getValue().equals(transTmpLog.getPayStatus())) {
			mobileShopMsgVo.setActivityCode("022053");	// 订单支付结果通知
		} else {
			mobileShopMsgVo.setActivityCode("022052");	// 缴费充值结果通知
		}
		mobileShopMsgVo.setReqSys(BankOrgCode.CMCC.getValue());		// 中国移动系统
		mobileShopMsgVo.setReqChannel(CnlType.CmccOwn.getValue());	// 中国移动系统自主发起
		mobileShopMsgVo.setReqTransID(intTxnSeq);
		mobileShopMsgVo.setReqDateTime(intTxnTime);
		mobileShopMsgVo.setReqDate(intTxnDate);
		mobileShopMsgVo.setActionCode(CommonConstant.ActionCode.Requset.getValue());
		mobileShopMsgVo.setRcvSys(OrgId.ORGID_0055.getValue());
		
		// 设置报文体
		MobileShopPayNoticeResVo mobileShopPayNoticeResVo = new MobileShopPayNoticeResVo();
		mobileShopPayNoticeResVo.setOrderID(outTradeNo);
		mobileShopPayNoticeResVo.setSettleDate(txnLog.getSettleDate());
		mobileShopPayNoticeResVo.setSettleOrg(BankOrgCode.TENPAY.getValue());
		mobileShopPayNoticeResVo.setResultCode(txnLog.getChlRspCode());
		mobileShopPayNoticeResVo.setResultDesc(txnLog.getChlRspDesc());
		mobileShopMsgVo.setBody(mobileShopPayNoticeResVo);
		
		txnLog.setReqTranshId(intTxnSeq);
		txnLog.setReqTranshDt(intTxnDate);
		txnLog.setReqTranshTm(intTxnTime);
		tpayCsysTxnLogService.modify(txnLog);
		
		try {
			logger.info("开始向移动商城前置发送支付结果通知! 内部交易流水号:{},内部交易代码:{},订单号:{},移动商城返回码:{},移动商城返回码描述:{}", new Object[] {
					intTxnSeq, intTransCode, outTradeNo, mobileShopPayNoticeResVo.getResultCode(), mobileShopPayNoticeResVo.getResultDesc()});
			mmarketRemoting.sendMsg(transTmpLog.getServerUrl(), MsgHandle.marshaller(mobileShopMsgVo));
			logger.debug("向移动商城前置发送支付结果通知完成。订单号:{};TenPayNoticeAction execute(Object) - end", outTradeNo);
		} catch (Exception e) {
			logger.error("向移动商城前置发送支付结果通知失败! 内部交易流水号:{},内部交易代码:{},订单号:{},移动商城返回码:{},移动商城返回码描述:{},错误信息：{}", new Object[] {
					intTxnSeq, intTransCode, outTradeNo, mobileShopPayNoticeResVo.getResultCode(), mobileShopPayNoticeResVo.getResultDesc(), e.getMessage()});
			logger.error("",e.getMessage());
			log.error("向移动商城前置发送支付结果通知失败! 内部交易流水号:{},内部交易代码:{},订单号:{},移动商城返回码:{},移动商城返回码描述:{},错误信息：{}", new Object[] {
					intTxnSeq, intTransCode, outTradeNo, mobileShopPayNoticeResVo.getResultCode(), mobileShopPayNoticeResVo.getResultDesc(), e.getMessage()});
		}
		return null;
	}

	/**
	 * 校验财付通支付结果通知报文
	 * @param paramsMap
	 * @return
	 */
	private String validataMapMessage(Map<String, Object> paramsMap) {
		String serviceVersion = (String) paramsMap.get(TenPayMsg.SERVICE_VERSION.getValue());
		String tradeMode = (String) paramsMap.get(TenPayMsg.TRADE_MODE.getValue());
		String tradeState = (String) paramsMap.get(TenPayMsg.TRADE_STATE.getValue());
//		String payInfo = (String) paramsMap.get(TenPayMsg.PAY_INFO.getValue());
		String partner = (String) paramsMap.get(TenPayMsg.PARTNER.getValue());
		String bankType = (String) paramsMap.get(TenPayMsg.BANK_TYPE.getValue());
		String bankBillno = (String) paramsMap.get(TenPayMsg.BANK_BILLNO.getValue());
		String totalFee = (String) paramsMap.get(TenPayMsg.TOTAL_FEE.getValue());
		String feeType = (String) paramsMap.get(TenPayMsg.FEE_TYPE.getValue());
		String notifyId = (String) paramsMap.get(TenPayMsg.NOTIFY_ID.getValue());
		String transactionId = (String) paramsMap.get(TenPayMsg.TRANSACTION_ID.getValue());
		String outTradeNo = (String) paramsMap.get(TenPayMsg.OUT_TRADE_NO.getValue());
//		String attach = (String) paramsMap.get(TenPayMsg.ATTACH.getValue());
		String timeEnd = (String) paramsMap.get(TenPayMsg.TIME_END.getValue());
//		String discount = (String) paramsMap.get(TenPayMsg.DISCOUNT.getValue());
		
		StringBuilder validataStr = new StringBuilder();
		
		if(StringUtils.isNotEmpty(serviceVersion) && !TenPayMsg.SERVICE_VERSION.getDesc().equals(serviceVersion)) {
			validataStr.append("SERVICE_VERSION校验失败：" + serviceVersion + " ");
		}
		if(!TenPayMsg.TRADE_MODE.getDesc().equals(tradeMode)) {
			validataStr.append("TRADE_MODE校验失败：" + tradeMode + " ");
		}
		if(StringUtils.isEmpty(tradeState) || !tradeState.matches("\\d+")) {
			validataStr.append("TRADE_STATE校验失败：" + tradeState + " ");
		}
		if(StringUtils.isEmpty(partner) || !partner.matches("\\d{1,10}")) {
			validataStr.append("PARTNER校验失败：" + partner + " ");
		}
		if(StringUtils.isEmpty(bankType) || bankType.length() > 16) {
			validataStr.append("BANK_TYPE校验失败：" + bankType + " ");
		}
		if(StringUtils.isNotEmpty(bankBillno) && bankBillno.length() > 32) {
			validataStr.append("BANK_BILLNO校验失败：" + bankBillno + " ");
		}
		if(StringUtils.isEmpty(totalFee) || !totalFee.matches("\\d{1,10}")) {
			validataStr.append("TOTAL_FEE校验失败：" + totalFee + " ");
		}		
		if(!TenPayMsg.FEE_TYPE.getDesc().equals(feeType)) {
			validataStr.append("FEE_TYPE校验失败：" + feeType + " ");
		}
		if(StringUtils.isEmpty(notifyId) || notifyId.length() > 128) {
			validataStr.append("NOTIFY_ID校验失败：" + notifyId + " ");
		}
		if(StringUtils.isEmpty(transactionId) || transactionId.length() != 28) {
			validataStr.append("TRANSACTION_ID校验失败：" + transactionId + " ");
		}		
		if(StringUtils.isEmpty(outTradeNo) || outTradeNo.length() > 28) {
			validataStr.append("OUT_TRADE_NO校验失败：" + outTradeNo + " ");
		}			
		if(StringUtils.isEmpty(timeEnd) || !timeEnd.matches("\\d{14}")) {
			validataStr.append("TIME_END校验失败：" + timeEnd + " ");
		}		
		
		return validataStr.toString();
	}
	
}
