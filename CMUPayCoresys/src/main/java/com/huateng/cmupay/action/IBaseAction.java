package com.huateng.cmupay.action; 

import com.huateng.cmupay.exception.AppBizException;


/** 
 * @author cmt  
 * @version 创建时间：2013-3-17 上午1:18:17 
 * 类说明 
 */
public interface IBaseAction<T,R> {

	R execute(T msgVo)throws AppBizException;



}


