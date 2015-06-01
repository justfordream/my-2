/**
 * 
 */
package com.huateng.cmupay.controller.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huateng.cmupay.controller.mapper.UpayCsysTransCodeMapper;
import com.huateng.cmupay.models.UpayCsysTransCode;
import com.huateng.toolbox.utils.StringUtil;

/**
 * 路由表索引类
 * 
 * @author cmt
 * 
 */

@Component
public class TransCodeCache {
	protected static final  Logger logger = LoggerFactory.getLogger(TransCodeCache.class);
	
	private final static Map<String, UpayCsysTransCode>  TRANSCODE_MAP = new HashMap<String, UpayCsysTransCode>();

	static final private List<UpayCsysTransCode> TRANSCODE_LIST = new ArrayList<UpayCsysTransCode>();

	

	@Autowired
	private UpayCsysTransCodeMapper upayCsysTransCodeMapper;

	/**
	 * 初始化数据字典
	 * 
	 * @author cmt
	 */
	@PostConstruct
	private void init() {

		TRANSCODE_MAP.clear();
		TRANSCODE_LIST.clear();
		
		Map<String, Object> paramInfo = new HashMap<String, Object>();
		
		List<UpayCsysTransCode> infoList = upayCsysTransCodeMapper
				.selectAllListByParams(paramInfo, null);
		TRANSCODE_LIST.addAll(infoList);
		for (UpayCsysTransCode info : infoList) {
			UpayCsysTransCode transInfo = null;
			String reqActivityCode = info.getReqActivityCode() == null||"".equals(info.getReqActivityCode())?null:info.getReqActivityCode().trim();
			if (transInfo == null&&null!=reqActivityCode) {
				if(null == info.getReqBipCode() || "".equals(info.getReqBipCode())){
					transInfo =	TRANSCODE_MAP.get(reqActivityCode);
				}else{
					transInfo =	TRANSCODE_MAP.get(info.getReqBipCode().trim()+reqActivityCode);
				}
			}
			
			if (transInfo == null&&null!=reqActivityCode) {
				if(null == info.getReqBipCode() || "".equals(info.getReqBipCode())){
					TRANSCODE_MAP.put(info.getReqActivityCode().trim(), info);
				}else{
					TRANSCODE_MAP.put(info.getReqBipCode().trim()+info.getReqActivityCode().trim(), info);
				}
			}
		}

	}

	/**
	 * 重载数据字典
	 * 
	 * @author cmt
	 */
	public void reLoad() {
		init();
	}

	/**
	 * @return
	 */
	public static List<UpayCsysTransCode> getTransCodeList() {
		
		return TRANSCODE_LIST;
	}
	
	/**
	 * @return
	 */
	public static Map<String ,UpayCsysTransCode> getTransCodeMap() {
		
		return TRANSCODE_MAP;
	}

	/**
	 * @return
	 */
	public static UpayCsysTransCode getTransCode(String str) {
		if (str == null || str.trim().equals(""))
			return null;
		

		UpayCsysTransCode code = TRANSCODE_MAP.get(str);

		

		return code;
	}

	
	public static UpayCsysTransCode getTransCode(String str,String str1) {
		if (str1 == null || str1.trim().equals("")){
			return null;
		}			
		UpayCsysTransCode transCode   = TRANSCODE_MAP.get(StringUtil.toTrim(str)+str1);;
		

		return transCode;
	}
	

	/*public static UpayCsysTransCode getTransCode(String str,String str1) {
		if ((str == null || str.trim().equals(""))&&(str1 == null || str1.trim().equals(""))){
			return null;
		}			
		UpayCsysTransCode transCode;
		try {
			transCode = BeanUtil.toBean(UpayCsysTransCode.class,
					UpayMemCache.getTransCode(str, str1));
		} catch (Exception e) {
			logger.error("内存数据库调用异常。",e);
			return null;
		}

		return transCode;
	}*/
	
	
	
	
	
	
	
	
	
	
	
	

}
