/**
 * 
 */
package com.huateng.cmupay.controller.cache;

import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.huateng.toolbox.xml.GetXmlFile;



/**
 * 字段 缓存
 * 数据库字段和 mybatis 隐射关系
 * @author cmt
 *
 */
@Component
@SuppressWarnings("unchecked")
public class ColumnCache {

	private static final Map<String,String> COLUMN_MAP=new HashMap<String,String>();
	
	protected final  Logger logger = LoggerFactory.getLogger(ColumnCache.class);
	@SuppressWarnings("unused")
	//@PostConstruct
	private void init(){
		try {
			String pathColumn = ColumnCache.class.getResource("/com/huateng/cmupay/mapper").getPath();
			String fileColumn[] = new File(URLDecoder.decode(pathColumn,"utf-8")).list();
			for (String f : fileColumn) {
				String fileName[] = f.split("\\.");
				if (fileName.length > 1 && fileName[fileName.length - 1].equals("xml")) {
					StringBuffer sb=new StringBuffer();
					sb.append(pathColumn).append(File.separator).append(f);
					
					GetXmlFile xmlFile = new GetXmlFile(sb.toString());

					List<Element> resultMapList = xmlFile.GetListByXpath("/mapper/resultMap");
					for (Element resultMap : resultMapList) {
						List<Element> columnList= resultMap.elements();
						for (Element element : columnList) {
							COLUMN_MAP.put(element.attributeValue("property"),element.attributeValue("column"));
						}
					}
					
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("未知异常:",e);
		}
	}
	
	
	/**
	 * 根据 映射值 获取 数据库字段 名称
	 * @author cmt
	 * @param property
	 * @return
	 */
	public static String getColumnByProperty(String property){
		return COLUMN_MAP.get(property);
	}
}
