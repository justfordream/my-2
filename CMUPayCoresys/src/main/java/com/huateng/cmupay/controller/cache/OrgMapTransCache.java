package com.huateng.cmupay.controller.cache;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.controller.h2.mapper.UpayCsysOrgMapTransCodeLdh2Mapper;
import com.huateng.cmupay.controller.h2.mapper.UpayCsysOrgMapTransCodeh2Mapper;
import com.huateng.cmupay.controller.mapper.UpayCsysOrgMapTransCodeLdMapper;
import com.huateng.cmupay.controller.mapper.UpayCsysOrgMapTransCodeMapper;
import com.huateng.cmupay.models.UpayCsysOrgMapTransCode;
import com.huateng.toolbox.utils.StringUtil;

@Component
public class OrgMapTransCache {

	protected static final Logger logger = LoggerFactory
			.getLogger("OrgMapTransCache");

	// private final static Map<String, UpayCsysOrgMapTransCode>
	// ORG_TRANSCODE_MAP = new HashMap<String, UpayCsysOrgMapTransCode>();
	//
	// static final private List<UpayCsysOrgMapTransCode> ORG_TRANSCODE_LIST =
	// new ArrayList<UpayCsysOrgMapTransCode>();
	//
	@Autowired
	private UpayCsysOrgMapTransCodeMapper mapper;
	private static UpayCsysOrgMapTransCodeMapper upayCsysOrgMapTransCodeMapper;
	
	@Autowired
	private UpayCsysOrgMapTransCodeh2Mapper h2mapper;
	private static UpayCsysOrgMapTransCodeh2Mapper upayCsysOrgMapTransCodeh2Mapper;

	// 和联通电信号码相关交易权限
	@Autowired
	private UpayCsysOrgMapTransCodeLdMapper ldMapper;
	private static UpayCsysOrgMapTransCodeLdMapper upayCsysOrgMapTransCodeLdMapper;
	
	@Autowired
	private UpayCsysOrgMapTransCodeLdh2Mapper ldL2mapper;
	private static UpayCsysOrgMapTransCodeLdh2Mapper upayCsysOrgMapTransCodeLdh2Mapper;

	
	
	@PostConstruct
	public void init() {
		
		upayCsysOrgMapTransCodeMapper = mapper;
		upayCsysOrgMapTransCodeh2Mapper = h2mapper;
		
		upayCsysOrgMapTransCodeLdMapper = ldMapper;
		upayCsysOrgMapTransCodeLdh2Mapper = ldL2mapper;
	}

	public static String getOrgMapTransCode(String reqOrg,
			String rcvOrg) {
		String o2o = null;
		try {
//			o2o = UpayMemCache.getOrgMapTransCode(reqOrg, rcvOrg);
			logger.info("getOrgMapTransCode查询内存数据库reqOrg:{},rcvOrg:{}",reqOrg,rcvOrg);
			UpayCsysOrgMapTransCode o3o = null;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("reqOrgId", reqOrg);
			params.put("rcvOrgId", rcvOrg);
			params.put("status", CommonConstant.IsActive.True.getValue());
			params.put("isHistory", CommonConstant.IsHistory.Normal.getValue());
			o3o = upayCsysOrgMapTransCodeh2Mapper.selectByParams(params);
			if(null == o3o || "".equals(StringUtil.toTrim(o3o.getReqOrgId()))){
				o2o = null;
			}else{
				o2o = o3o.getTransCodeCollect();
			}
//			logger.info("查询TransCode:{}",o2o);
//			return o2o;
		} catch (Exception e) {
			logger.error("内存数据库调用异常，getOrgMapTransCode查询物理数据库:",e);

		}finally{
			if (o2o == null || "".equals(o2o)||o2o.equalsIgnoreCase("null")) { 
				logger.info("内存数据库调用异常，getOrgMapTransCode查询物理数据库");
				UpayCsysOrgMapTransCode o3o = null;
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("reqOrgId", reqOrg);
				params.put("rcvOrgId", rcvOrg);
				params.put("status", CommonConstant.IsActive.True.getValue());
				params.put("isHistory", CommonConstant.IsHistory.Normal.getValue());
				o3o = upayCsysOrgMapTransCodeMapper.selectByParams(params);
				if(null == o3o || "".equals(StringUtil.toTrim(o3o.getReqOrgId()))){
					o2o = null;
				}else{
					o2o = o3o.getTransCodeCollect();
				}
				//logger.info("查询TransCode:{}",o2o);
				//return o2o;
			}
		}
		logger.info("查询TransCode:{}",o2o);
		return o2o;
	}
	
	/**
	 * 查询联通和电信号码
	 */
	public static String getOrgMapTransCodeForUnicomTelecom(String reqOrg,
			String rcvOrg) {
		String o2o = null;
		try {
//			o2o = UpayMemCache.getOrgMapTransCode(reqOrg, rcvOrg);
			logger.info("getOrgMapTransCodeForUnicomTelecom查询内存数据库reqOrg:{},rcvOrg:{}",reqOrg,rcvOrg);
			UpayCsysOrgMapTransCode o3o = null;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("reqOrgId", reqOrg);
			params.put("rcvOrgId", rcvOrg);
			params.put("status", CommonConstant.IsActive.True.getValue());
			params.put("isHistory", CommonConstant.IsHistory.Normal.getValue());
			o3o = upayCsysOrgMapTransCodeLdh2Mapper.selectByParams(params);
			if(null == o3o || "".equals(StringUtil.toTrim(o3o.getReqOrgId()))){
				o2o = null;
			}else{
				o2o = o3o.getTransCodeCollect();
			}
//			logger.info("查询TransCode:{}",o2o);
//			return o2o;
		} catch (Exception e) {
			logger.error("内存数据库调用异常，getOrgMapTransCodeForUnicomTelecom查询物理数据库:",e);

		}finally{
			if (o2o == null || "".equals(o2o)||o2o.equalsIgnoreCase("null")) { 
				logger.info("内存数据库调用异常，getOrgMapTransCodeForUnicomTelecom查询物理数据库");
				UpayCsysOrgMapTransCode o3o = null;
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("reqOrgId", reqOrg);
				params.put("rcvOrgId", rcvOrg);
				params.put("status", CommonConstant.IsActive.True.getValue());
				params.put("isHistory", CommonConstant.IsHistory.Normal.getValue());
				o3o = upayCsysOrgMapTransCodeLdMapper.selectByParams(params);
				if(null == o3o || "".equals(StringUtil.toTrim(o3o.getReqOrgId()))){
					o2o = null;
				}else{
					o2o = o3o.getTransCodeCollect();
				}
				//logger.info("查询TransCode:{}",o2o);
				//return o2o;
			}
		}
		logger.info("查询TransCode:{}",o2o);
		return o2o;
		
		
	}
}