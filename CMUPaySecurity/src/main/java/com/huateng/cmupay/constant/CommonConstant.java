/**
 * 
 */
package com.huateng.cmupay.constant;

/**
 * 公用常量类
 * @author cmt
 *
 */
public class CommonConstant {
	public static final String ENCODING="UTF-8";
	public static final String LOG_LEVEL_BUSINESS = "BUSINESS";
	public static final String LOG_LEVEL_INFO = "INFO";
	public static final String LOG_LEVEL_SUCC = "SUCC";
	public static final String LOG_LEVEL_WARNING = "WARNING";
	public static final String LOG_LEVEL_SERIOUS = "SERIOUS";
	public static final String LOG_LEVEL_ALARM = "ALARM";
	public static final String LOG_LEVEL_ERROR = "ERROR";
	public static final String LOG_LEVEL_DEBUG = "DEBUG";
	public static final String TEST_FLAG_CLOSE = "close";
	/**
	 * Java密钥库(Java Key Store，JKS)KEY_STORE
	 */
	public static final String KEY_STORE = "JKS";

	public static final String X509 = "X.509";

	/**
	 * 签名的算法 使用 keytool -v -list -keystore cmsz_keystore 查看证书的算法 SHA1WithRSA   MD5WithRSA
	 */
	public static final String SIGNATURE_METHOD = "SHA1withRSA";

	public static final int symType = 1282;// 算法类型
	
	public static final String ENCODEING = "utf-8";
	
	public static final String CMU = "cmu";
	
	public static final String DEFAULT = "default";
	
	public static final String bank_flag="open";

	public static final String BANK = "bank";
	/**
	 * 是否有效
	 * @author cmt
	 *
	 */
	public enum IsActive {
		True("0","有效"),
		False("1","无效")
		;
		
		String value;
		String desc;
		
		IsActive(String value,String desc){
			this.value=value;
			this.desc=desc;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return this.value;
		}
		
		public String getValue(){
			return this.value;
		}
		public String getDesc(){
			return this.desc;
		}
		public static String getDescByValue(String value){
			if(True.toString().equals(value)){
				return  True.desc;
			}else{
				return False.desc;
			}
		}
	}
	
	
	
	/**
	 * 是否历史
	 * @author cmt
	 *
	 */
	public enum IsHistory{
		Normal("0","正常"),
		History("1","历史记录")
		;
		
		String value;
		String desc;
		
		IsHistory(String value,String desc){
			this.value=value;
			this.desc=desc;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return this.value;
		}
		
		public String getDesc(){
			return this.desc;
		}
	}
	
	
	
	/**
	 * 成功标志
	 * @author cmt
	 *
	 */
	public enum SuccessFlag {
		Success("0","成功"),
		Error("1","失败")
		;
		
		String value;
		String desc;
		
		SuccessFlag(String value,String desc){
			this.value=value;
			this.desc=desc;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return this.value;
		}
		
		public String getDesc(){
			return this.desc;
		}
		
		public static String getDescByValue(String value){
			
			if (Success.value.equals(value)) 
				return Success.desc;
			else 
				return Error.desc;
		}
	}
	
	
	/**
	 * 是，否
	 * @author Ping.Hong
	 *
	 */
	public enum YesOrNo {
		Yes("0","是"),
		No("1","否")
		;
		
		String value;
		String desc;
		
		YesOrNo(String value,String desc){
			this.value=value;
			this.desc=desc;
		}
		
		public String getValue(){
			return this.value;
		}
		public String getDesc(){
			return this.desc;
		}
		public static String getDescByValue(String value){
			if(Yes.value .equals(value)){
				return  Yes.desc;
			}else{
				return No.desc;
			}
		}
	}
	
	
	
}