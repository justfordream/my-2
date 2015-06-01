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

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.controller.h2.mapper.UpayCsysOrgCodeh2Mapper;
import com.huateng.cmupay.controller.mapper.UpayCsysOrgCodeMapper;

import com.huateng.cmupay.models.UpayCsysOrgCode;


/**
 * 机构信息索引类
 * 
 * @author cmt
 * 
 */

@Component
public class OrgCodeCache {

	protected static final Logger logger = LoggerFactory.getLogger("OrgCodeCache");
	// private final static Map<String, UpayCsysOrgCode> ORG_CODE_MAP = new
	// HashMap<String, UpayCsysOrgCode>();
	//
	// static final private List<UpayCsysOrgCode> ORG_CODE_LIST = new
	// ArrayList<UpayCsysOrgCode>();

	@Autowired
	private UpayCsysOrgCodeMapper upayCsysOrgCodeMapper;
	private static UpayCsysOrgCodeMapper mapper;
	
	@Autowired
	private UpayCsysOrgCodeh2Mapper upayCsysOrgCodeh2Mapper;
	private static UpayCsysOrgCodeh2Mapper h2mapper;

	/**
	 * 初始化数据字典
	 * 
	 * @author cmt
	 */
	@PostConstruct
	private void init() {
		mapper = upayCsysOrgCodeMapper;
		h2mapper = upayCsysOrgCodeh2Mapper;
	}

	/**
	 * 重载数据字典
	 * 
	 * @author cmt
	 */
	public void reLoad() {
		init();
	}

	public static UpayCsysOrgCode getOrgCode(String str) {
		if (str == null || str.trim().equals(""))
			return null;
		UpayCsysOrgCode orgCode = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orgId", str);
			orgCode = h2mapper.selectByParams(params);
//			orgCode = BeanUtil.toBean(UpayCsysOrgCode.class,
//					UpayMemCache.getOrgCode(str));
		} catch (Exception e) {
			logger.error("查询内存数据库异常,直接查询物理数据库getOrgCode:",e);
		} finally {
			if (null == orgCode || "".equals(orgCode.getOrgId())||"null".equalsIgnoreCase(orgCode.getOrgId())) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("orgId", str);
				orgCode = mapper.selectByParams(params);
			}
		}
		return orgCode;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<UpayCsysOrgCode> getOrgCodeIn(List strList) {

		if (strList == null || strList.isEmpty())
			return null;
		List<UpayCsysOrgCode> orgCodeList = new ArrayList<UpayCsysOrgCode>();
		
		try {
			List orgCodeListStr = new ArrayList();
//			orgCodeListStr = UpayMemCache.getOgrCodeIn(strList);
//			for (int i = 0; i < orgCodeListStr.size(); i++) {
//				UpayCsysOrgCode org = null;
//				Map map = (Map) orgCodeListStr.get(i);
//				org = BeanUtil.toBean(UpayCsysOrgCode.class, map);
//				orgCodeList.add(org);
//				
//			}
			
			for (Object o : strList) {
				logger.info("getOrgCodeIn查询内存数据库OgrCode:{}",String.valueOf(o));
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("orgId", String.valueOf(o));
				params.put("status", CommonConstant.IsActive.True.getValue());
				params.put("isHistory",
						CommonConstant.IsHistory.Normal.getValue());
				UpayCsysOrgCode org = h2mapper.selectByParams(params);
			    String  status = org ==null?"null":org.getStatus();
				logger.info("查询OrgCode_status:{}",status);
				if(org!= null){
					orgCodeList.add(org);
				}
			}

		} catch (Exception e) {
				logger.error("内存数据库调用异常，getOrgCodeIn查询物理数据库OgrCode:",e);
		}finally{
			if (orgCodeList == null || orgCodeList.size()==0||orgCodeList.isEmpty()) { 
				orgCodeList = new ArrayList<UpayCsysOrgCode>();
				for (Object o : strList) {
					logger.info("内存数据库调用异常，getOrgCodeIn查询物理数据库OgrCode:{}",String.valueOf(o));
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("orgId", String.valueOf(o));
					params.put("status", CommonConstant.IsActive.True.getValue());
					params.put("isHistory",
							CommonConstant.IsHistory.Normal.getValue());
					UpayCsysOrgCode org = mapper.selectByParams(params);
				    String  status = org ==null?"null":org.getStatus();
					logger.info("查询OrgCode_status:{}",status);
					if(org!= null){
						orgCodeList.add(org);
					}
					
				}
				//return orgCodeList;
			}
		}

		return orgCodeList;


	}
}
