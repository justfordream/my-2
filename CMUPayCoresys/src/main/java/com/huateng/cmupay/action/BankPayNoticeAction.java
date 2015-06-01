package com.huateng.cmupay.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.controller.cache.SysMapCache;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogTmpService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.jms.business.crm.CrmChargeBus;
import com.huateng.cmupay.jms.business.crm.CrmTransQueryBus;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.UpayCsysTxnLogTmp;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankPayNoticeReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmChargeResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmTransQueryResVo;
import com.huateng.cmupay.parseMsg.webgate.vo.crm.CorePayResultNoticeReq;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.json.JacksonUtils;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;

/**
 *  
 * @author j.zeng 支付结果通知
 */
@Controller("bankPayNoticeAction")
@Scope("prototype")
public class BankPayNoticeAction extends AbsBaseAction<BankMsgVo, BankMsgVo> {

	@Autowired
	private IUpayCsysTxnLogTmpService upayCsysTxnLogTmpService;
//	@Autowired
//	private GatePayResultNoticeBus gatePayResultNoticeBus;

	@Autowired
	private CrmChargeBus crmChargeBus;
	@Autowired
	private CrmTransQueryBus crmTransQueryBus;

	@Override
	public BankMsgVo execute(BankMsgVo param) throws AppBizException {

		logger.debug("BankPayNoticeAction execute(Object) - start");
		BankMsgVo reqMsg = param;
		String intTxnTime = param.getTxnTime();
		BankPayNoticeReqVo reqBody = new BankPayNoticeReqVo();
		UpayCsysTxnLogTmp transLog = null;
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		BankMsgVo resMsg = new BankMsgVo();
		resMsg.setBody(null);
		String txnSeq = reqMsg.getTxnSeq();
		boolean isCrmChargeBus=false; //是否充值成功
		try {
			
			MsgHandle.unmarshaller(reqBody, reqMsg.getBody().toString());
			String intTxnDate = reqMsg.getTxnDate();
			UpayCsysTransCode transCode = reqMsg.getTransCode();
			resMsg.setReqChannel(reqMsg.getReqChannel());
			resMsg.setReqDate(reqMsg.getReqDate());
			resMsg.setReqDateTime(reqMsg.getReqDateTime());
			resMsg.setReqSys(reqMsg.getReqSys());
			resMsg.setReqTransID(reqMsg.getReqTransID());
			resMsg.setRcvDate(intTxnDate);
			resMsg.setRcvTransID(txnSeq);
			resMsg.setRcvDateTime(intTxnTime);
			resMsg.setActionCode(CommonConstant.ActionCode.Respone.toString());
            resMsg.setActivityCode(reqMsg.getActivityCode());
            resMsg.setRcvSys(CommonConstant.BankOrgCode.CMCC.getValue());
            
			String validateMsg = this.validateModel(reqBody);
			if (validateMsg == null || "".equals(validateMsg)) {
				logger.debug("success 报文体check,intTxnSeq:{}",new Object[]{txnSeq});
			} else {
				log.error("报文体check失败: {}，内部流水:{},发起方:{}", new Object[] {
						validateMsg,txnSeq, reqMsg.getReqSys() });
				logger.error("报文体check失败: {}，内部流水:{},发起方:{}", new Object[] {
						validateMsg, txnSeq, reqMsg.getReqSys() });
				String code = validateMsg.split(":")[0];

				resMsg.setRspCode(code);
				resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(code));
				return resMsg;
			}

			// 根据orderId和status查找支付流水
			logger.debug("查找流水 oderId: {},status: {}", reqBody.getOrderID(),CommonConstant.TxnStatus.TxnSuccess.getValue());
			Map<String, Object> paramsData = new HashMap<String, Object>();
			paramsData.put("orderId", reqBody.getOrderID());
			paramsData.put("status",CommonConstant.TxnStatus.TxnSuccess.getValue());
			UpayCsysTxnLog trans = upayCsysTxnLogService.findObj(paramsData);
			transLog = upayCsysTxnLogTmpService.findObj(paramsData);
			if (null == transLog) {
				log.error("无网关支付流水记录,订单号:{},内部流水:{},发起方:{}",
						new Object[] { reqBody.getOrderID(),txnSeq, reqMsg.getReqSys() });
				logger.error("无网关支付流水记录,订单号:{},内部流水:{},发起方:{}",
						new Object[] { reqBody.getOrderID(),txnSeq, reqMsg.getReqSys() });

				resMsg.setRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc()
						+ "无网关支付请求流水记录");
				return resMsg;
			}

			logger.debug("原交易流水信息：{}", JacksonUtils.bean2Json(transLog));
			if (null != trans) {
				if (CommonConstant.TxnStatus.TxnSuccess.getValue().equals(trans.getStatus())) {
					log.warn("重复交易,已收到原交易结果通知,并充值成功,内部流水:{},发起方:{}",
							txnSeq, reqMsg.getReqSys());
					logger.warn("重复交易,已收到原交易结果通知,并充值成功,内部流水:{},发起方:{}",
							txnSeq, reqMsg.getReqSys());
					// 已收到原交易结果通知并充值成功,返回
					resMsg.setRspCode(RspCodeConstant.Bank.BANK_013A17.getValue());
					resMsg.setRspDesc(RspCodeConstant.Bank.BANK_013A17.getDesc());
					return resMsg;
				} else {
					logger.warn("已收到原交易结果通知,但充值失败,内部流水:{},发起方:{}",
							txnSeq, reqMsg.getReqSys());
				}
			} else {
				BeanUtils.copyProperties(transLog, txnLog);
				txnLog.setSeqId(param.getSeqId());
				txnLog.setIntTxnSeq(param.getTxnSeq());
				txnLog.setIntTxnDate(param.getTxnDate());
				txnLog.setIntTxnTime(param.getTxnTime());
				txnLog.setReqTransDt(param.getTxnDate());
				txnLog.setReqTransId(param.getTxnSeq());
				txnLog.setOrderId(reqBody.getOrderID());
				txnLog.setReqOprId(reqMsg.getReqTransID());// TODO 对账是否以流水号为准
				txnLog.setReqTransId(reqMsg.getReqTransID());
				txnLog.setReqTransDt(reqMsg.getReqDate());
				txnLog.setReqTransTm(reqMsg.getReqDateTime());
				txnLog.setSettleDate(reqMsg.getReqDate());
				txnLog.setReqActivityCode(reqMsg.getActivityCode());
				txnLog.setReqOprTm(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setReqTranshTm(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				/**
				 * 移动商城发起缴费 reqdomain 填写 0051            渠道 51 内部交易代码  18100001
				 * 省份发起的缴费      reqdomain  填写 省份代码  渠道 03 内部交易代码  16100010
				 */
				if(!transLog.getReqDomain().equals(RspCodeConstant.OrgId.ORGID_0055.getValue())){
					txnLog.setReqDomain(reqMsg.getReqSys());
					txnLog.setBussType(transCode.getBussType());
					txnLog.setBussChl(transCode.getBussChl());
					txnLog.setIntTransCode(transCode.getTransCode());
					txnLog.setPayMode(transCode.getPayMode());
				}
				// 支付交易从临时表迁移到交易表
				txnLog.setSettleDate(reqMsg.getReqDate());
				upayCsysTxnLogService.add(txnLog);
			}
			txnLog.setSettleDate(reqMsg.getReqDate());//对于重复的交易，更新settledate
			// 如果支付结果通知为支付成功，则向省发起充值
			if (reqBody.getRspCode().equals(RspCodeConstant.Bank.BANK_020A00.getValue())) {
				// 向省发起充值
				logger.info("向省份发起充值请求,内部流水:{},发起方:{}", txnSeq,
						reqMsg.getReqSys());
				/** 报文头 */
//				String reqOprId = Serial.genSerialNo(CommonConstant.Sequence.OprId.getValue());
				//TransactionID设置成32位
				String reqOprId = Serial.genSerialNum(CommonConstant.Sequence.OprId.getValue());
				String transDate = DateUtil.getDateyyyyMMdd();
				String idValue = transLog.getIdValue();
				
				ProvincePhoneNum provincePhoneNum = findProvinceByMobileNumber(idValue);
				String provinceTemp =  provincePhoneNum == null ? null : provincePhoneNum.getProvinceCode();
				String forwadProvince = StringUtils.isBlank(transLog.getIdProvince()) ? provinceTemp : transLog.getIdProvince();
				if (null == forwadProvince) {
					log.error("手机号:{}不正确,内部流水:{},发起方:{}", new Object[] {
							idValue, txnSeq, reqMsg.getReqSys() });
					logger.error("手机号:{}不正确,内部流水:{},发起方:{}", new Object[] {
							idValue, txnSeq, reqMsg.getReqSys() });
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					
					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A17.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A17.getDesc());
//					txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A17.getValue());
//					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A17.getDesc());
					
					upayCsysTxnLogService.modify(txnLog);
					resMsg.setRspCode(RspCodeConstant.Bank.BANK_012A17.getValue());
					resMsg.setRspDesc(RspCodeConstant.Bank.BANK_012A17.getDesc());
					return resMsg;
				}
				txnLog.setIdProvince(forwadProvince);
				String forwardOrg = SysMapCache.getProvCd(forwadProvince).getSysCd();
				
				String orgFlag = offOrgTrans(param.getReqSys(), forwardOrg, param.getTransCode().getTransCode(),
						provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
				if(orgFlag !=null){
					logger.warn("CRM无权限!银行流水:{},平台流水:{},银行机构:{},CRM机构:{}", new Object[]{param.getReqTransID(),
							txnSeq,param.getReqSys(),forwardOrg});
					log.info("CRM无权限!银行流水:{},平台流水:{},银行机构:{},CRM机构:{}", new Object[]{param.getReqTransID(),
							txnSeq,param.getReqSys(),forwardOrg});
					
					txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
					
					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc()+orgFlag);
//					txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
//					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc()+orgFlag);
					
					upayCsysTxnLogService.modify(txnLog);
					resMsg.setRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
					resMsg.setRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
					return resMsg;
				}
				
				CrmMsgVo forwardMsg = new CrmMsgVo();
				forwardMsg.setTransCode(transCode);
				forwardMsg.setVersion(ExcConstant.CRM_VERSION);
				forwardMsg.setTestFlag(testFlag);
				forwardMsg.setBIPCode(CommonConstant.Bip.Bis14.getValue());
				forwardMsg.setActivityCode(CommonConstant.CrmTrans.Crm07.getValue());
				forwardMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
				forwardMsg.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
				forwardMsg.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
				forwardMsg.setRouteType(CommonConstant.RouteType.RoutePhone.getValue());
				forwardMsg.setRouteValue(transLog.getIdValue());
				
				forwardMsg.setSessionID(String.valueOf(txnSeq)); //
				forwardMsg.setTransIDO(String.valueOf(txnSeq));
				
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
				params1.put("idType", transLog.getIdType());
				params1.put("idValue", transLog.getIdValue());
				params1.put("transactionID", reqOprId);
				params1.put("actionDate", intTxnDate);
				params1.put("actionTime", StrUtil.subString(intTxnTime, 0, 14));
				//新充值改造的字段
				params1.put("busiTransID", String.valueOf(reqMsg.getReqTransID()));
				
				if(RspCodeConstant.OrgId.ORGID_0055.getValue().equals(transLog.getReqDomain())){
					//如果是商城,OrganID =0055和CnlTyp =51（银行网上银行），然后BusiTransID填写银行的流水
					params1.put("organID",transLog.getReqDomain());
					//busschl 业务渠道
					params1.put("cnlTyp", transLog.getBussChl());
					params1.put("chargeMoney", String.valueOf(txnLog.getNeedPayAmt()));

				}else{
					//如果是省
					params1.put("organID",String.valueOf(reqMsg.getReqSys()));
					params1.put("cnlTyp", reqMsg.getReqChannel());
					params1.put("chargeMoney", String.valueOf(txnLog.getPayAmt()));

				}
				params1.put("payedType",CommonConstant.PayType.PayPre.getValue());
				params1.put("settleDate", reqMsg.getReqDate());
				params1.put("orderNo", String.valueOf(txnLog.getOrderId()));
				params1.put("payment", String.valueOf(txnLog.getPayAmt()));
				params1.put("activityNo",txnLog.getActivityNo());
				
				//新增字段
				params1.put("orderCnt", txnLog.getOrderCnt()==null?null:txnLog.getOrderCnt().toString());//订单总数量
				params1.put("prodDiscount",txnLog.getProdDiscount()==null?null:txnLog.getProdDiscount().toString());//产品减折金额
				params1.put("creditCardFee",txnLog.getCreditCardFee()==null?null:txnLog.getCreditCardFee().toString());//信用卡费用
				params1.put("productNo", txnLog.getProductNo());//产品编号
				
				logger.debug("向省发起充值请求");
				CrmMsgVo rtMsg = crmChargeBus.execute(forwardMsg, params1,txnLog, null);
				
				logger.debug("省份充值返回结果:{}", rtMsg.getBody());

				CorePayResultNoticeReq noticeMsg = new CorePayResultNoticeReq();
				noticeMsg.setBackURL(transLog.getBackUrl());
				noticeMsg.setCurType(CommonConstant.CurType.rmbType.getValue());
				noticeMsg.setMCODE(RspCodeConstant.Crm.GATA_CODE_10002.getValue());
				noticeMsg.setMerID(transLog.getMerId());
				noticeMsg.setMerVAR(transLog.getMerVar());
				noticeMsg.setOrderID(transLog.getOrderId());
				noticeMsg.setOrderTime(transLog.getOrderTm());
				noticeMsg.setPayed(transLog.getPayAmt().toString());
				noticeMsg.setServerURL(transLog.getServerUrl());
				noticeMsg.setSig(reqBody.getSig());
				if (rtMsg.getBody()==null || rtMsg.getBody().equals("")) {
					log.error("省返回充值应答超时,充值失败,返回报文体为空,内部流水:{},发起方:{}",txnSeq, reqMsg.getReqSys());
					logger.error("省返回充值应答超时,充值失败,返回报文体为空,内部流水:{},发起方:{}",txnSeq, reqMsg.getReqSys());
					noticeMsg.setRspCode(RspCodeConstant.Crm.CRM_5A07.getValue());
					noticeMsg.setRspInfo(RspCodeConstant.Crm.CRM_5A07.getDesc());
					resMsg.setRspCode(RspCodeConstant.Bank.BANK_015A07.getValue());
					resMsg.setRspDesc(RspCodeConstant.Bank.BANK_015A07.getDesc());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					
					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A07.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A07.getDesc());
					
//					txnLog.setChlRspCode(RspCodeConstant.Crm.CRM_2998.getValue());
//					txnLog.setChlRspDesc(RspCodeConstant.Crm.CRM_2998.getDesc());
//					txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A02.getValue());
//					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A02.getDesc());
					
					logger.debug("intTxnSeq:{}txnLog 省返回充值应答超时,网关充值失败， 更新支付交易流水",txnSeq);
					upayCsysTxnLogService.modify(txnLog);
					logger.debug("intTxnSeq:{}txnLog end 省返回充值应答超时,网关充值失败，  更新支付交易流水",txnSeq);
					
//					logger.debug("充值失败直接返回 ,noticeMsg start 通知支付网关支付结果,内部流水:{}",txnSeq);
//					gatePayResultNoticeBus.execute(noticeMsg);
//					logger.debug("充值失败直接返回 ,noticeMsg end  通知支付网关支付结果,内部流水:{}",txnSeq);
					return resMsg;
				}
				CrmChargeResVo rtBody =  (CrmChargeResVo) rtMsg.getBody();
				
				txnLog.setRcvActivityCode(forwardMsg.getActivityCode());
				txnLog.setRcvBipCode(forwardMsg.getBIPCode());
				txnLog.setRcvDomain(forwardMsg.getMsgReceiver());
				txnLog.setRcvRouteType(forwardMsg.getRouteType());
				txnLog.setRcvRouteVal(forwardMsg.getRouteValue());
				txnLog.setRcvRspCode(rtMsg.getRspCode());
				txnLog.setRcvRspDesc(rtMsg.getRspDesc());
				txnLog.setRcvRspType(rtMsg.getRspType());
				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setIdProvince(forwadProvince);

				logger.info("支付结果通知返回码:{}描述:{},内部流水:{}",
						new Object[] { rtBody.getRspCode(),rtBody.getRspInfo(), txnSeq });

				if (RspCodeConstant.Wzw.WZW_0000.getValue().equals(rtMsg.getRspCode())
						&& RspCodeConstant.Crm.CRM_0000.getValue().equals(rtBody.getRspCode())) {
					isCrmChargeBus=true;
					log.succ("{}充值成功,内部流水:{},发起方:{},接收方:{}", new Object[] {
							idValue, txnSeq, reqMsg.getReqSys(),forwardOrg });
					
					txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
					
					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
					
//					txnLog.setChlRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
//					txnLog.setChlRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
//					txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
//					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
					
					logger.info("{}充值成功,内部流水:{},发起方:{},接收方:{}", new Object[] {idValue, txnSeq, reqMsg.getReqSys(),forwardOrg });
					
					noticeMsg.setRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
					noticeMsg.setRspInfo(RspCodeConstant.Wzw.WZW_0000.getDesc());
					resMsg.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
					resMsg.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
					
				} else if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(rtBody.getRspCode())
						|| RspCodeConstant.Crm.CRM_5A07.getValue().equals(rtBody.getRspCode())
						|| RspCodeConstant.Crm.CRM_5A12.getValue().equals(rtBody.getRspCode())
						|| RspCodeConstant.Crm.CRM_5A13.getValue().equals(rtBody.getRspCode())) {
					
					log.error("{}充值失败,向省{}发起交易结果查询,内部流水:{},发起方:{}",
							new Object[] { idValue, forwardOrg,txnSeq, reqMsg.getReqSys() });
					logger.error("{}充值失败,向省{}发起交易结果查询,内部流水:{},发起方:{}",
							new Object[] { idValue, forwardOrg,txnSeq, reqMsg.getReqSys() });
					// 超时查询交易状态
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("idProvince", forwadProvince);
					params.put("actionDate", transDate);
					params.put("oriReqSys",CommonConstant.BankOrgCode.CMCC.getValue());
					params.put("oprId", reqOprId);
					params.put("activityCode", forwardMsg.getActivityCode());
					//修改添加idValue 20131212 add by weiyi
					params.put("pidValue", transLog.getIdValue());
					
					CrmMsgVo transQueryRtMsg = null;
					// 查询充值记录
					transQueryRtMsg = crmTransQueryBus.execute(null, params,
							null, null);
					CrmTransQueryResVo transQueryRtBody = (CrmTransQueryResVo) transQueryRtMsg
							.getBody();

					if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(rtBody.getRspCode())) {
						log.error("向省{}发起交易结果查询超时,内部流水:{}", forwardOrg,txnSeq);
						txnLog.setRcvRspType(CommonConstant.CrmRspType.BusErr .getValue());
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
						
//						txnLog.setChlRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
//						txnLog.setChlRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
//						txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
//						txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
						
						
						noticeMsg.setRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
						noticeMsg.setRspInfo(RspCodeConstant.Crm.CRM_0000.getDesc());
						resMsg.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
						resMsg.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
					} else {
						if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(rtBody.getRspCode())) {
							log.error("{}向省{}发起交易结果查询超时,返回码:{},描述:{},内部流水:{}",
									new Object[] { idValue, forwardOrg,transQueryRtBody.getRspCode(),
									transQueryRtBody.getRspInfo(),txnSeq });
							logger.error("{}向省{}发起交易结果查询超时,返回码:{},描述:{},内部流水:{}",
									new Object[] { idValue, forwardOrg,transQueryRtBody.getRspCode(),
									transQueryRtBody.getRspInfo(),txnSeq });
							txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
							
							
							txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A07.getValue());
							txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A07.getDesc());
							noticeMsg.setRspCode(RspCodeConstant.Crm.CRM_2998.getValue());
							noticeMsg.setRspInfo(RspCodeConstant.Crm.CRM_2998.getDesc());
							resMsg.setRspCode(RspCodeConstant.Bank.BANK_015A07.getValue());
							resMsg.setRspDesc(RspCodeConstant.Bank.BANK_015A07.getDesc());
							
						}else{
							log.error("{}充值超时查询省:{}交易结果为充值失败,返回码:{},描述:{},内部流水:{}",
									new Object[] { idValue, forwardOrg,transQueryRtBody.getRspCode(),
									transQueryRtBody.getRspInfo(),txnSeq });
							logger.error("{}充值超时查询省:{}交易结果为充值失败,返回码:{},描述:{},内部流水:{}",
									new Object[] { idValue, forwardOrg,transQueryRtBody.getRspCode(),
											transQueryRtBody.getRspInfo(),txnSeq });
							txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
							
							
							txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A02.getValue());
							txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A02.getDesc());
							
	//						txnLog.setChlRspCode(RspCodeConstant.Crm.CRM_2998.getValue());
	//						txnLog.setChlRspDesc(RspCodeConstant.Crm.CRM_2998.getDesc());
	//						txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A02.getValue());
	//						txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A02.getDesc());
							
							
							noticeMsg.setRspCode(RspCodeConstant.Crm.CRM_2998.getValue());
							noticeMsg.setRspInfo(RspCodeConstant.Crm.CRM_2998.getDesc());
							resMsg.setRspCode(RspCodeConstant.Bank.BANK_015A02.getValue());
							resMsg.setRspDesc(RspCodeConstant.Bank.BANK_015A02.getDesc());
						}
					}
				}else{
					String errCode = rtBody.getRspCode();
					String errDesc = rtBody.getRspInfo();
					log.error("{}充值失败,向省{}发起交易结果查询,内部流水:{},发起方:{},返回码:{},描述:{}",
							new Object[] { idValue, forwardOrg,txnSeq, reqMsg.getReqSys(),errCode,errDesc });
					logger.error("{}充值失败,向省{}发起交易结果查询,内部流水:{},发起方:{},返回码:{},描述:{}",
							new Object[] { idValue, forwardOrg,txnSeq, reqMsg.getReqSys(),errCode,errDesc });
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					
					
					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A02.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A02.getDesc());
					
//					txnLog.setChlRspCode(RspCodeConstant.Crm.CRM_2998.getValue());
//					txnLog.setChlRspDesc(RspCodeConstant.Crm.CRM_2998.getDesc());
//					txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A02.getValue());
//					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A02.getDesc());
					
					
					noticeMsg.setRspCode(RspCodeConstant.Crm.CRM_2998.getValue());
					noticeMsg.setRspInfo(RspCodeConstant.Crm.CRM_2998.getDesc());
					resMsg.setRspCode(RspCodeConstant.Bank.BANK_015A02.getValue());
					resMsg.setRspDesc(RspCodeConstant.Bank.BANK_015A02.getDesc());
				}
				logger.debug("intTxnSeq:{}txnLog start 更新支付交易流水",txnSeq);
				upayCsysTxnLogService.modify(txnLog);
				logger.debug("intTxnSeq:{}txnLog end   更新支付交易流水",txnSeq);
//                try {
 //               	logger.debug("intTxnSeq:{},订单号{},noticeMsg start 通知支付网关开始",new Object[]{txnSeq,transLog.getOrderId()});
//    				gatePayResultNoticeBus.execute(noticeMsg);
    				logger.info("intTxnSeq:{},订单号{},支付网关缴费成功，流程完成",new Object[]{txnSeq,transLog.getOrderId()});
//				} catch (Exception e) {
//					logger.error("intTxnSeq:{},订单号{},noticeMsg error 通知支付网关失败",new Object[]{txnSeq,transLog.getOrderId()});
//					logger.error("失败原因",e);
//					log.error("intTxnSeq:{},订单号{},noticeMsg error 通知支付网关失败",new Object[]{txnSeq,transLog.getOrderId()});
//				}
			}else{
				logger.error("intTxnSeq:{},订单号{},银行扣款失败，返回银行直接充值失败",new Object[]{txnSeq,transLog.getOrderId()});
				log.error("intTxnSeq:{},订单号{},银行扣款失败，返回银行直接充值失败",new Object[]{txnSeq,transLog.getOrderId()});
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_015A02.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_015A02.getDesc());
			}
			logger.debug("BankPayNoticeAction execute(Object) - end");
			return resMsg;
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			log.error("系统异常!内部交易流水号:{},业务发起方:{}",
					new Object[] { txnSeq, reqMsg.getReqSys() });
			logger.error("系统异常,代码:{},内部交易流水号:{},业务发起方:{}",
					new Object[] { errCode,
					txnSeq, reqMsg.getReqSys() });
			logger.error("系统异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			/*txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			
			if(isCrmChargeBus){
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
			}else{
				txnLog.setChlSubRspCode(errCode);
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			}*/
			
			if(isCrmChargeBus){
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
			}else{
				txnLog.setChlRspCode(errCode);
				txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			}
			
			
			
			upayCsysTxnLogService.modify(txnLog);
			if(isCrmChargeBus){
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
			}else{
				resMsg.setRspCode(errCode);
				resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			}
			return resMsg;
		} catch (Exception e) {
			log.error("系统异常!内部交易流水号:{},业务发起方:{}",
					new Object[] { txnSeq, reqMsg.getReqSys() });
			logger.error("系统异常!内部交易流水号:{},业务发起方:{}",
					new Object[] { txnSeq, reqMsg.getReqSys() });
			logger.error("系统异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			
			/*txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+e.getMessage());
			if(isCrmChargeBus){
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
			}else{
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+e.getMessage());
			}*/
			if(isCrmChargeBus){
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
			}else{
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+e.getMessage());
			}
			
			
			upayCsysTxnLogService.modify(txnLog);
			//String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_100?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_100);
			if(isCrmChargeBus){
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
			}else{
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"/*+errDesc*/);
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			}
			return resMsg;
		}

	}
}
