package com.huateng.cmupay.controller.intercepter;

import org.apache.commons.beanutils.PropertyUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.huateng.cmupay.annotation.MyLogAction;
import com.huateng.cmupay.constant.LogTypeConstant;

/**
 * 记录日志（拦截器）
 * 
 * @author cmt
 * 
 */

//@Component("logIntercepter")
//@Aspect
public class LogIntercepter {

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 设置 切入点
	 * 
	 * @author cmt
	 * @param logAction
	 */
	@SuppressWarnings("unused")
	@Pointcut("execution(* com.huateng.cmupay.controller.service.system..*.*(..)) && @annotation(logAction)")
	private void logAnnotationMethod(MyLogAction logAction) {
	}

	/**
	 * 正常日志记录
	 * 
	 * @author cmt
	 * @param joinPoint
	 * @param logAction
	 * @param result
	 */
	@AfterReturning(pointcut = "logAnnotationMethod(logAction)")
	public void doAfterReturning(JoinPoint joinPoint, MyLogAction logAction) {

		String logDesc = logAction.logDesc();
		String fieldNames = logAction.fieldName();
		StringBuffer sb = new StringBuffer();
		sb.append(logDescFormat(logAction.logType(), logDesc, fieldNames,
				joinPoint));
	
		logger.info(sb.toString());
		

	}

	/**
	 * 遇到异常操作
	 * 
	 * @author cmt
	 */
	/*@AfterThrowing(pointcut = "logAnnotationMethod(logAction)", throwing = "ex")
	public void exception(JoinPoint joinPoint, MyLogAction logAction,
			Exception ex) {
		logger.error(logAction.logType().getValue(), ex);

		String logDesc = logAction.logDesc();
		String fieldNames = logAction.fieldName();
		StringBuffer sb = new StringBuffer();
		sb.append(
				logDescFormat(logAction.logType(), logDesc, fieldNames,
						joinPoint)).append(",异常描述：").append(ex.getMessage());

		try {
			logger.error(sb.toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}*/

	/**
	 * 格式化日志描述
	 * 
	 * @author cmt
	 * @param logType
	 * @param logDesc
	 * @param fieldNames
	 * @param joinPoint
	 * @return
	 */
	private String logDescFormat(LogTypeConstant logType, String logDesc,
			String fieldNames, JoinPoint joinPoint) {

		if (fieldNames != null && !fieldNames.equals("")) {
			String[] fieldName = fieldNames.split(",");
			Object obj = joinPoint.getArgs()[0];
			if (obj.getClass().isArray()) {
				StringBuilder sb = new StringBuilder();

				Object[] beans = (Object[]) obj;
				for (Object o : beans) {
					try {
						String temp = logDesc;
						for (String file : fieldName) {

							Object val = PropertyUtils.getProperty(o, file);
							temp = temp.replaceAll("\\$" + file,
									String.valueOf(val));

						}
						sb.append(temp);
						sb.append(",");
					} catch (Exception e) {
						logger.error("logDesc format error:{}",new Object[]{e.getMessage()});
					}
				}
				if (sb.length() > 0)
					sb.deleteCharAt(sb.length() - 1);
				logDesc = sb.toString();
			} else {
				for (String filed : fieldName) {
					try {
						Object val = PropertyUtils.getProperty(obj, filed);
						logDesc = logDesc.replaceAll("\\$" + filed,
								String.valueOf(val));
					} catch (Exception e) {
						logger.error("logDesc format error:{}",new Object[]{e.getMessage()});
					}
				}
			}
		}

		return logDesc;
	}
}
