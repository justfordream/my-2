package com.huateng.cmupay.action;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.controller.cache.BankErrorCodeCache;
import com.huateng.cmupay.controller.cache.SysMapCache;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.business.crm.CrmCompareUserInfoBus;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.cmupay.models.UpayCsysSysMapInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankCompareUserInfoReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankCompareUserInfoRespVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmCompareUserInfoReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmCompareUserInfoRspVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.cmupay.utils.StringFormat;
import com.huateng.cmupay.utils.UUIDGenerator;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StrUtil;
/**
 * 
 * @author fan_kui
 *  银行发起身份验证
 */
@Controller("bankCompareUserInfoAction")
@Scope("prototype")
public class BankCompareUserInfoAction extends  AbsBaseAction<BankMsgVo,BankMsgVo>{
	

	@Autowired
	private CrmCompareUserInfoBus compareUserInfoBus;

	@Override
	public BankMsgVo execute(BankMsgVo paramData) throws AppBizException {
		logger.debug("BankCompareUserInfoAction execute(Object) - start");
		//得到报文
		BankMsgVo bankMsgVo=paramData;		
		//得到平台时间，日期和交易流水
		String intTxnTime = bankMsgVo.getTxnTime();
		String intTxnDate = bankMsgVo.getTxnDate();
		String txnSeq =  bankMsgVo.getTxnSeq();
		Long seqId=bankMsgVo.getSeqId();
		//银行请求过来的Vo
		BankCompareUserInfoReqVo reqVo=new BankCompareUserInfoReqVo();
		//应答给银行侧的报文Vo
		BankMsgVo msgVoRtn=paramData;
		//设置应答报文头返回值
		msgVoRtn.setRcvDate(bankMsgVo.getTxnDate());
		msgVoRtn.setRcvDateTime(intTxnTime);
		msgVoRtn.setRcvTransID(txnSeq);
		msgVoRtn.setActionCode(CommonConstant.ActionCode.Respone.toString());
		//应答给银行侧报文体Vo
		BankCompareUserInfoRespVo respVO=new BankCompareUserInfoRespVo();
		UpayCsysTxnLog txnLog=new UpayCsysTxnLog();
		try{
			//解析银行侧请求报文体
			MsgHandle.unmarshaller(reqVo, (String)bankMsgVo.getBody());
			//生成初始交易流水			
			txnLog.setSeqId(seqId);
			txnLog.setIntTxnTime(intTxnTime);
			txnLog.setIntMqSeq(bankMsgVo.getMqSeq());
			txnLog.setIntTransCode((bankMsgVo.getTransCode()).getTransCode());
			txnLog.setIntTxnDate(intTxnDate);
			txnLog.setIntTxnSeq(txnSeq);
			txnLog.setPayMode(bankMsgVo.getTransCode().getPayMode());
			txnLog.setBussType(bankMsgVo.getTransCode().getBussType());
			txnLog.setBussChl(bankMsgVo.getTransCode().getBussChl());
			txnLog.setReconciliationFlag(CommonConstant.YesOrNo.No.toString());
//			txnLog.setTxnCat(bankMsgVo.getTransCode().getTxnCat());
			txnLog.setStatus(CommonConstant.TxnStatus.InitStatus.toString());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			//记录报文头信息
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
			//记录报文体信息
			txnLog.setIdType(reqVo.getIDType());
			txnLog.setIdValue(reqVo.getIDValue());
			
			txnLog.setBankId(bankMsgVo.getReqSys());
			txnLog.setMainFlag(CommonConstant.Mainflag.Master.getValue());
			
			txnLog.setUserName(StringFormat.formatNameString(reqVo.getUserName()));
			txnLog.setUserId(StringFormat.formatCodeString(reqVo.getUserID()));
			txnLog.setUserType(reqVo.getUserIDType());
			txnLog.setBackFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setRefundFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setReverseFlag(CommonConstant.YesOrNo.No.toString());
			txnLog.setSettleDate(bankMsgVo.getReqDate());
			upayCsysTxnLogService.add(txnLog);
			//校验请求报文体格式
			String valiMsg=this.validateModel(reqVo);
			if (!StringUtils.isNotBlank(valiMsg)) {
				logger.debug("valida success!intTxnSeq:{}",new Object[]{txnSeq});			
			}else{
				String code=valiMsg.split(":")[0];
				log.warn("格式校验失败!银行流水:{},平台流水:{},银行机构:{},错误码:{},错误描述:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),code,RspCodeConstant.Bank.getDescByValue(code)});
				logger.warn("报文体check失败!intTxnSeq:{},错误描述:{}" , new Object[]{txnSeq, valiMsg});
				
				//组装错误应答报文
				respVO.setRspCode(RspCodeConstant.Bank.BANK_012A99.getValue());
				respVO.setRspInfo(RspCodeConstant.Bank.BANK_012A99.getDesc());
				msgVoRtn.setBody(respVO);
				msgVoRtn.setRspCode(code);
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(code));
				//更新失败交易流水
				txnLog.setChlRspCode(code);
				txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(code));
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A99.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A99.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}		
			//查询手机号码归属省ID 省代码为3位 
//			String proId=upayCsysImsiLdCdService.findProvinceByMobileNumber(reqVo.getIdValue());
//			String proId = ProvAreaCache.getProvAreaByPrimary(reqVo.getIdValue());
//			String proId = findProvinceByMobileNumber(reqVo.getIDValue());		
			ProvincePhoneNum provincePhoneNum = findProvinceByMobileNumber(reqVo.getIDValue());	
			String proId = provincePhoneNum == null ? null : provincePhoneNum.getProvinceCode();

			// 从三期库中查找到省代码，如果未找到将返回错误报文
			if(proId==null){
				log.warn("查无手机号段信息!银行流水:{},平台流水:{},银行机构:{},号码:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),reqVo.getIDValue()});
				logger.warn("查无手机号段信息!银行流水:{},平台流水:{},银行机构:{},号码:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),reqVo.getIDValue()});
				respVO.setRspCode(RspCodeConstant.Bank.BANK_012A17.getValue());
				respVO.setRspInfo(RspCodeConstant.Bank.BANK_012A17.getDesc());
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_012A17.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_012A17.getDesc());
				msgVoRtn.setBody(respVO);
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A17.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A17.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A17.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A17.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			txnLog.setIdProvince(proId);
			//从缓存中取出系统编码映射数据，并得到4位省机构编码
			UpayCsysSysMapInfo upayCsysSysMapInfo=SysMapCache.getProvCd(proId);	
			//校验机构权限 bankMsgVo=paramData
//			String orgFlag = offOrgTrans(bankMsgVo.getReqSys(), upayCsysSysMapInfo.getSysCd(), paramData.getTransCode().getTransCode());
			//校验机构权限 bankMsgVo=paramData
			String orgFlag = offOrgTrans(bankMsgVo.getReqSys(), upayCsysSysMapInfo.getSysCd(), paramData.getTransCode().getTransCode(), 
					provincePhoneNum != null ? provincePhoneNum.getPhoneNumFlag() : CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
//			boolean	orgFlag = isO2OTransOn(bankMsgVo.getReqSys(), upayCsysSysMapInfo.getSysCd(), paramData.getTransCode().getTransCode());
//			boolean	orgFlag = orgStatusCheck(upayCsysSysMapInfo.getSysCd());
			if (orgFlag != null) {
				log.info("CRM无权限!银行流水:{},平台流水:{},银行机构:{},CRM机构:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),upayCsysSysMapInfo.getSysCd()});
				logger.warn("CRM无权限!银行流水:{},平台流水:{},银行机构:{},CRM机构:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),upayCsysSysMapInfo.getSysCd()});
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				respVO.setRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				respVO.setRspInfo(RspCodeConstant.Bank.BANK_012A18.getDesc());
				msgVoRtn.setBody(respVO);
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_012A18.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_012A18.getDesc());
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A99.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A99.getDesc()+orgFlag);
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			//组装发往移动报文
			CrmCompareUserInfoReqVo crmCompareUserInfoReqVo=new CrmCompareUserInfoReqVo();
			crmCompareUserInfoReqVo.setIdType(reqVo.getIDType());
			crmCompareUserInfoReqVo.setIdValue(reqVo.getIDValue());
			crmCompareUserInfoReqVo.setUserID(reqVo.getUserID());
			crmCompareUserInfoReqVo.setUserIDType(reqVo.getUserIDType());
			crmCompareUserInfoReqVo.setUserName(reqVo.getUserName());
			CrmMsgVo crmMsgVo=new CrmMsgVo();
			crmMsgVo.setBody(crmCompareUserInfoReqVo);
			crmMsgVo.setActionCode(CommonConstant.ActionCode.Requset.toString());
			crmMsgVo.setRouteType(CommonConstant.RouteType.RoutePhone.toString());
			crmMsgVo.setRouteValue(reqVo.getIDValue());
			crmMsgVo.setActivityCode(CommonConstant.CrmTrans.Crm15.toString());
			crmMsgVo.setBIPCode(CommonConstant.Bip.Bis21.toString());
			crmMsgVo.setHomeDomain(CommonConstant.OrgDomain.BOSS.toString());
			crmMsgVo.setMqSeq(Serial.genSerialNo(CommonConstant.Sequence.SendCrmMqSeq.getValue()));
			crmMsgVo.setOrigDomain(CommonConstant.OrgDomain.UPSS.toString());		
			crmMsgVo.setSessionID(txnSeq);
			crmMsgVo.setTestFlag(testFlag);
			crmMsgVo.setTransCode(crmMsgVo.getTransCode());
			crmMsgVo.setTransIDO(txnSeq);
			crmMsgVo.setTransIDOTime(StrUtil.subString(intTxnTime, 0, 14));
			crmMsgVo.setVersion(ExcConstant.CRM_VERSION);
			crmMsgVo.setMsgReceiver(upayCsysSysMapInfo.getSysCd());
			//记录发往移动报文消息
			txnLog.setRcvActivityCode(CommonConstant.CrmTrans.Crm15.toString());
			txnLog.setRcvBipCode(CommonConstant.Bip.Bis21.toString());
			txnLog.setRcvDomain(upayCsysSysMapInfo.getSysCd());
			txnLog.setRcvRouteType(crmMsgVo.getRouteType());
			txnLog.setRcvRouteVal(crmMsgVo.getRouteValue());
			txnLog.setRcvSessionId(txnSeq);
			txnLog.setRcvTransDt(intTxnDate);
			txnLog.setRcvTransId(txnSeq);
			txnLog.setRcvTransTm(intTxnTime);
			txnLog.setRcvVersion(ExcConstant.CRM_VERSION);
			//发送报文至移动侧
			CrmMsgVo crmMsgVoRtn=compareUserInfoBus.execute(crmMsgVo,null, txnLog, null);
			//记录移动侧返回的报文头应答信息
			txnLog.setRcvTranshDt(StrUtil.subString(crmMsgVoRtn.getTransIDHTime(), 0, 8));
			txnLog.setRcvTranshId(crmMsgVoRtn.getTransIDH());
			txnLog.setRcvTranshTm(crmMsgVoRtn.getTransIDHTime());
			txnLog.setRcvOprDt(StrUtil.subString(crmMsgVoRtn.getTransIDHTime(), 0, 8));
			//txnLog.setRcvOprId(UUIDGenerator.generateUUID());
			txnLog.setRcvOprTm(crmMsgVoRtn.getTransIDHTime());
			//判断应答报文是否超时
			if (RspCodeConstant.Upay.UPAY_U99998.getValue().equals(
					crmMsgVoRtn.getRspCode())) {
				log.warn("平台接收超时!银行流水:{},平台流水:{},银行机构:{},CRM机构:{},CRM应答流水:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),upayCsysSysMapInfo.getSysCd(),crmMsgVoRtn.getTransIDH()});
				logger.warn("intTxnSeq:{},核心平台检测到响应超时,rspCode:{}",
						txnSeq, crmMsgVoRtn.getRspCode());
				respVO.setRspCode(RspCodeConstant.Bank.BANK_012A99.getValue());
				respVO.setRspInfo(RspCodeConstant.Bank.BANK_012A99.getDesc());
				msgVoRtn.setBody(respVO);
				String errCd=BankErrorCodeCache.getBankErrCode(RspCodeConstant.Upay.UPAY_U99998.getValue());
				msgVoRtn.setRspCode(errCd);
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCd));
//				txnLog.setRcvRspType(parseRspType(msgVoRtn.getRspCode()));
				txnLog.setChlRspCode(errCd);
				txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCd));
				txnLog.setChlSubRspCode(errCd);
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCd));
				txnLog.setRcvRspCode(crmMsgVoRtn.getRspCode());
				txnLog.setRcvRspDesc(crmMsgVoRtn.getRspDesc());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			//得到移动侧的应答报文体
//			String rtBodyStr = (String)crmMsgVoRtn.getBody();
			//判断得到的报文体是不是为空
//			if(StringUtils.isBlank(rtBodyStr)){
			if(crmMsgVoRtn.getBody()==null || crmMsgVoRtn.getBody().equals("")){
				log.warn("无应答报文体!银行流水:{},平台流水:{},银行机构:{},CRM机构:{},CRM应答流水:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),upayCsysSysMapInfo.getSysCd(),crmMsgVoRtn.getTransIDH()});
				logger.error("无应答报文体!银行流水:{},平台流水:{},银行机构:{},CRM机构:{},CRM应答流水:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),upayCsysSysMapInfo.getSysCd(),crmMsgVoRtn.getTransIDH()});
				respVO.setRspCode(RspCodeConstant.Bank.BANK_012A99.getValue());
				respVO.setRspInfo(RspCodeConstant.Bank.BANK_012A99.getDesc());
				msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc());
				msgVoRtn.setBody(respVO);
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
				txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc());		
				txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A99.getValue());
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A99.getDesc());
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			//解析应答报文体
			CrmCompareUserInfoRspVo crmCompareUserInfoRspVo=new CrmCompareUserInfoRspVo();
			MsgHandle.unmarshaller(crmCompareUserInfoRspVo, (String)crmMsgVoRtn.getBody()); 
			//判断返回的报文体应答码是不是成功的应答码，这里是与0000比对
			if(!RspCodeConstant.Crm.CRM_0000.getValue().equals(crmCompareUserInfoRspVo.getRspCode())){
				log.warn("交易失败!银行流水:{},平台流水:{},银行机构:{},CRM机构:{},CRM应答流水:{},CRM应答码:{},CRM应答描述:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),upayCsysSysMapInfo.getSysCd(),crmMsgVoRtn.getTransIDH(),
						crmCompareUserInfoRspVo.getRspCode(),crmCompareUserInfoRspVo.getRspInfo()});
				logger.error("交易失败!银行流水:{},平台流水:{},银行机构:{},CRM机构:{},CRM应答流水:{},CRM应答码:{},CRM应答描述:{}", new Object[]{paramData.getReqTransID(),
						txnSeq,paramData.getReqSys(),upayCsysSysMapInfo.getSysCd(),crmMsgVoRtn.getTransIDH(),
						crmCompareUserInfoRspVo.getRspCode(),crmCompareUserInfoRspVo.getRspInfo()});
				String code=BankErrorCodeCache.getBankErrCode(crmCompareUserInfoRspVo.getRspCode());
				respVO.setRspCode(code);
				respVO.setRspInfo(RspCodeConstant.Bank.getDescByValue(code));
				msgVoRtn.setRspCode(code);
				msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(code));
				msgVoRtn.setBody(respVO);
				txnLog.setRcvRspCode(crmMsgVoRtn.getRspCode());
				txnLog.setRcvRspDesc(RspCodeConstant.Wzw.getDescByValue(crmMsgVoRtn.getRspCode()));
				txnLog.setRcvRspType(crmMsgVoRtn.getRspType());
				txnLog.setRcvSubRspCode(crmCompareUserInfoRspVo.getRspCode());
				txnLog.setRcvSubRspDesc(crmCompareUserInfoRspVo.getRspInfo());
				txnLog.setChlRspCode(code);
				txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(code));
				txnLog.setChlSubRspCode(code);
				txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(code));
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
				txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
				upayCsysTxnLogService.modify(txnLog);
				return msgVoRtn;
			}
			//组装正确应答报文至银行侧
			log.succ("交易成功！UPAY流水:{},银行流水:{},CRM流水:{},CRM:{},银行:{}",new Object[]{txnSeq,bankMsgVo.getReqTransID(),
					crmMsgVoRtn.getTransIDH(),crmMsgVoRtn.getMsgSender(),bankMsgVo.getReqSys()});
			respVO.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
			respVO.setRspInfo(RspCodeConstant.Bank.BANK_010A00.getDesc());
			msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
			msgVoRtn.setRspDesc(crmCompareUserInfoRspVo.getRspInfo());
			msgVoRtn.setBody(respVO);
			//更新成功交易流水
			txnLog.setRcvRspCode(RspCodeConstant.Wzw.WZW_0000.getValue());
			txnLog.setRcvRspDesc(RspCodeConstant.Wzw.WZW_0000.getDesc());
			txnLog.setRcvRspType(crmMsgVoRtn.getRspType());
			txnLog.setRcvSubRspCode(crmCompareUserInfoRspVo.getRspCode());
			txnLog.setRcvSubRspDesc(crmCompareUserInfoRspVo.getRspInfo());
			txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
			txnLog.setChlRspDesc(crmCompareUserInfoRspVo.getRspInfo());
			txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_010A00.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_010A00.getDesc());
			txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess.toString());
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			upayCsysTxnLogService.modify(txnLog);
		}catch(AppRTException e){
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			log.warn("运行异常!银行流水:{},平台流水:{},银行机构:{}", new Object[]{paramData.getReqTransID(),
					txnSeq,paramData.getReqSys()});
			logger.error("intTxnSeq:{},运行异常!" , new Object[]{txnSeq} );
			logger.error("运行异常:",e);
			respVO.setRspCode(RspCodeConstant.Bank.BANK_012A99.getValue());
			respVO.setRspInfo(RspCodeConstant.Bank.BANK_012A99.getDesc());
			msgVoRtn.setRspCode(errCode);
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			msgVoRtn.setBody(respVO);
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));		
			txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A99.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A99.getDesc());
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			upayCsysTxnLogService.modify(txnLog);
			return msgVoRtn;
		}catch(AppBizException e){
			String errCode = e.getCode();
			errCode = BankErrorCodeCache.getBankErrCode(errCode);
			log.warn("业务异常!银行流水:{},平台流水:{},银行机构:{}", new Object[]{paramData.getReqTransID(),
					txnSeq,paramData.getReqSys()});
			logger.error("intTxnSeq:{},业务异常" , new Object[]{txnSeq});
			logger.error("业务异常:",e);
			respVO.setRspCode(RspCodeConstant.Bank.BANK_012A99.getValue());
			respVO.setRspInfo(RspCodeConstant.Bank.BANK_012A99.getDesc());
			msgVoRtn.setRspCode(errCode);
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			msgVoRtn.setBody(respVO);
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(errCode);
			txnLog.setChlRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));		
			txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A99.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.getDescByValue(errCode));
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			upayCsysTxnLogService.modify(txnLog);
			return msgVoRtn;
		}catch(Exception e){
			log.warn("未知异常!银行流水:{},平台流水:{},银行机构:{}", new Object[]{paramData.getReqTransID(),
					txnSeq,paramData.getReqSys()});
			logger.error("intTxnSeq:{},未知异常!" , new Object[]{txnSeq} );
			logger.error("未知异常:",e);
			respVO.setRspCode(RspCodeConstant.Bank.BANK_012A99.getValue());
			respVO.setRspInfo(RspCodeConstant.Bank.BANK_012A99.getDesc());
			msgVoRtn.setRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			
			//String errDesc=e.getMessage().length()<=ExcConstant.MSG_LENGTH_100?e.getMessage():e.getMessage().substring(0, ExcConstant.MSG_LENGTH_100);
			msgVoRtn.setRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"/*+errDesc*/);

			msgVoRtn.setBody(respVO);
			txnLog.setLastUpdTime(DateUtil.getDateyyyyMMddHHmmssSSS());
			txnLog.setChlRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
			txnLog.setChlRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+":"+e.getMessage());		
			txnLog.setChlSubRspCode(RspCodeConstant.Bank.BANK_012A99.getValue());
			txnLog.setChlSubRspDesc(RspCodeConstant.Bank.BANK_012A99.getDesc());
			txnLog.setStatus(CommonConstant.TxnStatus.TxnFail.toString());
			upayCsysTxnLogService.modify(txnLog);
			return msgVoRtn;	
		}
		logger.debug("BankCompareUserInfoAction execute(Object) - end");
		return msgVoRtn;
	}
	
}
