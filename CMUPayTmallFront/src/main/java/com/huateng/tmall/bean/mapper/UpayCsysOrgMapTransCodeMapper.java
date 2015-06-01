package com.huateng.tmall.bean.mapper;


import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huateng.tmall.bean.UpayCsysOrgMapTransCode;

public interface UpayCsysOrgMapTransCodeMapper extends IBaseMapper<UpayCsysOrgMapTransCode> {
	//查询联通号码权限
	UpayCsysOrgMapTransCode selectByUnParams(@Param(value="params") Map<String,Object> params);
}