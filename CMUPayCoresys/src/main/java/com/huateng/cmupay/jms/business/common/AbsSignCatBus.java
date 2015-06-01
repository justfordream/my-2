package com.huateng.cmupay.jms.business.common; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-17 上午2:34:06 
 * 类说明 
 */
public abstract class AbsSignCatBus<T,R> implements ICommonBus<T,R>{

	
	protected  final Logger logger = LoggerFactory.getLogger(this.getClass());
}


