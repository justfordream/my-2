package com.huateng.cmupay.thrid.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.action.AbsBaseAction;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.CommonConstant.BankOrgCode;
import com.huateng.cmupay.constant.CommonConstant.CnlType;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.constant.RspCodeConstant.Market;
import com.huateng.cmupay.constant.RspCodeConstant.OrgId;
import com.huateng.cmupay.constant.TUPayConstant;
import com.huateng.cmupay.constant.TUPayConstant.UnPayRspCode;
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.controller.cache.SysMapCache;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogService;
import com.huateng.cmupay.controller.service.system.ITpayCsysTxnLogTmpService;
import com.huateng.cmupay.controller.service.system.IUpayCsysBatCutCtlService;
import com.huateng.cmupay.controller.service.system.IUpayCsysSeqMapInfoService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.jms.business.crm.CrmTransQueryBus;
import com.huateng.cmupay.jms.business.crm.TUpayCrmChargeBus;
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
 * 银联发来的支付结果通知
 * 
 * @author Manzhizhen
 */
@Controller("tupayNoticeAction")
@Scope("prototype")
public class TUPayNoticeAction extends
		AbsBaseAction<Map<String, Object>, Map<String, Object>> {
	@Autowired
	private ITpayCsysTxnLogService tpayCsysTxnLogService;
	@Autowired
	private ITpayCsysTxnLogTmpService tpayCsysTxnLogTmpService;
	@Autowired
	private IUpayCsysBatCutCtlService upayCsysBatCutCtlService;
	@Autowired
	private IUpayCsysSeqMapInfoService upayCsysSeqMapInfoService;
	@Autowired
	private TUpayCrmChargeBus tupayCrmChargeBus;
	@Autowired
	private CrmTransQueryBus crmTransQueryBus;
	@Autowired
	private MMarketRemoting mmarketRemoting;
	

	@Override
	public Map<String, Object> execute(Map<String, Object> paramsMap)
			throws AppBizException {
		logger.debug("TUPayNoticeAction execute(Object) - start");

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
		
		boolean isCrmChargeBus = false; // 是否充值成功

		String merId = (String) paramsMap.get(TUPayConstant.UnionPayMsg.MERID.getValue());
		String orderId = (String) paramsMap.get(TUPayConstant.UnionPayMsg.ORDERID.getValue());
		String txnTime = (String) paramsMap.get(TUPayConstant.UnionPayMsg.TXNTIME.getValue());
		String queryId = (String) paramsMap.get(TUPayConstant.UnionPayMsg.QUERYID.getValue());
		String respCode = (String) paramsMap.get(TUPayConstant.UnionPayMsg.RESPCODE.getValue());
		String respMsg = (String) paramsMap.get(TUPayConstant.UnionPayMsg.RESPMSG.getValue());
		String settleDate = (String) paramsMap.get(TUPayConstant.UnionPayMsg.SETTLEDATE.getValue());
		String traceNo = (String) paramsMap.get(TUPayConstant.UnionPayMsg.TRACENO.getValue());
		String traceTime = (String) paramsMap.get(TUPayConstant.UnionPayMsg.TRACETIME.getValue());
		String bizType = (String) paramsMap.get(TUPayConstant.UnionPayMsg.BIZTYPE.getValue());
		String accessType = (String) paramsMap.get(TUPayConstant.UnionPayMsg.ACCESSTYPE.getValue());
		String accNo = (String) paramsMap.get(TUPayConstant.UnionPayMsg.ACCNO.getValue());
		String txnAmt = (String) paramsMap.get(TUPayConstant.UnionPayMsg.TXNAMT.getValue());
		String respTime = (String) paramsMap.get(TUPayConstant.UnionPayMsg.RESPTIME.getValue());
		String version = (String) paramsMap.get(TUPayConstant.UnionPayMsg.VERSION.getValue());
		String settleAmt = (String) paramsMap.get(TUPayConstant.UnionPayMsg.SETTLEAMT.getValue());
		String txnType = (String) paramsMap.get(TUPayConstant.UnionPayMsg.TXNTYPE.getValue());
		String txnSubType = (String) paramsMap.get(TUPayConstant.UnionPayMsg.TXNSUBTYPE.getValue());
		
		// 将四位settleDate改成八位
		settleDate = (StringUtils.isNotBlank(settleDate) && settleDate.length() == 4) ? 
				intTxnTime.substring(0, 4) + settleDate : settleDate;
		
		try {
			// 校验报文格式是否正确
			String validateMsg = validataMapMessage(paramsMap);
			if(StringUtils.isNotBlank(validateMsg)) {
				log.error("银联支付结果通知报文校验失败: {},内部交易流水号:{},内部交易代码:{},商户代码:{},订单号:{},订单发送时间:{}," +
						"交易查询流水:{},返回码:{},返回码描述:{},清算日期:{}", new Object[] {
						validateMsg, intTxnSeq, intTransCode, merId, orderId, txnTime, queryId,
						respCode, respMsg, settleDate});
				logger.error("银联支付结果通知报文校验失败: {},内部交易流水号:{},内部交易代码:{},商户代码:{},订单号:{},订单发送时间:{}," +
						"交易查询流水:{},返回码:{},返回码描述:{},清算日期:{}", new Object[] {
						validateMsg, intTxnSeq, intTransCode, merId, orderId, txnTime, queryId,
						respCode, respMsg, settleDate});
				
				return null;
			}

			// 根据orderId和status查找支付流水
			logger.debug("查找流水 oderId: {},status: {}", orderId,
					CommonConstant.TxnStatus.TxnSuccess.getValue());
			Map<String, Object> paramsData = new HashMap<String, Object>();
			paramsData.put("orderId", orderId);
			paramsData.put("status",
					CommonConstant.TxnStatus.TxnSuccess.getValue());
			// 查询交易流水临时表
			transTmpLog = tpayCsysTxnLogTmpService.findObj(paramsData);
			if (null == transTmpLog) {
				log.error("无网关（银联）支付流水记录,内部交易流水号:{},内部交易代码:{},商户代码:{},订单号:{},订单发送时间:{}," +
						"交易查询流水:{},返回码:{},返回码描述:{},清算日期:{}", new Object[] {
						intTxnSeq, intTransCode, merId, orderId, txnTime, queryId,
						respCode, respMsg, settleDate});
				logger.error("无网关（银联）支付流水记录,内部交易流水号:{},内部交易代码:{},商户代码:{},订单号:{},订单发送时间:{}," +
						"交易查询流水:{},返回码:{},返回码描述:{},清算日期:{}", new Object[] {
						intTxnSeq, intTransCode, merId, orderId, txnTime, queryId,
						respCode, respMsg, settleDate});

				return null;
			}

			// 查询交易流水表
			TpayCsysTxnLog trans = tpayCsysTxnLogService.findObj(paramsData);
			if (null != trans) {
				if (CommonConstant.TxnStatus.TxnSuccess.getValue().equals(
						trans.getStatus())) {
					log.info("重复交易,已收到原交易结果通知,并充值成功,内部交易流水号:{},内部交易代码:{},商户代码:{},订单号:{},订单发送时间:{}," +
						"交易查询流水:{},返回码:{},返回码描述:{},清算日期:{}", new Object[] {
						intTxnSeq, intTransCode, merId, orderId, txnTime, queryId,
						respCode, respMsg, settleDate});
					logger.info("重复交易,已收到原交易结果通知,并充值成功,内部交易流水号:{},内部交易代码:{},商户代码:{},订单号:{},订单发送时间:{}," +
						"交易查询流水:{},返回码:{},返回码描述:{},清算日期:{}", new Object[] {
						intTxnSeq, intTransCode, merId, orderId, txnTime, queryId,
						respCode, respMsg, settleDate});
					
					return null;
				} else {
					logger.warn("已收到原交易结果通知,但充值失败,内部交易流水号:{},内部交易代码:{},商户代码:{},订单号:{},订单发送时间:{}," +
						"交易查询流水:{},返回码:{},返回码描述:{},清算日期:{}", new Object[] {
						intTxnSeq, intTransCode, merId, orderId, txnTime, queryId,
						respCode, respMsg, settleDate});
				}
			} else {
				// 支付交易从临时表迁移到交易表
				BeanUtils.copyProperties(transTmpLog, txnLog);
				txnLog.setSeqId(seqId);
				txnLog.setIntTxnSeq(intTxnSeq);
				txnLog.setIntTxnDate(intTxnDate);
				txnLog.setIntTxnTime(intTxnTime);
//				txnLog.setReqTransId(orderId);
//				txnLog.setOrderId(orderId);
//				txnLog.setReqOprId(queryId);
//				txnLog.setReqTransId(queryId);
//				txnLog.setReqTransDt(traceTime);
//				txnLog.setReqTransTm(traceTime);
				txnLog.setSettleDate(settleDate); 
				txnLog.setSettleAmt(Integer.parseInt(settleAmt));
//				txnLog.setReqActivityCode(txnType);
//				txnLog.setReqOprTm(DateUtil.getDateyyyyMMddHHmmssSSS());
//				txnLog.setReqTranshTm(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
//				txnLog.setReqDomain(transTmpLog.getBankId());	// TODO ??这里待确认
				txnLog.setBussType(upayCsysTransCode.getBussType());
				txnLog.setBussChl(upayCsysTransCode.getBussChl());
				txnLog.setIntTransCode(intTransCode);
				txnLog.setPayMode(upayCsysTransCode.getPayMode());
				txnLog.setTraceNo(traceNo);
				txnLog.setTraceTime(traceTime);
				txnLog.setThrTransId(queryId);
//				txnLog.setRcvRspCode(respCode);
//				txnLog.setRcvRspDesc(respMsg);
				txnLog.setOuterRspCode(respCode);
				txnLog.setOuterRspDesc(respMsg);
				txnLog.setAccessType(accessType);
				txnLog.setThrProductType(bizType);
				txnLog.setOrderTm(txnTime);
				txnLog.setBankAccId(accNo);
				txnLog.setPayAmt(Long.parseLong(txnAmt));
				txnLog.setReqBipCode(upayCsysTransCode.getReqBipCode());
				txnLog.setReqActivityCode(upayCsysTransCode.getReqActivityCode());
				txnLog.setThrVersion(version);
				txnLog.setThrTxnType(txnType);
				txnLog.setThrSubTxnType(txnSubType);
				
				// 保存交易流水表数据
				tpayCsysTxnLogService.add(txnLog);
			}
			
			// 对于重复的交易，更新settleDate
			txnLog.setSettleDate(settleDate);
			
			// 如果支付结果通知为支付成功
			if (UnPayRspCode.UNPAY_00.getValue().equals(respCode)) {
				
				 // 如果是缴费交易，则向省发起充值
				if(CommonConstant.payStatus.UPAY_STATUS.getValue().equals(transTmpLog.getPayStatus())) {
					// 向省发起充值
					logger.info("向省份发起充值请求,内部交易流水号:{},内部交易代码:{},商户代码:{},订单号:{},订单发送时间:{}," +
							"交易查询流水:{},返回码:{},返回码描述:{},清算日期:{}", new Object[] {
							intTxnSeq, intTransCode, merId, orderId, txnTime, queryId,
							respCode, respMsg, settleDate});
					
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
						log.error("手机号:{}不正确,内部交易流水号:{},内部交易代码:{},商户代码:{},订单号:{},订单发送时间:{}," +
							"交易查询流水:{},返回码:{},返回码描述:{},清算日期:{}", new Object[] {
							intTxnSeq, intTransCode, merId, orderId, txnTime, queryId,
							respCode, respMsg, settleDate});
						logger.error("手机号:{}不正确,内部交易流水号:{},内部交易代码:{},商户代码:{},订单号:{},订单发送时间:{}," +
							"交易查询流水:{},返回码:{},返回码描述:{},清算日期:{}", new Object[] {
							intTxnSeq, intTransCode, merId, orderId, txnTime, queryId,
							respCode, respMsg, settleDate});
						
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
								OrgId.ORGID_0055.getValue(),	// TODO ?? 这里填移动商城还是bankId？
								forwardOrg,
								intTransCode,
								provincePhoneNum != null ? provincePhoneNum
										.getPhoneNumFlag()
										: CommonConstant.PhoneNumType.UNKNOW_PHONENUM
										.getType());
						if (orgFlag != null) {
							logger.warn("CRM无权限!内部交易流水号:{},内部交易代码:{},商户代码:{},订单号:{},订单发送时间:{}," +
									"交易查询流水:{},返回码:{},返回码描述:{},清算日期:{}", new Object[] {
									intTxnSeq, intTransCode, merId, orderId, txnTime, queryId,
									respCode, respMsg, settleDate});
							log.info("CRM无权限!内部交易流水号:{},内部交易代码:{},商户代码:{},订单号:{},订单发送时间:{}," +
									"交易查询流水:{},返回码:{},返回码描述:{},清算日期:{}", new Object[] {
									intTxnSeq, intTransCode, merId, orderId, txnTime, queryId,
									respCode, respMsg, settleDate});
							
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
									String.valueOf(transTmpLog.getReqTransId()));  // TODO ??这里是否填写正确
							params1.put("organID", transTmpLog.getBankId()); // TODO ??这里是否填写正确
							// bussChl 业务渠道
							params1.put("cnlTyp", transTmpLog.getBussChl());
							params1.put("chargeMoney",
									String.valueOf(txnLog.getNeedPayAmt()));
							params1.put("payedType",
									CommonConstant.PayType.PayPre.getValue());
							params1.put("settleDate", settleDate);
							params1.put("orderNo", orderId);
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
							CrmMsgVo rtMsg = tupayCrmChargeBus.execute(forwardMsg, params1,
									txnLog, null);
							
							logger.debug("省份充值返回结果:{}", rtMsg.getBody());
							
							if (rtMsg.getBody() == null || rtMsg.getBody().equals("")) {
								log.error("省返回充值应答超时,充值失败,返回报文体为空,内部流水:{},发起方:{},订单号:{}", new Object[] {intTxnSeq,
										OrgId.ORGID_0055.getValue(), orderId});
								logger.error("省返回充值应答超时,充值失败,返回报文体为空,内部流水:{},发起方:{}，订单号:{}",
										new Object[] {intTxnSeq, OrgId.ORGID_0055.getValue(), orderId});
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
								logger.debug("intTxnSeq:{}txnLog end 省返回充值应答超时,网关(银联)充值失败，  更新支付交易流水",
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
											idValue, intTxnSeq, OrgId.ORGID_0055, forwardOrg, orderId });
									
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
											idValue, intTxnSeq, OrgId.ORGID_0055, forwardOrg, orderId});
									
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
													OrgId.ORGID_0055, orderId});
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
								logger.info("intTxnSeq:{},订单号{},支付网关(银联) 省端充值成功，流程完成 ", new Object[] {
										intTxnSeq, orderId });
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
//					txnLog.setRcvTranshTm(respTime);
//					txnLog.setRcvTranshDt(respTime.substring(0, 8));
					txnLog.setRcvDomain(BankOrgCode.TPAY.getValue());
					tpayCsysTxnLogService.modify(txnLog);
					logger.info("支付类的交易直接修改本地流水，intTxnSeq:{},订单号{},移动商城支付成功，流程完成", new Object[] {intTxnSeq, orderId });
				}
			} else {
				String mmarketCode = TUPayConstant.getMMarketErrorCode(respCode);
				String mmarketDesc = RspCodeConstant.Market.getDescByValue(mmarketCode);
				txnLog.setChlRspCode(mmarketCode);
				txnLog.setChlRspDesc(mmarketDesc);
				txnLog.setChlSubRspCode(mmarketCode);
				txnLog.setChlSubRspDesc(mmarketDesc);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
//				if(CommonConstant.payStatus.TUPAY_STATUS.getValue().equals(transTmpLog.getPayStatus())) {
//					txnLog.setRcvTranshTm(respTime);
//					txnLog.setRcvTranshDt(respTime.substring(0, 8));
//				}
				
				tpayCsysTxnLogService.modify(txnLog);
				logger.error("intTxnSeq:{},订单号{},银联扣款失败，返回银联扣款失败!返回码：{}返回码描述：{}",
						new Object[] { intTxnSeq, orderId, respCode, respMsg});
				log.error("intTxnSeq:{},订单号{},银联扣款失败，返回银联扣款失败!返回码：{}返回码描述：{}", new Object[] {
						intTxnSeq, orderId, respCode, respMsg});
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
		mobileShopPayNoticeResVo.setOrderID(orderId);
		mobileShopPayNoticeResVo.setSettleDate(settleDate);
		mobileShopPayNoticeResVo.setSettleOrg(BankOrgCode.TPAY.getValue());
		mobileShopPayNoticeResVo.setResultCode(txnLog.getChlRspCode());
		mobileShopPayNoticeResVo.setResultDesc(txnLog.getChlRspDesc());
		mobileShopMsgVo.setBody(mobileShopPayNoticeResVo);
		
		txnLog.setReqTranshId(intTxnSeq);
		txnLog.setReqTranshDt(intTxnDate);
		txnLog.setReqTranshTm(intTxnTime);
		tpayCsysTxnLogService.modify(txnLog);
		
		try {
			logger.info("开始向移动商城前置发送支付结果通知! 内部交易流水号:{},内部交易代码:{},订单号:{},移动商城返回码:{},移动商城返回码描述:{}", new Object[] {
					intTxnSeq, intTransCode, orderId, mobileShopPayNoticeResVo.getResultCode(), mobileShopPayNoticeResVo.getResultDesc()});
			mmarketRemoting.sendMsg(transTmpLog.getServerUrl(), MsgHandle.marshaller(mobileShopMsgVo));
			logger.debug("向移动商城前置发送支付结果通知完成。订单号:{};TUPayNoticeAction execute(Object) - end",orderId);
		} catch (Exception e) {
			logger.error("向移动商城前置发送支付结果通知失败! 内部交易流水号:{},内部交易代码:{},订单号:{},移动商城返回码:{},移动商城返回码描述:{},错误信息：{}", new Object[] {
					intTxnSeq, intTransCode, orderId, mobileShopPayNoticeResVo.getResultCode(), mobileShopPayNoticeResVo.getResultDesc(), e.getMessage()});
			logger.error("",e.getMessage());
			log.error("向移动商城前置发送支付结果通知失败! 内部交易流水号:{},内部交易代码:{},订单号:{},移动商城返回码:{},移动商城返回码描述:{},错误信息：{}", new Object[] {
					intTxnSeq, intTransCode, orderId, mobileShopPayNoticeResVo.getResultCode(), mobileShopPayNoticeResVo.getResultDesc(), e.getMessage()});
		}
		return null;
	}

	/**
	 * 校验银联支付结果通知报文
	 * @param paramsMap
	 * @return
	 */
	private String validataMapMessage(Map<String, Object> paramsMap) {
		String version = (String) paramsMap.get(TUPayConstant.UnionPayMsg.VERSION.getValue());
		String encoding = (String) paramsMap.get(TUPayConstant.UnionPayMsg.ENCODING.getValue());
		String certId = (String) paramsMap.get(TUPayConstant.UnionPayMsg.CERTID.getValue());
		String signature = (String) paramsMap.get(TUPayConstant.UnionPayMsg.SIGNATURE.getValue());
		String txnType = (String) paramsMap.get(TUPayConstant.UnionPayMsg.TXNTYPE.getValue());
//		String txnSubType = (String) paramsMap.get(TUPayConstant.UnionPayMsg.TXNSUBTYPE.getValue());
		String bizType = (String) paramsMap.get(TUPayConstant.UnionPayMsg.BIZTYPE.getValue());
		String accessType = (String) paramsMap.get(TUPayConstant.UnionPayMsg.ACCESSTYPE.getValue());
		String merId = (String) paramsMap.get(TUPayConstant.UnionPayMsg.MERID.getValue());
		String orderId = (String) paramsMap.get(TUPayConstant.UnionPayMsg.ORDERID.getValue());
		String txnTime = (String) paramsMap.get(TUPayConstant.UnionPayMsg.TXNTIME.getValue());
//		String accNo = (String) paramsMap.get(TUPayConstant.UnionPayMsg.ACCNO.getValue());
		String txnAmt = (String) paramsMap.get(TUPayConstant.UnionPayMsg.TXNAMT.getValue()); 
//		String currencyCode = (String) paramsMap.get(TUPayConstant.UnionPayMsg.CURRENCYCODE.getValue());
//		String reqReserved = (String) paramsMap.get(TUPayConstant.UnionPayMsg.REQRESERVED.getValue());
//		String reserved = (String) paramsMap.get(TUPayConstant.UnionPayMsg.RESERVED.getValue());
		String queryId = (String) paramsMap.get(TUPayConstant.UnionPayMsg.QUERYID.getValue());
		String respCode = (String) paramsMap.get(TUPayConstant.UnionPayMsg.RESPCODE.getValue());
		String respMsg = (String) paramsMap.get(TUPayConstant.UnionPayMsg.RESPMSG.getValue());
		String respTime = (String) paramsMap.get(TUPayConstant.UnionPayMsg.RESPTIME.getValue());
		String settleAmt = (String) paramsMap.get(TUPayConstant.UnionPayMsg.SETTLEAMT.getValue());
//		String settleCurrencyCode = (String) paramsMap.get(TUPayConstant.UnionPayMsg.SETTLECURRENCYCODE.getValue());
		String settleDate = (String) paramsMap.get(TUPayConstant.UnionPayMsg.SETTLEDATE.getValue());
		String traceNo = (String) paramsMap.get(TUPayConstant.UnionPayMsg.TRACENO.getValue());
		String traceTime = (String) paramsMap.get(TUPayConstant.UnionPayMsg.TRACETIME.getValue());
		
		StringBuilder validataStr = new StringBuilder();
		
		if(!TUPayConstant.UnionPayMsg.VERSION.getDesc().equals(version)) {
			validataStr.append("version校验失败：" + version + " ");
		}
		if(!TUPayConstant.UnionPayMsg.ENCODING.getDesc().equalsIgnoreCase(encoding)) {
			validataStr.append("encoding校验失败：" + encoding + " ");
		}
		if(StringUtils.isBlank(certId)) {
			validataStr.append("certId校验失败：" + certId + " ");
		}
		if(StringUtils.isBlank(signature)) {
			validataStr.append("signature校验失败：" + signature + " ");
		}
		if(!"01".equals(txnType)) {
			validataStr.append("txnType校验失败：" + txnType + " ");
		}
		if(StringUtils.isBlank(bizType) || !bizType.matches("\\d{6}")) {
			validataStr.append("bizType校验失败：" + bizType + " ");
		}
		if(StringUtils.isBlank(accessType) || !accessType.matches("\\d{1}")) {
			validataStr.append("accessType校验失败：" + accessType + " ");
		}
		if(StringUtils.isBlank(merId) || !merId.matches("\\S{15}")) {
			validataStr.append("merId校验失败：" + merId + " ");
		}
		if(StringUtils.isBlank(orderId) || !orderId.matches("\\S{1,32}")) {
			validataStr.append("orderId校验失败：" + orderId + " ");
		}
		if(StringUtils.isBlank(txnTime) || !txnTime.matches("\\d{14}")) {
			validataStr.append("txnTime校验失败：" + txnTime + " ");
		}
		if(StringUtils.isBlank(txnAmt) || !txnAmt.matches("\\d{1,12}")) {
			validataStr.append("txnAmt校验失败：" + txnAmt + " ");
		}
		if(StringUtils.isBlank(queryId) || !queryId.matches("\\S{21}")) {
			validataStr.append("queryId校验失败：" + queryId + " ");
		}
		if(StringUtils.isBlank(respCode) || !respCode.matches("\\S{2}")) {
			validataStr.append("respCode校验失败：" + respCode + " ");
		}
		if(StringUtils.isBlank(respMsg) || !respMsg.matches(".{1,256}")) {
			validataStr.append("respMsg校验失败：" + respMsg + " ");
		}
		if(StringUtils.isBlank(respTime) || !respTime.matches("\\d{14}")) {
			validataStr.append("respTime校验失败：" + respTime + " ");
		}
		if(StringUtils.isBlank(settleAmt) || !settleAmt.matches("\\d{1,12}")) {
			validataStr.append("settleAmt校验失败：" + settleAmt + " ");
		}
		if(StringUtils.isBlank(settleDate) || !settleDate.matches("\\d{4,8}")) {
			validataStr.append("settleDate校验失败：" + settleDate + " ");
		}		
		if(StringUtils.isBlank(traceNo) || !traceNo.matches("\\d{6}")) {
			validataStr.append("traceNo校验失败：" + traceNo + " ");
		}		
		if(StringUtils.isBlank(traceTime) || !traceTime.matches("\\d{14}")) {
			validataStr.append("traceTime校验失败：" + traceTime + " ");
		}			
				
		return validataStr.toString();
	}
}
