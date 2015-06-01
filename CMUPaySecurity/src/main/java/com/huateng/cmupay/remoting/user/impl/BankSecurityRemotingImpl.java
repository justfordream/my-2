package com.huateng.cmupay.remoting.user.impl;

import org.springframework.stereotype.Service;

import com.huateng.cmupay.models.BankSecurityVo;
import com.huateng.cmupay.models.common.DataWrapper;
import com.huateng.cmupay.remoting.user.BankSecurityRemoting;

/**
 * 
 * @author cmt
 *
 */
@Service("bankSecurityRemoting")
public class BankSecurityRemotingImpl implements BankSecurityRemoting {

	
	
	public DataWrapper<BankSecurityVo> digest(BankSecurityVo bankSecurityVo) {
		// TODO Auto-generated method stub
		
		DataWrapper<BankSecurityVo> dataWrapper = new DataWrapper<BankSecurityVo>();
		
		
		return dataWrapper;
	}

	

	
	
}
