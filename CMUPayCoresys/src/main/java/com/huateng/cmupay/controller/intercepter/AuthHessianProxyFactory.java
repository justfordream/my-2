/**
 * 
 */
package com.huateng.cmupay.controller.intercepter;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;

import com.caucho.hessian.client.HessianProxyFactory;

/**
 * @author cmt
 *
 */
public class AuthHessianProxyFactory extends HessianProxyFactoryBean {

	private HessianProxyFactory proxyFactory = new HessianProxyFactory();

    
	private int readTimeOut = 60000;
   
	private int connectTimeOut = 60000;

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }
    
    
    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public void afterPropertiesSet() {
        proxyFactory.setReadTimeout(readTimeOut);
        proxyFactory.setConnectTimeout(connectTimeOut);
        this.setProxyFactory(proxyFactory);
        super.afterPropertiesSet();
    }

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		//此处预留扩展功能
		return super.invoke(invocation);
	}
}
