/**
 * 
 */
package com.huateng.bank.common;




public interface AppCode {
	
	// String  INST_ID = HostMsgCache.getHostIP4Part();
	
	String  INST_ID = com.huateng.bank.common.UUIDGenerator.generateUUID();
}
