package com.huateng.cmupay.controller.service.system.impl;

import org.springframework.stereotype.Service;

import com.huateng.cmupay.controller.service.system.IUpayCsysSeqMapInfoService;
import com.huateng.cmupay.utils.SeqIdSerial;
import com.huateng.toolbox.utils.StringUtil;

/**
 * @author cmt
 * @version 创建时间：2013-3-17 下午2:40:33 类说明
 */
@Service("upayCsysSeqMapInfoService")
public class UpayCsysSeqMapInfoServiceImpl implements IUpayCsysSeqMapInfoService {
//	@Autowired
//	private UpayCsysSeqMapInfoMapper upayCsysSeqMapInfoMapper;

	@Override
	public Long selectSeqValue(Integer seqCode) {
		/*String seq = null;
		synchronized (UpayCsysSeqMapInfoServiceImpl.class) {
			upayCsysSeqMapInfoMapper.updateIdentity(seqCode);
			Long seqValue = upayCsysSeqMapInfoMapper.selectIdentity(seqCode);
			seq = "1".concat(StringUtil.fillLeft(String.valueOf(seqValue), '0',
					17));
		}
		*/
		String seq = StringUtil.fillLeft(String.valueOf(SeqIdSerial.genSeqId("")), '0' , 19);
		
		return Long.parseLong(seq);
	}
	
	
	@Override
	public void updateIdentity(Integer seqCode) {

	}

	@Override
	public Long selectIdentity(Integer seqCode) {
		return null;
	}


	@Override
	public void updateIdsatity(Integer seqCode) {
		
	}


	@Override
	public Long selectIdsatity(Integer seqCode) {
		return null;
	}

}
