package com.huateng.cmupay.controller.mapper;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.UpayCsysImsiLdCd;

public interface UpayCsysImsiLdCdMapper extends IBaseMapper<UpayCsysImsiLdCd> {
   List<List<String>> selectProvNoSegment();
   List<UpayCsysImsiLdCd> selectAll();
   void deleteByUnionKey(Map<String,String> key);
   String selectProvinceByMobileNumber(String num);
   List<String> selectProvincesByMobileNumber(String num);
}