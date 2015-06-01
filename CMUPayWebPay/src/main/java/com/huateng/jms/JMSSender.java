package com.huateng.jms;

import java.io.IOException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.huateng.utils.UUIDGenerator;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
@Component
public class JMSSender {

    private final Logger logger = LoggerFactory.getLogger(JMSSender.class);

    private JmsTemplate  template;
    private Destination  destination;
    private Destination  respDest;
    private ObjectMapper jsonMapper;
    private String       queryKey;
    private String       md5Key;
    /** 以报文中通讯流水为主键的一个线程安全hashmap */
	//private static ConcurrentHashMap<String, RecvMessageBean> excuterthreadsmap = new ConcurrentHashMap<String, RecvMessageBean>();
//    /**
//     * @param args
//     * @throws IOException
//     * @throws JsonMappingException
//     * @throws JsonGenerationException
//     */
//    public String send(String message){
//
//        /*使用单例模式获取mq序列*/
//        final long seq = System.currentTimeMillis();
//        /*消息*/
//        final String info = message;
//        RecvMessageBean temprecv = new RecvMessageBean();
//		RecvMessageBean exTempRecv = excuterthreadsmap.putIfAbsent(String.valueOf(seq), temprecv);
//		RecvMessageBean tempresult = null;
//		//如果已存在，就打个警告
//		if(exTempRecv!=null){
//			logger.error("{} key duplicated",String.valueOf(seq));
//		}
//		boolean acqureresult = false;
//		try {
//			template.send(destination, new MessageCreator() {
//	            public Message createMessage(Session session) throws JMSException {
//	                Message msg = session.createTextMessage(info);
//	                msg.setJMSReplyTo(respDest);
//	                msg.setStringProperty("senderid", IAppCode.INST_ID);
//	                msg.setStringProperty("reqTxnSeq", String.valueOf(seq));
//	                msg.setBooleanProperty("isaSync", false);
//	                return msg;
//	            }
//	        });
//
//	     // 挂在该资源上，等待N秒内有回文
//	      acqureresult = temprecv.getRecvsemap().tryAcquire(template.getReceiveTimeout(), TimeUnit.MILLISECONDS);
//		} catch (Exception e) {
//			logger.error("send JMS error!reqTxnSeq:{}",String.valueOf(seq));
//		}finally{
//			// 从hashmap里拿到收到的消息
//			 tempresult = excuterthreadsmap.remove(seq);
//		}
//		String respJson = null;
//		if (acqureresult && tempresult != null) {
//			TextMessage msg = (TextMessage) tempresult.getRecvmsg();
//			if (msg != null) {
//				logger.debug("succeed to receive message from {},reqTxnSeq_returnMsg:{}",respDest.toString(), seq + "_" + msg);
//			        try {
//			        	respJson=msg.getText();
//			        } catch (JMSException e) {
//			        	logger.error("getText error!reqTxnSeq:{}",String.valueOf(seq));
//			        } 
//			} else {
//				logger.error("receive message time out!reqTxnSeq:{}",String.valueOf(seq));
//			}
//		} else {
//			logger.error(" receive message time out!reqTxnSeq:{}",String.valueOf(seq));
//			logger.error(" map size is [{}]", String.valueOf(excuterthreadsmap.size()));
//		}
//        return respJson;
//    }

    /**
     * @param args
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonGenerationException
     */
    public String send(String message){

        /*使用单例模式获取mq序列*/
        final String seq = UUIDGenerator.generateUUID();
        
        /*消息*/
        final String info = message;

        template.send(destination, new MessageCreator() {

            public Message createMessage(Session session) throws JMSException {

                Message msg = session.createTextMessage(info);

                msg.setJMSReplyTo(respDest);

                msg.setStringProperty("reqTxnSeq", String.valueOf(seq));

                return msg;
            }
        });

        TextMessage respMsg = (TextMessage) template.receiveSelected(respDest, "reqTxnSeq='" + seq+"'");

        String respJson = null;
        try {
           
            respJson = respMsg.getText();
            
        } catch (JMSException e) {
        	logger.info("获取JMS 消息错误 error{}",respJson);
           // ErrorUtil.printErrorLog(e, logger);
        }
        logger.info(respJson);

        return respJson;

    }
//    
//    
//    public void recvmessagefromserver(Message recvmessage) {
//		if (recvmessage == null) {
//			logger.info(" recvmessagefromserver error");
//			return;
//		}
//		/* 得到通讯报文中包号 */
//
//		String key;
//		try {
//			key = recvmessage.getStringProperty("reqTxnSeq");
//			RecvMessageBean temprecv = excuterthreadsmap.get(key);
//
//			if (temprecv != null) {
//				temprecv.setRecvmsg(recvmessage);
//				temprecv.getRecvsemap().release();
//			} else {
//				logger.info(" map size is [{}]", String.valueOf(excuterthreadsmap.size()));
//				logger.info(" recvmessagefromserver key [{}]: is null", key);
//			}
//		} catch (JMSException e) {
//			logger.error("",e);
//		}
//		// 挂在资源上的消息放入，同时唤醒工作线程
//
//	}
    public JmsTemplate getTemplate() {
        return template;
    }

    public void setTemplate(JmsTemplate template) {
        this.template = template;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public Destination getRespDest() {
        return respDest;
    }

    public ObjectMapper getJsonMapper() {
        return jsonMapper;
    }

    public void setJsonMapper(ObjectMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public void setRespDest(Destination respDest) {
        this.respDest = respDest;
    }

    public String getQueryKey() {
        return queryKey;
    }

    public void setQueryKey(String queryKey) {
        this.queryKey = queryKey;
    }

    public String getMd5Key() {
        return md5Key;
    }

    public void setMd5Key(String md5Key) {
        this.md5Key = md5Key;
    }

}
