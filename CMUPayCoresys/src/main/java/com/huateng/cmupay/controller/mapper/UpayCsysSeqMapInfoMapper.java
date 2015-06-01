package com.huateng.cmupay.controller.mapper;

import com.huateng.cmupay.models.UpayCsysSeqMapInfo;

/**
 * @author cmt
 *
 */
public interface UpayCsysSeqMapInfoMapper  extends IBaseMapper<UpayCsysSeqMapInfo>{
	
	void updateIdentity(Integer seqCode );
	Long selectIdentity(Integer seqCode);
  
	void updateIdsatity(Integer seqCode );
	Long selectIdsatity(Integer seqCode);
}