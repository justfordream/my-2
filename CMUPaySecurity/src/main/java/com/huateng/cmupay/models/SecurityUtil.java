package com.huateng.cmupay.models;

import java.util.Map;


import com.huateng.cmupay.cert.CertHelper;
import com.huateng.cmupay.constant.CommonConstant;

public class SecurityUtil {

	/**
	 * 获取省份的配置信息
	 * @param certMaps
	 * @param cmuId
	 * @return
	 */
	public static CertHelper getCmuTestFlag(Map<String, Map<String, CertHelper>> certMaps,String cmuId,String singure_cmu_test_flag){
		CertHelper certHelper=null;
		Map<String, CertHelper> helperObj = certMaps.get(CommonConstant.CMU);
		if(CommonConstant.TEST_FLAG_CLOSE.equals(singure_cmu_test_flag)){
			certHelper = helperObj.get(CommonConstant.DEFAULT);
		}else{
		    certHelper = helperObj.get(cmuId);
			if (certHelper == null) {
				certHelper = helperObj.get(CommonConstant.DEFAULT);
			}
		}
		return certHelper;
	}
	/**
	 * 获取银行的配置信息
	 * @param certMaps
	 * @param cmuId
	 * @return
	 */
	public static CertHelper getBankTestFlag(Map<String, Map<String, CertHelper>> certMaps,String bankId,String singure_bank_test_flag){
		CertHelper certHelper=null;
		Map<String, CertHelper> helperObj = certMaps.get(CommonConstant.BANK);
		if(CommonConstant.TEST_FLAG_CLOSE.equals(singure_bank_test_flag)){
			certHelper = helperObj.get(CommonConstant.DEFAULT);
		}else{
		    certHelper = helperObj.get(bankId);
			if (certHelper == null) {
				certHelper = helperObj.get(CommonConstant.DEFAULT);
			}
		}
		return certHelper;
	}
}
