package com.huateng.core.parse.config;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.core.parse.config.bean.ProvinceBean;
import com.huateng.core.parse.config.bean.ProvinceMsg;

/**
 * 读取省信息配置文件
 * 
 * @author Gary
 * 
 */
public class ProvinceConfigUtil {
	/**
	 * 配置文件名称
	 */
	private final static String PROVINCE_FILE = "/config/ProvinceCode.xml";
	private final static String PRO_CODE = "code";
	private final static String PRO_NAME = "name";
	private final static String PRO_OSN_DUNS = "osnDuns";
	private static Logger logger = LoggerFactory.getLogger("ProvinceConfigUtil");

	/**
	 * 读取XML文件内容
	 * 
	 * @param fullPath
	 */
	@SuppressWarnings("unchecked")
	public static ProvinceMsg readConfigFile() {
		URL url = ProvinceConfigUtil.class.getResource(PROVINCE_FILE);
		SAXReader reader = new SAXReader();
		ProvinceMsg msg = new ProvinceMsg();
		List<ProvinceBean> provinces = new ArrayList<ProvinceBean>();
		try {
			Document doc = reader.read(url);
			List<Element> provinceList = doc.selectNodes("/ProvinceMsg/ProvinceList/Province");
			ProvinceBean province = null;
			for (Element ele : provinceList) {
				province = new ProvinceBean();
				province.setCode(ele.attributeValue(PRO_CODE));
				province.setName(ele.attributeValue(PRO_NAME));
				province.setOsnDuns(ele.attributeValue(PRO_OSN_DUNS));
				provinces.add(province);
			}
			msg.setProvinceList(provinces);
		} catch (DocumentException e) {
			logger.error("", e);
		}
		return msg;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(ProvinceConfigUtil.readConfigFile().getProvinceList().get(0).getCode());
	}

}
