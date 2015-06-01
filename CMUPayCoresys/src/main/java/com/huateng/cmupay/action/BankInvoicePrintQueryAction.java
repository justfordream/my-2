package com.huateng.cmupay.action;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.BeanUtils;
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
import com.huateng.cmupay.jms.business.crm.CrmInvoicePrintQueryBus;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysSysMapInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.models.common.multidatasource.DataSourceContextHolder;
import com.huateng.cmupay.models.his.UpayCsysTxnLogHis;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankPrintInvoiceQueryReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankPrintInvoiceQueryRspVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmPrintInvoiceQueryReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmPrintInvoiceQueryRspVo;
import com.huateng.cmupay.utils.UUIDGenerator;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;
import com.huateng.toolbox.xml.XmlUtil;

/**
 * 
 * @author fan_kui 发票打印查询接口
 */
@Controller("bankInvoicePrintQueryAction")
@Scope("prototype")
public class BankInvoicePrintQueryAction extends
		AbsBaseAction<BankMsgVo, BankMsgVo> {

//	private @Value("${unPrintCrm}") String[] crmPrint;
	@Autowired
	private CrmInvoicePrintQueryBus invoicePrintQueryBus;
	
	@Autowired
	private IUpayCsysTxnLogHisService upayCsysTxnLogHisService;
	@Override
	public BankMsgVo execute(BankMsgVo paramData) throws AppBizException {
		logger.debug("BankInvoicePrintQueryAction execute(Object) - start");
		String intTxnTime = paramData.getTxnTime();
		BankMsgVo bankMsgVo = new BankMsgVo();
		bankMsgVo = paramData;
		BankMsgVo bankRtnMsgVo = new BankMsgVo();
		UpayCsysTxnLog upayCsysTxnLog = null;
		BankPrintInvoiceQueryReqVo reqVo = new BankPrintInvoiceQueryReqVo();
		BankPrintInvoiceQueryRspVo respVo = new BankPrintInvoiceQueryRspVo();
		String txnSeq = bankMsgVo.getTxnSeq();
		try {
			// 解析报文体
			MsgHandle.unmarshaller(reqVo, (String) bankMsgVo.getBody());
			String intTxnDate = bankMsgVo.getTxnDate();
			Long seqId = bankMsgVo.getSeqId();
			bankRtnMsgVo = bankMsgVo;
			bankRtnMsgVo.setActionCode(CommonConstant.ActionCode.Respone
					.toString());
			bankRtnMsgVo.setRcvDate(intTxnDate);
			bankRtnMsgVo.setRcvDateTime(intTxnTime);
			bankRtnMsgVo.setRcvTransID(txnSeq);
			logger.info("发票打印查询接口!查是否有交易!发起方系统:reqDomain:{},发起方交易流水号:reqTransId:{}",new Object[]{bankMsgVo.getReqSys(),bankMsgVo.getReqTransID()});
			//检查交易流水表中是否有该笔交易
			Map<String, Object> params1 = new HashMap<String, Object>();
			params1.put("reqDomain", bankMsgVo.getReqSys());
			params1.put("reqTransId", bankMsgVo.getReqTransID());
			upayCsysTxnLog = upayCsysTxnLogService.findObj(params1);
			if (upayCsysTxnLog == null) {
				 upayCsysTxnLog =new UpayCsysTxnLog();
				 upayCsysTxnLog.setSeqId(seqId);
					upayCsysTxnLog.setIntTxnTime(intTxnTime);
					upayCsysTxnLog.setIntMqSeq(bankMsgVo.getMqSeq());
					upayCsysTxnLog.setIntTransCode((bankMsgVo.getTransCode())
							.getTransCode());
					upayCsysTxnLog.setIntTxnDate(intTxnDate);
					upayCsysTxnLog.setIntTxnSeq(txnSeq);
					upayCsysTxnLog.setPayMode(bankMsgVo.getTransCode().getPayMode());
					upayCsysTxnLog.setBussType(bankMsgVo.getTransCode().getBussType());
//					upayCsysTxnLog.setTxnCat(bankMsgVo.getTransCode().getTxnCat());
					upayCsysTxnLog.setBussChl(bankMsgVo.getTransCode().getBussChl());
					upayCsysTxnLog.setStatus(CommonConstant.TxnStatus.InitStatus
							.toString());
					upayCsysTxnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
					// 记录银行头信息
					upayCsysTxnLog.setReqActivityCode(bankMsgVo.getActivityCode());
					upayCsysTxnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
					upayCsysTxnLog.setReqDomain(bankMsgVo.getReqSys());
					upayCsysTxnLog.setReqCnlType(bankMsgVo.getReqChannel());
					upayCsysTxnLog.setReqTransDt(bankMsgVo.getReqDate());
					upayCsysTxnLog.setReqTransId(bankMsgVo.getReqTransID());
					upayCsysTxnLog.setReqTransTm(bankMsgVo.getReqDateTime());
					upayCsysTxnLog.setReqTranshDt(intTxnDate);
					upayCsysTxnLog.setReqTranshId(txnSeq);
					upayCsysTxnLog.setReqTranshTm(intTxnTime);
					
					upayCsysTxnLog.setMainFlag(CommonConstant.Mainflag.Master.getValue());
					upayCsysTxnLog.setBankId(bankMsgVo.getReqSys());
					upayCsysTxnLog.setRcvCnlType(bankMsgVo.getReqChannel());
					// 记录银行体信息
					upayCsysTxnLog.setIdProvince(reqVo.getBankProv());
					upayCsysTxnLog.setOriOprTransId(reqVo.getOriReqTransID());
					upayCsysTxnLog.setOriOrgId(reqVo.getOriReqSys());
					upayCsysTxnLog.setOriReqDate(reqVo.getOriReqDate());
					upayCsysTxnLog.setReqOprDt(bankMsgVo.getReqDate());
					upayCsysTxnLog.setReqOprId(bankMsgVo.getReqTransID());
					upayCsysTxnLog.setReqOprTm(bankMsgVo.getReqDateTime());
					upayCsysTxnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
					upayCsysTxnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
					upayCsysTxnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
					upayCsysTxnLog.setSettleDate(bankMsgVo.getReqDate());
					upayCsysTxnLogService.add(upayCsysTxnLog);
					logger.debug("insert succ,intTxnSeq:{}",new Object[]{txnSeq});
			} 
//			// 解析报文体
//			MsgHandle.unmarshaller(reqVo, (String) bankMsgVo.getBody());
			// 校验报文体格式
			String valitMsg = this.validateModel(reqVo);
			if (StringUtils.isNotBlank(valitMsg)) {
				String code = valitMsg.split(":")[0];
				logger.warn("发票打印查询接口!格式校验失败!银行流水:{},平台流水:{},银行机构:{},错误码:{},错误描述:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),code,RspCodeConstant.Bank.getDescByValue(code)});
				log.warn("发票打印查询接口!发票打印查询接口!发票打印查询接口!格式校验失败!银行流水:{},平台流水:{},银行机构:{},错误码:{},错误描述:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),code,RspCodeConstant.Bank.getDescByValue(code)});
				bankRtnMsgVo.setBody(respVo);
				bankRtnMsgVo.setRspCode(code);
				bankRtnMsgVo.setRspDesc(valitMsg);
				// 更新流水
				upayCsysTxnLog.setStatus(CommonConstant.TxnStatus.TxnFail
						.toString());
				upayCsysTxnLog.setChlRspCode(code);
				upayCsysTxnLog.setChlRspDesc(valitMsg);
				upayCsysTxnLog.setChlSubRspCode(code);
				upayCsysTxnLog.setChlSubRspDesc(valitMsg);
				upayCsysTxnLog
						.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
								.getDateyyyyMMddHHmmssSSS());

				upayCsysTxnLogService.modify(upayCsysTxnLog);
				logger.debug("BankInvoicePrintQueryAction execute(Object) - end");
				return bankRtnMsgVo;
			}
			logger.info("校验成功!银行流水:{},平台流水:{},银行机构:{}",new Object[]{paramData.getReqTransID(),txnSeq,paramData.getReqSys()});
			
			/**
			 * 判断移动省代码是否可以打印发票信息
			 * notAllow=true 代表不可以打印   配合移动测试，暂时注释掉，上线改
			 */
			String crmPrint = DictCodeCache.getDictCode(DictConst.DictId.UnPrintCrm.getValue(), 
					DictConst.CodeId.UnPrintCrm.getValue()).getCodeValue2();
			Boolean notAllow=Arrays.asList(crmPrint).contains(reqVo.getBankProv());
			if(notAllow){
				log.warn("发票打印查询接口!发票打印查询接口!发票打印查询接口!该省不能打印发票!银行流水:{},平台流水:{},银行机构:{},打印省:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),reqVo.getBankProv()});
				logger.warn("发票打印查询接口!该省不能打印发票!银行流水:{},平台流水:{},银行机构:{},打印省:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),reqVo.getBankProv()});
				bankRtnMsgVo.setRspCode(RspCodeConstant.Bank.BANK_013A09.getValue());
				bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_013A09.getDesc());
				bankRtnMsgVo.setBody(respVo);
				upayCsysTxnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				upayCsysTxnLog.setChlRspCode(RspCodeConstant.Bank.BANK_013A09.getValue());
				upayCsysTxnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_013A09.getDesc());
				upayCsysTxnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_013A09.getValue());
				upayCsysTxnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_013A09.getDesc());
	
				upayCsysTxnLog.setLastUpdTime(DateUtil
					.getDateyyyyMMddHHmmssSSS());
	
				upayCsysTxnLogService.modify(upayCsysTxnLog);
				logger.debug("BankInvoicePrintQueryAction execute(Object) - end");
				return bankRtnMsgVo;
			}
			
			// 根据下面3个条件查出唯一的原缴费交易
			UpayCsysTxnLog oriTxnLog = null;
			Map<String, Object> logMap = new HashMap<String, Object>();
			logMap.put("reqTransId", reqVo.getOriReqTransID());
			logMap.put("reqDomain", reqVo.getOriReqSys());
			logMap.put("reqTransDt", reqVo.getOriReqDate());
			logMap.put("status", CommonConstant.TxnStatus.TxnSuccess.getValue());
			
			SimpleDateFormat myFormatter = new SimpleDateFormat("yyyyMMdd");
			java.util.Date date1 = myFormatter.parse(paramData.getTxnDate());
			java.util.Date date2 = myFormatter.parse(reqVo.getOriReqDate());
			long date3 = (date1.getTime() - date2.getTime())/ (24 * 60 * 60 * 1000);// 表示相隔天数
			boolean isHis = date3 <=Long.valueOf(
					DictCodeCache.getDictCode(DictConst.DictId.InsertIntoHisTxnLogDay.getValue(),
							DictConst.CodeId.ChangeHisTxnLog.getValue()).getCodeValue2());
			UpayCsysTxnLogHis logHis=null;
			if(isHis){
				
				oriTxnLog=upayCsysTxnLogService.findObj(logMap);
				
				if (oriTxnLog== null) {
					logger.warn("发票打印查询接口!原缴费不存在!银行流水:{},平台流水:{},银行机构:{},缴费流水:{},缴费机构:{},缴费时间:{}", new Object[]{paramData.getReqTransID(),
							txnSeq,paramData.getReqSys(),reqVo.getOriReqTransID(),reqVo.getOriReqSys(),reqVo.getOriReqDate()});
					log.warn("发票打印查询接口!发票打印查询接口!发票打印查询接口!原缴费不存在!银行流水:{},平台流水:{},银行机构:{},缴费流水:{},缴费机构:{},缴费时间:{}", new Object[]{paramData.getReqTransID(),
							txnSeq,paramData.getReqSys(),reqVo.getOriReqTransID(),reqVo.getOriReqSys(),reqVo.getOriReqDate()});
					bankRtnMsgVo
							.setRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
					bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
					bankRtnMsgVo.setBody(respVo);
					// 更新流水
					upayCsysTxnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
					upayCsysTxnLog.setChlRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
					upayCsysTxnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
					upayCsysTxnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
					upayCsysTxnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());

					upayCsysTxnLog.setLastUpdTime(DateUtil
							.getDateyyyyMMddHHmmssSSS());

					upayCsysTxnLogService.modify(upayCsysTxnLog);
					logger.debug("BankInvoicePrintQueryAction execute(Object) - end");
					return bankRtnMsgVo;
				}
				
			}else{
				DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_HIS);
//				logHis = upayCsysTxnLogHisService.findObj(logMap);
				logHis = upayCsysTxnLogHisService.findHisStlObj(logMap);
			
				if (logHis== null) {
					logger.warn("发票打印查询接口!原缴费不存在!银行流水:{},平台流水:{},银行机构:{},缴费流水:{},缴费机构:{},缴费时间:{}", new Object[]{paramData.getReqTransID(),
							txnSeq,paramData.getReqSys(),reqVo.getOriReqTransID(),reqVo.getOriReqSys(),reqVo.getOriReqDate()});
					log.warn("发票打印查询接口!发票打印查询接口!发票打印查询接口!原缴费不存在!银行流水:{},平台流水:{},银行机构:{},缴费流水:{},缴费机构:{},缴费时间:{}", new Object[]{paramData.getReqTransID(),
							txnSeq,paramData.getReqSys(),reqVo.getOriReqTransID(),reqVo.getOriReqSys(),reqVo.getOriReqDate()});
					bankRtnMsgVo
							.setRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
					bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
					bankRtnMsgVo.setBody(respVo);
					// 更新流水
					upayCsysTxnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
					upayCsysTxnLog.setChlRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
					upayCsysTxnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());
					upayCsysTxnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_014A05.getValue());
					upayCsysTxnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_014A05.getDesc());

					upayCsysTxnLog.setLastUpdTime(DateUtil
							.getDateyyyyMMddHHmmssSSS());

					upayCsysTxnLogService.modify(upayCsysTxnLog);
					logger.debug("BankInvoicePrintQueryAction execute(Object) - end");
					return bankRtnMsgVo;
				}
			}
			if(!isHis){
				DataSourceContextHolder.setDataSourceType(DataSourceInstances.DS_NOW);
			}
			String idValue=oriTxnLog==null?logHis.getIdValue():oriTxnLog.getIdValue();
			String idProvince=oriTxnLog==null?logHis.getIdProvince():oriTxnLog.getIdProvince();
			upayCsysTxnLog.setIdValue(idValue);
			upayCsysTxnLog.setPayAmt(oriTxnLog==null?logHis.getPayAmt():oriTxnLog.getPayAmt());
			
			//从缓存中取出系统编码映射数据，并得到4位省机构编码
			UpayCsysSysMapInfo sysInfo = SysMapCache.getProvCd(reqVo.getBankProv());
			//判断原交易省代码与请求报文体中的银行省代码是否一致
			 if (!idProvince.equals(reqVo.getBankProv())) {
				 log.warn("发票打印查询接口!发票打印查询接口!发票打印查询接口!缴费省不一致!银行流水:{},平台流水:{},银行机构:{},缴费流水:{},缴费机构:{},缴费时间:{},缴费省:{}", new Object[]{paramData.getReqTransID(),
						 txnSeq,paramData.getReqSys(),reqVo.getOriReqTransID(),reqVo.getOriReqSys(),reqVo.getOriReqDate()
							,idProvince});
				 logger.warn("发票打印查询接口!缴费省不一致!银行流水:{},平台流水:{},银行机构:{},缴费流水:{},缴费机构:{},缴费时间:{},缴费省:{}", new Object[]{paramData.getReqTransID(),
						 txnSeq,paramData.getReqSys(),reqVo.getOriReqTransID(),reqVo.getOriReqSys(),reqVo.getOriReqDate()
							,idProvince});
				 bankRtnMsgVo
				 .setRspCode(RspCodeConstant.Bank.BANK_013A08.getValue());
				 bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_013A08.getDesc());
				 bankRtnMsgVo.setBody(respVo);
				 // 更新流水
				 upayCsysTxnLog.setStatus(CommonConstant.TxnStatus.TxnFail
				 .toString());
				 upayCsysTxnLog.setChlRspCode(RspCodeConstant.Bank.BANK_013A08.getValue());
				 upayCsysTxnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_013A08.getDesc());
				 upayCsysTxnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_013A08.getValue());
				 upayCsysTxnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_013A08.getDesc());
				
				 upayCsysTxnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				
				 upayCsysTxnLogService.modify(upayCsysTxnLog);
				 logger.debug("BankInvoicePrintQueryAction execute(Object) - end");
				 return bankRtnMsgVo;
			 }
			 
//			String  orgFlag = offOrgTrans(bankMsgVo.getReqSys(), sysInfo.getSysCd(), paramData.getTransCode().getTransCode());
			// 查找该号码是属于移动的还是联通电信的
			ProvincePhoneNum provincePhoneNum = ProvAreaCache.getProvAreaByPrimary(idValue);
			String orgFlag = offOrgTrans(bankMsgVo.getReqSys(), sysInfo.getSysCd(), paramData.getTransCode().getTransCode(), 
					provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
//			boolean orgFlag = isO2OTransOn(bankMsgVo.getReqSys(), sysInfo.getSysCd(), paramData.getTransCode().getTransCode());
			 //校验落地方省CRM是服务的权限 bankMsgVo = paramData;
//			boolean orgFlag = orgStatusCheck(sysInfo.getSysCd());
			if (orgFlag != null) {
				logger.warn("发票打印查询接口!CRM无权限!银行流水:{},平台流水:{},银行机构:{},CRM机构:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),sysInfo.getSysCd()});
				log.info("发票打印查询接口!发票打印查询接口!发票打印查询接口!CRM无权限!银行流水:{},平台流水:{},银行机构:{},CRM机构:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),sysInfo.getSysCd()});
				bankRtnMsgVo
						.setRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				bankRtnMsgVo.setBody(respVo);
				upayCsysTxnLog.setStatus(CommonConstant.TxnStatus.TxnFail
						.toString());
				upayCsysTxnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				upayCsysTxnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				upayCsysTxnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				upayCsysTxnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc() + orgFlag);
				upayCsysTxnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(upayCsysTxnLog);
				logger.debug("BankInvoicePrintQueryAction execute(Object) - end");
				return bankRtnMsgVo;
			}
			
			String rcvOprId=oriTxnLog==null?logHis.getRcvOprId():oriTxnLog.getRcvOprId();
			String crmSettleDate=oriTxnLog==null?logHis.getIntTxnDate():oriTxnLog.getIntTxnDate();
			
			// 组装发往移动请求报文
			CrmPrintInvoiceQueryReqVo crmPrintInvoiceQueryReqVo = new CrmPrintInvoiceQueryReqVo();
			crmPrintInvoiceQueryReqVo
					.setTransactionID(rcvOprId);
			crmPrintInvoiceQueryReqVo.setIdValue(idValue);
			crmPrintInvoiceQueryReqVo.setSettleDate(crmSettleDate);
			crmPrintInvoiceQueryReqVo.setPayed(oriTxnLog==null?logHis.getPayAmt():oriTxnLog.getPayAmt());
			crmPrintInvoiceQueryReqVo.setBankID(oriTxnLog==null?logHis.getBankId():oriTxnLog.getBankId());
			crmPrintInvoiceQueryReqVo.setCnlTyp(bankMsgVo.getReqChannel());
			crmPrintInvoiceQueryReqVo.setBankProv(reqVo.getBankProv());//TODO by 张宁
			CrmMsgVo crmMsgVo = new CrmMsgVo();
			crmMsgVo.setActionCode(CommonConstant.ActionCode.Requset.toString());
			crmMsgVo.setActivityCode(CommonConstant.CrmTrans.Crm10.toString());
			crmMsgVo.setBIPCode(CommonConstant.Bip.Bis20.toString());
			crmMsgVo.setBody(crmPrintInvoiceQueryReqVo);
			crmMsgVo.setTestFlag(testFlag);
			crmMsgVo.setVersion(ExcConstant.CRM_VERSION);
			crmMsgVo.setOrigDomain(CommonConstant.OrgDomain.UPSS.toString());
			crmMsgVo.setRouteType(CommonConstant.RouteType.RoutePhone
					.toString());
			crmMsgVo.setHomeDomain(CommonConstant.OrgDomain.BOSS.toString());
			crmMsgVo.setRouteValue(idValue);
			crmMsgVo.setSessionID(txnSeq);
			crmMsgVo.setTransIDO(txnSeq);
			crmMsgVo.setTransIDOTime(StrUtil.subString(intTxnTime, 0, 14));
			crmMsgVo.setMsgReceiver(sysInfo.getSysCd());
			
			upayCsysTxnLog.setRcvActivityCode(CommonConstant.CrmTrans.Crm10
					.toString());
			upayCsysTxnLog.setRcvBipCode(CommonConstant.Bip.Bis20.toString());
			upayCsysTxnLog.setRcvDomain(sysInfo.getSysCd());
			upayCsysTxnLog.setRcvRspType(CommonConstant.CrmRspType.Success
					.toString());
			
			upayCsysTxnLog.setRcvRouteVal(crmMsgVo.getRouteValue());
			upayCsysTxnLog.setRcvRouteType(crmMsgVo.getRouteType());
			
			upayCsysTxnLog.setRcvSessionId(txnSeq);
			upayCsysTxnLog.setRcvTransId(txnSeq);
			upayCsysTxnLog.setRcvTransTm(intTxnTime);
			upayCsysTxnLog.setRcvTransDt(intTxnDate);
			upayCsysTxnLog.setRcvVersion(crmMsgVo.getVersion());
			upayCsysTxnLog.setRcvDomain(sysInfo.getSysCd());
			////平台发给省的报文如果有TransactionID字段，那么RcvOprId值填TransactionID，
			//如果报文中没有TransactionID，那么RcvOprId不用填 UR单：UPAY_DT-243
			upayCsysTxnLog.setRcvOprId(rcvOprId);
			//upayCsysTxnLog.setRcvOprId(UUIDGenerator.generateUUID());
			// 发往发票查询信息至移动省
			CrmMsgVo crmMsgVoRtn = invoicePrintQueryBus.execute(crmMsgVo,
					crmPrintInvoiceQueryReqVo, upayCsysTxnLog, null);
			//记录返回的移动报文内容
			upayCsysTxnLog.setRcvTranshDt(StrUtil.subString(crmMsgVoRtn.getTransIDHTime(), 0, 8));
			upayCsysTxnLog.setRcvTranshId(crmMsgVoRtn.getTransIDH());
			upayCsysTxnLog.setRcvTranshTm(crmMsgVoRtn.getTransIDHTime());
			upayCsysTxnLog.setRcvOprDt(StrUtil.subString(crmMsgVoRtn.getTransIDHTime(), 0, 8));
			upayCsysTxnLog.setSettleDate(crmSettleDate);
			//检测是否平台收到超时
			if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(
					crmMsgVoRtn.getRspCode())) {
				log.warn("发票打印查询接口!发票打印查询接口!发票打印查询接口!平台接收超时!银行流水:{},平台流水:{},银行机构:{},CRM机构:{},CRM应答流水:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),sysInfo.getSysCd(),crmMsgVoRtn.getTransIDH()});
				logger.warn("发票打印查询接口!平台流水intTxnSeq:'{}',核心平台检测到响应超时,rspCode:'{}'",
						txnSeq, crmMsgVoRtn.getRspCode());
				bankRtnMsgVo.setBody(respVo);
				String errCd=BankErrorCodeCache.getBankErrCode(RspCodeConstant.Upay.UPAY_U99998.getValue());
				bankRtnMsgVo.setRspCode(errCd);
				bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCd));
//				upayCsysTxnLog.setRcvRspType(CommonConstant.CrmRspType.BusErr
//						.toString());
				upayCsysTxnLog.setChlRspCode(errCd);
				upayCsysTxnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCd));
				upayCsysTxnLog.setChlSubRspCode(errCd);
				upayCsysTxnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCd));
				upayCsysTxnLog.setRcvRspCode(crmMsgVoRtn.getRspCode());
				upayCsysTxnLog.setRcvRspDesc(crmMsgVoRtn.getRspDesc());
				upayCsysTxnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(upayCsysTxnLog);
				logger.debug("BankInvoicePrintQueryAction execute(Object) - end");
				return bankRtnMsgVo;
			}
			//得到报文体判断报文体是不是空的
//			String rtBodyStr = (String)crmMsgVoRtn.getBody();
//			if(StringUtils.isBlank(rtBodyStr)){
			if(crmMsgVoRtn.getBody()==null || crmMsgVoRtn.getBody().equals("")){
				log.warn("发票打印查询接口!发票打印查询接口!发票打印查询接口!无应答报文体!银行流水:{},平台流水:{},银行机构:{},CRM机构:{},CRM应答流水:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),sysInfo.getSysCd(),crmMsgVoRtn.getTransIDH()});
				logger.warn("无应答报文体!银行流水:{},平台流水:{},银行机构:{},CRM机构:{},CRM应答流水:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),sysInfo.getSysCd(),crmMsgVoRtn.getTransIDH()});
				bankRtnMsgVo.setRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
				bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc());
				bankRtnMsgVo.setBody(respVo);
				upayCsysTxnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
				upayCsysTxnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc());		
				upayCsysTxnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
				upayCsysTxnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc());
				upayCsysTxnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				upayCsysTxnLogService.modify(upayCsysTxnLog);
				logger.debug("BankInvoicePrintQueryAction execute(Object) - end");
				return bankRtnMsgVo;
			}
			//转换报文体为实体类
			String objBean = (String) crmMsgVoRtn.getBody();
			Document doc = DocumentHelper.parseText(objBean);
			Element root = doc.getRootElement();
			Document rootCDATA = DocumentHelper.parseText(root.getTextTrim());
			String cdataText = rootCDATA.getRootElement().asXML();
			CrmPrintInvoiceQueryRspVo crmPrintInvoiceQueryRspVo = XmlUtil
					.toBean(cdataText, CrmPrintInvoiceQueryRspVo.class);
			//将移动返回的应答码映射到银行端的应答码
			String errCode = BankErrorCodeCache
					.getBankErrCode(crmPrintInvoiceQueryRspVo.getRspCode());
			//判断二级应答码是打印还是未打印  2A07表示已打印
			if(crmPrintInvoiceQueryRspVo.getRspCode().
					equals(RspCodeConstant.Crm.CRM_2A07.getValue())){
				bankRtnMsgVo.setRspCode(errCode);
				bankRtnMsgVo.setRspDesc(crmPrintInvoiceQueryRspVo.getRspInfo());
				respVo.setPrintStatus(CommonConstant.PrintStatus.Printed.toString());
				
				upayCsysTxnLog.setChlRspCode(errCode);
				upayCsysTxnLog.setChlRspDesc(crmPrintInvoiceQueryRspVo.getRspInfo());
				upayCsysTxnLog.setChlSubRspDesc(crmPrintInvoiceQueryRspVo.getRspInfo());
				upayCsysTxnLog.setChlSubRspCode(errCode);

				upayCsysTxnLog.setRcvRspCode(crmMsgVoRtn.getRspCode());
				upayCsysTxnLog.setRcvRspDesc(crmMsgVoRtn.getRspDesc());
				upayCsysTxnLog.setRcvRspType(crmMsgVoRtn.getRspType());
				upayCsysTxnLog.setRcvSubRspCode(crmPrintInvoiceQueryRspVo.getRspCode());
				upayCsysTxnLog.setRcvSubRspDesc(crmPrintInvoiceQueryRspVo.getRspInfo());
			}else if(crmPrintInvoiceQueryRspVo.getRspCode().
					equals(RspCodeConstant.Crm.CRM_2A06.getValue())
					|| RspCodeConstant.Crm.CRM_0000.getValue().equals(crmPrintInvoiceQueryRspVo.getRspCode())){
				respVo.setPrintStatus(CommonConstant.PrintStatus.UnPrinted.toString());
				bankRtnMsgVo.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
				bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
				List<CrmPrintInvoiceQueryRspVo.Invoice> invoiceList = crmPrintInvoiceQueryRspVo
						.getListInvoices();
				//添加非空校验
				if(crmPrintInvoiceQueryRspVo.getListInvoices()!=null){
					for (int i = 0; i < crmPrintInvoiceQueryRspVo.getListInvoices()
							.size(); i++) {
						crmPrintInvoiceQueryRspVo.getListInvoices().get(i)
								.setAcctDate(null);
					}					
				}
				crmPrintInvoiceQueryRspVo.setListInvoices(invoiceList);
				BeanUtils.copyProperties(crmPrintInvoiceQueryRspVo, respVo);
				upayCsysTxnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
				upayCsysTxnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
				upayCsysTxnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
				upayCsysTxnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());

				upayCsysTxnLog.setRcvRspCode(crmMsgVoRtn.getRspCode());
				upayCsysTxnLog.setRcvRspDesc(crmMsgVoRtn.getRspDesc());
				upayCsysTxnLog.setRcvRspType(crmMsgVoRtn.getRspType());
				upayCsysTxnLog.setRcvSubRspCode(crmPrintInvoiceQueryRspVo.getRspCode());
				upayCsysTxnLog.setRcvSubRspDesc(crmPrintInvoiceQueryRspVo.getRspInfo());
				
			}else{
				log.warn("发票打印查询接口!发票打印查询接口!交易失败!银行流水:{},平台流水:{},银行机构:{},CRM机构:{},CRM应答流水:{},CRM应答码:{},CRM应答描述:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),sysInfo.getSysCd(),crmMsgVoRtn.getTransIDH(),
						crmPrintInvoiceQueryRspVo.getRspCode(),crmPrintInvoiceQueryRspVo.getRspInfo()});
				logger.warn("交易失败!银行流水:{},平台流水:{},银行机构:{},CRM机构:{},CRM应答流水:{},CRM应答码:{},CRM应答描述:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),sysInfo.getSysCd(),crmMsgVoRtn.getTransIDH(),
						crmPrintInvoiceQueryRspVo.getRspCode(),crmPrintInvoiceQueryRspVo.getRspInfo()});
				bankRtnMsgVo.setBody(respVo);
				bankRtnMsgVo.setRspCode(errCode);
				bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
//				upayCsysTxnLog.setRcvRspType(CommonConstant.CrmRspType.BusErr
//						.toString());
				upayCsysTxnLog.setChlRspCode(errCode);
				upayCsysTxnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
				upayCsysTxnLog.setChlSubRspCode(errCode);
				upayCsysTxnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
				upayCsysTxnLog.setRcvRspCode(crmMsgVoRtn.getRspCode());
				upayCsysTxnLog.setRcvRspDesc(crmMsgVoRtn.getRspDesc());
				upayCsysTxnLog.setRcvSubRspCode(crmPrintInvoiceQueryRspVo.getRspCode());
				upayCsysTxnLog.setRcvSubRspDesc(crmPrintInvoiceQueryRspVo.getRspInfo());
				upayCsysTxnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(upayCsysTxnLog);
				logger.debug("BankInvoicePrintQueryAction execute(Object) - end");
				return bankRtnMsgVo;
			}
			log.succ("交易成功！UPAY流水:{},银行流水:{},CRM流水:{},CRM:{},银行:{}",new Object[]{txnSeq,bankMsgVo.getReqTransID(),
					crmMsgVoRtn.getTransIDH(),crmMsgVoRtn.getMsgSender(),bankMsgVo.getReqSys()});			
			bankRtnMsgVo.setBody(respVo);
//			bankRtnMsgVo.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
//			bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
			//更新成功交易流水
			upayCsysTxnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess
					.toString());
			upayCsysTxnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(upayCsysTxnLog);		
//				if (crmPrintInvoiceQueryRspVo.getListInvoices().size() > invoincePrintCount
//						|| crmPrintInvoiceQueryRspVo.getListInvoices().size() == 0) {
//					logger.info("发票打印消息无或者发票打印条数超过6条");
//
//					bankRtnMsgVo.setBody(respVo);
//					bankRtnMsgVo.setRspCode(MessageHandler
//							.getBankErrCode("015A03"));
//					bankRtnMsgVo.setRspDesc(crmPrintInvoiceQueryRspVo
//							.getRspInfo());
//
//					upayCsysTxnLog.setStatus(CommonConstant.TxnStatus.TxnFail
//							.toString());
//					upayCsysTxnLog.setChlRspCode(MessageHandler
//							.getBankErrCode("015A03"));
//					upayCsysTxnLog.setChlRspDesc(MessageHandler
//							.getBankErrCode("015A03"));
//					upayCsysTxnLog.setChlSubRspDesc(MessageHandler
//							.getBankErrCode("015A03"));
//					upayCsysTxnLog.setChlSubRspCode(MessageHandler
//							.getBankErrCode("015A03"));
//
//					upayCsysTxnLog.setRcvRspCode(MessageHandler
//							.getCrmErrCode("2998"));
//					upayCsysTxnLog.setRcvRspDesc(MessageHandler
//							.getCrmErrMsg("2998"));
//					upayCsysTxnLog.setRcvRspType(crmMsgVoRtn.getRspType());
//					upayCsysTxnLog.setRcvSubRspCode(crmPrintInvoiceQueryRspVo
//							.getRspCode());
//					upayCsysTxnLog.setRcvSubRspDesc(crmPrintInvoiceQueryRspVo
//							.getRspInfo());
//					upayCsysTxnLog.setRcvTransId(txnSeq);
//					upayCsysTxnLog.setRcvTransTm(intTxnTime);
//					upayCsysTxnLog.setRcvTransDt(intTxnDate);
//					upayCsysTxnLog.setRcvTranshId(crmMsgVoRtn.getTransIDH());
//					upayCsysTxnLog
//							.setRcvTranshTm(crmMsgVoRtn.getTransIDHTime());
//					upayCsysTxnLog.setRcvTranshDt(rcvTransDt);
//					upayCsysTxnLog.setRcvOprDt(oriTxnLog.getIntTxnSeq());
//					upayCsysTxnLog.setRcvOprId(StrUtil.subString(
//							oriTxnLog.getRcvTransTm(), 0, 8));
//
//					upayCsysTxnLog
//							.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
//									.getDateyyyyMMddHHmmssSSS());
//
//					upayCsysTxnLogService.modify(upayCsysTxnLog);
//					return bankRtnMsgVo;
//
//				}
				
		} catch (AppRTException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			log.warn("发票打印查询接口!发票打印查询接口!运行异常!银行流水:{},平台流水:{},银行机构:{}", new Object[]{paramData.getReqTransID(),
					txnSeq,paramData.getReqSys()});
			logger.error("发票打印查询接口!intTxnSeq:{},运行异常!" , new Object[]{txnSeq});
			logger.error("发票打印查询接口!运行异常:",e);
			upayCsysTxnLog.setStatus(CommonConstant.TxnStatus.TxnFail
					.toString());
			bankRtnMsgVo.setRspCode(errCode);
			bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));

			upayCsysTxnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A03.getValue());
			upayCsysTxnLog.setChlRspDesc(e.getMessage());
			upayCsysTxnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A03.getValue());
			upayCsysTxnLog.setChlSubRspDesc(e.getMessage());
			upayCsysTxnLogService.modify(upayCsysTxnLog);
			logger.debug("BankInvoicePrintQueryAction execute(Object) - end");
			return bankRtnMsgVo;
		} catch (AppBizException e) {
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			log.warn("发票打印查询接口!发票打印查询接口!业务异常!银行流水:{},平台流水:{},银行机构:{}", new Object[]{paramData.getReqTransID(),
					txnSeq,paramData.getReqSys()});
			logger.error("发票打印查询接口!intTxnSeq:{},业务异常!" , new Object[]{txnSeq});
			logger.error("发票打印查询接口!业务异常:",e);
			upayCsysTxnLog.setStatus(CommonConstant.TxnStatus.TxnFail
					.toString());
			bankRtnMsgVo.setRspCode(errCode);
			bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			upayCsysTxnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			upayCsysTxnLog.setChlRspDesc(e.getMessage());
			upayCsysTxnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			upayCsysTxnLog.setChlSubRspDesc(e.getMessage());

			upayCsysTxnLogService.modify(upayCsysTxnLog);
			logger.debug("BankInvoicePrintQueryAction execute(Object) - end");
			return bankRtnMsgVo;
		} catch (Exception e) {
			logger.error("发票打印查询接口!intTxnSeq:{},未知异常!" , new Object[]{txnSeq});
			log.warn("发票打印查询接口!发票打印查询接口!未知异常!银行流水:{},平台流水:{},银行机构:{}", new Object[]{paramData.getReqTransID(),
					txnSeq,paramData.getReqSys()});
			logger.error("发票打印查询接口!未知异常:",e);
			bankRtnMsgVo.setRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			
			//String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_100?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_100);
			bankRtnMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"/*+errDesc*/);

			upayCsysTxnLog.setLastUpdTime(com.huateng.toolbox.utils.DateUtil
					.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			upayCsysTxnLog.setChlRspDesc(e.getMessage());
			upayCsysTxnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			upayCsysTxnLog.setChlSubRspDesc(e.getMessage());
			upayCsysTxnLogService.modify(upayCsysTxnLog);
			logger.debug("BankInvoicePrintQueryAction execute(Object) - end");
			return bankRtnMsgVo;
		}
		logger.debug("BankInvoicePrintQueryAction execute(Object) - end");
		return bankRtnMsgVo;
	}
}
