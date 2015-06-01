package com.huateng.cmupay.controller.cache;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huateng.cmupay.constant.CommonConstant;
import com.huateng.cmupay.controller.h2.mapper.UnicomMsisdnLdCdh2Mapper;
import com.huateng.cmupay.controller.h2.mapper.UpayCsysImsiLdCdh2Mapper;
import com.huateng.cmupay.controller.mapper.UnicomMsisdnLdCdMapper;
import com.huateng.cmupay.controller.mapper.UpayCsysImsiLdCdMapper;
import com.huateng.cmupay.models.ProvincePhoneNum;
import com.huateng.toolbox.utils.StrUtil;

/**
 * 省代码区域代码
 * @author zeng.j
 *
 */
@Component
public class ProvAreaCache {
	protected static final  Logger logger = LoggerFactory.getLogger("ProvAreaCache");
	
	
	@Autowired
	private UpayCsysImsiLdCdMapper mapper;
	private static UpayCsysImsiLdCdMapper  upayCsysImsiLdCdMapper;
	
	@Autowired
	private UpayCsysImsiLdCdh2Mapper h2mapper;
	private static UpayCsysImsiLdCdh2Mapper  upayCsysImsiLdCdh2Mapper;
	
	// 联通和电信物理映射表
	@Autowired
	private UnicomMsisdnLdCdMapper ldMapper;
	private static UnicomMsisdnLdCdMapper  upayCsysImsiLdCdLdMapper;	
	
	// 联通和电信内存数据库映射表
	@Autowired
	private UnicomMsisdnLdCdh2Mapper ldH2mapper;
	private static UnicomMsisdnLdCdh2Mapper  upayCsysImsiLdCdLdh2Mapper;
	
	@PostConstruct
	private void init(){
		upayCsysImsiLdCdMapper = mapper;
		upayCsysImsiLdCdh2Mapper =h2mapper;
		
		upayCsysImsiLdCdLdMapper = ldMapper;
		upayCsysImsiLdCdLdh2Mapper = ldH2mapper;
	}

	public void reload(){
		init();
	}	
	
//	public static String getProvAreaByPrimary(String str) {
//		if (str == null || str.trim().equals(""))
//			return null;
//		String reqProvince =null;
//		try {
//			logger.info("getProvAreaByPrimary查询物内存据库idvalue:{}",str);
////			reqProvince = UpayMemCache.findProvinceByMobileNumber(str);
////			return reqProvince;
//			if (!StringUtils.isBlank(str) && str.length() >= 7) {
//				String  mobile7 = StrUtil.subString(str, 0, 7);
//				String prov = upayCsysImsiLdCdh2Mapper
//						.selectProvinceByMobileNumber(mobile7);
//				reqProvince = StringUtils.isBlank(prov) ? null : prov;
////				logger.info("查询Province:{}",reqProvince);
////				return reqProvince;
//			} 
//			
//		} catch (Exception e) {
//			logger.error("查询内存数据库异常,getProvAreaByPrimary直接查询物理数据库idvalue:",e);
//			//return null;
//		}finally{
//			if (null == reqProvince || "".equals(reqProvince) || "null".equalsIgnoreCase(reqProvince)) {
//				logger.info("查询内存数据库异常,getProvAreaByPrimary直接查询物理数据库idvalue:{}",str);
//				if (!StringUtils.isBlank(str) && str.length() >= 7) {
//					String  mobile7 = StrUtil.subString(str, 0, 7);
//					String prov = upayCsysImsiLdCdMapper
//							.selectProvinceByMobileNumber(mobile7);
//					reqProvince = StringUtils.isBlank(prov) ? null : prov;
//					//return reqProvince;
//				} 
//			}
//		}
//		logger.info("查询Province:{}",reqProvince);
//		return reqProvince;
//
//	}
	
	public static ProvincePhoneNum getProvAreaByPrimary(String str) {
		if (str == null || str.trim().equals(""))
			return null;
		ProvincePhoneNum reqProvince = new ProvincePhoneNum();
		try {
			logger.info("getProvAreaByPrimary查询物内存据库idvalue:{}",str);
			if (!StringUtils.isBlank(str) && str.length() >= 7) {
				String  mobile7 = StrUtil.subString(str, 0, 7);
				String prov = upayCsysImsiLdCdh2Mapper
						.selectProvinceByMobileNumber(mobile7);
				reqProvince.setProvinceCode(StringUtils.isBlank(prov) ? null : prov);
				
				
				if(reqProvince.getProvinceCode() != null) {
					// 设置标记为移动的号码
					reqProvince.setPhoneNumFlag(CommonConstant.PhoneNumType.CHINA_MOBILE.getType());
				} else {
					logger.info("移动内存据库idvalue:{}查找不到，开始查询联通电信内存数据库",str);
					// 去联通和电信表中查找
					prov = upayCsysImsiLdCdLdh2Mapper
							.selectProvinceByMobileNumber(mobile7);
					reqProvince.setProvinceCode(StringUtils.isBlank(prov) ? null : prov);
					if(reqProvince.getProvinceCode() != null) {
						// 设置标记为联通或电信的号码
						reqProvince.setPhoneNumFlag(CommonConstant.PhoneNumType.UNICOM_TELECOM.getType());
					}
				}
			} 
			
		} catch (Exception e) {
			logger.error("查询内存数据库异常,getProvAreaByPrimary直接查询物理数据库idvalue:",e);
			reqProvince.setProvinceCode(null);
			reqProvince.setPhoneNumFlag(CommonConstant.PhoneNumType.UNKNOW_PHONENUM.getType());
			return reqProvince;
		}finally{
			String provinceCode = reqProvince.getProvinceCode();
			if (null == provinceCode || "".equals(provinceCode) || "null".equalsIgnoreCase(provinceCode)) {
				logger.info("查询移动内存数据库异常,getProvAreaByPrimary直接查询物理数据库idvalue:{}",str);
				if (!StringUtils.isBlank(str) && str.length() >= 7) {
					String  mobile7 = StrUtil.subString(str, 0, 7);
					String prov = upayCsysImsiLdCdMapper
							.selectProvinceByMobileNumber(mobile7);
					reqProvince.setProvinceCode(StringUtils.isBlank(prov) ? null : prov);
					
					if(reqProvince.getProvinceCode() != null) {
						// 设置标记为移动的号码
						reqProvince.setPhoneNumFlag(CommonConstant.PhoneNumType.CHINA_MOBILE.getType());
					}
				} 
			}
			
			provinceCode = reqProvince.getProvinceCode();
			if (null == provinceCode || "".equals(provinceCode) || "null".equalsIgnoreCase(provinceCode)) {
				logger.info("移动数据库查找不到,getProvAreaByPrimary直接查询联通电信的物理数据库idvalue:{}",str);
				if (!StringUtils.isBlank(str) && str.length() >= 7) {
					String  mobile7 = StrUtil.subString(str, 0, 7);
					String prov = upayCsysImsiLdCdLdMapper
							.selectProvinceByMobileNumber(mobile7);
					reqProvince.setProvinceCode(StringUtils.isBlank(prov) ? null : prov);
					
					if(reqProvince.getProvinceCode() != null) {
						// 设置标记为联通或电信的号码
						reqProvince.setPhoneNumFlag(CommonConstant.PhoneNumType.UNICOM_TELECOM.getType());
					}
				} 
			}
		}
		logger.info("查询Province:{}",reqProvince);
		return reqProvince;

	}
	
}
