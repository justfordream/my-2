package com.huateng.cmupay.jms.message;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;

import org.springframework.stereotype.Component;

import com.caucho.hessian.client.HessianConnectionException;
import com.caucho.hessian.client.HessianProxyFactory;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.jms.common.AbsSendJmsMessage;
import com.huateng.cmupay.jms.common.JmsMsgHeader;
import com.huateng.cmupay.models.UpayCsysRouteInfo;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.remoting.client.BankRemoting;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.json.JacksonUtils;
import com.huateng.toolbox.utils.StringUtil;

/**
 * @author cmt
 * @version 创建时间：2013-2-19 下午6:05:53 类说明
 */
@Component("sendBankJmsMessage")
public class SendBankJmsMessageImpl extends
		AbsSendJmsMessage<BankMsgVo, BankMsgVo> {

	// 发往银行
	@Override
	public BankMsgVo sendMsg(BankMsgVo vo) throws AppBizException {

		logger.debug("SendBankHessianMessageImpl sendMsg(BankMsgVo) - start");

		Map<String, String> paramRoute = new HashMap<String, String>();
		paramRoute.put("orgId", vo.getRcvSys());
		UpayCsysRouteInfo routeInfo = findRouteInfo(paramRoute);
		if (routeInfo == null) {
			logger.warn("发往银行流水号:{},内部交易流水:{},银行机构号:{},银行接收方交易路由关闭.",
					new Object[] {vo.getReqTransID(), vo.getTxnSeq(), vo.getRcvSys()});
			log.warn("发往银行流水号:{},内部交易流水:{},银行机构号:{},银行接收方交易路由关闭.",
					new Object[] {vo.getReqTransID(), vo.getTxnSeq(), vo.getRcvSys()});
			throw new AppBizException(
					RspCodeConstant.Upay.UPAY_U99997.getValue(),
					RspCodeConstant.Upay.UPAY_U99997.getDesc() + "银行接收方交易路由关闭.");
		}

		String strXml = MsgHandle.marshaller(vo);
		BankMsgVo bankMsgVoRtn = new BankMsgVo();
		JmsMsgHeader header = new JmsMsgHeader();
		header.setProtocol(routeInfo.getProtocol());
		header.setReqIp(routeInfo.getReqIp());
		header.setReqPort(routeInfo.getReqPort());
		header.setReqPath(routeInfo.getReqPath());
//		header.setReqIp("127.0.0.1");
//		header.setReqPort("8081");
//		header.setReqPath("/CMUPayImitator/ReceiveMsgAutoResponServletForBank");
		
		header.setRouteInfo(routeInfo.getRouteInfo());
		header.setReceiver(vo.getRcvSys());
		header.setAppCd(vo.getActivityCode());
		header.setMqSeq(Serial
				.genSerialNo(CommonConstant.Sequence.SendBankMqSeq.toString()));
		String jmsXmlRtn = "";
		try {
			String headerJson = JacksonUtils.bean2Json(header);
			log.info("发送银行 发起方流水:{},内部交易流水:{},银行机构号:{}",
					new Object[] { vo.getReqTransID(), vo.getTxnSeq(),
							vo.getRcvSys()});
			logger.info("发送银行 发起方流水:{},内部交易流水:{},银行机构号:{}",
					new Object[] { vo.getReqTransID(), vo.getTxnSeq(),
					vo.getRcvSys()});

			HessianProxyFactory factory = new HessianProxyFactory();
			BankRemoting rt = null;
			StringBuffer sb = new StringBuffer();
			sb.append(routeInfo.getHostProtocol()).append("://");
			sb.append(routeInfo.getHostIp()).append(":");
			sb.append(routeInfo.getHostPort());
			sb.append(routeInfo.getHostPath());
			String url = sb.toString();
			factory.setReadTimeout(Long.parseLong(this.readTimeOut));
			factory.setConnectTimeout(Long.parseLong(this.connectTimeout));
			rt = (BankRemoting) factory.create(BankRemoting.class, url);

			logger.debug("hession URL:{}",url);	
			// BankRemoting remoting =
			// (BankRemoting)ApplicationContextBean.getBean(header.getReceiver()+"_bankRemoting");
			// jmsXmlRtn = remoting.sendMsg(headerJson, strXml);

			jmsXmlRtn = rt.sendMsg(headerJson, strXml);
			//jmsXmlRtn = getRespText();
			if ("".equals(StringUtil.toTrim(jmsXmlRtn))) {
				log.error("银行交易超时返回报文体为空,发起方流水:{},内部交易流水:{},银行机构号:{}",
					new Object[] { vo.getReqTransID(), vo.getTxnSeq(),
							vo.getRcvSys()});
				logger.error("银行交易超时返回报文体为空,发起方流水:{},内部交易流水:{},银行机构号:{}",
						new Object[] { vo.getReqTransID(), vo.getTxnSeq(),
						vo.getRcvSys()});

				bankMsgVoRtn.setRspCode(RspCodeConstant.Upay.UPAY_U99998
						.getValue());
				bankMsgVoRtn.setRspDesc(RspCodeConstant.Upay.UPAY_U99998
						.getDesc());
				return bankMsgVoRtn;
			}
		}catch (HessianConnectionException e) {
			logger.error("银行Hessian 连接异常",e);
			logger.error("银行Hessian 连接异常,发起方流水:{},内部交易流水:{},银行机构号:{},异常:{}",
					new Object[] { vo.getReqTransID(), vo.getTxnSeq(),
					vo.getRcvSys(),e.getMessage()});
			log.error("银行Hessian 连接异常,发起方流水:{},内部交易流水:{},银行机构号:{}",
					new Object[] { vo.getReqTransID(), vo.getTxnSeq(),
					vo.getRcvSys()});
			bankMsgVoRtn.setRspCode(RspCodeConstant.Upay.UPAY_U99998.getValue());
			bankMsgVoRtn.setRspDesc(RspCodeConstant.Upay.UPAY_U99998.getDesc());
			return bankMsgVoRtn;
		}catch(Exception e){
			logger.error("发送银行异常",e);
			logger.error("发送银行异常,发起方流水:{},内部交易流水:{},银行机构号:{},异常:{}",
					new Object[] { vo.getReqTransID(), vo.getTxnSeq(),
					vo.getRcvSys(),e.getMessage()});
			log.error("发送银行异常,发起方流水:{},内部交易流水:{},银行机构号:{}",
					new Object[] { vo.getReqTransID(), vo.getTxnSeq(),
					vo.getRcvSys()});
			AppBizException e1 = new AppBizException(
					RspCodeConstant.Upay.UPAY_U99999.getValue(), e);
			throw e1;
		}


		MsgHandle.unmarshaller(bankMsgVoRtn, jmsXmlRtn);
		if (RspCodeConstant.Bank.BANK_020A00.getValue().equals(
				bankMsgVoRtn.getRspCode())) {
			log.succ("银行返回成功，发起方流水:{}, 落地方流水:{},内部流水:{},银行机构号:{},应答码:{} ", new Object[] {
					vo.getReqTransID(),bankMsgVoRtn.getRcvTransID(), vo.getTxnSeq(),
					bankMsgVoRtn.getRcvSys(),bankMsgVoRtn.getRspCode()});
			logger.info("银行返回成功，发起方流水:{}, 落地方流水:{},内部流水:{},银行机构号:{},应答码:{} ", new Object[] {
					vo.getReqTransID(),bankMsgVoRtn.getRcvTransID(), vo.getTxnSeq(),
					bankMsgVoRtn.getRcvSys(),bankMsgVoRtn.getRspCode()});
		} else {
			log.error("银行返回失败，发起方流水:{}, 落地方流水:{},内部流水:{},银行机构号:{},应答码:{} ", new Object[] {
					vo.getReqTransID(),bankMsgVoRtn.getRcvTransID(), vo.getTxnSeq(),
					bankMsgVoRtn.getRcvSys(),bankMsgVoRtn.getRspCode()});
			logger.error("银行返回失败，发起方流水:{}, 落地方流水:{},内部流水:{},银行机构号:{},应答码:{} ", new Object[] {
					vo.getReqTransID(),bankMsgVoRtn.getRcvTransID(), vo.getTxnSeq(),
					bankMsgVoRtn.getRcvSys(),bankMsgVoRtn.getRspCode()});
		}
		logger.info("银行返回，发起方流水:{}, 落地方流水:{},内部流水:{},银行机构号:{},应答码:{} ", new Object[] {
				vo.getReqTransID(),bankMsgVoRtn.getRcvTransID(), vo.getTxnSeq(),
				bankMsgVoRtn.getRcvSys(),bankMsgVoRtn.getRspCode()});
		log.info("银行返回，发起方流水:{}, 落地方流水:{},内部流水:{},银行机构号:{},应答码:{} ", new Object[] {
				vo.getReqTransID(),bankMsgVoRtn.getRcvTransID(), vo.getTxnSeq(),
				bankMsgVoRtn.getRcvSys(),bankMsgVoRtn.getRspCode()});
		logger.debug("SendBankHessianMessageImpl sendMsg(BankMsgVo) - end");
		return bankMsgVoRtn;

	}

	@Override
	protected Message putMessageProperties(Message msg, JmsMsgHeader header,
			String context) {

		return msg;
	}
	// private String getRespText(){
	// logger.error("################################");
	//
	// return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	// +"<GPay><Header><ActivityCode>020002</ActivityCode><ReqSys>0001</ReqSys><ReqChannel>01</ReqChannel><ReqDate>20130809</ReqDate><ReqTransID>101020130809163138000118927702</ReqTransID><ReqDateTime>20130809163138008</ReqDateTime><ActionCode>1</ActionCode><RcvSys>1001</RcvSys><RcvDate>20130809</RcvDate><RcvTransID>e16d7b51c7aa434a83163079ff1869f2</RcvTransID><RcvDateTime>20130809163300778</RcvDateTime><RspCode>020A00</RspCode><RspDesc>success</RspDesc></Header><Body><SubID>201308091633007799ae09</SubID><SubTime>20130620090412</SubTime><BankAcctID>622339743875843911</BankAcctID><BankAcctType>0</BankAcctType></Body></GPay>";
	// }
}
