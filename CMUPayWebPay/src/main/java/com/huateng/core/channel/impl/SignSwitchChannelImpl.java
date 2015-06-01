package com.huateng.core.channel.impl;

import com.huateng.bean.CmuSignRequest;
import com.huateng.core.channel.SignSwitchChannel;

public class SignSwitchChannelImpl implements SignSwitchChannel {

	public boolean validateLink(CmuSignRequest cmuSignRequest) {
		return false;
	}

	public void transferLink(CmuSignRequest cmuSignRequest) {

	}

}
