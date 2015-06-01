package com.huateng.cmupay.jms.business.crm;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.DictConst;
import com.huateng.cmupay.controller.cache.DictCodeCache;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.jms.business.common.AbsCommonBus;
import com.huateng.cmupay.jms.message.SendCrmJmsMessageImpl;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankReverseMsgReqVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmUseInfoQueryReq;

/**
 * @author ning.z
 * @version 手机号码查询冲正处理
 * 此类已不用，应删除
 */
@Deprecated
public class CrmQueryMobileReverseBus extends
		AbsCommonBus<CrmMsgVo, CrmMsgVo, Map<String, Object>> {

	// 重发次数
//	private @Value("${retry.num}")
//	Long retryNum;

	@Autowired
	private IUpayCsysTxnLogService uPayCsysTxnLogService;
	@Autowired
	private SendCrmJmsMessageImpl sendCrmJmsMessage;

	public CrmMsgVo execute(CrmMsgVo msgVo, Map<String, Object> params,
			UpayCsysTxnLog txnLog, UpayCsysBindInfo info)
			throws AppBizException {

		CrmMsgVo crmMsgVoRevert = new CrmMsgVo();

		// bankMsgVo.setReqSys(CommonConstant.BankOrgCode.CMCC.toString());//TODO//此处需要确认
		// 组装报文头信息

		// 报文体
		CrmUseInfoQueryReq crmUseInfoQueryReq = (CrmUseInfoQueryReq) msgVo
				.getBody();
		crmUseInfoQueryReq.setIdType("");
		crmUseInfoQueryReq.setIdValue("");
		msgVo.setBody(crmUseInfoQueryReq);

		BankReverseMsgReqVo revertReqVo = new BankReverseMsgReqVo();
		revertReqVo.setOriReqSys("0001");
		revertReqVo.setOriReqDate(txnLog.getOuterTransTm().substring(0,8));
		revertReqVo.setOriReqTransID(txnLog.getOuterTransId());
		txnLog.setOriOprTransId(txnLog.getOuterTransId());
		txnLog.setOriOrgId("0001");
		txnLog.setOriReqDate(txnLog.getOuterTransTm().substring(0,8));
		Long retryNum=Long.parseLong(DictCodeCache.getDictCode(DictConst.DictId.RetryTimes.getValue(), 
					DictConst.CodeId.RetryTimes.getValue()).getCodeValue2());
		for (int i = 0; i < retryNum.intValue(); i++) {
			try {

				// 超时进行冲正处理
				crmMsgVoRevert = sendCrmJmsMessage.sendMsg(msgVo);

			} catch (AppBizException e) {

				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
						.toString());
				uPayCsysTxnLogService.modify(txnLog);
				throw e;

			} catch (RuntimeException e) {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
						.toString());
				uPayCsysTxnLogService.modify(txnLog);
				throw e;
			}
			if (!"000000".equals(crmMsgVoRevert.getRspCode())) {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnFail
						.toString());
				uPayCsysTxnLogService.modify(txnLog);
			} else {
				txnLog.setStatus(CommonConstant.TxnStatus.TxnSuccess
						.toString());
				txnLog.setReverseFlag(CommonConstant.YesOrNo.Yes.toString());
				uPayCsysTxnLogService.modify(txnLog);
			}

		}

		return crmMsgVoRevert;
	}

}
