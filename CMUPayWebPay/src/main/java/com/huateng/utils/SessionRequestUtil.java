package com.huateng.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

/**
 * session和request常用方法汇总
 * @author 马博阳
 *
 */
public class SessionRequestUtil {

    /**
     * 获取session
     * @return
     */
    public static HttpSession getSession(){
        
        HttpServletRequest request = ServletActionContext.getRequest();
        
        HttpSession session = request.getSession();
        
        return session;
    }
    
    /**
     * 获取request
     * @return
     */
    public static HttpServletRequest getRequest(){
        
        HttpServletRequest request = ServletActionContext.getRequest();
        
        return request;
    }
    
    /**
     * 获取request
     * @return
     */
    public static HttpServletResponse getResponse(){
        
        HttpServletResponse sesponse = ServletActionContext.getResponse();
        
        return sesponse;
    }
    
    
    /**
     * 获取Parameter上下文值,如果不存在,在存session中取
     * @param s
     * @return
     */
    public static String getParameterOrSession(String key){
       
        String str= getParameter(key);
        
        if(StringUtils.isBlank(str)){
            
            str = getSessionValue(key);
        }
        
        return str;
    }
    
    /**
     * 获取Parameter上下文值
     * @param key
     * @return
     */
    public static String getParameter(String key){
       
        return ServletActionContext.getRequest().getParameter(key);
        
    }
    
    /**
     * 获取session中的值
     * @param s
     * @return
     */
    public static String getSessionValue(String key){
        
        return (String) getSession().getAttribute(key);
    }
    
    /**
     * 设置session中的值
     * @return
     */
    public static void setSessionValue(String key,String value){
        
       getSession().setAttribute(key,value);
    }
    
    
}
