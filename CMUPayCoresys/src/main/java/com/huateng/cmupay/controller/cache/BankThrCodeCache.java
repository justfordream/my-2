package com.huateng.cmupay.controller.cache;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.huateng.cmupay.controller.h2.mapper.TUpayBankThrCodeh2Mapper;
import com.huateng.cmupay.controller.mapper.TUpayBankThrCodeMapper;
import com.huateng.cmupay.models.TUpayBankThrCode;

@Component
public class BankThrCodeCache {
	
	protected static final Logger logger = LoggerFactory
			.getLogger("BankThrCodeCache");
	
	@Autowired
	public TUpayBankThrCodeMapper mapper;
	public static TUpayBankThrCodeMapper tUpayBankThrCodeMapper;
	
	@Autowired
	public TUpayBankThrCodeh2Mapper h2mapper;
	public static TUpayBankThrCodeh2Mapper tUpayBankThrCodeh2Mapper;
	
	@PostConstruct
	public void init(){
		tUpayBankThrCodeMapper = mapper;
		tUpayBankThrCodeh2Mapper = h2mapper;
	}
	
	public static TUpayBankThrCode getBankThrCode(TUpayBankThrCode tUpayBankThrCode){
		TUpayBankThrCode o3o =null;
		Map<String,Object> params =new HashMap<String,Object>();
		if(tUpayBankThrCode!=null){
			params.put("bankId", tUpayBankThrCode.getBankId());
			params.put("bankName", tUpayBankThrCode.getBankName());
			params.put("thrOrgId", tUpayBankThrCode.getThrOrgId());
			params.put("thrBankId", tUpayBankThrCode.getThrBankId());
		}
		try{
			o3o=tUpayBankThrCodeh2Mapper.selectByParams(params);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(o3o==null){
				o3o=tUpayBankThrCodeMapper.selectByParams(params);
			}
		}
		return o3o;
	}
}

