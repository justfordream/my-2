package com.huateng.core.channel;

import com.huateng.bean.AliData;
import com.huateng.bean.CoreResultPayRes;
import com.huateng.vo.MsgData;

public interface AliPaySwitchChannel {

	public void validateAliPayLink(AliData aliData);

	public MsgData transferAliPayLink(CoreResultPayRes coreRsp);

}
