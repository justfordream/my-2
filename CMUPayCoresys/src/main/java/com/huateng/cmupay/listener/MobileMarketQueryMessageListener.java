package com.huateng.cmupay.listener;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.MessageCreator;

import com.huateng.cmupay.action.IBaseAction;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopMsgResVo;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.MobileShopMsgVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StringUtil;


public class MobileMarketQueryMessageListener extends AbsMessageListener{

	private Map<String, IBaseAction<MobileShopMsgVo, MobileShopMsgVo>> transactionMap;
	
	public void setTransactionMap(
			Map<String, IBaseAction<MobileShopMsgVo, MobileShopMsgVo>> transactionMap) {
		this.transactionMap = transactionMap;
	}
//MobileShopMsgVo 头
//MobileShopMsgReqVo 体
	public void onMessage(Message message) {
		try {
			
			// 接受到文字消息
			if (message instanceof TextMessage) {
				/********** 接收参数 ***********/
				final Destination replyToDest = message.getJMSReplyTo();// 回复queue
				if (replyToDest == null) {
					throw new Exception("replyToDest is null");
				}
				final String senderid=message.getStringProperty("senderid");
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
				MobileShopMsgVo mobileShopMsgVo = new MobileShopMsgVo();
				try {
					MsgHandle.unmarshaller(mobileShopMsgVo, param);
					log.info("接收到商城报文,交易流水ReqTransID:{},内部交易流水:{},商城机构号:{}",
							new Object[] { mobileShopMsgVo.getReqTransID(),
									mobileShopMsgVo.getTxnSeq(),mobileShopMsgVo.getReqSys()});
					logger.info("接收到商城报文,交易流水ReqTransID:{},内部交易流水:{},商城机构号:{}", new Object[] {
							mobileShopMsgVo.getReqTransID(), mobileShopMsgVo.getTxnSeq(),mobileShopMsgVo.getReqSys()});
				}catch (Exception e) {
					logger.error("接收到商城发起交易流水ReqTransID:{},内部交易流水:{},报文为:{},报文头解析错误:{}",
							new Object[] {mobileShopMsgVo.getReqTransID(), mobileShopMsgVo.getTxnSeq(), param, e.getMessage()});
					log.error("报文头解析失败,接收到商城发起交易流水ReqTransID:{},内部交易流水:{}，商城机构号:{}",
							new Object[] {mobileShopMsgVo.getReqTransID(),mobileShopMsgVo.getTxnSeq(),mobileShopMsgVo.getReqSys()});
					mobileShopMsgVo.setRspCode(RspCodeConstant.Bank.BANK_015A05.getValue());
					mobileShopMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_015A05.getDesc()+"报文头解析失败");
					mobileShopMsgVo.setBody(CommonConstant.SpeSymbol.BLANK.toString());
					result = MsgHandle.marshaller(convertRtnMsgVo(mobileShopMsgVo)); 
				}
				// 基本信息的校验(头信息,签名信息)

				String validateRtn = this.validateModel(mobileShopMsgVo);
				if (validateRtn == null || "".equals(validateRtn)) {
					logger.debug("商城发起方流水:{},内部流水:{},商城机构号:{},报文头校验成功",
							new Object[] {mobileShopMsgVo.getReqTransID(), mobileShopMsgVo.getTxnSeq(),mobileShopMsgVo.getReqSys()});
				} else {
					logger.warn("商城发起方流水:{},内部流水:{},商城机构号:{},报文头校验成功:{}",
							new Object[] {mobileShopMsgVo.getReqTransID(), mobileShopMsgVo.getTxnSeq(),mobileShopMsgVo.getReqSys(),validateRtn});
					log.warn("商城发起方流水:{},内部流水:{},商城机构号:{},报文头校验成功:{}",
							new Object[] {mobileShopMsgVo.getReqTransID(), mobileShopMsgVo.getTxnSeq(),mobileShopMsgVo.getReqSys(),validateRtn});	
					mobileShopMsgVo.setRspCode(validateRtn.split(":")[0]);
					mobileShopMsgVo.setRspDesc(RspCodeConstant.Bank.getDescByValue(validateRtn.split(":")[0]) + validateRtn);
					mobileShopMsgVo.setBody(CommonConstant.SpeSymbol.BLANK.toString());
					result = MsgHandle.marshaller(convertRtnMsgVo(mobileShopMsgVo)); // 返回错误应答报文
				}
				
				String reqActivityCode = mobileShopMsgVo.getActivityCode(); // 交易码
				String transCode = "";
				UpayCsysTransCode code = new UpayCsysTransCode();

				try {

					if (result == null) {
						// 得到内部交易码
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put("reqActivityCode", reqActivityCode);
						code = transCodeConvert(paramMap);
						mobileShopMsgVo.setTransCode(code);
						if (code == null
								|| "".equals(StringUtil.toTrim(code
										.getTransCode()))) {
							logger.warn("发起方机构没有此服务的权限,商城发起方流水:{},内部流水:{},商城机构号:{},交易码:{}",
									new Object[] { mobileShopMsgVo.getReqTransID(),mobileShopMsgVo.getTxnSeq(),mobileShopMsgVo.getReqSys(), reqActivityCode });
							log.warn("发起方机构没有此服务的权限,商城发起方流水:{},内部流水:{},商城机构号:{},交易码:{}",
									new Object[] { mobileShopMsgVo.getReqTransID(),mobileShopMsgVo.getTxnSeq(),mobileShopMsgVo.getReqSys(), reqActivityCode });
							mobileShopMsgVo.setRspCode(RspCodeConstant.Bank.BANK_013A18.getValue());
							mobileShopMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_013A18.getDesc()+"没有此服务的权限");
							mobileShopMsgVo.setBody(CommonConstant.SpeSymbol.BLANK
									.toString());
							result = MsgHandle
									.marshaller(convertRtnMsgVo(mobileShopMsgVo)); // 返回错误应答报文
						}
						transCode = code.getTransCode();
						logger.warn("商城发起方流水:{},内部流水:{},商城机构号:{},交易码:{},内部交易码:{}",
								new Object[] { mobileShopMsgVo.getReqTransID(),mobileShopMsgVo.getTxnSeq(),mobileShopMsgVo.getReqSys(), reqActivityCode,transCode });
						log.debug("商城发起方流水:{},内部流水:{},商城机构号:{},交易码:{},内部交易码:{}",
								new Object[] { mobileShopMsgVo.getReqTransID(),mobileShopMsgVo.getTxnSeq(),mobileShopMsgVo.getReqSys(), reqActivityCode,transCode });
					}
//					
                   
					//交易处理 
					if (result == null || "".equals(result)) {
						mobileShopMsgVo.setTransCode(code);
						IBaseAction<MobileShopMsgVo, MobileShopMsgVo> action = transactionMap.get(transCode);
						mobileShopMsgVo = action.execute(mobileShopMsgVo);
						result = MsgHandle.marshaller(mobileShopMsgVo);
					}
				} catch (Exception e) {
					logger.error("未知异常", e);
					logger.error("商城发起方流水:{},内部流水:{},商城机构号:{},未知异常:{}",new Object[] {mobileShopMsgVo.getReqTransID(), mobileShopMsgVo.getTxnSeq(), mobileShopMsgVo.getReqSys(),e.getMessage()});
					log.error("未知异常,商城发起方流水:{},内部流水:{},商城机构号:{}",new Object[] {mobileShopMsgVo.getReqTransID(), mobileShopMsgVo.getTxnSeq(), mobileShopMsgVo.getReqSys()});
					mobileShopMsgVo.setRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
					mobileShopMsgVo.setRspDesc(RspCodeConstant.Bank.BANK_015A06.getDesc()+"未知异常");
					mobileShopMsgVo.setBody(CommonConstant.SpeSymbol.BLANK.toString());
					result = MsgHandle.marshaller(convertRtnMsgVo(mobileShopMsgVo));
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
					if(RspCodeConstant.Bank.BANK_010A00.getValue().equals(mobileShopMsgVo.getRspCode())){
						log.succ(
								"商城交易成功,发起流水ReqTransID:{},应答流水RcvTransID:{},内部交易流水:{},应答码:{},省机构号:{},内部交易码:{}",
								new Object[] { mobileShopMsgVo.getReqTransID(), mobileShopMsgVo.getRcvTransID(),
										mobileShopMsgVo.getTxnSeq(), mobileShopMsgVo.getRspCode(),mobileShopMsgVo.getReqSys(),transCode });
					} else {
						log.error(
								"商城交易失败，发起流水ReqTransID:{},应答流水RcvTransID:{},内部交易流水:{},应答码:{},省机构号:{},内部交易码:{}",
								new Object[] { mobileShopMsgVo.getReqTransID(),mobileShopMsgVo.getRcvTransID(),
										mobileShopMsgVo.getTxnSeq(), mobileShopMsgVo.getRspCode(),mobileShopMsgVo.getReqSys(),transCode});
					}
					logger.info(
							"商城交易完成,发起流水ReqTransID:{},应答流水RcvTransID:{},内部交易流水:{},应答码:{},省机构号:{},内部交易码:{}",
							new Object[] { mobileShopMsgVo.getReqTransID(), mobileShopMsgVo.getRcvTransID(),
									mobileShopMsgVo.getTxnSeq(), mobileShopMsgVo.getRspCode(),mobileShopMsgVo.getReqSys(),transCode });
				}

			}
		} catch (Exception e) {
			logger.error("系统错误", e);
			log.error("系统错误");
		}

	}

	@Override
	protected MobileShopMsgVo convertRtnMsgVo(BaseMsgVo baseVo) {

		MobileShopMsgVo  vo = (MobileShopMsgVo) baseVo;
		
//		vo.setResultCode(resultCode)
//		vo.setActionCode(CommonConstant.ActionCode.Respone.toString());
//		vo.setRcvDate(DateUtil.getDateyyyyMMdd());
//		vo.setRcvTransID(Serial.genSerialNo(CommonConstant.Sequence.IntSeq
//				.toString()));
//		vo.setRcvDateTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		return vo;
	}


}
