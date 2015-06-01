package com.huateng.cmupay.remoting.server.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huateng.cmupay.controller.cache.DictCodeCache;
import com.huateng.cmupay.models.UpayCsysDictCode;
import com.huateng.cmupay.models.common.DataWrapper;
import com.huateng.cmupay.remoting.server.DictRemoting;

@Service("dictRemoting")
public class DictRemotingImpl implements DictRemoting {

	@Autowired
	private DictCodeCache dictCache;

	@Override
	public DataWrapper<Map<String, UpayCsysDictCode>> getDictCodeMap(
			String dictId) {
		DataWrapper<Map<String, UpayCsysDictCode>> dataWrapper = new DataWrapper<Map<String, UpayCsysDictCode>>();
		//dataWrapper.setResultCode(RspCodeConstant.Success.toString());
		dataWrapper.setResultData(DictCodeCache.getDictCodeMap(dictId));
		return dataWrapper;
	}

	@Override
	public DataWrapper<List<UpayCsysDictCode>> getDictCodeList(String dictId) {
		DataWrapper<List<UpayCsysDictCode>> dataWrapper = new DataWrapper<List<UpayCsysDictCode>>();
		//dataWrapper.setResultCode(RspCodeConstant.Success.toString());
		dataWrapper.setResultData(DictCodeCache.getDictCodeList(dictId));
		return dataWrapper;
	}

	@Override
	public DataWrapper<UpayCsysDictCode> reLoadDict() {

		DataWrapper<UpayCsysDictCode> dataWrapper = new DataWrapper<UpayCsysDictCode>();
		//dataWrapper.setResultCode(RspCodeConstant.Success.toString());

		dictCache.reLoad();

		return dataWrapper;
	}

}
