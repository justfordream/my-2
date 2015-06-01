package com.huateng.crm.bean.message.head;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import com.huateng.core.exception.ServiceException;
import com.huateng.crm.common.parse.Dom4jXMLParser;

/**
 * 请求报文头
 * 
 * @author Gary
 * 
 */
public class InterBOSS {	
	/**
	 * 报文版本号(0100，对于同一交易应答与请求版本号始终一致)
	 */
	private String Version;
	/**
	 * 测试标记(发起方填写 0：非测试交易;1：测试交易)
	 */
	private String TestFlag;
	/**
	 * 交易类型信息
	 */
	private BIPType BIPType;
	/**
	 * 交易路由信息
	 */
	private RoutingInfo RoutingInfo;
	/**
	 * 交易流水信息
	 */
	private TransInfo TransInfo;
	/**
	 * 机构不填，SN保留信息(落地方在接到请求时、发起方在接到应答时读取)
	 */
	private SNReserve SNReserve;
	/**
	 * 应答节点(落地机构应答时填写)
	 */
	private Response Response;
	/**
	 * 业务应答内容(系统大圈类交易，落地机构应答时填写，XML格式的字符串，以CDATA区表达。
	 * 落地机构应答时，将各业务分册的交易报文中应答SvcCont的内容填入。 若业务分册中某交易报文的应答为空，则落地机构应答时无此SvcCont节点。
	 * )
	 */
	private String SvcCont;

	public String getVersion() {
		return Version;
	}

	public void setVersion(String version) {
		Version = version;
	}

	public String getTestFlag() {
		return TestFlag;
	}

	public void setTestFlag(String testFlag) {
		TestFlag = testFlag;
	}

	public BIPType getBIPType() {
		return BIPType;
	}

	public void setBIPType(BIPType bIPType) {
		BIPType = bIPType;
	}

	public RoutingInfo getRoutingInfo() {
		return RoutingInfo;
	}

	public void setRoutingInfo(RoutingInfo routingInfo) {
		RoutingInfo = routingInfo;
	}

	public TransInfo getTransInfo() {
		return TransInfo;
	}

	public void setTransInfo(TransInfo transInfo) {
		TransInfo = transInfo;
	}

	public SNReserve getSNReserve() {
		return SNReserve;
	}

	public void setSNReserve(SNReserve sNReserve) {
		SNReserve = sNReserve;
	}

	public Response getResponse() {
		return Response;
	}

	public void setResponse(Response response) {
		Response = response;
	}

	public String getSvcCont() {
		return SvcCont;
	}

	public void setSvcCont(String svcCont) {
		SvcCont = svcCont;
	}

	/**
	 * 获取密文
	 * 
	 * @return
	 */
	public String getEncryptText() {

		return this.SvcCont;

	}

	/**
	 * 获得报文头
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public String getHeader() throws ServiceException {
		// clone
		String xmlContent = "";
		try {
			InterBOSS boss = new InterBOSS();
			BeanUtils.copyProperties(boss, this);
			boss.setSvcCont(null);
			xmlContent = Dom4jXMLParser.parseInterBOSS(boss);
		} catch (Exception e) {
			//logger.error("", e);
			throw new ServiceException("UPAY-C-0105");
		}
		return xmlContent;

	}

	/**
	 * 获得报文体
	 * 
	 * @return
	 */
	public String getBody() {
		StringBuffer sb = new StringBuffer();
		if(StringUtils.isNotBlank(this.SvcCont)){
			
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<InterBOSS>");
			sb.append("<SvcCont>");
			sb.append("<![CDATA[");
			sb.append(this.SvcCont);
			sb.append("]]>");
			sb.append("</SvcCont>");
			sb.append("</InterBOSS>");
		}
		return sb.toString();
	}

	public String getXmlContent() throws ServiceException {
		String xmlContent = "";
		try {
			InterBOSS boss = new InterBOSS();
			BeanUtils.copyProperties(boss, this);
			if(StringUtils.isNotBlank(boss.getSvcCont())){
				boss.setSvcCont("<![CDATA[" + boss.getSvcCont() + "]]>");
			}
			xmlContent = Dom4jXMLParser.parseInterBOSS(boss);
		} catch (IllegalAccessException e) {
			//logger.error("", e);
			throw new ServiceException("UPAY-C-5A06");
		} catch (InvocationTargetException e) {
			//logger.error("", e);
			throw new ServiceException("UPAY-C-5A06");
		}
		return xmlContent;

	}
}
