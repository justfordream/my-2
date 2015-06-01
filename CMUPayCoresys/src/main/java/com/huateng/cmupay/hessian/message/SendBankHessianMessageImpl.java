package com.huateng.cmupay.hessian.message;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.caucho.hessian.client.HessianRuntimeException;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.models.UpayCsysRouteInfo;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.utils.Serial;

/**
 * @author cmt
 * @version 创建时间：2013-2-19 下午6:05:53 类说明
 */
@Component
public class SendBankHessianMessageImpl extends
		AbsSendHessianMessage<BankMsgVo, BankMsgVo> {
	// @Autowired
	// private BankRemoting bankRemoting ;

	// @Override
	// protected Message putMessageProperties(Message msg, HessianMsgHeader
	// header,
	// String context) {
	//
	// return msg;
	// }

	// 发往银行
	@Override
	public BankMsgVo sendMsg(BankMsgVo bankMsgVo) throws AppBizException {

		logger.debug("SendBankHessianMessageImpl sendMsg(BankMsgVo) - start");

		Map<String, String> paramRoute = new HashMap<String, String>();
		paramRoute.put("orgId", bankMsgVo.getRcvSys());
		// paramRoute.put("reqBipCode", reqBipCode);

		UpayCsysRouteInfo routeInfo = findRouteInfo(paramRoute);
		// msgVo.setRouteInfo(routeInfo);
		if (routeInfo == null) {
			logger.error("银行接收方交易路由关闭.bankID:", bankMsgVo.getRcvSys());
			throw new AppBizException(RspCodeConstant.Upay.UPAY_U99997.getValue(),
					RspCodeConstant.Upay.UPAY_U99997.getDesc() + "银行接收方交易路由关闭.");
		}

		String strXml = MsgHandle.marshaller(bankMsgVo);
		BankMsgVo bankMsgVoRtn = new BankMsgVo();
		HessianMsgHeader header = new HessianMsgHeader();
		header.setProtocol(routeInfo.getProtocol());
		header.setReqIp(routeInfo.getReqIp());
		header.setReqPort(routeInfo.getReqPort());
		header.setReqPath(routeInfo.getReqPath());
		header.setRouteInfo(routeInfo.getRouteInfo());
		header.setReceiver(bankMsgVo.getRcvSys());
		header.setAppCd(bankMsgVo.getActivityCode());
		//
		header.setMqSeq(Serial
				.genSerialNo(CommonConstant.Sequence.SendBankMqSeq.toString()));

		String jmsXmlRtn = "";

		try {
			// logger.debug("message send to bank is:" + strXml);
			log.info("->BANK:{}",new Object[]{strXml});
			// String headerJson = JacksonUtils.bean2Json(header);
			// jmsXmlRtn = bankRemoting.sendMsg(headerJson, strXml);
			log.info("<-BANK:{}",new Object[]{jmsXmlRtn});

		} catch (HessianRuntimeException e) {
			logger.error("Hessian HessianRuntimeException", e);
			bankMsgVoRtn.setRspCode(RspCodeConstant.Upay.UPAY_U99998.getValue());
			bankMsgVoRtn.setRspDesc(RspCodeConstant.Upay.UPAY_U99997.getDesc());
			return bankMsgVoRtn;

		} catch (AppRTException e) {
			logger.error("hessian AppRTException", e);
			throw e;

		} catch (Exception e) {
			logger.error("Hessian Exception", e);
			AppBizException e1 = new AppBizException(
					RspCodeConstant.Upay.UPAY_U99999.getValue(), e);
			throw e1;
		}
		// logger.debug("message received from bank is:" + jmsXmlRtn);
		MsgHandle.unmarshaller(bankMsgVoRtn, jmsXmlRtn);

		logger.debug("SendBankHessianMessageImpl sendMsg(BankMsgVo) - end");
		return bankMsgVoRtn;

	}

}
