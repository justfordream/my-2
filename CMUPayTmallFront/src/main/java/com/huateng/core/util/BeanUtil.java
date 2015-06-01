package com.huateng.core.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Gary
 * 
 */
public class BeanUtil {
	private static Logger logger = LoggerFactory.getLogger("BeanUtil");
	/**
	 * map转换为bean
	 * 
	 * @param map
	 * @param cls
	 * @return
	 */
	public static <T> T mapToBean(Map<?, ?> map, Class<T> cls) {
		T bean = null;
		try {
			 bean = cls.newInstance();
			BeanUtils.populate(bean, map);
		} catch (InstantiationException e) {
			logger.error("", e);
		} catch (IllegalAccessException e) {
			logger.error("", e);
		} catch (InvocationTargetException e) {
			logger.error("", e);
		}
		return bean;
	}
}
