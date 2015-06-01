package com.huateng.cmupay.hessian.message;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.caucho.hessian.client.HessianRuntimeException;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.models.UpayCsysRouteInfo;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.remoting.client.CrmRemoting;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.json.JacksonUtils;

/**
 * @author cmt
 * @version 创建时间：2013-2-19 下午6:05:53 类说明
 */
@Component
public class SendCrmHessianMessageImpl extends
		AbsSendHessianMessage<CrmMsgVo, CrmMsgVo> {
	/**
	 * Logger for this class
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private CrmRemoting crmRemoting ;

	/*@Override
	protected Message putMessageProperties(Message msg, HessianMsgHeader header,
			String context) {

		return msg;
	}*/

	// 发往crm
	public CrmMsgVo sendMsg(CrmMsgVo vo) throws AppBizException {

		logger.debug("SendCrmHessianMessageImpl sendMsg(CrmMsgVo) - start");

		Map<String, String> paramRoute = new HashMap<String, String>();
		paramRoute.put("orgId", vo.getMsgReceiver());
		// paramRoute.put("reqBipCode", reqBipCode);

		UpayCsysRouteInfo routeInfo = findRouteInfo(paramRoute);
		// msgVo.setRouteInfo(routeInfo);
		if (routeInfo == null) {
			logger.error("crm接收方交易路由关闭。");
			throw new AppBizException(RspCodeConstant.Upay.UPAY_U99997.getValue(),
					RspCodeConstant.Upay.UPAY_U99997.getDesc() + "crm接收方交易路由关闭。");
		}

		CrmMsgVo crmMsgVoRtn = new CrmMsgVo();
		String strXml = MsgHandle.marshaller(vo);

		HessianMsgHeader header = new HessianMsgHeader();
		header.setProtocol(routeInfo.getProtocol());
		header.setReqIp(routeInfo.getReqIp());
		header.setReqPort(routeInfo.getReqPort());
		header.setReqPath(routeInfo.getReqPath());
		header.setRouteInfo(routeInfo.getRouteInfo());
		header.setReceiver(CommonConstant.PlatformCd.CrmSys.toString());
		header.setAppCd(vo.getActivityCode());
		//
		header.setMqSeq(Serial.genSerialNo(CommonConstant.Sequence.SendCrmMqSeq
				.toString()));

		String jmsXmlRtn = "";

		try {
			//logger.info("message send to crm:" + strXml );
			log.info("->CRM:{}",new Object[]{strXml});
			String headerJson = JacksonUtils.bean2Json(header);
			jmsXmlRtn = crmRemoting.sendMsg(headerJson, strXml);
			log.info("<-CRM:{}",new Object[]{jmsXmlRtn});
		} catch (HessianRuntimeException e) {
			logger.error("hessian HessianRuntimeException", e);
			vo.setRspCode(RspCodeConstant.Upay.UPAY_U99998.getValue());
			vo.setRspDesc(RspCodeConstant.Upay.UPAY_U99998.getDesc());
			return vo;
			
		}catch (AppRTException e) {
			logger.error("hessian AppRTException", e);
			throw e;
		
		}catch(Exception e){
			logger.error("hessian Exception", e);
			AppBizException	e1 = new AppBizException(RspCodeConstant.Upay.UPAY_U99999.getValue(),e);
			throw e1;
		}
		//logger.info("message received from crm:" + jmsXmlRtn);
		MsgHandle.unmarshaller(crmMsgVoRtn, jmsXmlRtn);
		logger.debug("SendCrmHessianMessageImpl sendMsg(CrmMsgVo) - end");
		return crmMsgVoRtn;

	}

	


}
