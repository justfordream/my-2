package com.huateng.tmall.bean.mapper;

import com.huateng.tmall.bean.TMallEmergencyInfo;

public interface TMallEmergencyInfoMapper extends
		IBaseMapper<TMallEmergencyInfo> {
	String selectTmallEmergencyFlag();
}
