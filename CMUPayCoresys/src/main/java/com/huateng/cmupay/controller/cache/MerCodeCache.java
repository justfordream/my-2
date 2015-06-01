package com.huateng.cmupay.controller.cache;

import java.util.HashMap;
//import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huateng.cmupay.controller.h2.mapper.TpayCsysMerCodeh2Mapper;
import com.huateng.cmupay.controller.mapper.TpayCsysMerCodeMapper;
import com.huateng.cmupay.models.TpayCsysMerCodeInfo;

/**
 * 
 * 商户代码映射
 *
 */
@Component
public class MerCodeCache {
	
	protected static final Logger logger = LoggerFactory.getLogger(MerCodeCache.class);
	
	//通过商户号映射商户信息
//	private final static Map<String, TpayCsysMerCodeInfo> MER_CODE_MAP = new HashMap<String, TpayCsysMerCodeInfo>();
	
	//通过（机构代码+第三方支付机构代码） 映射商户信息
//	private final static Map<String, TpayCsysMerCodeInfo> ORG_CODE_MAP = new HashMap<String, TpayCsysMerCodeInfo>();
	
	@Autowired
	private TpayCsysMerCodeMapper tpayCsysMerCodeMapper;
	private static TpayCsysMerCodeMapper infoMapper;
	
	@Autowired
	private TpayCsysMerCodeh2Mapper tpayCsysMerCodeh2Mapper;
	private static TpayCsysMerCodeh2Mapper infoH2Mapper;
	
	/**
	 * 初始化数据，需要在内存中保存
	 */
	@PostConstruct
	private void init() {
		infoMapper = tpayCsysMerCodeMapper;
		infoH2Mapper = tpayCsysMerCodeh2Mapper;
		
//		MER_CODE_MAP.clear();
//		ORG_CODE_MAP.clear();
//		
//		List<TpayCsysMerCodeInfo> infoList = null;
//		
//		try {
//			//查询内存库
//			infoList = infoH2Mapper.selectAllListByParams(new HashMap<String, Object>(), null);
//		} catch (Exception e) {
//			logger.error("查询内存数据库异常,直接查询物理数据库init",e);
//		}finally{
//			try {
//				if(null == infoList || infoList.size() == 0){
//					//查询物理库
//					infoList = infoMapper.selectAllListByParams(new HashMap<String, Object>(), null);
//				}
//			} catch (Exception e) {
//				infoList = null;
//				logger.error("查询物理数据库异常,init",e);
//			}
//		}
//		
//		//直接覆盖旧数据
//		if(null != infoList && infoList.size() > 0){
//			
//			for (TpayCsysMerCodeInfo info : infoList) {
//				
//				if(StringUtils.isNotBlank(info.getMerId()))
//					MER_CODE_MAP.put(info.getMerId(), info);
//				
//				if(StringUtils.isNotBlank(info.getOrgCode()) && StringUtils.isNotBlank(info.getThrOrgCode()))
//					ORG_CODE_MAP.put(info.getOrgCode() + "#" + info.getThrOrgCode(), info);
//			}
//		}
	}
	
	public void reLoad() {
		init();
	}
	
	/**
	 * 通过商户代码、第三方支付机构号获取商户信息
	 * @param merId
	 * @param thrOrgCode
	 * @return
	 */
	public static TpayCsysMerCodeInfo getMerInfoByMerId(String merId, String thrOrgCode){
		if(StringUtils.isBlank(merId) || StringUtils.isBlank(thrOrgCode)) {
			logger.info("getMerInfoByMerId查询失败！merId:{} thrOrgCode:{}", new Object[]{merId, thrOrgCode});
			return null;
		}
		//从内存中取值
//		TpayCsysMerCodeInfo info = MER_CODE_MAP.get(merId);
//		if(null == info){
			TpayCsysMerCodeInfo info = null;
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("merId", merId);
			params.put("thrOrgCode", thrOrgCode);
			try {
				logger.info("getMerInfoByMerId查询内存数据库 merId:{} thrOrgCode:{} ", new Object[]{merId, thrOrgCode});
				//到内存库取值
				info = infoH2Mapper.selectByParams(params);
			} catch (Exception e) {
				logger.error("getMerInfoByMerId查询内存数据库异常,直接查询物理数据库，merId{},thrOrgCode:{},异常{}", 
						new Object[]{merId, thrOrgCode, e.getMessage()});
			} finally{
				try {
					//到物理库取值
					if(null == info) {
						logger.info("getMerInfoByMerId查询物理数据库merId:{},thrOrgCode:{}", 
								new Object[]{merId, thrOrgCode});						
						info = infoMapper.selectByParams(params);
					}
				} catch (Exception e) {
					info = null;
					logger.error("getMerInfoByMerId查询物理数据库异常，merId{},thrOrgCode:{},异常{}", 
							new Object[]{merId, thrOrgCode, e.getMessage()});
				}
			}
//		}
		
		return info;
	}
	
	/**
	 * 通过机构代码、第三方支付机构代码和商户类型获取商户信息
	 * 或者
	 * 通过机构代码、第三方支付机构代码、商户类型和移动商城自己的商户号获取商户信息（需要加mobileShopMerId参数，加这个参数表明调用者希望获得移动商城自己的商户号对应的第三方支付的商户号）
	 * @param orgCode
	 * @param thrOrgCode
	 * @param merType
	 * 
	 * @return
	 */
	public static TpayCsysMerCodeInfo getMerInfoByOrgCode(String orgCode, String thrOrgCode, String merType, String... mobileShopMerId){
		if(StringUtils.isBlank(orgCode) || StringUtils.isBlank(thrOrgCode) || StringUtils.isBlank(merType)) {
			logger.info("getMerInfoByOrgCode查询失败！orgCode:{} thrOrgCode:{} merType:{} fatherMerId:{}", 
					new Object[]{orgCode, thrOrgCode, merType, (mobileShopMerId != null && mobileShopMerId.length == 1) ? mobileShopMerId[0] : null});
			return null;
		}
//		String key = orgCode + "#" + thrOrgCode;
//		//从内存中取值
//		TpayCsysMerCodeInfo info = ORG_CODE_MAP.get(key);
//		
//		if(null == info){
			TpayCsysMerCodeInfo info = null;
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("orgCode", orgCode);
			params.put("thrOrgCode", thrOrgCode);
			params.put("merType", merType);
			if(mobileShopMerId != null && mobileShopMerId.length == 1) {
				params.put("fatherMerId", mobileShopMerId[0]);
			}
			
			try {
				logger.info("getMerInfoByOrgCode查询内存数据库 orgCode:{} thrOrgCode:{} merType:{} fatherMerId:{}", 
						new Object[]{orgCode, thrOrgCode, merType, (mobileShopMerId != null && mobileShopMerId.length == 1) ? mobileShopMerId[0] : null});
				//到内存库取值
				info = infoH2Mapper.selectByParams(params);
			} catch (Exception e) {
				logger.error("getMerInfoByOrgCode查询内存数据库异常,直接查询物理数据库 orgCode:{} thrOrgCode:{} merType:{} fatherMerId:{} 异常：{}", 
						new Object[]{orgCode, thrOrgCode, merType, (mobileShopMerId != null && mobileShopMerId.length == 1) ? mobileShopMerId[0] : null, e.getMessage()});
			}finally{
				try {
					//到物理库取值
					if(null == info) {
						logger.info("getMerInfoByOrgCode查询物理数据库 orgCode:{} thrOrgCode:{} merType:{} fatherMerId:{}", 
								new Object[]{orgCode, thrOrgCode, merType, (mobileShopMerId != null && mobileShopMerId.length == 1) ? mobileShopMerId[0] : null});
						info = infoMapper.selectByParams(params);
					}
				} catch (Exception e) {
					info = null;
					logger.error("getMerInfoByOrgCode查询物理数据库异常 orgCode:{} thrOrgCode:{} merType:{} fatherMerId:{} 异常：{}", 
							new Object[]{orgCode, thrOrgCode, merType, (mobileShopMerId != null && mobileShopMerId.length == 1) ? mobileShopMerId[0] : null, e.getMessage()});
				}
			}
//		}
		
		return info;
	}

}
