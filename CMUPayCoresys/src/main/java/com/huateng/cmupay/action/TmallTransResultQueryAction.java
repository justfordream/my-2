package com.huateng.cmupay.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.controller.cache.ProvAreaCache;
import com.huateng.cmupay.controller.service.system.IUpayCsysTmallTxnLogService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.message.SendCrmJmsMessageImpl;
import com.huateng.cmupay.logFormat.TmallMessageLogger;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysTmallTxnLog;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmTransQueryReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmTransQueryResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.tmall.TmallTransQueryReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.tmall.TmallTransQueryResVo;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.utils.StringUtil;
import com.huateng.toolbox.utils.DateUtil;


@Controller("tmallTransResultQueryAction")
@Scope("prototype")
public class TmallTransResultQueryAction extends AbsBaseAction<BankMsgVo, BankMsgVo>{
	
	
	private TmallMessageLogger tmallOperLogger = TmallMessageLogger.getLogger(this.getClass());
	
//天猫日志独立时使用	，把logger替换成tmallLogger即可
//	private final Logger tmallLogger = LoggerFactory.getLogger("TMALL_FILE");
	
	@Autowired
	private SendCrmJmsMessageImpl sendCrmJmsMessage;
	@Autowired
	protected IUpayCsysTmallTxnLogService upayCsysTmallTxnLogService;
	@Override
	public BankMsgVo execute(BankMsgVo msgVo) throws AppBizException {
		logger.debug("TmallTransResultQueryAction execute(Object) - start");
		// 请求报文
		BankMsgVo reqMsg = msgVo;
		TmallTransQueryReqVo reqBody = new TmallTransQueryReqVo();
		TmallTransQueryResVo resBody = new TmallTransQueryResVo();
		
		
		String transIDH = msgVo.getTxnSeq();
		BankMsgVo resMsg = reqMsg;
		List<UpayCsysTmallTxnLog> txnL = new ArrayList<UpayCsysTmallTxnLog>();
		UpayCsysTmallTxnLog txnLog = new UpayCsysTmallTxnLog();
		try {
			String intTxnDate = msgVo.getTxnDate();
			MsgHandle.unmarshaller(reqBody, (String)msgVo.getBody());
			reqMsg.setBody(reqBody);
			
			String transIDHTime = msgVo.getTxnTime();
			String transactionId = transIDH;
			Long seqId = msgVo.getSeqId();
			resMsg.setRcvDate(intTxnDate);
			resMsg.setRcvDateTime(transIDHTime);
			resMsg.setRcvTransID(transIDH);
			resMsg.setActionCode(CommonConstant.ActionCode.Respone.getValue());
			
			/* 验证消息 */
			String checkrtn = validateModel(reqBody);
			if (!"".equals(StringUtil.toTrim(checkrtn))) {
				tmallOperLogger.warn("报文体校验失败:{},内部交易流水:{},发起方:{}", new Object[] {
						checkrtn, transIDH, reqMsg.getReqSys() });
				logger.warn("报文体校验失败:{},内部交易流水:{},发起方:{}", new Object[] {
						checkrtn, transIDH, reqMsg.getReqSys() });
//				resMsg.setRspCode(code);
//				resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(code));
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_014A04.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_014A04.getDesc() + "," + checkrtn);
				resMsg.setBody(resBody);

				return resMsg;
			}
			
			if("01".equals(reqBody.getQueryType())){
				if((reqBody.getOriReqTransID() == null) || ("".equals(reqBody.getOriReqTransID()))){
					tmallOperLogger.warn("报文体校验失败,原业交易请求流水号（oriReqTransID）不能为空,内部交易流水:{},发起方:{}", new Object[] {
							transIDH, reqMsg.getReqSys() });
					logger.warn("报文体校验失败,原业交易请求流水号（oriReqTransID）不能为空,内部交易流水:{},发起方:{}", new Object[] {
							transIDH, reqMsg.getReqSys() });
					
					resMsg.setRspCode(RspCodeConstant.Bank.BANK_014A04.getValue());
					resMsg.setRspDesc(RspCodeConstant.Bank.BANK_014A04.getDesc() + "," + "原业交易请求流水号（oriReqTransID）不能为空");
					resMsg.setBody(resBody);
					
					return resMsg;
				}
			}else if("02".equals(reqBody.getQueryType())){
				if((reqBody.getOriOrderID() == null) || ("".equals(reqBody.getOriOrderID()))){
					tmallOperLogger.warn("报文体校验失败，原订单编号（OriOrderID）不能为空,内部交易流水:{},发起方:{}", new Object[] {
							transIDH, reqMsg.getReqSys() });
					logger.warn("报文体校验失败，原订单编号（OriOrderID）不能为空,内部交易流水:{},发起方:{}", new Object[] {
							transIDH, reqMsg.getReqSys() });
					
					resMsg.setRspCode(RspCodeConstant.Bank.BANK_014A04.getValue());
					resMsg.setRspDesc(RspCodeConstant.Bank.BANK_014A04.getDesc() + "," + "原订单编号（OriOrderID）不能为空");
					resMsg.setBody(resBody);
					
					return resMsg;
				}
			}
			
			/** 交易代码 */
			UpayCsysTransCode transCode = reqMsg.getTransCode();
			initLog(txnLog, seqId, transIDH, intTxnDate, transIDHTime, reqMsg, transCode, resMsg);
			upayCsysTmallTxnLogService.add(txnLog);
			
			if(((DateUtil.getsubDate(reqMsg.getTxnDate(), reqBody.getOriReqDate())) > 2)){
				tmallOperLogger.warn("天猫不支持查询三天前的交易数据,内部交易流水:{},发起方:{}", new Object[] {
						transIDH, reqMsg.getReqSys() });
				logger.warn("天猫不支持查询三天前的交易数据,内部交易流水:{},发起方:{}", new Object[] {
						transIDH, reqMsg.getReqSys() });
				
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setTmallRspCode(RspCodeConstant.Bank.BANK_013A38.getValue());
				txnLog.setTmallRspDesc(RspCodeConstant.Bank.BANK_013A38.getDesc());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTmallTxnLogService.modify(txnLog);
				
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_013A38.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_013A38.getDesc());
				resMsg.setBody(resBody);
				
				return  resMsg;
			}
			
			txnLog.setOriTransDate(reqBody.getOriReqDate());
			txnLog.setOriOrgId(reqBody.getOriReqSys());
			txnLog.setOriOprTransId(reqBody.getOriReqTransID());
			
			// 查询原交易及处理
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("tmallOrgId", reqBody.getOriReqSys());
//			params.put("tmallTransDt", reqBody.getOriReqDate());
			if("01".equals(reqBody.getQueryType())){
				params.put("tmallTransId", reqBody.getOriReqTransID());
			}else if("02".equals(reqBody.getQueryType())){
				params.put("orderId", reqBody.getOriOrderID());
			}
			txnL = upayCsysTmallTxnLogService.findList(params, null);
			
			UpayCsysTmallTxnLog transLog = null;
			if((txnL != null) && (txnL.size() !=0)  ){
				transLog = txnL.get(0);
			}
			
			if (null == transLog) {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setTmallRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
				txnLog.setTmallRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTmallTxnLogService.modify(txnLog);
				
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
				resBody.setQueryType(reqBody.getQueryType());
				resBody.setOriResultCode(RspCodeConstant.Bank.BANK_014A05.getValue());
				resBody.setOriResultDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
				resMsg.setBody(resBody);
				
				return resMsg;
			}
			txnLog.setIdValue(transLog.getIdValue());
			txnLog.setIdType(transLog.getIdType());
			txnLog.setIdProvince(transLog.getIdProvince());
			
			String forwardOrg = transLog.getCrmOrgId();// 转发方机构代码
			if((StringUtils.isBlank(forwardOrg)) && (CommonConstant.TxnStatus.InitStatus.getValue().equals(transLog.getStatus()))){
				tmallOperLogger.error("原交易未发送到省充值:{},内部交易流水:{},手机号:{},发起方:{}",
						new Object[] { reqBody.getOriReqTransID(), transIDH, transLog.getIdValue(), reqMsg.getReqSys() });
				logger.error("原交易未发送到省充值:{},内部交易流水:{},手机号:{},发起方:{}",
						new Object[] { reqBody.getOriReqTransID(), transIDH, transLog.getIdValue(), reqMsg.getReqSys() });
				
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setTmallRspCode(RspCodeConstant.Bank.BANK_014A08.getValue());
				txnLog.setTmallRspDesc(RspCodeConstant.Bank.BANK_014A08.getDesc());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTmallTxnLogService.modify(txnLog);
				
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
				resBody.setQueryType(reqBody.getQueryType());
				resBody.setOriRcvDate(transLog.getCrmTranshDt());
				resBody.setOriRcvTransID(transLog.getTmallTranshId());
				resBody.setOriOrderID(reqBody.getOriOrderID());
				resBody.setOriResultCode(RspCodeConstant.Bank.BANK_014A08.getValue());
				resBody.setOriResultDesc(RspCodeConstant.Bank.BANK_014A08.getDesc());
//				resBody.setOriResultTime(transLog.getCrmTranshTm());
				resMsg.setBody(resBody);
				
				return resMsg;
			}else if((StringUtils.isBlank(forwardOrg)) && (CommonConstant.TxnStatus.TxnFail.getValue().equals(transLog.getStatus()))){
				tmallOperLogger.error("原交易失败:{},内部交易流水:{},手机号:{},发起方:{}",
						new Object[] { reqBody.getOriReqTransID(), transIDH, transLog.getIdValue(), reqMsg.getReqSys() });
				logger.error("原交易失败:{},内部交易流水:{},手机号:{},发起方:{}",
						new Object[] { reqBody.getOriReqTransID(), transIDH, transLog.getIdValue(), reqMsg.getReqSys() });
				
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setTmallRspCode(transLog.getTmallRspCode());
				txnLog.setTmallRspDesc(transLog.getTmallRspDesc());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTmallTxnLogService.modify(txnLog);
				
				resMsg.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
				resBody.setQueryType(reqBody.getQueryType());
				resBody.setOriRcvDate(transLog.getCrmTranshDt());
				resBody.setOriRcvTransID(transLog.getTmallTranshId());
				resBody.setOriOrderID(reqBody.getOriOrderID());
				resBody.setOriResultCode(transLog.getTmallRspCode());
				resBody.setOriResultDesc("原天猫充值交易失败：" + transLog.getTmallRspDesc());
				resBody.setOriResultTime(transLog.getCrmTranshTm());
				resMsg.setBody(resBody);
				return resMsg;
			}
//			boolean checkFlag = true;// 转发机构交易权限标识

			/** 报文头 */
			CrmMsgVo forwardMsg = new CrmMsgVo();
			forwardMsg.setTransCode(transCode);
			forwardMsg.setVersion(ExcConstant.CRM_VERSION);
			forwardMsg.setTestFlag(testFlag);
			forwardMsg.setBIPCode(CommonConstant.Bip.Bis18.getValue());
			forwardMsg.setActivityCode(CommonConstant.CrmTrans.Crm09.getValue());
			forwardMsg.setActionCode(CommonConstant.ActionCode.Requset.getValue());
			forwardMsg.setOrigDomain(CommonConstant.OrgDomain.UPSS.getValue());
			forwardMsg.setHomeDomain(CommonConstant.OrgDomain.BOSS.getValue());
			forwardMsg.setRouteType(CommonConstant.RouteType.RoutePhone.getValue());
			forwardMsg.setRouteValue(transLog.getIdValue());
			forwardMsg.setSessionID(transactionId);
			forwardMsg.setTransIDO(transIDH);
			forwardMsg.setTransIDOTime(StrUtil.subString(transIDHTime, 0, 14));
			forwardMsg.setMsgSender(CommonConstant.BankOrgCode.CMCC.getValue());
			forwardMsg.setMsgReceiver(forwardOrg);//

			/** 报文体 */
			CrmTransQueryReqVo forwardBody = new CrmTransQueryReqVo();
			forwardBody.setOriActionDate(transLog.getIntTxnDate());
			forwardBody.setOriReqSys(CommonConstant.BankOrgCode.CMCC.getValue());
			forwardBody.setOriTransactionID(transLog.getCrmOprId());
			forwardBody.setOriActivityCode(transLog.getCrmActivityCode());
			forwardMsg.setBody(forwardBody);
			
			
			// 更新交易流水
			txnLog.setCrmActivityCode(forwardMsg.getActivityCode());
			txnLog.setCrmBipCode(forwardMsg.getBIPCode());
			txnLog.setCrmOrgId(forwardOrg);
			txnLog.setCrmRouteType(forwardMsg.getRouteType());
			txnLog.setCrmRouteVal(forwardMsg.getRouteValue());
			txnLog.setCrmSessionId(forwardMsg.getSessionID());
			txnLog.setCrmCnlType(msgVo.getReqChannel());
			txnLog.setCrmTransId(transIDH);
			txnLog.setCrmTransTm(transIDHTime);
			txnLog.setCrmTransDt(intTxnDate);
			txnLog.setCrmOprId(forwardBody.getOriTransactionID());
			txnLog.setCrmOprDt(forwardBody.getOriActionDate());
			txnLog.setCrmOprTm(forwardMsg.getTransIDHTime());
			
			ProvincePhoneNum provincePhoneNum = ProvAreaCache.getProvAreaByPrimary(transLog.getIdValue());
			//校验落地方机构权限
			String checkFlag = offOrgTrans(reqMsg.getReqSys(), forwardOrg, msgVo.getTransCode().getTransCode(), 
					provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
//			checkFlag = isO2OTransOn(reqMsg.getReqSys(), forwardOrg, msgVo.getTransCode().getTransCode());
//			checkFlag = orgStatusCheck(forwardOrg);
			if (checkFlag == null) {
				logger.debug("sendCrmJmsMessage.sendMsg(forwardMsg) - start,intTxnSeq:{}",new Object[]{transIDH});
				txnLog.setCrmStartTm(DateUtil.getDateyyyyMMddHHmmssSSS());
				forwardMsg = sendCrmJmsMessage.sendMsg(forwardMsg);
				txnLog.setCrmEndTm(DateUtil.getDateyyyyMMddHHmmssSSS());
				logger.debug("sendCrmJmsMessage.sendMsg(forwardMsg) - end,intTxnSeq:{}",new Object[]{transIDH});
			
				txnLog.setCrmTranshId(forwardMsg.getTransIDH());
				txnLog.setCrmTranshTm(forwardMsg.getTransIDHTime());
				txnLog.setCrmTranshDt(StrUtil.subString(forwardMsg.getTransIDHTime(), 0, 8));
				
				if(RspCodeConstant.Upay.UPAY_U99998.getValue().equals(forwardMsg.getRspCode())){
					tmallOperLogger.warn("天猫充值状态查询,CRM前置响应超时!内部交易流水:{},发起方:{},接收方:{},手机号:{}",new Object[] { transIDH,
									reqMsg.getReqSys(),forwardMsg.getMsgReceiver() ,transLog.getIdValue()});
					logger.warn("天猫充值状态查询,CRM前置响应超时!内部交易流水:{},发起方:{},接收方:{},手机号:{}",new Object[] { transIDH,
									reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue() });
					
					String errCode = forwardMsg.getRspCode();
					errCode = BankErrorCodeCache.getBankErrCode(errCode);
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setTmallRspCode(RspCodeConstant.Bank.BANK_015A07.getValue());
					txnLog.setTmallRspDesc(RspCodeConstant.Bank.BANK_015A07.getDesc() + ",CRM前置响应超时");
					txnLog.setCrmRspCode(forwardMsg.getRspCode());
					txnLog.setCrmRspDesc(forwardMsg.getRspDesc());
					txnLog.setCrmRspType(forwardMsg.getRspType());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTmallTxnLogService.modify(txnLog);
					
					resMsg.setRspCode(RspCodeConstant.Bank.BANK_015A07.getValue());
					resMsg.setRspDesc(RspCodeConstant.Bank.BANK_015A07.getDesc() + ",CRM前置响应超时");
					resMsg.setBody(resBody);
					return resMsg;
				}
				
				if (forwardMsg.getBody() == null
						|| "".equals(forwardMsg.getBody().toString())
						|| "null".equalsIgnoreCase(forwardMsg.getBody().toString())) {
					tmallOperLogger.error("天猫充值状态查询,CRM返回报文体为空!内部交易流水:{},发起方:{},接收方:{},手机号:{}",new Object[] { transIDH,
							reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue() });
					logger.error("天猫充值状态查询,CRM返回报文体为空!内部交易流水:{},发起方:{},接收方:{},手机号:{}",new Object[] { transIDH,
							reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue() });
					
					String errCode = RspCodeConstant.Bank.BANK_015A06.getValue();
					errCode = BankErrorCodeCache.getBankErrCode(errCode);
					
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setTmallRspCode(errCode);
					txnLog.setTmallRspDesc(RspCodeConstant.Bank.getDescByValue(errCode) + ",CRM返回报文体为空");
					txnLog.setCrmRspCode(forwardMsg.getRspCode());
					txnLog.setCrmRspDesc(forwardMsg.getRspDesc());
					txnLog.setCrmRspType(forwardMsg.getRspType());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTmallTxnLogService.modify(txnLog);

					resMsg.setRspCode(errCode);
					resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode) + ",CRM返回报文体为空");
					resMsg.setBody(resBody);
					return resMsg;
				}
				
				CrmTransQueryResVo forwardRtBody = new CrmTransQueryResVo();
				MsgHandle.unmarshaller(forwardRtBody, forwardMsg.getBody().toString());
//				resBody.setOriRspCode(BankErrorCodeCache.getBankErrCode(forwardRtBody.getRspCode()));
//				resBody.setOriRspDesc(forwardRtBody.getRspInfo());
				if (RspCodeConstant.Crm.CRM_0000.getValue().equals(forwardMsg.getRspCode())){
//						&& RspCodeConstant.Crm.CRM_0000.getValue().equals(
//								forwardRtBody.getRspCode())) {
					tmallOperLogger.succ("CRM响应成功!内部交易流水:{},发起方:{},接收方:{},手机号:{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue()});
					logger.info("CRM响应成功!内部交易流水:{},发起方:{},接收方:{},手机号:{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue() });
					String errName = BankErrorCodeCache.getBankErrCode(forwardRtBody.getRspCode());
					txnLog.setTmallRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
					txnLog.setTmallRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
					txnLog.setCrmRspCode(forwardMsg.getRspCode());
					txnLog.setCrmRspDesc(forwardMsg.getRspDesc());
					txnLog.setCrmRspType(forwardMsg.getRspType());
					txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.getValue());
					txnLog.setCrmSubRspCode(forwardRtBody.getRspCode());
					txnLog.setCrmSubRspDesc(forwardRtBody.getRspInfo());
					
					resBody.setQueryType(reqBody.getQueryType());
					resBody.setOriRcvDate(transLog.getCrmTranshDt());
					resBody.setOriRcvTransID(transLog.getTmallTranshId());
					resBody.setOriOrderID(reqBody.getOriOrderID());
					resBody.setOriResultCode(errName);
					resBody.setOriResultDesc(RspCodeConstant.Bank.getDescByValue(errName));
					resBody.setOriResultTime(transLog.getCrmTranshTm());
					
					resMsg.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
					resMsg.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
				}else if (RspCodeConstant.Crm.CRM_2998.getValue().equals(forwardMsg.getRspCode())){
					tmallOperLogger.error("天猫充值状态查询,CRM响应失败!内部交易流水:{},发起方:{},接收方:{},手机号:{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue()});
					logger.error("天猫充值状态查询,CRM响应失败!内部交易流水:{},发起方:{},接收方:{},手机号:{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue() });
					
					String errName = BankErrorCodeCache.getBankErrCode(forwardRtBody.getRspCode());
					txnLog.setTmallRspCode(errName);
					txnLog.setTmallRspDesc(RspCodeConstant.Bank.getDescByValue(errName));
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setCrmRspCode(forwardMsg.getRspCode());
					txnLog.setCrmRspDesc(forwardMsg.getRspDesc());
					txnLog.setCrmRspType(forwardMsg.getRspType());
					txnLog.setCrmSubRspCode(forwardRtBody.getRspCode());
					txnLog.setCrmSubRspDesc(forwardRtBody.getRspInfo());
					
					resMsg.setRspCode(errName);
					resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errName));
				}else{
					tmallOperLogger.error("天猫充值查询失败!内部交易流水:{},发起方:{},接收方:{},手机号:{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue()});
					logger.error("天猫充值查询失败!内部交易流水:{},发起方:{},接收方:{},手机号:{}",
							new Object[] { transIDH,reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue() });
					
					String errName = BankErrorCodeCache.getBankErrCode(forwardRtBody.getRspCode());
					if("012998".equals(errName)){
						errName = RspCodeConstant.Bank.BANK_015A06.getValue();
					}
					txnLog.setTmallRspCode(errName);
					txnLog.setTmallRspDesc(RspCodeConstant.Bank.getDescByValue(errName));
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
					txnLog.setCrmRspCode(forwardMsg.getRspCode());
					txnLog.setCrmRspDesc(forwardMsg.getRspDesc());
					txnLog.setCrmRspType(forwardMsg.getRspType());
					txnLog.setCrmSubRspCode(forwardRtBody.getRspCode());
					txnLog.setCrmSubRspDesc(forwardRtBody.getRspInfo());
					
					resBody.setQueryType(reqBody.getQueryType());
					resBody.setOriRcvDate(transLog.getCrmTranshDt());
					resBody.setOriRcvTransID(transLog.getTmallTranshId());
					resBody.setOriOrderID(reqBody.getOriOrderID());
					resBody.setOriResultCode(errName);
					resBody.setOriResultDesc(RspCodeConstant.Bank.getDescByValue(errName));
					resBody.setOriResultTime(transLog.getCrmTranshTm());
					
					resMsg.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
					resMsg.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
				}
				
				resMsg.setBody(resBody);
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTmallTxnLogService.modify(txnLog);
				logger.debug("TmallTransResultQueryAction execute(Object) - end");
				return resMsg;
			} else {
				tmallOperLogger.warn("天猫充值状态查询,落地方机构状态异常:{},内部交易流水:{},发起方:{},接收方:{},手机号:{}",new Object[] { forwardOrg, transIDH,
						reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue() });
				logger.warn("天猫充值状态查询,落地方机构状态异常:{},内部交易流水:{},发起方:{},接收方:{},手机号:{}",new Object[] { forwardOrg, transIDH,
						reqMsg.getReqSys(),forwardMsg.getMsgReceiver(),transLog.getIdValue() });
				
				txnLog.setTmallRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				txnLog.setTmallRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc() + ":接收方" + forwardOrg + "交易权限关闭");
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTmallTxnLogService.modify(txnLog);

				resMsg.setRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				resMsg.setRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				resMsg.setBody(resBody);
				logger.debug("TmallTransResultQueryAction execute(Object) - end");
				
				return resMsg;
			}
		} catch (AppRTException e) {
			String errCode = e.getCode();
			tmallOperLogger.error("天猫充值状态查询,运行异常!内部交易流水号:{},业务发起方:{}",
					new Object[] {RspCodeConstant.Bank.getDescByValue(errCode),transIDH, reqMsg.getReqSys() });
			logger.error("天猫充值状态查询,运行异常,错误代码:{},内部交易流水号:{},业务发起方:{}",
					new Object[] { errCode,transIDH, reqMsg.getReqSys() });
			logger.error("运行异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setTmallRspCode(errCode);
			txnLog.setTmallRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTmallTxnLogService.modify(txnLog);

			resMsg.setRspCode(errCode);
			resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			resMsg.setBody(resBody);
			return resMsg;
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			tmallOperLogger.error("天猫充值状态查询,业务异常!内部交易流水号:{},业务发起方:{}",
					new Object[] {RspCodeConstant.Bank.getDescByValue(errCode),transIDH, reqMsg.getReqSys() });
			logger.error("天猫充值状态查询,业务异常,代码:{},内部交易流水号:{},业务发起方:{}",
					new Object[] { errCode,transIDH, reqMsg.getReqSys() });
			logger.error("天猫充值状态查询,业务异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setTmallRspCode(errCode);
			txnLog.setTmallRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTmallTxnLogService.modify(txnLog);

			resMsg.setRspCode(errCode);
			resMsg.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			resMsg.setBody(resBody);
			return resMsg;
		} catch (Exception e) {
			String errCode = RspCodeConstant.Bank.BANK_015A06.getValue();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			tmallOperLogger.error("天猫充值状态查询,系统异常!内部交易流水号:{},业务发起方:{}",
					new Object[] {transIDH, reqMsg.getReqSys() });
			logger.error("天猫充值状态查询,系统异常!内部交易流水号:{},业务发起方:{}",
					new Object[] {transIDH, reqMsg.getReqSys() });
			logger.error("天猫充值状态查询,系统异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.getValue());
			txnLog.setTmallRspCode(errCode);
			txnLog.setTmallRspDesc(RspCodeConstant.Bank.getDescByValue(errCode)+":"+e.getMessage());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTmallTxnLogService.modify(txnLog);

			resMsg.setRspCode(errCode);
			//注释掉输出到应答报文的错误信息(该信息可能包含SQL异常) 20131213 modify by weiyi
//			String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_100?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_100);
//			resMsg.setRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+errDesc);
			resMsg.setRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc());
			resMsg.setBody(resBody);
			
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
	private void initLog(UpayCsysTmallTxnLog txnLog, Long seqId, String transIDH,
			String intTxnDate, String transIDHTime, BankMsgVo reqMsg,
			UpayCsysTransCode transCode, BankMsgVo resMsg) {
		/** 交易流水 */
		txnLog.setSeqId(seqId);
		txnLog.setIntTxnSeq(transIDH);
		txnLog.setIntTxnDate(intTxnDate);
		txnLog.setIntTxnTime(transIDHTime);
		txnLog.setBussType(transCode.getBussType());
		txnLog.setIntTransCode(transCode.getTransCode());
		txnLog.setPayMode(transCode.getPayMode());
		txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.getValue());
		txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setBussChl(transCode.getBussChl());
		txnLog.setTmallActivityCode(reqMsg.getActivityCode());
		txnLog.setTmallCnlType(reqMsg.getReqChannel());
		txnLog.setTmallOrgId(reqMsg.getReqSys());
//		txnLog.setTmallRouteInfo(reqMsg.getRouteInfo().getRouteInfo());//TODO
		txnLog.setTmallTransDt(reqMsg.getReqDate());
		txnLog.setTmallTransId(reqMsg.getReqTransID());
		txnLog.setTmallTransTm(reqMsg.getReqDateTime());	
		txnLog.setTmallTranshDt(intTxnDate);
		txnLog.setSettleDate(reqMsg.getReqDate());
		txnLog.setTmallTranshTm(transIDHTime);
		txnLog.setTmallTranshId(transIDH);
		txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
//		txnLog.setPayAmt(0L);//TODO
//		txnLog.setIdValue("13888888602");
//		txnLog.setIdType("01");
	}
}
