package com.beanpai.egr.shopping.secret;

import android.annotation.SuppressLint;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 密码加密
 * 
 * @author yangyh
 * @create 2013-9-26
 */
public class AesUserPass {

	private final static String Algorithm = "AES"; // 定义 加密算法,可用
	private final static String KEY_PASSWORD = "!@#$%^&*(istarview)";

	/**
	 * 根据参数生成KEY
	 * 
	 * @param strKey
	 *            密钥字符串
	 */
	@SuppressLint("TrulyRandom")
	private SecretKeySpec generateKey(String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(Algorithm);
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(password.getBytes());
			kgen.init(128, random);
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, Algorithm);
			return key;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 加密String明文输入,String密文输出
	 * 
	 * @param strMing
	 *            String明文
	 * @return String密文 Base64编码
	 */
	@SuppressWarnings("unused")
	public String encrypt(String strMing) {
		byte[] byteMi = null;
		byte[] byteMing = null;
		String strMi = "";
		try {
			byteMing = strMing.getBytes();
			byteMi = this.getEncCode(byteMing, KEY_PASSWORD);
			// strMi = Base64.encode(byteMing, 0);
		} catch (Exception e) {
			e.printStackTrace();
			byteMing = null;
			byteMi = null;
		}
		return strMi;
	}

	/**
	 * 加密以byte[]明文输入,byte[]密文输出
	 * 
	 * @param byteS
	 *            byte[]明文
	 * @return byte[]密文
	 */
	private byte[] getEncCode(byte[] byteS, String password) {
		byte[] byteFina = null;
		Cipher cipher;
		try {
			Key key = generateKey(password);
			cipher = Cipher.getInstance(Algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byteFina = cipher.doFinal(byteS);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cipher = null;
		}
		return byteFina;
	}
}