package com.huateng.cmupay.listener;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.BeanUtils;
import org.springframework.jms.core.MessageCreator;

import com.huateng.cmupay.action.IBaseAction;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankGpay;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankHeader;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankPrintInvoiceQueryRspVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StringUtil;
import com.huateng.toolbox.xml.XmlUtil;

/**
 * 
 * @author cmt
 * 
 */
public class BankMessageListener extends AbsMessageListener {

	private Map<String, IBaseAction<BankMsgVo, BankMsgVo>> transactionMap;

	public void setTransactionMap(
			Map<String, IBaseAction<BankMsgVo, BankMsgVo>> transactionMap) {
		this.transactionMap = transactionMap;
	}

	public void onMessage(Message message) {

		try {
			// 接受到文字消息
			if (message instanceof TextMessage) {
				/********** 接收参数 ***********/
				final Destination replyToDest = message.getJMSReplyTo();// 回复queue
				if (replyToDest == null) {
					throw new Exception("replyToDest is null");
				}
				final String senderid = message.getStringProperty("senderid");
				final String seq = message.getStringProperty("reqTxnSeq");// 回复流水
				if (seq == null || "".equals(seq)) {
					throw new Exception("mqSeq is null");
				}
				String param = ((TextMessage) message).getText();// 串参数
				if (param == null || "".equals(param)) {
					throw new Exception("TextMessage is null");
				}
				// 解析报文，取得外部业务功能码,交易码
				String result = null;
				BankMsgVo msgVo = new BankMsgVo();
				try {
					MsgHandle.unmarshaller(msgVo, param);
					log.info("接收到银行报文,交易流水ReqTransID:{},内部交易流水:{},银行机构号:{}",
							new Object[] { msgVo.getReqTransID(),
									msgVo.getTxnSeq(),msgVo.getReqSys()});
					logger.info("接收到银行报文,交易流水ReqTransID:{},内部交易流水:{},银行机构号:{}", new Object[] {
							msgVo.getReqTransID(), msgVo.getTxnSeq(),msgVo.getReqSys()});
				} catch (Exception e) {
					logger.error("接收到银行发起交易流水ReqTransID:{},内部交易流水:{},报文为:{},报文头解析错误:{}",
							new Object[] {msgVo.getReqTransID(), msgVo.getTxnSeq(), param, e.getMessage()});
					log.error("报文头解析失败,接收到银行发起交易流水ReqTransID:{},内部交易流水:{}，银行机构号:{}",
							new Object[] {msgVo.getReqTransID(),msgVo.getTxnSeq(),msgVo.getReqSys()});
					msgVo.setRspCode(RspCodeConstant.Bank.BANK_015A05
							.getValue());
					msgVo.setRspDesc(RspCodeConstant.Bank.BANK_015A05.getDesc()+"报文头解析错误");
					msgVo.setBody(CommonConstant.SpeSymbol.BLANK.toString());
					result = MsgHandle.marshaller(convertRtnMsgVo(msgVo));
				}
				// 基本信息的校验(头信息,签名信息)

				String validateRtn = this.validateModel(msgVo);
				if (validateRtn == null || "".equals(validateRtn)) {
					logger.debug("银行发起方流水:{},内部流水:{},银行机构号:{},报文头校验成功",
							new Object[] {msgVo.getReqTransID(), msgVo.getTxnSeq(),msgVo.getReqSys()});
				} else {
					logger.warn("银行发起方流水:{},内部流水:{},银行机构号:{},报文头校验成功:{}",
							new Object[] {msgVo.getReqTransID(), msgVo.getTxnSeq(),msgVo.getReqSys(),validateRtn});
					log.warn("银行发起方流水:{},内部流水:{},银行机构号:{},报文头校验成功:{}",
							new Object[] {msgVo.getReqTransID(), msgVo.getTxnSeq(),msgVo.getReqSys(),validateRtn});
					msgVo.setRspCode(validateRtn.split(":")[0]);
					msgVo.setRspDesc(RspCodeConstant.Bank.getDescByValue(validateRtn.split(":")[0]) + validateRtn);
					msgVo.setBody(CommonConstant.SpeSymbol.BLANK.toString());
					result = MsgHandle.marshaller(convertRtnMsgVo(msgVo)); // 返回错误应答报文
				}

				String reqActivityCode = msgVo.getActivityCode(); // 交易码
				String transCode = "";
				UpayCsysTransCode code = new UpayCsysTransCode();

				try {
					// if(result == null){
					// String orgId = msgVo.getReqSys();
					// Map<String,String> orgParam = new
					// HashMap<String,String>();
					// orgParam.put("orgId", orgId);
					// orgParam.put("status",
					// CommonConstant.IsActive.True.getValue());
					// orgParam.put("isHistory",
					// CommonConstant.IsHistory.Normal.getValue());
					// boolean flag = this.orgStatusCheck(orgParam);
					// if(!flag){
					// logger.warn("内部交易流水:{},bank发起方机构状态异常",new
					// Object[]{msgVo.getTxnSeq()});
					// log.warn("内部交易流水:{},bank发起方机构状态异常",new
					// Object[]{msgVo.getTxnSeq()});
					// msgVo.setRspCode(RspCodeConstant.Bank.BANK_013A18.getValue());
					// msgVo.setRspDesc(RspCodeConstant.Bank.BANK_013A18.getDesc());
					// msgVo.setBody(CommonConstant.SpeSymbol.BLANK
					// .toString());
					// result = MsgHandle
					// .marshaller(convertRtnMsgVo(msgVo)); // 返回错误应答报文
					// }
					// }

					if (result == null) {
						// 得到内部交易码
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put("reqActivityCode", reqActivityCode);
						code = transCodeConvert(paramMap);
						msgVo.setTransCode(code);
						if (code == null
								|| "".equals(StringUtil.toTrim(code
										.getTransCode()))) {
							logger.warn("发起方机构没有此服务的权限,银行发起方流水:{},内部流水:{},银行机构号:{},交易码:{}",
									new Object[] { msgVo.getReqTransID(),msgVo.getTxnSeq(),msgVo.getReqSys(), reqActivityCode });
							log.warn("发起方机构没有此服务的权限,银行发起方流水:{},内部流水:{},银行机构号:{},交易码:{}",
									new Object[] { msgVo.getReqTransID(),msgVo.getTxnSeq(),msgVo.getReqSys(), reqActivityCode });
							msgVo.setRspCode(RspCodeConstant.Bank.BANK_013A18
									.getValue());
							msgVo.setRspDesc(RspCodeConstant.Bank.BANK_013A18
									.getDesc()+"没有此服务的权限");
							msgVo.setBody(CommonConstant.SpeSymbol.BLANK
									.toString());
							result = MsgHandle
									.marshaller(convertRtnMsgVo(msgVo)); // 返回错误应答报文
						}
						transCode = code.getTransCode();
						logger.info("银行发起方流水:{},内部流水:{},银行机构号:{},交易码:{},内部交易码:{}",
								new Object[] { msgVo.getReqTransID(),msgVo.getTxnSeq(),msgVo.getReqSys(), reqActivityCode,transCode });
						log.debug("银行发起方流水:{},内部流水:{},银行机构号:{},交易码:{},内部交易码:{}",
								new Object[] { msgVo.getReqTransID(),msgVo.getTxnSeq(),msgVo.getReqSys(), reqActivityCode,transCode });
					}

					// if (result == null) {
					//
					// Map<String, String> paramOrgTrans = new HashMap<String,
					// String>();
					// paramOrgTrans.put("transCode", transCode);
					// paramOrgTrans.put("orgId", msgVo.getReqSys());
					// boolean orgTransFlag = orgTransCodeCheck(paramOrgTrans);
					// if (orgTransFlag == false) {
					// logger.warn("内部交易流水:{},银行发起方机构:{}服务关闭",new Object
					// []{msgVo.getTxnSeq(),msgVo.getReqSys()});
					// log.warn("内部交易流水:{},银行发起方机构:{}服务关闭",new Object
					// []{msgVo.getTxnSeq(),msgVo.getReqSys()});
					// msgVo.setRspCode(RspCodeConstant.Bank.BANK_013A18.getValue());
					// msgVo.setRspDesc(RspCodeConstant.Bank.BANK_013A18.getDesc());
					// msgVo.setBody(CommonConstant.SpeSymbol.BLANK.toString());
					// result = MsgHandle
					// .marshaller(convertRtnMsgVo(msgVo)); // 返回错误应答报文
					// }
					// }

					// UpayCsysTxnLog upayCsysTxnLog = null;
					// if(result == null){
					//
					// if (CommonConstant.TransCode.PreSign.toString().equals(
					// transCode)
					// || CommonConstant.TransCode.SignResltInf
					// .toString().equals(transCode)) {
					// logger.info("内部交易流水号:{},内部交易码  :{}, 不检查重复交易。可以重发 ",new
					// Object[]{msgVo.getTxnSeq(),transCode});
					// }else {
					// // 重复订单判断
					// Map<String, Object> params = new HashMap<String,
					// Object>();
					// //发起方渠道+操作流水号+时间唯一标识一笔流水。
					// params.put("reqDomain", msgVo.getReqSys());
					// params.put("reqTransId", msgVo.getReqTransID());
					// //发起方渠道+操作流水号+时间唯一标识一笔流水。
					// upayCsysTxnLog = upayCsysTxnLogService
					// .findObj(params);
					// if (upayCsysTxnLog != null) {
					// logger.warn("银行端:{},重复订单,内部交易流水:{},银行发起流水:{}",new
					// Object[]{msgVo.getReqSys(),msgVo.getTxnSeq(),msgVo.getReqTransID()});
					// log.warn("银行端:{},重复订单,内部交易流水:{},银行发起流水:{}",new
					// Object[]{msgVo.getReqSys(),msgVo.getTxnSeq(),msgVo.getReqTransID()});
					// msgVo.setRspCode(RspCodeConstant.Bank.BANK_013A17.getValue());
					// msgVo.setRspDesc(RspCodeConstant.Bank.BANK_013A17.getDesc());
					// msgVo.setBody(CommonConstant.SpeSymbol.BLANK.toString());
					// result = MsgHandle.marshaller(convertRtnMsgVo(msgVo)); //
					// 返回错误应答报文
					// }
					// }
					//
					// }
					// 交易处理
					if (result == null || "".equals(result)) {
						msgVo.setTransCode(code);
						IBaseAction<BankMsgVo, BankMsgVo> action = transactionMap
								.get(transCode);
						msgVo = action.execute(msgVo);
						if (transCode
								.equals(CommonConstant.TransCode.PrintInvocieQuery
										.toString())) {
							BankHeader header = new BankHeader();
							BeanUtils.copyProperties(msgVo, header);
							// BankSign sign=new BankSign();
							// BeanUtils.copyProperties(msgVo, sign);
							BankPrintInvoiceQueryRspVo body = (BankPrintInvoiceQueryRspVo) msgVo
									.getBody();
							BeanUtils.copyProperties(msgVo, body);
							BankGpay gPay = new BankGpay();

							gPay.setHeader(header);
							// gPay.setSign(sign);
							gPay.setBody(body);
							result = XmlUtil.toXml(gPay, BankGpay.class, false);
						} else {
							result = MsgHandle.marshaller(msgVo);
						}
					}

				} catch (Exception e) {
					logger.error("未知异常,内部流水:"+msgVo.getTxnSeq(), e);
					logger.error("银行发起方流水:{},内部流水:{},银行机构号:{},未知异常:{}",new Object[] {msgVo.getReqTransID(), msgVo.getTxnSeq(), msgVo.getReqSys(),e.getMessage()});
					log.error("未知异常,银行发起方流水:{},内部流水:{},银行机构号:{}",new Object[] {msgVo.getReqTransID(), msgVo.getTxnSeq(), msgVo.getReqSys()});
					msgVo.setRspCode(RspCodeConstant.Bank.BANK_015A06
							.getValue());
					msgVo.setRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+"未知异常");
					msgVo.setBody(CommonConstant.SpeSymbol.BLANK.toString());
					result = MsgHandle.marshaller(convertRtnMsgVo(msgVo));
				}
				/************ JMS交易处理结果消息回复 **************/
				final String rtnJson = result;// 返回结果
				if (replyToDest != null) {
					template.send(replyToDest, new MessageCreator() {
						public Message createMessage(Session session)
								throws JMSException {
							Message msg = session.createTextMessage(rtnJson);
							msg.setStringProperty("senderid", senderid);
							msg.setStringProperty("reqTxnSeq", seq);

							return msg;
						}
					});
					if (RspCodeConstant.Bank.BANK_010A00.getValue().equals(
							msgVo.getRspCode())) {
						log.succ(
								"银行交易成功,发起流水ReqTransID:{},应答流水RcvTransID:{},内部交易流水:{},应答码:{},省机构号:{},内部交易码:{}",
								new Object[] { msgVo.getReqTransID(), msgVo.getRcvTransID(),
										msgVo.getTxnSeq(), msgVo.getRspCode(),msgVo.getReqSys(),transCode });
					} else {
						log.error(
								"银行交易失败，发起流水ReqTransID:{},应答流水RcvTransID:{},内部交易流水:{},应答码:{},省机构号:{},内部交易码:{}",
								new Object[] { msgVo.getReqTransID(),msgVo.getRcvTransID(),
										msgVo.getTxnSeq(), msgVo.getRspCode(),msgVo.getReqSys(),transCode});
					}
					logger.info(
							"银行交易完成,发起流水ReqTransID:{},应答流水RcvTransID:{},内部交易流水:{},应答码:{},省机构号:{},内部交易码:{}",
							new Object[] { msgVo.getReqTransID(), msgVo.getRcvTransID(),
									msgVo.getTxnSeq(), msgVo.getRspCode(),msgVo.getReqSys(),transCode });
				}

			}
		} catch (Exception e) {
			logger.error("系统错误", e);
			log.error("系统错误");
		}

	}

	@Override
	protected BankMsgVo convertRtnMsgVo(BaseMsgVo baseVo) {

		BankMsgVo vo = (BankMsgVo) baseVo;
		vo.setActionCode(CommonConstant.ActionCode.Respone.toString());
		vo.setRcvDate(DateUtil.getDateyyyyMMdd());
		vo.setRcvTransID(Serial.genSerialNo(CommonConstant.Sequence.IntSeq
				.toString()));
		vo.setRcvDateTime(com.huateng.toolbox.utils.DateUtil
				.getDateyyyyMMddHHmmssSSS());
		return vo;
	}

}
