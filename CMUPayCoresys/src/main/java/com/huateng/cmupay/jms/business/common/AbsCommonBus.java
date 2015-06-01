package com.huateng.cmupay.jms.business.common; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huateng.cmupay.constant.DictConst;
import com.huateng.cmupay.controller.cache.DictCodeCache;
import com.huateng.cmupay.controller.service.system.IUpayCsysTxnLogService;
import com.huateng.cmupay.exception.AppBizException;
import com.huateng.cmupay.exception.AppRTException;
import com.huateng.cmupay.logFormat.MessageLogger;
import com.huateng.cmupay.models.UpayCsysBindInfo;
import com.huateng.cmupay.models.UpayCsysTxnLog;


/** 
 * @author cmt  
 * @version 创建时间：2013-3-9 上午11:05:20 
 * 类说明 
 * @param <T>
 */
public abstract class AbsCommonBus<T,R,V> implements ICommonBus<T,R> {
	public AbsCommonBus(){
		this.testFlag = DictCodeCache.getDictCode(
				DictConst.DictId.TestFlag.getValue(),
				DictConst.CodeId.TestFlag.getValue()).getCodeValue2();
	}
	protected String testFlag;
	@Autowired
	protected IUpayCsysTxnLogService uPayCsysTxnLogService;
	
	protected  final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected MessageLogger log = MessageLogger.getLogger(this.getClass());

	public abstract R execute(T msgVo ,V params,UpayCsysTxnLog txnLog ,UpayCsysBindInfo bindInfo
			) throws AppRTException, AppBizException ,Exception;
}


