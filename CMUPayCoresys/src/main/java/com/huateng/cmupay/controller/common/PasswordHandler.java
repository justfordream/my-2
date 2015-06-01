/**
 * 
 */
package com.huateng.cmupay.controller.common;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

import com.huateng.toolbox.security.Md5;



/**
 * 密码加密类
 * @author cmt
 *
 */
//@Component
public class PasswordHandler {

	private @Value("${global.password}") String globalPasswordReset;
	
	private static String passwordReset;
	
	/**
	 * 密码加密
	 * @author cmt
	 * @param password
	 * @return
	 */
	public static String getPassword(String password){
		return Md5.getMD5ofStrByUpperCase("HT"+password+"CS");
	}
	
	/**
	 * 重置密码
	 * @author cmt
	 * @return
	 */
	public static String resetPassword(){
		return getPassword(passwordReset);
	}
	
	@SuppressWarnings("unused")
	@PostConstruct
	private void init(){
		passwordReset=globalPasswordReset;
	}
	
	public static void main(String[] args) {
		System.out.println(getPassword("admin"));
	}
}
