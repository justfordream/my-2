package com.huateng.cmupay.controller.service.system.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.huateng.cmupay.controller.mapper.UpayCsysBatCutCtlMapper;
import com.huateng.cmupay.controller.service.system.IUpayCsysBatCutCtlService;
import com.huateng.cmupay.models.UpayCsysBatCutCtl;


@Service("upayCsysBatCutCtlService")
public class UpayCsysBatCutCtlServiceImpl implements IUpayCsysBatCutCtlService  {
	
	@Autowired
	private UpayCsysBatCutCtlMapper upayCsysBatCutCtlMapper;

	@Override
	
	public UpayCsysBatCutCtl findObjByKey(Long seq) {
		return upayCsysBatCutCtlMapper.selectByPrimaryKey(seq);
	}

	@Override
	//
	//@MyLogAction(logType=LogTypeConstant.BatCutCtl,logDesc="日切日期,取得：$seq",fieldName="seq")
	public String findCutOffDate(Long seq) {
		UpayCsysBatCutCtl upayCsysBatCutCtl =	upayCsysBatCutCtlMapper.selectByPrimaryKey(seq);
		if(upayCsysBatCutCtl==null){
			return null;
		}else {
			return upayCsysBatCutCtl.getCurrDate();
		}
		
	}
	
	

	
	
	
	
	
	
	
	
	
   
}