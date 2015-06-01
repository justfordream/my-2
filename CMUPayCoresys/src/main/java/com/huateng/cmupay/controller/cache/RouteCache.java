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

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.controller.mapper.UpayCsysRouteInfoMapper;
import com.huateng.cmupay.models.UpayCsysRouteInfo;

/**
 * 路由表索引类
 * 
 * @author cmt
 * 
 */

@Component
public class RouteCache {
    
	protected static final  Logger logger = LoggerFactory.getLogger(RouteCache.class);
	private final static Map<String, UpayCsysRouteInfo>  ROUTE_MAP = new HashMap<String, UpayCsysRouteInfo>();
    
	private final static   List<UpayCsysRouteInfo> ROUTE_LIST = new ArrayList<UpayCsysRouteInfo>();
    
	@Autowired
	private UpayCsysRouteInfoMapper upayCsysRouteInfoMapper;
	
	private static UpayCsysRouteInfoMapper routeInfoMapper;
	
	/**
	 * 初始化数据字典
	 * 根据新需求，已经将该表的数据从物理库（设置了二级缓存）读取，而不是放入内存，所以init实际已经不用
	 * @author cmt
	 */
	@PostConstruct
	private void init() {
		routeInfoMapper = upayCsysRouteInfoMapper;
//		ROUTE_MAP.clear();
//		ROUTE_LIST.clear();
//		Map<String, Object> paramInfo = new HashMap<String, Object>();
//		paramInfo.put("isHistory", CommonConstant.IsHistory.Normal.toString());
//		paramInfo.put("status", CommonConstant.IsActive.True.toString());
//		List<UpayCsysRouteInfo> infoList = upayCsysRouteInfoMapper
//				.selectAllListByParams(paramInfo, null);
//		ROUTE_LIST.addAll(infoList);
//		for (UpayCsysRouteInfo info : infoList) {
//			
//			UpayCsysRouteInfo routeInfo = ROUTE_MAP.get(info.getOrgId());
//			if (routeInfo == null) {
//				ROUTE_MAP.put(info.getOrgId(), info);
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
//	public static List<UpayCsysRouteInfo> getRouteInfoList() {
//		
//		return ROUTE_LIST;
//	}
//	
//	/**
//	 * @return
//	 */
//	public static Map<String ,UpayCsysRouteInfo> getRouteInfoMap() {
//		
//		return ROUTE_MAP;
//	}

//	/**
//	 * @return
//	 * 根据新的业务需求，已经不再将该表放入内存，从物理库取数据
//	 */
//	public static UpayCsysRouteInfo getRouteInfo(String str) {
//		if (str == null || str.trim().equals(""))
//			return null;
//		UpayCsysRouteInfo routeInfo = ROUTE_MAP.get(str);
//
//		return routeInfo;
//	}
	
	/**
	 * @return
	 * 从物理库取数据
	 */
	public static UpayCsysRouteInfo getRouteInfo(String str) {
		if (str == null || str.trim().equals(""))
			return null;
		
		HashMap<String, Object> selectParamInfo = new HashMap<String, Object>();
		selectParamInfo.put("isHistory", CommonConstant.IsHistory.Normal.toString());
		selectParamInfo.put("status", CommonConstant.IsActive.True.toString());
		selectParamInfo.put("orgId", str);
		UpayCsysRouteInfo routeInfo = routeInfoMapper
				.selectByParams(selectParamInfo);

		return routeInfo;
	}
	
//	/**
//	 * @return
//	 */
//	public static UpayCsysRouteInfo getRouteInfo(String str) {
//		if (str == null || str.trim().equals(""))
//			return null;
//		UpayCsysRouteInfo routeInfo = ROUTE_MAP.get(str);
//
//		return routeInfo;
//	}
	

//	@SuppressWarnings("unchecked")
//	public static UpayCsysRouteInfo getRouteInfo(String str) {
//		if (str == null || str.trim().equals(""))
//			return null;
//		UpayCsysRouteInfo routeInfo;
//		try {
//			routeInfo = BeanUtil.toBean(UpayCsysRouteInfo.class,
//					UpayMemCache.getRouteInfo(str));
//		} catch (Exception e) {
//			logger.error("内存数据库调用异常。",e);
//			return null;
//		}
//
//		return routeInfo;
//	}
}
