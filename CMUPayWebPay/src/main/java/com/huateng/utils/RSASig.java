package com.huateng.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;

import netpay.merchant.crypto.ABAProvider;
import netpay.merchant.crypto.RSAPrivKeyCrt;
import netpay.merchant.crypto.RSAPubKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RSASig {
	private String priKey;
	private String pubKey;
	private static final char[] bcdLookup = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f' };
	private Log log = LogFactory.getLog(this.getClass());

	public boolean generateKeys() {
		Security.addProvider(new ABAProvider());

		SecureRandom rand = new SecureRandom();

		rand.setSeed(System.currentTimeMillis());
		try {
			KeyPairGenerator fact = KeyPairGenerator.getInstance("RSA", "ABA");

			fact.initialize(1024, rand);

			KeyPair keyPair = fact.generateKeyPair();

			PublicKey keyPub = keyPair.getPublic();

			PrivateKey keyPri = keyPair.getPrivate();

			this.pubKey = bytesToHexStr(keyPub.getEncoded());

			this.priKey = bytesToHexStr(keyPri.getEncoded());
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public String getPublicKey() {
		return this.pubKey;
	}

	public String getPrivateKey() {
		return this.priKey;
	}

	public void setPublicKey(String pkey) {
		this.pubKey = pkey;
	}

	public void setPrivateKey(String pkey) {
		this.priKey = pkey;
	}

	public String generateDigest(String scr) {
		try {
			Security.addProvider(new ABAProvider());
			MessageDigest mDist = MessageDigest.getInstance("MD5", "CBC");
			mDist.update(scr.getBytes());
			byte[] Digest = mDist.digest();
			return bytesToHexStr(Digest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String generateSigature(String src) {
		try {
			Security.addProvider(new ABAProvider());
			Signature sigEng = Signature.getInstance("MD5withRSA", "ABA");

			byte[] pribyte = hexStrToBytes(this.priKey.trim());
			sigEng.initSign(new RSAPrivKeyCrt(pribyte));
			sigEng.update(src.getBytes());

			byte[] signature = sigEng.sign();
			return bytesToHexStr(signature);
		} catch (Exception e) {
		}
		return null;
	}

	public boolean verifySigature(String sign, String src) {
		try {
			log.info("start");
			Security.addProvider(new ABAProvider());
			Signature sigEng = Signature.getInstance("MD5withRSA");

			byte[] pubbyte = hexStrToBytes(this.pubKey.trim());
			sigEng.initVerify(new RSAPubKey(pubbyte));
			sigEng.update(src.getBytes());

			byte[] sign1 = hexStrToBytes(sign);

			String str = HexUtils.toHex(sign1);

			log.info(str);

			return sigEng.verify(sign1);
		} catch (Exception e) {
			log.error(e.getMessage());
			StackTraceElement[] arrayElements = e.getStackTrace();
			if (arrayElements != null) {
				for (int i = 0; i < arrayElements.length; i++) {
					log.error(arrayElements[i]);
				}
			}
		}

		return false;
	}

	public static final String bytesToHexStr(byte[] bcd) {
		StringBuffer s = new StringBuffer(bcd.length * 2);

		for (int i = 0; i < bcd.length; i++) {
			s.append(bcdLookup[(bcd[i] >>> 4 & 0xF)]);
			s.append(bcdLookup[(bcd[i] & 0xF)]);
		}

		return s.toString();
	}

	public static final byte[] hexStrToBytes(String s) {
		byte[] bytes = new byte[s.length() / 2];

		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
		}

		return bytes;
	}
}
