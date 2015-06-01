package com.huateng.cmupay.thrid.action;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.action.AbsBaseAction;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.constant.TUPayConstant;
import com.huateng.cmupay.constant.CommonConstant.BankOrgCode;
import com.huateng.cmupay.constant.CommonConstant.CnlType;
import com.huateng.cmupay.constant.RspCodeConstant.OrgId;
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
 * 支付宝支付结果通知
 *
 */
@Controller("alipayNoticeAction")
@Scope("prototype")
public class ALIPayNoticeAction extends AbsBaseAction<Map<String, Object>, Map<String, Object>> {
	
	@Autowired
	private ITpayCsysTxnLogTmpService tpayCsysTxnLogTmpService;
	@Autowired
	private TUpayCrmChargeBus tupayCrmChargeBus;
	@Autowired
	private CrmTransQueryBus crmTransQueryBus;
	@Autowired
	private MMarketRemoting mmarketRemoting;
	
	private static final String INFO = "INFO";
	private static final String WARN = "WARN";
	private static final String DEBUG = "DEBUG";
	private static final String ERROR = "ERROR";

	@Override
	public Map<String, Object> execute(Map<String, Object> paramsMap)
			throws AppBizException {
		
		logger.debug("ALIPayNoticeAction execute(Object) - start");

		// 交易流水临时表对象
		TpayCsysTxnLog transTmpLog = null;
		// 新建一个交易流水表对象
		TpayCsysTxnLog txnLog = new TpayCsysTxnLog();
		// 生成流水号
		Long seqId = upayCsysSeqMapInfoService.selectSeqValue(ExcConstant.TXN_LOG_SEQ);
		String intTxnTime =  DateUtil.getDateyyyyMMddHHmmssSSS();
		// 生成内部交易时间
		String intTxnDate = upayCsysBatCutCtlService.findCutOffDate(ExcConstant.CUT_OFF_DATE);
		
		String intTxnSeq = (String) paramsMap.get("#intTxnSeq");	// 获得监听生产的内部交易流水
		UpayCsysTransCode upayCsysTransCode = (UpayCsysTransCode) paramsMap.get("#upayCsysTransCode");
		String intTransCode = upayCsysTransCode.getTransCode();	// 获得内部交易代码
		
		boolean isCrmChargeBus = false; // 是否充值成功
		
		//获取银联参数
		String notifyTime = (String) paramsMap.get(TUPayConstant.ALIPayMsg.NOTIFY_TIME.getValue());//通知时间
		String notifyType = (String) paramsMap.get(TUPayConstant.ALIPayMsg.NOTIFY_TYPE.getValue());//通知类型
		String notifyId = (String) paramsMap.get(TUPayConstant.ALIPayMsg.NOTIFY_ID.getValue());//通知id
		String outTradeNo = (String) paramsMap.get(TUPayConstant.ALIPayMsg.OUT_TRADE_NO.getValue());//订单号    32位
		String tradeNo = (String) paramsMap.get(TUPayConstant.ALIPayMsg.TRADE_NO.getValue());//第三方支付流水   最大64位
		String tradeStatus = (String) paramsMap.get(TUPayConstant.ALIPayMsg.TRADE_STATUS.getValue());//交易状态  ，需转换为统一支付识别的代码
		String gmtPayment = (String) paramsMap.get(TUPayConstant.ALIPayMsg.GMT_PAYMENT.getValue());//付款时间 ，需转换为8位时间
		String totalFee = (String) paramsMap.get(TUPayConstant.ALIPayMsg.TOTAL_FEE.getValue());//订单金额 ， （单位 ：元）需转换
		String outChannelType = (String) paramsMap.get(TUPayConstant.ALIPayMsg.OUT_CHANNEL_TYPE.getValue());//支付渠道组合
		String outChannelAmount = (String) paramsMap.get(TUPayConstant.ALIPayMsg.OUT_CHANNEL_AMOUNT.getValue());//支付金额组合
		String outChannelInst = (String) paramsMap.get(TUPayConstant.ALIPayMsg.OUT_CHANNEL_INST.getValue());//实际支付渠道
		String paymentType = (String) paramsMap.get(TUPayConstant.ALIPayMsg.PAYMENT_TYPE.getValue());//支付类型
		
		String settleDate = null;
		
		
		try {
			//交易状态转码  //TODO 暂时用的银联的，需要定义支付宝的转码
			String marketErrCode = TUPayConstant.getMMarketErrorCode(tradeStatus);
			String marketErrDesc = RspCodeConstant.Market.getDescByValue(marketErrCode);
			
			// 校验报文格式是否正确
			String validateMsg = validataMapMessage(paramsMap);
			if(StringUtils.isNotBlank(validateMsg)) {
				printLog("银联支付结果通知报文校验失败", validateMsg, intTxnSeq, intTransCode, 
						outTradeNo, notifyTime, tradeNo, marketErrCode, marketErrDesc, WARN);
				
				return null;
			}
			
			// 根据orderId和status查找支付流水
			logger.debug("订单号 : {},status: {}", outTradeNo,CommonConstant.TxnStatus.TxnSuccess.getValue());
			Map<String, Object> paramsData = new HashMap<String, Object>();
			paramsData.put("orderId", outTradeNo);
			paramsData.put("status",CommonConstant.TxnStatus.TxnSuccess.getValue());
			// 查询交易流水临时表
			transTmpLog = tpayCsysTxnLogTmpService.findObj(paramsData);
			if (null == transTmpLog) {
				printLog("无网关（支付宝）支付流水记录", "", intTxnSeq, intTransCode, 
						outTradeNo, notifyTime, tradeNo, marketErrCode, marketErrDesc, WARN);

				return null;
			}
			
			// 将四位settleDate改成八位   
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat f1 = new SimpleDateFormat("yyyyMMdd");
			settleDate = StringUtils.isNotBlank(gmtPayment) ? f1.format(f.parse(gmtPayment)) : transTmpLog.getSettleDate();
			
			//订单金额  元转分
			String payment = String.valueOf(Integer.parseInt(totalFee)*100);
			
			//查询交易流水表
			TpayCsysTxnLog trans = tpayCsysTxnLogService.findObj(paramsData);
			if (null != trans) {
				if (CommonConstant.TxnStatus.TxnSuccess.getValue().equals(
						trans.getStatus())) {
					
					printLog("重复交易,已收到原交易结果通知,并充值成功", "", intTxnSeq, intTransCode, 
							outTradeNo, notifyTime, tradeNo, marketErrCode, marketErrDesc, WARN);
					
					return null;
					
				} else {
					
					printLog("已收到原交易结果通知,但充值失败", "", intTxnSeq, intTransCode, 
							outTradeNo, notifyTime, tradeNo, marketErrCode, marketErrDesc, WARN);
					
				}
			} else {
				
				// 支付交易从临时表迁移到交易表
				BeanUtils.copyProperties(transTmpLog, txnLog);
				txnLog.setSeqId(seqId);
				txnLog.setIntTxnSeq(intTxnSeq);
				txnLog.setIntTxnDate(intTxnDate);
				txnLog.setIntTxnTime(intTxnTime);
				txnLog.setIntTransCode(intTransCode);
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setBussType(upayCsysTransCode.getBussType());
				txnLog.setBussChl(upayCsysTransCode.getBussChl());
				txnLog.setPayMode(upayCsysTransCode.getPayMode());
				txnLog.setReqBipCode(upayCsysTransCode.getReqBipCode());
				txnLog.setReqActivityCode(upayCsysTransCode.getReqActivityCode());
				//支付宝数据
				txnLog.setSettleDate(settleDate); 
				txnLog.setThrTransId(tradeNo);
				txnLog.setOuterRspCode(marketErrCode);
				txnLog.setOuterRspDesc(marketErrDesc);
				txnLog.setPayAmt(Long.parseLong(payment));
				txnLog.setThrChannelAmount(outChannelAmount);
				txnLog.setThrChannelType(outChannelType);
				txnLog.setThrBankType(outChannelInst);
				txnLog.setNotifyId(notifyId);
				txnLog.setThrNotifyType(notifyType);
				txnLog.setThrPayType(paymentType);
				
				
				// 保存交易流水表数据
				tpayCsysTxnLogService.add(txnLog);
			}
			
			// 对于重复的交易，更新settleDate
			txnLog.setSettleDate(settleDate);
			
			// 如果支付结果通知为支付成功
			if (UnPayRspCode.UNPAY_00.getValue().equals(marketErrCode)) {
				
				//缴费交易向省发起充值
				if(CommonConstant.payStatus.UPAY_STATUS.getValue().equals(transTmpLog.getPayStatus())) {
					
					printLog("向省份发起充值请求", "", intTxnSeq, intTransCode, 
							outTradeNo, notifyTime, tradeNo, marketErrCode, marketErrDesc, INFO);
					
					String reqOprId = Serial.genSerialNum(CommonConstant.Sequence.OprId.getValue());
					String transDate = DateUtil.getDateyyyyMMdd();
					String idValue = transTmpLog.getIdValue();
					
					ProvincePhoneNum provincePhoneNum = findProvinceByMobileNumber(idValue);
					String provinceTemp = provincePhoneNum == null ? null : provincePhoneNum.getProvinceCode();
					String forwadProvince = StringUtils.isBlank(transTmpLog.getIdProvince()) ? provinceTemp : transTmpLog.getIdProvince();
					
					//手机号码校验
					if (null == forwadProvince) {
						printLog("手机号码不正确", "", intTxnSeq, intTransCode, 
								outTradeNo, notifyTime, tradeNo, RspCodeConstant.Market.MARKET_012A17.getValue(), 
								RspCodeConstant.Market.MARKET_012A17.getDesc(), ERROR);
						
						initRspLog(RspCodeConstant.Market.MARKET_012A17.getValue(), RspCodeConstant.Market.MARKET_012A17.getDesc(), 
								CommonConstant.TxnStatus.TxnFail.toString(), txnLog);
					}else{
						//校验权限
						txnLog.setIdProvince(forwadProvince);
						String forwardOrg = SysMapCache.getProvCd(forwadProvince).getSysCd();
						String orgFlag = offOrgTrans(OrgId.ORGID_0055.getValue(),forwardOrg,intTransCode,
								provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag(): CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
						
						if (orgFlag != null) {
							printLog("此业务渠道无此权限", "", intTxnSeq, intTransCode, 
									outTradeNo, notifyTime, tradeNo, RspCodeConstant.Market.MARKET_012A18.getValue(), 
									RspCodeConstant.Market.MARKET_012A18.getDesc(), ERROR);
							
							initRspLog(RspCodeConstant.Market.MARKET_012A18.getValue(), RspCodeConstant.Market.MARKET_012A18.getDesc(), 
									CommonConstant.TxnStatus.TxnFail.toString(), txnLog);
						}else{
							//发送到省充值
							CrmMsgVo forwardMsg = new CrmMsgVo();
							forwardMsg.setTransCode(upayCsysTransCode);
							forwardMsg.setVersion(ExcConstant.CRM_VERSION);
							forwardMsg.setTestFlag(testFlag);
							forwardMsg.setBIPCode(CommonConstant.Bip.Bis14.getValue());
							forwardMsg.setActivityCode(CommonConstant.CrmTrans.Crm07.getValue());
							forwardMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
							forwardMsg.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
							forwardMsg.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
							forwardMsg.setRouteType(CommonConstant.RouteType.RoutePhone.getValue());
							forwardMsg.setRouteValue(transTmpLog.getIdValue());
							
							forwardMsg.setSessionID(String.valueOf(intTxnSeq)); //
							forwardMsg.setTransIDO(String.valueOf(intTxnSeq));
							
							forwardMsg.setTransIDOTime(StrUtil.subString(intTxnTime, 0, 14));
							forwardMsg.setMsgSender(CommonConstant.BankOrgCode.CMCC.getValue());
							forwardMsg.setMsgReceiver(forwardOrg);//
							
							txnLog.setRcvTransDt(StrUtil.subString(forwardMsg.getTransIDOTime(), 0, 8));
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
							params1.put("busiTransID",String.valueOf(transTmpLog.getReqTransId()));
							params1.put("organID", transTmpLog.getBankId());
							// bussChl 业务渠道
							params1.put("cnlTyp", transTmpLog.getBussChl());
							params1.put("chargeMoney",String.valueOf(txnLog.getNeedPayAmt()));
							params1.put("payedType",CommonConstant.PayType.PayPre.getValue());
							params1.put("settleDate", settleDate);
							params1.put("orderNo", outTradeNo);
							params1.put("payment", String.valueOf(txnLog.getPayAmt()));
							params1.put("activityNo", txnLog.getActivityNo());
							
							// 新增字段
							params1.put("orderCnt", txnLog.getOrderCnt() == null ? null: txnLog.getOrderCnt().toString());// 订单总数量
							params1.put("prodDiscount",txnLog.getProdDiscount() == null ? null : txnLog.getProdDiscount().toString());// 产品减折金额
							params1.put("creditCardFee",txnLog.getCreditCardFee() == null ? null : txnLog.getCreditCardFee().toString());// 信用卡费用
							params1.put("productNo", txnLog.getProductNo());// 产品编号
							
							CrmMsgVo rtMsg = tupayCrmChargeBus.execute(forwardMsg, params1,txnLog, null);
							
							//返回报文体是否为空
							if (rtMsg.getBody() == null || rtMsg.getBody().equals("")) {
								printLog("省返回充值应答超时", "", intTxnSeq, intTransCode, 
										outTradeNo, notifyTime, tradeNo, RspCodeConstant.Market.MARKET_015A07.getValue(), 
										RspCodeConstant.Market.MARKET_015A07.getDesc(), ERROR);
								
								initRspLog(RspCodeConstant.Market.MARKET_015A07.getValue(), RspCodeConstant.Market.MARKET_015A07.getDesc(), 
										CommonConstant.TxnStatus.TxnFail.toString(), txnLog);
							}else{
								//初始化省应答参数
								CrmChargeResVo rtBody = (CrmChargeResVo) rtMsg.getBody();
								txnLog.setRcvDomain(forwardMsg.getMsgReceiver());
								txnLog.setRcvRspCode(rtMsg.getRspCode());
								txnLog.setRcvRspDesc(rtMsg.getRspDesc());
								txnLog.setRcvRspType(rtMsg.getRspType());
								txnLog.setChlSubRspCode(rtBody.getRspCode());
								txnLog.setChlSubRspDesc(rtBody.getRspInfo());
								txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
								txnLog.setIdProvince(forwadProvince);
								String timeStr = rtMsg.getTransIDOTime();
								txnLog.setRcvTranshTm(timeStr);
								txnLog.setRcvTranshDt((timeStr != null && timeStr.length() > 8) ? timeStr.substring(0, 8) : null);
								
								//省应答成功
								if (RspCodeConstant.Wzw.WZW_0000.getValue().equals(rtMsg.getRspCode())
										&& RspCodeConstant.Crm.CRM_0000.getValue().equals(rtBody.getRspCode())) {
									
									isCrmChargeBus = true;
									log.succ("{}充值成功,内部流水:{},发起方:{},接收方:{},订单号：{}", new Object[] {
											idValue, intTxnSeq, OrgId.ORGID_0055, forwardOrg, outTradeNo });
									logger.info("{}充值成功,内部流水:{},发起方:{},接收方:{},订单号：{}", new Object[] {
											idValue, intTxnSeq, OrgId.ORGID_0055, forwardOrg, outTradeNo});
									
									txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
									txnLog.setChlRspCode(RspCodeConstant.Market.MARKET_010A00.getValue());
									txnLog.setChlRspDesc(RspCodeConstant.Market.MARKET_010A00.getDesc());
									txnLog.setChlSubRspCode(RspCodeConstant.Market.MARKET_010A00.getValue());
									txnLog.setChlSubRspDesc(RspCodeConstant.Market.MARKET_010A00.getDesc());
									
								//省应答超时	
								} else if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(rtBody.getRspCode())
										|| RspCodeConstant.Crm.CRM_5A07.getValue().equals(rtBody.getRspCode())
												|| RspCodeConstant.Crm.CRM_5A12.getValue().equals(rtBody.getRspCode())
														|| RspCodeConstant.Crm.CRM_5A13.getValue().equals(rtBody.getRspCode())) {
									
									log.error("{}充值失败,向省{}发起交易结果查询,内部流水:{},发起方:{},订单号：{}",
											new Object[] { idValue, forwardOrg, intTxnSeq,OrgId.ORGID_0055, outTradeNo});
									logger.error("{}充值失败,向省{}发起交易结果查询,内部流水:{},发起方:{},订单号：{}",
											new Object[] { idValue, forwardOrg, intTxnSeq,OrgId.ORGID_0055, outTradeNo });
									
									// 超时查询交易状态
									Map<String, Object> params = new HashMap<String, Object>();
									params.put("idProvince", forwadProvince);
									params.put("actionDate", transDate);
									params.put("oriReqSys",CommonConstant.BankOrgCode.CMCC.getValue());
									params.put("oprId", reqOprId);
									params.put("activityCode", forwardMsg.getActivityCode());
									params.put("pidValue", transTmpLog.getIdValue());
									
									CrmMsgVo transQueryRtMsg = null;
									// 查询充值记录
									transQueryRtMsg = crmTransQueryBus.execute(null, params,null, null);
									CrmTransQueryResVo transQueryRtBody = (CrmTransQueryResVo) transQueryRtMsg.getBody();
									
									if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(rtBody.getRspCode())) {
										log.error("向省{}发起交易结果查询超时,内部流水:{}", forwardOrg, intTxnSeq);
										txnLog.setRcvRspType(CommonConstant.CrmRspType.BusErr.getValue());
										txnLog.setRcvRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
										txnLog.setRcvRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
										txnLog.setRcvSubRspCode(RspCodeConstant.Upay.UPAY_U99998.getValue());
										txnLog.setRcvSubRspDesc(RspCodeConstant.Upay.UPAY_U99998.getDesc());
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
										txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
										txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
										
									}else{
										
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
											txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A07.getValue());
											txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A07.getDesc());
											
										} else {
											log.error("{}充值超时查询省:{}交易结果为充值失败,返回码:{},描述:{},内部流水:{}",
													new Object[] { idValue, forwardOrg,transQueryRtBody.getRspCode(),
															transQueryRtBody.getRspInfo(),intTxnSeq });
											logger.error("{}充值超时查询省:{}交易结果为充值失败,返回码:{},描述:{},内部流水:{}",
													new Object[] { idValue, forwardOrg,
															transQueryRtBody.getRspCode(),transQueryRtBody.getRspInfo(),intTxnSeq });
											txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
											txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A02.getValue());
											txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A02.getDesc());
											txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A02.getValue());
											txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A02.getDesc());
										}
									}

								}else{
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
									txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A02.getValue());
									txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A02.getDesc());
								}
								
								logger.debug("intTxnSeq:{}txnLog start 更新支付交易流水", intTxnSeq);
								tpayCsysTxnLogService.modify(txnLog);
								logger.debug("intTxnSeq:{}txnLog end   更新支付交易流水", intTxnSeq);
								logger.info("intTxnSeq:{},订单号{},支付网关(支付宝) 省端充值成功，流程完成 ", new Object[] {
										intTxnSeq, outTradeNo });
							}
						}
					}
				//支付类交易
				}else{
					initRspLog(RspCodeConstant.Market.MARKET_010A00.getValue(), RspCodeConstant.Market.MARKET_010A00.getDesc(), 
							CommonConstant.TxnStatus.TxnSuccess.toString(), txnLog);
					
					printLog("支付宝支付成功", "", intTxnSeq, intTransCode, 
							outTradeNo, notifyTime, tradeNo, RspCodeConstant.Market.MARKET_010A00.getValue(), 
							RspCodeConstant.Market.MARKET_010A00.getDesc(), INFO);
				}
				
			}else{
				initRspLog(marketErrCode, marketErrDesc, CommonConstant.TxnStatus.TxnFail.toString(), txnLog);
				
				printLog("支付宝扣款失败", "", intTxnSeq, intTransCode, 
						outTradeNo, notifyTime, tradeNo, marketErrCode, marketErrDesc, ERROR);
			}
			
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			String errDesc = RspCodeConstant.Bank.getDescByValue(errCode);
			
			log.error("业务异常!内部交易流水号:{},业务发起方:{}",
					new Object[] { intTxnSeq, OrgId.ORGID_0055 });
			logger.error("业务异常,代码:{},内部交易流水号:{},业务发起方:{}", new Object[] {
					errCode, intTxnSeq, OrgId.ORGID_0055 });
			logger.error("业务异常:", e);
			
			if (isCrmChargeBus) {
				initRspLog(RspCodeConstant.Market.MARKET_010A00.getValue(), RspCodeConstant.Market.MARKET_010A00.getDesc(),
						CommonConstant.TxnStatus.TxnFail.toString(), txnLog);
			}else{
				initRspLog(errCode, errDesc,
						CommonConstant.TxnStatus.TxnFail.toString(), txnLog);
			}
			
		} catch (Exception e) {
			log.error("系统异常!内部交易流水号:{},业务发起方:{}",
					new Object[] { intTxnSeq, OrgId.ORGID_0055 });
			logger.error("系统异常!内部交易流水号:{},业务发起方:{}", new Object[] { 
					intTxnSeq,OrgId.ORGID_0055});
			logger.error("系统异常:", e);
			
			if (isCrmChargeBus) {
				initRspLog(RspCodeConstant.Market.MARKET_010A00.getValue(), RspCodeConstant.Market.MARKET_010A00.getDesc(),
						CommonConstant.TxnStatus.TxnFail.toString(), txnLog);
			}else{
				initRspLog(RspCodeConstant.Market.MARKET_015A06.getValue(), RspCodeConstant.Market.MARKET_015A06.getDesc(),
						CommonConstant.TxnStatus.TxnFail.toString(), txnLog);
			}
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
					intTxnSeq, intTransCode, outTradeNo, mobileShopPayNoticeResVo.getResultCode(), mobileShopPayNoticeResVo.getResultDesc()});
			mmarketRemoting.sendMsg(transTmpLog.getServerUrl(), MsgHandle.marshaller(mobileShopMsgVo));
			logger.debug("向移动商城前置发送支付结果通知完成。订单号:{};ALIPayNoticeAction execute(Object) - end",outTradeNo);
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
	 * 更新流水表数据
	 * @param rspCode
	 * @param rspDesc
	 * @param status
	 * @param resMsg
	 * @param tpayLog
	 */
	private void initRspLog(String rspCode,String rspDesc,String status,TpayCsysTxnLog tpayLog){
		
		tpayLog.setChlRspCode(rspCode);
		tpayLog.setChlRspDesc(rspDesc);
		tpayLog.setChlSubRspCode(rspCode);
		tpayLog.setChlSubRspDesc(rspDesc);
		tpayLog.setStatus(status);
		tpayLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		tpayCsysTxnLogService.modify(tpayLog);
	}
	
	/**
	 * 日志信息打印
	 * @param display
	 * @param tag
	 * @param intTxnSeq
	 * @param intTransCode
	 * @param orderId
	 * @param txnTime
	 * @param thrTransId
	 * @param resCode
	 * @param resDesc
	 * @param flag
	 */
	private void printLog(String display,String tag,String intTxnSeq,String intTransCode,
			String orderId,String txnTime,String thrTransId,String resCode,String resDesc,String flag){
		if(WARN.equalsIgnoreCase(flag)){
			log.warn("{}: {},内部交易流水号:{},内部交易代码:{},订单号:{},订单发送时间:{}," +
					"支付宝交易流水:{},返回码:{},返回码描述:{}", new Object[] {
					display, tag, intTxnSeq, intTransCode, orderId, txnTime, thrTransId,
					resCode, resDesc});
			logger.warn("{}: {},内部交易流水号:{},内部交易代码:{},订单号:{},订单发送时间:{}," +
					"支付宝交易流水:{},返回码:{},返回码描述:{}", new Object[] {
					display, tag, intTxnSeq, intTransCode, orderId, txnTime, thrTransId,
					resCode, resDesc});
		}else if(INFO.equalsIgnoreCase(flag)){
			log.info("{}: {},内部交易流水号:{},内部交易代码:{},订单号:{},订单发送时间:{}," +
					"支付宝交易流水:{},返回码:{},返回码描述:{}", new Object[] {
					display, tag, intTxnSeq, intTransCode, orderId, txnTime, thrTransId,
					resCode, resDesc});
			logger.info("{}: {},内部交易流水号:{},内部交易代码:{},订单号:{},订单发送时间:{}," +
					"支付宝交易流水:{},返回码:{},返回码描述:{}", new Object[] {
					display, tag, intTxnSeq, intTransCode, orderId, txnTime, thrTransId,
					resCode, resDesc});
		}if(DEBUG.equalsIgnoreCase(flag)){
			log.debug("{}: {},内部交易流水号:{},内部交易代码:{},订单号:{},订单发送时间:{}," +
					"支付宝交易流水:{},返回码:{},返回码描述:{}", new Object[] {
					display, tag, intTxnSeq, intTransCode, orderId, txnTime, thrTransId,
					resCode, resDesc});
			logger.debug("{}: {},内部交易流水号:{},内部交易代码:{},订单号:{},订单发送时间:{}," +
					"支付宝交易流水:{},返回码:{},返回码描述:{}", new Object[] {
					display, tag, intTxnSeq, intTransCode, orderId, txnTime, thrTransId,
					resCode, resDesc});
		}if(ERROR.equalsIgnoreCase(flag)){
			log.error("{}: {},内部交易流水号:{},内部交易代码:{},订单号:{},订单发送时间:{}," +
					"支付宝交易流水:{},返回码:{},返回码描述:{}", new Object[] {
					display, tag, intTxnSeq, intTransCode, orderId, txnTime, thrTransId,
					resCode, resDesc});
			logger.error("{}: {},内部交易流水号:{},内部交易代码:{},订单号:{},订单发送时间:{}," +
					"支付宝交易流水:{},返回码:{},返回码描述:{}", new Object[] {
					display, tag, intTxnSeq, intTransCode, orderId, txnTime, thrTransId,
					resCode, resDesc});
		}
		
	}
	
	/**
	 * 校验支付宝数据格式
	 * @param paramsMap
	 * @return
	 */
	private String validataMapMessage(Map<String, Object> paramsMap) {
		StringBuilder validataStr = new StringBuilder();
		
		//获取银联参数
		String notifyTime = (String) paramsMap.get(TUPayConstant.ALIPayMsg.NOTIFY_TIME.getValue());//通知时间
		String notifyType = (String) paramsMap.get(TUPayConstant.ALIPayMsg.NOTIFY_TYPE.getValue());//通知类型
		String notifyId = (String) paramsMap.get(TUPayConstant.ALIPayMsg.NOTIFY_ID.getValue());//通知id
		String outTradeNo = (String) paramsMap.get(TUPayConstant.ALIPayMsg.OUT_TRADE_NO.getValue());//订单号    32位
		String tradeNo = (String) paramsMap.get(TUPayConstant.ALIPayMsg.TRADE_NO.getValue());//第三方支付流水   最大64位
		String tradeStatus = (String) paramsMap.get(TUPayConstant.ALIPayMsg.TRADE_STATUS.getValue());//交易状态  ，需转换为统一支付识别的代码
		String gmtPayment = (String) paramsMap.get(TUPayConstant.ALIPayMsg.GMT_PAYMENT.getValue());//付款时间 ，需转换为8位时间
		String totalFee = (String) paramsMap.get(TUPayConstant.ALIPayMsg.TOTAL_FEE.getValue());//订单金额 ， （单位 ：元）需转换
		String outChannelType = (String) paramsMap.get(TUPayConstant.ALIPayMsg.OUT_CHANNEL_TYPE.getValue());//支付渠道组合
		String outChannelAmount = (String) paramsMap.get(TUPayConstant.ALIPayMsg.OUT_CHANNEL_AMOUNT.getValue());//支付金额组合
		String outChannelInst = (String) paramsMap.get(TUPayConstant.ALIPayMsg.OUT_CHANNEL_INST.getValue());//实际支付渠道
		String paymentType = (String) paramsMap.get(TUPayConstant.ALIPayMsg.PAYMENT_TYPE.getValue());//支付类型
		
		if(StringUtils.isEmpty(notifyTime)){
			validataStr.append("notify_time校验失败：" + notifyTime + " ");
		}
		
		if(StringUtils.isEmpty(notifyType)){
			validataStr.append("notify_type校验失败：" + notifyType + " ");
		}
		
		if(StringUtils.isEmpty(notifyId)){
			validataStr.append("notify_id校验失败：" + notifyId + " ");
		}
		
		if(StringUtils.isEmpty(outTradeNo) || outTradeNo.length() != 32){
			validataStr.append("out_trade_no校验失败：" + outTradeNo + " ");
		}
		
		if(StringUtils.isNotEmpty(outTradeNo) && (tradeNo.length() < 16 || tradeNo.length() > 64)){
			validataStr.append("trade_no校验失败：" + tradeNo + " ");
		}
		
		if(StringUtils.isNotEmpty(totalFee) && !totalFee.matches("\\d{1,10}")){
			validataStr.append("total_fee校验失败：" + totalFee + " ");
		}
		
		return validataStr.toString();
	}

}
