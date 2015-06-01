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

import com.huateng.cmupay.controller.mapper.UpayCsysBankOuterErrcdMapper;
import com.huateng.cmupay.models.UpayCsysBankOuterErrcd;

/**
 * 错误代码映射关系
 * 
 * @author cmt
 * 
 */

@Component
public class BankErrorCodeCache {

	private final static Map<String, UpayCsysBankOuterErrcd> BANK_ERRCODE_MAP = new HashMap<String, UpayCsysBankOuterErrcd>();

	static final private List<UpayCsysBankOuterErrcd> BANK_ERRCODE_LIST = new ArrayList<UpayCsysBankOuterErrcd>();

	@Autowired
	private  UpayCsysBankOuterErrcdMapper upayCsysBankOuterErrcdMapper;
	
	private  static  UpayCsysBankOuterErrcdMapper  errcdMapper;
	
	private static String UPAY_UPAY = "upay-upay";
	private static String UPAY = "upay" ;
	private static String CRM = "crm" ;
	private static String BANK = "bank";
	
	

	/**
	 * 初始化数据字典
	 * 根据新的业务需求，此表的数据将不保存在内存中，而是直接从物理数据库中取数据
	 * @author cmt
	 */
	@PostConstruct
	private void init() {
		
		errcdMapper = upayCsysBankOuterErrcdMapper;
//		BANK_ERRCODE_MAP.clear();
//		BANK_ERRCODE_LIST.clear();
//
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("errFlag", "0");
//		List<UpayCsysBankOuterErrcd> infoList = upayCsysBankOuterErrcdMapper
//				.selectAllListByParams(params, null);
//	
//		BANK_ERRCODE_LIST.addAll(infoList);
//		for (UpayCsysBankOuterErrcd info : infoList) {
//			UpayCsysBankOuterErrcd errInfo = null;
//			errInfo = BANK_ERRCODE_MAP.get(info.getPlatformCd()+info.getErrCode());
//			if (errInfo == null) {
//				BANK_ERRCODE_MAP.put(info.getPlatformCd()+info.getErrCode(), info);
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
	public static List<UpayCsysBankOuterErrcd> getErrCodeList() {

		return BANK_ERRCODE_LIST;
	}

	/**
	 * @return
	 */
	public static Map<String, UpayCsysBankOuterErrcd> getErrCodeMap() {

		return BANK_ERRCODE_MAP;
	}
	
	/**
	 * @return
	 */
	public static UpayCsysBankOuterErrcd getBankErrCodeObj(String str) {
		if (str == null || str.trim().equals(""))
			return null;

		Map<String ,Object> params = new HashMap<String ,Object>();
		params.put("errFlag", "0");
		
		if(str.startsWith(UPAY_UPAY)) {
			params.put("platformCd", UPAY_UPAY);
			params.put("errCode", str.replaceFirst(UPAY_UPAY, ""));
		} else if(str.startsWith(UPAY)) {
			params.put("platformCd", UPAY);
			params.put("errCode", str.replaceFirst(UPAY, ""));
		} else if(str.startsWith(BANK)) {
			params.put("platformCd", BANK);
			params.put("errCode", str.replaceFirst(BANK, ""));
		} else if(str.startsWith(CRM)) {
			params.put("platformCd", CRM);
			params.put("errCode", str.replaceFirst(CRM, ""));
		}

		return errcdMapper.selectByParams(params);
	}

//	/**
//	 * @return
//	 */
//	public static UpayCsysBankOuterErrcd getBankErrCodeObj(String str) {
//		if (str == null || str.trim().equals(""))
//			return null;
//
//		UpayCsysBankOuterErrcd code = BANK_ERRCODE_MAP.get(str);
//
//		return code;
//	}
	
	public static String getBankErrCode(String str) {
		if (str == null || str.trim().equals(""))
			return "015A06";
		
		if (str.length() != 4 && str.length() != 6)
			return "015A06";
		if(str.length()==6&&str.startsWith("U")){
			UpayCsysBankOuterErrcd code = BANK_ERRCODE_MAP.get(UPAY+str);
			if(code==null||"".equals(code.getInnerErrCode())){
				Map<String ,Object> params = new HashMap<String ,Object>();
				params.put("platformCd", UPAY);
				params.put("errCode", str);
				params.put("errFlag", "0");
				UpayCsysBankOuterErrcd  errCd =	errcdMapper.selectByParams(params);
			    if(errCd==null||"".equals(errCd.getInnerErrCode())){
			    	return "015A06";
			    }else {
			    	return errCd.getInnerErrCode();
			    }
			}else {
				return code.getInnerErrCode();
			}

		}else if(str.length()==4) {
			UpayCsysBankOuterErrcd code = BANK_ERRCODE_MAP.get(CRM+str);
			if(code==null||"".equals(code.getInnerErrCode())){
				Map<String ,Object> params = new HashMap<String ,Object>();
				params.put("platformCd", CRM);
				params.put("errCode", str);
				params.put("errFlag", "0");
				UpayCsysBankOuterErrcd  errCd =	errcdMapper.selectByParams(params);
			    if(errCd==null||"".equals(errCd.getInnerErrCode())){
			    	return "015A06";
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
	

	/*@SuppressWarnings("unchecked")
	public static String getBankErrCode(String str) {
		if (str == null || str.trim().equals(""))
			return "015A06";
		if (str.length() != 4 && str.length() != 6)
			return "015A06";
		if(str.length()==6&&str.startsWith("U")){
			UpayCsysBankOuterErrcd code = BANK_ERRCODE_MAP.get(str);
			if(code==null||"".equals(code.getInnerErrCode())){
				UpayCsysBankOuterErrcd  errCd =	BeanUtil.toBean(UpayCsysBankOuterErrcd.class,UpayMemCache.getBankErrCode(str));
			    if(errCd==null||"".equals(errCd.getInnerErrCode())){
			    	return "015A06";
			    }else {
			    	return errCd.getInnerErrCode();
			    }
			}else {
				return code.getInnerErrCode();
			}

		}else if(str.length()==4) {
			UpayCsysBankOuterErrcd code = BANK_ERRCODE_MAP.get(str);
			if(code==null||"".equals(code.getInnerErrCode())){
				UpayCsysBankOuterErrcd  errCd1 =BeanUtil.toBean(UpayCsysBankOuterErrcd.class,UpayMemCache.getBankErrCode(str));
			    if(errCd1==null||"".equals(errCd1.getInnerErrCode())){
			    	return "015A06";
			    }else {
			    	return errCd1.getInnerErrCode();
			    }
			}else {
				return code.getInnerErrCode();
			}
		}else {
			return str;
		}
	}*/
	
	
	
	
	
	

}
