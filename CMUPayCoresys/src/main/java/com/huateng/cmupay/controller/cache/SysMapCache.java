package com.huateng.cmupay.controller.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huateng.cmupay.controller.mapper.UpayCsysSysMapInfoMapper;
import com.huateng.cmupay.models.UpayCsysSysMapInfo;

/**
 * 
 * @author fan_kui 系统编码映射
 * 
 */
@Component
public class SysMapCache {

	/** 系统编码-映射信息 */
	private final static Map<String, UpayCsysSysMapInfo> SYSCD_SYSMAP_MAP = new HashMap<String, UpayCsysSysMapInfo>();
	/** 交换节点-映射信息 */
	private final static Map<String, UpayCsysSysMapInfo> DUNSCD_SYSMAP_MAP = new HashMap<String, UpayCsysSysMapInfo>();
	/** 省代码-映射信息 */
	private final static Map<String, UpayCsysSysMapInfo> PROVCD_SYSMAP_MAP = new HashMap<String, UpayCsysSysMapInfo>();

	@Autowired
	private UpayCsysSysMapInfoMapper upayCsysSysMapInfoMapper;
	private static UpayCsysSysMapInfoMapper infoMapper;

	public UpayCsysSysMapInfoMapper getUpayCsysSysMapInfoMapper() {
		return upayCsysSysMapInfoMapper;
	}

	public void setUpayCsysSysMapInfoMapper(
			UpayCsysSysMapInfoMapper upayCsysSysMapInfoMapper) {
		this.upayCsysSysMapInfoMapper = upayCsysSysMapInfoMapper;
	}

	/**
	 * 初始化数据字典
	 * 根据业务需求，不再在内存中保存数据，需要数据直接去数据库中取
	 * 
	 */
	@PostConstruct
	private void init() {
		infoMapper = upayCsysSysMapInfoMapper;
//		SYSCD_SYSMAP_MAP.clear();
//		DUNSCD_SYSMAP_MAP.clear();
//		PROVCD_SYSMAP_MAP.clear();
//
//		List<UpayCsysSysMapInfo> infoList = upayCsysSysMapInfoMapper
//				.selectAllListByParams(new HashMap<String, Object>(), null);
//
//		for (UpayCsysSysMapInfo info : infoList) {
//
//			UpayCsysSysMapInfo infosys = SYSCD_SYSMAP_MAP.get(info.getSysCd());
//			UpayCsysSysMapInfo infduns = DUNSCD_SYSMAP_MAP
//					.get(info.getDunsCd());
//			UpayCsysSysMapInfo infoprov = PROVCD_SYSMAP_MAP.get(info
//					.getAreaCd());
//
//			if (null == infosys) {
//				SYSCD_SYSMAP_MAP.put(info.getSysCd(), info);
//			}
//			if (null == infduns) {
//				DUNSCD_SYSMAP_MAP.put(info.getDunsCd(), info);
//			}
//			if (null == infoprov) {
//				PROVCD_SYSMAP_MAP.put(info.getAreaCd(), info);
//			}
//		}

	}

	/**
	 * 重载数据字典
	 * 
	 * @author c.mt
	 */
	public void reLoad() {
		init();
	}

	/**
	 * 根据系统的4位省份编码获取信息
	 * @return
	 */
	public static UpayCsysSysMapInfo getSysCd(String str) {
		if (str == null || str.trim().equals(""))
			return null;
		
		UpayCsysSysMapInfo routeInfo = SYSCD_SYSMAP_MAP.get(str);
		if(null == routeInfo){
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("sysCd", str);
			routeInfo = infoMapper.selectByParams(params);
		}
		return routeInfo;
	}

	/**
	 * @return
	 */
	public static UpayCsysSysMapInfo getDunsCd(String str) {
		if (str == null || str.trim().equals(""))
			return null;

		UpayCsysSysMapInfo routeInfo = DUNSCD_SYSMAP_MAP.get(str);
		if(null == routeInfo){
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("dunsCd", str);
			routeInfo = infoMapper.selectByParams(params);
		}
		return routeInfo;
	}

	/**
	 * 根据三位代码获取省份信息
	 * @return
	 */
	public static UpayCsysSysMapInfo getProvCd(String str) {
		if (str == null || str.trim().equals(""))
			return null;
		UpayCsysSysMapInfo routeInfo = PROVCD_SYSMAP_MAP.get(str);
		if(null == routeInfo){
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("areaCd", str);
			routeInfo = infoMapper.selectByParams(params);
		}
		return routeInfo;
	}
	
	
	
	
//	
//	@SuppressWarnings("unchecked")
//	public static UpayCsysSysMapInfo getSysCd(String str) {
//		if (str == null || str.trim().equals(""))
//			return null;
//		
//		UpayCsysSysMapInfo routeInfo = SYSCD_SYSMAP_MAP.get(str);
//		if(null == routeInfo){
//			routeInfo = BeanUtil.toBean(UpayCsysSysMapInfo.class,
//					UpayMemCache.getSysCd(str));
//		}
//		return routeInfo;
//	}
//
//
//	@SuppressWarnings("unchecked")
//	public static UpayCsysSysMapInfo getDunsCd(String str) {
//		if (str == null || str.trim().equals(""))
//			return null;
//
//		UpayCsysSysMapInfo routeInfo = DUNSCD_SYSMAP_MAP.get(str);
//		if(null == routeInfo){
//			routeInfo = BeanUtil.toBean(UpayCsysSysMapInfo.class,
//					UpayMemCache.getDunsCd(str));
//		}
//		return routeInfo;
//	}
//
//
//	@SuppressWarnings("unchecked")
//	public static UpayCsysSysMapInfo getProvCd(String str) {
//		if (str == null || str.trim().equals(""))
//			return null;
//		UpayCsysSysMapInfo routeInfo = PROVCD_SYSMAP_MAP.get(str);
//		if(null == routeInfo){
//			routeInfo = BeanUtil.toBean(UpayCsysSysMapInfo.class,
//					UpayMemCache.getProvCd(str));
//		}
//		return routeInfo;
//	}
	

}
