package com.huateng.toolbox.json;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;



/**
 * @author cmt
 *
 */
public class JsonUtils {
	
    private Map<String, Object> jsonMap = new HashMap<String, Object>();  
    private static SimpleDateFormat formatter = new SimpleDateFormat(  
            "yyyy-MM-dd");  
    public void clear() {  
        jsonMap.clear();  
    }  
    
    /**
     * 将一个实体类对象转换成Json数据格式
     * 
     * @param bean
     *            需要转换的实体类对象
     * @return 转换后的Json格式字符串
     */
    public static String beanToJson(Object bean) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        PropertyDescriptor[] props = null;
        try {
            props = Introspector.getBeanInfo(bean.getClass(), Object.class)
                    .getPropertyDescriptors();
        } catch (IntrospectionException e) {
        }
        if (props != null) {
            for (int i = 0; i < props.length; i++) {
                try {
                    String name = objectToJson(props[i].getName());
                    String value = objectToJson(props[i].getReadMethod()
                            .invoke(bean));
                    json.append(name);
                    json.append(":");
                    json.append(value);
                    json.append(",");
                } catch (Exception e) {
                }
            }
            json.setCharAt(json.length() - 1, '}');
        } else {
            json.append("}");
        }
        return json.toString();
    }

    /**
     * 将一个List对象转换成Json数据格式返回
     * 
     * @param list
     *            需要进行转换的List对象
     * @return 转换后的Json数据格式字符串
     */
    public static String listToJson(List<?> list) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (list != null && list.size() > 0) {
            for (Object obj : list) {
                json.append(objectToJson(obj));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }
        return json.toString();
    }

    /**
     * 将一个对象数组转换成Json数据格式返回
     * 
     * @param array
     *            需要进行转换的数组对象
     * @return 转换后的Json数据格式字符串
     */
    public static String arrayToJson(Object[] array) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (array != null && array.length > 0) {
            for (Object obj : array) {
                json.append(objectToJson(obj));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }
        return json.toString();
    }

    /**
     * 将一个Map对象转换成Json数据格式返回
     * 
     * @param map
     *            需要进行转换的Map对象
     * @return 转换后的Json数据格式字符串
     */
    public static String mapToJson(Map<?, ?> map) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        if (map != null && map.size() > 0) {
            for (Object key : map.keySet()) {
                json.append(objectToJson(key));
                json.append(":");
                json.append(objectToJson(map.get(key)));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, '}');
        } else {
            json.append("}");
        }
        return json.toString();
    }

    /**
     * 将一个Set对象转换成Json数据格式返回
     * 
     * @param set
     *            需要进行转换的Set对象
     * @return 转换后的Json数据格式字符串
     */
    public static String setToJson(Set<?> set) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (set != null && set.size() > 0) {
            for (Object obj : set) {
                json.append(objectToJson(obj));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }
        return json.toString();
    }

    private static String numberToJson(Number number) {
        return number.toString();
    }

    private static String booleanToJson(Boolean bool) {
        return bool.toString();
    }

    private static String nullToJson() {
        return "";
    }

    private static String stringToJson(String s) {
        if (s == null) {
            return nullToJson();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
            case '"':
                sb.append("\\\"");
                break;
            case '\\':
                sb.append("\\\\");
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\r':
                sb.append("\\r");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '/':
                sb.append("\\/");
                break;
            default:
                if (ch >= '\u0000' && ch <= '\u001F') {
                    String ss = Integer.toHexString(ch);
                    sb.append("\\u");
                    for (int k = 0; k < 4 - ss.length(); k++) {
                        sb.append('0');
                    }
                    sb.append(ss.toUpperCase());
                } else {
                    sb.append(ch);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 将对象 转换成指定的Json格式的字符串返回
     * 
     * @param obj
     *            传入的对象
     * @return 转换后的Json格式的字符串
     */
    private static String objectToJson(Object obj) {
        StringBuilder json = new StringBuilder();
        if (obj == null) {
            json.append("\"\"");
        } else if (obj instanceof Number) {
            json.append(numberToJson((Number) obj));
        } else if (obj instanceof Boolean) {
            json.append(booleanToJson((Boolean) obj));
        } else if (obj instanceof String) {
            json.append("\"").append(stringToJson(obj.toString())).append("\"");
        } else if (obj instanceof Object[]) {
            json.append(arrayToJson((Object[]) obj));
        } else if (obj instanceof List) {
            json.append(listToJson((List<?>) obj));
        } else if (obj instanceof Map) {
            json.append(mapToJson((Map<?, ?>) obj));
        } else if (obj instanceof Set) {
            json.append(setToJson((Set<?>) obj));
        } else {
            json.append(beanToJson(obj));
        }
        return json.toString();
    }

    // ============================================================================================

    /**
     * 将Json格式的字符串转换成指定的对象返回
     * 
     * @param jsonString
     *            Json格式的字符串
     * @param pojoCalss
     *            转换后的对象类型
     * @return 转换后的对象
     */

    public static Object json2Object(String jsonString, Class<?> pojoCalss) {
        Object pojo;
        JSONObject jsonObject = JSONObject.fromObject(jsonString);
        pojo = JSONObject.toBean(jsonObject, pojoCalss);
        return pojo;
    }
    
 

    /**
     * 将Json格式的字符串转换成Map<String,Object>对象返回
     * 
     * @param jsonString
     *            需要进行转换的Json格式字符串
     * @return 转换后的Map<String,Object>对象
     */
    public static Map<String, Object> json2Map(String jsonString) {
        JSONObject jsonObject = JSONObject.fromObject(jsonString);
        Iterator<?> keyIter = jsonObject.keys();
        String key;
        Object value;
        Map<String, Object> valueMap = new HashMap<String, Object>();
        while (keyIter.hasNext()) {
            key = (String) keyIter.next();
            value = jsonObject.get(key);
            valueMap.put(key, value);
        }
        return valueMap;
    }

    /**
     * 将Json格式的字符串转换成对象数组返回
     * 
     * @param jsonString
     *            需要进行转换的Json格式字符串
     * @return 转换后的对象数组
     */
    public static Object[] json2ObjectArray(String jsonString) {
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        return jsonArray.toArray();
    }

    /**
     * 将Json格式的字符串转换成指定对象组成的List返回
     * 
     * @param jsonString
     *            Json格式的字符串
     * @param pojoClass
     *            转换后的List中对象类型
     * @return 转换后的List对象
     */

    public static List<Object> json2List(String jsonString, Class<?> pojoClass) {
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        JSONObject jsonObject;
        Object pojoValue;
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < jsonArray.size(); i++) {
            jsonObject = jsonArray.getJSONObject(i);
            pojoValue = JSONObject.toBean(jsonObject, pojoClass);
            list.add(pojoValue);
        }
        return list;
    }

    /**
     * 将Json格式的字符串转换成字符串数组返回
     * 
     * @param jsonString
     *            需要进行转换的Json格式字符串
     * @return 转换后的字符串数组
     */
    public static String[] json2StringArray(String jsonString) {
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        String[] stringArray = new String[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            stringArray[i] = jsonArray.getString(i);
        }
        return stringArray;
    }

    /**
     * 将Json格式的字符串转换成Long数组返回
     * 
     * @param jsonString
     *            需要进行转换的Json格式字符串
     * @return 转换后的Long数组
     */
    public static Long[] json2LongArray(String jsonString) {
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        Long[] longArray = new Long[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            longArray[i] = jsonArray.getLong(i);
        }
        return longArray;
    }

    /**
     * 将Json格式的字符串转换成Integer数组返回
     * 
     * @param jsonString
     *            需要进行转换的Json格式字符串
     * @return 转换后的Integer数组
     */
    public static Integer[] json2IntegerArray(String jsonString) {
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        Integer[] integerArray = new Integer[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            integerArray[i] = jsonArray.getInt(i);
        }
        return integerArray;
    }

    /**
     * 将Json格式的字符串转换成日期数组返回
     * 
     * @param jsonString
     *            需要进行转换的Json格式字符串
     * @param DataFormat
     *            返回的日期格式
     * @return 转换后的日期数组
     */
    public static Date[] json2DateArray(String jsonString, String DataFormat) {
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        Date[] dateArray = new Date[jsonArray.size()];
      /*  String dateString;
        Date date;
        for (int i = 0; i < jsonArray.size(); i++) {
            dateString = jsonArray.getString(i);
            date = DateUtil.parseDate(dateString, DataFormat);
            dateArray[i] = date;

        }*/
        return dateArray;
    }

    /**
     * 将Json格式的字符串转换成Double数组返回
     * 
     * @param jsonString
     *            需要进行转换的Json格式字符串
     * @return 转换后的Double数组
     */
    public static Double[] json2DoubleArray(String jsonString) {
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        Double[] doubleArray = new Double[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            doubleArray[i] = jsonArray.getDouble(i);
        }
        return doubleArray;
    }
    
    /**
     * 将Json格式的字符串转换成List{ List<Map<String, Object>>}类型。
     * @param jsonStr
     * @return
     */
    @SuppressWarnings("unchecked")
	public static List<Map<String, Object>> parseJSON2List(String jsonStr){
        JSONArray jsonArr = JSONArray.fromObject(jsonStr);
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        Iterator<JSONObject> it = jsonArr.iterator();
        while(it.hasNext()){
            JSONObject json2 = it.next();
            list.add(parseJSON2Map(json2.toString()));
        }
        return list;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> parseJSON2Map(String jsonStr){
        Map<String, Object> map = new HashMap<String, Object>();
        //最外层解析
        JSONObject json = JSONObject.fromObject(jsonStr);
        for(Object k : json.keySet()){
            Object v = json.get(k); 
            //如果内层还是数组的话，继续解析
            if(v instanceof JSONArray){
                List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
                Iterator<JSONObject> it = ((JSONArray)v).iterator();
                while(it.hasNext()){
                    JSONObject json2 = it.next();
                    list.add(parseJSON2Map(json2.toString()));
                }
                map.put(k.toString(), list);
            } else {
                map.put(k.toString(), v);
            }
        }
        return map;
    }

    /**
     * 通过HTTP获取JSON数据
     * @param url
     * @return
     */
    public static List<Map<String, Object>> getListByUrl(String url){
        try {
            //通过HTTP获取JSON数据
            InputStream in = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line=reader.readLine())!=null){
                sb.append(line);
            }
            return parseJSON2List(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
   
    public static Map<String, Object> getMapByUrl(String url){
        try {
            //通过HTTP获取JSON数据
            InputStream in = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line=reader.readLine())!=null){
                sb.append(line);
            }
            return parseJSON2Map(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String object2json(Object obj) {    
        StringBuilder json = new StringBuilder();    
        if (obj == null) {    
          json.append("\"\"");    
        } else if (obj instanceof String ||  
             obj instanceof Integer ||  
             obj instanceof Float  ||  
             obj instanceof Boolean ||  
             obj instanceof Short ||  
             obj instanceof Double ||   
             obj instanceof Long ||  
             obj instanceof BigDecimal ||  
             obj instanceof BigInteger ||   
             obj instanceof Byte) {    
          json.append("\"").append(string2json(obj.toString())).append("\"");    
        } else if (obj instanceof Object[]) {    
          json.append(array2json((Object[]) obj));    
        } else if (obj instanceof List<?>) {    
          json.append(list2json((List<?>) obj));    
        } else if (obj instanceof Map<?,?>) {    
          json.append(map2json((Map<?, ?>) obj));    
        } else if (obj instanceof Set<?>) {    
          json.append(set2json((Set<?>) obj));    
        } else {    
          json.append(bean2json(obj));    
        }    
        return json.toString();    
      }    
       
         
      public static String bean2json(Object bean) {    
        StringBuilder json = new StringBuilder();    
        json.append("{");    
        PropertyDescriptor[] props = null;    
        try {    
          props = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();    
        } catch (IntrospectionException e) {}    
        if (props != null) {    
          for (int i = 0; i < props.length; i++) {    
            try {    
              String name = object2json(props[i].getName());    
              String value = object2json(props[i].getReadMethod().invoke(bean));    
              json.append(name);    
              json.append(":");    
              json.append(value);    
              json.append(",");    
            } catch (Exception e) {}    
          }    
          json.setCharAt(json.length() - 1, '}');    
        } else {    
          json.append("}");    
        }    
        return json.toString();    
      }    
       
         
      public static String list2json(List<?> list) {    
        StringBuilder json = new StringBuilder();    
        json.append("[");    
        if (list != null && list.size() > 0) {    
          for (Object obj : list) {    
            json.append(object2json(obj));    
            json.append(",");    
          }    
          json.setCharAt(json.length() - 1, ']');    
        } else {    
          json.append("]");    
        }    
        return json.toString();    
      }    
       
         
      public static String array2json(Object[] array) {    
        StringBuilder json = new StringBuilder();    
        json.append("[");    
        if (array != null && array.length > 0) {    
          for (Object obj : array) {    
            json.append(object2json(obj));    
            json.append(",");    
          }    
          json.setCharAt(json.length() - 1, ']');    
        } else {    
          json.append("]");    
        }    
        return json.toString();    
      }    
       
         
      public static String map2json(Map<?, ?> map) {    
        StringBuilder json = new StringBuilder();    
        json.append("{");    
        if (map != null && map.size() > 0) {    
          for (Object key : map.keySet()) {    
            json.append(object2json(key));    
            json.append(":");    
            json.append(object2json(map.get(key)));    
            json.append(",");    
          }    
          json.setCharAt(json.length() - 1, '}');    
        } else {    
          json.append("}");    
        }    
        return json.toString();    
      }    
       
         
      public static String set2json(Set<?> set) {    
        StringBuilder json = new StringBuilder();    
        json.append("[");    
        if (set != null && set.size() > 0) {    
          for (Object obj : set) {    
            json.append(object2json(obj));    
            json.append(",");    
          }    
          json.setCharAt(json.length() - 1, ']');    
        } else {    
          json.append("]");    
        }    
        return json.toString();    
      }    
       
         
      public static String string2json(String s) {    
        if (s == null)    
          return "";    
        StringBuilder sb = new StringBuilder();    
        for (int i = 0; i < s.length(); i++) {    
          char ch = s.charAt(i);    
          switch (ch) {    
          case '"':    
            sb.append("\\\"");    
            break;    
          case '\\':    
            sb.append("\\\\");    
            break;    
          case '\b':    
            sb.append("\\b");    
            break;    
          case '\f':    
            sb.append("\\f");    
            break;    
          case '\n':    
            sb.append("\\n");    
            break;    
          case '\r':    
            sb.append("\\r");    
            break;    
          case '\t':    
            sb.append("\\t");    
            break;    
          case '/':    
            sb.append("\\/");    
            break;    
          default:    
            if (ch >= '\u0000' && ch <= '\u001F') {    
              String ss = Integer.toHexString(ch);    
              sb.append("\\u");    
              for (int k = 0; k < 4 - ss.length(); k++) {    
                sb.append('0');    
              }    
              sb.append(ss.toUpperCase());    
            } else {    
              sb.append(ch);    
            }    
          }    
        }    
        return sb.toString();    
      }      
          

      /** 
       * 添加元素 <br/> 
       * 作者：wallimn　时间：2009-2-5　下午02:00:03<br/> 
       * 邮件：wallimn@sohu.com<br/> 
       * 博客：http://blog.csdn.net/wallimn<br/> 
       * 参数：<br/> 
       *  
       * @param key 
       * @param value 
       *            　支持简单类型（即原生类型的包装器类）、bean对象、List<Object>、Map<String,Object>以及数组 
       * @return 
       */  
      public Map<String, Object> put(String key, Object value) {  
          jsonMap.put(key, value);  
          return jsonMap;  
      }  
      // 判断是否要加引号   
      private static boolean isNoQuote(Object value) {  
          return (value instanceof Integer || value instanceof Boolean  
                  || value instanceof Double || value instanceof Float  
                  || value instanceof Short || value instanceof Long || value instanceof Byte);  
      }  
      private static boolean isQuote(Object value) {  
          return (value instanceof String || value instanceof Character);  
      }  
      @SuppressWarnings("unchecked")  
      @Override  
      /* 
       * 返回形如{'apple':'red','lemon':'yellow'}的字符串 
       */  
      public String toString() {  
          StringBuffer sb = new StringBuffer();  
          sb.append("{");  
          Set<Entry<String, Object>> set = jsonMap.entrySet();  
          for (Entry<String, Object> entry : set) {  
              Object value = entry.getValue();  
              if (value == null) {  
                  continue;// 对于null值，不进行处理，页面上的js取不到值时也是null   
              }  
              sb.append("'").append(entry.getKey()).append("':");  
              if (value instanceof JsonUtils) {  
                  sb.append(value.toString());  
              } else if (isNoQuote(value)) {  
                  sb.append(value);  
              } else if (value instanceof Date) {  
                  sb.append("'").append(formatter.format(value)).append("'");  
              } else if (isQuote(value)) {  
                  sb.append("'").append(value).append("'");  
              } else if (value.getClass().isArray()) {  
                  sb.append(ArrayToStr((int[]) value));  
              } else if (value instanceof Map) {  
                  sb.append(fromObject((Map<String, Object>) value).toString());  
              } else if (value instanceof List) {  
                  sb.append(ListToStr((List<Object>) value));  
              } else {  
                  sb.append(fromObject(value).toString());  
              }  
              sb.append(",");  
          }  
          int len = sb.length();  
          if (len > 1) {  
              sb.delete(len - 1, len);  
          }  
          sb.append("}");  
          return sb.toString();  
      }  
      public static String ArrayToStr(Object array) {  
          if (!array.getClass().isArray())  
              return "[]";  
          StringBuffer sb = new StringBuffer();  
          sb.append("[");  
          int len = Array.getLength(array);  
          Object v = null;  
          for (int i = 0; i < len; i++) {  
              v = Array.get(array, i);  
              if (v instanceof Date) {  
                  sb.append("'").append(formatter.format(v)).append("'").append(  
                          ",");  
              } else if (isQuote(v)) {  
                  sb.append("'").append(v).append("'").append(",");  
              } else if (isNoQuote(v)) {  
                  sb.append(i).append(",");  
              } else {  
                  sb.append(fromObject(v)).append(",");  
              }  
          }  
          len = sb.length();  
          if (len > 1)  
              sb.delete(len - 1, len);  
          sb.append("]");  
          return sb.toString();  
      }  
      @SuppressWarnings("unchecked")  
      public static String ListToStr(List<Object> list) {  
          if (list == null)  
              return null;  
          StringBuffer sb = new StringBuffer();  
          sb.append("[");  
          Object value = null;  
          for (java.util.Iterator<Object> it = list.iterator(); it.hasNext();) {  
              value = it.next();  
              if (value instanceof Map) {  
                  sb.append(fromObject((Map<String, Object>) value).toString()).append(",");  
              } else if (isNoQuote(value)) {  
                  sb.append(value).append(",");  
              } else if (isQuote(value)) {  
                  sb.append("'").append(value).append("'").append(",");  
              } else {  
                  sb.append(fromObject(value).toString()).append(",");  
              }  
          }  
          int len = sb.length();  
          if (len > 1)  
              sb.delete(len - 1, len);  
          sb.append("]");  
          return sb.toString();  
      }  
      /** 
       * 从一个bean装载数据，返回一个JsonUtils对象。 <br/> 
       * 作者：wallimn　时间：2009-2-5　下午02:05:51<br/> 
       * 邮件：wallimn@sohu.com<br/> 
       * 博客：http://blog.csdn.net/wallimn<br/> 
       * 参数：<br/> 
       *  
       * @param object 
       * @return 
       */  
     
      public static JsonUtils fromObject(Object bean) {  
          JsonUtils json = new JsonUtils();  
          if (bean == null)  
              return json;  
          Class<? extends Object> cls = bean.getClass();  
          Field[] fs = cls.getDeclaredFields();  
          Object value = null;  
          String fieldName = null;  
          Method method = null;  
          int len = fs.length;  
          for (int i = 0; i < len; i++) {  
              fieldName = fs[i].getName();  
              try {  
                  method = cls.getMethod(getGetter(fieldName), (Class[]) null);  
                  value = method.invoke(bean, (Object[]) null);  
              } catch (Exception e) {  
                  // System.out.println(method.getName());   
                  // e.printStackTrace();   
                  continue;  
              }  
              json.put(fieldName, value);  
          }  
          return json;  
      }  
      /** 
       * 从Map中装载数据 <br/> 
       * 作者：wallimn　时间：2009-2-5　下午04:05:04<br/> 
       * 邮件：wallimn@sohu.com<br/> 
       * 博客：http://blog.csdn.net/wallimn<br/> 
       * 参数：<br/> 
       *  
       * @param map 
       * @return 
       */  
      public static JsonUtils fromObject(Map<String, Object> map) {  
          JsonUtils json = new JsonUtils();  
          if (map == null)  
              return json;  
          json.getMap().putAll(map);  
          return json;  
      }  
      private static String getGetter(String property) {  
          return "get" + property.substring(0, 1).toUpperCase()  
                  + property.substring(1, property.length());  
      }  
      public Map<String, Object> getMap() {  
          return this.jsonMap;  
      }  
      
      /** *//**
       * 从一个JSON 对象字符格式中得到一个java对象
       * @param jsonString
       * @param pojoCalss
       * @return
       */
      public static Object getObject4JsonString(String jsonString,Class<?> pojoCalss){
          Object pojo;
          JSONObject jsonObject = JSONObject.fromObject( jsonString );  
          pojo = JSONObject.toBean(jsonObject,pojoCalss);
          return pojo;
      }
      
      
      
      /** *//**
       * 从json HASH表达式中获取一个map，改map支持嵌套功能
       * @param jsonString
       * @return
       */
      public static Map<String, Object> getMap4Json(String jsonString){
          JSONObject jsonObject = JSONObject.fromObject( jsonString );  
          Iterator<?>  keyIter = jsonObject.keys();
          String key;
          Object value;
          Map<String, Object> valueMap = new HashMap<String, Object>();

          while( keyIter.hasNext())
          {
              key = (String)keyIter.next();
              value = jsonObject.get(key);
              valueMap.put(key, value);
          }
          
          return valueMap;
      }
      
      
      /** *//**
       * 从json数组中得到相应java数组
       * @param jsonString
       * @return
       */
      public static Object[] getObjectArray4Json(String jsonString){
          JSONArray jsonArray = JSONArray.fromObject(jsonString);
          return jsonArray.toArray();
          
      }

      /** *//**
       * 从json对象集合表达式中得到一个java对象列表
       * @param jsonString
       * @param pojoClass
       * @return
       */
      public static List<Object> getList4Json(String jsonString, Class<?> pojoClass){
          
          JSONArray jsonArray = JSONArray.fromObject(jsonString);
          JSONObject jsonObject;
          Object pojoValue;
          
          List<Object> list = new ArrayList<Object>();
          for ( int i = 0 ; i<jsonArray.size(); i++){
              
              jsonObject = jsonArray.getJSONObject(i);
              pojoValue = JSONObject.toBean(jsonObject,pojoClass);
              list.add(pojoValue);
              
          }
          return list;

      }
      
      /** *//**
       * 从json数组中解析出java字符串数组
       * @param jsonString
       * @return
       */
      public static String[] getStringArray4Json(String jsonString){
          
          JSONArray jsonArray = JSONArray.fromObject(jsonString);
          String[] stringArray = new String[jsonArray.size()];
          for( int i = 0 ; i<jsonArray.size() ; i++ ){
              stringArray[i] = jsonArray.getString(i);
              
          }
          
          return stringArray;
      }
      
      /** *//**
       * 从json数组中解析出javaLong型对象数组
       * @param jsonString
       * @return
       */
      public static Long[] getLongArray4Json(String jsonString){
          
          JSONArray jsonArray = JSONArray.fromObject(jsonString);
          Long[] longArray = new Long[jsonArray.size()];
          for( int i = 0 ; i<jsonArray.size() ; i++ ){
              longArray[i] = jsonArray.getLong(i);
              
          }
          return longArray;
      }
      
      /** *//**
       * 从json数组中解析出java Integer型对象数组
       * @param jsonString
       * @return
       */
      public static Integer[] getIntegerArray4Json(String jsonString){
          
          JSONArray jsonArray = JSONArray.fromObject(jsonString);
          Integer[] integerArray = new Integer[jsonArray.size()];
          for( int i = 0 ; i<jsonArray.size() ; i++ ){
              integerArray[i] = jsonArray.getInt(i);
              
          }
          return integerArray;
      }
      
   
      
      /** *//**
       * 从json数组中解析出java Integer型对象数组
       * @param jsonString
       * @return
       */
      public static Double[] getDoubleArray4Json(String jsonString){
          
          JSONArray jsonArray = JSONArray.fromObject(jsonString);
          Double[] doubleArray = new Double[jsonArray.size()];
          for( int i = 0 ; i<jsonArray.size() ; i++ ){
              doubleArray[i] = jsonArray.getDouble(i);
              
          }
          return doubleArray;
      }
      
      
      /** *//**
       * 将java对象转换成json字符串
       * @param javaObj
       * @return
       */
      public static String getJsonString4JavaPOJO(Object javaObj){
          
          JSONObject json;
          json = JSONObject.fromObject(javaObj);
          return json.toString();
          
      }      

      /***

       * 将List对象序列化为JSON文本

       */
      public static <T> String toJSONString(List<T> list){
          JSONArray jsonArray = JSONArray.fromObject(list);
          return jsonArray.toString();
      }
      
      /***
       * 将对象序列化为JSON文本
       * @param object
       * @return
       */
      public static String toJSONString(Object object){
          JSONArray jsonArray = JSONArray.fromObject(object);
          return jsonArray.toString();
      }

      /***
       * 将JSON对象数组序列化为JSON文本
       * @param jsonArray
       * @return
       */
      public static String toJSONString(JSONArray jsonArray){
          return jsonArray.toString();
      }
      /***
       * 将JSON对象序列化为JSON文本
       * @param jsonObject
       * @return
       */
      public static String toJSONString(JSONObject jsonObject){
          return jsonObject.toString();
      } 
      /***
       * 将对象转换为List对象
       * @param object
       * @return
       */
      @SuppressWarnings("unchecked")
	public static List<Object> toArrayList(Object object){
          List<Object> arrayList = new ArrayList<Object>();
          JSONArray jsonArray = JSONArray.fromObject(object);
          Iterator<JSONObject> it = jsonArray.iterator();
          while (it.hasNext()){
              JSONObject jsonObject = (JSONObject) it.next();
              Iterator<?> keys = jsonObject.keys();
              while (keys.hasNext()){
                  Object key = keys.next();
                  Object value = jsonObject.get(key);
                  arrayList.add(value);
              }
          }
          return arrayList;
      }
      /***
       * 将对象转换为Collection对象
       * @param object
       * @return
       */
      public static Collection<?> toCollection(Object object){
          JSONArray jsonArray = JSONArray.fromObject(object);
          return JSONArray.toCollection(jsonArray);
      }
      /***
       * 将对象转换为JSON对象数组
       * @param object
       * @return
       */
      public static JSONArray toJSONArray(Object object){
          return JSONArray.fromObject(object);
      }
      /***
       * 将对象转换为JSON对象
       * @param object
       * @return
       */
      public static JSONObject toJSONObject(Object object){
          return JSONObject.fromObject(object);
      }
      /***
       * 将对象转换为HashMap
       * @param object
       * @return
       */
      public static HashMap<String, Object> toHashMap(Object object){
          HashMap<String, Object> data = new HashMap<String, Object>();
          JSONObject jsonObject = JsonUtils.toJSONObject(object);
          Iterator<?> it = jsonObject.keys();
          while (it.hasNext()){
              String key = String.valueOf(it.next());
              Object value = jsonObject.get(key);
              data.put(key, value);
          }
          return data;
      }
      /***
       * 将对象转换为List<Map<String,Object>>
       * @param object
       * @return
       */
      // 返回非实体类型(Map<String,Object>)的List
      public static List<Map<String, Object>> toList(Object object){
          List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
          JSONArray jsonArray = JSONArray.fromObject(object);
          for (Object obj : jsonArray){
              JSONObject jsonObject = (JSONObject) obj;
              Map<String, Object> map = new HashMap<String, Object>();
              Iterator<?> it = jsonObject.keys();
              while (it.hasNext())
              {
                  String key = (String) it.next();
                  Object value = jsonObject.get(key);
                  map.put((String) key, value);
              }
              list.add(map);
          }
          return list;
      }
      /***
       * 将JSON对象数组转换为传入类型的List
       * @param <T>
       * @param jsonArray
       * @param objectClass
       * @return
       */
      @SuppressWarnings({ "unchecked", "deprecation" })
	public static <T> List<T> toList(JSONArray jsonArray, Class<T> objectClass){
          return JSONArray.toList(jsonArray, objectClass);
      }
      /***
       * 将对象转换为传入类型的List
       * @param <T>
       * @param jsonArray
       * @param objectClass
       * @return
       */
      @SuppressWarnings({ "unchecked", "deprecation" })
      public static <T> List<T> toList(Object object, Class<T> objectClass){
          JSONArray jsonArray = JSONArray.fromObject(object);
          return JSONArray.toList(jsonArray, objectClass);
      }
      /***
       * 将JSON对象转换为传入类型的对象
       * @param <T>
       * @param jsonObject
       * @param beanClass
       * @return
       */
      @SuppressWarnings("unchecked")
	public static <T> T toBean(JSONObject jsonObject, Class<T> beanClass){
          return (T) JSONObject.toBean(jsonObject, beanClass);
      }
      /***
       * 将将对象转换为传入类型的对象
       * @param <T>
       * @param object
       * @param beanClass
       * @return
       */
      @SuppressWarnings("unchecked")
	public static <T> T toBean(Object object, Class<T> beanClass){
          JSONObject jsonObject = JSONObject.fromObject(object);
          return (T) JSONObject.toBean(jsonObject, beanClass);
      }
      /***
       * 将JSON文本反序列化为主从关系的实体
       * @param <T> 泛型T 代表主实体类型
       * @param <D> 泛型D 代表从实体类型
       * @param jsonString JSON文本
       * @param mainClass 主实体类型
       * @param detailName 从实体类在主实体类中的属性名称
       * @param detailClass 从实体类型
       * @return
       */
      public static <T, D> T toBean(String jsonString, Class<T> mainClass,String detailName, Class<D> detailClass){
          JSONObject jsonObject = JSONObject.fromObject(jsonString);
          JSONArray jsonArray = (JSONArray) jsonObject.get(detailName);
          T mainEntity = JsonUtils.toBean(jsonObject, mainClass);
          List<D> detailList = JsonUtils.toList(jsonArray, detailClass);
          try
          {
              BeanUtils.setProperty(mainEntity, detailName, detailList);
          }
          catch (Exception ex)
          {
              throw new RuntimeException("主从关系JSON反序列化实体失败！");
          }
          return mainEntity;
      }
      /***
       * 将JSON文本反序列化为主从关系的实体
       * @param <T>泛型T 代表主实体类型
       * @param <D1>泛型D1 代表从实体类型
       * @param <D2>泛型D2 代表从实体类型
       * @param jsonString JSON文本
       * @param mainClass 主实体类型
       * @param detailName1 从实体类在主实体类中的属性
       * @param detailClass1 从实体类型
       * @param detailName2 从实体类在主实体类中的属性
       * @param detailClass2 从实体类型
       * @return
       */
      public static <T, D1, D2> T toBean(String jsonString, Class<T> mainClass,String detailName1, 
      		Class<D1> detailClass1, String detailName2, Class<D2> detailClass2){
          JSONObject jsonObject = JSONObject.fromObject(jsonString);
          JSONArray jsonArray1 = (JSONArray) jsonObject.get(detailName1);
          JSONArray jsonArray2 = (JSONArray) jsonObject.get(detailName2);
          T mainEntity = JsonUtils.toBean(jsonObject, mainClass);
          List<D1> detailList1 = JsonUtils.toList(jsonArray1, detailClass1);
          List<D2> detailList2 = JsonUtils.toList(jsonArray2, detailClass2);
          try
          {
              BeanUtils.setProperty(mainEntity, detailName1, detailList1);
              BeanUtils.setProperty(mainEntity, detailName2, detailList2);
          }
          catch (Exception ex)
          {
              throw new RuntimeException("主从关系JSON反序列化实体失败！");
          }
          return mainEntity;
      }
      /***
       * 将JSON文本反序列化为主从关系的实体
       * @param <T>泛型T 代表主实体类型
       * @param <D1>泛型D1 代表从实体类型
       * @param <D2>泛型D2 代表从实体类型
       * @param jsonString JSON文本
       * @param mainClass 主实体类型
       * @param detailName1 从实体类在主实体类中的属性
       * @param detailClass1 从实体类型
       * @param detailName2 从实体类在主实体类中的属性
       * @param detailClass2 从实体类型
       * @param detailName3 从实体类在主实体类中的属性
       * @param detailClass3 从实体类型
       * @return
       */
      public static <T, D1, D2, D3> T toBean(String jsonString,Class<T> mainClass, String detailName1, Class<D1> detailClass1,
              String detailName2, Class<D2> detailClass2, String detailName3,Class<D3> detailClass3){
          JSONObject jsonObject = JSONObject.fromObject(jsonString);
          JSONArray jsonArray1 = (JSONArray) jsonObject.get(detailName1);
          JSONArray jsonArray2 = (JSONArray) jsonObject.get(detailName2);
          JSONArray jsonArray3 = (JSONArray) jsonObject.get(detailName3);
          T mainEntity = JsonUtils.toBean(jsonObject, mainClass);
          List<D1> detailList1 = JsonUtils.toList(jsonArray1, detailClass1);
          List<D2> detailList2 = JsonUtils.toList(jsonArray2, detailClass2);
          List<D3> detailList3 = JsonUtils.toList(jsonArray3, detailClass3);
          try
          {
              BeanUtils.setProperty(mainEntity, detailName1, detailList1);
              BeanUtils.setProperty(mainEntity, detailName2, detailList2);
              BeanUtils.setProperty(mainEntity, detailName3, detailList3);
          }
          catch (Exception ex)
          {
              throw new RuntimeException("主从关系JSON反序列化实体失败！");
          }
          return mainEntity;
      }
      /***
       * 将JSON文本反序列化为主从关系的实体
       * @param <T> 主实体类型
       * @param jsonString JSON文本
       * @param mainClass 主实体类型
       * @param detailClass 存放了多个从实体在主实体中属性名称和类型
       * @return
       */
      @SuppressWarnings("rawtypes")
	public static <T> T toBean(String jsonString, Class<T> mainClass,HashMap<String, Class> detailClass){
          JSONObject jsonObject = JSONObject.fromObject(jsonString);
          T mainEntity = JsonUtils.toBean(jsonObject, mainClass);
          for (Object key : detailClass.keySet()){
              try
              {
                  Class value = (Class) detailClass.get(key);
                  BeanUtils.setProperty(mainEntity, key.toString(), value);
              }
              catch (Exception ex)
              {
                  throw new RuntimeException("主从关系JSON反序列化实体失败！");
              }
          }
          return mainEntity;
      }      

      public static Date[] getDateArray4Json(String jsonString,String DataFormat) throws Exception{ 
     	 JSONArray jsonArray = JSONArray.fromObject(jsonString); 
     	 Date[] dateArray = new Date[jsonArray.size()]; 
   /*  	 String dateString; 
     	 Date date; 
     	 for( int i = 0 ; i<jsonArray.size() ; i++ ){ 
     	 dateString = jsonArray.getString(i); 
     	 date = DateUtil.stringToDate(dateString, DataFormat); 
     	 dateArray[i] = date; 
     	 } */
     	 return dateArray; 
     }  
      
      
    
      
}
