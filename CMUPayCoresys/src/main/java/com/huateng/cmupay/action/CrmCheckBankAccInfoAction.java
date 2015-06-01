package com.huateng.cmupay.action;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.constant.CommonConstant.BankOrgCode;
import com.huateng.cmupay.constant.CommonConstant.CnlType;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.controller.cache.CrmErrorCodeCache;
import com.huateng.cmupay.controller.cache.ProvAreaCache;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.business.bank.BankCheckBankBus;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankCheckMsgReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankCheckMsgRespVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmCheckMsgReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmCheckMsgRespVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.utils.StringFormat;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;

/**
 * 
 * @author fan_kui 银行账号校验
 */
@Controller("crmCheckBankAccInfoAction")
@Scope("prototype")
public class CrmCheckBankAccInfoAction extends
		AbsBaseAction<CrmMsgVo, CrmMsgVo> {

	@Autowired
	private BankCheckBankBus bankCheckInfoBus;

	@Override
	public CrmMsgVo execute(CrmMsgVo paramData) throws AppBizException {
		logger.debug("CrmCheckBankAccInfoAction execute(Object)-start");
		//得到请求报文Vo
		CrmMsgVo crmMsgVo = paramData;
		//渠道内部交易时间，日期以及流水号
		String intTxnTime = paramData.getTxnTime();
		String intTxnDate = paramData.getTxnDate();
		String txnSeq = paramData.getTxnSeq();
		
		// 返回VO
		CrmMsgVo msgVoRtn = paramData;
		//实例化请求报，响应以及交易流水Vo对象
		CrmCheckMsgReqVo reqVo = new CrmCheckMsgReqVo();
		CrmCheckMsgRespVo respVO = new CrmCheckMsgRespVo();
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		txnLog.setSettleDate(intTxnDate);
		try {
			//解析请求报文体
			MsgHandle.unmarshaller(reqVo, (String) crmMsgVo.getBody());
			respVO.setBankID(reqVo.getBankID());
			//请求中 BankAcctType 不是必填字段，返回报文中是必填
			respVO.setBankAcctType(StringUtils.isBlank(reqVo.getBankAcctType()) ? ""
					: reqVo.getBankAcctType());
			//组装公用应答报文
			msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.toString());
			msgVoRtn.setTransIDH(txnSeq);
			msgVoRtn.setTransIDHTime(StrUtil.subString(intTxnTime, 0, 14));
			
			
			if(!StringUtils.isBlank(reqVo.getTransactionID())){
				//根据发起方机构，操作流水号查询操作交易流水是否重复
				if(isRepeatTrans(reqVo.getTransactionID(),crmMsgVo.getMsgSender())){
					logger.warn("银行账号校验接口!操作流水重复!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{}",
							new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),txnSeq,
							paramData.getMsgSender(),reqVo.getIDValue()});
					
					log.warn("银行账号校验接口!操作流水重复!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{}",
							new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),txnSeq,
							paramData.getMsgSender(),reqVo.getIDValue()});
					
					msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					respVO.setRspCode(RspCodeConstant.Crm.CRM_3A17.getValue());
					respVO.setRspInfo(RspCodeConstant.Crm.CRM_3A17.getDesc());
					msgVoRtn.setBody(respVO);
					logger.debug("CrmCheckBankAccInfoAction execute(Object)-end");
					return msgVoRtn;
				}
			}
			//记录该笔交易的请求交易流水
			addTxnLog(txnLog,paramData,reqVo,txnSeq);
			logger.debug("add log is succ");
			// 校验报文体格式
			String validateMsg = this.validateModel(reqVo);
			if (!StringUtils.isNotBlank(validateMsg)) {
				logger.info("CrmCheckBankAccInfoAction body validate success");
			} else {
				log.warn("银行账号校验接口!格式校验失败!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},错误字段:{}",
						new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),txnSeq,
						paramData.getMsgSender(),reqVo.getIDValue(),validateMsg});
				
				logger.warn("银行账号校验接口!格式校验失败!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},错误字段:{}",
						new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),txnSeq,
						paramData.getMsgSender(),reqVo.getIDValue(),validateMsg});
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				respVO.setRspCode(RspCodeConstant.Crm.CRM_4A99.getValue());
				respVO.setRspInfo(RspCodeConstant.Crm.CRM_4A99.getDesc()
						+ validateMsg);
				msgVoRtn.setBody(respVO);

				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_4A99.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_4A99.getDesc()
						+ validateMsg);
				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				upayCsysTxnLogService.modify(txnLog);
				logger.debug("CrmCheckBankAccInfoAction execute(Object)-end");
				return msgVoRtn;
			}
			// 查询签约关系
			// Map<String,Object> paramBind=new HashMap<String, Object>();
			// paramBind.put("idValue",reqVo.getiDValue());;
			// paramBind.put("idType",
			// CommonConstant.UserSignType.Phone.toString());
			// UpayCsysBindInfo
			// uPayCsysBindInfo=upayCsysBindInfoService.findObj(paramBind);
			//
			// if(uPayCsysBindInfo!=null &&
			// CommonConstant.BindStatus.Bind.toString().equals(uPayCsysBindInfo.getStatus())){
			// logger.info("签约关系已存在，该号码无法签约.");
			// msgVoRtn.setRspCode(MessageHandler.getWzwErrCode("2998"));
			// msgVoRtn.setRspDesc(MessageHandler.getWzwErrMsg("2998"));
			// msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.toString());
			// respVO.setRspCode(MessageHandler.getCrmErrCode("2A10"));
			// respVO.setRspInfo(MessageHandler.getCrmErrMsg("2A10"));
			// msgVoRtn.setBody(respVO);
			//
			//
			//
			// txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			// txnLog.setChlRspCode(MessageHandler.getWzwErrCode("2998"));
			// txnLog.setChlRspDesc(MessageHandler.getWzwErrMsg("2998"));
			// txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.toString());
			// txnLog.setChlSubRspCode(MessageHandler.getCrmErrCode("2A10"));
			// txnLog.setChlSubRspDesc(MessageHandler.getCrmErrMsg("2A10"));
			// txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			// upayCsysTxnLogService.modify(txnLog);
			// return msgVoRtn;
			// }
			//根据银行机构编码，判断是否有服务权限
			
//			boolean orgFlag = isO2OTransOn(paramData.getMsgSender(),
//					reqVo.getBankID(),paramData.getTransCode().getTransCode());
			// 查询该交易的号码段属于移动还是联通电信的。
			ProvincePhoneNum provincePhoneNum = ProvAreaCache.getProvAreaByPrimary(reqVo.getIDValue());
			String  orgFlag = offOrgTrans(paramData.getMsgSender(),
					reqVo.getBankID(),paramData.getTransCode().getTransCode(), 
					provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());

			
//			boolean orgFlag =orgStatusCheck(reqVo.getBankID());
			if (orgFlag != null) {
				log.warn("银行账号校验接口!BANK无权限:{},CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{}",
						new Object[]{reqVo.getBankID(),paramData.getTransIDO(),reqVo.getTransactionID(),txnSeq,
						paramData.getMsgSender(),reqVo.getIDValue()});
				logger.warn("银行账号校验接口!BANK无权限:{},CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{}",
						new Object[]{reqVo.getBankID(),paramData.getTransIDO(),reqVo.getTransactionID(),txnSeq,
						paramData.getMsgSender(),reqVo.getIDValue()});
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				respVO.setRspCode(RspCodeConstant.Crm.CRM_3A35.getValue());
				respVO.setRspInfo(RspCodeConstant.Crm.CRM_3A35.getDesc() + ":"
						+ "落地方机构服务的权限关闭。");
				msgVoRtn.setBody(respVO);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlRspType(msgVoRtn.getRspType());
				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A35.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A35.getDesc()
						+ orgFlag);
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				logger.debug("CrmCheckBankAccInfoAction execute(Object)-end");
				return msgVoRtn;
			}
			// 组装to银行端请求报文
			BankCheckMsgReqVo bankCheckMsgReqVo = new BankCheckMsgReqVo();
			bankCheckMsgReqVo.setBankAcctID(reqVo.getBankAcctID());
			bankCheckMsgReqVo.setUserID(reqVo.getUserID());
			bankCheckMsgReqVo.setUserIDType(reqVo.getUserIDType());
			bankCheckMsgReqVo.setUserName(reqVo.getUserName());

			BankMsgVo bankMsgVo = new BankMsgVo();
			bankMsgVo.setTransCode(crmMsgVo.getTransCode());
			bankMsgVo.setRcvSys(reqVo.getBankID());
			bankMsgVo.setBody(bankCheckMsgReqVo);
			bankMsgVo.setReqChannel(CnlType.CmccHall.toString());
			bankMsgVo.setActivityCode(CommonConstant.BankTrans.Bank01
					.toString());
			bankMsgVo.setReqSys(BankOrgCode.CMCC.toString());
			bankMsgVo.setActionCode(CommonConstant.ActionCode.Requset
					.toString());
			bankMsgVo.setReqDate(intTxnDate);
			bankMsgVo.setReqDateTime(intTxnTime);
			bankMsgVo.setReqTransID(txnSeq);

			txnLog.setRcvActivityCode(CommonConstant.BankTrans.Bank01
					.toString());
			txnLog.setRcvCnlType(bankMsgVo.getReqChannel());
			txnLog.setRcvDomain(reqVo.getBankID());
			txnLog.setRcvTransDt(intTxnDate);
			txnLog.setRcvTransId(txnSeq);
			txnLog.setRcvTransTm(intTxnTime);

			// 发往银行
			BankMsgVo bankMsgVoRtn = bankCheckInfoBus.execute(bankMsgVo, null,
					txnLog, null);
			txnLog.setSettleDate(bankMsgVoRtn.getRcvDate());
			//记录银行返回的报文头应答信息
			txnLog.setRcvTranshDt(bankMsgVoRtn.getRcvDate());
			txnLog.setRcvTranshId(bankMsgVoRtn.getRcvTransID());
			txnLog.setRcvTranshTm(bankMsgVoRtn.getRcvDateTime());
			//txnLog.setRcvOprId(bankMsgVoRtn.getRcvTransID());
			txnLog.setRcvOprDt(bankMsgVoRtn.getRcvDate());
			txnLog.setRcvOprTm(bankMsgVoRtn.getRcvDateTime());
			
			
//			//判断核心是否接收超时 U99998为平台内部超时
//			if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(
//					bankMsgVoRtn.getRspCode())) {
//				log.warn("银行账号校验接口!BANK前置超时!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{}",
//						new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),txnSeq,
//						paramData.getMsgSender(),reqVo.getiDValue(),reqVo.getBankID()});
//				logger.info("核心平台检测到响应超时,rspCode:{},rspInfo:{}",
//						bankMsgVoRtn.getRspCode(), bankMsgVoRtn.getRspDesc());
//				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
//				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
//				msgVoRtn.setRspType(CommonConstant.CrmRspType.SysErr.toString());
//				respVO.setRspCode(RspCodeConstant.Crm.CRM_5A07.getValue());
//				respVO.setRspInfo(RspCodeConstant.Crm.CRM_5A07.getDesc()
//						+ bankMsgVoRtn.getRspDesc());
//				msgVoRtn.setBody(respVO);
//
//				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
//				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
//				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
//				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr
//						.toString());
//				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_5A07.getValue());
//				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_5A07.getDesc());
//				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
//				upayCsysTxnLogService.modify(txnLog);
//				return msgVoRtn;
//
//			}
//			//判断得到的银行端报文体是否为空，为空将返回错误报文
//			if("".equals(bankMsgVoRtn.getBody())||bankMsgVoRtn.getBody()==null){
//				log.warn("银行账号校验接口!应答报文空!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{},BANK流水:{},BANK应答码:{},BANK应答描述:{}",
//						new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),txnSeq,
//						paramData.getMsgSender(),reqVo.getiDValue(),reqVo.getBankID(),
//						bankMsgVoRtn.getRcvTransID(),bankMsgVoRtn.getRspCode(),bankMsgVoRtn.getRspDesc()});
//				logger.info("应答报文空!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{},BANK流水:{},BANK应答码:{},BANK应答描述:{}",
//						new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),txnSeq,
//						paramData.getMsgSender(),reqVo.getiDValue(),reqVo.getBankID(),
//						bankMsgVoRtn.getRcvTransID(),bankMsgVoRtn.getRspCode(),bankMsgVoRtn.getRspDesc()});
//				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
//				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
//				msgVoRtn.setRspType(CommonConstant.CrmRspType.SysErr.toString());
//				respVO.setRspCode(RspCodeConstant.Crm.CRM_5A07.getValue());
//				respVO.setRspInfo(RspCodeConstant.Crm.CRM_5A07.getDesc()
//						+ bankMsgVoRtn.getRspDesc());
//				msgVoRtn.setBody(respVO);
//
//				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
//				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
//				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
//				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr
//						.toString());
//				txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_5A07.getValue());
//				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_5A07.getDesc());
//				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
//				upayCsysTxnLogService.modify(txnLog);
//				return msgVoRtn;
//			}
//			
			//判断银行返回的应答码是不是成功的应答码，不是成功交易，将返回错误报文
			if (!(RspCodeConstant.Bank.BANK_020A00.getValue().equals(bankMsgVoRtn.getRspCode()))) {
				log.warn("银行账号校验接口!交易失败!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{},BANK流水:{},BANK应答码:{},BANK应答描述:{}",
						new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),txnSeq,
						paramData.getMsgSender(),reqVo.getIDValue(),reqVo.getBankID(),
						bankMsgVoRtn.getRcvTransID(),bankMsgVoRtn.getRspCode(),bankMsgVoRtn.getRspDesc()});
				//"银行账号:{}信息校验失败！！！", StringFormat.formatCodeString(reqVo.getBankAcctID())
				logger.warn("银行账号校验接口!交易失败!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{},BANK流水:{},BANK应答码:{},BANK应答描述:{}",
						new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),txnSeq,
						paramData.getMsgSender(),reqVo.getIDValue(),reqVo.getBankID(),
						bankMsgVoRtn.getRcvTransID(),bankMsgVoRtn.getRspCode(),bankMsgVoRtn.getRspDesc()});
				String errCode = CrmErrorCodeCache.getCrmErrCode(bankMsgVoRtn
						.getRspCode());

				msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
				respVO.setRspCode(errCode);
				respVO.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
				msgVoRtn.setBody(respVO);

				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
				txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setChlSubRspCode(errCode);
				txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
				txnLog.setRcvRspCode(bankMsgVoRtn.getRspCode());
				txnLog.setRcvRspDesc(bankMsgVoRtn.getRspDesc());
				txnLog.setRcvRspType(CommonConstant.CrmRspType.BusErr.getValue());
				txnLog.setRcvSubRspCode(bankMsgVoRtn.getRspCode());
				txnLog.setRcvSubRspDesc(bankMsgVoRtn.getRspDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				upayCsysTxnLogService.modify(txnLog);
				logger.debug("CrmCheckBankAccInfoAction execute(Object)-end");
				return msgVoRtn;
			}
			
			// 解析返回的报文体并set到响应移动的报文体中
			BankCheckMsgRespVo bankCheckMsgRespVo = new BankCheckMsgRespVo();
			MsgHandle.unmarshaller(bankCheckMsgRespVo,
					(String) bankMsgVoRtn.getBody());
			
			// TODO 如果返回信用卡则报错
			 if(CommonConstant.BankCardType.CreditCard.toString().equals(bankCheckMsgRespVo.getBankAcctType())){
			 logger.warn("银行账号校验接口!信用卡不能签约!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{},银行卡类型:{}",
						new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),txnSeq,
						paramData.getMsgSender(),reqVo.getIDValue(),reqVo.getBankID(),bankCheckMsgRespVo.getBankAcctType()});
			 
			 log.warn("银行账号校验接口!信用卡不能签约!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{},银行卡类型:{}",
						new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),txnSeq,
						paramData.getMsgSender(),reqVo.getIDValue(),reqVo.getBankID(),bankCheckMsgRespVo.getBankAcctType()});
			 msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			 msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			 msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			 respVO.setRspCode(RspCodeConstant.Crm.CRM_3A11.getValue());
			 respVO.setRspInfo(RspCodeConstant.Crm.CRM_3A11.getDesc());
			 msgVoRtn.setBody(respVO);
			 txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			 txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			 txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			 txnLog.setChlRspType(msgVoRtn.getRspType());
			 txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_3A11.getValue());
			 txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_3A11.getDesc());
			 txnLog.setRcvRspCode(bankMsgVoRtn.getRspCode());
			 txnLog.setRcvRspDesc(bankMsgVoRtn.getRspDesc());
			 txnLog.setRcvRspType(msgVoRtn.getRspType());
			 txnLog.setRcvSubRspCode(bankMsgVoRtn.getRspCode());
			 txnLog.setRcvSubRspDesc(bankMsgVoRtn.getRspDesc());
			 txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			 upayCsysTxnLogService.modify(txnLog);
			 logger.debug("CrmCheckBankAccInfoAction execute(Object)-end");
			 return msgVoRtn;
			 }
			txnLog.setBankAcctType(bankCheckMsgRespVo.getBankAcctType());
			respVO.setBankAcctType(bankCheckMsgRespVo.getBankAcctType());
			respVO.setBankID(reqVo.getBankID());
			respVO.setRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
			respVO.setRspInfo(RspCodeConstant.Crm.CRM_0000.getDesc());
			msgVoRtn.setBody(respVO);
			msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
			msgVoRtn.setRspDesc(bankMsgVoRtn.getRspDesc());
			msgVoRtn.setRspType(CommonConstant.CrmRspType.Success.toString());
			// 将响应的报文插入流水
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
			txnLog.setChlRspType(CommonConstant.CrmRspType.Success.toString());
			txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_0000.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_0000.getDesc());
			txnLog.setRcvRspCode(bankMsgVoRtn.getRspCode());
			txnLog.setRcvRspDesc(bankMsgVoRtn.getRspDesc());
			txnLog.setRcvRspType(CommonConstant.CrmRspType.Success.toString());
			txnLog.setRcvSubRspCode(bankMsgVoRtn.getRspCode());
			txnLog.setRcvSubRspDesc(bankMsgVoRtn.getRspDesc());
			txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.toString());
			upayCsysTxnLogService.modify(txnLog);
			log.succ("交易成功!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{},BANK流水:{},BANK应答码:{},BANK应答描述:{}",
					new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),txnSeq,
					paramData.getMsgSender(),reqVo.getIDValue(),reqVo.getBankID(),
					bankMsgVoRtn.getRcvTransID(),bankMsgVoRtn.getRspCode(),bankMsgVoRtn.getRspDesc()});
		} catch (AppRTException e) {
			String errCode = e.getCode();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			logger.error("银行账号校验接口!intTxnSeq:{},运行异常!" , new Object[]{txnSeq});
			log.error("银行账号校验接口!运行异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{}",
					new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),txnSeq,
					paramData.getMsgSender(),reqVo.getIDValue(),reqVo.getBankID()});
			logger.error("银行账号校验接口!运行异常:",e);
			msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			respVO.setRspCode(errCode);
			respVO.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
			msgVoRtn.setBody(respVO);

			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			upayCsysTxnLogService.modify(txnLog);
			logger.debug("CrmCheckBankAccInfoAction execute(Object)-end");
			return msgVoRtn;
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = CrmErrorCodeCache.getCrmErrCode(errCode);
			log.error("银行账号校验接口!系统异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{}",
					new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),txnSeq,
					paramData.getMsgSender(),reqVo.getIDValue(),reqVo.getBankID()});
			logger.error("银行账号校验接口!intTxnSeq:{},系统异常!" , new Object[]{txnSeq});
			logger.error("银行账号校验接口!系统异常:",e);
			msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			respVO.setRspCode(errCode);
			respVO.setRspInfo(RspCodeConstant.Crm.getDescByValue(errCode));
			msgVoRtn.setBody(respVO);

			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setChlSubRspCode(errCode);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.getDescByValue(errCode));
			upayCsysTxnLogService.modify(txnLog);
			logger.debug("CrmCheckBankAccInfoAction execute(Object)-end");
			return msgVoRtn;
		} catch (Exception e) {
			log.error("银行账号校验接口!未知异常!CRM流水:{},CRM操作流水:{},UPAY流水:{},发起省:{},号码:{},银行:{}",
					new Object[]{paramData.getTransIDO(),reqVo.getTransactionID(),txnSeq,
					paramData.getMsgSender(),reqVo.getIDValue(),reqVo.getBankID()});
			logger.error("银行账号校验接口!intTxnSeq:{},未知异常!" , new Object[]{txnSeq});
			logger.error("银行账号校验接口!未知异常:",e);
			msgVoRtn.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			msgVoRtn.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			msgVoRtn.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
			respVO.setRspCode(RspCodeConstant.Crm.CRM_5A06.getValue());
			
			//注释掉输出到应答报文的错误信息(该信息可能包含SQL异常) 20131213 modify by weiyi
//			String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_230?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_230);
//			respVO.setRspInfo(RspCodeConstant.Crm.CRM_5A06.getDesc()+":"+errDesc);
			
			respVO.setRspInfo(RspCodeConstant.Crm.CRM_5A06.getDesc());
			
			msgVoRtn.setBody(respVO);

			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
			txnLog.setChlRspType(CommonConstant.CrmRspType.BusErr.getValue());
			txnLog.setChlSubRspCode(RspCodeConstant.Crm.CRM_5A06.getValue());
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			txnLog.setChlSubRspDesc(RspCodeConstant.Crm.CRM_5A06.getDesc()+":"+e.getMessage());
			upayCsysTxnLogService.modify(txnLog);
			logger.debug("CrmCheckBankAccInfoAction execute(Object)-end");
			return msgVoRtn;
		}
		logger.debug("CrmCheckBankAccInfoAction execute(Object)-end");
		return msgVoRtn;

	}
	
	public void addTxnLog(UpayCsysTxnLog txnLog,CrmMsgVo crmMsgVo,CrmCheckMsgReqVo reqVo,String txnSeq){
		logger.info("intTxnSeq:{},记录交易流水", txnSeq);
		txnLog.setBussChl(crmMsgVo.getTransCode().getBussChl());
		txnLog.setSeqId(crmMsgVo.getSeqId());
		txnLog.setIntTxnTime(crmMsgVo.getTxnTime());
		txnLog.setIntMqSeq(crmMsgVo.getMqSeq());
		txnLog.setIntTransCode((crmMsgVo.getTransCode()).getTransCode());
		txnLog.setIntTxnDate(crmMsgVo.getTxnDate());
		txnLog.setIntTxnSeq(txnSeq);
		txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setPayMode(crmMsgVo.getTransCode().getPayMode());
		txnLog.setBussType(crmMsgVo.getTransCode().getBussType());
//		txnLog.setTxnCat(crmMsgVo.getTransCode().getTxnCat());
		txnLog.setMainFlag(CommonConstant.Mainflag.Master.getValue());
		txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.toString());
		txnLog.setReqVersion(ExcConstant.CRM_VERSION);
		txnLog.setReqCnlType(CommonConstant.CnlType.CmccHall.getValue());
		txnLog.setReqBipCode(crmMsgVo.getBIPCode());
		txnLog.setReqActivityCode(crmMsgVo.getActivityCode());
		txnLog.setReqDomain(crmMsgVo.getMsgSender());
		txnLog.setReqRouteType(crmMsgVo.getRouteType());
		txnLog.setReqRouteVal(crmMsgVo.getRouteValue());
		txnLog.setReqSessionId(crmMsgVo.getSessionID());
		txnLog.setReqTransTm(crmMsgVo.getTransIDOTime());
		txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
		txnLog.setReqTransId(crmMsgVo.getTransIDO());
		txnLog.setReqTranshDt(crmMsgVo.getTxnDate());
		txnLog.setReqTranshId(txnSeq);
		txnLog.setReqTranshTm(StrUtil.subString(crmMsgVo.getTxnTime(), 0, 14));
		txnLog.setReqOprId(reqVo.getTransactionID());
		txnLog.setReqOprDt(reqVo.getActionDate());
		txnLog.setReqTransDt(StrUtil.subString(crmMsgVo.getTransIDOTime(),0, 8));
		txnLog.setReqOprTm(crmMsgVo.getTransIDOTime());
		txnLog.setIdType(reqVo.getIDType());
		txnLog.setIdValue(reqVo.getIDValue());
		txnLog.setIdProvince(crmMsgVo.getMsgSender().substring(0,3));
		txnLog.setBankId(reqVo.getBankID());
		txnLog.setBankAccId(StringFormat.formatCodeString(reqVo.getBankAcctID()));
		txnLog.setBankAcctType(reqVo.getBankAcctType());
		txnLog.setUserId(StringFormat.formatCodeString(reqVo.getUserID()));
		txnLog.setUserName(StringFormat.formatNameString(reqVo.getUserName()));
		txnLog.setUserType(reqVo.getUserIDType());
		txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		txnLog.setSettleDate(DateUtil.getDateyyyyMMdd());
		upayCsysTxnLogService.add(txnLog);
	}
}
