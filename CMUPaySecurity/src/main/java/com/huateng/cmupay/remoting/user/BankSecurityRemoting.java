/**
 * 
 */
package com.huateng.cmupay.remoting.user;

import com.huateng.cmupay.models.BankSecurityVo;
import com.huateng.cmupay.models.common.DataWrapper;



/**
 * 
 * @author cmt
 *
 */
public interface BankSecurityRemoting {

	
	

	DataWrapper<BankSecurityVo> digest(BankSecurityVo userBasic);
	
	

}
