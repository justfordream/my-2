package com.huateng.cmupay.models.common.multidatasource;
/**  
* 上下文Holder  
*  
*/  
/**
 * @author cmt
 *
 * @param <T>
 */
@SuppressWarnings("unchecked")    
public class ContextHolder<T> {   
 
@SuppressWarnings("rawtypes")
private static final ThreadLocal contextHolder = new ThreadLocal();   
      
    public static <T> void setContext(T context)   
    {   
        contextHolder.set(context);   
    }   
       
    public static <T> T getContext()   
    {   
        return (T) contextHolder.get();   
    }   
       
    public static void clearContext()   
    {   
        contextHolder.remove();   
    }   
} 