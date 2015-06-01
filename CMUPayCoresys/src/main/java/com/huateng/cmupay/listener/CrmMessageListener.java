package com.huateng.cmupay.listener;

import java.util.HashMap;
import java.util.Map;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.springframework.jms.core.MessageCreator;
import com.huateng.cmupay.action.IBaseAction;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.RspCodeConstant;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.parseMsg.reflect.handle.MsgHandle;
import com.huateng.cmupay.parseMsg.reflect.vo.crm.CrmMsgVo;
import com.huateng.cmupay.utils.Serial;
import com.huateng.toolbox.utils.DateUtil;
import com.huateng.toolbox.utils.StringUtil;

/**
 * 
 * @author cmt
 * 
 */
public class CrmMessageListener extends AbsMessageListener {

	private Map<String, IBaseAction<CrmMsgVo, CrmMsgVo>> transactionMap;

	// private @Value("${testFlag}") Integer testFlag;
	public void setTransactionMap(
			Map<String, IBaseAction<CrmMsgVo, CrmMsgVo>> transactionMap) {
		this.transactionMap = transactionMap;
	}

	public void onMessage(Message message) {
		try {
			// 接受到文字消息
			if (message instanceof TextMessage) {

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
				String result = null; // TODO 报文格式错误需要返回
				CrmMsgVo msgVo = null;
				try {
					msgVo = new CrmMsgVo();
					MsgHandle.unmarshaller(msgVo, param);
					log.info(
							"接收到省发起方流水TransIDO:{},内部流水:{},省机构号:{}",
							new Object[] { msgVo.getTransIDO(),
									msgVo.getTxnSeq(),msgVo.getMsgSender()});
					logger.info(
							"接收到省发起方流水TransIDO:{},内部流水:{},省机构号:{}",
							new Object[] { msgVo.getTransIDO(),
									msgVo.getTxnSeq(),msgVo.getMsgSender()});
				} catch (Exception e) {
					logger.error("接收到省发起方流水TransIDO:{},内部流水:{},报文为:{},报文头解析失败:{}",new Object[] {msgVo.getTransIDO(), msgVo.getTxnSeq(),param,e.getMessage() });
					log.error("报文头解析失败，接收到省发起方流水TransIDO:{},内部流水:{},省机构号:{}",msgVo.getTransIDO(),msgVo.getTxnSeq(),msgVo.getMsgSender());
					
//					msgVo.setRouteType(CommonConstant.RouteType.RoutePhone
//							.toString());
					//报文头解析失败返回给省应答信息不变,RouteType为00 20131212 modify by weiyi 
					msgVo.setRouteType(CommonConstant.RouteType.RouteProvince
							.toString());
					msgVo.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					msgVo.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgVo.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc()+"报文头解析失败");
					msgVo.setBody(CommonConstant.SpeSymbol.BLANK.toString());
					result = MsgHandle.marshaller(convertRtnMsgVo(msgVo));
				}
				// if(!testFlag.equals(msgVo.getTestFlag())){
				// logger.info("testFlag错误。" );
				// msgVo.setRouteType(CommonConstant.routeType.RoutePhone
				// .toString());
				// msgVo.setRspType(CommonConstant.CrmRspType.BusErr
				// .toString());
				// msgVo.setRspCode(MessageHandler.getWzwErrCode("0105"));
				// msgVo.setRspDesc(MessageHandler.getWzwErrMsg("0105")
				// + "testFlag不是这个平台可以的标识。");
				// msgVo.setBody(CommonConstant.SpeSymbol.BLANK.toString());
				// result = MsgHandle.marshaller(convertRtnMsgVo(msgVo)); //
				// 返回错误应答报文
				// }
				// 基本信息的校验(头信息,签名信息)
				String validateRtn = this.validateModel(msgVo);
				if (validateRtn == null || "".equals(validateRtn)) {
					logger.debug("省发起方流水:{},内部流水:{},省机构号:{},报文头校验成功",
							new Object[] { msgVo.getTransIDO(), msgVo.getTxnSeq(),msgVo.getMsgSender() });
				} else {
					logger.warn("省发起方流水:{},内部流水:{},省机构号:{},报文头校验失败:{}",
							new Object[] { msgVo.getTransIDO(), msgVo.getTxnSeq(),msgVo.getMsgSender(), validateRtn });
					log.warn("省发起方流水:{},内部流水:{},省机构号:{},报文头校验失败",
							new Object[] {msgVo.getTransIDO(), msgVo.getTxnSeq(),msgVo.getMsgSender() });
//					msgVo.setRouteType(CommonConstant.RouteType.RoutePhone
//							.toString());
					//报文头解析失败返回给省应答信息不变,RouteType为00 20131212 modify by weiyi 
					msgVo.setRouteType(CommonConstant.RouteType.RouteProvince
							.toString());
					msgVo.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
					msgVo.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgVo.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc()+ validateRtn);
					msgVo.setBody(CommonConstant.SpeSymbol.BLANK.toString());
					result = MsgHandle.marshaller(convertRtnMsgVo(msgVo)); // 返回错误应答报文
				}
				String msgSender = msgVo.getMsgSender();
				String reqBipCode = msgVo.getBIPCode(); // 业务功能码
				String reqActivityCode = msgVo.getActivityCode(); // 交易码

				// 内部交易代码
				String transCode = "";
				UpayCsysTransCode code = new UpayCsysTransCode();

				// 机构状态校验
				try {
					// if (result == null) {
					// String orgId = msgVo.getMsgSender();
					// Map<String, String> orgParam = new HashMap<String,
					// String>();
					// orgParam.put("orgId", orgId);
					// orgParam.put("status",
					// CommonConstant.IsActive.True.getValue());
					// orgParam.put("isHistory",
					// CommonConstant.IsHistory.Normal.getValue());
					// boolean flag = this.orgStatusCheck(orgParam);
					// if (!flag) {
					// logger.warn("内部交易流水:{},CRM发起方机构:{},状态异常",new
					// Object[]{msgVo.getTxnSeq(),orgId});
					// log.warn("内部交易流水:{},CRM发起方机构:{},状态异常",new
					// Object[]{msgVo.getTxnSeq(),orgId});
					// msgVo.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					// msgVo.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc());
					// msgVo.setBody(CommonConstant.SpeSymbol.BLANK
					// .toString());
					// result = MsgHandle
					// .marshaller(convertRtnMsgVo(msgVo)); // 返回错误应答报文
					// }
					// }
					if (result == null) {
						// 得到内部交易码
						Map<String, String> transMap = new HashMap<String, String>();
						transMap.put("reqBipCode", reqBipCode);
						transMap.put("reqActivityCode", reqActivityCode);
						code = transCodeConvert(transMap);
						msgVo.setTransCode(code);
						if (code == null
								|| "".equals(StringUtil.toTrim(code
										.getTransCode()))) {
							
							logger.warn("发起方机构没有此服务的权限,省发起方流水:{},内部流水:{},省机构号:{},业务码:{},交易码:{}",
									new Object[] { msgVo.getTransIDO(),msgVo.getTxnSeq(),msgVo.getMsgSender(),
											reqBipCode, reqActivityCode });
							log.warn("发起方机构没有此服务的权限,省发起方流水:{},内部流水:{},省机构号:{},业务码:{},交易码:{}",
									new Object[] { msgVo.getTransIDO(),msgVo.getTxnSeq(),msgVo.getMsgSender(),
											reqBipCode, reqActivityCode });
//							msgVo.setRouteType(CommonConstant.RouteType.RoutePhone
//									.toString());
							//报文头解析失败返回给省应答信息不变,RouteType为00 20131212 modify by weiyi 
							msgVo.setRouteType(CommonConstant.RouteType.RouteProvince
									.toString());
							msgVo.setRspType(CommonConstant.CrmRspType.BusErr.getValue());
							msgVo.setRspCode(RspCodeConstant.Wzw.WZW_2998
									.getValue());
							msgVo.setRspDesc(RspCodeConstant.Wzw.WZW_2998
									.getDesc() + "没有此服务的权限");
							msgVo.setBody(CommonConstant.SpeSymbol.BLANK
									.toString());
							result = MsgHandle
									.marshaller(convertRtnMsgVo(msgVo)); // 返回错误应答报文
						}
						else{ //若查找到服务,则取操作码
							transCode = code.getTransCode();
							logger.info("省发起方流水:{},内部流水:{},省机构号:{},业务码:{},交易码:{},内部交易码:{}",
									new Object[] { msgVo.getTransIDO(),msgVo.getTxnSeq(),msgVo.getMsgSender(),
											reqBipCode, reqActivityCode,transCode });
							log.debug("省发起方流水:{},内部流水:{},省机构号:{},业务码:{},交易码:{},内部交易码:{}",
									new Object[] { msgVo.getTransIDO(),msgVo.getTxnSeq(),msgVo.getMsgSender(),
											reqBipCode, reqActivityCode,transCode  });	
						}
						
					}
					// if (result == null) {
					// Map<String, String> transOrgMap = new HashMap<String,
					// String>();
					// transOrgMap.put("transCode", transCode);
					// transOrgMap.put("orgId", msgSender);
					// boolean orgTransFlag = orgTransCodeCheck(transOrgMap);
					// if (orgTransFlag == false) {
					// logger.warn("内部交易流水:{},发起方机构:{}服务的权限关闭",new Object
					// []{msgVo.getTxnSeq(),msgSender});
					// log.warn("内部交易流水:{},发起方机构:{}服务的权限关闭",new Object
					// []{msgVo.getTxnSeq(),msgSender});
					// msgVo.setRouteType(CommonConstant.RouteType.RoutePhone
					// .toString());
					// msgVo.setRspType(CommonConstant.CrmRspType.BusErr
					// .toString());
					// msgVo.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					// msgVo.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc()+
					// "发起方机构服务的权限关闭");
					// msgVo.setBody(CommonConstant.SpeSymbol.BLANK
					// .toString());
					// result = MsgHandle
					// .marshaller(convertRtnMsgVo(msgVo)); // 返回错误应答报文
					// }
					// }
					// UpayCsysTxnLog upayCsysTxnLog = null;
					// if (result == null) {
					// Map<String, Object> txnMap = new HashMap<String,
					// Object>();
					// txnMap.put("reqDomain", msgVo.getMsgSender());
					// txnMap.put("reqTransId", msgVo.getTransIDO());
					// upayCsysTxnLog = upayCsysTxnLogService
					// .findObj(txnMap);
					// if (upayCsysTxnLog != null) {
					// logger.info("订单重复 OrigDomain=" + msgVo.getOrigDomain()
					// + ",TransIDO=" + msgVo.getTransIDO());
					// msgVo.setRouteType(CommonConstant.routeType.RoutePhone
					// .toString());
					// msgVo.setRspType(CommonConstant.CrmRspType.BusErr
					// .toString());
					// msgVo.setRspCode(MessageHandler.getWzwErrCode("0103"));
					// msgVo.setRspDesc(MessageHandler.getWzwErrMsg("0103"));
					// msgVo.setBody(CommonConstant.SpeSymbol.BLANK.toString());
					// result = MsgHandle.marshaller(convertRtnMsgVo(msgVo)); //
					// 返回错误应答报文
					// }
					//
					// }

					// 交易处理
					if (result == null || "".equals(result)) {
						msgVo.setTransCode(code);
						IBaseAction<CrmMsgVo, CrmMsgVo> action = transactionMap
								.get(transCode);
						msgVo = action.execute(msgVo);
						result = MsgHandle.marshaller(msgVo);
					}

				} catch (Exception e) {
					logger.error("未知异常,内部流水:"+msgVo.getTxnSeq(),e);
					 
					logger.error("省发起方流水:{},内部流水:{},省机构号:{},未知异常:{}",new Object[] {msgVo.getTransIDO(), msgVo.getTxnSeq(), msgVo.getMsgSender(),e.getMessage()});
					log.error("未知异常,省发起方流水:{},内部流水:{},省机构号:{}",new Object[] {msgVo.getTransIDO(), msgVo.getTxnSeq(), msgVo.getMsgSender()});
//					msgVo.setRouteType(CommonConstant.RouteType.RoutePhone
//							.toString());
					//报文头解析失败返回给省应答信息不变,RouteType为00 20131212 modify by weiyi 
					msgVo.setRouteType(CommonConstant.RouteType.RouteProvince
							.toString());
					msgVo.setRspCode(RspCodeConstant.Wzw.WZW_2998.getValue());
					msgVo.setRspDesc(RspCodeConstant.Wzw.WZW_2998.getDesc()+"未知异常");
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
					if (RspCodeConstant.Wzw.WZW_0000.getValue().equals(
							msgVo.getRspCode())) {
						log.succ(
								"省交易成功,发起流水TransIDO:{},应答流水TransIDH:{},内部流水:{},应答码:{},省机构号:{},内部交易码:{}",
								new Object[] {msgVo.getTransIDO(), msgVo.getTransIDH(),
										msgVo.getTxnSeq(), msgVo.getRspCode(),msgVo.getMsgSender(),transCode });
					} else {
						log.error(
								"省交易失败,发起流水TransIDO:{},应答流水TransIDH:{},内部流水:{},应答码:{},省机构号:{},内部交易码:{}",
								new Object[] {msgVo.getTransIDO(), msgVo.getTransIDH(),
										msgVo.getTxnSeq(), msgVo.getRspCode(),msgVo.getMsgSender(),transCode });
					}
					logger.info(
							"省交易完成,发起流水TransIDO:{},应答流水TransIDH:{},内部流水:{},应答码:{},省机构号:{},内部交易码:{}",
							new Object[] {msgVo.getTransIDO(), msgVo.getTransIDH(),
									msgVo.getTxnSeq(), msgVo.getRspCode(),msgVo.getMsgSender(),transCode });
				}

			}
		} catch (Exception e) {
			logger.error("系统 错误", e);
			log.error("系统错误");
		}
	}

	@Override
	protected CrmMsgVo convertRtnMsgVo(BaseMsgVo baseVo) {
		CrmMsgVo vo = (CrmMsgVo) baseVo;
		// vo.setRouteType(CommonConstant.routeType.RouteProvince.toString());
		vo.setActionCode(CommonConstant.ActionCode.Respone.toString());
		vo.setTransIDH(Serial.genSerialNo(CommonConstant.Sequence.IntSeq
				.toString()));
		vo.setTransIDHTime(DateUtil.getDateyyyyMMddHHmmss());
		return vo;
	}

}
