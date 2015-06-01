package com.huateng.cmupay.controller.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huateng.cmupay.controller.mapper.UpayCsysImsiLdCdMapper;
import com.huateng.cmupay.models.UpayCsysImsiLdCd;

/**
 * 号码段区域
 * @author zeng.j
 *
 */
@Component
public class NoSegmentAreaCache {
	/**号码段--号码段信息cache*/
	private static final Map<String,UpayCsysImsiLdCd> NOSEGMENTAREA_MAP = new HashMap<String,UpayCsysImsiLdCd>();
	/**号码段信息cache*/
	private static final List<UpayCsysImsiLdCd> NOSEGMENTAREA_LIST = new ArrayList<UpayCsysImsiLdCd>();
	/**号码段--省代码映射cache*/
	private static final Map<String,String> NOSEGMENTPROV_MAP = new HashMap<String,String>();
	@Autowired
	private UpayCsysImsiLdCdMapper upayCsysImsiLdCdMapper;
	
	//@PostConstruct
	private void init(){
		NOSEGMENTAREA_MAP.clear();
		NOSEGMENTAREA_LIST.clear();
		NOSEGMENTPROV_MAP.clear();
				
		List<UpayCsysImsiLdCd> all = upayCsysImsiLdCdMapper.selectAllListByParams(new HashMap<String,Object>(), null);
		NOSEGMENTAREA_LIST.addAll(all);
		for(UpayCsysImsiLdCd noSegmentArea:NOSEGMENTAREA_LIST){
			UpayCsysImsiLdCd noSegment = NOSEGMENTAREA_MAP.get(noSegmentArea.getMsisdnAreaId() + noSegmentArea.getEffcTm());
			if(null == noSegment){
				NOSEGMENTAREA_MAP.put(noSegmentArea.getMsisdnAreaId()+ noSegmentArea.getEffcTm(), noSegmentArea);
			}
		}		
		List<List<String>> provNoSegment = upayCsysImsiLdCdMapper.selectProvNoSegment();
		for(List<String> list:provNoSegment){
			String prov = NOSEGMENTPROV_MAP.get(list.get(1));
			if(null==prov){
				NOSEGMENTPROV_MAP.put(list.get(1), list.get(0));
			}
		}
	}
	
	public void reload(){
		init();
	}
	
	public List<UpayCsysImsiLdCd> getNoSegmentList(){
		return NOSEGMENTAREA_LIST;
	}
	
	public Map<String,UpayCsysImsiLdCd> getNoSegmentMap(){
		return NOSEGMENTAREA_MAP;
	}
	
	public UpayCsysImsiLdCd getNoSegmentAreaByPrimaryKey(String noSegMent){
		return NOSEGMENTAREA_MAP.get(noSegMent);
	}
	
	public String getProvince(String key){
		String noPre7 = key.substring(0,7);
		return NOSEGMENTPROV_MAP.get(noPre7);
	}
}
