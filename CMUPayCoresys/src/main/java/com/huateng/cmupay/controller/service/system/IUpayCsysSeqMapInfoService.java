package com.huateng.cmupay.controller.service.system; 


/** 
 * @author cmt  
 * @version 创建时间：2013-3-17 下午2:40:02 
 * 类说明 
 */
public interface IUpayCsysSeqMapInfoService {
	
	void updateIdentity( Integer seqCode );
	Long selectIdentity(Integer seqCode);
	
	void updateIdsatity( Integer seqCode );
	Long selectIdsatity(Integer seqCode);
	
	Long selectSeqValue(Integer
			seqCode);

	
	
	
}


