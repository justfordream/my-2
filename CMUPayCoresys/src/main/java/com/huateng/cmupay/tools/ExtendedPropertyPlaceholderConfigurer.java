package com.huateng.cmupay.tools;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;


public class ExtendedPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
	
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		try {
			Properties mergedProps = mergeProperties();
			// Convert the merged properties, if necessary.
			convertProperties(mergedProps);

			// Let the subclass process the properties.
			processProperties(beanFactory, mergedProps);
		}
		catch (IOException ex) {
			throw new BeanInitializationException("Could not load properties", ex);
		}
	}
	
	protected void convertProperties(Properties props) {
//		String RMI_HOST = props.getProperty("rmiHost");
//        //int    RMI_PORT = Integer.parseInt(props.getProperty("rmiPort"));
//        String RMI_PATH = props.getProperty("rmiProcPath"); 
//        if(!RMI_HOST.endsWith("/")) {
//        	RMI_HOST += "/";
//        }
//        //RmiClient rmiClient = new RmiClient(); 
//        CallBean rmiClient = new CallBean();
//		Enumeration propNames = props.propertyNames();
//        //�����ݿ��û���ƣ�����Զ�̷�����ȡ����ݿ�����	
//		while (propNames.hasMoreElements()) {
//			String propertyName = (String) propNames.nextElement();			
//			if (propertyName.endsWith(".username")) {			
//				String propertyValue = props.getProperty(propertyName);//�û����
//				String convertedValue = convertPropertyValue(propertyValue);
//				String passwordName = propertyName.substring(0,propertyName.lastIndexOf('.'))+".password";
//			    System.out.println("before setProperty " + passwordName + "=" +props.getProperty(passwordName) );
//				//����û�����
//				String passwordValue = rmiClient.callStr(RMI_HOST, RMI_PATH,convertedValue);				
//				//��дprops����
//				props.setProperty(passwordName, passwordValue);				
//				System.out.println("after setProperty " + passwordName + "=" +props.getProperty(passwordName) );
//				
//			}
//		}
		System.out.println("getPwd end" );
	}

}
