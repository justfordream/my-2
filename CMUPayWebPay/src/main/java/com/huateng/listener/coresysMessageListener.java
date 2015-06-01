package com.huateng.listener;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.huateng.bean.CoreResultPayRes;
import com.huateng.core.util.JacksonUtil;


public class coresysMessageListener implements MessageListener{
	private Logger logger = Logger.getLogger(coresysMessageListener.class);

    public void onMessage(Message message) {
    	Map<String, String> params;

        if (message instanceof TextMessage) {
            try {
            	String xmlContent = ((TextMessage) message).getText();
            	logger.debug(".######监听到了#####接收核心的信息 : " + xmlContent);
            	CoreResultPayRes r = JacksonUtil.strToBean(CoreResultPayRes.class, xmlContent);
        		params = this.initParams(r);
        		//HttpClientUtil.formSubmit(r.getServerURL(), params, "UTF-8");
        		
        		logger.debug("######省份的后台通知##### : MerId="+params.get("MerID")+",OrderId="+params.get("OrderID")
        				+",RspCode="+params.get("RspCode")+",RspInfo="+params.get("RspInfo")
        				+",MerVAR="+params.get("MerVAR")+",OrderTime="+params.get("OrderTime")
        				+",Payed="+params.get("Payed")+",CurType="+params.get("CurType")
        				+",MCODE="+params.get("MCODE")+",Sig="+params.get("Sig")
        				+",ServerURL="+params.get("ServerURL")+",BackURL="+params.get("BackURL"));
        		
        		logger.debug("####################缴费通知完成#########################");
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }else {
            throw new IllegalArgumentException("Message must be of type TextMessage");
        }
    }
    
    public Map<String, String> initParams(CoreResultPayRes r) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("MerID", r.getMerID());
		params.put("OrderID", r.getOrderID());
		params.put("RspCode", r.getRspCode());
		params.put("RspInfo", r.getRspInfo());
		params.put("MerVAR", r.getMerVAR());
		params.put("OrderTime", r.getOrderTime());
		params.put("Payed", r.getPayed());
		params.put("CurType", r.getCurType());
		params.put("MCODE", r.getMCODE());
		params.put("Sig", r.getSig());
		params.put("ServerURL", r.getServerURL());
		params.put("BackURL", r.getBackURL());
		
		return params;
    }

}
