package com.huateng.core.parse.config;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.huateng.core.parse.config.bean.ProvinceBean;
import com.huateng.core.parse.config.bean.ProvinceMsg;
import com.huateng.core.util.FileUtil;

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
	private final static String PROVINCE_FILE = "/property/ProvinceCode.xml";
	private final static String PRO_CODE = "code";
	private final static String PRO_NAME = "name";
	private final static String PRO_URL = "url";
	private final static String PRO_SIGN_URL = "signUrl";
	private final static String PRO_PAY_URL = "payUrl";
	private final static String PRO_SERVER_URL = "serverUrl";
	private static ProvinceMsg provinceMsg;

	static {
		if (provinceMsg == null) {
			provinceMsg = ProvinceConfigUtil.readConfigFile();
		}
	}

	/**
	 * 读取XML文件内容
	 * 
	 * @param fullPath
	 */
	@SuppressWarnings("unchecked")
	public static ProvinceMsg readConfigFile() {
		URL url = FileUtil.class.getClassLoader().getResource(PROVINCE_FILE);
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
				province.setUrl(ele.attributeValue(PRO_URL));
				province.setSignUrl(ele.attributeValue(PRO_SIGN_URL));
				province.setPayUrl(ele.attributeValue(PRO_PAY_URL));
				province.setServerUrl(ele.attributeValue(PRO_SERVER_URL));
				provinces.add(province);
			}
			msg.setProvinceList(provinces);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * 根据网厅地址取得省机构号
	 * 
	 * @param url
	 *            省网厅BackURL
	 * @return 省网厅机构号
	 */
	public static ProvinceBean getProvince(String url) {
		List<ProvinceBean> provList = provinceMsg.getProvinceList();
		ProvinceBean code = new ProvinceBean();
		if (StringUtils.isBlank(url)) {
			return code;
		}
		for (ProvinceBean bean : provList) {
			if (url.equalsIgnoreCase(bean.getUrl()) || url.equalsIgnoreCase(bean.getSignUrl())
					|| url.equalsIgnoreCase(bean.getPayUrl())) {
				code = bean;
				break;
			}
		}
		return code;
	}

	/**
	 * 根据网厅地址取得省机构号
	 * 
	 * @param url
	 *            省网厅BackURL
	 * @return 省网厅机构号
	 */
	public static ProvinceBean getProvinceByCode(String code) {
		List<ProvinceBean> provList = provinceMsg.getProvinceList();
		ProvinceBean bean = new ProvinceBean();
		if (StringUtils.isBlank(code)) {
			bean.setCode("upay");
			bean.setName("统一支付测试模拟程序");
			bean.setPayUrl("http://127.0.0.1:8080/CMUPayWebPay/CmuPay.jsp");
			bean.setSignUrl("http://127.0.0.1:8080/CMUPayWebPay/CmuSign.jsp");
			bean.setServerUrl("http://127.0.0.1:8080/CMUPayWebPay/redirectCmu.action");
			return bean;
		}
		for (ProvinceBean obj : provList) {
			if (code.equalsIgnoreCase(obj.getCode())) {
				bean = obj;
				break;
			}
		}
		return bean;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(ProvinceConfigUtil.readConfigFile().getProvinceList().get(0).getCode());
	}

}
