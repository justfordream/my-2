/**
 * 
 */
package com.huateng.cmupay.controller.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huateng.cmupay.controller.mapper.UpayCsysCrmOuterErrcdMapper;
import com.huateng.cmupay.models.UpayCsysCrmOuterErrcd;


/**
 * 错误代码映射关系
 * 
 * @author cmt
 * 
 */

@Component
public class CrmErrorCodeCache {

	private final static Map<String, UpayCsysCrmOuterErrcd> CRM_ERRCODE_MAP = new HashMap<String, UpayCsysCrmOuterErrcd>();

	static final private List<UpayCsysCrmOuterErrcd> CRM_ERRCODE_LIST = new ArrayList<UpayCsysCrmOuterErrcd>();

	@Autowired
	private UpayCsysCrmOuterErrcdMapper upayCsysCrmOuterErrcdMapper;

	
	private static UpayCsysCrmOuterErrcdMapper  errCdMapper ;
	
	
	
	private static String  UPAY = "upay" ;
	private static String  BANK = "bank" ;
	/**
	 * 初始化数据字典
	 * 根据新的业务需求，此表的数据将不保存在内存中，而是直接从物理数据库中取数据
	 * @author cmt
	 */
	@PostConstruct
	private void init() {
		errCdMapper = upayCsysCrmOuterErrcdMapper;
//		CRM_ERRCODE_MAP.clear();
//		CRM_ERRCODE_LIST.clear();
//		
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("errFlag", "0");
//		List<UpayCsysCrmOuterErrcd> infoList = upayCsysCrmOuterErrcdMapper
//				.selectAllListByParams(params, null);
//		CRM_ERRCODE_LIST.addAll(infoList);
//		for (UpayCsysCrmOuterErrcd info : infoList) {
//			UpayCsysCrmOuterErrcd errInfo = null;
//			errInfo = CRM_ERRCODE_MAP.get(info.getPlatformCd()+info.getErrCode());
//			if (errInfo == null) {
//				CRM_ERRCODE_MAP.put(info.getPlatformCd()+info.getErrCode(), info);
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

	/**
	 * @return
	 */
	public static List<UpayCsysCrmOuterErrcd> getErrCodeList() {

		return CRM_ERRCODE_LIST;
	}

	/**
	 * @return
	 */
	public static Map<String, UpayCsysCrmOuterErrcd> getErrCodeMap() {

		return CRM_ERRCODE_MAP;
	}
	
	/**
	 * @return
	 */
	public static UpayCsysCrmOuterErrcd getCrmErrCodeObj(String str) {
		if (str == null || str.trim().equals(""))
			return null;

		Map<String, Object> paramsTmp = new HashMap<String, Object>();
		paramsTmp.put("errFlag", "0");
		
		if(str.startsWith(UPAY)){
			paramsTmp.put("errCode", str.replaceFirst(UPAY, ""));
			paramsTmp.put("platformCd", UPAY);
		} else {
			paramsTmp.put("errCode", str.replaceFirst(BANK, ""));
			paramsTmp.put("platformCd", BANK);
		}

		return errCdMapper.selectByParams(paramsTmp);
	}

//	/**
//	 * @return
//	 */
//	public static UpayCsysCrmOuterErrcd getCrmErrCodeObj(String str) {
//		if (str == null || str.trim().equals(""))
//			return null;
//
//		UpayCsysCrmOuterErrcd code = CRM_ERRCODE_MAP.get(str);
//
//		return code;
//	}
	
	/**
	 * @return
	 * 改成从物理库中取数据
	 */
	public static String getCrmErrCode(String str) {
		if (str == null || str.trim().equals(""))
			return "5A06";
		if (str.length() != 4 && str.length() != 6)
			return "5A06";
		
		Map<String, Object> paramsTmp = new HashMap<String, Object>();
		paramsTmp.put("errFlag", "0");
		paramsTmp.put("errCode", str);
		
		if(str.length() != 4){
			UpayCsysCrmOuterErrcd code = null;
			if(str.startsWith("U")){
				paramsTmp.put("platformCd", UPAY);
			} else {
				paramsTmp.put("platformCd", BANK);
			}
			code = errCdMapper.selectByParams(paramsTmp);
			
			if(code == null || "".equals(code.getInnerErrCode())){
				Map<String ,Object> params = new HashMap<String ,Object>();
				params.put("errCode", str);
				params.put("errFlag", "0");
				if(str.startsWith("U")){
					params.put("platformCd", UPAY);
				}else {
					params.put("platformCd", BANK);
				}
				
				UpayCsysCrmOuterErrcd  errCd =	errCdMapper.selectByParams(params);
			    if(errCd == null || "".equals(errCd.getInnerErrCode())){
			    	return "5A06";
			    }else {
			    	return errCd.getInnerErrCode();
			    }
			}else {
				return code.getInnerErrCode();
			}
		}else {
			return str;
		}
	}
	
//	/**
//	 * @return
//	 */
//	public static String getCrmErrCode(String str) {
//		if (str == null || str.trim().equals(""))
//			return "5A06";
//		if (str.length() != 4 && str.length() != 6)
//			return "5A06";
//		if(str.length()!=4){
//			UpayCsysCrmOuterErrcd code = null;
//			if(str.startsWith("U")){
//				 code = CRM_ERRCODE_MAP.get(UPAY+str);
//			}else {
//				 code = CRM_ERRCODE_MAP.get(BANK+str);
//			}
//			
//			if(code==null||"".equals(code.getInnerErrCode())){
//				Map<String ,Object> params = new HashMap<String ,Object>();
//				params.put("errCode", str);
//				params.put("errFlag", "0");
//				if(str.startsWith("U")){
//					params.put("platformCd", UPAY);
//				}else {
//					params.put("platformCd", BANK);
//				}
//				UpayCsysCrmOuterErrcd  errCd =	errCdMapper.selectByParams(params);
//			    if(errCd==null||"".equals(errCd.getInnerErrCode())){
//			    	return "5A06";
//			    }else {
//			    	return errCd.getInnerErrCode();
//			    }
//			}else {
//				return code.getInnerErrCode();
//			}
//		}else {
//			return str;
//		}
//	}
	
//	@SuppressWarnings("unchecked")
//	public static String getCrmErrCode(String str) {
//		if (str == null || str.trim().equals(""))
//			return "5A06";
//		if (str.length() != 4 && str.length() != 6)
//			return "5A06";
//		if(str.length()!=4){
//			UpayCsysCrmOuterErrcd code = CRM_ERRCODE_MAP.get(str);
//			if(code==null||"".equals(code.getInnerErrCode())){
//				UpayCsysCrmOuterErrcd  errCd =BeanUtil.toBean(UpayCsysCrmOuterErrcd.class,UpayMemCache.getCrmErrCode(str));
//			    if(errCd==null||"".equals(errCd.getInnerErrCode())){
//			    	return "5A06";
//			    }else {
//			    	return errCd.getInnerErrCode();
//			    }
//			}else {
//				return code.getInnerErrCode();
//			}
//			
//			
//		}else {
//			return str;
//		}
//		
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}