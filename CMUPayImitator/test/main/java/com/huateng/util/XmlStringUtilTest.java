package main.java.com.huateng.util;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

public class XmlStringUtilTest {
	private String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<InterBOSS>" + "<Version>0100</Version>"
			+ "<TestFlag>1</TestFlag>" + "<BIPType>"
			+ "<BIPCode>BIP1A156</BIPCode>"
			+ "<ActivityCode>T1000153</ActivityCode>"
			+ "<ActionCode>0</ActionCode>" + "</BIPType>" + "<RoutingInfo>"
			+ "<OrigDomain>UPSS</OrigDomain>" + "<RouteType>01</RouteType>"
			+ "<Routing>" + "<HomeDomain>BOSS</HomeDomain>"
			+ "<RouteValue>15011427287</RouteValue>" + "</Routing>"
			+ "</RoutingInfo>" + "<TransInfo>"
			+ "<SessionID>101020130628103150000142524369</SessionID>"
			+ "<TransIDO>101020130628103150000142524369</TransIDO>"
			+ "<TransIDOTime>20130628103150</TransIDOTime>" + "</TransInfo>"
			+ "<SvcCont>" + "<AuthReq>" + "<DealType>02</DealType>"
			+ "<SubID>0100055220130628634147</SubID>" + "<IDType>01</IDType>"
			+ "<IDValue>15011427287</IDValue>" + "<BankID>0005</BankID>"
			+ "<BankAcctType>0</BankAcctType>"
			+ "<BankAcctID>6225212500000044</BankAcctID>"
			+ "<TransactionID>101020130628103150000142524369</TransactionID>"
			+ "<SubTime>20130628103042</SubTime>"
			+ "<ActionDate>20130628</ActionDate>" + "<CnlTyp>52</CnlTyp>"
			+ "<PayType>1</PayType>" + "<RechAmount>5000</RechAmount>"
			+ "<RechThreshold>1000</RechThreshold>" + "</AuthReq>"
			+ "</SvcCont>" + "</InterBOSS>";

	@Test
//	@Ignore
	public void testRelaceNodeContent() {
		String target = XmlStringUtil.relaceNodeContent("<TransIDO>",
				"</TransIDO>", IDGenerator.genTransIDH(), xml);
		System.out.println("target xml:" + target);
	}

	@Test
	public void testParseNodeValueFromXml() {
		String transIDO = XmlStringUtil.parseNodeValueFromXml("<TransIDO>",
				"</TransIDO>", xml);
		System.out.println("transIDO parse from xml:" + transIDO);
		Assert.assertEquals("101020130628103150000142524369", transIDO);
		String transIDOTime = XmlStringUtil.parseNodeValueFromXml(
				"<TransIDOTime>", "</TransIDOTime>", xml);
		System.out.println("transIDOTime parse from xml:" + transIDOTime);
		Assert.assertEquals("20130628103150", transIDOTime);
		String sessionID = XmlStringUtil.parseNodeValueFromXml("<SessionID>",
				"</SessionID>", xml);
		System.out.println("sessionID parse from xml:" + sessionID);
		Assert.assertEquals("101020130628103150000142524369", sessionID);
		String transactionID = XmlStringUtil.parseNodeValueFromXml(
				"<TransactionID>", "</TransactionID>", xml);
		System.out.println("transactionID parse from xml:" + transactionID);
		Assert.assertEquals("101020130628103150000142524369", transactionID);
	}

	@Test
	@Ignore
	public void testGetNodeValFromXml() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetNodeValFromStream() {
		fail("Not yet implemented");
	}

}
