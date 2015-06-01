package com.huateng.cmupay.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.huateng.cmupay.controller.cache.SysMapCache;
import com.huateng.cmupay.controller.service.system.IUpayCsysTmallBillPayService;
import com.huateng.cmupay.controller.service.system.IUpayCsysTmallTxnLogService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.business.crm.CrmChargeBus;
import com.huateng.cmupay.logFormat.TmallMessageLogger;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysTmallBillPay;
import com.huateng.cmupay.models.UpayCsysTmallTxnLog;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmChargeResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.tmall.TmallConsumeReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.tmall.TmallConsumeResVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.cmupay.utils.StringFormat;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;

/**
 * 天猫交易（全网方案）
 * 
 * @author panlg
 * 
 */
@Controller("tmallPayAction")
@Scope("prototype")
public class TmallPayAction extends AbsBaseAction<BankMsgVo, BankMsgVo> {
	private TmallMessageLogger tmallOperLogger = TmallMessageLogger.getLogger(this.getClass());

//天猫日志独立时使用	，把logger替换成tmallLogger即可	
//	private final Logger tmallLogger = LoggerFactory.getLogger("TMALL_FILE");
	@Autowired
	private CrmChargeBus crmChargeBus;
	@Autowired
	protected IUpayCsysTmallTxnLogService upayCsysTmallTxnLogService;
	@Autowired
	protected IUpayCsysTmallBillPayService upayCsysTmallBillPayService;
	
	@Override
	public BankMsgVo execute(BankMsgVo msgVo) throws AppBizException {
		logger.debug("TmallPayAction execute(Object) - start");
		/* 是否是两天以内的重发交易标志：否-false，是-true */
		boolean repTradeFlag = false;
		// 请求报文
		BankMsgVo reqMsg = msgVo;
		TmallConsumeReqVo reqBody = new TmallConsumeReqVo();
		TmallConsumeResVo resBody = new TmallConsumeResVo();
		BankMsgVo resMsg = reqMsg;
		UpayCsysTmallTxnLog txnL = null;
		UpayCsysTmallTxnLog txnLog = new  UpayCsysTmallTxnLog();
		List<UpayCsysTmallTxnLog> txnList = new ArrayList<UpayCsysTmallTxnLog>();
		UpayCsysTmallBillPay tmallBilllog = new UpayCsysTmallBillPay();

		try {
			MsgHandle.unmarshaller(reqBody, (String) reqMsg.getBody());
			reqMsg.setBody(reqBody);
			
			
			/* 验证消息 */
			String checkrtn = validateModel(reqBody);
			if (!"".equals(StringUtil.toTrim(checkrtn))) {
				tmallOperLogger.error("天猫充值,报文体校验失败:{},内部交易流水:{},发起方交易流水号:{},手机号:{},发起方:{},操作流水号:{}", 
						new Object[] {checkrtn, msgVo.getTxnSeq(), msgVo.getReqTransID(), reqBody.getIDValue(),reqMsg.getReqSys(),reqMsg.getReqTransID()});
				logger.error("天猫充值,报文体校验失败:{},内部交易流水:{},发起方交易流水号:{},手机号:{},发起方:{},操作流水号:{}", 
						new Object[] {checkrtn, msgVo.getTxnSeq(), msgVo.getReqTransID(), reqBody.getIDValue(),reqMsg.getReqSys(),reqMsg.getReqTransID()});
//				txnLog.setTmallRspCode(RspCodeConstant.Bank.BANK_014A04.getValue());
//				txnLog.setTmallRspDesc(RspCodeConstant.Bank.BANK_014A04.getDesc() + checkrtn);			
//				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
//				txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
//				upayCsysTmallTxnLogService.modify(txnLog);
				
				
				/*改成异步之后，返回充值结果通知消息改为了请求类型，ActionCode：0，下面的Rcv节点无需重发*/
				resMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
				resMsg.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
				resMsg.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
				resMsg.setRcvSys(CommonConstant.BankOrgCode.TMALL.getValue());
				resMsg.setActivityCode(CommonConstant.TmallTrans.Tmall05.getValue());
				resBody.setOriReqTransID(reqMsg.getReqTransID());
				resBody.setOriReqDate(reqMsg.getReqDate());
				resBody.setOrderID(reqBody.getOrderID());
				resBody.setResultCode(RspCodeConstant.Bank.BANK_014A04.getValue());
				resBody.setResultDesc(RspCodeConstant.Bank.BANK_014A04.getDesc() + "," + checkrtn);
				resMsg.setReqTransID(msgVo.getTxnSeq());
				resMsg.setBody(resBody);
				
				return resMsg;
			}
			
			// 响应报文
			/* 作为银行的接收方交易流水，同时作为upss向crm发起交易的发起方交易流水 */
			String transIDH = msgVo.getTxnSeq();
			/* 作为银行的接收方交易时间，同时作为upss向crm发起交易的发起方交易时间 */
			String transIDHTime = msgVo.getTxnTime();
			String intTxnDate = msgVo.getTxnDate();
			Long seqId = msgVo.getSeqId();
			
//			resMsg.setRcvDate(transIDHTime.substring(0, 8));
//			resMsg.setRcvDateTime(transIDHTime);
//			resMsg.setRcvTransID(transIDH);
//			resMsg.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			/** 交易代码 */
			UpayCsysTransCode transCode = msgVo.getTransCode();
			
			//
			if((Math.abs(DateUtil.getsubDate(reqMsg.getTxnDate(), msgVo.getReqDate())) > 2)){
				tmallOperLogger.error("天猫充值,超过交易限制期限,内部交易流水:{},发起方交易流水号:{},手机号:{},发起方:{},操作流水号:{}",
						new Object[] { msgVo.getTxnSeq(), msgVo.getReqTransID(), reqBody.getIDValue(),reqMsg.getReqSys(),reqMsg.getReqTransID() });
				logger.error("天猫充值,超过交易限制期限,内部交易流水:{},发起方交易流水号:{},手机号:{},发起方:{},操作流水号:{}",
						new Object[] { msgVo.getTxnSeq(), msgVo.getReqTransID(), reqBody.getIDValue(),reqMsg.getReqSys(),reqMsg.getReqTransID() });
				
				resMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
				resMsg.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
				resMsg.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
				resMsg.setRcvSys(CommonConstant.BankOrgCode.TMALL.getValue());
				resMsg.setActivityCode(CommonConstant.TmallTrans.Tmall05.getValue());
				resBody.setOriReqTransID(reqMsg.getReqTransID());
				resBody.setOriReqDate(reqMsg.getReqDate());
				resBody.setOrderID(reqBody.getOrderID());
				resBody.setResultCode(RspCodeConstant.Bank.BANK_013A35.getValue());
				resBody.setResultDesc(RspCodeConstant.Bank.BANK_013A35.getDesc());
				resMsg.setReqTransID(transIDH);
				resMsg.setBody(resBody);
				
				return resMsg;
			}
			
			//判断交易是否是重发交易（在天猫交易成功明细表里查询）
			Map<String, Object> params1 = new HashMap<String, Object>();
			params1.put("tmallTransId", msgVo.getReqTransID());
			params1.put("tmallOrgId", msgVo.getReqSys());
			params1.put("settleDate", msgVo.getReqDate());
			params1.put("bussType", CommonConstant.BussType.OnlineConsumeBus.getValue());
			params1.put("orderParam", "last_upd_time ASC ");
			txnList = upayCsysTmallTxnLogService.findList(params1, null);
			
			if((txnList != null) && (txnList.size() != 0) ){
//				BeanUtils.copyProperties(tmallBilllog, txnLog);
				
				/** 交易流水 */
				tmallOperLogger.info("天猫充值,重发交易,内部交易流水:{},发起方交易流水号:{},手机号:{},发起方:{},操作流水号:{}",
						new Object[] { msgVo.getTxnSeq(), msgVo.getReqTransID(), reqBody.getIDValue(),reqMsg.getReqSys(),reqMsg.getReqTransID() });
				logger.info("天猫充值,重发交易,内部交易流水:{},发起方交易流水号:{},手机号:{},发起方:{},操作流水号:{}",
						new Object[] { msgVo.getTxnSeq(), msgVo.getReqTransID(), reqBody.getIDValue(),reqMsg.getReqSys(),reqMsg.getReqTransID() });
				
				if(Math.abs(DateUtil.getsubDate(reqMsg.getTxnDate(), msgVo.getReqDate())) == 2){
					tmallOperLogger.error("天猫充值,重复交易,重发时间大于两天,内部交易流水:{},发起方交易流水号:{},手机号:{},发起方:{},操作流水号:{}",
							new Object[] {msgVo.getTxnSeq(), msgVo.getReqTransID(), reqBody.getIDValue(),reqMsg.getReqSys(),reqMsg.getReqTransID()});
					logger.error("天猫充值,重复交易,重发时间大于两天,内部交易流水:{},发起方交易流水号:{},手机号:{},发起方:{},操作流水号:{}",
							new Object[] {msgVo.getTxnSeq(), msgVo.getReqTransID(), reqBody.getIDValue(),reqMsg.getReqSys(),reqMsg.getReqTransID()});
					
					
					/*改成异步之后，返回充值结果通知消息改为了请求类型，ActionCode：0，下面的Rcv节点无需重发*/
					resMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
					resMsg.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
					resMsg.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
					resMsg.setRcvSys(CommonConstant.BankOrgCode.TMALL.getValue());
					resMsg.setActivityCode(CommonConstant.TmallTrans.Tmall05.getValue());
					resBody.setOriReqTransID(reqMsg.getReqTransID());
					resBody.setOriReqDate(reqMsg.getReqDate());
					resBody.setOrderID(reqBody.getOrderID());
					resBody.setResultCode(RspCodeConstant.Tmall.TMALL_030A01.getValue());
					resBody.setResultDesc(RspCodeConstant.Tmall.TMALL_030A01.getDesc());
					resMsg.setReqTransID(transIDH);
					resMsg.setBody(resBody);
					
					return resMsg;
				}else if(Math.abs(DateUtil.getsubDate(reqMsg.getTxnDate(), msgVo.getReqDate())) < 2){
//					logger.info("天猫发起方:{},重复订单,内部交易流水:{},天猫发起流水:{}",new Object[]{msgVo.getReqSys(),msgVo.getTxnSeq(),msgVo.getReqTransID()});
//					tmallOperLogger.info("天猫发起方:{},重复订单,内部交易流水:{},天猫发起流水:{}",new Object[]{msgVo.getReqSys(),msgVo.getTxnSeq(),msgVo.getReqTransID()});

					tmallBilllog = upayCsysTmallBillPayService.findObj(params1);
					if(tmallBilllog != null){//说明交易成功明细表里有记录，也就表面该交易是成功的
						tmallOperLogger.info("天猫充值,重复交易,原交易已处理成功,内部交易流水:{},发起方交易流水号:{},手机号:{},发起方:{},操作流水号:{}",
								new Object[] {msgVo.getTxnSeq(), msgVo.getReqTransID(), reqBody.getIDValue(),reqMsg.getReqSys(),reqMsg.getReqTransID()});
						logger.info("天猫充值,重复交易,原交易已处理成功,内部交易流水:{},发起方交易流水号:{},手机号:{},发起方:{},操作流水号:{}",
								new Object[] {msgVo.getTxnSeq(), msgVo.getReqTransID(), reqBody.getIDValue(),reqMsg.getReqSys(),reqMsg.getReqTransID()});
						
						/*改成异步之后，返回充值结果通知消息改为了请求类型，ActionCode：0，下面的Rcv节点无需重发*/
						resMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
						resMsg.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
						resMsg.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
						resMsg.setRcvSys(CommonConstant.BankOrgCode.TMALL.getValue());
						resMsg.setActivityCode(CommonConstant.TmallTrans.Tmall05.getValue());
						resBody.setOriReqTransID(reqMsg.getReqTransID());
						resBody.setOriReqDate(reqMsg.getReqDate());
						resBody.setOrderID(reqBody.getOrderID());
						resBody.setResultCode(RspCodeConstant.Bank.BANK_013A34.getValue());
						resBody.setResultDesc(RspCodeConstant.Bank.BANK_013A34.getDesc());
						resMsg.setReqTransID(transIDH);
						resMsg.setBody(resBody);
						
						return resMsg;
					}else{//交易流水表里有数据，交易成功明细表里没数据，说明交易不成功，需要重做
						tmallOperLogger.info("天猫充值,重复交易,原交易失败,需要重新充值,内部交易流水:{},发起方交易流水号:{},手机号:{},发起方:{},操作流水号:{}",
								new Object[] {msgVo.getTxnSeq(), msgVo.getReqTransID(), reqBody.getIDValue(),reqMsg.getReqSys(),reqMsg.getReqTransID()});
						logger.info("天猫充值,重复交易,原交易失败,需要重新充值:{},内部交易流水:{},发起方交易流水号:{},手机号:{},发起方:{},操作流水号:{}",
								new Object[] {msgVo.getTxnSeq(), msgVo.getReqTransID(), reqBody.getIDValue(),reqMsg.getReqSys(),reqMsg.getReqTransID()});
						
						repTradeFlag = true;
						
						//交易流水表里有多条重复充值交易，只取最近的那条充值交易
						txnL = txnList.get(0);
					}
				}
			}else{
				if((Math.abs(DateUtil.getsubDate(reqMsg.getTxnDate(), msgVo.getReqDate())) == 2)){
					tmallOperLogger.error("天猫充值,超过交易限制期限,当前数据库日切时间:{},内部交易流水:{},发起方交易流水号:{},手机号:{},发起方:{},操作流水号:{}",
							new Object[] { reqMsg.getTxnDate(),msgVo.getTxnSeq(), msgVo.getReqTransID(), reqBody.getIDValue(),reqMsg.getReqSys(),reqMsg.getReqTransID() });
					logger.warn("天猫充值,超过交易限制期限,当前数据库日切时间:{},内部交易流水:{},发起方交易流水号:{},手机号:{},发起方:{},操作流水号:{}",
							new Object[] { reqMsg.getTxnDate(),msgVo.getTxnSeq(), msgVo.getReqTransID(), reqBody.getIDValue(),reqMsg.getReqSys(),reqMsg.getReqTransID() });
					
					resMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
					resMsg.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
					resMsg.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
					resMsg.setRcvSys(CommonConstant.BankOrgCode.TMALL.getValue());
					resMsg.setActivityCode(CommonConstant.TmallTrans.Tmall05.getValue());
					resBody.setOriReqTransID(reqMsg.getReqTransID());
					resBody.setOriReqDate(reqMsg.getReqDate());
					resBody.setOrderID(reqBody.getOrderID());
					resBody.setResultCode(RspCodeConstant.Bank.BANK_013A35.getValue());
					resBody.setResultDesc(RspCodeConstant.Bank.BANK_013A35.getDesc());
					resMsg.setReqTransID(transIDH);
					resMsg.setBody(resBody);
					
					return resMsg;
				}
			}
			
			initLog(txnLog, reqMsg, resMsg, reqBody, seqId, transIDH, intTxnDate, transIDHTime);
			upayCsysTmallTxnLogService.add(txnLog);	
			
//			/** 重发交易不需重新记录天猫交易流水，更新原交易记录即可 */
//			if(!repTradeFlag){
//				initLog(txnLog, reqMsg, resMsg, reqBody, seqId, transIDH, intTxnDate, transIDHTime);
//				txnLog.setSettleDate(reqMsg.getReqDate());
//				upayCsysTmallTxnLogService.add(txnLog);
//			}else{
//				BeanUtils.copyProperties(txnL, txnLog);
//			}
			
			ProvincePhoneNum provincePhoneNum = ProvAreaCache.getProvAreaByPrimary(reqBody.getIDValue());
//			String idProvince = findProvinceByMobileNumber(reqBody.getIDValue());
			String idProvince = provincePhoneNum == null ? null : provincePhoneNum.getProvinceCode();
			if (null == idProvince) {
				tmallOperLogger.warn("天猫充值,手机号码不正确:内部交易流水:{},发起方交易流水号:{},手机号:{},发起方:{},操作流水号:{}",
						new Object[] {msgVo.getTxnSeq(), msgVo.getReqTransID(), reqBody.getIDValue(),reqMsg.getReqSys(),reqMsg.getReqTransID()});
				logger.warn("天猫充值,手机号码不正确:内部交易流水:{},发起方交易流水号:{},手机号:{},发起方:{},操作流水号:{}",
						new Object[] {msgVo.getTxnSeq(), msgVo.getReqTransID(), reqBody.getIDValue(),reqMsg.getReqSys(),reqMsg.getReqTransID()});
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setTmallRspCode(RspCodeConstant.Bank.BANK_012A17.getValue());
				txnLog.setTmallRspDesc(RspCodeConstant.Bank.BANK_012A17.getDesc());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTmallTxnLogService.modify(txnLog);

				/*改成异步之后，返回充值结果通知消息改为了请求类型，ActionCode：0，下面的Rcv节点无需重发*/
				resMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
				resMsg.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
				resMsg.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
				resMsg.setRcvSys(CommonConstant.BankOrgCode.TMALL.getValue());
				resMsg.setActivityCode(CommonConstant.TmallTrans.Tmall05.getValue());
				resBody.setOriReqTransID(reqMsg.getReqTransID());
				resBody.setOriReqDate(reqMsg.getReqDate());
				resBody.setOrderID(reqBody.getOrderID());
				resBody.setResultCode(RspCodeConstant.Bank.BANK_012A17.getValue());
				resBody.setResultDesc(RspCodeConstant.Bank.BANK_012A17.getDesc());
				resMsg.setReqTransID(transIDH);
				resMsg.setBody(resBody);
				
				return resMsg;
			}
			
			/*统一支付应根据手机号码查找的归属地和浙商发过来的归属地对比*/
			if(!idProvince.equals(txnLog.getHomeProv())){
				tmallOperLogger.warn("天猫充值,省代码和手机号码归属省不一致:内部交易流水:{},发起方交易流水号:{},手机号:{},发起方:{},浙商侧归属地:{},UPAY侧归属地:{}",
						new Object[] {msgVo.getTxnSeq(), msgVo.getReqTransID(), reqBody.getIDValue(),reqMsg.getReqSys(),txnLog.getHomeProv(),idProvince});
				logger.warn("天猫充值,省代码和手机号码归属省不一致:内部交易流水:{},发起方交易流水号:{},手机号:{},发起方:{},发起方:{},浙商侧归属地:{},UPAY侧归属地:{}",
						new Object[] {msgVo.getTxnSeq(), msgVo.getReqTransID(), reqBody.getIDValue(),reqMsg.getReqSys(),txnLog.getHomeProv(),idProvince});
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setTmallRspCode(RspCodeConstant.Bank.BANK_014A10.getValue());
				txnLog.setTmallRspDesc(RspCodeConstant.Bank.BANK_014A10.getDesc() + ",浙商侧归属地:" + txnLog.getHomeProv() + ",UPAY侧归属地:" + idProvince);
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTmallTxnLogService.modify(txnLog);

				/*改成异步之后，返回充值结果通知消息改为了请求类型，ActionCode：0，下面的Rcv节点无需重发*/
				resMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
				resMsg.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
				resMsg.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
				resMsg.setRcvSys(CommonConstant.BankOrgCode.TMALL.getValue());
				resMsg.setActivityCode(CommonConstant.TmallTrans.Tmall05.getValue());
				resBody.setOriReqTransID(reqMsg.getReqTransID());
				resBody.setOriReqDate(reqMsg.getReqDate());
				resBody.setOrderID(reqBody.getOrderID());
				resBody.setResultCode(RspCodeConstant.Bank.BANK_014A10.getValue());
				resBody.setResultDesc(RspCodeConstant.Bank.BANK_014A10.getDesc() + ",浙商侧归属地:" + txnLog.getHomeProv() + ",UPAY侧归属地:" + idProvince);
				resMsg.setReqTransID(transIDH);
				resMsg.setBody(resBody);
				
				return resMsg;
			}
			
			txnLog.setIdProvince(idProvince);
			String forwardOrg = SysMapCache.getProvCd(idProvince).getSysCd();// 转发方机构代码
//			boolean checkFlag = false;// 接收方机构状态正常标??

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
			forwardMsg.setRouteType(CommonConstant.RouteType.RoutePhone.getValue());
			forwardMsg.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
			forwardMsg.setRouteValue(reqBody.getIDValue());
			if(repTradeFlag){//处理重复交易
				forwardMsg.setSessionID(txnL.getCrmSessionId());
				forwardMsg.setTransIDOTime(StrUtil.subString(txnL.getCrmTransTm(), 0, 14));
				
				txnLog.setCrmSessionId(txnL.getCrmSessionId());
				txnLog.setCrmTransDt(txnL.getCrmTransDt());
				txnLog.setCrmTransTm(txnL.getCrmTransTm());
			}else{
				forwardMsg.setSessionID(transIDH);
				forwardMsg.setTransIDOTime(StrUtil.subString(transIDHTime, 0, 14));
				
				txnLog.setCrmSessionId(transIDH);
				txnLog.setCrmTransDt(intTxnDate);
				txnLog.setCrmTransTm(transIDHTime);
			}
			forwardMsg.setTransIDO(transIDH);
			forwardMsg.setMsgSender(CommonConstant.BankOrgCode.CMCC.getValue());
			forwardMsg.setMsgReceiver(forwardOrg);//

			txnLog.setCrmBipCode(forwardMsg.getBIPCode());
			txnLog.setCrmActivityCode(forwardMsg.getActivityCode());
//			txnLog.setOriOrgId(forwardOrg);
			txnLog.setCrmRouteType(forwardMsg.getRouteType());
			txnLog.setCrmRouteVal(forwardMsg.getRouteValue());
			txnLog.setCrmOrgId(forwardOrg);
			
//			checkFlag = orgStatusCheck(forwardOrg);
//			checkFlag = isO2OTransOn(reqMsg.getReqSys(), forwardOrg, msgVo.getTransCode().getTransCode());
//			String checkFlag = offOrgTrans(reqMsg.getReqSys(), forwardOrg, msgVo.getTransCode().getTransCode());
			String checkFlag = offOrgTrans(reqMsg.getReqSys(), forwardOrg, msgVo.getTransCode().getTransCode(), 
					provincePhoneNum == null ? CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType() : provincePhoneNum.getPhoneNumFlag());
			if (checkFlag == null) {
				String oprId = Serial.genSerialNos(CommonConstant.Sequence.OprId.toString());
				
				/*发往省端报文体 begin*/
				Map<String, String> params = new HashMap<String, String>();
				if(repTradeFlag){//处理重复交易
					txnLog.setIntTxnSeq(txnL.getIntTxnSeq());
					params.put("idType", txnL.getIdType());
					params.put("idValue", txnL.getIdValue());
					params.put("busiTransID", reqMsg.getReqTransID());
					params.put("payTransID", txnL.getPayTransId());
					params.put("transactionID", txnL.getCrmOprId());
					params.put("actionDate", txnL.getCrmOprDt());
					params.put("actionTime", txnL.getCrmOprTm());
					
					params.put("chargeMoney", txnL.getChargeMoney() != null ? String.valueOf(reqBody.getChargeMoney()) : null);
					params.put("organID", reqMsg.getReqSys());//0051
					params.put("cnlTyp", reqMsg.getReqChannel());
					params.put("payedType", CommonConstant.PayType.PayPre.getValue());
					params.put("settleDate", reqMsg.getReqDate());
					params.put("orderNo", txnL.getOrderId());
					params.put("productNo", txnL.getProdId());
					params.put("payment", txnL.getPayment() != null ? String.valueOf(reqBody.getPayment()) : null);
					params.put("orderCnt", txnL.getProdCnt() != null ? String.valueOf(reqBody.getProdCnt()) : null);
					params.put("commision", txnL.getCommision() != null ? String.valueOf(reqBody.getCommision()) : null);
					params.put("rebateFee", txnL.getRebateFee() != null ? String.valueOf(reqBody.getRebateFee()) : null);
					params.put("prodDiscount", txnL.getProdDiscount() != null ? String.valueOf(reqBody.getProdDiscount()) : null);
					params.put("creditCardFee", txnL.getCreditCardFee() != null ? String.valueOf(reqBody.getCreditCardFee()) : null);
					params.put("serviceFee", txnL.getServiceFee() != null ? String.valueOf(reqBody.getServiceFee()) : null);
					params.put("activityNo", txnL.getActivityNo());
					params.put("productShelfNo", txnL.getProdShelfNo());
					
					txnLog.setIntTxnDate(txnL.getIntTxnDate());
					txnLog.setIntTxnTime(txnL.getIntTxnTime());
					txnLog.setCrmOprTm(txnL.getCrmOprTm());
				}else{
					params.put("idType", reqBody.getIDType());
					params.put("idValue", reqBody.getIDValue());
					params.put("busiTransID", reqMsg.getReqTransID());
					params.put("payTransID", reqBody.getPayTransID());
					params.put("transactionID", oprId);
					params.put("actionDate", intTxnDate);
					params.put("actionTime", StrUtil.subString(transIDHTime, 0, 14));
					
					params.put("chargeMoney", reqBody.getChargeMoney() != null ? String.valueOf(reqBody.getChargeMoney()) : null);
					params.put("organID", reqMsg.getReqSys());//0051
					params.put("cnlTyp", reqMsg.getReqChannel());
					params.put("payedType", CommonConstant.PayType.PayPre.getValue());
					params.put("settleDate", reqMsg.getReqDate());
					params.put("orderNo", reqBody.getOrderID());
					params.put("productNo", reqBody.getProdId());
					params.put("payment", reqBody.getPayment() != null ? String.valueOf(reqBody.getPayment()) : null);
					params.put("orderCnt", reqBody.getProdCnt() != null ? String.valueOf(reqBody.getProdCnt()) : null);
					params.put("commision", reqBody.getCommision() != null ? String.valueOf(reqBody.getCommision()) : null);
					params.put("rebateFee", reqBody.getRebateFee() != null ? String.valueOf(reqBody.getRebateFee()) : null);
					params.put("prodDiscount", reqBody.getProdDiscount() != null ? String.valueOf(reqBody.getProdDiscount()) : null);
					params.put("creditCardFee", reqBody.getCreditCardFee() != null ? String.valueOf(reqBody.getCreditCardFee()) : null);
					params.put("serviceFee", reqBody.getServiceFee() != null ? String.valueOf(reqBody.getServiceFee()) : null);
					params.put("activityNo", reqBody.getActivityNO());
					params.put("productShelfNo", reqBody.getProdShelfNO());
					
					txnLog.setCrmOprTm(StrUtil.subString(transIDHTime, 0, 14));
				}

				/*发往省端报文体 end*/
				
				txnLog.setCrmTransId(forwardMsg.getTransIDO());
				txnLog.setCrmOprDt(intTxnDate);
				txnLog.setCrmStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
				logger.info("天猫充值,开始手机:{}充值,内部交易流水:{},发起方:{},接收方:{},操作流水号:{}",
						new Object[] { reqBody.getIDValue(), msgVo.getTxnSeq(),reqMsg.getReqSys(), forwardMsg.getMsgReceiver(), reqMsg.getReqTransID()});
				forwardRtMsg = crmChargeBus.tmallExecute(forwardMsg, params, txnLog, null);
				txnLog.setCrmEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
				
				//重发的交易，天猫请求流水号、省操作流水号、省请求交易流水号要和原交易一样
				if(repTradeFlag){
//					txnLog.setCrmTransId(txnL.getCrmTransId());
					txnLog.setCrmOprId(txnL.getCrmOprId());
				}else{
					txnLog.setCrmOprId(oprId);
				}
				
				txnLog.setCrmTranshId(forwardRtMsg.getTransIDH());
				txnLog.setCrmTranshDt(StrUtil.subString(forwardRtMsg.getTransIDHTime(), 0, 8));
				txnLog.setCrmTranshTm(forwardRtMsg.getTransIDHTime());
				logger.info("天猫充值,手机:{}充值返回,内部交易流水:{},发起方:{},接收方:{}, 发起方交易流水号:{}",
						new Object[] { reqBody.getIDValue(), msgVo.getTxnSeq(), reqMsg.getReqSys(), forwardMsg.getMsgReceiver(), reqMsg.getReqTransID()});
				CrmChargeResVo rtBody = null;
				if ("".equals(forwardRtMsg.getBody())) {
					tmallOperLogger.error("天猫充值,省充值返回报文体为空,内部交易流水:{},发起方:{},接收方:{},发起方交易流水号:{}",
							new Object[] { msgVo.getTxnSeq(),reqMsg.getReqSys(),forwardMsg.getMsgReceiver(), reqMsg.getReqTransID() });
					logger.error("天猫充值,省充值返回报文体为空,内部交易流水:{},发起方:{},接收方:{},发起方交易流水号:{}",
							new Object[] { msgVo.getTxnSeq(),reqMsg.getReqSys(),forwardMsg.getMsgReceiver(), reqMsg.getReqTransID() });

					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setTmallRspCode(RspCodeConstant.Bank.BANK_015A07.getValue());
					txnLog.setTmallRspDesc(RspCodeConstant.Bank.BANK_015A07.getDesc() + ":充值网状网超时" + checkFlag);
					txnLog.setCrmRspCode(forwardRtMsg.getRspCode());
					txnLog.setCrmRspDesc(forwardRtMsg.getRspDesc());
					txnLog.setCrmRspType(forwardRtMsg.getRspType());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTmallTxnLogService.modify(txnLog);

					/*改成异步之后，返回充值结果通知消息改为了请求类型，ActionCode：0，下面的Rcv节点无需重发*/
					resMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
					resMsg.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
					resMsg.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
					resMsg.setRcvSys(CommonConstant.BankOrgCode.TMALL.getValue());
					resMsg.setActivityCode(CommonConstant.TmallTrans.Tmall05.getValue());
					resBody.setOriReqTransID(reqMsg.getReqTransID());
					resBody.setOriReqDate(reqMsg.getReqDate());
					resBody.setOrderID(reqBody.getOrderID());
					resBody.setResultCode(RspCodeConstant.Bank.BANK_015A07.getValue());
					resBody.setResultDesc(RspCodeConstant.Bank.BANK_015A07.getDesc() + ":充值网状网超时");
					resMsg.setReqTransID(transIDH);
					resMsg.setBody(resBody);
					
					return resMsg;
				}else{
					rtBody =  (CrmChargeResVo) forwardRtMsg.getBody();
				}
				
				if (RspCodeConstant.Wzw.WZW_0000.getValue().equals(forwardRtMsg.getRspCode())
						&& RspCodeConstant.Crm.CRM_0000.getValue().equals(rtBody.getRspCode())) {
					tmallOperLogger.succ(
							"天猫充值成功,往浙江发起充值结果通知,内部交易流水:{},发起方:{},接收方:{},发起方交易流水号:{}",
							new Object[] { msgVo.getTxnSeq(),reqMsg.getReqSys(),forwardMsg.getMsgReceiver(), reqMsg.getReqTransID() });
					logger.info(
							"天猫充值成功,往浙江发起充值结果通知,内部交易流水:{},发起方:{},接收方:{},发起方交易流水号:{}",
							new Object[] { msgVo.getTxnSeq(),reqMsg.getReqSys(),forwardMsg.getMsgReceiver(), reqMsg.getReqTransID() });
					
					txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
					txnLog.setTmallRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
					txnLog.setTmallRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
					txnLog.setCrmRspCode(forwardRtMsg.getRspCode());
					txnLog.setCrmRspDesc(forwardRtMsg.getRspDesc());
					txnLog.setCrmRspType(forwardRtMsg.getRspType());
					txnLog.setCrmSubRspCode(rtBody.getRspCode());
					txnLog.setCrmSubRspDesc(rtBody.getRspInfo());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
//					upayCsysTmallTxnLogService.modify(txnLog);
					
					
					/*改成异步之后，返回充值结果通知消息改为了请求类型，ActionCode：0，下面的Rcv节点无需重发*/
					resMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
					resMsg.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
					resMsg.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
					resMsg.setRcvSys(CommonConstant.BankOrgCode.TMALL.getValue());
					resMsg.setActivityCode(CommonConstant.TmallTrans.Tmall05.getValue());
					resBody.setOriReqTransID(reqMsg.getReqTransID());
					resBody.setOriReqDate(reqMsg.getReqDate());
					resBody.setOrderID(reqBody.getOrderID());
					resBody.setResultCode(RspCodeConstant.Bank.BANK_010A00.getValue());
					resBody.setResultDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
					resBody.setResultTime(forwardRtMsg.getTransIDHTime());
					resMsg.setReqTransID(transIDH);
					resMsg.setBody(resBody);
					logger.debug("BankPayAction execute(Object) - end");
					
//					if(repTradeFlag){
//						int repTradeNum = 0;
//						if((tmallBilllog.getReserved1() != null) && (!"".equals(tmallBilllog.getReserved1()))){
//							if(StringUtil.isNumber(tmallBilllog.getReserved1())){
//								//重发次数加1
//								repTradeNum  = Integer.parseInt(tmallBilllog.getReserved1()) + 1;
//								tmallBilllog.setReserved1("" + repTradeNum);
//							}
//						}else{
//							tmallBilllog.setReserved1("" + (repTradeNum + 1));
//						}
//						
//						tmallBilllog.setIntTxnDate(intTxnDate);
//						tmallBilllog.setIntTxnTime(transIDHTime);
//						tmallBilllog.setSettleDate(reqMsg.getReqDate());
//						upayCsysTmallBillPayService.modify(tmallBilllog);
//					}else{
//						//插入成功明细表
//						tmallBilllog =  new UpayCsysTmallBillPay(); 
//						initTmallBilllog(txnLog, tmallBilllog);
//						upayCsysTmallBillPayService.add(tmallBilllog);
//					}
					 
					//插入成功明细表
					tmallBilllog =  new UpayCsysTmallBillPay(); 
					initTmallBilllog(txnLog, tmallBilllog);
					
					if(repTradeFlag){//处理重复交易
						tmallBilllog.setCrmOprId(txnL.getCrmOprId());
						tmallBilllog.setCrmTransId(txnL.getCrmTransId());
					}
					upayCsysTmallBillPayService.modifyAdd(tmallBilllog, txnLog);
					
					return resMsg;
				}
////////////////////////////////////
				else if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(forwardRtMsg.getRspCode())) {
					tmallOperLogger.error("天猫充值,省充值返回超时,内部交易流水:{},发起方:{},接收方:{},手机号:{},发起方交易流水号:{}",
							new Object[] { msgVo.getTxnSeq(),reqMsg.getReqSys(),forwardMsg.getMsgReceiver(), reqBody.getIDValue(),reqMsg.getReqTransID() });
					logger.error("天猫充值,省充值返回超时,内部交易流水:{},发起方:{},接收方:{},手机号:{},发起方交易流水号:{}",
							new Object[] { msgVo.getTxnSeq(),reqMsg.getReqSys(),forwardMsg.getMsgReceiver(), reqBody.getIDValue(), reqMsg.getReqTransID() });
					
					txnLog.setStatus(CommonConstant.TxnStatus.TxnTimeOut.getValue());
					txnLog.setTmallRspCode(RspCodeConstant.Bank.BANK_015A07.getValue());
					txnLog.setTmallRspDesc(RspCodeConstant.Bank.BANK_015A07.getDesc());
					txnLog.setCrmRspCode(forwardRtMsg.getRspCode());
					txnLog.setCrmRspDesc(forwardRtMsg.getRspDesc());
					txnLog.setCrmRspType(forwardRtMsg.getRspType());
					txnLog.setCrmSubRspCode(rtBody.getRspCode());
					txnLog.setCrmSubRspDesc(rtBody.getRspInfo());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTmallTxnLogService.modify(txnLog);
					
//					/**
//					 * 省返回超时也记为成功交易
//					 */
//					tmallBilllog =  new UpayCsysTmallBillPay(); 
//					initTmallBilllog(txnLog, tmallBilllog);
//					if(repTradeFlag){//处理重复交易
//						tmallBilllog.setCrmOprId(txnL.getCrmOprId());
//						tmallBilllog.setCrmTransId(txnL.getCrmTransId());
//					}
//					upayCsysTmallBillPayService.modifyAdd(tmallBilllog, txnLog);

					/*改成异步之后，返回充值结果通知消息改为了请求类型，ActionCode：0，下面的Rcv节点无需重发*/
					resMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
					resMsg.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
					resMsg.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
					resMsg.setRcvSys(CommonConstant.BankOrgCode.TMALL.getValue());
					resMsg.setActivityCode(CommonConstant.TmallTrans.Tmall05.getValue());
					resBody.setOriReqTransID(reqMsg.getReqTransID());
					resBody.setOriReqDate(reqMsg.getReqDate());
					resBody.setOrderID(reqBody.getOrderID());
					resBody.setResultCode(RspCodeConstant.Bank.BANK_015A07.getValue());
					resBody.setResultDesc(RspCodeConstant.Bank.BANK_015A07.getDesc());
					resMsg.setReqTransID(transIDH);
					resMsg.setBody(resBody);
					logger.debug("BankPayAction execute(Object) - end");
					
					return resMsg;
				} else {
					tmallOperLogger.error("天猫充值,省充值返回失败,内部交易流水:{},发起方:{},接收方:{},手机号:{},返回码:{},操作流水号:{}",
							new Object[] { msgVo.getTxnSeq(),reqMsg.getReqSys(),forwardMsg.getMsgReceiver(), reqBody.getIDValue(),rtBody.getRspCode(), reqMsg.getReqTransID() });
					logger.error("天猫充值,省充值返回失败,内部交易流水:{},发起方:{},接收方:{},手机号:{},返回码:{},操作流水号:{}",
							new Object[] { msgVo.getTxnSeq(),reqMsg.getReqSys(),forwardMsg.getMsgReceiver(), reqBody.getIDValue(),rtBody.getRspCode(), reqMsg.getReqTransID() });
					String errName = BankErrorCodeCache.getBankErrCode(rtBody.getRspCode());
					if("012998".equals(errName)){
						errName = RspCodeConstant.Bank.BANK_015A06.getValue();
					}
					logger.error("天猫充值,省内部交易流水:{}转换银行返回码crm 返回码:{},手机号:{},转化为银行的返回码:{}",
							new Object[] { msgVo.getTxnSeq(),rtBody.getRspCode(), reqBody.getIDValue(), errName });
					
					txnLog.setTmallRspCode(errName);
					txnLog.setTmallRspDesc(RspCodeConstant.Bank.getDescByValue(errName));
					txnLog.setCrmRspCode(forwardRtMsg.getRspCode());
					txnLog.setCrmRspDesc(forwardRtMsg.getRspDesc());
					txnLog.setCrmRspType(forwardRtMsg.getRspType());
					txnLog.setCrmSubRspCode(rtBody.getRspCode());
					txnLog.setCrmSubRspDesc(rtBody.getRspInfo());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTmallTxnLogService.modify(txnLog);

					/*改成异步之后，返回充值结果通知消息改为了请求类型，ActionCode：0，下面的Rcv节点无需重发*/
					resMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
					resMsg.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
					resMsg.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
					resMsg.setRcvSys(CommonConstant.BankOrgCode.TMALL.getValue());
					resMsg.setActivityCode(CommonConstant.TmallTrans.Tmall05.getValue());
					resBody.setOriReqTransID(reqMsg.getReqTransID());
					resBody.setOriReqDate(reqMsg.getReqDate());
					resBody.setOrderID(reqBody.getOrderID());
					resBody.setResultCode(errName);
					resBody.setResultDesc(RspCodeConstant.Bank.getDescByValue(errName));
					resBody.setResultTime(forwardRtMsg.getTransIDHTime());
					resMsg.setReqTransID(transIDH);
					resMsg.setBody(resBody);
					logger.debug("BankPayAction execute(Object) - end");
					
					return resMsg;
				}
			} else {
				tmallOperLogger.warn("天猫充值,接收方机构状态异常,内部交易流水:{},发起方:{},接收方:{},手机号:{},发起方交易流水号:{}",
						new Object[] { msgVo.getTxnSeq(), reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),reqBody.getIDValue(), reqMsg.getReqTransID() });
				logger.warn("天猫充值,接收方机构状态异常,内部交易流水:{},发起方:{},接收方:{},手机号:{},发起方交易流水号:{}",
						new Object[] { new Object[] { msgVo.getTxnSeq(),reqMsg.getReqSys(), forwardMsg.getMsgReceiver(), reqBody.getIDValue(),reqMsg.getReqTransID() } });
				txnLog.setTmallRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				txnLog.setTmallRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc()+checkFlag);
				txnLog.setCrmRspCode(forwardRtMsg.getRspCode());
				txnLog.setCrmRspDesc(forwardRtMsg.getRspDesc());
				txnLog.setCrmRspType(forwardRtMsg.getRspType());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTmallTxnLogService.modify(txnLog);

				/*改成异步之后，返回充值结果通知消息改为了请求类型，ActionCode：0，下面的Rcv节点无需重发*/
				resMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
				resMsg.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
				resMsg.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
				resMsg.setRcvSys(CommonConstant.BankOrgCode.TMALL.getValue());
				resMsg.setActivityCode(CommonConstant.TmallTrans.Tmall05.getValue());
				resBody.setOriReqTransID(reqMsg.getReqTransID());
				resBody.setOriReqDate(reqMsg.getReqDate());
				resBody.setOrderID(reqBody.getOrderID());
				resBody.setResultCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				resBody.setResultDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				resMsg.setReqTransID(transIDH);
				resMsg.setBody(resBody);
				logger.debug("BankPayAction execute(Object) - end");
				
				return resMsg;
			}
		} catch (AppRTException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			tmallOperLogger.error("天猫充值,运行异常!内部交易流水号:{},业务发起方:{},发起方交易流水号:{}",
					new Object[] {RspCodeConstant.Bank.getDescByValue(errCode),reqMsg.getTxnSeq(), reqMsg.getReqSys(), reqMsg.getReqTransID() });
			logger.error("天猫充值,运行异常!内部交易流水号:{},业务发起方:{},发起方交易流水号:{}}",
					new Object[] { reqMsg.getTxnSeq(), reqMsg.getReqSys(), reqMsg.getReqTransID() });
			logger.error("天猫充值,运行异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setTmallRspCode(errCode);
			txnLog.setTmallRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTmallTxnLogService.modify(txnLog);

			/*改成异步之后，返回充值结果通知消息改为了请求类型，ActionCode：0，下面的Rcv节点无需重发*/
			resMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
			resMsg.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
			resMsg.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
			resMsg.setRcvSys(CommonConstant.BankOrgCode.TMALL.getValue());
			resMsg.setActivityCode(CommonConstant.TmallTrans.Tmall05.getValue());
			resBody.setOriReqTransID(reqMsg.getReqTransID());
			resBody.setOriReqDate(reqMsg.getReqDate());
			resBody.setOrderID(reqBody.getOrderID());
			resBody.setResultCode(errCode);
			resBody.setResultDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			resMsg.setReqTransID(msgVo.getTxnSeq());
			resMsg.setBody(resBody);
			
			return resMsg;
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			tmallOperLogger.error("天猫充值,业务异常!内部交易流水号:{},业务发起方:{},操作流水号:{}",
					new Object[] {reqMsg.getTxnSeq(), reqMsg.getReqSys(), reqMsg.getReqTransID() });
			logger.error("天猫充值,业务异常!内部交易流水号:{},业务发起方:{},操作流水号:{}",
					new Object[] {reqMsg.getTxnSeq(), reqMsg.getReqSys(), reqMsg.getReqTransID() });
			logger.error("天猫充值,业务异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setTmallRspCode(errCode);
			txnLog.setTmallRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTmallTxnLogService.modify(txnLog);

			/*改成异步之后，返回充值结果通知消息改为了请求类型，ActionCode：0，下面的Rcv节点无需重发*/
			resMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
			resMsg.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
			resMsg.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
			resMsg.setRcvSys(CommonConstant.BankOrgCode.TMALL.getValue());
			resMsg.setActivityCode(CommonConstant.TmallTrans.Tmall05.getValue());
			resBody.setOriReqTransID(reqMsg.getReqTransID());
			resBody.setOriReqDate(reqMsg.getReqDate());
			resBody.setOrderID(reqBody.getOrderID());
			resBody.setResultCode(errCode);
			resBody.setResultDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			resMsg.setReqTransID(msgVo.getTxnSeq());
			resMsg.setBody(resBody);
			
			return resMsg;
		} catch (Exception e) {
			tmallOperLogger.error("天猫充值,系统异常!内部交易流水号:{},业务发起方:{},操作流水号:{}",
					new Object[] { reqMsg.getTxnSeq(), reqMsg.getReqSys(), reqMsg.getReqTransID() });
			logger.error("天猫充值,系统异常!内部交易流水号:{},业务发起方:{},操作流水号:{}",
					new Object[] {reqMsg.getTxnSeq(), reqMsg.getReqSys(), reqMsg.getReqTransID() });
			logger.error("天猫充值,系统异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setTmallRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setTmallRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc() + ":" + e.getMessage());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTmallTxnLogService.modify(txnLog);

			/*改成异步之后，返回充值结果通知消息改为了请求类型，ActionCode：0，下面的Rcv节点无需重发*/
			resMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
			resMsg.setReqChannel(CommonConstant.CnlType.CmccOwn.getValue());
			resMsg.setReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
			resMsg.setRcvSys(CommonConstant.BankOrgCode.TMALL.getValue());
			resMsg.setActivityCode(CommonConstant.TmallTrans.Tmall05.getValue());
			resBody.setOriReqTransID(reqMsg.getReqTransID());
			resBody.setOriReqDate(reqMsg.getReqDate());
			resBody.setOrderID(reqBody.getOrderID());
			resBody.setResultCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			//注释掉输出到应答报文的错误信息(该信息可能包含SQL异常) 20131213 modify by weiyi
//			String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_100?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_100);
//			resBody.setResultDesc(RspCodeConstant.Bank.getDescByValue(RspCodeConstant.Bank.BANK_015A06.getDesc() + ":" + errDesc));
			resBody.setResultDesc(RspCodeConstant.Bank.getDescByValue(RspCodeConstant.Bank.BANK_015A06.getDesc()));
			resMsg.setReqTransID(msgVo.getTxnSeq());
			resMsg.setBody(resBody);
			
			return resMsg;
		}
		
	}

	/**
	 * 初始化交易流表
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
	private void initLog(UpayCsysTmallTxnLog txnLog, BankMsgVo reqMsg,
			BankMsgVo resMsg, TmallConsumeReqVo reqBody, Long seqId,
			String transIDH, String intTxnDate, String transIDHTime) {
		/** 交易代码 */
		UpayCsysTransCode transCode = reqMsg.getTransCode();
		/** 交易流水 */
		txnLog.setSeqId(seqId);
		txnLog.setIntTxnSeq(transIDH);
		txnLog.setIntTransCode(transCode.getTransCode());
		txnLog.setIntTxnDate(intTxnDate);
		txnLog.setIntTxnTime(transIDHTime);
		txnLog.setSettleDate(reqMsg.getReqDate());
		txnLog.setPayMode(transCode.getPayMode());
		txnLog.setBussType(transCode.getBussType());
		txnLog.setBussChl(transCode.getBussChl());
		txnLog.setTmallActivityCode(reqMsg.getActivityCode());
		txnLog.setTmallOrgId(reqMsg.getReqSys());
		txnLog.setTmallRouteInfo("");
		txnLog.setTmallTransId(reqMsg.getReqTransID());
		txnLog.setTmallTransDt(reqMsg.getReqDate());
		txnLog.setTmallTransTm(reqMsg.getReqDateTime());
		txnLog.setTmallTranshId(transIDH);
		txnLog.setTmallTranshDt(intTxnDate);
		txnLog.setTmallTranshTm(transIDHTime);
		txnLog.setTmallCnlType(reqMsg.getReqChannel());
		txnLog.setCrmCnlType(reqMsg.getReqChannel());
		
		/*body*/
//		txnLog.setPayAmt(StringFormat.paseLong(reqBody.getPayed()));
		txnLog.setOrderId(reqBody.getOrderID());
		txnLog.setPayTransId(reqBody.getPayTransID());
		txnLog.setIdType(reqBody.getIDType());
		txnLog.setIdValue(reqBody.getIDValue());
		txnLog.setHomeProv(reqBody.getHomeProv());
		txnLog.setPayment(reqBody.getPayment());
		txnLog.setChargeMoney(reqBody.getChargeMoney());
		txnLog.setProdCnt(reqBody.getProdCnt());
		txnLog.setProdId(reqBody.getProdId());
		txnLog.setCommision(reqBody.getCommision());
		txnLog.setRebateFee(reqBody.getRebateFee());
		txnLog.setProdDiscount(reqBody.getProdDiscount());
		txnLog.setCreditCardFee(reqBody.getCreditCardFee());
		txnLog.setServiceFee(reqBody.getServiceFee());
		txnLog.setPayedType(reqBody.getPayedType());
		txnLog.setActivityNo(reqBody.getActivityNO());
		txnLog.setProdShelfNo(reqBody.getProdShelfNO());
		/*body*/
		
		txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.getValue());
		txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
	}
	
	private void initTmallBilllog(UpayCsysTmallTxnLog txnLog, UpayCsysTmallBillPay tmallBilllog){
//		BeanUtils.copyProperties(txnLog, tmallBilllog);
		
		tmallBilllog.setSeqId(txnLog.getSeqId());
		tmallBilllog.setIntTxnSeq(txnLog.getIntTxnSeq());
		tmallBilllog.setIntTransCode(txnLog.getIntTransCode());
		tmallBilllog.setIntTxnDate(txnLog.getIntTxnDate());
		tmallBilllog.setIntTxnTime(txnLog.getIntTxnTime());
		tmallBilllog.setSettleDate(txnLog.getSettleDate());
		tmallBilllog.setPayMode(txnLog.getPayMode());
		tmallBilllog.setBussType(txnLog.getBussType());
		tmallBilllog.setBussChl(txnLog.getBussChl());
		tmallBilllog.setTmallActivityCode(txnLog.getTmallActivityCode());
		tmallBilllog.setTmallOrgId(txnLog.getTmallOrgId());
		tmallBilllog.setTmallRouteInfo("");
		tmallBilllog.setTmallTransId(txnLog.getTmallTransId());
		tmallBilllog.setTmallTransDt(txnLog.getTmallTransDt());
		tmallBilllog.setTmallTransTm(txnLog.getTmallTransTm());
		tmallBilllog.setTmallTranshId(txnLog.getTmallTranshId());
		tmallBilllog.setTmallTranshDt(txnLog.getTmallTranshDt());
		tmallBilllog.setTmallTranshTm(txnLog.getTmallTranshTm());
		tmallBilllog.setTmallCnlType(txnLog.getTmallCnlType());
		tmallBilllog.setCrmBipCode(txnLog.getCrmBipCode());
		tmallBilllog.setCrmActivityCode(txnLog.getCrmActivityCode());
		tmallBilllog.setCrmOrgId(txnLog.getCrmOrgId());
		tmallBilllog.setCrmRouteType(txnLog.getCrmRouteType());
		tmallBilllog.setCrmRouteVal(txnLog.getCrmRouteVal());
		tmallBilllog.setCrmRouteInfo(txnLog.getCrmRouteInfo());
		tmallBilllog.setCrmSessionId(txnLog.getCrmSessionId());
		tmallBilllog.setCrmTransId(txnLog.getCrmTransId());
		tmallBilllog.setCrmTransDt(txnLog.getCrmTransDt());
		tmallBilllog.setCrmTransTm(txnLog.getCrmTransTm());
		tmallBilllog.setCrmTranshId(txnLog.getCrmTranshId());
		tmallBilllog.setCrmTranshDt(txnLog.getCrmTranshDt());
		tmallBilllog.setCrmTranshTm(txnLog.getCrmTranshTm());
		tmallBilllog.setCrmOprId(txnLog.getCrmOprId());
		tmallBilllog.setCrmOprDt(txnLog.getCrmOprDt());
		tmallBilllog.setCrmOprTm(txnLog.getCrmOprTm());
		tmallBilllog.setCrmCnlType(txnLog.getCrmCnlType());
		tmallBilllog.setCrmStartTm(txnLog.getCrmStartTm());
		tmallBilllog.setCrmEndTm(txnLog.getCrmEndTm());
		tmallBilllog.setIdProvince(txnLog.getIdProvince());
		tmallBilllog.setOrderId(txnLog.getOrderId());
		tmallBilllog.setPayTransId(txnLog.getPayTransId());
		tmallBilllog.setIdType(txnLog.getIdType());
		tmallBilllog.setIdValue(txnLog.getIdValue());
		tmallBilllog.setHomeProv(txnLog.getHomeProv());
		tmallBilllog.setPayment(txnLog.getPayment());
		tmallBilllog.setChargeMoney(txnLog.getChargeMoney());
		tmallBilllog.setProdCnt(txnLog.getProdCnt());
		tmallBilllog.setProdId(txnLog.getProdId());
		tmallBilllog.setCommision(txnLog.getCommision());
		tmallBilllog.setRebateFee(txnLog.getRebateFee());
		tmallBilllog.setProdDiscount(txnLog.getProdDiscount());
		tmallBilllog.setCreditCardFee(txnLog.getCreditCardFee());
		tmallBilllog.setServiceFee(txnLog.getServiceFee());
		tmallBilllog.setPayedType(txnLog.getPayedType());
		tmallBilllog.setActivityNo(txnLog.getActivityNo());
		tmallBilllog.setProdShelfNo(txnLog.getProdShelfNo());
		tmallBilllog.setResendCount(txnLog.getResendCount());
		tmallBilllog.setUserCat(txnLog.getUserCat());
		tmallBilllog.setTmallRspCode(txnLog.getTmallRspCode());
		tmallBilllog.setTmallRspDesc(txnLog.getTmallRspDesc());
		tmallBilllog.setCrmRspType(txnLog.getCrmRspType());
		tmallBilllog.setCrmRspCode(txnLog.getCrmRspCode());
		tmallBilllog.setCrmRspDesc(txnLog.getCrmRspDesc());
		tmallBilllog.setCrmSubRspCode(txnLog.getCrmSubRspCode());
		tmallBilllog.setCrmSubRspDesc(txnLog.getCrmSubRspDesc());
		tmallBilllog.setOriOrgId(txnLog.getOriOrgId());
		tmallBilllog.setOriOprTransId(txnLog.getOriOprTransId());
		tmallBilllog.setOriTransDate(txnLog.getOriTransDate());
		tmallBilllog.setBackFlag(txnLog.getBackFlag());
		tmallBilllog.setRefundFlag(txnLog.getRefundFlag());
		tmallBilllog.setReverseFlag(txnLog.getReverseFlag());
		tmallBilllog.setReconciliationFlag(txnLog.getReconciliationFlag());
		tmallBilllog.setStatus(txnLog.getStatus());
		tmallBilllog.setLastUpdOprid(txnLog.getLastUpdOprid());
		tmallBilllog.setLastUpdTime(txnLog.getLastUpdTime());
		tmallBilllog.setlSeqId(txnLog.getlSeqId());
		tmallBilllog.setReserved4(txnLog.getReserved4());
	}
	
}
