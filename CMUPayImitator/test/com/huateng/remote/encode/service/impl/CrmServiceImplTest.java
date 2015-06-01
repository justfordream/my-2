package com.huateng.remote.encode.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext-test.xml" })
public class CrmServiceImplTest extends CrmServiceImpl {
	//报文体明文
	private String decoded = "";
	//报文体密文
	private String encoded = "";
	@Test
	public void testEncryptXmlBody() throws Exception {
		String s = this.encryptXmlBody("", decoded, "", "");
		System.out.println(s);
	}

	@Test
	public void testDecryptXmlBody() throws Exception {
		String s = this.decryptXmlBody("", encoded, "", "");
		System.out.println(s);
	}

}
