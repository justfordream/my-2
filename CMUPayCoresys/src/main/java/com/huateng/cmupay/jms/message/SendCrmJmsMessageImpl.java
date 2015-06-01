package com.huateng.cmupay.jms.message;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteProxyFailureException;
import org.springframework.stereotype.Component;

import com.caucho.hessian.client.HessianConnectionException;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.jms.common.AbsSendJmsMessage;
import com.huateng.cmupay.jms.common.JmsMsgHeader;
import com.huateng.cmupay.models.UpayCsysRouteInfo;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgFromBankVo;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.remoting.client.CrmRemoting;
import com.huateng.toolbox.json.JacksonUtils;
import com.huateng.toolbox.utils.StringUtil;

/**
 * @author cmt
 * @version 创建时间：2013-2-19 下午6:05:53 类说明
 */
@Component("sendCrmJmsMessage")
public class SendCrmJmsMessageImpl extends
		AbsSendJmsMessage<CrmMsgVo, CrmMsgVo> {

	@Autowired
	private CrmRemoting crmRemoting;

	// 发往crm
	public CrmMsgVo sendMsg(CrmMsgVo vo) throws AppBizException {

		logger.debug("SendCrmHessianMessageImpl sendMsg(CrmMsgVo) - start");

		Map<String, String> paramRoute = new HashMap<String, String>();
		paramRoute.put("orgId", vo.getMsgReceiver());
		// paramRoute.put("reqBipCode", reqBipCode);

		UpayCsysRouteInfo routeInfo = findRouteInfo( paramRoute);
		// msgVo.setRouteInfo(routeInfo);
		if (routeInfo == null) {
			logger.warn(
					"发往省流水号:{},内部交易流水:{},省机构号:{},省接收方交易路由关闭.",
					new Object[] { vo.getTransIDO(), vo.getTxnSeq(),
							vo.getMsgReceiver() });
			log.warn("发往省流水号:{},内部交易流水:{},省机构号:{},省接收方交易路由关闭.", new Object[] {
					vo.getTransIDO(), vo.getTxnSeq(), vo.getMsgReceiver() });
			throw new AppBizException(
					RspCodeConstant.Upay.UPAY_U99997.getValue(),
					RspCodeConstant.Upay.UPAY_U99997.getDesc() + "省接收方交易路由关闭。");
		}

		CrmMsgVo crmMsgVoRtn = new CrmMsgVo();
		String strXml = MsgHandle.marshaller(new CrmMsgFromBankVo(vo));

		JmsMsgHeader header = new JmsMsgHeader();
		header.setProtocol(routeInfo.getProtocol());
		header.setReqIp(routeInfo.getReqIp());
		header.setReqPort(routeInfo.getReqPort());
		header.setReqPath(routeInfo.getReqPath());
		header.setRouteInfo(routeInfo.getRouteInfo());
//		header.setReqIp("127.0.0.1");
//		header.setReqPort("8081");
//		header.setReqPath("/CMUPayImitator/ReceiveMsgAutoResponServletForCRM");
		
		header.setReceiver(CommonConstant.PlatformCd.CrmSys.toString());
		header.setAppCd(vo.getActivityCode());
		String jmsXmlRtn = "";

		try {
			log.info(
					"发送省 发起方流水:{},内部交易流水:{},省机构号:{}",
					new Object[] { vo.getTransIDO(), vo.getTxnSeq(),
							vo.getMsgReceiver(), });
			logger.info(
					"发送省 发起方流水:{},内部交易流水:{},省机构号:{},",
					new Object[] { vo.getTransIDO(), vo.getTxnSeq(),
							vo.getMsgReceiver() });
			String headerJson = JacksonUtils.bean2Json(header);
			jmsXmlRtn = crmRemoting.sendMsg(headerJson, strXml);
			// vo.setBody(null);
			if ("".equals(StringUtil.toTrim(jmsXmlRtn))) {
				log.error(
						"省交易超时返回报文体为空,发起方流水:{},内部交易流水:{},省机构号:{},",
						new Object[] { vo.getTransIDO(), vo.getTxnSeq(),
								vo.getMsgReceiver() });
				logger.error(
						"省交易超时返回报文体为空,发起方流水:{},内部交易流水:{},省机构号:{},",
						new Object[] { vo.getTransIDO(), vo.getTxnSeq(),
								vo.getMsgReceiver() });
				crmMsgVoRtn.setRspCode(RspCodeConstant.Upay.UPAY_U99998
						.getValue());
				crmMsgVoRtn.setRspDesc(RspCodeConstant.Upay.UPAY_U99998
						.getDesc());
				return crmMsgVoRtn;
			}

		} catch (RemoteProxyFailureException e) {
			logger.error(
					"省Hessian 连接异常,发起方流水:{},内部交易流水:{},省机构号:{},异常:{}",
					new Object[] { vo.getTransIDO(), vo.getTxnSeq(),
							vo.getMsgReceiver(), e.getMessage() });
			logger.error("省Hessian 连接异常", e);
			log.error("省Hessian 连接异常,发起方流水:{},内部交易流水:{},省机构号:{}", new Object[] {
					vo.getTransIDO(), vo.getTxnSeq(), vo.getMsgReceiver() });

			vo.setRspCode(RspCodeConstant.Upay.UPAY_U99998.getValue());
			vo.setRspDesc(RspCodeConstant.Upay.UPAY_U99998.getDesc());
			return crmMsgVoRtn;

		} catch (RemoteConnectFailureException e) {
			logger.error(
					"省Hessian 连接异常,发起方流水:{},内部交易流水:{},省机构号:{}异常:{}",
					new Object[] { vo.getTransIDO(), vo.getTxnSeq(),
							vo.getMsgReceiver(), e.getMessage() });
			logger.error("省Hessian 连接异常", e);
			log.error("省Hessian 连接异常,发起方流水:{},内部交易流水:{},省机构号:{}", new Object[] {
					vo.getTransIDO(), vo.getTxnSeq(), vo.getMsgReceiver() });
			vo.setRspCode(RspCodeConstant.Upay.UPAY_U99998.getValue());
			vo.setRspDesc(RspCodeConstant.Upay.UPAY_U99998.getDesc());
			return crmMsgVoRtn;

		} catch (HessianConnectionException e) {
			logger.error(
					"省Hessian 连接异常,发起方流水:{},内部交易流水:{},省机构号:{},异常:{}",
					new Object[] { vo.getTransIDO(), vo.getTxnSeq(),
							vo.getMsgReceiver(), e.getMessage() });
			logger.error("省Hessian 连接异常", e);
			log.error("省Hessian 连接异常,发起方流水:{},内部交易流水:{},省机构号:{}", new Object[] {
					vo.getTransIDO(), vo.getTxnSeq(), vo.getMsgReceiver() });
			vo.setRspCode(RspCodeConstant.Upay.UPAY_U99998.getValue());
			vo.setRspDesc(RspCodeConstant.Upay.UPAY_U99998.getDesc());
			return crmMsgVoRtn;

		} catch (AppRTException e) {
			logger.error(
					"发往省运行期异常,发起方流水:{},内部交易流水:{},省机构号:{}异常:{}",
					new Object[] { vo.getTransIDO(), vo.getTxnSeq(),
							vo.getMsgReceiver(),
							vo.getTransCode().getTransCode(), e.getMessage() });
			logger.error("发往省运行期异常", e);
			log.error(
					"发往省运行期异常,发起方流水:{},内部交易流水:{},省机构号:{}",
					new Object[] { vo.getTransIDO(), vo.getTxnSeq(),
							vo.getMsgReceiver(),
							vo.getTransCode().getTransCode() });
			throw e;

		} catch (Exception e) {
			logger.error(
					"发往省未知异常,发起方流水:{},内部交易流水:{},省机构号:{},异常:{}",
					new Object[] { vo.getTransIDO(), vo.getTxnSeq(),
							vo.getMsgReceiver(), e.getMessage() });
			logger.error("发往省未知异常", e);
			log.error(
					"发往省未知异常,发起方流水:{},内部交易流水:{},省机构号:{}",
					new Object[] { vo.getTransIDO(), vo.getTxnSeq(),
							vo.getMsgReceiver() });
			AppBizException e1 = new AppBizException(
					RspCodeConstant.Upay.UPAY_U99999.getValue(), e);
			throw e1;
		}
		MsgHandle.unmarshaller(crmMsgVoRtn, jmsXmlRtn);
		if (RspCodeConstant.Crm.CRM_0000.getValue().equals(
				crmMsgVoRtn.getRspCode())) {
			log.succ(
					"省返回成功，发起方流水:{}, 落地方流水:{},内部流水:{},省机构号:{},应答码:{} ",
					new Object[] { vo.getTransIDO(),
							crmMsgVoRtn.getTransIDH(), vo.getTxnSeq(),
							crmMsgVoRtn.getMsgReceiver(),
							crmMsgVoRtn.getRspCode() });
		} else {
			log.error(
					"省返回失败，发起方流水:{}, 落地方流水:{},内部流水:{},省机构号:{},应答码:{} ",
					new Object[] { vo.getTransIDO(),
							crmMsgVoRtn.getTransIDH(), vo.getTxnSeq(),
							crmMsgVoRtn.getMsgReceiver(),
							crmMsgVoRtn.getRspCode() });
		}
		logger.info(
				"省返回，发起方流水:{}, 落地方流水:{},内部流水:{},省机构号:{},应答码:{} ",
				new Object[] { vo.getTransIDO(),
						crmMsgVoRtn.getTransIDH(), vo.getTxnSeq(),
						crmMsgVoRtn.getMsgReceiver(), crmMsgVoRtn.getRspCode() });
		logger.debug("SendCrmHessianMessageImpl sendMsg(CrmMsgVo) - end");
		return crmMsgVoRtn;

	}

	@Override
	protected Message putMessageProperties(Message msg, JmsMsgHeader header,
			String context) throws JMSException {
		// TODO Auto-generated method stub
		return msg;
	}

}
