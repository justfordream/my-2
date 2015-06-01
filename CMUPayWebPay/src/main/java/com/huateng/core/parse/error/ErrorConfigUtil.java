package com.huateng.core.parse.error;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.huateng.core.common.CoreConstant;
import com.huateng.core.parse.error.bean.ErrorBean;
import com.huateng.core.parse.error.bean.ErrorConfig;
import com.huateng.core.util.FileUtil;

/**
 * 读取错误码配置文件
 * 
 * @author Gary
 * 
 */
public class ErrorConfigUtil {
	/**
	 * 配置文件名称
	 */
	private final static String ERROR_CODE_FILE = "/property/ErrorCode.xml";
	private final static String INNER_CODE = "innerCode";
	private final static String OUTER_CODE = "outerCode";
	private final static String ERROR_MSG = "errorMsg";
	public final static String BANK = "bank";
	public final static String CMU = "cmu";
	private static ErrorConfig errorConfig;

	static {
		if (errorConfig == null) {
			errorConfig = ErrorConfigUtil.readConfigFile();
		}
	}

	/**
	 * 读取XML文件内容
	 * 
	 * @param fullPath
	 */
	@SuppressWarnings("unchecked")
	public static ErrorConfig readConfigFile() {
		URL url = FileUtil.class.getClassLoader().getResource(ERROR_CODE_FILE);
		SAXReader reader = new SAXReader();
		ErrorConfig msg = new ErrorConfig();
		List<ErrorBean> bankErrorList = new ArrayList<ErrorBean>();
		List<ErrorBean> cmuErrorList = new ArrayList<ErrorBean>();
		try {
			Document doc = reader.read(url);
			List<Element> provinceList = doc.selectNodes("/ErrorConfig/ErrorList");
			ErrorBean bean = null;
			String type = null;
			List<Element> errorList = null;
			for (Element ele : provinceList) {
				type = ele.attributeValue("type");
				errorList = ele.elements();
				if (BANK.equals(type)) {
					for (Element bankEle : errorList) {
						bean = new ErrorBean();
						bean.setInnerCode(bankEle.attributeValue(INNER_CODE));
						bean.setOuterCode(bankEle.attributeValue(OUTER_CODE));
						bean.setErrorMsg(bankEle.attributeValue(ERROR_MSG));
						bankErrorList.add(bean);
					}
				} else if (CMU.equals(type)) {
					for (Element cmuEle : errorList) {
						bean = new ErrorBean();
						bean.setInnerCode(cmuEle.attributeValue(INNER_CODE));
						bean.setOuterCode(cmuEle.attributeValue(OUTER_CODE));
						bean.setErrorMsg(cmuEle.attributeValue(ERROR_MSG));
						cmuErrorList.add(bean);
					}
				}
			}
			msg.setBankErrorList(bankErrorList);
			msg.setCmuErrorList(cmuErrorList);
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
	public static ErrorBean getErrorBean(String type, String innerCode) {

		List<ErrorBean> errorList = null;
		ErrorBean bean = null;
		if (BANK.equals(type)) {
			errorList = errorConfig.getBankErrorList();
		} else if (CMU.equals(type)) {
			errorList = errorConfig.getCmuErrorList();
		} else {
			errorList = new ArrayList<ErrorBean>();
		}
		for (ErrorBean obj : errorList) {
			if (obj.getInnerCode().equals(innerCode)) {
				bean = obj;
				break;
			}
		}
		if(bean==null){
			for (ErrorBean obj : errorList) {
				if (obj.getInnerCode().equals(CoreConstant.CmuErrorCode.FAILED)) {
					bean = obj;
					break;
				}
			}
		}
		return bean;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ErrorBean bean = ErrorConfigUtil.getErrorBean(BANK, "upay001");
		System.out.println(bean.getErrorMsg());
	}

}
