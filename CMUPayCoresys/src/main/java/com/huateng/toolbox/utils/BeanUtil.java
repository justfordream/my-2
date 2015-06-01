/**
 * 
 */
package com.huateng.toolbox.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.toolbox.annotation.BeanCopyAllowNull;
import com.huateng.toolbox.annotation.BeanCopyIgnore;

/**
 * Bean属性操作
 * 
 * @author cmt
 * 
 */
public class BeanUtil {

	protected static final Logger logger = LoggerFactory
			.getLogger(BeanUtil.class);

	/**
	 * COPY2个对象的值,2对象必须是同一个类
	 * 
	 * @author cmt
	 * @param obj1
	 *            :目标对象
	 * @param obj2
	 *            :被复制对象
	 * @throws Exception
	 */
	public static void copyProperty(Object obj1, Object obj2) throws Exception {
		Class<?> c = obj2.getClass();
		Field[] fields = c.getDeclaredFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			if (field.isAnnotationPresent(BeanCopyIgnore.class))
				continue;
			Object valueObject = PropertyUtils.getProperty(obj2, fieldName);
			if (valueObject != null) {
				PropertyUtils.setProperty(obj1, fieldName, valueObject);
			} else if (field.isAnnotationPresent(BeanCopyAllowNull.class))
				PropertyUtils.setProperty(obj1, fieldName, valueObject);
		}
	}

	/**
	 * COPY 第一个对象 中存在属性的值。2对象可以不同类
	 * 
	 * @author cmt
	 * @param obj1
	 *            :目标对象
	 * @param obj2
	 *            :被复制对象
	 * @throws Exception
	 */
	public static void copyFixedProperty(Object obj1, Object obj2)
			throws Exception {
		Class<?> c = obj2.getClass();
		Field[] fields = c.getDeclaredFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			if (field.isAnnotationPresent(BeanCopyIgnore.class))
				continue;
			Object valueObject = PropertyUtils.getProperty(obj2, fieldName);
			try {
				if (valueObject != null) {
					PropertyUtils.setProperty(obj1, fieldName, valueObject);
				} else if (field.isAnnotationPresent(BeanCopyAllowNull.class))
					PropertyUtils.setProperty(obj1, fieldName, valueObject);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 
	 * 转换时对map中的key里的字符串会做忽略处理的正则串
	 */

	private static final String OMIT_REG = "_";

	/**
	 * 
	 * 将map集合转换成Bean集合，Bean的属性名与map的key值对应时不区分大小写，并对map中key做忽略OMIT_REG正则处理
	 * 
	 * @param <E>
	 * @param cla
	 * @param mapList
	 * @return
	 */

	public static <E> List<E> toBeanList(Class<E> cla,
			List<Map<String, Object>> mapList) {
		List<E> list = new ArrayList<E>(mapList.size());
		for (Map<String, Object> map : mapList) {
			E obj = toBean(cla, map);
			list.add(obj);
		}
		return list;
	}

	/**
	 * 将map转换成Bean，Bean的属性名与map的key值对应时不区分大小写，并对map中key做忽略OMIT_REG正则处理
	 * 
	 * @param <E>
	 * @param cla
	 * @param map
	 * @return
	 */

	@SuppressWarnings({ "rawtypes" })
	public static <E> E toBean(Class<E> cla, Map<String, Object> map) {
		// 创建对象
		E obj = null;
		try {
			obj = cla.newInstance();
			if (obj == null) {
				throw new Exception();
			}
		} catch (Exception e) {
			logger.error("类型实例化对象失败,类型:" + cla);
			return null;
		}
		// 处理map的key
		Map<String, Object> newmap = new HashMap<String, Object>();
		for (Map.Entry<String, Object> en : map.entrySet()) {
			Object value = en.getValue();
			if(value instanceof BigDecimal){
				value = ((BigDecimal) value).longValue();
			}
			newmap.put("set"+ en.getKey().trim().replaceAll(OMIT_REG, "").toLowerCase(), 
					value);
		}
		// 进行值装入
		Method[] ms = cla.getMethods();
		for (Method method : ms) {
			String mname = method.getName().toLowerCase();
			if (mname.startsWith("set")) {
				Class[] clas = method.getParameterTypes();
				Object v = newmap.get(mname);
				if (v != null && clas.length == 1) {
					try {
						method.invoke(obj, v);
					} catch (Exception e) {
						logger.error("属性值装入失败,装入方法：" + cla + "."
								+ method.getName() + ".可装入类型" + clas[0]
								+ ";欲装入值的类型:" + v.getClass());
						// throw new RuntimeException(e);
					}
				}
			}
		}
		return obj;

	}
}
