package com.huateng.service;

import com.huateng.tcp.BaseTcp;
/**
 * 
 * @author Gary
 *
 */
public class BaseService {

    private BaseTcp baseTcp;

    /**
     * 发送消息
     */
    public String sendMessage(Object message) {

        return baseTcp.sendMessage((String) message);

    }

    public void setBaseTcp(BaseTcp baseTcp) {
        this.baseTcp = baseTcp;
    }

}
