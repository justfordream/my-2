package com.huateng.cmupay.controller.service.system; 

import com.huateng.cmupay.models.UpayCsysBatCutCtl;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-26 下午7:56:32 
 * 类说明 
 */
public interface IUpayCsysBatCutCtlService {
	
	 UpayCsysBatCutCtl findObjByKey(Long seq)  ;
	 
	 String findCutOffDate(Long seq)  ;

}


