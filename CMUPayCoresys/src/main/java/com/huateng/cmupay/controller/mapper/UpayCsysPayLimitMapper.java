package com.huateng.cmupay.controller.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huateng.cmupay.models.UpayCsysPayLimit;


public interface UpayCsysPayLimitMapper extends IBaseMapper<UpayCsysPayLimit>{
	
	
	public UpayCsysPayLimit selectByParamsLock(@Param(value="params") Map<String,Object> params);
   
}