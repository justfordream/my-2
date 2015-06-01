package main.java.com.huateng.util;

import com.huateng.bundle.PropertyBundle;

public class EnDecryptUtil {
	
	//解密
	public static  boolean isDecrypt(String msgHeader) {
		String provCode = XmlStringUtil.parseNodeValueFromXml("<MsgSender>",
				"</MsgSender>", msgHeader);
		String tranCode =	XmlStringUtil.parseNodeValueFromXml("<ActivityCode>",
				"</ActivityCode>", msgHeader);
		String provs = PropertyBundle.getConfig("decode.province");
		String tranCodePro = PropertyBundle.getConfig("decode.tradecode");
		//为空代表所有省都加密
		if (provs.isEmpty()==true &&tranCodePro.contains(tranCode)==true) {
			return true;
		}
		if (provs.contains(provCode)==true&&tranCodePro.contains(tranCode)==true) {
			return true;
		}
		return false;
	}
	
	//加密
	public static  boolean isEncrypt(String msgBody) {
		String provCode = XmlStringUtil.parseNodeValueFromXml("<MsgSender>",
				"</MsgSender>", msgBody);
		String tranCode =	XmlStringUtil.parseNodeValueFromXml("<ActivityCode>",
				"</ActivityCode>", msgBody);
		String provs = PropertyBundle.getConfig("encode.province");
		String tranCodePro = PropertyBundle.getConfig("encode.tradecode");
		//为空代表所有省都加密
		if (provs.isEmpty()==true &&tranCodePro.contains(tranCode)==true) {
			return true;
		}
		if (provs.contains(provCode)==true&&tranCodePro.contains(tranCode)==true) {
			return true;
		}
		return false;
	}
	
}
