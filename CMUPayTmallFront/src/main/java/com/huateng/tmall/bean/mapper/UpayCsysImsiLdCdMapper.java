package com.huateng.tmall.bean.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huateng.tmall.bean.UpayCsysImsiLdCd;

public interface UpayCsysImsiLdCdMapper extends IBaseMapper<UpayCsysImsiLdCd> {

	String selectProvinceByMobileNumber(String num);

	String selectProvinceByUnicomNumber(String substring);
	
	String selectErrcode(@Param(value="params") Map<String,Object> params);

}