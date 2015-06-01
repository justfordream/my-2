package com.huateng.cmupay.logFormat;


import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.cmupay.constant.ExcConstant;
import com.huateng.cmupay.controller.cache.HostMsgCache;
import com.huateng.toolbox.utils.DateUtil;



/**
 * @author oul
 *
 */
public class MobileMarketMessageLogger {
	
		private static final String APP_ID = "upay";
		private static final String CORE_SUB_SYS  = "CoreSys";
	    private Logger logger = LoggerFactory.getLogger(this.getClass());;
	    
	    private String  instance="TmallMessageLogger";
	    
	    private String  module = "Tmall";
	    
	    private String [] args = {};
	    
	    public static MobileMarketMessageLogger getLogger() {
	        return new MobileMarketMessageLogger();
	    }
	    
	   
	    public static MobileMarketMessageLogger getLogger(final Class<?> clazz) {
	    	return new MobileMarketMessageLogger(clazz.getName());
	    }
	    
	    public static MobileMarketMessageLogger getLogger(final String clazzname) {
	        return new MobileMarketMessageLogger(clazzname);
	    }
	   
	    private MobileMarketMessageLogger() {
	    	this.setInstance("TmallMessageLogger");
	    }
	    
	    private MobileMarketMessageLogger(final String clazzname) {
	    	 if(clazzname != null){
	    		 String [] a = clazzname.split("[.]");
			     this.setInstance(a[a.length-1]);
	    	 }
	    }
	    
		private static String getRegular(Object t) {
			/*
			 * if(null == t) { t = new Throwable(); } StackTraceElement element =
			 * t.getStackTrace()[1]; String className = element.getClassName(); if
			 * (className.lastIndexOf(".") != -1) className =
			 * className.substring(className.lastIndexOf(".") + 1);
			 * 
			 * StringBuilder sb = new StringBuilder(); return
			 * sb.append(className).append
			 * (":").append(element.getRegular()).append(" -").toString();
			 */
			
			 String regular ="";
			return regular;
		}
		
		
		
	     public  void debug(String logPoint) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_BUSINESS, logPoint, this.getInstance(), this.getModule());
			logger.debug(new StringBuilder().append(getRegular(null)).append(tmp)
					.toString());
		}
		  
		public  void debug(String logPoint, Object args1, Object args2) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_BUSINESS, logPoint, this.getInstance(), this.getModule());
			logger.debug(new StringBuilder().append(getRegular(null)).append(tmp)
					.toString(),args1 ,args1);
		}
		
		public  void debug(String logPoint, Object ... args) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_BUSINESS, logPoint, this.getInstance(), this.getModule());
			logger.debug(new StringBuilder().append(getRegular(null)).append(tmp)
					.toString(),args);
		}
		
		public  void debug(String logPoint, Throwable t) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_BUSINESS, logPoint, this.getInstance(), this.getModule());
			if (t == null) {
				t = new Throwable();
			}
			logger.debug(new StringBuilder().append(getRegular(t)).append(
					tmp).toString(), t);
		}
		
		public  void info(String logPoint) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_INFO, logPoint, this.getInstance(), this.getModule());
			logger.info(new StringBuilder().append(getRegular(null)).append(tmp)
					.toString());
		}
		
		public  void info(String logPoint, Object args1, Object args2) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_INFO, logPoint, this.getInstance(), this.getModule());
			logger.info(new StringBuilder().append(getRegular(null)).append(tmp)
					.toString(),args1 ,args1);
		}
		
		public  void info(String logPoint, Object... args) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_INFO, logPoint, this.getInstance(), this.getModule());
			logger.info(new StringBuilder().append(getRegular(null)).append(tmp)
					.toString(),args);
		}
		
		public  void info(String logPoint, Throwable t) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_INFO, logPoint, this.getInstance(), this.getModule());
			if (t == null) {
				t = new Throwable();
			}
			logger.info(new StringBuilder().append(getRegular(t)).append(
					tmp).toString(), t);
		}
		
		
		public  void succ(String logPoint) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_SUCC, logPoint, this.getInstance(), this.getModule());
			logger.info(new StringBuilder().append(getRegular(null)).append(tmp)
					.toString());
		}
		
		public  void succ(String logPoint, Object args1, Object args2) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_SUCC, logPoint, this.getInstance(), this.getModule());
			logger.info(new StringBuilder().append(getRegular(null)).append(tmp)
					.toString(),args1 ,args1);
		}
		
		public  void succ(String logPoint, Object... args) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_SUCC, logPoint, this.getInstance(), this.getModule());
			logger.info(new StringBuilder().append(getRegular(null)).append(tmp)
					.toString(),args);
		}

		public  void succ(String logPoint, Throwable t) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_SUCC, logPoint, this.getInstance(), this.getModule());
			if (t == null) {
				t = new Throwable();
			}
			logger.info(new StringBuilder().append(getRegular(t)).append(
					tmp).toString(), t);
		}
		
		
		
		public  void warn(String logPoint) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_WARNING, logPoint, this.getInstance(), this.getModule());
			logger.warn(new StringBuilder().append(getRegular(null)).append(
					tmp).toString());
		}

		public  void warn(String logPoint, Object args1, Object args2) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_WARNING, logPoint, this.getInstance(), this.getModule());
			logger.warn(new StringBuilder().append(getRegular(null)).append(tmp)
					.toString(),args1 ,args1);
		}
		
		public  void warn(String logPoint, Object... args) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_WARNING, logPoint, this.getInstance(), this.getModule());
			logger.warn(new StringBuilder().append(getRegular(null)).append(tmp)
					.toString(),args);
		}
		
		public  void warn(String logPoint, Throwable t) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_WARNING, logPoint, this.getInstance(), this.getModule());
			if (t == null) {
				t = new Throwable();
			}
			logger.warn(new StringBuilder().append(getRegular(t)).append(
					tmp).toString(), t);
		}
		

		public  void error(String logPoint) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_SERIOUS, logPoint, this.getInstance(), this.getModule());
			logger.error(tmp);
		}
		
		public  void error(String logPoint, Object args1, Object args2) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_SERIOUS, logPoint, this.getInstance(), this.getModule());
			logger.error(new StringBuilder().append(getRegular(null)).append(tmp)
					.toString(),args1 ,args1);
		}
		
		public  void error(String logPoint, Object... args) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_SERIOUS, logPoint, this.getInstance(), this.getModule());
			logger.error(new StringBuilder().append(getRegular(null)).append(tmp)
					.toString(),args);
		}

		public  void error(String logPoint, Throwable t) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_SERIOUS, logPoint, this.getInstance(), this.getModule());
			if (t == null) {
				t = new Throwable();
			}
			logger.error(new StringBuilder().append(getRegular(t)).append(
					tmp).toString(), t);
		}
		
		
		@Deprecated
		public  void serious(String logPoint, Exception ex) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_SERIOUS, logPoint, this.getInstance(), this.getModule());
			Throwable t = ex;
			logger.error(tmp, t);
		}
		@Deprecated
		public  void alarm(String logPoint) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_ALARM, logPoint, this.getInstance(), this.getModule());
			logger.warn(new StringBuilder().append(getRegular(null)).append(
					tmp).toString());
		}
		@Deprecated
		public  void alarm(String logPoint, Throwable t) {
			String tmp = convMessage(ExcConstant.LOG_LEVEL_ALARM, logPoint, this.getInstance(), this.getModule());
			if (t == null) {
				t = new Throwable();
			}
			logger.warn(new StringBuilder().append(getRegular(t)).append(
					tmp).toString(), t);
		}
		
		
		private String convMessage(final String p_loglevel ,final String p_logmsg, final String p_instance, final String p_module) {

	    	StringBuilder sb_tmp = new StringBuilder();
	    	//...........固定数据项部分
	        sb_tmp.append("BL#");
	        sb_tmp.append("#");
	        sb_tmp.append(p_loglevel);
	        sb_tmp.append("#");
	        sb_tmp.append(DateUtil.getDateyyyyMMddHHmmss());
	        sb_tmp.append("#");

	        	String hostip = HostMsgCache.getHostIP();
				sb_tmp.append(hostip);
			
//	        sb_tmp.append("hostname");
	        sb_tmp.append("#");
	        sb_tmp.append(APP_ID);
	        sb_tmp.append("#");
	        sb_tmp.append(CORE_SUB_SYS);
	        sb_tmp.append("#");
	        sb_tmp.append(p_module);
	        sb_tmp.append("#");
	        sb_tmp.append(p_instance);
	        sb_tmp.append("#");
	        sb_tmp.append("");
	        sb_tmp.append("#");
	        sb_tmp.append("");
	        sb_tmp.append("#");
	        sb_tmp.append("");
	        sb_tmp.append("#");
	        sb_tmp.append("");
	        sb_tmp.append("#");
	        sb_tmp.append("");
	        sb_tmp.append("#");
	        
	        
	        //...........业务数据项
	        
	        sb_tmp.append("");
	        sb_tmp.append("#");
	        sb_tmp.append("msg_cont:");
	        sb_tmp.append(p_logmsg);
	        sb_tmp.append("#");
	        sb_tmp.append("#LB");

	        return sb_tmp.toString().replaceAll("(\r\n|\r|\n|\n\r)", "<br>");  
	    }

		
		public String getInstance() {
			return instance;
		}


		public void setInstance(String instance) {
			this.instance = instance;
		}


		public String getModule() {
			return module;
		}

		public void setModule(String module) {
			this.module = module;
		}


		public String[] getArgs() {
			return args;
		}


		public void setArgs(String[] args) {
			this.args = args;
		}

		

		
		
		
		
		
//	    private String getStackTraceString(final Throwable p_cause) {
//	        if (p_cause == null) {
//	            return "";
//	        }
//	        StringBuffer sb = new StringBuffer();
//	        sb.append(p_cause);
	//
//	      
//	        StackTraceElement[] trace = p_cause.getStackTrace();
//	        for (int i = 0; trace != null && i < trace.length; i++) {
//	            sb.append("\n\tat ");
//	            sb.append(trace[i]);
//	        }
//	       
//	        Throwable ourCause = p_cause.getCause();
//	        if (ourCause != null) {
//	            sb.append("\n");
//	            sb.append(getStackTraceString(ourCause));
//	        }
//	        return sb.toString();
//	    }
		
		
		
		
    
}
