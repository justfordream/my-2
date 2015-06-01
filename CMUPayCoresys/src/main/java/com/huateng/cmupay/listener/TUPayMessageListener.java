/**
 * 
 */
package com.huateng.cmupay.listener;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.commons.lang.StringUtils;

import com.huateng.cmupay.action.IBaseAction;
import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.constant.TUPayConstant.UnionPayMsg;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.cmupay.parseMsg.reflect.handle.BaseMsgVo;
import com.huateng.cmupay.utils.Serial;

/**
 * 监听支付前置发送到核心的MQ消息
 * 
 * @author Manzhizhen
 * 
 */
public class TUPayMessageListener extends AbsMessageListener {

	// 用于内部交易代码和对应的Action的匹配
	private Map<String, IBaseAction<Map<String, Object>, Map<String, Object>>> transactionMap;
	
	@Override
	public void onMessage(Message message) {
		try {
			// 接受到对象消息
			if (message instanceof ObjectMessage) {
				
				// 生成一个新的交易流水号
				String intTxnSeq = Serial.genSerialNo(CommonConstant.Sequence.IntSeq
						.toString());
				
				// 第三方支付机构号，用于查找内部交易代码
				final String thridOrgId = message
						.getStringProperty("THIRD_PAY_ORG_ID");
				
				Object obj = ((ObjectMessage) message).getObject();// 串参数
				logger.info( "接收到支付前置发来的报文:{}, 内部交易流水：{}", new Object[] {(obj == null ? "报文内容为空！" : obj), intTxnSeq});
				log.info("接收到支付前置发来的报文:{}, 内部交易流水：{}", new Object[] {(obj == null ? "报文内容为空！" : obj), intTxnSeq});
				
				if (!(obj instanceof Map<?, ?>)) {
					logger.info( "支付前置发来的报文内容格式不是Map<String, String>:{}, 内部交易流水：{}", new Object[] {(obj == null ? "报文内容为空！" : obj), intTxnSeq});
					log.info("支付前置发来的报文内容格式不是Map<String, String>:{}, 内部交易流水：{}", new Object[] {(obj == null ? "报文内容为空！" : obj), intTxnSeq});
					throw new Exception(
							"ObjectMessage's Object is not Map<String, String>");
					
				}

				@SuppressWarnings("unchecked")
				Map<String, String> rcvMap = (Map<String, String>) obj;
				// 由于传给Action中还需要包含额外的数据，所以需要新的Map容器
				Map<String, Object> messageMap = new HashMap<String, Object>(rcvMap.size() + 2);	
				messageMap.putAll(rcvMap);

				/**
				 * 校验报文数据
				 */
//				boolean isTrue = checkMapMessage(messageMap);

				// 解析报文，取得外部业务功能码,交易码
				String reqActivityCode = (String) messageMap.get(UnionPayMsg.TXNTYPE.getValue()); // 获取报文的交易码
				String orderId = (String) messageMap.get(UnionPayMsg.ORDERID.getValue());
				String merId = (String) messageMap.get(UnionPayMsg.MERID.getValue());
				String txnTime = (String) messageMap.get(UnionPayMsg.TXNTIME.getValue());
				
				String transCode = "";
				UpayCsysTransCode upayCsysTransCode = new UpayCsysTransCode();

				try {
//					if (isTrue) {
						// 得到内部交易码
						Map<String, String> queryMap = new HashMap<String, String>();
						// 按照约定，第三方支付机构的交易代码在交易代码表中是由机构号+交易类型构成的
						queryMap.put("reqActivityCode", thridOrgId + reqActivityCode);
						upayCsysTransCode = transCodeConvert(queryMap);
						if (upayCsysTransCode == null || StringUtils.isBlank(upayCsysTransCode
										.getTransCode())) {
							logger.warn(
									"发起方机构没有此服务的权限，内部交易流水:{},订单号:{},商户ID:{},订单发送时间:{},交易类型:{}",
									new Object[] { intTxnSeq, orderId, merId, txnTime, reqActivityCode});
							log.warn(
									"发起方机构没有此服务的权限，内部交易流水:{},订单号:{},商户ID:{},订单发送时间:{},交易类型:{}",
									new Object[] { intTxnSeq, orderId, merId, txnTime, reqActivityCode });
							
							// 因为不需要返回给支付前置应答，所以这里可以直接返回
							return ;
						}
						
						transCode = upayCsysTransCode.getTransCode();
						logger.info("收到支付前置发来的报文，内部交易流水:{},内部交易代码{},订单号:{},商户ID:{},订单发送时间:{},交易类型:{}",
								new Object[] { intTxnSeq, transCode, orderId, merId, txnTime, reqActivityCode });
						log.debug("收到支付前置发来的报文，内部交易流水:{},内部交易代码{},订单号:{},商户ID:{},订单发送时间:{},交易类型:{}",
								new Object[] { intTxnSeq, transCode, orderId, merId, txnTime, reqActivityCode });
						
//					} else {
//						logger.warn("收到支付前置发来的报文格式有误！ 内部交易流水:{},内部交易代码{},订单号:{},商户ID:{},订单发送时间:{},交易类型:{}",
//								new Object[] { intTxnSeq, transCode, orderId, merId, txnTime, reqActivityCode });
//						log.warn("收到支付前置发来的报文格式有误！ 内部交易流水:{},内部交易代码{},订单号:{},商户ID:{},订单发送时间:{},交易类型:{}",
//								new Object[] { intTxnSeq, transCode, orderId, merId, txnTime, reqActivityCode });
//						// 如果报文校验不通过，直接返回
//						return ;
//					}

					// 交易处理
					Map<String, Object> returnToThirdMap = null;
					IBaseAction<Map<String, Object>, Map<String, Object>> action = transactionMap
							.get(transCode);
					
					// 设置内部交易流水值和内部交易代码等附加信息
					messageMap.put("#intTxnSeq", intTxnSeq);
					messageMap.put("#upayCsysTransCode", upayCsysTransCode);
					
					// 银联暂时不需要返回值
					if(action != null) {
						returnToThirdMap = action.execute(messageMap);
					}

				} catch (Exception e) {
					logger.error("未知异常 {}，内部交易流水:{},内部交易代码{},订单号:{},商户ID:{},订单发送时间:{},交易类型:{}" , 
							new Object[]{e, intTxnSeq, orderId, merId, txnTime, reqActivityCode });
					log.error("未知异常 {}，内部交易流水:{},内部交易代码{},订单号:{},商户ID:{},订单发送时间:{},交易类型:{}" , 
							new Object[]{e, intTxnSeq, orderId, merId, txnTime, reqActivityCode});
				}

			}
		} catch (Exception e) {
			logger.error("系统错误", e);
			log.error("系统错误", e);
		}

	}

	/**
	 * 校验报文数据
	 * 
	 * @param paramMap
	 */
	private boolean checkMapMessage(Map<String, Object> paramMap) {
		return paramMap.containsKey(UnionPayMsg.VERSION.getValue()) && 
			paramMap.containsKey(UnionPayMsg.ENCODING.getValue()) &&
			paramMap.containsKey(UnionPayMsg.CERTID.getValue()) &&
			paramMap.containsKey(UnionPayMsg.SIGNATURE.getValue()) &&
			paramMap.containsKey(UnionPayMsg.TXNTYPE.getValue());
	}

	@Override
	protected BaseMsgVo convertRtnMsgVo(BaseMsgVo vo) {
		return null;
	}
	
	public void setTransactionMap(
			Map<String, IBaseAction<Map<String, Object>, Map<String, Object>>> transactionMap) {
		this.transactionMap = transactionMap;
	}
}
