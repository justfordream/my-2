/**
 * 
 */
package com.huateng.crm.common;

//import org.springframework.stereotype.Component;

//import com.fasterxml.uuid.Generators;


public interface AppCode {
	
	// String  INST_ID = HostMsgCache.getHostIP4Part();
	 
	 String  INST_ID = UUIDGenerator.generateUUID();
}
