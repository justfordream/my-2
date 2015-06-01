package com.toolbox.util;

import java.security.MessageDigest;

/**
 * SHA1算法
 * 
 * @author Gary
 * 
 */
public class SHACoder {
	private static final String KEY_SHA = "SHA1";
	private volatile static SHACoder sha1Coder;

	private SHACoder() {

	}

	public static SHACoder getInstance() {
		/*
		 * 检查实例，如果不存在就进入同步块,只有第一次才彻底执行这里的代码
		 */
		if (sha1Coder == null) {
			synchronized (SHACoder.class) {
				/*
				 * 进入区块后再检查一次，如果仍是null，才创建实例
				 */
				if (sha1Coder == null) {
					sha1Coder = new SHACoder();
				}
			}
		}
		return sha1Coder;
	}

	/**
	 * SHA加密
	 * 
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public byte[] encryptSHA(byte[] source) throws Exception {
		MessageDigest md = MessageDigest.getInstance(KEY_SHA);
		md.update(source);
		return md.digest();

	}

	/**
	 * 二行制转字符串
	 * 
	 * @param bts
	 * @return
	 */
	public String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(SHACoder.getInstance().bytes2Hex(SHACoder.getInstance().encryptSHA("aa".getBytes())));

	}
}
