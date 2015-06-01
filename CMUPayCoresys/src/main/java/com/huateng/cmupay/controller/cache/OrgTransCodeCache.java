/**
 * 
 */
package com.huateng.cmupay.controller.cache;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * 路由表索引类
 * 
 * @author cmt
 * 
 */

@Component
public class OrgTransCodeCache {

	protected static final  Logger logger = LoggerFactory.getLogger(OrgTransCodeCache.class);

	/**
	 * 初始化数据字典
	 * 
	 * @author cmt
	 */
	@PostConstruct
	private void init() {

//		ORG_TRANSCODE_MAP.clear();
//		ORG_TRANSCODE_LIST.clear();
//		
//		Map<String, Object> paramInfo = new HashMap<String, Object>();
//		
//		paramInfo.put("status", CommonConstant.IsActive.True.toString());
//		paramInfo.put("isHistory", CommonConstant.IsHistory.Normal.toString());
//		List<UpayCsysOrgTransCode> infoList = upayCsysOrgTransCodeMapper
//				.selectAllListByParams(paramInfo, null);
//		ORG_TRANSCODE_LIST.addAll(infoList);
//		for (UpayCsysOrgTransCode info : infoList) {
//			
//			UpayCsysOrgTransCode transInfo = ORG_TRANSCODE_MAP.get(info.getOrgId()+info.getTransCode());
//			if (transInfo == null) {
//				ORG_TRANSCODE_MAP.put(info.getOrgId()+info.getTransCode(), info);
//			
//			}
//		}

	}

	/**
	 * 重载数据字典
	 * 
	 * @author cmt
	 */
	public void reLoad() {
		init();
	}

//	/**
//	 * @return
//	 */
//	public static List<UpayCsysOrgTransCode> getOrgTransCodeList() {
//		
//		return ORG_TRANSCODE_LIST;
//	}
//	
//	/**
//	 * @return
//	 */
//	public static Map<String ,UpayCsysOrgTransCode> getOrgTransCodeMap() {
//		
//		return ORG_TRANSCODE_MAP;
//	}

//	/**
//	 * @return
//	 */
//	public static UpayCsysOrgTransCode getOrgTransCode(String str) {
//		if (str == null || str.trim().equals(""))
//			return null;
//		
//
//		UpayCsysOrgTransCode transCode = ORG_TRANSCODE_MAP.get(str);
//
//		
//
//		return transCode;
//	}

//	
//	@SuppressWarnings("unchecked")
//	public static UpayCsysOrgTransCode getOrgTransCode(String str,String str1) {
//		if (str == null || str.trim().equals(""))
//			return null;
//		UpayCsysOrgTransCode orgTransCode;
//		try {
//			orgTransCode = BeanUtil.toBean(UpayCsysOrgTransCode.class,
//					UpayMemCache.getOrgTransCode(str, str1));
//		} catch (Exception e) {
//			logger.info("查询内存数据库异常,直接查询物理数据库");
//			return null;
//		}
//		return orgTransCode;
//	}
//	
//	
	
	
	
	
	
	

}
