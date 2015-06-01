/**
 * 
 */
package com.toolbox.util;

import java.lang.reflect.Field;

import org.apache.commons.beanutils.PropertyUtils;

import com.toolbox.annotation.BeanCopyAllowNull;
import com.toolbox.annotation.BeanCopyIgnore;

/**
 * Bean属性操作
 * @author cmt
 *
 */
public class BeanUtil {

	/**
	 * COPY2个对象的值,2对象必须是同一个类
	 * @author cmt
	 * @param obj1:目标对象
	 * @param obj2:被复制对象
	 * @throws Exception
	 */
	public static void copyProperty(Object obj1,Object obj2) throws Exception{
		Class<?> c= obj2.getClass();
		Field[] fields =c.getDeclaredFields();
		for(Field field:fields){
			String fieldName=field.getName();
			if (field.isAnnotationPresent(BeanCopyIgnore.class))
				continue;
			Object valueObject= PropertyUtils.getProperty(obj2,fieldName);
			if (valueObject!=null){
				PropertyUtils.setProperty(obj1, fieldName, valueObject);
			}else if (field.isAnnotationPresent(BeanCopyAllowNull.class))
				PropertyUtils.setProperty(obj1, fieldName, valueObject);
		}
	}
	
	
	/**
	 * COPY 第一个对象 中存在属性的值。2对象可以不同类
	 * @author cmt
	 * @param obj1 :目标对象
	 * @param obj2:被复制对象
	 * @throws Exception
	 */
	public static void copyFixedProperty(Object obj1,Object obj2) throws Exception{
		Class<?> c= obj2.getClass();
		Field[] fields =c.getDeclaredFields();
		for(Field field:fields){
			String fieldName=field.getName();
			if (field.isAnnotationPresent(BeanCopyIgnore.class))
				continue;
			Object valueObject= PropertyUtils.getProperty(obj2,fieldName);
			try{
				if (valueObject!=null){
					PropertyUtils.setProperty(obj1, fieldName, valueObject);
				}else if (field.isAnnotationPresent(BeanCopyAllowNull.class))
					PropertyUtils.setProperty(obj1, fieldName, valueObject);
			}catch (Exception e) {
			}
		}
	}
}
