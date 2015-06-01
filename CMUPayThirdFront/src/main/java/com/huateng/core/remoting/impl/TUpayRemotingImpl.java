package com.huateng.core.remoting.impl;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.huateng.core.common.CoreConstant;
import com.huateng.core.common.CoreConstant.UnionPayMsg;
import com.huateng.core.exception.ServiceException;
import com.huateng.core.jms.common.JmsMsgHeader;
import com.huateng.core.remoting.TUpayRemoting;
import com.huateng.core.util.DateUtil;
import com.huateng.core.util.JacksonUtils;
import com.huateng.log.LogHandle;
import com.huateng.third.logFormat.MessageLogger;
import com.huateng.third.service.TUPayRemoteService;
import com.unionpay.mpi.HttpClient;
import com.unionpay.mpi.MpiUtil;

/**
 * 发送消息到银联
 * @author Manzhizhen
 * 
 */
public class TUpayRemotingImpl implements TUpayRemoting {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private MessageLogger log = MessageLogger.getLogger(getClass());
	
	/**
	 * 验签服务
	 */
	@Autowired
	private TUPayRemoteService remoteService;

	/**
	 * 验签开关
	 */
	private @Value("${check.switch}")
	String checkSwitch;
	/**
	 * 签名开关
	 */
	private @Value("${check.switch}")
	String signSwitch;
	/**
	 * 连接超时时间
	 */
	private @Value("${http.conn.timeout}")
	String connTimeout;

	/**
	 * 接收响应超时时间
	 */
	private @Value("${http.rev.timeout}")
	String revTimeout;

	private LogHandle logHandle;

	public void setLogHandle(LogHandle logHandle) {
		this.logHandle = logHandle;
	}

	/**
	 * 由核心远程调用来向银联发送消息
	 */
	@SuppressWarnings("finally")
	@Override
	public Map<String, String> sendMsg(String header,
			Map<String, String> paramsMap) {
		
		Map<String, String> resp2CoreMap = null;
		Map<String, String> responseMap = null;
		
		

		try {
			if (MapUtils.isNotEmpty(paramsMap)) {
				String orderId = paramsMap.get(UnionPayMsg.ORDERID.getValue());
				String txnType = paramsMap.get(UnionPayMsg.TXNTYPE.getValue());
				String merId = paramsMap.get(UnionPayMsg.MERID.getValue());
				
				/**
				 * 路由信息
				 */
				JmsMsgHeader routeMsg = JacksonUtils.json2Bean(header,
						JmsMsgHeader.class);
				logger.info("支付前置接受核心的请求,报文内容{},订单号:{},商户代码:{},交易类型:{}",
						new Object[] { DateUtil.printMapLog(paramsMap), orderId, merId, txnType});

				this.logHandle.info(true,
						CoreConstant.ErrorCode.SUCCESS.getCode(),
						orderId, txnType, "", merId, "",
						CoreConstant.ErrorCode.SUCCESS.getDesc());

				log.debug("订单号:{},商户代码:{},交易类型:{}",
						new Object[] { orderId, merId, txnType});

				logger.info("......根据路由信息开始向银联[{}]发送信息 :{},订单号:{},商户代码:{},交易类型:{}", new Object[] {
						routeMsg.getReqIp(), DateUtil.printMapLog(paramsMap), orderId, merId, txnType});
				logger.debug("......根据路由信息开始向银联[{}]发送信息 :{},订单号:{},商户代码:{},交易类型:{}", new Object[] {
						routeMsg.getReqIp(), paramsMap, orderId, merId, txnType });

				/**
				 * 开始发送消息
				 */
				responseMap = sendToUnionPay(routeMsg, paramsMap);

				logger.info(
						"......支付前置接收到银联[{}]响应信息:{},订单号:{},商户代码:{},交易类型:{}",
						new Object[] { routeMsg.getReqIp(), DateUtil.printMapLog(responseMap),
								orderId, merId, txnType});
				logger.debug("......支付前置接收到银联[{}]响应信息:{},订单号:{},商户代码:{},交易类型:{}",
						new Object[] {routeMsg.getReqIp(), responseMap, orderId, merId, txnType });
				log.info("支付前置接收到银联[{}]响应信息:{},订单号:{},商户代码:{},交易类型:{}",
						new Object[] { routeMsg.getReqIp(), responseMap, orderId, merId, txnType });
				/**
				 * 验签
				 */
				resp2CoreMap = checkSign(CoreConstant.UNION_PAY_ORG_ID, responseMap);

			} else {
				logger.error("......支付前置TUpayRemotingImpl接收核心的消息Map为空！");
				log.error("......支付前置TUpayRemotingImpl接收核心的消息Map为空！");
				throw new ServiceException("UPAY-B-015A05");
			}
			
		} catch (ServiceException e) {
			logger.error("......支付前置TUpayRemotingImpl处理核心消息失败,失败原因:"
					+ e.getMessage());
			log.error("......支付前置TUpayRemotingImpl接收核心的消息Map为空！");
			
		} finally {
			return resp2CoreMap==null?new HashMap<String, String>():resp2CoreMap ;
		}

	}

	/**
	 * 根据路由信息主动发送信息到指定路由
	 * @param routeMsg
	 *            路由信息
	 * @param paramsMap
	 *            发送的信息
	 * @return
	 * @throws ServiceException
	 * @throws Exception
	 */
	protected Map<String, String> sendToUnionPay(
			JmsMsgHeader routeMsg, final Map<String, String> paramsMap) throws ServiceException {
		
		String orderId = paramsMap.get(UnionPayMsg.ORDERID.getValue());
		String txnType = paramsMap.get(UnionPayMsg.TXNTYPE.getValue());
		String merId = paramsMap.get(UnionPayMsg.MERID.getValue());
		
		if (routeMsg == null) {
			throw new ServiceException("UPAY-B-015A06");
		}
		
		String protocol = routeMsg.getProtocol();
		
		if (StringUtils.isBlank(protocol)) {
			throw new ServiceException("UPAY-B-015A06");
		}
		
		if (!protocol.toLowerCase().equals("http")
				&& !protocol.toLowerCase().equals("https")) {
			throw new ServiceException("UPAY-B-015A06");
		}
		
		String reqIp = routeMsg.getReqIp();
		if (StringUtils.isBlank(reqIp)) {
			throw new ServiceException("UPAY-B-015A06");
		}

		String reqPort = routeMsg.getReqPort();
		if (StringUtils.isBlank(reqPort)) {
			throw new ServiceException("UPAY-B-015A06");
		}
		
		String reqPath = routeMsg.getReqPath() == null ? "" : routeMsg
				.getReqPath();
		
		logger.debug("协议类型: " + protocol + ",请求IP: " + reqIp + ",请求端口号: "
				+ reqPort + ",请求地址: " + reqPath);
		
		Map<String, String> responseMap = null;
		try {
			String requestURL = protocol + "://" + reqIp + ":" + reqPort
					+ reqPath;
			logger.debug("Request URL is: " + requestURL);

			if (StringUtils.isBlank(connTimeout)
					|| StringUtils.isBlank(revTimeout)) {
				throw new ServiceException("UPAY-B-015A06");
			}

			/**
			 * 开始进行签名
			 */
			responseMap = sign(CoreConstant.UNION_PAY_ORG_ID, paramsMap);

			/**
			 * 发送消息
			 */
			HttpClient httpClient = new HttpClient(requestURL, Integer.parseInt(connTimeout), 
					Integer.parseInt(revTimeout));
			int status = httpClient.send(paramsMap, CoreConstant.MSG_ENCODING);

			String response = null;
			if (HttpStatus.SC_OK == status) {
				response = httpClient.getResult();
			}
			

			/**
			 * 将返回参数转换成Map
			 */
			responseMap = MpiUtil.coverResultString2Map(response);
			
		} catch (SocketTimeoutException e) {
			logger.error("支付前置连接银联超时!订单号:{},商户代码:{},交易类型:{}" + e.getMessage(),
					new Object[]{orderId, merId, txnType});
			log.error("支付前置连接银联超时!订单号:{},商户代码:{},交易类型:{}" + e.getMessage(),
					new Object[]{orderId, merId, txnType});
			throw new ServiceException("UPAY-B-015A07");// 超时未收到响应
		} catch (ConnectException e) {
			logger.error("支付前置连接银联超时!订单号:{},商户代码:{},交易类型:{}" + e.getMessage(),
					new Object[]{orderId, merId, txnType});
			log.error("支付前置连接银联超时!订单号:{},商户代码:{},交易类型:{}" + e.getMessage(),
					new Object[]{orderId, merId, txnType});
			throw new ServiceException("UPAY-B-015A07");
		} catch (Exception e) {
			logger.error("支付前置发送消息到银联失败!订单号:{},商户代码:{},交易类型:{}" + e.getMessage(),
					new Object[]{orderId, merId, txnType});
			log.error("支付前置发送消息到银联失败!订单号:{},商户代码:{},交易类型:{}" + e.getMessage(),
					new Object[]{orderId, merId, txnType});
			throw new ServiceException("UPAY-B-015A06");// 未知错误
		}
		
		if (MapUtils.isEmpty(responseMap)) {
			throw new ServiceException("UPAY-B-015A07");
		}
		
		return responseMap;
	}

	public Map<String, String> checkSign(String client,
			Map<String, String> paramsMap) throws ServiceException {
		if (MapUtils.isEmpty(paramsMap)) {
			throw new ServiceException("UPAY-B-015A05");// 解析报文失败
		}
		
		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(checkSwitch)) {
			logger.debug("......调用支付前置验签方法......");

			String orderId = paramsMap.get(UnionPayMsg.ORDERID.getValue());
			String txnType = paramsMap.get(UnionPayMsg.TXNTYPE.getValue());
			String merId = paramsMap.get(UnionPayMsg.MERID.getValue());
			/*
			 * 全网监控日志
			 */
			this.logHandle.info(false,
					CoreConstant.ErrorCode.SUCCESS.getCode(),
					orderId, txnType, "", merId, "",
					CoreConstant.ErrorCode.SUCCESS.getDesc());

			boolean isValid = remoteService.verify(client,
					paramsMap);
			
			if (!isValid) {
				logger.error("......验签失败......订单号:{},商户代码:{},交易类型:{}", new Object[]{orderId, merId, txnType});
				log.error("......验签失败......订单号:{},商户代码:{},交易类型:{}", new Object[]{orderId, merId, txnType});
				throw new ServiceException("UPAY-B-014A06");// 签名验证失败
			}
		}

		return paramsMap;
	}

	/**
	 * 对Map数据进行签名
	 * 
	 * @param client
	 * @param xmlContent
	 * @return
	 * @throws ServiceException
	 */
	public Map<String, String> sign(String client, Map<String, String> paramsMap)
			throws ServiceException {
		if (CoreConstant.SWITCH_OPEN.equalsIgnoreCase(signSwitch)) {
			logger.debug("......开始去调用加密机来执行支付前置签名.....");
			
			log.info("签名前报文{}", DateUtil.printMapLog(paramsMap));
			logger.info("......签名前报文{}", DateUtil.printMapLog(paramsMap));
			
			/**
			 * 去加密机调用签名方法
			 */
			paramsMap = remoteService.sign(client,
					paramsMap);

			log.info("签名成功,签名后报文{}", DateUtil.printMapLog(paramsMap));
			logger.info("......签名成功,签名后报文{}", DateUtil.printMapLog(paramsMap));
		}
		return paramsMap;
	}

	public String getCheckSwitch() {
		return checkSwitch;
	}

	public void setCheckSwitch(String checkSwitch) {
		this.checkSwitch = checkSwitch;
	}

	public String getConnTimeout() {
		return connTimeout;
	}

	public void setConnTimeout(String connTimeout) {
		this.connTimeout = connTimeout;
	}

	public String getRevTimeout() {
		return revTimeout;
	}

	public void setRevTimeout(String revTimeout) {
		this.revTimeout = revTimeout;
	}

	public String getSignSwitch() {
		return signSwitch;
	}

	public void setSignSwitch(String signSwitch) {
		this.signSwitch = signSwitch;
	}

}
