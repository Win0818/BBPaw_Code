package com.beanpai.egr.shopping.utils;

import android.annotation.SuppressLint;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

/**
 * �ַ���������
 * 
 * @author ������
 * 
 */
public class ToolString {

	/**
	 * ��ȡUUID
	 * 
	 * @return 32UUIDСд�ַ���
	 */
	@SuppressLint("DefaultLocale")
	public static String gainUUID() {
		String strUUID = UUID.randomUUID().toString();
		strUUID = strUUID.replaceAll("-", "").toLowerCase();
		return strUUID;
	}

	/**
	 * �ж��ַ����Ƿ�ǿշ�null
	 * 
	 * @param strParm
	 *            ��Ҫ�жϵ��ַ���
	 * @return ���
	 */
	public static boolean isNoBlankAndNoNull(String strParm) {
		return !((strParm == null) || (strParm.equals("")));
	}

	/**
	 * ����ת���ַ���
	 * 
	 * @param is
	 *            ������
	 * @return
	 * @throws Exception
	 */
	public static String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		return sb.toString();
	}

	/**
	 * ���ļ�ת���ַ���
	 * 
	 * @param file
	 *            �ļ�
	 * @return
	 * @throws Exception
	 */
	public static String getStringFromFile(File file) throws Exception {
		FileInputStream fin = new FileInputStream(file);
		String ret = convertStreamToString(fin);
		// Make sure you close all streams.
		fin.close();
		return ret;
	}

	/**
	 * �ַ�ȫ�ǻ�
	 * 
	 * @param input
	 * @return
	 */
	public static String ToSBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
}
