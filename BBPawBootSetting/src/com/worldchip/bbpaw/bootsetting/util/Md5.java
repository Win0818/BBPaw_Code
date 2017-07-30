
package com.worldchip.bbpaw.bootsetting.util;

import org.apache.http.protocol.HTTP;

import android.content.pm.PackageInfo;
import android.content.pm.Signature;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {

    private static String tag = "Md5Util";

    public static String md5LowerCase(String string) {
        try {
            MessageDigest digester = MessageDigest.getInstance(ALGORITHM);
            byte[] buffer = string.getBytes(DEFAULT_CHARSET);
            digester.update(buffer);
            buffer = digester.digest();
            string = toLowerCaseHex(buffer);
        } catch (NoSuchAlgorithmException e) {
            LogUtil.e(tag, e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            LogUtil.e(tag, e.getMessage(), e);
        }
        return string;
    }

    public static String md5Capitals(String string) {
        try {
            MessageDigest digester = MessageDigest.getInstance(ALGORITHM);
            byte[] buffer = string.getBytes(DEFAULT_CHARSET);
            digester.update(buffer);

            buffer = digester.digest();
            string = toHexCapitals(buffer);
        } catch (NoSuchAlgorithmException e) {
            LogUtil.e(tag, e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            LogUtil.e(tag, e.getMessage(), e);
        }
        return string;
    }

    private static String toHexCapitals(byte[] b) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            int v = b[i];
            builder.append(HEX_CAPITALS[(0xF0 & v) >> 4]);
            builder.append(HEX_CAPITALS[0x0F & v]);
        }
        return builder.toString();
    }

    private static String toLowerCaseHex(byte[] b) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            int v = b[i];
            builder.append(HEX_LOWER_CASE[(0xF0 & v) >> 4]);
            builder.append(HEX_LOWER_CASE[0x0F & v]);
        }
        return builder.toString();
    }

    private static final char[] HEX_CAPITALS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };
    private static final char[] HEX_LOWER_CASE = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };
    public static final String ALGORITHM = "MD5";
    public static final String DEFAULT_CHARSET = HTTP.UTF_8;

    private static final char HEX_DIGITS[] = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    private static String toHexString(byte[] b) { // String to byte
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * 根据文件的路径获取文件的MD5
     * 
     * @param filePath
     * @return 如果发生异常则返回空字符串
     */
    public static String getFileMD5(String filePath) {
        InputStream fis;
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5;
        try {
            fis = new FileInputStream(filePath);
            md5 = MessageDigest.getInstance("MD5");
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            fis.close();
            return toHexString(md5.digest());
        } catch (Exception e) {
            return "";
        }
    }

    /////////////// 以下方法为我的应用中使用 //////////////////
    /**
     * 根据apk文件签名得到md5值
     * 
     * @param packageinfo
     * @return
     */
    public static String getMd5(PackageInfo packageinfo) {
        if (packageinfo == null) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(getSingInfo(packageinfo));

            byte[] b = md.digest();
            char[] HEXCHAR = {
                    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
            };
            StringBuilder sb = new StringBuilder(b.length * 2);

            for (int i = 0; i < b.length; i++) {
                sb.append(HEXCHAR[(b[i] & 0xf0) >>> 4]);
                sb.append(HEXCHAR[(b[i] & 0x0f)]);
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * 得到APK文件的签名
     * 
     * @param packageinfo
     * @return
     */
    public static byte[] getSingInfo(PackageInfo packageinfo) {
        try {
            Signature[] signs = packageinfo.signatures;
            Signature sign = signs[0];
            return sign.toCharsString().getBytes();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * signmd5算法
     * 
     * @param md5
     * @return creatSignInt(getMd5(mContext.getPackageManager()
     *         .getPackageInfo(appinfo.packageName,
     *         PackageManager.GET_SIGNATURES)))
     */
    public static String creatSignInt(String md5) {
        if (md5 == null || md5.length() < 32) {
            return "-1";
        }

        String sign = md5.substring(8, 8 + 16);
        long id1 = 0;
        long id2 = 0;
        String s = "";
        for (int i = 0; i < 8; i++) {
            id2 *= 16;
            s = sign.substring(i, i + 1);
            id2 += Integer.parseInt(s, 16);
        }

        for (int i = 8; i < sign.length(); i++) {
            id1 *= 16;
            s = sign.substring(i, i + 1);
            id1 += Integer.parseInt(s, 16);
        }

        long id = (id1 + id2) & 0xFFFFFFFFL;
        return String.valueOf(id);
    }
}
