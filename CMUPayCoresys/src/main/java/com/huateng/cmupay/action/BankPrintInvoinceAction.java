package com.huateng.cmupay.action;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.DataSourceInstances;
import com.huateng.cmupay.constant.DictConst;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.controller.cache.DictCodeCache;
import com.huateng.cmupay.controller.cache.ProvAreaCache;
import com.huateng.cmupay.controller.cache.SysMapCache;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogHisService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.business.crm.CrmPrintInvoinceBus;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysSysMapInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.common.multidatasource.DataSourceContextHolder;
import com.huateng.cmupay.models.his.UpayCsysTxnLogHis;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankInvoinceVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankPrintInvoiceReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmPrintInvoiceRspVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmPrintInvoinceReqVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.cmupay.utils.UUIDGenerator;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.xml.XmlUtil;

/**
 * 
 * @author fan_kui 银行端发起发票打印结果通知
 */
@Controller("bankPrintInvoinceAction")
@Scope("prototype")
public class BankPrintInvoinceAction extends
		AbsBaseAction<BankMsgVo, BankMsgVo> {

	@Autowired
	private CrmPrintInvoinceBus crmPrintInvoinceBus;
	@Autowired
	private IUpayCsysTxnLogHisService upayCsysTxnLogHisService;
	@Override
	public BankMsgVo execute(BankMsgVo param) throws AppBizException {

		logger.debug("BankPrintInvoinceAction execute(Object) - start");
		BankMsgVo bankMsgVo = param;
		String intTxnTime = param.getTxnTime();
		UpayCsysTxnLog txnLog = new UpayCsysTxnLog();
		BankMsgVo bankRtnMsgVo = new BankMsgVo();
		bankRtnMsgVo = bankMsgVo;
		bankRtnMsgVo.setActionCode(CommonConstant.ActionCode.Respone.toString());
		String txnSeq =  bankMsgVo.getTxnSeq();
		try {
			//解析报文体的内容，转换成实体类
			BankPrintInvoiceReqVo reqVo = XmlUtil.toBean(
					(String) bankMsgVo.getBody(), BankPrintInvoiceReqVo.class);
			
			//检查重复订单 by:易振强 2013-11-26  
			Map<String, Object> params1 = new HashMap<String, Object>();
			params1.put("reqDomain", param.getReqSys());
			params1.put("reqTransId", param.getReqTransID());
			params1.put("settleDate", param.getReqDate());
			txnLog = upayCsysTxnLogService.findObj(params1);
			if (txnLog != null) {
				log.warn("银行发票打印结果通知,订单重复,银行流水:{},平台流水:{},银行机构:{},缴费流水:{},缴费机构:{},缴费时间:{}" , 
						new Object[]{param.getReqTransID(), txnSeq,param.getReqSys(),reqVo.getTransactionID(),reqVo.getOriReqSys(),reqVo.getOriReqDate()});
				logger.warn("银行发票打印结果通知,订单重复,内部交易流水号:{},业务发起方:{},银行交易流水号:{}" , 
						new Object[]{param.getReqTransID(), txnSeq,param.getReqSys(),reqVo.getTransactionID(),reqVo.getOriReqSys(),reqVo.getOriReqDate()});
				bankRtnMsgVo.setRspCode(RspCodeConstant.Bank.BANK_013A17.getValue());
				bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_013A17.getDesc());
				bankRtnMsgVo.setBody(null);
				logger.debug("BankPrintInvoinceAction execute(Object) - end");
				return bankRtnMsgVo;
			}
				
			txnLog = new UpayCsysTxnLog();
			
			String intTxnDate = bankMsgVo.getTxnDate();
			Long seqId = bankMsgVo.getSeqId();			
			bankRtnMsgVo.setRcvDate(intTxnDate);
			bankRtnMsgVo.setRcvTransID(txnSeq);
			bankRtnMsgVo.setRcvDateTime(intTxnTime);
			//生成一条新的交易流水
			txnLog.setSeqId(seqId);
			txnLog.setIntTxnTime(intTxnTime);
			txnLog.setIntMqSeq(bankMsgVo.getMqSeq());
			txnLog.setIntTransCode((bankMsgVo.getTransCode()).getTransCode());
			txnLog.setIntTxnDate(intTxnDate);
			txnLog.setIntTxnSeq(txnSeq);
			txnLog.setPayMode(bankMsgVo.getTransCode().getPayMode());
			txnLog.setBussType(bankMsgVo.getTransCode().getBussType());
//			txnLog.setTxnCat(bankMsgVo.getTransCode().getTxnCat());
			txnLog.setBussChl(bankMsgVo.getTransCode().getBussChl());
			txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.toString());
			txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
			// set header message
			txnLog.setReqActivityCode(bankMsgVo.getActivityCode());
			txnLog.setReqDomain(bankMsgVo.getReqSys());
			txnLog.setReqCnlType(bankMsgVo.getReqChannel());
			txnLog.setReqTransDt(bankMsgVo.getReqDate());
			txnLog.setReqTransId(bankMsgVo.getReqTransID());
			txnLog.setReqTransTm(bankMsgVo.getReqDateTime());
			txnLog.setReqTranshDt(intTxnDate);
			txnLog.setReqTranshId(txnSeq);
			txnLog.setReqTranshTm(intTxnTime);
			txnLog.setReqOprDt(bankMsgVo.getReqDate());
			txnLog.setReqOprId(bankMsgVo.getReqTransID());
			txnLog.setReqOprTm(bankMsgVo.getReqDateTime());
			// set body message
			txnLog.setOriOprTransId(reqVo.getTransactionID());
			txnLog.setOriOrgId(reqVo.getOriReqSys());
			txnLog.setOriReqDate(reqVo.getOriReqDate());
			txnLog.setSettleDate(bankMsgVo.getReqDate());
			//校验报文体格式
			String validateMsg = this.validateModel(reqVo);
			if (validateMsg == null || "".equals(validateMsg)) {
				logger.debug("body validate success");
			} else {
				String code = validateMsg.split(":")[0];
				logger.warn("格式校验失败!银行流水:{},平台流水:{},银行机构:{},错误码:{},错误描述:{}", new Object[]{param.getReqTransID(),
						txnSeq,param.getReqSys(),code,RspCodeConstant.Bank.getDescByValue(code)});
				log.warn("格式校验失败!银行流水:{},平台流水:{},银行机构:{},错误码:{},错误描述:{}", new Object[]{param.getReqTransID(),
						txnSeq,param.getReqSys(),code,RspCodeConstant.Bank.getDescByValue(code)});
				//取到错误的应答码
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				bankRtnMsgVo.setBody(null);
				bankRtnMsgVo.setRspCode(code);
				bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.getDescByValue(code));
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setChlRspCode(code);
				txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(code));
				upayCsysTxnLogService.modify(txnLog);
				return bankRtnMsgVo;
			}
			logger.info("查询原交易!reqTransId:{},reqDomain:{},reqTransDt:{}",new Object[]{reqVo.getTransactionID()
					,reqVo.getOriReqSys(),reqVo.getOriReqDate()});
			// 根据下面3个条件查出唯一的原交易
			Map<String, Object> logMap = new HashMap<String, Object>();
			logMap.put("reqTransId", reqVo.getTransactionID());
			logMap.put("reqDomain", reqVo.getOriReqSys());
			logMap.put("reqTransDt", reqVo.getOriReqDate());
			logMap.put("status", CommonConstant.TxnStatus.TxnSuccess.getValue());
			
			SimpleDateFormat myFormatter = new SimpleDateFormat("yyyyMMdd");
			java.util.Date date1 = myFormatter.parse(param.getTxnDate());
			java.util.Date date2 = myFormatter.parse(reqVo.getOriReqDate());
			long date3 = (date1.getTime() - date2.getTime())/ (24 * 60 * 60 * 1000);// 表示相隔天数
			boolean isHis = date3 <=Long.valueOf(
					DictCodeCache.getDictCode(DictConst.DictId.InsertIntoHisTxnLogDay.getValue(),
							DictConst.CodeId.ChangeHisTxnLog.getValue()).getCodeValue2());
			UpayCsysTxnLogHis logHis=null;
			UpayCsysTxnLog oriTxnLog=null;
			if(isHis){
				 oriTxnLog = upayCsysTxnLogService.findObj(logMap);
				//判断原交易是否存在
				if (oriTxnLog == null) {
					logger.warn("原缴费不存在!银行流水:{},平台流水:{},银行机构:{},缴费流水:{},缴费机构:{},缴费时间:{}", new Object[]{param.getReqTransID(),
							txnSeq,param.getReqSys(),reqVo.getTransactionID(),reqVo.getOriReqSys(),reqVo.getOriReqDate()});
					
					log.warn("原缴费不存在!银行流水:{},平台流水:{},银行机构:{},缴费流水:{},缴费机构:{},缴费时间:{}", new Object[]{param.getReqTransID(),
							txnSeq,param.getReqSys(),reqVo.getTransactionID(),reqVo.getOriReqSys(),reqVo.getOriReqDate()});
					
					bankRtnMsgVo.setBody(null);
					bankRtnMsgVo
							.setRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
					bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
					// 更新流水
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
							.toString());
					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
					txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);
					return bankRtnMsgVo;
				}
			}else{
				DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_HIS);
//				logHis = upayCsysTxnLogHisService.findObj(logMap);
				logHis = upayCsysTxnLogHisService.findHisStlObj(logMap);
			
				if (logHis== null) {
					logger.info("原缴费不存在!银行流水:{},平台流水:{},银行机构:{},缴费流水:{},缴费机构:{},缴费时间:{}", new Object[]{param.getReqTransID(),
							txnSeq,param.getReqSys(),reqVo.getTransactionID(),reqVo.getOriReqSys(),reqVo.getOriReqDate()});
					
					log.warn("原缴费不存在!银行流水:{},平台流水:{},银行机构:{},缴费流水:{},缴费机构:{},缴费时间:{}", new Object[]{param.getReqTransID(),
							txnSeq,param.getReqSys(),reqVo.getTransactionID(),reqVo.getOriReqSys(),reqVo.getOriReqDate()});
					
					bankRtnMsgVo.setBody(null);
					bankRtnMsgVo
							.setRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
					bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
					// 更新流水
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
							.toString());
					txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
					txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
					txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
					txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					upayCsysTxnLogService.modify(txnLog);
					return bankRtnMsgVo;
				}
			}
			if(!isHis){
				DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
			}
			
			String idValue=oriTxnLog==null?logHis.getIdValue():oriTxnLog.getIdValue();
			String idProvince=oriTxnLog==null?logHis.getIdProvince():oriTxnLog.getIdProvince();
			
			//从缓存中取出系统编码映射数据，并得到4位省机构编码
			UpayCsysSysMapInfo sysInfo = SysMapCache.getProvCd(idProvince);
			if (sysInfo==null) {
				log.warn("查无手机号段信息!银行流水:{},平台流水:{},银行机构:{},省代码:{}", new Object[]{param.getReqTransID(),
						txnSeq,param.getReqSys(),reqVo.getOriReqSys()});
				logger.warn("查无手机号段信息!银行流水:{},平台流水:{},银行机构:{},省代码:{}", new Object[]{param.getReqTransID(),
						txnSeq,param.getReqSys(),reqVo.getOriReqSys()});
				bankRtnMsgVo
						.setRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
				bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
				bankRtnMsgVo.setBody(null);
				// 更新流水
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc() + "IdProvince不存在");
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());

				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());

				upayCsysTxnLogService.modify(txnLog);
				return bankRtnMsgVo;
			}
			txnLog.setRcvActivityCode(CommonConstant.CrmTrans.Crm11.toString());
			txnLog.setRcvBipCode(CommonConstant.Bip.Bis20.toString());
			txnLog.setRcvDomain(sysInfo.getSysCd());
			txnLog.setRcvRouteType(CommonConstant.RouteType.RoutePhone
					.toString());
			txnLog.setRcvRouteVal(idValue);
			txnLog.setIdProvince(idProvince);
			txnLog.setIdType(oriTxnLog==null?logHis.getIdType():oriTxnLog.getIdType());
			txnLog.setIdValue(idValue);
			txnLog.setUserId(oriTxnLog==null?logHis.getUserId():oriTxnLog.getUserId());
			txnLog.setUserName(oriTxnLog==null?logHis.getUserName():oriTxnLog.getUserName());
			txnLog.setUserStatus(oriTxnLog==null?logHis.getUserStatus():oriTxnLog.getUserStatus());
			txnLog.setUserType(oriTxnLog==null?logHis.getUserType():oriTxnLog.getUserType());
			txnLog.setUserCat(oriTxnLog==null?logHis.getUserCat():oriTxnLog.getUserCat());
			txnLog.setBalance(oriTxnLog==null?logHis.getBalance():oriTxnLog.getBalance());
			txnLog.setBankAccId(oriTxnLog==null?logHis.getBankAccId():oriTxnLog.getBankAccId());
			txnLog.setBankAcctType(oriTxnLog==null?logHis.getBankAcctType():oriTxnLog.getBankAcctType());
			txnLog.setBankId(oriTxnLog==null?logHis.getBankId():oriTxnLog.getBankId());
			txnLog.setSettleDate(bankMsgVo.getReqDate());
			txnLog.setOrderId(oriTxnLog==null?logHis.getOrderId():oriTxnLog.getOrderId());
			txnLog.setPayType(oriTxnLog==null?logHis.getPayType():oriTxnLog.getPayType());
			txnLog.setNeedPayAmt(oriTxnLog==null?logHis.getNeedPayAmt():oriTxnLog.getNeedPayAmt());
			txnLog.setPayAmt(oriTxnLog==null?logHis.getPayAmt():oriTxnLog.getPayAmt());
			txnLog.setPayedType(oriTxnLog==null?logHis.getPayedType():oriTxnLog.getPayedType());
			txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.add(txnLog);
			logger.info("成功插入流水!intTxnSeq:{}",new Object[]{txnSeq});
			//判断报文体中的发票打印信息是不是为空，为空将返回错误报文
			if (reqVo.getListBankInvoince() == null
					|| reqVo.getListBankInvoince().get(0) == null) {
				
				log.warn("无发票打印结果信息!银行流水:{},平台流水:{},银行机构:{}", new Object[]{param.getReqTransID(),
						txnSeq,param.getReqSys(),reqVo.getOriReqSys()});
				logger.warn("无发票打印结果信息!银行流水:{},平台流水:{},银行机构:{}", new Object[]{param.getReqTransID(),
						txnSeq,param.getReqSys(),reqVo.getOriReqSys()});
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				bankRtnMsgVo.setRspCode(RspCodeConstant.Bank.BANK_015A05.getValue());
				bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_015A05.getDesc());
				bankRtnMsgVo.setBody(null);
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A05.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A05.getDesc());
				upayCsysTxnLogService.modify(txnLog);
				return bankRtnMsgVo;
			} else {
				BankInvoinceVo invoince = reqVo.getListBankInvoince().get(0);
				String validateInvoinceMsg = this.validateModel(invoince);
				if (validateInvoinceMsg == null
						|| "".equals(validateInvoinceMsg)) {
					logger.info("校验成功!intTxnSeq:{}",new Object[]{txnSeq});
				} else {
					String code = validateInvoinceMsg.split(":")[0];
					log.warn("格式校验失败!银行流水:{},平台流水:{},银行机构:{},错误码:{},错误描述:{}", new Object[]{param.getReqTransID(),
							txnSeq,param.getReqSys(),code,RspCodeConstant.Bank.getDescByValue(code)});
					logger.warn("格式校验失败!银行流水:{},平台流水:{},银行机构:{},错误码:{},错误描述:{}", new Object[]{param.getReqTransID(),
							txnSeq,param.getReqSys(),code,RspCodeConstant.Bank.getDescByValue(code)});
					txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
							.toString());
					bankRtnMsgVo.setBody(null);
					bankRtnMsgVo
							.setRspCode(code);
					bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.getDescByValue(code));
					txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					txnLog.setChlRspCode(code);
					txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(code));
					upayCsysTxnLogService.modify(txnLog);
					return bankRtnMsgVo;
				}
			}
			//校验接收方省移动是否有服务权限
//			String orgFlag = offOrgTrans(bankMsgVo.getReqSys(), sysInfo.getSysCd(), param.getTransCode().getTransCode());
			// 查询该交易的号码段属于移动还是联通电信的。
			ProvincePhoneNum provincePhoneNum = ProvAreaCache.getProvAreaByPrimary(idValue);
			//校验接收方省移动是否有服务权限
			String orgFlag = offOrgTrans(bankMsgVo.getReqSys(), sysInfo.getSysCd(), param.getTransCode().getTransCode(), 
					provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
//			boolean orgFlag = isO2OTransOn(bankMsgVo.getReqSys(), sysInfo.getSysCd(), param.getTransCode().getTransCode());
//			boolean orgFlag = orgStatusCheck(sysInfo.getSysCd());
			if (orgFlag != null) {
				log.warn("CRM无权限!银行流水:{},平台流水:{},银行机构:{},CRM机构:{}", new Object[]{param.getReqTransID(),
						txnSeq,param.getReqSys(),sysInfo.getSysCd()});
				logger.warn("CRM无权限!银行流水:{},平台流水:{},银行机构:{},CRM机构:{}", new Object[]{param.getReqTransID(),
						txnSeq,param.getReqSys(),sysInfo.getSysCd()});
				bankRtnMsgVo.setBody(null);
				bankRtnMsgVo
						.setRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc()+orgFlag);
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return bankRtnMsgVo;
			}

			// 组装发往移动报文	
			CrmPrintInvoinceReqVo crmPrintInvoinceReqVo = new CrmPrintInvoinceReqVo();
			crmPrintInvoinceReqVo.setSettleDate(oriTxnLog==null?logHis.getReqTransDt():oriTxnLog.getReqTransDt());//TODO 	充值账期
			crmPrintInvoinceReqVo.setBankID(oriTxnLog==null?logHis.getBankId():oriTxnLog.getBankId());
			crmPrintInvoinceReqVo.setIdValue(idValue);
			crmPrintInvoinceReqVo.setPayed(oriTxnLog==null?logHis.getPayAmt():oriTxnLog.getPayAmt());
			BankInvoinceVo invoince = reqVo.getListBankInvoince().get(0);
			crmPrintInvoinceReqVo.setInvoiceID(invoince == null ? "" : invoince
					.getInvoiceID());// TODO
			crmPrintInvoinceReqVo.setPrintDate(StrUtil.subString(intTxnTime, 0, 8));
			crmPrintInvoinceReqVo.setTransactionID(oriTxnLog==null?logHis.getRcvOprId():oriTxnLog.getRcvOprId());// TODO
			
			logger.info("组装报文");
			
			CrmMsgVo crmMsgVo = new CrmMsgVo();
			crmMsgVo.setBody(crmPrintInvoinceReqVo);
			crmMsgVo.setRouteType(CommonConstant.RouteType.RoutePhone
					.toString());
			crmMsgVo.setRouteValue(idValue);
			crmMsgVo.setActionCode(CommonConstant.ActionCode.Requset.toString());
			crmMsgVo.setActivityCode(CommonConstant.CrmTrans.Crm11.toString());
			crmMsgVo.setBIPCode(CommonConstant.Bip.Bis20.toString());
			crmMsgVo.setHomeDomain(CommonConstant.OrgDomain.BOSS.toString());
			crmMsgVo.setMqSeq(Serial.genSerialNo(CommonConstant.Sequence.SendCrmMqSeq
							.getValue()));
			crmMsgVo.setOrigDomain(CommonConstant.OrgDomain.UPSS.toString());
			crmMsgVo.setSessionID(txnSeq);
			crmMsgVo.setTestFlag(testFlag);
			crmMsgVo.setTransCode(crmMsgVo.getTransCode());
			crmMsgVo.setTransIDO(txnSeq);
			crmMsgVo.setTransIDOTime(StrUtil.subString(intTxnTime, 0, 14));
			crmMsgVo.setVersion(ExcConstant.CRM_VERSION);
			crmMsgVo.setMsgReceiver(sysInfo.getSysCd());
			
			//记录发送移动端的交易流水
			txnLog.setRcvActivityCode(crmMsgVo.getActivityCode());
			txnLog.setRcvBipCode(crmMsgVo.getBIPCode());
			txnLog.setRcvDomain(sysInfo.getSysCd());
			txnLog.setRcvRouteType(crmMsgVo.getRspType());
			txnLog.setRcvRouteVal(idValue);
			txnLog.setRcvSessionId(txnSeq);
			txnLog.setRcvTransDt(intTxnDate);
			txnLog.setRcvTransId(txnSeq);
			txnLog.setRcvTransTm(crmMsgVo.getTransIDOTime());
			
			logger.info("send to CRM!intTxnSeq:",new Object[]{txnSeq});
			//发送请求报文至移动侧
			CrmMsgVo crmRtnMsgVo = crmPrintInvoinceBus.execute(crmMsgVo,
					crmPrintInvoinceReqVo, txnLog, null);
			//判断是否超时
			if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(
					crmRtnMsgVo.getRspCode())) {
				log.warn("平台接收超时!银行流水:{},平台流水:{},银行机构:{},CRM机构:{},CRM应答流水:{}", new Object[]{param.getReqTransID(),
						txnSeq,param.getReqSys(),sysInfo.getSysCd(),crmRtnMsgVo.getTransIDH()});
				logger.warn("平台接收超时!银行流水:{},平台流水:{},银行机构:{},CRM机构:{},CRM应答流水:{}", new Object[]{param.getReqTransID(),
						txnSeq,param.getReqSys(),sysInfo.getSysCd(),crmRtnMsgVo.getTransIDH()});
				bankRtnMsgVo.setBody(null);
				String errCd=BankErrorCodeCache.getBankErrCode(RspCodeConstant.Upay.UPAY_U99998.getValue());
				bankRtnMsgVo.setRspCode(errCd);
				bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCd));
				txnLog.setRcvRspType(crmRtnMsgVo.getRspType());
				txnLog.setChlRspCode(errCd);
				txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCd));
				txnLog.setChlSubRspCode(errCd);
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCd));
				txnLog.setRcvRspCode(crmRtnMsgVo.getRspCode());
				txnLog.setRcvRspDesc(crmRtnMsgVo.getRspDesc());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return bankRtnMsgVo;
			}
			//得到报文体判断报文体是不是空的
//			String rtBodyStr = (String)crmRtnMsgVo.getBody();
			if(crmRtnMsgVo.getBody()==null |crmRtnMsgVo.getBody().equals("")){
//				if(StringUtils.isBlank(rtBodyStr)){
				log.warn("无应答报文体!银行流水:{},平台流水:{},银行机构:{},CRM机构:{},CRM应答流水:{}", new Object[]{param.getReqTransID(),
						txnSeq,param.getReqSys(),sysInfo.getSysCd(),crmRtnMsgVo.getTransIDH()});
				logger.warn("无应答报文体!银行流水:{},平台流水:{},银行机构:{},CRM机构:{},CRM应答流水:{}", new Object[]{param.getReqTransID(),
						txnSeq,param.getReqSys(),sysInfo.getSysCd(),crmRtnMsgVo.getTransIDH()});
				bankRtnMsgVo.setBody(null);
				String errCd=BankErrorCodeCache.getBankErrCode(RspCodeConstant.Upay.UPAY_U99998.getValue());
				bankRtnMsgVo.setRspCode(errCd);
				bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCd));
				txnLog.setRcvRspType(crmRtnMsgVo.getRspType());
				txnLog.setChlRspCode(errCd);
				txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCd));
				txnLog.setChlSubRspCode(errCd);
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCd));
				txnLog.setRcvRspCode(crmRtnMsgVo.getRspCode());
				txnLog.setRcvRspDesc(crmRtnMsgVo.getRspDesc());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return bankRtnMsgVo;
			}
			
			String rcvTransDt = StrUtil.subString(crmRtnMsgVo.getTransIDOTime(), 0, 8);
			//记录移动的应答流水
			txnLog.setRcvTranshId(crmRtnMsgVo.getTransIDH());
			txnLog.setRcvTranshTm(crmRtnMsgVo.getTransIDHTime());
			txnLog.setRcvTranshDt(rcvTransDt);
			txnLog.setRcvOprDt(rcvTransDt);
			
			//平台发给省的报文如果有TransactionID字段，那么RcvOprId值填TransactionID，
			//如果报文中没有TransactionID，那么RcvOprId不用填 UR单：UPAY_DT-24
			txnLog.setRcvOprId(crmPrintInvoinceReqVo.getTransactionID());
			//txnLog.setRcvOprId(UUIDGenerator.generateUUID());
			
			
			txnLog.setRcvOprTm(crmRtnMsgVo.getTransIDHTime());

			CrmPrintInvoiceRspVo crmPrintInvoiceRspVo = new CrmPrintInvoiceRspVo();
			//解析移动的应答报文体
			MsgHandle.unmarshaller(crmPrintInvoiceRspVo, (String)crmRtnMsgVo.getBody());
			//判断二级返回码是不是成功的返回
			if (!crmPrintInvoiceRspVo.getRspCode().equals(RspCodeConstant.Crm.CRM_0000.getValue())) {
				log.warn("交易失败!银行流水:{},平台流水:{},银行机构:{},CRM机构:{},CRM应答流水:{},CRM应答码:{},CRM应答描述:{}", new Object[]{param.getReqTransID(),
						txnSeq,param.getReqSys(),sysInfo.getSysCd(),crmRtnMsgVo.getTransIDH(),
						crmPrintInvoiceRspVo.getRspCode(),crmPrintInvoiceRspVo.getRspInfo()});
				logger.warn("交易失败!银行流水:{},平台流水:{},银行机构:{},CRM机构:{},CRM应答流水:{},CRM应答码:{},CRM应答描述:{}", new Object[]{param.getReqTransID(),
						txnSeq,param.getReqSys(),sysInfo.getSysCd(),crmRtnMsgVo.getTransIDH(),
						crmPrintInvoiceRspVo.getRspCode(),crmPrintInvoiceRspVo.getRspInfo()});
				
				String errCode = BankErrorCodeCache.getBankErrCode(crmPrintInvoiceRspVo.getRspCode());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setChlRspCode(errCode);
				txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
				txnLog.setChlSubRspCode(errCode);
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));

				txnLog.setRcvRspCode(crmRtnMsgVo.getRspCode());
				txnLog.setRcvRspDesc(crmRtnMsgVo.getRspDesc());
				txnLog.setRcvRspType(crmRtnMsgVo.getRspType());
				txnLog.setRcvSubRspCode(crmPrintInvoiceRspVo.getRspCode());
				txnLog.setRcvSubRspDesc(crmPrintInvoiceRspVo.getRspInfo());
				
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				
				upayCsysTxnLogService.modify(txnLog);
				bankRtnMsgVo.setBody(null);
				bankRtnMsgVo.setRspCode(errCode);
				bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
				return bankRtnMsgVo;
			}
			log.succ("交易成功！UPAY流水:{},银行流水:{},CRM流水:{},CRM:{},银行:{}",new Object[]{txnSeq,bankMsgVo.getReqTransID(),
					crmRtnMsgVo.getTransIDH(),crmRtnMsgVo.getMsgSender(),bankMsgVo.getReqSys()});			
			bankRtnMsgVo.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
			bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
			bankRtnMsgVo.setBody(null);
			//最后更新交易流水
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
			txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
			txnLog.setChlSubRspDesc(crmMsgVo.getRspDesc());
			txnLog.setRcvRspCode(crmRtnMsgVo.getRspCode());
			txnLog.setRcvRspDesc(crmRtnMsgVo.getRspDesc());
			txnLog.setRcvRspType(crmRtnMsgVo.getRspType());
			txnLog.setRcvSubRspCode(crmPrintInvoiceRspVo.getRspCode());
			txnLog.setRcvSubRspDesc(crmPrintInvoiceRspVo.getRspInfo());
			txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.toString());

			txnLog.setRcvOprDt(crmPrintInvoinceReqVo.getSettleDate());
			txnLog.setRcvOprId(crmPrintInvoinceReqVo.getTransactionID());

			upayCsysTxnLogService.modify(txnLog);

		} catch (AppRTException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			log.warn("运行异常!银行流水:{},平台流水:{},银行机构:{}", new Object[]{param.getReqTransID(),
					txnSeq,param.getReqSys()});
			logger.error("intTxnSeq:{},运行异常!" , new Object[]{txnSeq});
			logger.error("运行异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			bankRtnMsgVo.setBody(null);
			bankRtnMsgVo.setRspCode(errCode);
			bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A03.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A03.getDesc()
					+ e.getMessage());
			txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A03.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A03.getDesc()
					+ e.getMessage());
			upayCsysTxnLogService.modify(txnLog);
			return bankRtnMsgVo;
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			log.warn("业务异常!银行流水:{},平台流水:{},银行机构:{}", new Object[]{param.getReqTransID(),
					txnSeq,param.getReqSys()});
			logger.error("intTxnSeq:{},业务异常!" , new Object[]{txnSeq,e});
			logger.error("业务异常:",e);
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			bankRtnMsgVo.setBody(null);
			bankRtnMsgVo.setRspCode(errCode);
			bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));

			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()
					+ e.getMessage());
			txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()
					+ e.getMessage());
			upayCsysTxnLogService.modify(txnLog);
			return bankRtnMsgVo;
		} catch (Exception e) {
			
			log.warn("未知异常!银行流水:{},平台流水:{},银行机构:{}", new Object[]{param.getReqTransID(),
					txnSeq,param.getReqSys()});
			logger.error("intTxnSeq:{},未知异常!" , new Object[]{txnSeq});
			logger.error("未知异常:",e);
			bankRtnMsgVo.setBody(null);
			bankRtnMsgVo.setRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			
			//String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_100?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_100);
			bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"/*+errDesc*/);
			

			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()
					+ e.getMessage());
			txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()
					+ e.getMessage());

			upayCsysTxnLogService.modify(txnLog);
			return bankRtnMsgVo;
		}
		logger.debug("BankPrintInvoinceAction execute(Object) - end");
		return bankRtnMsgVo;
	}

}
