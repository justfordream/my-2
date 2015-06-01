package com.huateng.cmupay.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.cmupay.service.TUPayRemoteService;
import com.huateng.cmupay.util.MD5Util;
import com.toolbox.md5.Md5;
import com.unionpay.mpi.CertUtil;
import com.unionpay.mpi.MpiConfig;
import com.unionpay.mpi.MpiUtil;

/**
 * 银联的签名验签实现类
 * 
 * @author hys
 * 
 */
public class TUPayRemoteServiceImpl implements TUPayRemoteService {

	private static Logger logger = LoggerFactory
			.getLogger("TUPayRemoteServiceImpl");

	// 加载银联的配置文件的路径
	private String tupay_cert_path;

	public String getTupay_cert_path() {
		return tupay_cert_path;
	}

	public void setTupay_cert_path(String tupay_cert_path) {
		this.tupay_cert_path = tupay_cert_path;
	}

	/**
	 * 初始化配置文件
	 */
	public void init() {
		logger.info("开始加载银联的配置文件" + tupay_cert_path);
		MpiConfig.getConfig().loadPropertiesFromPath(tupay_cert_path);
	}

	@Override
	public Map<String, String> sign(String certId, Map<String, String> signDate) {
		logger.info("银联对参数开始签名,订单号:{}", signDate.get("orderId"));
		StringBuffer signStr = new StringBuffer();
		Map<String,String> signNewDate=new HashMap<String,String>();
		if (null != signDate && !signDate.isEmpty()) {
			Iterator<Entry<String, String>> it = signDate.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				if( null != value && !"null".equals(value)){
					signStr.append(key + "=" + value);
					signNewDate.put(key, value);
				}
			}
			logger.info("签名的原字符串:{}", signStr.toString());
			init();
			signNewDate.put("certId", this.getSignCertId());
			MpiUtil.sign(signNewDate, "");
			logger.info("银联签名完成，订单号:{},签名结果:{}", signNewDate.get("orderId"),
					signNewDate.get("signature"));
		}
		return signNewDate;
	}

	@Override
	public boolean verify(String certId, Map<String, String> verifyDate) {
		logger.info("银联对参数进行验签，订单号:{}", verifyDate.get("orderId"));
		StringBuffer signStr = new StringBuffer();
		Map<String,String> verifNewDate=new HashMap<String,String>();
		if (null != verifyDate &&verifyDate.size()!=0) {
			Iterator<Entry<String, String>> it = verifyDate.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				if(null != value && !"null".equals(value)){
					verifNewDate.put(key, value);
					signStr.append(key + "=" + value);
				}
			}
		}
			logger.info("验签的原字符串:{}", signStr.toString());
		init();
		boolean isValidate = MpiUtil.validate(verifNewDate, "");
		if (isValidate) {
			logger.info("银联对参数进行验签，订单号:{},验签成功", verifNewDate.get("orderId"));
		} else {
			logger.info("银联对参数进行验签，订单号:{},验签失败", verifNewDate.get("orderId"));
		}

		return isValidate;
	}

	/**
	 * 获取签名的证书ID
	 */
	@Override
	public String getSignCertId() {
		return CertUtil.getSignCertId();
	}

	/**
	 * 获取加密的证书ID
	 */
	@Override
	public String getEncryptCertId() {
		return CertUtil.getEncryptCertId();
	}

	/**
	 * 将Map形式的表单交易数据转换为key1=value1&key2=value2的形式
	 */
	@Override
	public String coverMap2String(Map<String, String> data) {
		return MpiUtil.coverMap2String(data);
	}

	/**
	 * 将形如key=value&key=value的字符串转换为相应的Map对象
	 */
	@Override
	public Map<String, String> coverResultString2Map(String res) {
		return MpiUtil.coverResultString2Map(res);
	}

	/**
	 * 密码加密，输入参数依次为卡号、密码、字符集。
	 */
	@Override
	public String encryptPin(String card, String pwd, String encoding) {
		return MpiUtil.encryptPin(card, pwd, encoding);
	}

	/*public static void main(String[] args) {
		TUPayRemoteServiceImpl tupay = new TUPayRemoteServiceImpl();
		Map<String, String> signDate = new HashMap<String, String>();
		// 版本号
		signDate.put("version", "3.0.0");
		// 交易类型 取值:同原交易
		signDate.put("txnType", "01");
		// 交易子类型 同原交易
		signDate.put("txnSubType", "01");
		// 业务类型 同原交易
		signDate.put("bizType", "000000");
		// 收单机构代码
		signDate.put("acqInsCode", "00201000");
		// 接入类型 商户:0 收单:1
		signDate.put("accessType", "0");
		// 商户类型
		signDate.put("merType", "0");
		signDate = tupay.sign("", signDate);
		System.out.println(signDate);
		String signature = signDate.get("signature");
		signDate.put("signature", signature);

		System.out.println("签名结果：" + signature);

		String data = tupay.coverMap2String(signDate);

		System.out.println("签名后的数据：" + data);

		String cerId = tupay.getSignCertId();
		System.out.println("证书编号：" + cerId);
		boolean isverfy = tupay.verify(cerId, signDate);
		System.out.println("验签结果:" + isverfy);
	}*/

	/** 财付通商户通信密钥*/
	private String tenpay_key;
	/** 财付通商户字符集 */
	private String tenpay_charset;
	
	/**
	 * 已在env.properties中配置
	 */
	/*public TUPayRemoteServiceImpl() {
		this.tenpay_key = "8934e7d15453e97507ef794cf7b0519d";
		this.tenpay_charset = "GBK";
	}*/
	
	public String getTenpay_key() {
		return tenpay_key;
	}

	public void setTenpay_key(String tenpay_key) {
		this.tenpay_key = tenpay_key;
	}

	public String getTenpay_charset() {
		return tenpay_charset;
	}

	public void setTenpay_charset(String tenpay_charset) {
		this.tenpay_charset = tenpay_charset;
	}

	/**
	 * TODO 财付通签名实现方法
	 * @param tenpaysignDate 签名前的字符串
	 * return Map	
	 */ 
	public Map<String, String> tenPaySign(Map<String, String> tenpaySignDate) {
		StringBuffer sb = new StringBuffer();
		Map<String, String> tenpaySignNewDate = new HashMap<String, String>();
		Map<String, String> returnTenpaySignDate = new HashMap<String, String>();
		if (null != tenpaySignDate && !tenpaySignDate.isEmpty()) {
			Iterator<Entry<String, String>> it = tenpaySignDate.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				if(null != value && !"".equals(value)){
					sb.append(key + "=" + value);
					tenpaySignNewDate.put(key, value);
				}
			}
		}
		//添加商户通信密钥
		sb.append("tenpayKey=" + this.getTenpay_key());
		tenpaySignNewDate.put("tenpayKey", this.getTenpay_key());
		logger.info("签名的原字符串:{}", tenpaySignNewDate);
		//System.out.println("签名过滤后的字符串\t"+tenpaySignNewDate);
		String sign="";
		String strKey = tenpaySignNewDate.get("tenpayKey");
		String s = "";
		//判断财付通密钥不为空
		if(null != strKey && !"".equals(strKey)){
			logger.info("支付宝的密钥是:{}，开始签名。", tenpaySignNewDate.get("tenpayKey"));
			//算出摘要(即为签名结果)
			sign = MD5Util.MD5Encode(sb.toString(), this.getTenpay_charset()).toLowerCase();
			s = Md5.getMD5ofStrByLowerCase(sb.toString());
			//TODO
			System.out.println("调用涛哥写的MD5工具类输出结果：====>>>"+s);
			logger.info("签名后转换为大写的字符串:{}", sign.toString());
		}
		logger.info("支付宝的密钥为空，不能签名。", tenpaySignNewDate.get("tenpayKey"));
		returnTenpaySignDate.put("sign", sign);
		return returnTenpaySignDate;
	}

	/**
	 * TODO 财付通验签实现方法
	 * @param tenpaysignDate 签名前的字符串
	 * return Map	
	 */
	public boolean tenPayVerify(Map<String, String> tenpaySignDate){
		//拿到验签集合中的sign
		String tenPayVerifySign = tenpaySignDate.get("sign");
		
		StringBuffer sb = new StringBuffer();
		Map<String, String> tenpaySignNewDate = new HashMap<String, String>();
		Map<String, String> returnTenpaySignDate = new HashMap<String, String>();
		if (null != tenpaySignDate && !tenpaySignDate.isEmpty()) {
			Iterator<Entry<String, String>> it = tenpaySignDate.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				if(null != value && !"".equals(value)){
					sb.append(key + "=" + value);
					tenpaySignNewDate.put(key, value);
				}
			}
		}
		sb.append("tenpayKey=" + this.getTenpay_key());
		tenpaySignNewDate.put("tenpayKey", this.getTenpay_key());
		logger.info("签名的原字符串:{}", tenpaySignNewDate);
		//System.out.println("签名过滤后的字符串\t"+tenpaySignNewDate);
		String sign="";
		String strKey = tenpaySignNewDate.get("tenpayKey");
		boolean returnTenPaySign = false;
		//判断财付通密钥不为空
		if(null != strKey && !"".equals(strKey)){
			logger.info("支付宝的密钥是:{}，开始签名。", tenpaySignNewDate.get("tenpayKey"));
			//算出摘要(即为签名结果)
			sign = MD5Util.MD5Encode(sb.toString(), this.getTenpay_charset()).toLowerCase();
			logger.info("签名后转换为大写的字符串:{}", sign.toString());
			if(sign.equals(tenPayVerifySign)){
				returnTenPaySign = true;
				return returnTenPaySign;
			}
		}
		logger.info("支付宝的密钥为空，不能签名。", tenpaySignNewDate.get("tenpayKey"));
		returnTenpaySignDate.put("sign", sign);
		return returnTenPaySign;
	}
	
	/**
	 * TODO 测试财付通签名方法
	 * @param args
	 */
	public static void main(String[] args) {
		TUPayRemoteServiceImpl tenpay = new TUPayRemoteServiceImpl();
		Map<String, String> signDate = new HashMap<String, String>();
		// 版本号
		signDate.put("partner", "1900000109");
		// 交易类型 取值:同原交易
		signDate.put("total_fee", "1");
		// 交易子类型 同原交易
		signDate.put("desc", "a&b");
		// 业务类型 同原交易
		signDate.put("attach", "");
		// 收单机构代码
		signDate.put("test", "1");
		System.out.println("原串："+signDate);
		Map<String, String> maps = new HashMap<String, String>();
		maps = tenpay.tenPaySign(signDate);
		System.out.println("签名后："+maps);
	}
	
	
	
	//支付宝私钥
	private String alipay_key;
	//支付宝字符集
	private String alipay_charset;
	
	public String getAlipay_key() {
		return alipay_key;
	}

	public void setAlipay_key(String alipay_key) {
		this.alipay_key = alipay_key;
	}

	public String getAlipay_charset() {
		return alipay_charset;
	}

	public void setAlipay_charset(String alipay_charset) {
		this.alipay_charset = alipay_charset;
	}

	/**
	 * 已在env.properties中配置
	 */
	/*public TUPayRemoteServiceImpl() {
		this.alipay_key = "7d314d22efba4f336fb187697793b9d2";
		this.alipay_charset = "GBK";
	}*/
	
	/**
	 * TODO 支付宝签名实现方法
	 * @param alipaySignDate 签名前的字符串
	 * return Map	
	 */
	@Override
	public Map<String, String> alipaySign(Map<String, String> alipaySignDate) {
		StringBuffer sb = new StringBuffer();
		Map<String, String> alipaySignNewDate = new HashMap<String, String>();
		Map<String, String> returnAlipaySignDate = new HashMap<String, String>();
		if (null != alipaySignDate && !alipaySignDate.isEmpty()) {
			Iterator<Entry<String, String>> it = alipaySignDate.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				if(null != value && !"".equals(value)){
					sb.append(key + "=" + value);
					alipaySignNewDate.put(key, value);
				}
			}
		}
		sb.append("tenpayKey=" + this.getTenpay_key());
		alipaySignNewDate.put("tenpayKey", this.getTenpay_key());
		logger.info("签名的原字符串:{}", alipaySignNewDate);
		//System.out.println("签名过滤后的字符串\t"+tenpaySignNewDate);
		String sign="";
		String strKey = alipaySignNewDate.get("tenpayKey");
		//判断财付通密钥不为空
		if(null != strKey && !"".equals(strKey)){
			logger.info("支付宝的密钥是:{}，开始签名。", alipaySignNewDate.get("tenpayKey"));
			//算出摘要(即为签名结果)
			sign = MD5Util.MD5Encode(sb.toString(), this.getTenpay_charset()).toLowerCase();
			logger.info("签名后转换为大写的字符串:{}", sign.toString());
		}
		logger.info("支付宝的密钥为空，不能签名。", alipaySignNewDate.get("tenpayKey"));
		returnAlipaySignDate.put("sign", sign);
		return returnAlipaySignDate;
	}

	/**
	 * TODO 支付宝验签实现方法
	 * @param alipayVerifyDate 验签前的字符串
	 * return Map	
	 */
	@Override
	public boolean alipayVerify(Map<String, String> alipayVerifyDate) {
		//拿到验签集合中的sign
		String tenPayVerifySign = alipayVerifyDate.get("sign");
		
		StringBuffer sb = new StringBuffer();
		Map<String, String> tenpaySignNewDate = new HashMap<String, String>();
		Map<String, String> returnTenpaySignDate = new HashMap<String, String>();
		if (null != alipayVerifyDate && !alipayVerifyDate.isEmpty()) {
			Iterator<Entry<String, String>> it = alipayVerifyDate.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				if(null != value && !"".equals(value)){
					sb.append(key + "=" + value);
					tenpaySignNewDate.put(key, value);
				}
			}
		}
		sb.append("tenpayKey=" + this.getTenpay_key());
		tenpaySignNewDate.put("tenpayKey", this.getTenpay_key());
		logger.info("签名的原字符串:{}", tenpaySignNewDate);
		//System.out.println("签名过滤后的字符串\t"+tenpaySignNewDate);
		String sign="";
		String strKey = tenpaySignNewDate.get("tenpayKey");
		boolean returnTenPaySign = false;
		//判断财付通密钥不为空
		if(null != strKey && !"".equals(strKey)){
			logger.info("支付宝的密钥是:{}，开始签名。", tenpaySignNewDate.get("tenpayKey"));
			//算出摘要(即为签名结果)
			sign = MD5Util.MD5Encode(sb.toString(), this.getTenpay_charset()).toLowerCase();
			logger.info("签名后转换为大写的字符串:{}", sign.toString());
			if(sign.equals(tenPayVerifySign)){
				returnTenPaySign = true;
				return returnTenPaySign;
			}
		}
		logger.info("支付宝的密钥为空，不能签名。", tenpaySignNewDate.get("tenpayKey"));
		returnTenpaySignDate.put("sign", sign);
		return returnTenPaySign;
	}
	
	/**
	 * 测试财付通签名方法
	 * @param args
	 *//*
	public static void main(String[] args) {
		TUPayRemoteServiceImpl tenpay = new TUPayRemoteServiceImpl();
		Map<String, String> signDate = new HashMap<String, String>();
		
		// 版本号
		signDate.put("partner", "1900000109");
		// 交易类型 取值:同原交易
		signDate.put("total_fee", "1");
		// 交易子类型 同原交易
		signDate.put("desc", "a&b");
		// 业务类型 同原交易
		signDate.put("attach", "");
		// 收单机构代码
		signDate.put("test", "1");
		System.out.println("原串："+signDate);
		Map<String, String> maps = new HashMap<String, String>();
		maps = tenpay.alipaySign(signDate);
		System.out.println("签名后："+maps);
	}*/
	
}