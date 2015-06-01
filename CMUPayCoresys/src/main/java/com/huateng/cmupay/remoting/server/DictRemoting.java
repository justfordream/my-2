package com.huateng.cmupay.remoting.server;

import java.util.List;
import java.util.Map;

import com.huateng.cmupay.models.UpayCsysDictCode;
import com.huateng.cmupay.models.common.DataWrapper;



public interface DictRemoting {
	
	
	
	

	DataWrapper<Map<String,UpayCsysDictCode>> getDictCodeMap(String dictId);
	
	

	public DataWrapper<List<UpayCsysDictCode>> getDictCodeList(String dictId);
	

	DataWrapper<UpayCsysDictCode> reLoadDict();
	
	

}
