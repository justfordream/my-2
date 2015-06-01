package com.huateng.cmupay.listener;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.MessageCreator;

import com.huateng.cmupay.action.IBaseAction;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.logFormat.TmallMessageLogger;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.bank.BankMsgVo;
import com.huateng.cmupay.parseMsg.reflect.vo.tmall.TmallConsumeResVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StringUtil;

/**
 * 天猫全网充值jms监听器（异步）
 * @author panlg
 *
 */
public class TmallMessageListener extends AbsMessageListener{

	private TmallMessageLogger tmallOperLogger = TmallMessageLogger.getLogger(this.getClass());
	
//天猫日志独立时使用	，把logger替换成tmallLogger即可
//	private final Logger tmallLogger = LoggerFactory.getLogger("TMALL_FILE");
	
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
				BankMsgVo msgVo = new BankMsgVo();
				try {
					MsgHandle.unmarshaller(msgVo, param);
					tmallOperLogger.info("接收到天猫报文,交易流水ReqTransID:{},内部交易流水:{},天猫机构号:{}",
							new Object[] { msgVo.getReqTransID(),
									msgVo.getTxnSeq(),msgVo.getReqSys()});
					logger.info("接收到天猫报文,交易流水ReqTransID:{},内部交易流水:{},天猫机构号:{}", new Object[] {
							msgVo.getReqTransID(), msgVo.getTxnSeq(),msgVo.getReqSys()});
				}catch (Exception e) {
					logger.error("接收到天猫发起交易流水ReqTransID:{},内部交易流水:{},报文为:{},报文头解析错误:{}",
							new Object[] {msgVo.getReqTransID(), msgVo.getTxnSeq(), param, e.getMessage()});
					tmallOperLogger.error("报文头解析失败,接收到天猫发起交易流水ReqTransID:{},内部交易流水:{}，天猫机构号:{}",
							new Object[] {msgVo.getReqTransID(),msgVo.getTxnSeq(),msgVo.getReqSys()});
					msgVo.setRspCode(RspCodeConstant.Bank.BANK_015A05.getValue());
					msgVo.setRspDesc(RspCodeConstant.Bank.BANK_015A05.getDesc()+"报文头解析失败");
					msgVo.setBody(CommonConstant.SpeSymbol.BLANK.toString());
					result = MsgHandle.marshaller(convertRtnMsgVo(msgVo)); 
				}
				// 基本信息的校验(头信息,签名信息)

				String validateRtn = this.validateModel(msgVo);
				if (validateRtn == null || "".equals(validateRtn)) {
					logger.debug("天猫发起方流水:{},内部流水:{},天猫机构号:{},报文头校验成功",
							new Object[] {msgVo.getReqTransID(), msgVo.getTxnSeq(),msgVo.getReqSys()});
				} else {
					logger.warn("天猫发起方流水:{},内部流水:{},天猫机构号:{},报文头校验成功:{}",
							new Object[] {msgVo.getReqTransID(), msgVo.getTxnSeq(),msgVo.getReqSys(),validateRtn});
					tmallOperLogger.warn("天猫发起方流水:{},内部流水:{},天猫机构号:{},报文头校验成功:{}",
							new Object[] {msgVo.getReqTransID(), msgVo.getTxnSeq(),msgVo.getReqSys(),validateRtn});	
					msgVo.setRspCode(validateRtn.split(":")[0]);
					msgVo.setRspDesc(RspCodeConstant.Bank.getDescByValue(validateRtn
							.split(":")[0]) + validateRtn);
					msgVo.setBody(CommonConstant.SpeSymbol.BLANK.toString());
					result = MsgHandle.marshaller(convertRtnMsgVo(msgVo)); // 返回错误应答报文
				}
				
				String reqActivityCode = msgVo.getActivityCode(); // 交易码
				String transCode = "";
				UpayCsysTransCode code = new UpayCsysTransCode();

				try {

					if (result == null) {
						// 得到内部交易码
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put("reqActivityCode", reqActivityCode);
						code = transCodeConvert(paramMap);
						msgVo.setTransCode(code);
						if (code == null
								|| "".equals(StringUtil.toTrim(code
										.getTransCode()))) {
							logger.warn("发起方机构没有此服务的权限,天猫发起方流水:{},内部流水:{},天猫机构号:{},交易码:{}",
									new Object[] { msgVo.getReqTransID(),msgVo.getTxnSeq(),msgVo.getReqSys(), reqActivityCode });
							tmallOperLogger.warn("发起方机构没有此服务的权限,天猫发起方流水:{},内部流水:{},天猫机构号:{},交易码:{}",
									new Object[] { msgVo.getReqTransID(),msgVo.getTxnSeq(),msgVo.getReqSys(), reqActivityCode });
							msgVo.setRspCode(RspCodeConstant.Bank.BANK_013A18.getValue());
							msgVo.setRspDesc(RspCodeConstant.Bank.BANK_013A18.getDesc()+"没有此服务的权限");
							msgVo.setBody(CommonConstant.SpeSymbol.BLANK
									.toString());
							result = MsgHandle
									.marshaller(convertRtnMsgVo(msgVo)); // 返回错误应答报文
						}
						transCode = code.getTransCode();
						logger.warn("天猫发起方流水:{},内部流水:{},天猫机构号:{},交易码:{},内部交易码:{}",
								new Object[] { msgVo.getReqTransID(),msgVo.getTxnSeq(),msgVo.getReqSys(), reqActivityCode,transCode });
						tmallOperLogger.debug("天猫发起方流水:{},内部流水:{},天猫机构号:{},交易码:{},内部交易码:{}",
								new Object[] { msgVo.getReqTransID(),msgVo.getTxnSeq(),msgVo.getReqSys(), reqActivityCode,transCode });
					}
					
                   
					//交易处理 
					if (result == null || "".equals(result)) {
						msgVo.setTransCode(code);
						IBaseAction<BankMsgVo, BankMsgVo> action = transactionMap.get(transCode);
						msgVo = action.execute(msgVo);
						result = MsgHandle.marshaller(msgVo);
					}


				} catch (Exception e) {
					logger.error("未知异常", e);
					logger.error("天猫发起方流水:{},内部流水:{},天猫机构号:{},未知异常:{}",new Object[] {msgVo.getReqTransID(), msgVo.getTxnSeq(), msgVo.getReqSys(),e.getMessage()});
					tmallOperLogger.error("未知异常,天猫发起方流水:{},内部流水:{},天猫机构号:{}",new Object[] {msgVo.getReqTransID(), msgVo.getTxnSeq(), msgVo.getReqSys()});
					msgVo.setRspCode(RspCodeConstant.Bank.BANK_015A06.getValue());
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
					
					String resultCode = ((TmallConsumeResVo)msgVo.getBody()).getResultCode();
					if(RspCodeConstant.Bank.BANK_010A00.getValue().equals(resultCode)){
						tmallOperLogger.succ("天猫交易成功,发起流水ReqTransID:{},内部交易流水:{},应答码:{},天猫机构号:{},内部交易码:{}",
								new Object[] { msgVo.getReqTransID(),msgVo.getTxnSeq(), resultCode,msgVo.getReqSys(),transCode });
					} else {
						tmallOperLogger.error("天猫交易失败，发起流水ReqTransID:{},内部交易流水:{},应答码:{},天猫机构号:{},内部交易码:{}",
								new Object[] { msgVo.getReqTransID(),msgVo.getTxnSeq(), resultCode,msgVo.getReqSys(),transCode});
					}
					
					logger.info("天猫交易完成,发起流水ReqTransID:{},内部交易流水:{},应答码:{},天猫机构号:{},内部交易码:{}",
							new Object[] { msgVo.getReqTransID(),msgVo.getTxnSeq(), resultCode,msgVo.getReqSys(),transCode });
				}

			}
		} catch (Exception e) {
			logger.error("系统错误", e);
			tmallOperLogger.error("系统错误");
		}

	}

	@Override
	protected BankMsgVo convertRtnMsgVo(BaseMsgVo baseVo) {

		BankMsgVo vo = (BankMsgVo) baseVo;
		vo.setActionCode(CommonConstant.ActionCode.Respone.toString());
		vo.setRcvDate(DateUtil.getDateyyyyMMdd());
		vo.setRcvTransID(Serial.genSerialNo(CommonConstant.Sequence.IntSeq
				.toString()));
		vo.setRcvDateTime(DateUtil.getDateyyyyMMddHHmmssSSS());
		return vo;
	}


}
